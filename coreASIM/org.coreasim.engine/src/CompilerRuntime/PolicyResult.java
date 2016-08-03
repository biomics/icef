package CompilerRuntime;

import org.coreasim.engine.absstorage.Element;

public class PolicyResult {
	public TriggerList triggers;
	public Element value;
	
	public PolicyResult(TriggerList triggers, Element value){
		this.triggers = triggers;
		this.value = value;
	}
}
