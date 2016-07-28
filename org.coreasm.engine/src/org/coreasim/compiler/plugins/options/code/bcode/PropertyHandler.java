package org.coreasim.compiler.plugins.options.code.bcode;

import org.coreasim.compiler.CompilerEngine;
import org.coreasim.compiler.codefragment.CodeFragment;
import org.coreasim.compiler.exception.CompilerException;
import org.coreasim.compiler.interfaces.CompilerCodeHandler;
import org.coreasim.engine.interpreter.ASTNode;
import org.coreasim.engine.plugins.options.OptionNode;

/**
 * Handles property creation
 * @author Spellmaker
 *
 */
public class PropertyHandler implements CompilerCodeHandler {	
	@Override
	public void compile(CodeFragment result, ASTNode node, CompilerEngine engine)
			throws CompilerException {
		OptionNode on = (OptionNode) node;
		String name = on.getOptionName();
		String value = on.getOptionValue();
		engine.getOptions().properties.put(name,value);
	}

}
