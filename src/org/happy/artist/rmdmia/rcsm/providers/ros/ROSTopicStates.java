package org.happy.artist.rmdmia.rcsm.providers.ros;

import java.util.Arrays;

/** ROSTopicStates.java Each call to getSystemState via XMLRPC must call updateStart() preceding 
 *  setting of services, subscriptions, and publishers for topics for ROSTopicStates,
 *  and updateDone() after last setting is done.
 *  Not following this rule will cause removed topic services, subscriptions,
 *  and publishers to remain in many circumstances. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2013-2014 Happy Artist. All rights reserved.
 */

public class ROSTopicStates 
{
    private String[] subTopicNames = new String[0];
    private ROSTopicState[] subTopics = new ROSTopicState[0];
    private String[] pubTopicNames = new String[0];
    private ROSTopicState[] pubTopics = new ROSTopicState[0];    
    private String[] serviceNames = new String[0];
    private ROSTopicState[] services = new ROSTopicState[0];    
    // fill all false after each reset call. every topic state will be set to true as a call to setTopicPublishers, setTopicSubscribers, or setTopicServices on a set Topic. This ensures all topics are synchronized every call to XMLRPC getSystemState().
    private boolean[] sub_topic_states=new boolean[0];
    private boolean[] pub_topic_states=new boolean[0];    
    private boolean[] service_states=new boolean[0];    
    // purge topics if no mapped elements on reset (will increase performance on false if multiple topics are and the topic names never change, otherwide set to true if unknown (true is more resource intensive)).
    private boolean purgeTopicsOnReset = true;
    // variables used by method endUpdate()
    private int arrayLength; 
    private int removeCounter;   
    // topic index count for getTopicIndex while loop.
    private int currentIndice=0;
    // int reference for appendToSubTopics private class.
    private int appendSubIndice;
    // int reference for appendToPubTopics private class.
    private int appendPubIndice;    
    // int reference for appendToServices private class.
    private int appendServiceIndice;    
    // variables for method setTopicServices, setTopicPublishers, and setTopicSubscriptions
    private int topicServiceID;
    private int topicSubscriberID;
    private int topicPublisherID;
    // variables for getServices() method.
    private Object[] serviceElements;
    private String[] serviceArray = new String[10];
    private String currentService;
    private int serviceCount;
    private boolean isNew=true;
    private int count1;
    private int count2;
    private int count3;   
    // variables for getTopics method for Pub/Sub String Array merge, and remove duplicates.
    private String[] result;
    private String[] returnResult;     
    private int i_count = 0, j_count = 0;
    private int subTopicNamesLength;
    private int pubTopicNamesLength;
    private int strCount;
    private int counter;    
    
    /** Set purge topics on reset, if no mapped elements on reset (will increase performance on false if multiple topics are and the topic names never change, otherwise set to true if unknown (true is more resource intensive)). Defaults to true. */
    public void setPurgeTopicsOnReset(boolean purgeTopicsOnReset)
    {
        this.purgeTopicsOnReset=purgeTopicsOnReset;
    }
    
    /** Return boolean is set to purge topics on reset. */
    public boolean isPurgeTopicsOnReset()
    {
        return purgeTopicsOnReset;
    }
    
    /** always call updateStart() every time the XMLRPC method getSystemStatus is called preceding the set pub, sub, ser methods to ensure data synchronization. */
    public void updateStart()
    {
        // Reset the topic arrays to 0 length
// TODO: Fix the commented code        
        Arrays.fill(sub_topic_states, false);
        Arrays.fill(pub_topic_states, false);
        Arrays.fill(service_states, false);        
        for(int i =0;i<subTopicNames.length;i++)
        {
            subTopics[i].reset();
        }
        for(int i =0;i<pubTopicNames.length;i++)
        {
            pubTopics[i].reset();
        }        
        for(int i =0;i<serviceNames.length;i++)
        {
            services[i].reset();
        }        
    }
  
    /** always call updateStart() every time the XMLRPC method getSystemStatus is called preceding the set pub, sub, ser methods to ensure data synchronization. */   
    public void updateDone()
    {
        if(purgeTopicsOnReset)
        {
            // Do Sub Topics
            this.removeCounter=0;
            while(removeCounter<sub_topic_states.length)
            {
                if(sub_topic_states[removeCounter]==false)
                {
                    this.subTopicNames[removeCounter]=null;
                }
                this.removeCounter=removeCounter + 1;
            }
            this.arrayLength=subTopicNames.length;
            this.removeCounter=0;
            while(removeCounter<arrayLength)
            {
                if(subTopicNames[removeCounter]==null)
                {
                    System.arraycopy(subTopicNames,removeCounter+1,subTopicNames,removeCounter,subTopicNames.length-1-removeCounter);
                    System.arraycopy(subTopics,removeCounter+1,subTopics,removeCounter,subTopics.length-1-removeCounter);                                      
                    System.arraycopy(sub_topic_states,removeCounter+1,sub_topic_states,removeCounter,sub_topic_states.length-1-removeCounter);             
                    this.subTopicNames=Arrays.copyOf(subTopicNames, subTopicNames.length - 1);
                    this.subTopics=Arrays.copyOf(subTopics, subTopics.length - 1);
                    this.sub_topic_states=Arrays.copyOf(sub_topic_states, sub_topic_states.length - 1);
                    this.arrayLength=arrayLength - 1;
                }
                this.removeCounter=removeCounter + 1;
            }  
            // Dp Pub Topics
            this.removeCounter=0;
            while(removeCounter<pub_topic_states.length)
            {
                if(pub_topic_states[removeCounter]==false)
                {
                    this.pubTopicNames[removeCounter]=null;
                }
                this.removeCounter=removeCounter + 1;
            }
            this.arrayLength=pubTopicNames.length;
            this.removeCounter=0;
            while(removeCounter<arrayLength)
            {
                if(pubTopicNames[removeCounter]==null)
                {
                    System.arraycopy(pubTopicNames,removeCounter+1,pubTopicNames,removeCounter,pubTopicNames.length-1-removeCounter);
                    System.arraycopy(pubTopics,removeCounter+1,pubTopics,removeCounter,pubTopics.length-1-removeCounter);                                      
                    System.arraycopy(pub_topic_states,removeCounter+1,pub_topic_states,removeCounter,pub_topic_states.length-1-removeCounter);             
                    this.pubTopicNames=Arrays.copyOf(pubTopicNames, pubTopicNames.length - 1);
                    this.pubTopics=Arrays.copyOf(pubTopics, pubTopics.length - 1);
                    this.pub_topic_states=Arrays.copyOf(pub_topic_states, pub_topic_states.length - 1);
                    this.arrayLength=arrayLength - 1;
                }
                this.removeCounter=removeCounter + 1;
            }              
            // Do services now
            this.removeCounter=0;
            while(removeCounter<service_states.length)
            {
                if(service_states[removeCounter]==false)
                {
                    this.serviceNames[removeCounter]=null;
                }
                this.removeCounter=removeCounter + 1;
            }
            this.arrayLength=serviceNames.length;
            this.removeCounter=0;
            while(removeCounter<arrayLength)
            {
                if(serviceNames[removeCounter]==null)
                {
                    System.arraycopy(serviceNames,removeCounter+1,serviceNames,removeCounter,serviceNames.length-1-removeCounter);
                    System.arraycopy(services,removeCounter+1,services,removeCounter,services.length-1-removeCounter);                                      
                    System.arraycopy(service_states,removeCounter+1,service_states,removeCounter,service_states.length-1-removeCounter);             
                    this.serviceNames=Arrays.copyOf(serviceNames, serviceNames.length - 1);
                    this.services=Arrays.copyOf(services, services.length - 1);
                    this.service_states=Arrays.copyOf(service_states, service_states.length - 1);
                    this.arrayLength=arrayLength - 1;
                }
                this.removeCounter=removeCounter + 1;
            }              
        }
    }
    
    /** Return a list of all services currently registered to services. */
    public String[] getServices()
    {
//        for(int i = 0;i<service_states.length;i++)
//        {
//            System.out.println(service_states[i]);
//        }
        //this.serviceCount = 0;

// begin new code
        // If the arrays and the service count are equal return the serviceArray.
          if(serviceCount==services.length&&services.length==serviceArray.length)
          {
              return serviceArray;
          }
          else if(services.length==serviceArray.length)
          {
              // Count from 0 on the service Count and recreate array data when necessary but reuse array Objects.
              this.serviceCount = 0;
              this.count1=0;
              while(count1<services.length)
               {
                    this.serviceArray[count1] = services[count1].topicName;  
                    this.serviceCount = serviceCount + 1;
                    // loop counter
                    this.count1 = count1 + 1;
               }             
              return serviceArray;
          }
          else
          {
              // Resize the serviceArray, and regenerate the serviceCount
              this.serviceCount = 0;
              this.count1=0;
              while(count1<services.length)
               {
                    this.serviceArray=Arrays.copyOf(serviceArray, services.length);
                    this.serviceArray[count1] = services[count1].topicName;  
                    this.serviceCount = serviceCount + 1;
                    // loop counter
                    this.count1 = count1 + 1;
               }  
              return serviceArray;              
          }              

      
        
// end new code        
        
/*        while(count1<topics.length)
        {
            this.serviceElements = topics[count1].getTopicServices();
            System.out.println(count1 + ". Service elements:" + serviceElements.length);
            this.count2=0;
            while(count2<serviceElements.length)
            {
                this.isNew=true;
                this.currentService=(String)serviceElements[count2];
                // if currentService already exists in array skip this value and move to next.
                this.count3 = 0;
                while(count3<serviceCount)
                {
                    if(currentService.equals((String)serviceElements[count3]))
                    {
                        System.out.println("Current Service already exists: " + currentService);
                        this.isNew=false;
                        this.count3=serviceCount;
                    }
                    this.count3 = count3 + 1;
                }
                if(isNew&&serviceCount<topicArray.length)
                {
                    System.out.println("is new array element use existing array...");
                    // use existing topicArray.
                    topicArray[serviceCount]=currentService;
                    this.serviceCount = serviceCount + 1;        
                }
                else if(isNew&&serviceCount>=topicArray.length)
                {
                    System.out.println("is new array element increasing array size...");                    
                    // make array bigger (double in size)
                    this.topicArray=Arrays.copyOf(topicArray, topicArray.length*2);
                    topicArray[serviceCount]=currentService;
                    this.serviceCount = serviceCount + 1;                      
                }
                this.count2 = count2 + 1;
            }
            this.count1 = count1 + 1;
        }
        
        return this.topicArray=Arrays.copyOf(topicArray, serviceCount);
        */
    }
    
    /** Return a list of topics that is Publishers and Subscribers combined. */
    public String[] getTopics()
    {
        // Reset all counter variables.
        this.strCount=0;
        this.counter=0;
        this.i_count=0;
        this.j_count=0;

        // Assign default values
        this.result = new String[(this.subTopicNamesLength = subTopicNames.length) + (this.pubTopicNamesLength = pubTopicNames.length)];
        this.returnResult = new String[subTopicNamesLength+pubTopicNamesLength];
        // Set the String Count for running tally of non-duplicate Strings
        // Join the String arrays
        System.arraycopy(subTopicNames, 0, result, 0, subTopicNamesLength);
        System.arraycopy(pubTopicNames, 0, result, subTopicNamesLength, this.pubTopicNamesLength);
        // step 2 process the String arrays to remove duplicates.
        while(i_count<result.length)
        {
            // set the j_count to i_count + 1;
            this.j_count=i_count+1;
            while(j_count<result.length)
            {
                if(result[i_count].equals(result[j_count]))
                {
                    this.counter+=1;
                }       
                this.j_count=j_count + 1;
            }
            
            if(counter<1)
            {
                // Add one to the string count.
                this.returnResult[strCount]=result[i_count];
                this.strCount=strCount + 1;
            }
            // reset count back to 0 for next element.
            this.counter=0;
            // increment the while loop
            i_count=i_count+1; 
        } 
        this.result=null;
        
        return this.returnResult=Arrays.copyOf(returnResult,strCount);
    }          

    
    /** Return a list of all currently registered topic subscribers. */
    public String[] getPublishable()
    {
        return subTopicNames;
    }    
    
    /** Return a list of all currently registered topic publishers. */
    public String[] getSubscribable()
    {
        return pubTopicNames;
    }        
    
    /** get the system state subscriber topic indice by name. Returns -1 if topic not found. */
    public int getTopicSubscriberIndice(String topicName)
    {
        if(0<subTopicNames.length)
        {
            // reset the while loop counter
            this.currentIndice=0;
            while(currentIndice<subTopicNames.length)
            {
                //System.out.println("getCurrentIndice topicNames Array index: " + topicNames[currentIndice] + ", topicName: " + topicName);
                if(subTopicNames[currentIndice].equals(topicName))
                {
                   // System.out.println("getCurrentIndice return existing index: " + currentIndice);
                    return currentIndice;
                }
                this.currentIndice=currentIndice + 1;
            }
        }
        return -1;
    }
 
    /** get the system state publisher topic indice by name. Returns -1 if topic not found. */
    public int getPublisherTopicIndice(String topicName)
    {
        if(0<pubTopicNames.length)
        {
            // reset the while loop counter
            this.currentIndice=0;
            while(currentIndice<pubTopicNames.length)
            {
                //System.out.println("getCurrentIndice topicNames Array index: " + topicNames[currentIndice] + ", topicName: " + topicName);
                if(pubTopicNames[currentIndice].equals(topicName))
                {
                   // System.out.println("getCurrentIndice return existing index: " + currentIndice);
                    return currentIndice;
                }
                this.currentIndice=currentIndice + 1;
            }
        }
        return -1;
    }    
    
    /** get the system state service indice by name. Returns -1 if topic not found. */
    public int getServiceIndice(String serviceName)
    {
        if(0<serviceNames.length)
        {
            // reset the while loop counter
            this.currentIndice=0;
            while(currentIndice<serviceNames.length)
            {
                //System.out.println("getCurrentIndice topicNames Array index: " + topicNames[currentIndice] + ", topicName: " + topicName);
                if(serviceNames[currentIndice].equals(serviceName))
                {
                   // System.out.println("getCurrentIndice return existing index: " + currentIndice);
                    return currentIndice;
                }
                this.currentIndice=currentIndice + 1;
            }
        }
        return -1;
    }    
    
    /** get the system state subscriber topic name by indice. */
    public String getSubscriberTopicName(int topicIndice)
    {
        return subTopicNames[topicIndice];
    }
    
    /** get the system state publisher topic name by indice. */
    public String getPublisherTopicName(int topicIndice)
    {
        return pubTopicNames[topicIndice];
    }    
   
    /** get the system state service name by indice. */
    public String getServiceName(int serviceIndice)
    {
        return serviceNames[serviceIndice];
    }    
    
    /** Return Object[] of String publishers of the specified topic. */
    public Object[] getTopicPublishers(int topicIndice)
    {
        return pubTopics[topicIndice].getTopicPublishers();
    }  
    
    /** Return Object[] of String subscribers of the specified topic. */
    public Object[] getTopicSubscribers(int topicIndice)
    {
        return subTopics[topicIndice].getTopicSubscribers();
    }    
    
    /** Return Object[] of String services of the specified service. */
    public Object[] getTopicServices(int serviceIndice)
    {
        return services[serviceIndice].getTopicServices();
    }      
  
    /** Return publishers of the specified topic. */
    public void setTopicPublishers(int topicIndice, Object[] topicPublishers)
    {
        this.pub_topic_states[topicIndice]=true;
        pubTopics[topicIndice].setTopicPublishers(topicPublishers);
    }  
    
    /** Set publishers of the specified topic by topicName. If topic does not exist, topic will be added. */
    public void setTopicPublishers(String topicName, Object[] topicPublishers)
    {        
        if((this.topicPublisherID=getPublisherTopicIndice(topicName))!=-1)
        {
           pubTopics[topicPublisherID].setTopicPublishers(topicPublishers);
        }
        else
        {
            this.topicPublisherID= appendToPubTopics(topicName);
            pubTopics[this.topicPublisherID].setTopicPublishers(topicPublishers);
        }
        this.pub_topic_states[topicPublisherID]=true; 
    }     
    
    /** Return subscribers of the specified topic. */
    public void setTopicSubscribers(int topicIndice, Object[] topicSubscribers)
    {
        this.sub_topic_states[topicIndice]=true;
        subTopics[topicIndice].setTopicSubscribers(topicSubscribers);
    }    
    
    /** Set subscribers of the specified topic by topicName. If topic does not exist, topic will be added. */
    public void setTopicSubscribers(String topicName, Object[] topicSubscribers)
    {        
        if((this.topicSubscriberID=getTopicSubscriberIndice(topicName))!=-1)
        {
            subTopics[topicSubscriberID].setTopicSubscribers(topicSubscribers);
        }
        else
        {
            this.topicSubscriberID= appendToSubTopics(topicName);
            subTopics[topicSubscriberID].setTopicSubscribers(topicSubscribers);
        }
        this.sub_topic_states[topicSubscriberID]=true; 
    }      
    
    /** Set services of the specified topic. */
    public void setTopicServices(int serviceIndice, Object[] topicServices)
    {
        this.service_states[serviceIndice]=true;        
        services[serviceIndice].setTopicServices(topicServices);
    }    
    

    /** Set services of the specified topic by topicName. If topic does not exist, topic will be added. */
    public void setTopicServices(String serviceName, Object[] serviceProviders)
    {        
        if((this.topicServiceID=getServiceIndice(serviceName))!=-1)
        {
//            System.out.println("Topic Service 1: "+Arrays.toString(serviceProviders));
            services[topicServiceID].setTopicServices(serviceProviders);
        }
        else
        {
//            System.out.println("Topic Service 2: "+Arrays.toString(serviceProviders));            
            this.topicServiceID= appendToServices(serviceName);
//            System.out.println("Topic Service 2 index: " + topicServiceID);
            services[topicServiceID].setTopicServices(serviceProviders);
        }
        this.service_states[topicServiceID]=true; 
    }     

    /** Append an element to this classes arrays, for a new topic. */
    private int appendToSubTopics(String subTopicName)
    {
        this.appendSubIndice=subTopics.length;
        this.subTopics=Arrays.copyOf(subTopics, subTopics.length + 1);
        this.subTopicNames=Arrays.copyOf(subTopicNames, subTopicNames.length + 1);
        this.sub_topic_states=Arrays.copyOf(sub_topic_states, sub_topic_states.length + 1);
        this.subTopics[appendSubIndice]=new ROSTopicState(appendSubIndice, subTopicName);
        this.subTopicNames[appendSubIndice]=subTopicName;
        this.sub_topic_states[appendSubIndice]=true;        
        return appendSubIndice;
    }
    
    /** Append an element to this classes arrays, for a new topic. */
    private int appendToPubTopics(String pubTopicName)
    {
        this.appendPubIndice=pubTopics.length;
        this.pubTopics=Arrays.copyOf(pubTopics, pubTopics.length + 1);
        this.pubTopicNames=Arrays.copyOf(pubTopicNames, pubTopicNames.length + 1);
        this.pub_topic_states=Arrays.copyOf(pub_topic_states, pub_topic_states.length + 1);
        this.pubTopics[appendPubIndice]=new ROSTopicState(appendPubIndice, pubTopicName);
        this.pubTopicNames[appendPubIndice]=pubTopicName;
        this.pub_topic_states[appendPubIndice]=true;        
        return appendPubIndice;
    }    

    /** Append an element to this classes arrays, for a new service. */
    private int appendToServices(String serviceName)
    {
        this.appendServiceIndice=services.length;
        this.services=Arrays.copyOf(services, services.length + 1);
        this.serviceNames=Arrays.copyOf(serviceNames, serviceNames.length + 1);
        this.service_states=Arrays.copyOf(service_states, service_states.length + 1);
        this.services[appendServiceIndice]=new ROSTopicState(appendServiceIndice, serviceName);
        this.serviceNames[appendServiceIndice]=serviceName;
        this.service_states[appendServiceIndice]=true;        
        return appendServiceIndice;
    }    
    
    
    
    /** Internal class ROSTopic State represents a single topic state. */
    class ROSTopicState 
    {
        public transient int topicIndice=-1;
        public String topicName;
        public Object[] publishers;
        public Object[] subscribers;
        public Object[] services;
        private final String[] resetArray = new String[0];
        public ROSTopicState(int topicIndice, String topicName)
        {
            this.topicIndice=topicIndice;
            this.topicName=topicName;
            this.publishers = new String[0];
            this.subscribers = new String[0];
            this.services = new String[0];
        }
        
// TODO: Fix reset method        
        /** Reset the Topic Mappings. */
        public void reset()
        {
            this.publishers=resetArray;
            this.subscribers=resetArray;
            this.services=resetArray;
        }

        public void setTopicPublishers(Object[] publishers)
        {
            this.publishers=publishers;
        }

        public void setTopicSubscribers(Object[] subscribers)
        {
            this.subscribers=subscribers;
        }    

        public void setTopicServices(Object[] services)
        {
            this.services=services;
        }          

        public Object[] getTopicPublishers()
        {
            return publishers;
        }

        public Object[] getTopicSubscribers()
        {
            return subscribers;
        }    

        public Object[] getTopicServices()
        {
            return services;
        }       
    }
    
    public void shutdown()
    {
        this.subTopicNames=null;
        this.subTopics=null;
        this.sub_topic_states=null;    
        this.pubTopicNames=null;
        this.pubTopics=null;
        this.pub_topic_states=null; 
        this.serviceNames=null;
        this.services=null;
        this.service_states=null;         
    }
}
