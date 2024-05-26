package org.happy.artist.rmdmia.rcsm.provider.message;

import org.happy.artist.rmdmia.rcsm.provider.message.MessageHandlerInterface;

/** DataProcessor.java An interface created for the purpose of representing
 * TCPDataProcessor and UDPDataProcessor classes that implement the 
 * MessageHandlerInterface.
 * 
 * @author Happy Artist
 * 
 * @copyright Copyright© 2014 Happy Artist. All rights reserved.
 */
public interface DataProcessor 
{
    /** Return the classname of the DataProcessor. */
    public String getClassname();  
    
    /** Return the data processor id. */
    public int getId();
    
    /** Return the MessageHandlerInterface to this DataProcessor. */    
    public MessageHandlerInterface getMessageHandler();
}
