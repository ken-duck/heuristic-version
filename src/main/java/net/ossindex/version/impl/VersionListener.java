/**
 * Copyright (c) 2015-2016 Vor Security Inc.
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

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.ossindex.version.IVersion;
import net.ossindex.version.IVersionRange;
import net.ossindex.version.parser.VersionBaseListener;
import net.ossindex.version.parser.VersionParser;

/**
 * Listener for the version parser.
 *
 * This converts all the various versions and ranges we can find to
 * semantic versions and ranges.
 *
 * @author Ken Duck
 */
public class VersionListener
    extends VersionBaseListener
{
  private static final Pattern numericHasLeadingZeroes = Pattern.compile(("\\b0+"));

  private static final Pattern startsWithDigitLetterOrHyphen = Pattern.compile("^[0-9a-zA-Z\\-]");

  private Stack<Object> stack = new Stack<Object>();

  private IVersionRange range;

  public IVersionRange getRange()
  {
    return range;
  }

  @Override
  public void exitNumeric_version(VersionParser.Numeric_versionContext ctx)
  {
    IVersion version = null;

    int count = ctx.getChildCount();
    switch (count) {
      case 1:
      case 2: {
        String token = ctx.getChild(0).getText();
        int major = Integer.parseInt(token);
        version = new SemanticVersion(major);
        break;
      }
      case 3:
      case 4: {
        int major = Integer.parseInt(ctx.getChild(0).getText());
        int minor = Integer.parseInt(ctx.getChild(2).getText());
        version = new SemanticVersion(major, minor);
        break;
      }
      case 5:
      case 6: {
        int major = Integer.parseInt(ctx.getChild(0).getText());
        int minor = Integer.parseInt(ctx.getChild(2).getText());
        try {
          int patch = Integer.parseInt(ctx.getChild(4).getText());
          version = new SemanticVersion(major, minor, patch);
        }
        catch (NumberFormatException e) {
          // This can happen if the number is a long. In this case we will force it to be an identifier as a reassonable
          // work-around.
          version = new SemanticVersion(major + "." + minor + ".0-" + ctx.getChild(4).getText());
        }
        break;
      }
      case 7:
      case 8: {
        int major = Integer.parseInt(ctx.getChild(0).getText());
        int minor = Integer.parseInt(ctx.getChild(2).getText());
        int patch = Integer.parseInt(ctx.getChild(4).getText());
        int build = Integer.parseInt(ctx.getChild(6).getText());
        version = new ExtendedSemanticVersion(major, minor, patch, build);
        break;
      }
    }
    stack.push(version);
  }

  /**
   * Normalize the postfix to something that semantic version can handle
   */
  @Override
  public void exitIdentifier(VersionParser.IdentifierContext ctx) {
    String postfix = ctx.getText();
    // A numeric postfix cannot have leading zeroes
    // FIXME: Check to see if an alphanumeric postfix with leading zeroes counts
    Matcher m = numericHasLeadingZeroes.matcher(postfix);
    while (m.find()) {
      postfix = m.replaceAll("");
    }
    // Hack to ensure correct parsing by SemanticVersion code. A postfix MUST start with a dash, digit, or letter
    while (!postfix.isEmpty() && !startsWithDigitLetterOrHyphen.matcher(postfix).find()) {
      postfix = postfix.substring(1);
    }
    stack.push(postfix);
  }

  /**
   * Simple semantic version
   */
  @Override
  public void exitPostfix_version(VersionParser.Postfix_versionContext ctx)
  {
    SemanticVersion version = null;

    int count = ctx.getChildCount();
    String postfix = (String) stack.pop();
    switch (count) {
      case 4: {
        //1.2.3alpha
        switch (postfix.toUpperCase()) {
          case "RELEASE":
          case "FINAL":
          case "GA":
            version = new SemanticVersion(
                ctx.getChild(0).getText() + "."
                    + ctx.getChild(2).getText());
            break;
          default:
            version = new SemanticVersion(
                ctx.getChild(0).getText() + "."
                    + ctx.getChild(2).getText() + "."
                    + "0" + "-"
                    + postfix);
            break;
        }
        break;
      }
      case 5:
        version = new SemanticVersion(
            ctx.getChild(0).getText() + "."
                + ctx.getChild(2).getText() + "."
                + "0" + "-"
                + postfix);
        break;
      case 6: {
        //1.2.3alpha
        switch (postfix.toUpperCase()) {
          case "RELEASE":
          case "FINAL":
          case "GA":
            version = new SemanticVersion(
                ctx.getChild(0).getText() + "."
                    + ctx.getChild(2).getText() + "."
                    + ctx.getChild(4).getText());
            break;
          default:
            version = new SemanticVersion(
                ctx.getChild(0).getText() + "."
                    + ctx.getChild(2).getText() + "."
                    + ctx.getChild(4).getText() + "-"
                    + postfix);
            break;
        }
        break;
      }
      case 7: {
        //1.2.3-alpha
        //1.2.3.alpha
        switch (postfix.toUpperCase()) {
          case "RELEASE":
          case "FINAL":
          case "GA":
            version = new SemanticVersion(
                ctx.getChild(0).getText() + "."
                    + ctx.getChild(2).getText() + "."
                    + ctx.getChild(4).getText());
            break;
          default:
            version = new SemanticVersion(
                ctx.getChild(0).getText() + "."
                    + ctx.getChild(2).getText() + "."
                    + ctx.getChild(4).getText() + "-"
                    + postfix);
            break;
        }
        break;
      }
      case 8:
      case 9: {
        // 0.2.4.23-1-deb7u1
        int major = Integer.parseInt(ctx.getChild(0).getText());
        int minor = Integer.parseInt(ctx.getChild(2).getText());
        int patch = Integer.parseInt(ctx.getChild(4).getText());
        int build = Integer.parseInt(ctx.getChild(6).getText());
        version = new ExtendedSemanticVersion(major, minor, patch, build, postfix);
        break;
      }
    }
    stack.push(version);
  }

  /**
   * Get a named version.
   */
  @Override
  public void exitNamed_version(VersionParser.Named_versionContext ctx)
  {
    IVersion version = new NamedVersion(ctx.getText());
    stack.push(version);
  }

  /**
   * Get whatever is on the stack and make a version range out of it
   *
   * @see net.ossindex.version.parser.VersionBaseListener#exitRange(net.ossindex.version.parser.VersionParser.RangeContext)
   */
  @Override
  public void exitRange(VersionParser.RangeContext ctx)
  {
    Object o = stack.pop();
    if (o instanceof IVersion) {
      //range = new SemanticVersionRange((SemanticVersion)o);
      range = new VersionSet((IVersion) o);
    }
    else if (o instanceof IVersionRange) {
      range = (IVersionRange) o;
    }

  }

  /**
   * A simple range.
   *
   * < 1.2.5
   *
   * @see net.ossindex.version.parser.VersionBaseListener#exitSimple_range(net.ossindex.version.parser.VersionParser.Simple_rangeContext)
   */
  @Override
  public void exitSimple_range(VersionParser.Simple_rangeContext ctx)
  {
    String operator = ctx.getChild(0).getText();
    Object o = stack.pop();
    if (o instanceof SemanticVersion) {
      //range = new SemanticVersionRange((SemanticVersion)o);
      switch (operator) {
        case "~>":
          // Special case for "pessimistic" range, see
          // https://www.devalot.com/articles/2012/04/gem-versions.html
          SemanticVersion sv = (SemanticVersion) o;
          VersionRange from = new VersionRange(">=", sv);
          VersionRange to = new VersionRange("<", sv.getNextParentVersion());
          range = new AndRange(from, to);
          break;
        default:
          range = new VersionRange(operator, (SemanticVersion) o);
          break;
      }
      stack.push(range);
    }
    else {
      throw new AssertionError("Expected a semantic version, got a " + o.getClass().getSimpleName());
    }
  }

  /**
   * Set of versions
   *
   * @see net.ossindex.version.parser.VersionBaseListener#exitVersion_set(net.ossindex.version.parser.VersionParser.Version_setContext)
   */
  @Override
  public void exitVersion_set(VersionParser.Version_setContext ctx)
  {
    Object o1 = stack.pop();
    if (stack.isEmpty()) {
      VersionSet set = new VersionSet((IVersion) o1);
      stack.push(set);
    }
    else {
      VersionSet set = (VersionSet) stack.peek();
      set.add((IVersion) o1);
    }
  }

  @Override
  public void exitLogical_range(VersionParser.Logical_rangeContext ctx)
  {
    // Two tokens is automatically an 'and'
    if (ctx.getChildCount() == 2) {
      Object o1 = stack.pop();
      Object o2 = stack.pop();
      stack.push(new AndRange((IVersionRange) o2, (IVersionRange) o1));
    }
    // Three tokens may be and OR or OR a bracketed version
    else if (ctx.getChildCount() == 3) {
      String first = ctx.getChild(0).getText();
      if ("(".equals(first)) {
        // bracketed token, do nothing
      }
      else {
        Object o1 = stack.pop();
        Object o2 = stack.pop();

        String operator = ctx.getChild(1).getText();

        switch (operator) {
          case "&":
          case ",":
            stack.push(new AndRange((IVersionRange) o2, (IVersionRange) o1));
            break;
        }
      }
    }
    // Everything else is a fall through
  }

  /*
   * (non-Javadoc)
   * @see net.ossindex.version.parser.VersionBaseListener#exitUnion_range(net.ossindex.version.parser.VersionParser.Union_rangeContext)
   */
  @Override
  public void exitUnion_range(VersionParser.Union_rangeContext ctx)
  {
    Object o1 = stack.pop();
    Object o2 = stack.pop();

    IVersionRange r1 = null;
    IVersionRange r2 = null;
    if (o1 instanceof IVersion) {
      r1 = new VersionSet((IVersion) o1);
    }
    else {
      r1 = (IVersionRange) o1;
    }
    if (o2 instanceof IVersion) {
      r2 = new VersionSet((IVersion) o2);
    }
    else {
      r2 = (IVersionRange) o2;
    }

    if (r1 instanceof OrRange) {
      stack.push(((OrRange) r1).add(r2));
    }
    else if (r2 instanceof OrRange) {
      stack.push(((OrRange) r2).add(r1));
    }
    else {
      stack.push(new OrRange(r2, r1));
    }
  }

  /*
   * (non-Javadoc)
   * @see net.ossindex.version.parser.VersionBaseListener#exitMaven_ranges(net.ossindex.version.parser.VersionParser.Maven_rangesContext)
   */
  @Override
  public void exitMaven_ranges(VersionParser.Maven_rangesContext ctx) {
    if (ctx.getChildCount() == 3) {
      IVersionRange r1 = (IVersionRange) stack.pop();
      IVersionRange r2 = (IVersionRange) stack.pop();
      stack.push(new OrRange(r2, r1));
    }
  }

  /*
   * (non-Javadoc)
   * @see net.ossindex.version.parser.VersionBaseListener#exitMaven_inclusive_range(net.ossindex.version.parser.VersionParser.Maven_inclusive_rangeContext)
   */
  @Override
  public void exitMaven_range(VersionParser.Maven_rangeContext ctx) {

    // This could be a version set
    if (ctx.getChildCount() == 3) {
      VersionSet set = new VersionSet();
      if (!"-".equals(ctx.getChild(1).getText())) {
        SemanticVersion v1 = (SemanticVersion) stack.pop();
        set.add(v1);
      }
      stack.push(set);
      return;
    }

    // Possibly an empty set
    if (ctx.getChildCount() == 2) {
      VersionSet set = new VersionSet();
      stack.push(set);
      return;
    }

    // Otherwise it is a range. Load the tokens first.
    int index = 0;
    String open = ctx.getChild(index).getText();
    index++;
    SemanticVersion v1 = null;
    SemanticVersion v2 = null;
    String text = ctx.getChild(index).getText();
    if (!",".equals(text)) {
      v1 = (SemanticVersion) stack.pop();
      index++;
    }
    index++;
    String close = null;
    text = ctx.getChild(index).getText();
    if ("]".equals(text) || ")".equals(text)) {
      close = text;
    }
    else {
      v2 = (SemanticVersion) stack.pop();
      index++;
      close = ctx.getChild(index).getText();
    }

    // We need to swap the values, cause they are peeled of the stack in reverse order
    if (v1 != null && v2 != null) {
      SemanticVersion tmp = v1;
      v1 = v2;
      v2 = tmp;
    }

    // Figure out what the range endpoints are
    IVersionRange r1 = null;
    if (v1 != null) {
      switch (open) {
        case "(":
          r1 = new VersionRange(">", v1);
          break;
        case "[":
          r1 = new VersionRange(">=", v1);
          break;
      }
    }

    IVersionRange r2 = null;
    if (v2 != null) {
      switch (close) {
        case ")":
          r2 = new VersionRange("<", v2);
          break;
        case "]":
          r2 = new VersionRange("<=", v2);
          break;
      }
    }

    // Now assemble the range
    if (r1 != null) {
      if (r2 != null) {
        IVersionRange andRange = new AndRange(r1, r2);
        andRange.setType("maven");
        stack.push(andRange);
      }
      else {
        stack.push(r1);
        r1.setType("maven");
      }
    }
    else {
      stack.push(r2);
      r2.setType("maven");
    }
  }

}
