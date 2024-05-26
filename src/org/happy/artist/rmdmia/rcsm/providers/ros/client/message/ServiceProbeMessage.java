package org.happy.artist.rmdmia.rcsm.providers.ros.client.message;

import java.io.UnsupportedEncodingException;
import org.happy.artist.rmdmia.utilities.HexStringConverter;
import org.happy.artist.rmdmia.utilities.HexToBytes;

/** ServiceProbeMessage.java - The Service Probe Message class is 
 * intended to be a reference class for obtaining pre-configured service 
 * probe message. The service probe allows a controller to obtain the service 
 * types, and md5sum from a system, without knowing them beforehand. This message 
 * will support startup configuration, and automatic dynamic configuration of 
 * the ROS client.
 *
 * @author Happy Artist
 *
 * @copyright Copyright © 2014 Happy Artist. All rights reserved.
 */
public class ServiceProbeMessage 
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
    private final static String _md5sum="md5sum=*";
    private final static String _service="service=";
    private final static String _probe="probe=1";  
    private String hexFieldLength;
    private int fieldLength=-1;  
    private StringBuilder sb=new StringBuilder();
    private HexStringConverter hexConvert = HexStringConverter.getHexStringConverterInstance();    
    
    public ServiceProbeMessage(String callerid, String service) throws UnsupportedEncodingException
    {
        setInitializationMessage(callerid, service);
    }
    
    private boolean isInitialized=false;
    public boolean isInitializedMessage()
    {
        return isInitialized;
    }
    
    public void setInitializationMessage(String callerid, String service) throws UnsupportedEncodingException
    {
        this.isInitialized=false;
        this.service=service;
        if(callerid!=null&&service!=null)
        {
            // Process Message Header Fields   
            sb.append(setPrependROSFieldLength(hexConvert.stringToHex(_service.concat(service))));          
            sb.append(setPrependROSFieldLength(hexConvert.stringToHex(_probe)));            
            sb.append(setPrependROSFieldLength(hexConvert.stringToHex(_callerid.concat(callerid))));
            sb.append(setPrependROSFieldLength(hexConvert.stringToHex(_md5sum)));
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
