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

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Option;
import com.io7m.jaux.functional.Option.Some;
import com.io7m.jsom0.Model;
import com.io7m.jsom0.ModelObject;

/**
 * A generic parser type that parses models consisting of objects of type
 * <code>O</code> and may raise exceptions of type <code>E</code> (in addition
 * to the normal parser or I/O errors, whilst parsing.
 * 
 * @param <O>
 *          The type of model object.
 * @param <E>
 *          The type of exceptions raised.
 */

public final class ModelParser<O extends ModelObject, E extends Throwable>
{
  private final @Nonnull ModelObjectParser<O, E> parser;

  public ModelParser(
    final @Nonnull ModelObjectParser<O, E> parser)
    throws ConstraintError
  {
    this.parser = Constraints.constrainNotNull(parser, "object parser");
  }

  public Model<O> model()
    throws IOException,
      Error,
      ConstraintError,
      E
  {
    final Model<O> m = new Model<O>();

    for (;;) {
      final Option<O> result = this.parser.tryModelObject();
      switch (result.type) {
        case OPTION_NONE:
          return m;
        case OPTION_SOME:
        {
          final Option.Some<O> some = (Some<O>) result;
          m.addObject(some.value);
        }
      }
    }
  }
}
