package com.audio;

public class G711Coder {

    public native byte linear2alaw(short pcm_val);

    public native short alaw2linear(byte a_val);

    public native byte linear2ulaw(short pcm_val);

    public native short ulaw2linear(byte u_val);
    
    static 
    {
        System.loadLibrary("G711");
    }
    
    public byte[] G711EncodeLinerToUlaw(byte[] nInBuf,int nInSize,byte[] buffExtraInfo)
	{		
		if(nInBuf==null || buffExtraInfo==null)
		{
			return null;
		}
		byte []szOutBuf= new byte[nInSize/2+buffExtraInfo.length];
		System.arraycopy(buffExtraInfo, 0, szOutBuf, 0, buffExtraInfo.length);
		short nTemp=0;
		int ii=0;
		for (ii=0; ii<nInSize/2;ii++)
		{
			nTemp= (short)((short)(nInBuf[ii*2]&0xff) | ((short)(nInBuf[ii*2+1]&0xff))<<8);
			szOutBuf[ii+buffExtraInfo.length]= linear2ulaw(nTemp);			
		}
		return szOutBuf;
	}
    
    public int G711EncodeLinerToAlaw(byte[] outBuff, byte[] nInBuf,int nInSize,byte[] buffExtraInfo)
    {		
    	if(nInBuf==null || buffExtraInfo==null)
    	{
    		return 0;
    	}
    	byte []szOutBuf= outBuff;//new byte[nInSize/2+buffExtraInfo.length];
    	System.arraycopy(buffExtraInfo, 0, szOutBuf, 0, buffExtraInfo.length);
    	short nTemp=0;
    	int ii=0;
    	for (ii=0; ii<nInSize/2;ii++)
    	{
    		nTemp= (short)((nInBuf[ii*2]&0xff) | ((nInBuf[ii*2+1] & 0xff)<<8));
    		szOutBuf[ii+buffExtraInfo.length]= linear2alaw(nTemp);			
    	}
    	return nInSize/2;//szOutBuf;
    }
    
    public byte[] G711EncodeLinerToAlawEt(byte[] nInBuf,int nInSize,byte[] buffExtraInfo)
    {		
    	byte []szOutBuf= new byte[nInSize];
    	int ii=0;
    	for (ii=0; ii<nInSize;ii++)
    	{
    		szOutBuf[ii]= linear2alaw(nInBuf[ii]);			
    	}
    	return szOutBuf;
    }
    
    public byte[] G711DecodeUlawToLinear(byte szInBuf[],int nOffset, int nInlength)
	{
		if((szInBuf==null))
		{
			return null;
		}
		int ii=0;
		byte Temp[] = new byte[2*nInlength+1];
		if(Temp!=null)
		{	
			short shortTemp = 0;
			for (ii=nOffset; ii<nInlength+nOffset; ii++)
			{
				shortTemp = ulaw2linear(szInBuf[ii]);
				Temp[2*(ii-nOffset)] = (byte)(shortTemp&0xFF);
				Temp[2*(ii-nOffset)+1] = (byte)((shortTemp&0xFF00)>>8);
			}
		}
		return Temp;
	}
    
    public int G711DecodeAlawToLinear(byte[] outBuff,byte szInBuf[],int nOffset, int nInlength)
    {
    	if((szInBuf==null))
    	{
    		return 0;
    	}
    	int ii=0;
    	byte Temp[] = outBuff;//new byte[2*nInlength];
    	if(Temp!=null)
    	{	
    		int shortTemp = 0;
    		for (ii=0; ii<nInlength; ii++)
    		{
    			shortTemp = 4*alaw2linear(szInBuf[ii]);
    			Temp[2*ii] = (byte)(shortTemp&0xff);
    			Temp[2*ii+1] = (byte)((shortTemp&0xff00)>>8);
    		}
    	}
    	return 128;
    }
}
