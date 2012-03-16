package com.io7m.jsom0.tests.parser;

import java.io.IOException;

import com.io7m.jsom0.parser.Error;
import com.io7m.jsom0.parser.ModelObjectLexer;
import com.io7m.jsom0.parser.ModelObjectToken;
import com.io7m.jsom0.parser.ModelObjectTokenType;

public final class ModelObjectLexerMain
{
  public static void main(
    final String args[])
    throws IOException,
      Error
  {
    final ModelObjectLexer lexer = new ModelObjectLexer(System.in);

    for (;;) {
      final ModelObjectToken token = lexer.token();
      if (token.type == ModelObjectTokenType.OBJECT_TOKEN_EOF) {
        break;
      }
      System.out.println(token);
    }
  }
}
