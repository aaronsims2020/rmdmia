package org.happy.artist.rmdmia.perception.engine.sensor;

import java.util.Arrays;

// Class based on SensorProcessorExchanger code.
// TODO: May need to define a maximum buffer size, that will force a blocking wait while processing catches up. This would be counter intuitive, if all messages are received at a simultaneous rate. 
/** SensorProcessorExchanger.java - A ROSMessage Provider Receiver for PE Providers.
 * ROSMessage Objects require a checkin upon completion, and SensorProcessorManager 
 * should run in own Thread, therefore, this class will allow passing incoming 
 * ROSMessage into SensorProcessorManager, and message buffering so RCSM can 
 * continue receiving messages while PE operates asynchronously, while all received 
 * messaged are queued. This will improve system performance, however, if to many 
 * messages are received latency will be increased, and messages will need to be 
 * managed so this does not occur. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2013-2014 Happy Artist. All rights reserved.
 */
public class SensorProcessorExchanger 
{
    // READ_BLOCKS The initial quantity of the byte[] Data blocks.
    private int READ_BLOCKS=1;
    // The exchanger is an array of instantiated and recycled byte[] Objects.	
    private SensorProcessorExchanger.Data[] exchanger; 
    // checkoutCount used for loop counting - Not associated with any checkoutCount in this class.
    public int checkoutCount=0;
    // recycleCount used for recycle loop counting.     
    private int recycleCount=0;
    // reference use by checkout() method
    private int lastCount=0;    
    private int byteArrayLength;
    
    /**
     * org.happy.artist.rmdmia.pe.sensor.SensorProcessorExchanger constructor.
     * 
     *  @param byteArrayLength
     *         The block size of the read byte[].
     */
    public SensorProcessorExchanger(int byteArrayLength)
    {
    	this.byteArrayLength=byteArrayLength;
        this.exchanger = new SensorProcessorExchanger.Data[READ_BLOCKS];	

        while(checkoutCount<exchanger.length)
        {
            // Core process            
            this.exchanger[checkoutCount]=new SensorProcessorExchanger.Data();
            this.checkoutCount=checkoutCount + 1;
        }
        this.checkoutCount = 0;
    }
    
    public byte[] checkout()
    {
    	this.lastCount=checkoutCount;
    	if(checkoutCount<exchanger.length)
    	{
    		// Checkout the byte[]
    		this.checkoutCount=checkoutCount + 1;
            return exchanger[lastCount].bytes;    		
    	}
    	else
    	{
    		// No more elements. Need more elements. Increment the Array Length, and then Checkout byte[].
//    		System.out.println("PRE ISBufLen:" + exchanger.length);
    		this.exchanger=Arrays.copyOf(exchanger, this.READ_BLOCKS=(exchanger.length + 1));
    		// add new Packet to new array element index
    		exchanger[exchanger.length - 1] = new SensorProcessorExchanger.Data();
    		// Checkout the byte[]
    		this.checkoutCount=checkoutCount + 1;
//    		System.out.println("POST ISBufLen:" + exchanger.length);
    		return exchanger[lastCount].bytes;     		
    	}
    }	    


    // reset the read buffer. Data was written. 
    public void reset()
    {
    	this.checkoutCount = 0;
    }
    
    public synchronized void recycle()
    {
        while(recycleCount<exchanger.length)
        {
            exchanger[recycleCount].recycle();
            this.recycleCount=recycleCount + 1;
        }
        this.recycleCount=0;
    }
    
    public synchronized void shutdown()
    {
        while(checkoutCount<exchanger.length)
        {
            exchanger[recycleCount]=null;
            this.checkoutCount=recycleCount + 1;
        }
        this.checkoutCount=0;        
    }  
    
    public class Data
    {
        public byte[] bytes=new byte[SensorProcessorExchanger.this.byteArrayLength];
        public void recycle()
        {
            this.bytes=null;
        }
    }        
}
