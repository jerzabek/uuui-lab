package ui.dataset;

import java.util.ArrayList;
import java.util.List;
import ui.Utility;

/**
 * @author Ivan Jeržabek - ivan.jerzabek@fer.hr
 */
public class ExamplesContainer {

  private final List<Example> examples;
  private final DatasetHeader header;

  public ExamplesContainer(List<Example> examples, DatasetHeader header) {
    this.examples = examples;
    this.header = header;
  }

  public ExamplesContainer(List<String> rows) {
    this.header = parseHeader(rows);
    this.examples = parseExamples(rows);
  }

  public List<Example> getExamples() {
    return examples;
  }

  public DatasetHeader getHeader() {
    return header;
  }

  private DatasetHeader parseHeader(List<String> rows) {
    String header = rows.remove(0);

    // Names of the features
    List<String> featureNames = new ArrayList<>(List.of(header.split(Utility.SEPARATOR)));

    // Name of current classification
    String className = featureNames.remove(featureNames.size() - 1);

    return new DatasetHeader(featureNames, className);
  }

  private List<Example> parseExamples(List<String> rows) {
    List<Example> examples = new ArrayList<>();

    for (String example : rows) {
      List<String> values = new ArrayList<>(List.of(example.split(Utility.SEPARATOR)));

      String exampleClassification = values.remove(values.size() - 1);

      examples.add(new Example(values, exampleClassification));
    }

    return examples;
  }

  public static class Example {

    private final List<String> values;
    private final String classification;

    public Example(List<String> values, String classification) {
      this.values = values;
      this.classification = classification;
    }

    public List<String> getValues() {
      return values;
    }

    public String getClassification() {
      return classification;
    }
  }

  public static class DatasetHeader {

    private final List<String> features;
    private final String classificationName;

    public DatasetHeader(List<String> features, String classification) {
      this.features = features;
      this.classificationName = classification;
    }

    public List<String> getFeatures() {
      return features;
    }

    public String getClassificationName() {
      return classificationName;
    }
  }
}
