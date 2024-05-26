package org.happy.artist.rmdmia.rcsm.providers.ros.client.transport;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import org.happy.artist.rmdmia.rcsm.provider.message.DataHandlerInterface;
import org.happy.artist.rmdmia.rcsm.provider.message.MessageHandlerInterface;
import java.util.logging.Logger;
import org.happy.artist.rmdmia.perception.engine.PEManager;
import org.happy.artist.rmdmia.perception.engine.PEProvider;
import org.happy.artist.rmdmia.perception.engine.sensor.SensorProcessorManager;
import org.happy.artist.rmdmia.rcsm.provider.CommunicationSenderInterface;
import org.happy.artist.rmdmia.rcsm.provider.message.MessageHandlerControllerInterface;
import org.happy.artist.rmdmia.rcsm.provider.message.DataProcessor;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.ROSMessageDecoder;
import org.happy.artist.rmdmia.rcsm.providers.ros.ROSMessage;
import org.happy.artist.rmdmia.rcsm.providers.ros.ROSMessagePool;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.ROSSubscriberMessageManager;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.ROSTopicRegistryMessageDefinition;


/** DefaultMessageHandler.java The default ROS Message Handler. This handler
 * is intended to direct the ROS Message to its next destination (RCSM Sensor Queues).
 *
 * @author Happy Artist
 * 
 * @copyright Copyright© 2013-2014 Happy Artist. All rights reserved.
 */
public class DefaultMessageHandler implements MessageHandlerInterface
{
    private ROSMessageDecoder decoder = new ROSMessageDecoder();
    private ROSMessage ros_message;
    private String threadName;
    // Reference to SensorProcessorManager for sending messages to PE
    private SensorProcessorManager spm;    
        // TODO: Update the message_definition in the configuration if it does 
        // not exist, or throw an exception if it does not match. Contemplate an 
        // option to update the message_definition automatically, if the version does not match). Defaulting to 10 Pool size for alpha release. This will cause higher memory usage.    
    private ROSMessagePool pool = ROSMessagePool.getInstance();  
    // Allows the MessageHandler to change the Message Handler on the fly.
    private MessageHandlerControllerInterface messageHandlerController;
    // set CommunicationSenderInterface
    private CommunicationSenderInterface senderInterface;    
    // The Logger
//    private final static Logger logger = Logger.getLogger(DefaultMessageHandler.class.getName());
    // Use the SubscriberMessageManager to obtain a reference to ROSNode.
    private ROSSubscriberMessageManager smm;    
    // PEProvider reference
    private PEProvider temp_provider;
    // Topic Registry Message Definition 
    private ROSTopicRegistryMessageDefinition trmd;
    
    private PEManager peManager;
    
    public DefaultMessageHandler()
    {
        try 
        {
            this.smm = ROSSubscriberMessageManager.getInstance("smmLoadedEarly",null);
        } 
        catch (UnsupportedEncodingException ex) 
        {
            Logger.getLogger(DefaultMessageHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // TODO: remove ROSMessage Decoder and comment out system 
    ROSMessageDecoder decode = new ROSMessageDecoder();
    @Override
    public void process(byte[] message, int dataLength) 
    {
//        logger.info("Received message on thread: " + threadName);
//System.out.println("Hex Message:" + org.happy.artist.rmdmia.utilities.BytesToHex.bytesToHex(message));        
//System.out.println("Message: "  + decode.convertHexToString(org.happy.artist.rmdmia.utilities.BytesToHex.bytesToHex(message).toCharArray()));
 //       System.out.println("Calling DefaultMessageHandler.");
        this.ros_message = decoder.getMessage(message);
        //System.out.println("Subscriber process");
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
        // Send ROSMessage to Perception Engine
        if(spm!=null)
        {
            //System.out.println("tid: " + tid);
            ros_message.hid=tid;                    
            spm.process(ros_message);
        }
        else
        {
            //System.out.println("Calling dmh null spm.process.");
            this.peManager=smm.rosNode.getController().getPE();
            this.temp_provider=peManager.getProviderByName("ros_pe_provider");
            if(temp_provider!=null)
            {
             //   System.out.println("temp_provider ! null" + threadName);
                this.spm = temp_provider.getSensorProcessorManager();
//                this.temp_provider=null;
                ros_message.hid=tid;
            }
            if(spm!=null)
            {
                ros_message.hid=tid;
               // System.out.println("spm ! null ros_message.hid:" + ros_message.hid);
                spm.process(ros_message);
            }
           // else
           // {
           //     System.out.println("We have a problem... Remove when fixed.");
           // }
        }        
        // Check the ROSMessage back into the ROSMessagePool.
        pool.checkin(ros_message.rosMessageID);
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
  //      System.out.println("Hex Message:" + org.happy.artist.rmdmia.utilities.BytesToHex.bytesToHex(message));        
//System.out.println("Message: "  + decode.convertHexToString(org.happy.artist.rmdmia.utilities.BytesToHex.bytesToHex(message).toCharArray()));
//        System.out.println("Calling DefaultMessageHandler.");
        this.ros_message = decoder.getMessage(message);
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
        if(ros_message.text_body!=null)
        {
            System.out.println("text_body(rmdmia)="+ String.valueOf(ros_message.text_body));         
        }
        if(ros_message.binary_body!=null)
        {
            System.out.println("binary_body(rmdmia)="+ ros_message.binary_body);         }
        System.out.println("error(rmdmia?)="+ ros_message.error);         
        * */
        // Send ROSMessage to Perception Engine
       if(spm!=null)
        {
     //       System.out.println("tid: " + tid);
            ros_message.hid=tid;
            spm.process(ros_message);
        }
        else
        {
       //     System.out.println("Calling dmh null spm.process.");            
            this.peManager=smm.rosNode.getController().getPE();
            this.temp_provider=peManager.getProviderByName("ros_pe_provider");
            if(temp_provider!=null)
            {
       //         System.out.println("temp_provider ! null" + threadName);
                this.spm = temp_provider.getSensorProcessorManager();
//                this.temp_provider=null;
                ros_message.hid=tid;
            }
            if(spm!=null)
            {
                ros_message.hid=tid;
       //         System.out.println("spm ! null ros_message.hid:" + ros_message.hid);
                spm.process(ros_message);
            }
         //   else
         //   {
         //       System.out.println("We have a problem... Remove when fixed.");
         //   }
        }              
        // Check the ROSMessage back into the ROSMessagePool.
        pool.checkin(ros_message.rosMessageID);
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
        throw new UnsupportedOperationException();
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
