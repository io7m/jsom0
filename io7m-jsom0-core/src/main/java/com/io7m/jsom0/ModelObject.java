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
