package com.io7m.jsom0.parser;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jcanephora.ArrayBuffer;
import com.io7m.jcanephora.ArrayBufferDescriptor;
import com.io7m.jcanephora.CursorWritable2f;
import com.io7m.jcanephora.CursorWritable3f;
import com.io7m.jcanephora.CursorWritableIndex;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.IndexBuffer;
import com.io7m.jlog.Log;
import com.io7m.jsom0.ModelObjectVBO;
import com.io7m.jtensors.VectorReadable2F;
import com.io7m.jtensors.VectorReadable3F;

/**
 * Abstract parser type that produces a vertex buffer object whilst parsing.
 * This class implements the parts that are common to all parsers that produce
 * VBOs directly.
 * 
 * See one of the subtypes for usable, concrete implementations.
 */

abstract class ModelObjectParserVBO extends
  ModelObjectParser<ModelObjectVBO, GLException>
{
  protected @CheckForNull String                object_name;
  protected @CheckForNull String                material_name;
  protected @CheckForNull ArrayBufferDescriptor array_buffer_type;
  protected @CheckForNull ArrayBuffer           array_buffer;
  protected @CheckForNull IndexBuffer           index_buffer;
  protected @CheckForNull CursorWritable3f      cursor_position;
  protected @CheckForNull CursorWritable3f      cursor_normal;
  protected @CheckForNull CursorWritable2f      cursor_uv;
  protected @CheckForNull CursorWritableIndex   cursor_index;

  ModelObjectParserVBO(
    final @Nonnull String file_name,
    final @Nonnull InputStream in,
    final @Nonnull Log log)
    throws ConstraintError,
      IOException,
      Error
  {
    super(file_name, in, log);
  }

  @Override final void eventIndexBufferAppendTriangle(
    final long v0,
    final long v1,
    final long v2)
    throws GLException,
      ConstraintError
  {
    this.cursor_index.putIndex((int) v0);
    this.cursor_index.putIndex((int) v1);
    this.cursor_index.putIndex((int) v2);
  }

  @Override final void eventMaterialName(
    final @Nonnull String name)
    throws GLException
  {
    this.material_name = name;
  }

  @Override final @Nonnull ModelObjectVBO eventModelObjectCompleted()
    throws GLException,
      ConstraintError
  {
    return new ModelObjectVBO(
      this.object_name,
      this.material_name,
      this.getBoundLower(),
      this.getBoundUpper(),
      this.array_buffer,
      this.index_buffer);
  }

  @Override final void eventObjectName(
    final @Nonnull String name)
    throws GLException
  {
    this.object_name = name;
  }

  @Override final void eventVertexBufferAppendP3N3(
    final @Nonnull VectorReadable3F position,
    final @Nonnull VectorReadable3F normal)
    throws GLException,
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
    throws GLException,
      ConstraintError
  {
    this.cursor_position.put3f(
      position.getXF(),
      position.getYF(),
      position.getZF());
    this.cursor_normal.put3f(normal.getXF(), normal.getYF(), normal.getZF());
    this.cursor_uv.put2f(uv.getXF(), uv.getYF());
  }
}
