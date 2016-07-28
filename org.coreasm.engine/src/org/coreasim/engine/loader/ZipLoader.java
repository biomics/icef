package org.coreasim.engine.loader;

import java.io.File;
import java.util.jar.JarEntry;

import org.coreasim.engine.EngineException;
import org.coreasim.engine.plugin.Plugin;

public class ZipLoader {
	/*
	 * Loads a plugin from a Zip file.
	 */
	public static Plugin loadPluginClassesFromZipFile(File file) throws EngineException {
		throw new EngineException("Plugin ZIP files are not supported." + file.getName());
	}

	/*
	 * Loads a plugin from a Zip file.
	 */
	public static Plugin loadPluginClassesFromZipFile(JarEntry entry) throws EngineException {
		throw new EngineException("Plugin ZIP files are not supported. " + entry.getName());
	}
}
