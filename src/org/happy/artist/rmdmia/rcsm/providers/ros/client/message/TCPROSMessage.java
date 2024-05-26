package org.happy.artist.rmdmia.rcsm.providers.ros.client.message;

/** TCPROSMessage.java Not sure what this class was written for yet. Will need to research more later.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright© 2013 Happy Artist. All rights reserved.
 */
public class TCPROSMessage 
{
    // callerid: Name of node sending data. 
    public String callerid;
    // topic: name of the topic the subscriber is connecting to.
    public String topic;
    // service: name of service the client is calling
    public String service;    
    // md5sum: md5sum of the message type
    public String md5sum;
    // type: message type
    public String type;
    //message_definition: full text of message definition (output of gendeps --cat)
    public String message_definition;
    //error: human-readable error message if the connection is not successful
    public String error;
    //persistent: sent from a service client to a service. If '1', keep connection open for multiple requests.
    public String persistent;
    //tcp_nodelay: sent from subscriber to publisher. If '1', publisher will set TCP_NODELAY on socket if possible
    public String tcp_nodelay;
    //latching: publisher is in latching mode (i.e. sends the last value published to new subscribers)
    public String latching;
    // on Text Data Type, use String text_body, otherwise, use binary_body.
//    public String text_body;
    // binary body, used on non String data types.
    public byte[] binary_body;
    
    private final static String pad8Zero="00000000";
    private final static String pad6Zero="000000";
    private final static String pad4Zero="0000";
    private final static String pad2Zero="00";
    private final static String pad1Zero="0";    
    private String hexFieldLength;
    private int fieldLength=-1;   
    
    // variables for method generateCodeForFinalByteArray
    private String commaSeparatedByteArray;
    
    public byte[] getSubscriptionInitMessage(String callerid, String md5sum, String tcp_nodelay, String topic, String type)
    {
        
        return null;
    }
    
    // Prepend the ROS field length to the ROS message field.
    public String setPrependROSFieldLength(String hexMessage)
    {
        this.hexFieldLength=Integer.toHexString(hexMessage.length()/2);
        this.fieldLength=hexFieldLength.length();
        // if number is odd pad a 0 to front of String.
        if (fieldLength % 2 != 0)
        {
            this.hexFieldLength=pad1Zero.concat(hexFieldLength);
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
    
    public String generateCodeForFinalByteArray(String hexMessage)
    {
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<hexMessage.length();i++)
        {
            sb.append("(byte)0x");
            sb.append(hexMessage.substring(i,i+2));
            sb.append(",");
            i=i+1;
        }
        return "public final static byte[] MESSAGE={".concat(sb.toString().substring(0, sb.toString().length()-1)).concat("};");
    }    
}
