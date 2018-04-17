package net.ossindex.version;

/**
 * The parser is not happy with us injecting a regular exception, so make this one a RuntimeException. We will
 * catch and convert it later.
 */
public class InvalidRangeRuntimeException
    extends RuntimeException
{
  public InvalidRangeRuntimeException(String msg) {
    super(msg);
  }

  public InvalidRangeRuntimeException(final String message, final InvalidRangeException cause) {
    super(message, cause);
  }
}
