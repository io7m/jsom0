/*
 * Copyright © 2013 <code@io7m.com> http://io7m.com
 * 
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

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

public final class JOGL3pModelObjectParserVBOImmediateTest extends
  ModelObjectParserVBOImmediateTestContract<GLInterfaceGL3>
{
  @Override public Log getLog()
  {
    return JOGLTestDisplay.getLog(JOGLTestDisplay.LOG_DESTINATION_OPENGL_3_p);
  }

  @Override public boolean isSupported()
  {
    return JOGLTestDisplay.isOpenGL3pSupported();
  }

  @Override public GLInterfaceGL3 makeNewGL()
    throws GLException,
      ConstraintError,
      GLUnsupportedException
  {
    final GLImplementation gi = JOGLTestDisplay.makeContextWithOpenGL3_p();
    final Option<GLInterfaceGL3> go = gi.getGL3();

    if (go.isSome()) {
      final Some<GLInterfaceGL3> gs = (Option.Some<GLInterfaceGL3>) go;
      return gs.value;
    }

    throw new UnreachableCodeException();
  }
}
