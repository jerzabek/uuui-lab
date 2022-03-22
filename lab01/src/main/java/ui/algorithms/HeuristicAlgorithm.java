package ui.algorithms;

import java.text.Collator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import ui.util.Utility;

/**
 * @author Ivan Jeržabek - ivan.jerzabek@fer.hr
 */
public abstract class HeuristicAlgorithm extends Algorithm {

  protected final HashMap<String, Double> heuristicValues = new LinkedHashMap<>();

  public HeuristicAlgorithm(String stateSpacePath, String heuristicDescriptorPath) {
    super(stateSpacePath);

    if (heuristicDescriptorPath == null) {
      System.out.println("No heuristic descriptor path provided");
      System.exit(1);
    }

    List<String> heuristicDescriptorLines = Utility.readLines(heuristicDescriptorPath);

    heuristicDescriptorLines.sort(this::sorter);

    for (String line : heuristicDescriptorLines) {
      // All lines will have the following format:
      // state: heuristic_value
      String[] details = line.split(":\\s");

      heuristicValues.put(details[0], Double.parseDouble(details[1]));
    }
  }

  private int sorter(String a, String b) {
    Locale langLocale = new Locale("hr");
    Collator langCollator = Collator.getInstance(langLocale);
    return langCollator.compare(a, b);
  }

}
