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
import edu.put.et.stik.mm.tfind.MetricConstrainedSteinerTreeFinder;

public class RdpQuasiExact implements MetricConstrainedSteinerTreeFinder {
	
	// External dependencies.
	private final ConstraintsComparer constraintsComparer;
	
	// State.
	private Map<Node, CostConvergenceProcess> convergenceProcesses;
	private Map<Node, Set<CostConvergenceProcess>> visitMap;
	
	public RdpQuasiExact(ConstraintsComparer constraintsComparer) {
		this.constraintsComparer = constraintsComparer;
	}

	@Override
	public Tree find(Graph graph, List<Node> group, List<Double> constraints) {		
		
		convergenceProcesses = new HashMap<>();
		for(Node node : group) {
			convergenceProcesses.put(node, new CostConvergenceProcess(graph, node));
		}
		
		visitMap = new HashMap<>();
		for (;;) {
			
			CostConvergenceProcess convergenceProcess = selectSoonestProcess();
			Node visited = convergenceProcess.handleNextEvent();
			registerVisit(visited, convergenceProcess);
			
			if(visitMap.get(visited).size() == group.size()) {
				Tree candidate = buildCandidate(graph, visited);
				if(constraintsComparer.fulfilsAll(candidate, group.get(0), constraints)) {
					return candidate;
				}
			}
			
			if (allDone()) {
				return null;
			}
		}
	}

	private Tree buildCandidate(Graph graph, Node rdp) {
		
		List<Path> paths = new ArrayList<>();
		for(CostConvergenceProcess process : convergenceProcesses.values()) {
			paths.add(process.buildPathFrom(rdp));
		}
		
		List<EdgeDefinition> edges = new ArrayList<>();
		for(Path path : paths) {
			edges.addAll(path.getEdgeDefinitions());
		}
		
		return new Tree(graph, edges);
	}

	private boolean allDone() {
		for(CostConvergenceProcess process : convergenceProcesses.values()) {
			if(!process.isDone()) {
				return false;
			}
		}
		return true;
	}

	private void registerVisit(Node visited,
			CostConvergenceProcess convergenceProcess) {		
		if(!visitMap.containsKey(visited)) {
			visitMap.put(visited, new HashSet<CostConvergenceProcess>());
		}		
		visitMap.get(visited).add(convergenceProcess);
	}

	private CostConvergenceProcess selectSoonestProcess() {
		double soonestTime = Double.POSITIVE_INFINITY;
		CostConvergenceProcess soonestProcess = null;
		for(CostConvergenceProcess process : convergenceProcesses.values()) {
			double time = process.nextEventTime();
			if(time < soonestTime) {
				soonestTime = time;
				soonestProcess = process;
			}
		}
		return soonestProcess;
	}

}
