package com.io7m.jsom0;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jcanephora.ArrayBuffer;
import com.io7m.jcanephora.IndexBuffer;
import com.io7m.jtensors.VectorReadable3F;

public final class ModelObjectVBO extends ModelObject
{
  private final @Nonnull ArrayBuffer array_buffer;
  private final @Nonnull IndexBuffer index_buffer;

  public ModelObjectVBO(
    final @Nonnull String name,
    final @Nonnull String material_name,
    final @Nonnull VectorReadable3F bound_lower,
    final @Nonnull VectorReadable3F bound_upper,
    final @Nonnull ArrayBuffer array_buffer,
    final @Nonnull IndexBuffer index_buffer)
    throws ConstraintError
  {
    super(name, material_name, bound_lower, bound_upper);
    this.array_buffer =
      Constraints.constrainNotNull(array_buffer, "Array buffer");
    this.index_buffer =
      Constraints.constrainNotNull(index_buffer, "Index buffer");
  }

  public @Nonnull ArrayBuffer getArrayBuffer()
  {
    return this.array_buffer;
  }

  public @Nonnull IndexBuffer getIndexBuffer()
  {
    return this.index_buffer;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[ModelObjectVBO ");
    builder.append(this.getName());
    builder.append(" ");
    builder.append(this.getMaterialName());
    builder.append(" ");
    builder.append(this.array_buffer);
    builder.append(" ");
    builder.append(this.index_buffer);
    builder.append(" ");
    builder.append(this.getLowerBound());
    builder.append(" ");
    builder.append(this.getUpperBound());
    builder.append("]");
    return builder.toString();
  }
}
