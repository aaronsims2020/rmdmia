package org.happy.artist.rmdmia.rcsm.providers.ros.client.message;

import org.happy.artist.rmdmia.utilities.HexStringConverter; 
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.happy.artist.rmdmia.rcsm.provider.message.SubscriberMessageManager;
import org.happy.artist.rmdmia.rcsm.provider.message.TopicRegistryMessageDefinition;
import org.happy.artist.rmdmia.rcsm.providers.ros.ROSNode;

// TODO: Minor performance enhancement (post 1.0 release) - add write to file method in this class so as to avoid writing the topic_registry.eax file every time multiple adds are called. Will require updating all of add & remove methods. 
/**
 * org.happy.artist.rmdmia.rcsm.client.message.ROSSubscriberMessageManager class 
 * manages initialization of messages at startup, storing message definitions 
 * during runtime, and storing updated message data not available at startup 
 * like message connection header message definition. 
 * 
 * topic_registry.eax key values are as follows:
 *  #.0=<topic>
 *  #.1=<md5sum>
 *  #.2=<type>
 *  #.3=<tcp_nodelay>
 *  #.4=<definition>
 *  #.5=<service>
 *  #.6=<connect_on_start>
 *  #.7=<always_connected>
 *  #.8=<persistant>
 *  #.9=<latching>
 *  #.10=<callerid>
 *  #.11=<redirect_class_name>
 *  #.12=<preferred_protocol>
 *  #.13=<tcp_block_size>
 *  #.14=<udp_packet_size>
 *  #.15=<redirect_initializer_class_name>
 *  #.16=<redirect_tuner_class_name>
 *  #.17=<request_type>
 *  #.18=<response_type>
 *  #.19=<request_type_definition>
 *  #.20=<response_type_definition>
 *  #.21=<publisher_redirect_class_name>
 *  #.22=<publisher_redirect_initializer_class_name>
 *  #.23=<publisher_redirect_tuner_class_name> 
 *  #.24=<publisher_connect_on_start>
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2013-2015 Happy Artist. All rights reserved.
 * 
 */
public class ROSSubscriberMessageManager implements SubscriberMessageManager
{
    // Topic registry stores pre-configured Topic information for current robot configuration.
    private final static TopicRegistry registry = TopicRegistry.getInstance();
    // keys array used by constructor to assign the topic registry keys for class initialization of pre-configured topic variables.
    // Note: Did not assign as final due to the expectation that this will eventually be a live system, and if the properties file is read while the system is live it will never be allowed to reload or update the array with updated subscriber message definitions via the Properties file.
    private Object[] keys;    
    // splitting properties keys at startup use this String[]
    private String[] keySplitArray;
    // splitting properties values at startup use this String[]
    private String[] valueSplitArray;    
    // while loop is always fewer CPU operations then a for loop. Also, pre-initializing the counter for loop variables is an efficiency increase.
    private int keyCount;
    
    // declare the Topic IDs.
    private int[] topicIDs;
    // declare the topic id message definition variables.
    private int[] tidVariables;    
    // int subscriber message definition count
    private int messageDefCount=0;
    // Topic registry message definition array for the message manager definition lookups.
    private ROSTopicRegistryMessageDefinition[] messageDefinitions = new ROSTopicRegistryMessageDefinition[0];
    // temp var declaration 
    private int tid;
    // temp var declaration     
    private int tidVar; 
    // temp var declaration 
    private ROSTopicRegistryMessageDefinition trmd;
    // a list of message definitions that meet the minimum requirements to establish a connection to the associated topic.
    private SubscriberMessage[] subscriberMessages;
    // a list of message definitions that meet the minimum requirements to establish a connection to the associated topic.
    private PublisherMessage[] publisherMessages;    
    // a list of message definitions that meet the minimum requirements to establish a connection to the associated service.
    private ServiceMessage[] serviceMessages;
    // global caller_id - used as a default if callerid is not specified on topic properties.
    private String caller_id;
    
    
    // Add method variables.
    private String definitionID;
    private final static String PERIOD = ".";
    private final static String ZERO = "0";    
    private final static String ONE = "1";    
    private final static String TWO = "2";    
    private final static String THREE = "3";    
    private final static String FOUR = "4";    
    private final static String FIVE = "5";    
    private final static String SIX = "6";    
    private final static String SEVEN = "7";    
    private final static String EIGHT = "8";    
    private final static String NINE = "9";    
    private final static String TEN = "10";  
    private final static String ELEVEN = "11";    
    private final static String EMPTY_STRING = "";     
    private boolean write_file_success=false;
    // Remove method variables
    private int removeElementIndex;
    private int count;
    private ROSTopicRegistryMessageDefinition[] replacementArray;    
    // variables for method getTIDByTopicName
    private int currentTID; 
    // Singleton variable.
    private static ROSSubscriberMessageManager smm;
    // HexStringConverter
    HexStringConverter hexConverter=HexStringConverter.getHexStringConverterInstance();
    // Reference to ROSNode used to replace the calls to getTIDbyTopic that are based on the .eax file, and not the order in ROSNode that can differ from the ROSSubscriberMessageManager conifiguration. 
    public ROSNode rosNode;
    
    /** If Object is already instantiated the passed in caller ID will be ignored, and the instantiated caller_id will be used. Limitation, this limits one ROS per VM, due to it being a Singleton, this may be reevaluated. */
    public static ROSSubscriberMessageManager getInstance(String caller_id, ROSNode node) throws UnsupportedEncodingException
    {
        if(smm!=null)
        {
            return smm;
        }
        else
        {
            return smm=new ROSSubscriberMessageManager(caller_id, node);
        }
    }
    /** ROSSubscriberMessageManager constructor, a ROSNode node can be a null value. */
    private ROSSubscriberMessageManager(String caller_id, ROSNode node) throws UnsupportedEncodingException
    {
        this.rosNode=node;
        this.caller_id=caller_id;
        // updateSubscriberMessages will load the properties data, and topic definitions.
        updateSubscriberMessages();
    }    
    
    /** return the ROSNode calling the ROSSubscriberMessageManager. */
    public ROSNode getROSNode()
    {
        return rosNode;
    }
        
    // Return topic registry message definitions.
    public ROSTopicRegistryMessageDefinition[] getTopicDefinitions()
    {
        return messageDefinitions;
    }
    
    // Unit Test
    public static void main(String[] args)
    {
        ROSSubscriberMessageManager smm=null;
        try {
            smm = new ROSSubscriberMessageManager("/rmdmia", null);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ROSSubscriberMessageManager.class.getName()).log(Level.SEVERE, null, ex);
        }
//        ROSTopicRegistryMessageDefinition[] defs = smm.getTopicDefinitions();
        System.out.println("Available Subscriber Topics: " + smm.getAvailableSubscriptionMessages().length);  
        System.out.println("Available Publisher Topics: " + smm.getAvailablePublisherMessages().length);          
        System.out.println("Available Services: " + smm.getAvailableServiceMessages().length);        ServiceMessage[] serviceMessages = smm.getAvailableServiceMessages();
        SubscriberMessage[] subscriberMessages = smm.getAvailableSubscriptionMessages(); 
        PublisherMessage[] publisherMessages = smm.getAvailablePublisherMessages();         
        System.out.println("Subscriber Messages:");
        for(int i = 0;i<subscriberMessages.length;i++)
        {
            System.out.println(subscriberMessages[i].getMessage());            
        }
//        System.out.println("Publisher Messages:");
//        for(int i = 0;i<publisherMessages.length;i++)
//        {
//            System.out.println(publisherMessages[i].getMessage());            
//        }

        System.out.println("Service Messages:");
        for(int i = 0;i<serviceMessages.length;i++)
        {
            System.out.println(serviceMessages[i].getMessage());            
        }        
    }
    
    // Call this method when the registered topics are removed, added, or updated to maintain the data model.
    private void updateSubscriberMessages() throws UnsupportedEncodingException
    {
       this.keys = ROSSubscriberMessageManager.registry.getProperties().keySet().toArray();
        // initialize the Topic IDs.
        this.topicIDs = new int[keys.length];
        // initialize the topic id message definition variables.
        this.tidVariables = new int[keys.length]; 
        // set keyCount to 0.
        this.keyCount=0;
        
        while(keyCount<keys.length)
        {
            //System.out.println("key:" + keys[keyCount] + ", value:" + );
            this.keySplitArray=((String)keys[keyCount]).split("\\.");
            if(keySplitArray.length==2)
            {
                topicIDs[keyCount]=new Integer(keySplitArray[0]).intValue();
                tidVariables[keyCount]=new Integer(keySplitArray[1]).intValue();
                // obtain a count of the the message definitions, for a later initialization of the message definition array for lookups.
                if(messageDefCount<topicIDs[keyCount])
                {
                    this.messageDefCount=topicIDs[keyCount];
                }
            }            
            // Remember to increment the count. Always use  i = i + 1 for primitive thread safety. ++ is not thread safe.
            this.keyCount = keyCount + 1;
        }
        // reset keyCount back to 0
        this.keyCount=0;       
        //initialize the topic registry message definition array.
        if(messageDefCount>0)
        {
            this.messageDefinitions = new ROSTopicRegistryMessageDefinition[messageDefCount+1];           
        }
        else
        {
 // TODO: Fix the save of this...
            // Trying with 0 instead of 1.
            this.messageDefinitions = new ROSTopicRegistryMessageDefinition[1];  
            //            this.messageDefinitions = new ROSTopicRegistryMessageDefinition[1];                       
        }
        while(keyCount<messageDefinitions.length)
        {  
            messageDefinitions[keyCount]=new ROSTopicRegistryMessageDefinition();
            // iterate the loop
            this.keyCount = keyCount + 1;
        }        
        
// loop through the keys length and populate the message definitions array.
        // reset keyCount back to 0

        this.keyCount=0; 
        while(keyCount<keys.length)
        {
            this.tid=topicIDs[keyCount];
            this.tidVar=tidVariables[keyCount];
            this.trmd = messageDefinitions[tid];                

            trmd.tid=tid;
// all definable if statements for key types are defined here.            
            // get the key value            
            if(tidVar==0)
            {
                trmd.topic=ROSSubscriberMessageManager.registry.get(((String)keys[keyCount]));    
            }
            else if(tidVar==1)
            {
                trmd.md5sum=ROSSubscriberMessageManager.registry.get(((String)keys[keyCount]));    
            }
            else if(tidVar==2)
            {
                trmd.type=ROSSubscriberMessageManager.registry.get(((String)keys[keyCount]));
            }            
            else if(tidVar==3)
            {
                trmd.tcp_nodelay=ROSSubscriberMessageManager.registry.get(((String)keys[keyCount]));    
            }            
            else if(tidVar==4)
            {
                // message_definition are stored in Hex String format in Properties file.     
                trmd.definition=hexConverter.hexToString(ROSSubscriberMessageManager.registry.get(((String)keys[keyCount])));
            } 
            else if(tidVar==5)
            {
                trmd.service=ROSSubscriberMessageManager.registry.get(((String)keys[keyCount]));    
            }            
            else if(tidVar==6)
            {
                trmd.connect_on_start=ROSSubscriberMessageManager.registry.get(((String)keys[keyCount]));    
            }
            else if(tidVar==7)
            {
                trmd.always_connected=ROSSubscriberMessageManager.registry.get(((String)keys[keyCount]));    
            }            
            else if(tidVar==8)
            {
                trmd.persistant=ROSSubscriberMessageManager.registry.get(((String)keys[keyCount]));
            }            
            else if(tidVar==9)
            {
                trmd.latching=ROSSubscriberMessageManager.registry.get(((String)keys[keyCount]));
            } 
            else if(tidVar==10)
            {
                trmd.callerid=ROSSubscriberMessageManager.registry.get(((String)keys[keyCount]));            
            }
            else if(tidVar==11)
            {
                trmd.redirect_class=ROSSubscriberMessageManager.registry.get(((String)keys[keyCount]));            
            }  
            else if(tidVar==12)
            {
                trmd.preferred_protocol=ROSSubscriberMessageManager.registry.get(((String)keys[keyCount]));            
            }
            else if(tidVar==13)
            {
                trmd.tcp_block_size=ROSSubscriberMessageManager.registry.get(((String)keys[keyCount]));            
            }
            else if(tidVar==14)
            {
                trmd.udp_packet_size=ROSSubscriberMessageManager.registry.get(((String)keys[keyCount]));            
            }              
            else if(tidVar==15)
            {
                trmd.redirect_class_initializer=ROSSubscriberMessageManager.registry.get(((String)keys[keyCount]));            
            } 
            else if(tidVar==16)
            {
                trmd.redirect_class_tuner=ROSSubscriberMessageManager.registry.get(((String)keys[keyCount]));            
            } 
            else if(tidVar==17)
            {
                trmd.request_type=ROSSubscriberMessageManager.registry.get(((String)keys[keyCount]));            
            }                
            else if(tidVar==18)
            {
                trmd.response_type=ROSSubscriberMessageManager.registry.get(((String)keys[keyCount]));            
            }       
            else if(tidVar==19)
            {
                trmd.request_type_definition=ROSSubscriberMessageManager.registry.get(((String)keys[keyCount]));            
            }                
            else if(tidVar==20)
            {
                trmd.response_type_definition=ROSSubscriberMessageManager.registry.get(((String)keys[keyCount]));            
            }
            
            
            else if(tidVar==21)
            {
                trmd.publisher_redirect_class=ROSSubscriberMessageManager.registry.get(((String)keys[keyCount]));            
            }                
            else if(tidVar==22)
            {
                trmd.publisher_redirect_class_initializer=ROSSubscriberMessageManager.registry.get(((String)keys[keyCount]));            
            }       
            else if(tidVar==23)
            {
                trmd.publisher_redirect_class_tuner=ROSSubscriberMessageManager.registry.get(((String)keys[keyCount]));            
            }                
            else if(tidVar==24)
            {
                trmd.publisher_connect_on_start=ROSSubscriberMessageManager.registry.get(((String)keys[keyCount]));            
            } 
     
            // iterate the loop
            this.keyCount = keyCount + 1;
        }
        // Updates the Available Subscription & Service Message index.
        updateAvailableMessages();
    }
    
    // updateAvailableMessage() method variables.
    private int updateCount;
    private int serviceCount;
    private int subscriberCount;
    private String tmpCallerID;
    private String tmpTopic;
    private String tmpService;    
    private String tmpMD5Sum;
    private String tmpType;
    private String tmpTCP_Nodelay;
    private String tmpPersistent;
    private String tmpLatching;
    private String tmpMessageDefinition;    
    private boolean boolPersistent;
    private SubscriberMessage tmpSubscriberMessage;
    private ServiceMessage tmpServiceMessage; 
    private PublisherMessage tmpPublisherMessage;     
    // TODO: Implement preferred protocol, persistant, and latching
    // Update the available subscriber/publisher topic & service message data.
    private void updateAvailableMessages() throws UnsupportedEncodingException
    {
        this.updateCount=0;
        if(subscriberMessages==null)
        {
            this.subscriberMessages=new SubscriberMessage[messageDefinitions.length];
        }
        if(publisherMessages==null)
        {
            this.publisherMessages=new PublisherMessage[messageDefinitions.length];
        }        
        if(serviceMessages==null)
        {
            this.serviceMessages=new ServiceMessage[messageDefinitions.length];            
        }        
        // set the serviceCount & subscriberCount to 0 for while loop processing.
        this.serviceCount=0;
        this.subscriberCount=0;    
        while(updateCount<messageDefinitions.length)
        {
            // service & subscriber/publisher topic counts are used following the loop to trim the associated arrays of null elements.
            // assign topic & service
            this.tmpTopic=messageDefinitions[updateCount].topic;        
            this.tmpService=messageDefinitions[updateCount].service;

            if(tmpTopic.isEmpty()==false)
            {
                if(messageDefinitions[updateCount].callerid.isEmpty())
                {
                    // assign global caller_id
                    this.tmpCallerID=caller_id;
                }
                else
                {
                    // use topic callerid
                    this.tmpCallerID=messageDefinitions[updateCount].callerid;
                }   

                this.tmpMD5Sum=messageDefinitions[updateCount].md5sum;
                this.tmpType=messageDefinitions[updateCount].type;
                this.tmpTCP_Nodelay=messageDefinitions[updateCount].tcp_nodelay;
                this.tmpLatching=messageDefinitions[updateCount].latching;
                this.tmpMessageDefinition=messageDefinitions[updateCount].definition;
               // System.out.println("messageDefinitions current index: " + updateCount + ", topicType: " + tmpType);
                
                if(tmpMD5Sum.isEmpty()==false&&tmpType.isEmpty()==false)
                {
                    // add message to array
                    if(tmpTCP_Nodelay.equals("1"))
                    {
                       // tcp_nodelay=true
                       this.tmpSubscriberMessage=new SubscriberMessage(tmpCallerID, tmpMD5Sum,true, tmpTopic, tmpType);
                        if(tmpLatching.equals("1"))
                        {
                           this.tmpPublisherMessage=new PublisherMessage(tmpCallerID, tmpMD5Sum, tmpType, true);

                        }
                        else
                        {
                           this.tmpPublisherMessage=new PublisherMessage(tmpCallerID, tmpMD5Sum, tmpType, false);                            
                        }
                    
                    }
                    else
                    {
                        // tcp_nodelay=false
                       this.tmpSubscriberMessage=new SubscriberMessage(tmpCallerID, tmpMD5Sum,false, tmpTopic, tmpType);      
                        if(tmpLatching.equals("1"))
                        {
                           this.tmpPublisherMessage=new PublisherMessage(tmpCallerID, tmpMD5Sum, tmpType, true);

                        }
                        else
                        {
                           this.tmpPublisherMessage=new PublisherMessage(tmpCallerID, tmpMD5Sum, tmpType, false);                            
                        }                       
                    }
 //                   System.out.println("subsciberMessages,length: " + subscriberMessages.length + ", Subscriber Count: " + subscriberCount);
                    // If subscriberMessages does not have enough elements add one.
                    if(subscriberMessages.length<=subscriberCount)
                    {
 // TODO: This is a performance killer, and was added as a crutch, but needs to be updated, if it is used during runtime.
        this.subscriberMessages=Arrays.copyOfRange(subscriberMessages, 0, subscriberMessages.length + 1);                 
        this.publisherMessages=Arrays.copyOfRange(publisherMessages, 0, subscriberMessages.length + 1);          
                    }
                    
                    subscriberMessages[subscriberCount]=tmpSubscriberMessage;
                    publisherMessages[subscriberCount]=tmpPublisherMessage;                   
                    // increment the array to the next element
                    this.subscriberCount=subscriberCount + 1;                    
                }
                
            }
            else if(tmpService.isEmpty()==false)
            {
                if(messageDefinitions[updateCount].callerid.isEmpty())
                {
                    // assign global caller_id
                    this.tmpCallerID=caller_id;                    
                }
                else
                {
                    // use topic callerid
                    this.tmpCallerID=messageDefinitions[updateCount].callerid;
                }
                this.tmpMD5Sum=messageDefinitions[updateCount].md5sum;
                this.tmpType=messageDefinitions[updateCount].type;
                this.tmpTCP_Nodelay=messageDefinitions[updateCount].tcp_nodelay;
                // Service Persistent property.
                this.tmpPersistent=messageDefinitions[updateCount].persistant;
                if(tmpPersistent.equals("1"))
                {
                    this.boolPersistent=true;
                }
                else
                {
                    this.boolPersistent=false;
                }
// TODO: Evaluate if commenting out this statement breaks unconfigured services.          
// Commenting out the following if statement since Services allow Probe to obtain the md5sum, and types.
//                if(tmpMD5Sum.isEmpty()==false&&tmpType.isEmpty()==false)
//                {
                    // add message to array
                    if(tmpTCP_Nodelay.equals(ONE))
                    {
                       // tcp_nodelay=true
                       this.tmpServiceMessage=new ServiceMessage(tmpCallerID, tmpMD5Sum,true, tmpService, tmpType, boolPersistent);
                    }
                    else
                    {
                        // tcp_nodelay=false
                       this.tmpServiceMessage=new ServiceMessage(tmpCallerID, tmpMD5Sum,false, tmpService, tmpType, boolPersistent);                        
                    }
                    // If serviceMessages does not have enough elements add one.
                    if(serviceMessages.length<=serviceCount)
                    {
 // TODO: This is a performance killer, and was added as a crutch, but needs to be updated, if it is used during runtime.
        this.serviceMessages=Arrays.copyOfRange(serviceMessages, 0, serviceMessages.length + 1);                         
                    }
                    serviceMessages[serviceCount]=tmpServiceMessage;
                    // increment the array to the next element
                    this.serviceCount=serviceCount + 1;                    
  //              }
            }
            // increment loop counter
            this.updateCount = updateCount + 1;
        }        
        // trim the extra elements in serviceMessage & subscriberMessage arrays.
        this.subscriberMessages=Arrays.copyOfRange(subscriberMessages, 0, subscriberCount);
        this.publisherMessages=Arrays.copyOfRange(publisherMessages, 0, subscriberCount);
        this.serviceMessages=Arrays.copyOfRange(serviceMessages, 0, serviceCount);  
             
    }
    
    /** Return an array of subscribable topics. */
    public SubscriberMessage[] getAvailableSubscriptionMessages()
    {
        return subscriberMessages;
    }
    
    /** Return an array of publishable topics. */
    public PublisherMessage[] getAvailablePublisherMessages()
    {
        return publisherMessages;
    }    

    /** Return an array of subscribable services. */
    public ServiceMessage[] getAvailableServiceMessages()
    {
        return serviceMessages;
    }  
    
    private ROSTopicRegistryMessageDefinition tmpDefinition;
    private boolean definitionExists=false;
    /** Add/update a topic registry message definition (thread-safe). Returns boolean true on success. A return of false means the topic_registry.eax file could not be written,  and changes will only apply in the running instance. */
    public synchronized boolean add(TopicRegistryMessageDefinition definition) throws UnsupportedEncodingException
    {
        // If message definition exists write data to that index number, otherwise add new element.
        this.definitionExists=false;
        if (messageDefinitions == null)
        {
             if (definition!= null)
             {
                  this.messageDefinitions = new ROSTopicRegistryMessageDefinition[1];
                  messageDefinitions[0]=((ROSTopicRegistryMessageDefinition)definition);
             }
        }
        else
        {
             if (definition!= null)
             {
                this.count=0;    
                while(count<messageDefinitions.length)
                {        
                    if(messageDefinitions[count].topic.equals(((ROSTopicRegistryMessageDefinition)definition).topic)&&messageDefinitions[count].service.equals(((ROSTopicRegistryMessageDefinition)definition).service))
                    {
                        definition.tid=count;
                        this.messageDefinitions[count]=((ROSTopicRegistryMessageDefinition)definition);
                        this.definitionExists=true;
                    }
                    this.count=count + 1;
                }                
                if(this.definitionExists==false)
                {
                  this.replacementArray = new ROSTopicRegistryMessageDefinition[messageDefinitions.length+1];
                  System.arraycopy(messageDefinitions,0,replacementArray,0,messageDefinitions.length);
                  replacementArray[replacementArray.length-1] = ((ROSTopicRegistryMessageDefinition)definition);
                  this.messageDefinitions=replacementArray;
                  this.replacementArray=null;
                }
             }
        }          
        // Set the key/value pair definition in the properties file.
        this.count=0;    
        while(count<messageDefinitions.length)
        {
            this.definitionID=String.valueOf(count);
            this.tmpDefinition=messageDefinitions[count]; 
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("0"),tmpDefinition.topic);  
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("1"),tmpDefinition.md5sum); 
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("2"),tmpDefinition.type); 
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("3"),tmpDefinition.tcp_nodelay); 
            // message_definition are stored in Hex String format in Properties file.
 // TODO: This if statement is a workaround for the unknown reason a definition keeps getting copied to the services from topics.
            if(tmpDefinition.service!=null&&!tmpDefinition.service.trim().equals(EMPTY_STRING))
            {
                ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("4"),EMPTY_STRING); 
            }
            else
            {
                if(tmpDefinition.definition!=null)
                {
                    ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("4"),hexConverter.stringToHex(tmpDefinition.definition));
                }
                else
                {
                    ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("4"),EMPTY_STRING);                 
                }
            }
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("5"),tmpDefinition.service); 
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("6"),tmpDefinition.connect_on_start); 
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("7"),tmpDefinition.always_connected); 
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("8"),tmpDefinition.persistant); 
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("9"),tmpDefinition.latching);     
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("10"),tmpDefinition.callerid);    
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("11"),tmpDefinition.redirect_class); 
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("12"),tmpDefinition.preferred_protocol);
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("13"),tmpDefinition.tcp_block_size); 
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("14"),tmpDefinition.udp_packet_size);   
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("15"),tmpDefinition.redirect_class_initializer);  
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("16"),tmpDefinition.redirect_class_tuner);       
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("17"),tmpDefinition.request_type);              
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("18"),tmpDefinition.response_type);
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("19"),tmpDefinition.request_type_definition); 
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("20"),tmpDefinition.response_type_definition);   
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("21"),tmpDefinition.publisher_redirect_class);  
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("22"),tmpDefinition.publisher_redirect_class_initializer);       
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("23"),tmpDefinition.publisher_redirect_class_tuner);              
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("24"),tmpDefinition.publisher_connect_on_start);            
            this.count=count + 1;         
        }         
        // Write the updated properties to file
        this.write_file_success=ROSSubscriberMessageManager.registry.writeFile();
        // Updates the Available Subscription & Service Message index.
        if(write_file_success==false)
        {
            updateSubscriberMessages();
        }
        else
        {
            //updateAvailableMessages();
            updateSubscriberMessages();            
        }
        return write_file_success;
    }
    
    // TODO: Test the remove & add methods ASAP (will wait to use the GUI front end to configure.
    /** Remove a topic registry message definition (thread-safe). Returns boolean true on success. A return of false means the topic_registry.eax file could not be written,  and changes will only apply in the running instance. */
    public synchronized boolean remove(TopicRegistryMessageDefinition definition) throws UnsupportedEncodingException
    {
// TODO: Implement remove method
        this.count=0;
        while(count<messageDefinitions.length)
        {
            if(definition.topic==messageDefinitions[count].topic&&((ROSTopicRegistryMessageDefinition)definition).service==messageDefinitions[count].service)
            {
                this.removeElementIndex=count;
                this.count=messageDefinitions.length;
            }
            this.count=count + 1;
        }
        // TODO: remove element index from messageDefinitions.
        replacementArray=new ROSTopicRegistryMessageDefinition[messageDefinitions.length - 1];
        System.arraycopy(messageDefinitions, 0, replacementArray, 0, removeElementIndex);
        if (messageDefinitions.length != removeElementIndex) 
        {
            System.arraycopy(messageDefinitions, removeElementIndex + 1,replacementArray, removeElementIndex, messageDefinitions.length - removeElementIndex - 1);
        }                
        this.messageDefinitions=replacementArray;
        this.replacementArray=null;               
        this.count=0;    
        while(count<messageDefinitions.length)
        {
            this.definitionID=String.valueOf(count);
            definition=messageDefinitions[count];
            // Set the key/value pair definition in the properties file.                
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("0"),definition.topic);  
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("1"),((ROSTopicRegistryMessageDefinition)definition).md5sum); 
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("2"),definition.type); 
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("3"),((ROSTopicRegistryMessageDefinition)definition).tcp_nodelay); 
            // message_definition are stored in Hex String format in Properties file.
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("4"),hexConverter.stringToHex(tmpDefinition.definition));  
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("5"),((ROSTopicRegistryMessageDefinition)definition).service); 
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("6"),definition.connect_on_start); 
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("7"),((ROSTopicRegistryMessageDefinition)definition).always_connected); 
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("8"),((ROSTopicRegistryMessageDefinition)definition).persistant); 
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("9"),((ROSTopicRegistryMessageDefinition)definition).latching);     
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("10"),((ROSTopicRegistryMessageDefinition)definition).callerid);    
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("11"),((ROSTopicRegistryMessageDefinition)definition).redirect_class); 
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("12"),((ROSTopicRegistryMessageDefinition)definition).preferred_protocol); 
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("13"),tmpDefinition.tcp_block_size); 
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("14"),tmpDefinition.udp_packet_size);     
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("15"),tmpDefinition.redirect_class_initializer); 
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("16"),tmpDefinition.redirect_class_tuner); 
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("17"),tmpDefinition.request_type);              
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("18"),tmpDefinition.response_type);
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("19"),tmpDefinition.request_type_definition); 
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("20"),tmpDefinition.response_type_definition);   
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("21"),tmpDefinition.publisher_redirect_class);  
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("22"),tmpDefinition.publisher_redirect_class_initializer);       
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("23"),tmpDefinition.publisher_redirect_class_tuner);              
            ROSSubscriberMessageManager.registry.put(definitionID.concat(PERIOD).concat("24"),tmpDefinition.publisher_connect_on_start);             
            this.count=count + 1;
        }        
        // Write the updated properties to file
        this.write_file_success=ROSSubscriberMessageManager.registry.writeFile();
        // Updates the Available Subscription & Service Message index.
        if(write_file_success==false)
        {
            System.out.println("Write file failure.");
            updateSubscriberMessages();
        }
        else
        {
            System.out.println("Write file success.");
            //updateSubscriberMessages();
            updateAvailableMessages();
        }        
        return write_file_success;        
    }    
    
    /** Returns the tid associated with the Topic name, or Service name. Returns -1 if no tid is associated with the specified topic or service name. */
    public synchronized int getTIDByTopicName(String topicName)
    {
        this.currentTID=0;
        while(currentTID<messageDefinitions.length)
        {
            if(messageDefinitions[currentTID].topic.equals(topicName))
            {
                if(messageDefinitions[currentTID].tid!=currentTID)
                {
                    this.messageDefinitions[currentTID].tid=currentTID;
                }
                return messageDefinitions[currentTID].tid;
            }
            this.currentTID=currentTID + 1;
        }
        return -1;
    }    
    
    /** Returns the tid associated with the Topic name, or Service name. Returns -1 if no tid is associated with the specified topic or service name. */
    public synchronized int getTIDByServiceName(String serviceName)
    {
        this.currentTID=0;
        while(currentTID<messageDefinitions.length)
        {
            if(messageDefinitions[currentTID].service.equals(serviceName))
            {
                if(messageDefinitions[currentTID].tid!=currentTID)
                {
                    this.messageDefinitions[currentTID].tid=currentTID;
                }
                return messageDefinitions[currentTID].tid;
            }
            this.currentTID=currentTID + 1;
        }
        return -1;
    }
    
    /** Returns the index associated with the Service name in the ServiceMessages array. Returns -1 if no service name is associated with the service name input parameter. */
    public synchronized int getIndexByServiceName(String serviceName)
    {
        this.currentTID=0;
 //       System.out.println("Service messages.length: " + serviceMessages.length);
        while(currentTID<serviceMessages.length)
        {
            if(serviceMessages[currentTID].service.equals(serviceName))
            {
                return currentTID;
            }
            this.currentTID=currentTID + 1;
        }
// TODO: Reanalyze the need for this next logic in the future. It will add a new element to the array if the service was not in the list due to lack of md5sum, or service type. This is added to support the ability to probe the service, which is not allowed without an index. This potentially could break other functionality if added later due to behavior.       
        // 
        return -1;
    }    
    
    /** Returns the index associated with the topic name in the SubscriberMessages array. Returns -1 if no topic name is associated with the topic name input parameter. */
    public synchronized int getIndexByTopicName(String topicName)
    {
        this.currentTID=0;
        while(currentTID<subscriberMessages.length)
        {
            if(subscriberMessages[currentTID].topic.equals(topicName))
            {
                return currentTID;
            }
            this.currentTID=currentTID + 1;
        }
        return -1;
    }       
    /** Returns the ROSTopicRegistryMessageDefinition Object associated with the tid. */    
    public ROSTopicRegistryMessageDefinition getTopicRegistryMessageDefinitionByTID(int tid)
    {
        return this.messageDefinitions[tid];
    }

}
