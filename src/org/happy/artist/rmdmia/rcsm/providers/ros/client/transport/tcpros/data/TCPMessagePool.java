package org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.tcpros.data;

import java.util.logging.Logger;

/** TCPMessagePool.java - Multi Packet TCPROS Message Object Pool 
 *  implemented to improve Object access efficiency over Java Collections. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2013 Happy Artist. All rights reserved.
 */
public class TCPMessagePool 
{
//TODO: Verify TCP Messages start with 0 or 1. It is a current assumption message IDs begin with 0;    
    // TCP_MESSAGE_POOL_SIZE The size of the TCPMessage Pool. This is a fixed pool size of 256, where 256 is the complete range of message IDs.
    private final static int TCP_MESSAGE_POOL_SIZE=5;
    // The availabilityArray specifies whether or not a TCPMessage Object is available to be checked out.
    private final boolean[] availabilityArray;  
    // The tcpMessageArray is an array of instantiated and recycled TCPMessage Objects, associated by index to the tcpMessageArray for fast lookup of TCPMessage Objects that are avaiable to checkout via the availability array.	
    private final TCPMessage[] tcpMessageArray; 
    // checkoutIndex used for loop counting.
    private int checkoutIndex=0;
    // recycleCount used for recycle loop counting.     
    private int recycleCount=0;
    // The number of currently checked out Objects.
    private int checkoutCount=0;
    
    // TODO: Implement shutdown method.
    /**
     * org.happy.artist.rmdmia.rcsm.client.transport.tcpros.data.TCPMessagePool constructor.
     * 
     *  Pre-generate TCPMessage Objects in an Array by TCPMessage Factories for each TCPMessage, with each processor pool being in the array range of int tcpMessagePoolRangeLength, and the sid being the index element number of the passed in tcpMessageFactoryArray.
     *
     *  Total Movement Processor Pool size is tcpMessageFactoryArray.length * tcpMessagePoolRangeLength
     * 
     *  @param tcpMessageFactory
     *         The ThreadMessageFactory from the concurrency API was used for 
     * convenience as an Object Factory interface. Will likely be replaced.
     */
    public TCPMessagePool(TCPMessageFactory tcpMessageFactory)
    {
        this.availabilityArray = new boolean[TCP_MESSAGE_POOL_SIZE];
        this.tcpMessageArray = new TCPMessage[TCP_MESSAGE_POOL_SIZE];	

        while(checkoutIndex<tcpMessageArray.length)
        {
            // Core process            
            this.availabilityArray[checkoutIndex]=true;
            this.tcpMessageArray[checkoutIndex]=(TCPMessage) tcpMessageFactory.newTCPMessage(checkoutIndex);
            this.checkoutIndex=checkoutIndex + 1;
        }
        this.checkoutIndex = 0;
    }

    // FUTURE TODO: checkout message ID is a hack to copy the UDPMessagePool class for the initial implementation. Performance related issues and Pool size can be worked out at a post 1.0 release.
    // Return null if no resources are available. Suggested action retry. Note
        // The Logger
    private final static Logger logger = Logger.getLogger(TCPMessagePool.class.getName());

    public TCPMessage checkout()
    {
        //
        if(checkoutCount==TCP_MESSAGE_POOL_SIZE)
        {
            logger.info("TCPMessagePool count exceeded, returning null..., maximum pool size is: " +  String.valueOf(TCP_MESSAGE_POOL_SIZE));
    
            return null;
        }
        // increment checkoutIndex to next index
        this.checkoutIndex = checkoutIndex + 1;
        if(checkoutIndex==TCP_MESSAGE_POOL_SIZE)
        {        
            // Set Checkout count back to 0.
            this.checkoutIndex=0;
        }
//        

        // If value is false, check out Object, and set value to true.
        if(this.availabilityArray[checkoutIndex])
        {            
            this.availabilityArray[checkoutIndex]=false;
            // increment the checkoutCount by one.
            this.checkoutCount = checkoutCount + 1;

        }
        // return TCPMessage
        return (TCPMessage)tcpMessageArray[checkoutIndex];      
    }	    


    // Checkin the TCPMessage Object for recycling. 
    // checkin TCPMessage by TCPROS TCP Header Message ID.
    public void checkin(final int messageID)
    {
        this.tcpMessageArray[messageID].recycle();
        availabilityArray[messageID]=true;
        this.checkoutCount=checkoutCount - 1;        
    }
    
    public synchronized void recycle()
    {
        while(recycleCount<tcpMessageArray.length)
        {
            tcpMessageArray[recycleCount].recycle();
            availabilityArray[recycleCount]=true;
            this.recycleCount=recycleCount + 1;
        }
        this.checkoutCount=0;
        this.checkoutIndex=0;
        this.recycleCount=0;
    }
    
    public synchronized void shutdown()
    {
        while(checkoutIndex<tcpMessageArray.length)
        {
            tcpMessageArray[recycleCount].shutdown();
            availabilityArray[recycleCount]=true;
            this.checkoutIndex=recycleCount + 1;
        }
        this.checkoutCount=0;        
        this.checkoutIndex=0;        
    }    
}
