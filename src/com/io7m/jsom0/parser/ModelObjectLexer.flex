package com.io7m.model.parser;

import java.io.IOException;
import java.lang.StringBuilder;

%%

%apiprivate
%class ModelObjectLexer
%column
%final
%line
%type ModelObjectToken
%unicode

%{
  private final StringBuilder buffer = new StringBuilder();

  public ModelObjectToken token()
    throws IOException, Error
  {
    return this.yylex();
  }

%}

Literal_Decimal = ("0" | [1-9]+ [0-9]*)
Literal_Float   = ("-")? ("0" | [1-9]+ [0-9]*) "." [0-9]+
Line_Terminator = \r|\n|\r\n
Input_Character = [^\r\n]
Whitespace      = [ \t\f] | {Line_Terminator}
Comment         = "--" {Input_Character}* {Line_Terminator}
Name            = ([:letter:] | [:digit:] | "_" | "-" | ".")+
StringCharacter = [^\r\n\"\\]

%state STATE_STRING

%%

<YYINITIAL> {
  \"                  { this.yybegin(STATE_STRING); this.buffer.setLength(0); }
  
  { Comment         } { /* Ignore */ }
  { Whitespace      } { /* Ignore */ }
  ";"                 { return new ModelObjectToken (ModelObjectTokenType.OBJECT_TOKEN_SEMICOLON,       yytext(), new Position (yyline, yycolumn)); }
  { Literal_Decimal } { return new ModelObjectToken (ModelObjectTokenType.OBJECT_TOKEN_LITERAL_DECIMAL, yytext(), new Position (yyline, yycolumn)); }
  { Literal_Float   } { return new ModelObjectToken (ModelObjectTokenType.OBJECT_TOKEN_LITERAL_FLOAT,   yytext(), new Position (yyline, yycolumn)); }
  { Name            } { return new ModelObjectToken (ModelObjectTokenType.OBJECT_TOKEN_SYMBOL,          yytext(), new Position (yyline, yycolumn)); }
}

<STATE_STRING> {
  \" {
    yybegin(YYINITIAL);
    return new ModelObjectToken (ModelObjectTokenType.OBJECT_TOKEN_LITERAL_STRING, this.buffer.toString(), new Position (yyline, yycolumn));
  }
  
  {StringCharacter}+ { this.buffer.append(yytext()); }
  
  /* Escape sequences */
  "\\b"  { this.buffer.append( '\b' ); }
  "\\t"  { this.buffer.append( '\t' ); }
  "\\n"  { this.buffer.append( '\n' ); }
  "\\f"  { this.buffer.append( '\f' ); }
  "\\r"  { this.buffer.append( '\r' ); }
  "\\\"" { this.buffer.append( '\"' ); }
  "\\'"  { this.buffer.append( '\'' ); }
  "\\\\" { this.buffer.append( '\\' ); }
  
  /* Error cases */
  \\.               { throw Error.lexicalError("Illegal escape sequence \"" + yytext() + "\""); }
  {Line_Terminator} { throw Error.lexicalError("Unterminated string at end of line"); }
}

<<EOF>> { return new ModelObjectToken (ModelObjectTokenType.OBJECT_TOKEN_EOF, "", new Position (yyline, yycolumn)); }

/* error fallback */
.|\n { throw Error.lexicalError("Illegal character <"+ yytext()+">"); }

