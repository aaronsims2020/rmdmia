package org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.udpros;
import java.io.*;
import java.net.*;
import org.happy.artist.rmdmia.rcsm.RCSMException;
import org.happy.artist.rmdmia.rcsm.provider.CommunicationReceiverInterface;
import org.happy.artist.rmdmia.rcsm.provider.message.MessageHandlerInterface;
import org.happy.artist.rmdmia.rcsm.provider.message.MessageHandlerControllerInterface;
import org.happy.artist.rmdmia.rcsm.provider.message.DataProcessor;

/** UDPPublisherReceiver.java UDPPublisherReceiver for specified UDP server, port number & packet length.
 *  Must call initialize() after construction is completed, and before the Runnable start() 
 *  method. Each UDPROS Listener Port should use this class.
 * 
 * @author Happy Artist
 * 
 * @copyright Copyright© 2013 Happy Artist. All rights reserved.
 */
public class UDPPublisherReceiver implements Runnable, CommunicationReceiverInterface
{
    // boolean is Run = true, set to false to stop thread.
    private boolean isRun=true;
    private String hostname;
    private int port;
    private int packetLength;
    private byte[] receiveData;
    private DatagramSocket serverSocket;
    private DatagramPacket receivePacket;
    //Process incoming data packets
    // TODO: Find the right place to instatiate this Data Processor
    private UDPDataProcessor processor;
    private String threadName;
    private UDPROSPublisherCommunicator parent;

    public boolean isRunning()
    {
        return isRun;
    }
    
    public UDPPublisherReceiver(UDPROSPublisherCommunicator parent)
    {
        this.parent=parent;
    }
    
    /** In UDP Pass in the DatagramSocket to the Receiver rather than instantiate a second, because an instance in the Sender and Receiver will cause a Bind Exception saying address already in use. */  
    public void initialize(String hostname, int port, int packetLength, DatagramSocket serverSocket, String threadName, String initializerMessageHandlerClassname, String messageHandlerClassname, String tunerMessageHandlerClassname) throws SocketException, ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        this.processor = new UDPDataProcessor(initializerMessageHandlerClassname,messageHandlerClassname,tunerMessageHandlerClassname,threadName);
        // Set the UDPROSPublisherCommunicator messageHandlerController.
        parent.messageHandlerController=(MessageHandlerControllerInterface)processor;
        this.threadName=threadName;
        this.hostname=hostname;
        this.port=port;
        this.packetLength=packetLength;
        this.serverSocket = serverSocket;     
        this.receiveData = new byte[packetLength];
        this.receivePacket = new DatagramPacket(receiveData, receiveData.length); 
        // TODO: Never times out, add timeout support later on. 
        serverSocket.setSoTimeout(0);
        serverSocket.setReuseAddress(true);
        this.isRun=true;
    }
    
    public void stop()
    {
        this.isRun=false; 
        if(serverSocket.isBound())
        {
            try
            {
                serverSocket.close();
            }
            catch(Exception e)
            {
                System.out.println("Datagram Socket Thread was interupted for shutdown of UDPReceiver: " + e.getMessage());
            }
            finally
            {
                serverSocket.disconnect();
            }
        }
    }
    
    /** Shutdown this UDPPublisherReceiver. Only call this method on shutdown. Cannot be reinitialized. */
    public void shutdown()
    {
        this.isRun=false; 
        if(serverSocket.isBound())
        {
            try
            {
                serverSocket.close();
            }
            catch(Exception e)
            {
                
            }
            finally
            {
                serverSocket.disconnect();
            }
        }
        this.serverSocket=null;
        this.receivePacket=null;
        this.receiveData=null;
        this.hostname=null;
    }
    
    /** Recycle this class for use in a Cache Pool. */
    public void recycle()
    {
//throw new UnsupportedOperationException("Not supported.");        
    }
    
    /** Return the currently registered UDP port. */
    public int getReceivePort()
    {
        return serverSocket.getPort();
    }
    
    /** Return the hostname. */    
    public String getHostName()
    {
        return hostname;
    }
    
    /** Return the packet length. */    
    public int getPacketLength()
    {
        return packetLength;
    }
    
    // Thread run method.
    public void run()
    {
        while(isRun)
        {
            try
            {
// TODO: This may need a InputStreamReadBuffer like TCPROSSubscriberCommunicator if it passes by reference like the TCPROSSubscriberCommunicator.                
                // This nested while loop is intended to bypass speed issues of looping through Exception handling (test to verify this theory).
                while(isRun)
                {
                   // receive next packet.
                   serverSocket.receive(receivePacket);
                   // process the packet data.
                   processor.process(receivePacket.getData());
                }
            }
            catch(PortUnreachableException e)
            {
                // TODO: Implement logic to update the ROSNode the Port is unreachable, and the topic/service is not connected. Include retry logic.
                System.out.println("Exception in thread " + UDPPublisherReceiver.this.threadName);
                e.printStackTrace();                
            }
            catch(IOException e)
            {
                System.out.println("Exception in thread " + UDPPublisherReceiver.this.threadName);
                e.printStackTrace();
            }
            
        }
    }

    @Override
    public void start() throws RCSMException 
    {
        throw new RCSMException("UDPReceiver start method not supported.", new UnsupportedOperationException("Not supported."));
    }

    @Override
    public MessageHandlerInterface getMessageHandlerInterface() 
    {
       return processor.getMessageHandler();
    }
}
