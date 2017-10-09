package net.ossindex.version.impl;

import net.ossindex.version.IVersion;

/**
 * A simple version implementation. Will eventually replace the use
 * of SemVer
 *
 * @author Ken Duck
 */
public class VersionImpl
    implements IVersion
{
  private int major;

  private int minor;

  private int patch;

  private int build;

  private String suffix;

  public VersionImpl(int major)
  {
    this.major = major;
  }

  public VersionImpl(int major, int minor)
  {
    this.major = major;
    this.minor = minor;
  }

  public VersionImpl(int major, int minor, int patch)
  {
    this.major = major;
    this.minor = minor;
    this.patch = patch;
  }

  public VersionImpl(int major, int minor, int patch, int build)
  {
    this.major = major;
    this.minor = minor;
    this.patch = patch;
  }

  public VersionImpl(int major, int minor, int patch, String suffix)
  {
    this.major = major;
    this.minor = minor;
    this.patch = patch;
    this.build = build;
    this.suffix = suffix;
  }

  public VersionImpl(int major, int minor, int patch, int build, String suffix)
  {
    this.major = major;
    this.minor = minor;
    this.patch = patch;
    this.build = build;
    this.suffix = suffix;
  }

  /*
   * (non-Javadoc)
   * @see net.ossindex.version.IVersion#getBuild()
   */
  @Override
  public int getBuild() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int compareTo(IVersion v)
  {
    if (v.getMajor() > major) {
      return -1;
    }
    if (major > v.getMajor()) {
      return 1;
    }

    if (v.getMinor() > minor) {
      return -1;
    }
    if (minor > v.getMinor()) {
      return 1;
    }

    if (v.getPatch() > patch) {
      return -1;
    }
    if (patch > v.getPatch()) {
      return 1;
    }

    // FIXME: Incomplete -- does not deal with suffix yet
    return 0;
  }

  @Override
  public int getMajor()
  {
    return major;
  }

  @Override
  public int getMinor()
  {
    return minor;
  }

  @Override
  public int getPatch()
  {
    return patch;
  }

  @Override
  public boolean isStable()
  {
    return suffix == null;
  }

}
