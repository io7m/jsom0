package com.io7m.jsom0.parser.contracts;

import java.io.IOException;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jcanephora.GLException;
import com.io7m.jsom0.contracts.JSOM0GLEmbeddedTestContract;
import com.io7m.jsom0.parser.Error;
import com.io7m.jsom0.parser.ModelMaterialParser;

public interface ModelMaterialParserContract extends JSOM0GLEmbeddedTestContract
{
  ModelMaterialParser getParser(
    final String file)
    throws IOException,
      Error,
      ConstraintError,
      GLException;
}
