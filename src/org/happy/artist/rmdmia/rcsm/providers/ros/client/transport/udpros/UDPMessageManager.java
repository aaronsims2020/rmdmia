package org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.udpros;

import org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.udpros.packet.UDPMessageFactory;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.udpros.packet.UDPMessagePool;

// UDPMessageManager.java Not operation in prototype (VRC Qualifications), until //timing and expiration of connections i * s supported.
 // UDP Multi Packet Message Manager. Manages multiple incoming UDPROS Messages, and timeout//s 
 // expired messages.  
 //
 // @author Happy Artist
 // 
 // @copyright Copyright© 2013 Happy Artist. All rights reserved.
 //
 
/** UDPMessageManager.java not implemented/not supported.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright© 2013 Happy Artist. All rights reserved.
 */
public class UDPMessageManager 
{
    private int timeout;
    // array of last time packet received for message id - length = message types * 2
    private final long[] messageUpdateTime; 
    // array of message ids - length = message types * 2.
    private final int[] messageIDs;
    // boolean array of is UDPMultiPacketMessage Object available if requesting new Object.  - length = message types * 2.
    private final boolean[] isAvailable;
    // Array of UDPMultiPacketMessages
    //private final UDPMessagePool[] messages;
    // messages in process is used for the max variable while looping the above arrays. Is incremented by 1 for each new message, and decremented for each removed message.
    private int messagesInProcess=0;

    /** The timeout in millis is the time each message will timeout from the last packet received. */
    public UDPMessageManager(UDPMessageFactory factory, int timeout, int arrayLength)
    {
//        this.messages=new UDPMessagePool(factory);
        this.messageUpdateTime=new long[arrayLength];
        this.messageIDs=new int[arrayLength];
        this.isAvailable=new boolean[arrayLength];
        
        //TODO: Implement this class and sending messages to an actual message processor for queue deployment, or removal on timeout.
        this.timeout=timeout;

    }
    
    // TODO: implement process & remember to call ROSMessageReceiver on message complete receipt
    public void process(byte opCode, byte messageID, int blockNumber, byte[] data)
    {
        for(int i=0;i<messagesInProcess;i++)
        {
            
        }
    }
    
    public void setTimeout(int timeout)
    {
        this.timeout=timeout;
    }
    
    public int getTimeout()
    {
        return timeout;
    }
    
}
