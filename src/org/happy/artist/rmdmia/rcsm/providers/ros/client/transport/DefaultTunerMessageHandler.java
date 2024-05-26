package org.happy.artist.rmdmia.rcsm.providers.ros.client.transport;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.happy.artist.rmdmia.perception.engine.PEProvider;
import org.happy.artist.rmdmia.perception.engine.sensor.SensorProcessorManager;
import org.happy.artist.rmdmia.rcsm.provider.message.DataHandlerInterface;
import org.happy.artist.rmdmia.rcsm.provider.message.MessageHandlerInterface;
import org.happy.artist.rmdmia.rcsm.provider.CommunicationSenderInterface;
import org.happy.artist.rmdmia.rcsm.provider.message.MessageHandlerControllerInterface;
import org.happy.artist.rmdmia.rcsm.provider.message.MessageHandlerTunerInterface;
import org.happy.artist.rmdmia.rcsm.provider.message.DataProcessor;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.ROSMessageDecoder;
import org.happy.artist.rmdmia.rcsm.providers.ros.ROSMessage;
import org.happy.artist.rmdmia.rcsm.providers.ros.ROSMessagePool;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.ROSSubscriberMessageManager;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.ROSTopicRegistryMessageDefinition;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.tcpros.TCPDataProcessor;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.udpros.UDPDataProcessor;

/** DefaultTunerMessageHandler.java The default automatic networking tuner 
 * ROS Message Handler. This handler is intended to direct the ROS Message to 
 * its next destination, and at the same time collect data to tune the most 
 * efficient UDP Packets & read array length for data processing (RCSM Sensor 
 * Queues). Due to the incoming data collection requiring more CPU overhead, 
 * this should only be used to optimize the data configuration, then switch back
 * to the DefaultMessageHandler.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright© 2013-2014 Happy Artist. All rights reserved.
 */
public class DefaultTunerMessageHandler implements MessageHandlerInterface, MessageHandlerTunerInterface
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
    // process method variables
    private int maxReceivedDataLength=0;
    // set CommunicationSenderInterface
    private CommunicationSenderInterface senderInterface;    
    
    // Reference to SensorProcessorManager for sending messages to PE
    private SensorProcessorManager spm;    
    // Use the SubscriberMessageManager to obtain a reference to ROSNode.
    private ROSSubscriberMessageManager smm;    
    // PEProvider reference
    private PEProvider temp_provider;    
    // topic id
    private ROSTopicRegistryMessageDefinition trmd;
    
    public DefaultTunerMessageHandler()
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
    
    @Override
    public void process(byte[] message, int dataLength) 
    {
        // if dataLength is greater than the maxReceivedDataLength, assign the dataLength value to maxReceivedDataLength.
        if(dataLength>maxReceivedDataLength)
        {
            this.maxReceivedDataLength=dataLength;
        }
        else if(dataLength==-1&&message.length>maxReceivedDataLength)
        {
            this.maxReceivedDataLength=message.length;            
        }
            
 //       System.out.println("Calling DefaultMessageHandler.");
        this.ros_message = decoder.getMessage(message);
        // DEBUG caller
        //this.ros_message = decoder.getMessage(message,threadName);
        
        // Send ROSMessage to Perception Engine
       if(spm!=null)
        {
            System.out.println("tid: " + tid);
            spm.process(ros_message);
        }
        else
        {
            System.out.println("Calling dmh null spm.process.");
            this.temp_provider=smm.rosNode.getController().getPE().getProviderByName("ros_pe_provider");
            if(temp_provider!=null)
            {
                System.out.println("temp_provider ! null" + threadName);
                this.spm = temp_provider.getSensorProcessorManager();
                this.temp_provider=null;
                ros_message.hid=tid;
            }
            if(spm!=null)
            {
                ros_message.hid=tid;
                System.out.println("spm ! null ros_message.hid:" + ros_message.hid);
                spm.process(ros_message);
            }
            else
            {
                System.out.println("We have a problem... Remove when fixed.");
            }
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
        // if message.length is greater than the maxReceivedDataLength, assign the message.length value to maxReceivedDataLength.
        if(message.length>maxReceivedDataLength)
        {
            this.maxReceivedDataLength=message.length;            
        }        
//        System.out.println("Calling DefaultMessageHandler.");
        this.ros_message = decoder.getMessage(message);
        // DEBUG caller
        //this.ros_message = decoder.getMessage(message,threadName);
        
        // Send ROSMessage to Perception Engine
       if(spm!=null)
        {
            System.out.println("tid: " + tid);
            spm.process(ros_message);
        }
        else
        {
            System.out.println("Calling dmh null spm.process.");
            this.temp_provider=smm.rosNode.getController().getPE().getProviderByName("ros_pe_provider");
            if(temp_provider!=null)
            {
                System.out.println("temp_provider ! null" + threadName);
                this.spm = temp_provider.getSensorProcessorManager();
                this.temp_provider=null;
                ros_message.hid=tid;
            }
            if(spm!=null)
            {
                ros_message.hid=tid;
                System.out.println("spm ! null ros_message.hid:" + ros_message.hid);
                spm.process(ros_message);
            }
            else
            {
                System.out.println("We have a problem... Remove when fixed.");
            }
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

    /** Return the length of the largest received message byte[] block 
     * size for processing incoming data communications.
     * 
     * @return int largest received message byte[] block size for processing incoming 
     * data communications.
     */
    public int getLargestReceivedBlockSize() 
    {
        //IP4 UDP/TCP Max length without Jumbogram is 65,507, (actual packet length is 65535 with Jumbogram up to 4GB.
        return maxReceivedDataLength;
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
