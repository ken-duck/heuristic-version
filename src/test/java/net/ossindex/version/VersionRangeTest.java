package net.ossindex.version;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Ignore;
import org.junit.Test;

/**
 * 
 * @author Ken Duck
 *
 */
public class VersionRangeTest
{
	@Test
	public void testComplexRange()
	{
		IVersionRange range = VersionFactory.getRange(">=2.5.0 <=2.5.6 || 2.5.6.SEC01 || 2.5.6.SEC02 || 2.5.7 || >=3.0.0 <3.0.3");
		assertNotNull(range);
		assertEquals(">=2.5.0 <=2.5.6 | 2.5.6-SEC01 | 2.5.6-SEC02 | 2.5.7 | >=3.0.0 <3.0.3", range.toString());
	}

	@Test
	public void testUnarySetRangeWithTextSuffix()
	{
		IVersionRange range = VersionFactory.getRange("2.5.6.SEC01");
		assertNotNull(range);
		assertEquals("2.5.6-SEC01", range.toString());
	}

	@Test
	public void testBinarySimpleRanges()
	{
		IVersionRange range = VersionFactory.getRange(">2.5.6 || >2.5.7");
		assertNotNull(range);
		assertEquals(">2.5.6 | >2.5.7", range.toString());
	}

	@Test
	public void testBinarySetRange()
	{
		IVersionRange range = VersionFactory.getRange("2.5.6 || 2.5.7");
		assertNotNull(range);
		assertEquals("2.5.6 | 2.5.7", range.toString());
	}

	@Test
	public void testBinarySetRangeWithTextSuffix()
	{
		IVersionRange range = VersionFactory.getRange("2.5.6.SEC01 || 2.5.6.SEC02");
		assertNotNull(range);
		assertEquals("2.5.6-SEC01 | 2.5.6-SEC02", range.toString());
	}
	
	@Test
	public void testWhitespaceAndRange()
	{
		IVersionRange range = VersionFactory.getRange("<1.4.1 >=0.4.3");
		assertNotNull(range);
		assertEquals(">=0.4.3 <1.4.1", range.toString());
	}

	@Test
	public void testCommaAndRange()
	{
		IVersionRange range = VersionFactory.getRange(">=0.10.0, <0.10.2");
		assertNotNull(range);
		assertEquals(">=0.10.0 <0.10.2", range.toString());
	}

	@Ignore
	@Test
	public void testTildeRange()
	{
		IVersionRange range = VersionFactory.getRange("~1.2.3");
		assertNotNull(range);
		assertEquals(">=1.2.3 <1.3.0", range.toString());
		
		range = VersionFactory.getRange("~1.2");
		assertNotNull(range);
		assertEquals(">=1.2.0 <1.3.0", range.toString());
		
		range = VersionFactory.getRange("~1");
		assertNotNull(range);
		assertEquals(">=1.0.0 <2.0.0", range.toString());
		
		range = VersionFactory.getRange("~0.2.3");
		assertNotNull(range);
		assertEquals(">=0.2.3 <0.3.0", range.toString());
		
		range = VersionFactory.getRange("~0.2");
		assertNotNull(range);
		assertEquals(">=0.2.0 <0.3.0", range.toString());
		
		range = VersionFactory.getRange("~0");
		assertNotNull(range);
		assertEquals(">=0.0.0 <1.0.0", range.toString());
		
		range = VersionFactory.getRange("~1.2.3-beta.2");
		assertNotNull(range);
		assertEquals(">=~1.2.3-beta.2 <1.3.0", range.toString());
	}

	@Ignore
	@Test
	public void testCaretRange()
	{
		IVersionRange range = VersionFactory.getRange("^1.2.3");
		assertNotNull(range);
		assertEquals(">=1.2.3 <2.0.0", range.toString());
		
		range = VersionFactory.getRange("^0.2.3");
		assertNotNull(range);
		assertEquals(">=0.2.3 <0.3.0", range.toString());
		
		range = VersionFactory.getRange("^0.0.3");
		assertNotNull(range);
		assertEquals(">=0.0.3 <0.0.4", range.toString());
		
		range = VersionFactory.getRange("^1.2.3-beta.2");
		assertNotNull(range);
		assertEquals(">=1.2.3-beta.2 <2.0.0", range.toString());
		
		range = VersionFactory.getRange("^0.0.3-beta");
		assertNotNull(range);
		assertEquals(">=0.0.3-beta <0.0.4", range.toString());
	}

	@Ignore
	@Test
	public void testXRange()
	{
		IVersionRange range = VersionFactory.getRange("*");
		assertNotNull(range);
		assertEquals(">=0.0.0", range.toString());
		
		range = VersionFactory.getRange("1.x");
		assertNotNull(range);
		assertEquals(">=1.0.0 <2.0.0", range.toString());
		
		range = VersionFactory.getRange("1.2.x");
		assertNotNull(range);
		assertEquals(">=1.2.0 <1.3.0", range.toString());
	}

}
