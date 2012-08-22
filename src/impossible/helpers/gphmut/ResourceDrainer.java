package impossible.helpers.gphmut;

import impossible.model.Graph;
import impossible.model.SubGraph;

public interface ResourceDrainer {

	Graph drain(Graph graph, SubGraph subGraph);

}
