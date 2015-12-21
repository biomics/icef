package org.coreasm.compiler.interfaces;

import java.util.List;

import org.coreasm.compiler.exception.CompilerException;

/**
 * Interfaces for operator providing plugins.
 * An operator providing plugin can provide unary and binary operations.
 * <p>
 * The code generated by the operator provider for an operator can assume that
 * the left-hand side and right-hand side(only in case of binary operations) are
 * stored as Objects in the temporary variables {@literal @}rhs{@literal @} and
 * {@literal @}lhs{@literal @}.
 * <p>
 * As operators can be overloaded (e.g. + is applicable for strings and numbers),
 * the compiled code has to be of the form
 * <p><blockquote><pre>
 * if(condition){
 * code pushing a result to the stack
 * } else
 * </pre></blockquote>
 * Note that there is no closing bracket after the else.
 * <p>
 * The condition has to check, if the provided arguments are of the correct
 * type for this operation.
 * Note that there is no check implemented, if an operators blocks other operators.
 * @author Markus Brenner
 *
 */
public interface CompilerOperatorPlugin extends CompilerPlugin{
	/**
	 * Provides a list of all unary operations provided by this plugin
	 * @return A list of unary operators, may not be null
	 */
	public List<String> unaryOperations();
	/**
	 * Provides a list of all binary operations provided by this plugin
	 * @return A list of binary operators, may not be null
	 */
	public List<String> binaryOperations();

	/**
	 * Compiles a binary operator node according to the specification of an operator.
	 * @param token The operator token
	 * @return The compiled String
	 * @throws CompilerException If an error occurred
	 */
	public String compileBinaryOperator(String token) throws CompilerException;
	
	/**
	 * Compiles an unary operator node according to the specification of an operator.
	 * @param token The operator token
	 * @return The compiled String
	 * @throws CompilerException If an error occurred
	 */
	public String compileUnaryOperator(String token) throws CompilerException;
}
