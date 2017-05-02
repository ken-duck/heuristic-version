/**
 *	Copyright (c) 2016-2017 Vor Security Inc.
 *	All rights reserved.
 *	
 *	Redistribution and use in source and binary forms, with or without
 *	modification, are permitted provided that the following conditions are met:
 *	    * Redistributions of source code must retain the above copyright
 *	      notice, this list of conditions and the following disclaimer.
 *	    * Redistributions in binary form must reproduce the above copyright
 *	      notice, this list of conditions and the following disclaimer in the
 *	      documentation and/or other materials provided with the distribution.
 *	    * Neither the name of the <organization> nor the
 *	      names of its contributors may be used to endorse or promote products
 *	      derived from this software without specific prior written permission.
 *	
 *	THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *	ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *	WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *	DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 *	DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *	(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *	LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *	ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *	(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *	SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.ossindex.version.impl;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import net.ossindex.version.IVersion;
import net.ossindex.version.IVersionRange;

/** 
 * Multiple ranges ORed together. The or range allows multiple inputs (not just two)
 * since it is generally the top level operator.
 */
public class OrRange extends AbstractCommonRange
{
	private SortedSet<IVersionRange> ranges = new TreeSet<IVersionRange>();
	private String type;
	private boolean hasErrors = false;

	/**
	 * 
	 * @param operator
	 * @param version
	 */
	public OrRange(IVersionRange range1, IVersionRange range2)
	{
		ranges.add(range1);
		ranges.add(range2);
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.version.IVersionRange#contains(net.ossindex.version.IVersion)
	 */
	@Override
	public boolean contains(IVersion version)
	{
		for (IVersionRange range : ranges) {
			if (range.contains(version)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.version.impl.IVersionRange#isAtomic()
	 */
	@Override
	public boolean isDiscrete()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.version.impl.IVersionRange#isSimple()
	 */
	@Override
	public boolean isSimple()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.version.impl.IVersionRange#getMinimum()
	 */
	@Override
	public IVersion getMinimum()
	{
		Iterator<IVersionRange> it = ranges.iterator();
		IVersionRange r1 = it.next();
		IVersion min = r1.getMinimum();
		
		while (it.hasNext()) {
			IVersionRange r2 = it.next();
			IVersion v2 = r2.getMinimum();
			int cmp = min.compareTo(v2);
			if (cmp > 0) {
				return min = v2;
			}
		}
		
		return min;
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.version.impl.IVersionRange#getMaximum()
	 */
	@Override
	public IVersion getMaximum()
	{
		throw new UnsupportedOperationException();
	}

	public String getOperator()
	{
		return "|";
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.version.IVersionRange#intersects(net.ossindex.version.IVersionRange)
	 */
	@Override
	public boolean intersects(IVersionRange yourRange)
	{
		for (IVersionRange range : ranges) {
			if (range.intersects(yourRange)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.version.IVersionRange#getSimplifiedRange()
	 */
	@Override
	public IVersionRange getSimplifiedRange() {
		return this;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		if (type == null) {
			for (IVersionRange range : ranges) {
				type = range.getType();
				if (type != null) {
					break;
				}
			}
		}
		return type;
	}

	public void setHasErrors(boolean b) {
		hasErrors = b;
	}

	public boolean hasErrors() {
		if (hasErrors) {
			return true;
		}
		
		for (IVersionRange range : ranges) {
			if (range.hasErrors()) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		Iterator<IVersionRange> it = ranges.iterator();
		while (it.hasNext()) {
			IVersionRange range = it.next();
			if (!range.isDiscrete() && !range.isSimple()) {
				sb.append("(");
			}
			sb.append(range);
			if (!range.isDiscrete() && !range.isSimple()) {
				sb.append(")");
			}
			if (it.hasNext()) {
				sb.append(" | ");
			}
		}
		return sb.toString();
	}

	public OrRange add(IVersionRange range) {
		ranges.add(range);
		return this;
	}
}
