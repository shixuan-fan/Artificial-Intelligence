#Decision Tree

This program will train a decision with the given train file and test them on the test file. Also, "prune" is an optional feature.

To run the program:

1. `javac *.java`

2. `java DecisionTreeMain <modeFlag> <trainFile> <testFile> [optional pruneFile]`

##Mode Flag

"0": Print the information gain for each attribute at the root node based on the training set.

"1": Create a decision tree from the training set and print the tree.

"2": Create a decision tree from the training set and print the classification for each example in the test set.

"3": Create a decision tree from the training set and print the accuracy of the classification for the test set.

"4": Create a decision tree from the training set, prune it using the tuning set, and then print the tree.

"5": Create a decision tree from the training set, prune it using the tuning set, and then print the 
classification for each example in the test set.

"6":  Create a decision tree from the training set, prune it using the tuning set, print the classification for each example in the test set, and then print the accuracy of the classification for the test set.
