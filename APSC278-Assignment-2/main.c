#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>
#include "header.h"

int main(void)
{
    Stack kek = initStack(10);
    
    Node *data;
    data->key = '5';

    push(kek,data);
    printStack(kek);
    data = pop(kek);
    printStack(kek);
    printf("%d", data);
}