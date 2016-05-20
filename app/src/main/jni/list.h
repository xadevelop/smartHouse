/*
 * list.h
 *
 *  Created on: Sep 3, 2013
 *      Author: danny
 */

#ifndef LIST_H_
#define LIST_H_

#include <stdio.h>

typedef struct listNode {
	struct listNode *next;
	struct AVFrame *frame;
}listNode;

typedef struct list {
	listNode *head;
	listNode *tail;
	void (*free) (void *ptr);
	int len;
} list;

list* list_init(void);
void list_push(struct list*, struct listNode*);
listNode* list_pop(struct list* myroot);
void list_remove(struct list*, struct listNode*);


#endif /* LIST_H_ */
