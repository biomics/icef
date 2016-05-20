/**
 * 
 */
package org.coreasm.engine.absstorage;

import org.coreasm.engine.interpreter.ScannerInfo;
import org.coreasm.engine.plugins.string.StringElement;

/** 
 * An Element to represent the action of creating an agent
 *   
 * @author Eric Rothstein
 * 
 */
public class AgentCreationElement extends Element {

	private StringElement name;
	private RuleElement initRule;
	private RuleElement program;
	private PolicyElement policy;
	private String signature;
	private Location location;
	private ScannerInfo scannerInfo;


	/**
	 * 
	 */
	public AgentCreationElement() {
		this.name = (StringElement) Element.UNDEF;
		this.initRule = (RuleElement)Element.UNDEF;
		this.program = (RuleElement) Element.UNDEF;
		this.policy = (PolicyElement) Element.UNDEF;
	}


	public AgentCreationElement(Element name, Element initRule, Element program, Element policy, String signature, Location loc, ScannerInfo scannerInfo) throws NameConflictException, IdentifierNotFoundException {
		if(!(name instanceof StringElement))
			throw new IdentifierNotFoundException("The name of the new agent must be a string");
		if (name.toString().equals("self"))
				throw new NameConflictException("Cannot create a new agent with identifier \"self\" ");
		if (! (initRule instanceof RuleElement))
			throw new IdentifierNotFoundException("Cannot use "+initRule.toString()+" as an initialization rule");
		if (! (program instanceof RuleElement))
			throw new IdentifierNotFoundException("Cannot use "+program.toString()+" as its main rule");
		if (! (policy instanceof PolicyElement))
			throw new IdentifierNotFoundException("Cannot use "+program.toString()+" as its scheduling policy");
		this.name = (StringElement) name;
		this.initRule = (RuleElement)initRule;
		this.program = (RuleElement) program;
		this.policy = (PolicyElement) policy;
		this.signature = signature;
		this.location = loc;
		this.scannerInfo = scannerInfo;
	}


	/**
	 * @return the scannerInfo
	 */
	public ScannerInfo getScannerInfo() {
		return scannerInfo;
	}


	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}


	/**
	 * @return the signature
	 */
	public String getSignature() {
		return signature;
	}


	/**
	 * @param policy the policy to set
	 */
	public void setPolicy(PolicyElement policy) {
		this.policy = policy;
	}


	/**
	 * @return the policy
	 */
	public PolicyElement getPolicy() {
		return policy;
	}


	/**
	 * @return the name
	 */
	public StringElement getName() {
		return name;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(StringElement name) {
		this.name = name;
	}


	/**
	 * @return the initRule
	 */
	public RuleElement getInitRule() {
		return initRule;
	}


	/**
	 * @param initRule the initRule to set
	 */
	public void setInitRule(RuleElement initRule) {
		this.initRule = initRule;
	}


	/**
	 * @return the program
	 */
	public RuleElement getProgram() {
		return program;
	}


	/**
	 * @param program the program to set
	 */
	public void setProgram(RuleElement program) {
		this.program = program;
	}

    public String toJSON() {
        String result = "{";
        if(!name.toString().equals(""))
            result += "\"name\" : \"" + name.toString().replace("\"", "\\\"") + "\",";
        result += "\"signature\" : \"" + signature.replace("\"", "\\\"") + "\\n\\n";
        result += initRule.getDeclarationNode().unparseTree().replace("\"", "\\\"") + "\\n\\n";
        result += program.getDeclarationNode().unparseTree().replace("\"", "\\\"") + "\\n\\n";
        result += policy.getDeclarationNode().unparseTree().replace("\"", "\\\"") + "\",";
        result += "\"init\" : \""+ initRule.getName() + "\",";
        result += "\"program\" : \""+ program.getName() + "\",";
        result += "\"policy\" : \""+ policy.getName() + "\",";
        result += "\"start\" : true }";

        return result;
    }
	
	@Override
	public String toString() {
		String result = "create agent \""+name+"\"";
		result +=" initialized by: "+initRule+"";
		result +="with program: "+program+""; 
		result +="and policy: "+policy+""; 
		return result;
	}

}
