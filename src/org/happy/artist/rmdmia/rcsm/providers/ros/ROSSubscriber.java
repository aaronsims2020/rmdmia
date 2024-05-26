package org.happy.artist.rmdmia.rcsm.providers.ros;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.webserver.WebServer;

/** ROSSubscriber.java - An implementation of the ROS Slave Group for the ROSNode
 *  Master/Slave API implementation. The ROSSubscriber is intended to implement 
 *  latching functionality for Topic Subscriptions that implement multiple topic
 *  advertisers (publishers via updatePublishers XML-RPC).  Multiple publishers 
 *  may be implemented for the purpose of higher bandwidth on multiple threads, 
 *  or any number of purposes where multiple publishers work together to support 
 *  a single topic. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2013 Happy Artist. All rights reserved.
 */
public class ROSSubscriber 
{
    // TODO: Performance Enhancement, use the Object Pool, and MovementProcessorManager and its implementations/algoritms to manage multiple Slaves, in multiple topics instead of this non-optimized code.
    protected ROSSlaveCaller[] callers;
    protected ROSSlave[] slaves;    
    protected String[] publicationURIs;
    // isAvailable simple marks a flag if a particular indice is removed so when add is called it can use that index.
    protected boolean[] isAvailable;
    private int latchedSlaves=0;
    protected String topic;
    protected String topicType;
    public int topicIndice=-1;
    private int initialGroupSize;
    // while loop counter for getSlaveByPublisherURI(String publisherURI)
    private int countPubURIs=0;
    // WebServer Definition to be used by the add method for adding new ROS Slave instances to this group. 
    private WebServer http;
    // HttpServer callback port definition for the HttpServer instance in the add method for adding new ROS Slave instance to this group.
    private int port;    
    // The reference to the ROSNode managing the ROSSubscriber topic.
    private ROSNode rosNode;
    // addLoopCount is the loop counter used in add method while loop.
    private int addLoopCount;
    // removeLoopCount is the loop counter used in remove method while loop.    
    private int removeLoopCount;
    //publisherUpdate method loop counter.
    private int pubUpdateCount;
    // URL reference for assigning new URLs.
    private URL pubUpdateURL;
    private ROSSlave pubUpdateSlave;
    private int pubUpdateCallbackPort;    
    
    /** The ROSSubscriber represents an array of multiple ROS Publishers that are used by the ROSSlave Subscriber.
     * @param topic - (String) The ROS Topic.
     * @param topicType - (String) The ROS Topic Type.
     * @param initialGroupSize - (int) initialGroupSize is the initial length of the ROSSlave array. 
     *  Every added Slave over the initial length require reinitialization of the 
     *  Arrays, thus causing a performance bottleneck when re-processing occurs 
     *  for multiple Array initializations.
     * @param topicIndice - the ROSNode Topic Indice assigned to this topic.  
     */
    public ROSSubscriber(ROSNode rosNode, String topic, String topicType, int initialGroupSize, int topicIndice)
    {
        if(rosNode==null)
        {
            throw new NullPointerException("ROSSlaveGroup for topic: " + topic + " ROSNode node input parameter is null.");
        }
        // Initialize the topic variables for the Topic ROSSlave latching.
        this.rosNode=rosNode;
        this.topic=topic;
        this.topicType=topicType;
        this.initialGroupSize=initialGroupSize;
        this.topicIndice=topicIndice;
        // Initialize the publicationURIs, and slaves arrays.
        this.publicationURIs=new String[initialGroupSize];
        this.slaves=new ROSSlave[initialGroupSize];
        this.callers=new ROSSlaveCaller[initialGroupSize];        
        this.isAvailable=new boolean[initialGroupSize];
        // initially populate with isAvailable.
        Arrays.fill(isAvailable, true);
        
    }
    
    // Done
    /** Add a new ROSSlave for the specified publisher URI. */
    public synchronized void add(URL publisherURI) throws IOException, XmlRpcException
    {
        if(publisherURI==null)
        {
            throw new NullPointerException("publisherURI input parameter is null.");
        }
        // increase array sizes if latchedSlaves is greater than or equal to publicationURIs.length. Theoretically, this could of simply been ==, however, in the case of a bizarre race condition between threads this just seemed safer without the use of synchronized blocks.
        if(latchedSlaves>=publicationURIs.length)
        {
            // increment the publicationURIs length by initialGroupSize.
            this.publicationURIs=Arrays.copyOf(publicationURIs, (publicationURIs.length + initialGroupSize)); 
            this.slaves=Arrays.copyOf(slaves, (slaves.length + initialGroupSize));
            this.callers=Arrays.copyOf(callers, (callers.length + initialGroupSize));           
            this.isAvailable=Arrays.copyOf(isAvailable, (isAvailable.length + initialGroupSize));            
            // add the new ROSSlave, and publisher URI arrays to the ROSSubscriber Object.
            this.publicationURIs[latchedSlaves]=publisherURI.toString();
            this.slaves[latchedSlaves] = new ROSSlave(rosNode.getMasterURL(), publisherURI, null, rosNode, topic);
            this.callers[latchedSlaves]=new ROSSlaveCaller(publisherURI);              
            // TODO: Instantiate the Connection in ROSSlave - not sure if this should apply to Remote Publisher URL, or local Server Socket Listener. May need to remove entirely, or focus on calling Master/Slave API for setting this one...      
            this.latchedSlaves=latchedSlaves + 1;
            this.isAvailable[latchedSlaves]=false;
        }
        else if(isAvailable[latchedSlaves])
        {
            // add the new ROSSlave, and publisher URI arrays to the ROSSubscriber Object.
            this.publicationURIs[latchedSlaves]=publisherURI.toString();
           // System.out.println("slaves.length:" + slaves.length + ", latchedSlaves:" + latchedSlaves);
            this.slaves[latchedSlaves] = new ROSSlave(rosNode.getMasterURL(), publisherURI, null, rosNode, topic);
            //System.out.println("callers.length:" + callers.length + ", latchedSlaves:" + latchedSlaves);
            try
            {
                this.callers[latchedSlaves]=new ROSSlaveCaller(publisherURI);              
            }
            catch(ArrayIndexOutOfBoundsException e)
            {
                System.out.println("callers.length:" + callers.length + ", latchedSlaves:" + latchedSlaves);         
                e.printStackTrace();
            }
            // TODO: Instantiate the Connection in ROSSlave - not sure if this should apply to Remote Publisher URL, or local Server Socket Listener. May need to remove entirely, or focus on calling Master/Slave API for setting this one...      
            this.isAvailable[latchedSlaves]=false;
            this.latchedSlaves=latchedSlaves + 1;            
        }
        else
        {
            // Add the Slave to the unused element 
            this.addLoopCount=0;
            while(addLoopCount<isAvailable.length)
            {
                if(isAvailable[addLoopCount])
                {
                    this.latchedSlaves=latchedSlaves + 1;
                    this.isAvailable[addLoopCount]=false;
                    // add the new ROSSlave, and publisher URI arrays to the ROSSubscriber Object.
                    this.publicationURIs[addLoopCount]=publisherURI.toString();
                    this.slaves[addLoopCount] = new ROSSlave(rosNode.getMasterURL(), publisherURI, null, rosNode, topic);
                    this.callers[addLoopCount]=new ROSSlaveCaller(publisherURI);  
                    // TODO: Instantiate the Connection in ROSSlave - not sure if this should apply to Remote Publisher URL, or local Server Socket Listener. May need to remove entirely, or focus on calling Master/Slave API for setting this one...      
                    // Terminate this while loop.
                    this.addLoopCount=isAvailable.length;
                }
                this.addLoopCount=addLoopCount + 1;
            }
        }
    }
    
    /** Set a new instance of ROSSlave for the specified slavesIndice (used to reset the Thread when the httpServer ServerSocket fails.). */
    public void set(URL publisherURI, int callbackPort, int slavesIndice) throws IOException, XmlRpcException
    {
        if(publisherURI==null)
        {
            throw new NullPointerException("publisherURI input parameter is null.");
        }
        // increase array sizes if latchedSlaves is greater than or equal to publicationURIs.length. Theoretically, this could of simply been ==, however, in the case of a bizarre race condition between threads this just seemed safer without the use of synchronized blocks.
        if(slavesIndice<publicationURIs.length)
        {
            // add the new ROSSlave element to the ROSSubscriber Object.
            this.slaves[slavesIndice] = new ROSSlave(rosNode.getMasterURL(), publisherURI, callbackPort, rosNode, topic);
            this.callers[slavesIndice]=new ROSSlaveCaller(publisherURI);  
            // TODO: Instantiate the Connection in ROSSlave - not sure if this should apply to Remote Publisher URL, or local Server Socket Listener. May need to remove entirely, or focus on calling Master/Slave API for setting this one...      
            this.isAvailable[slavesIndice]=false;
        }
    }
    
    //Done
    /** Remove a ROSSlave instance by the specified publisher URI. */
    public synchronized void remove(String publisherURI)
    {
        // remove the new ROSSlave, and publisher URI arrays to the ROSSubscriber Object.
        this.removeLoopCount=0;
        while(removeLoopCount<publicationURIs.length)
        {
            if(publicationURIs[removeLoopCount].equals(publisherURI))
            {
                this.isAvailable[latchedSlaves]=true;
                this.latchedSlaves=latchedSlaves - 1;
                this.publicationURIs[latchedSlaves]=null;
                this.slaves[latchedSlaves].shutdown();
                this.callers[latchedSlaves].shutdown();
                this.slaves[latchedSlaves]=null;
                this.callers[latchedSlaves]=null;                
                // TODO: Instantiate the Connection in ROSSlave - not sure if this should apply to Remote Publisher URL, or local Server Socket Listener. May need to remove entirely, or focus on calling Master/Slave API for setting this one...      
                this.removeLoopCount=publicationURIs.length;
            }
            this.removeLoopCount= removeLoopCount + 1;
        }
         
    } 
    
    //Done 
    /** Remove a ROSSlave instance by the specified publisher URI latching indice. */
    public synchronized void remove(int latchingIndice)
    {
        // remove the new ROSSlave, and publisher URI arrays to the ROSSubscriber Object.
        this.isAvailable[latchedSlaves]=true;
        this.latchedSlaves=latchedSlaves - 1;
        this.publicationURIs[latchedSlaves]=null;
        this.slaves[latchedSlaves].shutdown();
        this.slaves[latchedSlaves]=null;
        this.callers[latchedSlaves].shutdown();
        this.callers[latchedSlaves]=null;
        // TODO: Instantiate the Connection in ROSSlave - not sure if this should apply to Remote Publisher URL, or local Server Socket Listener. May need to remove entirely, or focus on calling Master/Slave API for setting this one...      
    }       
    
    private int isConnectedLoopCount;
    /** return boolean isConnected for ROSSlave associated with specified publisherURI. */
    public boolean isConnected(String publisherURI)
    {
        this.isConnectedLoopCount=0;
        while(isConnectedLoopCount<publicationURIs.length)
        {
            if(publicationURIs[isConnectedLoopCount].equals(publisherURI))
            {
                return slaves[isConnectedLoopCount].isListening();
            }
            this.isConnectedLoopCount= isConnectedLoopCount + 1;
        }        
        return false;
    }

    //Done
    /** return boolean isConnected for ROSSlave associated with specified publisherURI latching indice. */
    public boolean isConnected(int latchingIndice)
    {
         return slaves[latchedSlaves].isListening();
    }
    
    /** A call to publisherUpdate was made from a topic connection. Update the connections. */
    public void publisherUpdate(String caller_id, String topic, String[] publishers) throws XmlRpcException
    {
        // Check if publisher is initialized (if not initialize new ROSSlave on Publisher URI, and if so is publisher connected? If not reconnect
        // reset pubUpdateCount loop counter to 0;       
        this.pubUpdateCount=0;      
        if(publishers!=null&&latchedSlaves>0)
        {
            while(pubUpdateCount<publishers.length)
            {
                //System.out.println("publishers.length:" + publishers.length + ", publisher value:" + publishers[pubUpdateCount]);
                this.pubUpdateSlave=getSlaveByPublisherURI(publishers[pubUpdateCount]);
                if(pubUpdateSlave!=null)
                {
                    // TODO: add code to process slaves that may of experienced error for restart of Object.
                    // shutdown and restart with a new Slave. Probably a problem with connection or code or something (prototype approach).
                    if(pubUpdateSlave.isListening()==false)
                    {
                        this.pubUpdateCallbackPort=pubUpdateSlave.shutdown();
                        try 
                        { 
                            set(this.pubUpdateURL=new URL(publishers[pubUpdateCount]), pubUpdateCallbackPort, pubUpdateCount);
                        } 
                        catch (MalformedURLException ex) 
                        {
                            Logger.getLogger(ROSSubscriber.class.getName()).log(Level.WARNING, null, ex);
                        } 
                        catch (IOException ex) 
                        {
                            Logger.getLogger(ROSSubscriber.class.getName()).log(Level.WARNING, null, ex);
                        }
                    }
                }
                else
                {
                    // add new Publisher for specified publisherURI
                    try
                    {
                        add(this.pubUpdateURL=new URL(publishers[pubUpdateCount])); 
                    } 
                    catch (MalformedURLException ex) 
                    {
                        Logger.getLogger(ROSSubscriber.class.getName()).log(Level.WARNING, null, ex);
                    } 
                    catch (IOException ex) 
                    {
                        Logger.getLogger(ROSSubscriber.class.getName()).log(Level.WARNING, null, ex);
                    }
                }
                this.pubUpdateCount = pubUpdateCount + 1;
            }
        }    
        else if(publishers!=null)
        {
            // Add all publishers.
            while(pubUpdateCount<publishers.length)
            {
                try
                {
                    add(this.pubUpdateURL=new URL(publishers[pubUpdateCount]));
                } 
                catch (MalformedURLException ex) 
                {
                    Logger.getLogger(ROSSubscriber.class.getName()).log(Level.WARNING, null, ex);
                } 
                catch (IOException ex) 
                {
                    Logger.getLogger(ROSSubscriber.class.getName()).log(Level.WARNING, null, ex);
                }                
                this.pubUpdateCount = pubUpdateCount + 1;
            }            
        }
    //    System.out.println("Publisher Update:"+caller_id+", topic: "+topic+", publishers: "+Arrays.toString(publishers));
    }
    
    // Done
    // TODO: This method needs to be synchronized for the counter if it is used asynchronously (i.e. called from more than one thread at the same time.).
    /** Returns the associated ROSSlave, and null if the ROSSlave has not been added to the ROSSlave group. */
    public ROSSlave getSlaveByPublisherURI(String publisherURI)
    {
        // TODO: Method needs support for possible null values via isAvailable checks.
        // reset loop counter to 0;
        this.countPubURIs=0;        
        if(publisherURI!=null&&latchedSlaves>0)
        {
            while(countPubURIs<publicationURIs.length)
            {    
 //               System.out.println("publisherURI: " + publisherURI);
//                System.out.println("publicationURIs: " + publicationURIs[countPubURIs] + ",Element: " + countPubURIs); 
                // Happy artist 2282014 added if not=null for null values. 
                if(publicationURIs[countPubURIs]!=null&&publicationURIs[countPubURIs].equals(publisherURI))
                {
                    return slaves[countPubURIs];
                }
                this.countPubURIs = countPubURIs + 1;
            }
        }
        return null;
    }
   
    // Done
    /** Returns the associated ROSSlave by input parameter of latching indice, and null if the ROSSlave has not been added to the ROSSlave group. */
    public ROSSlave getSlaveByPublisherURI(int latchingIndice)
    {
        // If latching lindex is in valid latching index array range continue.
        if(latchingIndice<slaves.length&&latchingIndice>-1)
        {
            return slaves[latchingIndice];
        }
        else
        {
            return null;
        }
    }  

    /** Returns the associated ROSSlaveCaller, and null if the ROSSlaveCaller has not been added to the ROSSlave group. */
    public ROSSlaveCaller getSlaveCallerByPublisherURI(String publisherURI)
    {
        // TODO: Method needs support for possible null values via isAvailable checks.
        // reset loop counter to 0;
        this.countPubURIs=0;        
        if(publisherURI!=null&&latchedSlaves>0)
        {
            while(countPubURIs<publicationURIs.length)
            {    
                if(publicationURIs[countPubURIs].equals(publisherURI))
                {
                    return callers[countPubURIs];
                }
                this.countPubURIs = countPubURIs + 1;
            }
        }
        return null;
    }    
    
    // Done
    /** Returns the associated ROSSlaveCaller by input parameter of latching indice, and null if the ROSSlaveCaller has not been added to the ROSSlave group. */
    public ROSSlaveCaller getSlaveCallerByPublisherURI(int latchingIndice)
    {
        // If latching lindex is in valid latching index array range continue.
        if(latchingIndice<slaves.length&&latchingIndice>-1)
        {
            return callers[latchingIndice];
        }
        else
        {
            return null;
        }
    }     
    
/*    public int getNextEmptySlaveIndice()
    {
        // increase array sizes if latchedSlaves is greater than or equal to publicationURIs.length. Theoretically, this could of simply been ==, however, in the case of a bizarre race condition between threads this just seemed safer without the use of synchronized blocks.
        if(latchedSlaves>=publicationURIs.length)
        {
            // increment the publicationURIs length by initialGroupSize.
            this.publicationURIs=Arrays.copyOf(publicationURIs, (publicationURIs.length + initialGroupSize)); 
            this.slaves=Arrays.copyOf(slaves, (slaves.length + initialGroupSize));
            // add the new ROSSlave, and publisher URI arrays to the ROSSubscriber Object.
            this.publicationURIs[latchedSlaves]=publisherURI.toString();
            this.slaves[latchedSlaves] = new ROSSlave(rosNode.getMasterURL(), publisherURI, null, rosNode);
            // TODO: Instantiate the Connection in ROSSlave - not sure if this should apply to Remote Publisher URL, or local Server Socket Listener. May need to remove entirely, or focus on calling Master/Slave API for setting this one...      
            this.latchedSlaves=latchedSlaves + 1;
        }
        else if(isAvailable[latchedSlaves])
        {
            // add the new ROSSlave, and publisher URI arrays to the ROSSubscriber Object.
            this.publicationURIs[latchedSlaves]=publisherURI.toString();
            this.slaves[latchedSlaves] = new ROSSlave(rosNode.getMasterURL(), publisherURI, null, rosNode);
            // TODO: Instantiate the Connection in ROSSlave - not sure if this should apply to Remote Publisher URL, or local Server Socket Listener. May need to remove entirely, or focus on calling Master/Slave API for setting this one...      
            this.isAvailable[latchedSlaves]=false;
            this.latchedSlaves=latchedSlaves + 1;            
        }
        else
        {
            // Add the Slave to the unused element 
            this.addLoopCount=0;
            while(addLoopCount<isAvailable.length)
            {
                if(isAvailable[addLoopCount])
                {
                    this.latchedSlaves=latchedSlaves + 1;
                    this.isAvailable[addLoopCount]=false;
                    // add the new ROSSlave, and publisher URI arrays to the ROSSubscriber Object.
                    this.publicationURIs[addLoopCount]=publisherURI.toString();
                    this.slaves[addLoopCount] = new ROSSlave(rosNode.getMasterURL(), publisherURI, null, rosNode);
                    // TODO: Instantiate the Connection in ROSSlave - not sure if this should apply to Remote Publisher URL, or local Server Socket Listener. May need to remove entirely, or focus on calling Master/Slave API for setting this one...      
                    // Terminate this while loop.
                    this.addLoopCount=isAvailable.length;
                }
                this.addLoopCount=addLoopCount + 1;
            }
        }    
    }
  */  
    // Done
    /** Return the Topic for this ROS Slave Group. */
    public String getTopic()
    {
        return topic;
    }

    // Done
    /** Return the Topic Type for this ROS Slave Group. */
    public String getTopicType()
    {
        return topicType;
    }  
    
    /** Returns the starting array size of the publisher URI related arrays. This can be adjusted at instantiation for optimum performance. Every time the limit goes over arrays must be reconstructed. */
    public int getInitialGroupSize()
    {
        return initialGroupSize;
    }

    /** Returns the current number of Publisher URI associated with this ROS Slave Group. */
    public int getSlaveCount()
    {
        return latchedSlaves;
    }
    
    // The shutdown loop counter.
    private int count; 
    /** Shutdown the ROSSubscriber (topic listeners). Iteratively shutdown each ROSSlave. */
    public void shutdown()
    {
        this.count = 0;
        while(count<slaves.length)
        {
            if(slaves[count]!=null)
            {
                this.latchedSlaves=latchedSlaves - 1;
                this.publicationURIs[count]=null;
                this.slaves[count].shutdown();
                this.slaves[count]=null;     
                this.callers[count].shutdown();
                this.callers[count]=null;
                this.isAvailable[count]=true;                
            }
            this.count = count + 1;
        }
        this.http=null;
        this.rosNode=null;
        this.pubUpdateSlave=null;
        this.pubUpdateURL=null;
        this.topic=null;
        this.topicType=null;
    }
}

