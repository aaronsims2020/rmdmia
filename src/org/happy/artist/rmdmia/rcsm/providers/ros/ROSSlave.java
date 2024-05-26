package org.happy.artist.rmdmia.rcsm.providers.ros;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcHandler;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcNoSuchHandlerException;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;
import org.happy.artist.rmdmia.rcsm.RCSMException;
import org.happy.artist.rmdmia.rcsm.provider.CommunicationSenderInterface;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.MultiProtocolPublisherCommunicator;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.tcpros.TCPROSPublisherCommunicator;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.udpros.UDPProtocolUtility;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.udpros.UDPROSPublisherCommunicator;
//import org.happy.artist.http.HttpServer;

/** ROSSlave.java - An implementation of the ROS Slave API. This will be the 
 *  XML-RPC Handler API. 
 *  The slave API is an XMLRPC API that has two roles: receiving 
 *  callbacks from the Master, and negotiating connections with other nodes. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2013 Happy Artist. All rights reserved.
 */

public class ROSSlave implements XmlRpcHandler, XmlRpcHandlerMapping
{
     // TODO: Research paramUpdate input parameter of Object[], wondering if Map, Vector, or List makes sense.
     // Define the XMLRPC Client.
     private XmlRpcClient client = new XmlRpcClient();
     // Slave Client (previously said server so changed. May be server...) Connection Configuration
     private XmlRpcClientConfigImpl xmlRPCClientConfig = new XmlRpcClientConfigImpl();   
     // Slave Server Connection Configuration     
     private XmlRpcServerConfigImpl serverConfig;     
     // HttpServer for receiving XMLRPC calls from ROS
     //private HttpServer httpServer;
     private WebServer httpServer;
     // Define XMLRpcServer Reference
     private XmlRpcServer xmlRpcServer;
     // The XML-RPC Client Response Object[]
     private Object[] response;
     // The XML-RPC Response status (i.e. success=1, failure=0, and error=-1).
     private int responseStatus;  
     // XML-RPC Callback Port. callback port set to -1 by default, if not -1 is pre-set.
     private int callbackPort=-1;
     // Used by Server Response
     private Object[] statusResponse = new Object[3];
     private Integer statusCode;
     private String statusMessage;
     private Integer processID;   
     // ROS Master URL
     private URL rosMasterURL;
     // ROS Node reference =- manages ROSMaster & ROSSlave
     private ROSNode node;
     // Temp TopicIndex reference for publisherUpdate.
     private int pubUpdateTopicIndice;
     // reqTopicCount is the for the method request topic protocols loop count
     private int reqTopicCount;  
     // protocols supported Object[] stores supported protocols to be sent in response to getTopic
     private Object[] protocols_supported;
     // Ignore response Integer
     private final static Integer ignore = Integer.valueOf(0); 
     // topic 
     private String topic;
     
     private ROSSlaveParameterListener paramListener;
     private ROSSubscriber topicSlaves;
     // isListening variable for isListening() method is the best we can do for current WebServer to determine if start was called.
     private boolean isListening=false;
     // private URL rosTopicURL
     private URL rosTopicURL;
     
     public ROSSlave(URL rosMasterURL, URL rosTopicURL, int callbackPort, WebServer httpServer, ROSNode node, String topic) throws IOException, XmlRpcException
     {
         this.topic=topic;
         this.node = node;
         this.rosMasterURL=rosMasterURL;
         this.rosTopicURL = rosTopicURL;
         // Set the callback port.
         this.callbackPort=callbackPort;
         // Start ROS RMDMIA XMLRPC Callback dynamically. Call getCallbackPort() to get Callback Port number.         
         this.httpServer = httpServer;
         this.paramListener = node.getParameterListener();
     }
     
     /** Call getCallbackPort() to obtain the dynamically chosen callback port. */
     public ROSSlave(URL rosMasterURL, URL rosTopicURL, WebServer httpServer, ROSNode node, String topic) throws IOException, XmlRpcException
     {
         this.topic=topic;
         this.node = node;
         this.rosMasterURL=rosMasterURL;
         this.rosTopicURL = rosTopicURL;
         // Start ROS RMDMIA XMLRPC Callback dynamically. Call getCallbackPort() to get Callback Port number.         
         this.httpServer = httpServer;  
         // Don't set the callbackPort here since the httpServer may contain one already 
         this.paramListener = node.getParameterListener(); 
         start();
     }  


     /** Call getCallbackPort() to obtain the dynamically chosen callback port. */
     public ROSSlave(URL rosMasterURL, URL rosTopicURL, ROSNode node, String topic) throws IOException, XmlRpcException
     {
         this.topic=topic;
         this.node = node;
         this.rosMasterURL=rosMasterURL;
         this.rosTopicURL = rosTopicURL;
         this.callbackPort=0;
         this.paramListener = node.getParameterListener();   
         start();
     } 
     
     /** Call getCallbackPort() to obtain the dynamically chosen callback port. */
     public ROSSlave(URL rosMasterURL, URL rosTopicURL, int callbackPort, ROSNode node, String topic) throws IOException, XmlRpcException
     {
         this.topic=topic;
         this.node = node;
         this.rosMasterURL=rosMasterURL;
         this.rosTopicURL = rosTopicURL;
         this.callbackPort=callbackPort;
         this.paramListener = node.getParameterListener();         
         start();
     }   
     
     /** Call start to initialize the ROSSlave, once the Object is constructed. Will not work without this. */
     private void start() throws IOException
     {
        //Logger.getLogger(ROSSlave.class.getName()).log(Level.INFO,"Starting ROSSlave..."); 
         if(httpServer==null)
         {
             // If callbackPort is not set, set to 0 which is dynamic port selection.
             if(callbackPort==-1)
             {
                 callbackPort=0;
             }
             this.httpServer = new WebServer(callbackPort);
             httpServer.setParanoid(false);
             this.xmlRpcServer = httpServer.getXmlRpcServer();
             // set the XmlRpcHandlerMapping for the XmlRpcServer.
             xmlRpcServer.setHandlerMapping(this);
             // Set the XMLRPC Extensions enabled to true. A rare startup exception in ROS turtlesim seems tied to Publishers. Source unknown...
// TODO enabledForExtensions(true) breaks the RMDMIA:  
             this.serverConfig =(XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
             serverConfig.setEnabledForExtensions(false);
             serverConfig.setContentLengthOptional(false);
             //xmlRPCClientConfig.
         }         
         httpServer.start();
         this.isListening=true; 
         // Set the callback port for future reference.
         this.callbackPort=httpServer.getPort();
          // Setup the Slave Server XML-RPC Config Object
         xmlRPCClientConfig.setServerURL(rosTopicURL);
         client.setConfig(xmlRPCClientConfig);         
         //Logger.getLogger(ROSSlave.class.getName()).log(Level.INFO,"Started ROSSlave...");          
     }     
     
     /** Return the callback port. */
     public int getCallbackPort()
     {
         return httpServer.getPort();
     }

     /** Returns true if the XML-RPC Server Socket is Listening for incoming requests on the callback port. */
     public boolean isListening()
     {
         return isListening;
     }     
     
     /** Retrieve transport/topic statistics.
      * @param caller_id (String) ROS caller ID.
      * @return Returns (Integer, String, [XMLRPCLegalValue*]) = (code, statusMessage, stats)
      * stats is of the form [publishStats, subscribeStats, serviceStats] where
      *
      * publishStats: [[topicName, messageDataSent, pubConnectionData]...]
      * subscribeStats: [[topicName, subConnectionData]...]
      * serviceStats: (proposed) [numRequests, bytesReceived, bytesSent]
      *
      * pubConnectionData: [connectionId, bytesSent, numSent, connected]* 
      * subConnectionData: [connectionId, bytesReceived, dropEstimate, connected]*
      *
      * dropEstimate: -1 if no estimate. 
      */     
     public Object[] getBusStats(String caller_id) throws XmlRpcException, ROSXMLRPCException
     {
//      System.out.println("<-Inbound Get Bus Stats on topic: " + topic);          
         // Obtain the current ROS Slave Group for this topic.
         this.topicSlaves=node.getROSSlaveGroup(topic);         
         this.statusResponse[0]=(statusCode=Integer.valueOf(1));
         this.statusResponse[1]=(statusMessage="bus stats");
         this.statusResponse[2]=node.getBusStatsForSlave(caller_id);        
         return statusResponse;
     }

     /** Retrieve transport/topic connection information.
      * @param caller_id (String) ROS caller ID.
      * @return Returns (Integer, String, [XMLRPCLegalValue*] (Is Object[] or Object[][] not sure yet.)) = (code, statusMessage, busInfo)
      *  busInfo is of the form:
      *  [[connectionId1, destinationId1, direction1, transport1, topic1, connected1]... ]
      *  - connectionId is defined by the node and is opaque.
      *  - destinationId is the XMLRPC URI of the destination.
      *  - direction is one of 'i', 'o', or 'b' (in, out, both).
      *  - transport is the transport type (e.g. 'TCPROS').
      *  - topic is the topic name.
      *  - connected1 indicates connection status. Note that this field is only provided by slaves written in Python at the moment (cf. rospy/masterslave.py in _TopicImpl.get_stats_info() vs. roscpp/publication.cpp in Publication::getInfo()).
      */              
     public Object[] getBusInfo(String caller_id) throws XmlRpcException, ROSXMLRPCException
     {
//      System.out.println("<-Inbound Get Bus Info on topic: " + topic);          
            try
            {
                this.statusResponse[0]=(statusCode=Integer.valueOf(1));
                this.statusResponse[1]=(statusMessage="bus info");
                this.statusResponse[2]=node.getBusInfoForSlave(caller_id);        
                return statusResponse;
            }
            catch(Exception e)
            {
                e.printStackTrace();
                this.statusResponse[0]=(statusCode=Integer.valueOf(-1));
                this.statusResponse[1]=(statusMessage="Error processing getBusInfo.");
                this.statusResponse[2]=(this.processID=Integer.valueOf(0));        
                return statusResponse;                  
            }
     }

     /** Get the URI of the master node.
      * @param caller_id (String) ROS caller ID.
      * @return Returns (Integer, String, String) = (code, statusMessage, masterURI) 
      */            
     public Object[] getMasterUri(String caller_id)
     {
//      System.out.println("<-Inbound Get Master URI on topic: " + topic);          
        this.statusResponse[0]=(statusCode=Integer.valueOf(1));
        this.statusResponse[1]=(statusMessage="");
        this.statusResponse[2]=rosMasterURL;        
        return statusResponse;    
     }

     /** Stop this server.
      * @param caller_id (String) ROS caller ID.
      * @param message (String) A message describing why the node is being shutdown. 
      * @return Returns (Integer, String, Integer) = (code, statusMessage, ignore) 
      */        
     public Object[] shutdown(String caller_id, String message) throws XmlRpcException, ROSXMLRPCException
     {
//      System.out.println("<-Inbound Shutdown on topic: " + topic);          
         shutdown();
         this.statusResponse[0]=(statusCode=Integer.valueOf(1));
         this.statusResponse[1]=(statusMessage="shutdown node: " + caller_id);
         this.statusResponse[2]=(this.processID=Integer.valueOf(0));         
         return statusResponse;  
     }

     /** Get the PID of this server.
      * @param caller_id (String) ROS caller ID.
      * @return Returns (Integer, String, Integer) = (code, statusMessage, serverProcessPID) 
      */
     private int pid;
     private String strPID;
     public Object[] getPid(String caller_id)
     {
//      System.out.println("<-Inbound Get Pid on topic: " + topic);          
         try
         {   
             try
             {
                 // Java getPid
                 this.strPID=java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
                this.pid=strPID.indexOf("@");
                if(pid!=-1)
                {
                    this.pid=Integer.parseInt(strPID.split("@")[0]);
                }
                else
                {
                    this.pid=0;
                }
             }
             catch(NoClassDefFoundError e)
             {
                 // Android getPid
                try 
                {
                    this.pid=(Integer)Class.forName("android.os.Process").getMethod("myPid").invoke(null);
                }     
                catch (Exception ex) 
                {
                    throw new UnsupportedOperationException();
                }
            }
    
            if((this.processID=Integer.valueOf(pid))!=0)
            {
                this.statusResponse[0]=(statusCode=Integer.valueOf(1));
                this.statusResponse[1]=(statusMessage="PID: " + String.valueOf(processID));
                this.statusResponse[2]=processID;        
                return statusResponse;
            }
            else
            {
                this.statusResponse[0]=(statusCode=Integer.valueOf(0));
                this.statusResponse[1]=(statusMessage="Cannot retrieve PID on this platform.");
                this.statusResponse[2]=processID;        
                return statusResponse;
            }
         }
         catch(UnsupportedOperationException e)
         {
               // UnsupportedOperationException
               this.statusResponse[0]=(statusCode=Integer.valueOf(-1));
               this.statusResponse[1]=(statusMessage="Cannot retrieve PID on this platform.");
               this.statusResponse[2]=(this.processID=Integer.valueOf(0));        
               return statusResponse;           
         }
     }

     // Done
     // Each ROSSlave will operate in its own thread synchronously so theoretically multiple methods can share while loop variables and never conflict with other methods.
     private int count=0;
     private int subscriptionCount;
     private static final int one = 1;
     private Object[] subscriptions;
     /** Retrieve a list of topics that this node subscribes to.
      * @param caller_id (String) ROS caller ID.
      * @return Returns (code, statusMessage, topicList)  topicList is a list of topics this node subscribes to and is of the form [ [topic1, topicType1]...[topicN, topicTypeN] ]
      */     
     public Object[] getSubscriptions(String caller_id) throws XmlRpcException, ROSXMLRPCException
     {
//      TODO: Reference arrays in Node, but leave the getSubscriptions request inside Master for making calls to these methods directly. 
//        this is for receiving calls (Listeners) not making them.
//      System.out.println("<-Inbound Get Subscriptions on topic: " + topic);          
         this.count=0;
         this.subscriptionCount=0;
         while(count<node.isSubscribed.length)
         {
             if(node.isSubscribed[count])
             {
                 this.subscriptionCount = subscriptionCount + one;
             }
             this.count = count + one;
         }
         
         this.subscriptions = new Object[subscriptionCount];
         this.count=0;
         this.subscriptionCount=0;
         while(count<node.isSubscribed.length)
         {
             if(node.isSubscribed[count])
             {
                 this.subscriptions[subscriptionCount]=new Object[]{node.topicNames[count],node.subscriberTopicTypes[count]};
                 this.subscriptionCount = subscriptionCount + one;
             }             
             this.count = count + one;
         }         
         this.statusResponse[0]=(statusCode=Integer.valueOf(1));
         this.statusResponse[1]=(statusMessage="Success");
         this.statusResponse[2]=subscriptions;        
         return statusResponse;
     }

     //Done
     // Each ROSSlave will operate in its own thread synchronously so theoretically multiple methods can share while loop variables and never conflict with other methods, although due to consideration of possible future changes .
     private int pubLoopCount=0;
     private int publicationCount;
     private Object[] publications;
     /** Retrieve a list of topics that this node publishes.
      * @param caller_id (String) ROS caller ID.
      * @return Returns (Integer, String, [[String, String]]) = (code, statusMessage, topicList) 
      * topicList is a list of topics published by this node and is of the form [[topic1, topicType1]...[topicN, topicTypeN]]
      */
     public Object[] getPublications(String caller_id) throws XmlRpcException, ROSXMLRPCException
     {       
//      System.out.println("<-Inbound Get Publications on topic: " + topic);           
         this.pubLoopCount=0;
         this.publicationCount=0;
         while(pubLoopCount<node.isPublication.length)
         {
             if(node.isPublication[pubLoopCount])
             {
                 this.publicationCount = publicationCount + one;
             }
             this.pubLoopCount = pubLoopCount + one;
         }
         
         this.publications = new Object[publicationCount];
         this.pubLoopCount=0;
         this.publicationCount=0;
         while(pubLoopCount<node.isPublication.length)
         {
             if(node.isPublication[pubLoopCount])
             {
                 this.publications[publicationCount]=new Object[]{node.topicNames[pubLoopCount],node.subscriberTopicTypes[pubLoopCount]};
                 this.publicationCount = publicationCount + one;
             }             
             this.pubLoopCount = pubLoopCount + one;
         }         
         this.statusResponse[0]=(statusCode=Integer.valueOf(1));
         this.statusResponse[1]=(statusMessage="Success");
         this.statusResponse[2]=publications;        
         return statusResponse;
     }

     /** Callback from master with updated value of subscribed parameter.
      * @param caller_id (String) ROS caller ID.
      * @param key (String) Parameter name, globally resolved.
      * @param value (!XMLRPCLegalValue(Java Object)) New parameter value.  
      * @return Object[] Structure (Integer, String, Integer) = (code, statusMessage, ignore) 
      */         
  public Object[] paramUpdate(String caller_id, String key, boolean value) 
  {
//      System.out.println("<-Inbound Param Update on topic: " + topic);        
      paramListener.update(caller_id, topic, key, value);
      this.statusResponse[0]=(statusCode=Integer.valueOf(1));
      this.statusResponse[1]=(statusMessage="Success");
      this.statusResponse[2]=ignore;        
      return statusResponse;      
  }

  public Object[] paramUpdate(String caller_id, String key, char value) 
  {
//      System.out.println("<-Inbound Param Update on topic: " + topic);        
      paramListener.update(caller_id, topic, key, value);
      this.statusResponse[0]=(statusCode=Integer.valueOf(1));
      this.statusResponse[1]=(statusMessage="Success");
      this.statusResponse[2]=ignore;        
      return statusResponse;      
  }

  public Object[] paramUpdate(String caller_id, String key, byte value) 
  {
//      System.out.println("<-Inbound Param Update on topic: " + topic);        
      paramListener.update(caller_id, topic, key, value);
      this.statusResponse[0]=(statusCode=Integer.valueOf(1));
      this.statusResponse[1]=(statusMessage="Success");
      this.statusResponse[2]=ignore;        
      return statusResponse;      
  }

  public Object[] paramUpdate(String caller_id, String key, short value) 
  {
//      System.out.println("<-Inbound Param Update on topic: " + topic);        
      paramListener.update(caller_id, topic, key, value);
      this.statusResponse[0]=(statusCode=Integer.valueOf(1));
      this.statusResponse[1]=(statusMessage="Success");
      this.statusResponse[2]=ignore;        
      return statusResponse;      
  }

  public Object[] paramUpdate(String caller_id, String key, int value) 
  {
//      System.out.println("<-Inbound Param Update on topic: " + topic);        
      paramListener.update(caller_id, topic, key, value);
      this.statusResponse[0]=(statusCode=Integer.valueOf(1));
      this.statusResponse[1]=(statusMessage="Success");
      this.statusResponse[2]=ignore;        
      return statusResponse;      
  }

  public Object[] paramUpdate(String caller_id, String key, double value) 
  {
//      System.out.println("<-Inbound Param Update on topic: " + topic);        
      paramListener.update(caller_id, topic, key, value);
      this.statusResponse[0]=(statusCode=Integer.valueOf(1));
      this.statusResponse[1]=(statusMessage="Success");
      this.statusResponse[2]=ignore;        
      return statusResponse;      
  }

  public Object[] paramUpdate(String caller_id, String key, String value) 
  {
//      System.out.println("<-Inbound Param Update on topic: " + topic);        
      paramListener.update(caller_id, topic, key, value);
      this.statusResponse[0]=(statusCode=Integer.valueOf(1));
      this.statusResponse[1]=(statusMessage="Success");
      this.statusResponse[2]=ignore;        
      return statusResponse;      
  }

  public Object[] paramUpdate(String caller_id, String key, List<?> value) 
  {
//      System.out.println("<-Inbound Param Update on topic: " + topic);         
      paramListener.update(caller_id, topic, key, value);
      this.statusResponse[0]=(statusCode=Integer.valueOf(1));
      this.statusResponse[1]=(statusMessage="Success");
      this.statusResponse[2]=ignore;        
      return statusResponse;      
  }

  public Object[] paramUpdate(String caller_id, String key, Vector<?> value) 
  {
//      System.out.println("<-Inbound Param Update on topic: " + topic);         
      paramListener.update(caller_id, topic, key, value);
      this.statusResponse[0]=(statusCode=Integer.valueOf(1));
      this.statusResponse[1]=(statusMessage="Success");
      this.statusResponse[2]=ignore;        
      return statusResponse;      
  }

  public Object[] paramUpdate(String caller_id, String key, Map<?, ?> value) 
  {
//         System.out.println("<-Inbound Param Update on topic: " + topic);      
      paramListener.update(caller_id, topic, key, value);
      this.statusResponse[0]=(statusCode=Integer.valueOf(1));
      this.statusResponse[1]=(statusMessage="Success");
      this.statusResponse[2]=ignore;        
      return statusResponse;      
  }     
     
     /** Callback from master of current publisher list for specified topic.
      * @param caller_id (String) ROS caller ID.
      * @param topic (String) Topic name.
      * @param publishers ([String]) List of current publishers for topic in the form of XMLRPC URIs 
      * @return Returns (Integer, String, Integer) = (code, statusMessage, ignore) 
      */   
     public Object[] publisherUpdate(String caller_id, String topic, String[] publishers) throws XmlRpcException, ROSXMLRPCException
     {
//         System.out.println("<-Inbound Publisher Update on topic: " + topic);
        // Logger.getLogger(ROSSlave.class.getName()).log(Level.INFO, "<-Inbound Publisher Update on topic: " + topic + Arrays.toString(publishers));
         // Set the Topic Indice
         this.pubUpdateTopicIndice=node.getTopicIndex(topic);
         this.count=0;
         this.subscriptionCount=0;
         while(count<node.isSubscribed.length)
         {
             if(node.isSubscribed[count])
             {
                 this.subscriptionCount = subscriptionCount + one;
             }
             this.count = count + one;
         }
         
         this.subscriptions = new Object[subscriptionCount];
         this.count=0;
         this.subscriptionCount=0;
         while(count<node.isSubscribed.length)
         {
             if(node.isSubscribed[count])
             {
                 this.subscriptions[subscriptionCount]=new Object[]{node.topicNames[count],node.subscriberTopicTypes[count]};
                 this.subscriptionCount = subscriptionCount + one;
             }             
             this.count = count + one;
         }         
         this.statusResponse[0]=(statusCode=Integer.valueOf(1));
         this.statusResponse[1]=(statusMessage="");
         this.statusResponse[2]=subscriptions;  
         // Call publisherUpdate where it counts the most inside the ROSSubscriber for the specified topic.
         // TODO: REMOVE Begin Debug code
 //        if(node==null)
 //        {
 //           System.out.println("node is null in ROSSlave");
 //        }
 //        if(node.topicSlave==null)
 ///        {
//            System.out.println("node.topicSlave is null in ROSSlave");
//         }     
//         if(node.topicSlave[node.getTopicIndex(topic)]==null)
//         {
//            System.out.println("node.topicSlave[node.getTopicIndex(topic)] is null in ROSSlave");
//         }              
         // End Debug code.
         // TODO: Added if not null may be incorrect/ Only testing will reveal if it works correctly.
         if(node.topicSlave[node.getTopicIndex(topic)]!=null)
         {
            node.topicSlave[node.getTopicIndex(topic)].publisherUpdate(caller_id, topic, publishers);
         }
         return statusResponse;
     }

//Done.
/*
 * tcpros_params[0] = string("TCPROS");
 * tcpros_params[1] = network::getHost();
 * tcpros_params[2] = int(connection_manager_->getTCPPort());
 * 
 * udpros proto[0] = string("UDPROS");
 * udpros proto[1].getType() != XmlRpcValue::TypeBase64 ||
 * udpros proto[2].getType() != XmlRpcValue::TypeString ||
 * udpros proto[3].getType() != XmlRpcValue::TypeInt ||
 * udpros proto[4].getType() != XmlRpcValue::TypeInt)
 * 
 * UDPROS protcol array= UDPROS, address (connection_id), port, headers (int max_datagram_size)
 * 
 * UDP Transport for UDPROS may actually be UDP
 * [4] max datagram size (likely not larger than 1500)
 */       
     /** Publisher node API method called by a subscriber node. It is strongly suggested that
      *  implementors of this class override the requestTopic method  if users are 
      *  implementing a publisher Node. The default implementation will return support for no 
      *  protocols, due to the intent of a robotic controller being to subscribe to topics. 
      *  Publishers will need to put in some additional work until this API is upgraded to perform 
      *  Publisher work via a high level API.
      *  
      * This requests that source allocate a channel for communication. Subscriber provides a list of desired protocols for communication. Publisher returns the selected protocol along with any additional params required for establishing connection. For example, for a TCP/IP-based connection, the source node may return a port number of TCP/IP server.
      * 
      * @param caller_id (String) ROS caller ID.
      * @param topic (String) Topic name. 
      * @param protocols ([ [String, !XMLRPCLegalValue*] ]) List of desired protocols for communication in order of preference. Each protocol is a list of the form [ProtocolName, ProtocolParam1, ProtocolParam2...N]
      * @return Returns (Integer, String, [String, !XMLRPCLegalValue*] ) = (code, statusMessage, protocolParams) protocolParams may be an empty list if there are no compatible protocols.  
      */   
     public Object[] requestTopic(String caller_id, String topic, Object[] protocols) throws XmlRpcException, ROSXMLRPCException
     {
        // System.out.println("<-Inbound Request Topic on topic: " + topic);         
// start debug code
//         this.reqTopicCount=0;
//         System.out.print("Requested protocols for requestTopic: " + topic + "caller_id: " + caller_id);
//         while(reqTopicCount<protocols.length)
//         {
//             System.out.print((String) ((Object[]) protocols[reqTopicCount])[0] + ", ");
//             this.reqTopicCount = reqTopicCount + 1;
//         }
//         System.out.print("\r\n"); 
// end debog code         
         try
         {
             // Debug - uncomment commented return statement to revert back
             Object[] reqTopicResp = requestTopic(topic, protocols);
             this.statusResponse[0]=(statusCode=Integer.valueOf(1));
             this.statusResponse[1]=(statusMessage="");
             this.statusResponse[2]=reqTopicResp; 
             //Logger.getLogger(ROSSlave.class.getName()).log(Level.INFO,"ROS Slave requestTopic response for " + topic + ": " + Arrays.deepToString(statusResponse)); 
             return statusResponse;
             //return requestTopic(topic, protocols);
         }
         catch(Exception e)
         {
             // Most likely Exception thrown due to no abstract method implementation.return response with no protocols supported.
             this.statusResponse[0]=(statusCode=Integer.valueOf(1));
             this.statusResponse[1]=(statusMessage="No protocols supported for topic: " + topic + ", on " + caller_id);
             this.statusResponse[2]=(this.protocols_supported=new Object[]{});   
             return statusResponse;
         }

         //this.response = (Object[])client.execute("requestTopic", new Object[]{CALLER_ID,topic,new String[]{"TCPROS"}});
     }
     
     // temporary reference to CommunicationSenderInterface for reference to publisherThread
//     private TCPROSPublisherCommunicator sender;
     //private int topicIndice;
     // 0 = TCPROS, 1 = UDPROS for preferred_protocol
     //private int preferred_protocol;
     // String preferred_protocol
     //private String str_preferred_protocol;
     // Publisher receiver port
     //private int publisher_receiver_port;
     //private Object[] req_topic_response;
     //private Object[] protocol_array;
     /** Implement this class to support requestTopic as a Publisher via the Slave API. */
     public Object[] requestTopic(String topic, Object[] protocols)
     {
         // TODO: Implement these localized Objects in an Object Cache for better performance and less GC overhead.
         int topicIndice;
         // Publisher receiver port
         int publisher_receiver_port=-1;
         String str_preferred_protocol;
         Object[] req_topic_response;
         Object[] protocol_array;
         // temporary reference to CommunicationSenderInterface for reference to publisherThread
         TCPROSPublisherCommunicator tcp_sender;    
         UDPROSPublisherCommunicator udp_sender=null;          
         
         //System.out.println("Calling ROSSlave requestTopic on " + topic);
         // set the ROS Node topic indice.
         topicIndice=node.getTopicIndex(topic);
         str_preferred_protocol=node.topicPreferredProtocol[topicIndice];
         req_topic_response = new Object[3];
// TODO: Fix this method once the Publisher protocol stuff is fixed for multiple publisher, multiple protocols.         
         //System.out.println("protocols.length: " +protocols.length);
         for(int i = 0;i<protocols.length;i++)
         {
            protocol_array=((Object[])protocols[i]); 
            //System.out.println("ROSSlave Inside Slave requestTopic protocols for loop...");
            //try
            //{
            //    System.out.println("Subscriber protocol: " + ((String)((Object[])protocols[i])[0]) + ", preferred protocol: " + str_preferred_protocol);
            //}
           // catch(Exception e)
           // {
           //     e.printStackTrace();
            //}
            if(0<protocol_array.length&&((String)protocol_array[0]).equalsIgnoreCase("TCPROS")&&str_preferred_protocol.equalsIgnoreCase("TCPROS"))
            {
              //  System.out.println("ROSSlave requestTopic selected TCPROS protocol...");
                try
                {
                    if(node.isInitialized()==false)
                    {
                        while(node.isInitialized()==false)
                        {
                          Thread.sleep(15);
                        }
                    }
//                    System.out.println("node: " + node + ", node.getPublisherSenders: " + node.getPublisherSenders());   
 //            Logger.getLogger(ROSSlave.class.getName()).log(Level.INFO,"ROS Slave requestTopic topicIndex for " + topic + ": " + String.valueOf(topicIndice) + ". Call to node.getTopicIndex(topic) returns: " + String.valueOf(node.getTopicIndex(topic)));                     
                    tcp_sender = (TCPROSPublisherCommunicator)node.getPublisherSenders()[topicIndice];
                    if(tcp_sender!=null&&tcp_sender.isRunning())
                    {
                        //System.out.println("ROSSlave Sender is running...");
                        // Is initialized.
                        publisher_receiver_port=tcp_sender.getReceivePort();
                    }
                    else if(tcp_sender!=null)
                    {
                       // System.out.println("ROSSlave Sender not running... Calling start...");
                        try 
                        {
                            Thread.sleep(15);
                            if(!tcp_sender.isRunning())
                            {
                                // Need to call start 
                                tcp_sender.start();
                            }
                        } 
                        catch (RCSMException ex) 
                        {
                            Logger.getLogger(ROSSlave.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        publisher_receiver_port=tcp_sender.getReceivePort();
                        //System.out.println("Sender started on port: " + publisher_receiver_port);
                    }
                    else
                    {
                        // If Sender is null, throw an UnsupportedOperationException.
                        //System.out.println("ROSSlave Selected UDPROS protocol...");
//                        throw new UnsupportedOperationException();                        
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                req_topic_response[0]="TCPROS";
                //req_topic_response[1]=node.topicServerName[topicIndice];
                req_topic_response[1]=node.hostname;
                req_topic_response[2]=Integer.valueOf(publisher_receiver_port);                
//                System.out.println("Inside ROSSlave requestTopic returning:" + Arrays.deepToString(req_topic_response));
                return req_topic_response;
            }
            else if(0<protocol_array.length&&((String)protocol_array[0]).equalsIgnoreCase("UDPROS"))
            {

                    System.out.println("ROSSlave Selected UDPROS protocol...");
                    try
                    {
                        if(node.isInitialized()==false)
                        {
                            while(node.isInitialized()==false)
                            {
                              Thread.sleep(15);
                            }
                        }
    //                    System.out.println("node: " + node + ", node.getPublisherSenders: " + node.getPublisherSenders());   
     //            Logger.getLogger(ROSSlave.class.getName()).log(Level.INFO,"ROS Slave requestTopic topicIndex for " + topic + ": " + String.valueOf(topicIndice) + ". Call to node.getTopicIndex(topic) returns: " + String.valueOf(node.getTopicIndex(topic)));        
                        if(node.getPublisherSenders()[topicIndice]!=null&&node.getPublisherSenders()[topicIndice].getProtocolName().equals("UDPROS"))
                        {
                            // UDPROS Publisher Communicator
                            udp_sender=(UDPROSPublisherCommunicator)node.getPublisherSenders()[topicIndice];
                        }
                        else if(node.getPublisherSenders()[topicIndice]!=null&&node.getPublisherSenders()[topicIndice].getProtocolName().equals("TCPROS"))
                        {     
                            // Transform TCPROSPublisherCommunicator to MultiProtocolPublisherCommunicator
                            node.joinTCPUDPPublishers(topicIndice, "UDPROS");
                            // MultiProtocolPublisherCommunicator
                            udp_sender=((MultiProtocolPublisherCommunicator)node.getPublisherSenders()[topicIndice]).getUDPPublisherCommunicator();
                        }                    
                        else if(node.getPublisherSenders()[topicIndice]!=null&&node.getPublisherSenders()[topicIndice].getProtocolName().equals("UDPROS,TCPROS"))
                        {
                            // MultiProtocolPublisherCommunicator
                            udp_sender=((MultiProtocolPublisherCommunicator)node.getPublisherSenders()[topicIndice]).getUDPPublisherCommunicator();
                        }        

                        if(udp_sender!=null&&udp_sender.isRunning())
                        {
                            //System.out.println("ROSSlave Sender is running...");
                            // Is initialized.
                            publisher_receiver_port=udp_sender.getReceivePort();

                        }
                        else if(udp_sender!=null) 
                        {
                           // System.out.println("ROSSlave Sender not running... Calling start...");
                            try
                            {
                                Thread.sleep(15);
                                if(!udp_sender.isRunning())
                                {
                                    // Need to call start 
                                    udp_sender.start();
                                }
                            } 
                            catch (RCSMException ex) 
                            {
                                Logger.getLogger(ROSSlave.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            publisher_receiver_port=udp_sender.getReceivePort();
                            //System.out.println("Sender started on port: " + publisher_receiver_port);
                        }
                        else
                        {
                            // If Sender is null, throw an UnsupportedOperationException.
                            //System.out.println("ROSSlave Selected UDPROS protocol...");
    //                        throw new UnsupportedOperationException();                        
                        }
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                   
                try 
                {                    
                    req_topic_response[0]="UDPROS";
                    req_topic_response[1]=udp_sender.getHostName();
                    req_topic_response[2]=Integer.valueOf(udp_sender.getReceivePort());
                    req_topic_response[3]=Integer.valueOf(udp_sender.addSubscriber((String)protocol_array[2], ((Integer)protocol_array[3]).intValue(), ((Integer)protocol_array[4]).intValue()));
                    req_topic_response[4]=protocol_array[4];
                    req_topic_response[5]=UDPProtocolUtility.generateXMLRPCBase64(node.getCallerID(), node.topicMD5Sum[topicIndice], node.getSubscriberMessageManager().getTopicRegistryMessageDefinitionByTID(node.getSubscriberMessageManager().getTIDByTopicName(node.topicNames[topicIndice])).definition, topic, node.getTopicType(topic), node.topicLatching[topicIndice]);
                    
                  //  System.out.println("Inside ROSSlave requestTopic returning:" + Arrays.deepToString(req_topic_response));
                    return req_topic_response;
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(ROSSlave.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(ROSSlave.class.getName()).log(Level.SEVERE, null, ex);
                }
            } 
         }
         //System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!UnsupportedOperationException in ROSSlave");
         throw new UnsupportedOperationException();
     }
     
     /** Returns the callback port for reinitialization of Slave, and shutdown() the ROSSlave - includes closing the Callback port. */
     public int shutdown()
     {
         // TODO: Update the httpServer shutdown procedure to match the http server being used if changing server client.
         this.isListening=false;
         httpServer.shutdown();   
         this.client=null;
         httpServer=null;
         return callbackPort;
     }
     
     /** This method is intended for system use only (i.e. the ROSSlave is manually being shutdown, or a problem is detected that may require reset even though one does not exist). Not sure if this will update the SlaveGroup on a publisher update or not. Is untested, and may require additional line of code to set this slave to null in Slave Group... or shut it down. Just unknown.  */
     protected void setIsListening(boolean isListening)
     {
         this.isListening=isListening;
     }

    private String method_name;
    private int param_count;
    private Object type;
    private Object[] pub_object_array;
    private String[] pub_string_array;
    private int pub_string_convert_loop_counter=0;
    @Override
    public Object execute(XmlRpcRequest xrr) throws XmlRpcException 
    {
        this.method_name =xrr.getMethodName();
        this.param_count=xrr.getParameterCount();        
        if(method_name.equals("publisherUpdate"))
        {
            if(param_count==3)
            {
                try 
                {
                                        // Instantiate object and string array to convert Object array to String[].
                    this.pub_object_array=(Object[])xrr.getParameter(2);
                    this.pub_string_array=new String[pub_object_array.length];
                    // Set loop counter to 0.
                    this.pub_string_convert_loop_counter = 0;
                    while(pub_string_convert_loop_counter<pub_object_array.length)
                    {
                        pub_string_array[pub_string_convert_loop_counter]=pub_object_array[pub_string_convert_loop_counter].toString();
                        this.pub_string_convert_loop_counter = pub_string_convert_loop_counter + 1;
                    }                          
                    return publisherUpdate((String)xrr.getParameter(0), (String)xrr.getParameter(1), pub_string_array);
                } 
                catch (ROSXMLRPCException ex) 
                {
                    Logger.getLogger(ROSSlave.class.getName()).log(Level.INFO, null, ex);
                    this.statusResponse[0]=(statusCode=Integer.valueOf(-1));
                    this.statusResponse[1]=(statusMessage="Error executing XMLRPC method publisherUpdate.");
                    this.statusResponse[2]=(this.processID=Integer.valueOf(0));        
                    return statusResponse;  
                }
            }
            else
            {
                    this.statusResponse[0]=(statusCode=Integer.valueOf(-1));
                    this.statusResponse[1]=(statusMessage="Incorrect input parameters for publisherUpdate (" + param_count + "). Expected publisherUpdate(String caller_id, String topic, String[] publishers).");
                    this.statusResponse[2]=(this.processID=Integer.valueOf(0));        
                    return statusResponse;                  
            }              
        }
        else if(method_name.equals("getBusStats"))
        {
            if(param_count==1)
            {
                try 
                {
                    return getBusStats((String)xrr.getParameter(0));
                } 
                catch (ROSXMLRPCException ex) 
                {
                    Logger.getLogger(ROSSlave.class.getName()).log(Level.INFO, null, ex);
                    this.statusResponse[0]=(statusCode=Integer.valueOf(-1));
                    this.statusResponse[1]=(statusMessage="Error executing XMLRPC method getBusStats.");
                    this.statusResponse[2]=(this.processID=Integer.valueOf(0));        
                    return statusResponse;  
                }
            }
            else
            {
                    this.statusResponse[0]=(statusCode=Integer.valueOf(-1));
                    this.statusResponse[1]=(statusMessage="Incorrect input parameters for getBusStats (" + param_count + "). Expected getBusStats(String caller_id).");
                    this.statusResponse[2]=(this.processID=Integer.valueOf(0));        
                    return statusResponse;                  
            }
        }
        else if(method_name.equals("getBusInfo"))
        {
            if(param_count==1)
            {
                try 
                {
                    return getBusInfo((String)xrr.getParameter(0));
                } 
                catch (ROSXMLRPCException ex) 
                {
                    Logger.getLogger(ROSSlave.class.getName()).log(Level.INFO, null, ex);
                    this.statusResponse[0]=(statusCode=Integer.valueOf(-1));
                    this.statusResponse[1]=(statusMessage="Error executing XMLRPC method getBusInfo.");
                    this.statusResponse[2]=(this.processID=Integer.valueOf(0));        
                    return statusResponse;  
                }
            }
            else
            {
                    this.statusResponse[0]=(statusCode=Integer.valueOf(-1));
                    this.statusResponse[1]=(statusMessage="Incorrect input parameters for getBusInfo (" + param_count + "). Expected getBusInfo(String caller_id).");
                    this.statusResponse[2]=(this.processID=Integer.valueOf(0));        
                    return statusResponse;                  
            }            
        }
        else if(method_name.equals("getMasterUri"))
        {
            if(param_count==1)
            {
                return getMasterUri((String)xrr.getParameter(0));
            }
            else
            {
                this.statusResponse[0]=(statusCode=Integer.valueOf(-1));
                this.statusResponse[1]=(statusMessage="Incorrect input parameters for getMasterUri (" + param_count + "). Expected getMasterUri(String caller_id).");
                this.statusResponse[2]=(this.processID=Integer.valueOf(0));        
                return statusResponse;                  
            }              
        }
        else if(method_name.equals("shutdown"))
        {
            if(param_count==2)
            {
                try 
                {
                    return shutdown((String)xrr.getParameter(0),(String)xrr.getParameter(1));
                } 
                catch (ROSXMLRPCException ex) 
                {
                    Logger.getLogger(ROSSlave.class.getName()).log(Level.INFO, null, ex);
                    this.statusResponse[0]=(statusCode=Integer.valueOf(-1));
                    this.statusResponse[1]=(statusMessage="Error executing XMLRPC method shutdown.");
                    this.statusResponse[2]=(this.processID=Integer.valueOf(0));        
                    return statusResponse;                      
                }
            }
            else
            {
                this.statusResponse[0]=(statusCode=Integer.valueOf(-1));
                this.statusResponse[1]=(statusMessage="Incorrect input parameters for shutdown (" + param_count + "). Expected shutdown(String caller_id, String message).");
                this.statusResponse[2]=(this.processID=Integer.valueOf(0));        
                return statusResponse;                  
            }               
        } 
        else if(method_name.equals("getPid"))
        {
            if(param_count==1)
            {
                return getPid((String)xrr.getParameter(0));
            }
            else
            {
                this.statusResponse[0]=(statusCode=Integer.valueOf(-1));
                this.statusResponse[1]=(statusMessage="Incorrect input parameters for getPid (" + param_count + "). Expected getPid(String caller_id).");
                this.statusResponse[2]=(this.processID=Integer.valueOf(0));        
                return statusResponse;                  
            }   
                    
        }        
        else if(method_name.equals("getSubscriptions"))
        {
            if(param_count==1)
            {
                try 
                {
                    return getSubscriptions((String)xrr.getParameter(0));
                } 
                catch (ROSXMLRPCException ex) 
                {
                    Logger.getLogger(ROSSlave.class.getName()).log(Level.INFO, null, ex);
                    this.statusResponse[0]=(statusCode=Integer.valueOf(-1));
                    this.statusResponse[1]=(statusMessage="Error executing XMLRPC method getSubscriptions.");
                    this.statusResponse[2]=(this.processID=Integer.valueOf(0));        
                    return statusResponse;  
                }
            }
            else
            {
                    this.statusResponse[0]=(statusCode=Integer.valueOf(-1));
                    this.statusResponse[1]=(statusMessage="Incorrect input parameters for getSubscriptions (" + param_count + "). Expected getSubscriptions(String caller_id).");
                    this.statusResponse[2]=(this.processID=Integer.valueOf(0));        
                    return statusResponse;                  
            }            
        } 
        else if(method_name.equals("getPublications"))
        { 
            if(param_count==1)
            {
                try 
                {
                    return getPublications((String)xrr.getParameter(0));
                } 
                catch (ROSXMLRPCException ex) 
                {
                    Logger.getLogger(ROSSlave.class.getName()).log(Level.INFO, null, ex);
                    this.statusResponse[0]=(statusCode=Integer.valueOf(-1));
                    this.statusResponse[1]=(statusMessage="Error executing XMLRPC method getPublications.");
                    this.statusResponse[2]=(this.processID=Integer.valueOf(0));        
                    return statusResponse;  
                }
            }
            else
            {
                    this.statusResponse[0]=(statusCode=Integer.valueOf(-1));
                    this.statusResponse[1]=(statusMessage="Incorrect input parameters for getPublications (" + param_count + "). Expected getPublications(String caller_id).");
                    this.statusResponse[2]=(this.processID=Integer.valueOf(0));        
                    return statusResponse;                  
            }                
                       
        }   
        else if(method_name.equals("requestTopic"))
        {
            if(param_count==3)
            {
                try 
                {
                    return requestTopic((String)xrr.getParameter(0), (String)xrr.getParameter(1), (Object[])xrr.getParameter(2));
                } 
                catch (ROSXMLRPCException ex) 
                {
                    Logger.getLogger(ROSSlave.class.getName()).log(Level.INFO, null, ex);
                    this.statusResponse[0]=(statusCode=Integer.valueOf(-1));
                    this.statusResponse[1]=(statusMessage="Error executing XMLRPC method requestTopic.");
                    this.statusResponse[2]=(this.processID=Integer.valueOf(0));        
                    return statusResponse;  
                }
            }
            else
            {
                    this.statusResponse[0]=(statusCode=Integer.valueOf(-1));
                    this.statusResponse[1]=(statusMessage="Incorrect input parameters for requestTopic (" + param_count + "). Expected requestTopic(String caller_id, String topic, Object[] protocols).");
                    this.statusResponse[2]=(this.processID=Integer.valueOf(0));        
                    return statusResponse;                  
            }

        } 
        else if(method_name.equals("paramUpdate"))
        { 
             // Select specific paramUpdate Implementation here.
            if(param_count==3)
            {
                this.type=(Object[])xrr.getParameter(2);
                if(type instanceof Map)
                {
                    return paramUpdate((String)xrr.getParameter(0), (String)xrr.getParameter(1), (Map)xrr.getParameter(2));
                }
                else if(type instanceof Vector)
                {
                    return paramUpdate((String)xrr.getParameter(0), (String)xrr.getParameter(1), (Vector)xrr.getParameter(2));
                }
                else if(type instanceof List)
                {
                    return paramUpdate((String)xrr.getParameter(0), (String)xrr.getParameter(1), (List)xrr.getParameter(2));
                }
                else if(type instanceof String)
                {
                    return paramUpdate((String)xrr.getParameter(0), (String)xrr.getParameter(1), (String)xrr.getParameter(2));
                }        
                else if(type instanceof Double)
                {
                    return paramUpdate((String)xrr.getParameter(0), (String)xrr.getParameter(1), ((Double)xrr.getParameter(2)).doubleValue());
                }
                else if(type instanceof Integer)
                {
                    return paramUpdate((String)xrr.getParameter(0), (String)xrr.getParameter(1), ((Integer)xrr.getParameter(2)).intValue());
                }     
                else if(type instanceof Short)
                {
                    return paramUpdate((String)xrr.getParameter(0), (String)xrr.getParameter(1), ((Short)xrr.getParameter(2)).shortValue());
                }   
                else if(type instanceof Byte)
                {
                    return paramUpdate((String)xrr.getParameter(0), (String)xrr.getParameter(1), ((Byte)xrr.getParameter(2)).byteValue());
                }   
                else if(type instanceof Character)
                {
                    return paramUpdate((String)xrr.getParameter(0), (String)xrr.getParameter(1), ((Character)xrr.getParameter(2)).charValue());
                }     
                else if(type instanceof Boolean)
                {
                    return paramUpdate((String)xrr.getParameter(0), (String)xrr.getParameter(1), ((Boolean)xrr.getParameter(2)).booleanValue());
                }                                  
                else
                {
                    this.statusResponse[0]=(statusCode=Integer.valueOf(-1));
                    this.statusResponse[1]=(statusMessage="Unsupported input parameter Object type for paramUpdate (" + param_count + "). Expected paramUpdate(String caller_id, String key, Object value).");
                    this.statusResponse[2]=(this.processID=Integer.valueOf(0));        
                    return statusResponse;                         
                }
            }
            else
            {
                this.statusResponse[0]=(statusCode=Integer.valueOf(-1));
                this.statusResponse[1]=(statusMessage="Incorrect input parameters for paramUpdate (" + param_count + "). Expected paramUpdate(String caller_id, String key, Object value).");
                this.statusResponse[2]=(this.processID=Integer.valueOf(0));        
                return statusResponse;                  
            }            
        }           
        else
        {
            this.statusResponse[0]=(statusCode=Integer.valueOf(-1));
            this.statusResponse[1]=(statusMessage="Method not supported.");
            this.statusResponse[2]=(this.processID=Integer.valueOf(0));        
            return statusResponse;                         
        }
    }

    /** getHandler added to support ROSSlave class as a handler to avoid creating more unecessary Objects. */
    @Override
    public XmlRpcHandler getHandler(String string) throws XmlRpcNoSuchHandlerException, XmlRpcException 
    {
        return (XmlRpcHandler)this;
    }
    
    /** Return the slave XmlRpcClient. Used by ROSNode to make calls to associated Publisher nodes. */
    public XmlRpcClient getXmlRpcClient()
    {
        return client;
    }
}
