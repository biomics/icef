package CompilerRuntime;

import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.absstorage.Location;

public interface RuleParam {	
	public Location evaluateL(CompilerRuntime.LocalStack localStack) throws Exception;
	public Element evaluateR(CompilerRuntime.LocalStack localStack) throws Exception;
	public Rule getUpdateResponsible();
	public void setParams(java.util.Map<String, CompilerRuntime.RuleParam> params);
}
