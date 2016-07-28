/**
 * 
 */
package org.coreasim.eclipse.engine;

import org.coreasim.eclipse.CoreASIMPlugin;
import org.coreasim.eclipse.preferences.PreferenceConstants;
import org.coreasim.engine.CoreASIMEngine;
import org.coreasim.engine.EngineProperties;
import org.coreasim.util.CoreASMGlobal;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * A CoreASM Engine Factory for the Eclipse plugin.
 * 
 * @author Roozbeh Farahbod
 *
 */
public class CoreASMEngineFactory {

	/**
	 * Creates an instance of CoreASM Engine configured for the 
	 * CoreASM Eclipse plugin.
	 */
	public static CoreASIMEngine createCoreASMEngine() {
		CoreASIMEngine engine = null;
		CoreASMGlobal.setRootFolder(CoreASIMPlugin.getDefault().getPreferenceStore().getString(PreferenceConstants.ROOT_FOLDER));
		engine = org.coreasim.engine.CoreASIMEngineFactory.createEngine();
		setEngineProperties(engine);
		engine.setClassLoader(CoreASMEngineFactory.class.getClassLoader());
		engine.initialize();
		engine.waitWhileBusy();
		return engine;
	}
	
	private static void setEngineProperties(CoreASIMEngine engine) {
		IPreferenceStore prefStore = CoreASIMPlugin.getDefault().getPreferenceStore();
		
		engine.setProperty(EngineProperties.PLUGIN_FOLDERS_PROPERTY, 
				prefStore.getString(PreferenceConstants.ADDITIONAL_PLUGINS_FOLDERS));
	}

}
