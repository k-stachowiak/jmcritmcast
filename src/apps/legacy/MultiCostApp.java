package apps.legacy;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

public class MultiCostApp {

    static long randomSeed;
    static double fengDelta;
    static String topologiesDirecotry;
    static String topology;
    static int graphsInFile;
    static double redistributionMin;
    static double redistributionMax;
    static int graphs;
    static List<Integer> nodeSizes;
    static List<Integer> criteriaCounts;
    static List<Integer> groupSizes;
    static List<String> algNames;

    private static boolean readConfig() {

        Properties properties = new Properties();
        FileInputStream fis;

        // Read and parse configuration file.
        // ---------------------------
        try {
            fis = new FileInputStream("config");
            properties.load(fis);

        } catch (FileNotFoundException e) {
            System.err.println("Exception: Configuration file not found.");
            return false;

        } catch (IOException e) {
            System.err.println("Exception: Error loading configuration file.");
            return false;

        }

        // Peal out numeric values and collections.
        // ----------------------------------------
        try {
            randomSeed = Long.parseLong(properties.getProperty("randomSeed"));
            fengDelta = Double.parseDouble(properties.getProperty("fengDelta"));
            graphsInFile = Integer.parseInt(properties.getProperty("graphsInFile"));
            redistributionMin = Double.parseDouble(properties.getProperty("redistributionMin"));
            redistributionMax = Double.parseDouble(properties.getProperty("redistributionMax"));
            graphs = Integer.parseInt(properties.getProperty("graphs"));

            String nssStr = properties.getProperty("nodeSizes");
            String[] nss = nssStr.split(",");
            nodeSizes = new ArrayList<>();
            for (String ns : nss) {
                nodeSizes.add(Integer.parseInt(ns));
            }

            String ccsStr = properties.getProperty("criteriaCounts");
            String[] ccs = ccsStr.split(",");
            criteriaCounts = new ArrayList<>();
            for (String cc : ccs) {
                criteriaCounts.add(Integer.parseInt(cc));
            }

            String gssStr = properties.getProperty("groupSizes");
            String[] gss = gssStr.split(",");
            groupSizes = new ArrayList<>();
            for (String gs : gss) {
                groupSizes.add(Integer.parseInt(gs));
            }

        } catch (NumberFormatException ex) {
            System.err.println("Exception: Parameter parsing error \""
                    + ex.getMessage() + "\"");
            return false;

        }

        // Read the string properties.
        // ---------------------------
        topologiesDirecotry = properties.getProperty("topologiesDirectory");
        topology = properties.getProperty("topology");
        String ansStr = properties.getProperty("algNames");

        if (topologiesDirecotry == null) {
            System.err.println("Exception: Error while parsing topologies directory property.");
            return false;
        }

        if (topology == null) {
            System.err.println("Exception: Error while parsing topology property.");
            return false;
        }

        if (ansStr == null) {
            System.err.println("Exception: Error while parsing algorithm names property.");
            return false;
        }

        algNames = new ArrayList<>(Arrays.asList(ansStr.split(",")));

        return true;
    }

    public static void main(String[] args) {

        Locale.setDefault(Locale.ENGLISH);

        // Parse the configuration file.
        // -----------------------------
        if (!readConfig()) {
            System.err.println("Error in configuration file.");
            return;
        }

        // Build a setup definition for the application and run it.
        // --------------------------------------------------------
        final MultiCostSetup setup = new MultiCostSetup(randomSeed,
                fengDelta, graphs, nodeSizes,
                criteriaCounts, groupSizes, topologiesDirecotry, topology,
                graphsInFile, redistributionMin, redistributionMax,
                algNames);

        new MultiCostLogic(setup).run(args, System.out, System.err);
    }
}
