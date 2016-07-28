package org.coreasim.engine.plugins.blockpolicy;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.coreasim.compiler.interfaces.CompilerPlugin;
import org.coreasim.compiler.plugins.blockpolicy.CompilerBlockPolicyPlugin;
import org.coreasim.engine.EngineTools;
import org.coreasim.engine.VersionInfo;
import org.coreasim.engine.absstorage.TriggerMultiset;
import org.coreasim.engine.interpreter.ASTNode;
import org.coreasim.engine.interpreter.Interpreter;
import org.coreasim.engine.interpreter.Node;
import org.coreasim.engine.kernel.KernelServices;
import org.coreasim.engine.parser.GrammarRule;
import org.coreasim.engine.parser.ParserTools;
import org.coreasim.engine.plugin.InterpreterPlugin;
import org.coreasim.engine.plugin.ParserPlugin;
import org.coreasim.engine.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * Plugin for BlockRule construct:
 *    par
 *       rule_1
 *       rule_2
 *       ...
 *       rule_n
 *    endpar
 *   
 *  @author  George Ma, Roozbeh Farahbod
 *  
 */

public class BlockPolicyPlugin extends Plugin 
		implements InterpreterPlugin, ParserPlugin {
 
	private static final Logger logger = LoggerFactory.getLogger(BlockPolicyPlugin.class);
	
	public static final VersionInfo VERSION_INFO = new VersionInfo(1, 0, 1, "");
	
	public static final String PLUGIN_NAME = BlockPolicyPlugin.class.getSimpleName();

	private Map<String, GrammarRule> parsers = null;
	
	private final String[] keywords = {"par", "endpar"};
	private final String[] operators = {"{", "}"};
	
	private final CompilerPlugin compilerPlugin = new CompilerBlockPolicyPlugin(this);
	
    public ASTNode interpret(Interpreter interpreter, ASTNode pos) {
        String gRule = pos.getGrammarRule();
        
        if ((gRule != null) && (gRule.equals("BlockPolicy"))) {
            ASTNode currentPolicy = pos.getFirst();
   
            // check if all policies in the block have been
            // interpreted.  if not, interpret them by
            // giving the uninterpreted policy node back to the
            // interpreter
            while (currentPolicy != null) {
                if (!currentPolicy.isEvaluated()) {
                    return currentPolicy;
                }
                currentPolicy = currentPolicy.getNext();
            }

            // all policys have been evaluated.
            // accumulate all the updates for this block
            currentPolicy = pos.getFirst();
            TriggerMultiset triggers = new TriggerMultiset();
            
            while (currentPolicy != null) {
            	// TODO A decision needs to be made on the following pattern
            	//      Do we want to have this pattern in other plugins as well?
            	//TODO BSL fix this to hasTriggers
            	if (!EngineTools.hasTriggers(interpreter, currentPolicy, capi, logger)) {
        			return pos;
            	} else {
            		triggers.addAll(currentPolicy.getTriggers());
            		currentPolicy = currentPolicy.getNext();
            	}
            }
            
            // set the TriggerMultiset for this node
            pos.setNode(null,null,triggers,null);
            return pos;
        }
        else {
            return null;
        }
    }

    @Override
    public void initialize() {
        
    }

	public VersionInfo getVersionInfo() {
		return VERSION_INFO;
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

	public Map<String, GrammarRule> getParsers() {
		if (parsers == null) {
			parsers = new HashMap<String, GrammarRule>();
			Parser<Node> policyParser = 
				((KernelServices)capi.getPlugin("Kernel").getPluginInterface()).getPolicyParser();
			
			ParserTools pTools = ParserTools.getInstance(capi);
			
			Parser<Node> blockPolicyParser = Parsers.array(  
						Parsers.or(
								pTools.getKeywParser("par", this.getName()),
								pTools.getOprParser("{")),
						pTools.plus(policyParser),
						Parsers.or(
								pTools.getKeywParser("endpar", getName()),
								pTools.getOprParser("}"))
					).map(new ParserTools.ArrayParseMap(PLUGIN_NAME) {

						@Override
						public Node map(Object[] from) {
							ASTNode node = new ASTNode(
									BlockPolicyPlugin.PLUGIN_NAME,
									ASTNode.POLICY_CLASS,
									"BlockPolicy",
									null,
									((Node)from[0]).getScannerInfo());
							addChildren(node, from);
							return node;
						}
						
						public void addChild(Node parent, Node child) {
							if (child instanceof ASTNode)
								parent.addChild("lambda", child);
							else
								parent.addChild(child); //super.addChild(parent, child);
						}
					});
			parsers.put("Policy", 
					new GrammarRule("Policy",
							"'par' Policy+ 'endpar'", blockPolicyParser, this.getName()));
			
		}
		return parsers;
	}


	public String[] getKeywords() {
		return keywords;
	}

	public String[] getOperators() {
		return operators;
	}

	@Override
	public CompilerPlugin getCompilerPlugin(){
		return compilerPlugin;
	}
}
