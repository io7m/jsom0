package com.io7m.jsom0;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jaux.functional.Option;
import com.io7m.jaux.functional.Option.Some;
import com.io7m.jcanephora.JCGLException;
import com.io7m.jcanephora.JCGLImplementation;
import com.io7m.jcanephora.JCGLInterfaceGL2;
import com.io7m.jcanephora.JCGLUnsupportedException;
import com.io7m.jlog.Log;
import com.io7m.jsom0.parser.contracts.ModelObjectParserVBOImmediateTestContract;

public final class LWJGL21ModelObjectParserVBOImmediateTest extends
  ModelObjectParserVBOImmediateTestContract<JCGLInterfaceGL2>
{
  @Override public Log getLog()
  {
    return LWJGLTestContext
      .getLog(LWJGLTestContext.LOG_DESTINATION_OPENGL_2_1);
  }

  @Override public boolean isSupported()
  {
    return LWJGLTestContext.isOpenGL21Supported();
  }

  @Override public JCGLInterfaceGL2 makeNewGL()
    throws JCGLException,
      ConstraintError,
      JCGLUnsupportedException
  {
    final JCGLImplementation gi =
      LWJGLTestContext.makeContextWithOpenGL21_X();
    final Option<JCGLInterfaceGL2> go = gi.getGL2();

    if (go.isSome()) {
      final Some<JCGLInterfaceGL2> gs = (Option.Some<JCGLInterfaceGL2>) go;
      return gs.value;
    }

    throw new UnreachableCodeException();
  }
}
