package net.ossindex.version;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import junitparams.JUnitParamsRunner;
import net.ossindex.version.impl.SemanticVersion;
import net.ossindex.version.impl.VersionListener;
import net.ossindex.version.parser.VersionLexer;
import net.ossindex.version.parser.VersionParser;
import net.ossindex.version.parser.VersionParser.RangeContext;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(JUnitParamsRunner.class)
public class IdentifierPatternTests
{
  @Test
  public void testPostscriptComparison() throws IOException
  {
    assertPostscriptMatch("1.2.3-core", "1.2.4-core");
    assertPostscriptMatch("1.2.3-lts1", "1.2.4-lts2");
    assertPostscriptMatch("1.2.3-2010-09-14", "1.2.4-2011-03-30");
    // FIXME: Following test fails, but likely should not.
    //   This can be fixed in one of two ways:
    //     * Only verify milestone match on known milestone name
    //     * Any milestone can upgrade to ANY milestone
    //     * Do version analysis to identify tag patterns (most complex)
    // assertPostscriptMatch("1.2.3-snuggle-bear", "1.2.4-bitsy-pookums");

    assertPostscriptMismatch("1.2.3-lts1", "1.2.4-beta2");
    assertPostscriptMismatch("1.2.3-2010-09-14", "1.2.4-2011-03-30.M1");
  }

  private void assertPostscriptMatch(String s1, String s2) throws IOException {
    SemanticVersion v1 = parseSemanticVersion(s1);
    SemanticVersion v2 = parseSemanticVersion(s2);
    assertTrue(v1.hasPostfixMatch(v2));
  }

  private void assertPostscriptMismatch(String s1, String s2) throws IOException {
    SemanticVersion v1 = parseSemanticVersion(s1);
    SemanticVersion v2 = parseSemanticVersion(s2);
    assertFalse(v1.hasPostfixMatch(v2));
  }

  //@Test
  //public void testPrereleaseVersions() throws IOException
  //{
  //  assertTrue(parseSemanticVersion("1.2.3-beta1").isPrerelease());
  //  assertTrue(parseSemanticVersion("1.2.3alpha").isPrerelease());
  //}
  //
  //@Test
  //public void testReleaseVersions() throws IOException
  //{
  //  assertTrue(parseSemanticVersion("1.2.3-release").isStable());
  //}

  private SemanticVersion parseSemanticVersion(String vstring) throws IOException {
    IVersionRange range = parseVersion(vstring);
    IVersion version = range.getMinimum();
    assertTrue(version instanceof SemanticVersion);
    SemanticVersion v = (SemanticVersion)version;
    return v;
  }

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
