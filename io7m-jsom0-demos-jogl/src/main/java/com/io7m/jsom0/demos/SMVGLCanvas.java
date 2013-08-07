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
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
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
import com.io7m.jcanephora.FragmentShader;
import com.io7m.jcanephora.IndexBuffer;
import com.io7m.jcanephora.JCGLCompileException;
import com.io7m.jcanephora.JCGLException;
import com.io7m.jcanephora.JCGLImplementationJOGL;
import com.io7m.jcanephora.JCGLInterfaceCommon;
import com.io7m.jcanephora.JCGLMeta;
import com.io7m.jcanephora.JCGLSLVersion;
import com.io7m.jcanephora.JCGLShadersCommon;
import com.io7m.jcanephora.JCGLUnsupportedException;
import com.io7m.jcanephora.Primitives;
import com.io7m.jcanephora.ProgramReference;
import com.io7m.jcanephora.ProjectionMatrix;
import com.io7m.jcanephora.ShaderUtilities;
import com.io7m.jcanephora.Texture2DStatic;
import com.io7m.jcanephora.TextureFilterMagnification;
import com.io7m.jcanephora.TextureFilterMinification;
import com.io7m.jcanephora.TextureLoaderImageIO;
import com.io7m.jcanephora.TextureUnit;
import com.io7m.jcanephora.TextureWrapS;
import com.io7m.jcanephora.TextureWrapT;
import com.io7m.jcanephora.VertexShader;
import com.io7m.jcanephora.checkedexec.JCCEExecutionAbstract;
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
import com.io7m.jtensors.VectorI2F;
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
    protected static final @Nonnull NameUVAttribute               NAME_UV_ATTRIBUTE;
    protected static final @Nonnull NameNormalAttribute           NAME_NORMAL_ATTRIBUTE;
    protected static final @Nonnull NamePositionAttribute         NAME_POSITION_ATTRIBUTE;
    protected static final @Nonnull VectorReadable4F              GRID_COLOR;
    protected static final @Nonnull VectorReadable3F              X_AXIS;
    protected static final @Nonnull VectorReadable3F              Y_AXIS;
    protected static final @Nonnull VectorReadable3F              Z_AXIS;

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

    private final @Nonnull SMVGLCanvas                            canvas;
    private @CheckForNull JCGLImplementationJOGL                  gi;
    private final @Nonnull Log                                    log;
    private final @Nonnull AtomicReference<Model<ModelObjectVBO>> model;
    protected @CheckForNull String                                object;
    private final @Nonnull AtomicReference<ModelMaterial>         material;
    private final @Nonnull FSCapabilityRead                       filesystem;
    private final @Nonnull Map<SMVRenderStyle, ProgramReference>  shaders;
    private int                                                   frame = 0;
    private @CheckForNull ProgramReference                        program_current;
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
    private boolean                                               renderer_ok;

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
      this.shaders = new HashMap<SMVRenderStyle, ProgramReference>();

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
      this.renderer_ok = true;
    }

    @Override public void display(
      final @Nonnull GLAutoDrawable drawable)
    {
      ++this.frame;

      if (!this.renderer_ok) {
        return;
      }

      try {
        final JCGLInterfaceCommon gl = this.gi.getGLCommon();

        this.setCurrentProgram();
        this.makeProjectionMatrix(drawable.getWidth(), drawable.getHeight());
        this.makeCameraViewMatrix();

        this.reloadMaterialIfRequested(gl);
        this.reloadModelIfRequested(gl);
        this.selectObject();
        this.setCurrentProgram();
        this.makeProjectionMatrix(drawable.getWidth(), drawable.getHeight());
        this.makeCameraViewMatrix();

        gl.colorBufferClear3f(0.0f, 0.0f, 0.0f);
        gl.depthBufferWriteEnable();
        gl.depthBufferClear(1.0f);
        gl.depthBufferWriteDisable();
        gl.depthBufferTestDisable();

        this.renderGrid(gl);
        this.renderAxes(gl);

        gl.depthBufferWriteEnable();
        gl.depthBufferTestEnable(DepthFunction.DEPTH_LESS_THAN);

        final Model<ModelObjectVBO> model_actual = this.model.get();
        if (model_actual != null) {
          this.renderModel(gl, model_actual, this.program_current);
        }

      } catch (final JCGLException e) {
        this.fatalError("OpenGL error", e);
      } catch (final ConstraintError e) {
        this.fatalError("Constraint error", e);
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
        this.gi = new JCGLImplementationJOGL(drawable.getContext(), this.log);
        final JCGLInterfaceCommon gl = this.gi.getGLCommon();

        this.compilePrograms(gl, gl);
        this.grid = new SMVVisibleGridPlane(gl, 50, 0, 50);
        this.axes = new SMVVisibleAxes(gl, 50, 50, 50);
        this.texture_units = gl.textureGetUnits();
      } catch (final JCGLException e) {
        this.fatalError("OpenGL error", e);
      } catch (final JCGLUnsupportedException e) {
        this.fatalError("OpenGL unsupported error", e);
      } catch (final ConstraintError e) {
        this.fatalError("Constraint error", e);
      }
    }

    private void fatalError(
      final @Nonnull String message,
      final @Nonnull Throwable e)
    {
      SMVErrorBox.showError(message, e);
      this.renderer_ok = false;
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
      final @Nonnull JCGLInterfaceCommon gl)
    {
      final ModelMaterial new_material =
        this.canvas.want_load_material.getAndSet(null);

      if (new_material != null) {
        switch (new_material.texture.type) {
          case OPTION_NONE:
          {
            try {
              if (this.texture != null) {
                gl.texture2DStaticDelete(this.texture);
              }
              this.texture = null;
            } catch (final JCGLException e) {
              this.fatalError("OpenGL error", e);
            } catch (final ConstraintError e) {
              this.fatalError("Constraint error", e);
            }
            break;
          }
          case OPTION_SOME:
          {
            final Some<ModelTexture> texture_s =
              (Option.Some<ModelTexture>) new_material.texture;

            FileInputStream stream = null;
            final String tname = texture_s.value.name;

            try {
              stream = new FileInputStream(tname);

              final TextureLoaderImageIO loader = new TextureLoaderImageIO();
              final Texture2DStatic new_texture =
                loader.load2DStaticInferredCommon(
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
              this.fatalError("I/O error", e);
            } catch (final ConstraintError e) {
              this.fatalError("Constraint error", e);
            } catch (final JCGLException e) {
              this.fatalError("OpenGL error", e);
            } finally {
              if (stream != null) {
                try {
                  stream.close();
                } catch (final IOException e) {
                  this.fatalError("I/O error", e);
                }
              }
            }

            break;
          }
        }
      }
    }

    private void reloadModelIfRequested(
      final @Nonnull JCGLInterfaceCommon gl)
      throws JCGLException
    {
      final Pair<File, JComboBox<String>> pair =
        this.canvas.want_load_model.getAndSet(null);
      if (pair != null) {
        final File file = pair.first;
        final JComboBox<String> tell = pair.second;

        FileInputStream stream = null;

        try {
          stream = new FileInputStream(file);

          final ModelObjectParser<ModelObjectVBO, JCGLException> object_parser =
            new ModelObjectParserVBOImmediate<JCGLInterfaceCommon>(
              file.toString(),
              stream,
              ViewRenderer.NAME_POSITION_ATTRIBUTE,
              ViewRenderer.NAME_NORMAL_ATTRIBUTE,
              ViewRenderer.NAME_UV_ATTRIBUTE,
              this.log,
              gl);

          final ModelParser<ModelObjectVBO, JCGLException> model_parser =
            new ModelParser<ModelObjectVBO, JCGLException>(object_parser);

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
          this.fatalError("I/O error", e);
        } catch (final Error e) {
          this.fatalError("Parse error", e);
        } catch (final ConstraintError e) {
          this.fatalError("Constraint error", e);
        } finally {
          if (stream != null) {
            try {
              stream.close();
            } catch (final IOException e) {
              this.fatalError("I/O error", e);
            }
          }
        }
      }
    }

    private void compilePrograms(
      final @Nonnull JCGLMeta gm,
      final @Nonnull JCGLShadersCommon gs)
    {
      try {
        final StringBuilder name = new StringBuilder();
        final JCGLSLVersion version = gm.metaGetSLVersion();

        for (final SMVRenderStyle style : SMVRenderStyle.values()) {
          FragmentShader fs = null;
          VertexShader vs = null;

          {
            InputStream stream = null;

            try {
              name.setLength(0);
              name.append(style.getName());
              name.append("-v");

              final PathVirtual path =
                SMVShaderPaths.getShader(version, "standard.v");
              this.log.debug("Compiling " + path);
              stream = this.filesystem.openFile(path);

              final List<String> lines = ShaderUtilities.readLines(stream);
              vs = gs.vertexShaderCompile(name.toString(), lines);
            } finally {
              if (stream != null) {
                stream.close();
                stream = null;
              }
            }
          }

          {
            InputStream stream = null;

            try {
              name.setLength(0);
              name.append(style.getName());
              name.append("-f");

              final PathVirtual path =
                SMVShaderPaths.getShader(version, style.getName() + ".f");
              this.log.debug("Compiling " + path);
              stream = this.filesystem.openFile(path);

              final List<String> lines = ShaderUtilities.readLines(stream);
              fs = gs.fragmentShaderCompile(name.toString(), lines);
            } finally {
              if (stream != null) {
                stream.close();
                stream = null;
              }
            }
          }

          assert fs != null;
          assert vs != null;

          final ProgramReference p =
            gs.programCreateCommon(style.getName(), vs, fs);

          gs.vertexShaderDelete(vs);
          gs.fragmentShaderDelete(fs);
          this.shaders.put(style, p);
        }

      } catch (final JCGLException e) {
        this.fatalError("OpenGL error", e);
      } catch (final JCGLCompileException e) {
        this.fatalError("Compilation error", e);
      } catch (final ConstraintError e) {
        this.fatalError("Constraint error", e);
      } catch (final JCGLUnsupportedException e) {
        this.fatalError("OpenGL unsupported error", e);
      } catch (final IOException e) {
        this.fatalError("I/O error", e);
      } catch (final FilesystemError e) {
        this.fatalError("Filesystem error", e);
      }
    }

    private void renderGrid(
      final @Nonnull JCGLInterfaceCommon gl)
    {
      try {
        gl.blendingEnable(
          BlendFunction.BLEND_SOURCE_ALPHA,
          BlendFunction.BLEND_ONE_MINUS_SOURCE_ALPHA);

        MatrixM4x4F.setIdentity(this.matrix_model);
        MatrixM4x4F.multiply(
          this.matrix_view,
          this.matrix_model,
          this.matrix_modelview);

        final ProgramReference program =
          this.shaders.get(SMVRenderStyle.RENDER_STYLE_COLOR);

        final ArrayBuffer a = this.grid.getArrayBuffer();
        gl.arrayBufferBind(a);
        final ArrayBufferAttribute a_pos = a.getAttribute("v_position");
        final IndexBuffer i = this.grid.getIndexBuffer();

        final JCCEExecutionAbstract exec =
          new JCCEExecutionAbstract(program) {
            @Override protected void execRunActual()
              throws JCGLException,
                ConstraintError
            {
              gl.drawElements(Primitives.PRIMITIVE_LINES, i);
            }
          };

        exec.execPrepare(gl);
        exec.execUniformPutMatrix4x4F(
          gl,
          "m_modelview",
          this.matrix_modelview);
        exec.execUniformPutMatrix4x4F(
          gl,
          "m_projection",
          this.matrix_projection);
        exec.execUniformPutVector4F(gl, "color", ViewRenderer.GRID_COLOR);
        exec.execAttributeBind(gl, "v_position", a_pos);
        exec.execRun(gl);
      } catch (final Throwable e) {
        this.fatalError("Error", e);
      }
    }

    private void renderAxes(
      final @Nonnull JCGLInterfaceCommon gl)
    {
      try {
        gl.blendingEnable(
          BlendFunction.BLEND_SOURCE_ALPHA,
          BlendFunction.BLEND_ONE_MINUS_SOURCE_ALPHA);

        MatrixM4x4F.setIdentity(this.matrix_model);
        MatrixM4x4F.multiply(
          this.matrix_view,
          this.matrix_model,
          this.matrix_modelview);

        final ProgramReference program =
          this.shaders.get(SMVRenderStyle.RENDER_STYLE_VERTEX_COLOR);

        final ArrayBuffer a = this.axes.getArrayBuffer();
        gl.arrayBufferBind(a);

        final ArrayBufferAttribute a_pos = a.getAttribute("v_position");
        final ArrayBufferAttribute a_col = a.getAttribute("v_color");
        final IndexBuffer i = this.axes.getIndexBuffer();

        final JCCEExecutionAbstract e = new JCCEExecutionAbstract(program) {
          @Override protected void execRunActual()
            throws JCGLException,
              ConstraintError
          {
            gl.drawElements(Primitives.PRIMITIVE_LINES, i);
          }
        };

        e.execPrepare(gl);
        e.execUniformPutMatrix4x4F(gl, "m_modelview", this.matrix_modelview);
        e
          .execUniformPutMatrix4x4F(
            gl,
            "m_projection",
            this.matrix_projection);
        e.execAttributeBind(gl, "v_position", a_pos);
        e.execAttributeBind(gl, "v_color", a_col);
        e.execRun(gl);
      } catch (final Throwable e) {
        this.fatalError("Error", e);
      }
    }

    private void renderModel(
      final @Nonnull JCGLInterfaceCommon gl,
      final @Nonnull Model<ModelObjectVBO> model_actual,
      final @Nonnull ProgramReference program)
    {
      try {
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

        final IndexBuffer i = m.getIndexBuffer();
        final ArrayBuffer a = m.getArrayBuffer();
        gl.arrayBufferBind(a);

        final JCCEExecutionAbstract e = new JCCEExecutionAbstract(program) {
          @Override protected void execRunActual()
            throws JCGLException,
              ConstraintError
          {
            gl.drawElements(Primitives.PRIMITIVE_TRIANGLES, i);
          }
        };

        final float light_intensity =
          this.light != null ? this.light.getIntensity() : 1.0f;

        final VectorReadable3F light_colour =
          (this.light != null) ? this.light.getColour() : new VectorI3F(
            1.0f,
            1.0f,
            1.0f);

        final VectorM4F light_eyespace =
          new VectorM4F(this.light != null
            ? this.light.getDirection()
            : new VectorM4F(0, 0, -1, 0));

        MatrixM4x4F.multiplyVector4F(
          this.matrix_view,
          light_eyespace,
          light_eyespace);
        VectorM4F.normalizeInPlace(light_eyespace);

        if (this.texture != null) {
          gl.texture2DStaticBind(this.texture_units[0], this.texture);
        }

        final MatrixM4x4F mmview = this.matrix_modelview;
        final MatrixM4x4F mmproj = this.matrix_projection;

        e.execPrepare(gl);
        e.execUniformPutMatrix4x4F(gl, "m_modelview", mmview);
        e.execUniformPutMatrix4x4F(gl, "m_projection", mmproj);
        if (program.getUniforms().containsKey("l_intensity")) {
          e.execUniformPutFloat(gl, "l_intensity", light_intensity);
        }
        if (program.getUniforms().containsKey("l_color")) {
          e.execUniformPutVector3F(gl, "l_color", light_colour);
        }
        if (program.getUniforms().containsKey("l_direction")) {
          e.execUniformPutVector4F(gl, "l_direction", light_eyespace);
        }
        if (program.getUniforms().containsKey("t_diffuse_0")) {
          e.execUniformPutTextureUnit(
            gl,
            "t_diffuse_0",
            this.texture_units[0]);
        }

        e.execAttributeBind(
          gl,
          "v_position",
          a.getAttribute(ViewRenderer.NAME_POSITION_ATTRIBUTE.toString()));

        if (program.getAttributes().containsKey("v_normal")) {
          e.execAttributeBind(
            gl,
            "v_normal",
            a.getAttribute(ViewRenderer.NAME_NORMAL_ATTRIBUTE.toString()));
        }

        if (program.getAttributes().containsKey("v_uv")) {
          if (a.hasAttribute(ViewRenderer.NAME_UV_ATTRIBUTE.toString())) {
            e.execAttributeBind(
              gl,
              "v_uv",
              a.getAttribute(ViewRenderer.NAME_UV_ATTRIBUTE.toString()));
          } else {
            e.execAttributePutVector2F(gl, "v_uv", VectorI2F.ZERO);
          }
        }

        e.execRun(gl);

      } catch (final Throwable e) {
        this.fatalError("Error", e);
      }
    }

    @Override public void reshape(
      final GLAutoDrawable drawable,
      final int x,
      final int y,
      final int width,
      final int height)
    {
      if ((this.gi == null) || !this.renderer_ok) {
        return;
      }

      try {
        this.makeProjectionMatrix(drawable.getWidth(), drawable.getHeight());
      } catch (final ConstraintError e) {
        this.fatalError("Constraint error", e);
      }
    }

    private void setCurrentProgram()
    {
      if (this.program_current == null) {
        this.program_current =
          this.shaders.get(SMVRenderStyle.RENDER_STYLE_NORMALS_ONLY);
      }

      final SMVRenderStyle style =
        this.canvas.want_render_style.getAndSet(null);
      if (style != null) {
        this.program_current = this.shaders.get(style);
      }

      assert this.program_current != null;
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
