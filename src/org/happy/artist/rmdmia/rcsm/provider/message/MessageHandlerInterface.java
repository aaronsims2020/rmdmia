package org.happy.artist.rmdmia.rcsm.provider.message;

import org.happy.artist.rmdmia.rcsm.provider.CommunicationSenderInterface;

 /** org.happy.artist.rmdmia.rcsm.client.transport.MessageHandlerInterface - 
  * This interface directs received messages to the proper message destination..
  * 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2013-2014 Happy Artist. All rights reserved.
 * 
 */
public interface MessageHandlerInterface 
{
    /** Get the topic identifier. Some MessageHandlerInterface implementations use the tid to associate an RMDMIA Hardware ID. */
    public int getTID();
    /** Set the topic identifier. Some MessageHandlerInterface implementations use the tid to associate an RMDMIA Hardware ID. */
    public void setTID(int tid);
    /** process the message (Called by Multi Packet Message Implementations). */
    public void process(byte[] message);
// TODO: Evaluate and update this interface if necessary before a 1.0 release.    
    /** process the message. Called by direct stream read byte[]'s rather than making a new byte[] copy Object. dataLength was added due to issues with corrupt message data.If all ROS Messages were uncorrupted no need for this input would exist due to message lengths inside the ROS Message format, but this is an issue even on the turtlesim pose. This will likely be removed in favor of the byte[] only at a later date if a byte[] copy is made to the exact length. Until then a byte array copy will be allowed by implementers of this interface to avoid performance bottlenecks at the cost of increased complexity in the interim. */
    public void process(byte[] message, int dataLength);    
    /** Set the MessageHandlerControllerInterface Object. Reference used to change the MessageHandler Controller from within a message handler. */
    public void setMessageHandlerController(MessageHandlerControllerInterface messageHandlerController);   
    /** Set the thread name. Used for troubleshooting and debugging. */
    public void setThreadName(String threadName);
    /** Return the thread name. */
    public String getThreadName();
    /** Set the topic, or service. */
    public void setTopic(String topic);
    /** Return the topic, or service. */
    public String getTopic();
    /** Set isTopic to true for topic, and false for service. */
    public void setIsTopic(boolean isTopic);
    /** Return true for topic, and false for service. */
    public boolean isTopic();    
    /** Set an instance of the CommunicationSenderInterface. */
    public void setCommunicationSenderInterface(CommunicationSenderInterface senderInterface);
    /** Return an instance of the CommunicationSenderInterface. */
    public CommunicationSenderInterface getCommunicationSenderInterface();  
    /** Set an instance of the DataProcessor associated to the message data handler. */
    public void setDataProcessor(DataProcessor dataProcessor);
    /** Return the DataProcessor class that sets the MessageHandlers. */
    public DataProcessor getDataProcessor();

    /** Register a custom data handler for the MessageHandlerInterface.
     * 
     * @param dataHandler
     * @throws java.lang.UnsupportedOperationException if DataHandlerInterface is not supported
     * by MessageHandlerInterface implementation class.
     */
    public void registerDataHandler(DataHandlerInterface dataHandler) throws java.lang.UnsupportedOperationException;
}
