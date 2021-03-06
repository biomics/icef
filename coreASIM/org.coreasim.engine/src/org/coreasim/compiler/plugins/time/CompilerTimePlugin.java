package org.coreasim.compiler.plugins.time;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.coreasim.compiler.CompilerEngine;
import org.coreasim.compiler.components.classlibrary.ClassLibrary;
import org.coreasim.compiler.components.classlibrary.JarIncludeHelper;
import org.coreasim.compiler.components.mainprogram.EntryType;
import org.coreasim.compiler.components.mainprogram.MainFileEntry;
import org.coreasim.compiler.exception.CompilerException;
import org.coreasim.compiler.exception.EntryAlreadyExistsException;
import org.coreasim.compiler.interfaces.CompilerPlugin;
import org.coreasim.compiler.interfaces.CompilerVocabularyExtender;
import org.coreasim.engine.plugin.Plugin;
import org.coreasim.engine.plugins.time.NowFunctionElement;
import org.coreasim.engine.plugins.time.StepCountFunctionElement;

/**
 * Provides access to time functions
 * @author Spellmaker
 *
 */
public class CompilerTimePlugin implements CompilerPlugin, CompilerVocabularyExtender {
	private CompilerEngine engine;
	private Plugin interpreterPlugin;
	
	/**
	 * Constructs a new plugin
	 * @param parent The interpreter version
	 */
	public CompilerTimePlugin(Plugin parent){
		this.interpreterPlugin = parent;
	}

	@Override
	public void init(CompilerEngine engine) {
		this.engine = engine;
	}
	
	@Override
	public Plugin getInterpreterPlugin(){
		return interpreterPlugin;
	}

	@Override
	public List<MainFileEntry> loadClasses(ClassLibrary classLibrary)
			throws CompilerException {
		List<MainFileEntry> result = new ArrayList<MainFileEntry>();
		
		File enginePath = engine.getOptions().enginePath;
		
		if(enginePath == null){
			engine.getLogger().error(getClass(), "loading classes from a directory is currently not supported");
			throw new CompilerException("could not load classes");
		}
		else{
			try {
				//classLibrary.addPackageReplacement("org.coreasm.engine.plugins.number.NumberElement", "plugins.NumberPlugin.NumberElement");
				result = (new JarIncludeHelper(engine, this)).
						includeStatic("org/coreasm/engine/plugins/time/NowFunctionElement.java", EntryType.FUNCTION, NowFunctionElement.NOW_FUNC_NAME).
						includeStatic("org/coreasm/engine/plugins/time/StepCountFunctionElement.java", EntryType.FUNCTION_CAPI, StepCountFunctionElement.FUNC_NAME).
						build();
			} catch (EntryAlreadyExistsException e) {
				throw new CompilerException(e);
			}
			
		}
		
		return result;
	}

	@Override
	public String getName() {
		return "TimePlugin";
	}
}
