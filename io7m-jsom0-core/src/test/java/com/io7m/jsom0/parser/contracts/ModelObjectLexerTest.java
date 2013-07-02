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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.io7m.jsom0.parser.Error;
import com.io7m.jsom0.parser.ModelObjectLexer;
import com.io7m.jsom0.parser.ModelObjectToken;
import com.io7m.jsom0.parser.ModelObjectTokenType;

public class ModelObjectLexerTest
{
  @SuppressWarnings("resource") private static ModelObjectLexer getLexer(
    final String file)
  {
    final InputStream stream =
      ModelObjectLexerTest.class.getResourceAsStream(file);
    final BufferedInputStream bstream = new BufferedInputStream(stream);
    final ModelObjectLexer lexer = new ModelObjectLexer(bstream);
    return lexer;
  }

  @SuppressWarnings("static-method") @Test(timeout = 10000) public
    void
    testLexAll()
      throws IOException,
        Error
  {
    final ModelObjectLexer lexer =
      ModelObjectLexerTest.getLexer("/com/io7m/jsom0/lex-all.txt");

    for (;;) {
      final ModelObjectToken token = lexer.token();
      if (token.type == ModelObjectTokenType.OBJECT_TOKEN_EOF) {
        break;
      }
      System.out.println(token);
    }
  }
}
