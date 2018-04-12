/**
 * Copyright (c) 2015 Vör Security Inc.
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

import java.util.regex.Pattern;

import net.ossindex.version.IVersion;
import net.ossindex.version.InvalidRangeException;

/**
 * Simple version that is based on name comparisons. This should be used as
 * a last resort.
 *
 * @author Ken Duck
 */
public class NamedVersion
    implements IVersion
{
  // These characters have special meaning for semantic ranges, so should not be in named versions
  private static final Pattern SEMANTIC_RANGE_SPECIAL_CHARS = Pattern.compile("[><=|&]");

  // Maven ranges star with ( or [, so we should not allow any version wih these characters to become named versions
  private static final Pattern SET_RANGE_SPECIAL_CHARS = Pattern.compile("^[\\(\\[]");

  // We just won't let these charactersm happen in a named version because that would be madness
  private static final Pattern INVALID_VERSION_CHARS = Pattern.compile("[ \t\n\r]");

  private String name;

  public NamedVersion(String name) throws InvalidRangeException
  {
    if (!isValidNamedVersion(name)) {
      throw new InvalidRangeException("Could not parse: " + name);
    }
    this.name = name;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(IVersion o)
  {
    return name.compareTo(o.toString());
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    return name.hashCode();
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object o)
  {
    if (o instanceof NamedVersion) {
      return name.equals(((NamedVersion) o).name);
    }
    return false;
  }

  /*
   * (non-Javadoc)
   * @see net.ossindex.version.IVersion#getMajor()
   */
  @Override
  public int getMajor() {
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   * @see net.ossindex.version.IVersion#getMinor()
   */
  @Override
  public int getMinor() {
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   * @see net.ossindex.version.IVersion#getPatch()
   */
  @Override
  public int getPatch() {
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   * @see net.ossindex.version.IVersion#getBuild()
   */
  @Override
  public int getBuild() {
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   * @see net.ossindex.version.IVersion#isStable()
   */
  @Override
  public boolean isStable() {
    return false;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return name;
  }

  /**
   * People use all sorts of whack characters in version. We are just excluding
   * the smallest set that we can.
   */
  private boolean isValidNamedVersion(final String s) {
    if (SEMANTIC_RANGE_SPECIAL_CHARS.matcher(s).find()
        || SET_RANGE_SPECIAL_CHARS.matcher(s).find()
        || INVALID_VERSION_CHARS.matcher(s).find()
        ) {
      return false;
    }
    return true;
  }
}
