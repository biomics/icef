package CompilerRuntime;

import org.coreasim.engine.absstorage.Element;

public class RuleResult {
	public UpdateList updates;
	public Element value;
	
	public RuleResult(UpdateList updates, Element value){
		this.updates = updates;
		this.value = value;
	}
}
