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

import java.lang.reflect.Field;

import net.ossindex.version.IVersion;

import org.eclipse.aether.util.version.GenericVersionScheme;
import org.eclipse.aether.version.InvalidVersionSpecificationException;
import org.eclipse.aether.version.Version;
import org.eclipse.aether.version.VersionScheme;

/** Wrapper around the aether version.
 * 
 * This uses terrible reflection kludges to get details. Not guaranteed to work
 * in the future.
 * 
 * @author Ken Duck
 *
 */
public class AetherVersion implements IVersion
{

	/**
	 * Underlying implementation
	 */
	private Version version;

	/**
	 * We may be able to dig this information out
	 */
	int major = -1; // -1 means none have been set
	int minor = -1;
	int patch = -1;

	/**
	 * Indicates whether we think this is a stable version or not
	 */
	private boolean stable = true;

	/** Parse the version. Try and pull detail information from the private fields. 
	 * 
	 * @param version
	 * @throws InvalidVersionSpecificationException
	 */
	public AetherVersion(String version) throws InvalidVersionSpecificationException
	{
		VersionScheme scheme = new GenericVersionScheme();
		this.version = scheme.parseVersion(version);

		try
		{
			// Try to read through the private items of the class to retrieve
			// interesting information
			Field field = this.version.getClass().getDeclaredField("items");
			Field iField = null;
			try
			{
				field.setAccessible(true);
				Object[] items = (Object[]) field.get(this.version);
				for (Object item : items)
				{
					if(iField == null)
					{
						iField = item.getClass().getDeclaredField("value");
						iField.setAccessible(true);
					}
					Object itemValue = iField.get(item);
					if(itemValue instanceof Integer)
					{
						if(major < 0) major = (Integer)itemValue;
						else if(minor < 0) minor = (Integer)itemValue;
						else if(patch < 0) patch = (Integer)itemValue;
					}
					else
					{
						if(major >= 0)
						{
							String s = itemValue.toString();
							// Some strings denote a stable build
							if("ga".equals(s));
							else if("final".equals(s));
							else if("sp".equals(s));
							
							// Otherwise this is an unstable build
							else stable = false;
						}
					}
				}
				
				if(minor < 0) minor = 0;
				if(patch < 0) patch = 0;
			}
			finally
			{
				// Privatize the fields again
				if(field != null) field.setAccessible(false);
				if(iField != null) iField.setAccessible(false);
			}
		}
		catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e)
		{
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(IVersion o)
	{
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.version.IVersion#getMajor()
	 */
	@Override
	public int getMajor()
	{
		return major;
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.version.IVersion#getMinor()
	 */
	@Override
	public int getMinor()
	{
		return minor;
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.version.IVersion#getPatch()
	 */
	@Override
	public int getPatch()
	{
		return patch;
	}

	/*
	 * (non-Javadoc)
	 * @see net.ossindex.version.IVersion#isStable()
	 */
	@Override
	public boolean isStable()
	{
		return stable;
	}

	/** Get the underlying implementation
	 * 
	 * @return
	 */
	public Version getVersionImpl()
	{
		return version;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return version.toString();
	}
}
