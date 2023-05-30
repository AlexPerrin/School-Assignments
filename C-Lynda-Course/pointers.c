#include <stdio.h>

int main() {
    int num = 10; //initialize int variable with value
    int *pnum;  //initiliaze point variable
    pnum = &num;    //assign address to pointer

    printf("Variable\tValue\t\tAddress\n");
    printf("num\t\t\t%d\t\t\t%p\n",num,pnum);
    printf("num\t\t\t%d\t\t\t%p\n",*pnum,&num);
    printf("pnum\t\t%p\t%p\n\n",pnum,&pnum);


    int array[] = {10,20,30,40,50};
    int *parray = array;
    for(int i=0;i<5;i++) {
        printf("array\t\t%d\t\t%p\n",*parray,parray);
        *parray++;
    }
}