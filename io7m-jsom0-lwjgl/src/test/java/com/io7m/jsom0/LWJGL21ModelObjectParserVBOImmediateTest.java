package com.io7m.jsom0;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jaux.functional.Option;
import com.io7m.jaux.functional.Option.Some;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLImplementation;
import com.io7m.jcanephora.GLInterfaceGL2;
import com.io7m.jcanephora.GLUnsupportedException;
import com.io7m.jlog.Log;
import com.io7m.jsom0.parser.contracts.ModelObjectParserVBOImmediateTestContract;

public final class LWJGL21ModelObjectParserVBOImmediateTest extends
  ModelObjectParserVBOImmediateTestContract<GLInterfaceGL2>
{
  @Override public boolean isSupported()
  {
    return LWJGLTestContext.isOpenGL21Supported();
  }

  @Override public Log getLog()
  {
    return LWJGLTestContext
      .getLog(LWJGLTestContext.LOG_DESTINATION_OPENGL_2_1);
  }

  @Override public GLInterfaceGL2 makeNewGL()
    throws GLException,
      ConstraintError,
      GLUnsupportedException
  {
    final GLImplementation gi = LWJGLTestContext.makeContextWithOpenGL21_X();
    final Option<GLInterfaceGL2> go = gi.getGL2();

    if (go.isSome()) {
      final Some<GLInterfaceGL2> gs = (Option.Some<GLInterfaceGL2>) go;
      return gs.value;
    }

    throw new UnreachableCodeException();
  }
}
