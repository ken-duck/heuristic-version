package net.ossindex.version;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test range parsing
 *
 * @author Ken Duck
 */
public class MavenRangeTests
{

  @Test
  public void inclusiveVersionTest() throws InvalidRangeException
  {
    IVersionRange range = VersionFactory.getVersionFactory().getRange("4.3.2");
    assertNotNull(range);
    assertEquals("4.3.2", range.toString());
  }

  @Test
  public void inclusiveVersionSetTest() throws InvalidRangeException
  {
    IVersionRange range = VersionFactory.getVersionFactory().getRange("[4.3.2]");
    assertNotNull(range);
    assertEquals("4.3.2", range.toString());
  }

  @Test
  public void inclusiveRangeUpperBoundTest() throws InvalidRangeException
  {
    IVersionRange range = VersionFactory.getVersionFactory().getRange("[,4.3.2]");
    assertNotNull(range);
    assertEquals("<=4.3.2", range.toString());
  }

  @Test
  public void inclusiveRangeLowerBoundTest() throws InvalidRangeException
  {
    IVersionRange range = VersionFactory.getVersionFactory().getRange("[4.3.2,]");
    assertNotNull(range);
    assertEquals(">=4.3.2", range.toString());
  }

  @Test
  public void inclusiveRangeUpperBound2Test() throws InvalidRangeException
  {
    IVersionRange range = VersionFactory.getVersionFactory().getRange("(,4.3.2]");
    assertNotNull(range);
    assertEquals("<=4.3.2", range.toString());
  }

  @Test
  public void inclusiveRangeLowerBound2Test() throws InvalidRangeException
  {
    IVersionRange range = VersionFactory.getVersionFactory().getRange("[4.3.2,)");
    assertNotNull(range);
    assertEquals(">=4.3.2", range.toString());
  }

  @Test
  public void exclusiveRangeUpperBoundTest() throws InvalidRangeException
  {
    IVersionRange range = VersionFactory.getVersionFactory().getRange("(,4.3.2)");
    assertNotNull(range);
    assertEquals("<4.3.2", range.toString());
  }

  @Test
  public void exclusiveRangeLowerBoundTest() throws InvalidRangeException
  {
    IVersionRange range = VersionFactory.getVersionFactory().getRange("(4.3.2,)");
    assertNotNull(range);
    assertEquals(">4.3.2", range.toString());
  }

  @Test
  public void exclusiveRangeUpperBound2Test() throws InvalidRangeException
  {
    IVersionRange range = VersionFactory.getVersionFactory().getRange("[,4.3.2)");
    assertNotNull(range);
    assertEquals("<4.3.2", range.toString());
  }

  @Test
  public void exclusiveRangeLowerBound2Test() throws InvalidRangeException
  {
    IVersionRange range = VersionFactory.getVersionFactory().getRange("(4.3.2,]");
    assertNotNull(range);
    assertEquals(">4.3.2", range.toString());
  }

  @Test
  public void exclusiveRangeTest() throws InvalidRangeException
  {
    IVersionRange range = VersionFactory.getVersionFactory().getRange("(1.0,2.0)");
    assertNotNull(range);
    assertEquals(">1.0.0 <2.0.0", range.toString());
  }

  @Test
  public void inclusiveRangeTest() throws InvalidRangeException
  {
    IVersionRange range = VersionFactory.getVersionFactory().getRange("[1.0,2.0]");
    assertNotNull(range);
    assertEquals(">=1.0.0 <=2.0.0", range.toString());
  }

  @Test
  public void inclusiveExclusiveRangeTest() throws InvalidRangeException
  {
    IVersionRange range = VersionFactory.getVersionFactory().getRange("[1.0,2.0)");
    assertNotNull(range);
    assertEquals(">=1.0.0 <2.0.0", range.toString());
  }

  @Test
  public void exclusiveInclusiveRangeTest() throws InvalidRangeException
  {
    IVersionRange range = VersionFactory.getVersionFactory().getRange("(1.0,2.0]");
    assertNotNull(range);
    assertEquals(">1.0.0 <=2.0.0", range.toString());
  }

  @Test
  public void mavenRangeUnion1Test() throws InvalidRangeException
  {
    IVersionRange range = VersionFactory.getVersionFactory().getRange("(1.0,2.0],[3.0,4.0)");
    assertNotNull(range);
    assertEquals(">1.0.0 <=2.0.0 | >=3.0.0 <4.0.0", range.toString());
  }

  @Test
  public void mavenPostfixRangeUnionTest() throws InvalidRangeException
  {
    IVersionRange range = VersionFactory.getVersionFactory().getRange("[3.2,3.2.8.RELEASE], [4.0,4.0.4.RELEASE]");
    assertNotNull(range);
    assertEquals(">=3.2.0 <=3.2.8 | >=4.0.0 <=4.0.4", range.toString());
  }

  @Test
  public void mavenRcTest() throws InvalidRangeException
  {
    IVersionRange range = VersionFactory.getVersionFactory().getRange("[2.4-beta,2.4.0-rc1]");
    assertNotNull(range);
    assertEquals(">=2.4.0-beta <=2.4.0-rc1", range.toString());
    range = VersionFactory.getVersionFactory().getRange("[2.4beta,2.4.0rc1]");
    assertNotNull(range);
    assertEquals(">=2.4.0-beta <=2.4.0-rc1", range.toString());
  }

  @Test
  public void emptyRangeTest() throws InvalidRangeException
  {
    IVersionRange range = VersionFactory.getVersionFactory().getRange("[]");
    assertNotNull(range);
    assertEquals("", range.toString());
    range = VersionFactory.getVersionFactory().getRange("[-]");
    assertNotNull(range);
    assertEquals("", range.toString());
  }
}
