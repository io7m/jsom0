package com.io7m.jsom0.parser;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Option;
import com.io7m.jcanephora.ArrayBuffer;
import com.io7m.jcanephora.ArrayBufferCursorWritable2f;
import com.io7m.jcanephora.ArrayBufferCursorWritable3f;
import com.io7m.jcanephora.ArrayBufferDescriptor;
import com.io7m.jcanephora.ArrayBufferWritableMap;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLInterface;
import com.io7m.jcanephora.IndexBuffer;
import com.io7m.jcanephora.IndexBufferWritableMap;
import com.io7m.jlog.Log;
import com.io7m.jsom0.ModelObject;
import com.io7m.jsom0.Vertex;
import com.io7m.jsom0.VertexType;
import com.io7m.jtensors.VectorM2F;
import com.io7m.jtensors.VectorM3F;

public final class ModelObjectParser
{
  private final @Nonnull Log              log;
  private final @Nonnull ModelObjectLexer lexer;
  private final @Nonnull String           file_name;
  private @CheckForNull ModelObjectToken  token;
  private final @Nonnull GLInterface      gl;
  private int                             highest_vertex = 0;
  private int                             triangle_index = 0;

  public ModelObjectParser(
    final @Nonnull String file_name,
    final @Nonnull InputStream in,
    final @Nonnull GLInterface gl,
    final @Nonnull Log log)
    throws ConstraintError,
      IOException,
      Error
  {
    this.log =
      new Log(
        Constraints.constrainNotNull(log, "log output"),
        "object-parser");
    this.lexer =
      new ModelObjectLexer(Constraints.constrainNotNull(in, "input stream"));
    this.file_name = Constraints.constrainNotNull(file_name, "file name");
    this.gl = Constraints.constrainNotNull(gl, "OpenGL interface");
    this.token = this.lexer.token();

    this.log.info("parsing " + this.file_name);
  }

  private void checkIndex(
    final int value,
    final int minimum,
    final int maximum)
    throws Error
  {
    if ((value < 0) || (value >= this.highest_vertex)) {
      throw Error.indexOutOfRange(
        this.token.position,
        value,
        minimum,
        maximum);
    }
  }

  private void consume(
    final @Nonnull ModelObjectTokenType type)
    throws IOException,
      Error
  {
    assert (this.token != null);

    if (this.token.type != type) {
      throw Error.unexpectedToken(this.token, type);
    }

    this.token = this.lexer.token();
  }

  private void consumeSymbol(
    final @Nonnull String symbol)
    throws IOException,
      Error
  {
    assert this.token != null;
    assert symbol != null;

    if (this.token.type != ModelObjectTokenType.OBJECT_TOKEN_SYMBOL) {
      throw Error.expectedSymbol(this.token);
    }
    if (this.token.value.equals(symbol) == false) {
      throw Error.unexpectedSymbol(this.token, symbol);
    }

    this.token = this.lexer.token();
  }

  private final void debug(
    final String message)
  {
    this.log.debug(this.file_name + " " + message);
  }

  private @Nonnull String materialName()
    throws IOException,
      Error
  {
    this.consumeSymbol("material_name");
    final String value = this.token.value;
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_LITERAL_STRING);
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON);

    this.debug("material name: " + value);
    return value;
  }

  public @Nonnull ModelObject modelObject()
    throws IOException,
      Error,
      ConstraintError,
      GLException
  {
    assert this.log != null;
    assert this.file_name != null;

    this.highest_vertex = 0;
    this.triangle_index = 0;

    this.consumeSymbol("object");
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON);

    final String name = this.objectName();
    final String material_name = this.materialName();
    final ArrayBuffer vertices_buffer = this.vertices();
    final IndexBuffer index_buffer = this.triangles(vertices_buffer);

    this.consumeSymbol("end");
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON);

    return new ModelObject(name, material_name, vertices_buffer, index_buffer);
  }

  private @Nonnull String objectName()
    throws IOException,
      Error
  {
    this.consumeSymbol("name");
    final String value = this.token.value;
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_LITERAL_STRING);
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON);

    this.debug("object name: " + value);
    return value;
  }

  private void triangle(
    final @Nonnull IndexBufferWritableMap map)
    throws IOException,
      Error
  {
    assert map != null;

    this.consumeSymbol("triangle");
    final int i0 = Integer.parseInt(this.token.value);
    this.checkIndex(i0, 0, this.highest_vertex);
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_LITERAL_DECIMAL);
    final int i1 = Integer.parseInt(this.token.value);
    this.checkIndex(i1, 0, this.highest_vertex);
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_LITERAL_DECIMAL);
    final int i2 = Integer.parseInt(this.token.value);
    this.checkIndex(i2, 0, this.highest_vertex);
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_LITERAL_DECIMAL);
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON);

    this.debug("triangle [" + i0 + " " + i1 + " " + i2 + "]");

    map.put(this.triangle_index, i0);
    ++this.triangle_index;
    map.put(this.triangle_index, i1);
    ++this.triangle_index;
    map.put(this.triangle_index, i2);
    ++this.triangle_index;
  }

  private IndexBuffer triangles(
    final @Nonnull ArrayBuffer array)
    throws IOException,
      Error,
      GLException,
      ConstraintError
  {
    IndexBuffer index_id = null;

    this.debug("parsing triangles");

    this.consumeSymbol("triangles");
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON);
    {
      this.consumeSymbol("array");
      {
        final long elements = Long.parseLong(this.token.value);
        this.consume(ModelObjectTokenType.OBJECT_TOKEN_LITERAL_DECIMAL);

        if (elements < 1) {
          throw Error.arrayTooSmall(this.token.position, elements, 1);
        }
        if (elements > 65535) {
          throw Error.arrayTooLarge(this.token.position, elements, 65535);
        }

        this.debug("expecting " + elements + " triangles");
        this.consumeSymbol("triangle");
        this.consume(ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON);

        index_id = this.gl.allocateIndexBuffer(array, (int) (elements * 3));
        final IndexBufferWritableMap map =
          this.gl.mapIndexBufferWrite(index_id);

        for (long index = 0; index < elements; ++index) {
          this.triangle(map);
        }

        this.gl.unmapIndexBuffer(index_id);
      }
      this.consumeSymbol("end");
      this.consume(ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON);
    }
    this.consumeSymbol("end");
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON);
    return index_id;

  }

  public Option<ModelObject> tryModelObject()
    throws IOException,
      Error,
      GLException,
      ConstraintError
  {
    assert this.token != null;

    switch (this.token.type) {
      case OBJECT_TOKEN_EOF:
        return new Option.None<ModelObject>();
      default:
        return new Option.Some<ModelObject>(this.modelObject());
    }
  }

  private @Nonnull VectorM2F vector2f()
    throws IOException,
      Error
  {
    final VectorM2F v = new VectorM2F();

    v.x = Float.parseFloat(this.token.value);
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_LITERAL_FLOAT);
    v.y = Float.parseFloat(this.token.value);
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_LITERAL_FLOAT);

    return v;
  }

  private @Nonnull VectorM3F vector3f()
    throws IOException,
      Error
  {
    final VectorM3F v = new VectorM3F();

    v.x = Float.parseFloat(this.token.value);
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_LITERAL_FLOAT);
    v.y = Float.parseFloat(this.token.value);
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_LITERAL_FLOAT);
    v.z = Float.parseFloat(this.token.value);
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_LITERAL_FLOAT);

    return v;
  }

  private @Nonnull VectorM3F vertexNormal()
    throws IOException,
      Error
  {
    this.consumeSymbol("normal");
    final VectorM3F v = this.vector3f();
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON);
    return v;
  }

  private void vertexP3N3(
    final @Nonnull ArrayBufferCursorWritable3f cursor_position,
    final @Nonnull ArrayBufferCursorWritable3f cursor_normal)
    throws IOException,
      Error,
      ConstraintError
  {
    this.consumeSymbol(Vertex.vertexTypeName(VertexType.VERTEX_TYPE_P3N3));
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON);

    final VectorM3F position = this.vertexPosition();
    final VectorM3F normal = this.vertexNormal();

    this.consumeSymbol("end");
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON);

    this.debug("position " + position);
    this.debug("normal   " + normal);

    cursor_position.put3f(position.x, position.y, position.z);
    cursor_normal.put3f(normal.x, normal.y, normal.z);
  }

  private void vertexP3N3T2(
    final @Nonnull ArrayBufferCursorWritable3f cursor_position,
    final @Nonnull ArrayBufferCursorWritable3f cursor_normal,
    final @Nonnull ArrayBufferCursorWritable2f cursor_uv)
    throws IOException,
      Error,
      ConstraintError
  {
    this.consumeSymbol(Vertex.vertexTypeName(VertexType.VERTEX_TYPE_P3N3T2));
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON);

    final VectorM3F position = this.vertexPosition();
    final VectorM3F normal = this.vertexNormal();
    final VectorM2F texcoord = this.vertexTexCoords();

    this.consumeSymbol("end");
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON);

    this.debug("position " + position);
    this.debug("normal   " + normal);
    this.debug("uv       " + texcoord);

    cursor_position.put3f(position.x, position.y, position.z);
    cursor_normal.put3f(normal.x, normal.y, normal.z);
    cursor_uv.put2f(texcoord.x, texcoord.y);
  }

  private @Nonnull VectorM3F vertexPosition()
    throws IOException,
      Error
  {
    this.consumeSymbol("position");
    final VectorM3F v = this.vector3f();
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON);
    return v;
  }

  private @Nonnull VectorM2F vertexTexCoords()
    throws IOException,
      Error
  {
    this.consumeSymbol("uv");
    final VectorM2F v = this.vector2f();
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON);
    return v;
  }

  private ArrayBuffer vertices()
    throws IOException,
      Error,
      GLException,
      ConstraintError
  {
    ArrayBuffer array = null;

    this.debug("parsing vertices");

    this.consumeSymbol("vertices");
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON);
    {
      this.consumeSymbol("array");
      {
        final long elements = Long.parseLong(this.token.value);
        this.consume(ModelObjectTokenType.OBJECT_TOKEN_LITERAL_DECIMAL);

        if (elements < 1) {
          throw Error.arrayTooSmall(this.token.position, elements, 1);
        }
        if (elements > 65535) {
          throw Error.arrayTooLarge(this.token.position, elements, 65535);
        }

        this.debug("expecting " + elements + " vertices");

        if (Vertex.validTypeName(this.token.value) == false) {
          throw Error.unexpectedOneOf(this.token, Vertex.vertexTypeNames());
        }

        final String name = this.token.value;
        this.consumeSymbol(name);
        this.consume(ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON);

        final VertexType type = Vertex.lookupTypeByName(name);
        final ArrayBufferDescriptor descriptor =
          Vertex.vertexTypeDescriptor(type);

        array = this.gl.allocateArrayBuffer(elements, descriptor);
        final ArrayBufferWritableMap map = this.gl.mapArrayBufferWrite(array);

        switch (type) {
          case VERTEX_TYPE_P3N3:
          {
            final ArrayBufferCursorWritable3f cp =
              map.getCursor3f("position");
            final ArrayBufferCursorWritable3f cn = map.getCursor3f("normal");

            for (long index = 0; index < elements; ++index) {
              this.highest_vertex++;
              this.vertexP3N3(cp, cn);
            }

            assert cp.hasNext() == false;
            assert cn.hasNext() == false;
            break;
          }
          case VERTEX_TYPE_P3N3T2:
          {
            final ArrayBufferCursorWritable3f cp =
              map.getCursor3f("position");
            final ArrayBufferCursorWritable3f cn = map.getCursor3f("normal");
            final ArrayBufferCursorWritable2f cu = map.getCursor2f("uv");

            for (long index = 0; index < elements; ++index) {
              this.highest_vertex++;
              this.vertexP3N3T2(cp, cn, cu);
            }

            assert cp.hasNext() == false;
            assert cn.hasNext() == false;
            assert cu.hasNext() == false;
            break;
          }
        }

        this.gl.unmapArrayBuffer(array);
      }
      this.consumeSymbol("end");
      this.consume(ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON);
    }
    this.consumeSymbol("end");
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON);

    return array;
  }
}
