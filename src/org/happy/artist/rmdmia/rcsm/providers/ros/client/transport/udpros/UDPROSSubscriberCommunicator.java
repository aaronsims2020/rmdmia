package org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.udpros;
import java.io.*;
import java.net.*;
import org.happy.artist.rmdmia.rcsm.RCSMException;
import org.happy.artist.rmdmia.rcsm.provider.CommunicationReceiverInterface;
import org.happy.artist.rmdmia.rcsm.provider.CommunicationSenderInterface;
import org.happy.artist.rmdmia.rcsm.provider.message.DataProcessor;
import org.happy.artist.rmdmia.rcsm.provider.message.MessageHandlerControllerInterface;
import org.happy.artist.rmdmia.rcsm.provider.message.MessageHandlerInterface;

/** UDPROSSubscriberCommunicator.java UDPROSSubscriberCommunicator client for specified UDP server, port number & packet length.
 *  Must call initialize() after construction is completed, and before the Runnable start() 
 *  method. Each UDPROS Publisher connection should use this class to send data to the specified *  host/port, and UDP packet length.
 * 
 * @author Happy Artist
 * 
 * @copyright Copyright© 2013-2015 Happy Artist. All rights reserved.
 */
public class UDPROSSubscriberCommunicator implements CommunicationSenderInterface, CommunicationReceiverInterface
{
// 10/7/2013 TODO: Implement Receiver inside this class, and use the same DatagramSocket.    
    private String hostname;
    private int port;
    private int packetLength;
    private byte[] sendData;
    // TODO: check if calling send on this running as receiver in another Thread is Threadsafe.
    private DatagramSocket clientSocket;
    private DatagramPacket senderPacket;
    // UDPSubscriberReceiver implementation - remember to call initialize on UDPSubscriberReceiver.
    private UDPSubscriberReceiver udpReceiver= new UDPSubscriberReceiver(this); 
    private Thread listener = new Thread(udpReceiver); 
    private boolean isPersistant;
    private String threadName;
    // define the local port (server listener port) 
    private int localPort;
    // Message Handler Class Name is the class redirector property in topic_register.eax, if no property is specified the default sensor queue will be used.
    private String messageHandlerClassname;
    private String initializerMessageHandlerClassname;
    private String tunerMessageHandlerClassname;  
    // topic variable
    private String topic;
    private boolean isTopic;
    
    // getMessageHandlerController() method variables.
    protected MessageHandlerControllerInterface messageHandlerController;
    public UDPROSSubscriberCommunicator(String topic, boolean isTopic) throws SocketException {      
        // set the topic/service name
        this.topic=topic;
        this.isTopic=isTopic;  
    }    
   
     /** Specify -1 on the port to dynamically allocate a port at initialization. */
    public void initialize(String hostname, int port, int packetLength, final String threadName, boolean isPersistant, String initializerMessageHandlerClassname, String messageHandlerClassname, String tunerMessageHandlerClassname) throws SocketException, UnknownHostException, ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        this.threadName=threadName;
        this.hostname=hostname;
        this.messageHandlerClassname=messageHandlerClassname;
        this.initializerMessageHandlerClassname=initializerMessageHandlerClassname;
        this.tunerMessageHandlerClassname=tunerMessageHandlerClassname;          
        this.packetLength=packetLength;
        if(port==-1)
        {
            this.clientSocket = new DatagramSocket();
            this.port=clientSocket.getLocalPort();
//            System.out.println("Initialized UDP DatagramSocket..., Port: " + port);
        }  
        else
        {
            this.clientSocket = new DatagramSocket(port);            
            this.port=clientSocket.getLocalPort();
//            System.out.println("Initialized UDP DatagramSocket..., Port: " + port);
        }
                   
        clientSocket.setReuseAddress(true);
        this.sendData = new byte[packetLength];
        this.isPersistant=isPersistant;
        this.senderPacket = new DatagramPacket(sendData, sendData.length);    
// TODO: Implement TopicRegistry variable settings pre-instantiation.        
        // initialize UDPSubscriberReceiver
        udpReceiver.initialize(hostname, port, packetLength, clientSocket, threadName, initializerMessageHandlerClassname, messageHandlerClassname, tunerMessageHandlerClassname);
        // set thread name.
        listener.setName(threadName);        
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
    
    /** Shutdown this UDPROSSubscriberCommunicator. Only call this method on shutdown. Cannot be reinitialized. */
    public void shutdown()
    {
        if(clientSocket.isConnected()==true)
        {
            clientSocket.disconnect();
        }
        // Set the UDPROSSubscriberCommunicator messageHandlerController
        this.messageHandlerController=null;        
        this.clientSocket=null;
        this.senderPacket=null;
        this.sendData=null;
        udpReceiver.shutdown();
    }
    
    /** Disconnect the UDP Socket. */
    public void disconnect()
    {
        if(clientSocket.isConnected()==true)
        {
            clientSocket.disconnect();
        }
        udpReceiver.stop();
    }    

    /** Recycle this class for use in a Cache Pool. */
    public void recycle()
    {
        udpReceiver.recycle();
    }
    
    /** Return the currently registered UDP port. */
    public int getRemotePort()
    {
        return clientSocket.getPort();
    }
    
    /** Return the hostname. */    
    public String getRemoteHostName()
    {
        return hostname;
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
           return udpReceiver.isRunning(); 
        }
        return false;
    }
    
    /** only call send, after UDPROSSubscriberCommunicator initialize method is called at once at Object initialization time. Keep in mind this data array should match the length of the packetLength. */
    public void send(byte[] data) throws IOException
    {
        senderPacket.setData(data);
        clientSocket.send(senderPacket);
    }
    
    /** only call send, after UDPROSSubscriberCommunicator initialize method is called at once at Object initialization time. Keep in mind this data array should match the length of the packetLength. */
    public void send(byte[] data, int offset, int length) throws IOException
    {
        senderPacket.setData(data, offset, length);
        clientSocket.send(senderPacket);
    }   

    private final static String PROTOCOL_UDPROS="UDPROS";
        /** Return the protocol name of this CommunicationSenderInterface implementation. */
    public String getProtocolName() 
    {
        return PROTOCOL_UDPROS;
    }    

    @Override
    public boolean isRunning() {
        return udpReceiver.isRunning();
    }

    @Override
    public String getHostName() {
        return udpReceiver.getHostName();
    }

    @Override
    public int getReceivePort() {
        return udpReceiver.getReceivePort();
    }

    @Override
    public void stop() {
        udpReceiver.stop();
    }

    /** Connect & Start the Receiver Thread. */
    @Override
    public void start() throws RCSMException
    {
        // start the listener thread
//        listener.start();// moved listener start to avoid any potential NullPointerException issues. Was an issue on TCP, probably will not effect UDP.  
        try
        {
            // TODO: There is evidence start() is being called multiple times, and this must be figured out at some point in the future. It appears to only happen when Publisher is enabled, and not in the first start, but second and on and on. May be related to registerPublisher. or registerSubscriber... For now it is working...
if(listener!=null&&!listener.isAlive())
{
    listener.start();
} 
//else
//{
//    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");    
//}
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        //catch(UnknownHostException e)
       // {
       //     throw new RCSMException("Unknown Host called in start method of UDPROSCommunicator. hostname:" + hostname + ":" + String.valueOf(port),e);
       // }
    }

    @Override
    public void send(byte[] data, int data_processor_id) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
       return ((DataProcessor)messageHandlerController).getMessageHandler();
    }
    
    /** Sets the UDPReceiver Connection ID. */
    public void setConnectionID(int connectionID) throws UnsupportedEncodingException
    {
        //System.out.println("UDP Connection ID: " + JavaToROSTypes.getInt32(connectionID));
        udpReceiver.getDataProcessor().setConnectionID(Integer.toHexString(connectionID));
    }
}
