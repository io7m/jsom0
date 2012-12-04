package com.io7m.jsom0.parser.contracts;

import java.io.IOException;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jsom0.ModelObject;
import com.io7m.jsom0.contracts.JSOM0LogTestContract;
import com.io7m.jsom0.parser.Error;
import com.io7m.jsom0.parser.ModelObjectParser;

public interface ModelObjectParserContract<O extends ModelObject, E extends Throwable, P extends ModelObjectParser<O, E>> extends
  JSOM0LogTestContract
{
  P getParser(
    final @Nonnull String file)
    throws IOException,
      Error,
      ConstraintError,
      E;
}
