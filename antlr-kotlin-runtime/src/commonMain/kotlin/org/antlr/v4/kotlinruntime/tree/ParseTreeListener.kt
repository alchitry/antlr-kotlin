// Copyright 2017-present Strumenta and contributors, licensed under Apache 2.0.
// Copyright 2024-present Strumenta and contributors, licensed under BSD 3-Clause.

package org.antlr.v4.kotlinruntime.tree

import org.antlr.v4.kotlinruntime.ParserRuleContext

public sealed interface AnyParseTreeListener

/**
 * This interface describes the minimal core of methods triggered by [ParseTreeWalker].
 *
 * ```
 * val walker = ParseTreeWalker()
 * walker.walk(myParseTreeListener, myParseTree) // Triggers events in your listener
 * ```
 *
 * If you want to trigger events in multiple listeners during a single
 * tree walk, you can use the `ParseTreeDispatcher` object available at
 * [antlr4#841](https://github.com/antlr/antlr4/issues/841)
 */
public interface ParseTreeListener : AnyParseTreeListener {
  public fun visitTerminal(node: TerminalNode)
  public fun visitErrorNode(node: ErrorNode)
  public fun enterEveryRule(ctx: ParserRuleContext)
  public fun exitEveryRule(ctx: ParserRuleContext)

  public fun asSuspend(): SuspendParseTreeListener = object : SuspendParseTreeListener {
    override suspend fun visitTerminal(node: TerminalNode) = this@ParseTreeListener.visitTerminal(node)
    override suspend fun visitErrorNode(node: ErrorNode) = this@ParseTreeListener.visitErrorNode(node)
    override suspend fun enterEveryRule(ctx: ParserRuleContext) = this@ParseTreeListener.enterEveryRule(ctx)
    override suspend fun exitEveryRule(ctx: ParserRuleContext) = this@ParseTreeListener.exitEveryRule(ctx)
  }
}

public interface SuspendParseTreeListener : AnyParseTreeListener {
  public suspend fun visitTerminal(node: TerminalNode)
  public suspend fun visitErrorNode(node: ErrorNode)
  public suspend fun enterEveryRule(ctx: ParserRuleContext)
  public suspend fun exitEveryRule(ctx: ParserRuleContext)
}