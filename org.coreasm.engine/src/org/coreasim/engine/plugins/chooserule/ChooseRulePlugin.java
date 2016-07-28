/*	
 * ChooseRulePlugin.java 	1.0 	$Revision: 243 $
 *
 * Last modified on $Date: 2011-03-29 02:05:21 +0200 (Di, 29 Mrz 2011) $ by $Author: rfarahbod $
 * 
 * Copyright (C) 2006 George Ma
 * Copyright (c) 2007 Roozbeh Farahbod
 *
 * Licensed under the Academic Free License version 3.0
 *   http://www.opensource.org/licenses/afl-3.0.php
 *   http://www.coreasm.org/afl-3.0.php
 *
 */
 
package org.coreasim.engine.plugins.chooserule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.coreasim.compiler.interfaces.CompilerPlugin;
import org.coreasim.compiler.plugins.chooserule.CompilerChooseRulePlugin;
import org.coreasim.engine.ControlAPI;
import org.coreasim.engine.CoreASIMError;
import org.coreasim.engine.VersionInfo;
import org.coreasim.engine.absstorage.BooleanElement;
import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.absstorage.Enumerable;
import org.coreasim.engine.absstorage.UpdateMultiset;
import org.coreasim.engine.interpreter.ASTNode;
import org.coreasim.engine.interpreter.Interpreter;
import org.coreasim.engine.interpreter.InterpreterException;
import org.coreasim.engine.interpreter.Node;
import org.coreasim.engine.kernel.KernelServices;
import org.coreasim.engine.parser.GrammarRule;
import org.coreasim.engine.parser.ParserTools;
import org.coreasim.engine.plugin.InterpreterPlugin;
import org.coreasim.engine.plugin.ParserPlugin;
import org.coreasim.engine.plugin.Plugin;
import org.coreasim.engine.plugins.map.MapElement;
import org.coreasim.engine.plugins.number.NumberElement;
import org.coreasim.util.Tools;

/** 
 *	Plugin for choose rule
 *   
 *  @author  George Ma, Roozbeh Farahbod
 *  
 */
public class ChooseRulePlugin extends Plugin implements ParserPlugin,
        InterpreterPlugin {

	public static final VersionInfo VERSION_INFO = new VersionInfo(0, 9, 3, "");
	
	public static final String PLUGIN_NAME = ChooseRulePlugin.class.getSimpleName();
	
	protected static final String GUARD_NAME = "guard";
	protected static final String DISTRIBUTION_NAME = "distribution";
	protected static final String DO_RULE_NAME = "dorule";
	protected static final String IFNONE_RULE_NAME = "ifnonerule";

	private final String[] keywords = {"choose", "pick", "with", "in", "do", "ifnone", "endchoose", "using"};
	private final String[] operators = {};
	
    private ThreadLocal<Map<Node,List<Element>>> remained;

    private Map<String, GrammarRule> parsers;
	private Set<String> dependencies = null;
    
    private final CompilerPlugin compilerPlugin = new CompilerChooseRulePlugin(this);
    
    @Override
    public CompilerPlugin getCompilerPlugin(){
    	return compilerPlugin;
    }
    
    @Override
    public void initialize() {
        remained = new ThreadLocal<Map<Node, List<Element>>>() {
			@Override
			protected Map<Node, List<Element>> initialValue() {
				return new IdentityHashMap<Node, List<Element>>();
			}
        };
    }
    
    @Override
	public Set<String> getDependencyNames() {
		if (dependencies == null) {
			dependencies = new HashSet<String>();
			dependencies.add("MapPlugin");
		}
		return dependencies;
	}

    private Map<Node, List<Element>> getRemainedMap() {
    	return remained.get();
    }
    
	@Override
	public void setControlAPI(ControlAPI capi) {
		super.setControlAPI(capi);
	}

	public Set<Parser<? extends Object>> getLexers() {
		return Collections.emptySet();
	}
	
	/**
	 * @return <code>null</code>
	 */
	public Parser<Node> getParser(String nonterminal) {
		return null;
	}


	public String[] getKeywords() {
		return keywords;
	}

	public String[] getOperators() {
		return operators;
	}

	public Map<String, GrammarRule> getParsers() {
		if (parsers == null) {
			parsers = new HashMap<String, GrammarRule>();
			
			KernelServices kernel = (KernelServices)capi.getPlugin("Kernel").getPluginInterface();
			
			Parser<Node> ruleParser = kernel.getRuleParser();
			Parser<Node> termParser = kernel.getTermParser();
			Parser<Node> guardParser = kernel.getGuardParser();
			
			ParserTools npTools = ParserTools.getInstance(capi);
			Parser<Node> idParser = npTools.getIdParser();
			
			// ChooseRule : 'choose' ID 'in' Term (',' ID 'in' Term)* ('with' Guard)? ('using' Term)? 'do' Rule ('ifnone' Rule)? ('endchoose')?
			Parser<Node> chooseRuleParser = Parsers.array(
					npTools.getKeywParser("choose", PLUGIN_NAME),
					npTools.csplus(Parsers.array(idParser,
							npTools.getKeywParser("in", PLUGIN_NAME),
							termParser)),
					npTools.seq(
							npTools.getKeywParser("with", PLUGIN_NAME),
							guardParser).optional(),
					// BSL Here is where we put the distribution
					npTools.seq(
							npTools.getKeywParser("using", PLUGIN_NAME),
							termParser).optional(),
					npTools.getKeywParser("do", PLUGIN_NAME),
					ruleParser, 
					npTools.seq(
							npTools.getKeywParser("ifnone", PLUGIN_NAME),
							ruleParser).optional(),
					npTools.getKeywParser("endchoose", PLUGIN_NAME).optional()).map(
					new ChooseParseMap()); 
			parsers.put("Rule", 
					new GrammarRule("Rule",
							"'choose' ID 'in' Term (',' ID 'in' Term)* ('with' Guard)? ('using' Term)? 'do' Rule ('ifnone' Rule)? ('endchoose')?", chooseRuleParser, this.getName()));
			

			// PickExp: 'pick' ID 'in' Term 'with' Term
			Parser<Node> pickExpParser = Parsers.array(
					new Parser[] {
						npTools.getKeywParser("pick", PLUGIN_NAME),
						idParser,
						npTools.getKeywParser("in", PLUGIN_NAME),
						termParser,
						npTools.seq(
								npTools.getKeywParser("with", PLUGIN_NAME),
								termParser).optional(),
						npTools.seq(
								npTools.getKeywParser("using", PLUGIN_NAME),
								termParser).optional()
					}).map(
					new PickParseMap());
			parsers.put("PickExp",
					new GrammarRule("PickExp", 
							"'pick' ID 'in' Term ('with' Term)? ('using' Term)?", pickExpParser, PLUGIN_NAME));
			
			// ChooseRuleBasicTerm : PickExp
			parsers.put("BasicTerm", 
					new GrammarRule("ChooseRuleBasicTerm", "PickExp",
							pickExpParser, PLUGIN_NAME));
		}
		return parsers;
	}

    public ASTNode interpret(Interpreter interpreter, ASTNode pos) throws InterpreterException {
        
        if (pos instanceof ChooseRuleNode) {
            ChooseRuleNode chooseNode = (ChooseRuleNode) pos;
            // Here, we follow the specification of the choose rule
            // and for a more readable code, we clearly distinguish between various
            // forms of choose
            if (chooseNode.getDistribution() == null)
            {
	            // CASE 1. 'choose X in E do R'  
	            if (chooseNode.getCondition() == null && chooseNode.getIfnoneRule() == null ) 
	            	return interpretChooseRule_NoCondition_NoIfnone(interpreter, pos);
	   
	            // CASE 2. 'choose X in E do R1 ifnone R2'
	            if (chooseNode.getCondition() == null && chooseNode.getIfnoneRule() != null)
	            	return interpretChooseRule_NoCondition_WithIfnone(interpreter, pos);
	     
	            // CASE 3. 'choose X in E with C do R'  
	            if (chooseNode.getCondition() != null && chooseNode.getIfnoneRule() == null) 
	            	return interpretChooseRule_WithCondition_NoIfnone(interpreter, pos);
	   
	            // CASE 4. 'choose X in E with C do R1 ifnone R2'
	            if (chooseNode.getCondition() != null && chooseNode.getIfnoneRule() != null)
	            	return interpretChooseRule_WithCondition_WithIfnone(interpreter, pos);
            }
            else
            {
	            // CASE 1.D 'choose X in E using D do R'  
	            if (chooseNode.getCondition() == null && chooseNode.getIfnoneRule() == null ) 
	            	return interpretChooseRule_NoCondition_NoIfnone_WithDistribution(interpreter, pos);
	   
	            // CASE 2.D 'choose X in E using D do R1 ifnone R2'
	            if (chooseNode.getCondition() == null && chooseNode.getIfnoneRule() != null)
	            	return interpretChooseRule_NoCondition_WithIfnone_WithDistribution(interpreter, pos);
	            // CASE 3.D 'choose X in E with C using D do R'  
	            if (chooseNode.getCondition() != null && chooseNode.getIfnoneRule() == null) 
	            	return interpretChooseRule_WithCondition_NoIfnone_WithDistribution(interpreter, pos);
	   
	            // CASE 4.D 'choose X in E with C using D do R1 ifnone R2'
	            if (chooseNode.getCondition() != null && chooseNode.getIfnoneRule() != null)
	            	return interpretChooseRule_WithCondition_WithIfnone_WithDistribution(interpreter, pos);
            }
        }
        else if (pos instanceof PickExpNode) {
        	PickExpNode node = (PickExpNode)pos;
        	 if (node.getDistribution() == null)
             {
	        	if (node.getCondition() == null) 
	        		return interpretPickExpression_NoCondition(interpreter, node);
	        	else
	        		return interpretPickExpression_WithCondition(interpreter, node);
             }
        	 else
        	 {
        		 //BSL pick cases with distribution
        		 if (node.getCondition() == null) 
 	        		return interpretPickExpression_NoCondition_WithDistribution(interpreter, node);
 	        	else
 	        		return interpretPickExpression_WithCondition_WithDistribution(interpreter, node);
        	 }
        }

        // in case of error
        return pos;
    }

    private ASTNode interpretPickExpression_NoCondition(Interpreter interpreter, PickExpNode node) {
		// if domain 'E' is not evaluated
    	if (!node.getDomain().isEvaluated()) {
            // pos := beta
            return node.getDomain();
        }
        
    	// if domain 'E' is evaluated, but rule 'R' is not evaluated
    	else if (node.getDomain().getValue() instanceof Enumerable) {
        	// s := enumerate(v)
			Enumerable domain = (Enumerable)node.getDomain().getValue();
			List<Element> elements = null;
			if (domain.supportsIndexedView())
				elements = domain.getIndexedView();
			else
				elements = new ArrayList<Element>(((Enumerable) node.getDomain().getValue()).enumerate());
            if (elements.size() > 0) {
                // choose t in s
            	int i = Tools.randInt(elements.size());
                Element picked = elements.get(i);
                node.setNode(null, null, null, picked);
            }
            else {
                // [pos] := (undef,undef,uu)
                node.setNode(null, null, null, Element.UNDEF);
            }
        }
        else {
            capi.error("Cannot pick from " + Tools.sizeLimit(node.getDomain().getValue().denotation()) + ". " +
            		"Pick domain should be an enumerable element.", node.getDomain(), interpreter);
        }
    	
    	return node;
    }
    
    private ASTNode interpretPickExpression_NoCondition_WithDistribution(Interpreter interpreter, PickExpNode node) {
		// if domain 'E' is not evaluated
    	if (!node.getDomain().isEvaluated()) {
            // pos := beta
            return node.getDomain();
        }
    	
    	// evaluate probability distribution
        if (!node.getDistribution().isEvaluated())
        		return node.getDistribution();

        
    	// if domain 'E' is evaluated, but rule 'R' is not evaluated
    	else if (node.getDomain().getValue() instanceof Enumerable) {
        	// s := enumerate(v)
			Enumerable domain = (Enumerable)node.getDomain().getValue();
			List<Element> elements = null;
			if (domain.supportsIndexedView())
				elements = domain.getIndexedView();
			else
				elements = new ArrayList<Element>(((Enumerable) node.getDomain().getValue()).enumerate());
            if (elements.size() > 0) {
            	// BSL here is where we have to modify the choice under a probability distribution
            	// BSL first, we need to obtain the probability distribution
            	MapElement distribution = (MapElement) node.getDistribution().getValue();
            	String res = distribution.isProbabilityDistribution();
            	
            	if (!res.equals(""))
            	{
            		 capi.error("Cannot choose because the given map is not a probability distribution. " +
 	                		"Reason: "+ res, node.getDistribution(), interpreter);
            		node.setNode(null, null, null, Element.UNDEF);
 	                return node;
            	}
            	// BSL we now produce the commulative function of the distribution
            	Map<Element, Element> theMap = distribution.getMap();
            	HashMap<Pair<Double,Double>,Element> cumulativeeFunction = new HashMap<Pair<Double,Double>, Element>();
            	Double initialValue = new Double(0);
            	Double finalValue = new Double(0);
            	double total =0;
            	StringBuilder sb = new StringBuilder();
            	for(Element key : theMap.keySet())
            	{
            		if (!domain.contains(key))
            		{
            			//check if all elements in the keyset are elements of s
            			capi.error("We are choosing "+key+" , but the probability distribution is not defined over "+Tools.sizeLimit(domain.toString()));
            			node.setNode(null, null, null, Element.UNDEF);
    	                return node;
            		}
            		NumberElement n = (NumberElement)theMap.get(key);
            		if(n.doubleValue()==0)
            			continue;
            		initialValue = finalValue;
            		finalValue = new Double(total+n.doubleValue());
            		total+=n.doubleValue();
            		cumulativeeFunction.put(new Pair<Double, Double>(initialValue,finalValue), key);
            		sb.append("("+initialValue.toString()+","+finalValue.toString()+")->"+key.toString());
            	}
        		// choose a number greater than 0 and smaller than the total
            	double d = 2;
            	while(d==0 || d>total){
            		d = Tools.randDouble();
            	}
        		//now, find the element that corresponds to that interval
            	Element chosen = null;
            	for(Pair<Double,Double> key : cumulativeeFunction.keySet())
            	{
            		if (key.l.doubleValue() < d && key.r.doubleValue() >= d)
            		{
            			chosen = cumulativeeFunction.get(key);
            			break;
            		}
            	}
            	if(chosen == null)
        		{
        			//This should not happen
        			capi.error("There is an error in the implementation of the probability distribution. "
        					+ "the chosen number was "+d+" the total is "+total+" and the map is "+sb.toString());
        			 node.setNode(null, null, null, Element.UNDEF);
	                return node;
        		}                
                // choose t in s
                node.setNode(null, null, null, chosen);
            }
            else {
                // [pos] := (undef,undef,uu)
                node.setNode(null, null, null, Element.UNDEF);
            }
        }
        else {
            capi.error("Cannot pick from " + Tools.sizeLimit(node.getDomain().getValue().denotation()) + ". " +
            		"Pick domain should be an enumerable element.", node.getDomain(), interpreter);
        }
    	
    	return node;
    }
    
    private ASTNode interpretPickExpression_WithCondition(Interpreter interpreter, PickExpNode node) {
        String x = node.getVariable().getToken();
        
        Map<Node, List<Element>> remained = getRemainedMap();
        
		// if domain 'E' is not evaluated
        if (!node.getDomain().isEvaluated()) {
            // considered(beta) := {}
        	remained.remove(node.getDomain());
            // pos := beta
            return node.getDomain();
        }

    	// if domain 'E' is evaluated, but condition 'C' is not evaluated
    	else if (!node.getCondition().isEvaluated()) {
            if (node.getDomain().getValue() instanceof Enumerable) {
            	// s := enumerate(v)
                // s := enumerate(v)/considered(beta)
            	List<Element> s = null;
            	Enumerable domain = (Enumerable)node.getDomain().getValue();
        		s = remained.get(node.getDomain());
            	if (s == null) {
            		if (domain.supportsIndexedView())
            			s = new ArrayList<Element>(domain.getIndexedView());
            		else 
                    	s = new ArrayList<Element>(((Enumerable) node.getDomain().getValue()).enumerate());
            		remained.put(node.getDomain(), s);
            	}
                if (s.size() > 0) {
                    // choose t in s
                	int i = Tools.randInt(s.size());
                    Element chosen = s.get(i);
                    // AddEnv(x,t)s
                    interpreter.addEnv(x, chosen);
                    // considered := considered union {t}
                	s.remove(i);
                    //considered.get(chooseNode.getDomain()).add(chosen);
                    // pos := gamma
                    return node.getCondition();
                }
                else {
                	remained.remove(node.getDomain());
                	// [pos] := (undef,undef, uu)
                	node.setNode(null, null, null, Element.UNDEF);
                	return node;
                }
            }
            else {
                capi.error("Cannot pick from " + Tools.sizeLimit(node.getDomain().getValue().denotation()) + ". " +
                		"Pick domain should be an enumerable element.", node.getDomain(), interpreter);
            }
    	}

    	// if domain 'E' is evaluated and condition 'C' is evaluated
    	else {
            boolean value = false;            
            if (node.getCondition().getValue() instanceof BooleanElement) {
                value = ((BooleanElement) node.getCondition().getValue()).getValue();
            }
            else {
                capi.error("Value of pick condition is not Boolean.", node.getCondition(), interpreter);
                return node;
            }
            
            if (value) {
            	Element picked = interpreter.getEnv(x);
                // RemoveEnv(x)
                interpreter.removeEnv(x);
                remained.remove(node.getDomain());
                
                // [pos] := (undef,undef, value)
                node.setNode(null, null, null, picked);
                return node;
            }
            else {
                // ClearTree(gamma)
                interpreter.clearTree(node.getCondition());
                // RemoveEnv(x)
                interpreter.removeEnv(x);
                // pos := beta
                return node.getDomain();
            }
    	}
        
        return node;
    }
    
    private ASTNode interpretPickExpression_WithCondition_WithDistribution(Interpreter interpreter, PickExpNode node) {
        String x = node.getVariable().getToken();
        
        Map<Node, List<Element>> remained = getRemainedMap();
        
		// if domain 'E' is not evaluated
        if (!node.getDomain().isEvaluated()) {
            // considered(beta) := {}
        	remained.remove(node.getDomain());
            // pos := beta
            return node.getDomain();
        }
        
        // evaluate probability distribution
        if (!node.getDistribution().isEvaluated())
        		return node.getDistribution();

    	// if domain 'E' is evaluated, but condition 'C' is not evaluated
    	else if (!node.getCondition().isEvaluated()) {
            if (node.getDomain().getValue() instanceof Enumerable) {
            	// s := enumerate(v)
                // s := enumerate(v)/considered(beta)
            	List<Element> s = null;
            	Enumerable domain = (Enumerable)node.getDomain().getValue();
        		s = remained.get(node.getDomain());
            	if (s == null) {
            		if (domain.supportsIndexedView())
            			s = new ArrayList<Element>(domain.getIndexedView());
            		else 
                    	s = new ArrayList<Element>(((Enumerable) node.getDomain().getValue()).enumerate());
            		remained.put(node.getDomain(), s);
            	}
                if (s.size() > 0) {
                	// BSL here is where we have to modify the choice under a probability distribution
                	// BSL first, we need to obtain the probability distribution
                	MapElement distribution = (MapElement) node.getDistribution().getValue();
                	String res = distribution.isProbabilityDistribution();
                	
                	if (!res.equals(""))
                	{
                		 capi.error("Cannot choose because the given map is not a probability distribution. " +
     	                		"Reason: "+ res, node.getDistribution(), interpreter);
                		 node.setNode(null, null, null, Element.UNDEF);
                     	return node;
                	}
                	// BSL we now produce the commulative function of the distribution
                	Map<Element, Element> theMap = distribution.getMap();
                	HashMap<Pair<Double,Double>,Element> cumulativeeFunction = new HashMap<Pair<Double,Double>, Element>();
                	Double initialValue = new Double(0);
                	Double finalValue = new Double(0);
                	double total =0;
                	StringBuilder sb = new StringBuilder();
                	for(Element key : theMap.keySet())
                	{
                		if (!domain.contains(key))
                		{
                			//check if all elements in the keyset are elements of the domain
                			capi.error("We are choosing "+key+" , but the probability distribution is not defined over "+Tools.sizeLimit(domain.toString()));
                			node.setNode(null, null, null, Element.UNDEF);
                        	return node;
                		}
                		if (!s.contains(key))
                		{
                			//This happens when we try to choose an element that does not satisfy the given condition
                			//thus, we don't add it to the cumulativee function
                			continue;
                		}
                		NumberElement n = (NumberElement)theMap.get(key);
                		if(n.doubleValue()==0)
                			continue;
                		initialValue = finalValue;
                		finalValue = new Double(total+n.doubleValue());
                		total+=n.doubleValue();
                		cumulativeeFunction.put(new Pair<Double, Double>(initialValue,finalValue), key);
                		sb.append("("+initialValue.toString()+","+finalValue.toString()+")->"+key.toString());
                	}
                	if (cumulativeeFunction.isEmpty())
                	{
                		//This happens when we could not find an element that satisfied the condition and had a probability greater than 0 to be chosen
                		//Elements cannot be chosen because the ones that satisfy the condition have probability zero!
                		node.setNode(null, null, null, Element.UNDEF);
                    	return node;
                	}
            		// choose a number greater than 0 and smaller than the total
                	double d = 2;
                	while(d==0 || d>total){
                		d = Tools.randDouble();
                	}
            		//now, find the element that corresponds to that interval
                	Element chosen = null;
                	for(Pair<Double,Double> key : cumulativeeFunction.keySet())
                	{
                		if (key.l.doubleValue() < d && key.r.doubleValue() >= d)
                		{
                			chosen = cumulativeeFunction.get(key);
                			s.remove(chosen);
                			break;
                		}
                	}
                	if(chosen == null)
            		{
            			//This should not happen
            			capi.error("There is an error in the implementation of the probability distribution. "
            					+ "the chosen number was "+d+" the total is "+total+" and the map is "+sb.toString());
            			node.setNode(null, null, null, Element.UNDEF);
                    	return node;
            		}                		
                    // AddEnv(x,t)s
                    interpreter.addEnv(x, chosen);
                    // considered := considered union {t}
                	s.remove(chosen);
                    //considered.get(chooseNode.getDomain()).add(chosen);
                    // pos := gamma
                    return node.getCondition();
                }
                else {
                	remained.remove(node.getDomain());
                	// [pos] := (undef,undef, uu)
                	node.setNode(null, null, null, Element.UNDEF);
                	return node;
                }
            }
            else {
                capi.error("Cannot pick from " + Tools.sizeLimit(node.getDomain().getValue().denotation()) + ". " +
                		"Pick domain should be an enumerable element.", node.getDomain(), interpreter);
            }
    	}

    	// if domain 'E' is evaluated and condition 'C' is evaluated
    	else {
            boolean value = false;            
            if (node.getCondition().getValue() instanceof BooleanElement) {
                value = ((BooleanElement) node.getCondition().getValue()).getValue();
            }
            else {
                capi.error("Value of pick condition is not Boolean.", node.getCondition(), interpreter);
                return node;
            }
            
            if (value) {
            	Element picked = interpreter.getEnv(x);
                // RemoveEnv(x)
                interpreter.removeEnv(x);
                remained.remove(node.getDomain());
                
                // [pos] := (undef,undef, value)
                node.setNode(null, null, null, picked);
                return node;
            }
            else {
                // ClearTree(gamma)
                interpreter.clearTree(node.getCondition());
                // RemoveEnv(x)
                interpreter.removeEnv(x);
                // pos := beta
                return node.getDomain();
            }
    	}
        
        return node;
    }
    
	/*
     * Interpreting rule of the form: 'choose x in E do R'
     */
	private ASTNode interpretChooseRule_NoCondition_NoIfnone_WithDistribution(Interpreter interpreter, ASTNode pos) {
        ChooseRuleNode chooseNode = (ChooseRuleNode) pos;
        Map<String, ASTNode> variableMap = null;
        
        try {
        	variableMap = chooseNode.getVariableMap();
        }
        catch (CoreASIMError e) {
        	capi.error(e);
        	return pos;
        }
        
        // evaluate all domains
        for (ASTNode domain : variableMap.values()) {
        	if (!domain.isEvaluated())
        		return domain;
        }
        
     // evaluate probability distribution
        if (!chooseNode.getDistribution().isEvaluated())
        		return chooseNode.getDistribution();

        
    	// if rule is not evaluated
    	if (!chooseNode.getDoRule().isEvaluated()) {
    		boolean none = false;
    		for (Entry<String, ASTNode> variable : variableMap.entrySet()) {
	    		if (variable.getValue().getValue() instanceof Enumerable) {
	            	// s := enumerate(v)
	    			Enumerable domain = (Enumerable) variable.getValue().getValue();
	    			List<Element> s = null;
	    			if (domain.supportsIndexedView())
	    				s = domain.getIndexedView();
	    			else 
	    				s = new ArrayList<Element>(((Enumerable) variable.getValue().getValue()).enumerate());
	                if (s.size() > 0) 
	                {
	                	// BSL here is where we have to modify the choice under a probability distribution
	                	// BSL first, we need to obtain the probability distribution
	                	MapElement distribution = (MapElement) chooseNode.getDistribution().getValue();
	                	String res = distribution.isProbabilityDistribution();
	                	
	                	if (!res.equals(""))
	                	{
	                		 capi.error("Cannot choose because the given map is not a probability distribution. " +
	     	                		"Reason: "+ res, chooseNode.getDistribution(), interpreter);
	     	                return pos;
	                	}
	                	// BSL we now produce the commulative function of the distribution
	                	Map<Element, Element> theMap = distribution.getMap();
	                	HashMap<Pair<Double,Double>,Element> cumulativeeFunction = new HashMap<Pair<Double,Double>, Element>();
	                	Double initialValue = new Double(0);
	                	Double finalValue = new Double(0);
	                	double total =0;
	                	StringBuilder sb = new StringBuilder();
	                	for(Element key : theMap.keySet())
	                	{
	                		if (!domain.contains(key))
	                		{
	                			//check if all elements in the keyset are elements of s
	                			capi.error("We are choosing "+key+" , but the probability distribution is not defined over "+Tools.sizeLimit(variable.getValue().getValue().denotation()), variable.getValue(), interpreter);
	        	                return pos;
	                		}
	                		NumberElement n = (NumberElement)theMap.get(key);
	                		if(n.doubleValue()==0)
	                			continue;
	                		initialValue = finalValue;
	                		finalValue = new Double(total+n.doubleValue());
	                		total+=n.doubleValue();
	                		cumulativeeFunction.put(new Pair<Double, Double>(initialValue,finalValue), key);
	                		sb.append("("+initialValue.toString()+","+finalValue.toString()+")->"+key.toString());
	                	}
                		// choose a number greater than 0 and smaller than the total
	                	double d = 2;
	                	while(d==0 || d>total){
	                		d = Tools.randDouble();
	                	}
                		//now, find the element that corresponds to that interval
	                	Element chosen = null;
	                	for(Pair<Double,Double> key : cumulativeeFunction.keySet())
	                	{
	                		if (key.l.doubleValue() < d && key.r.doubleValue() >= d)
	                		{
	                			chosen = cumulativeeFunction.get(key);
	                			break;
	                		}
	                	}
	                	if(chosen == null)
                		{
                			//This should not happen
                			capi.error("There is an error in the implementation of the probability distribution. "
                					+ "the chosen number was "+d+" the total is "+total+" and the map is "+sb.toString());
        	                return pos;
                		}                		
	                	// AddEnv(x,t)s
                		interpreter.addEnv(variable.getKey(), chosen);
	                }
	                else {
	                	none = true;
	                	interpreter.addEnv(variable.getKey(), Element.UNDEF);
	                }
	            }
	            else {
	                capi.error("Cannot choose from " + Tools.sizeLimit(variable.getValue().getValue().denotation()) + ". " +
	                		"Choose domain should be an enumerable element.", variable.getValue(), interpreter);
	                return pos;
	            }
    		}
    		if (none) {
    			for (String x : variableMap.keySet())
        			interpreter.removeEnv(x);
    			// [pos] := (undef,{},undef)
                pos.setNode(null, new UpdateMultiset(), null, null);
                return pos;
    		}
    		// pos := gamma
            return chooseNode.getDoRule();
    	}
    	
    	// if rule 'R' is evaluated as well
    	else {
            // RemoveEnv(x)
    		for (String x : variableMap.keySet())
    			interpreter.removeEnv(x);
            // [pos] := (undef,u,undef)
            pos.setNode(null,chooseNode.getDoRule().getUpdates(),null, null);
            return pos;
    	}
	}
	
	/*
     * Interpreting rule of the form: 'choose x in E do R'
     */
	private ASTNode interpretChooseRule_NoCondition_NoIfnone(Interpreter interpreter, ASTNode pos) {
        ChooseRuleNode chooseNode = (ChooseRuleNode) pos;
        Map<String, ASTNode> variableMap = null;
        
        try {
        	variableMap = chooseNode.getVariableMap();
        }
        catch (CoreASIMError e) {
        	capi.error(e);
        	return pos;
        }
        
        // evaluate all domains
        for (ASTNode domain : variableMap.values()) {
        	if (!domain.isEvaluated())
        		return domain;
        }
        
    	// if rule is not evaluated
    	if (!chooseNode.getDoRule().isEvaluated()) {
    		boolean none = false;
    		for (Entry<String, ASTNode> variable : variableMap.entrySet()) {
	    		if (variable.getValue().getValue() instanceof Enumerable) {
	            	// s := enumerate(v)
	    			Enumerable domain = (Enumerable) variable.getValue().getValue();
	    			List<Element> s = null;
	    			if (domain.supportsIndexedView())
	    				s = domain.getIndexedView();
	    			else 
	    				s = new ArrayList<Element>(((Enumerable) variable.getValue().getValue()).enumerate());
	                if (s.size() > 0) {
	                    // choose t in s
	                	int i = Tools.randInt(s.size());
	                    Element chosen = s.get(i);
	                    // AddEnv(x,t)s
	                    interpreter.addEnv(variable.getKey(), chosen);
	                }
	                else {
	                	none = true;
	                	interpreter.addEnv(variable.getKey(), Element.UNDEF);
	                }
	            }
	            else {
	                capi.error("Cannot choose from " + Tools.sizeLimit(variable.getValue().getValue().denotation()) + ". " +
	                		"Choose domain should be an enumerable element.", variable.getValue(), interpreter);
	                return pos;
	            }
    		}
    		if (none) {
    			for (String x : variableMap.keySet())
        			interpreter.removeEnv(x);
    			// [pos] := (undef,{},undef)
                pos.setNode(null, new UpdateMultiset(), null, null);
                return pos;
    		}
    		// pos := gamma
            return chooseNode.getDoRule();
    	}
    	
    	// if rule 'R' is evaluated as well
    	else {
            // RemoveEnv(x)
    		for (String x : variableMap.keySet())
    			interpreter.removeEnv(x);
            // [pos] := (undef,u,undef)
            pos.setNode(null,chooseNode.getDoRule().getUpdates(),null, null);
            return pos;
    	}
	}

	
	/*
     * Interpreting rule of the form: 'choose x in E do R1 ifnone R2'
     */
    private ASTNode interpretChooseRule_NoCondition_WithIfnone(Interpreter interpreter, ASTNode pos) {
        ChooseRuleNode chooseNode = (ChooseRuleNode) pos;
        Map<String, ASTNode> variableMap = null;
        
        try {
        	variableMap = chooseNode.getVariableMap();
        }
        catch (CoreASIMError e) {
        	capi.error(e);
        	return pos;
        }
        
        // evaluate all domains
        for (ASTNode domain : variableMap.values()) {
        	if (!domain.isEvaluated())
        		return domain;
        }
        
    	// if neither of the rules 'R1' or 'R2' are evaluated
    	if (!chooseNode.getDoRule().isEvaluated() && !chooseNode.getIfnoneRule().isEvaluated()) { 
    		boolean none = false;
    		for (Entry<String, ASTNode> variable : variableMap.entrySet()) {
	    		if (variable.getValue().getValue() instanceof Enumerable) {
	            	// s := enumerate(v)
	    			Enumerable domain = (Enumerable) variable.getValue().getValue();
	    			List<Element> s = null;
	    			if (domain.supportsIndexedView())
	    				s = domain.getIndexedView();
	    			else 
	    				s = new ArrayList<Element>(((Enumerable) variable.getValue().getValue()).enumerate());
	                if (s.size() > 0) {
	                    // choose t in s
	                	int i = Tools.randInt(s.size());
	                    Element chosen = s.get(i);
	                    // AddEnv(x,t)s
	                    interpreter.addEnv(variable.getKey(), chosen);
	                }
	                else {
	                	none = true;
	                	interpreter.addEnv(variable.getKey(), Element.UNDEF);
	                }
	            }
	            else {
	                capi.error("Cannot choose from " + Tools.sizeLimit(variable.getValue().getValue().denotation()) + ". " +
	                		"Choose domain should be an enumerable element.", variable.getValue(), interpreter);
	                return pos;
	            }
    		}
    		if (none) {
    			// RemoveEnv(x)
        		for (String x : variableMap.keySet())
        			interpreter.removeEnv(x);
    			// pos := delta
                return chooseNode.getIfnoneRule();
    		}
    		 // pos := gamma
            return chooseNode.getDoRule();
    	}

    	// if rule 'R1' is evaluated 
    	else if (chooseNode.getDoRule().isEvaluated()) {
            // RemoveEnv(x)
    		for (String x : variableMap.keySet())
    			interpreter.removeEnv(x);
            // [pos] := (undef,u,undef)
            pos.setNode(null,chooseNode.getDoRule().getUpdates(),null, null);
            return pos;
    	}
    	
    	// if rule 'R2' is evaluated
    	else {
            // [pos] := (undef,u,undef)
            pos.setNode(null,chooseNode.getIfnoneRule().getUpdates(),null, null);
            return pos;
    	}
	}
    
    /*
     * Interpreting rule of the form: 'choose x in E using D do R1 ifnone R2'
     */
    private ASTNode interpretChooseRule_NoCondition_WithIfnone_WithDistribution(Interpreter interpreter, ASTNode pos) {
        ChooseRuleNode chooseNode = (ChooseRuleNode) pos;
        Map<String, ASTNode> variableMap = null;
        
        try {
        	variableMap = chooseNode.getVariableMap();
        }
        catch (CoreASIMError e) {
        	capi.error(e);
        	return pos;
        }
        
        // evaluate all domains
        for (ASTNode domain : variableMap.values()) {
        	if (!domain.isEvaluated())
        		return domain;
        }
        
        // evaluate probability distribution
        if (!chooseNode.getDistribution().isEvaluated())
        		return chooseNode.getDistribution();
        
    	// if neither of the rules 'R1' or 'R2' are evaluated
    	if (!chooseNode.getDoRule().isEvaluated() && !chooseNode.getIfnoneRule().isEvaluated()) { 
    		boolean none = false;
    		for (Entry<String, ASTNode> variable : variableMap.entrySet()) {
	    		if (variable.getValue().getValue() instanceof Enumerable) {
	            	// s := enumerate(v)
	    			Enumerable domain = (Enumerable) variable.getValue().getValue();
	    			List<Element> s = null;
	    			if (domain.supportsIndexedView())
	    				s = domain.getIndexedView();
	    			else 
	    				s = new ArrayList<Element>(((Enumerable) variable.getValue().getValue()).enumerate());
	                if (s.size() > 0) {
	                	// BSL here is where we have to modify the choice under a probability distribution
	                	// BSL first, we need to obtain the probability distribution
	                	MapElement distribution = (MapElement) chooseNode.getDistribution().getValue();
	                	String res = distribution.isProbabilityDistribution();
	                	
	                	if (!res.equals(""))
	                	{
	                		 capi.error("Cannot choose because the given map is not a probability distribution. " +
	     	                		"Reason: "+ res, chooseNode.getDistribution(), interpreter);
	     	                return pos;
	                	}
	                	// BSL we now produce the commulative function of the distribution
	                	Map<Element, Element> theMap = distribution.getMap();
	                	HashMap<Pair<Double,Double>,Element> cumulativeeFunction = new HashMap<Pair<Double,Double>, Element>();
	                	Double initialValue = new Double(0);
	                	Double finalValue = new Double(0);
	                	double total =0;
	                	StringBuilder sb = new StringBuilder();
	                	for(Element key : theMap.keySet())
	                	{
	                		if (!domain.contains(key))
	                		{
	                			//check if all elements in the keyset are elements of s
	                			capi.error("We are choosing "+key+" , but the probability distribution is not defined over "+Tools.sizeLimit(variable.getValue().getValue().denotation()), variable.getValue(), interpreter);
	        	                return pos;
	                		}
	                		NumberElement n = (NumberElement)theMap.get(key);
	                		if(n.doubleValue()==0)
	                			continue;
	                		initialValue = finalValue;
	                		finalValue = new Double(total+n.doubleValue());
	                		total+=n.doubleValue();
	                		cumulativeeFunction.put(new Pair<Double, Double>(initialValue,finalValue), key);
	                		sb.append("("+initialValue.toString()+","+finalValue.toString()+")->"+key.toString());
	                	}
                		// choose a number greater than 0 and smaller than the total
	                	double d = 2;
	                	while(d==0 || d>total){
	                		d = Tools.randDouble();
	                	}
                		//now, find the element that corresponds to that interval
	                	Element chosen = null;
	                	for(Pair<Double,Double> key : cumulativeeFunction.keySet())
	                	{
	                		if (key.l.doubleValue() < d && key.r.doubleValue() >= d)
	                		{
	                			chosen = cumulativeeFunction.get(key);
	                			break;
	                		}
	                	}
	                	if(chosen == null)
                		{
                			//This should not happen
                			capi.error("There is an error in the implementation of the probability distribution. "
                					+ "the chosen number was "+d+" the total is "+total+" and the map is "+sb.toString());
        	                return pos;
                		}                		
	                	// AddEnv(x,t)s
                		interpreter.addEnv(variable.getKey(), chosen);
	                }
	                else {
	                	none = true;
	                	interpreter.addEnv(variable.getKey(), Element.UNDEF);
	                }
	            }
	            else {
	                capi.error("Cannot choose from " + Tools.sizeLimit(variable.getValue().getValue().denotation()) + ". " +
	                		"Choose domain should be an enumerable element.", variable.getValue(), interpreter);
	                return pos;
	            }
    		}
    		if (none) {
    			// RemoveEnv(x)
        		for (String x : variableMap.keySet())
        			interpreter.removeEnv(x);
    			// pos := delta
                return chooseNode.getIfnoneRule();
    		}
    		 // pos := gamma
            return chooseNode.getDoRule();
    	}

    	// if rule 'R1' is evaluated 
    	else if (chooseNode.getDoRule().isEvaluated()) {
            // RemoveEnv(x)
    		for (String x : variableMap.keySet())
    			interpreter.removeEnv(x);
            // [pos] := (undef,u,undef)
            pos.setNode(null,chooseNode.getDoRule().getUpdates(),null, null);
            return pos;
    	}
    	
    	// if rule 'R2' is evaluated
    	else {
            // [pos] := (undef,u,undef)
            pos.setNode(null,chooseNode.getIfnoneRule().getUpdates(),null, null);
            return pos;
    	}
	}


	/*
     * Interpreting rule of the form: 'choose x in E with C do R'
     */
	private ASTNode interpretChooseRule_WithCondition_NoIfnone(Interpreter interpreter, ASTNode pos) {
        ChooseRuleNode chooseNode = (ChooseRuleNode) pos;
        Map<Node, List<Element>> remained = getRemainedMap();
        Map<String, ASTNode> variableMap = null;
        
        try {
        	variableMap = chooseNode.getVariableMap();
        }
        catch (CoreASIMError e) {
        	capi.error(e);
        	return pos;
        }
        
        // evaluate all domains
        for (ASTNode domain : variableMap.values()) {
        	if (!domain.isEvaluated()) {
        		// considered(beta) := {}
        		remained.remove(domain);
        		// pos := beta
        		return domain;
        	}
        }
        
    	// if condition 'C' is not evaluated
    	if (!chooseNode.getCondition().isEvaluated())
    		return chooseVariableValues_WithCondition(chooseNode, remained, variableMap, interpreter);

    	// if domain 'E' is evaluated, condition 'C' is evaluated, but rule 'R' is not evaluated
    	else if (!chooseNode.getDoRule().isEvaluated()) {
            boolean value = false;            
            if (chooseNode.getCondition().getValue() instanceof BooleanElement) {
                value = ((BooleanElement) chooseNode.getCondition().getValue()).getValue();
            }
            else {
                capi.error("Value of choose condition is not Boolean.", chooseNode.getCondition(), interpreter);
                return pos;
            }
            
            if (value) {
                // pos := delta
                return chooseNode.getDoRule();
            }
            else {
                // ClearTree(gamma)
                interpreter.clearTree(chooseNode.getCondition());
                
                return chooseNode;
            }
    	}
        
    	// if domain 'E' is evaluated, condition 'C' is evaluated, and rule 'R' is evaluated
    	else {
            // RemoveEnv(x)
    		for (Entry<String, ASTNode> variable : variableMap.entrySet()) {
    			if (remained.remove(variable.getValue()) != null)
    				interpreter.removeEnv(variable.getKey());
    		}
            
            // [pos] := (undef,u,undef)
            pos.setNode(null,chooseNode.getDoRule().getUpdates(),null,null);
            return pos;
    	}
	}
	
	/*
     * Interpreting rule of the form: 'choose x in E with C using D do R'
     */
	private ASTNode interpretChooseRule_WithCondition_NoIfnone_WithDistribution(Interpreter interpreter, ASTNode pos) {
        ChooseRuleNode chooseNode = (ChooseRuleNode) pos;
        Map<Node, List<Element>> remained = getRemainedMap();
        Map<String, ASTNode> variableMap = null;
        
        try {
        	variableMap = chooseNode.getVariableMap();
        }
        catch (CoreASIMError e) {
        	capi.error(e);
        	return pos;
        }
        
        // evaluate all domains
        for (ASTNode domain : variableMap.values()) {
        	if (!domain.isEvaluated()) {
        		// considered(beta) := {}
        		remained.remove(domain);
        		// pos := beta
        		return domain;
        	}
        }
        
        // evaluate probability distribution
        if (!chooseNode.getDistribution().isEvaluated())
        		return chooseNode.getDistribution();
        
    	// if condition 'C' is not evaluated
    	if (!chooseNode.getCondition().isEvaluated())
    		return chooseVariableValues_WithCondition_WithDistribution(chooseNode, remained, variableMap, interpreter);

    	// if domain 'E' is evaluated, condition 'C' is evaluated, but rule 'R' is not evaluated
    	else if (!chooseNode.getDoRule().isEvaluated()) {
            boolean value = false;            
            if (chooseNode.getCondition().getValue() instanceof BooleanElement) {
                value = ((BooleanElement) chooseNode.getCondition().getValue()).getValue();
            }
            else {
                capi.error("Value of choose condition is not Boolean.", chooseNode.getCondition(), interpreter);
                return pos;
            }
            
            if (value) {
                // pos := delta
                return chooseNode.getDoRule();
            }
            else {
                // ClearTree(gamma)
                interpreter.clearTree(chooseNode.getCondition());
                
                return chooseNode;
            }
    	}
        
    	// if domain 'E' is evaluated, condition 'C' is evaluated, and rule 'R' is evaluated
    	else {
            // RemoveEnv(x)
    		for (Entry<String, ASTNode> variable : variableMap.entrySet()) {
    			if (remained.remove(variable.getValue()) != null)
    				interpreter.removeEnv(variable.getKey());
    		}
            
            // [pos] := (undef,u,undef)
            pos.setNode(null,chooseNode.getDoRule().getUpdates(),null,null);
            return pos;
    	}
	}


	/*
     * Interpreting rule of the form: 'choose x in E with C do R1 ifnone R2'
     */
    private ASTNode interpretChooseRule_WithCondition_WithIfnone(Interpreter interpreter, ASTNode pos) {
        ChooseRuleNode chooseNode = (ChooseRuleNode) pos;
        Map<Node, List<Element>> remained = getRemainedMap();
        Map<String, ASTNode> variableMap = null;
        
        try {
        	variableMap = chooseNode.getVariableMap();
        }
        catch (CoreASIMError e) {
        	capi.error(e);
        	return pos;
        }
        
        // evaluate all domains
        for (ASTNode domain : variableMap.values()) {
        	if (!domain.isEvaluated()) {
        		// considered(beta) := {}
        		remained.remove(domain);
        		// pos := beta
        		return domain;
        	}
        }

    	// if condition 'C' is not evaluated
    	if (!chooseNode.getCondition().isEvaluated() && !chooseNode.getIfnoneRule().isEvaluated())
    		return chooseVariableValues_WithCondition(chooseNode, remained, variableMap, interpreter);

    	// if domain 'E' is evaluated, condition 'C' is evaluated, but neither of the rules 'R1' or 'R2' are evaluated
    	else if (chooseNode.getCondition().isEvaluated() && !chooseNode.getDoRule().isEvaluated() && !chooseNode.getIfnoneRule().isEvaluated()) {
            boolean value = false;            
            if (chooseNode.getCondition().getValue() instanceof BooleanElement) {
                value = ((BooleanElement) chooseNode.getCondition().getValue()).getValue();
            }
            else {
                capi.error("Value of choose condition not Boolean", chooseNode.getCondition(), interpreter);
                return pos;
            }
            
            if (value) {
                // pos := delta
                return chooseNode.getDoRule();
            }
            else {
                // ClearTree(gamma)
                interpreter.clearTree(chooseNode.getCondition());
                
                return chooseNode;
            }
    	}
        
    	// if domain 'E' is evaluated, condition 'C' is evaluated, and rule 'R1' is evaluated
    	else if (chooseNode.getCondition().isEvaluated() && chooseNode.getDoRule().isEvaluated()) {
            // RemoveEnv(x)
    		for (Entry<String, ASTNode> variable : variableMap.entrySet()) {
    			if (remained.remove(variable.getValue()) != null)
    				interpreter.removeEnv(variable.getKey());
    		}
            
            // [pos] := (undef,u,undef)
            pos.setNode(null,chooseNode.getDoRule().getUpdates(),null, null);
            return pos;
    	}

    	// if domain 'E' is evaluated and rule 'R2' is evaluated
    	else if (chooseNode.getIfnoneRule().isEvaluated()) {
    		// RemoveEnv(x)
    		for (Entry<String, ASTNode> variable : variableMap.entrySet()) {
    			if (remained.remove(variable.getValue()) != null)
    				interpreter.removeEnv(variable.getKey());
    		}
            
            // [pos] := (undef,u,undef)
            pos.setNode(null,chooseNode.getIfnoneRule().getUpdates(),null,null);
            return pos;
    	}
        
        // in case of error
        return pos;
	}
    
    /*
     * Interpreting rule of the form: 'choose x in E with C do R1 ifnone R2'
     */
    private ASTNode interpretChooseRule_WithCondition_WithIfnone_WithDistribution(Interpreter interpreter, ASTNode pos) {
        ChooseRuleNode chooseNode = (ChooseRuleNode) pos;
        Map<Node, List<Element>> remained = getRemainedMap();
        Map<String, ASTNode> variableMap = null;
        
        try {
        	variableMap = chooseNode.getVariableMap();
        }
        catch (CoreASIMError e) {
        	capi.error(e);
        	return pos;
        }
        
        // evaluate all domains
        for (ASTNode domain : variableMap.values()) {
        	if (!domain.isEvaluated()) {
        		// considered(beta) := {}
        		remained.remove(domain);
        		// pos := beta
        		return domain;
        	}
        }
        
        // evaluate probability distribution
        if (!chooseNode.getDistribution().isEvaluated())
        		return chooseNode.getDistribution();

    	// if condition 'C' is not evaluated
    	if (!chooseNode.getCondition().isEvaluated() && !chooseNode.getIfnoneRule().isEvaluated())
    		return chooseVariableValues_WithCondition_WithDistribution(chooseNode, remained, variableMap, interpreter);

    	// if domain 'E' is evaluated, condition 'C' is evaluated, but neither of the rules 'R1' or 'R2' are evaluated
    	else if (chooseNode.getCondition().isEvaluated() && !chooseNode.getDoRule().isEvaluated() && !chooseNode.getIfnoneRule().isEvaluated()) {
            boolean value = false;            
            if (chooseNode.getCondition().getValue() instanceof BooleanElement) {
                value = ((BooleanElement) chooseNode.getCondition().getValue()).getValue();
            }
            else {
                capi.error("Value of choose condition not Boolean", chooseNode.getCondition(), interpreter);
                return pos;
            }
            
            if (value) {
                // pos := delta
                return chooseNode.getDoRule();
            }
            else {
                // ClearTree(gamma)
                interpreter.clearTree(chooseNode.getCondition());
                
                return chooseNode;
            }
    	}
        
    	// if domain 'E' is evaluated, condition 'C' is evaluated, and rule 'R1' is evaluated
    	else if (chooseNode.getCondition().isEvaluated() && chooseNode.getDoRule().isEvaluated()) {
            // RemoveEnv(x)
    		for (Entry<String, ASTNode> variable : variableMap.entrySet()) {
    			if (remained.remove(variable.getValue()) != null)
    				interpreter.removeEnv(variable.getKey());
    		}
            
            // [pos] := (undef,u,undef)
            pos.setNode(null,chooseNode.getDoRule().getUpdates(),null, null);
            return pos;
    	}

    	// if domain 'E' is evaluated and rule 'R2' is evaluated
    	else if (chooseNode.getIfnoneRule().isEvaluated()) {
    		// RemoveEnv(x)
    		for (Entry<String, ASTNode> variable : variableMap.entrySet()) {
    			if (remained.remove(variable.getValue()) != null)
    				interpreter.removeEnv(variable.getKey());
    		}
            
            // [pos] := (undef,u,undef)
            pos.setNode(null,chooseNode.getIfnoneRule().getUpdates(),null,null);
            return pos;
    	}
        
        // in case of error
        return pos;
	}
    
    private ASTNode chooseVariableValues_WithCondition(ChooseRuleNode chooseNode, Map<Node, List<Element>> remained, Map<String, ASTNode> variableMap, Interpreter interpreter) {
    	// pos := gamma
    	ASTNode pos = chooseNode.getCondition();
    	boolean shouldChoose = true;
		for (Entry<String, ASTNode> variable : variableMap.entrySet()) {
    		if (variable.getValue().getValue() instanceof Enumerable) {
                // s := enumerate(v)/considered(beta)
            	List<Element> s = remained.get(variable.getValue());
                if (s == null) {
        			Enumerable domain = (Enumerable) variable.getValue().getValue();
        			if (domain.supportsIndexedView())
        				s = new ArrayList<Element>(domain.getIndexedView());
        			else 
        				s = new ArrayList<Element>(((Enumerable) variable.getValue().getValue()).enumerate());
                	if (s.isEmpty()) {
                		if (chooseNode.getIfnoneRule() == null) {
                			for (Entry<String, ASTNode> var : variableMap.entrySet()) {
            	    			if (remained.remove(var.getValue()) != null)
            	    				interpreter.removeEnv(var.getKey());
            	    		}
            				// [pos] := (undef,{},undef)
                			chooseNode.setNode(null, new UpdateMultiset(), null, null);
            	            return chooseNode;
            			}
                		// pos := delta
                        pos = chooseNode.getIfnoneRule();
                        interpreter.addEnv(variable.getKey(), Element.UNDEF);
                	}
                	remained.put(variable.getValue(), s);
                	shouldChoose = true;
                }
                else if (shouldChoose)
                	interpreter.removeEnv(variable.getKey());
                if (shouldChoose) {
	                if (!s.isEmpty()) {
	                    // choose t in s
	                    Element chosen = s.remove(Tools.randInt(s.size()));
	                    // AddEnv(x,t)s
	                    interpreter.addEnv(variable.getKey(), chosen);
	            	}
	            	else {
	            		remained.remove(variable.getValue());
	            		if (pos != chooseNode.getIfnoneRule())
	            			pos = chooseNode;
	            		shouldChoose = true;
	            		continue;
	            	}
                }
            }
            else {
                capi.error("Cannot choose from " + Tools.sizeLimit(variable.getValue().getValue().denotation()) + ". " +
                		"Choose domain should be an enumerable element.", variable.getValue(), interpreter);
                return pos;
            }
    		shouldChoose = false;
		}
		if (shouldChoose) {
			if (chooseNode.getIfnoneRule() == null) {
				// [pos] := (undef,{},undef)
				chooseNode.setNode(null, new UpdateMultiset(),null, null);
	            return chooseNode;
			}
			// pos := delta
            pos = chooseNode.getIfnoneRule();
		}
		if (pos == chooseNode.getIfnoneRule()) {
			// RemoveEnv(x)
			for (Entry<String, ASTNode> var : variableMap.entrySet()) {
    			if (remained.remove(var.getValue()) != null)
    				interpreter.removeEnv(var.getKey());
    		}
		}
        return pos;
    }
    
    private ASTNode chooseVariableValues_WithCondition_WithDistribution(ChooseRuleNode chooseNode, Map<Node, List<Element>> remained, Map<String, ASTNode> variableMap, Interpreter interpreter) {
    	// pos := gamma
    	ASTNode pos = chooseNode.getCondition();
    	boolean shouldChoose = true;
		for (Entry<String, ASTNode> variable : variableMap.entrySet()) {
    		if (variable.getValue().getValue() instanceof Enumerable) {
                // s := enumerate(v)/considered(beta)
            	List<Element> s = remained.get(variable.getValue());
            	 //This code checks whether the domains are non empty.
                if (s == null) {
        			Enumerable domain = (Enumerable) variable.getValue().getValue();
        			if (domain.supportsIndexedView())
        				s = new ArrayList<Element>(domain.getIndexedView());
        			else 
        				s = new ArrayList<Element>(((Enumerable) variable.getValue().getValue()).enumerate());
                	if (s.isEmpty()) {
                		if (chooseNode.getIfnoneRule() == null) {
                			for (Entry<String, ASTNode> var : variableMap.entrySet()) {
            	    			if (remained.remove(var.getValue()) != null)
            	    				interpreter.removeEnv(var.getKey());
            	    		}
            				// [pos] := (undef,{},undef)
                			chooseNode.setNode(null, new UpdateMultiset(), null, null);
            	            return chooseNode;
            			}
                		// pos := delta
                        pos = chooseNode.getIfnoneRule();
                        interpreter.addEnv(variable.getKey(), Element.UNDEF);
                	}
                	remained.put(variable.getValue(), s);
                	shouldChoose = true;
                }
               //This verifies if there are still domains where we can choose from
                else if (shouldChoose)
                	interpreter.removeEnv(variable.getKey());
                if (shouldChoose) {
                	//If the domain is not empty...
	                if (!s.isEmpty())
	                {
	                	// BSL here is where we have to modify the choice under a probability distribution
	                	// BSL first, we need to obtain the probability distribution
	                	MapElement distribution = (MapElement) chooseNode.getDistribution().getValue();
	                	String res = distribution.isProbabilityDistribution();
	                	
	                	if (!res.equals(""))
	                	{
	                		 capi.error("Cannot choose because the given map is not a probability distribution. " +
	     	                		"Reason: "+ res, chooseNode.getDistribution(), interpreter);
	     	                return pos;
	                	}
	                	// BSL we now produce the commulative function of the distribution
	                	Map<Element, Element> theMap = distribution.getMap();
	                	HashMap<Pair<Double,Double>,Element> cumulativeeFunction = new HashMap<Pair<Double,Double>, Element>();
	                	Double initialValue = new Double(0);
	                	Double finalValue = new Double(0);
	                	double total =0;
	                	StringBuilder sb = new StringBuilder();
	                	Enumerable domain = (Enumerable) variable.getValue().getValue();
	                	for(Element key : theMap.keySet())
	                	{
	                		if (!domain.contains(key))
	                		{
	                			//check if all elements in the keyset are elements of the domain
	                			capi.error("We are choosing "+key+" , but the probability distribution is not defined over "+Tools.sizeLimit(variable.getValue().getValue().denotation()), variable.getValue(), interpreter);
	        	                return pos;
	                		}
	                		if (!s.contains(key))
	                		{
	                			//This happens when we try to choose an element that does not satisfy the given condition
	                			//thus, we don't add it to the cumulativee function
	                			continue;
	                		}
	                		NumberElement n = (NumberElement)theMap.get(key);
	                		if(n.doubleValue()==0)
	                			continue;
	                		initialValue = finalValue;
	                		finalValue = new Double(total+n.doubleValue());
	                		total+=n.doubleValue();
	                		cumulativeeFunction.put(new Pair<Double, Double>(initialValue,finalValue), key);
	                		sb.append("("+initialValue.toString()+","+finalValue.toString()+")->"+key.toString());
	                	}
	                	if (cumulativeeFunction.isEmpty())
	                	{
	                		//This happens when we could not find an element that satisfied the condition and had a probability greater than 0 to be chosen
	                		//Elements cannot be chosen because the ones that satisfy the condition have probability zero!
	                		remained.remove(variable.getValue());
		            		if (pos != chooseNode.getIfnoneRule())
		            			pos = chooseNode;
		            		shouldChoose = true;
		            		continue;
	                	}
                		// choose a number greater than 0 and smaller than the total
	                	double d = 2;
	                	while(d==0 || d>total){
	                		d = Tools.randDouble();
	                	}
                		//now, find the element that corresponds to that interval
	                	Element chosen = null;
	                	for(Pair<Double,Double> key : cumulativeeFunction.keySet())
	                	{
	                		if (key.l.doubleValue() < d && key.r.doubleValue() >= d)
	                		{
	                			chosen = cumulativeeFunction.get(key);
	                			s.remove(chosen);
	                			break;
	                		}
	                	}
	                	if(chosen == null)
                		{
                			//This should not happen
                			capi.error("There is an error in the implementation of the probability distribution. "
                					+ "the chosen number was "+d+" the total is "+total+" and the map is "+sb.toString());
        	                return pos;
                		}                		
	                	// AddEnv(x,t)s
                		interpreter.addEnv(variable.getKey(), chosen);
	                }
//	                {
//	                    // choose t in s
//	                    Element chosen = s.remove(Tools.randInt(s.size()));
//	                    // AddEnv(x,t)s
//	                    interpreter.addEnv(variable.getKey(), chosen);
//	            	}
	            	else {
	            		remained.remove(variable.getValue());
	            		if (pos != chooseNode.getIfnoneRule())
	            			pos = chooseNode;
	            		shouldChoose = true;
	            		continue;
	            	}
                }
            }
            else {
                capi.error("Cannot choose from " + Tools.sizeLimit(variable.getValue().getValue().denotation()) + ". " +
                		"Choose domain should be an enumerable element.", variable.getValue(), interpreter);
                return pos;
            }
    		shouldChoose = false;
		}
		if (shouldChoose) {
			if (chooseNode.getIfnoneRule() == null) {
				// [pos] := (undef,{},undef)
				chooseNode.setNode(null, new UpdateMultiset(),null, null);
	            return chooseNode;
			}
			// pos := delta
            pos = chooseNode.getIfnoneRule();
		}
		if (pos == chooseNode.getIfnoneRule()) {
			// RemoveEnv(x)
			for (Entry<String, ASTNode> var : variableMap.entrySet()) {
    			if (remained.remove(var.getValue()) != null)
    				interpreter.removeEnv(var.getKey());
    		}
		}
        return pos;
    }

	public VersionInfo getVersionInfo() {
		return VERSION_INFO;
	}

	/**
	 * Mapping of node elements into the the choose rule node.
	 *   
	 * @author Roozbeh Farahbod
	 */
	public static class ChooseParseMap extends ParserTools.ArrayParseMap {

	    String nextChildName = "alpha";

	    public ChooseParseMap() {
			super(PLUGIN_NAME);
		}
		
		public Node map(Object[] v) {
			nextChildName = "alpha";
			ASTNode node = new ChooseRuleNode(((Node)v[0]).getScannerInfo());

			addChildren(node, v);
			return node;
		}
		
		public void addChild(Node parent, Node child) {
			if (child instanceof ASTNode)
				parent.addChild(nextChildName, child);
			else {
				String token = child.getToken();
		        if (token.equals("with"))
		        	nextChildName = ChooseRulePlugin.GUARD_NAME;
		        else if (token.equals("using"))
		        	nextChildName = ChooseRulePlugin.DISTRIBUTION_NAME;
		        else if (token.equals("do"))
		        	nextChildName = ChooseRulePlugin.DO_RULE_NAME;
		        else if (token.equals("ifnone"))
		        	nextChildName = ChooseRulePlugin.IFNONE_RULE_NAME;
				parent.addChild(child);
		        //super.addChild(parent, child);
			}
		}

	}
	
	/**
	 * Mapping of node elements into the the choose rule node.
	 *   
	 * @author Roozbeh Farahbod
	 */
	public static class PickParseMap extends ParserTools.ArrayParseMap {

	    String nextChildName = "alpha";

	    public PickParseMap() {
			super(PLUGIN_NAME);
		}
		
		public Node map(Object[] v) {
			nextChildName = "alpha";
			ASTNode node = new PickExpNode(((Node)v[0]).getScannerInfo());

			addChildren(node, v);
			return node;
		}
		
		public void addChild(Node parent, Node child) {
			if (child instanceof ASTNode)
				parent.addChild(nextChildName, child);
			else {
				String token = child.getToken();
		        if (token.equals("with"))
		        	nextChildName = ChooseRulePlugin.GUARD_NAME;
		        else if (token.equals("using"))
		        	nextChildName = ChooseRulePlugin.DISTRIBUTION_NAME;
				parent.addChild(child);
		        //super.addChild(parent, child);
			}
		}

	}
	
	public class Pair<L,R> {
	    private L l;
	    private R r;
	    public Pair(L l, R r){
	        this.l = l;
	        this.r = r;
	    }
	    public L getL(){ return l; }
	    public R getR(){ return r; }
	    public void setL(L l){ this.l = l; }
	    public void setR(R r){ this.r = r; }
	}
}
