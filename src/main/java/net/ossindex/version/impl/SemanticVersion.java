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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.zafarkhaja.semver.Version;

/** Useful docs here: https://github.com/zafarkhaja/jsemver
 * 
 * Wrapper around jsemver semantic version parsing code.
 * 
 * Semantic versioning is described here: http://semver.org/
 * 
 * @author Ken Duck
 *
 */
public class SemanticVersion implements Comparable<IVersion>, IVersion
{
	private static final Logger LOG = LoggerFactory.getLogger(SemanticVersion.class);
	private Version v;
	
	/** Use an external library for parsing.
	 * 
	 * @param buf Version we are trying to parse
	 */
	public SemanticVersion(String buf)
	{
		setVersion(buf);
	}
	
	// Used by subclasses only
	protected SemanticVersion()
	{
	}
	
	/** Set the version
	 * 
	 * @param buf Version we are trying to parse
	 */
	protected void setVersion(String buf)
	{
		v = Version.valueOf(buf);
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.version.IVersion#getMajor()
	 */
	@Override
	public int getMajor()
	{
		if(v == null) return 0;
		return v.getMajorVersion();
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.version.IVersion#getMinor()
	 */
	@Override
	public int getMinor()
	{
		if(v == null) return 0;
		return v.getMinorVersion();
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.version.IVersion#getPatch()
	 */
	@Override
	public int getPatch()
	{
		if(v == null) return 0;
		return v.getPatchVersion();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof SemanticVersion)
		{
			SemanticVersion v = (SemanticVersion)o;
			if(this.v != null)
			{
				return this.v.equals(v.v);
			}
			else
			{
				return super.equals(v);
			}
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		if(v != null)
		{
			return v.hashCode();
		}
		return super.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(IVersion other)
	{
		if(other instanceof SemanticVersion)
		{
			SemanticVersion sv = (SemanticVersion)other;
			// If neither is named, then compare them as semantic values
			return this.v.compareTo(sv.v);
		}
		else
		{
			throw new UnsupportedOperationException();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return v.toString();
	}

	/** Returns true if this represents a stable release. We take this to mean
	 * unnamed and no suffix.
	 * 
	 * @return True if this is a stable release.
	 */
	public boolean isStable()
	{
		return true;
	}

	/** Get the SemVer instance.
	 * 
	 * @return
	 */
	public Version getVersionImpl()
	{
		return v;
	}
}
