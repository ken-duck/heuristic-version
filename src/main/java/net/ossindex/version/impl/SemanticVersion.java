/**
 * Copyright (c) 2015-2017 Vor Security Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * * Neither the name of the <organization> nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.ossindex.version.impl;

import com.github.zafarkhaja.semver.Version;
import net.ossindex.version.IVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Useful docs here: https://github.com/zafarkhaja/jsemver
 *
 * Wrapper around jsemver semantic version parsing code.
 *
 * Semantic versioning is described here: http://semver.org/
 *
 * @author Ken Duck
 *
 */
public class SemanticVersion
    implements Comparable<IVersion>, IVersion
{
  private static final Logger LOG = LoggerFactory.getLogger(SemanticVersion.class);

  protected Version head;

  protected PostscriptPattern postscriptPattern;

  /**
   * Remember the number of significant digits when created. This is important
   * for some methods.
   */
  protected int significantDigits = -1;

  /** Use an external library for parsing.
   *
   * @param buf Version we are trying to parse
   */
  public SemanticVersion(String buf)
  {
    // Sadly, the external library cannot handle some characters, so we do a replacement to be as close as possible
    buf = buf.replace('_', '-');
    buf = buf.replace('~', '-');
    setVersion(buf);
  }

  public SemanticVersion(int major)
  {
    head = Version.forIntegers(major);
    significantDigits = 1;
  }

  public SemanticVersion(int major, int minor)
  {
    head = Version.forIntegers(major, minor);
    significantDigits = 2;
  }

  public SemanticVersion(int major, int minor, int patch)
  {
    head = Version.forIntegers(major, minor, patch);
    significantDigits = 3;
  }


  // Used by subclasses only
  protected SemanticVersion()
  {
  }

  /** Set the version
   *
   * @param buf Version we are trying to parse
   */
  protected void setVersion(String buf)
  {
    // HACK to handle empty postfix
    while (buf.endsWith("-")) {
      buf = buf.substring(0, buf.length() - 1);
    }
    while (buf.endsWith(".")) {
      buf = buf.substring(0, buf.length() - 1);
    }
    head = Version.valueOf(buf);

    significantDigits = -1;
  }

  /*
   * (non-Javadoc)
   * @see net.ossindex.version.IVersion#getMajor()
   */
  @Override
  public int getMajor()
  {
    if (head == null) {
      return 0;
    }
    return head.getMajorVersion();
  }

  /*
   * (non-Javadoc)
   * @see net.ossindex.version.IVersion#getMinor()
   */
  @Override
  public int getMinor()
  {
    if (head == null) {
      return 0;
    }
    return head.getMinorVersion();
  }

  /*
   * (non-Javadoc)
   * @see net.ossindex.version.IVersion#getPatch()
   */
  @Override
  public int getPatch()
  {
    if (head == null) {
      return 0;
    }
    return head.getPatchVersion();
  }

  /*
   * (non-Javadoc)
   * @see net.ossindex.version.IVersion#getBuild()
   */
  @Override
  public int getBuild() {
    throw new UnsupportedOperationException();
  }

  public boolean isPrerelease() {
    return head.getPreReleaseVersion() != null;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object o)
  {
    if (o instanceof SemanticVersion) {
      SemanticVersion v = (SemanticVersion) o;
      if (this.head != null) {
        return this.head.equals(v.head);
      }
      else {
        return super.equals(v);
      }
    }
    return false;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    if (head != null) {
      return head.hashCode();
    }
    return super.hashCode();
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(IVersion other)
  {
    if (other instanceof SemanticVersion) {
      SemanticVersion sv = (SemanticVersion) other;
      // If neither is named, then compare them as semantic values
      return this.head.compareTo(sv.head);
    }
    else {
      // Fall back to simple string comparison
      return toString().compareTo(other.toString());
    }
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return head.toString();
  }

  /** Returns true if this represents a stable release. We take this to mean
   * unnamed and no suffix.
   *
   * @return True if this is a stable release.
   */
  public boolean isStable()
  {
    System.err.println("PRERELEASE: " + head.getPreReleaseVersion());
    System.err.println("METADATA: " + head.getBuildMetadata());
    System.err.println("NORMAL: " + head.getNormalVersion());
    return head.getPreReleaseVersion() == null;
  }

  /** Get the SemVer instance.
   *
   * @return
   */
  public Version getVersionImpl()
  {
    return head;
  }

  public boolean lessThan(IVersion version) {
    // Can the versions be compared?
    if (!(version instanceof SemanticVersion)) {
      return false;
    }

    return head.lessThan(((SemanticVersion) version).head);
  }

  public boolean lessThanOrEqualTo(IVersion version) {
    // Can the versions be compared?
    if (!(version instanceof SemanticVersion)) {
      return false;
    }

    return head.lessThanOrEqualTo(((SemanticVersion) version).head);
  }

  public boolean greaterThan(IVersion version) {
    // Can the versions be compared?
    if (!(version instanceof SemanticVersion)) {
      return false;
    }

    return head.greaterThan(((SemanticVersion) version).head);
  }

  public boolean greaterThanOrEqualTo(IVersion version) {
    // Can the versions be compared?
    if (!(version instanceof SemanticVersion)) {
      return false;
    }

    return head.greaterThanOrEqualTo(((SemanticVersion) version).head);
  }

  /**
   * Get the next logical version in line. For example:
   *
   *   1.2.3 becomes 1.2.4
   */
  public SemanticVersion getNextVersion() {
    int major = head.getMajorVersion();
    int minor = head.getMinorVersion();
    int patch = head.getPatchVersion();
    return new SemanticVersion(major, minor, patch + 1);
  }

  /**
   * Get the prev logical version in line. For example:
   *
   *   1.2.3 becomes 1.2.2
   *
   * This gets ugly if we underflow, cause we don't know what the top patch version
   * for the lower minor version could be.
   */
  public SemanticVersion getPrevVersion() {
    int major = head.getMajorVersion();
    int minor = head.getMinorVersion();
    int patch = head.getPatchVersion();
    if (patch > 0) {
      return new SemanticVersion(major, minor, patch - 1);
    }
    if (minor > 0) {
      return new SemanticVersion(major, minor - 1, 999);
    }
    if (major > 0) {
      return new SemanticVersion(major - 1, 999, 999);
    }
    return new SemanticVersion(0, 0, 0);
  }

  /**
   * Strip the lowest number and increment the next one up. For example:
   *
   *   1.2.3 becomes 1.3.0
   */
  public SemanticVersion getNextParentVersion() {
    int major = head.getMajorVersion();
    int minor = head.getMinorVersion();
    int patch = head.getPatchVersion();

    switch (significantDigits) {
      case 1:
        throw new UnsupportedOperationException();
      case 2:
        major++;
        minor = 0;
        patch = 0;
        return new SemanticVersion(major, minor, patch);
      case 3:
        minor++;
        patch = 0;
        return new SemanticVersion(major, minor, patch);
      default:
        throw new UnsupportedOperationException();
    }
  }

  /**
   * Get the top of a version range when following Semantic Versionings "caret" semantics.
   *
   * https://docs.npmjs.com/misc/semver
   */
  public SemanticVersion getNextCaretVersion() {
    int major = head.getMajorVersion();
    int minor = head.getMinorVersion();
    int patch = head.getPatchVersion();

    if (major != 0) {
      return new SemanticVersion(++major, 0, 0);
    }

    if (minor != 0) {
      return new SemanticVersion(major, ++minor, 0);
    }

    return new SemanticVersion(major, minor, ++patch);
  }

  public void setPostscriptPattern(final PostscriptPattern postscriptPattern) {
    this.postscriptPattern = postscriptPattern;
  }

  public boolean hasPostfixMatch(final SemanticVersion v2) {
    // Simple string match
    if (postscriptPattern.toString().equals(v2.postscriptPattern.toString())) {
      return true;
    }

    if (postscriptPattern.matches(v2.postscriptPattern)) {
      return true;
    }

    return false;
  }
}
