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
