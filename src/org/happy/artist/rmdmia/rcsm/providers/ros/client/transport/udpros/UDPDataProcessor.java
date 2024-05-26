package org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.udpros;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.happy.artist.rmdmia.rcsm.provider.message.MessageHandlerControllerInterface;
import org.happy.artist.rmdmia.rcsm.provider.message.DataProcessor;
import org.happy.artist.rmdmia.rcsm.provider.message.MessageHandlerInterface;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.udpros.packet.UDPMessagePool;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.udpros.packet.UDPMultiPacketMessage;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.udpros.packet.UDPMultiPacketMessageFactory;
import org.happy.artist.rmdmia.timing.TimerService;
import org.happy.artist.rmdmia.utilities.HexStringConverter;

// Future TODO: Performance enhancement: Passing processToQueue TCP or UDPMessage will improve performance by not calling getMessage on MultiPacketMessages, and thus cutting down on processing. The 1.0 release plan is to convert to bytes by a call to get message, and not to pass by reference (nevermind, technically an array ofprimitives is an Object, thus, will be pass by reference) in order to return system resources. Not doing this could potentially be beneficial if a particular sensor is in a queue and it is a seldomly used queue, therefore new data may be updated, therefore making the additional CPU processing a waste of CPU. 
/** UDPDataProcessor.java Process incoming UDP DataGram byte[] and UDPROS Header, then send ROS message for processing.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright© 2013-2014 Happy Artist. All rights reserved.
 */
public class UDPDataProcessor implements MessageHandlerControllerInterface, DataProcessor
{
    // TODO: Remove TimerService from here at later date and replace with global timer service at RMDMIA level.
    TimerService timer =new TimerService(1,1,TimeUnit.MILLISECONDS);
    // UDP packet header variables.
    // 4 byte connectionID
    private byte[] connectionID;
    // 1 byte opCode
    private byte opCode;
    // 1 byte messageID
    private byte messageID;
    // 2 byte blockNumber up to 65536
    private int blockNumber;

    // Used to determine if a MultiPacket message return is the current message, otherwise checking currently checked out message, and checkout new message.
    private byte lastMessageID;
    
    // UDP Header OpCodes
    private final static byte ROS_UDP_DATA=(byte)0x00;
    private final static byte ROS_UDP_DATAN=(byte)0x01;
    private final static byte ROS_UDP_PING=(byte)0x02;
    private final static byte ROS_UDP_ERR=(byte)0x03;   
    
    // used to determine if header connection matches 
    private boolean isMatch=true;
    
    // The maximum number of packets used in a multi-packet message
    private int maxArrayLength=-1;
    
    // Initialize the HexStringConverter for converting between bytes, hex, and Strings
    private HexStringConverter convert = HexStringConverter.getHexStringConverterInstance();

    // Manages multi-packet UDP messages.
    private UDPMessagePool multiMessagePool;
    
    // A single Multi-Packet message reference.
    private UDPMultiPacketMessage multiMessage;
    
    
    // Send ROS Message to Queue
    private MessageHandlerInterface receiver;
    
    // Initializer Message Handler
//    private MessageHandlerInterface initializerMessageHandler;
    
    // Message Handler
    private MessageHandlerInterface messageHandler;    
    
    // Tuner Message Handler
    private MessageHandlerInterface tunerMessageHandler;    
    
    // The last time a network heartbeat arrived via UDP.
    private long lastHeartbeat=-1;
    // thread name
    private String threadName;
    
    /** Throws ClassNotFoundException if the MessageHandlerInterface implementation class cannot be found. Throws the following Exceptions on Class instantiation: InstantiationException, IllegalAccessException. */    
    public UDPDataProcessor(String initializerMessageHandlerClassname, String messageHandlerClassname, String tunerMessageHandlerClassname, String threadName) throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        this.threadName=threadName;
        // Initialize Message Handler Object
        this.messageHandler = ((MessageHandlerInterface)Class.forName(messageHandlerClassname).newInstance());
        this.messageHandler.setMessageHandlerController(this);
        this.messageHandler.setThreadName(threadName);        
        // Initialize Initializer Message Handler Object
//        this.initializerMessageHandler = ((MessageHandlerInterface)Class.forName(initializerMessageHandlerClassname).newInstance());
//        this.initializerMessageHandler.setMessageHandlerController(this);        
//        this.initializerMessageHandler.setThreadName(threadName);           
        // Initialize Tuner Message Handler Object
        this.tunerMessageHandler = ((MessageHandlerInterface)Class.forName(tunerMessageHandlerClassname).newInstance());
        this.tunerMessageHandler.setMessageHandlerController(this);
        this.tunerMessageHandler.setThreadName(threadName);          
        // Set the MessageHandler Object to Message handler by default.
        this.receiver = messageHandler;
        // Instantiate the MultiPacketMessagePool
        this.multiMessagePool=new UDPMessagePool(new UDPMultiPacketMessageFactory());
    }
    
    public void process(byte[] packet)
    {
//        System.out.println("UDP process connectionID: " + BytesToHex.bytesToHex(connectionID) + " packet[0-3]: " + BytesToHex.bytesToHex(Arrays.copyOfRange(packet, 0, 4)));
        // If connection id equals set connection id process this packet.
       // if(connectionID[0]==packet[0]&&connectionID[1]==packet[1]&&connectionID[2]==packet[2]&&connectionID[3]==packet[3]&&connectionID[4]==packet[4]&&connectionID[5]==packet[5]&&connectionID[6]==packet[6]&&connectionID[7]==packet[7])
        if(connectionID[0]==packet[0]&&connectionID[1]==packet[1]&&connectionID[2]==packet[2]&&connectionID[3]==packet[3])        
        {
            // Set connection match to true
            this.isMatch=true;
            // Check OpCode
            this.opCode=packet[4];
            this.messageID=packet[5];
            // block number in little endian may be incorrect.
            this.blockNumber=((packet[7] & 0xFF) << 8) | (packet[6] & 0xFF);   
//            System.out.println("UDP_BLOCK_#="+ blockNumber);
        }
        else
        {
            this.isMatch=false;
        }
        
        // Process OpCode
        if(isMatch&&ROS_UDP_DATA==opCode)
        {
//            System.out.println("UDP receiver.process() - ROS_UDP_DATA block #: " + blockNumber);
            // If block numbeeer is 1, and opCode==0
            if(blockNumber==1)
            {
                this.lastMessageID=messageID;     
                // process message here
                //Arrays.copyOfRange(packet, 8, packet.length);
                //receiver.process(packet);                   
                // Need to copy the range due to 8 start bytes, and a running reference to the packets that will overwrite the data on DataSocket read.
// TODO: PERFORMANCE TODO!!! replace with more efficient byte[] copy....
                receiver.process(Arrays.copyOfRange(packet, 8, packet.length));
            }
            else
            {
                if(multiMessage!=null&&messageID!=lastMessageID)
                {
                    multiMessagePool.checkin(multiMessage.messageID);
                    this.multiMessage=null;
                }
                //TODO: Setup simple packet loss algorthm, and throw out out of sequence messages and blocks.
                // need to process multi-packet message - calls processToQueue when message is complete
//                multiMessageManager.process(opCode, messageID, blockNumber, packet);
                this.multiMessage=(UDPMultiPacketMessage)multiMessagePool.checkout(messageID & 0xff);
                this.lastMessageID=messageID;      
                //multiMessage.first(packet, 0, blockNumber);
// TODO: PERFORMANCE TODO!!! replace with more efficient byte[] copy....  
                multiMessage.first(Arrays.copyOfRange(packet, 8, packet.length), 0, blockNumber);
                
            }
        }
        else if(isMatch&&ROS_UDP_DATAN==opCode)
        {
//            System.out.println("UDP receiver.process() - isMatch&&ROS_UDP_DATAN==opCode");
            // need to process multi-packet message - calls processToQueue when message is complete
           // multiMessageManager.process(opCode, messageID, blockNumber, packet);
            if(messageID==lastMessageID&&multiMessage!=null)
            {
                //multiMessage.append(packet, blockNumber);
// TODO: PERFORMANCE TODO!!! replace with more efficient byte[] copy....  
                multiMessage.append(Arrays.copyOfRange(packet, 8, packet.length), blockNumber);                
            }
            else
            {
                this.lastMessageID=messageID;
            }
            if(multiMessage!=null&&multiMessage.blocksRemaining==0)
            {
//                System.out.println("UDP multimessage receiver.process()");
                receiver.process(multiMessage.getMessage());
                multiMessagePool.checkin(messageID & 0xff);
                multiMessage=null;
            }
        }
        else if(isMatch&&ROS_UDP_PING==opCode)
        {
//            System.out.println("UDP receiver.process() - isMatch&&ROS_UDP_PING==opCode");            
            // TODO: Implement Heart beat on connection based on heartbeart if used.            
            // ROS Keep-alive heartbeat.
            this.lastHeartbeat=timer.SYSTEM_TIME;
        }
        else if(isMatch&&ROS_UDP_ERR==opCode)
        {
            // ROS encountered an error.
            System.out.println("UDP receiver.process() - isMatch&&ROS_UDP_ERR==opCode"); 
        }          
        else
        {
            // If error occurs mark message as bad and throw out or ignore.
            // TODO: implement error processing
//            System.out.println("UDP receiver.process() - UNKNOWN");             
        }
        
        
    }
    

    /** Switches to using the tuner message handler. */    
    public void setUseTunerMessageHandler()
    {
        this.receiver = tunerMessageHandler;        
    }
    
    /** Overidden with MessageHandler, on UDP implementation. InitializerMessageHandler not supported on UDP. */    
    public void setUseInitializerMessageHandler()
    {
        this.receiver = messageHandler;        
    }
    
    /** Switches to using the base message handler. */
    public void setUseMessageHandler()
    {
        this.receiver = messageHandler;        
    }     
    
     private int connIDLength;
    /** Set the Connection ID. Used to compare incoming packets against this connection to verify they are coming from ROS. */
    public void setConnectionID(String connectionId) throws UnsupportedEncodingException
    {
        if((this.connIDLength=connectionId.length())==1)
        {
                connectionId=UDPProtocolUtility.pad1Zero.concat(connectionId).concat(UDPProtocolUtility.pad6Zero);
        }
        else if(connIDLength==2)
        {
                 connectionId=connectionId.concat(UDPProtocolUtility.pad6Zero);           
        }
        else if(connIDLength==3)
        {
                connectionId=UDPProtocolUtility.pad1Zero.concat(connectionId).concat(UDPProtocolUtility.pad4Zero);
        }    
        else if(connIDLength==4)
        {
                 connectionId=connectionId.concat(UDPProtocolUtility.pad4Zero);           
        }      
        else if(connIDLength==5)
        {
                connectionId=UDPProtocolUtility.pad1Zero.concat(connectionId).concat(UDPProtocolUtility.pad2Zero);
        }           
        else if(connIDLength==6)
        {
                 connectionId=connectionId.concat(UDPProtocolUtility.pad2Zero);           
        }    
        else if(connIDLength==7)
        {
                connectionId=UDPProtocolUtility.pad1Zero.concat(connectionId);
        }           
        this.connectionID=HexStringConverter.hexToBytes(connectionId);      
    }
    
    /** Return the set Connection ID. byte[] processing more efficient then doing conversion on byte[] here. */
    public String getConnectionID()
    {
        return convert.convertHexToString(HexStringConverter.bytesToHex(connectionID));
    }
    
    private final static String classname="UDPDataProcessor";
    @Override
    public String getClassname() {
        return classname;
    }    

    public int data_processor_id = 0;
    @Override
    public int getId() {
        return data_processor_id;
    }    

    /** Return the MessageHandlerInterface. */
    public MessageHandlerInterface getMessageHandler() 
    {
       return receiver;
    }
    
    @Override
    public MessageHandlerInterface getUseTunerMessageHandler() {
        return tunerMessageHandler;
    }

    @Override
    public MessageHandlerInterface getUseInitializerMessageHandler() {
        return messageHandler;
    }

    @Override
    public MessageHandlerInterface getUseMessageHandler() {
        return messageHandler;
    }    
}
