package org.happy.artist.rmdmia.rcsm.providers.ros;

/** ROSMessagePool.java - High Performance Object Pool used by the 
 * MessageHandlerInterface implementations for super fast access to reusable ROSMessage 
 * Objects.
 * Warning: ROSMessagePool Size must be large enough to handle all ROSMessages 
 * currently processing at a time. This is the maximum pool size. It should be 
 * the simultaneously operating topic subscriptions/publishers/services * 2, or 
 * if message queue is being used the number of elements in each message queue 
 * + the sum + an additional element for each sender/listener (receiver). The 
 * default number is randomly chosen, and will need to be adjusted based on 
 * each system implementation.
 * 
 * ROSMessagePool handles writing of received messages to the ROS Bag OutputStream.
 * Therefore if a MessageHandler is directed at an alternative location other than 
 * the ROSMessagePool the MessageHandler will need to implement calls to the ROS Bag
 * OutputStream. This would require a custom MessageHandler implementation.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2012-2014 Happy Artist. All rights reserved.
 */
public class ROSMessagePool 
{
    // The availabilityArray specifies whether or not a rosMessage Object is available to be checked out.
    private final boolean[] availabilityArray;  
    // The rosMessageArray is an array of instantiated and recycled ROSMessage Objects, associated by index to the rosMessageArray for fast lookup of ROSMessage Objects that are avaiable to checkout via the availability array.	
    private final ROSMessage[] rosMessageArray; 
    private int checkoutCount = 0;    
    // ROS_MESSAGE_POOL_SIZE The size of the ROSMessage Pool. This is a fixed pool size.
    // ROS_MESSAGE_POOL_SIZE The size of the ROSMessage Pool. This is a fixed pool size.
    private final int ROS_MESSAGE_POOL_SIZE;
    private static ROSMessagePool rosMessagePool;
    
    // TODO: Implement shutdown method.
    /**
     * org.happy.artist.rmdmia.rcsm.ROSMessagePool constructor.
     * 
     *  Pre-generate ROSMessage Objects in an Array (Theoretical maximum number 
     * of ROSMessages should be: It should be the simultaneously operating topic 
     * subscriptions/publishers/services * 2, or if message queue is being used 
     * the number of elements in each message queue + the sum + an additional 
     * element for each sender/listener (receiver). The default number is randomly 
     * chosen, and will need to be adjusted based on each system implementation. 
     * Reality is..., based on System memory usage and power consumption (Need 
     * to revisit this later on in performance testing)). 
     * Create a second Array the same length that each Object is checked in on 
     * finish. The second counter counts checkins (which will be the amount of 
     * objects in the first Array). Continue switching Checkout and checkin Arrays.  
     *  Use two counters - one for incrementing element index in array on checkout 
     * and one for counting element check-in index for the second array.
     *
     *  @param rosMessageFactory
     *         The ThreadFactory from the concurrency API was used for convenience 
     * as an Object Factory interface. Will likely be replaced.
     */

    // TODO: revert back to private if getInstance() implementation is completed.
    // TODO: Enhancement add a configuration properties for each topic/service for
    // ROSMessageFactory initialization on InitializationMessageHandler, BaseMessageHandler,
    // and TunerMessageHandlers. This will allow API users to replace the MessageHandlers
    // on individual topics/services with custom/third party handlers.
    /**
     * @param rosMessageFactory
     */
    private ROSMessagePool(ROSMessageFactory rosMessageFactory)
    {
        ROS_MESSAGE_POOL_SIZE=ROSNode.ros_message_pool_size;
        this.availabilityArray = new boolean[ROS_MESSAGE_POOL_SIZE];
        this.rosMessageArray = new ROSMessage[ROS_MESSAGE_POOL_SIZE];		
        while(checkoutCount<rosMessageArray.length)
        {
            this.availabilityArray[checkoutCount]=true;
            this.rosMessageArray[checkoutCount]=(ROSMessage) rosMessageFactory.newMessage();
            this.rosMessageArray[checkoutCount].rosMessageID = checkoutCount;
            this.checkoutCount=checkoutCount + 1;
        }
        this.checkoutCount = 0;
    }
	
    // TODO: Only implementable if a single ROSMessagePool is used for all ROSMessages. Implement later...
	// Return the singleton instance of ROSMessagePool.
	public final static ROSMessagePool getInstance()
	{
		if(ROSMessagePool.rosMessagePool!=null)
		{
			return ROSMessagePool.rosMessagePool;
		}
                else
                {
                    return ROSMessagePool.rosMessagePool = new ROSMessagePool(new DefaultROSMessageFactory());
                }
        }    
        
    // Return null if no resources are available. Suggested action retry.
   // Return null if no resources are available. Suggested action retry.
    public ROSMessage checkout()
    {
        if(checkoutCount==ROS_MESSAGE_POOL_SIZE)
        {
            this.checkoutCount=0;           
        }
        while(checkoutCount<rosMessageArray.length)
        {

            if(this.availabilityArray[checkoutCount])
            {
                // current Count is costing 10 to 13 ms
    //						this.checkedOutCount = this.checkedOutCount + 1;
                this.availabilityArray[checkoutCount]=false;
                return (ROSMessage)rosMessageArray[(this.checkoutCount = checkoutCount + 1)-1];
            }
            this.checkoutCount = checkoutCount + 1;
        }
        return null;
    }
     
        /*    public ROSMessage checkout()
    {
        if(checkoutCount==ROS_MESSAGE_POOL_SIZE)
        {
                this.checkoutCount=0;
        }
        else
        {
                while(checkoutCount<rosMessageArray.length)
                {

                    if(this.availabilityArray[checkoutCount])
                    {
                        // current Count is costing 10 to 13 ms
//						this.checkedOutCount = this.checkedOutCount + 1;
                        this.availabilityArray[checkoutCount]=false;
                        return (ROSMessage)rosMessageArray[(this.checkoutCount = checkoutCount + 1)-1];
                    }
                    this.checkoutCount = checkoutCount + 1;
                }
        }
        return null;

    }	
*/
    // Checkin the ROSMessage Object for recycling. No synchronized blocks are included, because the 
    // checkout mechanism will skip to the next array element if the current element is not true.
    public void checkin(final int rosMessageID)
    {
        ROSMessagePool.this.rosMessageArray[rosMessageID].recycle();
        availabilityArray[rosMessageID]=true;
    }

    /** The InitializerMessage can use this method to ensure all messages contain the established header information for reference purposes. */
    public void setROSMessageHeaderInPooledMessages(ROSMessage message)
    {
        // Set the header values of each Pooled Object.
        for(int i=checkoutCount;i<ROSMessagePool.this.rosMessageArray.length;i++)
        {
            // Set the header values of each Object.
            ROSMessagePool.this.rosMessageArray[checkoutCount].isTopic=message.isTopic;
            ROSMessagePool.this.rosMessageArray[checkoutCount].callerid=message.callerid;
            ROSMessagePool.this.rosMessageArray[checkoutCount].topic=message.topic;
            ROSMessagePool.this.rosMessageArray[checkoutCount].service=message.service;    
            ROSMessagePool.this.rosMessageArray[checkoutCount].md5sum=message.md5sum;
            ROSMessagePool.this.rosMessageArray[checkoutCount].type=message.type;
            ROSMessagePool.this.rosMessageArray[checkoutCount].message_definition=message.message_definition;
            ROSMessagePool.this.rosMessageArray[checkoutCount].persistent=message.persistent;
            ROSMessagePool.this.rosMessageArray[checkoutCount].tcp_nodelay=message.tcp_nodelay;
            ROSMessagePool.this.rosMessageArray[checkoutCount].latching=message.latching;  
            ROSMessagePool.this.rosMessageArray[checkoutCount].request_type=message.request_type;
            ROSMessagePool.this.rosMessageArray[checkoutCount].response_type=message.response_type;            
        }
    }
}

