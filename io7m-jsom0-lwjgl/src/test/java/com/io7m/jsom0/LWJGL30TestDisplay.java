package com.io7m.jsom0;

import org.lwjgl.opengl.Pbuffer;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Option;
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

  public static Option<GLInterface> makeFreshGLFull()
    throws GLException,
      ConstraintError
  {
    LWJGL30TestDisplay.openContext();
    return new Option.Some<GLInterface>(new GLInterface_LWJGL30(
      LWJGL30TestLog.getLog()));
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
