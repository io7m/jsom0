package com.io7m.jsom0.parser;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jcanephora.ArrayBufferWritableData;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLInterfaceEmbedded;
import com.io7m.jcanephora.IndexBufferWritableData;
import com.io7m.jlog.Log;
import com.io7m.jsom0.VertexType;
import com.io7m.jsom0.VertexTypeInformation;

/**
 * A parser that produces a vertex buffer object (and associated index buffer
 * object) whilst parsing the given file.
 */

public final class ModelObjectParserVBOImmediate extends ModelObjectParserVBO
{
  private final @Nonnull GLInterfaceEmbedded    gl;
  private @CheckForNull ArrayBufferWritableData array_buffer_data;
  private @CheckForNull IndexBufferWritableData index_data;

  public ModelObjectParserVBOImmediate(
    final @Nonnull String file_name,
    final @Nonnull InputStream in,
    final @Nonnull Log log,
    final @Nonnull GLInterfaceEmbedded gl)
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
    this.index_data = new IndexBufferWritableData(this.index_buffer);
    this.cursor_index = this.index_data.getCursor();
  }

  @Override void eventIndexBufferCompleted()
    throws GLException,
      ConstraintError
  {
    this.gl.indexBufferUpdate(this.index_buffer, this.index_data);
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
    this.array_buffer_data = new ArrayBufferWritableData(this.array_buffer);

    switch (type) {
      case VERTEX_TYPE_P3N3:
      {
        this.cursor_position =
          this.array_buffer_data.getCursor3f("vertex_position");
        this.cursor_normal =
          this.array_buffer_data.getCursor3f("vertex_normal");
        this.cursor_uv = null;
        break;
      }
      case VERTEX_TYPE_P3N3T2:
      {
        this.cursor_position =
          this.array_buffer_data.getCursor3f("vertex_position");
        this.cursor_normal =
          this.array_buffer_data.getCursor3f("vertex_normal");
        this.cursor_uv = this.array_buffer_data.getCursor2f("vertex_uv");
        break;
      }
    }
  }

  @Override void eventVertexBufferCompleted()
    throws GLException,
      ConstraintError
  {
    this.gl.arrayBufferBind(this.array_buffer);
    this.gl.arrayBufferUpdate(this.array_buffer, this.array_buffer_data);
    this.array_buffer_data = null;
    this.cursor_normal = null;
    this.cursor_position = null;
    this.cursor_uv = null;
  }
}
