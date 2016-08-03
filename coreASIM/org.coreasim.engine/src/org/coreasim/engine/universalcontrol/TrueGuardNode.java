package org.coreasim.engine.universalcontrol;
 
import org.coreasim.engine.interpreter.ASTNode;
import org.coreasim.engine.interpreter.Node;
import org.coreasim.engine.interpreter.ScannerInfo;


@SuppressWarnings("serial") 
public class TrueGuardNode extends ASTNode {

	public TrueGuardNode(Node parent) {
		super(
				UniversalControlPlugin.PLUGIN_NAME,
				ASTNode.EXPRESSION_CLASS,
				"",
				null,
				ScannerInfo.NO_INFO);
    	parent.addChild(this);
    	this.setParent(parent);
    }

}
