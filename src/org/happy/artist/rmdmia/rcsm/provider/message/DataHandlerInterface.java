package org.happy.artist.rmdmia.rcsm.provider.message;

/** org.happy.artist.rmdmia.rcsm.client.transport.DataHandlerInterface - 
 * This interface acts as a proxy to the MessageHandlerInterface process
 * methods byte[] data. The best performing MessageHandlerInterface implementations
 * will implement the process methods using Abstract MessageHandler classes, however,
 * some MessageHandlerInterface implementations use the process method for other purposes.
 * Non-persistent service message handler interface implementations including 
 * DefaultServiceMessageHandler, and AbstractServiceMessageHandler are an example of 
 * classes that use the process methods for other purposes, that must us the 
 * DataHandlerInterface to process response data.
 * 
 * Pass this object implementation into the MessageHandlerInterface 
 * registerDataHandler(DataHandlerInterface dataHandler) 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2014 Happy Artist. All rights reserved.
 * 
 */
public interface DataHandlerInterface 
{
    /** process the message (Called by Multi Packet Message Implementations). */
    public void process(byte[] data);
    /** process the message. Called by direct stream read byte[]'s rather than making a new byte[] copy Object. dataLength was added due to issues with corrupt message data.If all ROS Messages were uncorrupted no need for this input would exist due to message lengths inside the ROS Message format, but this is an issue even on the turtlesim pose. This will likely be removed in favor of the byte[] only at a later date if a byte[] copy is made to the exact length. Until then a byte array copy will be allowed by implementers of this interface to avoid performance bottlenecks at the cost of increased complexity in the interim. */    
    public void process(byte[] data, int dataLength);
}
