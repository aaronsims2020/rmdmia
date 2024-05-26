package org.happy.artist.rmdmia.rcsm.providers.ros.client.message;

import java.io.UnsupportedEncodingException;
import org.happy.artist.rmdmia.utilities.HexStringConverter;
import org.happy.artist.rmdmia.utilities.HexToBytes;

/** ServiceMessage.java - The Service Message class is intended to be a reference class for
 * obtaining pre-configured topic message. This class will be instantiated and 
 * available in an array referenced by topic type element ids. All service 
 * topics will be subscribed to using these Objects, instead of reprocessing a 
 * new message every time a service is subscribed to. Additionally, 
 * these Objects will be initialized at startup to avoid processing delays on 
 * subscription messages.  
 *
 * @author Happy Artist
 *
 * @copyright Copyright © 2013 Happy Artist. All rights reserved.
 */
public class ServiceMessage 
{
    // The complete message to return via the getMessage method.
    private byte[] message;
    // set isReady to true once all message data is set, and this will subscribe using the defined parameters.
    private boolean isReady=false;
    // Service name for id
    public String service;
    // message generation variables
    private final static String pad6Zero="000000";
    private final static String pad4Zero="0000";
    private final static String pad2Zero="00";
    private final static String pad1Zero="0";  
    private final static String _callerid="callerid=";
    private final static String _md5sum="md5sum=";
    private final static String _type="type=";
    private final static String _service="service=";
    private final static String _tcp_nodelay0="tcp_nodelay=0";  
    private final static String _tcp_nodelay1="tcp_nodelay=1";    
    private final static String _persistent0="persistent=0";  
    private final static String _persistent1="persistent=1";      
    private String hexFieldLength;
    private int fieldLength=-1;  
    private StringBuilder sb=new StringBuilder();
    private HexStringConverter hexConvert = HexStringConverter.getHexStringConverterInstance();    
    
    public ServiceMessage(String callerid, String md5sum,boolean tcp_nodelay, String service, String type, boolean persistent) throws UnsupportedEncodingException
    {
        setInitializationMessage(callerid, md5sum, tcp_nodelay, service, type, persistent);
    }
    
    private boolean isInitialized=false;
    public boolean isInitializedMessage()
    {
        return isInitialized;
    }
    
    public void setInitializationMessage(String callerid, String md5sum,boolean tcp_nodelay, String service, String type, boolean persistent) throws UnsupportedEncodingException
    {
        this.isInitialized=false;
        this.service=service;
        if(callerid!=null&&md5sum!=null&&service!=null&&type!=null)
        {
            // Process Message Header Fields    
            sb.append(setPrependROSFieldLength(hexConvert.stringToHex(_callerid.concat(callerid))));
            sb.append(setPrependROSFieldLength(hexConvert.stringToHex(_md5sum.concat(md5sum))));  
            if(tcp_nodelay)
            {
                sb.append(setPrependROSFieldLength(hexConvert.stringToHex(_tcp_nodelay1)));          
            }
            else
            {
                sb.append(setPrependROSFieldLength(hexConvert.stringToHex(_tcp_nodelay0)));
            }  
            sb.append(setPrependROSFieldLength(hexConvert.stringToHex(_service.concat(service))));
            if(persistent)
            {
                sb.append(setPrependROSFieldLength(hexConvert.stringToHex(_persistent1)));          
            }
            else
            {
                sb.append(setPrependROSFieldLength(hexConvert.stringToHex(_persistent0)));
            } 
            sb.append(setPrependROSFieldLength(hexConvert.stringToHex(_type.concat(type))));
            this.message=HexToBytes.hexToBytes(setPrependROSFieldLength(sb.toString()));
            this.sb=null;
            this.isReady=true;
        }        
    }     
    
    public void setInitializedMessage(String callerid, String md5sum,boolean tcp_nodelay, String service, String type, boolean persistent) throws UnsupportedEncodingException
    {
        //callerid: node name of service client
        //service: name of the topic the subscriber is connecting to
        //md5sum: md5sum of the message type
        //type: service type 
        this.isInitialized=true;
        this.service=service;
        if(callerid!=null&&md5sum!=null&&service!=null&&type!=null)
        {
            // Process Message Header Fields    
            sb.append(setPrependROSFieldLength(hexConvert.stringToHex(_callerid.concat(callerid))));
            sb.append(setPrependROSFieldLength(hexConvert.stringToHex(_md5sum.concat(md5sum))));  
            if(tcp_nodelay)
            {
                sb.append(setPrependROSFieldLength(hexConvert.stringToHex(_tcp_nodelay1)));          
            }
            else
            {
                sb.append(setPrependROSFieldLength(hexConvert.stringToHex(_tcp_nodelay0)));
            }  
            sb.append(setPrependROSFieldLength(hexConvert.stringToHex(_service.concat(service))));
            if(persistent)
            {
                sb.append(setPrependROSFieldLength(hexConvert.stringToHex(_persistent1)));          
            }
            else
            {
                sb.append(setPrependROSFieldLength(hexConvert.stringToHex(_persistent0)));
            } 
            sb.append(setPrependROSFieldLength(hexConvert.stringToHex(_type.concat(type))));
            this.message=HexToBytes.hexToBytes(setPrependROSFieldLength(sb.toString()));
            this.sb=null;
            this.isReady=true;
        }        
    }    
    
    // Return the complete message byte[] to register a subscription.
    public byte[] getMessage()
    {
        return message;
    }
    
    // Returns true if message was constructed successfully. Returns false if message details are missing. 
    public boolean isReady()
    {
        return isReady;
    }
    
    // Prepend the ROS field length to the ROS message field.
    private String setPrependROSFieldLength(String hexMessage)
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
}
