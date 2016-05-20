/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
#include <jni.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <android/log.h>
#include <android/bitmap.h>
#include <pthread.h>
#include <sys/time.h>
#include <memory.h>

#include "libavcodec/avcodec.h"
#include "libavformat/avformat.h"
#include "libavutil/mathematics.h"
#include "libswscale/swscale.h"
#include  "libavutil/avutil.h"


#define LOG_TAG "JNIDemo"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

#define INBUF_SIZE 4096
#define AUDIO_INBUF_SIZE 20480
#define AUDIO_REFILL_THRESH 4096

#define CODEC_TYPE_AUDIO AVMEDIA_TYPE_AUDIO
#define CODEC_TYPE_VIDEO AVMEDIA_TYPE_VIDEO

#define AV_CODEC_MAX_AUDIO_FRAME_SIZE 192000

typedef struct PCMDecode {
	short table[64];
}PCMDecode;


pthread_mutex_t mut_decode = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t mut_update = PTHREAD_MUTEX_INITIALIZER;
pthread_cond_t cnd = PTHREAD_COND_INITIALIZER;

pthread_t thread[2];
pthread_attr_t attr_sws;
pthread_attr_t attr_update;

jobject currentBitmap;
int currentBitmapWidth;
int currentBitmapHeight;
AVPacket avpkt;
static AVFrame *picture;
static AVFrame *pFrameRGB;
static AVCodec *codec;
static AVCodecContext *c = NULL;
static struct SwsContext *img_convert_ctx;
AndroidBitmapInfo info;
void *pixels;

AVFormatContext *mFormatContext;
AVCodecContext *mCodecContext;
AVCodecParserContext *mParserContext;
AVCodecParser *mParser;
AVFrame *mFrame;
AVFrame *mFrameRGB;
int videoStream;
int framenum = 0;

jobject currentJVMInstance;
jclass clazz;
jmethodID mid;
JNIEnv *env;

volatile int holdon = 0;



/* This is a trivial JNI example where we use a native method
 * to return a new VM String. See the corresponding Java source
 * file located at:
 *
 *   apps/samples/hello-jni/project/src/com/example/hellojni/HelloJni.java
 */
jstring
Java_com_example_jnidemo_MainActivity_stringFromJNI( JNIEnv* env,
                                                  jobject thiz )
{
        char str[25];
        sprintf(str, "%d", avcodec_version());
        return (*env)->NewStringUTF(env, str);
}


void
Java_com_example_jnidemo_MainActivity_audioEncodeWithByte(JNIEnv* env, jbyteArray data)
{
	LOGE("--------> audio encode with byte array passed from java start -------->");
	jsize size = (*env)->GetArrayLength(env, data);
	LOGE("--------> data length: %d", size);
	LOGE("--------> audio encode with byte array passed from java end   -------->");
}

void
Java_com_example_jnidemo_MainActivity_createEngine(JNIEnv* env)
{
	LOGE("--------> create engine start -------->");
	avcodec_init();
	avcodec_register_all();
	LOGE("--------> create engine end   -------->");
}

int check_sample_fmt(AVCodec *codec, enum AVSampleFormat sample_fmt)
{
	const enum AVSampleFormat *p = codec->sample_fmts;
	while(*p != AV_SAMPLE_FMT_NONE) {
		if(*p == sample_fmt)
		{
			return 1;
		}
		p ++;
	}
	return 0;
}

//just pick the highest supported samplerate
int select_sample_rate(AVCodec *codec)
{
	const int *p;
	int best_samplerate = 0;

	if(!codec->supported_framerates)
		return 44100;
	p = codec->supported_framerates;
	while(*p)
	{
		best_samplerate = FFMAX(*p, best_samplerate);
		p++;
	}
	return best_samplerate;
}

//select layout with highest channel count
int select_channel_layout(AVCodec *codec)
{
	const uint64_t *p;
	uint64_t best_ch_layout = 0;
	int best_nb_channels = 0;

	if(!codec->channel_layouts)
		return AV_CH_LAYOUT_STEREO;
	p = codec->channel_layouts;
	while(*p) {
		int nb_channels = av_get_channel_layout_nb_channels(*p);

		if(nb_channels > best_nb_channels) {
			best_ch_layout = *p;
			best_nb_channels = nb_channels;
		}

		p++;
	}
	return best_ch_layout;
}

void
Java_com_example_jnidemo_MainActivity_play(JNIEnv* env, jbyteArray data)
{
	LOGE("-------> play start -------->");
	avcodec_init();
	avcodec_register_all();
//	uint16_t* temp = (*env)->GetPrimitiveArrayCritical(env, (jarray)data, 0);
	jbyte* temp = (*env)->GetByteArrayElements(env, data, NULL);
	AVCodec *codec = avcodec_find_decoder(CODEC_ID_PCM_ALAW);
	if(!codec)
	{
		LOGE("--------> cannot find decoder CODEC_ID_PCM_ALAW");
		exit(1);
	}
	AVCodecContext *codecContext = avcodec_alloc_context3(codec);
	if(!codecContext)
	{
		LOGE("--------> cannot alloc context3 through codec");
		exit(1);
	}

	codecContext->bit_rate = 64000;
	codecContext->sample_fmt = AV_SAMPLE_FMT_S16;
	if(!check_sample_fmt(codec, codecContext->sample_fmt))
	{
		LOGE("--------> Encoder does not support sample format %s", av_get_sample_fmt_name(codecContext->sample_fmt));
		exit(1);
	}

	codecContext->sample_rate = select_sample_rate(codec);
	codecContext->channel_layout = select_channel_layout(codec);
	codecContext->channels = av_get_channel_layout_nb_channels(codecContext->channel_layout);

	AVPacket packet;
	AVFrame *frame;

	if(avcodec_open2(codecContext, codec, NULL) < 0) {
		LOGE("--------> avcodec_open failed");
		exit(1);
	}

	//init packet
	av_init_packet(&packet);
	memcpy(packet.data, temp, 64);
	uint8_t *pktdata;
	int pktsize;
	int out_size = AVCODEC_MAX_AUDIO_FRAME_SIZE;
	uint8_t *inbuf = (uint8_t*)malloc(out_size);
	pktdata = packet.data;
	pktsize = packet.size;
	LOGE("--------> pktsize: %d", packet.size);
	while(pktsize > 0)
	{
		int len = avcodec_decode_audio3(codecContext, (short*)inbuf, &out_size, &packet);
		if(len == 0) {
			LOGE("--------> error while decoding");
			break;
		}
		LOGE("--------> decode length: %d", len);

		pktsize -= len;
		pktdata += len;
	}
	free(inbuf);
	av_free_packet(&packet);
	LOGE("-------> play end   -------->");
}

void
Java_com_example_jnidemo_VIDEOTALKService_encode(JNIEnv* env, jobject object, jbyteArray data) {
	LOGE("---------> encode start -------->");
	jclass clazz = (*env)->FindClass(env, "com/example/jnidemo/VIDEOTALKService");
	jmethodID mid = (*env)->GetMethodID(env, clazz, "getEncodedData", "([B)V");

	avcodec_init();
	avcodec_register_all();

	jbyte* temp = (*env)->GetByteArrayElements(env, data, NULL);
	jsize dataLength = (*env)->GetArrayLength(env, data);
	LOGE("-------> encode data length: %d", dataLength);


	AVCodec *codec = avcodec_find_encoder(CODEC_ID_PCM_MULAW);
	if(!codec) {
		LOGE("***************> encode audio, cant not find encoder CODEC_ID_PCM_MULAW");
		exit(1);
	}

	AVCodecContext *codecContext = avcodec_alloc_context();
	codecContext->codec_type = CODEC_TYPE_AUDIO;
	codecContext->codec_id = CODEC_ID_PCM_MULAW;
	codecContext->bit_rate = 64000;
	codecContext->sample_rate = 44100;
	codecContext->channels =2 ;
	codecContext->sample_fmt = AV_SAMPLE_FMT_S16;

	if(avcodec_open(codecContext, codec) < 0) {
		LOGE("--------> could not open codec");
		exit(1);
	}

	int frame_size, i, j, out_size, outbuf_size;
	short* samples;
	float t, tincr;
	uint8_t* outbuf;

	frame_size = codecContext->frame_size;
	samples = malloc(dataLength);
	memset(samples, 0, dataLength);
	memcpy(samples, temp, dataLength);
	outbuf_size = 10000;
	outbuf = malloc(outbuf_size);
	memset(outbuf, 0, outbuf_size);

	LOGE("--------> init end -------->");

	while(1) {
		out_size = avcodec_encode_audio(codecContext, outbuf, outbuf_size, samples);
		LOGE("&&&&&&&&&&&&&&&&&&&&&& out_size: %d", out_size);
		if(out_size > 0) {
			break;
		}
	}

	free(samples);
	free(outbuf);

	avcodec_close(codecContext);
	av_free(codecContext);

	jbyteArray returnData = (*env)->NewByteArray(env, out_size);
	(*env)->SetByteArrayRegion(env, returnData, 0, out_size, outbuf);


	(*env)->CallVoidMethod(env, object, mid, returnData);
	(*env)->ReleaseByteArrayElements(env, data, JNI_FALSE, JNI_ABORT);
	LOGE("---------> encode end   -------->");
}

void
Java_com_example_jnidemo_VIDEOTALKService_decodeDemo(JNIEnv *env, jobject object, jint width, jint height, jbyteArray data) {
	LOGE(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>decode demo>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
	jbyte *temp = (*env)->GetByteArrayElements(env, data, NULL);
	jsize dataLength = (*env)->GetArrayLength(env, data);
	LOGE("--------> data length: %d", dataLength);

	AVCodecContext *pCodecContext;
	AVCodec *pCodec;
	AVFrame *pFrame;
	AVFrame *pFrameRGB;
	AVPacket packet;
	uint8_t *buffer;
	int got_picutre;
	int out_size;

	pCodec = avcodec_find_decoder(CODEC_ID_MPEG4);

	if(!pCodec) {
		LOGE("--------> could not open CODEC_ID_MPEG4");
		exit(1);
	}

	pCodecContext = avcodec_alloc_context3(pCodec);

	if(!pCodecContext) {
		LOGE("-------> Could not allocate codec context for CODEC_ID_MPEG4");
		exit(1);
	}

	pCodecContext->width = width;
	pCodecContext->height = height;
	pCodecContext->pix_fmt = PIX_FMT_YUV420P;



	if(avcodec_open(pCodecContext, pCodec) < 0) {
		LOGE("--------> could not open codec. avcodec_open failed.");
		exit(1);
	}

	av_init_packet(&packet);
	pFrame = avcodec_alloc_frame();

	buffer = malloc(dataLength);
	memset(buffer, 0, dataLength);
	memcpy(buffer, temp, dataLength);

	packet.data = buffer;
	packet.size = dataLength;

	LOGE(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>decode demo>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
}

void fill_bitmap(AndroidBitmapInfo *info, void *pixels, AVFrame *pFrame) {
	uint8_t *frameLine;
	int yy;
	for(yy = 0; yy < info->height; yy++) {
		uint8_t* line = (uint8_t*)pixels;
		frameLine = (uint8_t*)pFrame->data[0] + (yy * pFrame->linesize[0]);

		int xx;
		for(xx = 0; xx < info->width; xx++) {
			int out_offset = xx * 4;
			int in_offset = xx * 3;

			line[out_offset] = frameLine[in_offset];
			line[out_offset + 1] = frameLine[in_offset + 1];
			line[out_offset + 2] = frameLine[in_offset + 2];
			line[out_offset + 3] = 0;
		}
		pixels = (char*)pixels + info->stride;
	}
}

void
Java_com_example_jnidemo_VideoDemoActivity_openFile(JNIEnv *env, jobject this) {
	int ret;
	int err;
	int i;
	AVCodec *pCodec;
	uint8_t *buffer;
	int numBytes;

	av_register_all();
	LOGE("Registered formats");
	err = av_open_input_file(&mFormatContext, "/mnt/sdcard/video.mp4", NULL, 0, NULL);
	LOGE("--------> Called open file");

	if(err != 0) {
		LOGE("--------> Couldn't open file");
		exit(1);
	}

	if(av_find_stream_info(mFormatContext) < 0) {
		LOGE("--------> unable to get stream info");
		exit(1);
	}

	videoStream = -1;
	for(i=0; i<mFormatContext->nb_streams; i++) {
		if(mFormatContext->streams[i]->codec->codec_type == CODEC_TYPE_VIDEO) {
			videoStream = i;
			break;
		}
	}

	if(videoStream == -1) {
		LOGE("--------> Unable to find video stream.");
		exit(1);
	}

	LOGE("--------> video stream is [%d]", videoStream);

	mCodecContext = mFormatContext->streams[videoStream]->codec;

	LOGE("--------> mCodecContext->pix_fmt: %d", mCodecContext->pix_fmt);

	pCodec = avcodec_find_decoder(mCodecContext->codec_id);

	LOGE("--------> codec_id: %d", mFormatContext->streams[videoStream]->codec->codec_id);

	if(pCodec == NULL) {
		LOGE("--------> Unsupported codec.");
		exit(1);
	}

	if(avcodec_open(mCodecContext, pCodec) < 0) {
		LOGE("--------> Unable to open codec");
		exit(1);
	}

	mFrame = avcodec_alloc_frame();
	mFrameRGB = avcodec_alloc_frame();

	LOGE("--------> video size is [%d x %d]", mCodecContext->width, mCodecContext->height);

	numBytes = avpicture_get_size(PIX_FMT_RGB24, mCodecContext->width, mCodecContext->height);
	buffer = (uint8_t*)av_malloc(numBytes*sizeof(uint8_t));

	avpicture_fill((AVPicture*)mFrameRGB, buffer, PIX_FMT_RGB24, mCodecContext->width, mCodecContext->height);
}

void
Java_com_example_jnidemo_VideoDemoActivity_drawFrame(JNIEnv *env, jobject this, jstring bitmap) {

	AndroidBitmapInfo 	info;
	void*				pixels;
	int 				ret;

	int err;
	int i;
	int frameFinshied = 0;
	AVPacket packet;
	static struct SwsContext *img_convert_context;
	int64_t seek_target;

	if((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
		LOGE("--------> AndroidBitmap_Info() failed! error=%d", ret);
		exit(1);
	}

	LOGE("Checked on the bitmap.");

	if((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
		LOGE("--------> AndroidBitmap_lockPixels failed! error=%d", ret);
		exit(1);
	}

	LOGE("Grabbed the pixels");

	i = 0;
	while((i == 0) && (av_read_frame(mFormatContext, &packet) >= 0)) {
		if(packet.stream_index == videoStream) {
			avcodec_decode_video2(mCodecContext, mFrame, &frameFinshied, &packet);

			if(frameFinshied) {
				int target_width = 320;
				int target_height = 240;
				img_convert_context = sws_getContext(mCodecContext->width, mCodecContext->height, mCodecContext->pix_fmt, target_width, target_height, PIX_FMT_RGB24, SWS_BICUBIC, NULL, NULL, NULL);
				if(img_convert_context == NULL) {
					LOGE("-------->Could not initialize conversion context");
					exit(1);
				}

				sws_scale(img_convert_context, (const uint8_t* const*)mFrame->data, mFrame->linesize, 0, mCodecContext->height, mFrameRGB->data, mFrameRGB->linesize);

				fill_bitmap(&info, pixels, mFrameRGB);

				i=1;
			}
		}
		av_free_packet(&packet);
	}
	AndroidBitmap_unlockPixels(env, bitmap);
}



void
Java_com_example_jnidemo_VideoDemoActivity_initDecode(JNIEnv *env, jobject this, int width, int height, jobject bitmap) {

	avcodec_init();
	av_register_all();
	av_log_set_level(AV_LOG_DEBUG);

	clazz = (*env)->FindClass(env, "com/example/jnidemo/VideoDemoActivity");
	mid = (*env)->GetMethodID(env, clazz, "updateImg", "()V");

	int ret;

	AVCodec *pCodec;
	pCodec = avcodec_find_decoder(CODEC_ID_MPEG4);

	if(pCodec == NULL) {
		LOGE("Could not find decoder CODEC_ID_MPEG4");
		exit(1);
	}

	mCodecContext = avcodec_alloc_context3(pCodec);

	if(mCodecContext == NULL) {
		LOGE("Could not allocate AVCodecContext.");
		exit(1);
	}

	mCodecContext->width = width;
	mCodecContext->height = height;
	/*--------start--------*/
	mCodecContext->time_base.den = 1;
	mCodecContext->time_base.num = 25;
	mCodecContext->gop_size = 25;
	mCodecContext->max_b_frames = 1;
	mCodecContext->bit_rate = 400000;
	/*--------end  --------*/
	mCodecContext->pix_fmt = PIX_FMT_YUV420P;

	if(pCodec->capabilities & CODEC_CAP_TRUNCATED) {
		mCodecContext->flags |= CODEC_FLAG_TRUNCATED;
	}

	mParserContext = av_parser_init(CODEC_ID_MPEG4);

	if(mParserContext == NULL) {
		LOGE("Could not init AVCodecParserContext.");
		exit(1);
	}

	if(avcodec_open2(mCodecContext, pCodec, NULL) < 0) {
		LOGE("Could not open codec. avcodec_open2 failed.");
		exit(1);
	}

	mFrame = avcodec_alloc_frame();
	mFrameRGB = avcodec_alloc_frame();

	int numBytes;
	uint8_t* buffer;

	numBytes = avpicture_get_size(PIX_FMT_RGB24, 352, 240);
	buffer = av_malloc(numBytes);
	avpicture_fill((AVPicture*)mFrameRGB, buffer, PIX_FMT_RGB24, 352, 240);

	numBytes = avpicture_get_size(PIX_FMT_YUV420P, width, height);
	buffer = av_malloc(numBytes);
	avpicture_fill((AVPicture*)mFrame, buffer, PIX_FMT_YUV420P, width, height);

	img_convert_ctx = sws_getContext(width, height, PIX_FMT_YUV420P, 352, 240, PIX_FMT_RGB24, SWS_SINC, NULL, NULL, NULL);
	if(img_convert_ctx == NULL) {
		LOGE("Could not init sws_context");
		exit(0);
	}


	LOGE("------------------> init completed ---------------------->");

//	LOGE("------------------> thread relatived start  ------------->");

//	frameList = list_init();
//	rgbFrameList = list_init();
//
//	holdon = 1;
//
//	currentBitmap = bitmap;
//
//	currentBitmapWidth = width;
//	currentBitmapHeight = height;
//
//	currentJVMInstance = this;
//
//	pthread_attr_init(&attr_sws);
//	pthread_attr_init(&attr_update);
//
//	memset(&thread, 0, sizeof(thread));
//
//	int createThreadRs;
//	if((createThreadRs = pthread_create(&thread[0], &attr_sws, thread_sws, NULL)) != 0) {
//		LOGE("--------> create sws thread failed.");
//		exit(1);
//	}

//	if((createThreadRs = pthread_create(&thread[1], &attr_update, thread_update, NULL)) != 0) {
//		LOGE("--------> create update thread failed.");
//		exit(1);
//	}


//	LOGE("------------------> thread relatived end    ------------->");

}

void
Java_com_pullmi_shanghai_service_Monitor_initDecode(JNIEnv *env, jobject this, int width, int height, jstring bitmap) {

	avcodec_init();
	av_register_all();
	av_log_set_level(AV_LOG_DEBUG);

	int ret;

	AVCodec *pCodec;
	pCodec = avcodec_find_decoder(CODEC_ID_MPEG4);

	if(pCodec == NULL) {
		LOGE("Could not find decoder CODEC_ID_MPEG4");
		exit(1);
	}

	mCodecContext = avcodec_alloc_context3(pCodec);

	if(mCodecContext == NULL) {
		LOGE("Could not allocate AVCodecContext.");
		exit(1);
	}

	mCodecContext->width = width;
	mCodecContext->height = height;
	/*--------start--------*/
	mCodecContext->time_base.den = 1;
	mCodecContext->time_base.num = 25;
	mCodecContext->gop_size = 10;
	mCodecContext->max_b_frames = 1;
	mCodecContext->bit_rate = 400000;
	/*--------end  --------*/
	mCodecContext->pix_fmt = PIX_FMT_YUV420P;

	if(pCodec->capabilities & CODEC_CAP_TRUNCATED) {
		mCodecContext->flags |= CODEC_FLAG_TRUNCATED;
	}

	mParserContext = av_parser_init(CODEC_ID_MPEG4);

	if(mParserContext == NULL) {
		LOGE("Could not init AVCodecParserContext.");
		exit(1);
	}

	if(avcodec_open2(mCodecContext, pCodec, NULL) < 0) {
		LOGE("Could not open codec. avcodec_open2 failed.");
		exit(1);
	}

	mFrame = avcodec_alloc_frame();
	mFrameRGB = avcodec_alloc_frame();

	int numBytes;
	uint8_t* buffer;

	numBytes = avpicture_get_size(PIX_FMT_RGB24, width, height);
	buffer = av_malloc(numBytes);
	avpicture_fill((AVPicture*)mFrameRGB, buffer, PIX_FMT_RGB24, width, height);

	numBytes = avpicture_get_size(PIX_FMT_YUV420P, width, height);
	buffer = av_malloc(numBytes);
	avpicture_fill((AVPicture*)mFrame, buffer, PIX_FMT_YUV420P, width, height);

	img_convert_ctx = sws_getContext(width, height, PIX_FMT_YUV420P, width, height, PIX_FMT_RGB24, SWS_BICUBIC, NULL, NULL, NULL);
	if(img_convert_ctx == NULL) {
		LOGE("Could not init sws_context");
		exit(0);
	}

	LOGE("------------------> init completed ---------------------->");
}


void
Java_com_example_jnidemo_VideoDemoActivity_NewDecodeFrame(JNIEnv *env, jobject this, jstring bitmap, jbyteArray data, int width, int height) {

	jbyte *temp = (uint8_t*)(*env)->GetByteArrayElements(env, data, NULL);
	jsize dataLength = (*env)->GetArrayLength(env, data);

	jclass clazz = (*env)->FindClass(env, "com/example/jnidemo/VideoDemoActivity");
	jmethodID mid = (*env)->GetMethodID(env, clazz, "updateImg", "()V");


	LOGE("--------> data length: %d", dataLength);

	uint8_t *inbuf[dataLength + FF_INPUT_BUFFER_PADDING_SIZE];

	int ret;

	ret = AndroidBitmap_getInfo(env, bitmap, &info);
	if(ret < 0) {
		LOGE("Could not get bitmap info.");
		exit(1);
	}


	int frame_count;
	int out_size;
	frame_count = 0;

	int pos;
	int64_t pts = AV_NOPTS_VALUE;
	int64_t dts = AV_NOPTS_VALUE;

	while(dataLength) {
		LOGE("--------> start -------->");
		uint8_t *pout;
		int pout_len;
		int len = av_parser_parse2(mParserContext, mCodecContext, &pout, &pout_len, temp, dataLength, pts, dts, AV_NOPTS_VALUE);
		LOGE("--------> end   -------->");

		temp += len;
		dataLength -= len;
		LOGE("pout_len: %d", pout_len);

		if(pout_len > 0) {
			int got_picture = 0;
			AVPacket avp;
			av_init_packet(&avp);
			avp.data = pout;
			avp.size = pout_len;

			while(avp.size > 0) {

				int rs = avcodec_decode_video2(mCodecContext, mFrame, &got_picture, &avp);

				LOGE("rs: %d, got_picture: %d", rs, got_picture);

				if(rs < 0 ) {
					return;
				}

				if(got_picture > 0) {

					img_convert_ctx = sws_getContext(width, height, PIX_FMT_YUV420P, width, height, PIX_FMT_RGB24, SWS_BICUBIC, NULL, NULL, NULL);

					if(img_convert_ctx == NULL) {
						LOGE("Could not init sws_context");
						exit(0);
					}

					LOGE("mFrame->data: %x", mFrame->data);
					LOGE("mframe line1[%d] line2[%d] line3[%d] line4[%d]", mFrame->linesize[0], mFrame->linesize[1], mFrame->linesize[2], mFrame->linesize[3]);
					LOGE("mFrameRGB->data: %x", mFrameRGB->data);
					LOGE("mFrameRGB linsize1[%d] linesize2[%d] linesize3[%d] linesize4[%d]", mFrameRGB->linesize[0], mFrameRGB->linesize[1], mFrameRGB->linesize[2], mFrameRGB->linesize[3]);

					ret = AndroidBitmap_lockPixels(env, bitmap, &pixels);

					if(ret < 0) {
						LOGE("AndroidBitmap_lockPixels failed.");
						exit(1);
					}

					LOGE("--------> er");
					int sacale_rs = sws_scale(img_convert_ctx, (const uint8_t* const*)mFrame->data, mFrame->linesize, 0, height, mFrameRGB->data, mFrameRGB->linesize);
					LOGE("--------> scale_rs: %d", sacale_rs);
					LOGE("--------> re");

					fill_bitmap(&info, pixels, mFrameRGB);

					AndroidBitmap_unlockPixels(env, bitmap);

					LOGE("--------> sd");
					(*env)->CallVoidMethod(env, this, mid);
					LOGE("--------> ds");
				}

				avp.data += rs;
				avp.size -= rs;
			}
			av_free_packet(&avp);

		}

	}

}

//void
//Java_com_example_jnidemo_VideoDemoActivity_NewVideoDecode(JNIEnv *env, jobject this, jstring bitmap, jbyteArray data, int width, int height) {
//
//	jbyte *temp = (uint8_t*)(*env)->GetByteArrayElements(env, data, NULL);
//	jsize dataLength = (*env)->GetArrayLength(env, data);
//
//	uint8_t *inbuf[dataLength + FF_INPUT_BUFFER_PADDING_SIZE];
//
//	int ret;
//
//	ret = AndroidBitmap_getInfo(env, bitmap, &info);
//	if(ret < 0) {
//		LOGE("Could not get bitmap info.");
//		exit(1);
//	}
//
//
//	int frame_count;
//	int out_size;
//	frame_count = 0;
//
//	int pos;
//	int64_t pts = AV_NOPTS_VALUE;
//	int64_t dts = AV_NOPTS_VALUE;
//
//	uint8_t *pout;
//	int pout_len;
//
//	while(dataLength) {
//		int len = av_parser_parse2(mParserContext, mCodecContext, &pout, &pout_len, temp, dataLength, pts, dts, AV_NOPTS_VALUE);
//
//		temp += len;
//		dataLength -= len;
//
//		if(pout_len > 0) {
//			int got_picture = 0;
//			AVPacket avp;
//			av_init_packet(&avp);
//			avp.data = pout;
//			avp.size = pout_len;
//
//			while(avp.size > 0) {
//
//				int framesize = avpicture_get_size(PIX_FMT_YUV420P, width, height);
//				AVFrame *tempFrame = avcodec_alloc_frame();
//
//				uint8_t *buffer = av_malloc(framesize);
//				avpicture_fill((AVPicture*)tempFrame, buffer, PIX_FMT_YUV420P, width, height);
//
////				int rs = avcodec_decode_video2(mCodecContext, mFrame, &got_picture, &avp);
//				int rs = avcodec_decode_video2(mCodecContext, tempFrame, &got_picture, &avp);
//
//
//				if(rs < 0 ) {
//					break;
//				}
//
//				if(got_picture > 0) {
//					listNode *currentNode = malloc(sizeof(struct listNode));
//					currentNode->frame = tempFrame;
//					pthread_mutex_lock(&mut_decode);
//					list_push(frameList, currentNode);
//					pthread_mutex_unlock(&mut_decode);
//				}
//
////				if(got_picture > 0) {
////
////					ret = AndroidBitmap_lockPixels(env, bitmap, &pixels);
////
////					if(ret < 0) {
////						LOGE("AndroidBitmap_lockPixels failed.");
////						exit(1);
////					}
////
////					int sacale_rs = sws_scale(img_convert_ctx, (const uint8_t* const*)mFrame->data, mFrame->linesize, 0, height, mFrameRGB->data, mFrameRGB->linesize);
////
////					fill_bitmap(&info, pixels, mFrameRGB);
////
////					AndroidBitmap_unlockPixels(env, bitmap);
////
////				}
//
//				avp.data += rs;
//				avp.size -= rs;
//			}
//
////			(*env)->CallVoidMethod(env, this, mid);
//
//			av_free_packet(&avp);
//
//		}
//	}
//
//}



void
Java_com_pullmi_shanghai_service_Monitor_NewDecodeFrame(JNIEnv *env, jobject this, jstring bitmap, jbyteArray data, int width, int height) {

	jbyte *temp = (uint8_t*)(*env)->GetByteArrayElements(env, data, NULL);
	jsize dataLength = (*env)->GetArrayLength(env, data);

	jclass clazz = (*env)->FindClass(env, "com/pullmi/shanghai/VideoTalkActivity");
	jmethodID mid = (*env)->GetStaticMethodID(env, clazz, "updateImg", "()V");


	LOGE("--------> data length: %d", dataLength);

	uint8_t *inbuf[dataLength + FF_INPUT_BUFFER_PADDING_SIZE];

	int ret;

	ret = AndroidBitmap_getInfo(env, bitmap, &info);
	if(ret < 0) {
		LOGE("Could not get bitmap info.");
		exit(1);
	}


	int frame_count;
	int out_size;
	frame_count = 0;

	int pos;
	int64_t pts = AV_NOPTS_VALUE;
	int64_t dts = AV_NOPTS_VALUE;

	while(dataLength) {
		LOGE("--------> start -------->");
		uint8_t *pout;
		int pout_len;
		int len = av_parser_parse2(mParserContext, mCodecContext, &pout, &pout_len, temp, dataLength, pts, dts, AV_NOPTS_VALUE);
		LOGE("--------> end   -------->");

		temp += len;
		dataLength -= len;
		LOGE("pout_len: %d", pout_len);

		if(pout_len > 0) {
			int got_picture = 0;
			AVPacket avp;
			av_init_packet(&avp);
			avp.data = pout;
			avp.size = pout_len;

			while(avp.size > 0) {

				int rs = avcodec_decode_video2(mCodecContext, mFrame, &got_picture, &avp);

				LOGE("rs: %d, got_picture: %d", rs, got_picture);

				if(rs < 0 ) {
					return;
				}

				if(got_picture > 0) {

					img_convert_ctx = sws_getContext(width, height, PIX_FMT_YUV420P, width, height, PIX_FMT_RGB24, SWS_BICUBIC, NULL, NULL, NULL);

					if(img_convert_ctx == NULL) {
						LOGE("Could not init sws_context");
						exit(0);
					}

					LOGE("mFrame->data: %x", mFrame->data);
					LOGE("mframe line1[%d] line2[%d] line3[%d] line4[%d]", mFrame->linesize[0], mFrame->linesize[1], mFrame->linesize[2], mFrame->linesize[3]);
					LOGE("mFrameRGB->data: %x", mFrameRGB->data);
					LOGE("mFrameRGB linsize1[%d] linesize2[%d] linesize3[%d] linesize4[%d]", mFrameRGB->linesize[0], mFrameRGB->linesize[1], mFrameRGB->linesize[2], mFrameRGB->linesize[3]);

					ret = AndroidBitmap_lockPixels(env, bitmap, &pixels);

					if(ret < 0) {
						LOGE("AndroidBitmap_lockPixels failed.");
						exit(1);
					}

					LOGE("--------> er");
					int sacale_rs = sws_scale(img_convert_ctx, (const uint8_t* const*)mFrame->data, mFrame->linesize, 0, height, mFrameRGB->data, mFrameRGB->linesize);
					LOGE("--------> scale_rs: %d", sacale_rs);
					LOGE("--------> re");

					fill_bitmap(&info, pixels, mFrameRGB);

					AndroidBitmap_unlockPixels(env, bitmap);

					LOGE("--------> sd");
					(*env)->CallStaticObjectMethod(env, clazz, mid);
					LOGE("--------> ds");
				}

				avp.data += rs;
				avp.size -= rs;
			}
			av_free_packet(&avp);

		}

	}

}


void
Java_com_example_jnidemo_VideoDemoActivity_decodeFrame(JNIEnv *env, jobject this, jstring bitmap, jbyteArray data, int width, int height) {

	jbyte *temp = (*env)->GetByteArrayElements(env, data, NULL);
	jsize dataLength = (*env)->GetArrayLength(env, data);

	LOGE("--------> data length: %d", dataLength);

	jclass clazz = (*env)->FindClass(env, "com/example/jnidemo/VideoDemoActivity");
	jmethodID mid = (*env)->GetMethodID(env, clazz, "updateImg", "()V");

	avcodec_init();
	av_register_all();

	AVFormatContext *pFormatContext;
	AVCodecContext *pCodecContext;
	AVCodec *pCodec;
	AVFrame *pFrame;
	AVFrame *PFrameRGB;

	AndroidBitmapInfo		info;
	void*					pixels;
	int						ret;
	int						i;
	int						frameFinished;
	static struct SwsContext *img_convert_context;
	int						numBytes;
//	uint8_t*				buffer;
	uint8_t					buffer[dataLength + FF_INPUT_BUFFER_PADDING_SIZE];
	int						out_size;
	AVPacket				packet;


	pCodec = avcodec_find_decoder(CODEC_ID_H264);
	LOGE("--------> vale for CODEC_ID_MPEG4: %d", CODEC_ID_H264);

	if(pCodec == NULL) {
		LOGE("--------> Couldn't find decoder for codec id CODEC_ID_MPEG4.");
		exit(1);
	}

	pCodecContext = avcodec_alloc_context3(pCodec);
	pCodecContext->width = 1280;
	pCodecContext->height = 720;
	pCodecContext->codec_type = CODEC_TYPE_VIDEO;
	pCodecContext->codec_id = CODEC_ID_H264;
//	pCodecContext->frame_number = 1;
//	pCodecContext->bit_rate = 1691000;
//	pCodecContext->time_base.den = 10;
//	pCodecContext->time_base.num = 1;
//	pCodecContext->pix_fmt = PIX_FMT_YUV420P;

	LOGE("--------> width: %d, height: %d, frame_size: %d", pCodecContext->width, pCodecContext->height, pCodecContext->frame_size);

	if(avcodec_open(pCodecContext, pCodec) < 0) {

		LOGE("--------> Couldn't open codec.");
		exit(1);

	}

	pFrame = avcodec_alloc_frame();
	pFrameRGB = avcodec_alloc_frame();

	av_init_packet(&packet);
	packet.data = malloc(dataLength + FF_INPUT_BUFFER_PADDING_SIZE);
	memset(packet.data+dataLength, 0, FF_INPUT_BUFFER_PADDING_SIZE);
	memcpy(buffer, temp, dataLength);
	packet.size = dataLength + FF_INPUT_BUFFER_PADDING_SIZE;

	numBytes = avpicture_get_size(PIX_FMT_RGB24, pCodecContext->width, pCodecContext->height);
	avpicture_fill((AVPicture*)pFrameRGB, buffer, PIX_FMT_RGB24, pCodecContext->width, pCodecContext->height);


	if((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
		LOGE("--------> AndroidBitmap_getInfo() failed. error[%d]", ret);
		exit(1);
	}

	if((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
		LOGE("--------> AndroidBitmap_lockPixels failed. error[%d]", ret);
		exit(1);
	}

	LOGE("--------> Grabbed the pixels.");


	while(packet.size > 0) {
		out_size = avcodec_decode_video2(pCodecContext, pFrame, &frameFinished, &packet);
		if(out_size < 0) {
			LOGE("--------> decode frame failed. out_size: %d, framefinished: %d", out_size, frameFinished);
			return;
		}
		packet.size -= out_size;
		packet.data += out_size;
	}

	if(frameFinished) {
		int target_width = 320;
		int target_height = 240;
		img_convert_context = sws_getContext(pCodecContext->width, pCodecContext->height, pCodecContext->pix_fmt, target_width, target_height, PIX_FMT_RGB24, SWS_BICUBIC, NULL, NULL, NULL);
		if(img_convert_context == NULL) {
			LOGE("--------> Could not initialize conversion context.");
			exit(1);
		}
		sws_scale(img_convert_context, (const uint8_t* const*)pFrame->data, pFrame->linesize, 0, pCodecContext->height, pFrameRGB->data, pFrameRGB->linesize);

		fill_bitmap(&info, pixels, pFrameRGB);
	}

	av_free_packet(&packet);
	free(buffer);

	AndroidBitmap_unlockPixels(env, bitmap);

	(*env)->CallVoidMethod(env, this, mid);

}

void
Java_com_example_jnidemo_VIDEOTALKService_decodeFrame(JNIEnv *env, jobject this, jstring bitmap, jbyteArray data, int width, int height) {

	jbyte *temp = (*env)->GetByteArrayElements(env, data, NULL);
	jsize dataLength = (*env)->GetArrayLength(env, data);

	LOGE("--------> data length: %d", dataLength);

	jclass clazz = (*env)->FindClass(env, "com/example/jnidemo/VideoDemoActivity");
	jmethodID mid = (*env)->GetMethodID(env, clazz, "updateImg", "()V");

	avcodec_init();
	av_register_all();

	AVFormatContext *pFormatContext;
	AVCodecContext *pCodecContext;
	AVCodec *pCodec;
	AVFrame *pFrame;
	AVFrame *PFrameRGB;

	AndroidBitmapInfo		info;
	void*					pixels;
	int						ret;
	int						i;
	int						frameFinished;
	static struct SwsContext *img_convert_context;
	int						numBytes;
//	uint8_t*				buffer;
	uint8_t					buffer[dataLength + FF_INPUT_BUFFER_PADDING_SIZE];
	int						out_size;
	AVPacket				packet;


	pCodec = avcodec_find_decoder(CODEC_ID_H264);
	LOGE("--------> vale for CODEC_ID_MPEG4: %d", CODEC_ID_H264);

	if(pCodec == NULL) {
		LOGE("--------> Couldn't find decoder for codec id CODEC_ID_MPEG4.");
		exit(1);
	}

	pCodecContext = avcodec_alloc_context3(pCodec);
	pCodecContext->width = width;
	pCodecContext->height = height;
	pCodecContext->codec_type = CODEC_TYPE_VIDEO;
	pCodecContext->codec_id = CODEC_ID_H264;
	pCodecContext->frame_number = 1;
	pCodecContext->bit_rate = 1691000;
	pCodecContext->time_base.den = 10;
	pCodecContext->time_base.num = 1;
	pCodecContext->pix_fmt = PIX_FMT_YUV420P;

	LOGE("--------> width: %d, height: %d, frame_size: %d", pCodecContext->width, pCodecContext->height, pCodecContext->frame_size);

	if(avcodec_open(pCodecContext, pCodec) < 0) {

		LOGE("--------> Couldn't open codec.");
		exit(1);

	}

	pFrame = avcodec_alloc_frame();
	pFrameRGB = avcodec_alloc_frame();

	av_init_packet(&packet);
	packet.data = malloc(dataLength + FF_INPUT_BUFFER_PADDING_SIZE);
	memset(packet.data+dataLength, 0, FF_INPUT_BUFFER_PADDING_SIZE);
	memcpy(buffer, temp, dataLength);
	packet.size = dataLength + FF_INPUT_BUFFER_PADDING_SIZE;

	numBytes = avpicture_get_size(PIX_FMT_RGB24, pCodecContext->width, pCodecContext->height);
	avpicture_fill((AVPicture*)pFrameRGB, buffer, PIX_FMT_RGB24, pCodecContext->width, pCodecContext->height);


	if((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
		LOGE("--------> AndroidBitmap_getInfo() failed. error[%d]", ret);
		exit(1);
	}

	if((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
		LOGE("--------> AndroidBitmap_lockPixels failed. error[%d]", ret);
		exit(1);
	}

	LOGE("--------> Grabbed the pixels.");


	while(packet.size > 0) {
		out_size = avcodec_decode_video2(pCodecContext, pFrame, &frameFinished, &packet);
		if(out_size < 0) {
			LOGE("--------> decode frame failed. out_size: %d, framefinished: %d", out_size, frameFinished);
			return;
		}
		packet.size -= out_size;
		packet.data += out_size;
	}

	if(frameFinished) {
		int target_width = 320;
		int target_height = 240;
		img_convert_context = sws_getContext(pCodecContext->width, pCodecContext->height, pCodecContext->pix_fmt, target_width, target_height, PIX_FMT_RGB24, SWS_BICUBIC, NULL, NULL, NULL);
		if(img_convert_context == NULL) {
			LOGE("--------> Could not initialize conversion context.");
			exit(1);
		}
		sws_scale(img_convert_context, (const uint8_t* const*)pFrame->data, pFrame->linesize, 0, pCodecContext->height, pFrameRGB->data, pFrameRGB->linesize);

		fill_bitmap(&info, pixels, pFrameRGB);
	}

	av_free_packet(&packet);
	free(buffer);

	AndroidBitmap_unlockPixels(env, bitmap);

	(*env)->CallVoidMethod(env, this, mid);

}

void
Java_com_example_jnidemo_VideoDemoActivity_drawFrameAt(JNIEnv *env, jobject this, jstring bitmap, jint secs) {

}

void
Java_com_example_jnidemo_VIDEOTALKService_videoDecode(JNIEnv* env, jobject object, jint width, jint height, jobject bitmap) {
	LOGE(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

	AVCodecContext *pCodecContext;
	AVFormatContext *pFormatContext;
	AVCodec *pCodec;
	uint8_t *buffer;
	int numBytes;
	int err;

	av_register_all();
	err = av_open_input_file(&pFormatContext, "/mnt/sdcard/video.mp4", NULL, 0, NULL);
	if(err != 0) {
		LOGE("Couldn't open input file.");
		exit(1);
	}

	LOGE("--------> file opended -------->");

	if(av_find_stream_info(pFormatContext) < 0) {
		LOGE("--------> Unable to get stream info.");
		exit(1);
	}

	LOGE("--------> video steam total num is: %d", pFormatContext->nb_streams);
	LOGE("--------> codec_video_type: %d", CODEC_TYPE_VIDEO);

	int videoStream;
	videoStream = -1;
	int i;
	for(i=0; i<pFormatContext->nb_streams; i++) {
		if(pFormatContext->streams[i]->codec->codec_type == CODEC_TYPE_VIDEO) {
			videoStream = i;
			break;
		}
	}

	if(videoStream == -1) {
		LOGE("--------> Unable to find video stream.");
		exit(1);
	}

	LOGE("--------> codec id value: %d, CODEC_ID_H264: %d", pFormatContext->streams[videoStream]->codec->codec_id, CODEC_ID_H264);

	pCodecContext = pFormatContext->streams[videoStream]->codec;
	pCodec = avcodec_find_decoder(pCodecContext->codec_id);


	LOGE("width: %d, height: %d", pCodecContext->width, pCodecContext->height);

	if(pCodec == NULL) {
		LOGE("--------> unsupported codec.");
		exit(1);
	}

	if(avcodec_open(pCodecContext, pCodec) < 0) {
		LOGE("--------> unable to open codec");
		exit(1);
	}

	AVFrame *pFrame;
	AVFrame *pFrameRGB;
	pFrame = avcodec_alloc_frame();
	pFrameRGB = avcodec_alloc_frame();

	numBytes = avpicture_get_size(PIX_FMT_RGB24, pCodecContext->width, pCodecContext->height);
	buffer = (uint8_t*)av_malloc(numBytes*sizeof(uint8_t));
	avpicture_fill((AVPicture*)pFrameRGB, buffer, PIX_FMT_RGB24, pCodecContext->width, pCodecContext->height);

	AndroidBitmapInfo info;
	void* pixels;
	int ret;

	int err1;
	int frameFinished = 0;
	AVPacket packet;
	static struct SwsContext *img_convert_ctx;
	int64_t seek_target;

	ret = AndroidBitmap_getInfo(env, bitmap, &info);
	if(ret < 0) {
		LOGE("--------> AndroidBitmap_getInfo failed, error: %d", ret);
		exit(1);
	}

	ret = AndroidBitmap_lockPixels(env, bitmap, &pixels);
	if( ret < 0	) {
		LOGE("--------> AndroidBitmap_lockPixels() failed. error: %d", ret);
		exit(1);
	}

	i = 0;
	av_init_packet(&packet);
	while((i == 0) && (av_read_frame(pFormatContext, &packet)) >= 0) {
		if(packet.stream_index == videoStream) {
			avcodec_decode_video2(pCodecContext, pFrame, &frameFinished, &packet);
			if(frameFinished) {
				LOGE("--------> decode succeeded");
				int target_width = width;
				int target_height = height;
				img_convert_ctx = sws_getContext(pCodecContext->width, pCodecContext->height, pCodecContext->pix_fmt, target_width, target_height, PIX_FMT_RGB24, SWS_BICUBIC, NULL, NULL, NULL);
				if(img_convert_ctx == NULL) {
					LOGE("--------> Could not initialize conversion context");
					exit(1);
				}
				sws_scale(img_convert_ctx, (const uint8_t* const*)pFrame->data, pFrame->linesize, 0, pCodecContext->height, pFrameRGB->data, pFrameRGB->linesize);

				fill_bitmap(&info, pixels, pFrameRGB);

				i = 1;
			}
		}
		av_free_packet(&packet);
	}

	AndroidBitmap_unlockPixels(env, bitmap);
	LOGE(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
}

void
Java_com_example_jnidemo_VIDEOTALKService_VideoEncode(JNIEnv* env, jobject object, jint width, jint height, jbyteArray data) {

	jbyte* temp = (*env)->GetByteArrayElements(env, data, NULL);
	jsize dataLength = (*env)->GetArrayLength(env, data);
	LOGE("-------> data length: %d", dataLength);

	avcodec_init();
	avcodec_register_all();

	AVCodec *codec;
	AVCodecContext *codecContext;

	int i, out_size, size, x, y, outbuf_size;
	AVFrame *picture;
	uint8_t *outbuf, *picture_buf;

	/*find xvide decoder*/
	codec = avcodec_find_encoder(CODEC_ID_MPEG2VIDEO);
	if(!codec) {
		LOGE("--------> CODEC_ID_MPEG2VIDEO not found.");
		exit(1);
	}

	codecContext = avcodec_alloc_context();
	picture = avcodec_alloc_frame();

	/*put sample parameters*/
	codecContext->bit_rate = 400000;
	codecContext->width = width;
	codecContext->height = height;
	codecContext->pix_fmt = PIX_FMT_YUV420P;

	/*open codec*/
	if(avcodec_open(codecContext, codec) < 0) {
		LOGE("--------> Could not open codec.");
		exit(1);
	}

	/*allocate image and output buffer*/
	outbuf_size = 100000;
	outbuf = malloc(outbuf_size);
	size = codecContext->width  * codecContext->height;
	picture_buf = malloc((size * 3) / 2); /*size of YUV420*/

	picture->data[0] = picture_buf;
	picture->data[1] = picture->data[0] + size;
	picture->data[2] = picture->data[1] + size/4;
	picture->linesize[0] = codecContext->width;
	picture->linesize[1] = codecContext->width/2;
	picture->linesize[2] = codecContext->width/2;

}

void
Java_com_example_jnidemo_VIDEOTALKService_play(JNIEnv* env, jobject object, jbyteArray data)
{
	LOGE("--------> play start -------->");
	jclass clazz = (*env)->FindClass(env, "com/example/jnidemo/VIDEOTALKService");
	jmethodID mid = (*env)->GetMethodID(env, clazz, "testAudio", "([B)V");

	avcodec_init();
	avcodec_register_all();
	jbyte* temp = (*env)->GetByteArrayElements(env, data, NULL);
	jsize dataLength = (*env)->GetArrayLength(env, data);

	LOGE("--------> audio data length: %d", dataLength);

	AVCodec *codec = avcodec_find_decoder(CODEC_ID_PCM_MULAW);
	if(!codec)
	{
		exit(1);
	}


	AVCodecContext *codecContext = avcodec_alloc_context3(codec);
	if(!codecContext)
	{
		exit(1);
	}


//	codecContext->bit_rate = 705600;
	codecContext->sample_fmt = AV_SAMPLE_FMT_S16;
	if(!check_sample_fmt(codec, codecContext->sample_fmt))
	{
		exit(1);
	}


	codecContext->bit_rate = 64000;
	codecContext->sample_rate = select_sample_rate(codec);
	codecContext->channel_layout = select_channel_layout(codec);
	codecContext->channels = av_get_channel_layout_nb_channels(codecContext->channel_layout);


	AVPacket packet;
	AVFrame *frame;

	if(avcodec_open2(codecContext, codec, NULL) < 0) {
		exit(1);
	}

	//init packet
	av_init_packet(&packet);
	packet.data = malloc(dataLength);
	memcpy(packet.data, temp, dataLength);
	packet.size = dataLength;
	uint8_t *pktdata;
	int pktsize;
	int out_size = AVCODEC_MAX_AUDIO_FRAME_SIZE;
	uint8_t *inbuf = (uint8_t*)malloc(out_size);
	pktdata = packet.data;
	pktsize = packet.size;
	LOGE(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
	while(pktsize > 0)
	{
		int len = avcodec_decode_audio3(codecContext, inbuf, &out_size, &packet);
		if(len <= 0) {
			break;
		}
		LOGE("--------> decode length: %d", len);

		pktsize -= len;
		pktdata += len;
	}

	jbyteArray returnData = (*env)->NewByteArray(env, out_size);
	(*env)->SetByteArrayRegion(env, returnData, 0, out_size, inbuf);

	(*env)->CallVoidMethod(env, object, mid, returnData);
	(*env)->ReleaseByteArrayElements(env, data, JNI_FALSE, JNI_ABORT);
	free(inbuf);
	av_free_packet(&packet);
	LOGE("-------> play end   -------->");
}

void
Java_com_pullmi_audio_encode_MainActivity_encode(JNIEnv *env, jobject object, jbyteArray data)
{
	jclass clazz = (*env)->FindClass(env, "com/pullmi/audio/encode/MainActivity");
	jmethodID mid = (*env)->GetMethodID(env, clazz, "test", "([B)V");
	avcodec_init();
	avcodec_register_all();
	jbyte* temp = (*env)->GetByteArrayElements(env, data, NULL);
	jsize inAudioLength = (*env)->GetArrayLength(env, data);

	LOGE("--------> in audio length: %d", inAudioLength);

	AVCodec *encodeCodec = avcodec_find_encoder(CODEC_ID_PCM_MULAW);
	AVCodec *decodeCodec = avcodec_find_decoder(CODEC_ID_PCM_MULAW);

	AVCodecContext *encodeCodecContext;
	AVCodecContext *decodeCodecContext;

	if(encodeCodec == NULL) {
		LOGE("--------> Can not find encode for CODEC_ID_PCM_MULAW.");
		exit(1);
	}

	if(decodeCodec == NULL) {
		LOGE("--------> Cant not find decode for CODEC_ID_PCM_MULAW.");
		exit(1);
	}

	//allocate encode and decode codec context
	encodeCodecContext = avcodec_alloc_context();
	decodeCodecContext = avcodec_alloc_context();

	//set encode parameter
	encodeCodecContext->bit_rate = 64000;
	encodeCodecContext->sample_rate = 44100;
	encodeCodecContext->channels = 2;

	//set decode parameter
	decodeCodecContext->bit_rate = 64000;
	decodeCodecContext->sample_rate = 44100;
	decodeCodecContext->channels = 2;

	//open encode and decode codec
	if(avcodec_open(encodeCodecContext, encodeCodec) < 0) {
		LOGE("--------> can not open codec for encode.");
		exit(1);
	}

	if(avcodec_open(decodeCodecContext, decodeCodec) < 0) {
		LOGE("--------> can not open codec for decode.");
		exit(1);
	}

	int outbuf_size = FF_MIN_BUFFER_SIZE*10;
	uint8_t *outbuf = malloc(outbuf_size);
	memset(outbuf, 0, outbuf_size);

	int data_length = inAudioLength;
	while(data_length != 0) {
		int out_size = avcodec_encode_audio(encodeCodecContext, outbuf, outbuf_size, temp);
		if(out_size < 0) {
			LOGE("--------> avcodec_encode_audio failed.");
			exit(1);
		}
		data_length -= out_size;
		temp += data_length;
	}

	LOGE("--------> avcodec_encode_auido succeeded.");

	AVPacket pkt;
	av_init_packet(&pkt);
	pkt.data = outbuf;
	pkt.size = outbuf_size;


}

void
Java_com_pullimi_shanghai_service_Monitor_play(JNIEnv* env, jobject object, jbyteArray data)
{
	LOGE("--------> play start -------->");
	jclass clazz = (*env)->FindClass(env, "com/pullmi/shanghai/service/Monitor");
	jmethodID mid = (*env)->GetMethodID(env, clazz, "testAudio", "([B)V");

	avcodec_init();
	avcodec_register_all();
	jbyte* temp = (*env)->GetByteArrayElements(env, data, NULL);
	jsize dataLength = (*env)->GetArrayLength(env, data);

	LOGE("--------> audio data length: %d", dataLength);

	AVCodec *codec = avcodec_find_decoder(CODEC_ID_PCM_MULAW);
	if(!codec)
	{
		exit(1);
	}


	AVCodecContext *codecContext = avcodec_alloc_context3(codec);
	if(!codecContext)
	{
		exit(1);
	}


//	codecContext->bit_rate = 705600;
	codecContext->sample_fmt = AV_SAMPLE_FMT_S16;
	if(!check_sample_fmt(codec, codecContext->sample_fmt))
	{
		exit(1);
	}


	codecContext->bit_rate = 64000;
	codecContext->sample_rate = select_sample_rate(codec);
	codecContext->channel_layout = select_channel_layout(codec);
	codecContext->channels = av_get_channel_layout_nb_channels(codecContext->channel_layout);


	AVPacket packet;
	AVFrame *frame;

	if(avcodec_open2(codecContext, codec, NULL) < 0) {
		exit(1);
	}

	//init packet
	av_init_packet(&packet);
	packet.data = malloc(dataLength);
	memcpy(packet.data, temp, dataLength);
	packet.size = dataLength;
	uint8_t *pktdata;
	int pktsize;
	int out_size = AVCODEC_MAX_AUDIO_FRAME_SIZE;
	uint8_t *inbuf = (uint8_t*)malloc(out_size);
	pktdata = packet.data;
	pktsize = packet.size;
	LOGE(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
	while(pktsize > 0)
	{
		int len = avcodec_decode_audio3(codecContext, inbuf, &out_size, &packet);
		if(len <= 0) {
			break;
		}
		LOGE("--------> decode length: %d", len);

		pktsize -= len;
		pktdata += len;
	}

	jbyteArray returnData = (*env)->NewByteArray(env, out_size);
	(*env)->SetByteArrayRegion(env, returnData, 0, out_size, inbuf);

	(*env)->CallVoidMethod(env, object, mid, returnData);
	(*env)->ReleaseByteArrayElements(env, data, JNI_FALSE, JNI_ABORT);
	free(inbuf);
	av_free_packet(&packet);
	LOGE("-------> play end   -------->");
}

//void
//Java_com_example_jnidemo_MainActivity_audioEncode(JNIEnv* env)
//{
//	LOGE("--------> audio encode start --------->");
//	char *filename = "/mnt/sdcard/test.wav";
//	avcodec_init();
//	av_register_all();
//	AVFormatContext *pInFmtCtx = NULL;
//	AVCodecContext  *pInCodecCtx = NULL;
//	if(av_open_input_file(&pInFmtCtx, filename, NULL, 0, NULL) != 0)
//	{
//		LOGE("--------> av_open_input_file error");
//	} else
//	{
//		LOGE("--------> av_open_input_file success.");
//	}
//	if(av_find_stream_info(pInFmtCtx) < 0)
//	{
//		LOGE("--------> av_find_stream error");
//	} else
//	{
//		LOGE("--------> av_find_stream success.");
//	}
//
//	unsigned int j;
//	int audioStream = -1;
//	for(j=0; j<pInFmtCtx->nb_streams; j++)
//	{
//		if(pInFmtCtx->streams[j]->codec->codec_type == CODEC_TYPE_AUDIO)
//		{
//			audioStream = j;
//			break;
//		}
//	}
//
//	if(audioStream == -1)
//	{
//		LOGE("--------> input file has no audio stream");
//		exit(1);
//	} else
//	{
//		LOGE("--------> input file has audio steam.");
//		LOGE("--------> audio stream num: %d", audioStream);
//	}
//
//	pInCodecCtx = pInFmtCtx->streams[audioStream]->codec;
//	AVCodec *pInCodec = NULL;
//	LOGE("--------> CODEC_ID_PCM_MULAW: %d", CODEC_ID_PCM_MULAW);
//	LOGE("--------> pInCodecCtxt->codec_id: %d", pInCodecCtx->codec_id);
//	LOGE("--------> CODEC SAMPLE_FMT: %d", pInCodecCtx->sample_fmt);
//	LOGE("--------> CODEC_SAMPLE_FMT: %d", AV_SAMPLE_FMT_S16);
//	pInCodec = avcodec_find_decoder(pInCodecCtx->codec_id);
//
//	if(pInCodec == NULL)
//	{
//		LOGE("--------> no codec found");
//		return;
//	}
//
//	if(avcodec_open(pInCodecCtx, pInCodec) < 0)
//	{
//		LOGE("--------> avcodec_open failed.");
//		return;
//	}
//
//	static AVPacket packet;
//	LOGE("--------> bit_rate = %d", pInCodecCtx->bit_rate);
//	LOGE("--------> sample_rate = %d", pInCodecCtx->sample_rate);
//	LOGE("--------> channels = %d", pInCodecCtx->channels);
//	LOGE("--------> code_name = %d", pInCodecCtx->codec->name);
//	LOGE("--------> block_align = %d", pInCodecCtx->block_align);
//
//
//	uint8_t *pktdata;
//	int pktsize;
//	int out_size = AVCODEC_MAX_AUDIO_FRAME_SIZE*60;
//	uint8_t *inbuf = (uint8_t*)malloc(out_size);
//	FILE *pcm;
//	char *output_file = "/mnt/sdcard/rt.pcm";
//	pcm = fopen(output_file, "wb");
//	if(pcm == NULL)
//	{
//		LOGE("--------> can not create new result pcm file.");
//	} else
//	{
//		LOGE("--------> success create new result pcm file.");
//	}
//	long start  = clock();
//	while(av_read_frame(pInFmtCtx, &packet) >= 0)
//	{
//		LOGE("--------> av_read_frame --------->");
//		LOGE("--------> packet.stream_index: %d, audioStream: %d", packet.stream_index, audioStream);
//		if(packet.stream_index == audioStream)
//		{
//			LOGE("--------> packet.stream_index == audioStream");
//			LOGE("--------> pktsize: %d", packet.size);
//			pktdata = packet.data;
//			pktsize = packet.size;
//			while(pktsize > 0)
//			{
//				out_size = AV_CODEC_MAX_AUDIO_FRAME_SIZE*100;
//				//int len = avcodec_decode_audio2(pInCodecCtx, (short*)inbuf, &out_size, pktdata, pktsize);
//				int len = avcodec_decode_audio3(pInCodecCtx, (short*)inbuf, &out_size, &packet);
//				if(len == 0)
//				{
//					LOGE("--------> error while decoding.");
//					break;
//				}
//				LOGE("--------> decode length: %d", len);
//				if(out_size > 0)
//				{
//					fwrite(inbuf, 1, out_size, pcm);
//					fflush(pcm);
//				}
//				pktsize -= len;
//				pktdata += len;
//			}
//		}
//		av_free_packet(&packet);
//	}
//
//	long end = clock();
//
//	LOGE("---------> decoding cost time: %s", (end-start)/CLOCKS_PER_SEC);
//	free(inbuf);
//	fclose(pcm);
//	if(pInCodecCtx != NULL)
//	{
//		avcodec_close(pInCodecCtx);
//	}
//
//	av_close_input_file(pInFmtCtx);
//}

AVFormatContext *createAVFormatContext()
{
	LOGE("-@OPEN - crateAVFormatContext@-");
	AVFormatContext *ctx = avformat_alloc_context();
	LOGE("-@CLOSE - createAVFormatContext@-");
}

AVStream *add_audio_stream(AVFormatContext *oc, enum CodecID codec_id)
{
	LOGE("-@OPEN - add_audio_stream@-");
	AVCodecContext *c;
	AVStream *st = av_new_stream(oc, codec_id);

	if(!st)
	{
		LOGE("-@add_audio_stream - Could not allocate stream@-");
		exit(1);
	}

	st->id = 1;

	c = st->codec;
	c->codec_id = CODEC_ID_PCM_ALAW;
	c->codec_type = AVMEDIA_TYPE_AUDIO;
	c->sample_fmt = AV_SAMPLE_FMT_S16;
	c->bit_rate = 705600;
	c->sample_rate = 44100;
	c->channels = 2;

	//some formats want stream headers to be separate
	if(oc->oformat->flags & AVFMT_GLOBALHEADER)
	{
		c->flags |= CODEC_FLAG_GLOBAL_HEADER;
	}

	LOGE("-@Close - add_audio_stream@-");

	return st;
}



void
Java_com_example_jnidemo_MainActivity_audioEncodeExample(JNIEnv* env, const char *filename)
{
    AVCodec *codec;
    AVCodecContext *c= NULL;
    int frame_size, i, j, out_size, outbuf_size;
    FILE *f;
    short *samples;
    float t, tincr;
    uint8_t *outbuf;

    printf("Audio encoding\n");

    /* find the MP2 encoder */
    codec = avcodec_find_encoder(CODEC_ID_MP2);
    if (!codec) {
        fprintf(stderr, "codec not found\n");
        exit(1);
    }

    c= avcodec_alloc_context();

    /* put sample parameters */
    c->bit_rate = 64000;
    c->sample_rate = 44100;
    c->channels = 2;

    /* open it */
    if (avcodec_open(c, codec) < 0) {
        fprintf(stderr, "could not open codec\n");
        exit(1);
    }

    /* the codec gives us the frame size, in samples */
    frame_size = c->frame_size;
    samples = malloc(frame_size * 2 * c->channels);
    outbuf_size = 10000;
    outbuf = malloc(outbuf_size);

    f = fopen(filename, "wb");
    if (!f) {
        fprintf(stderr, "could not open %s\n", filename);
        exit(1);
    }

    /* encode a single tone sound */
    t = 0;
    tincr = 2 * M_PI * 440.0 / c->sample_rate;
    for(i=0;i<200;i++) {
        for(j=0;j<frame_size;j++) {
            samples[2*j] = (int)(sin(t) * 10000);
            samples[2*j+1] = samples[2*j];
            t += tincr;
        }
        /* encode the samples */
        out_size = avcodec_encode_audio(c, outbuf, outbuf_size, samples);
        fwrite(outbuf, 1, out_size, f);
    }
    fclose(f);
    free(outbuf);
    free(samples);

    avcodec_close(c);
    av_free(c);
}

