package apps;

import java.util.Arrays;
import java.util.List;

public class CommonConfig {

	public static final List<Integer> nodesCounts = Arrays
			.asList(new Integer[] { 50, 100, 250, 500, 1500, 3037, 3600, 4750,
					6000 });

	public static final List<Integer> groupSizes = Arrays.asList(new Integer[] {
			4, 8, 12, 16, 20, 24, 28 });

	public static final List<Double> constraintBases = Arrays
			.asList(new Double[] { 100.0, 1000.0, 10000.0 });

	public static final double significance = 0.01;

	public static String dbUri = "jdbc:postgresql://localhost:5432/phd";
	public static String dbUser = "postgres";
	public static String dbPass = "admin";
}
