package org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.udpros;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.happy.artist.rmdmia.rcsm.RCSMException;
import org.happy.artist.rmdmia.rcsm.provider.CommunicationReceiverInterface;
import org.happy.artist.rmdmia.rcsm.provider.message.MessageHandlerInterface;
import org.happy.artist.rmdmia.rcsm.provider.message.MessageHandlerControllerInterface;

/** UDPSubscriberReceiver.java UDPSubscriberReceiver for specified UDP server, port number & packet length.
 *  Must call initialize() after construction is completed, and before the Runnable start() 
 *  method. Each UDPROS Listener Port should use this class.
 * 
 * @author Happy Artist
 * 
 * @copyright Copyright© 2013-2014 Happy Artist. All rights reserved.
 */
public class UDPSubscriberReceiver implements Runnable, CommunicationReceiverInterface
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
    private UDPROSSubscriberCommunicator parent;
    // Class Logger define & instantiation
    private Logger logger = Logger.getLogger(UDPSubscriberReceiver.class.getName());    

    public boolean isRunning()
    {
        return isRun;
    }
    
    public boolean isConnected()
    {
        return serverSocket.isConnected();
    }
    
    public UDPSubscriberReceiver(UDPROSSubscriberCommunicator parent)
    {
        this.parent=parent;
    }
    
    /** In UDP Pass in the DatagramSocket to the Receiver rather than instantiate a second, because an instance in the Sender and Receiver will cause a Bind Exception saying address already in use. */  
    public void initialize(String hostname, int port, int packetLength, DatagramSocket serverSocket, String threadName, String initializerMessageHandlerClassname, String messageHandlerClassname, String tunerMessageHandlerClassname) throws SocketException, ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        this.processor = new UDPDataProcessor(initializerMessageHandlerClassname,messageHandlerClassname,tunerMessageHandlerClassname,threadName);
        // Set the UDPROSCommunicator messageHandlerController.
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
                logger.info("Datagram Socket Thread was interupted for shutdown of UDPReceiver: " + e.getMessage());
            }
            finally
            {
                serverSocket.disconnect();
            }
        }
    }
    
    /** Shutdown this UDPSubscriberReceiver. Only call this method on shutdown. Cannot be reinitialized. */
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
        return serverSocket.getLocalPort();
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
  //                  System.out.println("Inside UDPReceiver run method 2. ServerSocket Local port: " + serverSocket.getLocalPort());
                   // receive next packet.
                   serverSocket.receive(receivePacket);
//                   System.out.println("Calling process on UDP Data..." + BytesToHex.bytesToHex(receivePacket.getData()));                   
                   // process the packet data.
                   processor.process(receivePacket.getData());
 //                  System.out.println("Processed incoming UDP Data...");
                }
            }
            catch(NullPointerException e)
            {
                logger.info("Received UDP Packet on port, before connection ID is set. Throwing out this packet...");
            }            
            catch(PortUnreachableException e)
            {
                // TODO: Implement logic to update the ROSNode the Port is unreachable, and the topic/service is not connected. Include retry logic.
                    // Class Logger define & instantiation
            logger.warning(UDPSubscriberReceiver.this.threadName + ": Port Unreachable Exception " + e.getMessage());
            }
            catch(IOException e)
            {
                logger.log(Level.WARNING, UDPSubscriberReceiver.this.threadName + ": IOException on UDPSubscriberReceiver.", e);
            }
            
        }
    }

    @Override
    public void start() throws RCSMException 
    {
        throw new RCSMException(UDPSubscriberReceiver.this.threadName + ":  UDPReceiver start method not supported.", new UnsupportedOperationException("Not supported."));
    }

    @Override
    public MessageHandlerInterface getMessageHandlerInterface() 
    {
       return processor.getMessageHandler();
    }
    
    /** Return the UDPDataProcessor. */
    public UDPDataProcessor getDataProcessor()
    {
        return processor;
    }
}
