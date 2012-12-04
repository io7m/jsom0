package com.io7m.jsom0;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLInterfaceEmbedded;
import com.io7m.jlog.Log;
import com.io7m.jsom0.contracts.ModelObjectVBOTestContract;

public final class LWJGL30ModelObjectVBOTest extends
  ModelObjectVBOTestContract
{
  @Override public Log getLog()
  {
    return LWJGL30TestLog.getLog();
  }

  @Override public GLInterfaceEmbedded makeNewGL()
    throws GLException,
      ConstraintError
  {
    return LWJGL30TestDisplay.makeFreshGLEmbedded();
  }
}
