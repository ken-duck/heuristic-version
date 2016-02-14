grammar Version;

@header {
}

range
	: version_set EOF
	| range_type EOF
	;

range_type
	: logical_range
	| simple_range
	;

/** Ranges connected by logical operators
 */
logical_range
	: '(' logical_range ')'
	| '(' simple_range ')'
	| simple_range '&' simple_range
	| simple_range '|' simple_range
	
	| logical_range '&' simple_range
	| simple_range '|' logical_range
	
	| logical_range '&' logical_range
	| logical_range '|' logical_range
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
	| NUMBER '.' NUMBER '.' NUMBER '-' identifier
	;

/** Simple numeric matching
 */
numeric_version
	: NUMBER '.' NUMBER '.' NUMBER
	| NUMBER '.' NUMBER
	| NUMBER
	;

/** A fall back for when all else fails
 */
named_version
	: any+
	;

/** Note that identifier is NOT GREEDY
 */
identifier
	: ~(NUMBER | '-') any*?
	;

/** "any" exclusive of comparison operators and such
 */
any
	: ANY
	| '-'
	| '.'
	| NUMBER
	;

NUMBER
	: ('0'..'9')+
	;

ANY
	: 'a'..'z'
	| 'A'..'Z'
	| '0'..'9'
	| '+'
	| '('
	| ')'
	| NUMBER
	;

WS
	: (' ' | '\t' | '\\n') -> channel(HIDDEN)
	;
