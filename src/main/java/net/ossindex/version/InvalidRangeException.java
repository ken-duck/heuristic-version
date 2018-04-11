package net.ossindex.version;

public class InvalidRangeException
    extends Exception
{
  public InvalidRangeException(Throwable cause) {
    super(cause);
  }

  public InvalidRangeException(String msg) {
    super(msg);
  }
}
