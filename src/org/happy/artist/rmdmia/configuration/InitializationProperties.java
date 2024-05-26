package org.happy.artist.rmdmia.configuration;

/** InitializationProperties.java - The RMDMIA Initialization Properties Object. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2012-2013 Happy Artist. All rights reserved.
 */
public class InitializationProperties 
{
    // GesturePool Size needs to be tested to determine maximum performance efficiency
    public final static int GESTURE_POOL_SIZE = 100;
// TODO: fix this pool size to a dynamic size, and add the ability to increment the pool size, or create an Object of all are unavailable.
    // ROSMessagePool Size must be large enough to handle all ROSMessages currently processing at a time. This is the maximum pool size. It should be the simultaneously operating topic subscriptions/publishers/services * 2, or if message queue is being used the number of elements in each message queue + the sum + an additional element for each sender/listener (receiver). The default number is randomly chosen, and will need to be adjusted based on each system implementation.
    public final static int ROS_MESSAGE_POOL_SIZE=512;
        
    public final static String HOSTNAME="thyatira";
    public final static String ROS_MASTER_URL="http://thyatira:11311";
    public final static String CALLER_ID="/rmdmia";          
}
