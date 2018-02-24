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
package net.ossindex.version;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import net.ossindex.version.impl.VersionListener;
import net.ossindex.version.parser.VersionLexer;
import net.ossindex.version.parser.VersionParser;
import net.ossindex.version.parser.VersionParser.RangeContext;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/** Test the version parser
 *
 * @author Ken Duck
 *
 */
public class VersionTests
{
  @Before
  public void before()
  {

  }

  @After
  public void after()
  {

  }

  /** Test simple semantic ranges
   *
   * @throws IOException
   */
  @Test
  public void testSimpleSemanticVersion() throws IOException
  {
    IVersionRange range = parseVersion("1.2.3");
    assertNotNull(range);
    assertEquals("1.2.3", range.toString());
  }

  @Test
  public void testSimpleSemanticVersionAlpha() throws IOException
  {
    IVersionRange range = parseVersion("1.2.3alpha");
    assertNotNull(range);
    assertEquals("1.2.3-alpha", range.toString());
  }

  @Test
  public void testSimpleSemanticVersionDashAlpha() throws IOException
  {
    IVersionRange range = parseVersion("1.2.3-alpha");
    assertNotNull(range);
    assertEquals("1.2.3-alpha", range.toString());
  }

  @Test
  public void testSimpleSemanticVersionDashRcPlus() throws IOException
  {
    IVersionRange range = parseVersion("1.2.3-rc+build");
    assertNotNull(range);
    assertEquals("1.2.3-rc+build", range.toString());
  }

  @Test
  public void testSimpleSemanticVersionDashRcPlusDot() throws IOException
  {
    IVersionRange range = parseVersion("1.2.3-rc+build.1");
    assertNotNull(range);
    assertEquals("1.2.3-rc+build.1", range.toString());
  }

  @Test
  public void testMajor() throws IOException
  {
    IVersionRange range = parseVersion("1");
    assertNotNull(range);
    assertEquals("1.0.0", range.toString());
  }

  @Test
  public void testMajorMinor() throws IOException
  {
    IVersionRange range = parseVersion("1.2");
    assertNotNull(range);
    assertEquals("1.2.0", range.toString());
  }

  @Test
  public void testMajorMinorPatch() throws IOException
  {
    IVersionRange range = parseVersion("1.2.3");
    assertNotNull(range);
    assertEquals("1.2.3", range.toString());
  }

  @Test
  public void testMajorMinorPatchBuild() throws IOException
  {
    IVersionRange range = parseVersion("1.2.3.4");
    assertNotNull(range);
    assertEquals("1.2.3.4", range.toString());
  }

  @Test
  public void testSimplePrefix() throws IOException
  {
    IVersionRange range = parseVersion("demo-1.2.3");
    assertNotNull(range);
    assertEquals("1.2.3", range.toString());
  }

  @Test
  public void testNumberPrefix() throws IOException
  {
    IVersionRange range = parseVersion("5demo-1.2.3");
    assertNotNull(range);
    assertEquals("1.2.3", range.toString());
  }

  /**
   * Both this and the above cannot be true.
   */
  @Test
  public void testPrefixPostfix() throws IOException
  {
    IVersionRange range = parseVersion("5demo-1.2.3alpha");
    assertNotNull(range);
    assertEquals("1.2.3-alpha", range.toString());
  }

  /**
   * Both this and the above cannot be true.
   */
  @Test
  public void testEndsWithDot1() throws IOException
  {
    IVersionRange range = parseVersion("5.");
    assertNotNull(range);
    assertEquals("5.0.0", range.toString());
  }

  /**
   * Both this and the above cannot be true.
   */
  @Test
  public void testEndsWithDot2() throws IOException
  {
    IVersionRange range = parseVersion("5.5.");
    assertNotNull(range);
    assertEquals("5.5.0", range.toString());
  }

  /**
   * Both this and the above cannot be true.
   */
  @Test
  public void testEndsWithDot3() throws IOException
  {
    IVersionRange range = parseVersion("5.5.5.");
    assertNotNull(range);
    assertEquals("5.5.5", range.toString());
  }

  /**
   * Both this and the below cannot be true.
   */
  @Test
  public void testEndsWithDot4() throws IOException
  {
    IVersionRange range = parseVersion("5.5.5.5.");
    assertNotNull(range);
    assertEquals("5.5.5.5", range.toString());
  }

  /**
   * Both this and the above cannot be true.
   */
  @Test
  @Ignore
  public void testPrefixPostfix2() throws IOException
  {
    IVersionRange range = parseVersion("2.2.3alpha1.1");
    assertNotNull(range);
    assertEquals("2.2.3alpha1.1", range.toString());
  }

  @Test
  public void testNamedVersion() throws IOException
  {
    IVersionRange range = parseVersion("named");
    assertNotNull(range);
    assertEquals("named", range.toString());
  }

  @Test
  public void testInvalidVersion() throws IOException
  {
    IVersionRange range = parseVersion("1.2.3 &");
    assertNotNull(range);
    assertEquals("1.2.3", range.toString());
  }

  @Test
  public void testMajorMinorBuildRevisionVersion() throws IOException
  {
    IVersionRange range = parseVersion("1.0.2-v20150114");
    assertNotNull(range);
    assertEquals("1.0.2-v20150114", range.toString());
  }

  @Test
  public void testFinalVersion() throws IOException
  {
    IVersionRange range = parseVersion("4.3.2.Final");
    assertNotNull(range);
    assertEquals("4.3.2", range.toString());
  }

  @Test
  public void testEqualVersion() throws IOException
  {
    IVersionRange range = parseVersion("= 4.3.2.Final");
    assertNotNull(range);
    assertEquals("4.3.2", range.toString());
  }

  /**
   * Due to limitations in some underlying libraries, underscores are replaced with hyphens.>=1.10.1 <1.10.10
   */
  @Test
  public void testUnderscoreVersion() throws IOException
  {
    IVersionRange range = parseVersion("= 4.3.2.Final_Score");
    assertNotNull(range);
    assertEquals("4.3.2-Final-Score", range.toString());
  }

  @Test
  public void testPlusVersion() throws IOException {
    IVersionRange range = parseVersion("10.0.6+7-e2ba6752");
    assertNotNull(range);
    assertEquals("10.0.6-7-e2ba6752", range.toString());
  }

  @Test
  public void testTimestampSuffixVersion() throws IOException {
    IVersionRange range = parseVersion("1.7");
    assertNotNull(range);
    assertEquals("1.7.0", range.toString());

    range = parseVersion("1.7-a");
    assertNotNull(range);
    assertEquals("1.7.0-a", range.toString());

    range = parseVersion("1.7-201002241055");
    assertNotNull(range);
    assertEquals("1.7.0-201002241055", range.toString());

    range = parseVersion("4.0.2-3eab439-20180109215419");
    assertNotNull(range);
    assertEquals("4.0.2-3eab439-20180109215419", range.toString());
  }

  @Test
  public void testLongVersion() throws IOException {
    IVersionRange range = parseVersion("0.0.111111111111");
    assertNotNull(range);
    assertEquals("0.0.0-111111111111", range.toString());
  }

  @Test
  public void testDebVersion() throws IOException {
    IVersionRange range = parseVersion("0.2.4.23-1~deb7u1");
    assertNotNull(range);
    assertEquals("0.2.4.23-1~deb7u1", range.toString());
  }

  @Test
  public void testColonVersion() throws IOException {
    IVersionRange range = parseVersion("<1:1.0.5-1");
    assertNotNull(range);
    assertEquals("<1.0.5-1", range.toString());

    range = parseVersion("<2:12.0.0-1");
    assertNotNull(range);
    assertEquals("<12.0.0-1", range.toString());

    range = parseVersion("<1:1.8.7+dfsg-1+deb8u1");
    assertNotNull(range);
    assertEquals("<1.8.7+dfsg-1+deb8u1", range.toString());

    range = parseVersion("1:4.6.0-4.6.1-pre1-2");
    assertNotNull(range);
    assertEquals("4.6.0-4.6.1-pre1-2", range.toString());
  }

  @Test
  public void testLeadingZeros() throws IOException {
    IVersionRange range = parseVersion("01.0.0");
    assertNotNull(range);
    assertEquals("1.0.0", range.toString());

    range = parseVersion("01.0.0.5");
    assertNotNull(range);
    assertEquals("1.0.0.5", range.toString());

    range = parseVersion("01");
    assertNotNull(range);
    assertEquals("1.0.0", range.toString());

    range = parseVersion("1.02.3");
    assertNotNull(range);
    assertEquals("1.2.3", range.toString());

    range = parseVersion("1.02");
    assertNotNull(range);
    assertEquals("1.2.0", range.toString());

    range = parseVersion("1.02.3.4");
    assertNotNull(range);
    assertEquals("1.2.3.4", range.toString());

    range = parseVersion("1.2.03");
    assertNotNull(range);
    assertEquals("1.2.3", range.toString());

    range = parseVersion("1.2.03.4");
    assertNotNull(range);
    assertEquals("1.2.3.4", range.toString());

    range = parseVersion("1.2.3.04");
    assertNotNull(range);
    assertEquals("1.2.3.4", range.toString());

    range = parseVersion("1.2.3-04");
    assertNotNull(range);
    assertEquals("1.2.3-4", range.toString());
  }



  /** Common test code
   *
   * @param vstring
   * @return
   * @throws IOException
   */
  private IVersionRange parseVersion(String vstring) throws IOException
  {
    InputStream stream = new ByteArrayInputStream(vstring.getBytes(StandardCharsets.UTF_8));
    ANTLRInputStream input = new ANTLRInputStream(stream);
    VersionLexer lexer = new VersionLexer(input);

    CommonTokenStream tokens = new CommonTokenStream(lexer);

    VersionParser parser = new VersionParser(tokens);

    RangeContext context = parser.range();

    ParseTreeWalker walker = new ParseTreeWalker();
    VersionListener listener = new VersionListener();
    walker.walk(listener, context);

    IVersionRange range = listener.getRange();
    return range;
  }
}
