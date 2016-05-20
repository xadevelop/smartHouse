#include <jni.h>

#ifndef _Included_speex_jni
#define _Included_speex_jni

#ifdef __cplusplus
extern "C"{
#endif

JNIEXPORT void JNICALL java_com_pullmi_shanghai_TalkActivity_speex_EchoCanceller_open
(JNIEnv *env, jobject jobj, jint jSampleRate, jint jBufSize, jint jTotalSize);

JNIEXPORT jshortArray JNICALL java_scom_pullmi_shanghai_TalkActivity_speex_EchoCanceller_process
(JNIEnv *env, jobject jobj, jshortArray input_frame, jshortArray echo_frame);

JNIEXPORT void JNICALL java_scom_pullmi_shanghai_TalkActivity_speex_EchoCanceller_close
(JNIEnv *env, jobject jobj);

#ifdef __cplusplus
}
#endif
#endif
