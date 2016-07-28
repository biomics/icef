package org.coreasim.compiler.plugins.conditionalrule.code.rcode;

import org.coreasim.compiler.CodeType;
import org.coreasim.compiler.CompilerEngine;
import org.coreasim.compiler.codefragment.CodeFragment;
import org.coreasim.compiler.exception.CompilerException;
import org.coreasim.compiler.interfaces.CompilerCodeHandler;
import org.coreasim.engine.interpreter.ASTNode;
import org.coreasim.engine.plugins.conditionalrule.ConditionalTermNode;

/**
 * Handles the conditional term
 * @author Spellmaker
 *
 */
public class ConditionalTermHandler implements CompilerCodeHandler {

	@Override
	public void compile(CodeFragment result, ASTNode node, CompilerEngine engine)
			throws CompilerException {
		if(!(node instanceof ConditionalTermNode)) throw new CompilerException("invalid node type in conditionalterm");
		ConditionalTermNode cond = (ConditionalTermNode) node;
		
		result.appendFragment(engine.compile(cond.getCondition(), CodeType.R));
		result.appendLine("if(@RuntimePkg@.BooleanElement.TRUE.equals(evalStack.pop())){\n");
		result.appendFragment(engine.compile(cond.getIfTerm(), CodeType.R));
		result.appendLine("}\n");
		result.appendLine("else{\n");
		if(cond.getElseTerm() == null)
			result.appendLine("evalStack.push(@RuntimePkg@.Element.UNDEF);\n");
		else
			result.appendFragment(engine.compile(cond.getElseTerm(), CodeType.R));
		result.appendLine("}\n");
	}

}
