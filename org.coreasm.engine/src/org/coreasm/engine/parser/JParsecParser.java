/*	
 * JParsecParser.java 	$Revision: 243 $
 * 
 * Copyright (C) 2007 Roozbeh Farahbod
 *
 * Last modified by $Author: rfarahbod $ on $Date: 2011-03-29 02:05:21 +0200 (Di, 29 Mrz 2011) $.
 *
 * Licensed under the Academic Free License version 3.0
 *   http://www.opensource.org/licenses/afl-3.0.php
 *   http://www.coreasm.org/afl-3.0.php
 *
 */
 
package org.coreasm.engine.parser;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.coreasm.engine.ControlAPI;
import org.coreasm.engine.EngineError;
import org.coreasm.engine.SpecLine;
import org.coreasm.engine.Specification;
import org.coreasm.engine.interpreter.ASTNode;
import org.coreasm.engine.interpreter.Node;
import org.coreasm.engine.plugin.ParserPlugin;
import org.coreasm.engine.plugin.Plugin;
import org.coreasm.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.coreasm.engine.kernel.Kernel;

import java.lang.reflect.*;

/** 
 * This is an implementation of the {@link Parser} component 
 * using the JParsec libraries.
 *   
 * @author Roozbeh Farahbod, Mashaal Memon
 * 
 */
public class JParsecParser implements Parser {

	private static final Logger logger = LoggerFactory.getLogger(JParsecParser.class);
	
	/** Control API of engine which this parser belongs to */
	private ControlAPI capi;
		
	/** names of all plugins used by specification*/
	private HashSet<String> pluginNames;

	/* CoreASM specification */
	private Specification specification = null;
	
	private PositionMap positionMap = null;
	
	private boolean headerParsed = false;
	
	/** the actual parser -- a JParsec parser */ 
	private org.codehaus.jparsec.Parser<Node> parser;
	
	/** the root grammar rule */
	private GrammarRule rootGrammarRule;
	
	/** the root node of the specification (after parsing) */
	private ASTNode rootNode = null;

    /** node containing the policy rule */
    private ASTNode policyNode = null;
	
	//private final ParserTools parserTools;
	private final ParserTools parserTools;
	
	
	/**
	 * Implementation of the parser interface using the JParsec library.
	 * The
	 * control API for the engine which this parser belongs to is passed in.
	 *
	 * @param capi control api for engine which this parser belongs to.
	 */
	public JParsecParser(ControlAPI capi) {
		super();
		this.capi = capi;
		parserTools = ParserTools.getInstance(capi);
	}
	
	/* (non-Javadoc)
	 * @see org.coreasm.engine.parser.Parser#getRequiredPlugins()
	 */
	public Set<String> getRequiredPlugins() {
		return pluginNames;
	}

	/* (non-Javadoc)
	 * @see org.coreasm.engine.parser.Parser#getRootNode()
	 */
	public ASTNode getRootNode() {
		return rootNode;
	}

	/**
	 * Plugins to be used for the specification are specified with "use"
	 * directive on their own lines. Fine these lines and take note of
	 * plugin names found beside the "use" directives.
	 * 
	 * @see org.coreasm.engine.parser.Parser#parseHeader()
	 */
	public void parseHeader() throws ParserException
	{
            
		String useRegex;
		Pattern usePattern;
		Matcher useMatcher;
                
		// instantiate new plugin names set.
		pluginNames = new HashSet<String>();
		
		try
		{
			// compile pattern to find "use" directive using regular expression
			useRegex = "^[\\s]*[uU][sS][eE][\\s]+"; // regex to fine "use" directive followed by whitespace at beginning of line
			// compile and get a reference to a Pattern object.
			usePattern = Pattern.compile(useRegex);
			
			// error if specification is not set
			if (specification==null)
			{
				logger.error("Specification file must first be set before its header can be parsed.");
				throw new ParserException("Specification file must first be set before its header can be parsed.");
			}
		
			boolean multiLineComment = false;
			// for each line of specification file
			for (SpecLine line: specification.getLines()) {
				if (multiLineComment || line.text.contains("*/")) {
					multiLineComment = !line.text.contains("*/");
					continue;
				}
				// get a "use" directive matcher object for the line
				useMatcher = usePattern.matcher(line.text);

				// if match found
				if (useMatcher.find())
				{
					// get plugin name and add to the list
					String pluginName = useMatcher.replaceFirst("").trim();
					pluginNames.add(pluginName);
				}
				else if (line.text.contains("/*"))
					multiLineComment = true;
			}
			
			headerParsed = true;
			
		}
		catch (NullPointerException e)
		{
			logger.error("CoreASM specification cannot be read from.");	
		} 
	}

    public ASTNode parseRuleDeclarationOnly(String strRule) throws ParserException {
        Plugin kernel = capi.getPlugin("Kernel");
        if (kernel != null) {
            try {
                org.codehaus.jparsec.Parser<Node> p = ((ParserPlugin)kernel).getParsers().get("RuleDeclaration").parser.from(parserTools.getTokenizer(), parserTools.getIgnored());
                ASTNode a = (ASTNode) p.parse(strRule);

                return a;
            } catch (Throwable e) {
					if (e instanceof org.codehaus.jparsec.error.ParserException) {
						org.codehaus.jparsec.error.ParserException pe = (org.codehaus.jparsec.error.ParserException) e;
						Throwable cause = pe.getCause();
						String msg = pe.getMessage();
						msg = msg.substring(msg.indexOf("\n")+1);
						msg = "Error parsing " + msg + (cause==null?"":"\n" + cause.getMessage());
						
						String errorLogMsg = "Error in parsing.";
						if (cause != null) {
							StringWriter strWriter = new StringWriter();
							cause.printStackTrace(new PrintWriter(strWriter));
							errorLogMsg = errorLogMsg + Tools.getEOL() + strWriter.toString();
						}
						logger.error(errorLogMsg);
						
						throw new ParserException(msg, 
								new CharacterPosition(pe.getLocation().line, pe.getLocation().column));
					}
					throw new ParserException(e);
            }
        } else {
            logger.error("Parser cannot find the Kernel plugin.");
            throw new EngineError("Parser cannot find the Kernel plugin.");
        }        
    }

    public ASTNode parseRuleOnly(String strRule) throws ParserException {
        Plugin kernel = capi.getPlugin("Kernel");
        if (kernel != null) {
            try {
                org.codehaus.jparsec.Parser<Node> p = ((ParserPlugin)kernel).getParsers().get("Rule").parser.from(parserTools.getTokenizer(), parserTools.getIgnored());
                
                ASTNode a = (ASTNode) p.parse(strRule);
                System.out.println("THIS IS A RULE: "+a);

                return a;
            } catch (Throwable e) {
					if (e instanceof org.codehaus.jparsec.error.ParserException) {
						org.codehaus.jparsec.error.ParserException pe = (org.codehaus.jparsec.error.ParserException) e;
						Throwable cause = pe.getCause();
						String msg = pe.getMessage();
						msg = msg.substring(msg.indexOf("\n")+1);
						msg = "Error parsing " + msg + (cause==null?"":"\n" + cause.getMessage());
						
						String errorLogMsg = "Error in parsing.";
						if (cause != null) {
							StringWriter strWriter = new StringWriter();
							cause.printStackTrace(new PrintWriter(strWriter));
							errorLogMsg = errorLogMsg + Tools.getEOL() + strWriter.toString();
						}
						logger.error(errorLogMsg);
						
						throw new ParserException(msg, 
								new CharacterPosition(pe.getLocation().line, pe.getLocation().column));
					}
					throw new ParserException(e);
            }
        } else {
            logger.error("Parser cannot find the Kernel plugin.");
            throw new EngineError("Parser cannot find the Kernel plugin.");
        }        
    }

    public Set<String> getFunctionNames() {
       	Set<Plugin> plugins = capi.getPlugins();
        Set<String> standard = new HashSet<String>();

        for(Plugin p : plugins) {
            Class pluginClass = p.getClass();
            Method m = null;
            boolean retrieveFunctions = false;
            
            try {
                m = pluginClass.getDeclaredMethod("getFunctionNames", new Class[] {});
                retrieveFunctions = true;
            } catch(NoSuchMethodException e) {
                // ignore
            }
            
            Set<String> funcNames = null;
            if(retrieveFunctions) {
                try {
                    funcNames = (Set<String>)m.invoke(p, null);
                }
                catch (InvocationTargetException e) {
                    // e.printStackTrace();
                    // System.out.println("\tInvocationTargetException");
                }
                catch(IllegalAccessException e) {
                    // e.printStackTrace();
                    // System.out.println("\tIllegalAccessException");
                }
            }

            if(funcNames != null)
                standard.addAll(funcNames);
        }

        return standard;
    }

    public Map<String, ASTNode> getDeclarations(ASTNode start) {
        Map<String, ASTNode> declared = new HashMap<String, ASTNode>();

        Kernel kernel = (Kernel) capi.getPlugin("Kernel");

        if(start.getGrammarRule().equals("EnumerationDefinition")) {
            List<ASTNode> childs = start.getAbstractChildNodes();
            for(ASTNode child : childs) {
                declared.put(child.getToken(), child);
            }
            return declared;
        }

        List<ASTNode> childs = start.getAbstractChildNodes();
        for(ASTNode child : childs) {

            // first handle childs such that top level definitions
            // always have highest priority in evaluation
            declared.putAll(getDeclarations(child));

            String gc = child.getGrammarClass();
            String gr = child.getGrammarRule();

            if(gc.equals(ASTNode.DECLARATION_CLASS))  {
                ASTNode n = child;
                ASTNode last = n;
                while(n != null && n.getGrammarClass().equals(ASTNode.DECLARATION_CLASS)) {
                    last = n;
                    n = n.getFirst();
                }
                if(n == null)
                    n = last;
                if(!n.getPluginName().equals(Kernel.PLUGIN_NAME)) {
                    n = n.getNext();
                }
                if (n== null)
                	System.out.println("ERROR!");
                declared.put(n.getToken(), child);
                // System.out.println("DECLARATION: "+child+" => "+n.getToken());
            }
        }

        return declared;
    }

    public Set<String> getUndeclaredRec(ASTNode node, Map<String, ASTNode> declared, Set<String> visited, Set<String> variables) {
        Set<String> locations = new HashSet<String>();

        // System.out.println("getUndeclared in node: "+node);

        String gr = node.getGrammarRule();

        // controlled functions are locations
        if(gr.equals("FunctionClass") && node.getToken().equals("controlled")) {
            // System.out.println(" ==> '"+node.getNext().getToken()+"' *** is *** a location (controlled function)");
            locations.add(node.getNext().getToken());
        }

        if(node.getGrammarClass() == ASTNode.ID_CLASS) {
            String nodeId = node.getToken();

            if(declared.containsKey(nodeId) && !visited.contains(nodeId)) {
                ASTNode start = declared.get(nodeId);
                visited.add(nodeId);
                ASTNode next = start.getFirst();
                while(next != null) {
                    locations.addAll(getUndeclaredRec(next, declared, visited, variables));
                    next = next.getNext();
                }
            } else {
                if(!visited.contains(nodeId)) {
                    // check if parent node is a declaration
                    if(node.getParent().getGrammarClass().equals(ASTNode.DECLARATION_CLASS)) {
                        // System.out.println("==> no location as parent is declaration (assume variable)");
                        variables.add(node.getToken());
                    } else if(!variables.contains(nodeId)) {
                        System.out.println("==> *** " + nodeId + " is probably a location ***");
                        locations.add(nodeId);
                    }
                }
            }
        } else {
            ASTNode cur = node.getFirst();

            if(gr.equals("LetPolicy") || gr.equals("ChoosePolicy") || gr.equals("ForallPolicy")) {
                /* System.out.println("\tCur '"+cur+"' is exists expression");
                   System.out.println("\tAdd '"+cur.getToken()+"' as variable"); 
                */
                variables.add(cur.getToken());
                ASTNode search = cur = cur.getNext();
                while(search != null) {
                    if(search.getGrammarClass() == ASTNode.ID_CLASS) {
                        variables.add(search.getToken());
                    }
                    search = search.getNext();
                }     
            }
            if(gr.equals("ExistsExp")) {
                /* System.out.println("Cur '"+cur+"' is exists expression");
                   System.out.println("Add '"+cur.getToken()+"' as variable");
                */
                variables.add(cur.getToken());
                ASTNode search = cur = cur.getNext();
                while(search != null) {
                    if(search.getGrammarClass() == ASTNode.ID_CLASS) {
                        variables.add(search.getToken());
                    }
                    search = search.getNext();
                }     
            }
            if(gr.equals("SetComprehension")) {
                variables.add(cur.getNext().getToken());
                cur = cur.getNext().getNext();
            }
            
            while(cur != null) {
                locations.addAll(getUndeclaredRec(cur, declared, visited, variables));
                cur = cur.getNext();
            }
        }

        return locations;
    }

    public Set<String> getLocations(ASTNode node) {
        Set<String> visited = getFunctionNames();
        HashSet<String> variables = new HashSet<String>();
        Map<String, ASTNode> declared = getDeclarations(rootNode);

        return getUndeclaredRec(node, declared, visited, variables);
    }

	/* (non-Javadoc)
	 * @see org.coreasm.engine.parser.Parser#parseSpecification()
	 */
	public void parseSpecification() throws ParserException {
		if (headerParsed) {
			Plugin kernel = capi.getPlugin("Kernel");
			if (kernel != null) {
				//FIXME Is this what determines the COREASM keyword in scripts?
				this.rootGrammarRule = ((ParserPlugin)kernel).getParsers().get("CoreASM");
				this.parser = rootGrammarRule.parser;
				try {
					org.codehaus.jparsec.Parser<Node> _parser =  parser.from(parserTools.getTokenizer(), parserTools.getIgnored());
                                        
					rootNode = (ASTNode) _parser.parse(specification.getText());

                    System.out.println("parseSpecification():");
                    for(Node n : rootNode.getChildNodes()) {
                        if(n instanceof ASTNode) {
                            ASTNode an = (ASTNode) n;
                            // Check if Grammarrule is scheduling then find policy name
                            if(an.getGrammarRule().equals(Kernel.GR_SCHEDULING)) {
                                Set<String> locations = getLocations(an);
                                System.out.println("Locations required for scheduler: ");
                                for(String l : locations) {
                                    System.out.println("\t"+l);
                                }
                                break;
                            }
                            
                            /* .getNext();
                            while(an != null) {
                                System.out.println("ASTNode: "+an.toString());
                                
                                an = rootNode.getNext();
                                }*/
                        }
                    }

				} catch (Throwable e) {
					if (e instanceof org.codehaus.jparsec.error.ParserException) {
						org.codehaus.jparsec.error.ParserException pe = (org.codehaus.jparsec.error.ParserException) e;
						Throwable cause = pe.getCause();
						String msg = pe.getMessage();
						msg = msg.substring(msg.indexOf("\n")+1);
						msg = "Error parsing " + msg + (cause==null?"":"\n" + cause.getMessage());
						
						String errorLogMsg = "Error in parsing.";
						if (cause != null) {
							StringWriter strWriter = new StringWriter();
							cause.printStackTrace(new PrintWriter(strWriter));
							errorLogMsg = errorLogMsg + Tools.getEOL() + strWriter.toString();
						}
						logger.error(errorLogMsg);
						
						throw new ParserException(msg, 
								new CharacterPosition(pe.getLocation().line, pe.getLocation().column));
					}
					throw new ParserException(e);
				}
			} else {
				logger.error("Parser cannot find the Kernel plugin.");
				throw new EngineError("Parser cannot find the Kernel plugin.");
			}
		} else {
			logger.error("Header must be parsed before the entire specification can be parsed.");
			throw new ParserException("Header must be parsed before the entire specification can be parsed.");
		}
	}

	public void setSpecification(Specification spec) {
		positionMap = null;
		pluginNames = null;
		parser = null;
		headerParsed = false;
		this.specification = spec;
	}

	/*
	 * @see org.coreasm.engine.parser.Parser#getPositionMap()
	 */
	public PositionMap getPositionMap() {
		if (positionMap == null) {
			positionMap = new PositionMap(specification.getText(), 1, 1);
		}
		return positionMap;
	}

	public ParserTools getParserTools() {
		return parserTools;
	}

}
