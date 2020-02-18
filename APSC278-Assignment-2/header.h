#include <stdio.h>
#include <stdbool.h>

typedef struct Node {
    char key;
    struct Node* left;
    struct Node* right;
} Node;

typedef struct Tree {
    Node* root;
} Tree;

typedef struct Stack {
    int size;
    int top;
    Tree array[100];
} Stack;

Stack initStack(int size);

bool isempty(Stack ARRAY);

bool isfull(Stack ARRAY);

Tree peek(Stack ARRAY);

Tree pop(Stack ARRAY);

void push(Stack ARRAY, Node* data);

void printStack(Stack ARRAY);

Node newTreeNode(char key);

bool isEmpty(Node *r);

char getKey(Node *r);

bool isLeaf(Node *r);

int getHeight(Node *r);

void buildExpTree(Stack s, char postfixExp[]);

char parseInfix(char *s);

char parsePrefix(char *s);