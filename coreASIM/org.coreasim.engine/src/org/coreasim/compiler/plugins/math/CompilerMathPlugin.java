package org.coreasim.compiler.plugins.math;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import org.coreasim.compiler.plugins.math.code.rcode.RandomValueHandler;
import org.coreasim.engine.plugin.Plugin;
import org.coreasim.engine.plugins.math.MathPlugin;

/**
 * Provides additional mathematical functions
 * @author Spellmaker
 *
 */
public class CompilerMathPlugin extends CompilerCodePlugin implements CompilerPlugin,
		CompilerVocabularyExtender {

	private Plugin interpreterPlugin;
	
	/**
	 * Constructs a new plugin
	 * @param parent The interpreter version
	 */
	public CompilerMathPlugin(Plugin parent){
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

		Map<String, MathFunctionEntry> functions = MathPluginHelper
				.createFunctions(engine);

		List<MainFileEntry> result = new ArrayList<MainFileEntry>();
		
		File enginePath = engine.getOptions().enginePath;
		
		if(enginePath == null){
			engine.getLogger().error(getClass(), "loading classes from a directory is currently not supported");
			throw new CompilerException("could not load classes");
		}
		else{
			try {
				//classLibrary.addPackageReplacement("org.coreasm.engine.plugins.set.SetElement", "plugins.SetPlugin.SetElement");
				classLibrary.addPackageReplacement("org.coreasm.engine.plugins.math.MathFunction", engine.getPath().getEntryName(LibraryEntryType.STATIC, "MathFunction", "MathPlugin"));
				classLibrary.addPackageReplacement("org.coreasm.compiler.plugins.math.include.PowerSetElement", engine.getPath().getEntryName(LibraryEntryType.STATIC, "PowerSetElement", "MathPlugin"));
				
				result = (new JarIncludeHelper(engine, this)).
						includeStatic("org/coreasm/engine/plugins/math/MathFunction.java", EntryType.INCLUDEONLY).
						includeStatic("org/coreasm/compiler/plugins/math/include/PowerSetElement.java", EntryType.INCLUDEONLY).
						build();
				for(Entry<String, MathFunctionEntry> e : functions.entrySet()){
					classLibrary.addEntry(e.getValue());
					result.add(new MainFileEntry(e.getValue(), EntryType.FUNCTION, e.getKey()));
				}
				
			} catch (EntryAlreadyExistsException e) {
				throw new CompilerException(e);
			}
		}

		return result;
	}

	@Override
	public String getName() {
		return MathPlugin.PLUGIN_NAME;
	}

	@Override
	public void registerCodeHandlers() throws CompilerException {
		register(new RandomValueHandler(), CodeType.R, "Expression", "RandomValue", null);
	}
}
