grammar Version;

@header {
}

@parser::members {
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

        /**
         * No whitespace in the HIDDEN channel
         */
	private boolean nw() {
	  return !whitespace();
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
    | '[' '-'? ']'
    | '(' '-'? ')'
    | '[' '-'? ')'
    | '(' '-'? ']'
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
	: semantic_range
	| logical_range
	| simple_range
	;

semantic_range
        : '^' version
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
	| '(' union_range ')'

	| simple_range {whitespace();} simple_range
	| logical_range {whitespace();} simple_range
	| logical_range {whitespace();} logical_range
	
	| simple_range ',' simple_range
	| logical_range ',' simple_range
	| logical_range ',' logical_range
	
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
	| '~>' version
	;

version
	: '='? stream? (numeric_version
	| postfix_version
	| prefixed_version
	| named_version)
	;

/** FIXME: Should we be ignoring the "stream"?
 */
stream
        : NUMBER ':';

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
	: NUMBER dot NUMBER dot NUMBER dot NUMBER sep identifier
	| NUMBER dot NUMBER dot NUMBER dot NUMBER {nw()}? identifier
	| NUMBER dot NUMBER dot NUMBER sep identifier
	| NUMBER dot NUMBER dot NUMBER {nw()}? identifier
	| NUMBER dot NUMBER sep identifier
	| NUMBER dot NUMBER {nw()}? identifier
	;

/** Simple numeric matching. Strip trailing dots if they exist.
 */
numeric_version
	: NUMBER dot NUMBER dot NUMBER dot NUMBER dot?
	| NUMBER dot NUMBER dot NUMBER dot?
	| NUMBER dot NUMBER dot?
	| NUMBER dot?
	;

sep
  : {nw()}? (
    '.' | '_' | '-'
  ) {nw()}?;

/** Do we need to loosen this up to allow spaces around dots?
 */
dot : {nw()}? '.' {nw()}?;

/** A fall back for when all else fails. Spaces are not valid in named versions, regardless.
 */
named_version : valid_named_version;

/** Named versions can contain all sorts of crazy values. We try and avoid completely invalid named
 * versions by disallowing certain "special characters" that should never be used in a named version
 * unless the developer is particularly mad.
 */
valid_named_version
	: any
	| any {nw()}? valid_named_version
	;

/** Note that identifier is NOT GREEDY.
 * We need special handling of the first character
 */
identifier
	: ~('.' | '-' | '&' | OR | ',') any*?
	;

/** "any" exclusive of comparison operators and such
 */
any
	: ANY
	| '-'
	| '_'
	| '.'
	| '~'
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
	: (' ' | '\t' | '\n' | '\r') -> channel(HIDDEN)
	;
