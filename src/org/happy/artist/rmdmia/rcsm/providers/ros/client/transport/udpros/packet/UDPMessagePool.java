package org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.udpros.packet;

/** UDPMessagePool.java - Multi Packet UDPROS Message Object Pool 
 *  implemented to improve Object access efficiency over Java Collections. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2013 Happy Artist. All rights reserved.
 */
public class UDPMessagePool 
{
//TODO: Verify UDP Messages start with 0 or 1. It is a current assumption message IDs begin with 0;    
    // UDP_MESSAGE_POOL_SIZE The size of the UDPMessage Pool. This is a fixed pool size of 256, where 256 is the complete range of message IDs.
    private final static int UDP_MESSAGE_POOL_SIZE=256;
    // The availabilityArray specifies whether or not a UDPMessage Object is available to be checked out.
    private final boolean[] availabilityArray;  
    // The udpMessageArray is an array of instantiated and recycled UDPMessage Objects, associated by index to the udpMessageArray for fast lookup of UDPMessage Objects that are avaiable to checkout via the availability array.	
    private final UDPMessage[] udpMessageArray; 
    // checkoutCount used for loop counting - Not associated with any checkoutCount in this class.
    private int checkoutCount=0;
    // recycleCount used for recycle loop counting.     
    private int recycleCount=0;
    
    // TODO: Implement shutdown method.
    /**
     * org.happy.artist.rmdmia.rcsm.client.transport.udpros.packet.UDPMessagePool constructor.
     * 
     *  Pre-generate UDPMessage Objects in an Array by UDPMessage Factories for each UDPMessage, with each processor pool being in the array range of int udpMessagePoolRangeLength, and the sid being the index element number of the passed in udpMessageFactoryArray.
     *
     *  Total Movement Processor Pool size is udpMessageFactoryArray.length * udpMessagePoolRangeLength
     * 
     *  @param udpMessageFactory
     *         The UDP Message Factory interface.
     */
    public UDPMessagePool(UDPMessageFactory udpMessageFactory)
    {
        this.availabilityArray = new boolean[UDP_MESSAGE_POOL_SIZE];
        this.udpMessageArray = new UDPMessage[UDP_MESSAGE_POOL_SIZE];	

        while(checkoutCount<udpMessageArray.length)
        {
            // Core process            
            this.availabilityArray[checkoutCount]=true;
            this.udpMessageArray[checkoutCount]=(UDPMessage) udpMessageFactory.newUDPMessage(checkoutCount);
            this.checkoutCount=checkoutCount + 1;
        }
        this.checkoutCount = 0;
    }

    // Return null if no resources are available. Suggested action retry. Note
    public UDPMessage checkout(int messageID)
    {
//        System.out.println("Checkout UDP Message ID: " + messageID);
        // If value is false, check out Object, and set value to true.
       // if(this.availabilityArray[checkoutCount])
         if(this.availabilityArray[messageID])
        {
//            System.out.println("Checkout Success");
            //this.availabilityArray[checkoutCount]=false;
            this.availabilityArray[messageID]=false;
            return (UDPMessage)udpMessageArray[messageID];
        }
//             System.out.println("Checkout Fail");       
        return null;
    }	    

/*
 *     public TCPMessage checkout()
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
 */    
    
    // Checkin the UDPMessage Object for recycling. 
    // checkin UDPMessage by UDPROS UDP Header Message ID.
    public void checkin(final int messageID)
    {
        this.udpMessageArray[messageID].recycle();
        availabilityArray[messageID]=true;
    }
    
    
    
    public synchronized void recycle()
    {
        while(recycleCount<udpMessageArray.length)
        {
            udpMessageArray[recycleCount].recycle();
            availabilityArray[recycleCount]=true;
            this.recycleCount=recycleCount + 1;
        }
        this.recycleCount=0;
    }
    
    public synchronized void shutdown()
    {
        while(checkoutCount<udpMessageArray.length)
        {
            udpMessageArray[recycleCount].shutdown();
            availabilityArray[recycleCount]=true;
            this.checkoutCount=recycleCount + 1;
        }
        this.checkoutCount=0;        
    }    
}
