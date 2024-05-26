package org.happy.artist.rmdmia.rcsm.providers.ros.client.message;

import java.io.UnsupportedEncodingException;
import org.happy.artist.rmdmia.utilities.HexStringConverter;
import org.happy.artist.rmdmia.utilities.HexToBytes;

/** PublisherMessage.java - The Publisher Message class is intended to 
 * be a reference class for obtaining pre-configured topic message. This 
 * class will be instantiated and available in an array referenced by topic type 
 * element ids. All publisher topics will be published to using these Objects, 
 * instead of reprocessing a new message every time a topic is published to. 
 * Additionally, these Objects will be initialized at startup to avoid processing 
 * delays on published messages.  
 *
 * @author Happy Artist
 *
 * @copyright Copyright © 2014 Happy Artist. All rights reserved.
 */
public class PublisherMessage 
{
    // The complete message to return via the getMessage method.
    private byte[] message;
    // set isReady to true once all message data is set, and this will subscribe using the defined parameters.
    private boolean isReady=false;
    // message generation variables
    private final static String pad6Zero="000000";
    private final static String pad4Zero="0000";
    private final static String pad2Zero="00";
    private final static String pad1Zero="0";  
    private final static String _callerid="callerid=";
    private final static String _md5sum="md5sum=";
    private final static String _type="type=";
    private final static String _latching0="latching=0";  
    private final static String _latching1="latching=1";  
    private final static String _error="error=";     
    
    private String hexFieldLength;
    private int fieldLength=-1;  
    private StringBuilder sb=new StringBuilder();
    private HexStringConverter hexConvert = HexStringConverter.getHexStringConverterInstance();    
    public boolean latching;
    public String callerid;
    public String md5sum;
    public String type;
    
    public PublisherMessage(String callerid, String md5sum, String type, boolean latching) throws UnsupportedEncodingException
    {
        this.latching=latching;
        this.callerid=callerid;
        this.md5sum=md5sum;
        this.type=type;
        
        if(callerid!=null&&md5sum!=null&&type!=null)
        {
            // Process Message Header Fields    
            sb.append(setPrependROSFieldLength(hexConvert.stringToHex(_callerid.concat(callerid))));
            sb.append(setPrependROSFieldLength(hexConvert.stringToHex(_md5sum.concat(md5sum))));  
            sb.append(setPrependROSFieldLength(hexConvert.stringToHex(_type.concat(type))));              if(latching)
            {
                sb.append(setPrependROSFieldLength(hexConvert.stringToHex(_latching1)));          
            }
            else
            {
                sb.append(setPrependROSFieldLength(hexConvert.stringToHex(_latching0)));
            }  
            this.message=HexToBytes.hexToBytes(setPrependROSFieldLength(sb.toString()));
            this.sb=null;
            this.isReady=true;
        }
    }
    
    /** The latching can change if it is enabled and no messages have been set on a topic. */
    public PublisherMessage updateLatching(boolean latching) throws UnsupportedEncodingException
    {
         this.sb=new StringBuilder();
        if(callerid!=null&&md5sum!=null&&type!=null)
        {
            // Process Message Header Fields    
            sb.append(setPrependROSFieldLength(hexConvert.stringToHex(_callerid.concat(callerid))));
            sb.append(setPrependROSFieldLength(hexConvert.stringToHex(_md5sum.concat(md5sum))));  
            sb.append(setPrependROSFieldLength(hexConvert.stringToHex(_type.concat(type))));              if(latching)
            {
                sb.append(setPrependROSFieldLength(hexConvert.stringToHex(_latching1)));          
            }
            else
            {
                sb.append(setPrependROSFieldLength(hexConvert.stringToHex(_latching0)));
            }  
            this.message=HexToBytes.hexToBytes(setPrependROSFieldLength(sb.toString()));
            this.sb=null;
            this.isReady=true;
        }
        return this;
    }    
    
    /** Updates PublisherMessage with an error message. */
    public PublisherMessage updateError(String error) throws UnsupportedEncodingException
    {
        this.sb=new StringBuilder();
        if(callerid!=null&&md5sum!=null&&type!=null)
        {
            // Process Message Header Fields    
            sb.append(setPrependROSFieldLength(hexConvert.stringToHex(_callerid.concat(callerid))));
            sb.append(setPrependROSFieldLength(hexConvert.stringToHex(_md5sum.concat(md5sum))));  
            sb.append(setPrependROSFieldLength(hexConvert.stringToHex(_type.concat(type))));    
            sb.append(setPrependROSFieldLength(hexConvert.stringToHex(_error.concat(error))));        
            this.message=HexToBytes.hexToBytes(setPrependROSFieldLength(sb.toString()));
            this.sb=null;
            this.isReady=true;
        }
        else
        {
            // Process Message Header Fields   
            if(callerid!=null)
            {
            sb.append(setPrependROSFieldLength(hexConvert.stringToHex(_callerid.concat(callerid))));
            }
            if(md5sum!=null)
            {
                sb.append(setPrependROSFieldLength(hexConvert.stringToHex(_md5sum.concat(md5sum)))); 
            }
            if(type!=null)
            {
                sb.append(setPrependROSFieldLength(hexConvert.stringToHex(_type.concat(type))));    
            }
            sb.append(setPrependROSFieldLength(hexConvert.stringToHex(_error.concat(error))));        
            this.message=HexToBytes.hexToBytes(setPrependROSFieldLength(sb.toString()));
            this.sb=null;
            this.isReady=true;            
        }
        return this;
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
