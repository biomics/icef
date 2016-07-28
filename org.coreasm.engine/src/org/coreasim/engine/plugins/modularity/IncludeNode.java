package org.coreasim.engine.plugins.modularity;

import org.coreasim.engine.interpreter.ASTNode;
import org.coreasim.engine.interpreter.ScannerInfo;

@SuppressWarnings("serial")
public class IncludeNode extends ASTNode {
	public IncludeNode(IncludeNode node){
		super(node);
	}
	
	public IncludeNode(ScannerInfo scannerInfo) {
		super(ModularityPlugin.PLUGIN_NAME, ASTNode.DECLARATION_CLASS, "Include", null, scannerInfo);
	}
	
	public String getFilename() {
		return getFirst().getToken();
	}
}