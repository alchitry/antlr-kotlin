/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.kotlinruntime.tree

import org.antlr.v4.kotlinruntime.ParserRuleContext

/** This interface describes the minimal core of methods triggered
 * by [ParseTreeWalker]. E.g.,
 *
 * ParseTreeWalker walker = new ParseTreeWalker();
 * walker.walk(myParseTreeListener, myParseTree); <-- triggers events in your listener
 *
 * If you want to trigger events in multiple listeners during a single
 * tree walk, you can use the ParseTreeDispatcher object available at
 *
 * https://github.com/antlr/antlr4/issues/841
 */
interface ParseTreeListener {
    fun visitTerminal(node: TerminalNode)
    fun visitErrorNode(node: ErrorNode)
    fun enterEveryRule(ctx: ParserRuleContext)
    fun exitEveryRule(ctx: ParserRuleContext)

    fun asSuspend(): SuspendParseTreeListener = object : SuspendParseTreeListener {
        override suspend fun visitTerminal(node: TerminalNode) = this@ParseTreeListener.visitTerminal(node)
        override suspend fun visitErrorNode(node: ErrorNode) = this@ParseTreeListener.visitErrorNode(node)
        override suspend fun enterEveryRule(ctx: ParserRuleContext) = this@ParseTreeListener.enterEveryRule(ctx)
        override suspend fun exitEveryRule(ctx: ParserRuleContext) = this@ParseTreeListener.exitEveryRule(ctx)
    }
}

interface SuspendParseTreeListener {
    suspend fun visitTerminal(node: TerminalNode)
    suspend fun visitErrorNode(node: ErrorNode)
    suspend fun enterEveryRule(ctx: ParserRuleContext)
    suspend fun exitEveryRule(ctx: ParserRuleContext)
}
