package com.io7m.jsom0;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jaux.functional.Option;
import com.io7m.jaux.functional.Option.Some;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLImplementation;
import com.io7m.jcanephora.GLInterfaceGL3;
import com.io7m.jcanephora.GLUnsupportedException;
import com.io7m.jlog.Log;
import com.io7m.jsom0.parser.contracts.ModelObjectParserVBOImmediateTestContract;

public final class LWJGL30ModelObjectParserVBOImmediateTest extends
  ModelObjectParserVBOImmediateTestContract<GLInterfaceGL3>
{
  @Override public Log getLog()
  {
    return LWJGLTestContext
      .getLog(LWJGLTestContext.LOG_DESTINATION_OPENGL_3_0);
  }

  @Override public boolean isSupported()
  {
    return LWJGLTestContext.isOpenGL30Supported();
  }

  @Override public GLInterfaceGL3 makeNewGL()
    throws GLException,
      ConstraintError,
      GLUnsupportedException
  {
    final GLImplementation gi = LWJGLTestContext.makeContextWithOpenGL3_X();
    final Option<GLInterfaceGL3> go = gi.getGL3();

    if (go.isSome()) {
      final Some<GLInterfaceGL3> gs = (Option.Some<GLInterfaceGL3>) go;
      return gs.value;
    }

    throw new UnreachableCodeException();
  }
}
