package net.ossindex.version;

import java.io.IOException;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import net.ossindex.version.impl.NamedVersion;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Ken Duck
 */
@RunWith(JUnitParamsRunner.class)
public class VersionRangeTest
{
  @Test
  public void testComplexRange() throws InvalidRangeException
  {
    IVersionRange range = VersionFactory.getVersionFactory()
        .getRange(">=2.5.0 <=2.5.6 || 2.5.6.SEC01 || 2.5.6.SEC02 || 2.5.7 || >=3.0.0 <3.0.3");
    assertNotNull(range);
    assertEquals(">=2.5.0 <=2.5.6 | 2.5.6-SEC01 | 2.5.6-SEC02 | 2.5.7 | >=3.0.0 <3.0.3", range.toString());
  }

  @Test
  public void testUnarySetRangeWithTextSuffix() throws InvalidRangeException
  {
    IVersionRange range = VersionFactory.getVersionFactory().getRange("2.5.6.SEC01");
    assertNotNull(range);
    assertEquals("2.5.6-SEC01", range.toString());
  }

  @Test
  public void testBinarySimpleRanges() throws InvalidRangeException
  {
    IVersionRange range = VersionFactory.getVersionFactory().getRange(">2.5.6 || >2.5.7");
    assertNotNull(range);
    assertEquals(">2.5.6 | >2.5.7", range.toString());
  }

  @Test
  public void testBinarySetRange() throws InvalidRangeException
  {
    IVersionRange range = VersionFactory.getVersionFactory().getRange("2.5.6 || 2.5.7");
    assertNotNull(range);
    assertEquals("2.5.6 | 2.5.7", range.toString());
  }

  @Test
  public void testBinarySetRangeWithTextSuffix() throws InvalidRangeException
  {
    IVersionRange range = VersionFactory.getVersionFactory().getRange("2.5.6.SEC01 || 2.5.6.SEC02");
    assertNotNull(range);
    assertEquals("2.5.6-SEC01 | 2.5.6-SEC02", range.toString());
  }

  @Test
  public void testWhitespaceAndRange() throws InvalidRangeException
  {
    IVersionRange range = VersionFactory.getVersionFactory().getRange("<1.4.1 >=0.4.3");
    assertNotNull(range);
    assertEquals(">=0.4.3 <1.4.1", range.toString());
  }

  @Test
  public void testCommaAndRange() throws InvalidRangeException
  {
    IVersionRange range = VersionFactory.getVersionFactory().getRange(">=0.10.0, <0.10.2");
    assertNotNull(range);
    assertEquals(">=0.10.0 <0.10.2", range.toString());
  }

  /**
   * FIXME: This test should be enabled and fixed
   */
  @Test
  @Ignore
  public void testTildeRange() throws InvalidRangeException
  {
    IVersionRange range = VersionFactory.getVersionFactory().getRange("~1.2.3");
    assertNotNull(range);
    assertEquals(">=1.2.3 <1.3.0", range.toString());

    range = VersionFactory.getVersionFactory().getRange("~1.2");
    assertNotNull(range);
    assertEquals(">=1.2.0 <1.3.0", range.toString());

    range = VersionFactory.getVersionFactory().getRange("~1");
    assertNotNull(range);
    assertEquals(">=1.0.0 <2.0.0", range.toString());

    range = VersionFactory.getVersionFactory().getRange("~0.2.3");
    assertNotNull(range);
    assertEquals(">=0.2.3 <0.3.0", range.toString());

    range = VersionFactory.getVersionFactory().getRange("~0.2");
    assertNotNull(range);
    assertEquals(">=0.2.0 <0.3.0", range.toString());

    range = VersionFactory.getVersionFactory().getRange("~0");
    assertNotNull(range);
    assertEquals(">=0.0.0 <1.0.0", range.toString());

    range = VersionFactory.getVersionFactory().getRange("~1.2.3-beta.2");
    assertNotNull(range);
    assertEquals(">=~1.2.3-beta.2 <1.3.0", range.toString());
  }

  /**
   * FIXME: This test should be enabled and fixed
   */
  @Test
  @Ignore
  public void testCaretRange() throws InvalidRangeException
  {
    IVersionRange range = VersionFactory.getVersionFactory().getRange("^1.2.3");
    assertNotNull(range);
    assertEquals(">=1.2.3 <2.0.0", range.toString());

    range = VersionFactory.getVersionFactory().getRange("^0.2.3");
    assertNotNull(range);
    assertEquals(">=0.2.3 <0.3.0", range.toString());

    range = VersionFactory.getVersionFactory().getRange("^0.0.3");
    assertNotNull(range);
    assertEquals(">=0.0.3 <0.0.4", range.toString());

    range = VersionFactory.getVersionFactory().getRange("^1.2.3-beta.2");
    assertNotNull(range);
    assertEquals(">=1.2.3-beta.2 <2.0.0", range.toString());

    range = VersionFactory.getVersionFactory().getRange("^0.0.3-beta");
    assertNotNull(range);
    assertEquals(">=0.0.3-beta <0.0.4", range.toString());
  }

  /**
   * FIXME: This test should be enabled and fixed
   */
  @Test
  @Ignore
  public void testXRange() throws InvalidRangeException
  {
    IVersionRange range = VersionFactory.getVersionFactory().getRange("*");
    assertNotNull(range);
    assertEquals(">=0.0.0", range.toString());

    range = VersionFactory.getVersionFactory().getRange("1.x");
    assertNotNull(range);
    assertEquals(">=1.0.0 <2.0.0", range.toString());

    range = VersionFactory.getVersionFactory().getRange("1.2.x");
    assertNotNull(range);
    assertEquals(">=1.2.0 <1.3.0", range.toString());
  }

  @Test
  public void testImplicitAnd() throws InvalidRangeException {
    IVersionRange range = VersionFactory.getVersionFactory().getRange(">=2.0.0 <=2.5.3-SP13");
    assertNotNull(range);
    assertEquals("[2.0.0,2.5.3-SP13]", range.toMavenString());
  }

  @Test
  public void testUnionRanges() throws InvalidRangeException {
    IVersionRange range = VersionFactory.getVersionFactory().getRange("(>=2.0.0 <=2.5.3-SP13) | (>=2.6.0 <=2.6.1)");
    assertNotNull(range);
    assertEquals("[2.0.0,2.5.3-SP13],[2.6.0,2.6.1]", range.toMavenString());
  }

  @Test
  public void testTripleUnionRanges() throws InvalidRangeException {
    IVersionRange range = VersionFactory.getVersionFactory()
        .getRange("(>=2.0.0 <=2.5.3-SP13) | (>=2.6.0 <=2.6.1) | (>=2.7.0 <=2.7.1-Beta2)");
    assertNotNull(range);
    assertEquals("[2.0.0,2.5.3-SP13],[2.6.0,2.6.1],[2.7.0,2.7.1-Beta2]", range.toMavenString());
  }

  @Test
  public void testNestedUnionRanges() throws InvalidRangeException {
    IVersionRange range = VersionFactory.getVersionFactory()
        .getRange("(>=2.0.0 <=2.5.3-SP13) | ((>=2.6.0 <=2.6.1) | (>=2.7.0 <=2.7.1-Beta2))");
    assertNotNull(range);
    assertEquals("[2.0.0,2.5.3-SP13],[2.6.0,2.6.1],[2.7.0,2.7.1-Beta2]", range.toMavenString());
  }

  @Test
  @Parameters({
      ">=1.2.3 <1.1.1", // Ranges do not intersect
      "[named]",
      "(named]",
      "named&version",
      "named=version",
      "named>version",
      "named<version",
      "1.2.3[zounds]",
      "named version",
      "named[wow]",
      "named(zounds)",
      "named version"
  })
  public void testStrictInvalidVersions(final String name) throws IOException
  {
    try {
      IVersionRange version = VersionFactory.getStrictVersionFactory().getRange(name);
      assertFalse("Strict mode expects an exception, got " + version, true);
    }
    catch (InvalidRangeException e) {
    }
  }

  @Test
  public void testStrictCrazyInvalidVersion() throws IOException
  {
    try {
      IVersionRange version = VersionFactory.getStrictVersionFactory().getRange(">=3.0.0 <=3.9.1 =3.10.2");
      assertFalse("Strict mode expects an exception, got " + version, true);
    }
    catch (InvalidRangeException e) {
    }
  }

  @Test
  @Parameters({
      "namedVersion",
      "named-version",
      "named.version",
      "named+version",
  })
  public void testStrictValidNamedVersions(final String name) throws IOException
  {
    try {
      IVersion range = VersionFactory.getStrictVersionFactory().getVersion(name);
      assertTrue(range instanceof NamedVersion);
    }
    catch (InvalidRangeException e) {
      assertFalse("Strict mode does not expect an exception", true);
    }
  }
}
