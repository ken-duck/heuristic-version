package net.ossindex.version;

/**
 * FIXME: This should extend exception, except that the library has dependents not expecting it. Upversion and fix.
 */
public class InvalidRangeException extends RuntimeException
{
  public InvalidRangeException(String msg) {
    super(msg);
  }
}
