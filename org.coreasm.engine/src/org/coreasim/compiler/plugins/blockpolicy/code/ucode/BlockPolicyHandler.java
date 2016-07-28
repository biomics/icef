package org.coreasim.compiler.plugins.blockpolicy.code.ucode;

import org.coreasim.compiler.CodeType;
import org.coreasim.compiler.CompilerEngine;
import org.coreasim.compiler.codefragment.CodeFragment;
import org.coreasim.compiler.components.classlibrary.CodeWrapperEntry;
import org.coreasim.compiler.exception.CompilerException;
import org.coreasim.compiler.interfaces.CompilerCodeHandler;
import org.coreasim.engine.interpreter.ASTNode;

/**
 * Handles the block rule.
 * The implementation tries to ensure that the produced code never exceeds java limits
 * by using {@link CodeWrapperEntry}
 * @author Spellmaker
 *
 */
public class BlockPolicyHandler implements CompilerCodeHandler {

	@Override
	public void compile(CodeFragment result, ASTNode node, CompilerEngine engine)
			throws CompilerException {
		
		CodeFragment tmpresult = new CodeFragment("");
		
		result.appendLine("");
		
		if(node.getAbstractChildNodes().size() <= 0) throw new CompilerException("empty BlockPolicy");
		
		for(int i = 0; i < node.getAbstractChildNodes().size(); i++){
			tmpresult.appendLine("//blockpolicy child " + i + " start\n");
			tmpresult.appendFragment(engine.compile(node.getAbstractChildNodes().get(i), CodeType.U));
			if(tmpresult.getByteCount() > 40000){
				tmpresult = CodeWrapperEntry.buildWrapper(tmpresult, "blockpolicyhandler", engine);
			}
		}
	
		tmpresult.appendLine("@decl(@RuntimePkg@.TriggerList,tlist)=new @RuntimePkg@.TriggerList();\n");
		
		tmpresult.appendLine("//blockpolicy collection handler\n");
		tmpresult.appendLine("for(@decl(int,i)=0; @i@ < " + node.getAbstractChildNodes().size() + "; @i@++){\n");
		tmpresult.appendLine("@tlist@.addAll((@RuntimePkg@.TriggerList)evalStack.pop());\n");
		tmpresult.appendLine("}\n");
		tmpresult.appendLine("evalStack.push(@tlist@);\n");
		
		result.appendFragment(tmpresult);
	}

}
