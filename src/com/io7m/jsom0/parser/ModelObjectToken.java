package com.io7m.jsom0.parser;

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

    throw new AssertionError("unreachable code: report this bug!");
  }

  public final Position             position;
  public final ModelObjectTokenType type;

  public final String               value;

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
    if (this.position == null) {
      if (other.position != null) {
        return false;
      }
    } else if (!this.position.equals(other.position)) {
      return false;
    }
    if (this.type != other.type) {
      return false;
    }
    if (this.value == null) {
      if (other.value != null) {
        return false;
      }
    } else if (!this.value.equals(other.value)) {
      return false;
    }
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result =
      (prime * result)
        + ((this.position == null) ? 0 : this.position.hashCode());
    result =
      (prime * result) + ((this.type == null) ? 0 : this.type.hashCode());
    result =
      (prime * result) + ((this.value == null) ? 0 : this.value.hashCode());
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
      default:
        return ModelObjectToken.prettyTypeName(this.type);
    }
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
