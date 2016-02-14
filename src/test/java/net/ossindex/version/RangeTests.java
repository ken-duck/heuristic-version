package net.ossindex.version;

import static org.junit.Assert.*;

import org.junit.Test;

import net.ossindex.version.impl.VersionSet;

/** Test range parsing
 * 
 * @author Ken Duck
 *
 */
public class RangeTests
{

	@Test
	public void testLtRange()
	{
		IVersionRange range = VersionFactory.getRange("<1.2.5");
		assertNotNull(range);
		assertEquals("<1.2.5", range.toString());
		assertTrue(range.contains(VersionFactory.getVersion("1.2.4")));
	}
	@Test
	public void testLeRange()
	{
		IVersionRange range = VersionFactory.getRange("<=1.2.5");
		assertNotNull(range);
		assertEquals("<=1.2.5", range.toString());
		assertTrue(range.contains(VersionFactory.getVersion("1.2.5")));
	}
	@Test
	public void testGtRange()
	{
		IVersionRange range = VersionFactory.getRange(">1.2.5");
		assertNotNull(range);
		assertEquals(">1.2.5", range.toString());
		assertTrue(range.contains(VersionFactory.getVersion("2.2.6")));
	}
	@Test
	public void testGeRange()
	{
		IVersionRange range = VersionFactory.getRange(">=1.2.5");
		assertNotNull(range);
		assertEquals(">=1.2.5", range.toString());
		assertTrue(range.contains(VersionFactory.getVersion("1.2.5")));
	}
	
	@Test
	public void testExactVersion()
	{
		IVersionRange range = VersionFactory.getRange("1.2.5");
		assertNotNull(range);
		assertTrue(range instanceof VersionSet);
		assertEquals("1.2.5", range.toString());
		assertTrue(range.contains(VersionFactory.getVersion("1.2.5")));
	}
	@Test
	public void testVersionSet()
	{
		IVersionRange range = VersionFactory.getRange("1.2.5,1.2.6,1.2.8");
		assertNotNull(range);
		assertTrue(range instanceof VersionSet);
		assertEquals("1.2.5,1.2.6,1.2.8", range.toString());
		assertTrue(range.contains(VersionFactory.getVersion("1.2.6")));
	}
	
	@Test
	public void testAndVersion()
	{
		IVersionRange range = VersionFactory.getRange(">1.2.5 & <1.3");
		assertNotNull(range);
		assertEquals(">1.2.5 & <1.3.0", range.toString());
		assertTrue(range.contains(VersionFactory.getVersion("1.2.6")));
	}

	@Test
	public void testOrVersion()
	{
		IVersionRange range = VersionFactory.getRange("<1.2.5 | >1.3");
		assertNotNull(range);
		assertEquals("<1.2.5 | >1.3.0", range.toString());
		assertFalse(range.contains(VersionFactory.getVersion("1.2.6")));
	}

	
	@Test
	public void testBracketedRange()
	{
		IVersionRange range = VersionFactory.getRange("(>1.2.5 & <1.3)");
		assertNotNull(range);
		assertEquals(">1.2.5 & <1.3.0", range.toString());
		assertTrue(range.contains(VersionFactory.getVersion("1.2.6")));
	}

	@Test
	public void testComplexLogic()
	{
		IVersionRange range = VersionFactory.getRange("(>1.2.5 & <1.3) | (>2.2.5 & <2.3)");
		assertNotNull(range);
		assertEquals("(>1.2.5 & <1.3.0) | (>2.2.5 & <2.3.0)", range.toString());
		assertTrue(range.contains(VersionFactory.getVersion("1.2.6")));
		assertTrue(range.contains(VersionFactory.getVersion("2.2.6")));
	}
}
