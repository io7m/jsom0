package com.io7m.jsom0;

import org.lwjgl.opengl.Pbuffer;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLInterface;
import com.io7m.jcanephora.GLInterfaceEmbedded;
import com.io7m.jcanephora.GLInterfaceEmbedded_LWJGL_ES2;
import com.io7m.jcanephora.GLInterface_LWJGL30;

public final class LWJGL30TestDisplay
{
  private static Pbuffer buffer = null;

  public static GLInterfaceEmbedded makeFreshGLEmbedded()
    throws GLException,
      ConstraintError
  {
    LWJGL30TestDisplay.openContext();
    return new GLInterfaceEmbedded_LWJGL_ES2(LWJGL30TestLog.getLog());
  }

  public static boolean isFullGLSupported()
  {
    /**
     * XXX: What's the right way to test context compatibility in LWJGL?
     */
    final boolean supported = true;
    System.err.println("GL is supported: " + supported);
    return supported;
  }

  public static GLInterface makeFreshGLFull()
    throws GLException,
      ConstraintError
  {
    LWJGL30TestDisplay.openContext();
    return new GLInterface_LWJGL30(LWJGL30TestLog.getLog());
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
