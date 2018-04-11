package net.ossindex.version;

/**
 * The parser is not happy with us injection a regular exception, so make this one a RuntimeException. We will
 * catch and convert it later.
 */
public class InvalidRangeRuntimeException
    extends RuntimeException
{
  public InvalidRangeRuntimeException(String msg) {
    super(msg);
  }
}
