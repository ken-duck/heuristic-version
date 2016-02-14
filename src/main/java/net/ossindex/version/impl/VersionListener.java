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

import java.util.Stack;

import net.ossindex.version.IVersion;
import net.ossindex.version.IVersionRange;
import net.ossindex.version.parser.VersionBaseListener;
import net.ossindex.version.parser.VersionParser;

/** Listener for the version parser.
 * 
 * This converts all the various versions and ranges we can find to
 * semantic versions and ranges.
 * 
 * @author Ken Duck
 *
 */
public class VersionListener extends VersionBaseListener
{
	private Stack<Object> stack = new Stack<Object>();

	private IVersionRange range;

	public IVersionRange getRange()
	{
		return range;
	}

	@Override
	public void exitNumeric_version(VersionParser.Numeric_versionContext ctx)
	{
		SemanticVersion version = null;

		int count = ctx.getChildCount();
		switch(count)
		{
		case 1:
		{
			int major = Integer.parseInt(ctx.getChild(0).getText());
			version = new SemanticVersion(major);
			break;
		}
		case 3:
		{
			int major = Integer.parseInt(ctx.getChild(0).getText());
			int minor = Integer.parseInt(ctx.getChild(2).getText());
			version = new SemanticVersion(major, minor);
			break;
		}
		case 5:
		{
			int major = Integer.parseInt(ctx.getChild(0).getText());
			int minor = Integer.parseInt(ctx.getChild(2).getText());
			int patch = Integer.parseInt(ctx.getChild(4).getText());
			version = new SemanticVersion(major, minor, patch);
			break;
		}
		}
		stack.push(version);
	}

	/** Simple semantic version
	 * 
	 * @see net.ossindex.version.parser.VersionBaseListener#exitSemantic_version(net.ossindex.version.parser.VersionParser.Semantic_versionContext)
	 */
	@Override
	public void exitPostfix_version(VersionParser.Postfix_versionContext ctx)
	{
		SemanticVersion version = null;

		int count = ctx.getChildCount();
		switch(count)
		{
		case 5:
			int major = Integer.parseInt(ctx.getChild(0).getText());
			int minor = Integer.parseInt(ctx.getChild(2).getText());
			int patch = Integer.parseInt(ctx.getChild(4).getText());
			version = new SemanticVersion(major, minor, patch);
			break;
		case 6:
			version = new SemanticVersion(
					ctx.getChild(0).getText() + "."
							+ ctx.getChild(2).getText() + "."
							+ ctx.getChild(4).getText() + "-"
							+ ctx.getChild(5).getText());
			break;
		case 7:
			version = new SemanticVersion(ctx.getText());
			break;
		}
		stack.push(version);
	}

	/**
	 * Get a named version.
	 */
	@Override
	public void exitNamed_version(VersionParser.Named_versionContext ctx)
	{
		IVersion version = new NamedVersion(ctx.getText());
		stack.push(version);
	}

	/** Get whatever is on the stack and make a version range out of it
	 * 
	 * @see net.ossindex.version.parser.VersionBaseListener#exitRange(net.ossindex.version.parser.VersionParser.RangeContext)
	 */
	@Override
	public void exitRange(VersionParser.RangeContext ctx)
	{
		Object o = stack.pop();
		if(o instanceof IVersion)
		{
			//range = new SemanticVersionRange((SemanticVersion)o);
			range = new VersionSet((IVersion)o);
		}
		else if(o instanceof IVersionRange)
		{
			range = (IVersionRange)o;
		}

	}

	/** A simple range.
	 * 
	 * < 1.2.5
	 * 
	 * @see net.ossindex.version.parser.VersionBaseListener#exitSimple_range(net.ossindex.version.parser.VersionParser.Simple_rangeContext)
	 */
	@Override
	public void exitSimple_range(VersionParser.Simple_rangeContext ctx)
	{
		String operator = ctx.getChild(0).getText();
		Object o = stack.pop();
		if(o instanceof SemanticVersion)
		{
			//range = new SemanticVersionRange((SemanticVersion)o);
			range = new VersionRange(operator, (SemanticVersion)o);
			stack.push(range);
		}
		else
		{
			throw new AssertionError("Expected a semantic version, got a " + o.getClass().getSimpleName());
		}
	}

	/** Set of versions
	 * 
	 * @see net.ossindex.version.parser.VersionBaseListener#exitVersion_set(net.ossindex.version.parser.VersionParser.Version_setContext)
	 */
	@Override
	public void exitVersion_set(VersionParser.Version_setContext ctx)
	{
		Object o1 = stack.pop();
		if(stack.isEmpty())
		{
			VersionSet set = new VersionSet((IVersion)o1);
			stack.push(set);
		}
		else
		{
			VersionSet set = (VersionSet)stack.peek();
			set.add((IVersion)o1);
		}
	}

	@Override
	public void exitLogical_range(VersionParser.Logical_rangeContext ctx)
	{
		// If there are not three tokens then ignore
		if(ctx.getChildCount() == 3)
		{
			String first = ctx.getChild(0).getText();
			if("(".equals(first))
			{
				// bracketed token, do nothing
			}
			else
			{
				Object o1 = stack.pop();
				Object o2 = stack.pop();

				String operator = ctx.getChild(1).getText();

				switch(operator)
				{
				case "&":
					stack.push(new AndRange((IVersionRange)o2, (IVersionRange)o1));
					break;
				case "|":
					stack.push(new OrRange((IVersionRange)o2, (IVersionRange)o1));
					break;
				}
			}
		}
	}

}
