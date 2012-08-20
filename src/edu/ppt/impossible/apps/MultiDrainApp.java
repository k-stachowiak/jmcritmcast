package edu.ppt.impossible.apps;

import java.util.ArrayList;
import java.util.List;

import edu.ppt.impossible.hlanalysis.MultiDrain;
import edu.ppt.impossible.hlanalysis.MultiDrainSetup;

public class MultiDrainApp {

	public static void main(String[] args) {

		final long randomSeed = 1L;
		final double fengDelta = 0.9;
		final double baseBandwidth = 10000.0;
		final double drainedBandwidth = 10.0;

		final String topologiesDirecotry = "data/new";
		final String topology = "ASBarabasi";
		final int graphsInFile = 1000;

		final int graphs = 100;

		final List<Integer> nodeSizes = new ArrayList<>();
		nodeSizes.add(50);
		nodeSizes.add(150);

		final List<Integer> criteriaCounts = new ArrayList<>();
		criteriaCounts.add(2);
		criteriaCounts.add(3);
		criteriaCounts.add(4);

		final List<Integer> groupSizes = new ArrayList<>();
		groupSizes.add(5);
		groupSizes.add(20);
		groupSizes.add(80);

		final MultiDrainSetup setup = new MultiDrainSetup(randomSeed,
				fengDelta, baseBandwidth, drainedBandwidth, graphs, nodeSizes,
				criteriaCounts, groupSizes, topologiesDirecotry, topology,
				graphsInFile);

		new MultiDrain(setup).run(args);
	}

}