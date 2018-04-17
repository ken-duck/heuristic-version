package net.ossindex.version;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Test range intersections
 *
 * @author Ken Duck
 */
public class RangeCompareTests
{
  @Test
  public void versionCompareTests() throws InvalidRangeException
  {
    IVersionRange range1 = VersionFactory.getVersionFactory().getRange("1.2.5");
    IVersionRange range2 = VersionFactory.getVersionFactory().getRange("1.2.6");
    assertTrue(range1.compareTo(range2) < 0);

    range2 = VersionFactory.getVersionFactory().getRange("1.2.5");
    assertTrue(range1.compareTo(range2) == 0);

    range2 = VersionFactory.getVersionFactory().getRange("1.2.4");
    assertTrue(range1.compareTo(range2) > 0);
  }

  @Test
  public void simpleRangeCompareTests() throws InvalidRangeException
  {
    IVersionRange range1 = VersionFactory.getVersionFactory().getRange(">1.2.5");
    IVersionRange range2 = VersionFactory.getVersionFactory().getRange(">1.2.6");
    assertTrue(range1.compareTo(range2) < 0);

    range2 = VersionFactory.getVersionFactory().getRange(">1.2.5");
    assertTrue(range1.compareTo(range2) == 0);

    range2 = VersionFactory.getVersionFactory().getRange(">1.2.4");
    assertTrue(range1.compareTo(range2) > 0);
  }

  @Ignore
  @Test
  public void boundedRangeCompareTests() throws InvalidRangeException
  {
    IVersionRange range1 = VersionFactory.getVersionFactory().getRange(">1.2.5 <1.2.7");
    IVersionRange range2 = VersionFactory.getVersionFactory().getRange(">1.2.6 <1.2.7");
    assertTrue(range1.compareTo(range2) < 0);

    range2 = VersionFactory.getVersionFactory().getRange(">1.2.5 <1.2.7");
    assertTrue(range1.compareTo(range2) == 0);

    range2 = VersionFactory.getVersionFactory().getRange(">1.2.4 <1.2.7");
    assertTrue(range1.compareTo(range2) > 0);

    // Bottom of range is same, but top is different
    range2 = VersionFactory.getVersionFactory().getRange(">1.2.5 <1.2.6");
    assertTrue(range1.compareTo(range2) > 0);
  }

  @Test
  public void unionRangeCompareTests() throws InvalidRangeException
  {
    IVersionRange range1 = VersionFactory.getVersionFactory().getRange(">1.2.5 <1.2.7 | >2.2.5 <2.2.7");
    IVersionRange range2 = VersionFactory.getVersionFactory().getRange(">1.2.6 <1.2.7 | >2.2.5 <2.2.7");
    assertTrue(range1.compareTo(range2) < 0);

    range2 = VersionFactory.getVersionFactory().getRange(">1.2.5 <1.2.7 | >2.2.5 <2.2.7");
    assertTrue(range1.compareTo(range2) == 0);

    range2 = VersionFactory.getVersionFactory().getRange(">1.2.4 <1.2.7 | >2.2.5 <2.2.7");
    assertTrue(range1.compareTo(range2) > 0);
  }
}
