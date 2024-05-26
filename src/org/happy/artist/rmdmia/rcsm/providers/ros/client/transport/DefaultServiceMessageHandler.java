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
import org.happy.artist.rmdmia.rcsm.providers.ros.DefaultROSMessageFactory;
import org.happy.artist.rmdmia.rcsm.providers.ros.ROSMessage;
import org.happy.artist.rmdmia.rcsm.providers.ros.ROSMessagePool;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.ROSSubscriberMessageManager;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.ROSTopicRegistryMessageDefinition;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.tcpros.TCPDataProcessor;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.udpros.UDPDataProcessor;
import org.happy.artist.rmdmia.utilities.HexStringConverter;

/** DefaultServiceMessageHandler.java The default ROS Service Message Handler. 
 * This handler is intended to direct the ROS Message to its next destination 
 * defaulting to (RCSM Sensor Queues).
 *
 * @author Happy Artist
 * 
 * @copyright Copyright© 2013-2014 Happy Artist. All rights reserved.
 */
public class DefaultServiceMessageHandler implements MessageHandlerInterface
{
    private ROSMessageDecoder decoder = new ROSMessageDecoder();
    private ROSMessage ros_message;
    private String threadName;
        // TODO: Update the message_definition in the configuration if it does 
        // not exist, or throw an exception if it does not match. Contemplate an 
        // option to update the message_definition automatically, if the version does not match). Defaulting to 10 Pool size for alpha release. This will cause higher memory usage.    
    private ROSMessagePool pool = ROSMessagePool.getInstance();  
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
//    private final static Logger logger = Logger.getLogger(DefaultServiceMessageHandler.class.getName());
    // TODO: remove ROSMessage Decoder and comment out system 
  ROSMessageDecoder decode = new ROSMessageDecoder();
    
    public DefaultServiceMessageHandler()
    {
        try 
        {
            this.smm = ROSSubscriberMessageManager.getInstance("smmLoadedEarly",null);
        } 
        catch (UnsupportedEncodingException ex) 
        {
            Logger.getLogger(DefaultPublisherInitializerMessageHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void process(byte[] message, int dataLength) 
    { 
//        logger.info("Received message on thread: " + threadName);
//System.out.println("Hex Message:" + org.happy.artist.rmdmia.utilities.BytesToHex.bytesToHex(message));        
//System.out.println("Message closer: "  + decode.convertHexToString(org.happy.artist.rmdmia.utilities.BytesToHex.bytesToHex(message).toCharArray()));
 //       System.out.println("Calling DefaultServiceMessageHandler.");
//        this.ros_message = decoder.getMessage(message);
        // DEBUG caller
        //this.ros_message = decoder.getMessage(message,threadName);
/*        if(ros_message.callerid!=null)
        {
            System.out.println("callerid="+ String.valueOf(ros_message.callerid));
        }
        if(ros_message.isTopic)
        {
            if(ros_message.topic!=null)
            {     
                System.out.println("topic="+ String.valueOf(ros_message.topic));
            }
        }
        else
        {
            if(ros_message.service!=null)
            {     
                System.out.println("service="+ String.valueOf(ros_message.service));
            }
        }
        if(ros_message.type!=null)
        {        
            System.out.println("type="+ String.valueOf(ros_message.type));
        }
        if(ros_message.message_definition!=null)
        {
            System.out.println("message_definition="+ String.valueOf(ros_message.message_definition));
        }
        if(ros_message.md5sum!=null)
        {        
            System.out.println("md5sum="+ String.valueOf(ros_message.md5sum));           }
        if(ros_message.persistent!=null)
        {
            System.out.println("persistant="+ String.valueOf(ros_message.persistent));        
        }
        if(ros_message.latching!=null)
        {
            System.out.println("latching="+ String.valueOf(ros_message.latching));
        }
        if(ros_message.tcp_nodelay!=null)
        {        
            System.out.println("tcp_nodelay="+ String.valueOf(ros_message.tcp_nodelay));     
        }
        if(ros_message.binary_body!=null)
        {
            System.out.println("binary_body(rmdmia)="+ ros_message.binary_body);         }
        System.out.println("error(rmdmia?)="+ ros_message.error);         
        */ 
/////////////////////////////////////////////////////////////////        
        //System.out.println(ROSMessageDecoder.convertHexToString(BytesToHex.bytesToHex(ros_message)));
        // Check the ROSMessage back into the ROSMessagePool.
//        pool.checkin(ros_message.rosMessageID);
  
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
//        logger.info("Received message on thread: " + threadName);
//System.out.println("Hex Message:" + org.happy.artist.rmdmia.utilities.BytesToHex.bytesToHex(message));        
//System.out.println("Message closer: "  + decode.convertHexToString(org.happy.artist.rmdmia.utilities.BytesToHex.bytesToHex(message).toCharArray()));
 //       System.out.println("Calling DefaultServiceMessageHandler.");
//        this.ros_message = decoder.getMessage(message);
        // DEBUG caller
        //this.ros_message = decoder.getMessage(message,threadName);
/*        if(ros_message.callerid!=null)
        {
            System.out.println("callerid="+ String.valueOf(ros_message.callerid));
        }
        if(ros_message.isTopic)
        {
            if(ros_message.topic!=null)
            {     
                System.out.println("topic="+ String.valueOf(ros_message.topic));
            }
        }
        else
        {
            if(ros_message.service!=null)
            {     
                System.out.println("service="+ String.valueOf(ros_message.service));
            }
        }
        if(ros_message.type!=null)
        {        
            System.out.println("type="+ String.valueOf(ros_message.type));
        }
        if(ros_message.message_definition!=null)
        {
            System.out.println("message_definition="+ String.valueOf(ros_message.message_definition));
        }
        if(ros_message.md5sum!=null)
        {        
            System.out.println("md5sum="+ String.valueOf(ros_message.md5sum));           }
        if(ros_message.persistent!=null)
        {
            System.out.println("persistant="+ String.valueOf(ros_message.persistent));        
        }
        if(ros_message.latching!=null)
        {
            System.out.println("latching="+ String.valueOf(ros_message.latching));
        }
        if(ros_message.tcp_nodelay!=null)
        {        
            System.out.println("tcp_nodelay="+ String.valueOf(ros_message.tcp_nodelay));     
        }
        if(ros_message.binary_body!=null)
        {
            System.out.println("binary_body(rmdmia)="+ ros_message.binary_body);         }
        System.out.println("error(rmdmia?)="+ ros_message.error);         
        */ 
/////////////////////////////////////////////////////////////////        
        //System.out.println(ROSMessageDecoder.convertHexToString(BytesToHex.bytesToHex(ros_message)));
        // Check the ROSMessage back into the ROSMessagePool.
//        pool.checkin(ros_message.rosMessageID);
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
    public DataProcessor getDataProcessor() {
        return dataProcessor;
    }  
    
    @Override
    public void registerDataHandler(DataHandlerInterface dataHandler) throws UnsupportedOperationException 
    {
        this.dataHandler=dataHandler;
    }
    
    private int tid=-1;
    @Override
    public int getTID() {
        return tid;
    }

    @Override
    public void setTID(int tid) {
        this.tid=tid;    
    }       
}
