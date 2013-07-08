package com.io7m.jsom0.demos;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jaux.functional.Function;
import com.io7m.jaux.functional.Option;
import com.io7m.jaux.functional.Option.Some;
import com.io7m.jaux.functional.Option.Type;
import com.io7m.jaux.functional.Unit;
import com.io7m.jcanephora.ArrayBuffer;
import com.io7m.jcanephora.ArrayBufferAttribute;
import com.io7m.jcanephora.BlendFunction;
import com.io7m.jcanephora.DepthFunction;
import com.io7m.jcanephora.FaceSelection;
import com.io7m.jcanephora.FaceWindingOrder;
import com.io7m.jcanephora.GLArrayBuffers;
import com.io7m.jcanephora.GLCompileException;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLIndexBuffers;
import com.io7m.jcanephora.GLInterfaceCommon;
import com.io7m.jcanephora.Primitives;
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
import com.io7m.jsom0.parser.ModelMaterialParser;
import com.io7m.jsom0.parser.ModelObjectParserVBOImmediate;
import com.io7m.jsom0.parser.ModelParser;
import com.io7m.jtensors.MatrixM3x3F;
import com.io7m.jtensors.MatrixM4x4F;
import com.io7m.jtensors.VectorI3F;
import com.io7m.jtensors.VectorM2F;
import com.io7m.jtensors.VectorM3F;
import com.io7m.jtensors.VectorM4F;
import com.io7m.jtensors.VectorReadable3F;
import com.io7m.jtensors.VectorReadable4F;
import com.io7m.jvvfs.Filesystem;
import com.io7m.jvvfs.FilesystemError;
import com.io7m.jvvfs.PathVirtual;

public final class ModelViewer
{
  private static final VectorReadable3F AXIS_Y;
  private static final VectorReadable3F AXIS_X;

  static {
    AXIS_X = new VectorI3F(1.0f, 0.0f, 0.0f);
    AXIS_Y = new VectorI3F(0.0f, 1.0f, 0.0f);
  }

  private static ModelMaterial loadMaterial(
    final String file,
    final Log log)
    throws IOException,
      ConstraintError,
      Error
  {
    final InputStream stream = new FileInputStream(file);
    final ModelMaterialParser parser =
      new ModelMaterialParser(file, stream, log);
    final ModelMaterial m = parser.modelMaterial();
    stream.close();
    return m;
  }

  private static
    <G extends GLArrayBuffers & GLIndexBuffers>
    Model<ModelObjectVBO>
    loadModel(
      final String file,
      final G gl,
      final Log log)
      throws IOException,
        Error,
        ConstraintError,
        GLException
  {
    final InputStream stream = new FileInputStream(file);
    final ModelObjectParserVBOImmediate<G> op =
      new ModelObjectParserVBOImmediate<G>(
        file,
        stream,
        new NamePositionAttribute("vertex_position"),
        new NameNormalAttribute("vertex_normal"),
        new NameUVAttribute("vertex_uv"),
        log,
        gl);

    final ModelParser<ModelObjectVBO, GLException> parser =
      new ModelParser<ModelObjectVBO, GLException>(op);

    final Model<ModelObjectVBO> m = parser.model();
    stream.close();
    return m;
  }

  private static ModelObjectVBO loadObject(
    final String object_name,
    final Model<ModelObjectVBO> m,
    final Log log)
    throws ConstraintError
  {
    final Option<ModelObjectVBO> oo = m.get(object_name);
    if (oo.type == Type.OPTION_NONE) {
      final StringBuilder builder = new StringBuilder();

      log.error("could not find object '" + object_name + "' in model");
      m.forEachObject(new Function<ModelObjectVBO, Unit>() {
        @Override public Unit call(
          final @Nonnull ModelObjectVBO x)
        {
          builder.append(x.getName());
          builder.append(" ");
          return new Unit();
        }
      });
      log.info("available objects in model: " + builder.toString());
      System.exit(1);
    }

    final Option.Some<ModelObjectVBO> so = (Some<ModelObjectVBO>) oo;
    return so.value;
  }

  private static ModelProgram loadShader(
    final ModelMaterial m,
    final Log log)
    throws ConstraintError
  {
    switch (m.texture.type) {
      case OPTION_NONE:
      {
        final ModelProgramFlat p = new ModelProgramFlat(log);
        p.addVertexShader(PathVirtual.ofString("/shaders/jsom0.v"));
        p.addFragmentShader(ModelViewer.selectShader(log, m));
        return p;
      }
      case OPTION_SOME:
      {
        final ModelProgramTextured p = new ModelProgramTextured(log);
        p.addVertexShader(PathVirtual.ofString("/shaders/jsom0.v"));
        p.addFragmentShader(ModelViewer.selectShader(log, m));
        return p;
      }
    }

    throw new UnreachableCodeException();
  }

  private static PathVirtual selectShader(
    final Log log,
    final ModelMaterial material)
    throws ConstraintError
  {
    switch (material.texture.type) {
      case OPTION_NONE:
      {
        log.debug("using untextured shader");
        return PathVirtual.ofString("/shaders/jsom0-untextured.f");
      }
      case OPTION_SOME:
      {
        final Option.Some<ModelTexture> some_texture =
          (Some<ModelTexture>) material.texture;
        switch (some_texture.value.mapping) {
          case MODEL_TEXTURE_MAPPING_CHROME:
            log.debug("using chrome shader");
            return PathVirtual.ofString("/shaders/jsom0-textured-chrome.f");
          case MODEL_TEXTURE_MAPPING_UV:
            log.debug("using uv shader");
            return PathVirtual.ofString("/shaders/jsom0-textured-uv.f");
        }
      }
    }

    throw new UnreachableCodeException();
  }

  private final @Nonnull Filesystem            fs;
  private final @Nonnull Log                   log;
  private final @Nonnull TextureUnit[]         texture_units;

  private final @Nonnull ModelMaterial         model_material;
  private final @Nonnull Model<ModelObjectVBO> model;
  private final @Nonnull ModelProgram          model_program;
  private final @Nonnull ModelObjectVBO        model_object;

  private final float                          texture_alpha;
  private final @Nonnull Texture2DStatic       texture;
  private final @Nonnull TextureLoaderImageIO  texture_loader;
  private final @Nonnull String                texture_directory;
  private final @Nonnull VectorReadable4F      background_color;
  private final @Nonnull MatrixM4x4F           matrix_view;
  private final @Nonnull MatrixM4x4F           matrix_projection;
  private final @Nonnull MatrixM4x4F           matrix_model;
  private final @Nonnull MatrixM4x4F           matrix_modelview;
  private final @Nonnull MatrixM3x3F           matrix_normal;
  private final @Nonnull VectorM3F             camera_position;
  private final @Nonnull VectorM3F             camera_inverse;
  private boolean                              model_wireframe = false;
  private final @Nonnull VectorM3F             model_position;
  private final @Nonnull VectorM2F             model_orientation;
  private final @Nonnull VectorM3F             light_position;

  ModelViewer(
    final @Nonnull GLInterfaceCommon g,
    final @Nonnull Log log,
    final @Nonnull String texture_directory,
    final @Nonnull String file_material,
    final @Nonnull String file_model,
    final @Nonnull String object_name)
    throws GLException,
      ConstraintError,
      IOException,
      Error,
      GLCompileException,
      FilesystemError
  {
    Constraints.constrainNotNull(texture_directory, "Texture directory");
    Constraints.constrainNotNull(file_material, "Material file");
    Constraints.constrainNotNull(file_model, "Model file");
    Constraints.constrainNotNull(object_name, "Object name");

    this.log = new Log(log, "model-viewer");
    this.fs = Filesystem.makeWithoutArchiveDirectory(this.log);
    this.fs.mountClasspathArchive(Model.class, PathVirtual.ROOT);
    this.fs.mountClasspathArchive(ModelViewer.class, PathVirtual.ROOT);

    this.texture_directory = texture_directory;
    this.texture_units = g.textureGetUnits();
    this.texture_loader = new TextureLoaderImageIO();

    this.model_material = ModelViewer.loadMaterial(file_material, this.log);
    this.model = ModelViewer.loadModel(file_model, g, this.log);
    this.model_object =
      ModelViewer.loadObject(object_name, this.model, this.log);
    this.model_program =
      ModelViewer.loadShader(this.model_material, this.log);
    this.model_program.compile(this.fs, g);

    if (this.model_material.texture.type == Type.OPTION_SOME) {
      final Option.Some<ModelTexture> some_texture =
        (Some<ModelTexture>) this.model_material.texture;
      final ModelTexture m_texture = some_texture.value;

      final StringBuilder texture_path = new StringBuilder();
      texture_path.append(this.texture_directory);
      texture_path.append(File.separatorChar);
      texture_path.append(m_texture.getName());

      final InputStream stream = new FileInputStream(texture_path.toString());
      this.texture =
        this.texture_loader.load2DStaticRGBA8888(
          g,
          TextureWrapS.TEXTURE_WRAP_REPEAT,
          TextureWrapT.TEXTURE_WRAP_REPEAT,
          TextureFilterMinification.TEXTURE_FILTER_NEAREST,
          TextureFilterMagnification.TEXTURE_FILTER_NEAREST,
          stream,
          m_texture.getName());
      stream.close();
      this.texture_alpha = m_texture.getAlpha();
    } else {
      this.texture = null;
      this.texture_alpha = 1.0f;
    }

    this.background_color = new VectorM4F(0.2f, 0.2f, 0.2f, 1.0f);
    this.camera_position = new VectorM3F(0.0f, 0.0f, 0.0f);
    this.camera_inverse = new VectorM3F(0.0f, 0.0f, 0.0f);
    this.model_position = new VectorM3F(0.0f, 0.0f, -8.0f);
    this.model_orientation = new VectorM2F((float) Math.PI / 2, 0.0f);

    this.light_position = new VectorM3F(8f, 8f, 8f);

    this.matrix_model = new MatrixM4x4F();
    this.matrix_modelview = new MatrixM4x4F();
    this.matrix_view = new MatrixM4x4F();
    this.matrix_projection = new MatrixM4x4F();
    this.matrix_normal = new MatrixM3x3F();

    ProjectionMatrix.makePerspective(
      this.matrix_projection,
      1.0f,
      1000.0f,
      640.0 / 480.0,
      Math.PI / 8.0);
  }

  private void drawViewedModel(
    final @Nonnull GLInterfaceCommon g)
    throws ConstraintError,
      GLException
  {
    this.model_program.activate(g);
    {
      /**
       * Produce model matrix: the translation and orientation of the model.
       */

      MatrixM4x4F.setIdentity(this.matrix_model);
      MatrixM4x4F.translateByVector3FInPlace(
        this.matrix_model,
        this.model_position);
      MatrixM4x4F.rotateInPlace(
        this.model_orientation.y,
        this.matrix_model,
        ModelViewer.AXIS_Y);
      MatrixM4x4F.rotateInPlace(
        this.model_orientation.x,
        this.matrix_model,
        ModelViewer.AXIS_X);

      /**
       * Produce modelview matrix (view * model).
       */

      MatrixM4x4F.multiply(
        this.matrix_view,
        this.matrix_model,
        this.matrix_modelview);

      /**
       * Produce normal matrix.
       */

      this.matrix_normal.set(0, 0, this.matrix_modelview.get(0, 0));
      this.matrix_normal.set(0, 1, this.matrix_modelview.get(0, 1));
      this.matrix_normal.set(0, 2, this.matrix_modelview.get(0, 2));
      this.matrix_normal.set(1, 0, this.matrix_modelview.get(1, 0));
      this.matrix_normal.set(1, 1, this.matrix_modelview.get(1, 1));
      this.matrix_normal.set(1, 2, this.matrix_modelview.get(1, 2));
      this.matrix_normal.set(2, 0, this.matrix_modelview.get(2, 0));
      this.matrix_normal.set(2, 1, this.matrix_modelview.get(2, 1));
      this.matrix_normal.set(2, 2, this.matrix_modelview.get(2, 2));
      MatrixM3x3F.invertInPlace(this.matrix_normal);
      MatrixM3x3F.transposeInPlace(this.matrix_normal);

      /**
       * Bind attributes.
       */

      final ArrayBuffer buffer = this.model_object.getArrayBuffer();
      final ArrayBufferAttribute bpa =
        buffer.getDescriptor().getAttribute("vertex_position");
      final ArrayBufferAttribute bna =
        buffer.getDescriptor().getAttribute("vertex_normal");

      g.arrayBufferBind(buffer);
      g.arrayBufferBindVertexAttribute(
        buffer,
        bpa,
        this.model_program.getPositionAttribute());
      g.arrayBufferBindVertexAttribute(
        buffer,
        bna,
        this.model_program.getNormalAttribute());

      /**
       * Set program parameters.
       */

      this.model_program.putModelMatrix(g, this.matrix_model);
      this.model_program.putViewMatrix(g, this.matrix_view);
      this.model_program.putProjectionMatrix(g, this.matrix_projection);
      this.model_program.putNormalMatrix(g, this.matrix_normal);
      this.model_program.putLightPosition(g, this.light_position);

      this.model_program.putAmbient(g, this.model_material.ambient);
      this.model_program.putDiffuse(g, this.model_material.diffuse);
      this.model_program.putSpecular(g, this.model_material.specular);
      this.model_program.putShininess(g, this.model_material.shininess);

      switch (this.model_material.texture.type) {
        case OPTION_SOME:
        {
          g.texture2DStaticBind(this.texture_units[0], this.texture);

          final ModelProgramTextured p =
            (ModelProgramTextured) this.model_program;
          p.putTexture(g, this.texture_units[0]);
          p.putTextureAlpha(g, this.texture_alpha);

          final ArrayBufferAttribute bua =
            buffer.getDescriptor().getAttribute("vertex_uv");
          g.arrayBufferBindVertexAttribute(buffer, bua, p.getUVAttribute());
          break;
        }
        case OPTION_NONE:
        default:
          break;
      }

      g.drawElements(
        Primitives.PRIMITIVE_TRIANGLES,
        this.model_object.getIndexBuffer());
    }
    this.model_program.deactivate(g);
  }

  public void moveCameraY(
    final float y)
  {
    this.camera_position.y += y;
    this.log.debug("camera position: " + this.camera_position);
  }

  public void moveCameraZ(
    final float z)
  {
    this.camera_position.z += z;
    this.log.debug("camera position: " + this.camera_position);
  }

  public void moveLightX(
    final float x)
  {
    this.light_position.x += x;
    this.log.debug("light position: " + this.light_position);
  }

  public void moveLightY(
    final float y)
  {
    this.light_position.y += y;
    this.log.debug("light position: " + this.light_position);
  }

  public void moveLightZ(
    final float z)
  {
    this.light_position.z += z;
    this.log.debug("light position: " + this.light_position);
  }

  public void render(
    final @Nonnull GLInterfaceCommon g)
    throws GLException,
      ConstraintError
  {
    g.depthBufferWriteEnable();
    g.depthBufferEnable(DepthFunction.DEPTH_LESS_THAN);
    g.depthBufferClear(1.0f);

    g.cullingEnable(
      FaceSelection.FACE_BACK,
      FaceWindingOrder.FRONT_FACE_COUNTER_CLOCKWISE);
    g.blendingEnable(
      BlendFunction.BLEND_SOURCE_ALPHA,
      BlendFunction.BLEND_ONE_MINUS_SOURCE_ALPHA);

    g.colorBufferClearV4f(this.background_color);

    /**
     * Produce view matrix: the inverse of the camera translation.
     */

    this.camera_inverse.x = -this.camera_position.x;
    this.camera_inverse.y = -this.camera_position.y;
    this.camera_inverse.z = -this.camera_position.z;

    MatrixM4x4F.setIdentity(this.matrix_view);
    MatrixM4x4F.translateByVector3FInPlace(
      this.matrix_view,
      this.camera_inverse);

    this.drawViewedModel(g);
  }

  public void rotateX(
    final float x)
  {
    this.model_orientation.x =
      (float) ((this.model_orientation.x + x) % (2 * Math.PI));
    this.log.debug("model orientation: " + this.model_orientation);
  }

  public void rotateY(
    final float y)
  {
    this.model_orientation.y =
      (float) ((this.model_orientation.y + y) % (2 * Math.PI));
    this.log.debug("model orientation: " + this.model_orientation);
  }

  public void toggleWireframe()
  {
    this.model_wireframe = !this.model_wireframe;
  }
}
