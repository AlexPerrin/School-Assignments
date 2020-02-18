#include <stdio.h>
#include <stdlib.h>
#include <math.h>

typedef struct node {
    double coef;
    int expn;
    struct node *next;
} NODE;

typedef struct poly {
	NODE	*head;
	int		terms;
} POLY;

NODE *createNode (int i, double c, NODE *nxt) {
// Create new Node and initialize its value and next pointer
	NODE *pNode = (NODE*)malloc(sizeof(NODE));
	if (pNode != NULL)	{
		pNode->coef = c;
        pNode->expn = i;
		pNode->next = nxt;
		}
	return pNode;
}

void init(POLY *p) {
    p->head = NULL;
    p->terms = 0;
}

void addFront(POLY *p, int i, double c) {
    NODE* newNode = createNode(i, c, p->head);
    p->head = newNode;
    p->terms++;
}

void addEnd(POLY *p, int i, double c) {
    NODE* newNode = createNode (i, c, NULL);
    p->terms++;
	if (p->head == NULL) {
		p->head = newNode;
	}
    else {
		NODE* ptr = p->head;
		while(ptr->next!=NULL) {
			ptr = ptr->next;
	    }
		ptr->next = newNode;
	}
}

void setCoefficient(POLY *p, int i, double c) {
    NODE* ptr = p->head;
    NODE* prev;
    //addFront if poly is empty
    if(ptr == NULL) {
        addFront(p, i, c);
        return;
    }
    //addFront if expn is bigger than the first term
    if(i > ptr->expn) {
        addFront(p, i, c);
        return;
    }
    //Iterate until an expn smaller is found
    while(ptr->next && !(i > ptr->next->expn)) {
        prev = ptr;
        ptr = ptr->next;
    }
    //addEnd if at the end
    if(ptr->next == NULL) {
        addEnd(p, i, c);
        return;
    }
    //Replace term if expn already exists
    if(i == ptr->expn) {
        NODE* newNode = createNode(i, c, ptr->next);
        prev->next = newNode;
        p->terms++;
        return;
    }
    //Insert term if next expn is smaller
    if(i > ptr->next->expn) {
        NODE* newNode = createNode(i, c, ptr->next);
        ptr->next = newNode;
        p->terms++;
        return;
    }
}

double getCoefficient(POLY *p, int i) {
    NODE* ptr = p->head;
    while(ptr->next) {
        if(i == ptr->expn) {
            return ptr->coef;
        }
    ptr = ptr->next;
    }
}

int getDegree(POLY *p) {
    return p->terms;
}

void display(POLY *p) {
    NODE *ptr = p->head;
	while(ptr->next) {
    	printf("%.0lfx^%d + ", ptr->coef, ptr->expn);
    	ptr = ptr->next;
	}
	printf("%.0lfx^%d", ptr->coef, ptr->expn);
	printf(" [%d]\n",p->terms);
}

double eval(POLY *p, double x) {
    NODE *ptr = p->head;
    double sum;
    while(ptr->next) {
        sum += pow(ptr->coef*x, ptr->expn);
        ptr = ptr->next;
    }
    return sum;
}


int main(void) {
    POLY eqn;
    init(&eqn);
    setCoefficient(&eqn,2,5);
    setCoefficient(&eqn,5,1);
    setCoefficient(&eqn,3,1);
    setCoefficient(&eqn,1,1);
    printf("x^%d has coefficient %.0lf\n",2,getCoefficient(&eqn,2));
    printf("%lf\n",eval(&eqn,3));
    display(&eqn);
}