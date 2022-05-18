package ui.dataset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ui.dataset.ExamplesContainer.Example;

/**
 * @author Ivan Jeržabek - ivan.jerzabek@fer.hr
 */
public class Dataset {

  private final Map<String, List<String>> dataset;
  private final Map<String, Set<String>> featureValues;
  private final Set<String> classificationValues;
  private final List<String> classifications;
  private final ExamplesContainer examplesContainer;

  private Dataset(ExamplesContainer examplesContainer,
      Map<String, List<String>> dataset,
      Map<String, Set<String>> featureValues,
      Set<String> classificationValues,
      List<String> classifications) {
    this.examplesContainer = examplesContainer;
    this.dataset = dataset;
    this.featureValues = featureValues;
    this.classificationValues = classificationValues;
    this.classifications = classifications;
  }

  public Map<String, List<String>> getDataset() {
    return dataset;
  }

  public ExamplesContainer getExamplesContainer() {
    return examplesContainer;
  }

  public Map<String, Set<String>> getFeatureValues() {
    return featureValues;
  }

  public Set<String> getClassificationValues() {
    return classificationValues;
  }

  public List<String> getClassifications() {
    return classifications;
  }

  public static Dataset datasetFactory(List<String> rows) {
    ExamplesContainer examplesContainer = new ExamplesContainer(rows);
    Map<String, List<String>> dataset = new HashMap<>();
    Map<String, Set<String>> featureValues = new HashMap<>();
    Set<String> classificationValues = new HashSet<>();
    List<String> classifications = new ArrayList<>();

    List<String> features = examplesContainer.getHeader().getFeatures();

    for (int i = 0; i < features.size(); i++) {
      String feature = features.get(i);

      extractExampleDetails(examplesContainer, dataset, featureValues, classificationValues, i, feature);
    }

    extractClassificationValues(classifications, examplesContainer.getExamples());

    return new Dataset(examplesContainer, dataset, featureValues, classificationValues, classifications);
  }

  public static Dataset partitionDataset(Dataset dataset, String feature, String value) {
    ExamplesContainer examplesContainer = new ExamplesContainer(
        new ArrayList<>(dataset.getExamplesContainer().getExamples()),
        dataset.getExamplesContainer().getHeader());

    Map<String, List<String>> newDataset = new HashMap<>();
    Map<String, Set<String>> newFeatureValues = new HashMap<>();
    List<String> newClassifications = new ArrayList<>();
    Set<String> newClassificationValues = new HashSet<>();

    int featureIndex = examplesContainer.getHeader().getFeatures().indexOf(feature);

    examplesContainer.getExamples().removeIf(example -> !example.getValues().get(featureIndex).equals(value));

    List<String> features = examplesContainer.getHeader().getFeatures();

    for (int i = 0; i < features.size(); i++) {
      String currentFeature = features.get(i);

      if (currentFeature.equals(feature)) {
        continue;
      }

      extractExampleDetails(examplesContainer, newDataset, newFeatureValues, newClassificationValues, i, currentFeature);
    }

    extractClassificationValues(newClassifications, examplesContainer.getExamples());

    return new Dataset(examplesContainer, newDataset, newFeatureValues, newClassificationValues, newClassifications);
  }

  private static void extractClassificationValues(List<String> classificationValues, List<Example> examples) {
    // we add them to a list so that we can count them
    for (Example example : examples) {
      classificationValues.add(example.getClassification());
    }
  }

  private static void extractExampleDetails(ExamplesContainer examplesContainer, Map<String, List<String>> dataset, Map<String, Set<String>> featureValues,
      Set<String> classificationValues, int i, String feature) {
    for (Example example : examplesContainer.getExamples()) {
      if (!dataset.containsKey(feature)) {
        dataset.put(feature, new ArrayList<>());
      }

      if (!featureValues.containsKey(feature)) {
        featureValues.put(feature, new HashSet<>());
      }

      // we add all values from examples into their individual lists, grouped by feature
      dataset.get(feature).add(example.getValues().get(i));

      // We add all values into sets, so we have a collection of unique feature values
      featureValues.get(feature).add(example.getValues().get(i));

      // we add all classification values into a set, so we have a unique list of classes
      classificationValues.add(example.getClassification());
    }
  }

}
