package ui.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author Ivan Jeržabek - ivan.jerzabek@fer.hr
 */
public class Utility {

  // Program argument flags
  public static final String FLAG_ALGORITHM = "--alg";
  public static final String FLAG_STATE_SPACE = "--ss";
  public static final String FLAG_HEURISTIC_DESCRIPTOR = "--h";
  public static final String FLAG_CHECK_OPTIMISTIC = "--check-optimistic";
  public static final String FLAG_CHECK_CONSISTENT = "--check-consistent";

  // Algorithm short names
  public static final String ALG_BFS = "bfs";
  public static final String ALG_UCS = "ucs";
  public static final String ALG_ASTAR = "astar";

  public static final String COMMENT = "#";
  public static final String LOG_FOUND_SOLUTION = "[FOUND_SOLUTION]";
  public static final String LOG_STATES_VISITED = "[STATES_VISITED]";
  public static final String LOG_PATH_LENGTH = "[PATH_LENGTH]";
  public static final String LOG_TOTAL_COST = "[TOTAL_COST]";
  public static final String LOG_PATH = "[PATH]";

  public static List<String> readLines(String path) {
    try {
      return Files.readAllLines(Paths.get(path));
    } catch (IOException e) {
      System.out.println("Could not read file " + path);
      System.exit(1);
      return null;
    }
  }
}
