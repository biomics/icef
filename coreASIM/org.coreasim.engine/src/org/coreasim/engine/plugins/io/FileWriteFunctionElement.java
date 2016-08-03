package org.coreasim.engine.plugins.io;

import org.coreasim.engine.absstorage.MapFunction;


/** 
 * @see org.coreasim.engine.plugins.io.IOPlugin
 */
public class FileWriteFunctionElement extends MapFunction {


	public FileWriteFunctionElement() {
		setFClass(FunctionClass.fcOut);
	}
}
