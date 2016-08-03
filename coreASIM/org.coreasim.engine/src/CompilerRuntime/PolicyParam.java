package CompilerRuntime;

import org.coreasim.engine.absstorage.Element;

public interface PolicyParam {	
	public Element evaluateTrigger(CompilerRuntime.LocalStack localStack) throws Exception;
	public Policy getTriggerResponsible();
	public void setParams(java.util.Map<String, CompilerRuntime.PolicyParam> params);
}
