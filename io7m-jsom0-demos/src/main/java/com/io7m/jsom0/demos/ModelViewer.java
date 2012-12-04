package com.io7m.jsom0.demos;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
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
import com.io7m.jcanephora.GLCompileException;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLInterfaceEmbedded;
import com.io7m.jcanephora.Primitives;
import com.io7m.jcanephora.ProjectionMatrix;
import com.io7m.jcanephora.Texture2DStatic;
import com.io7m.jcanephora.TextureFilter;
import com.io7m.jcanephora.TextureLoaderImageIO;
import com.io7m.jcanephora.TextureType;
import com.io7m.jcanephora.TextureUnit;
import com.io7m.jcanephora.TextureWrap;
import com.io7m.jlog.Log;
import com.io7m.jsom0.Model;
import com.io7m.jsom0.ModelMaterial;
import com.io7m.jsom0.ModelObjectVBO;
import com.io7m.jsom0.ModelTexture;
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

  private static Model<ModelObjectVBO> loadModel(
    final String file,
    final GLInterfaceEmbedded gl,
    final Log log)
    throws IOException,
      Error,
      ConstraintError,
      GLException
  {
    final InputStream stream = new FileInputStream(file);
    final ModelObjectParserVBOImmediate op =
      new ModelObjectParserVBOImmediate(file, stream, log, gl);

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
        p.addVertexShader(new PathVirtual("/com/io7m/jsom0/shaders/jsom0.v"));
        p.addFragmentShader(ModelViewer.selectShader(log, m));
        return p;
      }
      case OPTION_SOME:
      {
        final ModelProgramTextured p = new ModelProgramTextured(log);
        p.addVertexShader(new PathVirtual("/com/io7m/jsom0/shaders/jsom0.v"));
        p.addFragmentShader(ModelViewer.selectShader(log, m));
        return p;
      }
    }

    throw new AssertionError("unreachable code: report this bug!");
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
        return new PathVirtual("/com/io7m/jsom0/shaders/jsom0-untextured.f");
      }
      case OPTION_SOME:
      {
        final Option.Some<ModelTexture> some_texture =
          (Some<ModelTexture>) material.texture;
        switch (some_texture.value.mapping) {
          case MODEL_TEXTURE_MAPPING_CHROME:
            log.debug("using chrome shader");
            return new PathVirtual(
              "/com/io7m/jsom0/shaders/jsom0-textured-chrome.f");
          case MODEL_TEXTURE_MAPPING_UV:
            log.debug("using uv shader");
            return new PathVirtual(
              "/com/io7m/jsom0/shaders/jsom0-textured-uv.f");
        }
      }
    }

    /* UNREACHABLE */
    throw new AssertionError("unreachable code: report this bug!");
  }

  private final @Nonnull Filesystem            fs;
  private final @Nonnull Log                   log;
  private final @Nonnull TextureUnit[]         texture_units;
  private final @Nonnull ModelMaterial         material;
  private final @Nonnull Model<ModelObjectVBO> model;
  private final @Nonnull ModelProgram          program;
  private final @Nonnull ModelObjectVBO        object;
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
    final @Nonnull GLInterfaceEmbedded g,
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
    this.fs = new Filesystem(this.log);
    this.fs.mountUnsafeClasspathItem(Model.class, new PathVirtual("/"));

    this.texture_directory = texture_directory;
    this.texture_units = g.textureGetUnits();
    this.texture_loader = new TextureLoaderImageIO();

    this.material = ModelViewer.loadMaterial(file_material, this.log);
    this.model = ModelViewer.loadModel(file_model, g, this.log);
    this.object = ModelViewer.loadObject(object_name, this.model, this.log);

    this.program = ModelViewer.loadShader(this.material, this.log);
    this.program.compile(this.fs, g);

    if (this.material.texture.type == Type.OPTION_SOME) {
      final Option.Some<ModelTexture> some_texture =
        (Some<ModelTexture>) this.material.texture;
      final ModelTexture m_texture = some_texture.value;

      final StringBuilder texture_path = new StringBuilder();
      texture_path.append(this.texture_directory);
      texture_path.append(File.separatorChar);
      texture_path.append(m_texture.getName());

      final InputStream stream = new FileInputStream(texture_path.toString());
      this.texture =
        this.texture_loader.load2DStaticSpecific(
          g,
          TextureType.TEXTURE_TYPE_RGBA_8888_4BPP,
          TextureWrap.TEXTURE_WRAP_REPEAT,
          TextureWrap.TEXTURE_WRAP_REPEAT,
          TextureFilter.TEXTURE_FILTER_NEAREST,
          TextureFilter.TEXTURE_FILTER_NEAREST,
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
    this.light_position = new VectorM3F(16.0f, 16.0f, 0.0f);

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

  public void render(
    final @Nonnull GLInterfaceEmbedded g)
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

    g.lineSmoothingEnable();

    g.colorBufferClearV4f(this.background_color);

    /*
     * Select rendering style.
     */

    // if (this.model_wireframe) {
    // g.polygonSetMode(FaceSelection.FACE_FRONT, PolygonMode.POLYGON_LINES);
    // g.lineSetWidth(2.0f);
    // } else {
    // g.polygonSetMode(FaceSelection.FACE_FRONT, PolygonMode.POLYGON_FILL);
    // }

    /*
     * Produce view matrix: the inverse of the camera translation.
     */

    this.camera_inverse.x = -this.camera_position.x;
    this.camera_inverse.y = -this.camera_position.y;
    this.camera_inverse.z = -this.camera_position.z;

    MatrixM4x4F.setIdentity(this.matrix_view);
    MatrixM4x4F.translateByVector3FInPlace(
      this.matrix_view,
      this.camera_inverse);

    this.program.activate(g);
    {
      /*
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

      /*
       * Produce modelview matrix (view * model).
       */

      MatrixM4x4F.multiply(
        this.matrix_view,
        this.matrix_model,
        this.matrix_modelview);

      /*
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

      /*
       * Bind attributes.
       */

      final ArrayBuffer buffer = this.object.getArrayBuffer();
      final ArrayBufferAttribute bpa =
        buffer.getDescriptor().getAttribute("position");
      final ArrayBufferAttribute bna =
        buffer.getDescriptor().getAttribute("normal");

      g.arrayBufferBind(buffer);
      g.arrayBufferBindVertexAttribute(
        buffer,
        bpa,
        this.program.getPositionAttribute());
      g.arrayBufferBindVertexAttribute(
        buffer,
        bna,
        this.program.getNormalAttribute());

      /*
       * Set program parameters.
       */

      this.program.putModelviewMatrix(g, this.matrix_modelview);
      this.program.putProjectionMatrix(g, this.matrix_projection);
      this.program.putNormalMatrix(g, this.matrix_normal);
      this.program.putAmbient(g, this.material.ambient);
      this.program.putDiffuse(g, this.material.diffuse);
      this.program.putSpecular(g, this.material.specular);
      this.program.putShininess(g, this.material.shininess);
      this.program.putAlpha(g, 1.0f);
      this.program.putLightPosition(g, this.light_position);

      switch (this.material.texture.type) {
        case OPTION_SOME:
        {
          g.texture2DStaticBind(this.texture_units[0], this.texture);

          final ModelProgramTextured p = (ModelProgramTextured) this.program;
          p.putTexture(g, this.texture_units[0]);
          p.putTextureAlpha(g, this.texture_alpha);

          final ArrayBufferAttribute bua =
            buffer.getDescriptor().getAttribute("uv");
          g.arrayBufferBindVertexAttribute(buffer, bua, p.getUVAttribute());
          break;
        }
        case OPTION_NONE:
        default:
          break;
      }

      g.drawElements(
        Primitives.PRIMITIVE_TRIANGLES,
        this.object.getIndexBuffer());
    }
    this.program.deactivate(g);
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
