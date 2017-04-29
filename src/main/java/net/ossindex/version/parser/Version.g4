grammar Version;

@header {
}

@parser::members {
	// From: http://stackoverflow.com/questions/29060496/allow-whitespace-sections-antlr4/29115489#29115489
	/** We can enable and disable whitespace, which is required for some special
	 * parsing rules.
	 */
	 /*
	public void enableWs() {
	    if (_input instanceof MultiChannelTokenStream) {
	        ((MultiChannelTokenStream) _input).enable(HIDDEN);
	    }
	}
	
	public void disableWs() {
	    if (_input instanceof MultiChannelTokenStream) {
	        ((MultiChannelTokenStream) _input).disable(HIDDEN);
	    }
	}
	*/
	
	/**
	 * Returns true if there is whitespace ahead in the HIDDEN channel.
	 */
	private boolean whitespace() {
		// Get the token ahead of the current index
		int index = this.getCurrentToken().getTokenIndex() - 1;
		Token ahead = _input.get(index);
		// If the next token is not hidden then we don't care
		if(ahead.getChannel() != Lexer.HIDDEN) return false;
		
		int type = ahead.getType();
		return (type == WS);
	}
}

range
	: maven_ranges EOF
	| version_set EOF
	| union_range EOF
	| range_type EOF
	| broken_range EOF
	;

maven_ranges
    : maven_range
    | maven_range ',' maven_ranges
    ;

/**
 * Handle a variety of maven-style ranges
 */
maven_range
    : ('[' | '(') (
      | version ',' version
      | version ','
      | version
      | ',' version
      ) (']' | ')')
    ;

/** A few special cases of broken ranges. We are trying to handle unfortunate
 * situations as best we can to get SOMETHING from the chaos.
 */
broken_range
	: simple_range .*
	| version .*
	;

range_type
	: logical_range
	| simple_range
	;

union_range
	: version OR union_range
	| version OR version
	| version OR range_type
	| range_type OR union_range
	| range_type OR range_type
	| range_type OR version
	;

/** Ranges connected by logical operators
 */
logical_range
	: '(' logical_range ')'
	| '(' simple_range ')'
	
	| simple_range {whitespace();} simple_range
	| logical_range {whitespace();} simple_range
	| logical_range {whitespace();} logical_range
	
	| simple_range '&' simple_range
	
	| logical_range '&' simple_range
	
	| logical_range '&' logical_range
	;

/** A set of versions
 */
version_set
	: version
	| version_set ',' version
	;

simple_range
	: '<' version
	| '<=' version
	| '>' version
	| '>=' version
	;

version
	: numeric_version
	| postfix_version
	| prefixed_version
	| named_version
	;

/** A version which has text at the beginning
 *
 * The match at the beginning is non-greedy, so we should get the best
 * version possible.
 */
prefixed_version
	: any*? numeric_version
	| any*? postfix_version
	;

/* Matches versions of various sorts that at least appear like semantic versioning,
 * though they may not strictly match. Close enough to handle in this one place.
 */
postfix_version
	: NUMBER '.' NUMBER '.' NUMBER identifier
	| NUMBER '.' NUMBER '.' NUMBER '.' identifier
	| NUMBER '.' NUMBER '.' NUMBER '-' identifier
	;

/** Simple numeric matching. Strip trailing dots if they exist.
 */
numeric_version
	: NUMBER '.' NUMBER '.' NUMBER '.' NUMBER '.'?
	| NUMBER '.' NUMBER '.' NUMBER '.'?
	| NUMBER '.' NUMBER '.'?
	| NUMBER '.'?
	;

/** A fall back for when all else fails
 */
named_version
	: any+
	;

/** Note that identifier is NOT GREEDY.
 * We need special handling of the first character
 */
identifier
	: ~(NUMBER | '.' | '-' | '&' | OR) any*?
	;

/** "any" exclusive of comparison operators and such
 */
any
	: ANY
	| '-'
	| '.'
	| NUMBER
	;

/** This is a bit of a kludge, since it will also match ||| and |||| which are
 * quite silly.
 */
OR
	: '|'+
	;

NUMBER
	: ('0'..'9')+
	;

ANY
	: 'a'..'z'
	| 'A'..'Z'
	| '0'..'9'
	| '+'
	| NUMBER
	;

WS
	: (' ' | '\t' | '\\n') -> channel(HIDDEN)
	;
