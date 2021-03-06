package org.coreasim.eclipse.engine.debugger;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.coreasim.eclipse.debug.core.model.ASMStorage;
import org.coreasim.eclipse.debug.ui.views.ASMUpdate;
import org.coreasim.engine.ControlAPI;
import org.coreasim.engine.CoreASIMError;
import org.coreasim.engine.CoreASIMWarning;
import org.coreasim.engine.EngineObserver;
import org.coreasim.engine.InconsistentUpdateSetException;
import org.coreasim.engine.Specification;
import org.coreasim.engine.VersionInfo;
import org.coreasim.engine.absstorage.AbstractStorage;
import org.coreasim.engine.absstorage.AgentCreationElement;
import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.absstorage.InvalidLocationException;
import org.coreasim.engine.absstorage.MessageElement;
import org.coreasim.engine.absstorage.RuleElement;
import org.coreasim.engine.absstorage.State;
import org.coreasim.engine.absstorage.Update;
import org.coreasim.engine.absstorage.UpdateMultiset;
import org.coreasim.engine.interpreter.ASTNode;
import org.coreasim.engine.interpreter.Interpreter;
import org.coreasim.engine.interpreter.InterpreterException;
import org.coreasim.engine.interpreter.InterpreterImp;
import org.coreasim.engine.interpreter.InterpreterListener;
import org.coreasim.engine.interpreter.Node;
import org.coreasim.engine.mailbox.Mailbox;
import org.coreasim.engine.parser.OperatorRegistry;
import org.coreasim.engine.parser.Parser;
import org.coreasim.engine.plugin.Plugin;
import org.coreasim.engine.plugin.PluginServiceInterface;
import org.coreasim.engine.plugin.ServiceProvider;
import org.coreasim.engine.plugin.ServiceRequest;
import org.coreasim.engine.scheduler.Scheduler;

public class WatchExpressionAPI implements ControlAPI {
	private ASMStorage storage;
	private ControlAPI capi;
	private CoreASIMError lastError = null;
	private List<CoreASIMWarning> warnings = new ArrayList<CoreASIMWarning>();

	public WatchExpressionAPI(ControlAPI capi) {
		this.capi = capi;
	}
	
	public synchronized Element evaluateExpression(ASTNode expression, Element agent, ASMStorage storage) throws InterpreterException {
		this.storage = storage;
		copyOprRegFromCapi();
		
		Interpreter interpreter = new InterpreterImp(this);
		interpreter.cleanUp();
		
		for (Entry<String, Element> environmentVariable : storage.getEnvVars().entrySet())
			interpreter.addEnv(environmentVariable.getKey(), environmentVariable.getValue());
		
		bindPlugins();
		storage.applyStackedUpdates();
		
		try {
			if (!(storage.getChosenProgram(agent) instanceof RuleElement))
				throw new InterpreterException("The program of agent '" + agent + "' is not a rule but " + storage.getChosenProgram(agent) + " instead.");
			interpreter.setSelf(agent);
			interpreter.setPosition(expression);
			
			lastError = null;
		
			do {
				interpreter.executeTree();
			} while (!(interpreter.isExecutionComplete() || hasErrorOccurred()));
		}
		finally {
			interpreter.dispose();
			unbindPlugins();
			storage.discardStackedUpdates();
			OperatorRegistry.removeInstance(this);
		}
		
		if (hasErrorOccurred())
			throw new InterpreterException(lastError);
		
		return expression.getValue();
	}
	
	public void dispose() {
		capi = null;
		storage = null;
		lastError = null;
		warnings = null;
	}
	
	private void bindPlugins() {
		for (Plugin plugin : getPlugins())
			plugin.setControlAPI(this);
	}
	
	private void unbindPlugins() {
		for (Plugin plugin : getPlugins())
			plugin.setControlAPI(capi);
	}
	
	private void copyOprRegFromCapi() {
		OperatorRegistry oprRegCapi = OperatorRegistry.getInstance(capi);
		OperatorRegistry oprReg = OperatorRegistry.getInstance(this);
		oprReg.binOps.clear();
    	oprReg.binOps.putAll(oprRegCapi.binOps);
    	oprReg.unOps.clear();
    	oprReg.unOps.putAll(oprRegCapi.unOps);
    	oprReg.indexOps.clear();
    	oprReg.indexOps.putAll(oprRegCapi.indexOps);
	}
	
	@Override
	public void initialize() {
	}

	@Override
	public void terminate() {
	}

	@Override
	public void recover() {
	}

	@Override
	public void loadSpecification(String specFileName) {
	}

	@Override
	public void loadSpecification(Reader src) {
	}

	@Override
	public void loadSpecification(String name, Reader src) {
	}

	@Override
	public void parseSpecification(String specFileName) {
	}

	@Override
	public void parseSpecification(Reader src) {
	}

	@Override
	public void parseSpecification(String name, Reader src) {
	}

	@Override
	@Deprecated
	public void parseSpecificationHeader(String specFileName) {
	}

	@Override
	public void parseSpecificationHeader(String specFileName, boolean loadPlugins) {
	}

	@Override
	@Deprecated
	public void parseSpecificationHeader(Reader src) {
	}

	@Override
	public void parseSpecificationHeader(Reader src, boolean loadPlugins) {
	}

	@Override
	@Deprecated
	public void parseSpecificationHeader(String name, Reader src) {
	}

	@Override
	public void parseSpecificationHeader(String name, Reader src, boolean loadPlugins) {
	}

	@Override
	public Specification getSpec() {
		return capi.getSpec();
	}
	
	@Override
	public State getState() {
		return storage;
	}

	@Override
	public State getPrevState(int i) {
		return capi.getPrevState(i);
	}

	@Override
	public Set<Update> getUpdateSet(int i) {
		if (i == 0)
			return ASMUpdate.unwrap(storage.getUpdates());
		else
			return null;
	}

	@Override
	public UpdateMultiset getUpdateInstructions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateState(Set<Update> update) throws InconsistentUpdateSetException, InvalidLocationException {
		// TODO implementation for updateState(..)
	}

	@Override
	public Set<? extends Element> getAgentSet() {
		return storage.getAgents();
	}

	@Override
	public Properties getProperties() {
		return capi.getProperties();
	}

	@Override
	public void setProperties(Properties newProperties) {
	}

	@Override
	public String getProperty(String property) {
		return capi.getProperty(property);
	}

	@Override
	public String getProperty(String property, String defaultValue) {
		return capi.getProperty(property, defaultValue);
	}

	@Override
	public boolean propertyHolds(String property) {
		return capi.propertyHolds(property);
	}

	@Override
	public void setProperty(String property, String value) {
	}

	@Override
	public EngineMode getEngineMode() {
//		TODO: check
		return capi.getEngineMode();
	}

	@Override
	public PluginServiceInterface getPluginInterface(String pName) {
		return capi.getPluginInterface(pName);
	}

	@Override
	public void hardInterrupt() {
	}

	@Override
	public void softInterrupt() {
	}

	@Override
	public void step() {
	}

	@Override
	public void run(int i) {
	}

	@Override
	public void addObserver(EngineObserver observer) {
		// TODO Auto-generated method stub
	}

	@Override
	public void removeObserver(EngineObserver observer) {
		// TODO Auto-generated method stub
	}

	@Override
	public Collection<EngineObserver> getObservers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Deprecated
	public void waitForIdleOrError() {
	}

	@Override
	public void waitWhileBusy() {
	}
	

	public void waitWhileBusyOrUntilCreation() {
	}

	@Override
	public boolean isBusy() {
		return capi.isBusy();
	}

	@Override
	public Set<? extends Element> getLastSelectedAgents() {
		return storage.getLastSelectedAgents();
	}

	@Override
	public ClassLoader getClassLoader() {
		return capi.getClassLoader();
	}

	@Override
	public void setClassLoader(ClassLoader classLoader) {
	}

	@Override
	public Map<String, VersionInfo> getPluginsVersionInfo() {
		return capi.getPluginsVersionInfo();
	}

	@Override
	public int getStepCount() {
		return storage.getStep();
	}

	@Override
	public List<CoreASIMWarning> getWarnings() {
		return warnings;
	}

	@Override
	public VersionInfo getVersionInfo() {
		return capi.getVersionInfo();
	}

	@Override
	public void addServiceProvider(String type, ServiceProvider provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void removeServiceProvider(String type, ServiceProvider provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public Set<ServiceProvider> getServiceProviders(String type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> serviceCall(ServiceRequest sr, boolean withResults) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addInterpreterListener(InterpreterListener listener) {

	}

	@Override
	public void removeInterpreterListener(InterpreterListener listener) {

	}

	@Override
	public List<InterpreterListener> getInterpreterListeners() {
		return Collections.emptyList();
	}

	@Override
	public Scheduler getScheduler() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractStorage getStorage() {
		return storage;
	}

	@Override
	public Interpreter getInterpreter() {
		/// TODO check
		return capi.getInterpreter();
	}

	@Override
	public Parser getParser() {
		/// TODO check
		return capi.getParser();
	}

	@Override
	public Plugin getPlugin(String name) {
		return capi.getPlugin(name);
	}

	@Override
	public Set<Plugin> getPlugins() {
		return capi.getPlugins();
	}

	@Override
	public void error(String msg) {
		error(msg, null, null);
	}

	@Override
	public void error(Throwable e) {
		error(e, null, null);
	}

	@Override
	public void error(String msg, Node errorNode, Interpreter interpreter) {
		CoreASIMError error; 
		if (interpreter != null)
			error = new CoreASIMError(msg, interpreter.getCurrentCallStack(), errorNode);
		else
			error = new CoreASIMError(msg, errorNode);
		this.error(error);
	}

	@Override
	public void error(Throwable e, Node errorNode, Interpreter interpreter) {
		CoreASIMError error; 
		if (interpreter != null)
			error = new CoreASIMError(e, interpreter.getCurrentCallStack(), errorNode);
		else
			error = new CoreASIMError(e, null, errorNode);
		this.error(error);
	}

	@Override
	public void error(CoreASIMError e) {
		if (lastError != null)
			return;
		
		lastError = e;

		e.setContext(getParser(), getSpec());
	}

	@Override
	public void warning(String src, String msg) {
		warning(src, msg, null, null);
	}

	@Override
	public void warning(String src, Throwable e) {
		warning(src, e, null, null);
	}

	@Override
	public void warning(String src, String msg, Node node, Interpreter interpreter) {
		CoreASIMWarning warning; 
		if (interpreter != null)
			warning = new CoreASIMWarning(src, msg, interpreter.getCurrentCallStack(), node);
		else
			warning = new CoreASIMWarning(src, msg, node);
		this.warning(warning);
	}

	@Override
	public void warning(String src, Throwable e, Node node, Interpreter interpreter) {
		CoreASIMWarning warning; 
		if (interpreter != null)
			warning = new CoreASIMWarning(src, e, interpreter.getCurrentCallStack(), node);
		else
			warning = new CoreASIMWarning(src, e, null, node);
		this.warning(warning);
	}

	@Override
	public void warning(CoreASIMWarning w) {
		w.setContext(getParser(), getSpec());
		warnings.add(w);
	}

	@Override
	public boolean hasErrorOccurred() {
		return lastError != null;
	}

	@Override
	public CoreASIMError getError() {
		return lastError;
	}

	@Override
	public Mailbox getMailbox() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void reportNewAgents(Map<String, String> agents) {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public Map<String, AgentCreationElement> getAgentsToCreate() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> getAgentsToRegister() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> getAgentsToDeregister() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> getAgentsToDelete() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<? extends Element> getASIMSet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addASIMs(Set<String> asims) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteASIMs(Set<String> asims) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setSelfName(String name) {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public String getSelfAgentName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clearOutboxLocation() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<MessageElement> emptyOutbox() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void fillInBox(Set<MessageElement> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<String> getAgentsToDestroy() {
		// TODO Auto-generated method stub
		return null;
	}

}
