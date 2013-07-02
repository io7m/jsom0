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

package com.io7m.jsom0.parser;

import javax.annotation.Nonnull;

import com.io7m.jaux.UnreachableCodeException;

public final class ModelObjectToken
{
  public static String prettyTypeName(
    final ModelObjectTokenType type)
  {
    switch (type) {
      case OBJECT_TOKEN_EOF:
        return "<EOF>";
      case OBJECT_TOKEN_LITERAL_DECIMAL:
        return "decimal literal";
      case OBJECT_TOKEN_LITERAL_FLOAT:
        return "floating point literal";
      case OBJECT_TOKEN_SYMBOL:
        return "name";
      case OBJECT_TOKEN_LITERAL_STRING:
        return "string literal";
      case OBJECT_TOKEN_SEMICOLON:
        return "semicolon";
    }

    throw new UnreachableCodeException();
  }

  public final @Nonnull Position             position;
  public final @Nonnull ModelObjectTokenType type;
  public final @Nonnull String               value;

  public ModelObjectToken(
    final ModelObjectTokenType type,
    final String value,
    final Position position)
  {
    assert (type != null);
    assert (value != null);
    assert (position != null);

    this.type = type;
    this.value = value;
    this.position = position;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override public boolean equals(
    final Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    final ModelObjectToken other = (ModelObjectToken) obj;
    if (!this.position.equals(other.position)) {
      return false;
    }
    if (this.type != other.type) {
      return false;
    }
    if (!this.value.equals(other.value)) {
      return false;
    }
    return true;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.position.hashCode();
    result = (prime * result) + this.type.hashCode();
    result = (prime * result) + this.value.hashCode();
    return result;
  }

  public String prettyName()
  {
    switch (this.type) {
      case OBJECT_TOKEN_SYMBOL:
      case OBJECT_TOKEN_LITERAL_DECIMAL:
      case OBJECT_TOKEN_LITERAL_FLOAT:
      case OBJECT_TOKEN_LITERAL_STRING:
        return this.value;
      case OBJECT_TOKEN_EOF:
      case OBJECT_TOKEN_SEMICOLON:
        return ModelObjectToken.prettyTypeName(this.type);
    }

    throw new UnreachableCodeException();
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("ObjectToken ");
    builder.append(this.position.toString());
    builder.append(" ");
    builder.append(this.type);

    if (this.value.equals("") == false) {
      builder.append(" '");
      builder.append(this.value);
      builder.append("'");
    }

    return builder.toString();
  }
}
