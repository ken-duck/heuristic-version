/**
 *	Copyright (c) 2016 Vör Security Inc.
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

/** An version set is a range where all the contained values are
 * explicit ranges. 
 * 
 * @author Ken Duck
 *
 */
public class VersionSet implements IVersionRange
{
	/**
	 * Used for both atomic and simple versions
	 */
	private SortedSet<IVersion> set = new TreeSet<IVersion>();

	/**
	 * 
	 * @param range
	 */
	public VersionSet(IVersion version)
	{
		set.add(version);
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.version.IVersionRange#contains(net.ossindex.version.IVersion)
	 */
	@Override
	public boolean contains(IVersion version)
	{
		return set.contains(version);
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.version.impl.IVersionRange#isAtomic()
	 */
	@Override
	public boolean isAtomic()
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.version.impl.IVersionRange#isSimple()
	 */
	@Override
	public boolean isSimple() {
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ossindex.version.impl.IVersionRange#getMinimum()
	 */
	@Override
	public IVersion getMinimum()
	{
		return set.first();
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.version.impl.IVersionRange#getMaximum()
	 */
	@Override
	public IVersion getMaximum()
	{
		return set.last();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		for(Iterator<IVersion> it = set.iterator(); it.hasNext();)
		{
			IVersion version = it.next();
			sb.append(version);
			if(it.hasNext()) sb.append(",");
		}
		return sb.toString();
	}

	/** Add another version to the set
	 * 
	 * @param version
	 */
	public void add(IVersion version)
	{
		set.add(version);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ossindex.version.IVersionRange#intersects(net.ossindex.version.IVersionRange)
	 */
	@Override
	public boolean intersects(IVersionRange yourRange)
	{
		throw new UnsupportedOperationException();
	}
}
