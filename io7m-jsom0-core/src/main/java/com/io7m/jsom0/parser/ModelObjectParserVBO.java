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

package com.io7m.jsom0.parser;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jcanephora.ArrayBuffer;
import com.io7m.jcanephora.ArrayBufferAttributeDescriptor;
import com.io7m.jcanephora.ArrayBufferTypeDescriptor;
import com.io7m.jcanephora.CursorWritable2f;
import com.io7m.jcanephora.CursorWritable3f;
import com.io7m.jcanephora.CursorWritableIndex;
import com.io7m.jcanephora.IndexBuffer;
import com.io7m.jcanephora.JCGLException;
import com.io7m.jcanephora.JCGLScalarType;
import com.io7m.jlog.Log;
import com.io7m.jsom0.ModelObjectVBO;
import com.io7m.jsom0.NameNormalAttribute;
import com.io7m.jsom0.NamePositionAttribute;
import com.io7m.jsom0.NameUVAttribute;
import com.io7m.jsom0.VertexType;
import com.io7m.jtensors.VectorReadable2F;
import com.io7m.jtensors.VectorReadable3F;

/**
 * <p>
 * Abstract parser type that produces a vertex buffer object whilst parsing.
 * This class implements the parts that are common to all parsers that produce
 * VBOs directly.
 * </p>
 * <p>
 * See one of the subtypes for usable, concrete implementations.
 * </p>
 */

abstract class ModelObjectParserVBO extends
  ModelObjectParser<ModelObjectVBO, JCGLException>
{
  protected @CheckForNull String                    object_name;
  protected @CheckForNull String                    material_name;
  protected @CheckForNull ArrayBufferTypeDescriptor array_buffer_type;
  protected @CheckForNull ArrayBuffer               array_buffer;
  protected @CheckForNull IndexBuffer               index_buffer;
  protected @CheckForNull CursorWritable3f          cursor_position;
  protected @CheckForNull CursorWritable3f          cursor_normal;
  protected @CheckForNull CursorWritable2f          cursor_uv;
  protected @CheckForNull CursorWritableIndex       cursor_index;
  private final @Nonnull NamePositionAttribute      name_position_attribute;
  private final @Nonnull NameNormalAttribute        name_normal_attribute;
  private final @Nonnull NameUVAttribute            name_uv_attribute;

  ModelObjectParserVBO(
    final @Nonnull String file_name,
    final @Nonnull InputStream in,
    final @Nonnull NamePositionAttribute name_position_attribute,
    final @Nonnull NameNormalAttribute name_normal_attribute,
    final @Nonnull NameUVAttribute name_uv_attribute,
    final @Nonnull Log log)
    throws ConstraintError,
      IOException,
      Error
  {
    super(file_name, in, log);

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

  @Override final void eventIndexBufferAppendTriangle(
    final long v0,
    final long v1,
    final long v2)
    throws JCGLException,
      ConstraintError
  {
    this.cursor_index.putIndex((int) v0);
    this.cursor_index.putIndex((int) v1);
    this.cursor_index.putIndex((int) v2);
  }

  @Override final void eventMaterialName(
    final @Nonnull String name)
    throws JCGLException
  {
    this.material_name = name;
  }

  @Override final @Nonnull ModelObjectVBO eventModelObjectCompleted()
    throws JCGLException,
      ConstraintError
  {
    return new ModelObjectVBO(
      this.object_name,
      this.material_name,
      this.getBoundLower(),
      this.getBoundUpper(),
      this.array_buffer,
      this.index_buffer,
      this.name_position_attribute,
      this.name_normal_attribute,
      this.name_uv_attribute);
  }

  @Override final void eventObjectName(
    final @Nonnull String name)
    throws JCGLException
  {
    this.object_name = name;
  }

  @Override final void eventVertexBufferAppendP3N3(
    final @Nonnull VectorReadable3F position,
    final @Nonnull VectorReadable3F normal)
    throws JCGLException,
      ConstraintError
  {
    this.cursor_position.put3f(
      position.getXF(),
      position.getYF(),
      position.getZF());
    this.cursor_normal.put3f(normal.getXF(), normal.getYF(), normal.getZF());
  }

  @Override final void eventVertexBufferAppendP3N3T2(
    final @Nonnull VectorReadable3F position,
    final @Nonnull VectorReadable3F normal,
    final @Nonnull VectorReadable2F uv)
    throws JCGLException,
      ConstraintError
  {
    this.cursor_position.put3f(
      position.getXF(),
      position.getYF(),
      position.getZF());
    this.cursor_normal.put3f(normal.getXF(), normal.getYF(), normal.getZF());
    this.cursor_uv.put2f(uv.getXF(), uv.getYF());
  }

  /**
   * Construct a type descriptor to use for the VBO.
   * 
   * @throws ConstraintError
   */

  protected final @Nonnull ArrayBufferTypeDescriptor getArrayTypeDescriptor(
    final @Nonnull VertexType type)
    throws ConstraintError
  {
    switch (type) {
      case VERTEX_TYPE_P3N3:
      {
        final ArrayBufferAttributeDescriptor[] attributes =
          new ArrayBufferAttributeDescriptor[2];
        attributes[0] =
          new ArrayBufferAttributeDescriptor(this
            .getVertexPositionAttributeName()
            .toString(), JCGLScalarType.TYPE_FLOAT, 3);
        attributes[1] =
          new ArrayBufferAttributeDescriptor(this
            .getVertexNormalAttributeName()
            .toString(), JCGLScalarType.TYPE_FLOAT, 3);

        return new ArrayBufferTypeDescriptor(attributes);
      }
      case VERTEX_TYPE_P3N3T2:
      {
        final ArrayBufferAttributeDescriptor[] attributes =
          new ArrayBufferAttributeDescriptor[3];
        attributes[0] =
          new ArrayBufferAttributeDescriptor(this
            .getVertexPositionAttributeName()
            .toString(), JCGLScalarType.TYPE_FLOAT, 3);
        attributes[1] =
          new ArrayBufferAttributeDescriptor(this
            .getVertexNormalAttributeName()
            .toString(), JCGLScalarType.TYPE_FLOAT, 3);
        attributes[2] =
          new ArrayBufferAttributeDescriptor(this
            .getVertexUVAttributeName()
            .toString(), JCGLScalarType.TYPE_FLOAT, 2);

        return new ArrayBufferTypeDescriptor(attributes);
      }
    }

    throw new UnreachableCodeException();
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
}
