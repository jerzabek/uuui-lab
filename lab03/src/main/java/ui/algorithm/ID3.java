package ui.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import ui.Utility;
import ui.dataset.Dataset;
import ui.dataset.ExamplesContainer.Example;
import ui.dataset.tree.Leaf;
import ui.dataset.tree.Node;

/**
 * @author Ivan Jeržabek - ivan.jerzabek@fer.hr
 */
public class ID3 extends Algorithm {

  private Node treeParent;

  @Override
  public void fit(Dataset dataset) {
    treeParent = ID3Iteration(dataset, dataset, dataset.getExamplesContainer().getHeader().getFeatures());
    System.out.println("[BRANCHES]:");

    for (String feature : treeParent.subtrees.keySet()) {
      Node node = treeParent.subtrees.get(feature);

      printBranch(treeParent.feature, feature, node, 1);
    }
  }

  private void printBranch(String parentFeature, String value, Node node, int depth) {
    if (node instanceof Leaf) {
      System.out.printf("%d:%s=%s ", depth, parentFeature, value);
      System.out.printf("%s\n", node.feature);
      return;
    }

    for (String feature : node.subtrees.keySet()) {
      Node childNode = node.subtrees.get(feature);
      System.out.printf("%d:%s=%s ", depth, parentFeature, value);

      printBranch(node.feature, feature, childNode, depth + 1);
    }
  }

  private Node ID3Iteration(Dataset dataset, Dataset parentDataset, List<String> listOfFeatures) {
    if (dataset.getExamplesContainer().getExamples().isEmpty()) {
      String mostPopularClassification = argmaxClassification(parentDataset);

      return new Leaf(mostPopularClassification);
    }

    String mostPopularClassification = argmaxClassification(dataset);

    if (listOfFeatures.isEmpty() || onlySpecifiedClassification(dataset)) {
      return new Leaf(mostPopularClassification);
    }

    String maxIGFeature = getMaxInformationGainFeature(dataset, listOfFeatures);
    Map<String, Node> subtrees = new HashMap<>();

    for (String value : dataset.getFeatureValues().get(maxIGFeature)) {
      List<String> newListOfFeatures = new ArrayList<>(listOfFeatures);
      newListOfFeatures.remove(maxIGFeature);

      Node newNode = ID3Iteration(Dataset.partitionDataset(dataset, maxIGFeature, value), dataset, newListOfFeatures);
      subtrees.put(value, newNode);
    }

    return new Node(maxIGFeature, subtrees);
  }

  private String getMaxInformationGainFeature(Dataset dataset, List<String> listOfFeatures) {
    double maxIG = 0d;
    String winnerFeature = null;

    for (String feature : listOfFeatures) {
      double ig = Utility.informationGain(dataset, feature);

      if (ig > maxIG) {
        maxIG = ig;
        winnerFeature = feature;
      }
    }

    return winnerFeature;
  }

  private boolean onlySpecifiedClassification(Dataset dataset) {
    return dataset.getClassificationValues().size() == 1;
  }

  private String argmaxClassification(Dataset dataset) {
    Map<String, Long> counter = dataset.getExamplesContainer().getExamples().stream()
        .collect(Collectors.groupingBy(Example::getClassification, Collectors.counting()));

    // TODO: Possibly sort alphabetically
    return counter.keySet().stream()
        .max(Comparator.comparing(counter::get)).orElse("bruh idk");
  }

  @Override
  public void predict(Dataset dataset) {
    StringBuilder prediction = new StringBuilder("[PREDICTIONS]:");
    Map<String, Map<String, Integer>> confusionMatrix = new HashMap<>();

    int numOfCorrect = 0;

    for (Example example : dataset.getExamplesContainer().getExamples()) {
      String leaf = getLeaf(treeParent, example, dataset.getExamplesContainer().getHeader().getFeatures());
      prediction.append(" ").append(leaf);

      if (leaf.equals(example.getClassification())) {
        numOfCorrect++;
      }

      insertIntoConfusionMatrix(confusionMatrix, leaf, example.getClassification());
    }

    double accuracy = ((double) numOfCorrect) / dataset.getExamplesContainer().getExamples().size();

    System.out.println(prediction);
    System.out.print(String.format("[ACCURACY]: %.5f\n", accuracy).replaceAll(",", "."));

    System.out.println("[CONFUSION_MATRIX]:");

    List<String> list = new ArrayList<>(dataset.getClassificationValues());
    Collections.sort(list);

    for (String i : list) {
      for (String j : list) {
        Integer val = confusionMatrix.get(i).get(j);

        System.out.print((val == null ? 0 : val) + " ");
      }
      System.out.println();
    }
  }

  private void insertIntoConfusionMatrix(Map<String, Map<String, Integer>> confusionMatrix, String predicted, String answer) {
    if (!confusionMatrix.containsKey(answer)) {
      Map<String, Integer> temp = new HashMap<>();
      temp.put(predicted, 1);
      confusionMatrix.put(answer, temp);
      return;
    }

    if (!confusionMatrix.get(answer).containsKey(predicted)) {
      confusionMatrix.get(answer).put(predicted, 1);
      return;
    }

    confusionMatrix.get(answer).put(predicted, confusionMatrix.get(answer).get(predicted) + 1);
  }

  private String getLeaf(Node node, Example example, List<String> features) {
    if (node instanceof Leaf) {
      return node.feature;
    }

    String value = example.getValues().get(features.indexOf(node.feature));

    return getLeaf(node.subtrees.get(value), example, features);
  }
}
