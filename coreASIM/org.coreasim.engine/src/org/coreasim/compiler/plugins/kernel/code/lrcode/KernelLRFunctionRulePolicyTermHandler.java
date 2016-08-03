package org.coreasim.compiler.plugins.kernel.code.lrcode;

import org.coreasim.compiler.CodeType;
import org.coreasim.compiler.CompilerEngine;
import org.coreasim.compiler.codefragment.CodeFragment;
import org.coreasim.compiler.exception.CompilerException;
import org.coreasim.compiler.interfaces.CompilerCodeHandler;
import org.coreasim.engine.interpreter.ASTNode;
import org.coreasim.engine.interpreter.FunctionRulePolicyTermNode;

/**
 * Handles l-r code for f(t1, t2, ... tn)
 * @author Spellmaker
 *
 */
public class KernelLRFunctionRulePolicyTermHandler implements CompilerCodeHandler {

	@Override
	public void compile(CodeFragment result, ASTNode node, CompilerEngine engine)
			throws CompilerException {
		// evaluate the right side of an expression
		// and push the location of the expression and the value to the
		// stack
		
		//TODO: Maybe it should be considered, that the name could be the name of a rule parameter evaluating to a location?

		result.appendLine("");
		FunctionRulePolicyTermNode frtn = (FunctionRulePolicyTermNode) node;

		result.appendLine("@decl(java.util.List<@RuntimePkg@.Element>,args)=new java.util.ArrayList<@RuntimePkg@.Element>();\n");

		if (frtn.hasArguments()) {
			for (ASTNode child : frtn.getArguments()) {
				result.appendFragment(engine.compile(child,
						CodeType.R));
				result.appendLine("@args@.add((@RuntimePkg@.Element)evalStack.pop());\n");
			}
		}

		result.appendLine("@decl(@RuntimePkg@.Location,loc)=new @RuntimePkg@.Location(\""
				+ frtn.getName() + "\", @args@);\n");
		result.appendLine("evalStack.push(@loc@);\n");
		result.appendLine("evalStack.push(@RuntimeProvider@.getStorage().getValue(@loc@));\n");
	}

}
