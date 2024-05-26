package org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.udpros;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.happy.artist.rmdmia.rcsm.providers.ros.ROSMessage;
import org.happy.artist.rmdmia.rcsm.providers.ros.ROSMessagePool;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.ROSMessageDecoder;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.ROSSubscriberMessageManager;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.ROSTopicRegistryMessageDefinition;

/** UDPProtocolUtility.java Helper class for UDP requestTopic XMLRPC structure.
 * 
 * @author Happy Artist
 * 
 * @copyright Copyright© 2014 Happy Artist. All rights reserved.
 */
public class UDPProtocolUtility 
{
    // message generation variables
    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();
    public final static String pad6Zero="000000";
    public final static String pad4Zero="0000";
    public final static String pad2Zero="00";
    public final static String pad1Zero="0";      
    private final static String _callerid="callerid=";
    private final static String _md5sum="md5sum=";
    private final static String _type="type=";
    private final static String _topic="topic=";
    private final static String _message_definition="message_definition=";    
    private final static String _latching="latching="; 
    
    private ROSMessageDecoder decoder = new ROSMessageDecoder();
    private ROSMessage ros_message;
    private String threadName;
    // TODO: Make the ROSMessagePoolFactory size definable in properties at the 
    // topic/service level.
    private ROSMessagePool pool = ROSMessagePool.getInstance();  
    // TODO: remove this from here, and place in Tuner, or implement a flag that only updates if enabled to do so.
    private final String smmLoadedEarly="SubscriberMessageManager_Prematurely Loaded.";
    // Update the SusbscriberMessageManager with the message_definition information.
    private ROSSubscriberMessageManager smm;
    // Allows the MessageHandler to change the Message Handler on the fly.
  //  private MessageHandlerControllerInterface messageHandlerController;
    // Empty String for an equals compare using final static
    private final static String EMPTY_STRING="";
    private ROSTopicRegistryMessageDefinition trmd;
    // call private add if the following variable is set to true.
    private boolean isChanged;    

    /** Return the base64 encoded String containing the Base64 section of the UDPROS XMLRPC requestTopic call. */ 
    public static byte[] generateXMLRPCBase64(String callerid, String md5sum, String message_definition, String topic, String type, boolean latching) throws UnsupportedEncodingException, IOException
    {
        StringBuilder sb=new StringBuilder();        
        if(callerid!=null&&md5sum!=null&&topic!=null&&type!=null)
        {
            // Process Message Header Fields    
            sb.append(setPrependROSFieldLength(asHex(_callerid.concat(callerid).getBytes())));
            sb.append(setPrependROSFieldLength(asHex(_md5sum.concat(md5sum).getBytes())));  
            sb.append(setPrependROSFieldLength(asHex(_topic.concat(topic).getBytes())));
            sb.append(setPrependROSFieldLength(asHex(_type.concat(type).getBytes())));
            if(latching==true)
            {
                sb.append(setPrependROSFieldLength(asHex(_latching.concat("1").getBytes())));    
            }
            if(message_definition!=null)
            {
                sb.append(setPrependROSFieldLength(asHex(_message_definition.concat(message_definition).getBytes())));
            }
        }
//        System.out.println(getBase64FromHEX(sb.toString().toCharArray()));
        return javax.xml.bind.DatatypeConverter.parseBase64Binary(getBase64FromHEX(sb.toString().toCharArray()));
    }     
    
    
    public static String getBase64FromHEX(char[] input) {
        
        byte bytes[] = new byte[input.length/2];
        int count = 0;
        int i1=0;
        int i2=0;
        int j;
        for (int i = 0; i < input.length; i += 2) 
        {
            char c1 = input[i];
            char c2 = input[i + 1];
            j=0;
            while(j<HEX_CHARS.length) 
            {
                if (c1 == HEX_CHARS[j]) 
                {
                    i1=j;
                }
                j=j+1;
            }
            // set j to 0 for new loop.
            j=0;
            while(j<HEX_CHARS.length) 
            {
                if (c2 == HEX_CHARS[j]) 
                {
                    i2=j;
                }
                j=j+1;
            }

            bytes[count] = 0;
            bytes[count] |= (byte) ((i1 & 0x0F) << 4);
            bytes[count] |= (byte) (i2 & 0x0F);
            count++;
        }
        return javax.xml.bind.DatatypeConverter.printBase64Binary(bytes);

    }    

    // Prepend the ROS field length to the ROS message field.
    private static String setPrependROSFieldLength(String hexMessage)
    {
        String hexFieldLength=Integer.toHexString(hexMessage.length()/2);
        int fieldLength=hexFieldLength.length();
        // if number is odd pad a 0 to front of String.
        if (fieldLength % 2 != 0)
        {
            hexFieldLength=pad1Zero.concat(hexFieldLength);
            fieldLength=fieldLength + 1;
        }        
        if(fieldLength==2)
        {
            return hexFieldLength.concat(pad6Zero).concat(hexMessage);
        }
        else if(fieldLength==4)
        {
            return hexFieldLength.concat(pad4Zero).concat(hexMessage);
        }
        else if(fieldLength==6)
        {
            return hexFieldLength.concat(pad2Zero).concat(hexMessage);
        }
        else
        {
            return hexFieldLength.concat(hexMessage);            
        }
    }     
    
    private static String asHex(byte[] buf)
    {
        char[] chars = new char[2 * buf.length];
        for (int i = 0; i < buf.length; ++i)
        {
            chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
            chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
        }
        return new String(chars);
    }
    
   public UDPProtocolUtility() 
   {
        try {
            this.smm = ROSSubscriberMessageManager.getInstance("smmLoadedEarly",null);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(UDPProtocolUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void processUDPConnectionHeader(byte[] message, String topic, boolean isTopic) 
    {
//System.out.println("Hex Message:" + org.happy.artist.rmdmia.utilities.BytesToHex.bytesToHex(message));        
//System.out.println("Message: "  + decode.convertHexToString(org.happy.artist.rmdmia.utilities.BytesToHex.bytesToHex(message).toCharArray()));          
        this.isChanged=false;
//        System.out.println("Calling DefaultInitializerMessageHandler.");        
        this.ros_message = decoder.getUDPInitializationMessage(message,topic,isTopic);
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
                System.out.println("Topic message_definition not defined in Subscriber Message Manager, updating message_definition."); 
                trmd.definition=String.valueOf(ros_message.message_definition);
                this.isChanged=true;
            }
        }
     
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
                
        // If data is changed update the SubscriberMessageManager, and topic_registry.eax.
        if(isChanged)
        {
            try 
            {
                smm.add(trmd);
            } 
            catch (UnsupportedEncodingException ex) 
            {
                Logger.getLogger(UDPProtocolUtility.class.getName()).log(Level.SEVERE, "Exception adding message_definition, request_topic, or response_topic to SubscroberMessageManager.", ex);
            }
        }        
        // Set Message Pool Message Object Header Values:
        pool.setROSMessageHeaderInPooledMessages(ros_message);
        // Check the ROSMessage back into the ROSMessagePool.
        pool.checkin(ros_message.rosMessageID);
//        System.out.println("DATA: " + String.valueOf(HexStringConverter.bytesToHex(message)));
//        System.out.println("DefaultInitializerMessageHandler processing not implemented yet. Need to implement the SubscriberMessageManager definition.");
        // Set the trmd reference to null before preceding.
        this.trmd=null;
    }
}
