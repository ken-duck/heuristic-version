/**
 *	Copyright (c) 2015-2016 Vör Security Inc.
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.aether.version.InvalidVersionSpecificationException;

import net.ossindex.version.impl.AetherVersion;
import net.ossindex.version.impl.FlexibleSemanticVersion;
import net.ossindex.version.impl.NamedVersion;
import net.ossindex.version.impl.NpmVersion;
import net.ossindex.version.impl.SemanticVersion;

import com.github.zafarkhaja.semver.ParseException;

/** This factory provides an appropriate version implementation for
 * specified version strings.
 * 
 * @author Ken Duck
 *
 */
public class VersionFactory
{
	private static VersionFactory instance;
	
	public static Pattern mdmdp_s = Pattern.compile("^([0-9]+)\\.([0-9]+)\\.([0-9]+)(.*)?$");
	public static Pattern mdm_s = Pattern.compile("^([0-9]+)\\.([0-9]+)(.*)?$");
	public static Pattern m_s = Pattern.compile("^([0-9]+)(.*)?$");

	/**
	 * Private. Use "getVersionFactory" instead.
	 */
	private VersionFactory()
	{
	}
	
	/**
	 * 
	 * @return The instance of the version factory
	 */
	public synchronized static VersionFactory getVersionFactory()
	{
		if(instance == null) instance = new VersionFactory();
		return instance;
	}
	
	/** Get a version implementation. Return the best match for the provided string.
	 * 
	 * @param buf A string version to be parsed
	 * @return A version implementation
	 */
	public IVersion getVersion(String buf)
	{
		if(buf == null || buf.isEmpty()) throw new IllegalArgumentException("Input string is NULL or empty");
		
		// Try a semantic match first
		try
		{
			return new SemanticVersion(buf);
		}
		catch(ParseException e)
		{
			// try something else
		}
		
		// Perhaps we can force the buffer into the semantic version definition
		IVersion version = getFlexibleVersion(buf);
		if(version != null)
		{
			return version;
		}
		
		// If all else fails, this is a named version
		return new NamedVersion(buf);
	}
	
	/** Get a version implementation. A hint may be provided to help
	 * choose the best implementation.
	 * 
	 * @param hint Hint of the version style
	 * @param version A string version to be parsed
	 * @return A version implementation
	 */
	public IVersion getVersion(String hint, String version)
	{
		if(hint != null)
		{
			switch(hint)
			{
			case "npm": return new NpmVersion(version);
			}
		}
		return getVersion(version);
	}
	
	/**
	 * 
	 * @param buf A string version to be parsed
	 * @return A version implementation if successful
	 */
	private FlexibleSemanticVersion getFlexibleVersion(String buf)
	{
		VersionParts parts = preprocess(buf);
		String ver = parts.version;
		
		String[] tokens = ver.split("\\.");
		switch(tokens.length)
		{
		case 2:
			ver = ver + ".0";
			break;
		case 3:
			// In some rare cases the patch version starts with a zero. Consider this
			// a suffix instead.
			if(tokens[2].length() > 1 && tokens[2].startsWith("0"))
			{
				ver = tokens[0] + "." + tokens[1] + ".0";
				if(parts.suffix == null) parts.suffix = "p" + tokens[2];
				else parts.suffix = "p" + tokens[2] + "-" + parts.suffix;
			}
			break;
		case 4:
			// Ignore the last digit. Assume it is a suffix.
			ver = tokens[0] + "." + tokens[1] + "." + tokens[2];
			if(parts.suffix == null) parts.suffix = "p" + tokens[3];
			else parts.suffix = "p" + tokens[3] + "-" + parts.suffix;
			break;
		default:
			// Cannot handle
			return null;
		}
		try
		{
			if(parts.suffix == null) return new FlexibleSemanticVersion(ver);
			parts.suffix = parts.suffix.trim();
			return new FlexibleSemanticVersion(ver + "-" + parts.suffix);
		}
		catch(Exception e)
		{
			// Cannot handle
			return null;
		}
	}

	/** Preprocess to try to create a reasonable semantic version. Keep the
	 * remainder of the information in this class. This requires the use of
	 * some static fields, which means the constructors.
	 * 
	 * @param buf A string version to be parsed
	 * @return A VersionParts class indicating the various parsed bits
	 */
	private VersionParts preprocess(String buf)
	{
		VersionParts version = new VersionParts();
		
		// First remove any prefix information
		int index;
		for(index = 0; index < buf.length(); index++)
		{
			char c = buf.charAt(index);
			if(c >= '0' && c <= '9') break;
		}
		if(index < buf.length())
		{
			version.prefix = buf.substring(0, index);
			buf = buf.substring(index);
		}
		
		// Major.Minor.Patch-suffix
		Matcher m = mdmdp_s.matcher(buf);
		if(m.matches())
		{
			if(m.groupCount() == 4) version.suffix = m.group(4);
			version.version = m.group(1) + "." + m.group(2) + "." + m.group(3);
			return version;
		}
		
		// Major.Minor-suffix
		m = mdm_s.matcher(buf);
		if(m.matches())
		{
			if(m.groupCount() == 3) version.suffix = m.group(3);
			version.version = m.group(1) + "." + m.group(2) + ".0";
			return version;
		}
		
		// Major suffix
		m = m_s.matcher(buf);
		if(m.matches())
		{
			if(m.groupCount() == 2) version.suffix = m.group(2);
			version.version = m.group(1) + ".0.0";
			return version;
		}

		version.version = buf;
		return version;
	}
	
	/** Adapt versions to other version classes when possible
	 * 
	 * @param type
	 * @param version
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T adapt(Class<T> type, IVersion version)
	{
		if(type.isAssignableFrom(AetherVersion.class))
		{
			try
			{
				return (T) new AetherVersion(version.toString());
			}
			catch (InvalidVersionSpecificationException e) {} // Ignore parse errors
		}
		throw new IllegalArgumentException("Cannot adapt version to " + type.getSimpleName() + " class");
	}

	/** Simple POJO used to collect useful information during version
	 * parsing.
	 * 
	 * @author Ken Duck
	 *
	 */
	class VersionParts
	{
		String suffix;
		String prefix;
		String version;
	}
}
