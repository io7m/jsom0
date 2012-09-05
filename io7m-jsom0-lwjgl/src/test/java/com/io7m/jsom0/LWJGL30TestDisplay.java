package com.io7m.jsom0;

import org.lwjgl.opengl.Pbuffer;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLInterface;
import com.io7m.jcanephora.GLInterfaceLWJGL30;

public final class LWJGL30TestDisplay
{
  private static Pbuffer buffer = null;

  public static GLInterface makeFreshGL()
    throws GLException,
      ConstraintError
  {
    LWJGL30TestDisplay.openContext();
    return new GLInterfaceLWJGL30(LWJGL30TestLog.getLog());
  }

  private static Pbuffer openContext()
  {
    if (LWJGL30TestDisplay.buffer != null) {
      LWJGL30TestDisplay.buffer.destroy();
    }

    LWJGL30TestDisplay.buffer = LWJGL30.createOffscreenDisplay(640, 480);
    return LWJGL30TestDisplay.buffer;
  }
}
