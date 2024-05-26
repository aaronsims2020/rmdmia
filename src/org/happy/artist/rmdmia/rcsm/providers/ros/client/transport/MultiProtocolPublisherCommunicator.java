package org.happy.artist.rmdmia.rcsm.providers.ros.client.transport;

import java.io.IOException;
import java.net.Socket;
import org.happy.artist.rmdmia.rcsm.RCSMException;
import org.happy.artist.rmdmia.rcsm.provider.CommunicationReceiverInterface;
import org.happy.artist.rmdmia.rcsm.provider.CommunicationSenderInterface;
import org.happy.artist.rmdmia.rcsm.provider.message.MessageHandlerInterface;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.tcpros.TCPROSPublisherCommunicator;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.udpros.UDPROSPublisherCommunicator;

/** MultiProtocolPublisherCommunicator.java Simultaneously publishes to 
 * TCPROS & UDPROS at the cost of an extra layer of methods between both 
 * publisher objects. This class manages publishing of TCPROS, and UDPROS 
 * protocols on a single CommunicationSenderInterface. Due to performance hits of 
 * running an extra layer of method calls between 2 different communication objects
 * this class will only be used when a Publisher is publishing to 2 protocols at once,
 *  Must call initialize() after construction is completed. An assumption exists that 
 * users of UDP desire lower latency therefore, all send communications are iteratively 
 * performed on UDPROS followed by TCPROS. 
 * 
 * @author Happy Artist
 * 
 * @copyright Copyright© 2014 Happy Artist. All rights reserved.
 */
public class MultiProtocolPublisherCommunicator  implements CommunicationSenderInterface, CommunicationReceiverInterface
{
    private UDPROSPublisherCommunicator udpSender;
    private TCPROSPublisherCommunicator tcpSender;    
    
    public MultiProtocolPublisherCommunicator(TCPROSPublisherCommunicator tcpSender, UDPROSPublisherCommunicator udpSender)
    {
        this.udpSender=udpSender;
        this.tcpSender=tcpSender;
    }
    
    /** Return the TCPROSPublisherCommunicator. */
    public TCPROSPublisherCommunicator getTCPPublisherCommunicator()
    {
        return tcpSender;
    }    

    /** Return the UDPROSPublisherCommunicator. */    
    public UDPROSPublisherCommunicator getUDPPublisherCommunicator()
    {
        return udpSender;
    }
    
    @Override
    public void send(byte[] data) throws IOException 
    {
        udpSender.send(data);
        tcpSender.send(data);
    }

    @Override
    public void send(byte[] data, int offset, int length) throws IOException 
    {
        udpSender.send(data, offset, length);
        tcpSender.send(data, offset, length);
    }

    @Override
    public void send(byte[] data, int data_processor_id) throws IOException 
    {
        udpSender.send(data, data_processor_id);
        tcpSender.send(data, data_processor_id);
    }

    @Override
    public String getProtocolName() {
        return "UDPROS,TCPROS";
    }

    @Override
    public void shutdown() 
    {
        udpSender.shutdown();
        tcpSender.shutdown();
        this.udpSender=null;
        this.tcpSender=null;        
    }

    @Override
    public void recycle() 
    {
        udpSender.recycle();
        tcpSender.recycle();
    }

    @Override
    public void start() throws RCSMException {
        // Do nothing... Senders are passed in via constructor as references.
    }

    /** Call the TCPROSPublisherCommunicator connect(). */
    public void connect() throws RCSMException 
    {
        tcpSender.connect();
    }

    /** Call the TCPROSPublisherCommunicator disconnect(). */
    public void disconnect() 
    {
        tcpSender.disconnect();
    }

    /** Call the TCPROSPublisherCommunicator isConnected(). */
    public boolean isConnected() 
    {
        return tcpSender.isConnected();
    }

    /** Call the TCPROSPublisherCommunicator isRunning(). */
    public boolean isRunning() {
        return tcpSender.isRunning();
    }

    /** Return the TCPROSPublisherCommunicator remote hostname. */
    public String getRemoteHostName() {
        return tcpSender.getRemoteHostName();
                }

    /** Return the TCPROSPublisherCommunicator remote port. */
    public int getRemotePort() {
        return tcpSender.getRemotePort();
    }

    /** Return the TCPROSPublisherCommunicator getSocket(). */
    public Socket getSocket() {
        return tcpSender.getSocket();
    }

    /** Return the TCPROSPublisherCommunicator hostname. */
    public String getHostName() {
        return tcpSender.getHostName();
    }

        /** Call the TCPROSPublisherCommunicator getReceivePort(). */
    public int getReceivePort() 
    {
        return tcpSender.getReceivePort();
    }

    /** Call the TCPROSPublisherCommunicator stop(). */
    public void stop() 
    {
        tcpSender.stop();
    }

    /** Call the TCPROSPublisherCommunicator getMessageHandlerInterface(). */
    public MessageHandlerInterface getMessageHandlerInterface() 
    {
        return tcpSender.getMessageHandlerInterface(); 
    }

    
}
