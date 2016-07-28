package org.coreasim.engine.kernel;

import org.coreasim.engine.CoreASMError;
import org.coreasim.engine.absstorage.Element;
import org.coreasim.engine.absstorage.Location;
import org.coreasim.engine.absstorage.TriggerMultiset;
import org.coreasim.engine.absstorage.UpdateMultiset;
import org.coreasim.engine.interpreter.ASTNode;
import org.coreasim.engine.interpreter.ScannerInfo;

/**
 * A node holding a constant value that can be used to replace ASTNodes with constant values.
 * @author Michael Stegmaier
 *
 */
public class ConstantValueNode extends ASTNode {
	private static final long serialVersionUID = 1L;

	public ConstantValueNode(ConstantValueNode node) {
		super(node);
		setValue(node.getValue());
	}
	
	public ConstantValueNode(ScannerInfo info, Element value) {
		super(Kernel.PLUGIN_NAME, ASTNode.EXPRESSION_CLASS, "", null, info);
		setValue(value);
	}
	
	public void setValue(Element value) {
		if (value == null)
			throw new CoreASMError("Constant value must not be null", this);
		super.setNode(null, new UpdateMultiset(), new TriggerMultiset(), value);
	}
	
	@Override
	public void setNode(Location loc, UpdateMultiset updates, TriggerMultiset triggers, Element value) {
	}
}
