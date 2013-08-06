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
import com.io7m.jcanephora.ArrayBufferWritableData;
import com.io7m.jcanephora.IndexBufferWritableData;
import com.io7m.jcanephora.JCGLArrayBuffers;
import com.io7m.jcanephora.JCGLException;
import com.io7m.jcanephora.JCGLIndexBuffers;
import com.io7m.jcanephora.UsageHint;
import com.io7m.jlog.Log;
import com.io7m.jsom0.NameNormalAttribute;
import com.io7m.jsom0.NamePositionAttribute;
import com.io7m.jsom0.NameUVAttribute;
import com.io7m.jsom0.VertexType;

/**
 * <p>
 * A parser that produces a vertex buffer object (and associated index buffer
 * object) whilst parsing the given file.
 * </p>
 */

public final class ModelObjectParserVBOImmediate<G extends JCGLArrayBuffers & JCGLIndexBuffers> extends
  ModelObjectParserVBO
{
  private final @Nonnull G                      gl;
  private @CheckForNull ArrayBufferWritableData array_buffer_data;
  private @CheckForNull IndexBufferWritableData index_data;

  public ModelObjectParserVBOImmediate(
    final @Nonnull String file_name,
    final @Nonnull InputStream in,
    final @Nonnull NamePositionAttribute name_position_attribute,
    final @Nonnull NameNormalAttribute name_normal_attribute,
    final @Nonnull NameUVAttribute name_uv_attribute,
    final @Nonnull Log log,
    final @Nonnull G gl)
    throws ConstraintError,
      IOException,
      Error
  {
    super(
      file_name,
      in,
      name_position_attribute,
      name_normal_attribute,
      name_uv_attribute,
      log);
    this.gl = Constraints.constrainNotNull(gl, "GL interface");
  }

  @Override void eventIndexBufferAllocate(
    final long count)
    throws JCGLException,
      ConstraintError
  {
    this.index_buffer =
      this.gl.indexBufferAllocate(this.array_buffer, (int) count);
    this.index_data = new IndexBufferWritableData(this.index_buffer);
    this.cursor_index = this.index_data.getCursor();
  }

  @Override void eventIndexBufferCompleted()
    throws JCGLException,
      ConstraintError
  {
    this.gl.indexBufferUpdate(this.index_data);
    this.index_data = null;
    this.cursor_index = null;
  }

  @Override void eventVertexBufferAllocate(
    final @Nonnull VertexType type,
    final long count)
    throws JCGLException,
      ConstraintError
  {
    this.array_buffer_type = this.getArrayTypeDescriptor(type);

    this.array_buffer =
      this.gl.arrayBufferAllocate(
        count,
        this.array_buffer_type,
        UsageHint.USAGE_STATIC_READ);
    this.array_buffer_data = new ArrayBufferWritableData(this.array_buffer);

    switch (type) {
      case VERTEX_TYPE_P3N3:
      {
        this.cursor_position =
          this.array_buffer_data.getCursor3f(this
            .getVertexPositionAttributeName()
            .toString());
        this.cursor_normal =
          this.array_buffer_data.getCursor3f(this
            .getVertexNormalAttributeName()
            .toString());
        this.cursor_uv = null;
        break;
      }
      case VERTEX_TYPE_P3N3T2:
      {
        this.cursor_position =
          this.array_buffer_data.getCursor3f(this
            .getVertexPositionAttributeName()
            .toString());
        this.cursor_normal =
          this.array_buffer_data.getCursor3f(this
            .getVertexNormalAttributeName()
            .toString());
        this.cursor_uv =
          this.array_buffer_data.getCursor2f(this
            .getVertexUVAttributeName()
            .toString());
        break;
      }
    }
  }

  @Override void eventVertexBufferCompleted()
    throws JCGLException,
      ConstraintError
  {
    this.gl.arrayBufferBind(this.array_buffer);
    this.gl.arrayBufferUpdate(this.array_buffer_data);
    this.array_buffer_data = null;
    this.cursor_normal = null;
    this.cursor_position = null;
    this.cursor_uv = null;
  }
}
