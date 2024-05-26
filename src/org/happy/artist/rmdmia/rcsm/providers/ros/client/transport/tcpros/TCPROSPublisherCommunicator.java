package org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.tcpros;
import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.happy.artist.rmdmia.rcsm.RCSMException;

import org.happy.artist.rmdmia.rcsm.provider.CommunicationReceiverInterface;
import org.happy.artist.rmdmia.rcsm.provider.CommunicationSenderInterface;
import org.happy.artist.rmdmia.rcsm.provider.message.MessageHandlerInterface;
import org.happy.artist.rmdmia.rcsm.provider.message.MessageHandlerControllerInterface;
import org.happy.artist.rmdmia.rcsm.provider.message.DataProcessor;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.ROSMessageDecoder;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.util.InputStreamReadBuffer;

/** TCPROSPublisherCommunicator.java TCPROSPublisherCommunicator client for specified TCP 
 * server, port number & packet length.
 * Must call initialize() after construction is completed, and before the Runnable 
 * start() method. Each TCPROS Publisher connection should use this class to 
 * send data to the specified host/port.
 * 
 * @author Happy Artist
 * 
 * @copyright Copyright© 2013-2014 Happy Artist. All rights reserved.
 */
public class TCPROSPublisherCommunicator implements CommunicationSenderInterface, CommunicationReceiverInterface
{
    private final static String PROTOCOL_TCPROS="TCPROS";
    private String hostname;
    private int port;
    private byte[] sendData;
    private Socket socket;
//    private byte[] readBytes;
    private byte[] readData;
    private int lastBlockSize=2048;
   // private int bLength;    
    private InetSocketAddress addr;
    private boolean isRunning=false;
  //  private Thread listener;    
    public String threadName="UNNAMED_TCPROSPublisherCommunicator";
    // Message Handler Class Name is the class redirector property in topic_register.eax, if no property is specified the default sensor queue will be used.
    private String messageHandlerClassname;
    private String initializerMessageHandlerClassname;
    private String tunerMessageHandlerClassname;    
    // InputStreamReadBuffer
//    private InputStreamReadBuffer isbr;
    private String topic;
    private boolean isTopic;
    // define the local port (server listener port) 
    //private int localPort;
    // Define the serverListener Thread 
    private Thread serverListener;  
    // Last Message Sent for Latching, if length is 0 no messages sent yet.
    public byte[] last_msg= new byte[0];
    // Define and instantiate Logger
    private Logger logger = Logger.getLogger(TCPROSPublisherCommunicator.class.getName());

    public TCPROSPublisherCommunicator(String initializerMessageHandlerClassname,String messageHandlerClassname, String tunerMessageHandlerClassname, String topic, boolean isTopic) {
//        this.localPort=localPort;
        this.messageHandlerClassname=messageHandlerClassname;
        this.initializerMessageHandlerClassname=initializerMessageHandlerClassname;
        this.tunerMessageHandlerClassname=tunerMessageHandlerClassname;        
        // set the topic/service name
        this.topic=topic;
        this.isTopic=isTopic;
    }
    
    /** Return the Subscriber Threads in an array of SocketThread Objects. */
    public TCPROSPublisherCommunicator.SocketThread[] getConnectedSubscribers()
    {
        return subscriberThreads;
    }
    
    // The SocketThread array of Published Subscribers.
    private TCPROSPublisherCommunicator.SocketThread[] subscriberThreads=new TCPROSPublisherCommunicator.SocketThread[0];
    /** Add new subscriber, and return the subscriberThreads array indice of the new added SocketThread. */
    private synchronized int addSubscriber(TCPROSPublisherCommunicator.SocketThread thread)
    {
        this.subscriberThreads=Arrays.copyOf(subscriberThreads, subscriberThreads.length + 1);
        subscriberThreads[subscriberThreads.length-1]=thread;
        return subscriberThreads.length-1;
    }

    // copy used in the removeSubscriber method. Calls shutdown on SocketThread before removing from array.
    private TCPROSPublisherCommunicator.SocketThread[] copy;
    /** remove subscriber by id. */
    public synchronized void removeSubscriber(int id)
    {
        for (int i = 0; i < subscriberThreads.length; i++)
        {
            if (subscriberThreads[i].id == id)
            {
                if(subscriberThreads[i]!=null)
                {
                    subscriberThreads[i].shutdown();
                }
                this.copy = new TCPROSPublisherCommunicator.SocketThread[subscriberThreads.length-1];
                System.arraycopy(subscriberThreads, 0, copy, 0, i);
                System.arraycopy(subscriberThreads, i+1, copy, i, subscriberThreads.length-i-1);
                this.subscriberThreads=copy;
                // destroy the copy reference
                this.copy=null;
                return;
            }
        }
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
    
    // Define the ServerSocket port listener
    private ServerSocket server;
    // Dynamic port finder
    private ServerSocket tmpServer;
    /** Specify -1 on the port to dynamically allocate a port at initialization. */
    public void initialize(String hostname, int port, final int blockSize, final boolean tcp_nodelay, final boolean keep_alive, final String threadName) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        TCPROSPublisherCommunicator.this.threadName=threadName;
        //logger.info("TCPROSPublisherCommunicator initialize: ".concat("hostname: ").concat(hostname).concat(", port: ").concat(String.valueOf(port)).concat("thread name: ").concat(threadName));
        // instantiate the ServerSocket.
        this.server = new ServerSocket();
        if(port==-1)
        {
            try
            {
                this.tmpServer = new ServerSocket(0);
                port = tmpServer.getLocalPort();
            }
            catch(Exception e)
            {
                logger.log(Level.WARNING,"unable to find a free port for publisher serversocket on topic: " + topic, e);            
            }
            finally
            {
                tmpServer.close();
            }
        }        
        this.addr = new InetSocketAddress(hostname, port);
        // server options should be called before bind.
        //server.setReuseAddress(true);
        server.setPerformancePreferences(0, 1, 2);         
        server.bind(addr);
        
// TODO: aaron Create a server threadc for port listening, that generates listener thread as below inside while loop.
        this.serverListener = new Thread(new Runnable() 
        {
            //Socket serverSocket;
            int i = 0;
            int sub_id;
            public void run()
            {
                try
                {
                    TCPROSPublisherCommunicator.this.isRunning=true;
//                    System.out.println(threadName + " Running Publisher Listener for topic: " + topic + ", on port: " + server.getLocalPort());
                    while (TCPROSPublisherCommunicator.this.isRunning) 
                    {
                        Socket serverSocket = server.accept();
                        //logger.log(Level.INFO, "Added Subscriber to Publisher");
                        try 
                        {
                            this.sub_id=TCPROSPublisherCommunicator.this.addSubscriber(new TCPROSPublisherCommunicator.SocketThread(serverSocket, i, blockSize, tcp_nodelay, keep_alive, threadName.concat("_").concat(String.valueOf(i))));
                            // instantiate the new subscriber, and start the thread. 
                            TCPROSPublisherCommunicator.this.subscriberThreads[sub_id].start();
                        } 
                        catch(NullPointerException ex)
                        {
                            //System.out.println("NullPointerException values - serverSocket: " + serverSocket + ", threadName: " + TCPROSPublisherCommunicator.this.threadName);
                            Logger.getLogger(TCPROSPublisherCommunicator.class.getName()).log(Level.SEVERE, null, ex);                            
                            //ex.printStackTrace();
                        }
                        catch (ClassNotFoundException ex) 
                        {
                            Logger.getLogger(TCPROSPublisherCommunicator.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (InstantiationException ex) {
                            Logger.getLogger(TCPROSPublisherCommunicator.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IllegalAccessException ex) {
                            Logger.getLogger(TCPROSPublisherCommunicator.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        i=i+1;
                    }
                    System.out.println("Exited Publisher Listener for topic: " + TCPROSPublisherCommunicator.this.topic);
                } 
                catch (IOException ioe) 
                {
                     logger.log(Level.WARNING, "Need to add topic connection handling for PUB Topics... IOException on PUB_SERVER_TOPIC_" + TCPROSPublisherCommunicator.this.topic + " socket listen: " + ioe);
                     ioe.printStackTrace();
                 }                           
            }
        }, "PUB_SERVER_TOPIC_".concat(topic)); 
    }
        
    /** Starts the Receiver Thread and connects the Sender, using the specified variables set in the . */
    @Override
    public synchronized void start() throws RCSMException
    {
        try
        {
            if(serverListener.isAlive()==false)
            {
                serverListener.start();
            }
        }
        catch(IllegalThreadStateException e)
        {
            System.out.println("Publisher ServerSocket Thread already started on topic: " + topic);
            e.printStackTrace();
  //          throw new RCSMException(e);
        }
    }
    
    /** Shutdown this Publisher. Only call this method on shutdown. Cannot be reinitialized. */
    public void shutdown()
    {
        this.isRunning=false;
        disconnect();
        // Set the TCPROSPublisherCommunicator messageHandlerController to null
        this.messageHandlerController=null;
        this.serverListener=null;
    }
    
    // disconnect method variables.
    private int disc_count;
    /** Disconnect the TCPROS ServerSocket. */
    public synchronized void disconnect()
    {
        this.isRunning=false;
        try 
        {
            if(server!=null)
            {
                this.disc_count=0;
                // disconnect each socket.
                while(disc_count<subscriberThreads.length)
                {
                    if(subscriberThreads[disc_count]!=null)
                    {
                        try
                        {
                            removeSubscriber(subscriberThreads[disc_count].id);
                        }
                        catch(Exception e)
                        {
                            logger.log(Level.WARNING, null, e);
                        }
                    }
                    this.disc_count = disc_count + 1;
                }
                if(subscriberThreads.length>0)
                {
                    this.subscriberThreads=new TCPROSPublisherCommunicator.SocketThread[0];
                }
            }
        } 
        finally
        {
            try 
            {
                // Close the server.
                server.close();
            } 
            catch (IOException e) 
            {
                logger.log(Level.WARNING, "IOException calling SocketServer close() on Publisher topic: ".concat(topic), e);
            }
        }
    }    

    /** Recycle this class for use in a Cache Pool. */
    public void recycle()
    {
        
    }
    
    /** Not implemented. Always returns -1. Return the currently registered UDP port. */
    public int getRemotePort()
    {
        return -1;
    }
    
    /** Return the hostname. */    
    public String getRemoteHostName()
    {
        return hostname;
    }
        
    // isConnected method variables
    //private int conn_sub_count;
    //private boolean[] is_connected;
    /** Returns isConnected (is listening on port.). */
    public boolean isConnected()
    {
      //  this.conn_sub_count=0;
       // while(conn_sub_count<subscriberThreads.length)
       // {         
        //    return socket.isConnected();
        //}    
        if(isRunning&&server!=null&&server.isBound()&&!server.isClosed())
        {
            // Server Listening
            return true;
        }
        else
        {
            // Server closed
            return false;
        }
    }

// TODO: Implement Exception Handling that removes bad connections/dead subscribers.    
    // Send method variables
    private int sub_count;
    // TODO: remove ROSMessage Decoder and comment out system 
  //  ROSMessageDecoder decode = new ROSMessageDecoder();     
    /** only call send, after TCPROSPublisherCommunicator initialize method is called at once at Object initialization time. Keep in mind this data array should match the length of the packetLength. */
    public void send(byte[] data) throws IOException
    {
 //       System.out.println("Publisher Send Hex Message:" + org.happy.artist.rmdmia.utilities.BytesToHex.bytesToHex(data));        
//System.out.println("Publisher send message: "  + decode.convertHexToString(org.happy.artist.rmdmia.utilities.BytesToHex.bytesToHex(data).toCharArray())); 
        // set the last_msg for latching
        this.last_msg=data;
        // set sub_count to 0
        this.sub_count=0;
        while(sub_count<subscriberThreads.length)
        {            
            try
            {
                subscriberThreads[sub_count].os.write(data);
            }
            catch(Exception e)
            {
                logger.log(Level.WARNING, "Topic Publisher Exception in Subscriber Connection for: ".concat(topic.concat(" ")).concat(String.valueOf(sub_count)),e);
            }
            this.sub_count=sub_count + 1;
        }
    }
    
    /** Send a message to the specific topic data processor connection, by 
     * connection id. This send message is used to establish a publisher/subscriber
     * method, and is ignored once the TCPROS Connection Header, and latched 
     * message (if enabled) is exchanged. 
     */
    public void send(byte[] data, int publisher_id) throws IOException
    {
 /////       System.out.println("Publisher Send Hex Message:" + org.happy.artist.rmdmia.utilities.BytesToHex.bytesToHex(data));        
//System.out.println("Publisher send message: "  + decode.convertHexToString(org.happy.artist.rmdmia.utilities.BytesToHex.bytesToHex(data).toCharArray()));         
        try
        {
            subscriberThreads[publisher_id].os.write(data);
        }
        catch(Exception e)
        {
            logger.log(Level.WARNING, "Topic Publisher Exception calling send on publisher_id: ".concat(topic.concat(" ")).concat(String.valueOf(publisher_id)),e);
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
        return hostname;
    }

    @Override
    public int getReceivePort() 
    {
        return server.getLocalPort();
    }

    @Override
    public Socket getSocket() {
        throw new UnsupportedOperationException("Not supported on TCPROSPublisherCommunicator.");
    }

    /** Not implemented by TCPROSPublisherCommunicator. */
    public void connect() {
        throw new UnsupportedOperationException("Not implemented by TCPROSPublisherCommunicator.");
    }
    
    class SocketThread extends Thread
    {
        private boolean isRunning=false;
        private byte[] readData;
        private int lastBlockSize=2048;     
        private String threadName;
        private InputStreamReadBuffer isbr;
        private TCPDataProcessor processor;
        private String hostname;
        private int port;
        private Socket socket;
        private OutputStream os;
        private InputStream is;        
        public transient int id;
        private int bLength;
        public SocketThread(Socket socket, int publisher_id, int blockSize, boolean tcp_nodelay, boolean keep_alive, String threadName) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException
        {
            this.id=publisher_id;
            this.socket=socket;
            this.os=socket.getOutputStream();
            this.is=socket.getInputStream();
            this.isbr = new InputStreamReadBuffer(blockSize);
            this.threadName=threadName;
            // Set the read byte[] block size
            this.lastBlockSize=blockSize;
            this.readData = new byte[blockSize];

            this.processor = new TCPDataProcessor(lastBlockSize,TCPROSPublisherCommunicator.this.initializerMessageHandlerClassname,TCPROSPublisherCommunicator.this.messageHandlerClassname, TCPROSPublisherCommunicator.this.tunerMessageHandlerClassname, threadName, TCPROSPublisherCommunicator.this.topic, TCPROSPublisherCommunicator.this.isTopic, TCPROSPublisherCommunicator.this, socket,publisher_id);
            this.hostname=hostname;
            this.port=port;
            socket.setReuseAddress(true);
            // default to true - set by subscriber here...
            //socket.setTcpNoDelay(tcp_nodelay);
            // default to true
            socket.setKeepAlive(keep_alive);
            socket.setPerformancePreferences(0, 1, 2); 
            // assign the IO streams.
            this.os=socket.getOutputStream();
            this.is=socket.getInputStream();         
            // set thread name.
     //       this.threadName=threadName;
            setName(threadName);
        }
        
        @Override
        public void run() 
        {
//            System.out.println(threadName + " starting Publisher SocketThread on topic " + topic);
            // Set isRunning to true.
            this.isRunning=true;
            // Set the TCPROSPublisherCommunicator messageHandlerController
            TCPROSPublisherCommunicator.this.messageHandlerController=(MessageHandlerControllerInterface)processor;
            // Set the TCPROSPublisherCommunicator isRunning to true.
            TCPROSPublisherCommunicator.this.isRunning=true;
//            System.out.println("Publisher Socket Thread ".concat(threadName).concat(": ").concat(socket.getInetAddress().toString()).concat(":").concat(String.valueOf(socket.getPort())).concat(", remote socket address: ").concat(String.valueOf(socket.getRemoteSocketAddress())));
            
            while(isRunning)
            {
                try
                {
                    while ((this.bLength = is.read(readData=isbr.checkout())) != -1) 
                    {
//                        System.out.println(threadName + " incoming data");
                        // Read bytes, and write to byte[]
                        processor.process(readData,bLength);
                        if(processor.isNewMessage)
                        {
                              // If process message process completed call reset on InputStreamReadBuffer. Setting checkoutCount to 0 is same as calling method but less overhead.
                              isbr.checkoutCount=0;
                        }
                    }  
                }
                catch(Exception e)
                {
                    //if(is==null)
                    //{
                    //    System.out.println(getName() + " is is null on publisher_id:" + id);
                   // }
                   // if(isbr==null)
                    //{
                        //System.out.println("isbr is null.");
                    //}   
                   // if(readData==null)
                   // {
                   //     System.out.println("readData is null.");
                   // }             /        
                    //System.out.println("Exception in thread: " + threadName);
                    //e.printStackTrace();
                    logger.log(Level.INFO,getName() + ": ", e);
                }
            }
            logger.log(Level.INFO,"Publisher Thread CLOSED: " + threadName);
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
        
        /** Shutdown this UDPSender. Only call this method on shutdown. Cannot be reinitialized. */
        public void shutdown()
        {
            this.isRunning=false;
            if(socket.isConnected()==true||socket.isClosed()==false)
            {
                try {
                    socket.close();
                } catch (IOException ex) {
                    Logger.getLogger(TCPROSPublisherCommunicator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            // Set the TCPROSPublisherCommunicator messageHandlerController to null
            this.messageHandlerController=null;
            this.socket=null;
            this.os=null;
            this.is=null;
        }

        /** Disconnect the UDP Socket. */
        public void disconnect()
        {
            this.isRunning=false;
            stop();        
            if(socket.isConnected()==true||socket.isClosed()==false)
            {
                try {
                    socket.close();
                } catch (IOException ex) {
                    Logger.getLogger(TCPROSPublisherCommunicator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if(os!=null)
            {
                try 
                {
                    os.close();
                } 
                catch (IOException ex) 
                {
                    Logger.getLogger(TCPROSPublisherCommunicator.class.getName()).log(Level.SEVERE, null, ex);
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
                    Logger.getLogger(TCPROSPublisherCommunicator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
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
            return socket.isConnected();
        }

        /** only call send, after TCPROSPublisherCommunicator initialize method is called at once at Object initialization time. Keep in mind this data array should match the length of the packetLength. */
        public void send(byte[] data) throws IOException
        {
            os.write(data);
//            logger.log(Level.INFO,"Pub Sending message length: ".concat(String.valueOf(data.length)));
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

        public boolean isRunning() 
        {
            return this.isRunning;
        }

        public int getLocalPort() 
        {
            return socket.getLocalPort(); 
        }        
    }
    
    @Override
    public MessageHandlerInterface getMessageHandlerInterface() 
    {
       return ((DataProcessor)messageHandlerController).getMessageHandler();
    }    
 }
