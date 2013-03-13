package edu.put.et.stik.mm.tfind.rdp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.put.et.stik.mm.helpers.ConstraintsComparer;
import edu.put.et.stik.mm.model.topology.EdgeDefinition;
import edu.put.et.stik.mm.model.topology.Graph;
import edu.put.et.stik.mm.model.topology.Node;
import edu.put.et.stik.mm.model.topology.Path;
import edu.put.et.stik.mm.model.topology.Tree;
import edu.put.et.stik.mm.tfind.ConstrainedSteinerTreeFinder;


public class RdpHeuristic implements ConstrainedSteinerTreeFinder {

	// External dependencies.
	private final ConstraintsComparer constraintsComparer;

	// State.
	private Map<Node, HeurNlConvergenceProcess> convergenceProcesses;
	private Map<Node, Set<HeurNlConvergenceProcess>> visitMap;

	public RdpHeuristic(ConstraintsComparer constraintsComparer) {
		this.constraintsComparer = constraintsComparer;
	}

	@Override
	public Tree find(Graph graph, List<Node> group, List<Double> constraints) {

		convergenceProcesses = new HashMap<>();
		for (Node node : group) {
			convergenceProcesses.put(node, new HeurNlConvergenceProcess(graph,
					constraints, node));
		}
		
		visitMap = new HashMap<>();
		for (;;) {
			
			HeurNlConvergenceProcess convergenceProcess = selectSoonestProcess();
			Node visited = convergenceProcess.handleNextEvent();
			registerVisit(visited, convergenceProcess);
			
			if(visitMap.get(visited).size() == group.size()) {
				
				Tree candidate = buildCandidate(graph, visited);
				
				if(constraintsComparer.fulfilsAll(candidate, group.get(0), constraints)) {
					return candidate;
					
				} else {
					return null;
					
				}
			}
		}
	}

	private Tree buildCandidate(Graph graph, Node rdp) {
		
		List<Path> paths = new ArrayList<>();
		for(HeurNlConvergenceProcess process : convergenceProcesses.values()) {
			paths.add(process.buildPathFrom(rdp));
		}
		
		List<EdgeDefinition> edges = new ArrayList<>();
		for(Path path : paths) {
			edges.addAll(path.getEdgeDefinitions());
		}
		
		return new Tree(graph, edges);
	}

	private void registerVisit(Node visited,
			HeurNlConvergenceProcess convergenceProcess) {		
		if(!visitMap.containsKey(visited)) {
			visitMap.put(visited, new HashSet<HeurNlConvergenceProcess>());
		}		
		visitMap.get(visited).add(convergenceProcess);
	}

	private HeurNlConvergenceProcess selectSoonestProcess() {
		double soonestTime = Double.POSITIVE_INFINITY;
		HeurNlConvergenceProcess soonestProcess = null;
		for(HeurNlConvergenceProcess process : convergenceProcesses.values()) {
			double time = process.nextEventTime();
			if(time < soonestTime) {
				soonestTime = time;
				soonestProcess = process;
			}
		}
		return soonestProcess;
	}

}
