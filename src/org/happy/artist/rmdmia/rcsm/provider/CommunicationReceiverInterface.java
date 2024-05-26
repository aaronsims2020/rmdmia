package org.happy.artist.rmdmia.rcsm.provider;

import org.happy.artist.rmdmia.rcsm.provider.message.MessageHandlerInterface;
import org.happy.artist.rmdmia.rcsm.RCSMException;

/** A protocol independent interface for a provider Receiver to receive 
 * data through the RCSM.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright© 2013-2014 Happy Artist. All rights reserved.
 */
public interface CommunicationReceiverInterface 
{
   /** Shutdown this CommunicationReceiverInterface implementation. 
    * Only call this method on shutdown. Cannot be reinitialized. 
    */
    public void shutdown();
    
    /** Recycle this class for use in a Cache Pool. */
    public void recycle();
    
    /** Return boolean is connected on socket. 
     * 
     * @return boolean Is connected.
     */
    public boolean isRunning();
    
    /** Return the hostname. 
     * 
     * @return String hostname
     */    
    public String getHostName();
    
    /** Return the currently registered socket port.
     * 
     * @return int receiver port. -1 no port.
     */
    public int getReceivePort();
    
    /** Stop the associated provider control identifier Receiver thread 
     * from running, and shutdown the socket. 
     */
    public void stop();    
    
    /** Start the receiver, and connect to the remote socket. 
     * 
     * @throws RCSMException 
     */ 
    public void start() throws RCSMException;   
    
    /** Return the MessageHandlerInterface. */ 
    public MessageHandlerInterface getMessageHandlerInterface();    
}
