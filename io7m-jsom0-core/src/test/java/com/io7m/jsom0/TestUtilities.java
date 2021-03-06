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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.Nonnull;

import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jlog.Log;

public final class TestUtilities
{
  public static @Nonnull Log getLog()
    throws IOException
  {
    final InputStream rstream =
      TestUtilities.class
        .getResourceAsStream("/com/io7m/jsom0/test.properties");
    final Properties props = new Properties();
    props.load(rstream);
    rstream.close();
    return new Log(props, "com.io7m.jsom0", "test");
  }

  private TestUtilities()
  {
    throw new UnreachableCodeException();
  }
}
