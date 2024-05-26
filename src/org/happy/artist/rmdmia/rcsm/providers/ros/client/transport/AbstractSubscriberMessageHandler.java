package org.happy.artist.rmdmia.rcsm.providers.ros.client.transport;

import org.happy.artist.rmdmia.rcsm.provider.message.DataHandlerInterface;
import org.happy.artist.rmdmia.rcsm.provider.message.MessageHandlerInterface;
import org.happy.artist.rmdmia.rcsm.provider.CommunicationSenderInterface;
import org.happy.artist.rmdmia.rcsm.provider.message.MessageHandlerControllerInterface;
import org.happy.artist.rmdmia.rcsm.provider.message.DataProcessor;

/** AbstractSubscriberMessageHandler - extend this class to receive 
 * subscribed messages of the associated topic (This message handler 
 * class can also be used on persistent services). Requires implementation 
 * of the process methods. The new class will need to be located in the RCSM Plugins
 * directory as a JAR file, and updated in the topic_registry.eax. The simplest 
 * way to set this configuration is to modify it using the RMDMIA for ROS 
 * Configuration Manager GUI Tool. Only update the MessageHandler class. Do not 
 * update the Initialization Message Handler, or the Tuner Message Handler. Neither,
 * is designed to use this class, and it will cause unexpected results break 
 * TCPROS and UDPROS protocol handling.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright© 2013-2014 Happy Artist. All rights reserved.
 */
public abstract class AbstractSubscriberMessageHandler implements MessageHandlerInterface
{
    private String threadName;
    // Allows the MessageHandler to change the Message Handler on the fly.
    private MessageHandlerControllerInterface messageHandlerController;
    // set CommunicationSenderInterface
    private CommunicationSenderInterface senderInterface;    
    
    /** Process the message. Called by direct stream read byte[]'s rather than making a new byte[] copy Object. This will likely be removed in favor of the byte[] only at a later date if a byte[] copy is made to the exact length. Until then a byte array copy will be allowed by implementers of this interface to avoid performance bottlenecks at the cost of increased complexity in the interim. In other words this byte[] is a reference that is used by the InputStream reader, and therefore a copy of this byte[] must be made, or the next incoming message will overwrite it. Unless, the data is handled here directly (i.e. a clock value is read and written directly from this method. Then a copy is never needed. This is much higher performance way to read the clock then making a copy of the incoming byte[] and sending it somewhere else, then processing the message). */
    public abstract void process(byte[] message, int dataLength);

    @Override
    public void setThreadName(String threadName) {
        this.threadName=threadName;
    }

    @Override
    public String getThreadName() 
    {
        return threadName;
    }

    @Override
    public void setMessageHandlerController(MessageHandlerControllerInterface messageHandlerController) {
        this.messageHandlerController=messageHandlerController;
    }

    /** Process the message (Called by Multi Packet Message Implementations - this is a new Array Object that is not referenced anywhere else). */    
    public abstract void process(byte[] message);

    private String topic="";
    private boolean isTopic=true;
    @Override
    public void setTopic(String topic) 
    {
        this.topic = topic;
    }

    @Override
    public String getTopic() 
    {
        return topic;    
    }

    @Override
    public void setIsTopic(boolean isTopic) 
    {
        this.isTopic=isTopic;
    }

    @Override
    public boolean isTopic() 
    {
        return isTopic;
    }    
    
    public void setCommunicationSenderInterface(CommunicationSenderInterface senderInterface)
    {
        this.senderInterface=senderInterface;
    }
  
    public CommunicationSenderInterface getCommunicationSenderInterface()
    {
        return senderInterface;
    }        
    private DataProcessor dataProcessor;
    @Override
    public void setDataProcessor(DataProcessor dataProcessor) {
        this.dataProcessor=dataProcessor;
    }

    @Override
    public DataProcessor getDataProcessor() {
        return dataProcessor;
    }  
    
    @Override
    public void registerDataHandler(DataHandlerInterface dataHandler) throws UnsupportedOperationException 
    {
        throw new UnsupportedOperationException();
    }    
}
