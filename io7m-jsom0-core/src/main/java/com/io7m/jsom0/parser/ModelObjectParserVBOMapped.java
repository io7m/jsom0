package com.io7m.jsom0.parser;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jcanephora.ArrayBufferWritableMap;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLInterface;
import com.io7m.jcanephora.IndexBufferWritableMap;
import com.io7m.jlog.Log;
import com.io7m.jsom0.VertexType;
import com.io7m.jsom0.VertexTypeInformation;

/**
 * A parser that produces a vertex buffer object (and associated index buffer
 * object) whilst parsing the given file.
 * 
 * This implementation uses memory-mapped buffer objects on systems that
 * support them (for reduced memory allocation and potentially increased
 * performance).
 */

public final class ModelObjectParserVBOMapped extends ModelObjectParserVBO
{
  private final @Nonnull GLInterface           gl;
  private @CheckForNull ArrayBufferWritableMap array_buffer_data;
  private @CheckForNull IndexBufferWritableMap index_data;

  public ModelObjectParserVBOMapped(
    final @Nonnull String file_name,
    final @Nonnull InputStream in,
    final @Nonnull Log log,
    final @Nonnull GLInterface gl)
    throws ConstraintError,
      IOException,
      Error
  {
    super(file_name, in, log);
    this.gl = Constraints.constrainNotNull(gl, "GL interface");
  }

  @Override void eventIndexBufferAllocate(
    final long count)
    throws GLException,
      ConstraintError
  {
    this.index_buffer =
      this.gl.indexBufferAllocate(this.array_buffer, (int) count);
    this.index_data = this.gl.indexBufferMapWrite(this.index_buffer);
    this.cursor_index = this.index_data.getCursor();
  }

  @Override void eventIndexBufferCompleted()
    throws GLException,
      ConstraintError
  {
    this.gl.indexBufferUnmap(this.index_buffer);
    this.index_data = null;
    this.cursor_index = null;
  }

  @Override void eventVertexBufferAllocate(
    final @Nonnull VertexType type,
    final long count)
    throws GLException,
      ConstraintError
  {
    this.array_buffer_type = VertexTypeInformation.typeDescriptor(type);
    this.array_buffer =
      this.gl.arrayBufferAllocate(count, this.array_buffer_type);
    this.array_buffer_data = this.gl.arrayBufferMapWrite(this.array_buffer);

    switch (type) {
      case VERTEX_TYPE_P3N3:
      {
        this.cursor_position = this.array_buffer_data.getCursor3f("position");
        this.cursor_normal = this.array_buffer_data.getCursor3f("normal");
        this.cursor_uv = null;
        break;
      }
      case VERTEX_TYPE_P3N3T2:
      {
        this.cursor_position = this.array_buffer_data.getCursor3f("position");
        this.cursor_normal = this.array_buffer_data.getCursor3f("normal");
        this.cursor_uv = this.array_buffer_data.getCursor2f("uv");
        break;
      }
    }
  }

  @Override void eventVertexBufferCompleted()
    throws GLException,
      ConstraintError
  {
    this.gl.arrayBufferUnmap(this.array_buffer);
    this.array_buffer_data = null;
    this.cursor_normal = null;
    this.cursor_position = null;
    this.cursor_uv = null;
  }
}
