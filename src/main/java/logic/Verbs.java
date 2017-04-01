package logic;

import utils.Params;

import java.io.*;
import java.util.*;

public class Verbs {

    public static int numVerbClusters;
    public static Map<Integer, String> verbClusterCategory;
    public static Map<String, Integer> verbCluster;
    public static Map<String, double[]> vectors;

    static {
        try {
            System.out.println("Reading verbnet ...");
            readVerbClusters(Params.verbnetDir);
            System.out.println("Reading vectors for verbs ...");
            vectors = readVectors(Params.vectorsFile);
            System.out.println("Assigning verb category using verbnet ...");
            getVerbCategoryUsingVerbNet();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readVerbClusters(String verbnetDir) throws IOException {
        verbCluster = new HashMap<>();
        int index = 0;
        File dir = new File(verbnetDir);
        File[] directoryListing = dir.listFiles();
        BufferedReader br;
        String line;
        if (directoryListing != null) {
            for (File child : directoryListing) {
                br = new BufferedReader(new FileReader(child));
                while((line = br.readLine()) != null) {
                    if(line.contains("<MEMBER") && line.contains("name=")) {
                        int start = line.indexOf("name=")+6;
                        int end = line.indexOf("\"", start);
                        String verb = line.substring(start, end);
                        verbCluster.put(verb, index);
                    }
                }
                br.close();
                index++;
            }
        }
        numVerbClusters = index;
    }

    public static void getVerbCategoryUsingVerbNet() {
        verbClusterCategory = new HashMap<>();
        for (int index=0; index<numVerbClusters; ++index) {
            Map<String, Double> map = new HashMap<>();
            map.put("STATE", 0.0);
            map.put("POSITIVE", 0.0);
            map.put("NEGATIVE", 0.0);
            for(String verb : verbCluster.keySet()) {
                if(verbCluster.get(verb) != index) continue;
                String clusterCategory = verbCategory(verb);
                map.put(clusterCategory, map.get(clusterCategory) + 1.0);
                String finalCategory = maxWeightCategory(map);
                verbClusterCategory.put(index, finalCategory);
            }
        }
    }

    public static String verbCategory(String v) {
        List<String> state = Arrays.asList("is", "have", "own");
        List<String> positive = Arrays.asList("got", "gain", "borrow");
        List<String> negative = Arrays.asList("give", "lost", "lend");
        Map<String, Double> map = new HashMap<>();
        map.put("STATE", 0.0);
        map.put("POSITIVE", 0.0);
        map.put("NEGATIVE", 0.0);
        for (String verb : state) {
            map.put("STATE", map.get("STATE") + getVectorSim(verb, v));
        }
        map.put("STATE", map.get("STATE") / state.size());
        for (String verb : positive) {
            map.put("POSITIVE", map.get("POSITIVE") + getVectorSim(verb, v));
        }
        map.put("POSITIVE", map.get("POSITIVE") / positive.size());
        for (String verb : negative) {
            map.put("NEGATIVE", map.get("NEGATIVE") + getVectorSim(verb, v));
        }
        map.put("NEGATIVE", map.get("NEGATIVE") / negative.size());
        return maxWeightCategory(map);
    }

    public static String maxWeightCategory(Map<String, Double> map) {
        if(map.get("NEGATIVE") > map.get("POSITIVE") && map.get("NEGATIVE") > map.get("STATE")) {
            return "NEGATIVE";
        }
        if(map.get("POSITIVE") > map.get("NEGATIVE")) {
            return "POSITIVE";
        }
        return "STATE";
    }

    public static Map<String, Double> verbClassify(LogicInput num) {
        Map<String, Double> map = new HashMap<>();
        map.put("STATE", 0.0);
        map.put("POSITIVE", 0.0);
        map.put("NEGATIVE", 0.0);
        // Hard decision for now
        if(verbCluster.containsKey(num.verbLemma)) {
            int vc = verbCluster.get(num.verbLemma);
            String vcc = verbClusterCategory.get(vc);
            map.put(vcc, 1.0);
        }
        return map;
    }

    public static double getVectorSim(String word1, String word2) {
        if (word1 == null || word2 == null) {
            return 0.0;
        }
        if(vectors.containsKey(word1) && vectors.containsKey(word2)) {
            double[] v1 = vectors.get(word1);
            double[] v2 = vectors.get(word2);
            double dot = 0.0, norm1 = 0.0, norm2 = 0.0;
            for (int i=0; i<v1.length; ++i) {
                dot += (v1[i]*v2[i]);
                norm1 += (v1[i]*v1[i]);
                norm2 += (v2[i]*v2[i]);
            }
            return dot / (Math.sqrt(norm1 * norm2));
        }
        return 0.0;
    }

    public static Map<String, double[]> readVectors(String vectorFile) throws IOException {
        Map<String, double[]> vectors = new HashMap<>();
        BufferedReader br;
        String line;
        br = new BufferedReader(new FileReader(new File(vectorFile)));
        while((line = br.readLine()) != null) {
            String strArr[] = line.split(" ");
            String word = strArr[0].trim();
            if (!verbCluster.keySet().contains(word)) continue;
            double d[] = new double[strArr.length-1];
            for(int i=1; i<strArr.length; ++i) {
                d[i-1] = Double.parseDouble(strArr[i]);
            }
            vectors.put(word, d);
        }
        br.close();
        return vectors;
    }
}