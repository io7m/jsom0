package com.io7m.jsom0.parser.contracts;

import java.io.IOException;
import java.io.InputStream;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jcanephora.GLException;
import com.io7m.jsom0.ModelObjectVBO;
import com.io7m.jsom0.contracts.JSOM0GLTestContract;
import com.io7m.jsom0.parser.Error;
import com.io7m.jsom0.parser.ModelObjectParserVBOImmediate;

public abstract class ModelObjectParserVBOMappedTestContract extends
  ModelObjectParserTestContract<ModelObjectVBO, GLException, ModelObjectParserVBOImmediate> implements
  JSOM0GLTestContract
{
  @SuppressWarnings("resource") @Override public
    ModelObjectParserVBOImmediate
    getParser(
      final String file)
      throws IOException,
        Error,
        ConstraintError,
        GLException
  {
    final InputStream stream =
      ModelObjectParserVBOMappedTestContract.class.getResourceAsStream(file);

    return new ModelObjectParserVBOImmediate(
      file,
      stream,
      this.getLog(),
      this.makeNewGL());
  }
}
