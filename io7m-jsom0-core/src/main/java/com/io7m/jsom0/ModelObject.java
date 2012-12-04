package com.io7m.jsom0;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jtensors.VectorReadable3F;

public abstract class ModelObject implements Comparable<ModelObject>
{
  private final @Nonnull String           material_name;
  private final @Nonnull String           name;
  private final @Nonnull VectorReadable3F bound_lower;
  private final @Nonnull VectorReadable3F bound_upper;

  protected ModelObject(
    final @Nonnull String name,
    final @Nonnull String material_name,
    final @Nonnull VectorReadable3F bound_lower,
    final @Nonnull VectorReadable3F bound_upper)
    throws ConstraintError
  {
    this.name = Constraints.constrainNotNull(name, "Object name");
    this.material_name =
      Constraints.constrainNotNull(material_name, "Material name");
    this.bound_lower =
      Constraints.constrainNotNull(bound_lower, "Lower bound");
    this.bound_upper =
      Constraints.constrainNotNull(bound_upper, "Upper bound");
  }

  @Override public final int compareTo(
    final ModelObject other)
  {
    return this.name.compareTo(other.name);
  }

  public final @Nonnull VectorReadable3F getLowerBound()
  {
    return this.bound_lower;
  }

  public final @Nonnull String getMaterialName()
  {
    return this.material_name;
  }

  public final @Nonnull String getName()
  {
    return this.name;
  }

  public final @Nonnull VectorReadable3F getUpperBound()
  {
    return this.bound_upper;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.name.hashCode();
    return result;
  }

  @Override public boolean equals(
    final Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    final ModelObject other = (ModelObject) obj;
    if (!this.name.equals(other.name)) {
      return false;
    }
    return true;
  }

  @Override public @Nonnull String toString()
  {
    final StringBuilder b = new StringBuilder();
    b.append("[ModelObject ");
    b.append(this.name);
    b.append("]");
    return b.toString();
  }
}
