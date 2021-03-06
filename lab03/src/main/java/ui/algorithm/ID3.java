package ui.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
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
  private final Integer maxDepth;
  private Dataset originalDataset;

  public ID3(Integer depth) {
    this.maxDepth = depth;
  }

  @Override
  public void fit(Dataset dataset) {
    originalDataset = dataset;
    treeParent = ID3Iteration(dataset, dataset, dataset.getExamplesContainer().getHeader().getFeatures(), 0);
    System.out.println("[BRANCHES]:");

    Stack<TreeNode> stack = new Stack<>();
    stack.add(new TreeNode(treeParent, 0, ""));

    while (!stack.isEmpty()) {
      TreeNode treeNode = stack.pop();
      Node node = treeNode.node;

      if (node instanceof Leaf) {
        System.out.printf("%s%s\n", treeNode.path, node.feature);
        continue;
      }

      for (String value : node.subtrees.keySet()) {
        stack.add(
            new TreeNode(
                node.subtrees.get(value),
                treeNode.depth + 1,
                treeNode.path + String.format("%d:%s=%s ", treeNode.depth + 1, node.feature, value)));
      }
    }
  }

  private static class TreeNode {

    private final Node node;
    private final int depth;
    private final String path;

    public TreeNode(Node node, int depth, String path) {
      this.node = node;
      this.depth = depth;
      this.path = path;
    }
  }

  private Node ID3Iteration(Dataset dataset, Dataset parentDataset, List<String> listOfFeatures, int depth) {
    if (dataset.getExamplesContainer().getExamples().isEmpty()) {
      String mostPopularClassification = argmaxClassification(parentDataset);

      return new Leaf(mostPopularClassification);
    }

    String mostPopularClassification = argmaxClassification(dataset);

    if (listOfFeatures.isEmpty() || onlySpecifiedClassification(dataset)) {
      return new Leaf(mostPopularClassification);
    }

    if (maxDepth != null && depth >= maxDepth) {
      return new Leaf(argmaxClassification(dataset));
    }

    String maxIGFeature = getMaxInformationGainFeature(dataset, listOfFeatures);
    Map<String, Node> subtrees = new HashMap<>();

    for (String value : originalDataset.getFeatureValues().get(maxIGFeature)) {
      List<String> newListOfFeatures = new ArrayList<>(listOfFeatures);
      Collections.sort(newListOfFeatures);
      newListOfFeatures.remove(maxIGFeature);

      Node newNode = ID3Iteration(Dataset.partitionDataset(dataset, maxIGFeature, value), dataset, newListOfFeatures, depth + 1);
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

    Comparator<Entry<String, Long>> sortByCount = Entry.comparingByValue();
    Comparator<Entry<String, Long>> sortByName = Entry.comparingByKey();

    return counter.entrySet().stream()
        .max(sortByCount.thenComparing(sortByName.reversed())).map(Entry::getKey).orElse("bruh");
  }

  @Override
  public void predict(Dataset dataset) {
    StringBuilder prediction = new StringBuilder("[PREDICTIONS]:");
    Map<String, Map<String, Integer>> confusionMatrix = new HashMap<>();

    int numOfCorrect = 0;

    for (Example example : dataset.getExamplesContainer().getExamples()) {
      String leaf = getLeaf(treeParent, dataset, example, dataset.getExamplesContainer().getHeader().getFeatures());
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

  private String getLeaf(Node node, Dataset dataset, Example example, List<String> features) {
    if (node instanceof Leaf) {
      return node.feature;
    }

    String value = example.getValues().get(features.indexOf(node.feature));

    if (!node.subtrees.containsKey(value)) {
      ArrayList<String> values = new ArrayList<>(dataset.getClassificationValues());
      Collections.sort(values);
      return values.get(0);
    }

    return getLeaf(node.subtrees.get(value), dataset, example, features);
  }
}
