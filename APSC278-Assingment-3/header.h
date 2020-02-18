#include <stdio.h>
#include <stdbool.h>

typedef struct Node {
    bool isWord;
    struct Node* *letters
} Node;

typedef struct Trie {
    Node* root;
} Trie;