package org.coreasim.engine.plugins.foreachrule;

import java.util.HashMap;
import java.util.Map;

import org.coreasim.engine.CoreASIMError;
import org.coreasim.engine.interpreter.ASTNode;
import org.coreasim.engine.interpreter.ScannerInfo;

/** 
 *	A ForeachRuleNode represents a foreach rule.
 *   
 *  @author  Michael Stegmaier
 *  
 */
public class ForeachRuleNode extends ASTNode {

    /**
     * Creates a new ForeachRuleNode
     */
    public ForeachRuleNode(ScannerInfo info) {
        super(
        		ForeachRulePlugin.PLUGIN_NAME,
        		ASTNode.RULE_CLASS,
        		"ForeachRule",
        		null,
        		info);
    }

    public ForeachRuleNode(ForeachRuleNode node) {
    	super(node);
    }

    /**
     * Returns a map of the variable names to the nodes which
     * represent the domains that variable should be taken from
     * @throws CoreASIMError A CoreASMError is thrown if a variable is defined multiple times
     */
    public Map<String,ASTNode> getVariableMap() throws CoreASIMError {
    	Map<String,ASTNode> variableMap = new HashMap<String,ASTNode>();
        
        for (ASTNode current = getFirst(); current.getNext() != null && current.getNext().getNext() != null && ASTNode.ID_CLASS.equals(current.getGrammarClass()); current = current.getNext().getNext()) {
            if (variableMap.put(current.getToken(),current.getNext()) != null)
            	throw new CoreASIMError("Variable \""+current.getToken()+"\" already defined in foreach rule.", this);
        }
        
        return variableMap;
    }
    
    /**
     * Returns the node representing the 'do' part of the foreach rule.
     */
    public ASTNode getDoRule() {
        return (ASTNode)getChildNode("rule");   
    }
    
    /**
     * Returns the node representing the 'ifnone' part of the foreach rule.
     */
    public ASTNode getIfnoneRule() {
        return (ASTNode)getChildNode("ifnone");
    }
    
    /**
     * Returns the node representing the 'with' part of the foreach rule.
     * If there is no 'with' condition specified, null is returned.
     */
    public ASTNode getCondition() {
    	return (ASTNode)getChildNode("guard"); 
    }

}
