package org.coreasim.compiler.plugins.map;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.coreasim.compiler.CodeType;
import org.coreasim.compiler.CompilerEngine;
import org.coreasim.compiler.components.classlibrary.ClassLibrary;
import org.coreasim.compiler.components.classlibrary.JarIncludeHelper;
import org.coreasim.compiler.components.classlibrary.LibraryEntryType;
import org.coreasim.compiler.components.mainprogram.EntryType;
import org.coreasim.compiler.components.mainprogram.MainFileEntry;
import org.coreasim.compiler.exception.CompilerException;
import org.coreasim.compiler.exception.EntryAlreadyExistsException;
import org.coreasim.compiler.interfaces.CompilerCodePlugin;
import org.coreasim.compiler.interfaces.CompilerPlugin;
import org.coreasim.compiler.interfaces.CompilerVocabularyExtender;
import org.coreasim.compiler.plugins.map.code.rcode.MapHandler;
import org.coreasim.engine.plugin.Plugin;
import org.coreasim.engine.plugins.map.MapBackgroundElement;
import org.coreasim.engine.plugins.map.MapToPairsFunctionElement;
import org.coreasim.engine.plugins.map.ToMapFunctionElement;

/**
 * Provides maps to the compiler.
 * @author Spellmaker
 *
 */
public class CompilerMapPlugin extends CompilerCodePlugin implements CompilerPlugin,
		CompilerVocabularyExtender {

	private Plugin interpreterPlugin;
	
	/**
	 * Constructs a new plugin
	 * @param parent The interpreter version
	 */
	public CompilerMapPlugin(Plugin parent){
		this.interpreterPlugin = parent;
	}
	
	@Override
	public Plugin getInterpreterPlugin(){
		return interpreterPlugin;
	}

	@Override
	public void init(CompilerEngine engine) {
		this.engine = engine;
	}

	@Override
	public List<MainFileEntry> loadClasses(ClassLibrary classLibrary)
			throws CompilerException {
		File enginePath = engine.getOptions().enginePath;
		List<MainFileEntry> result = new ArrayList<MainFileEntry>();
		
		if(enginePath == null){
			engine.getLogger().error(getClass(), "loading classes from a directory is currently not supported");
			throw new CompilerException("could not load classes");
		}
		else{
			try {
				//classLibrary.addPackageReplacement("org.coreasm.engine.plugins.collection.AbstractMapElement", "plugins.CollectionPlugin.AbstractMapElement");
				//classLibrary.addPackageReplacement("org.coreasm.compiler.plugins.collection.include.ModifiableCollection", "plugins.CollectionPlugin.ModifiableCollection");
				//classLibrary.addPackageReplacement("org.coreasm.engine.plugins.list.ListElement", "plugins.ListPlugin.ListElement");
				
				//package replacements for classes accessible from other plugins
				classLibrary.addPackageReplacement("org.coreasm.engine.plugins.map.MapBackgroundElement", engine.getPath().getEntryName(LibraryEntryType.STATIC, "MapBackgroundElement", "MapPlugin"));
				classLibrary.addPackageReplacement("org.coreasm.compiler.plugins.map.include.MapElement", engine.getPath().getEntryName(LibraryEntryType.STATIC, "MapElement", "MapPlugin"));
				
				result = (new JarIncludeHelper(engine, this)).
						includeStatic("org/coreasm/engine/plugins/map/MapBackgroundElement.java", EntryType.BACKGROUND, MapBackgroundElement.NAME).
						includeStatic("org/coreasm/engine/plugins/map/MapToPairsFunctionElement.java", EntryType.FUNCTION, MapToPairsFunctionElement.NAME).
						includeStatic("org/coreasm/engine/plugins/map/ToMapFunctionElement.java", EntryType.FUNCTION, ToMapFunctionElement.NAME).
						includeStatic("org/coreasm/compiler/plugins/map/include/MapElement.java", EntryType.INCLUDEONLY).
						build();
			} catch (EntryAlreadyExistsException e) {
				throw new CompilerException(e);
			}
		}

		return result;
	}

	@Override
	public String getName() {
		return "MapPlugin";
	}

	@Override
	public void registerCodeHandlers() throws CompilerException {
		register(new MapHandler(), CodeType.R, "Expression", "MapTerm", null);
	}
}
