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
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import net.ossindex.version.impl.AndRange;
import net.ossindex.version.impl.NamedVersion;
import net.ossindex.version.impl.OrRange;
import net.ossindex.version.impl.VersionErrorListener;
import net.ossindex.version.impl.VersionListener;
import net.ossindex.version.impl.VersionRange;
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
		if (range == null) {
			return null;
		}
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
		if (vstring == null || vstring.isEmpty()) {
			IVersion version = new NamedVersion("");
			return new VersionSet(version);
		}
		try
		{
			InputStream stream = new ByteArrayInputStream(vstring.getBytes(StandardCharsets.UTF_8));
			ANTLRInputStream input = new ANTLRInputStream(stream);
			
			VersionErrorListener errorListener = new VersionErrorListener();
			
			VersionLexer lexer = new VersionLexer(input);
			lexer.removeErrorListeners();
			lexer.addErrorListener(errorListener);

			CommonTokenStream tokens = new CommonTokenStream(lexer);

			VersionParser parser = new VersionParser(tokens);
			
			parser.addErrorListener(errorListener);

			RangeContext context = parser.range();

			ParseTreeWalker walker = new ParseTreeWalker();
			VersionListener listener = new VersionListener();
			walker.walk(listener, context);

			IVersionRange range = listener.getRange();
			if (errorListener.hasErrors()) {
				range.setHasErrors(true);
			}
			return range;
		}
		catch (EmptyStackException e) {
			System.err.println("Could not parse: " + vstring);
		}
		catch (Exception e)
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
		if (versions == null) {
			return null;
		}
		IVersionRange results = null;
		for (String version : versions) {
			IVersionRange range = VersionFactory.getRange(version);
			if (results == null) {
				results = range;
			} else {
				results = new OrRange(results, range);
			}
		}
		return results;
	}

	/** Join this set of ranges together. This could result in a set, or in a
	 * logical range.
	 * 
	 * @param versions
	 * @return
	 */
	public static IVersionRange getRange(Collection<String> versions) {
		if (versions == null) {
			return null;
		}
		return getRange(versions.toArray(new String[versions.size()]));
	}

	public static boolean isMavenRange(String vstring) {
		IVersionRange range = VersionFactory.getRange(vstring);
		return "maven".equals(range.getType());
	}

	/**
	 * Given two "or" ranges or "simple" ranges, merge them together. We are
	 * assuming that the ranges are provided in order for now.
	 * 
	 * For example:
	 * 
	 *   >1.0.0 merged with (<2.0.0 | >3.0.0) will become (>1.0.0 <2.0.0 | >3.0.0)
	 */
	public static IVersionRange merge(IVersionRange... ranges) {
		if (ranges.length < 2) {
			return ranges[0];
		}
		
		// Check the type of the first range
		if (!(ranges[0] instanceof VersionRange)) {
			if (!(ranges[0] instanceof OrRange)) {
				throw new UnsupportedOperationException("Incorrect type for ranges[0]");
			}
			if (((OrRange)ranges[0]).size() != 2) {
				throw new UnsupportedOperationException("Incorrect size for ranges[0]");
			}
		} else {
			if (!((VersionRange)ranges[0]).isUnbounded()) {
				throw new UnsupportedOperationException("ranges[0] should be unbounded (> or >=)");
			}
		}
		
		int lastIndex = ranges.length - 1;
		
		// Check the type of the last range
		if (!(ranges[lastIndex] instanceof VersionRange)) {
			if (!(ranges[lastIndex] instanceof OrRange)) {
				throw new UnsupportedOperationException("Incorrect type for ranges[last]");
			}
			if (((OrRange)ranges[lastIndex]).size() != 2) {
				throw new UnsupportedOperationException("Incorrect size for ranges[last]");
			}
		} else {
			if (((VersionRange)ranges[lastIndex]).isUnbounded()) {
				throw new UnsupportedOperationException("ranges[0] should be bounded (< or <=)");
			}
		}
		
		// Check the rest of the types
		for (int i = 1; i < lastIndex; i++) {
			if (!(ranges[i] instanceof OrRange)) {
				throw new UnsupportedOperationException("Incorrect type for ranges[" + i + "]");
			}
			
			if (((OrRange)ranges[i]).size() != 2) {
				throw new UnsupportedOperationException("Incorrect size for ranges[" + i + "]");
			}
		}
		
		List<IVersionRange> results = new LinkedList<IVersionRange>();
		IVersionRange last = null;
		for (int i = 0; i < ranges.length; i++) {
			IVersionRange range = ranges[i];
			if (last == null) {
				if (range instanceof VersionRange) {
					last = range;
				} else {
					OrRange orange = (OrRange)range;
					results.add(orange.first());
					last = orange.last();
				}
			} else {
				if (range instanceof VersionRange) {
					AndRange arange = new AndRange(last, range);
					results.add(arange);
					last = null;
				} else {
					OrRange orange = (OrRange)range;
					AndRange arange = new AndRange(last, orange.first());
					results.add(arange);
					last = orange.last();
				}
			}
		}
		
		if (last != null) {
			results.add(last);
		}
		
		return new OrRange(results);
	}
}
