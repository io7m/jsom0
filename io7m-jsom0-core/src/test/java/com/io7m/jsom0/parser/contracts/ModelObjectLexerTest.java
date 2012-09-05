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
