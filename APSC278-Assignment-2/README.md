# Assignment 2

The goal of this project is to extend your knowledge of Tree data structures. In particular, you will be
given the struct definition of ExpressionTree extended from a BinaryTreeNode struct. You are
responsible to provide implementation of the ExpressionTree.

## 1 Objectives

The objective of this project is to implement an expression tree that represents an algebraic expression in
a tree structure. For example, an algebraic expression such as

a/b + (c-d)e Equation (1)

can be represented as an expression tree, shown as follows.

The terminal nodes (leaves) of an expression tree are the variables or constants in the expression (a, b, c,
d, and e). The non-terminal nodes of an expression tree are the operators (+, -, *, /). Notice that the
parentheses that appear in the Equation (1) do not appear in the tree. Nevertheless, the tree representation
has captured the intent of the parentheses since the subtraction is lower in the tree than the multiplication.

## 2 Requirements

You are required to provide the implementation the following functions of an ExpressionTree.

struct ExpressionTreeNode{
char key;
ExpressTreeNode *left;
ExpressTreeNode *right;
}

ExpressionTreeNode *newTreeNode (char key)
creates a new tree node using the key and return the reference to the newly created node

bool isEmpty(ExpressionTreeNode *r)
check the tree with the root, r, if it is an empty tree

char getKey(ExpressionTreeNode *r);


return the key of the tree node, r.

boolean isLeaf(ExpressionTreeNode *r)
check if the tree node, r, is a leave

int getHeight(ExpressionTreeNode *r)
return the higheight of the tree with the root of r

ExpressionTreeNode * buildExpTree(char * postfixExp)
creates an expression tree from a postfix expression string as the parameter (i.e., postfixExp) and the
return the root of the expression tree

char *parseInfix (char *s)
reads an infix expression, s and converts it to a postfix expression as a return from the function

char *parsePrefix(char *s)
reads a prefix expression, s and converts it to a postfix expression as a return from the function

## 2.2 Implementation

o Complete the methods defined in ExpressionTree
o Write a PreOrder traversal function that displays the nodes in the expression tree in an infix format
expression, and computes the expression
o Write a PostOrder traversal function that displays the nodes in the expression tree in a postfix format,
and computes the expression
o Write an InOrder traversal function that displays the nodes in the expression tree in an infix format,
and computes the expression
o Write an Breadth-First Traversal function that displays the nodes in the expression tree following the
breadth-first traversal.

## 2.3 Main Program

You should thoroughly test and debug your implementation.
