# Project 3

The goal of this project is to evaluate your ability to design, implement and test a large software
application. It is not just a programming assignment -- you are exposed to the engineering issues
related to requirement analysis, software design and development. You will have the sole responsibility
to choose the appropriate data structures, develop the application in an efficient way, and test it
thoroughly against the project requirements.

**1 Requirement**

In this project, you will design and implement a simple search engine that stores words from a file into
a special dictionary database. Thereby, the search engine allows users to specify several keywords, and
return a list of files that contain the keywords. You will provide _an implementation_ of the dictionary
database. You are to use a “trie” and apply this data structure to store and search for words (strings) in
a large dictionary. The use of “trie” is discussed in the following section.

_Note: you can use the code provided by the labs, lectures, or used in your previous assignments._

**2. Trie**

A trie is a kind of search tree that stores sequences of data, such as character strings. A trie can make
searching efficient. The word trie comes from reTRIEval, but is pronounced TRY to distinguish it from
other kinds of trees.

A trie can be used to implement a spelling dictionary. Each node stores one letter, plus an array of 26
references, one for each possible next (lower case) letter. Each node stores a boolean value that
indicates if the node is the last letter in a word. If the letter of the node is the last letter, the boolean
value is set to true. Additional, each node will keep the frequency of the word ending at that node. If
the boolean value is true, the frequency is greater or equal to 1.

The root node is a special case; it stores a space as its letter value. The diagram below shows a trie into
which the following words have been added: " **_beer bee bid_** ".

Also, note that all of the nodes currently in the trie are shown in the diagram. Assume all references are
"null" unless there is an arrow shown.

_You can consider that tracing a path through a trie from root to leaf spells a word. You can assume
that the input file contains only lower case characters a-z. You can add other fileds to the trie node._


_Implementing the Dictionary:_

The Dictionary contains the following functions, (you can add other methods if you need)

**void insertWord (String word)**
insert the indicated word into the trie. Create nodes as needed and be sure to set the boolean on
the last node to true. If a word has already been entered, increase the count field of the last
node.

**void addFileContentsToDictionary (String inputFileName)**
add each word in the indicated file to the Dictionary. This method contains repeated calls to
insertWord.

**int findWord (String word)**
return the count of the indicated word.

_Implementing the TrieNode:_

A TrieNode represents a node in the tree. You will need to define its data fields and add some functions
too.

_Implementing the Search Engine:_

A search engine contains the following functions, (you can add other functions).

**void readFiles(char* filenames)**
Read a list of filenames, and create a dictionary for each file

**char* findKeywords (char* keywords)**


```
search the keywords in the existing filenames, and return the a list of filenames that contain the
keywords. The filename with the most frequent keywords is ordered with the highest rank. The
rank can be computed by the summation of the frequency of each keyword in a file.
```
**Hints:**
The following is a list of suggestions for things you should consider in your submission and
implementation:

1. You should consider the tradeoffs of using a single dictionary to track the words in all the files or
having a dictionary for each file
2. You can consider the worst, best, and average performance of your implementations
3. You can consider the run-time performance for your implementation (using
System.currentTimeMillis() to measure the running time)
