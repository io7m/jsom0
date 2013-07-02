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

package com.io7m.jsom0.contracts;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jcanephora.GLArrayBuffers;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLIndexBuffers;
import com.io7m.jcanephora.GLUnsupportedException;

public interface JSOM0GLUnmappedTestContract extends JSOM0LogTestContract
{
  public <G extends GLArrayBuffers & GLIndexBuffers> G makeNewGL()
    throws GLException,
      ConstraintError,
      GLUnsupportedException;

  public boolean isSupported();
}
