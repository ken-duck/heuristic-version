package net.ossindex.version;

import static org.junit.Assert.*;

import org.junit.Test;

import net.ossindex.version.impl.VersionSet;

/** Test range parsing
 * 
 * @author Ken Duck
 *
 */
public class MavenRangeTests
{

	@Test
	public void inclusiveVersionTest()
	{
		IVersionRange range = VersionFactory.getRange("4.3.2");
		assertNotNull(range);
		assertEquals("4.3.2", range.toString());
	}
	
	@Test
	public void inclusiveVersionSetTest()
	{
		IVersionRange range = VersionFactory.getRange("[4.3.2]");
		assertNotNull(range);
		assertEquals("4.3.2", range.toString());
	}
	
	@Test
	public void inclusiveRangeUpperBoundTest()
	{
		IVersionRange range = VersionFactory.getRange("[,4.3.2]");
		assertNotNull(range);
		assertEquals("<=4.3.2", range.toString());
	}
	
	@Test
	public void inclusiveRangeLowerBoundTest()
	{
		IVersionRange range = VersionFactory.getRange("[4.3.2,]");
		assertNotNull(range);
		assertEquals(">=4.3.2", range.toString());
	}
	
	@Test
	public void inclusiveRangeUpperBound2Test()
	{
		IVersionRange range = VersionFactory.getRange("(,4.3.2]");
		assertNotNull(range);
		assertEquals("<=4.3.2", range.toString());
	}
	
	@Test
	public void inclusiveRangeLowerBound2Test()
	{
		IVersionRange range = VersionFactory.getRange("[4.3.2,)");
		assertNotNull(range);
		assertEquals(">=4.3.2", range.toString());
	}
	
	@Test
	public void exclusiveRangeUpperBoundTest()
	{
		IVersionRange range = VersionFactory.getRange("(,4.3.2)");
		assertNotNull(range);
		assertEquals("<4.3.2", range.toString());
	}
	
	@Test
	public void exclusiveRangeLowerBoundTest()
	{
		IVersionRange range = VersionFactory.getRange("(4.3.2,)");
		assertNotNull(range);
		assertEquals(">4.3.2", range.toString());
	}
	
	@Test
	public void exclusiveRangeUpperBound2Test()
	{
		IVersionRange range = VersionFactory.getRange("[,4.3.2)");
		assertNotNull(range);
		assertEquals("<4.3.2", range.toString());
	}
	
	@Test
	public void exclusiveRangeLowerBound2Test()
	{
		IVersionRange range = VersionFactory.getRange("(4.3.2,]");
		assertNotNull(range);
		assertEquals(">4.3.2", range.toString());
	}
	
	@Test
	public void exclusiveRangeTest()
	{
		IVersionRange range = VersionFactory.getRange("(1.0,2.0)");
		assertNotNull(range);
		assertEquals(">1.0.0 & <2.0.0", range.toString());
	}
	
	@Test
	public void inclusiveRangeTest()
	{
		IVersionRange range = VersionFactory.getRange("[1.0,2.0]");
		assertNotNull(range);
		assertEquals(">=1.0.0 & <=2.0.0", range.toString());
	}
	
	@Test
	public void inclusiveExclusiveRangeTest()
	{
		IVersionRange range = VersionFactory.getRange("[1.0,2.0)");
		assertNotNull(range);
		assertEquals(">=1.0.0 & <2.0.0", range.toString());
	}
	
	@Test
	public void exclusiveInclusiveRangeTest()
	{
		IVersionRange range = VersionFactory.getRange("(1.0,2.0]");
		assertNotNull(range);
		assertEquals(">1.0.0 & <=2.0.0", range.toString());
	}
	
	@Test
	public void mavenRangeUnion1Test()
	{
		IVersionRange range = VersionFactory.getRange("(1.0,2.0],[3.0,4.0)");
		assertNotNull(range);
		assertEquals("(>1.0.0 & <=2.0.0) | (>=3.0.0 & <4.0.0)", range.toString());
	}
	
	@Test
	public void mavenPostfixRangeUnionTest()
	{
		IVersionRange range = VersionFactory.getRange("[3.2,3.2.8.RELEASE], [4.0,4.0.4.RELEASE]");
		assertNotNull(range);
		assertEquals("(>=3.2.0 & <=3.2.8) | (>=4.0.0 & <=4.0.4)", range.toString());
	}
	
	@Test
	public void mavenRcTest()
	{
		IVersionRange range = VersionFactory.getRange("[2.4-beta,2.4.0-rc1]");
		assertNotNull(range);
		assertEquals(">=2.4.0-beta & <=2.4.0-rc1", range.toString());
		range = VersionFactory.getRange("[2.4beta,2.4.0rc1]");
		assertNotNull(range);
		assertEquals(">=2.4.0-beta & <=2.4.0-rc1", range.toString());
	}
}
