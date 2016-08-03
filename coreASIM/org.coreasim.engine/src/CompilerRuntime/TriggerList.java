package CompilerRuntime;

import java.util.ArrayList;
import java.util.Collection;

import org.coreasim.engine.absstorage.Trigger;

/**
 * A list of agents
 * @author Eric Rothstein
 *
 */
public class TriggerList extends ArrayList<Trigger>{
	private static final long serialVersionUID = 1L;
	
	public TriggerList(){
		super();
	}
	
	public TriggerList(Collection<Trigger> set) {
		super(set);
	}
	
	public TriggerList(Trigger t){
		super();
		this.add(t);
	}

	@Override
	public String toString(){
		String s = "TriggerList\n";
		for(int i = 0; i < this.size(); i++){
			s = s + "(" + this.get(i).toString() + ")\n";
		}
		return s;
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof TriggerList){
			TriggerList tl = (TriggerList)o;
			if(tl.size() == this.size()){
				for(int i = 0; i < this.size(); i++){
					if(!this.get(i).equals(tl.get(i))) return false;
				}
				return true;
			}
		}
		return false;
	}
}
