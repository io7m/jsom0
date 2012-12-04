package com.io7m.jsom0;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Option;
import com.io7m.jaux.functional.Option.Some;
import com.io7m.jtensors.VectorM3F;
import com.io7m.jtensors.VectorM4F;

public final class ModelMaterial
{
  public final @Nonnull String               name;
  public final @Nonnull VectorM3F            diffuse;
  public final @Nonnull VectorM3F            ambient;
  public final @Nonnull VectorM4F            specular;
  public float                               shininess;
  public final @Nonnull Option<ModelTexture> texture;
  public float                               alpha;

  private static final @Nonnull String       EOL;

  static {
    EOL = System.getProperty("line.separator");
  }

  private static final String vector3f(
    final VectorM3F v)
  {
    final StringBuilder b = new StringBuilder();
    b.append(v.x);
    b.append(" ");
    b.append(v.y);
    b.append(" ");
    b.append(v.z);
    return b.toString();
  }

  private static final String vector4f(
    final VectorM4F v)
  {
    final StringBuilder b = new StringBuilder();
    b.append(v.x);
    b.append(" ");
    b.append(v.y);
    b.append(" ");
    b.append(v.z);
    b.append(" ");
    b.append(v.w);
    return b.toString();
  }

  public ModelMaterial(
    final @Nonnull String name)
    throws ConstraintError
  {
    this.name = Constraints.constrainNotNull(name, "name");
    this.diffuse = new VectorM3F(0.0f, 0.0f, 1.0f);
    this.ambient = new VectorM3F(0.1f, 0.1f, 0.1f);
    this.specular = new VectorM4F(1.0f, 1.0f, 1.0f, 1.0f);
    this.shininess = 10.0f;
    this.alpha = 1.0f;
    this.texture = new Option.None<ModelTexture>();
  }

  public ModelMaterial(
    final @Nonnull String name,
    final @Nonnull ModelTexture texture)
    throws ConstraintError
  {
    this.name = Constraints.constrainNotNull(name, "name");
    this.diffuse = new VectorM3F(0.0f, 0.0f, 1.0f);
    this.ambient = new VectorM3F(0.1f, 0.1f, 0.1f);
    this.specular = new VectorM4F(1.0f, 1.0f, 1.0f, 1.0f);
    this.shininess = 10.0f;
    this.alpha = 1.0f;
    this.texture =
      new Some<ModelTexture>(Constraints.constrainNotNull(texture, "texture"));
  }

  @Override public String toString()
  {
    assert ModelMaterial.EOL != null;

    final StringBuilder b = new StringBuilder();
    b.append("material;\n");
    b.append("  name      \"" + this.name + "\";");
    b.append(ModelMaterial.EOL);

    b.append("  diffuse   ");
    b.append(ModelMaterial.vector3f(this.diffuse));
    b.append(";");
    b.append(ModelMaterial.EOL);

    b.append("  ambient   ");
    b.append(ModelMaterial.vector3f(this.ambient));
    b.append(";");
    b.append(ModelMaterial.EOL);

    b.append("  specular  ");
    b.append(ModelMaterial.vector4f(this.specular));
    b.append(";");
    b.append(ModelMaterial.EOL);

    b.append("  shininess ");
    b.append(this.shininess);
    b.append(";\n");

    b.append("  alpha ");
    b.append(this.alpha);
    b.append(";");
    b.append(ModelMaterial.EOL);

    switch (this.texture.type) {
      case OPTION_SOME:
      {
        final Some<ModelTexture> s = (Some<ModelTexture>) this.texture;
        b.append(s.value);
        break;
      }
      case OPTION_NONE:
        break;
    }

    b.append("end;");
    b.append(ModelMaterial.EOL);
    return b.toString();
  }
}
