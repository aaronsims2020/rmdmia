package org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.tcpros;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.happy.artist.rmdmia.rcsm.RCSMException;

import org.happy.artist.rmdmia.rcsm.provider.CommunicationReceiverInterface;
import org.happy.artist.rmdmia.rcsm.provider.CommunicationSenderInterface;
import org.happy.artist.rmdmia.rcsm.provider.message.MessageHandlerControllerInterface;
import org.happy.artist.rmdmia.rcsm.providers.ros.ROSNode;
import org.happy.artist.rmdmia.rcsm.provider.message.MessageHandlerInterface;
import org.happy.artist.rmdmia.rcsm.provider.message.DataProcessor;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.util.InputStreamReadBuffer;

/** TCPROSSubscriberCommunicator.java TCPROSSubscriberCommunicator client for specified TCP 
 * server, port number & packet length.
 * Must call initialize() after construction is completed, and before the Runnable 
 * start() method. Each TCPROS Publisher connection should use this class to 
 * send data to the specified host/port.
 * 
 * @author Happy Artist
 * 
 * @copyright Copyright© 2013-2014 Happy Artist. All rights reserved.
 */
public class TCPROSSubscriberCommunicator implements CommunicationSenderInterface, CommunicationReceiverInterface
{
    private final static String PROTOCOL_TCPROS="TCPROS";
    private String hostname;
    private int port;
    private byte[] sendData;
    private Socket socket;
    private OutputStream os;
    private InputStream is;
    private byte[] readBytes;
    private byte[] readData;
    private int lastBlockSize=2048;
    private int bLength;    
    private InetSocketAddress addr;
    private boolean isRunning=false;
    private boolean tcp_nodelay;
    private boolean keep_alive;    
    private Thread listener;    
    private String threadName;
    // Message Handler Class Name is the class redirector property in topic_register.eax, if no property is specified the default sensor queue will be used.
    private String messageHandlerClassname;
    private String initializerMessageHandlerClassname;
    private String tunerMessageHandlerClassname;    
    // InputStreamReadBuffer
    private InputStreamReadBuffer isbr;
    private String topic;
    private boolean isTopic;
    // Reference to ROSNode
    private ROSNode rosNode;    

    public TCPROSSubscriberCommunicator(String initializerMessageHandlerClassname,String messageHandlerClassname, String tunerMessageHandlerClassname, String topic, boolean isTopic, ROSNode rosNode) {
        this.rosNode=rosNode;
        this.messageHandlerClassname=messageHandlerClassname;
        this.initializerMessageHandlerClassname=initializerMessageHandlerClassname;
        this.tunerMessageHandlerClassname=tunerMessageHandlerClassname;        
        // set the topic/service name
        this.topic=topic;
        this.isTopic=isTopic;
    }

    // getMessageHandlerController() method variables.
    private MessageHandlerControllerInterface messageHandlerController;
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

    public DataProcessor dataProcessor;
    public void initialize(String hostname, int port, int blockSize, boolean tcp_nodelay, boolean keep_alive, String threadName) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        this.isbr = new InputStreamReadBuffer(blockSize);
        this.threadName=threadName;
        // Set the read byte[] block size
        this.lastBlockSize=blockSize;
        this.readData = new byte[blockSize];
        this.tcp_nodelay=tcp_nodelay;
        this.keep_alive=keep_alive;
        // Load the listener here instead of in the constructor due to issue with blocksize not being set preceding this call.
        this.listener = new Thread(new Runnable() 
        {
            private TCPDataProcessor processor = new TCPDataProcessor(lastBlockSize,TCPROSSubscriberCommunicator.this.initializerMessageHandlerClassname,TCPROSSubscriberCommunicator.this.messageHandlerClassname, TCPROSSubscriberCommunicator.this.tunerMessageHandlerClassname, TCPROSSubscriberCommunicator.this.threadName, TCPROSSubscriberCommunicator.this.topic, TCPROSSubscriberCommunicator.this.isTopic,TCPROSSubscriberCommunicator.this,TCPROSSubscriberCommunicator.this.socket,0);
            @Override
            public void run() 
            {
                // Set the TCPROSSubscriberCommunicator messageHandlerController
                TCPROSSubscriberCommunicator.this.messageHandlerController=(MessageHandlerControllerInterface)processor;
                // Set the TCPROSSubscriberCommunicator isRunning to true.
                TCPROSSubscriberCommunicator.this.isRunning=true;
                while(isRunning)
                {
                    try
                    {
                        while ((bLength = is.read(readData=isbr.checkout())) != -1) 
                        {
                            // Read bytes, and write to byte[]
                            processor.process(readData,bLength);
                            if(processor.isNewMessage)
                            {
                                  // If process message process completed call reset on InputStreamReadBuffer. Setting checkoutCount to 0 is same as calling method but less overhead.
                                  isbr.checkoutCount=0;
                            }
                        }  
                    }
                    catch(SocketException e)
                    {
                     //   disconnect();
                        TCPROSSubscriberCommunicator.this.isRunning=false;
                        System.out.println("TCPROSSubscriberCommunicator: called disconnect()");                                   System.out.println("TCPROSSubscriberCommunicator: SocketException in thread: " + TCPROSSubscriberCommunicator.this.threadName + ", called disconnect on listener thread.");                  
                    }                    
                    catch(Exception e)
                    {
                        System.out.println("Exception in thread: " + TCPROSSubscriberCommunicator.this.threadName);
                        e.printStackTrace();
                    }
                }
            }
        });        
        this.hostname=hostname;
        this.port=port;
        this.socket = new Socket();
        socket.setReuseAddress(true);
        // default to true
        socket.setTcpNoDelay(tcp_nodelay);
        // default to true
        socket.setKeepAlive(keep_alive);
        socket.setPerformancePreferences(0, 1, 2);       
        // set thread name.
 //       this.threadName=threadName;
        listener.setName(threadName);
        // start the listener thread
        //listener.start();
    }
        
    /** Called to reinitialize the listener and socket for connection. Input 
     * variables were assigned in initialize, and this method requires no input
     * parameters.
     */ 
    public void reinitialize() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        // Load the listener here.
        this.listener = new Thread(new Runnable() 
        {
            private TCPDataProcessor processor = new TCPDataProcessor(lastBlockSize,TCPROSSubscriberCommunicator.this.initializerMessageHandlerClassname,TCPROSSubscriberCommunicator.this.messageHandlerClassname, TCPROSSubscriberCommunicator.this.tunerMessageHandlerClassname, TCPROSSubscriberCommunicator.this.threadName, TCPROSSubscriberCommunicator.this.topic, TCPROSSubscriberCommunicator.this.isTopic,TCPROSSubscriberCommunicator.this,TCPROSSubscriberCommunicator.this.socket,0);
            
            @Override
            public void run() 
            {
                // Set the TCPROSServiceCommunicator messageHandlerController
                TCPROSSubscriberCommunicator.this.messageHandlerController=(MessageHandlerControllerInterface)processor;
                // Set the TCPROSServiceCommunicator isRunning to true.
                TCPROSSubscriberCommunicator.this.isRunning=true;
                while(isRunning)
                {
                    try
                    {
                        while ((bLength = is.read(readData=isbr.checkout())) != -1) 
                        {
                            // Read bytes, and write to byte[]
                            processor.process(readData,bLength);
                            if(processor.isNewMessage)
                            {
                                  // If process message process completed call reset on InputStreamReadBuffer. Setting checkoutCount to 0 is same as calling method but less overhead.
                                  isbr.checkoutCount=0;
                            }
                        }  
                    }
                    catch(SocketException e)
                    {
                        TCPROSSubscriberCommunicator.this.isRunning=false;
                    }                    
                    catch(Exception e)
                    {
                        System.out.println("Exception in thread: " + TCPROSSubscriberCommunicator.this.threadName);
                        e.printStackTrace();
                    }
                }
            }
        });        
        this.socket = new Socket();
        socket.setReuseAddress(true);
        // default to true
        socket.setTcpNoDelay(tcp_nodelay);
        // default to true
        socket.setKeepAlive(keep_alive);
        socket.setPerformancePreferences(0, 1, 2);       
        // set thread name.
 //       this.threadName=threadName;
        listener.setName(threadName);
        // start the listener thread
        //listener.start();
    }       
    
    /** Starts the Receiver Thread and connects the Sender, using the specified variables set in the . */
    @Override
    public void start() throws RCSMException
    {
// TODO: Implement thread on start mechanism & other TopicRegistry variable settings pre-instantiation. Verify if it includes connect method as well. Start method should be called via the Array after initialization and not in initialization.           
        try
        {
            if(socket!=null&&!socket.isClosed())
            {
                socket.connect(this.addr = new InetSocketAddress(hostname, port));
                this.os=socket.getOutputStream();
                this.is=socket.getInputStream();
                listener.start();        
            }
            else
            {
                // Socket is closed, need new Socket.
                this.socket = new Socket();
                socket.setReuseAddress(true);
                // default to true
                socket.setTcpNoDelay(tcp_nodelay);
                // default to true
                socket.setKeepAlive(keep_alive);
                socket.setPerformancePreferences(0, 1, 2);                 
                socket.connect(this.addr = new InetSocketAddress(hostname, port));
                this.os=socket.getOutputStream();
                this.is=socket.getInputStream();
                // socket is passed into listener on initialization.
                listener.start();        
            }
        }
        catch (UnknownHostException ex) 
        {
            throw new RCSMException("Unknown Host called in start method of TCPROSSubscriberCommunicator. hostname:" + hostname + ":" + String.valueOf(port),ex);
        }        
        catch (IOException ex) 
        {         
            throw new RCSMException("IOException in start method of TCPROSSubscriberCommunicator.",ex);
        }
    }
    
    /** Shutdown this UDPSender. Only call this method on shutdown. Cannot be reinitialized. */
    public void shutdown()
    {
//        this.finalByteArray=null;
        if(socket.isConnected()==true||socket.isClosed()==false)
        {
            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(TCPROSSubscriberCommunicator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // Set the TCPROSSubscriberCommunicator messageHandlerController to null
        this.messageHandlerController=null;
        this.socket=null;
        this.os=null;
        this.is=null;
    }
    
    /** Disconnect the TCP Socket. */
    public void disconnect()
    {
        //this.isRunning=false;
       // listener.stop();        
        if(socket.isConnected()==true||socket.isClosed()==false)
        {
        if(os!=null)
        {
            try 
            {
                os.close();
            } 
            catch (IOException ex) 
            {
                Logger.getLogger(TCPROSSubscriberCommunicator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if(is!=null)
        {
            try 
            {
                is.close();
            } 
            catch (IOException ex) 
            {
                Logger.getLogger(TCPROSSubscriberCommunicator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }            
            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(TCPROSSubscriberCommunicator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }    

    /** Recycle this class for use in a Cache Pool. */
    public void recycle()
    {
        
    }
    
    /** Return the currently registered UDP port. */
    public int getRemotePort()
    {
        return socket.getPort();
    }
    
    /** Return the hostname. */    
    public String getRemoteHostName()
    {
        return hostname;
    }
        
    /** Return boolean is connected on socket. */
    public boolean isConnected()
    {
        if(!socket.isClosed())
        {
            return socket.isConnected();
        }
        else
        {
            return false;
        }
    }
    
    /** only call send, after TCPROSSubscriberCommunicator initialize method is called at once at Object initialization time. Keep in mind this data array should match the length of the packetLength. */
    public void send(byte[] data) throws IOException
    {
        try
        {
            os.write(data);
        }
        catch(NullPointerException e)
        {
            try 
            {
                connect();
                os.write(data);                
            } 
            catch (RCSMException ex) 
            {
                Logger.getLogger(TCPROSServiceCommunicator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        catch(SocketException e)
        {
            try 
            {
                connect();
                os.write(data);                
            } 
            catch (RCSMException ex) 
            {
                Logger.getLogger(TCPROSServiceCommunicator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
    }

    /** Set the read block size for incoming TCP data reads. Only use if the block size changes from the initialize black size. */
    public void setReadBlockSize(int blockSize)
    {
        if(lastBlockSize!=blockSize)
        {
            this.lastBlockSize=blockSize;
            this.readData = new byte[blockSize];
        }
    }

    /** Return the currently set read block size. */
    public int getReadBlockSize()
    {
        return lastBlockSize;
    }

    /** Return the protocol name of this CommunicationSenderInterface implementation. */
    public String getProtocolName() 
    {
        return PROTOCOL_TCPROS;
    }

    /** Not supported by the TCPROS implementation. This is a UDPROS specific method. */
    public void send(byte[] data, int offset, int length) throws IOException {
        throw new UnsupportedOperationException("Not supported in TCPROS. This method was a one off for the UDPROS UDPSender implementation.");
    }

    @Override
    public boolean isRunning() 
    {
        return this.isRunning;
    }

    @Override
    public void stop() 
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getHostName() 
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getReceivePort() 
    {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void send(byte[] data, int data_processor_id) throws IOException {
        throw new UnsupportedOperationException("Not supported in TCPROS Subscribers. This method is intended for use by Publishers with multiple Subscribers.");
    }
    
    /** Return the currently used Socket. Only applies to Sender implementations 
     * using the TCP/IP Socket.
     * 
     * @return Socket
     */
    public Socket getSocket()
    {
        return socket;
    }

    /** Connect the provider connection. */
    public void connect() throws RCSMException 
    {
        try 
        {
            reinitialize();
            start();
            //TODO: add the reference to the message, and topic index in the constructor, or initializer for performance purposes, and remove the rosNode reference. Additionally, make the subMessages, and svcMessages private again.
            send(rosNode.subMessages[rosNode.getTopicIndex(topic)].getMessage());
        } 
        catch (IOException ex) 
        {
            throw new RCSMException(ex);
        } 
        catch (ClassNotFoundException ex) 
        {
            throw new RCSMException(ex);
        } 
        catch (InstantiationException ex) 
        {
            throw new RCSMException(ex);
        } 
        catch (IllegalAccessException ex) 
        {
            throw new RCSMException(ex);
        }
    }      

    @Override
    public MessageHandlerInterface getMessageHandlerInterface() 
    {
       return ((DataProcessor)messageHandlerController).getMessageHandler();
    }
}
