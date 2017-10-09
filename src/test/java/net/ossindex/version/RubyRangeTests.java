package net.ossindex.version;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Ranges found in the ruby advisories
 *
 * @author Ken Duck
 */
public class RubyRangeTests
{

  @Test
  public void simplePessimistic() {
    IVersionRange range = VersionFactory.getRange("~> 1.9.3.484");
    assertNotNull(range);
    assertEquals(">=1.9.3.484 <1.9.4.0", range.toString());

    range = VersionFactory.getRange("~> 1.9.3");
    assertNotNull(range);
    assertEquals(">=1.9.3 <1.10.0", range.toString());

    range = VersionFactory.getRange("~> 1.9");
    assertNotNull(range);
    assertEquals(">=1.9.0 <2.0.0", range.toString());
  }
}
