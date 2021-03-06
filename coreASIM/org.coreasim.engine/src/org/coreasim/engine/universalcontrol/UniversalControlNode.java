/*
 * UniversalControlNode.java
 *   
 * Copyright (C) 2006-2016 The CoreASM Team
 * Licensed under the Academic Free License version 3.0
 *   http://www.opensource.org/licenses/afl-3.0.php
 *   http://www.coreasm.org/afl-3.0.php
 * 
 * This file contains source code developed by the European FP7 research project BIOMICS (Grant no. 318202)
 * Copyright (C) 2016 Daniel Schreckling, Eric Rothstein (BIOMICS)
 * Licensed under the Academic Free License version 3.0 
 *   http://www.opensource.org/licenses/afl-3.0.php
 *
 */

package org.coreasim.engine.universalcontrol;
   
import org.coreasim.engine.interpreter.ASTNode;
import org.coreasim.engine.interpreter.Node;
import org.coreasim.engine.interpreter.ScannerInfo;

/**
 * A node representing the Universal Control Construct
 * @author Michael Stegmaier 
 * 
 */
@SuppressWarnings("serial")
public class UniversalControlNode extends ASTNode {
	private ASTNode trueGuard;
	
	public UniversalControlNode(ScannerInfo info) {
		super(UniversalControlPlugin.PLUGIN_NAME, ASTNode.RULE_CLASS, "UniversalControlRule", null, info);
	}

	public UniversalControlNode(UniversalControlNode node) {
		super(node);
	}

	public Node getRepetitionNode() {
		Node prevChild = null;
		for (Node child = getFirstCSTNode(); child != null; child = child.getNextCSTNode()) {
			if (UniversalControlPlugin.KEYWORD_FOREVER.equals(child.getToken()))
				return null;
			if (UniversalControlPlugin.KEYWORD_ONCE.equals(child.getToken())
			|| 	UniversalControlPlugin.KEYWORD_NO_UPDATES.equals(child.getToken())
			||	UniversalControlPlugin.KEYWORD_FIXPOINT.equals(child.getToken()))
				return child;
			if (UniversalControlPlugin.KEYWORD_TIMES.equals(child.getToken()))
				return prevChild;
			prevChild = child;
		}
		return null;
	}

	public ASTNode getResetCondition() {
		for (Node child = getFirstCSTNode(); child != null; child = child.getNextCSTNode()) {
			if (UniversalControlPlugin.KEYWORD_RESETTING.equals(child.getToken()))
				return (ASTNode)child.getNextCSTNode().getNextCSTNode();
		}
		return null;
	}
	
	public boolean isRuleByRule() {
		for (Node child = getFirstCSTNode(); child != null; child = child.getNextCSTNode()) {
			if (UniversalControlPlugin.KEYWORD_SEQUENCE.equals(child.getToken()))
				return false;
			if (UniversalControlPlugin.KEYWORD_PARALLEL.equals(child.getToken()))
				return false;
			if (UniversalControlPlugin.KEYWORD_RULE_BY_RULE.equals(child.getToken()))
				return true;
			if (UniversalControlPlugin.KEYWORD_STEPWISE.equals(child.getToken()))
				return false;
		}
		return false;
	}
	
	public boolean isStepwise() {
		for (Node child = getFirstCSTNode(); child != null; child = child.getNextCSTNode()) {
			if (UniversalControlPlugin.KEYWORD_SEQUENCE.equals(child.getToken()))
				return false;
			if (UniversalControlPlugin.KEYWORD_PARALLEL.equals(child.getToken()))
				return false;
			if (UniversalControlPlugin.KEYWORD_RULE_BY_RULE.equals(child.getToken()))
				return false;
			if (UniversalControlPlugin.KEYWORD_STEPWISE.equals(child.getToken()))
				return true;
		}
		return false;
	}

	public boolean isSequential() {
		for (Node child = getFirstCSTNode(); child != null; child = child.getNextCSTNode()) {
			if (UniversalControlPlugin.KEYWORD_SEQUENCE.equals(child.getToken()))
				return true;
			if (UniversalControlPlugin.KEYWORD_PARALLEL.equals(child.getToken()))
				return false;
			if (UniversalControlPlugin.KEYWORD_RULE_BY_RULE.equals(child.getToken()))
				return false;
			if (UniversalControlPlugin.KEYWORD_STEPWISE.equals(child.getToken()))
				return false;
		}
		return false;
	}

	public ASTNode getCondition() {
		for (Node child = getFirstCSTNode(); child != null; child = child.getNextCSTNode()) {
			if (UniversalControlPlugin.KEYWORD_IF.equals(child.getToken()) || UniversalControlPlugin.KEYWORD_WHILE.equals(child.getToken()))
				return (ASTNode)child.getNextCSTNode();
			if (UniversalControlPlugin.KEYWORD_ITERATE.equals(child.getToken())) {
				if (trueGuard == null)
					trueGuard = new TrueGuardNode(this);
				return trueGuard;
			}
		}
		return null;
	}

	public boolean shouldLoop() {
		for (Node child = getFirstCSTNode(); child != null; child = child.getNextCSTNode()) {
			if (UniversalControlPlugin.KEYWORD_IF.equals(child.getToken()) || UniversalControlPlugin.KEYWORD_WHILE.equals(child.getToken()) || UniversalControlPlugin.KEYWORD_ITERATE.equals(child.getToken()))
				return UniversalControlPlugin.KEYWORD_WHILE.equals(child.getToken()) || UniversalControlPlugin.KEYWORD_ITERATE.equals(child.getToken());
		}
		return false;
	}
	
	public boolean isIterate() {
		for (Node child = getFirstCSTNode(); child != null; child = child.getNextCSTNode()) {
			if (UniversalControlPlugin.KEYWORD_IF.equals(child.getToken()) || UniversalControlPlugin.KEYWORD_WHILE.equals(child.getToken()) || UniversalControlPlugin.KEYWORD_ITERATE.equals(child.getToken()))
				return UniversalControlPlugin.KEYWORD_ITERATE.equals(child.getToken());
		}
		return false;
	}

	public boolean isVariableSelection() {
		for (Node child = getFirstCSTNode(); child != null; child = child.getNextCSTNode()) {
			if (UniversalControlPlugin.KEYWORD_VARIABLE.equals(child.getToken()) || UniversalControlPlugin.KEYWORD_FIXED.equals(child.getToken()))
				return UniversalControlPlugin.KEYWORD_VARIABLE.equals(child.getToken());
		}
		return true;
	}

	public String getSelectionKeyword() {
		for (Node child = getFirstCSTNode(); child != null; child = child.getNextCSTNode()) {
			if (UniversalControlPlugin.KEYWORD_ALL.equals(child.getToken()) || UniversalControlPlugin.KEYWORD_ANY.equals(child.getToken()) || UniversalControlPlugin.KEYWORD_SINGLE.equals(child.getToken()))
				return child.getToken();
		}
		return UniversalControlPlugin.KEYWORD_ALL;
	}
	
	public boolean isNonEmptySelection() {
		for (Node child = getFirstCSTNode(); child != null; child = child.getNextCSTNode()) {
			if (UniversalControlPlugin.KEYWORD_NON_EMPTY.equals(child.getToken()))
				return true;
		}
		return false;
	}

	public ASTNode getRuleBlock() {
		ASTNode ruleBlock = getFirst();
		while (ruleBlock == getCondition() || ruleBlock == getResetCondition() || ruleBlock == getRepetitionNode())
			ruleBlock = ruleBlock.getNext();
		return ruleBlock;
	}
}
