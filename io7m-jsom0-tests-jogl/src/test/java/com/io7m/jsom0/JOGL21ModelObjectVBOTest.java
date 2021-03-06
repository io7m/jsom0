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
import com.io7m.jcanephora.JCGLException;
import com.io7m.jcanephora.JCGLImplementation;
import com.io7m.jcanephora.JCGLInterfaceGL2;
import com.io7m.jcanephora.JCGLInterfaceGL3;
import com.io7m.jcanephora.JCGLUnsupportedException;
import com.io7m.jlog.Log;
import com.io7m.jsom0.contracts.ModelObjectVBOTestContract;

public final class JOGL21ModelObjectVBOTest extends
  ModelObjectVBOTestContract<JCGLInterfaceGL3>
{
  @Override public Log getLog()
  {
    return JOGLTestDisplay.getLog(JOGLTestDisplay.LOG_DESTINATION_OPENGL_2_1);
  }

  @Override public boolean isSupported()
  {
    return JOGLTestDisplay.isOpenGL21WithExtensionsSupported();
  }

  @Override public JCGLInterfaceGL2 makeNewGL()
    throws JCGLException,
      ConstraintError,
      JCGLUnsupportedException
  {
    final JCGLImplementation gi = JOGLTestDisplay.makeContextWithOpenGL2_1();
    final Option<JCGLInterfaceGL2> go = gi.getGL2();

    if (go.isSome()) {
      final Some<JCGLInterfaceGL2> gs = (Option.Some<JCGLInterfaceGL2>) go;
      return gs.value;
    }

    throw new UnreachableCodeException();
  }
}
