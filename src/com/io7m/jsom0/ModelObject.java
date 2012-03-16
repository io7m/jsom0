package com.io7m.jsom0;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jcanephora.ArrayBuffer;
import com.io7m.jcanephora.IndexBuffer;

public final class ModelObject
{
  private final @Nonnull String      material_name;
  private final @Nonnull String      name;
  private final @Nonnull ArrayBuffer array_id;
  private final @Nonnull IndexBuffer index_id;

  private static final String        EOL = System
                                           .getProperty("line.separator");

  public ModelObject(
    final @Nonnull String name,
    final @Nonnull String material_name,
    final @Nonnull ArrayBuffer vertices_buffer,
    final @Nonnull IndexBuffer index_buffer)
    throws ConstraintError
  {
    this.name = Constraints.constrainNotNull(name, "Object name");
    this.material_name =
      Constraints.constrainNotNull(material_name, "Material name");
    this.array_id =
      Constraints.constrainNotNull(vertices_buffer, "Vertex buffer ID");
    this.index_id =
      Constraints.constrainNotNull(index_buffer, "Index buffer ID");
  }

  public @Nonnull ArrayBuffer getArrayBuffer()
  {
    return this.array_id;
  }

  public @Nonnull IndexBuffer getIndexBuffer()
  {
    return this.index_id;
  }

  public @Nonnull String getMaterialName()
  {
    return this.material_name;
  }

  public @Nonnull String getName()
  {
    return this.name;
  }

  @Override public @Nonnull String toString()
  {
    assert ModelObject.EOL != null;

    final StringBuilder b = new StringBuilder();
    b.append("object;");
    b.append(ModelObject.EOL);

    b.append("  name          \"");
    b.append(this.getName());
    b.append("\";");
    b.append(ModelObject.EOL);

    b.append("  material_name \"");
    b.append(this.getMaterialName());
    b.append("\";");
    b.append(ModelObject.EOL);

    b.append("  vertices;");
    b.append(ModelObject.EOL);
    b.append("  -- "
      + this.getArrayBuffer().getElements()
      + " elements omitted");
    b.append(";");
    b.append(ModelObject.EOL);
    b.append("  end;");
    b.append(ModelObject.EOL);

    b.append("  triangles;");
    b.append(ModelObject.EOL);
    b.append("  -- "
      + this.getIndexBuffer().getElements()
      + " elements omitted");
    b.append(";");
    b.append(ModelObject.EOL);
    b.append("  end;");
    b.append(ModelObject.EOL);

    b.append("end;");
    b.append(ModelObject.EOL);
    return b.toString();
  }
}
