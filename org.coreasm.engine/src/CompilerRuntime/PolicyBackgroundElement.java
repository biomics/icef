package CompilerRuntime;

import org.coreasm.engine.absstorage.BackgroundElement;
import org.coreasm.engine.absstorage.Element;
import org.coreasm.engine.absstorage.BooleanElement;

public class PolicyBackgroundElement extends BackgroundElement {
	public static final String POLICY_BACKGROUND_NAME = "POLICY";
	
	
	@Override
	public Element getNewValue() {
		throw new UnsupportedOperationException("Cannot create new policy.");
	}

	@Override
	protected Element getValue(Element e) {
		return BooleanElement.valueOf(e instanceof Policy);
	}

}
