#include <jni.h>
#include <android/log.h>
#include <stdio.h>

#include <speex/speex.h>
#include <speex/speex_echo.h>
#include <speex/speex_preprocess.h>
#include "include/speex/speex_echo.h"
#include "include/speex/speex_preprocess.h"

#define  TAG    "echo_process"
// 定义info信息
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG,__VA_ARGS__)
// 定义debug信息
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
// 定义error信息
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG,__VA_ARGS__)

SpeexEchoState *st;
SpeexPreprocessState *den;

int SPEEX_FRAME_BYTE = 64;
//#define SPEEX_FRAME_BYTE 64

  JNIEXPORT void JNICALL Java_com_pullmi_shanghai_TalkActivity_speex_1EchoCanceller_1open
(JNIEnv *env, jobject jobj, jint jSampleRate, jint jBufSize, jint jTotalSize)
{
  //init 
  int sampleRate = jSampleRate;
  st = speex_echo_state_init(jBufSize, jTotalSize);
  den = speex_preprocess_state_init(jBufSize, sampleRate);
  speex_echo_ctl(st, SPEEX_ECHO_SET_SAMPLING_RATE, &sampleRate);
  speex_preprocess_ctl(den, SPEEX_PREPROCESS_SET_ECHO_STATE, st);
}

  JNIEXPORT jshortArray JNICALL Java_com_pullmi_shanghai_TalkActivity_speex_1EchoCanceller_1process
(JNIEnv *env, jobject jobj, jshortArray input_frame, jshortArray echo_frame)
{
  //create native shorts from java shorts
  jint length = (*env)->GetArrayLength(env, input_frame);
  jshort *native_input_frame = (*env)->GetShortArrayElements(env, input_frame, 0);
  jshort *native_echo_frame = (*env)->GetShortArrayElements(env, echo_frame, 0);

  SPEEX_FRAME_BYTE = length;
  short ref[SPEEX_FRAME_BYTE], mic[SPEEX_FRAME_BYTE], out[SPEEX_FRAME_BYTE];
  int i;
  for ( i = 0; i < SPEEX_FRAME_BYTE; ++i)
  {
    // code
    ref[i] = native_echo_frame[i];
    mic[i] = native_input_frame[i];
  }
  //allocate memory for output data

  jshortArray temp = (*env)->NewShortArray(env, length);
  jshort *native_output_frame = (*env)->GetShortArrayElements(env, temp, 0);
  if (0 >= length || NULL == native_output_frame)
  {
    /* code */
    LOGE("create out error");
    return;
  }

  //call echo cancellation
  //speex_echo_cancellation(st, native_input_frame,native_echo_frame,native_output_frame);
  speex_echo_cancellation(st, mic, ref, out);
  //preprocess output frame
  //speex_preprocess_run(den, native_output_frame);
  speex_preprocess_run(den, out);

  for ( i = 0; i < length; ++i)
  {
    //LOGE("output_frame %d = %d",i,out[i]);
    native_output_frame[i] = out[i];
  }

  //convert native output to java layer output 
  jshortArray output_shorts = (*env)->NewShortArray(env, length);
  (*env)->SetShortArrayRegion(env, output_shorts, 0, length, native_output_frame);

  //cleanup and return 
  (*env)->ReleaseShortArrayElements(env, input_frame, native_input_frame, 0);
  (*env)->ReleaseShortArrayElements(env, echo_frame, native_echo_frame, 0);
  (*env)->ReleaseShortArrayElements(env, temp, native_output_frame, 0);

  return output_shorts;
}

  JNIEXPORT void JNICALL Java_com_pullmi_shanghai_TalkActivity_speex_1EchoCanceller_1close
(JNIEnv *env, jobject jobj)
{
  //close
  speex_echo_state_destroy(st);
  speex_preprocess_state_destroy(den);
}
