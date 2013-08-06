package com.io7m.jsom0;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jaux.functional.Option;
import com.io7m.jaux.functional.Option.Some;
import com.io7m.jcanephora.JCGLException;
import com.io7m.jcanephora.JCGLImplementation;
import com.io7m.jcanephora.JCGLInterfaceGL3;
import com.io7m.jcanephora.JCGLUnsupportedException;
import com.io7m.jlog.Log;
import com.io7m.jsom0.parser.contracts.ModelObjectParserVBOMappedTestContract;

public final class LWJGL30ModelObjectParserVBOMappedTest extends
  ModelObjectParserVBOMappedTestContract<JCGLInterfaceGL3>
{
  @Override public Log getLog()
  {
    return LWJGLTestContext
      .getLog(LWJGLTestContext.LOG_DESTINATION_OPENGL_3_X);
  }

  @Override public boolean isSupported()
  {
    return LWJGLTestContext.isOpenGL30Supported();
  }

  @Override public JCGLInterfaceGL3 makeNewGL()
    throws JCGLException,
      ConstraintError,
      JCGLUnsupportedException
  {
    final JCGLImplementation gi = LWJGLTestContext.makeContextWithOpenGL3_X();
    final Option<JCGLInterfaceGL3> go = gi.getGL3();

    if (go.isSome()) {
      final Some<JCGLInterfaceGL3> gs = (Option.Some<JCGLInterfaceGL3>) go;
      return gs.value;
    }

    throw new UnreachableCodeException();
  }
}
