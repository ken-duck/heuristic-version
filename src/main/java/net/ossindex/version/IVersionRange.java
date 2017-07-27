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

/** Interface that all range implementations need to implement.
 * 
 * @author Ken Duck
 * 
 */
public interface IVersionRange extends Comparable<IVersionRange>
{
	/**
	 * 
	 * @param version
	 * @return
	 */
	public abstract boolean contains(IVersion version);

	/** Returns true if this range is a simple singular version
	 * 
	 * @return
	 */
	public abstract boolean isDiscrete();

	/** Return the minimum version that satisfies this range
	 * 
	 * @return
	 */
	public abstract IVersion getMinimum();

	/** Return the maximum version that satisfies this range
	 * 
	 * @return
	 */
	public abstract IVersion getMaximum();

	/** Return true if this is a simple range which has no gaps
	 * 
	 * @return
	 */
	public abstract boolean isSimple();

	/** Check if two ranges intersect
	 * 
	 * @param yourRange
	 * @return
	 */
	public abstract boolean intersects(IVersionRange yourRange);

	/**
	 * Check if one range contains another range.
	 */
	public abstract boolean contains(IVersionRange trange);
	
	/**
	 * Get a simplified version range, suitable for users to read quickly
	 * @return
	 */
	public abstract IVersionRange getSimplifiedRange();

	/**
	 * Give the range a type. This indicates the "style" of the range.
	 * 
	 * @param type
	 */
	public abstract void setType(String type);

	/**
	 * Get the range type
	 * 
	 * @return
	 */
	public abstract String getType();

	public abstract void setHasErrors(boolean b);
	
	public abstract boolean hasErrors();

	/**
	 * Generate a range that is the inverse of the provided range
	 */
	public abstract IVersionRange invert();
}