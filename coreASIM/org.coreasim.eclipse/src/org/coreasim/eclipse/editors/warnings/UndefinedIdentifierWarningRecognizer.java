package org.coreasim.eclipse.editors.warnings;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.coreasim.eclipse.editors.ASMDeclarationWatcher;
import org.coreasim.eclipse.editors.ASMDocument;
import org.coreasim.eclipse.editors.ASMEditor;
import org.coreasim.eclipse.editors.ASMDeclarationWatcher.Declaration;
import org.coreasim.engine.Specification.FunctionInfo;
import org.coreasim.engine.interpreter.ASTNode;
import org.coreasim.engine.interpreter.FunctionRulePolicyTermNode;
import org.coreasim.engine.interpreter.Node;
import org.coreasim.engine.kernel.Kernel;
import org.coreasim.engine.kernel.MacroCallRuleNode;
import org.coreasim.engine.kernel.RuleOrFuncElementNode;
import org.coreasim.engine.plugins.modularity.IncludeNode;
import org.coreasim.engine.plugins.signature.DerivedFunctionNode;
import org.coreasim.engine.plugins.signature.EnumerationElement;
import org.coreasim.engine.plugins.signature.EnumerationNode;
import org.coreasim.engine.plugins.signature.FunctionNode;
import org.coreasim.engine.plugins.signature.UniverseNode;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

/**
 * This WarningRecognizer searches an ASM document for undefined identifiers.
 * @author Michael Stegmaier
 *
 */
public class UndefinedIdentifierWarningRecognizer implements IWarningRecognizer {
	private final ASMEditor parentEditor;
	private Set<String> pluginFunctionNames;

	public UndefinedIdentifierWarningRecognizer(ASMEditor parentEditor) {
		this.parentEditor = parentEditor;
	}
	
	@Override
	public List<AbstractWarning> checkForWarnings(ASMDocument document) {
		List<AbstractWarning> warnings = new LinkedList<AbstractWarning>();
		Set<String> functionNames = getDeclaredNames(document);
		Stack<ASTNode> fringe = new Stack<ASTNode>();
		
		pluginFunctionNames = null;
		
		for (ASTNode declarationNode = ((ASTNode)document.getRootnode()).getFirst(); declarationNode != null; declarationNode = declarationNode.getNext()) {
			if (ASTNode.DECLARATION_CLASS.equals(declarationNode.getGrammarClass())) {
				if (Kernel.GR_RULEDECLARATION.equals(declarationNode.getGrammarRule())
				|| "Signature".equals(declarationNode.getGrammarRule()) && declarationNode.getFirst() instanceof DerivedFunctionNode) {
					fringe.add(declarationNode);
					while (!fringe.isEmpty()) {
						ASTNode node = fringe.pop();
						if (ASTNode.FUNCTION_RULE_POLICY_CLASS.equals(node.getGrammarClass()) && node instanceof FunctionRulePolicyTermNode) {
							if (node.getParent() instanceof MacroCallRuleNode)	// Undefined rules cause an error
								break;
							FunctionRulePolicyTermNode frNode = (FunctionRulePolicyTermNode)node;
							if (frNode.hasName()) {
								if (!ASMDeclarationWatcher.isEnvironmentVariable(frNode) && !isFunctionName(frNode.getName(), functionNames) && !ASMDeclarationWatcher.isLocalFunction(frNode))
									warnings.add(new UndefinedIdentifierWarning(frNode.getName(), Collections.<ASTNode>emptyList(), frNode, document));
							}
						}
						else if (ASTNode.EXPRESSION_CLASS.equals(node.getGrammarClass())) {
							if (node instanceof RuleOrFuncElementNode) {
								RuleOrFuncElementNode ruleOrFuncElementNode = (RuleOrFuncElementNode)node;
								if (!isFunctionName(ruleOrFuncElementNode.getElementName(), functionNames))
									warnings.add(new UndefinedIdentifierWarning(ruleOrFuncElementNode.getElementName(), Collections.<ASTNode>emptyList(), node, document));
							}
						}
						fringe.addAll(node.getAbstractChildNodes());
					}
				}
			}
		}
		
		return warnings;
	}
	
	private boolean isFunctionName(String functionName, Set<String> functionNames) {
		if (getPluginFunctionNames().contains(functionName))
			return true;
		return functionNames.contains(functionName);
	}
	
	private Set<String> getPluginFunctionNames() {
		if (pluginFunctionNames == null) {
			pluginFunctionNames = new HashSet<String>();
			for (FunctionInfo functionInfo : parentEditor.getSpec().getDefinedFunctions())
				pluginFunctionNames.add(functionInfo.name);
			for (FunctionInfo functionInfo : parentEditor.getSpec().getDefinedUniverses())
				pluginFunctionNames.add(functionInfo.name);
			for (FunctionInfo functionInfo : parentEditor.getSpec().getDefinedBackgrounds())
				pluginFunctionNames.add(functionInfo.name);
		}
		return pluginFunctionNames;
	}
	
	private Set<String> getDeclaredNames(ASMDocument document) {
		Set<String> declaredNames = new HashSet<String>();
		for (ASTNode node = ((ASTNode)document.getRootnode()).getFirst(); node != null; node = node.getNext()) {
			if (ASTNode.DECLARATION_CLASS.equals(node.getGrammarClass())) {
				if ("Signature".equals(node.getGrammarRule())) {
					for (ASTNode signature = node.getFirst(); signature != null; signature = signature.getNext()) {
						if (signature instanceof EnumerationNode) {
							EnumerationNode enumerationNode = (EnumerationNode)signature;
							declaredNames.add(enumerationNode.getName());
							for (EnumerationElement member : enumerationNode.getMembers())
								declaredNames.add(member.getName());
						}
						else if (signature instanceof FunctionNode)
							declaredNames.add(((FunctionNode)signature).getName());
						else if (signature instanceof UniverseNode) {
							UniverseNode universeNode = (UniverseNode)signature;
							declaredNames.add(((UniverseNode)signature).getName());
							for (ASTNode member = universeNode.getFirst().getNext(); member != null; member = member.getNext())
								declaredNames.add(member.getToken());
						}
						else if (signature instanceof DerivedFunctionNode)
							declaredNames.add(((DerivedFunctionNode)signature).getNameSignatureNode().getFirst().getToken());
					}
				}
				else if (Kernel.GR_RULEDECLARATION.equals(node.getGrammarRule()))
					declaredNames.add(node.getFirst().getFirst().getToken());
			}

		}
		IPath relativePath = parentEditor.getInputFile().getProjectRelativePath().removeLastSegments(1);
		IProject project = parentEditor.getInputFile().getProject();
		for (Node node = document.getRootnode().getFirstCSTNode(); node != null; node = node.getNextCSTNode()) {
			if (node instanceof IncludeNode) {
				try {
					for (Declaration declaration : ASMDeclarationWatcher.getDeclarations(project.getFile(relativePath.append(((IncludeNode)node).getFilename())), true))
						declaredNames.add(declaration.getName());
				} catch (IllegalArgumentException e) {
				}
			}
		}
		return declaredNames;
	}
}