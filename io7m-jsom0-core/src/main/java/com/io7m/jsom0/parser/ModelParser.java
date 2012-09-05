package com.io7m.jsom0.parser;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Option;
import com.io7m.jaux.functional.Option.Some;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLInterface;
import com.io7m.jlog.Log;
import com.io7m.jsom0.Model;
import com.io7m.jsom0.ModelObject;

public final class ModelParser
{
  public static @Nonnull ModelParser makeParser(
    final @Nonnull String file_name,
    final @Nonnull InputStream in,
    final @Nonnull GLInterface gl,
    final @Nonnull Log log)
    throws IOException,
      Error,
      ConstraintError
  {
    final ModelObjectParser parser =
      new ModelObjectParser(file_name, in, gl, log);
    return new ModelParser(parser);
  }

  private final @Nonnull ModelObjectParser parser;

  public ModelParser(
    final @Nonnull ModelObjectParser parser)
    throws ConstraintError
  {
    this.parser = Constraints.constrainNotNull(parser, "object parser");
  }

  public Model model()
    throws IOException,
      Error,
      GLException,
      ConstraintError
  {
    final Model m = new Model();

    for (;;) {
      final Option<ModelObject> result = this.parser.tryModelObject();
      switch (result.type) {
        case OPTION_NONE:
          return m;
        case OPTION_SOME:
        {
          final Option.Some<ModelObject> some = (Some<ModelObject>) result;
          m.addObject(some.value);
        }
      }
    }
  }
}
