package org.coreasim.compiler.plugins.kernelextensions.code.ucode;

import org.coreasim.compiler.CodeType;
import org.coreasim.compiler.CompilerEngine;
import org.coreasim.compiler.codefragment.CodeFragment;
import org.coreasim.compiler.exception.CompilerException;
import org.coreasim.compiler.interfaces.CompilerCodeHandler;
import org.coreasim.engine.interpreter.ASTNode;
import org.coreasim.engine.plugins.kernelextensions.ExtendedFunctionRulePolicyTermNode;

/**
 * Handles ExtendedFunctionRulePolicyTerms
 * @author Spellmaker
 *
 */
public class CompilerExtendedFunctionRulePolicyTermHandler implements
		CompilerCodeHandler {

	@Override
	public void compile(CodeFragment result, ASTNode node, CompilerEngine engine)
			throws CompilerException {
		if(!(node instanceof ExtendedFunctionRulePolicyTermNode)) throw new CompilerException("expected astnode of type ExtendedFunctionRulePolicyTermNode");
		ExtendedFunctionRulePolicyTermNode n = (ExtendedFunctionRulePolicyTermNode) node;
		result.appendFragment(engine.compile(n.getTerm(), CodeType.R));
		result.appendLine("@decl(Object,o)=evalStack.pop();\n");
		result.appendLine("if(!(@o@ instanceof @RuntimePkg@.FunctionElement)){\n");
		result.appendLine("throw new Exception(\"cannot handle an extended rule call on a non-function element\");\n");
		result.appendLine("}\n");
		result.appendLine("@decl(@RuntimePkg@.FunctionElement,function)=(@RuntimePkg@.FunctionElement) @o@;\n");
		result.appendLine("@decl(java.util.LinkedList<@RuntimePkg@.Element>,params)=new java.util.LinkedList<@RuntimePkg@.Element>();\n");
		for(ASTNode a : n.getArguments()){
			result.appendFragment(engine.compile(a, CodeType.R));
			result.appendLine("@params@.add((@RuntimePkg@.Element)evalStack.pop());\n");
		}
		result.appendLine("@decl(@RuntimePkg@.ElementList,plist)=new @RuntimePkg@.ElementList(@params@);\n");
		result.appendLine("@decl(String,fname)=@RuntimeProvider@.getStorage().getFunctionName(@function@);\n");
		result.appendLine("if(@fname@ != null){\n");
		result.appendLine("evalStack.push(@RuntimeProvider@.getStorage().getValue(new @RuntimePkg@.Location(@fname@, @plist@)));\n");
		result.appendLine("} else {\n");
		result.appendLine("evalStack.push(@function@.getValue(@plist@));\n");
		result.appendLine("}\n");
	}

}
