package com.io7m.jsom0.parser;

import java.util.Iterator;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * Parse error type.
 */

public final class Error extends Exception
{
  public enum Code
  {
    ERROR_CODE_LEXICAL_ERROR,
    ERROR_CODE_UNEXPECTED_SYMBOL,
    ERROR_CODE_DUPLICATE_ELEMENT,
    ERROR_CODE_MISSING_ELEMENT,
    ERROR_CODE_UNEXPECTED_TOKEN,
    ERROR_CODE_RANGE_ERROR
  }

  private static final long serialVersionUID = 1L;

  static Error arrayTooLarge(
    final @Nonnull Position position,
    final long elements,
    final long maximum)
  {
    final StringBuilder builder = new StringBuilder();
    builder.append(position);
    builder.append(": array is too large (");
    builder.append(elements);
    builder.append(" elements, maximum is ");
    builder.append(maximum);
    builder.append(")");

    return new Error(Code.ERROR_CODE_RANGE_ERROR, builder.toString());
  }

  static Error arrayTooSmall(
    final @Nonnull Position position,
    final long elements,
    final long maximum)
  {
    final StringBuilder builder = new StringBuilder();
    builder.append(position);
    builder.append(": array is too small (");
    builder.append(elements);
    builder.append(" elements, minimum is ");
    builder.append(maximum);
    builder.append(")");

    return new Error(Code.ERROR_CODE_RANGE_ERROR, builder.toString());
  }

  static Error duplicateElement(
    final @Nonnull String element)
  {
    return new Error(Code.ERROR_CODE_DUPLICATE_ELEMENT, "element '"
      + element
      + "' already specified");
  }

  static Error expectedSymbol(
    final @Nonnull ModelObjectToken received)
  {
    final StringBuilder builder = new StringBuilder();
    builder.append(received.position);
    builder.append(": parse error: expected a symbol but got '");
    builder.append(received.prettyName());
    builder.append("'");

    return new Error(
      Code.ERROR_CODE_UNEXPECTED_TOKEN,
      builder.toString(),
      ModelObjectTokenType.OBJECT_TOKEN_SYMBOL,
      received);
  }

  public static Error indexOutOfRange(
    final @Nonnull Position position,
    final int value,
    final int minimum,
    final int maximum)
  {
    final StringBuilder builder = new StringBuilder();
    builder.append(position);
    builder.append(": index ");
    builder.append(value);
    builder.append(" is not in the range ");
    builder.append(minimum);
    builder.append(" .. ");
    builder.append(maximum);

    return new Error(Code.ERROR_CODE_RANGE_ERROR, builder.toString());
  }

  public static Error lexicalError(
    final @Nonnull String message)
  {
    return new Error(Code.ERROR_CODE_LEXICAL_ERROR, message);
  }

  static Error missingElement(
    final @Nonnull String element)
  {
    return new Error(Code.ERROR_CODE_MISSING_ELEMENT, "required element '"
      + element
      + "' was not specified");
  }

  static Error unexpectedOneOf(
    final @Nonnull ModelObjectToken received,
    final @Nonnull Set<String> type_names)
  {
    final StringBuilder builder = new StringBuilder();
    builder.append(received.position);
    builder.append(": parse error: expected one of {");

    final int size = type_names.size();
    int index = 0;
    final Iterator<String> iterator = type_names.iterator();
    while (iterator.hasNext()) {
      ++index;
      builder.append("'");
      builder.append(iterator.next());
      builder.append("'");
      if (index < (size - 1)) {
        builder.append(" | ");
      }
    }

    builder.append("} but got '");
    builder.append(received.prettyName());
    builder.append("'");

    return new Error(
      Code.ERROR_CODE_UNEXPECTED_TOKEN,
      builder.toString(),
      null,
      received);
  }

  static Error unexpectedSymbol(
    final @Nonnull ModelObjectToken received,
    final @Nonnull String expected)
  {
    final StringBuilder builder = new StringBuilder();
    builder.append(received.position);
    builder.append(": parse error: expected symbol '");
    builder.append(expected);
    builder.append("' but got '");
    builder.append(received.prettyName());
    builder.append("'");

    return new Error(
      Code.ERROR_CODE_UNEXPECTED_SYMBOL,
      builder.toString(),
      received,
      ModelObjectTokenType.OBJECT_TOKEN_SYMBOL);
  }

  static Error unexpectedToken(
    final @Nonnull ModelObjectToken received,
    final @Nonnull ModelObjectTokenType expected)
  {
    final StringBuilder builder = new StringBuilder();
    builder.append(received.position);
    builder.append(": parse error: expected '");
    builder.append(ModelObjectToken.prettyTypeName(expected));
    builder.append("' but got '");
    builder.append(received.prettyName());
    builder.append("'");

    return new Error(
      Code.ERROR_CODE_UNEXPECTED_TOKEN,
      builder.toString(),
      expected,
      received);
  }

  public static Error unexpectedTokenAlt(
    final @Nonnull Position position,
    final @Nonnull String message)
  {
    return new Error(Code.ERROR_CODE_UNEXPECTED_SYMBOL, position
      + ": "
      + message);
  }

  private final @CheckForNull ModelObjectToken     expected_token;

  private final @CheckForNull ModelObjectToken     received_token;

  private final @CheckForNull ModelObjectTokenType expected_type;

  private final @CheckForNull ModelObjectTokenType received_type;

  private final @Nonnull Code                      code;

  private Error(
    final @Nonnull Code code,
    final @Nonnull String message)
  {
    super(message);
    this.code = code;
    this.expected_token = null;
    this.expected_type = null;
    this.received_token = null;
    this.received_type = null;
  }

  private Error(
    final @Nonnull Code code,
    final @Nonnull String message,
    final @Nonnull ModelObjectToken expected,
    final @Nonnull ModelObjectTokenType received)
  {
    super(message);
    this.code = code;
    this.expected_token = expected;
    this.expected_type = null;
    this.received_type = received;
    this.received_token = null;
  }

  private Error(
    final @Nonnull Code code,
    final @Nonnull String message,
    final @Nonnull ModelObjectTokenType expected,
    final @Nonnull ModelObjectToken received)
  {
    super(message);
    this.code = code;
    this.expected_token = null;
    this.expected_type = expected;
    this.received_type = received.type;
    this.received_token = received;
  }

  public @Nonnull Code errorCode()
  {
    return this.code;
  }

  public @CheckForNull ModelObjectToken expectedToken()
  {
    return this.expected_token;
  }

  public @CheckForNull ModelObjectTokenType expectedTokenType()
  {
    return this.expected_type;
  }

  public @CheckForNull ModelObjectToken receivedToken()
  {
    return this.received_token;
  }

  public @CheckForNull ModelObjectTokenType receivedTokenType()
  {
    return this.received_type;
  }
}
