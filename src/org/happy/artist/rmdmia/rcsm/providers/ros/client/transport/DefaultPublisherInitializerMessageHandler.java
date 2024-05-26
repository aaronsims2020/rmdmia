package org.happy.artist.rmdmia.rcsm.providers.ros.client.transport;

import org.happy.artist.rmdmia.rcsm.provider.message.DataHandlerInterface;
import org.happy.artist.rmdmia.rcsm.provider.message.MessageHandlerInterface;
import java.io.IOException;
import org.happy.artist.rmdmia.rcsm.provider.message.MessageHandlerControllerInterface;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.happy.artist.rmdmia.rcsm.provider.CommunicationSenderInterface;
import org.happy.artist.rmdmia.rcsm.provider.message.DataProcessor;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.ROSMessageDecoder;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.ROSSubscriberMessageManager;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.ROSTopicRegistryMessageDefinition;
import org.happy.artist.rmdmia.rcsm.providers.ros.ROSMessage;
import org.happy.artist.rmdmia.rcsm.providers.ros.ROSMessagePool;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.tcpros.TCPDataProcessor;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.tcpros.TCPROSPublisherCommunicator;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.udpros.UDPDataProcessor;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.udpros.UDPROSSubscriberCommunicator;
import org.happy.artist.rmdmia.utilities.HexStringConverter;


/** DefaultPublisherInitializerMessageHandler.java The default ROS Initializer 
 * Message Handler. This message handler obtains the message definition, and 
 * gives a warning if the definition is changed. If the message definition did 
 * not exist it is saved to the topic_registry configuration file, and the 
 * message is directed to its next destination (RCSM Sensor Queues).
 *
 * @author Happy Artist
 * 
 * @copyright Copyright© 2013-2014 Happy Artist. All rights reserved.
 */
public class DefaultPublisherInitializerMessageHandler implements MessageHandlerInterface
{
    private ROSMessageDecoder decoder = new ROSMessageDecoder();
    private ROSMessage ros_message;
    private String threadName;
    // TODO: Make the ROSMessagePoolFactory size definable in properties at the 
    // topic/service level.
    private ROSMessagePool pool = ROSMessagePool.getInstance();  
    // TODO: remove this from here, and place in Tuner, or implement a flag that only updates if enabled to do so.
//    private final String smmLoadedEarly="SubscriberMessageManager_Prematurely Loaded.";
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
    private boolean latching;

    public DefaultPublisherInitializerMessageHandler() {
        try {
            this.smm = ROSSubscriberMessageManager.getInstance("smmLoadedEarly",null);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DefaultPublisherInitializerMessageHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void process(byte[] message) 
    {
//System.out.println("Hex Message:" + org.happy.artist.rmdmia.utilities.BytesToHex.bytesToHex(message));        
//System.out.println(threadName + " Publisher Message received: "  + decode.convertHexToString(org.happy.artist.rmdmia.utilities.BytesToHex.bytesToHex(message).toCharArray()));          
        this.isChanged=false;
        //System.out.println("Calling DefaultPublisherInitializerMessageHandler."); 
//        System.out.println(threadName + " Generating ros_message (if inputs match the issue is out of scope variables in ros_message..., topic: " + topic);
        this.ros_message = decoder.getInitializationMessage(message,topic,isTopic);
        // TODO: Update the message_definition in the configuration if it does 
        // not exist, or throw an exception if it does not match. Contemplate an 
        // option to update the message_definition automatically, if the version does not match).
        // Figure out how to do this and notify user. Prompt user via ROCI GUI, to update if message_definition does not match.
        if(ros_message.md5sum==null||ros_message.md5sum.equals(EMPTY_STRING))
        {   
//                System.out.println(threadName + " DefaultPublisherInitializerMessageHandler missing md5sum, need to implement send error message."); 
// TODO: Add error code response, and then disconnect Remove Socket.        
            byte[] bytes=null;                        
            try 
            {
//                System.out.println(threadName + " smm.getTIDByTopicName(getTopic()):" + smm.getTIDByTopicName(getTopic()));
                bytes = smm.getAvailablePublisherMessages()[smm.getTIDByTopicName(getTopic())-1].updateError("md5sum missing in connection header.").getMessage();
                
            } 
            catch (UnsupportedEncodingException ex) 
            {
                Logger.getLogger(DefaultPublisherInitializerMessageHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            try 
            {
                // md5sum matches, topic matches
                senderInterface.send(bytes,((TCPDataProcessor)dataProcessor).data_processor_id);                
            } 
            catch (IOException ex) 
            {
                Logger.getLogger(DefaultPublisherInitializerMessageHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
                // remove subscriber connection on error.
                 if(dataProcessor.getClassname().indexOf("TCPDataProcessor")!=-1)
                 {
                    ((TCPROSPublisherCommunicator)senderInterface).removeSubscriber(((TCPDataProcessor)dataProcessor).data_processor_id);
                 }
        }
        
        else if(ros_message.md5sum!=null&&!ros_message.md5sum.equals(EMPTY_STRING))
        {               
            // Topic
            this.trmd=smm.getTopicRegistryMessageDefinitionByTID(smm.getTIDByTopicName(getTopic()));  
            if(trmd.latching.indexOf("1")!=-1&&((TCPROSPublisherCommunicator)senderInterface).last_msg.length>0)
            {
                this.latching=true;
            }
            else
            {
                this.latching=false;
            }
// TODO: implement TCP_NODELAY, and other subscriber options.  
            if("1".contains(java.nio.CharBuffer.wrap(ros_message.tcp_nodelay))&&dataProcessor.getClassname().indexOf("TCPDataProcessor")!=-1)
            {
                try 
                {
                    ((TCPDataProcessor)dataProcessor).getSocket().setTcpNoDelay(true);
                } 
                catch (SocketException ex) 
                {
                    Logger.getLogger(DefaultPublisherInitializerMessageHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else if("0".contains(java.nio.CharBuffer.wrap(ros_message.tcp_nodelay))&&dataProcessor.getClassname().indexOf("TCPDataProcessor")!=-1)
            {
                try 
                {
                    ((TCPDataProcessor)dataProcessor).getSocket().setTcpNoDelay(false);
                } 
                catch (SocketException ex) 
                {
                    Logger.getLogger(DefaultPublisherInitializerMessageHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }            
 //           System.out.println(threadName + " Approaching if statement to call send in response to Sub request.");
            if(trmd.md5sum.contains(java.nio.CharBuffer.wrap(ros_message.md5sum))&&trmd.topic.contains(java.nio.CharBuffer.wrap(ros_message.topic)))
            {
//                  System.out.println("Inside if statement to call send in response to Sub request.");
//                  System.out.println("in (ros_message): md5sum:" + ros_message.md5sum +", out md5sum: " + trmd.md5sum + ", in topic (ros_message):" + ros_message.topic + ", out (trmd) topic:" + trmd.topic + "topic variable:" + topic);
                try {
                    // latching code
                    byte[] bytes;
                    if(!latching&&trmd.latching.indexOf("1")!=-1)
                    {
                        bytes = smm.getAvailablePublisherMessages()[smm.getTIDByTopicName(getTopic())-1].updateLatching(false).getMessage();                        
                    }
                    else
                    {
                        bytes = smm.getAvailablePublisherMessages()[smm.getTIDByTopicName(getTopic())-1].getMessage();
                    }
                    // md5sum matches, topic matches
                    senderInterface.send(bytes,((TCPDataProcessor)dataProcessor).data_processor_id);
                    // if latching=true send last message
                    if(latching&&dataProcessor.getClassname().indexOf("TCPDataProcessor")!=-1)
                    {
//                        System.out.println(threadName + " Latching is true, sending latching message: " + decode.convertHexToString(org.happy.artist.rmdmia.utilities.BytesToHex.bytesToHex(((TCPROSPublisherCommunicator)senderInterface).last_msg).toCharArray()) + ", data processor id: " + ((TCPDataProcessor)dataProcessor).data_processor_id);
                        ((TCPROSPublisherCommunicator)senderInterface).send(((TCPROSPublisherCommunicator)senderInterface).last_msg,((TCPDataProcessor)dataProcessor).data_processor_id);
                    }
  // TODO: Implement UDP Latching support...
//                    else if(trmd.latching.indexOf("1")!=-1&&dataProcessor.getClassname().indexOf("UDPDataProcessor")!=-1)
//                    {
//                        ((UDPROSSubscriberCommunicator)senderInterface).send(((UDPROSSubscriberCommunicator)senderInterface).last_msg,((UDPDataProcessor)dataProcessor).data_processor_id);                        
  //                  }
//                    System.out.println(threadName + " Publisher Response Message sent: "  + decode.convertHexToString(org.happy.artist.rmdmia.utilities.BytesToHex.bytesToHex(bytes).toCharArray()));  
                } catch (IOException ex) {
                    Logger.getLogger(DefaultPublisherInitializerMessageHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
//                 System.out.println("DefaultPublisherInitializerMessageHandler sending subscriber response message.");               
            }
            else
            {
//                System.out.println(threadName + " DefaultPublisherInitializerMessageHandler md5sum, or type does not match, need to implement send error message.\ntrmd.md5sum=" + trmd.md5sum + ", ros_message.md5sum="+ String.valueOf(ros_message.md5sum) + ", trmd.topic=" + trmd.topic + ", ros_message.topic=" + String.valueOf(ros_message.topic)); 
                byte[] bytes=null;                        
                try 
                {
                    bytes = smm.getAvailablePublisherMessages()[smm.getTIDByTopicName(getTopic())-1].updateError("md5sum or topic does not match.").getMessage();
 //               System.out.println(threadName + " smm.getTIDByTopicName(getTopic()):" + smm.getTIDByTopicName(getTopic()));                    
                } 
                catch (UnsupportedEncodingException ex) 
                {
                    Logger.getLogger(DefaultPublisherInitializerMessageHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
                try 
                {
                    // md5sum or topic does not match send message.
                    senderInterface.send(bytes,((TCPDataProcessor)dataProcessor).data_processor_id);                
                } 
                catch (IOException ex) 
                {
                    Logger.getLogger(DefaultPublisherInitializerMessageHandler.class.getName()).log(Level.SEVERE, null, ex);
                }                
               
                // remove subscriber connection on error.
                 if(dataProcessor.getClassname().indexOf("TCPDataProcessor")!=-1)
                 {
                    ((TCPROSPublisherCommunicator)senderInterface).removeSubscriber(((TCPDataProcessor)dataProcessor).data_processor_id);
                 }
            }

        }

        // Set Message Pool Message Object Header Values:
        pool.setROSMessageHeaderInPooledMessages(ros_message);
        // Check the ROSMessage back into the ROSMessagePool.
        pool.checkin(ros_message.rosMessageID);
//        System.out.println("DATA: " + String.valueOf(HexStringConverter.bytesToHex(message)));
//        System.out.println("DefaultPublisherInitializerMessageHandler processing not implemented yet. Need to implement the SubscriberMessageManager definition.");
        // Set the trmd reference to null before preceding.
        this.trmd=null;
        // Change to the Base Message Handler.
        messageHandlerController.setUseMessageHandler();
        
        // DEBUG
 //       for(int i =0;i<smm.getAvailablePublisherMessages().length;i++)
 //       {
 //           System.out.println("Publisher Messages Type: " + smm.getAvailablePublisherMessages()[i].type + ", iteration: " + i);
   //     }
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
//System.out.println(threadName + " Publisher Message received: "  + decode.convertHexToString(org.happy.artist.rmdmia.utilities.BytesToHex.bytesToHex(message).toCharArray()));          
        this.isChanged=false;
        //System.out.println("Calling DefaultPublisherInitializerMessageHandler."); 
//        System.out.println(threadName + " Generating ros_message (if inputs match the issue is out of scope variables in ros_message..., topic: " + topic);
        this.ros_message = decoder.getInitializationMessage(message,topic,isTopic);
        // TODO: Update the message_definition in the configuration if it does 
        // not exist, or throw an exception if it does not match. Contemplate an 
        // option to update the message_definition automatically, if the version does not match).
        // Figure out how to do this and notify user. Prompt user via ROCI GUI, to update if message_definition does not match.
        if(ros_message.md5sum==null||ros_message.md5sum.equals(EMPTY_STRING))
        {   
            // TODO: Send an Error message missing md5sum
 //               System.out.println(threadName + " DefaultPublisherInitializerMessageHandler missing md5sum, need to implement send error message."); 
// TODO: Add error code response, and then disconnect Remove Socket.        
            byte[] bytes=null;                        
            try 
            {
 //               System.out.println(threadName + " smm.getTIDByTopicName(getTopic()):" + smm.getTIDByTopicName(getTopic()));
                bytes = smm.getAvailablePublisherMessages()[smm.getTIDByTopicName(getTopic())-1].updateError("md5sum missing in connection header.").getMessage();
                
            } 
            catch (UnsupportedEncodingException ex) 
            {
                Logger.getLogger(DefaultPublisherInitializerMessageHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            try 
            {
                // md5sum matches, topic matches
                senderInterface.send(bytes,((TCPDataProcessor)dataProcessor).data_processor_id);                
            } 
            catch (IOException ex) 
            {
                Logger.getLogger(DefaultPublisherInitializerMessageHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
                // remove subscriber connection on error.
                 if(dataProcessor.getClassname().indexOf("TCPDataProcessor")!=-1)
                 {
                    ((TCPROSPublisherCommunicator)senderInterface).removeSubscriber(((TCPDataProcessor)dataProcessor).data_processor_id);
                 }
        }
        
        else if(ros_message.md5sum!=null&&!ros_message.md5sum.equals(EMPTY_STRING))
        {               
            // Topic
            this.trmd=smm.getTopicRegistryMessageDefinitionByTID(smm.getTIDByTopicName(getTopic()));  
            if(trmd.latching.indexOf("1")!=-1&&((TCPROSPublisherCommunicator)senderInterface).last_msg.length>0)
            {
                this.latching=true;
            }
            else
            {
                this.latching=false;
            }
// TODO: implement TCP_NODELAY, and other subscriber options.  
            if("1".contains(java.nio.CharBuffer.wrap(ros_message.tcp_nodelay))&&dataProcessor.getClassname().indexOf("TCPDataProcessor")!=-1)
            {
                try 
                {
                    ((TCPDataProcessor)dataProcessor).getSocket().setTcpNoDelay(true);
                } 
                catch (SocketException ex) 
                {
                    Logger.getLogger(DefaultPublisherInitializerMessageHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else if("0".contains(java.nio.CharBuffer.wrap(ros_message.tcp_nodelay))&&dataProcessor.getClassname().indexOf("TCPDataProcessor")!=-1)
            {
                try 
                {
                    ((TCPDataProcessor)dataProcessor).getSocket().setTcpNoDelay(false);
                } 
                catch (SocketException ex) 
                {
                    Logger.getLogger(DefaultPublisherInitializerMessageHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }            
   //         System.out.println(threadName + " Approaching if statement to call send in response to Sub request.");
            if(trmd.md5sum.contains(java.nio.CharBuffer.wrap(ros_message.md5sum))&&trmd.topic.contains(java.nio.CharBuffer.wrap(ros_message.topic)))
            {
//                  System.out.println("Inside if statement to call send in response to Sub request.");
//                  System.out.println("in (ros_message): md5sum:" + ros_message.md5sum +", out md5sum: " + trmd.md5sum + ", in topic (ros_message):" + ros_message.topic + ", out (trmd) topic:" + trmd.topic + "topic variable:" + topic);
                try {
                    // latching code
                    byte[] bytes;
                    if(!latching&&trmd.latching.indexOf("1")!=-1)
                    {
                        bytes = smm.getAvailablePublisherMessages()[smm.getTIDByTopicName(getTopic())-1].updateLatching(false).getMessage();                        
                    }
                    else
                    {
                        bytes = smm.getAvailablePublisherMessages()[smm.getTIDByTopicName(getTopic())-1].getMessage();
                    }
                    // md5sum matches, topic matches
                    senderInterface.send(bytes,((TCPDataProcessor)dataProcessor).data_processor_id);
                    // if latching=true send last message
                    if(latching&&dataProcessor.getClassname().indexOf("TCPDataProcessor")!=-1)
                    {
//                       System.out.println(threadName + " Latching is true, sending latching message: " + decode.convertHexToString(org.happy.artist.rmdmia.utilities.BytesToHex.bytesToHex(((TCPROSPublisherCommunicator)senderInterface).last_msg).toCharArray()) + ", data processor id: " + ((TCPDataProcessor)dataProcessor).data_processor_id);
                        ((TCPROSPublisherCommunicator)senderInterface).send(((TCPROSPublisherCommunicator)senderInterface).last_msg,((TCPDataProcessor)dataProcessor).data_processor_id);
                    }
  // TODO: Implement UDP Latching support...
//                    else if(trmd.latching.indexOf("1")!=-1&&dataProcessor.getClassname().indexOf("UDPDataProcessor")!=-1)
//                    {
//                        ((UDPROSSubscriberCommunicator)senderInterface).send(((UDPROSSubscriberCommunicator)senderInterface).last_msg,((UDPDataProcessor)dataProcessor).data_processor_id);                        
  //                  }
//                    System.out.println(threadName + " Publisher Response Message sent: "  + decode.convertHexToString(org.happy.artist.rmdmia.utilities.BytesToHex.bytesToHex(bytes).toCharArray()));  
                } catch (IOException ex) {
                    Logger.getLogger(DefaultPublisherInitializerMessageHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
//                 System.out.println("DefaultPublisherInitializerMessageHandler sending subscriber response message.");               
            }
            else
            {
//                System.out.println(threadName + " DefaultPublisherInitializerMessageHandler md5sum, or type does not match, need to implement send error message.\ntrmd.md5sum=" + trmd.md5sum + ", ros_message.md5sum="+ String.valueOf(ros_message.md5sum) + ", trmd.topic=" + trmd.topic + ", ros_message.topic=" + String.valueOf(ros_message.topic)); 
                byte[] bytes=null;                        
                try 
                {
                    bytes = smm.getAvailablePublisherMessages()[smm.getTIDByTopicName(getTopic())-1].updateError("md5sum or topic does not match.").getMessage();
 //               System.out.println(threadName + " smm.getTIDByTopicName(getTopic()):" + smm.getTIDByTopicName(getTopic()));                    
                } 
                catch (UnsupportedEncodingException ex) 
                {
                    Logger.getLogger(DefaultPublisherInitializerMessageHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
                try 
                {
                    // md5sum or topic does not match send message.
                    senderInterface.send(bytes,((TCPDataProcessor)dataProcessor).data_processor_id);                
                } 
                catch (IOException ex) 
                {
                    Logger.getLogger(DefaultPublisherInitializerMessageHandler.class.getName()).log(Level.SEVERE, null, ex);
                }                
               
                // remove subscriber connection on error.
                 if(dataProcessor.getClassname().indexOf("TCPDataProcessor")!=-1)
                 {
                    ((TCPROSPublisherCommunicator)senderInterface).removeSubscriber(((TCPDataProcessor)dataProcessor).data_processor_id);
                 }
            }

        }

        // Set Message Pool Message Object Header Values:
        pool.setROSMessageHeaderInPooledMessages(ros_message);
        // Check the ROSMessage back into the ROSMessagePool.
        pool.checkin(ros_message.rosMessageID);
//        System.out.println("DATA: " + String.valueOf(HexStringConverter.bytesToHex(message)));
//        System.out.println("DefaultPublisherInitializerMessageHandler processing not implemented yet. Need to implement the SubscriberMessageManager definition.");
        // Set the trmd reference to null before preceding.
        this.trmd=null;
        // Change to the Base Message Handler.
        messageHandlerController.setUseMessageHandler();
        
        // DEBUG
 //       for(int i =0;i<smm.getAvailablePublisherMessages().length;i++)
 //       {
 //           System.out.println("Publisher Messages Type: " + smm.getAvailablePublisherMessages()[i].type + ", iteration: " + i);
   //     }
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
