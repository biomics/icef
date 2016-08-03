package CompilerRuntime;

import java.util.List;

import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.absstorage.ElementList;
import org.coreasim.engine.absstorage.MapFunction;

public class ProgramFunction extends MapFunction {
	@Override
	public Element getValue(List<? extends Element> args) {
		ElementList el;
		if (args instanceof ElementList)
			el = (ElementList)args;
		else
			el = ElementList.create(args);
		Element temp = table.get(el);
		if (temp == null) 
			return defaultValue;
		else{
			if(temp instanceof CompilerRuntime.Rule){
				return ((CompilerRuntime.Rule) temp).getCopy();
			}
			return temp;
		}
	}

}
