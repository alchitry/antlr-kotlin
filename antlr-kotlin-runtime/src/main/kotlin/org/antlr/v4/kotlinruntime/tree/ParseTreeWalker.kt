/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.kotlinruntime.tree

import org.antlr.v4.kotlinruntime.ParserRuleContext
import org.antlr.v4.kotlinruntime.RuleContext

object ParseTreeWalker {
    fun walk(listener: ParseTreeListener, t: ParseTree) {
        if (t is ErrorNode) {
            listener.visitErrorNode(t)
            return
        } else if (t is TerminalNode) {
            listener.visitTerminal(t)
            return
        }
        val r = t as RuleNode
        enterRule(listener, r)
        val n = r.childCount
        for (i in 0 until n) {
            walk(listener, r.getChild(i)!!)
        }
        exitRule(listener, r)
    }

    suspend fun walk(listener: SuspendParseTreeListener, t: ParseTree) {
        if (t is ErrorNode) {
            listener.visitErrorNode(t)
            return
        } else if (t is TerminalNode) {
            listener.visitTerminal(t)
            return
        }
        val r = t as RuleNode
        enterRule(listener, r)
        val n = r.childCount
        for (i in 0 until n) {
            walk(listener, r.getChild(i)!!)
        }
        exitRule(listener, r)
    }

    /**
     * The discovery of a rule node, involves sending two events: the generic
     * [ParseTreeListener.enterEveryRule] and a
     * [RuleContext]-specific event. First we trigger the generic and then
     * the rule specific. We to them in reverse order upon finishing the node.
     */
    fun enterRule(listener: ParseTreeListener, r: RuleNode) {
        val ctx = r.ruleContext as ParserRuleContext
        listener.enterEveryRule(ctx)
        ctx.enterRule(listener)
    }

    fun exitRule(listener: ParseTreeListener, r: RuleNode) {
        val ctx = r.ruleContext as ParserRuleContext
        ctx.exitRule(listener)
        listener.exitEveryRule(ctx)
    }

    suspend fun enterRule(listener: SuspendParseTreeListener, r: RuleNode) {
        val ctx = r.ruleContext as ParserRuleContext
        listener.enterEveryRule(ctx)
        ctx.enterRule(listener)
    }

    suspend fun exitRule(listener: SuspendParseTreeListener, r: RuleNode) {
        val ctx = r.ruleContext as ParserRuleContext
        ctx.exitRule(listener)
        listener.exitEveryRule(ctx)
    }
}
