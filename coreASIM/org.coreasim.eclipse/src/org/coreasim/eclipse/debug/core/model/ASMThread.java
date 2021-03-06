package org.coreasim.eclipse.debug.core.model;

import java.util.HashMap;

import org.coreasim.eclipse.engine.debugger.EngineDebugger;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;

/**
 * This class is needed by the eclipse debug framework. Incoming calls are redirected to the debug target.
 * @author Michael Stegmaier
 *
 */
public class ASMThread extends ASMDebugElement implements IThread {
	private HashMap<ASMStorage, IVariable[]> variables = new HashMap<ASMStorage, IVariable[]>();
	private HashMap<IStackFrame, ASMStorage> states = new HashMap<IStackFrame, ASMStorage>();

	public ASMThread(ASMDebugTarget debugTarget) {
		super(debugTarget);
	}
	
	public void cleanUp() {
		variables.clear();
		states.clear();
	}

	@Override
	public boolean canResume() {
		return getDebugTarget().canResume();
	}

	@Override
	public boolean canSuspend() {
		return getDebugTarget().canSuspend();
	}

	@Override
	public boolean isSuspended() {
		return getDebugTarget().isSuspended();
	}

	@Override
	public void resume() throws DebugException {
		getDebugTarget().resume();
	}

	@Override
	public void suspend() throws DebugException {
		getDebugTarget().suspend();
	}

	@Override
	public boolean canStepInto() {
		return isSuspended();
	}

	@Override
	public boolean canStepOver() {
		return isSuspended();
	}

	@Override
	public boolean canStepReturn() {
		return isSuspended();
	}

	@Override
	public boolean isStepping() {
		if (EngineDebugger.getRunningInstance()==null)
			return false;
		return EngineDebugger.getRunningInstance().isStepping();
	}

	@Override
	public void stepInto() throws DebugException {
		EngineDebugger.getRunningInstance().stepInto();
	}

	@Override
	public void stepOver() throws DebugException {
		EngineDebugger.getRunningInstance().stepOver();
	}

	@Override
	public void stepReturn() throws DebugException {
		EngineDebugger.getRunningInstance().stepReturn();
	}

	@Override
	public boolean canTerminate() {
		return getDebugTarget().canTerminate();
	}

	@Override
	public boolean isTerminated() {
		return getDebugTarget().isTerminated();
	}

	@Override
	public void terminate() throws DebugException {
		getDebugTarget().terminate();
	}

	@Override
	public IStackFrame[] getStackFrames() throws DebugException {
		if (isSuspended()) {
			ASMStorage[] states = EngineDebugger.getRunningInstance().getStates();
			IStackFrame[] frames = new IStackFrame[states.length];
			this.states.clear();
			
			for (int i = 0; i < frames.length; i++) {
				IStackFrame stackFrame = new ASMStackFrame(this, i);
				this.states.put(stackFrame, states[i]);
				frames[frames.length - 1 - i] = stackFrame;
			}
			
			return frames;
		}
		return new IStackFrame[0];
	}
	
	public void setVariables(ASMStorage state, IVariable[] variables) {
		this.variables.put(state, variables);
	}
	
	public IVariable[] getVariables(ASMStorage state) {
		return variables.get(state);
	}
	
	public ASMStorage getState(ASMStackFrame stackFrame) {
		return states.get(stackFrame);
	}

	@Override
	public boolean hasStackFrames() throws DebugException {
		return getTopStackFrame() != null;
	}

	@Override
	public int getPriority() throws DebugException {
		return 0;
	}

	@Override
	public IStackFrame getTopStackFrame() throws DebugException {
		IStackFrame[] frames = getStackFrames();
		if (frames.length > 0)
			return frames[0];
		return null;
	}

	@Override
	public String getName() throws DebugException {
		return "Thread [main]";
	}
	
	@Override
	public IBreakpoint[] getBreakpoints() {
		// TODO getBreakPoints()
		return null;
	}
}
