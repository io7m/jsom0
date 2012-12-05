package com.io7m.jsom0;

import org.junit.Assume;
import org.junit.BeforeClass;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLInterface;
import com.io7m.jlog.Log;
import com.io7m.jsom0.parser.contracts.ModelObjectParserVBOMappedTestContract;

public final class LWJGL30ModelObjectParserVBOMappedTest extends
  ModelObjectParserVBOMappedTestContract
{
  @BeforeClass public static void isSupported()
  {
    Assume.assumeTrue(LWJGL30TestDisplay.isFullGLSupported());
  }

  @Override public Log getLog()
  {
    return LWJGL30TestLog.getLog();
  }

  @Override public GLInterface makeNewGL()
    throws GLException,
      ConstraintError
  {
    return LWJGL30TestDisplay.makeFreshGLFull();
  }
}
