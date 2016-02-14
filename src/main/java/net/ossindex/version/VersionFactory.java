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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import net.ossindex.version.impl.NamedVersion;
import net.ossindex.version.impl.VersionListener;
import net.ossindex.version.impl.VersionSet;
import net.ossindex.version.parser.VersionLexer;
import net.ossindex.version.parser.VersionParser;
import net.ossindex.version.parser.VersionParser.RangeContext;

/** This factory provides an appropriate version implementation for
 * specified version strings.
 * 
 * @author Ken Duck
 *
 */
public class VersionFactory
{
	private static VersionFactory instance;

	/**
	 * Private. Use "getVersionFactory" instead.
	 */
	private VersionFactory()
	{
	}

	/** Version factory, which is really not needed since we have all
	 * static methods now.
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
	 * @param vstring A string version to be parsed
	 * @return A version implementation
	 */
	public static IVersion getVersion(String vstring)
	{
		IVersionRange range = getRange(vstring);
		return range.getMinimum();
	}

	/** Get a version implementation. A hint may be provided to help
	 * choose the best implementation.
	 * 
	 * @param hint Hint of the version style
	 * @param version A string version to be parsed
	 * @return A version implementation
	 */
	public static IVersion getVersion(String hint, String version)
	{
		return getVersion(version);
	}

	
	/** Get a version range
	 * 
	 * @param vstring
	 * @return
	 */
	public static IVersionRange getRange(String vstring)
	{
		try
		{
			InputStream stream = new ByteArrayInputStream(vstring.getBytes(StandardCharsets.UTF_8));
			ANTLRInputStream input = new ANTLRInputStream(stream);
			VersionLexer lexer = new VersionLexer(input);

			CommonTokenStream tokens = new CommonTokenStream(lexer);

			VersionParser parser = new VersionParser(tokens);

			RangeContext context = parser.range();

			ParseTreeWalker walker = new ParseTreeWalker();
			VersionListener listener = new VersionListener();
			walker.walk(listener, context);

			IVersionRange range = listener.getRange();
			return range;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		IVersion version = new NamedVersion(vstring);
		return new VersionSet(version);
	}

	/** Join this set of ranges together. This could result in a set, or in a
	 * logical range.
	 * 
	 * @param versions
	 * @return
	 */
	public static IVersionRange getRange(String[] versions)
	{
		return null;
	}
}
