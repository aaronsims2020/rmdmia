package org.happy.artist.rmdmia.rcsm.providers.ros;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
//import java.util.logging.Level;
//import java.util.logging.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

/** ROSMaster.java - An implementation of the ROS Master/Slave API. This is the caller API.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2013 Happy Artist. All rights reserved.
 */
public class ROSMaster 
{
     // Define the XMLRPC Client.
     private XmlRpcClient client = new XmlRpcClient();
     // Master Server Connection Configuration
     private XmlRpcClientConfigImpl masterServerConfig = new XmlRpcClientConfigImpl();     
     // The XML-RPC Client Response Object[]
     private Object[] response;
     // The XML-RPC Response status (i.e. success=1, failure=0, and error=-1).
     private int responseStatus;
     // This nodes caller id for ROS.
     private String caller_id="/rmdmia";

     /** Instantiate the ROSMaster with the ROS Master URL, and the http callback port. If caller_id is null it defaults to /rmdmia. */
     public ROSMaster(URL rosMasterURL, String caller_id) throws IOException
     {
         if(caller_id!=null)
         {
             this.caller_id=caller_id;
         }
         // Setup the Master Server XML-RPC Config Object
         masterServerConfig.setServerURL(rosMasterURL);
         client.setConfig(masterServerConfig);         
     }     
     
     /** Instantiate the ROSMaster with the ROS Master URL, and the http callback port. If caller_id is null it defaults to /rmdmia. */
     public ROSMaster(URL rosMasterURL) throws IOException
     {
         // Setup the Master Server XML-RPC Config Object
         masterServerConfig.setServerURL(rosMasterURL);
         client.setConfig(masterServerConfig);
     }     
     
     /** Register the caller as a provider of the specified service. Returns Object[] (int, str, int) = (code, statusMessage, ignore). Exception is thrown on 0, or -1, all other codes are returned in Object[]. */
     public Object[] registerService(String callerID, String service, String serviceAPI, String callerAPI) throws XmlRpcException, ROSXMLRPCException
     {
         //System.out.println("registerService(".concat(callerID).concat(", ").concat(service).concat(", ").concat(serviceAPI).concat(", ").concat(callerAPI));           
         this.response=(Object[])client.execute("registerService", new String[]{callerID, service, serviceAPI, callerAPI});
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
         //   System.out.println("Values:" + ((Integer)serverResponse[0]).intValue() + ":" + ((String)serverResponse[1]) + ":" + ((Integer)serverResponse[2]).intValue());        
     }   
     
     /** Unregister the caller as a provider of the specified service. Returns Object[] (int, str, int) = (code, statusMessage, numUnregistered). Number of unregistrations (either 0 or 1). If this is zero it means that the caller was not registered as a service provider. The call still succeeds as the intended final state is reached.   */
     public Object[] unregisterService(String callerID, String service, String serviceAPI) throws XmlRpcException, ROSXMLRPCException
     {
         //System.out.println("unregisterService(".concat(callerID).concat(", ").concat(service).concat(", ").concat(serviceAPI));          
         this.response = (Object[])client.execute("unregisterService", new String[]{callerID, service, serviceAPI});
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
         //System.out.println("Values:" + ((Integer)serverResponse[0]).intValue() + ":" + ((String)serverResponse[1]) + ":" + ((Integer)serverResponse[2]).intValue());        
     }      

     /** Register the topic subscriber. Returns the topic index in the topicPublisherURLs array for getTopic String URL input parameter. */
     public Object[] registerSubscriber(String caller_id, String topic, String topic_type, String caller_api) throws XmlRpcException, ROSXMLRPCException
     {
         //System.out.println("registerSubscriber(".concat(caller_id).concat(", ").concat(topic).concat(", ").concat(topic_type).concat(", ").concat(caller_api));         
         this.response = (Object[])client.execute("registerSubscriber", new String[]{caller_id, topic, topic_type, caller_api});
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
     
     /** Unregister the caller as a publisher of the topic. Returns (int, str, int) = (code, statusMessage, numUnsubscribed). If numUnsubscribed is zero it means that the caller was not registered as a subscriber. The call still succeeds as the intended final state is reached. */
     public Object[] unregisterSubscriber(String caller_id, String topic, String caller_api) throws XmlRpcException, ROSXMLRPCException
     {
         //System.out.println("unregisterSubscriber(".concat(caller_id).concat(", ").concat(topic).concat(", ").concat(caller_api));               
         this.response = (Object[])client.execute("unregisterSubscriber", new String[]{caller_id, topic, caller_api});
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
         //  System.out.println("Values:" + ((Integer)serverResponse[0]).intValue() + ":" + ((String)serverResponse[1]) + ":" + ((Integer)serverResponse[2]).intValue());
     }        
     
    /** Register the caller as a publisher the topic. Returns (int, str, [str]) = (code, statusMessage, subscriberApis) List of current subscribers of topic in the form of XMLRPC URIs. */
     public Object[] registerPublisher(String caller_id, String topic, String topic_type, String caller_api) throws XmlRpcException, ROSXMLRPCException
     {
         //System.out.println("registerPublisher(".concat(caller_id).concat(", ").concat(topic).concat(", ").concat(topic_type).concat(", ").concat(caller_api));
         this.response = (Object[])client.execute("registerPublisher", new String[]{caller_id, topic, topic_type, caller_api});
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
         //System.out.println("Object [] length: " + serverResponse.length);
         // System.out.println("Object type 1: " + serverResponse[0].getClass().getName());
         // System.out.println("Object type 2: " + serverResponse[1].getClass().getName());
         // System.out.println("Object type 3: " + serverResponse[2].getClass().getName());  
         // System.out.println("Values:" + ((Integer)serverResponse[0]).intValue() + ":" + ((String)serverResponse[1]) + ":" + ((Object[])serverResponse[2]));        

          //         this.registerResponse = ((Object[])serverResponse[2]);
          //         System.out.println("Register publisher length: " + registerResponse.length + ", value: " + ((String)registerResponse[0]));
                   // set the topic publisher URL by register URL response
          //         for(int i = 0;i<subscriberTopics.length;i++)
          //         {
          //             if(subscriberTopics[i].equals(topic))
          //             {
          //                 topicSubscriberAPIs[i]=((String)registerResponse[0]);
          //                 return i;
          //             }
          //         }
     }
     
     /** Unregister the caller as a publisher of the topic. Returns (int, str, int) = (code, statusMessage, numUnsubscribed). If numUnregistered is zero it means that the caller was not registered as a publisher. The call still succeeds as the intended final state is reached. */
     public Object[] unregisterPublisher(String caller_id, String topic, String caller_api) throws XmlRpcException, ROSXMLRPCException
     {
         //System.out.println("unregisterPublisher(".concat(caller_id).concat(", ").concat(topic).concat(", ").concat(caller_api));         
        this.response = (Object[])client.execute("unregisterPublisher", new String[]{caller_id, topic, caller_api});
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
        //System.out.println("Values:" + ((Integer)serverResponse[0]).intValue() + ":" + ((String)serverResponse[1]) + ":" + ((Integer)serverResponse[2]).intValue());
     }   
     
     /** Get the XML-RPC URI of the node with the associated name/caller_id. This API is for looking information about publishers and subscribers. Use lookupService instead to lookup ROS-RPC URIs. Returns (int, str, str) - (code, statusMessage, URI)  */
     public Object[] lookupNode(String caller_id, String node_name) throws XmlRpcException, ROSXMLRPCException
     {
         this.response = (Object[])client.execute("lookupNode", new String[]{caller_id, node_name});
         this.responseStatus=((Integer)response[0]).intValue();
         if(responseStatus==1)
         {
             return response;
         }
         else if(responseStatus==0)
         {
             throw new ROSXMLRPCException("FAILURE (".concat(Arrays.toString(response)).concat("): Method failed to complete correctly. In general, this means that the master/slave attempted the action and failed, and there may have been side-effects as a result."));          
         } 
         else if(responseStatus==-1)
         {
             throw new ROSXMLRPCException("ERROR (".concat(Arrays.toString(response)).concat("): Error on the part of the caller, e.g. an invalid parameter. In general, this means that the master/slave did not attempt to execute the action."));             
         }
         else
         {
             // Unknown response - return normally for receiver to handle.
             return response;
         }
     }     
     
     /** Retrieve list topic names and their types. Returns (int, str, [ [str,str] ]) - (code, statusMessage, topicTypes). topicTypes is a list of [topicName, topicType] pairs.  */
     public Object[] getTopicTypes(String caller_id) throws XmlRpcException, ROSXMLRPCException
     {
         this.response = (Object[])client.execute("getTopicTypes", new String[]{caller_id});        
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

    /** Retrieve list representation of system state (i.e. publishers, subscribers, and services). 
     *  Returns (int, str, [[str,[str]],[str,[str]],[str,[str]]]) = (code, statusMessage, systemState)
     *
     *  - System state is in list representation: [publishers, subscribers, services]
     *  - publishers is of the form: [[topic1,[topic1Publisher1...topic1PublisherN]] ... ]
     *  - subscribers is of the form: [[topic1,[topic1Subscriber1...topic1SubscriberN]] ... ]
     *  - services is of the form: [[service1,[service1Provider1...service1ProviderN]] ... ]
     */
    public Object[] getSystemState(String caller_id) throws XmlRpcException, ROSXMLRPCException
    {
        this.response = (Object[])client.execute("getSystemState", new String[]{caller_id});      
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

    /** Get the URI of the the master. Returns (int, str, str) - (code, statusMessage, masterURI) */
    public Object[] getUri(String caller_id) throws XmlRpcException, ROSXMLRPCException
    {
        this.response = (Object[])client.execute("getUri", new String[]{caller_id});       
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

    /** Lookup all provider of a particular service. Returns (int, str, str) - (code, statusMessage, serviceUrl) service URL is provides address and port of the service. Fails if there is no provider. */
    public Object[] lookupService(String caller_id, String service) throws XmlRpcException, ROSXMLRPCException
    {
        this.response = (Object[])client.execute("lookupService", new String[]{caller_id, service});
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
     
     /** Get list of topics that can be subscribed to. This does not return topics that have no publishers. See getSystemState() to get more comprehensive list. Returns (int, str, [[str, str],]) - (code, statusMessage, [ [topic1, type1]...[topicN, typeN] ])  */
     public Object[] getPublishedTopics(String caller_id, String subgraph) throws XmlRpcException, ROSXMLRPCException
     {
         this.response = (Object[])client.execute("getPublishedTopics", new String[]{caller_id, subgraph});
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
         //            this.publisherCount=((Object[])serverResponse[2]).length;
         //            this.publishers = ((Object[])serverResponse[2]);

         //            for(int i=0;i<publishers.length;i++)
         //            {
         //                System.out.print(i + ":");
         //                this.fields = (Object[])publishers[i];
         //                for(int j=0;j<fields.length;j++)
         //                {
         //                    subscriberTopics[i]=((String)fields[0]);
         //                    subscriberTopicTypes[i]=((String)fields[1]);
         //                    System.out.print(((String)fields[0]) + ", type: " +((String)fields[1]) + "\n");      
         //                }
                      //System.out.println(i + ":" + ((String)publishers[i][0]) + ", type: " +((String)publishers[i][1]));      
         //            }             
     }
     
     /** shutdown() the ROSMaster - includes closing the Callback port. */
     public void shutdown()
     {
         try 
         {
             shutdown(caller_id, "Shutting down ROSMaster...");
         } 
         catch (XmlRpcException ex) 
         {
             // Ignore shutting down.
             //Logger.getLogger(ROSMaster.class.getName()).log(Level.SEVERE, null, ex);
         } 
         catch (ROSXMLRPCException ex) 
         {
             // Ignore shutting down.
             //Logger.getLogger(ROSMaster.class.getName()).log(Level.SEVERE, null, ex);
         }
         this.client=null;
     }
     
////////////////////// The following code below is the Slave API Implementation
////////////////////// for ROSMaster class. ///////////////////////////////////
     
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
                 // Logger.getLogger(ROSMaster.class.getName()).log(Level.INFO, "<-ROSMaster calling Publisher Update on topic: " + topic + Arrays.toString(publishers));
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
         this.response = (Object[])client.execute("requestTopic", new Object[]{caller_id,topic,protocols});
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
         //this.response = (Object[])client.execute("requestTopic", new Object[]{CALLER_ID,topic,new String[]{"TCPROS"}});
     }     
}
