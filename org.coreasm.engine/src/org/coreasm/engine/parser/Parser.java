/*	
 * Parser.java 	1.5 	$Revision: 243 $
 * 
 *
 * Copyright (C) 2005 Mashaal Memon
 * Copyright (C) 2005-2007 Roozbeh Farahbod
 * Copyright (C) 2016 Daniel Schreckling
 * 
 * Last modified by $Author: rfarahbod $ on $Date: 2011-03-29 02:05:21 +0200 (Di, 29 Mrz 2011) $
 * 
 * Last modified by Daniel Schreckling, 2016-04-12
 *
 * Licensed under the Academic Free License version 3.0 
 *   http://www.opensource.org/licenses/afl-3.0.php
 *   http://www.coreasm.org/afl-3.0.php
 *
 */
 
package org.coreasm.engine.parser;

import java.util.Set;

import org.coreasm.engine.Specification;
import org.coreasm.engine.interpreter.ASTNode;

/** 
 *	Defines the interface of the Parser module.
 *   
 *  @author  Mashaal Memon, Roozbeh Farahbod
 *  
 */
public interface Parser {

	/**
	 * Set the specification to be parsed.
	 * 
	 * @param specification CoreASM specification 
	 */
	public void setSpecification(Specification specification);
	
	/**
	 * Parses the header of the given specification file, decifering the names
	 * of plug-ins to be used with the specification file.
	 * 
	 * @throws ParserException when a parser module specific expection occurs
	 */
	public void parseHeader() throws ParserException;
	
	/**
	 * Get names plug-ins required by specification who's header was last parsed.
	 * 
	 * @return Set of <code>String</code> representing plug-in names to be used.
	 */
	public Set<String> getRequiredPlugins();
	
	/**
	 * Parse the given specification using given grammar.
	 * 
	 * @throws ParserException when a parser module specific expection occurs
	 */
	public void parseSpecification() throws ParserException;

    /**
	 * Parse the specified rule using the currently specified rule grammar
	 * 
	 * @throws ParserException when a parser module specific expection occurs
	 */
    public ASTNode parseRuleOnly(String strRule) throws ParserException;

    /**
	 * Parse the specified rule declaration using the currently specified rule grammar
	 * 
	 * @throws ParserException when a parser module specific expection occurs
	 */
    public ASTNode parseRuleDeclarationOnly(String strRule) throws ParserException;
	
	/**
	 * Get the root node of the AST tree generated as a result of parsing the
	 * specification. <code>null</code> is returned if there is none.
	 * 
	 * @return an AST <code>Node</code> object for the root of the AST.
	 */
	public ASTNode getRootNode();
	
	/**
	 * Returns a position map object based on the currently loaded specification.
	 */
	public PositionMap getPositionMap();

	/**
	 * Parses the string of derived function definitions, all separated by '\\n'
	 * 
	 * @throws ParserException when a parser module specific exception occurs
	 */
	public ASTNode parseDerivedFunction(String function) throws ParserException;
	
	/**
	 * Returns the {@link ParserTools} instance of this parser.
	 */
	//public ParserTools getParserTools();
	
}
