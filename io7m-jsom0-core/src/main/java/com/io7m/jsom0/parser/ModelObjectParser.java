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
import com.io7m.jaux.functional.Option;
import com.io7m.jlog.Log;
import com.io7m.jsom0.ModelObject;
import com.io7m.jsom0.VertexType;
import com.io7m.jsom0.VertexTypeInformation;
import com.io7m.jtensors.VectorM2F;
import com.io7m.jtensors.VectorM3F;
import com.io7m.jtensors.VectorReadable2F;
import com.io7m.jtensors.VectorReadable3F;

/**
 * A generic parser type that parses model objects of type <code>O</code> and
 * may raise exceptions of type <code>E</code> (in addition to the normal
 * parser or I/O errors, whilst parsing.
 * 
 * @param <O>
 *          The type of model object.
 * @param <E>
 *          The type of exceptions raised.
 */

public abstract class ModelObjectParser<O extends ModelObject, E extends Throwable>
{
  private final @Nonnull Log              log;
  private final @Nonnull ModelObjectLexer lexer;
  private final @Nonnull String           file_name;
  private @CheckForNull ModelObjectToken  token;
  private int                             highest_vertex = 0;
  private final @Nonnull VectorM3F        bound_lower;
  private final @Nonnull VectorM3F        bound_upper;

  ModelObjectParser(
    final @Nonnull String file_name,
    final @Nonnull InputStream in,
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
    this.token = this.lexer.token();

    this.bound_lower = new VectorM3F();
    this.bound_lower.x = Float.MAX_VALUE;
    this.bound_lower.y = Float.MAX_VALUE;
    this.bound_lower.z = Float.MAX_VALUE;

    this.bound_upper = new VectorM3F();
    this.bound_upper.x = Float.MIN_VALUE;
    this.bound_upper.y = Float.MIN_VALUE;
    this.bound_upper.z = Float.MIN_VALUE;

    this.log.info("parsing " + this.file_name);
  }

  private final void checkIndex(
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

  private final void consume(
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

  private final void consumeSymbol(
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

  abstract void eventIndexBufferAllocate(
    final long count)
    throws E,
      ConstraintError;

  abstract void eventIndexBufferAppendTriangle(
    final long v0,
    final long v1,
    final long v2)
    throws E,
      ConstraintError;

  abstract void eventIndexBufferCompleted()
    throws E,
      ConstraintError;

  abstract void eventMaterialName(
    final @Nonnull String name)
    throws E,
      ConstraintError;

  abstract @Nonnull O eventModelObjectCompleted()
    throws E,
      ConstraintError;

  abstract void eventObjectName(
    final @Nonnull String name)
    throws E,
      ConstraintError;

  abstract void eventVertexBufferAllocate(
    final @Nonnull VertexType type,
    final long count)
    throws E,
      ConstraintError;

  abstract void eventVertexBufferAppendP3N3(
    final @Nonnull VectorReadable3F position,
    final @Nonnull VectorReadable3F normal)
    throws E,
      ConstraintError;

  abstract void eventVertexBufferAppendP3N3T2(
    final @Nonnull VectorReadable3F position,
    final @Nonnull VectorReadable3F normal,
    final @Nonnull VectorReadable2F uv)
    throws E,
      ConstraintError;

  abstract void eventVertexBufferCompleted()
    throws E,
      ConstraintError;

  final @Nonnull VectorReadable3F getBoundLower()
  {
    return this.bound_lower;
  }

  final @Nonnull VectorReadable3F getBoundUpper()
  {
    return this.bound_upper;
  }

  private final @Nonnull String materialName()
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

  public final @Nonnull O modelObject()
    throws IOException,
      Error,
      ConstraintError,
      E
  {
    assert this.log != null;
    assert this.file_name != null;

    this.highest_vertex = 0;

    this.consumeSymbol("object");
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON);

    final String name = this.objectName();
    this.eventObjectName(name);

    final String material_name = this.materialName();
    this.eventMaterialName(material_name);

    this.vertices();
    this.triangles();

    this.consumeSymbol("end");
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON);

    return this.eventModelObjectCompleted();
  }

  private final @Nonnull String objectName()
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

  private final void triangle()
    throws IOException,
      Error,
      E,
      ConstraintError
  {
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
    this.eventIndexBufferAppendTriangle(i0, i1, i2);
  }

  private final void triangles()
    throws IOException,
      Error,
      E,
      ConstraintError
  {
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

        this.eventIndexBufferAllocate(elements * 3);

        for (long index = 0; index < elements; ++index) {
          this.triangle();
        }

        this.eventIndexBufferCompleted();
      }
      this.consumeSymbol("end");
      this.consume(ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON);
    }
    this.consumeSymbol("end");
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON);
  }

  public final Option<O> tryModelObject()
    throws IOException,
      Error,
      ConstraintError,
      E
  {
    assert this.token != null;

    switch (this.token.type) {
      case OBJECT_TOKEN_EOF:
        return new Option.None<O>();
      case OBJECT_TOKEN_LITERAL_DECIMAL:
      case OBJECT_TOKEN_LITERAL_FLOAT:
      case OBJECT_TOKEN_LITERAL_STRING:
      case OBJECT_TOKEN_SEMICOLON:
      case OBJECT_TOKEN_SYMBOL:
        return new Option.Some<O>(this.modelObject());
    }

    throw new UnreachableCodeException();
  }

  private final void updateBounds(
    final @Nonnull VectorReadable3F position)
  {
    this.bound_lower.x = Math.min(this.bound_lower.x, position.getXF());
    this.bound_lower.y = Math.min(this.bound_lower.y, position.getYF());
    this.bound_lower.z = Math.min(this.bound_lower.z, position.getZF());
    this.bound_upper.x = Math.max(this.bound_upper.x, position.getXF());
    this.bound_upper.y = Math.max(this.bound_upper.y, position.getYF());
    this.bound_upper.z = Math.max(this.bound_upper.z, position.getZF());
  }

  private final @Nonnull VectorM2F vector2f()
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

  private final @Nonnull VectorM3F vector3f()
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

  private final @Nonnull VectorM3F vertexNormal()
    throws IOException,
      Error
  {
    this.consumeSymbol("normal");
    final VectorM3F v = this.vector3f();
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON);
    return v;
  }

  private final void vertexP3N3()
    throws IOException,
      Error,
      ConstraintError,
      E
  {
    ++this.highest_vertex;

    this.consumeSymbol(VertexTypeInformation
      .vertexTypeName(VertexType.VERTEX_TYPE_P3N3));
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON);

    final VectorM3F position = this.vertexPosition();
    final VectorM3F normal = this.vertexNormal();

    this.consumeSymbol("end");
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON);

    this.debug("position " + position);
    this.debug("normal   " + normal);

    this.updateBounds(position);
    this.eventVertexBufferAppendP3N3(position, normal);
  }

  private final void vertexP3N3T2()
    throws IOException,
      Error,
      ConstraintError,
      E
  {
    ++this.highest_vertex;

    this.consumeSymbol(VertexTypeInformation
      .vertexTypeName(VertexType.VERTEX_TYPE_P3N3T2));
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON);

    final VectorM3F position = this.vertexPosition();
    final VectorM3F normal = this.vertexNormal();
    final VectorM2F texcoord = this.vertexTexCoords();

    this.consumeSymbol("end");
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON);

    this.debug("position " + position);
    this.debug("normal   " + normal);
    this.debug("uv       " + texcoord);

    this.updateBounds(position);
    this.eventVertexBufferAppendP3N3T2(position, normal, texcoord);
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

  private final void vertices()
    throws IOException,
      Error,
      ConstraintError,
      E
  {
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

        if (VertexTypeInformation.validTypeName(this.token.value) == false) {
          throw Error.unexpectedOneOf(
            this.token,
            VertexTypeInformation.vertexTypeNames());
        }

        final String name = this.token.value;
        this.consumeSymbol(name);
        this.consume(ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON);

        final VertexType type = VertexTypeInformation.lookupTypeByName(name);
        switch (type) {
          case VERTEX_TYPE_P3N3:
          {
            this.eventVertexBufferAllocate(
              VertexType.VERTEX_TYPE_P3N3,
              elements);

            for (long index = 0; index < elements; ++index) {
              this.vertexP3N3();
            }

            break;
          }
          case VERTEX_TYPE_P3N3T2:
          {
            this.eventVertexBufferAllocate(
              VertexType.VERTEX_TYPE_P3N3T2,
              elements);

            for (long index = 0; index < elements; ++index) {
              this.vertexP3N3T2();
            }

            break;
          }
        }

        this.eventVertexBufferCompleted();
      }
      this.consumeSymbol("end");
      this.consume(ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON);
    }
    this.consumeSymbol("end");
    this.consume(ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON);
  }
}
