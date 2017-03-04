package net.ossindex.version.impl;

import com.github.zafarkhaja.semver.Version;

import net.ossindex.version.IVersion;

/** Support four digit builds, which seem to be reasonably popular in nuget
 * 
 * @author Ken Duck
 *
 */
public class ExtendedSemanticVersion extends SemanticVersion {
	/**
	 * The trailing values are considered the tail
	 */
	private SemanticVersion tail;
	
	/**
	 * Dirty hack for four digit builds
	 */
	protected ExtendedSemanticVersion(int major, int minor, int patch, int build)
	{
		head = Version.forIntegers(major, minor, patch);
		tail = new SemanticVersion(build);
	}

	@Override
	public int getBuild() {
		return tail.getMajor();
	}

	@Override
	public boolean isStable() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof ExtendedSemanticVersion)
		{
			ExtendedSemanticVersion v = (ExtendedSemanticVersion)o;
			
			if (!this.head.equals(v.head)) {
				return false;
			}
			
			return this.tail.equals(v.tail);
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
		if(head != null)
		{
			return head.hashCode();
		}
		return super.hashCode();
	}
	
	@Override
	public int compareTo(IVersion other) {
		if(other instanceof ExtendedSemanticVersion)
		{
			ExtendedSemanticVersion sv = (ExtendedSemanticVersion)other;
			int diff = this.head.compareTo(sv.head);
			if (diff != 0) {
				return diff;
			}
			return this.tail.compareTo(sv.tail);
		} else if (other instanceof SemanticVersion) {
			SemanticVersion sv = (SemanticVersion)other;
			int diff = this.head.compareTo(sv.head);
			if (diff != 0) {
				return diff;
			}
			if (tail.getMajor() == 0) {
				return 0;
			}
			return 1;
		} else {
			// Fall back to simple string comparison
			return toString().compareTo(other.toString());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		// Currently we only support a single extra digit for extended semver,
		// we will have to support more soon.
		return head.toString() + "." + tail.getMajor();
	}
	
	public boolean lessThan(IVersion version) {
		// Can the versions be compared?
		if(!(version instanceof SemanticVersion)) {
			return false;
		}
		
		if (head.lessThan(((SemanticVersion)version).head)) {
			return true;
		}
		
		if (head.greaterThan(((SemanticVersion)version).head)) {
			return false;
		}
		
		if (version instanceof ExtendedSemanticVersion) {
			return tail.lessThan(((ExtendedSemanticVersion)version).tail);
		}
		
		return false;
	}

	public boolean lessThanOrEqualTo(IVersion version) {
		// Can the versions be compared?
		if(!(version instanceof SemanticVersion)) {
			return false;
		}
		
		// Less than automatically works
		if (head.lessThan(((SemanticVersion)version).head)) {
			return true;
		}
		
		if (head.greaterThan(((SemanticVersion)version).head)) {
			return false;
		}
		
		if (version instanceof ExtendedSemanticVersion) {
			return tail.lessThanOrEqualTo(((ExtendedSemanticVersion)version).tail);
		} else {
			// Equals is still possible
			if (tail.getMajor() == 0) {
				return true;
			}
		}
		return false;
	}

	public boolean greaterThan(IVersion version) {
		// Can the versions be compared?
		if(!(version instanceof SemanticVersion)) {
			return false;
		}
		
		if (head.greaterThan(((SemanticVersion)version).head)) {
			return true;
		}
		
		if (head.lessThan(((SemanticVersion)version).head)) {
			return false;
		}
		
		if (version instanceof ExtendedSemanticVersion) {
			return tail.greaterThan(((ExtendedSemanticVersion)version).tail);
		}
		
		return true;
	}

	public boolean greaterThanOrEqualTo(IVersion version) {
		// Can the versions be compared?
		if(!(version instanceof SemanticVersion)) {
			return false;
		}
		
		if (head.greaterThan(((SemanticVersion)version).head)) {
			return true;
		}
		
		if (head.lessThan(((SemanticVersion)version).head)) {
			return false;
		}
		
		if (version instanceof ExtendedSemanticVersion) {
			return tail.greaterThanOrEqualTo(((ExtendedSemanticVersion)version).tail);
		} else {
			// Equals is still possible
			if (tail.getMajor() == 0) {
				return true;
			}
		}
		return false;
	}
}
