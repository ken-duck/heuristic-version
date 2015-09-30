/**
 *	Copyright (c) 2015 Vör Security Inc.
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

/** SemanticVersion extension that allows for somewhat more broad matching
 * by "repairing" the version.
 * 
 * Subclass this class to provide special preprocessing and comparison handling.
 * 
 * @author Ken Duck
 *
 */
public class FlexibleSemanticVersion extends SemanticVersion implements Comparable<IVersion>, IVersion
{
	/** Use an external library for parsing.
	 * 
	 * @param buf
	 */
	public FlexibleSemanticVersion(String buf)
	{
		setVersion(preprocess(buf));
	}


	/** Ensure there is a hyphen before the suffix
	 * 
	 * @param buf
	 * @return
	 */
	private String preprocess(String buf)
	{
		int index;
		// Now find the end of numerals and dots, we'll call what remains the
		// 'suffix'.
		for(index = 0; index < buf.length(); index++)
		{
			char c = buf.charAt(index);
			if(c < '0' || c > '9')
			{
				if(c != '.')
				{
					break;
				}
			}
		}
		
		String ver = buf.substring(0, index);
		String[] tokens = ver.split("\\.");
		StringBuilder sb = new StringBuilder();
		for (int tokenIndex = 0; tokenIndex < tokens.length; tokenIndex++)
		{
			String token = tokens[tokenIndex];
			if(tokenIndex > 0) sb.append(".");
			boolean hasNonZero = false;
			for(int i = 0; i < token.length(); i++)
			{
				char c = token.charAt(i);
				if(hasNonZero)
				{
					sb.append(c);
				}
				else if(c > '0' && c <= '9')
				{
					sb.append(c);
					hasNonZero = true;
				}
			}
			if(!hasNonZero) sb.append('0');
		}
		buf = sb.toString();

		if(index < buf.length())
		{
			String suffix = buf.substring(index);
			// Remove hyphens, we'll put them back later
			while(suffix.startsWith("-")) suffix = suffix.substring(1);
			buf = buf.substring(0, index) + "-" + suffix;
		}
		return buf;
	}
}
