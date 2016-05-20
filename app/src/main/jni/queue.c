/*
 * queue.c
 *
 *  Created on: 2013-9-2
 *      Author: Jean
 */

#include <stdlib.h>
#include <pthread.h>
#include <unistd.h>

#include "queue.h"

#ifndef ALOG
#include <stdio.h>
#define ALOG(...) printf(__VA_ARGS__);
#endif

pthread_mutex_t mtx = PTHREAD_MUTEX_INITIALIZER;
pthread_cond_t cnd = PTHREAD_COND_INITIALIZER;

volatile int holdon = 1;

queue *queueCreate(void) {
	struct queue *q;
	if ((q = malloc(sizeof(queue))) == NULL) {
		return NULL;
	}

	q->head = q->tail = NULL;
	q->len = 0;
	q->free = NULL;
	return q;
}

queue *queuePush(queue *q, void *data) {
	pthread_mutex_lock(&mtx);
	ALOG("add\n");
	queueNode *node;
	if ((node = malloc(sizeof(queueNode))) == NULL) {
		return NULL;
	}

	node->data = data;
	if (q->len == 0) {
		q->head = q->tail = node;
		node->prev = node->next = NULL;
	} else {
		node->prev = q->tail;
		node->next = NULL;
		q->tail->next = node;
		q->tail = node;
	}

	q->len++;
	pthread_cond_signal(&cnd);
	pthread_mutex_unlock(&mtx);
	return q;
}

void queueDelNode(queue *q, queueNode *node) {
	if (node->prev) {
		node->prev->next = node->next;
	} else {
		q->head = node->next;
	}

	if (node->next) {
		node->next->prev = node->prev;
	} else {
		q->tail = node->next;
	}
	q->len--;
}

void queueRelease(queue *q) {
	unsigned long len;
	queueNode *current, *next;

	current = q->head;
	len = q->len;
	while (len--) {
		next = current->next;
		if (q->free) q->free(current->data);
		free(current);
		current = next;
	}

	free(q);
}

queueNode* queuePop(queue *q) {
	queueNode *node;
	pthread_mutex_lock(&mtx);
	if (q->len == 0) {
		pthread_cond_wait(&cnd, &mtx);
	}
	node = q->head;
	queueDelNode(q, q->head);
	pthread_mutex_unlock(&mtx);
	return node;
}

void queuePrint(queue *q) {
	queueNode *current;

	current = q->head;
	while (current != NULL) {
		ALOG("%s\n", (char *)current->data);
		current = current->next;
	}
}

void queueFreeNode(queue *q, queueNode *node) {
	if (q->free) {
		q->free(node);
	}

	free(node);
	node = NULL;
}

/*for test*/
void randomSleep() {
	srand(time(NULL));
	int i = rand() % 5 + 1;
	ALOG("sleep:%ds\n", i);
	sleep(i); /* 1s - 5s*/
}

void video_sws_func(void *data) {
	// todo:
	queue *q = (queue *) data;

	while (holdon) {
		queueNode *node = queuePop(q);
		ALOG("sws process node:%s\n", (char*)node->data);
		queueFreeNode(q, node);
	}
}

void freeAVFrame(void *frame) {
	//todo: free avframe
	ALOG("free frame\n");
}

int main(int argc, char **argv) {
	pthread_t tId;
	pthread_attr_t attr;
	queueNode *current;
	unsigned long len;
	queue *q;
	q = queueCreate();
	q->free = freeAVFrame;
	pthread_attr_init(&attr);
	pthread_create(&tId, &attr, video_sws_func, (void *) q);

	randomSleep();
	queuePush(q, "1");
	randomSleep();
	queuePush(q, "2");
	randomSleep();
	queuePush(q, "3");
	randomSleep();
	queuePush(q, "4");

	holdon = 0;
	pthread_join(tId, NULL);

	queueRelease(q);
	return 0;
}
