package org.happy.artist.rmdmia.rcsm.providers.ros.client.transport;

import org.happy.artist.rmdmia.rcsm.provider.message.DataHandlerInterface;
import org.happy.artist.rmdmia.rcsm.provider.message.MessageHandlerInterface;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.happy.artist.rmdmia.rcsm.provider.CommunicationSenderInterface;
import org.happy.artist.rmdmia.rcsm.provider.message.MessageHandlerControllerInterface;
import org.happy.artist.rmdmia.rcsm.provider.message.DataProcessor;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.ROSMessageDecoder;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.ROSSubscriberMessageManager;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.ROSTopicRegistryMessageDefinition;

/** AbstractServiceMessageHandler - This handler is intended to direct 
 * the ROS Message to its next destination. Extend this class to receive 
 * a service response message of the associated service. Requires implementation 
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
public class AbstractServiceMessageHandler implements MessageHandlerInterface
{
//    private ROSMessageDecoder decoder = new ROSMessageDecoder();
    private String threadName;
    // Allows the MessageHandler to change the Message Handler on the fly.
    private MessageHandlerControllerInterface messageHandlerController;
    // set CommunicationSenderInterface
    private CommunicationSenderInterface senderInterface;    
    // Update the SubsscriberMessageManager with the message_definition information.
    private ROSSubscriberMessageManager smm; 
    // Topic Registry Message Definition for persistance variable
    private ROSTopicRegistryMessageDefinition trmd;    
    // DataHandlerInterface for handling incoming data on supported MessageHandlerInterface classes.
    private DataHandlerInterface dataHandler;
    // The Logger
    private final static Logger logger = Logger.getLogger(DefaultServiceMessageHandler.class.getName());
    
    /** Always call super() on extended constructor. */
    public AbstractServiceMessageHandler()
    {
        try 
        {
            this.smm = ROSSubscriberMessageManager.getInstance("smmLoadedEarly",null);
        } 
        catch (UnsupportedEncodingException ex) 
        {
            logger.log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void process(byte[] message, int dataLength) 
    { 
        // Topic
        if(dataHandler!=null)
        {
            dataHandler.process(message, dataLength);
        }    
        // Topic
        // TODO: pass in the trmd into a constructor, to avoid this lookup every call.
        if(trmd!=null)
        {
            // Topic
            this.trmd=smm.getTopicRegistryMessageDefinitionByTID(smm.getTIDByServiceName(getTopic()));  
        }  
        if(trmd!=null&&trmd.persistant.indexOf("1")!=-1)
        {
            // service is persistent
            
            //System.out.println("DefaultServiceMessageHandler: persistant connection on service: " + topic);            
        }
        else
        {
            // If service is not persistent close the connection.
            senderInterface.disconnect();
            //System.out.println("DefaultServiceMessageHandler: Called disconnect on service: " + topic);
        }
    }

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

    @Override
    public void process(byte[] message) 
    { 
        if(dataHandler!=null)
        {
            dataHandler.process(message);
        }        
        if(trmd!=null)
        {
            // Topic
            this.trmd=smm.getTopicRegistryMessageDefinitionByTID(smm.getTIDByServiceName(getTopic()));  
        }
        if(trmd!=null&&trmd.persistant.indexOf("1")!=-1)
        {
            // service is persistent
            
            //System.out.println("DefaultServiceMessageHandler: persistant connection on service: " + topic);            
        }
        else
        {
            // If service is not persistent close the connection.
            senderInterface.disconnect();
            //System.out.println("DefaultServiceMessageHandler: Called disconnect on service: " + topic);
        }
    }

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
    public DataProcessor getDataProcessor() 
    {
        return dataProcessor;
    }  

    @Override
    public void registerDataHandler(DataHandlerInterface dataHandler) throws UnsupportedOperationException 
    {
        this.dataHandler=dataHandler;
    }

    private int tid=-1;
    public int getTID() {
        return tid;
    }

    public void setTID(int tid) {
        this.tid=tid;
    }
}
