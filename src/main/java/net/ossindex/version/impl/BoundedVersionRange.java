/**
 * Copyright (c) 2016 Vör Security Inc.
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

import com.github.zafarkhaja.semver.expr.Expression;
import net.ossindex.version.IVersion;
import net.ossindex.version.IVersionRange;

/** A bounded version range is capped at both ends, for example 1.2.5 - 1.2.8
 *
 * @author Ken Duck
 *
 */
public class BoundedVersionRange
    extends AbstractCommonRange
    implements IVersionRange
{
  private String type;

  private Expression expression;

  /**
   * Used for both atomic and simple versions
   */
  private IVersion minimum;

  private IVersion maximum;

  /**
   * Remember the range for toString
   */
  private String range;

  private boolean hasErrors = false;

  /** A "range" of a single version
   *
   * @param version
   */
  public BoundedVersionRange(SemanticVersion version)
  {
    this.minimum = version;
  }

  /** Inclusive range
   *
   * @param min
   * @param max
   */
  public BoundedVersionRange(IVersion min, IVersion max) {
    this.minimum = min;
    this.maximum = max;
    this.range = ">=" + min + " <=" + max;
  }

  /*
   * (non-Javadoc)
   * @see net.ossindex.version.IVersionRange#contains(net.ossindex.version.IVersion)
   */
  @Override
  public boolean contains(IVersion version)
  {
    // This will match both SemanticVersion and FlexibleSemanticVersion
    if (version instanceof SemanticVersion) {
      return expression.interpret(((SemanticVersion) version).getVersionImpl());
    }
    throw new IllegalArgumentException("Semantic ranges expect semantic versions");
  }

  /*
   * (non-Javadoc)
   * @see net.ossindex.version.impl.IVersionRange#isAtomic()
   */
  @Override
  public boolean isDiscrete()
  {
    return minimum != null && maximum == null;
  }

  /*
   * (non-Javadoc)
   * @see net.ossindex.version.impl.IVersionRange#isSimple()
   */
  @Override
  public boolean isSimple() {
    return minimum != null && maximum != null;
  }

  /*
   * (non-Javadoc)
   * @see net.ossindex.version.impl.IVersionRange#getMinimum()
   */
  @Override
  public IVersion getMinimum()
  {
    return minimum;
  }

  /*
   * (non-Javadoc)
   * @see net.ossindex.version.impl.IVersionRange#getMaximum()
   */
  @Override
  public IVersion getMaximum()
  {
    return maximum;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    if (range != null) {
      return range;
    }
    if (minimum != null) {
      if (maximum == null) {
        return minimum.toString();
      }
    }
    throw new UnsupportedOperationException("Cannot get string for range");
  }

  @Override
  public String toMavenString()
  {
    if (range != null) {
      return "[" + minimum + "," + maximum + "]";
    }
    if (minimum != null) {
      if (maximum == null) {
        return minimum.toString();
      }
    }
    throw new UnsupportedOperationException("Cannot get maven string for range");
  }

  /*
   * (non-Javadoc)
   * @see net.ossindex.version.IVersionRange#intersects(net.ossindex.version.IVersionRange)
   */
  @Override
  public boolean intersects(IVersionRange yourRange)
  {
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   * @see net.ossindex.version.IVersionRange#getSimplifiedRange()
   */
  @Override
  public IVersionRange getSimplifiedRange() {
    return this;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setHasErrors(boolean b) {
    hasErrors = b;
  }

  public boolean hasErrors() {
    return hasErrors;
  }
}
