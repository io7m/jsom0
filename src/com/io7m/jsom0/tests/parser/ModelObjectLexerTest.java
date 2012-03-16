package com.io7m.jsom0.tests.parser;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Test;

import com.io7m.jsom0.parser.Error;
import com.io7m.jsom0.parser.ModelObjectLexer;
import com.io7m.jsom0.parser.ModelObjectToken;
import com.io7m.jsom0.parser.ModelObjectTokenType;

public class ModelObjectLexerTest
{
  private static ModelObjectLexer getLexer(
    final String file)
    throws IOException
  {
    final BufferedInputStream stream =
      new BufferedInputStream(new FileInputStream(file));
    final ModelObjectLexer lexer = new ModelObjectLexer(stream);
    return lexer;
  }

  @Test(timeout = 10000) public void testLexAll()
    throws IOException,
      Error
  {
    final ModelObjectLexer lexer =
      ModelObjectLexerTest.getLexer("test-data/lex-all.txt");

    for (;;) {
      final ModelObjectToken token = lexer.token();
      if (token.type == ModelObjectTokenType.OBJECT_TOKEN_EOF) {
        break;
      }
      System.out.println(token);
    }
  }
}
