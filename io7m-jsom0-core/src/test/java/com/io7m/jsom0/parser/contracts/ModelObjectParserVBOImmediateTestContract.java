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

package com.io7m.jsom0.parser.contracts;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Assume;
import org.junit.Before;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jcanephora.GLArrayBuffers;
import com.io7m.jcanephora.GLException;
import com.io7m.jcanephora.GLIndexBuffers;
import com.io7m.jcanephora.GLUnsupportedException;
import com.io7m.jsom0.ModelObjectVBO;
import com.io7m.jsom0.contracts.JSOM0GLUnmappedTestContract;
import com.io7m.jsom0.parser.Error;
import com.io7m.jsom0.parser.ModelObjectParserVBOImmediate;

public abstract class ModelObjectParserVBOImmediateTestContract<G extends GLArrayBuffers & GLIndexBuffers> extends
  ModelObjectParserTestContract<ModelObjectVBO, GLException, ModelObjectParserVBOImmediate<G>> implements
  JSOM0GLUnmappedTestContract
{
  @Before public final void checkSupport()
  {
    Assume.assumeTrue(this.isSupported());
  }

  @SuppressWarnings("resource") @Override public
    ModelObjectParserVBOImmediate<G>
    getParser(
      final String file)
      throws IOException,
        Error,
        ConstraintError,
        GLException,
        GLUnsupportedException
  {
    final InputStream stream =
      ModelObjectParserVBOImmediateTestContract.class
        .getResourceAsStream(file);
    final G gl = this.makeNewGL();

    return new ModelObjectParserVBOImmediate<G>(
      file,
      stream,
      this.getLog(),
      gl);
  }
}
