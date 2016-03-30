/**
 * 
 */
package org.coreasm.engine.absstorage;

import org.coreasm.engine.plugins.string.StringElement;

/**
 * @author EricRothstein
 *
 */
public class AgentCreationElement extends Element {

	private StringElement name;
	private RuleElement initRule;
	private RuleElement program;


	/**
	 * 
	 */
	public AgentCreationElement() {
		this.name = (StringElement) Element.UNDEF;
		this.initRule = (RuleElement)Element.UNDEF;
		this.program = (RuleElement) Element.UNDEF;
	}


	public AgentCreationElement(Element name, Element initRule, Element program) throws NameConflictException, IdentifierNotFoundException {
		if(!(name instanceof StringElement))
			throw new IdentifierNotFoundException("The name of the new agent must be a string");
		if (name.toString().equals("self"))
				throw new NameConflictException("Cannot create a new agent with identifier \"self\" ");
		if (! (initRule instanceof RuleElement))
			throw new IdentifierNotFoundException("Cannot use "+initRule.toString()+" as an initialization rule");
		if (! (program instanceof RuleElement))
			throw new IdentifierNotFoundException("Cannot use "+program.toString()+" as a main rule");
		this.name = (StringElement) name;
		this.initRule = (RuleElement)initRule;
		this.program = (RuleElement) program;
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
	
	@Override
	public String toString() {
		String result = "create agent \""+name+"\"";
		result +=" initialized by: "+initRule+"";
		result +="with program: "+program+""; 
		return result;
	}

}
