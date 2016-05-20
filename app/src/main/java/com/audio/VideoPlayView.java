package com.audio;

import java.nio.ByteBuffer;

import com.pullmi.net.VideoPacket;
import com.pullmi.net.VideoQueue;
import com.pullmi.service.TalkService;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.view.View;


public class VideoPlayView extends View {
	
	public static final int VIEW_WIDTH = 640;
	public static final int VIEW_HEIGHT = 480;
	
	public VideoQueue mQueue;
	
	public static boolean mBoolStartPlay = false;
	public static boolean mBoolStopPlay = false;
	
	public static final byte[] jpegHeader = new byte[]{};
	public static final byte[] jpegTail	= new byte[]{};
	
	public static byte[] bitmapPixels;
	public byte[] pixels;
	public static ByteBuffer jpegBuffer;
	public static Bitmap jpeg;
	public static Paint mPaint;
	public static Thread mThreadPlay;
	
	private Context mContext;
	
	public VideoPlayView(Context context) {
		super(context);
		this.setBackgroundColor(Color.TRANSPARENT);
		mPaint = new Paint();
		mPaint.setColor(Color.YELLOW);
		mPaint.setTextAlign(Paint.Align.LEFT);
		mPaint.setTextSize(16);
		destroyBitmap();
		setFocusable(true);
		this.mContext = context;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if(bitmapPixels!=null&&bitmapPixels.length!=0)  
		{
			if(jpeg!=null) 
			{				
				//LogUtils.LOGE("CALLUP", ">>>>>>>>>>>>>>>>>>>>>>>>>>> not null >>>>>>>>>>>>>>>>>>>>>>>>>>>> " + bitmapPixels.length);
				if(!jpeg.isRecycled()) 
				{
					canvas.drawBitmap(jpeg, 0, 0, null);					
				}
			}
		}
		else
		{
			canvas.drawText("正在加载数据，清稍后", 100, 100, mPaint);
		}
	}
	
	public void destroyBitmap() {
		if(jpeg != null && !jpeg.isRecycled()) {
			jpeg.recycle();
		}
	}
	
	public void clear() {
		mPaint = null;
		if(mQueue != null) {
			mQueue.clearQueue();
			mQueue = null;
		}
		jpeg = null;
	}
	
	public void startPlay() {
		this.mQueue = new VideoQueue();
		mThreadPlay = new Thread(new VideoPlayThread());
		mThreadPlay.start();
	}
	
	public void stopPlay() {
		mBoolStartPlay = false;
		while(!mBoolStopPlay) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(mQueue != null) {
			mQueue.clearQueue();
			mQueue = null;
		}
	}

	class VideoPlayThread implements Runnable {

		@Override
		public void run() {
			mBoolStartPlay = true;
			mBoolStopPlay = false;	
			int i;
			VideoPacket packet;
			while(mBoolStartPlay) 
			{
				if(mQueue == null) 
				{
					return;
				}
				i = TalkService.getVideoBuff2Read();
				if(i==-1)
				{
					try 
					{
						Thread.sleep(2);
					} 
					catch (InterruptedException e) 
					{
						e.printStackTrace();
					}
					continue;
				}
				packet = TalkService.mVideoBuffAry[i];
				/*packet = mQueue.getQueueFirstData();
				if(packet == null) 
				{
					try 
					{
						Thread.sleep(10);
					} 
					catch (InterruptedException e) 
					{
						e.printStackTrace();
					}
					continue;
				}*/
				
			
				bitmapPixels = packet.dataBuff;
				//LogUtils.LOGE("video play thread", ">>>>get a video pkt"+Integer.toString(packet.frameLen));
				jpeg = BitmapFactory.decodeByteArray(bitmapPixels, 0, packet.frameLen);
				TalkService.SetVideoBuff2Readed();
				postInvalidate();
			}
			
			mBoolStopPlay = true;
		}
		
	}
}
