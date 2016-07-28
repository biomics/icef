package CompilerRuntime;

import org.coreasim.engine.absstorage.BackgroundElement;
import org.coreasim.engine.absstorage.BooleanElement;
import org.coreasim.engine.absstorage.Element;

public class RuleBackgroundElement extends BackgroundElement {
	public static final String RULE_BACKGROUND_NAME = "RULE";
	
	
	@Override
	public Element getNewValue() {
		throw new UnsupportedOperationException("Cannot create new rule.");
	}

	@Override
	protected Element getValue(Element e) {
		return BooleanElement.valueOf(e instanceof Rule);
	}

}
