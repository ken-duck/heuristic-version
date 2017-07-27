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

import net.ossindex.version.IVersion;
import net.ossindex.version.IVersionRange;

/** Two ranges anded together
 * 
 * @author Ken Duck
 *
 */
public class AndRange extends AbstractCommonRange
{
	/**
	 * 
	 */
	protected IVersionRange range1;
	protected IVersionRange range2;
	
	private String type;
	private boolean hasErrors = false;
	
	private boolean isBounded = false;


	/**
	 * And the ranges, ordering them in Vor's preferred order.
	 */
	public AndRange(IVersionRange range1, IVersionRange range2)
	{
		// The ranges should intersect
		if (!range1.intersects(range2)) {
			throw new AssertionError("Anded ranges do not intersect; this can never happen [" + range1 + " & " + range2 + "]");
		}
		
		// If both ranges are VersionRange then these are likely overlapping
		// ranges which cause a bounded range. This is the most likely case.
		if ((range1 instanceof VersionRange) && (range2 instanceof VersionRange)) {
			if (((VersionRange)range1).isUnbounded()) {
				if (((VersionRange)range2).isUnbounded()) {
					IVersion v1 = range1.getMinimum();
					IVersion v2 = range2.getMinimum();
					if (v1.compareTo(v2) <= 0) {
						this.range1 = range1;
						this.range2 = range2;
					} else {
						this.range2 = range1;
						this.range1 = range2;
					}
				} else {
					this.range1 = range1;
					this.range2 = range2;
					isBounded = true;
				}
			} else if (((VersionRange)range2).isUnbounded()) {
				this.range2 = range1;
				this.range1 = range2;
				isBounded = true;
			} else {
				// Both ranges are from 0 to n/m, I don't have a preference cause that
				// is silly.
				this.range1 = range1;
				this.range2 = range2;
			}
		} else if ((range1 instanceof AndRange) && ((AndRange)range1).isBounded && (range2 instanceof VersionRange)) {
			// Special case where a bounded range is reduced by another simple range
			// We already know they intersect. We just replace one end with the other.
			AndRange arange = (AndRange)range1;
			VersionRange boundingRange = (VersionRange)range2;
			if (boundingRange.isUnbounded()) {
				this.range2 = arange.last();
				this.range1 = boundingRange;
			} else {
				this.range1 = arange.first();
				this.range2 = boundingRange;
			}
		} else {
			// I honestly doubt we will run into situations like this, so I don't really
			// care on order for now.
			this.range1 = range1;
			this.range2 = range2;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.version.IVersionRange#contains(net.ossindex.version.IVersion)
	 */
	@Override
	public boolean contains(IVersion version)
	{
		return range1.contains(version) && range2.contains(version);
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
		if ((range1 instanceof VersionRange) && (range2 instanceof VersionRange)) {
			if (isBounded) {
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.version.impl.IVersionRange#getMinimum()
	 */
	@Override
	public IVersion getMinimum()
	{
		IVersion v1 = range1.getMinimum();
		return v1;
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.version.impl.IVersionRange#getMaximum()
	 */
	@Override
	public IVersion getMaximum()
	{
		if (isBounded) {
			IVersion v2 = range2.getMaximum();
			return v2;
		}
		
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean contains(IVersionRange yourRange) {
		// Deal with the simple situation first, which is when we have two ranges
		// that define a bounded range. In this case both ranges should contain
		// the target range.
		if ((range1 instanceof VersionRange) && (range2 instanceof VersionRange)) {
			return range1.contains(yourRange) && range2.contains(yourRange);
		}
		
		// Unhandled situation
		return false;
	}	
	
	public String getOperator()
	{
		return "&";
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.version.IVersionRange#intersects(net.ossindex.version.IVersionRange)
	 */
	@Override
	public boolean intersects(IVersionRange yourRange)
	{
		return range1.intersects(yourRange) && range2.intersects(yourRange);
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
			type = range1.getType();
		}
		if (type == null) {
			type = range2.getType();
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
		return range1.hasErrors() || range2.hasErrors();
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		if (!range1.isDiscrete() && !range1.isSimple()) {
			sb.append("(").append(range1).append(")");
		} else {
			sb.append(range1);
		}
		// The "&" is implicit
		sb.append(" ");
		if (!range2.isDiscrete() && !range2.isSimple()) {
			sb.append("(").append(range2).append(")");
		} else {
			sb.append(range2);
		}
		return sb.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ossindex.version.impl.AbstractCommonRange#invert()
	 */
	@Override
	public IVersionRange invert() {
		IVersionRange irange1 = range1.invert();
		IVersionRange irange2 = range2.invert();
		return new OrRange(irange1, irange2);
	}
	

	public IVersionRange first() {
		return range1;
	}

	public IVersionRange last() {
		return range2;
	}
}
