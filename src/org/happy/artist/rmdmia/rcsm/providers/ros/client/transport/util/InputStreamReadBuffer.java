package org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.util;

import java.util.Arrays;

/** InputStreamReadBuffer.java - An InputStream byte[] read buffer for ROS Messages. To use call checkout on each read, and upon Message completion call reset, to start new message read. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2013 Happy Artist. All rights reserved.
 */
public class InputStreamReadBuffer 
{
    // READ_BLOCKS The initial quantity of the byte[] Data blocks.
    private int READ_BLOCKS=1;
    // The inputStreamReadBuffer is an array of instantiated and recycled byte[] Objects.	
    private InputStreamReadBuffer.Data[] inputStreamReadBuffer; 
    // checkoutCount used for loop counting - Not associated with any checkoutCount in this class.
    public int checkoutCount=0;
    // recycleCount used for recycle loop counting.     
    private int recycleCount=0;
    // reference use by checkout() method
    private int lastCount=0;    
    private int byteArrayLength;
    
    /**
     * org.happy.artist.rmdmia.rcsm.client.transport.tcpros.data.InputStreamReadBuffer constructor.
     * 
     *  @param byteArrayLength
     *         The block size of the read byte[].
     */
    public InputStreamReadBuffer(int byteArrayLength)
    {
    	this.byteArrayLength=byteArrayLength;
        this.inputStreamReadBuffer = new InputStreamReadBuffer.Data[READ_BLOCKS];	

        while(checkoutCount<inputStreamReadBuffer.length)
        {
            // Core process            
            this.inputStreamReadBuffer[checkoutCount]=new InputStreamReadBuffer.Data();
            this.checkoutCount=checkoutCount + 1;
        }
        this.checkoutCount = 0;
    }
    
    public byte[] checkout()
    {
    	this.lastCount=checkoutCount;
    	if(checkoutCount<inputStreamReadBuffer.length)
    	{
    		// Checkout the byte[]
    		this.checkoutCount=checkoutCount + 1;
            return inputStreamReadBuffer[lastCount].bytes;    		
    	}
    	else
    	{
    		// No more elements. Need more elements. Increment the Array Length, and then Checkout byte[].
//    		System.out.println("PRE ISBufLen:" + inputStreamReadBuffer.length);
    		this.inputStreamReadBuffer=Arrays.copyOf(inputStreamReadBuffer, this.READ_BLOCKS=(inputStreamReadBuffer.length + 1));
    		// add new Packet to new array element index
    		inputStreamReadBuffer[inputStreamReadBuffer.length - 1] = new InputStreamReadBuffer.Data();
    		// Checkout the byte[]
    		this.checkoutCount=checkoutCount + 1;
//    		System.out.println("POST ISBufLen:" + inputStreamReadBuffer.length);
    		return inputStreamReadBuffer[lastCount].bytes;     		
    	}
    }	    


    // reset the read buffer. Data was written. 
    public void reset()
    {
    	this.checkoutCount = 0;
    }
    
    public synchronized void recycle()
    {
        while(recycleCount<inputStreamReadBuffer.length)
        {
            inputStreamReadBuffer[recycleCount].recycle();
            this.recycleCount=recycleCount + 1;
        }
        this.recycleCount=0;
    }
    
    public synchronized void shutdown()
    {
        while(checkoutCount<inputStreamReadBuffer.length)
        {
            inputStreamReadBuffer[recycleCount]=null;
            this.checkoutCount=recycleCount + 1;
        }
        this.checkoutCount=0;        
    }  
    
    public class Data
    {
        public byte[] bytes=new byte[InputStreamReadBuffer.this.byteArrayLength];
        public void recycle()
        {
            this.bytes=null;
        }
    }    
}
