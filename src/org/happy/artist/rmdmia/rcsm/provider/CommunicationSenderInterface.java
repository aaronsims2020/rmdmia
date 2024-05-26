package org.happy.artist.rmdmia.rcsm.provider;

import java.io.IOException;
import java.net.Socket;
import org.happy.artist.rmdmia.rcsm.RCSMException;

/** A protocol independent interface for a provider to sending data 
 * through the RCSM, via the implementing Sender.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright© 2013-2014 Happy Artist. All rights reserved.
 */
public interface CommunicationSenderInterface 
{
     /** Send message data. Cannot be called until the provider Sender 
      * initialize method is called.
      * 
      * @param data The byte[] data.
      * 
      * @throws IOException
      */
    public void send(byte[] data) throws IOException;
    
     /** Send message data. Cannot be called until the provider Sender 
      * initialize method is called.
      * 
      * @param data The byte[] data.
      * @param offset The int data offset.
      * @param length The int data length.
      * 
      * @throws IOException
      */
    public void send(byte[] data, int offset, int length) throws IOException;
    
    /** Send a message to the specific topic data processor connection, by 
     * data processor id. This send message is used to establish a publisher/subscriber
     * method, and is ignored once the TCPROS Connection Header, and latched 
     * message (if enabled) is exchanged. 
     * 
     * 
     * @param data The byte[] data.
     * @param data_processor_id The int data processor id.
     * 
     * @throws IOException
     */
    public void send(byte[] data, int data_processor_id) throws IOException;
    /** Return the protocol name.
     * 
     * @return String protocol name,
     */
    public String getProtocolName();  
    
    /** Shutdown this CommunicationSenderInterface implementation. Only call this method on shutdown. Cannot be reinitialized. */
    public void shutdown();
    
    /** Recycle this class for use in a Cache Pool. */
    public void recycle();

    /** Connect the provider connection, and start the receiver thread. 
     * Reserving the connect() method as a possible replacement for start().
     * 
     * @throws RCSMException
     */
    public void start() throws RCSMException;    

    /** Connect the provider connection.
     *  
     * @throws RCSMException
     */
    public void connect() throws RCSMException;
    
    /** Disconnect the provider connection. */
    public void disconnect();
    
    /** Return boolean is connected on socket. 
     * 
     * @return boolean is provider connected.
     */
    public boolean isConnected();
    
    /** Return boolean is running. This is important if the start method is called followed by a call to send, to prevent an exception due to null streams caused by a thread that did not complete its startup process before calling send. In a later release this may be separated to another interface.
     * 
     * @return boolean is provider running.
     */
    public boolean isRunning();    
    
    /** Return the hostname.
     * 
     * @return String hostname
     */    
    public String getRemoteHostName();
    
    /** Return the currently used port (if a port is being used), else 
     * return -1.
     * 
     * @return The set/in use port, or -1. 
     */
    public int getRemotePort(); 

    /** Return the currently used Socket. Only applies to Sender implementations 
     * using the TCP/IP Socket.
     * 
     * @return Socket
     */
    public Socket getSocket(); 
}
