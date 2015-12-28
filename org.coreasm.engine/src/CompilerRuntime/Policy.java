package CompilerRuntime;

import java.util.concurrent.Callable;
import org.coreasm.engine.absstorage.Element;
import org.coreasm.engine.absstorage.PolicyBackgroundElement;
/**
 * An interface representing a CoreASM Policy
 * @author Markus Brenner
 *
 */
public abstract class Policy extends Element implements Callable<PolicyResult> {
	protected Element agent;
	protected java.util.ArrayList<CompilerRuntime.PolicyParam> params;
	protected CompilerRuntime.LocalStack localStack;
	protected CompilerRuntime.EvalStack evalStack;
	
	@Override
	public String getBackground(){
		return PolicyBackgroundElement.POLICY_BACKGROUND_NAME;
	}
	
	public void clearResults(){
		localStack = new CompilerRuntime.LocalStack();
		evalStack = new CompilerRuntime.EvalStack();
	}
	public void setAgent(Element a){
		this.agent = a;
	}
	public Element getAgent(){
		return this.agent;
	}
	public void initPolicy(java.util.ArrayList<CompilerRuntime.PolicyParam> params, CompilerRuntime.LocalStack ls){
		this.evalStack = new CompilerRuntime.EvalStack();
		this.params = new java.util.ArrayList<CompilerRuntime.PolicyParam>(params);
		if(ls == null){
			this.localStack = new CompilerRuntime.LocalStack();
		}
		else{
			this.localStack = ls;
		}
	}
	
	public Policy getPolicyResponsible(){
		return this;
	}
	
	public abstract Policy getCopy();
	
	public abstract int parameterCount();
}
