#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include "header.h"

Stack initStack(int size) {
    Stack newStack;
    newStack.size = size;
    Tree array[size];
    newStack.array = array;
    newStack.top = -1;
    return newStack;
}

bool isempty(Stack stack) {
    if(stack.top == -1)
      return true;
   else
      return false;
}

bool isfull(Stack stack) {
    if(stack.top == stack.size)
      return true;
   else
      return false;
}

Tree peek(Stack stack) {
   return stack.array[stack.top];
}

Tree pop(Stack stack) {
   Node* data;
   if(!isempty(stack)) {
      data = stack.array[stack.top];
      stack.top--;  
      return data;
   } else {
      printf("Could not retrieve data, Stack is empty->\n");
   }
}

void push(Stack stack, Node* data) {
   if(!isfull(stack)) {
      stack.top++;   
      stack->stack[stack.top] = data;
   } else {
      printf("Could not insert data, Stack is full->\n");
   }
}

void printStack(Stack stack) {
    int i = stack.size;
    printf("[size: %d] ",stack->size);
    printf("[top: %d] ",stack->top);
    for (int i = 0; i<stack->top; i++) {
        printf("%d ",stack->stack[i]);
    } 
    printf("\n");
}