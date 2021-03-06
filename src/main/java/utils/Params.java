package utils;

public class Params {
	
	public static String spConfigFile = "config/DCD.config";
	public static boolean printMistakes = false;
	public static boolean printCorrect = true;

	public static String pipelineConfig = "config/pipeline.config";
	
	public static String patternsFile = "data/patterns.txt";
	public static String questionsFile = "data/questions.json";

	public static String vectorsFile = "data/glove.6B.300d.verbs.txt";

	public static boolean useIllinoisTools, useStanfordTools, noUDG, printLog = false,
			runDemo = false, startDemoServer = false;
	public static String modelDir = "models/";
	public static String relPrefix = "Rel";
	public static String pairPrefix = "Pair";
	public static String ratePrefix = "Rate";
	public static String runPrefix = "Run";
	public static String graphPrefix = "Graph";
	public static String corefPrefix = "Coref";
	public static String logicPrefix = "Logic";
	public static String modelSuffix = ".save";
	
}
