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
import com.io7m.jcanephora.ArrayBuffer;
import com.io7m.jcanephora.IndexBuffer;
import com.io7m.jtensors.VectorReadable3F;

public final class ModelObjectVBO extends ModelObject
{
  private final @Nonnull ArrayBuffer           array_buffer;
  private final @Nonnull IndexBuffer           index_buffer;
  private final @Nonnull NamePositionAttribute name_position_attribute;
  private final @Nonnull NameNormalAttribute   name_normal_attribute;
  private final @Nonnull NameUVAttribute       name_uv_attribute;

  public ModelObjectVBO(
    final @Nonnull String name,
    final @Nonnull String material_name,
    final @Nonnull VectorReadable3F bound_lower,
    final @Nonnull VectorReadable3F bound_upper,
    final @Nonnull ArrayBuffer array_buffer,
    final @Nonnull IndexBuffer index_buffer,
    final @Nonnull NamePositionAttribute name_position_attribute,
    final @Nonnull NameNormalAttribute name_normal_attribute,
    final @Nonnull NameUVAttribute name_uv_attribute)
    throws ConstraintError
  {
    super(name, material_name, bound_lower, bound_upper);
    this.array_buffer =
      Constraints.constrainNotNull(array_buffer, "Array buffer");
    this.index_buffer =
      Constraints.constrainNotNull(index_buffer, "Index buffer");

    this.name_normal_attribute =
      Constraints.constrainNotNull(
        name_normal_attribute,
        "Normal attribute name");
    this.name_uv_attribute =
      Constraints.constrainNotNull(name_uv_attribute, "UV attribute name");
    this.name_position_attribute =
      Constraints.constrainNotNull(
        name_position_attribute,
        "Position attribute name");
  }

  public @Nonnull ArrayBuffer getArrayBuffer()
  {
    return this.array_buffer;
  }

  public @Nonnull IndexBuffer getIndexBuffer()
  {
    return this.index_buffer;
  }

  /**
   * Retrieve the name used for the vertex normal attribute configured in the
   * array buffer.
   */

  public final @Nonnull NameNormalAttribute getVertexNormalAttributeName()
  {
    return this.name_normal_attribute;
  }

  /**
   * Retrieve the name used for the vertex position attribute configured in
   * the array buffer.
   */

  public final @Nonnull
    NamePositionAttribute
    getVertexPositionAttributeName()
  {
    return this.name_position_attribute;
  }

  /**
   * Retrieve the name used for the vertex UV attribute configured in the
   * array buffer.
   */

  public final @Nonnull NameUVAttribute getVertexUVAttributeName()
  {
    return this.name_uv_attribute;
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
