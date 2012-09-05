package com.io7m.jsom0.parser.contracts;

import java.io.IOException;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLInterface;
import com.io7m.jsom0.contracts.Jsom0GLTestContract;
import com.io7m.jsom0.parser.Error;
import com.io7m.jsom0.parser.ModelObjectParser;

public interface ModelObjectParserContract extends Jsom0GLTestContract
{
  ModelObjectParser getParser(
    final @Nonnull String file,
    final @Nonnull GLInterface gl)
    throws IOException,
      Error,
      ConstraintError,
      GLException;
}
