package com.io7m.jsom0;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLInterfaceEmbedded;
import com.io7m.jlog.Log;
import com.io7m.jsom0.parser.contracts.ModelObjectParserVBOImmediateTestContract;

public final class JOGL30ModelObjectParserVBOImmediateTest extends
  ModelObjectParserVBOImmediateTestContract
{
  @Override public Log getLog()
  {
    return JOGL30TestLog.getLog();
  }

  @Override public GLInterfaceEmbedded makeNewGL()
    throws GLException,
      ConstraintError
  {
    return JOGL30TestDisplay.makeFreshGLEmbedded();
  }
}
