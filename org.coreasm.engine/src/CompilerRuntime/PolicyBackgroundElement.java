package CompilerRuntime;

import org.coreasim.engine.absstorage.BackgroundElement;
import org.coreasim.engine.absstorage.BooleanElement;
import org.coreasim.engine.absstorage.Element;

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
