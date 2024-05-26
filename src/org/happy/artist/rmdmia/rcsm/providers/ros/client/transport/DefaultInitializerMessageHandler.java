package org.happy.artist.rmdmia.rcsm.providers.ros.client.transport;

import org.happy.artist.rmdmia.rcsm.provider.message.DataHandlerInterface;
import org.happy.artist.rmdmia.rcsm.provider.message.MessageHandlerInterface;
import org.happy.artist.rmdmia.rcsm.provider.message.MessageHandlerControllerInterface;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.happy.artist.rmdmia.perception.engine.PEProvider;
import org.happy.artist.rmdmia.perception.engine.sensor.SensorProcessorManager;
import org.happy.artist.rmdmia.rcsm.provider.CommunicationSenderInterface;
import org.happy.artist.rmdmia.rcsm.provider.message.DataProcessor;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.ROSMessageDecoder;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.ROSSubscriberMessageManager;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.ROSTopicRegistryMessageDefinition;
import org.happy.artist.rmdmia.rcsm.providers.ros.ROSMessage;
import org.happy.artist.rmdmia.rcsm.providers.ros.ROSMessagePool;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.tcpros.TCPDataProcessor;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.udpros.UDPDataProcessor;


/** DefaultInitializerMessageHandler.java The default ROS Initializer 
 * Message Handler. This message handler obtains the message definition, and 
 * gives a warning if the definition is changed. If the message definition did 
 * not exist it is saved to the topic_registry configuration file, and the 
 * message is directed to its next destination (RCSM Sensor Queues).
 *
 * @author Happy Artist
 * 
 * @copyright Copyright© 2013-2014 Happy Artist. All rights reserved.
 */
public class DefaultInitializerMessageHandler implements MessageHandlerInterface
{
    private ROSMessageDecoder decoder = new ROSMessageDecoder();
    private ROSMessage ros_message;
    private String threadName;
    // Reference to SensorProcessorManager for sending messages to PE
    private SensorProcessorManager spm;
    // TODO: Make the ROSMessagePoolFactory size definable in properties at the 
    // topic/service level.
    private ROSMessagePool pool = ROSMessagePool.getInstance();  
    // TODO: remove this from here, and place in Tuner, or implement a flag that only updates if enabled to do so.
    private final String smmLoadedEarly="SubscriberMessageManager_Prematurely Loaded.";
    // Update the SusbscriberMessageManager with the message_definition information.
    private ROSSubscriberMessageManager smm;
    // Allows the MessageHandler to change the Message Handler on the fly.
    private MessageHandlerControllerInterface messageHandlerController;
    // Empty String for an equals compare using final static
    private final static String EMPTY_STRING="";
    private ROSTopicRegistryMessageDefinition trmd;
    // call private add if the following variable is set to true.
    private boolean isChanged;
    // set CommunicationSenderInterface
    private CommunicationSenderInterface senderInterface;  
    // PEProvider reference
    private PEProvider temp_provider;

    public DefaultInitializerMessageHandler() {
        try {
            this.smm = ROSSubscriberMessageManager.getInstance("smmLoadedEarly",null);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DefaultInitializerMessageHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void process(byte[] message) 
    {
//System.out.println("Hex Message:" + org.happy.artist.rmdmia.utilities.BytesToHex.bytesToHex(message));        
//System.out.println("Message: "  + decode.convertHexToString(org.happy.artist.rmdmia.utilities.BytesToHex.bytesToHex(message).toCharArray()));          
        this.isChanged=false;
//        System.out.println("Calling DefaultInitializerMessageHandler.");        
        this.ros_message = decoder.getInitializationMessage(message,topic,isTopic);

        // TODO: Update the message_definition in the configuration if it does 
        // not exist, or throw an exception if it does not match. Contemplate an 
        // option to update the message_definition automatically, if the version does not match).
        // Figure out how to do this and notify user. Prompt user via ROCI GUI, to update if message_definition does not match.
        if(ros_message.message_definition!=null&&!ros_message.message_definition.equals(EMPTY_STRING))
        {   
if(ros_message.isTopic)
            {
                if(ros_message.topic!=null)
                {     
                    // Topic
                    this.trmd=smm.getTopicRegistryMessageDefinitionByTID(smm.getTIDByTopicName(String.valueOf(ros_message.topic)));  
                }
            }
            else
            {
                if(ros_message.service!=null)
                {     
                    //Service
                    this.trmd=smm.getTopicRegistryMessageDefinitionByTID(smm.getTIDByServiceName(String.valueOf(ros_message.service)));
                }
            }    
            
            if(!trmd.definition.contains(java.nio.CharBuffer.wrap(ros_message.message_definition)))
            {
                System.out.println("DefaultInitializerMessageHandler message_definition not defined, updating message_definition."); 
                trmd.definition=String.valueOf(ros_message.message_definition);
                this.isChanged=true;
            }
  //          else
//            {
//                System.out.println("DefaultInitializerMessageHandler message_definition already defined."); 
//            }
        }
//        else
//        {
            // Log warning message that message_definition not found in connection header. Will not be an issue if user has manually defined the message_definition in the UI, or configuration files.
//            if(ros_message.isTopic&&ros_message.topic!=null)
//            {
//                System.out.println("WARNING: message_definition not found in connection header of topic: " + String.valueOf(ros_message.topic));
//            }
//            else if(ros_message.service!=null)
//            {
//                System.out.println("WARNING: message_definition not found in connection header of service: " + String.valueOf(ros_message.service));  
//            }
//        }
//TODO: 11/23/2013 Add request_topic and response_topic support

/////////////////////////////////////       
                    if(ros_message.request_type!=null&&!ros_message.request_type.equals(EMPTY_STRING))
                    {
                        if(ros_message.service!=null&&trmd==null)
                        {
                                //Service
                                this.trmd=smm.getTopicRegistryMessageDefinitionByTID(smm.getTIDByServiceName(String.valueOf(ros_message.service)));
                        }                             
                        if(!trmd.request_type.contains(java.nio.CharBuffer.wrap(ros_message.request_type)))
                        {
                            System.out.println("DefaultInitializerMessageHandler request_type not defined, updating request_type.");                         
                            trmd.request_type=String.valueOf(ros_message.request_type);
                            this.isChanged=true;                     
                        }
                    }
                            

                    if(ros_message.response_type!=null&&!ros_message.response_type.equals(EMPTY_STRING))
                    {
                        if(ros_message.service!=null&&trmd==null)
                        {
                                //Service
                                this.trmd=smm.getTopicRegistryMessageDefinitionByTID(smm.getTIDByServiceName(String.valueOf(ros_message.service)));

                        }                             
                        if(!trmd.response_type.contains(java.nio.CharBuffer.wrap(ros_message.response_type)))
                        {
                            System.out.println("DefaultInitializerMessageHandler response_type not defined, updating response_type.");                         
                            trmd.response_type=String.valueOf(ros_message.response_type);
                            this.isChanged=true;
                        }
                    }
                
/////////////////////////////////////////////   
                
        // DEBUG caller
        //this.ros_message = decoder.getMessage(message,threadName);
/*
        if(ros_message.callerid!=null)
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
        */ 
/////////////////////////////////////////////////////////////////        
        //System.out.println(ROSMessageDecoder.convertHexToString(BytesToHex.bytesToHex(ros_message)));
        // If data is changed update the SubscriberMessageManager, and topic_registry.eax.
        if(isChanged)
        {
            try 
            {
                smm.add(trmd);
            } 
            catch (UnsupportedEncodingException ex) 
            {
                Logger.getLogger(DefaultInitializerMessageHandler.class.getName()).log(Level.SEVERE, "Exception adding message_definition, request_topic, or response_topic to SubscroberMessageManager.", ex);
            }
        }        
        // Set Message Pool Message Object Header Values:
        pool.setROSMessageHeaderInPooledMessages(ros_message);
        // Send ROSMessage to Perception Engine
        // Set tid
        setTID(trmd.tid);
        ros_message.hid=trmd.tid;
        if(spm!=null)
        {
            spm.process(ros_message);
        }
        else
        {
            this.temp_provider=smm.rosNode.getController().getPE().getProviderByName("ros_pe_provider");
            if(temp_provider!=null)
            {
                this.spm = temp_provider.getSensorProcessorManager();
                this.temp_provider=null;
            }
            if(spm!=null)
            {
                spm.process(ros_message);
            }
        }
        // Check the ROSMessage back into the ROSMessagePool.
        pool.checkin(ros_message.rosMessageID);
//        System.out.println("DATA: " + String.valueOf(HexStringConverter.bytesToHex(message)));
//        System.out.println("DefaultInitializerMessageHandler processing not implemented yet. Need to implement the SubscriberMessageManager definition.");
        // Set the trmd reference to null before preceding.
        this.trmd=null;
        // Change to the Base Message Handler.
        messageHandlerController.setUseMessageHandler();
    }

    @Override
    public void setMessageHandlerController(MessageHandlerControllerInterface messageHandlerController) {
        this.messageHandlerController=messageHandlerController;
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

    
    // TODO: remove ROSMessage Decoder and comment out system 
    ROSMessageDecoder decode = new ROSMessageDecoder();      
    // TODO: Implement this method using dataLength if data is attached to header.
    @Override
    public void process(byte[] message, int dataLength) 
    {
//System.out.println("Hex Message:" + org.happy.artist.rmdmia.utilities.BytesToHex.bytesToHex(message));        
//System.out.println("Message: "  + decode.convertHexToString(org.happy.artist.rmdmia.utilities.BytesToHex.bytesToHex(message).toCharArray()));          
//        System.out.println("Calling DefaultInitializerMessageHandler.");  
        this.isChanged=false;
        this.ros_message = decoder.getInitializationMessage(message,topic,isTopic);
        // TODO: Update the message_definition in the configuration if it does 
        // not exist, or throw an exception if it does not match. Contemplate an 
        // option to update the message_definition automatically, if the version does not match).
        // Figure out how to do this and notify user. Prompt user via ROCI GUI, to update if message_definition does not match.
        if(ros_message.message_definition!=null&&!ros_message.message_definition.equals(EMPTY_STRING))
        {                      
            if(ros_message.isTopic&&ros_message.topic!=null)
            {
                    // Topic
                    this.trmd=smm.getTopicRegistryMessageDefinitionByTID(smm.getTIDByTopicName(String.valueOf(ros_message.topic)));            
             
            }
            else if(ros_message.service!=null)
            {
                    //Service
                    this.trmd=smm.getTopicRegistryMessageDefinitionByTID(smm.getTIDByServiceName(String.valueOf(ros_message.service)));

            }
                              
            
            if(!trmd.definition.contains(java.nio.CharBuffer.wrap(ros_message.message_definition)))
            {
                System.out.println("DefaultInitializerMessageHandler message_definition not defined, updating message_definition."); 
                trmd.definition=String.valueOf(ros_message.message_definition);
                this.isChanged=true;
            }
//            else
//            {
//                System.out.println("DefaultInitializerMessageHandler message_definition already defined."); 
//            }
        }
 //       else
//        {
            // Log warning message that message_definition not found in connection header. Will not be an issue if user has manually defined the message_definition in the UI, or configuration files.
//            if(ros_message.isTopic&&ros_message.topic!=null)
//            {
//                System.out.println("WARNING: message_definition not found in connection header of topic: " + String.valueOf(ros_message.topic));
//            }
//            else if(ros_message.service!=null)
//            {
//                System.out.println("WARNING: message_definition not found in connection header of service: " + String.valueOf(ros_message.service));                
//            }
//        }
//TODO: 11/23/2013 Add request_topic and response_topic support
       // System.out.println(ros_message.request_type);
       // System.out.println(ros_message.response_type);   
        
                    if(ros_message.request_type!=null&&!ros_message.request_type.equals(EMPTY_STRING))
                    {
                        if(ros_message.service!=null&&trmd==null)
                        {
                                //Service
                                this.trmd=smm.getTopicRegistryMessageDefinitionByTID(smm.getTIDByServiceName(String.valueOf(ros_message.service)));

                        }                        
                        if(!trmd.request_type.contains(java.nio.CharBuffer.wrap(ros_message.request_type)))
                        {
                            System.out.println("DefaultInitializerMessageHandler request_type not defined, updating request_type.");                         
                            trmd.request_type=String.valueOf(ros_message.request_type);
                            this.isChanged=true;
                        }
                    }
                            

                    if(ros_message.response_type!=null&&!ros_message.response_type.equals(EMPTY_STRING))
                    {
                        if(ros_message.service!=null&&trmd==null)
                        {
                                //Service
                                this.trmd=smm.getTopicRegistryMessageDefinitionByTID(smm.getTIDByServiceName(String.valueOf(ros_message.service)));

                        }                             
                        if(!trmd.response_type.contains(java.nio.CharBuffer.wrap(ros_message.response_type)))
                        {
                            System.out.println("DefaultInitializerMessageHandler response_type not defined, updating response_type.");                         
                            trmd.response_type=String.valueOf(ros_message.response_type);
                            this.isChanged=true;
                        }
                    }        
        /*
/////////////////////////////////////
                           if(ros_message.request_type!=null&&!ros_message.request_type.equals(EMPTY_STRING))
                    {
                        System.out.println("INSIDE");
                        if(!trmd.request_type.contains(java.nio.CharBuffer.wrap(ros_message.request_type)))
                        {
                            System.out.println("DefaultInitializerMessageHandler request_type not defined, updating request_type.");                         
                            trmd.request_type=String.valueOf(ros_message.request_type);
                            try 
                            {
                                System.out.println("ADD");
                                smm.add(trmd);
                            } 
                            catch (UnsupportedEncodingException ex) 
                            {
                                Logger.getLogger(DefaultInitializerMessageHandler.class.getName()).log(Level.SEVERE, null, ex);
                            }                      
                        }
                    }
                            

                    if(ros_message.response_type!=null&&!ros_message.response_type.equals(EMPTY_STRING))
                    {
                        if(!trmd.response_type.contains(java.nio.CharBuffer.wrap(ros_message.response_type)))
                        {
                            System.out.println("DefaultInitializerMessageHandler response_type not defined, updating response_type.");                         
                            trmd.response_type=String.valueOf(ros_message.response_type);
                            try 
                            {
                                System.out.println("ADD");
                                smm.add(trmd);
                            } 
                            catch (UnsupportedEncodingException ex) 
                            {
                                Logger.getLogger(DefaultInitializerMessageHandler.class.getName()).log(Level.SEVERE, null, ex);
                            }                      
                        }
                    }
                
/////////////////////////////////////////////   
 */        
        /*
        if(ros_message.callerid!=null)
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
            System.out.println("binary_body(rmdmia)="+ ros_message.binary_body);         
        }
        System.out.println("error(rmdmia?)="+ ros_message.error);  
        */
/////////////////////////////////////////////////////////////////        
        // If data is changed update the SubscriberMessageManager, and topic_registry.eax.
        if(isChanged)
        {
            try 
            {
                smm.add(trmd);
            } 
            catch (UnsupportedEncodingException ex) 
            {
                Logger.getLogger(DefaultInitializerMessageHandler.class.getName()).log(Level.SEVERE, "Exception adding message_definition, request_topic, or response_topic to SubscroberMessageManager.", ex);
            }
        }  
        // Set Message Pool Message Object Header Values:
        pool.setROSMessageHeaderInPooledMessages(ros_message);
        // Send ROSMessage to Perception Engine
        // Set tid
        setTID(trmd.tid);
        ros_message.hid=trmd.tid;
        if(spm!=null)
        {
            spm.process(ros_message);
        }
        else
        {
            this.temp_provider=smm.rosNode.getController().getPE().getProviderByName("ros_pe_provider");
            if(temp_provider!=null)
            {
                this.spm = temp_provider.getSensorProcessorManager();
                this.temp_provider=null;
            }
            if(spm!=null)
            {
                spm.process(ros_message);
            }
        }        
        // Check the ROSMessage back into the ROSMessagePool.
        pool.checkin(ros_message.rosMessageID);
//        System.out.println("DATA: " + String.valueOf(HexStringConverter.bytesToHex(message)));
//        System.out.println("DefaultInitializerMessageHandler processing not implemented yet. Need to implement the SubscriberMessageManager definition.");
        // Set the trmd reference to null before preceding.
        this.trmd=null;        
        // Change to the Base Message Handler.
        messageHandlerController.setUseMessageHandler();

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
        if(getDataProcessor() instanceof TCPDataProcessor)
        {
            ((TCPDataProcessor)getDataProcessor()).getMessageHandler().setTID(tid);
            ((TCPDataProcessor)getDataProcessor()).getUseTunerMessageHandler().setTID(tid);              }
        else if(getDataProcessor() instanceof UDPDataProcessor)
        {
            ((UDPDataProcessor)getDataProcessor()).getMessageHandler().setTID(tid);
            ((UDPDataProcessor)getDataProcessor()).getUseTunerMessageHandler().setTID(tid);              }        
    }    
}
