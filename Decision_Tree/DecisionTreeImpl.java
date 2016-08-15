import java.util.*;

/**
 * Fill in the implementation details of the class DecisionTree using this file. Any methods or
 * secondary classes that you want are fine but we will only interact with those methods in the
 * DecisionTree framework.
 * 
 * You must add code for the 1 member and 4 methods specified below.
 * 
 * See DecisionTree for a description of default methods.
 */
public class DecisionTreeImpl extends DecisionTree {
  private DecTreeNode root;
  //ordered list of class labels
  private List<String> labels; 
  //ordered list of attributes
  private List<String> attributes; 
  //map to ordered discrete values taken by attributes
  private Map<String, List<String>> attributeValues; 
  
  /**
   * Answers static questions about decision trees.
   */
  DecisionTreeImpl() {
    // no code necessary this is void purposefully
  }

  /**
   * Build a decision tree given only a training set.
   * 
   * @param train: the training set
   */
  DecisionTreeImpl(DataSet train) {

    this.labels = train.labels;
    this.attributes = train.attributes;
    this.attributeValues = train.attributeValues;

    List<String> attributeList = new LinkedList<String>(attributes);
    String maxLabel = getMajorVote(train.instances);
    this.root = buildTree(train.instances, attributeList, null, maxLabel);
  }

  /**
   * Classify a given instance.
   */
  @Override
  public String classify(Instance instance) {
    DecTreeNode current = root;
    while (!current.terminal) {
      int attrIndex = getAttributeIndex(current.attribute);
      String attrValue = instance.attributes.get(attrIndex);
      int attrValueIndex = getAttributeValueIndex(current.attribute, attrValue);
      current = current.children.get(attrValueIndex);
    }

    return current.label;
  }

  /**
   * Calculate the information gain at the root.
   */
  @Override
  public void rootInfoGain(DataSet train) {
    this.labels = train.labels;
    this.attributes = train.attributes;
    this.attributeValues = train.attributeValues;

    double originalEntropy = getEntropy(train.instances);
    for (String attr : attributes) {
      double infoGain = originalEntropy - getEntropy(train.instances, attr);
      System.out.print(attr + ' ');
      System.out.format("%.5f\n", infoGain);
    }
  }
  
  @Override
  public void printAccuracy(DataSet test) {
    double accuracy = getAccuracy(test.instances);
    System.out.format("%.5f\n", accuracy);
  }
  
  /**
   * Build a decision tree given a training set then prune it using a tuning set.
   * ONLY for extra credits
   * @param train: the training set
   * @param tune: the tuning set
   */
  DecisionTreeImpl(DataSet train, DataSet tune) {

    this.labels = train.labels;
    this.attributes = train.attributes;
    this.attributeValues = train.attributeValues;

    // Build the original tree.
    List<String> attributeList = new LinkedList<String>(attributes);
    String maxLabel = getMajorVote(train.instances);
    this.root = buildTree(train.instances, attributeList, null, maxLabel);

    // Prune.
    prune(tune.instances);
  }
  
  @Override
  /**
   * Print the decision tree in the specified format
   */
  public void print() {

    printTreeNode(root, null, 0);
  }

  /**
   * Prints the subtree of the node with each line prefixed by 4 * k spaces.
   */
  public void printTreeNode(DecTreeNode p, DecTreeNode parent, int k) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < k; i++) {
      sb.append("    ");
    }
    String value;
    if (parent == null) {
      value = "ROOT";
    } else {
      int attributeValueIndex = this.getAttributeValueIndex(parent.attribute, p.parentAttributeValue);
      value = attributeValues.get(parent.attribute).get(attributeValueIndex);
    }
    sb.append(value);
    if (p.terminal) {
      sb.append(" (" + p.label + ")");
      System.out.println(sb.toString());
    } else {
      sb.append(" {" + p.attribute + "?}");
      System.out.println(sb.toString());
      for (DecTreeNode child : p.children) {
        printTreeNode(child, p, k + 1);
      }
    }
  }

  /**
   * Helper function to get the index of the label in labels list
   */
  private int getLabelIndex(String label) {
    for (int i = 0; i < this.labels.size(); i++) {
      if (label.equals(this.labels.get(i))) {
        return i;
      }
    }
    return -1;
  }
 
  /**
   * Helper function to get the index of the attribute in attributes list
   */
  private int getAttributeIndex(String attr) {
    for (int i = 0; i < this.attributes.size(); i++) {
      if (attr.equals(this.attributes.get(i))) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Helper function to get the index of the attributeValue in the list for the attribute key in the attributeValues map
   */
  private int getAttributeValueIndex(String attr, String value) {
    for (int i = 0; i < attributeValues.get(attr).size(); i++) {
      if (value.equals(attributeValues.get(attr).get(i))) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Build a decision tree by creating DecTreeNode recursively.
   */
  private DecTreeNode buildTree(List<Instance> instances, List<String> candidates, String parentAttrValue, String defaultLabel) {
    // No more instances.
    if (instances.isEmpty()) {
      return new DecTreeNode(defaultLabel, null, parentAttrValue, true);
    }
    // Check if all instances have the same label and count the vote for each label.
    if (allSameLabel(instances)) {
      return new DecTreeNode(instances.get(0).label, null, parentAttrValue, true);
    }

    String maxLabel = getMajorVote(instances);
    
    // No remaining attribute.
    if (candidates.isEmpty()) {
      return new DecTreeNode(maxLabel, null, parentAttrValue, true);
    }

    // Find best attribute and create tree node.
    String bestAttr = getBestAttr(instances, candidates);
    double infoGain = getEntropy(instances) - getEntropy(instances, bestAttr);
    int bestIndex = candidates.indexOf(bestAttr);
    candidates.remove(bestAttr);
    DecTreeNode node = new DecTreeNode(maxLabel, bestAttr, parentAttrValue, false);

    for (String attrValue : attributeValues.get(bestAttr)) {
      List<Instance> valueInstances = new LinkedList<Instance>();
      Iterator<Instance> instanceIt = instances.iterator();
      while (instanceIt.hasNext()) {
        Instance instance = instanceIt.next();
        if (instance.attributes.get(getAttributeIndex(bestAttr)).equals(attrValue)) {
          instanceIt.remove();
          valueInstances.add(instance);
        }
      }

      node.addChild(buildTree(valueInstances, candidates, attrValue, maxLabel));
    }

    candidates.add(bestIndex, bestAttr);
    return node;
  }

  /**
   * Use entropy to find attribute with lowewt entropy.
   */
  private String getBestAttr(List<Instance> instances, List<String> candidates) {
    double minEntropy = Double.MAX_VALUE;
    String minAttr = null;
    for (String candidate : candidates) {
      double currentEntropy = getEntropy(instances, candidate);
      if (currentEntropy < minEntropy) {
        minEntropy = currentEntropy;
        minAttr = candidate;
      }
    }

    return minAttr;
  }

  // Calculate the entropy based on current instances (without attributes).
  private double getEntropy(List<Instance> instances) {
    int[] labelCounts = new int[labels.size()];
    for (Instance instance: instances) {
      labelCounts[getLabelIndex(instance.label)]++;
    }

    double entropy = 0;
    for (int labelCount : labelCounts) {
      double fraction = (double) labelCount / instances.size();
      entropy += -fraction * (Math.log(fraction) / Math.log(2));
    }

    return entropy;
  }

  private double getEntropy(List<Instance> instances, String attr) {
    int attrIndex = getAttributeIndex(attr);
    // Store the count for each label in each attribute value.
    int[][] labelCount = new int[attributeValues.get(attr).size()][labels.size()];
    for (Instance instance : instances) {
      int attrValueIndex = getAttributeValueIndex(attr, instance.attributes.get(attrIndex));
      labelCount[attrValueIndex][getLabelIndex(instance.label)]++;
    }

    // Calculate the entropy.
    int totalNum = instances.size();
    double totalEntropy = 0;
    for (int[] valueCounts : labelCount) {
      // Get the total number of instances with the attribute value.
      int valueNum = 0;
      for (int valueCount : valueCounts) {
        valueNum += valueCount;
      }

      if (valueNum == 0) {
        continue;
      }

      // Calculate single entropy.
      double entropy = 0;
      for (int valueCount : valueCounts) {
        if (valueCount == 0) {
          continue;
        }
        
        double fraction = (double) valueCount / valueNum;
        entropy += -fraction * (Math.log(fraction) / Math.log(2));
      }

      totalEntropy += (double) valueNum / totalNum * entropy;
    }

    return totalEntropy;
  }

  private String getMajorVote(List<Instance> instances) {
    // Count each label.
    int[] labelCount = new int[labels.size()];
    for (Instance instance : instances) {
      labelCount[getLabelIndex(instance.label)]++;
    }

    int maxCount = 0;
    int maxIndex = -1;
    for (int i = 0; i < labelCount.length; ++i) {
      System.out.println(labelCount[i]);
      if (labelCount[i] > maxCount) {
        maxCount = labelCount[i];
        maxIndex = i;
      }
    }

    return labels.get(maxIndex);
  }

  private boolean allSameLabel(List<Instance> instances) {
    String firstLabel = instances.get(0).label;
    for (Instance instance : instances) {
      if (!firstLabel.equals(instance.label)) {
        return false;
      }
    }

    return true;
  }

  private double getAccuracy(List<Instance> instances) {
    int correct = 0;
    for (Instance instance : instances) {
      if (instance.label.equals(classify(instance))) {
        correct++;
      }
    }

    return (double) correct / instances.size();
  }

  private void prune(List<Instance> instances) {
    // Keep pruning until no more improvement.
    for (;;) {
      double accuracyOrig = getAccuracy(instances);
      // Check the max accuracy the tree could get if one node's children is pruned.
      double accuracyMax = 0;
      DecTreeNode picked = null;

      // Use BFS to iterate through all nodes.
      Deque<DecTreeNode> queue = new LinkedList<DecTreeNode>();
      queue.addLast(root);
      while (!queue.isEmpty()) {
        DecTreeNode currentNode = queue.removeFirst();
        if (currentNode.terminal) {
          continue;
        }
        
        List<DecTreeNode> currentChildren = currentNode.children;

        // Prune the nodes' children and calculate the accuracy.
        currentNode.children = null;
        currentNode.terminal = true;
        double currentAccuracy = getAccuracy(instances);
        if (currentAccuracy > accuracyMax) {
          accuracyMax = currentAccuracy;
          picked = currentNode;
        }

        // Add children to the queue.
        for (DecTreeNode child : currentChildren) {
          queue.addLast(child);
        }

        // Reset current node.
        currentNode.children = currentChildren;
        currentNode.terminal = false;
      }

      // Check if there is improvement.
      // Remove the deeper node first.
      if (accuracyMax >= accuracyOrig) {
        picked.children = null;
        picked.terminal = true;
      } else {
        break;
      }
    }
  }
}
