package org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.udpros;
import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.happy.artist.rmdmia.rcsm.RCSMException;
import org.happy.artist.rmdmia.rcsm.provider.CommunicationReceiverInterface;
import org.happy.artist.rmdmia.rcsm.provider.CommunicationSenderInterface;
import org.happy.artist.rmdmia.rcsm.provider.message.MessageHandlerControllerInterface;
import org.happy.artist.rmdmia.rcsm.provider.message.MessageHandlerInterface;

/** UDPROSCommunicator.java UDPROSCommunicator client for specified UDP server, port number & packet length.
 *  Must call initialize() after construction is completed, and before the Runnable start() 
 *  method. Each UDPROS Publisher connection should use this class to send data to the specified *  host/port, and UDP packet length.
 * 
 * @author Happy Artist
 * 
 * @copyright Copyright© 2013-2014 Happy Artist. All rights reserved.
 */
public class UDPROSPublisherCommunicator implements CommunicationSenderInterface, CommunicationReceiverInterface
{
// 10/7/2013 TODO: Implement Receiver inside this class, and use the same DatagramSocket.    
    private String hostname;
    private int port;
    private int packetLength;
    private byte[] sendData;
    // TODO: check if calling send on this running as receiver in another Thread is Threadsafe.
    private DatagramSocket clientSocket;
//    private DatagramPacket senderPacket;
    // UDPPublisherReceiver implementation - remember to call initialize on UDPPublisherReceiver.
 //   private UDPPublisherReceiver UDPPublisherReceiver= new UDPPublisherReceiver(this); 
//    private Thread listener = new Thread(UDPPublisherReceiver); 
    private boolean isPersistant;
    private String threadName;
    // Constructor variables
 //   private String messageHandlerClassname;
//    private String initializerMessageHandlerClassname;
//    private String tunerMessageHandlerClassname;  
    // topic variable
    private String topic;
    private boolean isTopic;  
    private boolean isRun=false;
    
    // Last Message Sent for Latching, if length is 0 no messages sent yet.
    public byte[] last_msg= new byte[0];
    // Define and instantiate Logger
    private Logger logger = Logger.getLogger(UDPROSPublisherCommunicator.class.getName());    
    //
    private int localPort=-1;
    // getMessageHandlerController() method variables.
    protected MessageHandlerControllerInterface messageHandlerController;
    // Send method variables
    private int sub_count;    
 
    public UDPROSPublisherCommunicator(String topic, boolean isTopic) {      
        // set the topic/service name
        this.topic=topic;
        this.isTopic=isTopic;
        try {
            // initialize the client socket.
            this.clientSocket = new DatagramSocket();
        clientSocket.setReuseAddress(true);
                } catch (SocketException ex) {
            Logger.getLogger(UDPROSPublisherCommunicator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }        
    
    public void initialize(String hostname, int port, int packetLength, String threadName, boolean isPersistant) throws SocketException, UnknownHostException, ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        // Set isRun to true on initialization
        this.isRun=true;
//        this.messageHandlerClassname=messageHandlerClassname;
//        this.initializerMessageHandlerClassname=initializerMessageHandlerClassname;
//        this.tunerMessageHandlerClassname=tunerMessageHandlerClassname;      
        this.addresses=new DatagramPacket[0];
        this.threadName=threadName;
        this.hostname=hostname;
        this.port=port;
        this.packetLength=packetLength;
        //this.clientSocket = new DatagramSocket(port);
        //clientSocket.setReuseAddress(true);
        this.sendData = new byte[packetLength];
        this.isPersistant=isPersistant;
        //this.senderPacket = new DatagramPacket(sendData, sendData.length);    
// TODO: Implement TopicRegistry variable settings pre-instantiation.        
        // initialize UDPPublisherReceiver
//        UDPPublisherReceiver.initialize(hostname, port, packetLength, clientSocket, threadName, initializerMessageHandlerClassname, messageHandlerClassname, tunerMessageHandlerClassname);
        // set thread name.
//        listener.setName(threadName);        
    }

    // The SocketAddress array of Published Subscribers IP & Port# for constructing DatagramPackets.
    private DatagramPacket[] addresses=new DatagramPacket[0];
    
    /** Return the Topic Subscribers SocketAddress array. */
    public DatagramPacket[] getConnectedSubscribers()
    {
        return addresses;
    }
    
    private InetSocketAddress tmpSocketAddress;
    /** Add new subscriber, and return the addresses array indice of the new added subscriber SocketAddress. -1 for SocketException. If subscriber already exists the subscriber index will be returned. */
    public synchronized int addSubscriber(String subscriber_hostname, int subscriber_port, int packet_length)
    {
        // Check if the address is already registered and if so return the index it is in.
        if(addresses.length>0)
        {
            for(int i=0;i<addresses.length;i++)
            {
                this.tmpSocketAddress=((InetSocketAddress)addresses[i].getSocketAddress());
                if(tmpSocketAddress.getPort()==subscriber_port&&tmpSocketAddress.getHostName().equals(subscriber_hostname))
                {
                    return i;
                }
            }
        }
        try
        {
            this.addresses=Arrays.copyOf(addresses, addresses.length + 1);
            addresses[addresses.length-1]=new DatagramPacket(sendData, packet_length, new InetSocketAddress(subscriber_hostname, subscriber_port));
        }
        catch(IllegalArgumentException e)
        {
            logger.log(Level.WARNING,"Port parameter is outside the range of valid port values, or if the hostname parameter is null on UDPROSPublisherCommnunicator addSubscriber. Host address: " + subscriber_hostname + ", Port: " + String.valueOf(subscriber_port),e);
        }
        catch(SecurityException e)
        {
            logger.log(Level.WARNING,"Security manager is present and permission to resolve the host name is denied on UDPROSPublisherCommnunicator addSubscriber. Hostname: " + subscriber_hostname + ", Port: " + String.valueOf(subscriber_port),e);
        } catch (SocketException ex) {
            Logger.getLogger(UDPROSPublisherCommunicator.class.getName()).log(Level.SEVERE, "SocketException constructing DataPacket on UDPROSPublisherCommnunicator addSubscriber. Host address: " + subscriber_hostname + ", Port: " + String.valueOf(subscriber_port), ex);
        }
        return addresses.length-1;
    }


    // removeSubscriber method variables.
    // copy used in the removeSubscriber method. Calls shutdown on SocketThread before removing from array    
    private DatagramPacket[] copy;
    private InetSocketAddress tempInetSocketAddress;
    /** remove subscriber by id. */
    public synchronized void removeSubscriber(String subscriber_hostname, int subscriber_port)
    {
        for (int i = 0; i < addresses.length; i++)
        {
            this.tempInetSocketAddress=((InetSocketAddress)addresses[i].getSocketAddress());
            if(tempInetSocketAddress.getHostName().equalsIgnoreCase(subscriber_hostname)&&tempInetSocketAddress.getPort()==subscriber_port)
            {
                this.copy = new DatagramPacket[addresses.length-1];
                System.arraycopy(addresses, 0, copy, 0, i);
                System.arraycopy(addresses, i+1, copy, i, addresses.length-i-1);
                this.addresses=copy;
                // destroy the copy reference
                this.copy=null;
                this.tempInetSocketAddress=null;
                return;
            }
        }
        this.tempInetSocketAddress=null;
    }
   
    
    /** Return the MessageHandlerControllerInterface. The Controller interface
     * sets the message handler on the message receiver to one of 3 pre-configured 
     * message handler classes (includes the tuner).
     * 
     * @return MessageHandlerControllerInterface
     * @throws RCSMException if the initialize method has not been called.
     */
    public MessageHandlerControllerInterface getMessageHandlerController() throws RCSMException
    {
        if(messageHandlerController!=null)
        {
            return messageHandlerController;
        }
        else
        {
            throw new RCSMException("initialize method has not been called for CommunicationReceiverInterface thread: ".concat(threadName), new Exception());
        }
    }    
    
    /** Shutdown this UDPROSCommunicator. Only call this method on shutdown. Cannot be reinitialized. */
    public void shutdown()
    {
        this.isRun=false;
        try
        {
            if(clientSocket.isConnected()==true)
            {
                clientSocket.disconnect();
            }
        }
        catch(Exception e)
        {
            
        }
        finally
        {
            clientSocket.close();            
        }
        // Set the UDPROSCommunicator messageHandlerController
        this.messageHandlerController=null;
        this.addresses=null;
        this.clientSocket=null;
        this.sendData=null;
    }
    
    /** Disconnect the UDP Socket. */
    public void disconnect()
    {
        
        if(clientSocket.isConnected()==true)
        {
            clientSocket.disconnect();
        }
       // UDPPublisherReceiver.stop();
    }    

    /** Recycle this class for use in a Cache Pool. */
    public void recycle()
    {
        //UDPPublisherReceiver.recycle();
    }
    
    /** Return the currently registered UDP port. */
    public int getRemotePort()
    {
       // return clientSocket.getPort();
        return -1;
    }
    
    /** Return the hostname. */    
    public String getRemoteHostName()
    {
        return null;
    }
    
    /** Return the packet length. */    
    public int getPacketLength()
    {
        return packetLength;
    }
    
    /** Return boolean is connected on socket. */
    public boolean isConnected()
    {
        if(clientSocket==null)
        {
            
            return false;
        }
        //System.out.println("UDPReceiver isConnected(): " + udpReceiver.isConnected());
        // UDP does not support a connection state, therefore if the Receiver is listening it will return is connected.
        if(clientSocket.isClosed()==false)
        {
           return true; 
        }
        return false;
    }
    
    /** only call send, after UDPROSCommunicator initialize method is called at once at Object initialization time. Keep in mind this data array should match the length of the packetLength. */
    public void send(byte[] data) throws IOException
    {
        // set the last_msg for latching
        this.last_msg=data;
        // set sub_count to 0
        this.sub_count=0;
        while(sub_count<addresses.length)
        {            
            try
            {
                addresses[sub_count].setData(data);
                clientSocket.send(addresses[sub_count]);
            }
            catch(Exception e)
            {
                logger.log(Level.WARNING, "Topic UDPROS Publisher Exception in Subscriber Connection for: ".concat(topic.concat(" ")).concat(String.valueOf(sub_count)),e);
            }    
            this.sub_count=sub_count + 1;    
        }
    }
    
    /** only call send, after UDPROSCommunicator initialize method is called at once at Object initialization time. Keep in mind this data array should match the length of the packetLength. */
    public void send(byte[] data, int offset, int length) throws IOException
    {
        
        // set the last_msg for latching
        this.last_msg=data;
        // set sub_count to 0
        this.sub_count=0;
        while(sub_count<addresses.length)
        {            
            try
            {     
                addresses[sub_count].setData(data, offset, length);
                clientSocket.send(addresses[sub_count]);
            }
            catch(Exception e)
            {
                logger.log(Level.WARNING, "Topic UDPROS Publisher Exception in Subscriber Connection for: ".concat(topic.concat(" ")).concat(String.valueOf(sub_count)),e);
            }    
            this.sub_count=sub_count + 1;    
        }        
    }   

    private final static String PROTOCOL_UDPROS="UDPROS";
        /** Return the protocol name of this CommunicationSenderInterface implementation. */
    public String getProtocolName() 
    {
        return PROTOCOL_UDPROS;
    }    

    @Override
    public boolean isRunning() {
        return isRun;
        //return UDPPublisherReceiver.isRunning();
    }

    @Override
    public String getHostName() {
        return ((InetSocketAddress)clientSocket.getLocalSocketAddress()).getHostName();
    }

    @Override
    public int getReceivePort() {
        return ((InetSocketAddress)clientSocket.getLocalSocketAddress()).getPort();
    }

    @Override
    public void stop() {
       
        // UDPPublisherReceiver.stop();
    }

    /** Connect & Start the Receiver Thread. */
    @Override
    public void start() throws RCSMException
    {
        // start the listener thread
//        listener.start();// moved listener start to avoid any potential NullPointerException issues. Was an issue on TCP, probably will not effect UDP.  
 //       try
//        {
//            clientSocket.connect(InetAddress.getByName(hostname), port);
   //         listener.start();
//        }
//        catch(UnknownHostException e)
//        {
//            throw new RCSMException("Unknown Host called in start method of UDPROSCommunicator. hostname:" + hostname + ":" + String.valueOf(port),e);
//        }
    }

    @Override
    public void send(byte[] data, int data_processor_id) throws IOException {
// TODO: Implement multiple UDP Subscribers.
                // set the last_msg for latching
        this.last_msg=data;
        try
        {
            addresses[data_processor_id].setData(data);
            clientSocket.send(addresses[data_processor_id]);
        }
        catch(Exception e)
        {
            logger.log(Level.WARNING, "Topic UDPROS Publisher Exception in Subscriber Connection for: ".concat(topic.concat(" ")).concat(String.valueOf(sub_count)),e);
        }   
    }

    @Override
    public Socket getSocket() {
        throw new UnsupportedOperationException("Not supported on UDPROSCommunicator.");
    }

    @Override
    public void connect() throws RCSMException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public MessageHandlerInterface getMessageHandlerInterface() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
