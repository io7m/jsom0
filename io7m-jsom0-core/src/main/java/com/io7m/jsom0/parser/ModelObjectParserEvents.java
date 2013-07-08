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

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jsom0.ModelObject;
import com.io7m.jsom0.VertexType;
import com.io7m.jtensors.VectorReadable2F;
import com.io7m.jtensors.VectorReadable3F;

/**
 * The interface supported by types that can accept results during the parsing
 * of model objects of type <code>O</code>. The types may raise exceptions of
 * type <code>E</code> (in addition to the normal parser or I/O errors).
 * 
 * @param <O>
 *          The type of model object.
 * @param <E>
 *          The type of exceptions raised.
 */

public interface ModelObjectParserEvents<O extends ModelObject, E extends Throwable>
{
  void eventIndexBufferAllocate(
    final long count)
    throws E,
      ConstraintError;

  void eventIndexBufferAppendTriangle(
    final long v0,
    final long v1,
    final long v2)
    throws E,
      ConstraintError;

  void eventIndexBufferCompleted()
    throws E,
      ConstraintError;

  void eventMaterialName(
    final @Nonnull String name)
    throws E,
      ConstraintError;

  @Nonnull O eventModelObjectCompleted()
    throws E,
      ConstraintError;

  void eventObjectName(
    final @Nonnull String name)
    throws E,
      ConstraintError;

  void eventVertexBufferAllocate(
    final @Nonnull VertexType type,
    final long count)
    throws E,
      ConstraintError;

  void eventVertexBufferAppendP3N3(
    final @Nonnull VectorReadable3F position,
    final @Nonnull VectorReadable3F normal)
    throws E,
      ConstraintError;

  void eventVertexBufferAppendP3N3T2(
    final @Nonnull VectorReadable3F position,
    final @Nonnull VectorReadable3F normal,
    final @Nonnull VectorReadable2F uv)
    throws E,
      ConstraintError;

  void eventVertexBufferCompleted()
    throws E,
      ConstraintError;
}
