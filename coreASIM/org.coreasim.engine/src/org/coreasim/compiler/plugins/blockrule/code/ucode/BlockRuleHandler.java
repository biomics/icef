package org.coreasim.compiler.plugins.blockrule.code.ucode;

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
public class BlockRuleHandler implements CompilerCodeHandler {

	@Override
	public void compile(CodeFragment result, ASTNode node, CompilerEngine engine)
			throws CompilerException {
		
		CodeFragment tmpresult = new CodeFragment("");
		
		result.appendLine("");
		
		if(node.getAbstractChildNodes().size() <= 0) throw new CompilerException("empty BlockRule");
		
		for(int i = 0; i < node.getAbstractChildNodes().size(); i++){
			tmpresult.appendLine("//blockrule child " + i + " start\n");
			tmpresult.appendFragment(engine.compile(node.getAbstractChildNodes().get(i), CodeType.U));
			if(tmpresult.getByteCount() > 40000){
				tmpresult = CodeWrapperEntry.buildWrapper(tmpresult, "blockrulehandler", engine);
			}
		}
	
		tmpresult.appendLine("@decl(@RuntimePkg@.UpdateList,ulist)=new @RuntimePkg@.UpdateList();\n");
		
		tmpresult.appendLine("//blockrule collection handler\n");
		tmpresult.appendLine("for(@decl(int,i)=0; @i@ < " + node.getAbstractChildNodes().size() + "; @i@++){\n");
		tmpresult.appendLine("@ulist@.addAll((@RuntimePkg@.UpdateList)evalStack.pop());\n");
		tmpresult.appendLine("}\n");
		tmpresult.appendLine("evalStack.push(@ulist@);\n");
		
		result.appendFragment(tmpresult);
	}

}
