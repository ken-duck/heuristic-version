// Generated from /Users/twoducks/IdeaProjects/heuristic-version/src/main/java/net/ossindex/version/parser/Version.g4 by ANTLR 4.13.1
package net.ossindex.version.parser;


import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link VersionParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface VersionVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link VersionParser#range}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRange(VersionParser.RangeContext ctx);
	/**
	 * Visit a parse tree produced by {@link VersionParser#maven_ranges}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMaven_ranges(VersionParser.Maven_rangesContext ctx);
	/**
	 * Visit a parse tree produced by {@link VersionParser#maven_range}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMaven_range(VersionParser.Maven_rangeContext ctx);
	/**
	 * Visit a parse tree produced by {@link VersionParser#broken_range}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBroken_range(VersionParser.Broken_rangeContext ctx);
	/**
	 * Visit a parse tree produced by {@link VersionParser#range_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRange_type(VersionParser.Range_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link VersionParser#semantic_range}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSemantic_range(VersionParser.Semantic_rangeContext ctx);
	/**
	 * Visit a parse tree produced by {@link VersionParser#union_range}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnion_range(VersionParser.Union_rangeContext ctx);
	/**
	 * Visit a parse tree produced by {@link VersionParser#logical_range}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogical_range(VersionParser.Logical_rangeContext ctx);
	/**
	 * Visit a parse tree produced by {@link VersionParser#version_set}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVersion_set(VersionParser.Version_setContext ctx);
	/**
	 * Visit a parse tree produced by {@link VersionParser#simple_range}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimple_range(VersionParser.Simple_rangeContext ctx);
	/**
	 * Visit a parse tree produced by {@link VersionParser#version}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVersion(VersionParser.VersionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VersionParser#stream}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStream(VersionParser.StreamContext ctx);
	/**
	 * Visit a parse tree produced by {@link VersionParser#prefixed_version}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrefixed_version(VersionParser.Prefixed_versionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VersionParser#postfix_version}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPostfix_version(VersionParser.Postfix_versionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VersionParser#numeric_version}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumeric_version(VersionParser.Numeric_versionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VersionParser#sep}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSep(VersionParser.SepContext ctx);
	/**
	 * Visit a parse tree produced by {@link VersionParser#dot}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDot(VersionParser.DotContext ctx);
	/**
	 * Visit a parse tree produced by {@link VersionParser#named_version}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNamed_version(VersionParser.Named_versionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VersionParser#valid_named_version}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValid_named_version(VersionParser.Valid_named_versionContext ctx);
	/**
	 * Visit a parse tree produced by {@link VersionParser#identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentifier(VersionParser.IdentifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link VersionParser#numeric_segment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumeric_segment(VersionParser.Numeric_segmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link VersionParser#character_segment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCharacter_segment(VersionParser.Character_segmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link VersionParser#string}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitString(VersionParser.StringContext ctx);
	/**
	 * Visit a parse tree produced by {@link VersionParser#separator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSeparator(VersionParser.SeparatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link VersionParser#any}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAny(VersionParser.AnyContext ctx);
}