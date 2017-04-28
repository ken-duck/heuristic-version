package net.ossindex.version;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

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
		assertEquals("(>=2.5.0 & <=2.5.6) | (2.5.6-SEC01 | (2.5.6-SEC02 | (2.5.7 | (>=3.0.0 & <3.0.3))))", range.toString());
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
}
