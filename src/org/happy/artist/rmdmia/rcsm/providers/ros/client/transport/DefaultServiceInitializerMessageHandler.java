package org.happy.artist.rmdmia.rcsm.providers.ros.client.transport;

import org.happy.artist.rmdmia.rcsm.provider.message.DataHandlerInterface;
import org.happy.artist.rmdmia.rcsm.provider.message.MessageHandlerInterface;
import org.happy.artist.rmdmia.rcsm.provider.message.MessageHandlerControllerInterface;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.happy.artist.rmdmia.rcsm.provider.CommunicationSenderInterface;
import org.happy.artist.rmdmia.rcsm.provider.message.DataProcessor;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.ROSMessageDecoder;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.ROSSubscriberMessageManager;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.ROSTopicRegistryMessageDefinition;
import org.happy.artist.rmdmia.rcsm.providers.ros.ROSMessage;
import org.happy.artist.rmdmia.rcsm.providers.ros.ROSMessagePool;
import org.happy.artist.rmdmia.rcsm.providers.ros.ROSNode;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.tcpros.TCPDataProcessor;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.udpros.UDPDataProcessor;
import org.happy.artist.rmdmia.utilities.HexStringConverter;

/**
 * DefaultServiceInitializerMessageHandler.java The default ROS Service
 * Initializer Message Handler. This message handler obtains the message
 * definition, and gives a warning if the definition is changed. If the message
 * definition did not exist it is saved to the topic_registry configuration file
 * (The topic registry message definition stuff does not apply to Services, and
 * this overview must be updated... message is directed to its next destination
 * defaulting to (RCSM Sensor Queues).
 *
 * @author Happy Artist
 *
 * @copyright Copyright© 2013-2014 Happy Artist. All rights reserved.
 */
public class DefaultServiceInitializerMessageHandler implements MessageHandlerInterface {

    private ROSMessageDecoder decoder = new ROSMessageDecoder();
    private ROSMessage ros_message;
    private String threadName;
    // TODO: Make the ROSMessagePoolFactory size definable in properties at the 
    // topic/service level.
    private ROSMessagePool pool = ROSMessagePool.getInstance();
    // TODO: remove this from here, and place in Tuner, or implement a flag that only updates if enabled to do so.
    private final String smmLoadedEarly = "SubscriberMessageManager_Prematurely Loaded.";
    // Update the SusbscriberMessageManager with the message_definition information.
    private ROSSubscriberMessageManager smm;
    // Allows the MessageHandler to change the Message Handler on the fly.
    private MessageHandlerControllerInterface messageHandlerController;
    // Empty String for an equals compare using final static
    private final static String EMPTY_STRING = "";
    private ROSTopicRegistryMessageDefinition trmd;
    // call private add if the following variable is set to true.
    private boolean isChanged;
    // set CommunicationSenderInterface
    private CommunicationSenderInterface senderInterface;
    // Attempt to probe for m5dsum on mismatch, and update the md5sum startup configuration.
    private boolean attempt_probe = ROSNode.attempt_probe_save_on_md5_mismatch;

    public DefaultServiceInitializerMessageHandler() {
        try {
            this.smm = ROSSubscriberMessageManager.getInstance("smmLoadedEarly", null);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DefaultServiceInitializerMessageHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // words String array is used in parsing out md5sums from a ROS error message.
    private String[] words;
    // wordCount is the while loop iteration count (of word elements)
    private int wordCount;
    // The valid md5hash
    private String md5hash;
    
    @Override
    public void process(byte[] message) {
//       System.out.println("Hex Message:" + org.happy.artist.rmdmia.utilities.BytesToHex.bytesToHex(message));
//System.out.println("Message Init: "  + decode.convertHexToString(org.happy.artist.rmdmia.utilities.BytesToHex.bytesToHex(message).toCharArray()));          
        this.isChanged = false;
//        System.out.println("Calling DefaultServiceInitializerMessageHandler.");        
        this.ros_message = decoder.getInitializationMessage(message, topic, isTopic);
        
        // TODO: if a ros_message error exists, and attempt_probe is enabled, check to see if the error contains the correct md5. This would be the result of a probe with an empty String or mismatch. 
        if(attempt_probe==true&&ros_message.error!=null)
        {
            if(ros_message.error.indexOf("md5sum")!=-1)
            {
                // Split the error message into words for processing work lengths
                // Check the error message for 2 md5 hashes (a hash mismatch)
                // If only one md5 hash exists... (an empty hash)            
                this.words = ros_message.error.split(" ");
                this.wordCount=0;
                this.md5hash=null;
                while(wordCount<words.length)
                {
                    // Length is 33 because both hashes contain an additional character before the space character used to split the words. The final character will be stripped later.
                    if(words[wordCount].length()==33)
                    {
                        // Found a md5hash.
                        this.md5hash=words[wordCount].substring(0, 32);
                    }
                    // iterate the loop count.
                    this.wordCount=wordCount + 1;
                }
                if(md5hash!=null)
                {
                    System.out.println("DefaultServiceInitializerMessageHandler md5sum mismatch, or not defined, updating md5sum.");
                    if (ros_message.isTopic) {
                        if (ros_message.topic != null) {
                            // Topic
                            this.trmd = smm.getTopicRegistryMessageDefinitionByTID(smm.getTIDByTopicName(String.valueOf(ros_message.topic)));
                        }
                    } else {
                        if (ros_message.service != null) {
                            //Service
                            this.trmd = smm.getTopicRegistryMessageDefinitionByTID(smm.getTIDByServiceName(String.valueOf(ros_message.service)));
                        }
                    }                    
                    trmd.md5sum = md5hash;
                    this.isChanged = true;                
                }
            }
            // If Hash is not null, send a message to the message sender it is OK to send message... 
            // TODO: Save the new md5sum, and then notify sender.
            
            
        }
        // TODO: Update the message_definition in the configuration if it does 
        // not exist, or throw an exception if it does not match. Contemplate an 
        // option to update the message_definition automatically, if the version does not match).
        // Figure out how to do this and notify user. Prompt user via ROCI GUI, to update if message_definition does not match.
        if (ros_message.message_definition != null && !ros_message.message_definition.equals(EMPTY_STRING)) {
            if (trmd==null&&ros_message.isTopic) {
                if (ros_message.topic != null) {
                    // Topic
                    this.trmd = smm.getTopicRegistryMessageDefinitionByTID(smm.getTIDByTopicName(String.valueOf(ros_message.topic)));
                }
            } else {
                if (trmd==null&&ros_message.service != null) {
                    //Service
                    this.trmd = smm.getTopicRegistryMessageDefinitionByTID(smm.getTIDByServiceName(String.valueOf(ros_message.service)));
                }
            }

            if (!trmd.definition.contains(java.nio.CharBuffer.wrap(ros_message.message_definition))) {
                System.out.println("DefaultServiceInitializerMessageHandler message_definition not defined, updating message_definition.");
                trmd.definition = String.valueOf(ros_message.message_definition);
                this.isChanged = true;
            }
            //          else
//            {
//                System.out.println("DefaultServiceInitializerMessageHandler message_definition already defined."); 
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
        if (ros_message.request_type != null && !ros_message.request_type.equals(EMPTY_STRING)) {
            if (ros_message.service != null && trmd == null) {
                //Service
                this.trmd = smm.getTopicRegistryMessageDefinitionByTID(smm.getTIDByServiceName(String.valueOf(ros_message.service)));

            }
            if (!trmd.request_type.contains(java.nio.CharBuffer.wrap(ros_message.request_type))) {
                System.out.println("DefaultServiceInitializerMessageHandler request_type not defined, updating request_type.");
                trmd.request_type = String.valueOf(ros_message.request_type);
                this.isChanged = true;
            }
        }


        if (ros_message.response_type != null && !ros_message.response_type.equals(EMPTY_STRING)) {
            if (ros_message.service != null && trmd == null) {
                //Service
                this.trmd = smm.getTopicRegistryMessageDefinitionByTID(smm.getTIDByServiceName(String.valueOf(ros_message.service)));

            }
            if (!trmd.response_type.contains(java.nio.CharBuffer.wrap(ros_message.response_type))) {
                System.out.println("DefaultServiceInitializerMessageHandler response_type not defined, updating response_type.");
                trmd.response_type = String.valueOf(ros_message.response_type);
                this.isChanged = true;
            }
        }

        // type field        
        if (ros_message.type != null && !ros_message.type.equals(EMPTY_STRING)) {
            if (ros_message.service != null && trmd == null) {
                //Service
                this.trmd = smm.getTopicRegistryMessageDefinitionByTID(smm.getTIDByServiceName(String.valueOf(ros_message.service)));

            }
            if (!trmd.type.contains(java.nio.CharBuffer.wrap(ros_message.type))) {
                System.out.println("DefaultServiceInitializerMessageHandler type not defined, updating type.");
                trmd.type = String.valueOf(ros_message.type);
                this.isChanged = true;
            }
        }    
        
        // md5sum field        
        if (ros_message.error==null && ros_message.md5sum != null && !ros_message.md5sum.equals(EMPTY_STRING)) {
            if (ros_message.service != null && trmd == null) {
                //Service
                this.trmd = smm.getTopicRegistryMessageDefinitionByTID(smm.getTIDByServiceName(String.valueOf(ros_message.service)));

            }
            if (!trmd.md5sum.contains(java.nio.CharBuffer.wrap(ros_message.md5sum))) {
                System.out.println("DefaultServiceInitializerMessageHandler md5sum not defined, updating md5sum.");
                trmd.md5sum = String.valueOf(ros_message.md5sum);
                this.isChanged = true;
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
        if (isChanged) {
            try {
                smm.add(trmd);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(DefaultServiceInitializerMessageHandler.class.getName()).log(Level.SEVERE, "Exception adding message_definition, request_topic, or response_topic to SubscroberMessageManager.", ex);
            }
        }
        // Set Message Pool Message Object Header Values:
        pool.setROSMessageHeaderInPooledMessages(ros_message);
        // Check the ROSMessage back into the ROSMessagePool.
        pool.checkin(ros_message.rosMessageID);
//        System.out.println("DATA: " + String.valueOf(HexStringConverter.bytesToHex(message)));
//        System.out.println("DefaultServiceInitializerMessageHandler processing not implemented yet. Need to implement the SubscriberMessageManager definition.");
        // Set the trmd reference to null before preceding.
        this.trmd = null;
        // Change to the Base Message Handler.
        messageHandlerController.setUseMessageHandler();
    }

    @Override
    public void setMessageHandlerController(MessageHandlerControllerInterface messageHandlerController) {
        this.messageHandlerController = messageHandlerController;
    }

    @Override
    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    @Override
    public String getThreadName() {
        return threadName;
    }
    // TODO: remove ROSMessage Decoder and comment out system 
    ROSMessageDecoder decode = new ROSMessageDecoder();
    // TODO: Implement this method using dataLength if data is attached to header.

    @Override
    public void process(byte[] message, int dataLength) {
//        System.out.println("Hex Message:" + org.happy.artist.rmdmia.utilities.BytesToHex.bytesToHex(message));
//System.out.println("Message Init: "  + decode.convertHexToString(org.happy.artist.rmdmia.utilities.BytesToHex.bytesToHex(message).toCharArray()));          
        this.isChanged = false;
//        System.out.println("Calling DefaultServiceInitializerMessageHandler.");        
        this.ros_message = decoder.getInitializationMessage(message, topic, isTopic);
        
        // TODO: if a ros_message error exists, and attempt_probe is enabled, check to see if the error contains the correct md5. This would be the result of a probe with an empty String or mismatch. 
        if(attempt_probe==true&&ros_message.error!=null)
        {
            if(ros_message.error.indexOf("md5sum")!=-1)
            {
                // Split the error message into words for processing work lengths
                // Check the error message for 2 md5 hashes (a hash mismatch)
                // If only one md5 hash exists... (an empty hash)            
                this.words = ros_message.error.split(" ");
                this.wordCount=0;
                this.md5hash=null;
                while(wordCount<words.length)
                {
                    // Length is 33 because both hashes contain an additional character before the space character used to split the words. The final character will be stripped later.                    
                    if(words[wordCount].length()==33)
                    {
                        // Found a md5hash.
                        this.md5hash=words[wordCount].substring(0, 32);
                    }
                    // iterate the loop count.
                    this.wordCount=wordCount + 1;
                }
                if(md5hash!=null)
                {
                    System.out.println("DefaultServiceInitializerMessageHandler md5sum mismatch, or not defined, updating md5sum.");
                    if (ros_message.isTopic) {
                        if (ros_message.topic != null) {
                            // Topic
                            this.trmd = smm.getTopicRegistryMessageDefinitionByTID(smm.getTIDByTopicName(String.valueOf(ros_message.topic)));
                        }
                    } else {
                        if (ros_message.service != null) {
                            //Service
                            this.trmd = smm.getTopicRegistryMessageDefinitionByTID(smm.getTIDByServiceName(String.valueOf(ros_message.service)));
                        }
                    }                    
                    trmd.md5sum = md5hash;
                    this.isChanged = true;                
                }
            }
            // If Hash is not null, send a message to the message sender it is OK to send message... 
            // TODO: Save the new md5sum, and then notify sender.
            
            
        }
        // TODO: Update the message_definition in the configuration if it does 
        // not exist, or throw an exception if it does not match. Contemplate an 
        // option to update the message_definition automatically, if the version does not match).
        // Figure out how to do this and notify user. Prompt user via ROCI GUI, to update if message_definition does not match.
        if (ros_message.message_definition != null && !ros_message.message_definition.equals(EMPTY_STRING)) {
            if (trmd==null&&ros_message.isTopic) {
                if (ros_message.topic != null) {
                    // Topic
                    this.trmd = smm.getTopicRegistryMessageDefinitionByTID(smm.getTIDByTopicName(String.valueOf(ros_message.topic)));
                }
            } else {
                if (trmd==null&&ros_message.service != null) {
                    //Service
                    this.trmd = smm.getTopicRegistryMessageDefinitionByTID(smm.getTIDByServiceName(String.valueOf(ros_message.service)));
                }
            }

            if (!trmd.definition.contains(java.nio.CharBuffer.wrap(ros_message.message_definition))) {
                System.out.println("DefaultServiceInitializerMessageHandler message_definition not defined, updating message_definition.");
                trmd.definition = String.valueOf(ros_message.message_definition);
                this.isChanged = true;
            }
            //          else
//            {
//                System.out.println("DefaultServiceInitializerMessageHandler message_definition already defined."); 
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
        if (ros_message.request_type != null && !ros_message.request_type.equals(EMPTY_STRING)) {
            if (ros_message.service != null && trmd == null) {
                //Service
                this.trmd = smm.getTopicRegistryMessageDefinitionByTID(smm.getTIDByServiceName(String.valueOf(ros_message.service)));

            }
            if (!trmd.request_type.contains(java.nio.CharBuffer.wrap(ros_message.request_type))) {
                System.out.println("DefaultServiceInitializerMessageHandler request_type not defined, updating request_type.");
                trmd.request_type = String.valueOf(ros_message.request_type);
                this.isChanged = true;
            }
        }


        if (ros_message.response_type != null && !ros_message.response_type.equals(EMPTY_STRING)) {
            if (ros_message.service != null && trmd == null) {
                //Service
                this.trmd = smm.getTopicRegistryMessageDefinitionByTID(smm.getTIDByServiceName(String.valueOf(ros_message.service)));

            }
            if (!trmd.response_type.contains(java.nio.CharBuffer.wrap(ros_message.response_type))) {
                System.out.println("DefaultServiceInitializerMessageHandler response_type not defined, updating response_type.");
                trmd.response_type = String.valueOf(ros_message.response_type);
                this.isChanged = true;
            }
        }

        // type field        
        if (ros_message.type != null && !ros_message.type.equals(EMPTY_STRING)) {
            if (ros_message.service != null && trmd == null) {
                //Service
                this.trmd = smm.getTopicRegistryMessageDefinitionByTID(smm.getTIDByServiceName(String.valueOf(ros_message.service)));

            }
            if (!trmd.type.contains(java.nio.CharBuffer.wrap(ros_message.type))) {
                System.out.println("DefaultServiceInitializerMessageHandler type not defined, updating type.");
                trmd.type = String.valueOf(ros_message.type);
                this.isChanged = true;
            }
        }    
        
        // md5sum field        
        if (ros_message.error==null && ros_message.md5sum != null && !ros_message.md5sum.equals(EMPTY_STRING)) {
            if (ros_message.service != null && trmd == null) {
                //Service
                this.trmd = smm.getTopicRegistryMessageDefinitionByTID(smm.getTIDByServiceName(String.valueOf(ros_message.service)));

            }
            if (!trmd.md5sum.contains(java.nio.CharBuffer.wrap(ros_message.md5sum))) {
                System.out.println("DefaultServiceInitializerMessageHandler md5sum not defined, updating md5sum.");
                trmd.md5sum = String.valueOf(ros_message.md5sum);
                this.isChanged = true;
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
        if (isChanged) {
            try {
                smm.add(trmd);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(DefaultServiceInitializerMessageHandler.class.getName()).log(Level.SEVERE, "Exception adding message_definition, request_topic, or response_topic to SubscroberMessageManager.", ex);
            }
        }
        // Set Message Pool Message Object Header Values:
        pool.setROSMessageHeaderInPooledMessages(ros_message);
        // Check the ROSMessage back into the ROSMessagePool.
        pool.checkin(ros_message.rosMessageID);
//        System.out.println("DATA: " + String.valueOf(HexStringConverter.bytesToHex(message)));
//        System.out.println("DefaultServiceInitializerMessageHandler processing not implemented yet. Need to implement the SubscriberMessageManager definition.");
        // Set the trmd reference to null before preceding.
        this.trmd = null;
        // Change to the Base Message Handler.
        messageHandlerController.setUseMessageHandler();
    }
    private String topic = "";
    private boolean isTopic = true;

    @Override
    public void setTopic(String topic) {
        this.topic = topic;
    }

    @Override
    public String getTopic() {
        return topic;
    }

    @Override
    public void setIsTopic(boolean isTopic) {
        this.isTopic = isTopic;
    }

    @Override
    public boolean isTopic() {
        return isTopic;
    }

    public void setCommunicationSenderInterface(CommunicationSenderInterface senderInterface) {
        this.senderInterface = senderInterface;
    }

    public CommunicationSenderInterface getCommunicationSenderInterface() {
        return senderInterface;
    }
    private DataProcessor dataProcessor;

    @Override
    public void setDataProcessor(DataProcessor dataProcessor) {
        this.dataProcessor = dataProcessor;
    }

    @Override
    public DataProcessor getDataProcessor() {
        return dataProcessor;
    }

    @Override
    public void registerDataHandler(DataHandlerInterface dataHandler) throws UnsupportedOperationException {
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
