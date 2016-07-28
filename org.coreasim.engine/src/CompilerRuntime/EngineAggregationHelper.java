package CompilerRuntime;

import java.util.Collection;

import org.coreasim.engine.absstorage.Update;

public interface EngineAggregationHelper {
	void setUpdateInstructions(UpdateList updates);
	boolean isConsistent();
	Collection<Update> getFailedInstructions();
	Collection<Update> getUnprocessedInstructions();
	UpdateList getResultantUpdates();

}
