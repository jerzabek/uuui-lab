package ui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import ui.algorithm.Algorithm;
import ui.algorithm.ID3;
import ui.dataset.Dataset;

/**
 * @author Ivan Jeržabek - ivan.jerzabek@fer.hr
 */
public class Solution {

  public static void main(String[] args) {
    if (args.length < 2) {
      System.out.println("Missing arguments");
      System.exit(1);
      return;
    }

    String pathToFile = args[0];
    String pathToTest = args[1];
    Integer maxDepth = null;

    if (args.length >= 3) {
      maxDepth = Integer.parseInt(args[2]);
    }

    List<String> datasetDescriptor = getDatasetDescriptorContent(pathToFile);
    List<String> testDatasetDescriptor = getDatasetDescriptorContent(pathToTest);

    Dataset dataset = Dataset.datasetFactory(datasetDescriptor);
    Dataset testDataset = Dataset.datasetFactory(testDatasetDescriptor);

    Algorithm algorithm = new ID3(maxDepth);

    algorithm.fit(dataset);
    algorithm.predict(testDataset);
  }

  /**
   * Method that wraps the Files.readAllLines call in a try/catch block for easier readability.
   *
   * @param pathToFile String path to file on disk
   * @return List of lines in the file
   */
  private static List<String> getDatasetDescriptorContent(String pathToFile) {
    try {
      return Files.readAllLines(Paths.get(pathToFile));
    } catch (IOException e) {
      System.out.println("Could not load file: " + e.getMessage());
      System.exit(1);
      return null;
    }
  }

}
