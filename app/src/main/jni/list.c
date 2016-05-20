/*
 * list.c
 *
 *  Created on: Sep 3, 2013
 *      Author: danny
 */
#include <stdlib.h>
#include <pthread.h>
#include <unistd.h>

#include "list.h"

#ifndef ALOG
#include <stdio.h>
#define ALOG(...) printf(__VA_ARGS__);
#endif

#define LOG_TAG "JNIDemo"
#define ENCODE_TAG "ENCODE"
#undef LOG
#include<android/log.h>
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)


list *list_init(void) {

	struct list *myroot;

	if((myroot = malloc(sizeof(list))) == NULL) {
		exit(1);
	}

	myroot->len = 0;
	myroot->head = NULL;
	myroot->tail = NULL;

	return myroot;
}

void list_push(struct list* myroot, struct listNode* mylistNode) {

	mylistNode->next = NULL;

	if(myroot->len == 0) {
		LOGE("===================================2");
		myroot->head = mylistNode;
		myroot->tail = mylistNode;
	} else {
		LOGE("===================================3");
		myroot->tail->next = mylistNode;
		myroot->tail = mylistNode;
	}

	if(myroot->head == NULL) {
		LOGE("++++++++++++++ head NULL ++++++++++++++++++");
	}

	myroot->len ++;

	LOGE("--------> current capactiy for list: %d", myroot->len);
}

void list_remove(struct list* myroot, struct listNode* mylistNode) {

	//list has only one element
	if(myroot->head == mylistNode && myroot->tail == mylistNode) {
		myroot->head = NULL;
		myroot->tail = NULL;
		myroot->len --;
		return;
	} else {
		//more than one elements
		myroot->head = mylistNode->next;
		myroot->len --;
	}

}

listNode* list_pop(struct list* myroot) {
	LOGE("+++++++++++ list pop ++++++++++");
	listNode *node = myroot->head;
	if(node == NULL) {
		LOGE("++++++++++ NULL ++++++++++");
		return NULL;
	} else {
		list_remove(myroot, node);
		return node;
	}
}

