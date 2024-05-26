package org.happy.artist.rmdmia.rcsm.providers.ros;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

/** ROSSlaveCaller.java - An implementation of the ROS Slave Caller. This class makes calls to topic publishers Slave APIs.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2013 Happy Artist. All rights reserved.
 */
public class ROSSlaveCaller 
{
     // Define the XMLRPC Client.
     private XmlRpcClient client = new XmlRpcClient();
     // Master Server Connection Configuration
     private XmlRpcClientConfigImpl topicClientConfig = new XmlRpcClientConfigImpl();     
     // The XML-RPC Client Response Object[]
     private Object[] response;
     // The XML-RPC Response status (i.e. success=1, failure=0, and error=-1).
     private int responseStatus;
     // This nodes caller id for ROS.
     private String caller_id="/rmdmia";

     /** Instantiate the ROSSlaveCaller with the ROS Master URL, and the http callback port. If caller_id is null it defaults to /rmdmia. */
     public ROSSlaveCaller(URL rosTopicURL, String caller_id) throws IOException
     {
         if(caller_id!=null)
         {
             this.caller_id=caller_id;
         }
         // Setup the Master Server XML-RPC Config Object
         topicClientConfig.setServerURL(rosTopicURL);
         client.setConfig(topicClientConfig);         
     }     
     
     /** Instantiate the ROSSlaveCaller with the ROS Master URL, and the http callback port. If caller_id is null it defaults to /rmdmia. */
     public ROSSlaveCaller(URL rosTopicURL) throws IOException
     {
         // Setup the Master Server XML-RPC Config Object
         topicClientConfig.setServerURL(rosTopicURL);
         client.setConfig(topicClientConfig);
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
//      System.out.println("->Outbound Calling Get Bus Stats.");           
          this.response = (Object[])client.execute("getBusStats", new Object[]{caller_id});         
         this.responseStatus=((Integer)response[0]).intValue();
         if(responseStatus==1)
         {
             return response;
         }
         else if(responseStatus==0)
         {
             throw new ROSXMLRPCException("FAILURE (".concat(((Integer)response[2]).toString()).concat("): Method failed to complete correctly. In general, this means that the master/slave attempted the action and failed, and there may have been side-effects as a result."));          
         } 
         else if(responseStatus==-1)
         {
             throw new ROSXMLRPCException("ERROR (".concat(((Integer)response[2]).toString()).concat("): Error on the part of the caller, e.g. an invalid parameter. In general, this means that the master/slave did not attempt to execute the action."));             
         }
         else
         {
             // Unknown response - return normally for receiver to handle.
             return response;
         }
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
//      System.out.println("->Outbound Calling Get Bus Info.");          
          this.response = (Object[])client.execute("getBusInfo", new Object[]{caller_id});           
         this.responseStatus=((Integer)response[0]).intValue();
         if(responseStatus==1)
         {
             return response;
         }
         else if(responseStatus==0)
         {
             throw new ROSXMLRPCException("FAILURE (".concat(((Integer)response[2]).toString()).concat("): Method failed to complete correctly. In general, this means that the master/slave attempted the action and failed, and there may have been side-effects as a result."));          
         } 
         else if(responseStatus==-1)
         {
             throw new ROSXMLRPCException("ERROR (".concat(((Integer)response[2]).toString()).concat("): Error on the part of the caller, e.g. an invalid parameter. In general, this means that the master/slave did not attempt to execute the action."));             
         }
         else
         {
             // Unknown response - return normally for receiver to handle.
             return response;
         }     
     }

     /** Get the URI of the master node.
      * @param caller_id (String) ROS caller ID.
      * @return Returns (Integer, String, Integer) = (code, statusMessage, masterURI) 
      */            
     public Object[] getMasterUri(String caller_id) throws XmlRpcException, ROSXMLRPCException
     {
//      System.out.println("->Outbound Calling Get Master URI.");          
          this.response = (Object[])client.execute("getMasterUri", new Object[]{caller_id});          
         this.responseStatus=((Integer)response[0]).intValue();
         if(responseStatus==1)
         {
             return response;
         }
         else if(responseStatus==0)
         {
             throw new ROSXMLRPCException("FAILURE (".concat(((Integer)response[2]).toString()).concat("): Method failed to complete correctly. In general, this means that the master/slave attempted the action and failed, and there may have been side-effects as a result."));          
         } 
         else if(responseStatus==-1)
         {
             throw new ROSXMLRPCException("ERROR (".concat(((Integer)response[2]).toString()).concat("): Error on the part of the caller, e.g. an invalid parameter. In general, this means that the master/slave did not attempt to execute the action."));             
         }
         else
         {
             // Unknown response - return normally for receiver to handle.
             return response;
         }     
     }

     /** Stop this server.
      * @param caller_id (String) ROS caller ID.
      * @param message (String) A message describing why the node is being shutdown. 
      * @return Returns (Integer, String, Integer) = (code, statusMessage, ignore) 
      */        
     public Object[] shutdown(String caller_id, String message) throws XmlRpcException, ROSXMLRPCException
     {
//          System.out.println("->Outbound Calling shutdown."); 
          this.response = (Object[])client.execute("shutdown", new Object[]{caller_id, message});          
         this.responseStatus=((Integer)response[0]).intValue();
         if(responseStatus==1)
         {
             shutdown();
             return response;
         }
         else if(responseStatus==0)
         {
             throw new ROSXMLRPCException("FAILURE (".concat(((Integer)response[2]).toString()).concat("): Method failed to complete correctly. In general, this means that the master/slave attempted the action and failed, and there may have been side-effects as a result."));          
         } 
         else if(responseStatus==-1)
         {
             throw new ROSXMLRPCException("ERROR (".concat(((Integer)response[2]).toString()).concat("): Error on the part of the caller, e.g. an invalid parameter. In general, this means that the master/slave did not attempt to execute the action."));             
         }
         else
         {
             // Unknown response - return normally for receiver to handle.
             shutdown();
             return response;
         }     
     }

     /** Get the PID of this server.
      * @param caller_id (String) ROS caller ID.
      * @return Returns (Integer, String, Integer) = (code, statusMessage, serverProcessPID) 
      */
     public Object[] getPid(String caller_id) throws XmlRpcException, ROSXMLRPCException
     {
//          System.out.println("->Outbound Calling Get PID."); 
          this.response = (Object[])client.execute("getPid", new Object[]{caller_id});          
         this.responseStatus=((Integer)response[0]).intValue();
         if(responseStatus==1)
         {
             return response;
         }
         else if(responseStatus==0)
         {
             throw new ROSXMLRPCException("FAILURE (".concat(((Integer)response[2]).toString()).concat("): Method failed to complete correctly. In general, this means that the master/slave attempted the action and failed, and there may have been side-effects as a result."));          
         } 
         else if(responseStatus==-1)
         {
             throw new ROSXMLRPCException("ERROR (".concat(((Integer)response[2]).toString()).concat("): Error on the part of the caller, e.g. an invalid parameter. In general, this means that the master/slave did not attempt to execute the action."));             
         }
         else
         {
             // Unknown response - return normally for receiver to handle.
             return response;
         }     
     }

     /** Retrieve a list of topics that this node subscribes to.
      * @param caller_id (String) ROS caller ID.
      * @return Returns (code, statusMessage, topicList)  topicList is a list of topics this node subscribes to and is of the form [ [topic1, topicType1]...[topicN, topicTypeN] ]
      */     
     public Object[] getSubscriptions(String caller_id) throws XmlRpcException, ROSXMLRPCException
     {
//          System.out.println("->Outbound Calling Get Subscriptions.");          
          this.response = (Object[])client.execute("getSubscriptions", new Object[]{caller_id});          
         this.responseStatus=((Integer)response[0]).intValue();
         if(responseStatus==1)
         {
             return response;
         }
         else if(responseStatus==0)
         {
             throw new ROSXMLRPCException("FAILURE (".concat(((Integer)response[2]).toString()).concat("): Method failed to complete correctly. In general, this means that the master/slave attempted the action and failed, and there may have been side-effects as a result."));          
         } 
         else if(responseStatus==-1)
         {
             throw new ROSXMLRPCException("ERROR (".concat(((Integer)response[2]).toString()).concat("): Error on the part of the caller, e.g. an invalid parameter. In general, this means that the master/slave did not attempt to execute the action."));             
         }
         else
         {
             // Unknown response - return normally for receiver to handle.
             return response;
         }     
     }
        
     /** Retrieve a list of topics that this node publishes.
      * @param caller_id (String) ROS caller ID.
      * @return Returns (Integer, String, [[String, String]]) = (code, statusMessage, topicList) 
      * topicList is a list of topics published by this node and is of the form [[topic1, topicType1]...[topicN, topicTypeN]]
      */
     public Object[] getPublications(String caller_id) throws XmlRpcException, ROSXMLRPCException
     {
//          System.out.println("->Outbound Calling Get Publications.");          
          this.response = (Object[])client.execute("getPublications", new Object[]{caller_id});         
         this.responseStatus=((Integer)response[0]).intValue();
         if(responseStatus==1)
         {
             return response;
         }
         else if(responseStatus==0)
         {
             throw new ROSXMLRPCException("FAILURE (".concat(((Integer)response[2]).toString()).concat("): Method failed to complete correctly. In general, this means that the master/slave attempted the action and failed, and there may have been side-effects as a result."));          
         } 
         else if(responseStatus==-1)
         {
             throw new ROSXMLRPCException("ERROR (".concat(((Integer)response[2]).toString()).concat("): Error on the part of the caller, e.g. an invalid parameter. In general, this means that the master/slave did not attempt to execute the action."));             
         }
         else
         {
             // Unknown response - return normally for receiver to handle.
             return response;
         }     
     }

     /** Callback from master with updated value of subscribed parameter.
      * @param caller_id (String) ROS caller ID.
      * @param parameter_key (String) Parameter name, globally resolved.
      * @param parameter_value (!XMLRPCLegalValue(Java Object)) New parameter value.  
      * @return Returns (Integer, String, Integer) = (code, statusMessage, ignore) 
      */         
     public Object[] paramUpdate(String caller_id, String parameter_key, Object parameter_value) throws XmlRpcException, ROSXMLRPCException
     {
//          System.out.println("->Outbound Calling Param Update.");          
          this.response = (Object[])client.execute("paramUpdate", new Object[]{caller_id,parameter_key,parameter_value});             
         this.responseStatus=((Integer)response[0]).intValue();
         if(responseStatus==1)
         {
             return response;
         }
         else if(responseStatus==0)
         {
             throw new ROSXMLRPCException("FAILURE (".concat(((Integer)response[2]).toString()).concat("): Method failed to complete correctly. In general, this means that the master/slave attempted the action and failed, and there may have been side-effects as a result."));          
         } 
         else if(responseStatus==-1)
         {
             throw new ROSXMLRPCException("ERROR (".concat(((Integer)response[2]).toString()).concat("): Error on the part of the caller, e.g. an invalid parameter. In general, this means that the master/slave did not attempt to execute the action."));             
         }
         else
         {
             // Unknown response - return normally for receiver to handle.
             return response;
         }     
     }
     

     /** Callback from master of current publisher list for specified topic.
      * @param caller_id (String) ROS caller ID.
      * @param topic (String) Topic name.
      * @param publishers ([String]) List of current publishers for topic in the form of XMLRPC URIs 
      * @return Returns (Integer, String, Integer) = (code, statusMessage, ignore) 
      */   
     public Object[] publisherUpdate(String caller_id, String topic, String[] publishers) throws XmlRpcException, ROSXMLRPCException
     {
//          System.out.println("->Outbound Calling Publisher Update.");             
          this.response = (Object[])client.execute("publisherUpdate", new Object[]{caller_id,topic,publishers});        
         this.responseStatus=((Integer)response[0]).intValue();
         if(responseStatus==1)
         {
             return response;
         }
         else if(responseStatus==0)
         {
             throw new ROSXMLRPCException("FAILURE (".concat(((Integer)response[2]).toString()).concat("): Method failed to complete correctly. In general, this means that the master/slave attempted the action and failed, and there may have been side-effects as a result."));          
         } 
         else if(responseStatus==-1)
         {
             throw new ROSXMLRPCException("ERROR (".concat(((Integer)response[2]).toString()).concat("): Error on the part of the caller, e.g. an invalid parameter. In general, this means that the master/slave did not attempt to execute the action."));             
         }
         else
         {
             // Unknown response - return normally for receiver to handle.
             return response;
         }     
     }

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
     /** Publisher node API method called by a subscriber node. This requests that source allocate a channel for communication. Subscriber provides a list of desired protocols for communication. Publisher returns the selected protocol along with any additional params required for establishing connection. For example, for a TCP/IP-based connection, the source node may return a port number of TCP/IP server.
      * @param caller_id (String) ROS caller ID.
      * @param topic (String) Topic name. 
      * @param protocols ([ [String, !XMLRPCLegalValue*] ]) List of desired protocols for communication in order of preference. Each protocol is a list of the form [ProtocolName, ProtocolParam1, ProtocolParam2...N]
      * @return Returns (Integer, String, [String, !XMLRPCLegalValue*] ) = (code, statusMessage, protocolParams) protocolParams may be an empty list if there are no compatible protocols.  
      */   
     public Object[] requestTopic(String caller_id, String topic, Object[] protocols) throws XmlRpcException, ROSXMLRPCException
     {
         // System.out.println("->Outbound Calling Request Topic. Array:" + Arrays.deepToString(protocols) + ", byte[]" + ((byte[])((Object[])protocols[0])[1]).length);             
         this.response = (Object[])client.execute("requestTopic", new Object[]{caller_id,topic,protocols});
         this.responseStatus=((Integer)response[0]).intValue();
         if(responseStatus==1)
         {
             return response;
         }
         else if(responseStatus==0)
         {
             //System.out.println("requestTopic error: " + response[1]);
             System.out.println("requestTopic error: " + Arrays.deepToString(response));
             throw new ROSXMLRPCException("FAILURE (".concat(((Integer)response[2]).toString()).concat("): Method failed to complete correctly. In general, this means that the master/slave attempted the action and failed, and there may have been side-effects as a result."));          
         } 
         else if(responseStatus==-1)
         {
             throw new ROSXMLRPCException("ERROR (".concat(((Integer)response[2]).toString()).concat("): Error on the part of the caller, e.g. an invalid parameter. In general, this means that the master/slave did not attempt to execute the action."));             
         }
         else
         {
             // Unknown response - return normally for receiver to handle.
             return response;
         }     
         //this.response = (Object[])client.execute("requestTopic", new Object[]{CALLER_ID,topic,new String[]{"TCPROS"}});
     }
     
     /** shutdown() the ROSSlaveCaller - includes closing the Callback port. */
     public void shutdown()
     {
         try 
         {
             shutdown(caller_id, "Shutting down ROSSlaveCaller...");
         } 
         catch (XmlRpcException ex) 
         {
             // Ignore shutting down.
             //Logger.getLogger(ROSSlaveCaller.class.getName()).log(Level.SEVERE, null, ex);
         } 
         catch (ROSXMLRPCException ex) 
         {
             // Ignore shutting down.
             //Logger.getLogger(ROSSlaveCaller.class.getName()).log(Level.SEVERE, null, ex);
         }
         this.client=null;
     }     
     
     /** Return the XmlRpcClient Slave caller. */
     protected XmlRpcClient getXmlRpcClient()
     {
         return client;
     }
}