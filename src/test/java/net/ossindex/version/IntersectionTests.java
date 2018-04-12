package net.ossindex.version;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Test range intersections
 *
 * @author Ken Duck
 */
public class IntersectionTests
{

  /**
   *
   */
  @Test
  public void testSetIntersection() throws InvalidRangeException
  {
    IVersionRange range1 = VersionFactory.getVersionFactory().getRange("1.2.5,1.2.6,1.2.8");
    IVersionRange range2 = VersionFactory.getVersionFactory().getRange("1.2.6,1.2.9");
    assertTrue(range1.intersects(range2));
  }

  @Test
  public void testSetOverlappingRange() throws InvalidRangeException
  {
    IVersionRange range1 = VersionFactory.getVersionFactory().getRange("<1.5");
    IVersionRange range2 = VersionFactory.getVersionFactory().getRange("1.2.6,2.2.9");
    assertTrue(range1.intersects(range2));
  }

  @Test
  public void testRangeOverlappingSet() throws InvalidRangeException
  {
    IVersionRange range1 = VersionFactory.getVersionFactory().getRange("1.2.2,2.2.9");
    IVersionRange range2 = VersionFactory.getVersionFactory().getRange(">1.2");
    assertTrue(range1.intersects(range2));
  }


  @Test
  public void testOverlappingSimpleRanges() throws InvalidRangeException
  {
    IVersionRange range1 = VersionFactory.getVersionFactory().getRange("<1.5");
    IVersionRange range2 = VersionFactory.getVersionFactory().getRange(">1.2");
    assertTrue(range1.intersects(range2));
  }

  @Test
  public void testOverlappingComplexRanges() throws InvalidRangeException
  {
    IVersionRange range1 = VersionFactory.getVersionFactory().getRange("(>0.2 & <0.5) | (>1.2 & <1.5)");
    IVersionRange range2 = VersionFactory.getVersionFactory().getRange("(>1.4 & <1.9) | (>2.4 & <2.9)");
    assertTrue(range1.intersects(range2));
  }

  @Test
  public void testOverlappingSimpleRanges2() throws InvalidRangeException
  {
    IVersionRange range1 = VersionFactory.getVersionFactory().getRange(">=4.2.5");
    IVersionRange range2 = VersionFactory.getVersionFactory().getRange(">=4.2.5.1");
    IVersion version2 = VersionFactory.getVersionFactory().getVersion(">=4.2.5.1");
    assertTrue(range1.contains(version2));
    assertTrue(range1.intersects(range2));
  }

}
