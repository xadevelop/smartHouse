/*
 * queue.h
 *
 *  Created on: 2013-9-2
 *      Author: Jean
 */

#ifndef QUEUE_H_
#define QUEUE_H_


typedef struct queueNode {
	struct queueNode *prev;
	struct queueNode *next;
	void * data;
} queueNode;

typedef struct queue {
	queueNode *head;
	queueNode *tail;
	void (*free)(void *ptr);
	unsigned long len;
} queue;

queue *queueCreate(void);
queue *queuePush(queue *q, void *data);
void queueDelNode(queue *q, queueNode *node);
void queueRelease(queue *q);
queueNode* queuePop(queue *q);
void queuePrint(queue *q);

#endif /* QUEUE_H_ */
