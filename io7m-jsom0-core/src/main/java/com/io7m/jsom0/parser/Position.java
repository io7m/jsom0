package com.io7m.jsom0.parser;

public final class Position
{
  public final int line;
  public final int column;

  public Position(
    final int line,
    final int column)
  {
    this.line = line;
    this.column = column;
  }

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
    final Position other = (Position) obj;
    if (this.column != other.column) {
      return false;
    }
    if (this.line != other.line) {
      return false;
    }
    return true;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.column;
    result = (prime * result) + this.line;
    return result;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append(this.line + 1);
    builder.append(":");
    builder.append(this.column);
    return builder.toString();
  }
}
