package edu.put.et.stik.mm.helpers.algqry;

public class AlgorithmQuery {

	String buildDotFile(QueryableAlgorithm alg) {

		StringBuilder sb = new StringBuilder();

		// Begin graph.
		sb.append("digraph dummy_name {\n");

		// Print nodes.
		for (NodeInfo ni : alg.getNodeInfos()) {
			sb.append(String.format("%1$s [label=\"%1$s\"];\n", ni.getId(),
					ni.getLabel()));
		}
		
		// Print edges.
		for(EdgeInfo ei : alg.getEdgeInfos()) {
			sb.append(String.format("", ei.getFromId(), ei.getToId()));
		}

		// End graph.
		sb.append("}");

		return sb.toString();

	}

}
