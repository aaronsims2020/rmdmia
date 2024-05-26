package org.happy.artist.rmdmia.rcsm.providers.ros.client.message;

import java.util.Arrays;
import org.happy.artist.rmdmia.rcsm.providers.ros.ROSMessage;
import org.happy.artist.rmdmia.rcsm.providers.ros.ROSMessagePool;
import org.happy.artist.rmdmia.utilities.HexStringConverter;
import org.happy.artist.rmdmia.utilities.HexToBytes;

/** ROSMessageDecoder.java - Utilities for reading ROS Messages from byte[] and converting them to Java Objects.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2013 Happy Artist. All rights reserved.
 */
public class ROSMessageDecoder 
{   
    private String threadName;
    
    // The length of the TCPROS LENGTH field.
    public static final int LENGTH_FIELD=4;
    
    private StringBuilder messageStringBuilder;
    private int curPos;
    private int packetLength;
    private int msgLength;
    private int fieldLength;
    // set after header parsing completes
    private int bodyMsgLength;    
    private int pmLoopCount;
    
    // convertHexToString method variables
    private StringBuilder builder;
    private int hexLoopCount;
    private int firstDigit;
    private int lastDigit;
    private int hexDec;  
    
    // bytesToHex method variables.
    private final static char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    private char[] hexChars;
    private int v;
    private int endIndex;
    private int hexArrayIndex;
    private int b2hLoopCount;
    private int byteToHexFieldIndex;
    // getMessage variables
    private ROSMessagePool pool = ROSMessagePool.getInstance();
    private ROSMessage rosMessage;
    
    /** for debugging. */
    public ROSMessage getMessage(byte[] message, String threadName)
    {
        this.threadName=threadName;
        return getMessage(message);
    }
        
    /** Call getMessage on iterations following initial call to 
     * getInitializationMessage. Remember to call 
     * ROSMessage.checkin(ROSMessage.rosMessageID); 
     * When the Object is processed, or the system will max out at its 
     * pool limit and hang the system. */
    public ROSMessage getMessage(byte[] message)
    {
        this.rosMessage=pool.checkout();
 // TODO: Decide on Object output approach, until then append message to body of Object.using preloadecd cached message Objects containing header information preloaded. Remember to set non header fields to null or empty string.
        rosMessage.binary_body=message;
        return rosMessage;
    }
    
    /** getInitializationMessage - Call on the initialization message, 
     * followed by getMessage on following messages. Remember to call 
     * ROSMessage.checkin(ROSMessage.rosMessageID); When the Object is processed, 
     * or the system will max out at its pool limit and hang the system. 
     *
     * @param isTopic true for topic, false for service
     * @param topic the topic, or the service name, depending on which it is.
     */
    public ROSMessage getInitializationMessage(byte[] message, String topic, boolean isTopic)
    {
        this.rosMessage=pool.checkout();
        // add message to connection header variable.
        this.rosMessage.connection_header=message;
        this.messageStringBuilder = new StringBuilder();
        // The current position in the byte array processing.        
        this.curPos=0;
        // if message length is at least 4 bytes then read message. Reality is at least 8 bytes would be needed preceding any header field.
        this.msgLength = ((message[curPos+3] & 0xFF) << 24) | ((message[curPos+2] & 0xFF) << 16)
    | ((message[curPos+1] & 0xFF) << 8) | (message[curPos] & 0xFF);
        if(msgLength>LENGTH_FIELD)
        {
            // Set current field position
            this.curPos = 4;
            // set the loop count start position.
            this.pmLoopCount = curPos;
// TODO: 11042013 FIX Looping code here to avoid OutOfMemoryExceptions on large files.    
            while(pmLoopCount<msgLength)
            {
                // Get current field length
                this.fieldLength = ((message[curPos+3] & 0xFF) << 24) | ((message[curPos+2] & 0xFF) << 16)
            | ((message[curPos+1] & 0xFF) << 8) | (message[curPos] & 0xFF);
                // setup the topic for connection headers that do not contain topic information.
                if(isTopic)
                {
                    // set topic name
                    rosMessage.isTopic=true;
                    rosMessage.topic=topic.toCharArray();
                }
                else
                {
                    // set service name.
                    rosMessage.isTopic=false;
                    rosMessage.service=topic.toCharArray();                    
                }
//                System.out.println("Calling bytesToROSMessage... This may be the buggy method...");
                bytesToROSMessage(message, curPos, fieldLength, rosMessage);
                
                this.curPos=curPos + fieldLength + 4;
                // Iterate loop count
                this.pmLoopCount=curPos + 1;
            }
            this.curPos=msgLength+4;
        }        
 // TODO: Decide on Object output approach, until then append message to body of Object.using preloadecd cached message Objects containing header information preloaded. Remember to set non header fields to null or empty string.
        rosMessage.binary_body=message;
        return rosMessage;
    }    
    
    /** getUDPInitializationMessage - Call on the initialization message, 
     * followed by getMessage on following messages. Remember to call 
     * ROSMessage.checkin(ROSMessage.rosMessageID); When the Object is processed, 
     * or the system will max out at its pool limit and hang the system. 
     *
     * @param isTopic true for topic, false for service
     * @param topic the topic, or the service name, depending on which it is.
     */
    public ROSMessage getUDPInitializationMessage(byte[] message, String topic, boolean isTopic)
    {
        this.rosMessage=pool.checkout();
        // add message to connection header variable.
        this.rosMessage.connection_header=message;        
        this.messageStringBuilder = new StringBuilder();
        // The current position in the byte array processing.        
        this.curPos=0;
        // if message length is at least 4 bytes then read message. Reality is at least 8 bytes would be needed preceding any header field.
//        this.msgLength = ((message[curPos+3] & 0xFF) << 24) | ((message[curPos+2] & 0xFF) << 16)
//    | ((message[curPos+1] & 0xFF) << 8) | (message[curPos] & 0xFF);
//        if(msgLength>LENGTH_FIELD)
//        {
            // Set current field position
//            this.curPos = 4;
            // set the loop count start position.
            this.pmLoopCount = curPos;
// TODO: 11042013 FIX Looping code here to avoid OutOfMemoryExceptions on large files.    
            while(pmLoopCount<msgLength)
            {
                // Get current field length
                this.fieldLength = ((message[curPos+3] & 0xFF) << 24) | ((message[curPos+2] & 0xFF) << 16)
            | ((message[curPos+1] & 0xFF) << 8) | (message[curPos] & 0xFF);
                // setup the topic for connection headers that do not contain topic information.
                if(isTopic)
                {
                    // set topic name
                    rosMessage.isTopic=true;
                    rosMessage.topic=topic.toCharArray();
                }
                else
                {
                    // set service name.
                    rosMessage.isTopic=false;
                    rosMessage.service=topic.toCharArray();                    
                }
//                System.out.println("Calling bytesToROSMessage... This may be the buggy method...");
                bytesToROSMessage(message, curPos, fieldLength, rosMessage);
                
                this.curPos=curPos + fieldLength + 4;
                // Iterate loop count
                this.pmLoopCount=curPos + 1;
            }
//            this.curPos=msgLength+4;
//        }        
 // TODO: Decide on Object output approach, until then append message to body of Object.using preloadecd cached message Objects containing header information preloaded. Remember to set non header fields to null or empty string.
        rosMessage.binary_body=message;
        return rosMessage;
    }        

    /** Process the ROSMessage hex into the ROSMessage Object. */
    private void processDataToMessage(char[] charArray, int pos, int dataLength, ROSMessage rosMessage)
    {
        // TODO: Replace compares using chars with hex codes so conversion to String char[] is not necessary (extra step).
        charArray=convertHexToString(charArray).toCharArray();
//        System.out.println("header message: " + String.valueOf(charArray));
/*
callerid=
definition=
topic=
type=
tcp_nodelay=
md5sum=
service=
persistant=
latching=
request_type
response_type
c
me
to
ty
tc
md
s
p
l
req
res
*/


        // ROS Message header processing
        if(charArray[pos]=='c')
        {
                // callerid
                this.rosMessage.callerid=Arrays.copyOfRange(charArray, pos + 9, pos + dataLength);
        }
        else if(charArray[pos]=='t')
        {
                // topic, type, or tcp_nodelay
                if(charArray[pos+1]=='o')
                {
                    // topic
                    this.rosMessage.topic=Arrays.copyOfRange(charArray, pos + 6, pos + dataLength);       
                    this.rosMessage.isTopic=true;
                }
                else if(charArray[pos+1]=='y')
                {
                    // type
                    this.rosMessage.type=Arrays.copyOfRange(charArray, pos + 5, pos + dataLength);
                }
                else if(charArray[pos+1]=='c')
                {
                    // tcp_nodelay
                    this.rosMessage.tcp_nodelay=Arrays.copyOfRange(charArray, pos + 13, pos + dataLength);  
                }
        }
        else if(charArray[pos]=='s')
        {
                // service
                this.rosMessage.service=Arrays.copyOfRange(charArray, pos + 8, pos + dataLength);       
                this.rosMessage.isTopic=false;
        }
        else if(charArray[pos]=='m')
        {
                if(charArray[pos+1]=='d')
                {
                // md5sum
                this.rosMessage.md5sum=Arrays.copyOfRange(charArray, pos + 7, pos + dataLength);
                }
                else if(charArray[pos+1]=='e')
                {
                // message_definition
                this.rosMessage.message_definition=Arrays.copyOfRange(charArray, pos + 19, pos + dataLength);                    
                }
        }
        else if(charArray[pos]=='p')
        {
            // persistant
            this.rosMessage.persistent=Arrays.copyOfRange(charArray, pos + 11, pos + dataLength);
        }
        else if(charArray[pos]=='l')
        {
            // latching
            this.rosMessage.latching=Arrays.copyOfRange(charArray, pos + 9, pos + dataLength);
        }
        else if(charArray[pos]=='r'&&charArray[pos + 1]=='e')
        {
            // service request type & service response_type
                if(charArray[pos+2]=='q')
                {            
                    this.rosMessage.request_type=Arrays.copyOfRange(charArray, pos + 13, pos + dataLength);
                }
                else if(charArray[pos+2]=='s')
                {              
                    this.rosMessage.response_type=Arrays.copyOfRange(charArray, pos + 14, pos + dataLength);            
                }
        }        
        else if(charArray[pos]=='e')
        {
                // error
                this.rosMessage.error=String.valueOf(Arrays.copyOfRange(charArray, pos + 6, pos + dataLength));
        }       
        else
        {
            System.out.println("Not Connection Header recognized attribute: " + String.valueOf(charArray));
        }
    }
          
    // Idea for this one came from http://stackoverflow.com/questions/12039341/hex-to-string-in-java-performance-is-too-slow
public String convertHexToString(char[] hex) 
{
    this.builder = new StringBuilder();
    this.hexLoopCount=0;
    while (hexLoopCount < hex.length - 1) 
    {
        this.firstDigit = Character.digit(hex[hexLoopCount], 16);
        this.lastDigit = Character.digit(hex[hexLoopCount + 1], 16);
        this.hexDec = firstDigit * 16 + lastDigit;
        builder.append((char)hexDec);
        // increment the loop count by 2.
        this.hexLoopCount=hexLoopCount + 2;
    }
    return builder.toString();
}    
    // Read ints in little-endian order.
    public void processMessage(byte[] message)
    { 
        this.messageStringBuilder = new StringBuilder();
        // The current position in the byte array processing.        
        this.curPos=0;
        // if message length is at least 4 bytes then read message. Reality is at least 8 bytes would be needed preceding any header field.
        if(message.length>LENGTH_FIELD)
        {
            this.msgLength = ((message[curPos+3] & 0xFF) << 24) | ((message[curPos+2] & 0xFF) << 16)
    | ((message[curPos+1] & 0xFF) << 8) | (message[curPos] & 0xFF);

//            System.out.println("Message length: " + msgLength);
            if(msgLength==3)
            {
//                 System.out.println("Hex Message (3 length):" + String.valueOf(HexStringConverter.bytesToHex(message)));   
            }
    bytesToHex(message, curPos, msgLength, messageStringBuilder);
    
    // TODO: Add the sub header support and parsing...

            // Set current field position
            this.curPos = 4;
            // set the loop count start position.
            this.pmLoopCount = curPos;
            while(pmLoopCount<msgLength)
            {
                // Get current field length
                this.fieldLength = ((message[curPos+3] & 0xFF) << 24) | ((message[curPos+2] & 0xFF) << 16)
            | ((message[curPos+1] & 0xFF) << 8) | (message[curPos] & 0xFF);
                bytesToHex(message, curPos, fieldLength, messageStringBuilder);
                
                this.curPos=curPos + fieldLength + 4;
                // Iterate loop count
                this.pmLoopCount=curPos + 1;
            }
        }
    }

    // We know the message length, and the current Index Position in message processing
    // We also can assume + 4 bytes for the field length 
    // Multiple messages are contained inside a byte array packet received by the Socket.
    // Need to give credit on this method being from the following URL: http://stackoverflow.com/questions/9655181/convert-from-byte-array-to-hex-string-in-java
    public void bytesToHex(byte[] bytes, int curPos, int fieldLength, StringBuilder sbToAppendMessage) 
    {
        if(hexChars==null||hexChars.length<fieldLength*2)
        {
            this.hexChars = new char[this.byteToHexFieldIndex = fieldLength * 2];    
        }
        this.endIndex = curPos + fieldLength + 4;
        this.hexArrayIndex=curPos+4;
        this.b2hLoopCount=hexArrayIndex;
        while (this.b2hLoopCount < endIndex) 
        {
            this.v = bytes[b2hLoopCount] & 0xFF;
            this.hexChars[(b2hLoopCount-hexArrayIndex) * 2] = ROSMessageDecoder.hexArray[v >>> 4];
            this.hexChars[(b2hLoopCount-hexArrayIndex) * 2 + 1] = ROSMessageDecoder.hexArray[v & 0x0F];
            this.b2hLoopCount=b2hLoopCount + 1;
        }
        sbToAppendMessage.append(hexChars,0,fieldLength);
    }    

    /** Convert a ROS Message in a byte[] to a ROSMessage Object. */
    public void bytesToROSMessage(byte[] bytes, int curPos, int fieldLength, ROSMessage message) 
    {
       // System.out.println("bytesToROSMessage fieldLength:" + fieldLength + ", curPos:" + curPos);
        // TODO: Fix this method. Problem appears to be this is tring to convert non char data into chars? Or misreading data passed into field length.
        try
        {
            if(hexChars==null||hexChars.length<fieldLength*2)
            {
                this.hexChars = new char[this.byteToHexFieldIndex = fieldLength * 2];    
            }
            this.endIndex = curPos + fieldLength + 4;
            this.hexArrayIndex=curPos+4;
            this.b2hLoopCount=hexArrayIndex;
            while (this.b2hLoopCount < endIndex) 
            {
                this.v = bytes[b2hLoopCount] & 0xFF;
                this.hexChars[(b2hLoopCount-hexArrayIndex) * 2] = ROSMessageDecoder.hexArray[v >>> 4];
                this.hexChars[(b2hLoopCount-hexArrayIndex) * 2 + 1] = ROSMessageDecoder.hexArray[v & 0x0F];
                this.b2hLoopCount=b2hLoopCount + 1;
            }
            processDataToMessage(hexChars,0,fieldLength,message);
        }
        catch(NegativeArraySizeException e)
        {
                    //System.out.println("bytesToROSMessage fieldLength:" + fieldLength + ", curPos:" + curPos + ", byteToHexFieldIndex: " + byteToHexFieldIndex + "\nHex: " + String.valueOf(HexStringConverter.bytesToHex(bytes)));
                    
            e.printStackTrace();
        } 
        catch(ArrayIndexOutOfBoundsException e)
        {
                    //System.out.println("bytesToROSMessage fieldLength:" + fieldLength + ", curPos:" + curPos + ", byteToHexFieldIndex: " + byteToHexFieldIndex + "\nHex: " + String.valueOf(HexStringConverter.bytesToHex(bytes)));
                    
            e.printStackTrace();            
        }
        catch(NullPointerException e)
        {
            e.printStackTrace();
        }
    }        

    public static void main(String[] args)
    {
        ROSMessageDecoder decoder = new ROSMessageDecoder();
        String hexMessage="14000000176CB140176CB14000000000000000000000000014000000176CB140176CB14000000000000000000000000014000000176CB140176CB14000000000000000000000000014000000176CB140176CB140000000000000000000000000646566696E6974696F6E3D666C6F6174333220780A666C6F6174333220790A666C6F617433322074686574610A0A666C6F61743332206C696E6561725F76656C6F636974790A666C6F6174333220616E67756C61725F76656C6F636974790A13000000747970653D747572746C6573696D2F506F7365";  
        decoder.getMessage(HexToBytes.hexToBytes(hexMessage));
    }
    
}
