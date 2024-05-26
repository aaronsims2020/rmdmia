package org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.tcpros;

import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import org.happy.artist.rmdmia.rcsm.provider.CommunicationSenderInterface;
import org.happy.artist.rmdmia.rcsm.provider.message.MessageHandlerControllerInterface;
import org.happy.artist.rmdmia.rcsm.provider.message.DataProcessor;
import org.happy.artist.rmdmia.rcsm.provider.message.MessageHandlerInterface;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.tcpros.data.TCPMessagePool;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.tcpros.data.TCPMultiPacketMessage;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.tcpros.data.TCPMultiPacketMessageFactory;
import org.happy.artist.rmdmia.timing.TimerService;
import org.happy.artist.rmdmia.utilities.HexStringConverter;
// Future TODO: Performance enhancement: Passing processToQueue TCP or UDPMessage will improve performance by not calling getMessage on MultiPacketMessages, and thus cutting down on processing. The 1.0 release plan is to convert to bytes by a call to get message, and not to pass by reference in order to return system resources (nevermind, technically an array ofprimitives is an Object, thus, will be pass by reference). Not doing this could potentially be beneficial if a particular sensor is in a queue and it is a seldomly used queue, therefore new data may be updated, therefore making the additional CPU processing a waste of CPU.  
/** TCPDataProcessor.java Process incoming TCPROS byte[] and TCPROS Header, then send ROS message for processing.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright© 2013 Happy Artist. All rights reserved.
 */
public class TCPDataProcessor implements MessageHandlerControllerInterface, DataProcessor
{
        // FUTURE TODO: Update the TCPMessagePool to resolve appropriate IDs, checkout message ID is a hack to copy the UDPMessagePool class for the initial implementation. Performance related issues and Pool size can be worked out at a post 1.0 release.
    // TODO: Remove TimerService from here at later date and replace with global timer service at RMDMIA level.
    TimerService timer =new TimerService(1,1,TimeUnit.MILLISECONDS);
    // TCP packet header variables.
    // 4 byte connectionID
    private byte[] connectionID;
    // 1 byte opCode
    private byte opCode;
    // 1 int messageID
// TODO: Figure out this messageID issue - removed from checkin and checkout    
    private int messageID=0;
    // 2 byte blockNumber up to 65536
    private int blockNumber;

    // Used to determine if a MultiPacket message return is the current message, otherwise checking currently checked out message, and checkout new message. This variable is not technically needed, with exception we are reusing UDP classes for TCP classes. To workaround we will use this method to loop through the TCPMessagePool Array of 256 IDs. once messageID >255 we will reset the number to 0. The default range in the TCPMessage pool is 256 messages.
    private int lastMessageID=0;
    
    // used to determine if header connection matches 
   // private boolean isMatch=true;
    
    // The maximum number of packets used in a multi-packet message
    private int maxArrayLength=-1;
    
    // Initialize the HexStringConverter for converting between bytes, hex, and Strings
    private HexStringConverter convert = HexStringConverter.getHexStringConverterInstance();

    // Manages multi-packet TCP messages.
    private TCPMessagePool multiMessagePool;
    
    // A single Multi-Packet message reference.
    private TCPMultiPacketMessage multiMessage;
    
    // Send ROS Message to Queue
    private MessageHandlerInterface receiver;

    // Initializer Message Handler
    private MessageHandlerInterface initializerMessageHandler;
    
    // Message Handler
    private MessageHandlerInterface messageHandler;    
    
    // Tuner Message Handler
    private MessageHandlerInterface tunerMessageHandler;
    
    // The last time a network heartbeat arrived via TCP.
    private long lastHeartbeat=-1;
    
    // the message length of the current message
    private int msgLength;

    // The current position in the byte array processing.        
    private int curPos=0;
    
    // the block size is used to determine the set block size increment byte array length used by the TCP input stream.
    private int blockSize=0;
    
    // The expected number of packets accumulated + the number till  the next message, or end of message.
    private double expectedPackets=0;
    
    // Default isNewMessage is true for process method.
    public boolean isNewMessage=true;
    private String threadName;
    private String topic;
    private boolean isTopic;   
    private CommunicationSenderInterface communicator;  
    private Socket parent_socket;
    
    /** The blockSize is the default incoming stream byte array processing length. Throws ClassNotFoundException if the MessageHandlerInterface implementation class cannot be found. Throws the following Exceptions on Class instantiation: InstantiationException, IllegalAccessException. */
    boolean isPublisher=false;
    public TCPDataProcessor(int blockSize,String initializerMessageHandlerClassname, String messageHandlerClassname, String tunerMessageHandlerClassname, String threadName, String topic, boolean isTopic, CommunicationSenderInterface communicator, Socket parent_socket,int data_processor_id) throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        this.data_processor_id=data_processor_id;
        this.parent_socket=parent_socket;
        // assign reference of communicator to class
        this.communicator=communicator;
///// DEBUG BEGIN
        if(communicator.getClass().getName().contains("Publisher"))
        {
            this.isPublisher=true;
//            System.out.println("Iniitializing Publisher TCPDataProcessor for topic: " + topic);
        }
///// DEBUG END        
        // set the topic/service name
        this.topic=topic;
        this.isTopic=isTopic;        
        // thread name
        this.threadName=threadName;
        // Set the TCP byte array block size.
        this.blockSize=blockSize;
        // Initialize Message Handler Object
        this.messageHandler = ((MessageHandlerInterface)Class.forName(messageHandlerClassname).newInstance());
// TODO: Determine if the setMessageHandlerController in the constructor is a memory link.        
        this.messageHandler.setMessageHandlerController(this);        
        this.messageHandler.setThreadName(threadName);   
        this.messageHandler.setTopic(topic);
        this.messageHandler.setIsTopic(isTopic);       
        this.messageHandler.setCommunicationSenderInterface(communicator);    
        this.messageHandler.setDataProcessor(this);
        // Initialize Initializer Message Handler Object
        this.initializerMessageHandler = ((MessageHandlerInterface)Class.forName(initializerMessageHandlerClassname).newInstance());
        this.initializerMessageHandler.setMessageHandlerController(this);        
        this.initializerMessageHandler.setThreadName(threadName);  
        this.initializerMessageHandler.setTopic(topic);
        this.initializerMessageHandler.setIsTopic(isTopic); 
        this.initializerMessageHandler.setCommunicationSenderInterface(communicator);     
        this.initializerMessageHandler.setDataProcessor(this);        
        // Initialize Tuner Message Handler Object
        this.tunerMessageHandler = ((MessageHandlerInterface)Class.forName(tunerMessageHandlerClassname).newInstance());
        this.tunerMessageHandler.setMessageHandlerController(this);        
        this.tunerMessageHandler.setThreadName(threadName);  
        this.tunerMessageHandler.setTopic(topic);
        this.tunerMessageHandler.setIsTopic(isTopic);    
        this.tunerMessageHandler.setCommunicationSenderInterface(communicator);             this.tunerMessageHandler.setDataProcessor(this);     
        // Set the MessageHandler Object to Initializer Message handler by defualt.
        this.receiver = initializerMessageHandler;
        // Instantiate the MultiPacketMessagePool
        this.multiMessagePool=new TCPMessagePool(new TCPMultiPacketMessageFactory());
    }

    /** Switches to using the tuner message handler. */    
    public void setUseTunerMessageHandler()
    {
        this.receiver = tunerMessageHandler;        
    }
    
    /** Switches to using the initializer message handler. */    
    public void setUseInitializerMessageHandler()
    {
        this.receiver = initializerMessageHandler;        
    }
    
    /** Switches to using the base message handler. */
    public void setUseMessageHandler()
    {
        this.receiver = messageHandler;          
    }    
    
// TODO: Figure out this message ID stuff. Created a sufficient default implementation, but additional mods can remove unused code.    
    // TODO: ROS Message bytes counter intended to determine if a message length is equal to the block size. If so -1 is passed into the dataLength as a warning to the MultiPacketArrayMessage append method. 
          private int bytesCount=0;
          private int msgBytesLength=0;
          // block size offset is the block size minus 4 required bytes for an additional message or field size. Used in calculating if message is single or multiple blocks.
          private int blockSizeOffset;
    // Process the TCP input stream by bytes, and stream data length. 
    public void process(byte[] packet, int dataLength)
    {   
        // Begin debugging
//        if(isPublisher)
//        {
//            System.out.println(threadName + " process called on incoming data for topic: " + topic + ", in TCPDataProcessor class.");
 //       }
        // End debugging
        
        if(isNewMessage)
        {
//TODO Alpha: Determine message length, then make sure counting continues at the end of the message, until all follow on messages are read. This is only used in rules if end is reached at the last array element, otherwise the size is cross checked against maximum value, and message thrown out if it is exceeded.
            //System.out.println("PACKET LENGTH:" + packet.length);

            if(dataLength<blockSize)
            {//done
                // is single packet message
                // process message(s) here
                this.lastMessageID=messageID; 
                receiver.process(packet,dataLength);                  
            }
            else
            {
                // is multi-packet message unless single packet message is equal to blockSize, in which case the message length needs to be read from the binary data.
                // reset curPos to 0
                this.curPos=0;
                // Read the message size from the data.
                this.msgLength = ((packet[curPos+3] & 0xFF) << 24) | ((packet[curPos+2] & 0xFF) << 16)
    | ((packet[curPos+1] & 0xFF) << 8) | (packet[curPos] & 0xFF);
                this.msgBytesLength=msgLength + 4;
                // if message Length is greater than block size more blocks to come.
                if(msgBytesLength>blockSize)
                {
// TODO: Investigate this link on the messageID is related to out of memory exception                    
                    // Multi Packet Array
                    if(multiMessage!=null&&messageID!=lastMessageID)
                    {
                       multiMessagePool.checkin(multiMessage.messageID);
                       // update message id to next iterative message ID... what to do when messageID is equal to lastMessageID? Will need to think about this more
                       this.messageID = messageID + 1;
                       // if messageID is greater than 255 reset messageID to 0.
                       if(messageID>255)
                       {
                           this.messageID=0;
                       }
                       this.multiMessage=null;
                    }
                    // need to process multi-packet message - calls processToQueue when message is complete - default message ID for first will always be 0 in this class.
                   this.multiMessage=(TCPMultiPacketMessage)multiMessagePool.checkout();
                    
                    this.lastMessageID=messageID; 
                    this.isNewMessage=false;                    
                    multiMessage.first(packet, blockSize, dataLength);                   // TEST multiMessage values
//                    System.out.println("after first() called: " + String.valueOf(HexStringConverter.bytesToHex(multiMessage.getPackets()[0].bytes)));
                }
                // else if message Length is less than block size check if more blocks exist in .
                else if(msgBytesLength<blockSize)
                {
                    // We know message bytes length is less than block size, and read data length is equal to block size. Now we determine if message is single or multimessage. 
                    this.bytesCount=msgBytesLength;
                    this.blockSizeOffset=blockSize-4;
                    if(bytesCount<blockSizeOffset)
                    {
                        while(bytesCount<blockSizeOffset)
                        {
                            // Set the curPos
                            this.curPos=bytesCount;
                    // Read the message size from the data.
                    this.bytesCount = bytesCount + (((packet[curPos+3] & 0xFF) << 24) | ((packet[curPos+2] & 0xFF) << 16)
        | ((packet[curPos+1] & 0xFF) << 8) | (packet[curPos] & 0xFF)) + 4;

                              if(bytesCount<=msgBytesLength)
                          {
                               // ERROR in message data.
                                break;
                            }
                        }
                              if(bytesCount==blockSize)
                              {
                                  // is single packet message 
                                 // process message(s) here
                                 this.lastMessageID=messageID;  
                                 receiver.process(packet, dataLength); 
                              }
                              else
                              {
                                  // is multi message
                 // Multi Packet Array
                    if(multiMessage!=null&&messageID!=lastMessageID)
                    {
                       multiMessagePool.checkin(multiMessage.messageID);
                       // update message id to next iterative message ID... what to do when messageID is equal to lastMessageID? Will need to think about this more
                       this.messageID = messageID + 1;
                       // if messageID is greater than 255 reset messageID to 0.
                       if(messageID>255)
                       {
                           this.messageID=0;
                       }
                       this.multiMessage=null;
                    }
                    // need to process multi-packet message - calls processToQueue when message is complete - default message ID for first will always be 0 in this class.
                    this.multiMessage=(TCPMultiPacketMessage)multiMessagePool.checkout();
//                    this.multiMessage=(TCPMultiPacketMessage)multiMessagePool.checkout(this.messageID);
                    
                    this.lastMessageID=messageID; 
                    this.isNewMessage=false;                    
                    multiMessage.first(packet, blockSize, dataLength);                                               
                              }
                    }
                    else
                    {
                        // Message is multi message.
                 // Multi Packet Array
                    if(multiMessage!=null&&messageID!=lastMessageID)
                    {
                       multiMessagePool.checkin(multiMessage.messageID);
                       // update message id to next iterative message ID... what to do when messageID is equal to lastMessageID? Will need to think about this more
                       this.messageID = messageID + 1;
                       // if messageID is greater than 255 reset messageID to 0.
                       if(messageID>255)
                       {
                           this.messageID=0;
                       }
                       this.multiMessage=null;
                    }
                    // need to process multi-packet message - calls processToQueue when message is complete - default message ID for first will always be 0 in this class.
                    this.multiMessage=(TCPMultiPacketMessage)multiMessagePool.checkout();
//                    this.multiMessage=(TCPMultiPacketMessage)multiMessagePool.checkout(this.messageID);
                    
                    this.lastMessageID=messageID; 
                    this.isNewMessage=false;                    
                    multiMessage.first(packet, blockSize, dataLength);                                     
                    }
                }
                else
                {
                    // is single packet message
                    // process message(s) here
                    this.lastMessageID=messageID;  
                    receiver.process(packet, dataLength);                     
                }
            }
        }
        else
        {         
            // is multi packet message, and is started.
            if(messageID==lastMessageID&&multiMessage!=null)
            {                 
                this.expectedPackets = Math.ceil((double)blockSize/msgBytesLength);
                if (expectedPackets == (long)expectedPackets&&expectedPackets==(multiMessage.totalPackets + 1))
                {
                    // last byte of message, pass in -1 to append for end of message in param dataLength.
//                    System.out.println("Expected packets: " + expectedPackets);
                    multiMessage.append(packet, -1);
                }
                else
                {
                    // Is not perfectly divided, therefore is not the final byte.
                    multiMessage.append(packet, dataLength);
                }
 //                   System.out.println("after append() called: " + String.valueOf(HexStringConverter.bytesToHex(multiMessage.getPackets()[0].bytes)));                    
            }
            else
            {
                this.lastMessageID=messageID;
            }
            // TODO: figure out replacement for blocksRemaining==0 - replacement will be if last received append datalength is less than block size, or if last received append ROS message defined bytes data was the end of length of message at block size. otherwise ensure block data if block data is more than x size throw it out. 
            if(multiMessage!=null&&multiMessage.isComplete)
            {
                receiver.process(multiMessage.getMessage());
                multiMessagePool.checkin(multiMessage.messageID);
                // update message id to next iterative message ID... what to do when messageID is equal to lastMessageID? Will need to think about this more
                this.messageID = messageID + 1;
                // if messageID is greater than 255 reset messageID to 0.
                if(messageID>255)
                {
                    this.messageID=0;
                }                
                multiMessage=null;
                this.isNewMessage=true;
            }            
        }      
    }
    
    /** Set the Connection ID. Used to compare incoming packets against this connection to verify they are coming from ROS. */
    public void setConnectionID(String connectionID) throws UnsupportedEncodingException
    {
        this.connectionID=HexStringConverter.hexToBytes(convert.stringToHex(connectionID));
    }
    
    /** Return the set Connection ID. byte[] processing more efficient then doing conversion on byte[] here. */
    public String getConnectionID()
    {
        return convert.convertHexToString(HexStringConverter.bytesToHex(connectionID));
    }
 
    
    public void setSocket(Socket socket) {
        this.parent_socket=socket;
    }

    public Socket getSocket() {
        return parent_socket;
    }        

    private final static String classname="TCPDataProcessor";
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
        return messageHandler;
    }

    @Override
    public MessageHandlerInterface getUseTunerMessageHandler() {
        return tunerMessageHandler;
    }

    @Override
    public MessageHandlerInterface getUseInitializerMessageHandler() {
        return initializerMessageHandler;
    }

    @Override
    public MessageHandlerInterface getUseMessageHandler() {
        return messageHandler;
    }
}
