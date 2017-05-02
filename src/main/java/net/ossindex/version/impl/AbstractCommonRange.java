package net.ossindex.version.impl;

import net.ossindex.version.IVersion;
import net.ossindex.version.IVersionRange;

public abstract class AbstractCommonRange implements IVersionRange {
	
	@Override
	public int compareTo(IVersionRange yourRange) {
		IVersion myMin = getMinimum();
		IVersion yourMin = yourRange.getMinimum();
		return myMin.compareTo(yourMin);
	}

}
