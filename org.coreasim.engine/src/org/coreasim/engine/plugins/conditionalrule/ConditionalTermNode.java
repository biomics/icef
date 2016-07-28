package org.coreasim.engine.plugins.conditionalrule;

import org.coreasim.engine.interpreter.ASTNode;
import org.coreasim.engine.interpreter.ScannerInfo;

/**
 * A node representing a conditional term
 * @author Michael Stegmaier
 *
 */
@SuppressWarnings("serial")
public class ConditionalTermNode extends ASTNode {

	public ConditionalTermNode(ScannerInfo info) {
		super(ConditionalRulePlugin.PLUGIN_NAME, ASTNode.EXPRESSION_CLASS, "ConditionalTerm", null, info);
	}

    public ConditionalTermNode(ConditionalTermNode node) {
    	super(node);
    }
    
    public ASTNode getCondition() {
        return getFirst();
    }
    
    public ASTNode getIfTerm() {
        return getCondition().getNext();
    }
    
    public ASTNode getElseTerm() {
        return getIfTerm().getNext();
    }
}
