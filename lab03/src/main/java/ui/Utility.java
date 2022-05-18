package ui;

import java.util.Set;
import ui.dataset.Dataset;

/**
 * @author Ivan Jeržabek - ivan.jerzabek@fer.hr
 */
public class Utility {

  public static final String SEPARATOR = ",";

  public static double informationGain(Dataset dataset, String feature) {
    double parentalEntropy = entropy(dataset);
    double partitionExpectancy = 0d;

    double numOfExamples = dataset.getExamplesContainer().getExamples().size();

    // iterate over all values of a certain feature in the given dataset
    for (String featureValue : dataset.getFeatureValues().get(feature)) {
      Dataset partition = Dataset.partitionDataset(dataset, feature, featureValue);

      double relativeFrequency = ((double) partition.getExamplesContainer().getExamples().size()) / numOfExamples;

      partitionExpectancy += relativeFrequency * entropy(partition);
    }

    return parentalEntropy - partitionExpectancy;
  }

  public static double entropy(Dataset dataset) {
    Set<String> classes = dataset.getClassificationValues();

    int numOfExamples = dataset.getExamplesContainer().getExamples().size();
    double entropy = 0d;

    for (String classification : classes) {
      // Cast to double to get floating point division
      double classCount = (double) dataset.getClassifications().stream().filter(cls -> cls.equals(classification)).count();

      double relativeFrequency = classCount / numOfExamples;

      // log_b(x) = log_a(x) / log_a(b)
      entropy += relativeFrequency * (Math.log(relativeFrequency) / Math.log(2));
    }

    return -entropy;
  }

}
