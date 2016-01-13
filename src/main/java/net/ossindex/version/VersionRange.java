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
package net.ossindex.version;

import java.util.LinkedList;
import java.util.List;

import net.ossindex.version.impl.AetherVersionRange;
import net.ossindex.version.impl.IVersionRange;
import net.ossindex.version.impl.SemanticVersionRange;

import org.eclipse.aether.util.version.GenericVersionScheme;
import org.eclipse.aether.version.InvalidVersionSpecificationException;
import org.eclipse.aether.version.VersionScheme;

import com.github.zafarkhaja.semver.expr.LexerException;
import com.github.zafarkhaja.semver.expr.UnexpectedTokenException;

/** Uses a variety of specific implementations of the VersionRange classes to
 * build a range and compare versions.
 * 
 * @author Ken Duck
 *
 */
public class VersionRange
{
	/**
	 * Specific range implementations we are using
	 */
	private List<IVersionRange> ranges = new LinkedList<IVersionRange>();
	
	/**
	 * 
	 * @param ranges
	 */
	public VersionRange(String[] ranges)
	{
		for (String range : ranges)
		{
			this.ranges.add(parse(range));
		}
	}

	public VersionRange(String range)
	{
		ranges.add(parse(range));
	}
	
	
	/** Parse the string, trying to figure out the best range implementation
	 * to use.
	 * 
	 * @param range
	 * @return
	 */
	private IVersionRange parse(String range)
	{
		// First try semantic ranges
		try
		{
			return new SemanticVersionRange(range);
		}
		catch(UnexpectedTokenException | LexerException e) {} // Ignore the parsing exceptions
		catch(Exception e)
		{
			System.err.println("Exception parsing range: " + e.getClass().getSimpleName());
			e.printStackTrace();
		}
		

		try
		{
			return new AetherVersionRange(range);
		}
		catch(InvalidVersionSpecificationException e) {} // Ignore the parsing exceptions
		catch(Exception e)
		{
			System.err.println("Exception parsing range: " + e.getClass().getSimpleName());
			e.printStackTrace();
		}

//		try
//		{
//			return new FlexibleVersionRange(range);
//		}
//		catch(Exception e)
//		{
//			System.err.println("Exception parsing range: " + e.getClass().getSimpleName());
//			e.printStackTrace();
//		}
		throw new UnsupportedOperationException("Cannot parse range: " + range);
	}

	/** An imperfect method that tried to determine if one range intersects
	 * the other range. This is imperfect in that there are complex ranges
	 * for which this will not work at this time.
	 * 
	 * @param target
	 * @return
	 */
	boolean intersects(VersionRange target)
	{
		for(IVersionRange myRange: ranges)
		{
			for(IVersionRange yourRange: target.ranges)
			{
				// Check simple atomic cases
				if(myRange.isAtomic() && yourRange.contains(myRange.getMinimum())) return true;
				if(yourRange.isAtomic() && myRange.contains(yourRange.getMinimum())) return true;
				
				// Simple ranges
				if(myRange.isSimple()
						&& yourRange.contains(myRange.getMinimum())
						&& yourRange.contains(myRange.getMaximum())) return true;
				
				if(yourRange.isSimple()
						&& myRange.contains(yourRange.getMinimum())
						&& myRange.contains(yourRange.getMaximum())) return true;
				
				// Both ranges are complex. Panic!
			}
		}
		return false;
	}

	/**
	 * 
	 * @param version
	 * @return
	 */
	public boolean contains(IVersion version)
	{
		for(IVersionRange myRange: ranges)
		{
			if(myRange.contains(version)) return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for(IVersionRange myRange: ranges)
		{
			if(!first) sb.append(",");
			sb.append(myRange);
		}
		return sb.toString();
	}
}