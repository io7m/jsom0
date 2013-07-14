/*
 * Copyright © 2013 <code@io7m.com> http://io7m.com
 * 
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.jsom0.demos;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLCapabilitiesImmutable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jaux.functional.Option;
import com.io7m.jaux.functional.Option.Some;
import com.io7m.jaux.functional.Pair;
import com.io7m.jcanephora.ArrayBuffer;
import com.io7m.jcanephora.ArrayBufferAttribute;
import com.io7m.jcanephora.BlendFunction;
import com.io7m.jcanephora.DepthFunction;
import com.io7m.jcanephora.GLCompileException;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLImplementationJOGL;
import com.io7m.jcanephora.GLInterfaceCommon;
import com.io7m.jcanephora.GLUnsupportedException;
import com.io7m.jcanephora.IndexBuffer;
import com.io7m.jcanephora.Primitives;
import com.io7m.jcanephora.Program;
import com.io7m.jcanephora.ProgramAttribute;
import com.io7m.jcanephora.ProgramUniform;
import com.io7m.jcanephora.ProjectionMatrix;
import com.io7m.jcanephora.Texture2DStatic;
import com.io7m.jcanephora.TextureFilterMagnification;
import com.io7m.jcanephora.TextureFilterMinification;
import com.io7m.jcanephora.TextureLoaderImageIO;
import com.io7m.jcanephora.TextureUnit;
import com.io7m.jcanephora.TextureWrapS;
import com.io7m.jcanephora.TextureWrapT;
import com.io7m.jlog.Log;
import com.io7m.jsom0.Model;
import com.io7m.jsom0.ModelMaterial;
import com.io7m.jsom0.ModelObjectVBO;
import com.io7m.jsom0.ModelTexture;
import com.io7m.jsom0.NameNormalAttribute;
import com.io7m.jsom0.NamePositionAttribute;
import com.io7m.jsom0.NameUVAttribute;
import com.io7m.jsom0.parser.Error;
import com.io7m.jsom0.parser.ModelObjectParser;
import com.io7m.jsom0.parser.ModelObjectParserVBOImmediate;
import com.io7m.jsom0.parser.ModelParser;
import com.io7m.jtensors.MatrixM4x4F;
import com.io7m.jtensors.QuaternionM4F;
import com.io7m.jtensors.VectorI3F;
import com.io7m.jtensors.VectorI4F;
import com.io7m.jtensors.VectorM3F;
import com.io7m.jtensors.VectorM4F;
import com.io7m.jtensors.VectorReadable3F;
import com.io7m.jtensors.VectorReadable4F;
import com.io7m.jvvfs.FSCapabilityRead;
import com.io7m.jvvfs.Filesystem;
import com.io7m.jvvfs.FilesystemError;
import com.io7m.jvvfs.PathVirtual;

public final class SMVGLCanvas extends GLCanvas
{
  static final @Nonnull VectorReadable3F INITIAL_ORIGIN_MODEL;
  static final @Nonnull VectorReadable3F INITIAL_ORIGIN_CAMERA;

  static {
    INITIAL_ORIGIN_MODEL = new VectorI3F(0.0f, 2.0f, 0f);
    INITIAL_ORIGIN_CAMERA = new VectorI3F(0.0f, 2.0f, 5.0f);
  }

  private static final class ViewKeyListener implements KeyListener
  {
    ViewKeyListener(
      @SuppressWarnings("unused") final @Nonnull SMVGLCanvas canvas,
      @SuppressWarnings("unused") final @Nonnull Log log)
    {
      // Nothing
    }

    @Override public void keyPressed(
      final @Nonnull KeyEvent e)
    {
      // TODO Auto-generated method stub
    }

    @Override public void keyReleased(
      final @Nonnull KeyEvent e)
    {
      // TODO Auto-generated method stub
    }

    @Override public void keyTyped(
      final @Nonnull KeyEvent e)
    {
      // TODO Auto-generated method stub
    }
  }

  private static final class ViewRenderer implements GLEventListener
  {
    protected static final @Nonnull NameUVAttribute       NAME_UV_ATTRIBUTE;
    protected static final @Nonnull NameNormalAttribute   NAME_NORMAL_ATTRIBUTE;
    protected static final @Nonnull NamePositionAttribute NAME_POSITION_ATTRIBUTE;
    protected static final @Nonnull VectorReadable4F      GRID_COLOR;
    protected static final @Nonnull VectorReadable3F      X_AXIS;
    protected static final @Nonnull VectorReadable3F      Y_AXIS;
    protected static final @Nonnull VectorReadable3F      Z_AXIS;

    static {
      GRID_COLOR = new VectorI4F(1, 1, 1, 0.2f);

      X_AXIS = new VectorI3F(1, 0, 0);
      Y_AXIS = new VectorI3F(0, 1, 0);
      Z_AXIS = new VectorI3F(0, 0, 1);

      try {
        NAME_UV_ATTRIBUTE = new NameUVAttribute("v_uv");
        NAME_NORMAL_ATTRIBUTE = new NameNormalAttribute("v_normal");
        NAME_POSITION_ATTRIBUTE = new NamePositionAttribute("v_position");
      } catch (final ConstraintError e) {
        throw new UnreachableCodeException();
      }
    }

    private static boolean isTimeToCheckPrograms(
      final int frame_current)
    {
      return (frame_current == 1) || ((frame_current % (10 * 60)) == 0);
    }

    private final @Nonnull SMVGLCanvas                            canvas;
    private @CheckForNull GLImplementationJOGL                    gi;
    private final @Nonnull Log                                    log;
    private final @Nonnull AtomicReference<Model<ModelObjectVBO>> model;
    protected @CheckForNull String                                object;
    private final @Nonnull AtomicReference<ModelMaterial>         material;
    private final @Nonnull FSCapabilityRead                       filesystem;
    private final @Nonnull Map<SMVRenderStyle, Program>           shaders;
    private int                                                   frame = 0;
    private @CheckForNull Program                                 program_current;
    private @CheckForNull SMVVisibleGridPlane                     grid;
    private @CheckForNull SMVVisibleAxes                          axes;
    protected @CheckForNull Texture2DStatic                       texture;
    protected final @Nonnull MatrixM4x4F                          matrix_temp;
    protected final @Nonnull MatrixM4x4F                          matrix_modelview;
    protected final @Nonnull MatrixM4x4F                          matrix_projection;
    protected final @Nonnull MatrixM4x4F                          matrix_model;
    protected final @Nonnull MatrixM4x4F                          matrix_view;
    protected final @Nonnull MatrixM4x4F.Context                  m4_context;
    private final @Nonnull SMVCamera                              camera;
    protected final @Nonnull VectorM3F                            model_position;
    protected final @Nonnull QuaternionM4F                        model_orientation;
    protected final @Nonnull VectorM3F                            model_rotation;
    protected @Nonnull TextureUnit[]                              texture_units;
    protected final @Nonnull AtomicBoolean                        rotating_y;
    private final @Nonnull QuaternionM4F                          rotate_x;
    private final @Nonnull QuaternionM4F                          rotate_y;
    private final @Nonnull QuaternionM4F                          rotate_z;
    private @CheckForNull SMVLightDirectional                     light;

    ViewRenderer(
      final @Nonnull SMVGLCanvas canvas,
      final @Nonnull Log log)
      throws ConstraintError,
        FilesystemError
    {
      final Filesystem fs = Filesystem.makeWithoutArchiveDirectory(log);
      fs.mountClasspathArchive(SMVGLCanvas.class, PathVirtual.ROOT);
      this.filesystem = fs;
      this.canvas = canvas;
      this.gi = null;
      this.log = new Log(log, "view-renderer");
      this.model = new AtomicReference<Model<ModelObjectVBO>>();
      this.object = null;
      this.material = new AtomicReference<ModelMaterial>();
      this.shaders = new HashMap<SMVRenderStyle, Program>();

      this.matrix_temp = new MatrixM4x4F();
      this.matrix_modelview = new MatrixM4x4F();
      this.matrix_model = new MatrixM4x4F();
      this.matrix_view = new MatrixM4x4F();
      this.matrix_projection = new MatrixM4x4F();
      this.m4_context = new MatrixM4x4F.Context();

      this.rotate_x = new QuaternionM4F();
      this.rotate_y = new QuaternionM4F();
      this.rotate_z = new QuaternionM4F();

      this.model_position = new VectorM3F(SMVGLCanvas.INITIAL_ORIGIN_MODEL);
      this.model_orientation = new QuaternionM4F();
      this.model_rotation = new VectorM3F();

      this.camera = new SMVCamera();
      this.camera.setPosition(
        SMVGLCanvas.INITIAL_ORIGIN_CAMERA.getXF(),
        SMVGLCanvas.INITIAL_ORIGIN_CAMERA.getYF(),
        SMVGLCanvas.INITIAL_ORIGIN_CAMERA.getZF());
      this.camera.setTarget(
        this.model_position.x,
        this.model_position.y,
        this.model_position.z);

      this.rotating_y = new AtomicBoolean(true);
    }

    @Override public void display(
      final @Nonnull GLAutoDrawable drawable)
    {
      ++this.frame;

      if (this.gi == null) {
        return;
      }

      try {
        final GLInterfaceCommon gl = this.gi.getGLCommon();

        if (this.grid == null) {
          this.grid = new SMVVisibleGridPlane(gl, 50, 0, 50);
        }
        if (this.axes == null) {
          this.axes = new SMVVisibleAxes(gl, 50, 50, 50);
        }
        if (this.texture_units == null) {
          this.texture_units = gl.textureGetUnits();
        }

        this.reloadMaterialIfRequested(gl);
        this.reloadModelIfRequested(gl);
        this.selectObject();

        if (ViewRenderer.isTimeToCheckPrograms(this.frame)) {
          this.log.debug("frame is "
            + this.frame
            + ", time to recompile shaders");
          this.reloadProgramsIfNecessary(gl);
        }

        this.setCurrentProgram();

        this.makeProjectionMatrix(drawable.getWidth(), drawable.getHeight());
        this.makeCameraViewMatrix();

        gl.colorBufferClear3f(0.0f, 0.0f, 0.0f);

        if (this.program_current != null) {
          gl.depthBufferWriteDisable();
          gl.depthBufferDisable();

          this.renderGrid(gl);
          this.renderAxes(gl);

          gl.depthBufferWriteEnable();
          gl.depthBufferClear(1.0f);
          gl.depthBufferEnable(DepthFunction.DEPTH_LESS_THAN);

          final Model<ModelObjectVBO> model_actual = this.model.get();
          if (model_actual != null) {
            this.renderModel(gl, model_actual, this.program_current);
          }
        }

      } catch (final GLException e) {
        SMVErrorBox.showError("OpenGL error", e);
      } catch (final ConstraintError e) {
        SMVErrorBox.showError("Constraint error", e);
      }
    }

    private void selectObject()
    {
      final String new_object = this.canvas.want_object.getAndSet(null);
      if (new_object != null) {
        this.object = new_object;
      }

      if (this.object == null) {
        final Model<ModelObjectVBO> m = this.model.get();
        if (m != null) {
          this.object = m.getObjectNames().first();
        }
      }
    }

    @Override public void dispose(
      final @Nonnull GLAutoDrawable drawable)
    {
      // Nothing
    }

    @Override public void init(
      final @Nonnull GLAutoDrawable drawable)
    {
      try {
        this.gi = new GLImplementationJOGL(drawable.getContext(), this.log);
      } catch (final GLException e) {
        SMVErrorBox.showError("OpenGL error", e);
      } catch (final GLUnsupportedException e) {
        SMVErrorBox.showError("OpenGL unsupported error", e);
      } catch (final ConstraintError e) {
        SMVErrorBox.showError("Constraint error", e);
      }
    }

    private void makeCameraViewMatrix()
    {
      final Pair<VectorReadable3F, VectorReadable3F> pair =
        this.canvas.want_camera_set.getAndSet(null);

      if (pair != null) {
        this.camera.setPosition(
          pair.first.getXF(),
          pair.first.getYF(),
          pair.first.getZF());
        this.camera.setTarget(
          pair.second.getXF(),
          pair.second.getYF(),
          pair.second.getZF());
      }

      this.camera.makeViewMatrix(this.m4_context, this.matrix_view);
    }

    private void makeProjectionMatrix(
      final int width,
      final int height)
      throws ConstraintError
    {
      ProjectionMatrix.makePerspective(
        this.matrix_projection,
        1.0f,
        1000.0f,
        (width) / (height),
        Math.PI / 8.0);
    }

    private void reloadMaterialIfRequested(
      final @Nonnull GLInterfaceCommon gl)
    {
      final ModelMaterial new_material =
        this.canvas.want_load_material.getAndSet(null);
      if (new_material != null) {
        if (new_material.texture.isSome()) {
          final Some<ModelTexture> texture_s =
            (Option.Some<ModelTexture>) new_material.texture;

          FileInputStream stream = null;

          final String tname = texture_s.value.name;

          try {
            stream = new FileInputStream(tname);

            final TextureLoaderImageIO loader = new TextureLoaderImageIO();
            final Texture2DStatic new_texture =
              loader.load2DStaticInferredGLES2(
                gl,
                TextureWrapS.TEXTURE_WRAP_REPEAT,
                TextureWrapT.TEXTURE_WRAP_REPEAT,
                TextureFilterMinification.TEXTURE_FILTER_NEAREST,
                TextureFilterMagnification.TEXTURE_FILTER_NEAREST,
                stream,
                tname);

            if (this.texture != null) {
              gl.texture2DStaticDelete(this.texture);
            }
            this.texture = new_texture;

            this.log.info("Loaded " + tname);
          } catch (final IOException e) {
            SMVErrorBox.showError("I/O error", e);
          } catch (final ConstraintError e) {
            SMVErrorBox.showError("Constraint error", e);
          } catch (final GLException e) {
            SMVErrorBox.showError("OpenGL error", e);
          } finally {
            if (stream != null) {
              try {
                stream.close();
              } catch (final IOException e) {
                SMVErrorBox.showError("I/O error", e);
              }
            }
          }
        }
      }
    }

    private void reloadModelIfRequested(
      final @Nonnull GLInterfaceCommon gl)
      throws GLException
    {
      final Pair<File, JComboBox<String>> pair =
        this.canvas.want_load_model.getAndSet(null);
      if (pair != null) {
        final File file = pair.first;
        final JComboBox<String> tell = pair.second;

        FileInputStream stream = null;

        try {
          stream = new FileInputStream(file);

          final ModelObjectParser<ModelObjectVBO, GLException> object_parser =
            new ModelObjectParserVBOImmediate<GLInterfaceCommon>(
              file.toString(),
              stream,
              ViewRenderer.NAME_POSITION_ATTRIBUTE,
              ViewRenderer.NAME_NORMAL_ATTRIBUTE,
              ViewRenderer.NAME_UV_ATTRIBUTE,
              this.log,
              gl);

          final ModelParser<ModelObjectVBO, GLException> model_parser =
            new ModelParser<ModelObjectVBO, GLException>(object_parser);

          final Model<ModelObjectVBO> m = model_parser.model();
          this.model.set(m);

          SwingUtilities.invokeLater(new Runnable() {
            @Override public void run()
            {
              tell.removeAllItems();
              for (final String name : m.getObjectNames()) {
                tell.addItem(name);
              }
            }
          });

          this.log.info("Loaded " + file);
        } catch (final IOException e) {
          SMVErrorBox.showError("I/O error", e);
        } catch (final Error e) {
          SMVErrorBox.showError("Parse error", e);
        } catch (final ConstraintError e) {
          SMVErrorBox.showError("Constraint error", e);
        } finally {
          if (stream != null) {
            try {
              stream.close();
            } catch (final IOException e) {
              SMVErrorBox.showError("I/O error", e);
            }
          }
        }
      }
    }

    private void reloadProgramsIfNecessary(
      final @Nonnull GLInterfaceCommon gl)
    {
      for (final SMVRenderStyle style : SMVRenderStyle.values()) {
        if (this.shaders.containsKey(style)) {
          final Program program = this.shaders.get(style);
          try {
            if (program.requiresCompilation(this.filesystem, gl)) {
              program.compile(this.filesystem, gl);
            }
          } catch (final FilesystemError e) {
            SMVErrorBox.showError("Filesystem error", e);
          } catch (final GLCompileException e) {
            SMVErrorBox.showError("Compilation error", e);
          } catch (final ConstraintError e) {
            SMVErrorBox.showError("Constraint error", e);
          }
        } else {
          try {
            final PathVirtual shader_v =
              SMVShaderPaths.getShader(
                gl.metaIsES(),
                gl.metaGetVersionMajor(),
                gl.metaGetVersionMinor(),
                "standard.v");
            final PathVirtual shader_f =
              SMVShaderPaths.getShader(
                gl.metaIsES(),
                gl.metaGetVersionMajor(),
                gl.metaGetVersionMinor(),
                style.getName() + ".f");

            final Program program = new Program(style.getName(), this.log);
            program.addVertexShader(shader_v);
            program.addFragmentShader(shader_f);
            program.compile(this.filesystem, gl);
            this.shaders.put(style, program);
          } catch (final ConstraintError e) {
            SMVErrorBox.showError("Constraint error", e);
          } catch (final GLCompileException e) {
            SMVErrorBox.showError("Compilation error", e);
          } catch (final GLUnsupportedException e) {
            SMVErrorBox.showError("OpenGL unsupported error", e);
          }
        }
      }
    }

    private void renderGrid(
      final @Nonnull GLInterfaceCommon gl)
      throws GLException,
        ConstraintError
    {
      gl.blendingEnable(
        BlendFunction.BLEND_SOURCE_ALPHA,
        BlendFunction.BLEND_ONE_MINUS_SOURCE_ALPHA);

      MatrixM4x4F.setIdentity(this.matrix_model);
      MatrixM4x4F.multiply(
        this.matrix_view,
        this.matrix_model,
        this.matrix_modelview);

      final Program program =
        this.shaders.get(SMVRenderStyle.RENDER_STYLE_COLOR);

      program.activate(gl);
      try {
        final ProgramUniform u_mmview = program.getUniform("m_modelview");
        gl.programPutUniformMatrix4x4f(u_mmview, this.matrix_modelview);
        final ProgramUniform u_mproj = program.getUniform("m_projection");
        gl.programPutUniformMatrix4x4f(u_mproj, this.matrix_projection);

        final ProgramUniform u_fcolor = program.getUniform("color");
        gl.programPutUniformVector4f(u_fcolor, ViewRenderer.GRID_COLOR);

        final ProgramAttribute p_pos = program.getAttribute("v_position");

        final ArrayBuffer a = this.grid.getArrayBuffer();
        final ArrayBufferAttribute a_pos =
          a.getDescriptor().getAttribute("v_position");

        final IndexBuffer i = this.grid.getIndexBuffer();

        gl.arrayBufferBind(a);
        gl.arrayBufferBindVertexAttribute(a, a_pos, p_pos);
        gl.drawElements(Primitives.PRIMITIVE_LINES, i);

      } finally {
        program.deactivate(gl);
      }
    }

    private void renderAxes(
      final @Nonnull GLInterfaceCommon gl)
      throws GLException,
        ConstraintError
    {
      gl.blendingEnable(
        BlendFunction.BLEND_SOURCE_ALPHA,
        BlendFunction.BLEND_ONE_MINUS_SOURCE_ALPHA);

      MatrixM4x4F.setIdentity(this.matrix_model);
      MatrixM4x4F.multiply(
        this.matrix_view,
        this.matrix_model,
        this.matrix_modelview);

      final Program program =
        this.shaders.get(SMVRenderStyle.RENDER_STYLE_VERTEX_COLOR);

      program.activate(gl);
      try {
        final ProgramUniform u_mmview = program.getUniform("m_modelview");
        gl.programPutUniformMatrix4x4f(u_mmview, this.matrix_modelview);
        final ProgramUniform u_mproj = program.getUniform("m_projection");
        gl.programPutUniformMatrix4x4f(u_mproj, this.matrix_projection);

        final ProgramAttribute p_pos = program.getAttribute("v_position");
        final ProgramAttribute p_col = program.getAttribute("v_color");

        final ArrayBuffer a = this.axes.getArrayBuffer();
        final ArrayBufferAttribute a_pos =
          a.getDescriptor().getAttribute("v_position");
        final ArrayBufferAttribute a_col =
          a.getDescriptor().getAttribute("v_color");

        final IndexBuffer i = this.axes.getIndexBuffer();

        gl.arrayBufferBind(a);
        gl.arrayBufferBindVertexAttribute(a, a_pos, p_pos);
        gl.arrayBufferBindVertexAttribute(a, a_col, p_col);
        gl.drawElements(Primitives.PRIMITIVE_LINES, i);

      } finally {
        program.deactivate(gl);
      }
    }

    private void renderModel(
      final @Nonnull GLInterfaceCommon gl,
      final @Nonnull Model<ModelObjectVBO> model_actual,
      final @Nonnull Program program)
      throws ConstraintError,
        GLException
    {
      final SMVLightDirectional new_light =
        this.canvas.want_light.getAndSet(null);
      if (new_light != null) {
        this.light = new_light;
      }

      final VectorReadable3F position =
        this.canvas.want_model_position.getAndSet(null);
      if (position != null) {
        VectorM3F.copy(position, this.model_position);
      }

      final VectorReadable3F rotation =
        this.canvas.want_model_rotation.getAndSet(null);
      if (rotation != null) {
        VectorM3F.copy(rotation, this.model_rotation);
      }

      this.rotating_y.set(this.canvas.want_rotating_y.get());

      QuaternionM4F.makeFromAxisAngle(
        ViewRenderer.X_AXIS,
        Math.toRadians(this.model_rotation.getXF()),
        this.rotate_x);

      double y_radians = 0.0;
      if (this.rotating_y.get()) {
        final double plus = (this.frame * 0.1) % 360.0;
        y_radians = Math.toRadians(this.model_rotation.getYF() + plus);
      } else {
        y_radians = Math.toRadians(this.model_rotation.getYF());
      }

      QuaternionM4F.makeFromAxisAngle(
        ViewRenderer.Y_AXIS,
        y_radians,
        this.rotate_y);

      QuaternionM4F.makeFromAxisAngle(
        ViewRenderer.Z_AXIS,
        Math.toRadians(this.model_rotation.getZF()),
        this.rotate_z);

      this.model_orientation.x = 0;
      this.model_orientation.y = 0;
      this.model_orientation.z = 0;
      this.model_orientation.w = 1;

      QuaternionM4F.multiplyInPlace(this.model_orientation, this.rotate_z);
      QuaternionM4F.multiplyInPlace(this.model_orientation, this.rotate_y);
      QuaternionM4F.multiplyInPlace(this.model_orientation, this.rotate_x);

      if (this.rotating_y.get()) {
        final double frame_double = this.frame / 100.0;
        final QuaternionM4F temp = new QuaternionM4F();
        QuaternionM4F.makeFromAxisAngle(
          ViewRenderer.Y_AXIS,
          Math.toRadians(frame_double % 360),
          temp);
        QuaternionM4F.multiplyInPlace(this.model_orientation, temp);
      }

      final Option<ModelObjectVBO> om = model_actual.get(this.object);
      if (om.isNone()) {
        return;
      }

      final ModelObjectVBO m = ((Option.Some<ModelObjectVBO>) om).value;

      gl.blendingDisable();

      MatrixM4x4F.setIdentity(ViewRenderer.this.matrix_temp);
      QuaternionM4F.makeRotationMatrix4x4(
        ViewRenderer.this.model_orientation,
        ViewRenderer.this.matrix_temp);

      MatrixM4x4F.setIdentity(ViewRenderer.this.matrix_model);
      MatrixM4x4F.translateByVector3FInPlace(
        ViewRenderer.this.matrix_model,
        ViewRenderer.this.model_position);
      MatrixM4x4F.multiplyInPlace(
        ViewRenderer.this.matrix_model,
        ViewRenderer.this.matrix_temp);

      MatrixM4x4F.multiply(
        ViewRenderer.this.matrix_view,
        ViewRenderer.this.matrix_model,
        ViewRenderer.this.matrix_modelview);

      program.activate(gl);
      try {
        final ProgramUniform u_mmview = program.getUniform("m_modelview");
        gl.programPutUniformMatrix4x4f(
          u_mmview,
          ViewRenderer.this.matrix_modelview);
        final ProgramUniform u_mproj = program.getUniform("m_projection");
        gl.programPutUniformMatrix4x4f(
          u_mproj,
          ViewRenderer.this.matrix_projection);

        if (this.light != null) {
          final ProgramUniform u_lcolor = program.getUniform("l_color");
          if (u_lcolor != null) {
            gl.programPutUniformVector3f(u_lcolor, this.light.getColour());
          }
          final ProgramUniform u_ldirect = program.getUniform("l_direction");
          if (u_ldirect != null) {
            final VectorM4F light_eyespace =
              new VectorM4F(this.light.getDirection());
            MatrixM4x4F.multiplyVector4F(
              this.matrix_view,
              light_eyespace,
              light_eyespace);
            VectorM4F.normalizeInPlace(light_eyespace);
            gl.programPutUniformVector4f(u_ldirect, light_eyespace);
          }
          final ProgramUniform u_linten = program.getUniform("l_intensity");
          if (u_linten != null) {
            gl.programPutUniformFloat(u_linten, this.light.getIntensity());
          }
        }

        if (ViewRenderer.this.texture != null) {
          final ProgramUniform u_texture = program.getUniform("t_diffuse_0");
          if (u_texture != null) {
            gl.texture2DStaticBind(
              ViewRenderer.this.texture_units[0],
              ViewRenderer.this.texture);
            gl.programPutUniformTextureUnit(
              u_texture,
              ViewRenderer.this.texture_units[0]);
          }
        }

        final ProgramAttribute p_pos = program.getAttribute("v_position");
        final ProgramAttribute p_norm = program.getAttribute("v_normal");
        final ProgramAttribute p_uv = program.getAttribute("v_uv");

        final ArrayBuffer a = m.getArrayBuffer();
        gl.arrayBufferBind(a);

        final ArrayBufferAttribute a_pos =
          a.getDescriptor().getAttribute(
            ViewRenderer.NAME_POSITION_ATTRIBUTE.toString());
        gl.arrayBufferBindVertexAttribute(a, a_pos, p_pos);

        final ArrayBufferAttribute a_norm =
          a.getDescriptor().getAttribute(
            ViewRenderer.NAME_NORMAL_ATTRIBUTE.toString());
        if (p_norm != null) {
          gl.arrayBufferBindVertexAttribute(a, a_norm, p_norm);
        }

        if (a.getDescriptor().hasAttribute(
          ViewRenderer.NAME_UV_ATTRIBUTE.toString())) {
          final ArrayBufferAttribute a_uv =
            a.getDescriptor().getAttribute(
              ViewRenderer.NAME_UV_ATTRIBUTE.toString());
          if (p_uv != null) {
            gl.arrayBufferBindVertexAttribute(a, a_uv, p_uv);
          }
        }

        final IndexBuffer i = m.getIndexBuffer();
        gl.drawElements(Primitives.PRIMITIVE_TRIANGLES, i);

      } finally {
        try {
          program.deactivate(gl);
        } catch (final GLException e) {
          SMVErrorBox.showError("OpenGL error", e);
        } catch (final ConstraintError e) {
          SMVErrorBox.showError("Constraint error", e);
        }
      }
    }

    @Override public void reshape(
      final GLAutoDrawable drawable,
      final int x,
      final int y,
      final int width,
      final int height)
    {
      if (this.gi == null) {
        return;
      }

      try {
        this.makeProjectionMatrix(drawable.getWidth(), drawable.getHeight());
      } catch (final ConstraintError e) {
        SMVErrorBox.showError("Constraint error", e);
      }
    }

    private void setCurrentProgram()
    {
      if (this.program_current == null) {
        this.program_current =
          this.shaders.get(SMVRenderStyle.RENDER_STYLE_TEXTURED_FLAT);
      }

      final SMVRenderStyle style =
        this.canvas.want_render_style.getAndSet(null);
      if (style != null) {
        this.program_current = this.shaders.get(style);
      }
    }
  }

  private static final long serialVersionUID = -4066678161602346966L;

  static @Nonnull SMVGLCanvas makeCanvas(
    final @Nonnull SMVConfig config,
    final @Nonnull Log log)
    throws ConstraintError,
      FilesystemError
  {
    final GLProfile profile = SMVGLCanvas.makeProfile(config);
    final GLCapabilities caps = new GLCapabilities(profile);
    final SMVGLCanvas canvas = new SMVGLCanvas(caps, log);
    canvas.addGLEventListener(new ViewRenderer(canvas, log));
    canvas.addKeyListener(new ViewKeyListener(canvas, log));
    return canvas;
  }

  private static @Nonnull GLProfile makeProfile(
    @SuppressWarnings("unused") final @Nonnull SMVConfig config)
  {
    return GLProfile.getDefault();
  }

  private final @Nonnull Log                                                         log_canvas;
  protected final @Nonnull AtomicReference<String>                                   want_object;
  protected final @Nonnull AtomicReference<Pair<File, JComboBox<String>>>            want_load_model;
  protected final @Nonnull AtomicReference<ModelMaterial>                            want_load_material;
  protected final @Nonnull AtomicReference<SMVRenderStyle>                           want_render_style;
  protected final @Nonnull AtomicReference<Pair<VectorReadable3F, VectorReadable3F>> want_camera_set;
  protected final @Nonnull AtomicReference<VectorReadable3F>                         want_model_position;
  protected final @Nonnull AtomicReference<VectorReadable3F>                         want_model_rotation;
  protected final @Nonnull AtomicBoolean                                             want_rotating_y;
  protected final @Nonnull AtomicReference<SMVLightDirectional>                      want_light;

  private SMVGLCanvas(
    final @Nonnull GLCapabilitiesImmutable caps,
    final @Nonnull Log log)
  {
    super(caps);
    this.log_canvas = new Log(log, "canvas");
    this.want_load_model =
      new AtomicReference<Pair<File, JComboBox<String>>>();
    this.want_object = new AtomicReference<String>();
    this.want_load_material = new AtomicReference<ModelMaterial>();
    this.want_render_style = new AtomicReference<SMVRenderStyle>();
    this.want_camera_set =
      new AtomicReference<Pair<VectorReadable3F, VectorReadable3F>>();
    this.want_model_position = new AtomicReference<VectorReadable3F>();
    this.want_model_rotation = new AtomicReference<VectorReadable3F>();
    this.want_light = new AtomicReference<SMVLightDirectional>();
    this.want_rotating_y = new AtomicBoolean(false);
  }

  void loadModel(
    final @Nonnull File file,
    final @Nonnull JComboBox<String> tell)
  {
    this.log_canvas.info("Loading model " + file);
    this.want_load_model.set(new Pair<File, JComboBox<String>>(file, tell));
  }

  void selectRenderStyle(
    final @Nonnull SMVRenderStyle style)
  {
    this.log_canvas.info("Setting render style to " + style);
    this.want_render_style.set(style);
  }

  void selectObject(
    final @Nonnull String name)
  {
    this.log_canvas.info("Selecting object " + name);
    this.want_object.set(name);
  }

  void setCameraOriginTarget(
    final @Nonnull VectorReadable3F origin_new,
    final @Nonnull VectorReadable3F target_new)
  {
    this.log_canvas.info("New camera origin: " + origin_new);
    this.log_canvas.info("New camera target: " + target_new);

    this.want_camera_set.set(new Pair<VectorReadable3F, VectorReadable3F>(
      origin_new,
      target_new));
  }

  void setModelPosition(
    final @Nonnull VectorReadable3F position_new)
  {
    this.log_canvas.info("New model origin: " + position_new);
    this.want_model_position.set(position_new);
  }

  void setModelRotations(
    final @Nonnull VectorReadable3F rotation_new)
  {
    this.want_model_rotation.set(rotation_new);
  }

  void setMaterial(
    final ModelMaterial m)
  {
    this.want_load_material.set(m);
  }

  void setWandering(
    final boolean wandering)
  {
    this.want_rotating_y.set(wandering);
  }
}
