package org.happy.artist.rmdmia.rcsm.providers.ros;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.happy.artist.rmdmia.instruction.InstructionDefinition;
import org.happy.artist.rmdmia.rcsm.RCSMException;
import org.happy.artist.rmdmia.rcsm.RCSMProvider;
import org.happy.artist.rmdmia.rcsm.provider.CommunicationReceiverInterface;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.ServiceMessage;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.SubscriberMessage;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.ROSTopicRegistryMessageDefinition;
import org.happy.artist.rmdmia.rcsm.provider.CommunicationSenderInterface;
import org.happy.artist.rmdmia.rcsm.provider.message.SubscriberMessageManager;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.ROSInstructionDefinition;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.ROSSubscriberMessageManager;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.ServiceProbeMessage;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.MultiProtocolPublisherCommunicator;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.tcpros.TCPROSSubscriberCommunicator;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.tcpros.TCPROSPublisherCommunicator;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.tcpros.TCPROSServiceCommunicator;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.udpros.UDPProtocolUtility;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.udpros.UDPROSSubscriberCommunicator;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.udpros.UDPROSPublisherCommunicator;

/**
 * ROSNode.java - An implementation of the ROS Node via ROS Master/Slave API.
 *
 * @author Happy Artist
 *
 * @copyright Copyright © 2013-2014 Happy Artist. All rights reserved.
 */
public class ROSNode extends RCSMProvider {
    // Put ROS Master URL here, and test is ready to go.

    private URL rosMasterURL;
    // The ROSMaster instance
    private ROSMaster master;
    private Object[] response;
    // topicNames loaded by call to getPublishedTopics
    protected transient String[] topicNames;
    // serviceNames
    protected String[] serviceNames = new String[0];
    // serviceAPIs
    protected String[] serviceAPIs = new String[0];
    // serviceCallerAPIs
    protected String[] serviceCallerAPIs = new String[0];
    // service server
    protected String[] serviceServer = new String[0];
    // service port
    protected transient int[] servicePort = new int[0];
    // service protocol
    protected String[] serviceProtocol = new String[0];
    // service preferred protocol
    protected String[] servicePreferredProtocol = new String[0];
    // service persistance
    protected boolean[] servicePersistance = new boolean[0];
    // service md5sum
    protected String[] serviceMD5Sum = new String[0];
    // service tcp_nodelay
    protected boolean[] serviceTCP_NoDelay = new boolean[0];
    // service connect_on_startup
    protected boolean[] serviceConnectOnStartup = new boolean[0];
    // service tcp_block_size
    protected int[] serviceTCP_Block_Size = new int[0];
    // service udp_packet_size
    protected int[] serviceUDPPacketSize = new int[0];
    // service message handler class (redirect_class)
    protected String[] serviceMessageHandlerClass = new String[0];
    // service initializer message handler class (redirect_class_initializer)
    protected String[] serviceInitializerMessageHandlerClass = new String[0];
    // service tuner message handler class (redirect_class_tuner)
    protected String[] serviceTunerMessageHandlerClass = new String[0];
    // service topic registry definitions
    protected ROSTopicRegistryMessageDefinition[] serviceTopicRegistryDefinitions = new ROSTopicRegistryMessageDefinition[0];
    // subscriberTopicTypes loaded by call to getPublishedTopics
    protected transient String[] subscriberTopicTypes;
    // subscriber message lookup Object
    protected SubscriberMessage[] subscriberMessages;
    // publisher message lookup Object
//    protected PublisherMessage[] publisherMessages;    
    // subscriber message lookup Object
    protected ServiceMessage[] serviceMessages;
    // topic publisher String URLs
    protected transient String[] topicPublisherURLs;
    // topic subscriber apis
    protected transient String[] topicSubscriberAPIs;
    // publisherCount loaded by call to getPublishedTopics. -1 if not set.
    protected transient int publisherCount = -1;
    // is publishable topic (some topics are only publishable, or subscribable)
    protected transient boolean[] isPublishable;
    protected String[] publishableTopics;
    // is subscribable topic (some topics are only publishable, or subscribable)
    protected transient boolean[] isSubscribable;
    protected String[] subscribableTopics;
    // is registered publication
    protected transient boolean[] isPublication;
    // is registered subscription
    protected transient boolean[] isSubscribed;
    // is registered service
    protected transient boolean[] isRegisteredService;
    // tempServiceIndice used by register service methods.
    private int tempServiceIndice;
    // Topic ROSSubscriber for subscribing single topic to multiple Publication Advertisers.
    protected transient ROSSubscriber[] topicSlave;
    // publishers loaded by call to getPublishedTopics.
//    protected transient Object[] publishers;
    // Topic Server Name
    protected transient String[] topicServerName;
    // Topic Protocol
    protected transient String[] topicProtocol;
    // Topic Port
    protected transient int[] topicPort;
    // Local UDP Topic Publisher Port
//    protected transient int[] localUDPTopicPublisherPort;    
    // Local TCP Topic Publisher Port
//    protected transient int[] localTCPTopicPublisherPort;        
    // service preferred protocol
    protected String[] topicPreferredProtocol = new String[0];
    // topic always connected
    protected boolean[] topicAlwaysConnected = new boolean[0];
    // topic md5sum
    protected String[] topicMD5Sum = new String[0];
    // topic tcp_nodelay
    protected boolean[] topicTCP_NoDelay = new boolean[0];
    // topic tcp_block_size
    protected int[] topicTCP_Block_Size = new int[0];
    // topic udp_packet_size
    protected int[] topicUDPPacketSize = new int[0];
    // topic udp+packet_size negotiated packet length response from remote node.
    protected int[] topicNegotiatedUDPPacketSize = new int[0];
    // topic subscriber/service connect_on_startup
    protected boolean[] topicConnectOnStartup = new boolean[0];
    // topic latching
    protected boolean[] topicLatching = new boolean[0];
    // topic publisher_connect_on_startup
    protected boolean[] topicPublisherConnectOnStartup = new boolean[0];
    // topic subscriber message handler class (redirect_class)
    protected String[] topicSubscriberMessageHandlerClass = new String[0];
    // topic subscriber initializer message handler class (redirect_class_initializer)
    protected String[] topicSubscriberInitializerMessageHandlerClass = new String[0];
    // topic subscriber tuner message handler class (redirect_class_tuner)
    protected String[] topicSubscriberTunerMessageHandlerClass = new String[0];
    // topic publisher message handler class (redirect_class)
    protected String[] topicPublisherMessageHandlerClass = new String[0];
    // topic publisher initializer message handler class (redirect_class_initializer)
    protected String[] topicPublisherInitializerMessageHandlerClass = new String[0];
    // topic publisher tuner message handler class (redirect_class_tuner)
    protected String[] topicPublisherTunerMessageHandlerClass = new String[0];
    // topic registry definitions
    protected ROSTopicRegistryMessageDefinition[] topicRegistryDefinitions = new ROSTopicRegistryMessageDefinition[0];
    // ROS Node Caller_id
    protected transient String caller_id;
    // Thr ROSMaster callback port
    private int port;
    // The XML-RPC Response status (i.e. success=1, failure=0, and error=-1).
    private int responseStatus;
    // The register response data i.e. response[2]
    private Object[] registerResponse;
    // regSubCount is the register subscriber while loop count
    private int regSubCount;
    // regPubCount is the register Publisher while loop count
    private int regPubCount;
    // Used by Server Response
    private Object[] statusResponse = new Object[3];
    private Integer statusCode;
    private String statusMessage;
    // Parameter Server Implemented purely for parameter updates. Not using at this point in time for RMDMIA implementation. So Dummy Object for most part.
    private ROSSlaveParameterListener paramListener;
    // topic slave caller reference definition.
    private ROSSlaveCaller topicCaller;
    // Updated every time a call is made to getSystemState();
    private ROSTopicStates topicSystemStates;
    // Lookup the ROS Topic Type by Topic.
    private ROSTopicTypeLookup topicTypeLookup;
    // The initial number of Subscriber Slaves allocated in each ROSSubscriber Object
    private int initialGroupSize = 2;
    // pubURL definition for requestTopic method.
    private URL pubURL;
    // Definition of the caller_api ROSSlave
    protected ROSSlave caller_api;
    // TCPROS & UDPROS Protocol implementations for requestTopic method.
    private Object[] SUPPORTED_PROTOCOLS = new Object[1];
    // PREFERRED PROTOCOL UDPROS
    private Object[] SUPPORTED_PROTOCOLS_UDP_PREFERRED = new Object[1];
    private Object[] TCPROS_PROTOCOL = new Object[1];
    private Object[] UDPROS_PROTOCOL = new Object[5];
    private Object[] UDPROS_CONNECTION_ARRAY;
    // server address for local reference.
    public String hostname;
    // Master caller_api publisher uri
    private String master_caller_api;
    // variables used by method updateSystemState
    private int status;
    // updateSystemState method variables.
    private Object[] publishers_state;
    private Object[] subscribers_state;
    private Object[] services_state;
    private String pubTopic;
    private String subTopic;
    private String srvTopic;
    private Object[] stateTopicPublishers;
    private Object[] stateTopicSubscribers;
    private Object[] stateTopicServices;
    private int stateLoopCounter;
    // updateServiceAPIsTableData() method variables
    private int serviceList;
    private int serviceCount;
    private String service_api1;
    private final static String EMPTY_STRING = "";
    private final static String PROTOCOL_SEARCH_ID = "://";
    private final static String COLON = ":";
    private final static String FORWARD_SLASH = "/";
    private int tmpIndex;
    private int tmpIndex2;
    private String protocol;
    private String server;
    private String rpcPort = EMPTY_STRING;
    private int serviceLoopMonIndice;
    // Implementation of SubscriberMessageManager
    protected ROSSubscriberMessageManager subscriberMessageManager;
    protected ROSTopicRegistryMessageDefinition tempTRMD;
    private int tempTID;
    private final static String TCPROS = "TCPROS";
    private final static String UDPROS = "UDPROS";
    // getSenderLookupMap method variables.
    // The sender lookup Map define.
    private Map<String, CommunicationSenderInterface[]> senderLookupMap;
    // The sender subscriber lookup Map define.
    private Map<String, CommunicationSenderInterface> senderSubscriberLookupMap;
    // The sender publisher lookup Map define.
    private Map<String, CommunicationSenderInterface> senderPublisherLookupMap;
    // The sender service lookup Map define.
    private Map<String, CommunicationSenderInterface> senderServiceLookupMap;
    // The while loop counter for getSenderLookupMap
    private int senderLookupLoopCounter;
    // The length of defined topics and services. 
    private int senderLookupElementCount;
    // The current service index variable stores a variable of current iteration in second while loop - topicNames.length to call the correct serviceNames index.
    private int currentServiceIndex;
    // The Default Message Handler Class that forwards all process calls in the MessageHandlerInterface to the Sensor Queues. The topic_registry.eax reditect_class value supports custom interface implementations.
    private final static String DEFAULT_MESSAGE_HANDLER_CLASS = "org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.DefaultMessageHandler";
    // Initializes MessageHandler Class, and launches the Message handler on following messages.
    private final static String DEFAULT_INITIALIZER_MESSAGE_HANDLER_CLASS = "org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.DefaultInitializerMessageHandler";
    // Used to Auto tune the UDP Packer Size & TCP Read block byte array sizes,
    private final static String DEFAULT_TUNER_MESSAGE_HANDLER_CLASS = "org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.DefaultTunerMessageHandler";
    // The Default Publisher Message Handler Class that forwards all process calls in the MessageHandlerInterface to the Sensor Queues. The topic_registry.eax reditect_class value supports custom interface implementations.
    private final static String DEFAULT_PUBLISHER_MESSAGE_HANDLER_CLASS = "org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.DefaultPublisherMessageHandler";
    // Initializes Publisher MessageHandler Class, and launches the Message handler on following messages.
    private final static String DEFAULT_PUBLISHER_INITIALIZER_MESSAGE_HANDLER_CLASS = "org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.DefaultPublisherInitializerMessageHandler";
    // Used to Auto tune the Publisher UDP Packer Size & TCP Read block byte array sizes,
    private final static String DEFAULT_PUBLISHER_TUNER_MESSAGE_HANDLER_CLASS = "org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.DefaultPublisherTunerMessageHandler";
    //
    private Properties properties;
    // Class Logger define & instantiation
    private Logger logger = Logger.getLogger(ROSNode.class.getName());
// ROSMessagePool property variable for access by ROSMessagePool
    protected static int ros_message_pool_size;
    // Attempt to probe for m5dsum on mismatch, and update the md5sum startup configuration.
    public static boolean attempt_probe_save_on_md5_mismatch;
    // Fill in empty configuration values at startup values include service md5sum, type, req_type, res_type, and message_definition. Implements Usage of client without manual configutation.
    public static boolean startup_auto_config;
    // initialize variables
    private Object[] fields;

// TODO: Implement getServiceType method funtionality, that retrieves service type from connection header. Additionally, on calls to getServiceType append the service type to the TopicTypes Map class and call a get there, before calling a call to the Master to obtain the value. Expected Behavior is if the data exists do not look for it via XMLRPC, unless user overrides setting via a method call setting, or property setting.
    public ROSNode() {
        /*
         try
         {
         // server address
         this.hostname=(String)properties.get("hostname");
         }
         catch(NullPointerException e)
         {
         this.hostname="localhost";
         logger.log(Level.SEVERE,"RCSM ros \"hostname\" property key was not specified for RCSM Plugin Properties. Defaulting to hostname: localhost",e);
         }                
         // ROS caller_id
         if((String)properties.get("caller_id")!=null)
         {
         this.caller_id=(String)properties.get("caller_id");
         }
         else
         {
         logger.log(Level.SEVERE,"RCSM ros \"caller_id\" property key was not specified for RCSM Plugin Properties. Defaulting to caller_id: /rmdmia");
         this.caller_id="/rmdmia";
         }
         try
         {
         // ROS Master URL
         this.rosMasterURL = new URL((String)properties.get("master_url"));
         }
         catch(MalformedURLException e)
         {
         logger.log(Level.SEVERE,"RCSM ros \"master_url\" property key contained a malformed URL in the RCSM Plugin Properties. Please update he master_url with the ROS_MASTER_URI Environment variable. ",e);          
         }        
         catch(NullPointerException e)
         {
         logger.log(Level.SEVERE,"RCSM ros \"master_url\" property key was not specified for RCSM Plugin Properties. Typically this is the ROS_MASTER_URI Environment variable. ",e);
         }                   
        
         // Set the TCPROS & UDPROS String IDs.
         TCPROS_PROTOCOL[0] = "TCPROS";
         UDPROS_PROTOCOL[0] = "UDPROS";
         SUPPORTED_PROTOCOLS[0] = TCPROS_PROTOCOL;
         SUPPORTED_PROTOCOLS[1] = UDPROS_PROTOCOL;
         // Set the UDPROS Preferred Protocol supported protocol.
         SUPPORTED_PROTOCOLS_UDP_PREFERRED[0] = UDPROS_PROTOCOL;
         SUPPORTED_PROTOCOLS_UDP_PREFERRED[1] = TCPROS_PROTOCOL;

         this.subscriberMessageManager = SubscriberMessageManager.getInstance(caller_id);
         this.master = new ROSMaster(rosMasterURL, caller_id);
         // setUp the ROS Node.
         this.response = master.getPublishedTopics(caller_id, EMPTY_STRING);
         this.publisherCount = ((Object[]) response[2]).length;
         this.publishers = ((Object[]) response[2]);
         // Initialize the topic arrays.
         this.topicNames = new String[publisherCount];
         this.subscriberTopicTypes = new String[publisherCount];
         this.subscriberMessages = new SubscriberMessage[publisherCount];
         //this.serviceMessages = new ServiceMessage[publisherCount];
         this.topicPublisherURLs = new String[publisherCount];
         this.topicSubscriberAPIs = new String[publisherCount];
         // stores a boolean value of true if the topic indice registered a subscription to the specified topic.
         this.isSubscribed = new boolean[publisherCount];
         // stores a boolean value of true if the topic indice registered a publication to the specified topic.
         this.isPublication = new boolean[publisherCount];
         this.topicProtocol = new String[publisherCount];
         this.topicServerName = new String[publisherCount];
         this.topicSlave = new ROSSubscriber[publisherCount];
         this.topicPort = new int[publisherCount];
         this.topicPreferredProtocol = new String[publisherCount];
         this.topicAlwaysConnected = new boolean[publisherCount];
         this.topicMD5Sum = new String[publisherCount];
         this.topicTCP_NoDelay = new boolean[publisherCount];
         this.topicTCP_Block_Size = new int[publisherCount];
         this.topicUDPPacketSize = new int[publisherCount];
         this.topicConnectOnStartup = new boolean[publisherCount];
         this.topicSubscriberMessageHandlerClass = new String[publisherCount];
         this.topicSubscriberInitializerMessageHandlerClass = new String[publisherCount];
         this.topicSubscriberTunerMessageHandlerClass = new String[publisherCount];
         this.topicRegistryDefinitions = new ROSTopicRegistryMessageDefinition[publisherCount];
         // Register the caller_api here for incoming master requests.
         register_caller_api();
         // process the getPublishedTopics response
         if (((Integer) response[0]).intValue() == 1) {
         this.publisherCount = ((Object[]) response[2]).length;
         this.publishers = ((Object[]) response[2]);
         for (int i = 0; i < this.publishers.length; i++) {
         //                System.out.print(i + COLON);
         this.fields = (Object[]) publishers[i];
         for (int j = 0; j < fields.length; j++) {
         this.topicNames[i] = ((String) fields[0]);
         this.subscriberTopicTypes[i] = ((String) fields[1]);
         // TODO: Add troubleshooting code in case -1 is returned on getMessageDefinition by TID.
         this.tempTID = subscriberMessageManager.getTIDByTopicName(topicNames[i]);
         if (tempTID != -1) {
         this.tempTRMD = subscriberMessageManager.getTopicRegistryMessageDefinitionByTID(tempTID);
         this.topicRegistryDefinitions[i] = tempTRMD;
         this.topicPreferredProtocol[i] = tempTRMD.preferred_protocol;
         if (tempTRMD.always_connected.equals(ONE)) {
         this.topicAlwaysConnected[i] = true;
         } else {
         this.topicAlwaysConnected[i] = false;
         }
         this.topicMD5Sum[i] = tempTRMD.md5sum;
         if (tempTRMD.tcp_nodelay.equals(ONE)) {
         this.topicTCP_NoDelay[i] = true;
         } else {
         this.topicTCP_NoDelay[i] = false;
         }
         if (tempTRMD.connect_on_start.equals(ONE)) {
         this.topicConnectOnStartup[i] = true;
         } else {
         this.topicConnectOnStartup[i] = false;
         }
         // set the topic TCP Block Size
         this.topicTCP_Block_Size[i] = Integer.valueOf(tempTRMD.tcp_block_size).intValue();
         //Set the topic UDP Packet Size.
         this.topicUDPPacketSize[i] = Integer.valueOf(tempTRMD.udp_packet_size).intValue();
         // Set the Message Handler Class.
         if (tempTRMD.redirect_class.isEmpty()) {
         this.topicSubscriberMessageHandlerClass[i] = DEFAULT_MESSAGE_HANDLER_CLASS;
         } else {
         this.topicSubscriberMessageHandlerClass[i] = tempTRMD.redirect_class;
         }
         // Set the Initializer Message Handler Class.
         if (tempTRMD.redirect_class_initializer.isEmpty()) {
         this.topicSubscriberInitializerMessageHandlerClass[i] = DEFAULT_INITIALIZER_MESSAGE_HANDLER_CLASS;
         } else {
         this.topicSubscriberInitializerMessageHandlerClass[i] = tempTRMD.redirect_class_initializer;
         }
         // Set the Tuner Message Handler Class.
         if (tempTRMD.redirect_class_tuner.isEmpty()) {
         this.topicSubscriberTunerMessageHandlerClass[i] = DEFAULT_TUNER_MESSAGE_HANDLER_CLASS;
         } else {
         this.topicSubscriberTunerMessageHandlerClass[i] = tempTRMD.redirect_class_tuner;
         }
         // Set the tempTRMD to null;
         this.tempTRMD = null;
         }
         //                    System.out.print(((String)this.fields[0]) + ", type: " +((String)this.fields[1]) + "\n");
         }
         }
         }
         this.paramListener = new ROSSlaveParameterListener();
         // Only updated on calls to getSystemState() on the Master.
         this.topicSystemStates = new ROSTopicStates();

         // setUp the topic types table.
         getTopicTypes(caller_id);
         // Update service names, and APIs at initialization
         updateServiceAPIsTableData();
         */
    }

    /**
     * Register the Master Caller API.
     */
    private void register_caller_api() throws IOException, XmlRpcException {
        // Register caller api
        //this.caller_api = new ROSSlave(rosMasterURL, rosMasterURL, this, "caller_api");
        this.caller_api = new ROSSlave(rosMasterURL, rosMasterURL, this, caller_id);
        this.master_caller_api = "http://" + hostname + COLON + String.valueOf(caller_api.getCallbackPort());
    }

    /**
     * Returns boolean is registered service.
     *
     * @return boolean
     */
    public boolean isRegisteredService(int serviceIndice) {
        return isRegisteredService[serviceIndice];
    }

    /**
     * Returns boolean is registered topic subscriber.
     *
     * @return boolean
     */
    public boolean isRegisteredSubscriber(int topicIndice) {
        return isSubscribed[topicIndice];
    }

    /**
     * Returns boolean is registered topic publisher.
     *
     * @return boolean
     */
    public boolean isRegisteredPublisher(int topicIndice) {
        return isPublication[topicIndice];
    }

    /**
     * Increment topic indexes and return the new index of the specified topic.
     * -1 if either input parameter is null.
     */
    private int incrementTopicIndex(String topic, String topicType) {
        if (topic != null && topicType != null) {
            this.publisherCount = publisherCount + 1;
            this.pubSubTopics = Arrays.copyOf(pubSubTopics, publisherCount);
            // Initialize the topic arrays.
            this.topicNames = Arrays.copyOf(topicNames, publisherCount);
            topicNames[publisherCount - 1] = topic;
            this.subscriberTopicTypes = Arrays.copyOf(subscriberTopicTypes, publisherCount);
            subscriberTopicTypes[publisherCount - 1] = topic;
// TODO: update increment topicIndex with subscriber message if one exists in configuration.
            this.subscriberMessages = Arrays.copyOf(subscriberMessages, publisherCount);
//            this.publisherMessages = Arrays.copyOf(publisherMessages, publisherCount);            
            //subscriberMessages[publisherCount - 1] = topic;

            this.topicPublisherURLs = Arrays.copyOf(topicPublisherURLs, publisherCount);
            this.topicSubscriberAPIs = Arrays.copyOf(topicSubscriberAPIs, publisherCount);
            // is a publishable topic
            this.isPublishable = Arrays.copyOf(isPublishable, publisherCount);
            // is a subscribable topic.
            this.isSubscribable = Arrays.copyOf(isSubscribable, publisherCount);
            // stores a boolean value of true if the topic indice registered a subscription to the specified topic.
            this.isSubscribed = Arrays.copyOf(isSubscribed, publisherCount);
            // stores a boolean value of true if the topic indice registered a publication to the specified topic.
            this.isPublication = Arrays.copyOf(isPublication, publisherCount);
            this.topicProtocol = Arrays.copyOf(topicProtocol, publisherCount);
            this.topicServerName = Arrays.copyOf(topicServerName, publisherCount);
            this.topicSlave = Arrays.copyOf(topicSlave, publisherCount);
            this.topicPort = Arrays.copyOf(topicPort, publisherCount);
// TODO: Add all topic variables            
            // topic always connected
            this.topicAlwaysConnected = Arrays.copyOf(topicAlwaysConnected, publisherCount);
            // topic md5sum
            this.topicMD5Sum = Arrays.copyOf(topicMD5Sum, publisherCount);
            // topic tcp_nodelay
            this.topicTCP_NoDelay = Arrays.copyOf(topicTCP_NoDelay, publisherCount);
            // topic tcp_block_size
            this.topicTCP_Block_Size = Arrays.copyOf(topicTCP_Block_Size, publisherCount);
            // topic udp_packet_size
            this.topicUDPPacketSize = Arrays.copyOf(topicUDPPacketSize, publisherCount);
            // topic udp+packet_size negotiated packet length response from remote node.
            this.topicNegotiatedUDPPacketSize = Arrays.copyOf(topicNegotiatedUDPPacketSize, publisherCount);
            // topic subscriber/service connect_on_startup
            this.topicConnectOnStartup = Arrays.copyOf(topicConnectOnStartup, publisherCount);
            // topic latching
            this.topicLatching = Arrays.copyOf(topicLatching, publisherCount);
            // UDPROS requestTopic connection Strings
            this.UDPROS_CONNECTION_ARRAY = Arrays.copyOf(UDPROS_CONNECTION_ARRAY, publisherCount);
            return publisherCount - 1;
        }
        return -1;
    }

    /**
     * Return the ROS Master API.
     */
    public ROSMaster getMasterAPI() {
        return master;
    }

    /**
     * Return the callback port.
     */
    public int getCallbackPort() {
        return port;
    }

    /**
     * Register the caller as a provider of the specified service. Returns
     * Object[] (int, str, int) = (code, statusMessage, ignore). Exception is
     * thrown on 0, or -1, all other codes are returned in Object[].
     */
    public Object[] registerService(String caller_id, String service, String service_api, String caller_api) throws XmlRpcException, ROSXMLRPCException {
        this.tempServiceIndice = getServiceIndex(service);
        if (tempServiceIndice != -1 && tempServiceIndice < isRegisteredService.length) {
            this.isRegisteredService[getServiceIndex(service)] = false;
        }
        return master.registerService(caller_id, service, service_api, caller_api);
    }

    /**
     * Unregister the caller as a provider of the specified service. Returns
     * Object[] (int, str, int) = (code, statusMessage, numUnregistered). Number
     * of unregistrations (either 0 or 1). If this is zero it means that the
     * caller was not registered as a service provider. The call still succeeds
     * as the intended final state is reached.
     */
    public Object[] unregisterService(String caller_id, String service, String service_api) throws XmlRpcException, ROSXMLRPCException {
        this.tempServiceIndice = getServiceIndex(service);
        if (tempServiceIndice != -1 && tempServiceIndice < isRegisteredService.length) {
            this.isRegisteredService[getServiceIndex(service)] = false;
        }
        return master.unregisterService(caller_id, service, service_api);
    }

    /**
     * Register the topic subscriber. Returns the topic index in the
     * topicPublisherURLs array for getTopic String URL input parameter. Returns
     * -1 if topic not found.
     */
    public int registerSubscriber(String caller_id, String topic, String topic_type, String caller_api) throws XmlRpcException, ROSXMLRPCException {
        this.response = master.registerSubscriber(caller_id, topic, topic_type, caller_api);
        //System.out.println("registerSubscriber Topic: " + topic);
        this.responseStatus = ((Integer) response[0]).intValue();
        if (responseStatus == 1) {
            // this.isSubscribed[getTopicIndex(topic)] = true;
            this.registerResponse = ((Object[]) response[2]);
// TODO: implement support for subscriberMessages configuration.
            // set the topic publisher URL by register URL response
            this.regSubCount = 0;
            while (regSubCount < topicNames.length) {
                if (topicNames[regSubCount].equals(topic)) {
//                    System.out.println("registerResponse[0].length: " + registerResponse.length + ", topicPublisherURLs[regSubCount]:length: " + topicPublisherURLs.length + ", topic: " + topic);
                    if (registerResponse.length > 0) {
                        // Contains a Publisher URL
                        topicPublisherURLs[regSubCount] = ((String) registerResponse[0]);
                        // Set isSubscribed to true.
                        this.isSubscribed[getTopicIndex(topic)] = true;
                    } else {
                        // Contains no PublisherURL therefore cannot be subscribed.
                        this.isSubscribed[getTopicIndex(topic)] = false;
                    }
                    return regSubCount;
                }
                this.regSubCount = regSubCount + 1;
            }
        } else {
            this.isSubscribed[getTopicIndex(topic)] = false;
        }
        return -1;
    }
    private int count = 0;
    private final static int one = 1;
    private final static String ONE = "1";

    /**
     * Returns the topic index, or -1 if the topic was not found.
     */
    public int getTopicIndex(String topic) {
        this.count = 0;
        while (count < topicNames.length) {
            if (topicNames[count].equals(topic)) {
                return count;
            }
            this.count = count + one;
        }
        return -1;
    }
    // getServiceIndex method variables.
    private int serviceIndexCount;

    /**
     * Returns the service index, or -1 if the service was not found.
     */
    public int getServiceIndex(String service) {
        this.serviceIndexCount = 0;
        while (serviceIndexCount < serviceNames.length) {
            if (serviceNames[serviceIndexCount].equals(service)) {
                return serviceIndexCount;
            }
            this.serviceIndexCount = serviceIndexCount + one;
        }
        return -1;
    }

    /**
     * Unregister the caller as a publisher of the topic. Returns (int, str,
     * int) = (code, statusMessage, numUnsubscribed). If numUnsubscribed is zero
     * it means that the caller was not registered as a subscriber. The call
     * still succeeds as the intended final state is reached.
     */
    public Object[] unregisterSubscriber(String caller_id, String topic, String caller_api) throws XmlRpcException, ROSXMLRPCException {
        this.isSubscribed[getTopicIndex(topic)] = false;
        return master.unregisterSubscriber(caller_id, topic, caller_api);
    }
    //done
    private int pubIndex;

    /**
     * Register the caller as a publisher the topic. Returns the
     * topicSubscriberAPIs index. Returns -1 if topic not found.
     */
    public int registerPublisher(String caller_id, String topic, String topic_type, String caller_api) throws XmlRpcException, ROSXMLRPCException {
        this.response = master.registerPublisher(caller_id, topic, topic_type, caller_api);
        this.responseStatus = ((Integer) response[0]).intValue();
        if (responseStatus == 1) {
            if ((this.pubIndex = getTopicIndex(topic)) == -1) {
                // Set the subscriberTopic and topic type and new index
                this.pubIndex = incrementTopicIndex(topic, topic_type);
            }
            this.registerResponse = ((Object[]) response[2]);
            
 //           System.out.println("registerPublisher("+caller_id+", "+topic+", "+topic_type+", "+caller_api+"\nResponse array: "+Arrays.toString(registerResponse));
            // set the topic publisher URL by register URL response
            this.regPubCount = 0;
            while (regPubCount < topicNames.length) {
                if (topicNames[regPubCount].equals(topic)) {
                    if (registerResponse.length > 0) {
                        this.isPublication[getTopicIndex(topic)] = true;
                        topicSubscriberAPIs[regPubCount] = ((String) registerResponse[0]);
                        return regPubCount;
                    } else {
                        this.isPublication[getTopicIndex(topic)] = false;
                    }
                }
                this.regPubCount = regPubCount + 1;
            }
        } else {
            this.isPublication[getTopicIndex(topic)] = false;
        }
        return -1;
    }

    //Done
    /**
     * Unregister the caller as a publisher of the topic. Returns (int, str,
     * int) = (code, statusMessage, numUnsubscribed). If numUnregistered is zero
     * it means that the caller was not registered as a publisher. The call
     * still succeeds as the intended final state is reached.
     */
    public Object[] unregisterPublisher(String caller_id, String topic, String caller_api) throws XmlRpcException, ROSXMLRPCException {
        this.isPublication[getTopicIndex(topic)] = false;
        return master.unregisterPublisher(caller_id, topic, caller_api);
    }

    /**
     * Get the XML-RPC URI of the node with the associated name/caller_id. This
     * API is for looking information about publishers and subscribers. Use
     * lookupService instead to lookup ROS-RPC URIs. Returns (int, str, str) -
     * (code, statusMessage, URI)
     */
    public Object[] lookupNode(String caller_id, String node_name) throws XmlRpcException, ROSXMLRPCException {
        return master.lookupNode(caller_id, node_name);
    }
    private String responseMessage;
    private Object[] topicTypes;

    /**
     * Retrieve list topic names and their types. Returns (int, str, [ [str,str]
     * ]) - (code, statusMessage, topicTypes). topicTypes is a list of
     * [topicName, topicType] pairs.
     */
    public Object[] getTopicTypes(String caller_id) throws XmlRpcException, ROSXMLRPCException {
        this.response = master.getTopicTypes(caller_id);
        this.status = ((Integer) response[0]).intValue();
        if (status == 1) {
            this.responseMessage = ((String) response[1]);
            this.topicTypeLookup = new ROSTopicTypeLookup(this.topicTypes = ((Object[]) response[2]));
        }

        return master.getTopicTypes(caller_id);
    }

    /**
     * Return topic topic by topic name.
     */
    public String getTopicType(String topic) {
        return topicTypeLookup.getTopicType(topic);
    }

    /**
     * Get the URI of the the master. Returns (int, str, str) - (code,
     * statusMessage, masterURI)
     */
    public Object[] getUri(String caller_id) throws XmlRpcException, ROSXMLRPCException {
        return master.getUri(caller_id);
    }

    /**
     * Lookup all provider of a particular service. Returns (int, str, str) -
     * (code, statusMessage, serviceUrl) service URL is provides address and
     * port of the service. Fails if there is no provider.
     */
    public Object[] lookupService(String caller_id, String service) throws XmlRpcException, ROSXMLRPCException {
        return master.lookupService(caller_id, service);
    }

    /**
     * Get list of topics that can be subscribed to. This does not return topics
     * that have no publishers. See getSystemState() to get more comprehensive
     * list. Returns (int, str, [[str, str],]) - (code, statusMessage, [
     * [topic1, type1]...[topicN, typeN] ])
     */
    public Object[] getPublishedTopics(String caller_id, String subgraph) throws XmlRpcException, ROSXMLRPCException {
        return master.getPublishedTopics(caller_id, subgraph);
    }

    /**
     * shutdown() the ROSMaster - includes closing the Callback port.
     */
    public void shutdown() {
        // Set isInitialized to false.
        this.isInitialized = false;
        master.shutdown();
    }

    /**
     * Return boolean is initialized on RCSM Provider.
     *
     * @return boolean returns false if RCSM Provider is not initialized.
     */
    public boolean isInitialized() {
        return isInitialized;
    }
////////////////////// The following code below is the Slave API Implementation
////////////////////// for ROSMaster class. ///////////////////////////////////

    /**
     * Retrieve transport/topic statistics.
     *
     * @param caller_id (String) ROS caller ID.
     * @return Returns (Integer, String, [XMLRPCLegalValue*]) = (code,
     * statusMessage, stats) stats is of the form [publishStats, subscribeStats,
     * serviceStats] where
     *
     * publishStats: [[topicName, messageDataSent, pubConnectionData]...]
     * subscribeStats: [[topicName, subConnectionData]...] serviceStats:
     * (proposed) [numRequests, bytesReceived, bytesSent]
     *
     * pubConnectionData: [connectionId, bytesSent, numSent, connected]*
     * subConnectionData: [connectionId, bytesReceived, dropEstimate,
     * connected]*
     *
     * dropEstimate: -1 if no estimate.
     */
    public Object[] getBusStats(String caller_id) throws XmlRpcException, ROSXMLRPCException {
        return master.getBusStats(caller_id);
    }

    /**
     * Retrieve transport/topic connection information.
     *
     * @param caller_id (String) ROS caller ID.
     * @return Returns (Integer, String, [XMLRPCLegalValue*] (Is Object[] or
     * Object[][] not sure yet.)) = (code, statusMessage, busInfo) busInfo is of
     * the form: [[connectionId1, destinationId1, direction1, transport1,
     * topic1, connected1]... ] - connectionId is defined by the node and is
     * opaque. - destinationId is the XMLRPC URI of the destination. - direction
     * is one of 'i', 'o', or 'b' (in, out, both). - transport is the transport
     * type (e.g. 'TCPROS'). - topic is the topic name. - connected1 indicates
     * connection status. Note that this field is only provided by slaves
     * written in Python at the moment (cf. rospy/masterslave.py in
     * _TopicImpl.get_stats_info() vs. roscpp/publication.cpp in
     * Publication::getInfo()).
     */
    public Object[] getBusInfo(String caller_id) throws XmlRpcException, ROSXMLRPCException {
        return master.getBusInfo(caller_id);
    }

    /**
     * Return the ROS Master URI.
     */
    public URL getMasterURL() {
        return rosMasterURL;
    }

    /**
     * Get the URI of the master node.
     *
     * @param caller_id (String) ROS caller ID.
     * @return Returns (Integer, String, Integer) = (code, statusMessage,
     * masterURI)
     */
    public Object[] getMasterUri(String caller_id) throws XmlRpcException, ROSXMLRPCException {
        return master.getMasterUri(caller_id);
    }

    /**
     * Stop this server.
     *
     * @param caller_id (String) ROS caller ID.
     * @param message (String) A message describing why the node is being
     * shutdown.
     * @return Returns (Integer, String, Integer) = (code, statusMessage,
     * ignore)
     */
    public Object[] shutdown(String caller_id, String message) throws XmlRpcException, ROSXMLRPCException {
        return master.shutdown(caller_id, message);
    }

    /**
     * Get the PID of this server.
     *
     * @param caller_id (String) ROS caller ID.
     * @return Returns (Integer, String, Integer) = (code, statusMessage,
     * serverProcessPID)
     */
    public Object[] getPid(String caller_id) throws XmlRpcException, ROSXMLRPCException {
        return master.getPid(caller_id);
    }

    /**
     * Retrieve a list of topics that this node subscribes to.
     *
     * @param caller_id (String) ROS caller ID.
     * @return Returns (code, statusMessage, topicList) topicList is a list of
     * topics this node subscribes to and is of the form [ [topic1,
     * topicType1]...[topicN, topicTypeN] ]
     */
    public Object[] getSubscriptions(String caller_id) throws XmlRpcException, ROSXMLRPCException {
        return master.getSubscriptions(caller_id);
    }

    /**
     * Retrieve a list of topics that this node publishes.
     *
     * @param caller_id (String) ROS caller ID.
     * @return Returns (Integer, String, [[String, String]]) = (code,
     * statusMessage, topicList) topicList is a list of topics published by this
     * node and is of the form [[topic1, topicType1]...[topicN, topicTypeN]]
     */
    public Object[] getPublications(String caller_id) throws XmlRpcException, ROSXMLRPCException {
        return master.getPublications(caller_id);
    }

    /**
     * Callback from master with updated value of subscribed parameter.
     *
     * @param caller_id (String) ROS caller ID.
     * @param parameter_key (String) Parameter name, globally resolved.
     * @param parameter_value (!XMLRPCLegalValue(Java Object)) New parameter
     * value.
     * @return Returns (Integer, String, Integer) = (code, statusMessage,
     * ignore)
     */
    public Object[] paramUpdate(String caller_id, String parameter_key, Object parameter_value) throws XmlRpcException, ROSXMLRPCException {
        return master.paramUpdate(caller_id, parameter_key, parameter_value);
    }

    /**
     * Callback from master of current publisher list for specified topic.
     *
     * @param caller_id (String) ROS caller ID.
     * @param topic (String) Topic name.
     * @param publishers ([String]) List of current publishers for topic in the
     * form of XMLRPC URIs
     * @return Returns (Integer, String, Integer) = (code, statusMessage,
     * ignore)
     */
    public Object[] publisherUpdate(String caller_id, String topic, String[] publishers) throws XmlRpcException, ROSXMLRPCException {
        return master.publisherUpdate(caller_id, topic, publishers);
    }

    /**
     * Update the System State calls XMLRPC Master API getSystemState(), and
     * updates the data to the ROSNode ROSTopicStates object.
     */
    public void updateSystemState(String callerID) throws XmlRpcException, ROSXMLRPCException {

        this.response = master.getSystemState(callerID);
        this.status = ((Integer) response[0]).intValue();
        if (status == 1) {
            // must call updateStart() on start of getSystemState()
            topicSystemStates.updateStart();
            //System.out.println("Master System State Status: "+((String)response[1]));
            //System.out.println("System State Array Length: " + ((Object[])response[2]).length);
            this.publishers_state = ((Object[]) ((Object[]) response[2])[0]);
            this.subscribers_state = ((Object[]) ((Object[]) response[2])[1]);
            this.services_state = ((Object[]) ((Object[]) response[2])[2]);
            //System.out.println("System State Publishers Array Length: " + publishers.length);
            //System.out.println("System State Subscribers Array Length: " + subscribers.length);
//            System.out.println("System State Services Array Length: " + services_state.length);
            this.stateLoopCounter = 0;
            while (stateLoopCounter < publishers_state.length) {
                this.pubTopic = ((String) ((Object[]) publishers_state[stateLoopCounter])[0]);
                this.stateTopicPublishers = ((Object[]) ((Object[]) publishers_state[stateLoopCounter])[1]);
                topicSystemStates.setTopicPublishers(pubTopic, stateTopicPublishers);
                this.stateLoopCounter = stateLoopCounter + 1;
            }
            this.stateLoopCounter = 0;
            while (stateLoopCounter < subscribers_state.length) {
                this.subTopic = ((String) ((Object[]) subscribers_state[stateLoopCounter])[0]);
                this.stateTopicSubscribers = ((Object[]) ((Object[]) subscribers_state[stateLoopCounter])[1]);
                topicSystemStates.setTopicSubscribers(subTopic, stateTopicSubscribers);
                this.stateLoopCounter = stateLoopCounter + 1;
            }
            this.stateLoopCounter = 0;
            while (stateLoopCounter < services_state.length) {

                this.srvTopic = ((String) ((Object[]) services_state[stateLoopCounter])[0]);
                this.stateTopicServices = ((Object[]) ((Object[]) services_state[stateLoopCounter])[1]);
//                System.out.println("updateSystemState - services topic=" + srvTopic + ", topicServices: " + Arrays.toString(stateTopicServices));
                topicSystemStates.setTopicServices(srvTopic, stateTopicServices);
                this.stateLoopCounter = stateLoopCounter + 1;
            }

            // must call updateDone() after all data is processed on getSystemState()
            topicSystemStates.updateDone();
        } else if (status == 0) {
            throw new ROSXMLRPCException("FAILURE (".concat(((Integer) response[2]).toString()).concat("): Method failed to complete correctly. In general, this means that the master/slave attempted the action and failed, and there may have been side-effects as a result."));
        } else if (status == -1) {
            throw new ROSXMLRPCException("ERROR (".concat(((Integer) response[2]).toString()).concat("): Error on the part of the caller, e.g. an invalid parameter. In general, this means that the master/slave did not attempt to execute the action."));
        } else {
            // Unknown response - return normally for receiver to handle.
            throw new ROSXMLRPCException("UNSUPPORTED RETURN CODE");
        }
    }

    /**
     * Return ROSTopicStates Object for this ROSNode, after it calls
     * updateSystemState(), which calls XMLRPC Master API getSystemState(), and
     * updates the data to the ROSNode ROSTopicStates object before it is
     * returned.
     */
    public ROSTopicStates getSystemState(String callerID) throws XmlRpcException, ROSXMLRPCException {
        updateSystemState(callerID);
        return topicSystemStates;
    }

    /**
     * Return the last updated ROSTopicStates Object for this ROSNode, without
     * calling getSystemState again.
     */
    public ROSTopicStates getSystemStateObject() throws XmlRpcException, ROSXMLRPCException {
        return topicSystemStates;
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
    /**
     * Publisher node API method called by a subscriber node. This requests that
     * source allocate a channel for communication. Subscriber provides a list
     * of desired protocols for communication. Publisher returns the selected
     * protocol along with any additional params required for establishing
     * connection. For example, for a TCP/IP-based connection, the source node
     * may return a port number of TCP/IP server.
     *
     * @param caller_id (String) ROS caller ID.
     * @param topic (String) Topic name.
     * @param protocols ([ [String, !XMLRPCLegalValue*] ]) List of desired
     * protocols for communication in order of preference. Each protocol is a
     * list of the form [ProtocolName, ProtocolParam1, ProtocolParam2...N]
     * @return Returns (Integer, String, [String, !XMLRPCLegalValue*] ) = (code,
     * statusMessage, protocolParams) protocolParams may be an empty list if
     * there are no compatible protocols.
     */
    private int tmpPort;

    public Object[] requestTopic(String caller_id, String topic, Object[] protocols) throws XmlRpcException, ROSXMLRPCException {
//
//TODO: Implement topicSlave i.e. ROSSubscriber in requestTopic()
        return master.requestTopic(caller_id, topic, protocols);
    }
    //TODO: Implement topicSlave i.e. ROSSubscriber in getTopic(), ROSSlave will require XMLRPC Server Handler registration.
    // then verify server handshake order on wiki
    private Object[] topicServer;

    /**
     * Request the specified topic by topic index int.
     */
    public void getTopic(int topicArrayIndice) throws MalformedURLException, IOException, XmlRpcException, ROSXMLRPCException {
        if (topicSlave[topicArrayIndice] == null) {
            this.topicSlave[topicArrayIndice] = new ROSSubscriber(this, topicNames[topicArrayIndice], subscriberTopicTypes[topicArrayIndice], initialGroupSize, topicArrayIndice);
//            System.out.println("Topic PubURL: " + topicPublisherURLs[topicArrayIndice]);
            if (topicPublisherURLs[topicArrayIndice] != null) {
                this.pubURL = new URL(topicPublisherURLs[topicArrayIndice]);
                this.topicSlave[topicArrayIndice].add(this.pubURL = new URL(topicPublisherURLs[topicArrayIndice]));
                if (topicPreferredProtocol[topicArrayIndice].equalsIgnoreCase(UDPROS)) {
                    // UDPROS is Preferred Protocol
                    //System.out.println("UDPROS Protocol: " + topicNames[topicArrayIndice]);                    

// TODO: Process UDPROS_CONNECTION_ARRAY array here, or where it is called. Likely in other place.                    // TEST CODE
                    if (UDPROS_CONNECTION_ARRAY[topicArrayIndice] == null) {
                        try {
                            if (subscriberThreads[topicArrayIndice] == null) {
                                this.subscriberThreads[topicArrayIndice] = (this.tmpUDPCommunicator = new UDPROSSubscriberCommunicator(topicNames[topicArrayIndice], true));
                                tmpUDPCommunicator.initialize(topicServerName[topicArrayIndice], topicPort[topicArrayIndice], topicUDPPacketSize[topicArrayIndice], "UDPROS_SUB_" + topicNames[topicArrayIndice], topicAlwaysConnected[topicArrayIndice], topicSubscriberInitializerMessageHandlerClass[topicArrayIndice], topicSubscriberMessageHandlerClass[topicArrayIndice], topicSubscriberTunerMessageHandlerClass[topicArrayIndice]);
                                if (isSubscribed[topicArrayIndice]) {
                                    tmpUDPCommunicator.start();
                                    this.tmpPort = tmpUDPCommunicator.getReceivePort();
                                }
                            }
                        } catch (Exception e) {
                            logger.log(Level.WARNING, "unable to find a free port for UDPROS Subscriber socket on topic: " + topicNames[topicArrayIndice], e);
                        }
                        // finally
                        // {
                        //tmpServer.close();
                        //this.tmpServer=null;
                        //}

//TODO: Implement this String in this message... Includes validating the subscriberMessageManager variables are initialize beforehand.            
                        UDPROS_CONNECTION_ARRAY[topicArrayIndice] = new Object[]{UDPROS, UDPProtocolUtility.generateXMLRPCBase64(caller_id, topicMD5Sum[topicArrayIndice], null, topicNames[topicArrayIndice], subscriberTopicTypes[topicArrayIndice], false), hostname, Integer.valueOf(tmpPort), Integer.valueOf(((ROSTopicRegistryMessageDefinition)subscriberMessageManager.getTopicRegistryMessageDefinitionByTID(subscriberMessageManager.getTIDByTopicName(topicNames[topicArrayIndice]))).udp_packet_size)};
                        // Set the UDPROS Preferred Protocol supported protocol.
                        SUPPORTED_PROTOCOLS_UDP_PREFERRED[0] = UDPROS_CONNECTION_ARRAY[topicArrayIndice];
                        // System.out.println("SUPPORTED_PROTOCOLS_UDP_PREFERRED:" + Arrays.deepToString(SUPPORTED_PROTOCOLS_UDP_PREFERRED));
                    }

                    // END TEST CODE    
                    //  UDPROS_CONNECTION_ARRAY[topicArrayIndice]
// TODO: Generate a new Preferred Protocols list that includes TCPROS, and replace SUPPORTED_PROTOCOLS_UDP_PREFERRED.                            
                    this.response = this.topicSlave[topicArrayIndice].getSlaveCallerByPublisherURI(topicPublisherURLs[topicArrayIndice]).requestTopic(caller_id, topicNames[topicArrayIndice], SUPPORTED_PROTOCOLS_UDP_PREFERRED);
                    //System.out.println("getTopic response: " + Arrays.deepToString(response));
                    // this.response = this.topicSlave[topicArrayIndice].getSlaveCallerByPublisherURI(topicPublisherURLs[topicArrayIndice]).requestTopic(caller_id, topicNames[topicArrayIndice], UDPROS_PROTOCOL);
                    //UDPROS_PROTOCOL
                } else {
                    // Preferred Protocol TCPROS
                    //System.out.println("TCPROS Preferred Protocol: " + topicNames[topicArrayIndice]);
                    this.response = this.topicSlave[topicArrayIndice].getSlaveCallerByPublisherURI(topicPublisherURLs[topicArrayIndice]).requestTopic(caller_id, topicNames[topicArrayIndice], SUPPORTED_PROTOCOLS);
                    // DEBUG
                    //System.out.println("ROSNode requestTopic(\"" + topicNames[topicArrayIndice] + "\") method response: " + Arrays.deepToString(response));

                }
            } else if (topicSubscriberAPIs != null) {
                // registered Publisher calling this method.
                //TODO: Create a Publisher Caller client and replace Subscriber in publisher call with publisher. Not sure if this is legal, but am trying in case of no Subscriber Api
                try {
                    if (topicSubscriberAPIs[topicArrayIndice] != null && !topicSubscriberAPIs[topicArrayIndice].isEmpty()) {
                        this.pubURL = new URL(topicSubscriberAPIs[topicArrayIndice]);
                    }
                } catch (MalformedURLException e) {
                    // Do nothing
                }

                this.topicSlave[topicArrayIndice].add(pubURL);

                if (topicPreferredProtocol[topicArrayIndice].equalsIgnoreCase(UDPROS)) {
                    // TODO: Process UDPROS_CONNECTION_ARRAY array here, or where it is called. Likely in other place.                    // TEST CODE
                    if (UDPROS_CONNECTION_ARRAY[topicArrayIndice] == null) {
                        try {
                            if (subscriberThreads[topicArrayIndice] == null) {
                                this.subscriberThreads[topicArrayIndice] = (this.tmpUDPCommunicator = new UDPROSSubscriberCommunicator(topicNames[topicArrayIndice], true));
                                tmpUDPCommunicator.initialize(topicServerName[topicArrayIndice], topicPort[topicArrayIndice], topicUDPPacketSize[topicArrayIndice], "UDPROS_SUB_" + topicNames[topicArrayIndice], topicAlwaysConnected[topicArrayIndice], topicSubscriberInitializerMessageHandlerClass[topicArrayIndice], topicSubscriberMessageHandlerClass[topicArrayIndice], topicSubscriberTunerMessageHandlerClass[topicArrayIndice]);
                                if (isSubscribed[topicArrayIndice]) {
                                    tmpUDPCommunicator.start();
                                    this.tmpPort = tmpUDPCommunicator.getReceivePort();
                                }
                            }
                        } catch (Exception e) {
                            logger.log(Level.WARNING, "unable to find a free port for UDPROS Subscriber socket on topic: " + topicNames[topicArrayIndice], e);
                        }
                        //finally
                        //{
                        //    tmpServer.close();
                        //    this.tmpServer=null;
                        //}
                        //TODO: Implement this String in this message... Includes validating the subscriberMessageManager variables are initialize beforehand.            
                        UDPROS_CONNECTION_ARRAY[topicArrayIndice] = new Object[]{UDPROS, UDPProtocolUtility.generateXMLRPCBase64(caller_id, topicMD5Sum[topicArrayIndice], null, topicNames[topicArrayIndice], subscriberTopicTypes[topicArrayIndice], false), hostname, Integer.valueOf(tmpPort), Integer.valueOf(((ROSTopicRegistryMessageDefinition)subscriberMessageManager.getTopicRegistryMessageDefinitionByTID(subscriberMessageManager.getTIDByTopicName(topicNames[topicArrayIndice]))).udp_packet_size)};
                        // Set the UDPROS Preferred Protocol supported protocol.
                        SUPPORTED_PROTOCOLS_UDP_PREFERRED[0] = UDPROS_CONNECTION_ARRAY[topicArrayIndice];
                    }
                    // UDPROS is Preferred Protocol
                    this.response = this.topicSlave[topicArrayIndice].getSlaveCallerByPublisherURI(topicSubscriberAPIs[topicArrayIndice]).requestTopic(caller_id, topicNames[topicArrayIndice], SUPPORTED_PROTOCOLS_UDP_PREFERRED);
                } else {
                    // Preferred Protocol TCPROS
                    this.response = this.topicSlave[topicArrayIndice].getSlaveCallerByPublisherURI(topicSubscriberAPIs[topicArrayIndice]).requestTopic(caller_id, topicNames[topicArrayIndice], SUPPORTED_PROTOCOLS);
                }
                // DEBUG 
//                System.out.println("ROSNode requestTopic(\"" + topicNames[topicArrayIndice] + "\") method response: " + Arrays.deepToString(response));
            }
        }

        // Get the current response status for method call;
        this.responseStatus = ((Integer) response[0]).intValue();
        if (responseStatus == 1) {
            // Success
            this.topicServer = ((Object[]) response[2]);
            if (((String) topicServer[0]).equals("TCPROS")) {
                topicProtocol[topicArrayIndice] = ((String) topicServer[0]);
                topicServerName[topicArrayIndice] = ((String) topicServer[1]);
                topicPort[topicArrayIndice] = ((Integer) topicServer[2]).intValue();

            } //else if (((String) topicServer[0]).equals("UDPROS")) {
            else if (((String) topicServer[0]).indexOf("UDPROS") == 0) {
                // TODO: setup global variables
                UDPProtocolUtility utility = new UDPProtocolUtility();


                // TODO: Add the other UDP variables.
                topicProtocol[topicArrayIndice] = ((String) topicServer[0]);
                topicServerName[topicArrayIndice] = ((String) topicServer[1]);
                topicPort[topicArrayIndice] = ((Integer) topicServer[2]).intValue();
                ((UDPROSSubscriberCommunicator) subscriberThreads[topicArrayIndice]).setConnectionID(((Integer) topicServer[3]).intValue());
// TODO: 3 int meaning? response processing.
//                System.out.println("UDPROS Connection ID in Decimal: " + ((Integer)topicServer[3]).intValue());                
                topicNegotiatedUDPPacketSize[topicArrayIndice] = ((Integer) topicServer[4]).intValue();
// TODO: 5 byte[] base64 header response processing.
                utility.processUDPConnectionHeader((byte[]) topicServer[5], topicNames[topicArrayIndice], true);
                //System.out.println("UDP requestTopic Base64 response 5: " + HexStringConverter.getHexStringConverterInstance().hexToString(BytesToHex.bytesToHex((byte[])topicServer[5])));
            } else {
                topicProtocol[topicArrayIndice] = ((String) topicServer[0]);
            }

//            for (int i = 0; i < topicServer.length; i++) {
//                System.out.println(i + ". Type: " + topicServer[i].getClass().getName());
//            }

        } else if (responseStatus == 0) {
            throw new ROSXMLRPCException("FAILURE (".concat(((Integer) response[2]).toString()).concat("): Method failed to complete correctly. In general, this means that the master/slave attempted the action and failed, and there may have been side-effects as a result."));
            // System.out.println("FAILURE: " + ((String) topicServer[1]));
        } else if (responseStatus == -1) {
            throw new ROSXMLRPCException("ERROR (".concat(((Integer) response[2]).toString()).concat("): Error on the part of the caller, e.g. an invalid parameter. In general, this means that the master/slave did not attempt to execute the action."));
            // System.out.println("ERROR: " + ((String) topicServer[1]));
        } else {
            // Unknown response - return normally for receiver to handle.
            //return response;
        }
        // Test port or method
        // this.serverResponse = (Object[])topicRPCClient[topicArrayIndice].execute("getPublications", new Object[]{CALLER_ID});

        //            this.serverResponse = (Object[])topicRPCClient[topicArrayIndice].call("requestTopic", new Object[]{CALLER_ID,topicNames[topicArrayIndice],"[[TCPROS]]"});
        //            System.out.println("requestTopic length: " + serverResponse.length);
        //System.out.println("requestTopic Object type 1: " + serverResponse[0].getClass().getName());
        // System.out.println("requestTopic Object type 2: " + serverResponse[1].getClass().getName());
        // System.out.println("requestTopic String type 2: " + ((String)serverResponse[1]));
//System.out.println("requestTopic Object type 3: " + serverResponse[2].getClass().getName());
//this.topicServer=((Object[])serverResponse[2]);
//  System.out.println("requestTopic Values 1:" + ((Integer)topicServer[0]).intValue() + COLON + ((String)topicServer[1]) + ": String values:\n");
//      System.out.println("requestTopic String value 1: " + ((String)topicServer[0]));
//      System.out.println("requestTopic String value 2: " + ((String)topicServer[1]));
//      System.out.println("requestTopic Integer value 3: " + ((Integer)topicServer[2]).intValue());
//topicProtocol[topicArrayIndice]=((String)topicServer[0]);
//      topicServerName[topicArrayIndice]=((String)topicServer[1]);
//topicPort[topicArrayIndice]=((Integer)topicServer[2]).intValue();
        //Object[] topicInfo = ((Object[])topicServer[2]);
    }

    /**
     * Add a new subscriber to a Publisher. This method manages juggling of
     * TCPROS, and UDPROS protocols on the same publisher. Due to performance
     * hits of running an extra layer of method calls between 2 different
     * communication objects this method will always start the only called pub
     * protocol, unless multiple protocols exist on the same pub, in which case
     * a Manager class will be uploaded to publisherThreads that will store both
     * UDP and TCP Communicator classes, while, calling a send loop on both
     * protocols synchronously. As soon as protocols revert back to a single
     * protocol the publisherThreads Object will be set back to only that
     * protocol publisher communicator.
     */
    private void joinTCPUDPPublishers(String topic, String topicProtocol) throws RCSMException {
        // get topic indice
        joinTCPUDPPublishers(this.getTopicIndex(topic), topicProtocol);
    }

    /**
     * Add a new UDPROSPublisher subscriber to a Publisher. This method manages
     * juggling of TCPROS, and UDPROS protocols on the same publisher. Due to
     * performance hits of running an extra layer of method calls between 2
     * different communication objects this method will always start the only
     * called pub protocol, unless multiple protocols exist on the same pub, in
     * which case a Manager class will be uploaded to publisherThreads that will
     * store both UDP and TCP Communicator classes, while, calling a send loop
     * on both protocols synchronously. As soon as protocols revert back to a
     * single protocol the publisherThreads Oject will be set back to only that
     * protocol publisher communicator.
     */
    protected void joinTCPUDPPublishers(int topicIndice, String topicProtocol) throws RCSMException {// This method will likely be called from requestTopic in ROSSlave, or ROSNode...
// TODO: improve performance, possibly use globally defined variables.

        // Get the currently set  publisherThreads topic protocol Communicator
        if (publisherThreads[topicIndice] == null) {
            System.out.println("joinTCPUDPPulishers() topic publisher does not exist. Creating publisher for " + topicProtocol + " calling launchPublisher on topic: " + topicNames[topicIndice]);
            // publisherThreads not yet set
            launchPublisher(topicIndice, topicProtocol);
        } else if (publisherThreads[topicIndice].getProtocolName().indexOf("TCPROS") != -1 && publisherThreads[topicIndice].getProtocolName().indexOf("UDPROS") != -1) {
            // Is MultiProtocolPublisherCommunicator
            System.out.println("joinTCPUDPPulishers() MultiProtocolPublisherCommunicator already loaded...");
        } else if (publisherThreads[topicIndice].getProtocolName().indexOf("TCPROS") != -1 && topicProtocol.indexOf(publisherThreads[topicIndice].getProtocolName()) == -1) {
            // Load MultiProtocol, and load UDP, append TCP to Multi, and UDP to Multi
            System.out.println("joinTCPUDPPulishers() Instantiating UDPROSPublisherCommunicator & MultiProtocolPublisherCommunicator. TCPROSPublisherCommunicator already instantiated...");

            try {
                UDPROSPublisherCommunicator joinUDPPublisherCommunicator = new UDPROSPublisherCommunicator(topicNames[topicIndice], true);                    // initialize the Thread
                joinUDPPublisherCommunicator.initialize(topicServerName[topicIndice], 0, topicTCP_Block_Size[topicIndice], "UDPROS_PUB_" + topicNames[topicIndice], topicAlwaysConnected[topicIndice]);
                // Connect and start the thread
                joinUDPPublisherCommunicator.start();
                // Instantiate the MultiProtocolPublisherCommunicator
                MultiProtocolPublisherCommunicator multiProtocolPublisherCommunicator = new MultiProtocolPublisherCommunicator((TCPROSPublisherCommunicator) publisherThreads[topicIndice], joinUDPPublisherCommunicator);
                multiProtocolPublisherCommunicator.start();
                this.publisherThreads[topicIndice] = multiProtocolPublisherCommunicator;
                System.out.println("Transferred Topic: " + topicNames[topicIndice] + " from TCPROS Publishing to Multiprotocol (TCPROS/UDPROS) publisher.");
            } catch (IOException ex) {
                throw new RCSMException("IOException on ROSNode joinTCPUDPPublishers method. ", ex);
            } catch (ClassNotFoundException ex) {
                throw new RCSMException("ClassNotFoundException on ROSNode joinTCPUDPPublishers method. ", ex);
            } catch (InstantiationException ex) {
                throw new RCSMException("InstantiationException on ROSNode joinTCPUDPPublishers method. ", ex);
            } catch (IllegalAccessException ex) {
                throw new RCSMException("IllegalAccessException on ROSNode joinTCPUDPPublishers method. ", ex);
            }
        } else if (publisherThreads[topicIndice].getProtocolName().indexOf("UDPROS") != -1 && topicProtocol.indexOf(publisherThreads[topicIndice].getProtocolName()) == -1) {
            // is UDPROSPublisherCommunicator
            // Load MultiProtocol, and load TCP, append UCP to Multi, and TCP to Multi     
            System.out.println("joinTCPUDPPulishers() Instantiating TCPROSPublisherCommunicator & MultiProtocolPublisherCommunicator. UDPROSPublisherCommunicator already instantiated...");

            // instantiate the TCPROSPublisherCommunicator
            try {
                TCPROSPublisherCommunicator joinTCPPublisherCommunicator = new TCPROSPublisherCommunicator(topicPublisherInitializerMessageHandlerClass[topicIndice], topicPublisherMessageHandlerClass[topicIndice], topicPublisherTunerMessageHandlerClass[topicIndice], topicNames[topicIndice], true);                    // initialize the Thread
                joinTCPPublisherCommunicator.initialize(topicServerName[topicIndice], 0, topicTCP_Block_Size[topicIndice], topicTCP_NoDelay[topicIndice], topicAlwaysConnected[topicIndice], "TCPROS_PUB_" + topicNames[topicIndice]);
                // Connect and start the thread
                joinTCPPublisherCommunicator.start();
                // Instantiate the MultiProtocolPublisherCommunicator
                MultiProtocolPublisherCommunicator multiProtocolPublisherCommunicator = new MultiProtocolPublisherCommunicator(joinTCPPublisherCommunicator, (UDPROSPublisherCommunicator) publisherThreads[topicIndice]);
                multiProtocolPublisherCommunicator.start();
                this.publisherThreads[topicIndice] = multiProtocolPublisherCommunicator;
                System.out.println("Transferred Topic: " + topicNames[topicIndice] + " from UDPROS Publishing to Multiprotocol (TCPROS/UDPROS) publisher.");


            } catch (IOException ex) {
                throw new RCSMException("IOException on ROSNode joinTCPUDPPublishers method. ", ex);
            } catch (ClassNotFoundException ex) {
                throw new RCSMException("ClassNotFoundException on ROSNode joinTCPUDPPublishers method. ", ex);
            } catch (InstantiationException ex) {
                throw new RCSMException("InstantiationException on ROSNode joinTCPUDPPublishers method. ", ex);
            } catch (IllegalAccessException ex) {
                throw new RCSMException("IllegalAccessException on ROSNode joinTCPUDPPublishers method. ", ex);
            }
        }
    }

    /**
     * UDPPublisher requires manual add call to add a subscriber. This is that
     * call.
     */
    protected void addUDPPublisherSubscriber(int topicIndice, String subscriber_hostname, int subscriber_port, int packet_length) throws RCSMException {
        // Get the currently set  publisherThreads topic protocol Communicator
        if (publisherThreads[topicIndice] == null) {
            System.out.println("addUDPPublisherSubscriber() topic publisher does not exist. Creating publisher for " + topicProtocol + " calling launchPublisher on topic: " + topicNames[topicIndice]);
            // publisherThreads not yet set
            launchPublisher(topicIndice, "UDPROS");
            ((UDPROSPublisherCommunicator) publisherThreads[topicIndice]).addSubscriber(subscriber_hostname, subscriber_port, packet_length);
        } else if (publisherThreads[topicIndice].getProtocolName().equals("UDPROS")) {
            ((UDPROSPublisherCommunicator) publisherThreads[topicIndice]).addSubscriber(subscriber_hostname, subscriber_port, packet_length);
        } else if (publisherThreads[topicIndice].getProtocolName().equals("TCPROS")) {
            // initialize the MultiProtocolPublisherCommunicator
            joinTCPUDPPublishers(topicIndice, "UDPROS");
            // Add UDPROS subscriber
            ((MultiProtocolPublisherCommunicator) publisherThreads[topicIndice]).getUDPPublisherCommunicator().addSubscriber(subscriber_hostname, subscriber_port, packet_length);
        }
        System.out.println("addUDPPublisherSubscriber() added subscriber for publisher protocol " + topicProtocol + ",  topic: " + topicNames[topicIndice]);
    }

    /**
     * Remove a subscriber from the UDPPublisherCommunicator.
     */
    protected void removeUDPPublisherSubscriber(int topicIndice, String topic, String subscriber_hostname, int subscriber_port) {
        if (publisherThreads[topicIndice] != null) {
            if (publisherThreads[topicIndice].getProtocolName().equals("UDPROS")) {
                ((UDPROSPublisherCommunicator) publisherThreads[topicIndice]).removeSubscriber(subscriber_hostname, subscriber_port);
            } else if (publisherThreads[topicIndice].getProtocolName().indexOf("UDPROS,TCPROS") != -1) {
                // Add UDPROS subscriber
                ((MultiProtocolPublisherCommunicator) publisherThreads[topicIndice]).getUDPPublisherCommunicator().removeSubscriber(subscriber_hostname, subscriber_port);
            }
            System.out.println("removeUDPPublisherSubscriber() removed subscriber for publisher protocol " + topicProtocol + ",  topic: " + topicNames[topicIndice]);
        }
    }

    /**
     * Return the Parameter Server (used by the Slave API). Not supported.
     */
    public ROSSlaveParameterListener getParameterListener() {
        return paramListener;
    }

    /**
     * Return the ROS Slave Group for the specified Topic Indice (used by the
     * Slave API).
     */
    public ROSSubscriber getROSSlaveGroup(int topicIndice) {
        return topicSlave[topicIndice];
    }
    /**
     * Return the ROS Slave Group for the specified Topic name (used by the
     * Slave API).
     */
    // DEBUG variable
    int index;

    public ROSSubscriber getROSSlaveGroup(String topic) {
// TODO: Remove this DEBUG code.        
        this.index = getTopicIndex(topic);
        //       logger.log(Level.WARNING, "getTopicIndex topic: " + topic + ", index: " + index);
        // End DEBUG
        if (index == -1) {
            logger.log(Level.WARNING, "Topic not supported: " + topic);
            return null;
        } else {
            return topicSlave[index];
        }
    }
    // bus_info contains the Object Array that goes into the response indice [2] 3rd element for Slave getBusInfo method.
    private Object[] bus_stats;

    /**
     * Returns the Bus Stats Object[] for Slave API 3rd element in XML-RPC
     * response.
     */
    public Object[] getBusStatsForSlave(String caller_id) {
        // TODO: implement this method.

        return bus_stats;
    }
    // bus_info contains the Object Array that goes into the response indice [2] 3rd element for Slave getBusInfo method.
    //private int busInfoCount;
    //private List<Object> bus_info;
    private final static String DIRECTION_INPUT = "i";
    private final static String DIRECTION_OUTPUT = "o";
    private final static String DIRECTION_BOTH = "b";
    // TODO: Implement Cache Pool on Object Arrays to improve performance and lower GC.

    /**
     * Returns the Bus Info for Slave API 3rd element in XML-RPC response.
     * Future review to determine how to represent multiple protocols subscribed
     * to a single Publisher. The XMLRPC Slave API seems limited to not be able
     * to support this functionality in this method, even though the ROS may.
     */
    public Object[] getBusInfoForSlave(String caller_id) {
        // Loop through topics
        List<Object> bus_info = new ArrayList<Object>();
        int busInfoCount = 0;
        while (busInfoCount < topicNames.length) {
            // Subscribers
            if (isSubscribed[busInfoCount]) {
                Object[] subBusInfo = new Object[6];
                if (subscriberThreads[busInfoCount] != null) {
                    // Connection ID
                    subBusInfo[0] = Integer.valueOf(busInfoCount);
                    // Destination ID
                    subBusInfo[1] = topicPublisherURLs[busInfoCount];
                    // Direction
                    subBusInfo[2] = DIRECTION_INPUT;
                    // Protocol name
                    subBusInfo[3] = topicProtocol[busInfoCount];
                    // Topic
                    subBusInfo[4] = topicNames[busInfoCount];
                    // Is Connected
                    subBusInfo[5] = Boolean.valueOf(subscriberThreads[busInfoCount].isConnected());
                } else {
                    // Connection ID
                    subBusInfo[0] = Integer.valueOf(busInfoCount);
                    // Destination ID
                    subBusInfo[1] = topicPublisherURLs[busInfoCount];
                    // Direction
                    subBusInfo[2] = DIRECTION_INPUT;
                    // Protocol name
                    subBusInfo[3] = topicProtocol[busInfoCount];
                    // Topic
                    subBusInfo[4] = topicNames[busInfoCount];
                    // Is Connected
                    subBusInfo[5] = Boolean.valueOf(false);
                }
                // Add item to List busInfo.
                bus_info.add(subBusInfo);
            }
            // Publishers
            if (isPublication[busInfoCount]) {
                Object[] pubBusInfo = new Object[6];
                if (publisherThreads[busInfoCount] != null) {
                    // Connection ID
                    pubBusInfo[0] = Integer.valueOf(busInfoCount + topicNames.length);
                    // Destination ID
                    pubBusInfo[1] = topicSubscriberAPIs[busInfoCount];
                    // Direction
                    pubBusInfo[2] = DIRECTION_OUTPUT;
                    // Protocol name
                    pubBusInfo[3] = topicProtocol[busInfoCount];
                    // Topic
                    pubBusInfo[4] = topicNames[busInfoCount];
                    // Is Connected
                    pubBusInfo[5] = Boolean.valueOf(publisherThreads[busInfoCount].isConnected());
                } else {
                    // Connection ID
                    pubBusInfo[0] = Integer.valueOf(busInfoCount + topicNames.length);
                    // Destination ID
                    pubBusInfo[1] = topicSubscriberAPIs[busInfoCount];
                    // Direction
                    pubBusInfo[2] = DIRECTION_OUTPUT;
                    // Protocol name
                    pubBusInfo[3] = topicProtocol[busInfoCount];
                    // Topic
                    pubBusInfo[4] = topicNames[busInfoCount];
                    // Is Connected
                    pubBusInfo[5] = Boolean.valueOf(false);
                }
                // Add item to List busInfo.
                bus_info.add(pubBusInfo);
            }
        }
        return bus_info.toArray();
    }

    /**
     * Update the service APIs data. Call for data refresh. Performs XMLRPC
     * Server calls so use sparingly. Expensive call.
     */
    protected void updateServiceAPIsTableData() throws XmlRpcException, ROSXMLRPCException {
        // Update System State
        updateSystemState(caller_id);
        this.serviceNames = topicSystemStates.getServices();
        //System.out.println("Service names length: " + serviceNames.length);
        this.serviceList = serviceNames.length;
        this.serviceAPIs = new String[serviceList];
        this.serviceCallerAPIs = new String[serviceList];
        this.serviceServer = new String[serviceList];
        this.servicePort = new int[serviceList];
        this.serviceProtocol = new String[serviceList];
        this.isRegisteredService = new boolean[serviceList];
        this.servicePreferredProtocol = new String[serviceList];
        this.servicePersistance = new boolean[serviceList];
        this.serviceMD5Sum = new String[serviceList];
        this.serviceTCP_NoDelay = new boolean[serviceList];
        this.serviceConnectOnStartup = new boolean[serviceList];
        this.serviceTCP_Block_Size = new int[serviceList];
        this.serviceUDPPacketSize = new int[serviceList];
        this.serviceInitializerMessageHandlerClass = new String[serviceList];
        this.serviceMessageHandlerClass = new String[serviceList];
        this.serviceTunerMessageHandlerClass = new String[serviceList];
        this.serviceTopicRegistryDefinitions = new ROSTopicRegistryMessageDefinition[serviceList];

        this.serviceCount = 0;
        while (serviceCount < serviceList) {
            try {
                this.response = lookupService(caller_id, serviceNames[serviceCount]);
                this.status = ((Integer) response[0]).intValue();
                if (status == 1) {
                    this.service_api1 = ((String) response[2]);
                    this.serviceCallerAPIs[serviceCount] = service_api1;
                    // Update the Server, Port, and Protocol information
                    this.tmpIndex = -1;
                    this.tmpIndex2 = -1;
                    this.protocol = EMPTY_STRING;
                    this.protocol = service_api1.substring(0, service_api1.indexOf(PROTOCOL_SEARCH_ID)).toUpperCase();
                    this.server = EMPTY_STRING;
                    this.rpcPort = EMPTY_STRING;
                    this.service_api1 = service_api1.substring(service_api1.indexOf(PROTOCOL_SEARCH_ID) + 3);
                    if ((this.tmpIndex = service_api1.indexOf(COLON)) != -1) {
                        this.server = service_api1.substring(0, tmpIndex);
                        if ((this.tmpIndex2 = service_api1.indexOf(FORWARD_SLASH)) != -1) {
                            this.rpcPort = service_api1.substring(tmpIndex + 1, tmpIndex2);
                        } else {
                            if (tmpIndex > -1 && tmpIndex2 > -1) {
                                System.out.println("HELP " + service_api1 + " " + tmpIndex + " " + tmpIndex2);
                                this.rpcPort = service_api1.substring(tmpIndex + 1, tmpIndex2);
                            } else if (tmpIndex > -1) {
                                this.rpcPort = service_api1.substring(tmpIndex + 1);
                            }
                        }
                    } else {
                        if ((this.tmpIndex2 = service_api1.indexOf(FORWARD_SLASH)) != -1) {
                            this.server = service_api1.substring(tmpIndex, tmpIndex2);
                            this.rpcPort = EMPTY_STRING;
                        } else {
                            this.server = service_api1.substring(0);
                            this.rpcPort = EMPTY_STRING;
                        }
                    }
                    this.serviceServer[serviceCount] = server;
                    this.servicePort[serviceCount] = Integer.valueOf(rpcPort).intValue();
                    this.serviceProtocol[serviceCount] = protocol;
// TODO: Add troubleshooting code in case -1 is returned on getMessageDefinitionByServiceName.
                    this.tempTID = subscriberMessageManager.getTIDByServiceName(serviceNames[serviceCount]);
                    if (tempTID != -1) {
                        this.tempTRMD = (ROSTopicRegistryMessageDefinition)subscriberMessageManager.getTopicRegistryMessageDefinitionByTID(tempTID);
                        this.serviceTopicRegistryDefinitions[serviceCount] = tempTRMD;
                        this.servicePreferredProtocol[serviceCount] = tempTRMD.preferred_protocol;
                        if (tempTRMD.persistant.equals(ONE)) {
                            this.servicePersistance[serviceCount] = true;
                        } else {
                            this.servicePersistance[serviceCount] = false;
                        }
                        this.serviceMD5Sum[serviceCount] = tempTRMD.md5sum;
                        if (tempTRMD.tcp_nodelay.equals(ONE)) {
                            this.serviceTCP_NoDelay[serviceCount] = true;
                        } else {
                            this.serviceTCP_NoDelay[serviceCount] = false;
                        }
                        if (tempTRMD.connect_on_start.equals(ONE)) {
                            this.serviceConnectOnStartup[serviceCount] = true;
                        } else {
                            this.serviceConnectOnStartup[serviceCount] = false;
                        }
                        this.serviceTCP_Block_Size[serviceCount] = Integer.valueOf(tempTRMD.tcp_block_size).intValue();
                        this.serviceUDPPacketSize[serviceCount] = Integer.valueOf(tempTRMD.udp_packet_size).intValue();
                        if (tempTRMD.redirect_class.isEmpty()) {
                            this.serviceMessageHandlerClass[serviceCount] = DEFAULT_MESSAGE_HANDLER_CLASS;
                        } else {
                            this.serviceMessageHandlerClass[serviceCount] = tempTRMD.redirect_class;
                        }
                        // Set the Initializer Message Handler Class.
                        if (tempTRMD.redirect_class_initializer.isEmpty()) {
                            this.serviceInitializerMessageHandlerClass[serviceCount] = DEFAULT_INITIALIZER_MESSAGE_HANDLER_CLASS;
                        } else {
                            this.serviceInitializerMessageHandlerClass[serviceCount] = tempTRMD.redirect_class_initializer;
                        }
                        // Set the Tuner Message Handler Class.
                        if (tempTRMD.redirect_class_tuner.isEmpty()) {
                            this.serviceTunerMessageHandlerClass[serviceCount] = DEFAULT_TUNER_MESSAGE_HANDLER_CLASS;
                        } else {
                            this.serviceTunerMessageHandlerClass[serviceCount] = tempTRMD.redirect_class_tuner;
                        }
                        // Set the tempTRMD to null;
                        this.tempTRMD = null;
                    }

                }
                //System.out.println("Service Name: " + serviceNames[serviceCount] + ", caller API: " + serviceCallerAPIs[serviceCount]);
            } catch (XmlRpcException e) {
                e.printStackTrace();
            } catch (ROSXMLRPCException e) {
                e.printStackTrace();
                //Logger.getLogger(ROSNode.class.getName()).log(Level.SEVERE, null, ex);
            }
            // increment the service count.
            this.serviceCount = serviceCount + 1;
        }
    }
    //   private Socket tmpServer;
    /**
     * Update the UDPROS Protocol Topic Index. For subscribers of UDPROS Topics.
     */
    /*    private void updateXMLRPCUDPROSProtocolIndice(int topicIndice) throws IOException
     {
     int locPort=-1;
     try
     {
     this.tmpServer = new Socket();
     tmpServer.setReuseAddress(true);
     tmpServer.bind(new InetSocketAddress(0));            
     locPort = tmpServer.getLocalPort();
     }
     catch(Exception e)
     {
     logger.log(Level.WARNING,"unable to find a free port for publisher serversocket on topic: " + topicNames[topicIndice], e);            
     }
     finally
     {
     if(tmpServer!=null)
     {
     tmpServer.close();
     }
     this.tmpServer=null;
     }
     }
     */
//TODO: Remove Development code.
    // Development only monitoring
//    private ConnectionMonitor monitor;
    // End Development Code
    // initialize method variables
    private int pid = -1;
    private Object[] types;
    private String[] serviceCallerIDs;
    private int currentIndex;
    private boolean tmpTCP_NoDelay;
    private boolean tmpTCP_KeepAlive;
    private int topicID;
    public SubscriberMessage[] subMessages;
//    private PublisherMessage[] pubMessages;
    public ServiceMessage[] srvMessages;
    private CommunicationSenderInterface[] subscriberThreads;
    private CommunicationSenderInterface[] publisherThreads;
    private CommunicationSenderInterface[] serviceThreads;
    private TCPROSSubscriberCommunicator tmpTCPCommunicator;
    private TCPROSServiceCommunicator tmpTCPServiceCommunicator;
    private UDPROSSubscriberCommunicator tmpUDPCommunicator;
    private TCPROSPublisherCommunicator tmpTCPPublisherCommunicator;
    private UDPROSPublisherCommunicator tmpUDPPublisherCommunicator;
    // initialize method variables
    private boolean isInitialized = false;
    private String[] pubSubTopics;
    private Object[] publishers;
    /**
     * reinitialize is called from inside the initialize method, when a state is
     * reached in startup configuration automation, requiring a reinitialization
     * of updated configuration variables preceding isInitialized=true.
     */
    public int reinitializeCount = 0;

    public void reinitialize() throws RCSMException {
        // reinitialize count must be incremented.
        this.reinitializeCount = reinitializeCount + 1;
        logger.log(Level.INFO, ("Reinitializing RCSM Plugin: " + getName() + "_v" + getVersion()));
//        shutdown();
        initialize();
    }

    /**
     * Initialize must be called after the Object is constructed.
     */
//TODO: Wrap these exceptions in a single RCSMInitializationException, and implement application properties using RCSM Properties instead of Initialization Properties, and Update Logging.    
    public void initialize() throws RCSMException {
        try {
            // Setup the RCSM Plugin properties.
            this.properties = getProperties();
            try {
                // server address
                this.hostname = (String) properties.get("hostname");
            } catch (NullPointerException e) {
                this.hostname = "localhost";
                logger.log(Level.SEVERE, "RCSM ros \"hostname\" property key was not specified for RCSM Plugin Properties. Defaulting to hostname: localhost", e);
            }
            // ROS caller_id
            if ((String) properties.get("caller_id") != null) {
                this.caller_id = (String) properties.get("caller_id");
            } else {
                logger.log(Level.SEVERE, "RCSM ros \"caller_id\" property key was not specified for RCSM Plugin Properties. Defaulting to caller_id: /rmdmia");
                this.caller_id = "/rmdmia";
            }
            try {
                // ROS Master URL
                this.rosMasterURL = new URL((String) properties.get("master_url"));
            } catch (MalformedURLException e) {
                logger.log(Level.SEVERE, "RCSM ros \"master_url\" property key contained a malformed URL in the RCSM Plugin Properties. Please update he master_url with the ROS_MASTER_URI Environment variable. ", e);
            } catch (NullPointerException e) {
                logger.log(Level.SEVERE, "RCSM ros \"master_url\" property key was not specified for RCSM Plugin Properties. Typically this is the ROS_MASTER_URI Environment variable. ", e);
            }
            try {
                // ROS Message Pool Size
                ROSNode.ros_message_pool_size = Integer.valueOf((String) properties.get("ros_message_pool_size"));
            } catch (NumberFormatException e) {
                logger.log(Level.SEVERE, "RCSM ros \"ros_message_pool_size\" property key contained a non-numeric value in the RCSM Plugin Properties. Please update he ros_message_pool_size with a numeric variable. ", e);
            } catch (NullPointerException e) {
                logger.log(Level.SEVERE, "RCSM ros \"ros_message_pool_size\" property key was not specified for RCSM Plugin Properties. ", e);
            }
            try {
                // Attempt to probe for m5dsum on mismatch, and update the md5sum startup configuration.
                ROSNode.attempt_probe_save_on_md5_mismatch = Boolean.parseBoolean((String) properties.get("attempt_probe_save_on_md5_mismatch"));
            } catch (NullPointerException e) {
                logger.log(Level.SEVERE, "RCSM ros \"attempt_probe_save_on_md5_mismatch\" property key was not specified for RCSM Plugin Properties. ", e);
            }
            try {
                // Fill in empty configuration values at startup values include service md5sum, type, req_type, res_type, and message_definition. Implements Usage of client without manual configutation.                
                ROSNode.startup_auto_config = Boolean.parseBoolean((String) properties.get("startup_auto_config"));
            } catch (NullPointerException e) {
                logger.log(Level.SEVERE, "RCSM ros \"startup_auto_config\" property key was not specified for RCSM Plugin Properties. ", e);
            }

            // Set the TCPROS & UDPROS String IDs.
            TCPROS_PROTOCOL[0] = "TCPROS";
            UDPROS_PROTOCOL[0] = "UDPROS";
// TODO: Fix the protocol array size to 1 if a length of 5 doesn't do it. Also, update the dynamic variables....
            //        UDPROS_PROTOCOL[1] = "thyatira";
            //           UDPROS_PROTOCOL[2] = new Integer(20001);
            //           UDPROS_PROTOCOL[3] = HexStringConverter.hexToBytes(HexStringConverter.getHexStringConverterInstance().stringToHex("/rmdmia_udp1"));
            //           UDPROS_PROTOCOL[4] = new Integer(1024);          

            SUPPORTED_PROTOCOLS[0] = TCPROS_PROTOCOL;
            // SUPPORTED_PROTOCOLS[1] = UDPROS_PROTOCOL;
            // Set the UDPROS Preferred Protocol supported protocol.
            SUPPORTED_PROTOCOLS_UDP_PREFERRED[0] = UDPROS_PROTOCOL;
            //SUPPORTED_PROTOCOLS_UDP_PREFERRED[1] = TCPROS_PROTOCOL;

            this.subscriberMessageManager = ROSSubscriberMessageManager.getInstance(caller_id, this);
            this.master = new ROSMaster(rosMasterURL, caller_id);
            // TODO: Remove DEBUG below
            // DEBUG Missing Publishable, or Subscribable Topics
//            System.out.println("getPublishedTopics: " + Arrays.deepToString(master.getPublishedTopics(caller_id, EMPTY_STRING)));
//            System.out.println("getSystemState: " + Arrays.deepToString(master.getSystemState(caller_id)));  
//a
            // End DEBUG
// DEBUG Attempt to load pubs and subs for all topics....
            this.paramListener = new ROSSlaveParameterListener();
            // Only updated on calls to getSystemState() on the Master.
            this.topicSystemStates = new ROSTopicStates();

            // setUp the topic types table.
            getTopicTypes(caller_id);
            // Update service names, and APIs at initialization
            updateServiceAPIsTableData();
            // Update System State
            updateSystemState(caller_id);

// TODO: Test these messages.
//            System.out.println("Calling topicSystemStates.getTopics()");


// TODO: Implement getPublishedTopics, and replace below.
            this.response = master.getPublishedTopics(caller_id, EMPTY_STRING);
            this.publishers = ((Object[]) response[2]);
            // process the getPublishedTopics response
            if (((Integer) response[0]).intValue() == 1) {
//                System.out.println("getPublishedTopics success.");
                this.publisherCount = ((Object[]) response[2]).length;
                this.subscribableTopics = new String[publisherCount];
                this.publishers = ((Object[]) response[2]);
                for (int i = 0; i < this.publishers.length; i++) {
                    this.fields = (Object[]) publishers[i];
                    for (int j = 0; j < fields.length; j++) {
                        this.subscribableTopics[i] = ((String) fields[0]);
                        //this.subscriberTopicTypes[i] = ((String) fields[1]);
                    }
                }
                // System.out.println("getPublishedTopics subscribableTopics[]: " + Arrays.deepToString(subscribableTopics));
            } else {
                // System.out.println("getPublishedTopics fail.");
                // if the Master API getPublisherTopics Fails fall back on error prone method.
                this.subscribableTopics = topicSystemStates.getSubscribable();
            }

            this.publishableTopics = topicSystemStates.getPublishable();
            this.pubSubTopics = topicSystemStates.getTopics();

            //   System.out.println("publishableTopics: " + Arrays.deepToString(publishableTopics));
            //  System.out.println("subscribableTopics: " + Arrays.deepToString(subscribableTopics));                
//            System.out.println("topicSystemStates.getTopics(): " + Arrays.deepToString(topicSystemStates.getTopics()));    
//            for(int i=0;i<topicTypesTest.length;i++)
            //          {
            //            System.out.println("Topic: " + topicTypesTest[i] + ", Type: " + getTopicType(topicTypesTest[i]));
            //      }
            // End DEBUG getTopicType(String topic)

// END DEBUG PUBS/SUBS, if causes problems revert back to getPublishedTopics

            // setUp the ROS Node.
            //this.response = master.getPublishedTopics(caller_id, EMPTY_STRING);
            this.publisherCount = pubSubTopics.length;
            // Set the isPublishable and is Subscribable arrays
            // is a publishable topic
            this.isPublishable = new boolean[publisherCount];
            // is a subscribable topic.
            this.isSubscribable = new boolean[publisherCount];

            //this.publishers = ((Object[]) response[2]);
            // Initialize the topic arrays.
            this.topicNames = new String[publisherCount];
            this.subscriberTopicTypes = new String[publisherCount];
            this.subscriberMessages = new SubscriberMessage[publisherCount];
//            this.publisherMessages = new PublisherMessage[publisherCount];            
            //this.serviceMessages = new ServiceMessage[publisherCount];
            this.topicPublisherURLs = new String[publisherCount];
            this.topicSubscriberAPIs = new String[publisherCount];
            // stores a boolean value of true if the topic indice registered a subscription to the specified topic.
            this.isSubscribed = new boolean[publisherCount];
            // stores a boolean value of true if the topic indice registered a publication to the specified topic.
            this.isPublication = new boolean[publisherCount];
            this.topicProtocol = new String[publisherCount];
            this.topicServerName = new String[publisherCount];
            this.topicSlave = new ROSSubscriber[publisherCount];
            this.topicPort = new int[publisherCount];
            Arrays.fill(this.topicPreferredProtocol = new String[publisherCount], TCPROS);
            // UDPROS requestTopic connection Strings
            this.UDPROS_CONNECTION_ARRAY = new Object[publisherCount];
            this.topicAlwaysConnected = new boolean[publisherCount];
            this.topicMD5Sum = new String[publisherCount];
            this.topicTCP_NoDelay = new boolean[publisherCount];
            this.topicTCP_Block_Size = new int[publisherCount];
            this.topicUDPPacketSize = new int[publisherCount];
            this.topicNegotiatedUDPPacketSize = new int[publisherCount];
            this.topicConnectOnStartup = new boolean[publisherCount];
            this.topicLatching = new boolean[publisherCount];
            this.topicPublisherConnectOnStartup = new boolean[publisherCount];
            this.topicSubscriberMessageHandlerClass = new String[publisherCount];
            this.topicSubscriberInitializerMessageHandlerClass = new String[publisherCount];
            this.topicSubscriberTunerMessageHandlerClass = new String[publisherCount];
            this.topicPublisherMessageHandlerClass = new String[publisherCount];
            this.topicPublisherInitializerMessageHandlerClass = new String[publisherCount];
            this.topicPublisherTunerMessageHandlerClass = new String[publisherCount];
            this.topicRegistryDefinitions = new ROSTopicRegistryMessageDefinition[publisherCount];
            // Register the caller_api here for incoming master requests.
            register_caller_api();
            // process the Pub/Sub Topics response
            this.publisherCount = pubSubTopics.length;
            for (int j = 0; j < pubSubTopics.length; j++) {
                this.topicNames[j] = pubSubTopics[j];
                isPublishable[j] = getIsPublishable(pubSubTopics[j]);
                isSubscribable[j] = getIsSubscribable(pubSubTopics[j]);
                this.subscriberTopicTypes[j] = getTopicType(pubSubTopics[j]);
// TODO: Add troubleshooting code in case -1 is returned on getMessageDefinition by TID.
                this.tempTID = subscriberMessageManager.getTIDByTopicName(topicNames[j]);
                if (tempTID != -1) {
                    this.tempTRMD = (ROSTopicRegistryMessageDefinition)subscriberMessageManager.getTopicRegistryMessageDefinitionByTID(tempTID);
                    this.topicRegistryDefinitions[j] = tempTRMD;
                    this.topicPreferredProtocol[j] = tempTRMD.preferred_protocol;
                    if (tempTRMD.always_connected.equals(ONE)) {
                        this.topicAlwaysConnected[j] = true;
                    } else {
                        this.topicAlwaysConnected[j] = false;
                    }
                    this.topicMD5Sum[j] = tempTRMD.md5sum;
                    if (tempTRMD.tcp_nodelay.equals(ONE)) {
                        this.topicTCP_NoDelay[j] = true;
                    } else {
                        this.topicTCP_NoDelay[j] = false;
                    }
                    if (tempTRMD.connect_on_start.equals(ONE)) {
                        this.topicConnectOnStartup[j] = true;
                    } else {
                        this.topicConnectOnStartup[j] = false;
                    }
                    if (tempTRMD.publisher_connect_on_start.equals(ONE)) {
                        this.topicPublisherConnectOnStartup[j] = true;
                    } else {
                        this.topicPublisherConnectOnStartup[j] = false;
                    }
                    // set the topic TCP Block Size
                    this.topicTCP_Block_Size[j] = Integer.valueOf(tempTRMD.tcp_block_size).intValue();
                    //Set the topic UDP Packet Size.
                    this.topicUDPPacketSize[j] = Integer.valueOf(tempTRMD.udp_packet_size).intValue();
                    // Set the Message Handler Class.
                    if (tempTRMD.redirect_class.isEmpty()) {
                        this.topicSubscriberMessageHandlerClass[j] = DEFAULT_MESSAGE_HANDLER_CLASS;
                    } else {
                        this.topicSubscriberMessageHandlerClass[j] = tempTRMD.redirect_class;
                    }
                    // Set the Initializer Message Handler Class.
                    if (tempTRMD.redirect_class_initializer.isEmpty()) {
                        this.topicSubscriberInitializerMessageHandlerClass[j] = DEFAULT_INITIALIZER_MESSAGE_HANDLER_CLASS;
                    } else {
                        this.topicSubscriberInitializerMessageHandlerClass[j] = tempTRMD.redirect_class_initializer;
                    }
                    // Set the Tuner Message Handler Class.
                    if (tempTRMD.redirect_class_tuner.isEmpty()) {
                        this.topicSubscriberTunerMessageHandlerClass[j] = DEFAULT_TUNER_MESSAGE_HANDLER_CLASS;
                    } else {
                        this.topicSubscriberTunerMessageHandlerClass[j] = tempTRMD.redirect_class_tuner;
                    }

                    // Set the Publisher Message Handler Class.
                    if (tempTRMD.publisher_redirect_class.isEmpty()) {
                        this.topicPublisherMessageHandlerClass[j] = DEFAULT_PUBLISHER_MESSAGE_HANDLER_CLASS;
                    } else {
                        this.topicPublisherMessageHandlerClass[j] = tempTRMD.publisher_redirect_class;
                    }
                    // Set the Publisher Initializer Message Handler Class.
                    if (tempTRMD.publisher_redirect_class_initializer.isEmpty()) {
                        this.topicPublisherInitializerMessageHandlerClass[j] = DEFAULT_PUBLISHER_INITIALIZER_MESSAGE_HANDLER_CLASS;
                    } else {
                        this.topicPublisherInitializerMessageHandlerClass[j] = tempTRMD.publisher_redirect_class_initializer;
                    }
                    // Set the Publisher Tuner Message Handler Class.
                    if (tempTRMD.publisher_redirect_class_tuner.isEmpty()) {
                        this.topicPublisherTunerMessageHandlerClass[j] = DEFAULT_PUBLISHER_TUNER_MESSAGE_HANDLER_CLASS;
                    } else {
                        this.topicPublisherTunerMessageHandlerClass[j] = tempTRMD.publisher_redirect_class_tuner;
                    }
                    // Set the Publisher Latching.
                    if (tempTRMD.latching.equals(ONE)) {
                        this.topicLatching[j] = true;
                    } else {
                        this.topicLatching[j] = false;
                    }

                    // Set the tempTRMD to null;
                    this.tempTRMD = null;
                }
//                    System.out.print(((String)this.fields[0]) + ", type: " +((String)this.fields[1]) + "\n");
            }


// TODO: aaronsc commenting out to try above instead.            
            //         this.paramListener = new ROSSlaveParameterListener();
            // Only updated on calls to getSystemState() on the Master.
            //         this.topicSystemStates = new ROSTopicStates();

            // setUp the topic types table.
            //         getTopicTypes(caller_id);
            // Update service names, and APIs at initialization
            //         updateServiceAPIsTableData();        
// TODO: Uncomment above if change didn't work.


            //////end constructor code now in initialize//////////// begin initialize////////////////////        
            this.pid = -1;
            try {
                this.response = getPid(caller_id);
                this.status = ((Integer) response[0]).intValue();
                if (status == 1) {
                    this.pid = ((Integer) response[2]).intValue();
                }
            } catch (XmlRpcException e) {
                logger.log(Level.WARNING, "Exception thrown obtaining PID", e);
            } catch (ROSXMLRPCException e) {
                logger.log(Level.WARNING, "Exception thrown obtaining PID", e);
            }
            // Write the PID to the log. -1=no PID available.
            logger.log(Level.INFO, "PID: " + pid);

            // Call lookup topic types, then call updateSystemState, the loop lookupService to get list of Services.
            // TODO: Finish the lookupService table code.
            try {
                // Sets up the topic Types table that can be referenced by the lookupService method
                getTopicTypes(caller_id);
                this.types = topicTypeLookup.getTopicTypes();

                // Update System State
                updateSystemState(caller_id);

                this.serviceNames = topicSystemStates.getServices();
                //            System.out.println("Services List: " + Arrays.toString(serviceNames));

                // populate the serviceAPIs array by looping the lookupService - Lookup the ROS RPC Service URI (3rd parameter in register service)
                this.serviceList = serviceNames.length;
                this.serviceAPIs = new String[serviceList];
                this.serviceCallerAPIs = new String[serviceList];
                this.serviceCount = 0;
                while (serviceCount < serviceList) {
                    this.response = lookupService(caller_id, serviceNames[serviceCount]);
                    this.status = ((Integer) response[0]).intValue();
                    if (status == 1) {
                        this.service_api1 = ((String) response[2]);
                        this.serviceCallerAPIs[serviceCount] = service_api1;
                    }
                    //                System.out.println("Service Name: " + serviceNames[serviceCount] + ", caller API: " + serviceCallerAPIs[serviceCount]);
                    // increment the service count.
                    this.serviceCount = serviceCount + 1;
                }

            } catch (XmlRpcException e) {
                throw new RCSMException("Exception Initializing ROSNode... ", e);
            } catch (ROSXMLRPCException e) {
                throw new RCSMException("Exception Initializing ROSNode... ", e);
            }

            // initialize the pub/sub/service threads.
            this.subscriberThreads = new CommunicationSenderInterface[topicNames.length];
            this.publisherThreads = new CommunicationSenderInterface[topicNames.length];
            this.serviceThreads = new CommunicationSenderInterface[serviceNames.length];

            for (int j = 0; j < topicNames.length; j++) {
                try {
                    //                   int topicID=node.registerSubscriber(caller_id, "/clock", "rosgraph_msgs/Clock",node.master_caller_api);
                    if (isSubscribable[j]) {
                        //System.out.println("Calling registerSubscriber on topic: " + topicNames[j]);
                        this.topicID = registerSubscriber(caller_id, topicNames[j], subscriberTopicTypes[j], master_caller_api);
                    }
                    //                   int topicID=node.registerSubscriber(caller_id, "/clock", "rosgraph_msgs/Clock",node.master_caller_api);



// TODO: aaron 2/24/2014 - comment this code back if it causes something to break...
                    //this.topicID = 
                    //if(isPublishable[j]&&topicPublisherConnectOnStartup[j])
                    if (isPublishable[j] && topicPublisherConnectOnStartup[j]) {
                        //System.out.println("Calling registerPublisher on topic: " + topicNames[j]);
                        registerPublisher(caller_id, topicNames[j], subscriberTopicTypes[j], master_caller_api);
                    }

                    //                   System.out.println("Publisher topicID: " + topicID);
                    // Load the topicNames into topic arrays.

                    if (isSubscribable[j]) {
// TODO: if Topic is UDPROS preprocess: UDPROS_CONNECTION_ARRAY for getTopic

                        getTopic(topicID);
                    }
                    if (isSubscribable[j] && !topicConnectOnStartup[j]) {
                        unregisterSubscriber(caller_id, topicNames[j], master_caller_api);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // Initialize & start subscriber messages, and publisher connections that are set to load on start.
            try {

                // TODO: Replace the srvMessages with serviceMessage & subMessages with subscriberMessages.
                this.serviceMessages = subscriberMessageManager.getAvailableServiceMessages();
                this.subscriberMessages = subscriberMessageManager.getAvailableSubscriptionMessages();
//                this.publisherMessages = subscriberMessageManager.getAvailablePublisherMessages();                
                this.subMessages = new SubscriberMessage[topicNames.length];
//                this.pubMessages = new PublisherMessage[topicNames.length];                
                this.srvMessages = new ServiceMessage[serviceNames.length];

                for (int j = 0; j < subscriberMessages.length; j++) {
                    if ((currentIndex = getTopicIndex(subscriberMessages[j].topic)) != -1) {
                        // set the subscriber message into topic names length array here
                        this.subMessages[currentIndex] = subscriberMessages[j];
//                        this.pubMessages[currentIndex] = publisherMessages[j];                        
                        // TODO: replace startsWith("UDPROS") with equals once the actual UDP string is figured out (currently UDPROS).
                        //System.out.println("Subscriber Message index:" + j + " topic protocol:" + topicProtocol[currentIndex]);
                        // TCPROS Subscriber
                        if (topicConnectOnStartup[currentIndex] && topicProtocol[currentIndex] != null && topicProtocol[currentIndex].equals("TCPROS")) {
                            // TCP Subscriber Implementation
                            this.subscriberThreads[currentIndex] = (this.tmpTCPCommunicator = new TCPROSSubscriberCommunicator(topicSubscriberInitializerMessageHandlerClass[currentIndex], topicSubscriberMessageHandlerClass[currentIndex], topicSubscriberTunerMessageHandlerClass[currentIndex], topicNames[currentIndex], true, this));
                            // initialize the Thread
                            tmpTCPCommunicator.initialize(topicServerName[currentIndex], topicPort[currentIndex], topicTCP_Block_Size[currentIndex], topicTCP_NoDelay[currentIndex], topicAlwaysConnected[currentIndex], "TCPROS_SUB_" + topicNames[currentIndex]);
                            if (topicConnectOnStartup[currentIndex]) {
                                if (isSubscribed[currentIndex]) {
                                    // Connect and start the thread
                                    tmpTCPCommunicator.start();
                                    // Send first message with ROS COnnection Header to perform connection handshake - returns response with definition in Connection Header.
                                    tmpTCPCommunicator.send(subMessages[currentIndex].getMessage());
                                }
                            }
                        } else if (topicConnectOnStartup[currentIndex] && topicProtocol[currentIndex] != null && topicProtocol[currentIndex].startsWith("UDPROS")) {
                            // UDP Subscriber Implementation
                            //if subscriberThreads is null instantiate.
                            if (subscriberThreads[currentIndex] == null) {
                                this.subscriberThreads[currentIndex] = (this.tmpUDPCommunicator = new UDPROSSubscriberCommunicator(topicNames[currentIndex], true));
                            }
                            // Initialize UDP Subscriber.
                            if (!subscriberThreads[currentIndex].isRunning()) {
                                tmpUDPCommunicator.initialize(topicServerName[currentIndex], topicPort[currentIndex], topicUDPPacketSize[currentIndex], "UDPROS_SUB_" + topicNames[currentIndex], topicAlwaysConnected[currentIndex], topicSubscriberInitializerMessageHandlerClass[currentIndex], topicSubscriberMessageHandlerClass[currentIndex], topicSubscriberTunerMessageHandlerClass[currentIndex]);
                            }

                            if (topicConnectOnStartup[currentIndex]) {
                                if (isSubscribed[currentIndex]) {
// TODO: Implement Start in UDPROSSubscriber...                                    
                                    // Connect and start the thread
//                                    tmpUDPCommunicator.start();
                                    // ROSUDP sends the Connection header via XMLRPC, and listens, until the Publisher contacts the listener, so send not allowed..
                                }
                            }
                        }

                        if (topicPublisherConnectOnStartup[currentIndex] && topicProtocol[currentIndex] != null) {
                            if (topicProtocol[currentIndex].startsWith("TCPROS")) {
                                // TCP Publisher Implementation
                                this.publisherThreads[currentIndex] = (this.tmpTCPPublisherCommunicator = new TCPROSPublisherCommunicator(topicPublisherInitializerMessageHandlerClass[currentIndex], topicPublisherMessageHandlerClass[currentIndex], topicPublisherTunerMessageHandlerClass[currentIndex], topicNames[currentIndex], true));
                                // initialize the Thread
                                // TODO: Determine if another variable can be defined on a topic by topic basis for Publisher hostname (likely not useful, but may be in a computer with multiple network cards defining multiple host names)...                             
                                tmpTCPPublisherCommunicator.initialize(hostname, 0, topicTCP_Block_Size[currentIndex], topicTCP_NoDelay[currentIndex], topicAlwaysConnected[currentIndex], "TCPROS_PUB_" + topicNames[currentIndex]);
                                if (topicPublisherConnectOnStartup[currentIndex] && !topicNames[currentIndex].equals("/rosout")) {
                                    if (isPublication[currentIndex]) {
                                        // Connect and start the thread
                                        tmpTCPPublisherCommunicator.start();
                                    }
                                }
                            } else if (topicProtocol[currentIndex].startsWith("UDPROS")) {
                                // TODO: Fix possible bug of loading Communicators even though isPublication is false. Also start on UDP Publisher does nothing....
                                // UDP Publisher Implementation
                                this.publisherThreads[currentIndex] = (this.tmpUDPPublisherCommunicator = new UDPROSPublisherCommunicator(topicNames[currentIndex], true));
                                tmpUDPPublisherCommunicator.initialize(topicServerName[currentIndex], topicPort[currentIndex], topicUDPPacketSize[currentIndex], "UDPROS_PUB_" + topicNames[currentIndex], topicAlwaysConnected[currentIndex]);
                                if (isPublication[currentIndex]) {
                                    // Connect and start the thread
                                    tmpUDPCommunicator.start();
                                }
                            }
                        }



                    }
                }
                // TODO: Implement the service code (topic code is done minus send first message to establish connection, and obtaining definition on return data. If definition exists verify it is same, otherwise new data needs to be updated, as well as notifying the operator if the client data schema will change (this is just an idea that may be wrong.).
                for (int j = 0; j < serviceMessages.length; j++) {
                    if ((this.currentIndex = getServiceIndex(serviceMessages[j].service)) != -1 && serviceConnectOnStartup[currentIndex]) {
                        // set the service message into service names length array here
                        this.srvMessages[currentIndex] = serviceMessages[j];
                        // Default to Preferred Protocol on Services due to no protocol to determine which to use. ROS should suport either or (TCP or UDP).
                        if (servicePreferredProtocol[currentIndex].equals("TCPROS")) {
                            this.serviceThreads[currentIndex] = (this.tmpTCPServiceCommunicator = new TCPROSServiceCommunicator(serviceInitializerMessageHandlerClass[currentIndex], serviceMessageHandlerClass[currentIndex], serviceTunerMessageHandlerClass[currentIndex], serviceNames[currentIndex], false, this));
                            // initialize the Thread
                            tmpTCPServiceCommunicator.initialize(serviceServer[currentIndex], servicePort[currentIndex], serviceTCP_Block_Size[currentIndex], serviceTCP_NoDelay[currentIndex], servicePersistance[currentIndex], "TCPROS_SVC_" + serviceNames[currentIndex]);
                            if (serviceConnectOnStartup[currentIndex]) {
                                // Connect and start the thread
                                tmpTCPServiceCommunicator.start();
                                // Send first message with ROS Connection Header to perform connection handshake - returns response with definition in Connection Header. ROS may technically only allow a handshake,  response, then disconnect unless persistent is true.
                                tmpTCPServiceCommunicator.send(srvMessages[currentIndex].getMessage());
                            }
                        } else if (servicePreferredProtocol[currentIndex].startsWith("UDPROS")) {
                            // instantiate serviceThreads[currentIndex] if null. 
                            if (serviceThreads[currentIndex] == null) {
                                this.serviceThreads[currentIndex] = (this.tmpUDPCommunicator = new UDPROSSubscriberCommunicator(serviceNames[currentIndex], false));
                            }
                            // Initialize UDP Subscriber.
                            if (!serviceThreads[currentIndex].isRunning()) {
                                tmpUDPCommunicator.initialize(serviceServer[currentIndex], servicePort[currentIndex], serviceUDPPacketSize[currentIndex], "UDPROS_SVC_" + serviceNames[currentIndex], servicePersistance[currentIndex], serviceInitializerMessageHandlerClass[currentIndex], serviceMessageHandlerClass[currentIndex], serviceTunerMessageHandlerClass[currentIndex]);
                            }

                            if (serviceConnectOnStartup[currentIndex]) {
                                // Connect and start the thread
                                tmpUDPCommunicator.start();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Exception initializing topics and services in ROSNode.", e);
            }
// process missing configuration and auto-populate if missing (applies only to a properties file property startup_auto_config=true)
//            if (startup_auto_config) {
//                processAutoConfiguration();
//            }


        } catch (UnsupportedEncodingException ex) {
            throw new RCSMException("UnsupportedEncodingException on ROSNode initialize() method. ", ex);
        } catch (IOException ex) {
            throw new RCSMException("IOException on ROSNode initialize() method. ", ex);
        } catch (XmlRpcException ex) {
            throw new RCSMException("Unable to connect to ROS Master URL: " + rosMasterURL.toString(), null);
        } catch (ROSXMLRPCException ex) {
            throw new RCSMException("ROSXMLRPCException on ROSNode initialize() method. ", ex);
        }
        // Set isInitialized to true.
        this.isInitialized = true;

        // DEBUG Code for publisherThreads
        //     for(int i = 0;i<publisherThreads.length;i++)
        //     {
        //        System.out.println("Publisher thread " + i + ": " + ((TCPROSPublisherCommunicator)publisherThreads[i]).getReceivePort());
        //    }
    }

    /**
     * Return the Topic Subscriber APIs by topic index.
     */
    public String[] getTopicSubscriberAPIs() {
        return topicSubscriberAPIs;
    }

    /**
     * Return the Topic Publisher URLs by topic index.
     */
    public String[] getTopicPublisherURLs() {
        return topicPublisherURLs;
    }

    /**
     * Launch a ROS Subscriber for the specified topic. Returns false if already
     * registered, or if topic does not exist.
     */
    public boolean launchSubscriber(String topic) throws RCSMException {
        // ROSNode topic indice
        int topicIndice = getTopicIndex(topic);
        // SubscriberMessageManager subscription message array topic indice.
        int subMsgIndice;
        if (isSubscribable[topicIndice]) {
            try {
                if (isSubscribable[topicIndice]) {
                    //System.out.println("Calling registerSubscriber on topic: " + topicNames[j]);
                    //this.topicID = registerSubscriber(caller_id, topicNames[topicIndice], subscriberTopicTypes[topicIndice], master_caller_api);
                    getTopic(registerSubscriber(caller_id, topicNames[topicIndice], subscriberTopicTypes[topicIndice], master_caller_api));
                } else {
                    throw new RCSMException("Topic " + topic + " not subscribable: " + rosMasterURL.toString(), null);
                }
            } catch (XmlRpcException ex) {
                throw new RCSMException("Unable to connect to ROS Master URL: " + rosMasterURL.toString(), null);
            } catch (ROSXMLRPCException ex) {
                throw new RCSMException("ROSXMLRPCException on ROSNode registerSubscriber() method. ", ex);
            } catch (MalformedURLException ex) {
                throw new RCSMException("Unable to connect to ROS Master URL: " + rosMasterURL.toString() + " MalformedURLException.", null);
            } catch (IOException ex) {
                throw new RCSMException("IOException on ROSNode registerSubscriber() method. ", ex);
            }
        }

        // If topicIndice not equal to -1 registerSubscriber, else return false;
        if (subscriberThreads[topicIndice] != null) {
            // Return false id subscriber is already tegistered.
            return false;
        }
        if (topicIndice != -1) {
            if (topicProtocol[topicIndice].equals("TCPROS")) {
                try {
                    // TCP Subscriber Implementation
                    this.subscriberThreads[topicIndice] = (this.tmpTCPCommunicator = new TCPROSSubscriberCommunicator(topicSubscriberInitializerMessageHandlerClass[topicIndice], topicSubscriberMessageHandlerClass[topicIndice], topicSubscriberTunerMessageHandlerClass[topicIndice], topicNames[topicIndice], true, this));
                    // initialize the Thread
                    tmpTCPCommunicator.initialize(topicServerName[topicIndice], topicPort[topicIndice], topicTCP_Block_Size[topicIndice], topicTCP_NoDelay[topicIndice], topicAlwaysConnected[topicIndice], "TCPROS_SUB_" + topicNames[topicIndice]);
// TODO: Research what isSubscribed does here...                    
                    if (isSubscribed[topicIndice]) {
                        // Connect and start the thread
                        tmpTCPCommunicator.start();
                        // Send first message with ROS COnnection Header to perform connection handshake - returns response with definition in Connection Header.
                        tmpTCPCommunicator.send(subMessages[topicIndice].getMessage());

                        return true;
                    }
                } catch (IOException ex) {
                    throw new RCSMException("IOException on ROSNode registerSubscriber(String) method. ", ex);
                } catch (ClassNotFoundException ex) {
                    throw new RCSMException("ClassNotFoundException on ROSNode registerSubscriber(String) method. ", ex);
                } catch (InstantiationException ex) {
                    throw new RCSMException("InstantiationException on ROSNode registerSubscriber(String) method. ", ex);
                } catch (IllegalAccessException ex) {
                    throw new RCSMException("IllegalAccessException on ROSNode registerSubscriber(String) method. ", ex);
                }
            } else if (topicProtocol[topicIndice].startsWith("UDPROS")) {
                try {
                    // UDP Subscriber Implementation

                    if (subscriberThreads[topicIndice] == null) {
                        this.subscriberThreads[topicIndice] = (this.tmpUDPCommunicator = new UDPROSSubscriberCommunicator(topicNames[topicIndice], true));
                    }

                    if (!subscriberThreads[topicIndice].isRunning()) {
                        tmpUDPCommunicator.initialize(topicServerName[topicIndice], topicPort[topicIndice], topicUDPPacketSize[topicIndice], "UDPROS_SUB_" + topicNames[topicIndice], topicAlwaysConnected[topicIndice], topicSubscriberInitializerMessageHandlerClass[topicIndice], topicSubscriberMessageHandlerClass[topicIndice], topicSubscriberTunerMessageHandlerClass[topicIndice]);
                    }

                    if (isSubscribed[topicIndice]) {
                        // Connect and start the thread
                        tmpUDPCommunicator.start();
                        return true;
                    }
                } catch (IOException ex) {
                    throw new RCSMException("IOException on ROSNode registerSubscriber(String) method. ", ex);
                } catch (ClassNotFoundException ex) {
                    throw new RCSMException("ClassNotFoundException on ROSNode registerSubscriber(String) method. ", ex);
                } catch (InstantiationException ex) {
                    throw new RCSMException("InstantiationException on ROSNode registerSubscriber(String) method. ", ex);
                } catch (IllegalAccessException ex) {
                    throw new RCSMException("IllegalAccessException on ROSNode registerSubscriber(String) method. ", ex);
                }
            }
        }
        return false;
    }

    /**
     * Unregister and destroy the Subscriber Object that implements the
     * CommunicatinSenderInterface, and CommunicationReceiverInterface for the
     * specified Subscriber. Set the Sender, and Receiver Objects associated
     * with the Subscriber topic index to null (removing up to 3 in use threads
     * per subscriber).
     */
    public void destroySubscriber(String topic) throws RCSMException {
        // Get the topic indice.
        int topicIndice = getTopicIndex(topic);
        // unregister via XMLRPC Master API
        try {
            unregisterSubscriber(caller_id, topic, master_caller_api);
        } catch (XmlRpcException ex) {
            throw new RCSMException("Unable to connect to ROS Master URL (calling unregisterSubscriber(String): " + rosMasterURL.toString(), null);
        } catch (ROSXMLRPCException ex) {
            throw new RCSMException("ROSXMLRPCException on ROSNode unregisterSubscriber() method. ", ex);
        }
        try {
            subscriberThreads[topicIndice].shutdown();
            subscriberThreads[topicIndice] = null;
        } catch (ArrayIndexOutOfBoundsException e) {
            // do nothing
        } catch (Exception e) {
            //nothing to do
            if (topicIndice > 0 && topicIndice < subscriberThreads.length) {
                subscriberThreads[topicIndice] = null;
            }
        }
    }

    /**
     * Launch a ROS Publisher for the specified topic, that implements the
     * CommunicatinSenderInterface, and CommunicationReceiverInterface for the
     * specified Publisher. Returns false if topic is not defined, or the topic
     * is not publishable.
     */
    public boolean launchPublisher(String topic) throws RCSMException {
        int topicIndice = getTopicIndex(topic);
        return launchPublisher(topicIndice, topicProtocol[topicIndice]);
    }

    /**
     * Launch a ROS Publisher for the specified topic, that implements the
     * CommunicatinSenderInterface, and CommunicationReceiverInterface for the
     * specified Publisher. Returns false if topic is not defined, or the topic
     * is not publishable.
     */
    public boolean launchPublisher(int topicIndice, String topicProtocolName) throws RCSMException {
//        int topicIndice=getTopicIndex(topic);
        try {
            if (isPublishable[topicIndice]) {
                //System.out.println("Calling registerPublisher on topic: " + topicNames[j]);
                registerPublisher(caller_id, topicNames[topicIndice], subscriberTopicTypes[topicIndice], master_caller_api);
            } else {
                // Return false because topic is not Publishable
                return false;
            }
        } catch (XmlRpcException ex) {
            throw new RCSMException("Unable to connect to ROS Master URL (calling registerPublisher(String): " + rosMasterURL.toString(), null);
        } catch (ROSXMLRPCException ex) {
            throw new RCSMException("ROSXMLRPCException on ROSNode registerPublisher() method. ", ex);
        }

        // Implement the PublisherCommunicator Objects.
        if (topicProtocolName.equals("TCPROS") && isPublication[topicIndice]) {
            // TCPROS Publisher Implementation
            try {
                this.publisherThreads[topicIndice] = (this.tmpTCPPublisherCommunicator = new TCPROSPublisherCommunicator(topicPublisherInitializerMessageHandlerClass[topicIndice], topicPublisherMessageHandlerClass[topicIndice], topicPublisherTunerMessageHandlerClass[topicIndice], topicNames[topicIndice], true));
                // initialize the Thread
                tmpTCPPublisherCommunicator.initialize(topicServerName[topicIndice], 0, topicTCP_Block_Size[topicIndice], topicTCP_NoDelay[topicIndice], topicAlwaysConnected[topicIndice], "TCPROS_PUB_" + topicNames[topicIndice]);

                // Connect and start the thread
                tmpTCPPublisherCommunicator.start();
                return true;
            } catch (IOException ex) {
                throw new RCSMException("IOException on ROSNode registerPublisher(String) method. ", ex);
            } catch (ClassNotFoundException ex) {
                throw new RCSMException("ClassNotFoundException on ROSNode registerPublisher(String) method. ", ex);
            } catch (InstantiationException ex) {
                throw new RCSMException("InstantiationException on ROSNode registerPublisher(String) method. ", ex);
            } catch (IllegalAccessException ex) {
                throw new RCSMException("IllegalAccessException on ROSNode registerPublisher(String) method. ", ex);
            }
        } else if (topicProtocolName.startsWith("UDPROS") && isPublication[topicIndice]) {
            // UDPROS Publisher Implementation
            try {
                this.publisherThreads[topicIndice] = (this.tmpUDPPublisherCommunicator = new UDPROSPublisherCommunicator(topicNames[topicIndice], true));
                tmpUDPPublisherCommunicator.initialize(topicServerName[topicIndice], topicPort[topicIndice], topicUDPPacketSize[topicIndice], "UDPROS_PUB_" + topicNames[topicIndice], topicAlwaysConnected[topicIndice]);
                // Connect and start the thread
                tmpUDPPublisherCommunicator.start();
                return true;

            } catch (IOException ex) {
                throw new RCSMException("IOException on ROSNode registerPublisher(String) method. ", ex);
            } catch (ClassNotFoundException ex) {
                throw new RCSMException("ClassNotFoundException on ROSNode registerPublisher(String) method. ", ex);
            } catch (InstantiationException ex) {
                throw new RCSMException("InstantiationException on ROSNode registerPublisher(String) method. ", ex);
            } catch (IllegalAccessException ex) {
                throw new RCSMException("IllegalAccessException on ROSNode registerPublisher(String) method. ", ex);
            }
        }
        return false;
    }

    /**
     * Unregister and destroy the Publisher Object that implements the
     * CommunicatinSenderInterface, and CommunicationReceiverInterface for the
     * specified Publisher. Set the Sender, and Receiver Objects associated with
     * the topic index to null (removing up to multiple in use threads per
     * Publisher).
     */
    public void destroyPublisher(String topic) throws RCSMException {
        // Get the topic indice.
        int topicIndice = getTopicIndex(topic);
        // unregister via XMLRPC Master API
        try {
            unregisterPublisher(caller_id, topic, master_caller_api);
        } catch (XmlRpcException ex) {
            throw new RCSMException("Unable to connect to ROS Master URL (calling unregisterPublisher(String): " + rosMasterURL.toString(), null);
        } catch (ROSXMLRPCException ex) {
            throw new RCSMException("ROSXMLRPCException on ROSNode unregisterPublisher() method. ", ex);
        }
        try {
            publisherThreads[topicIndice].shutdown();
            publisherThreads[topicIndice] = null;
        } catch (ArrayIndexOutOfBoundsException e) {
            // do nothing
        } catch (Exception e) {
            //nothing to do
            if (topicIndice > 0 && topicIndice < publisherThreads.length) {
                publisherThreads[topicIndice] = null;
            }
        }
    }

    /**
     * Launch the Service Object that implements the
     * CommunicationSenderInterface, and CommunicationReceiverInterface, that
     * implements the CommunicatinSenderInterface, and
     * CommunicationReceiverInterface for the specified Service. Returns false
     * if service is not defined.
     */
    public boolean launchService(String service) throws RCSMException {
        //     System.out.println("Calling launchService");
        // Get the service indice
        int serviceIndice = getServiceIndex(service);
        if (serviceIndice == -1) {
            return false;
        }
//        System.out.println("srvMessages.length:" + srvMessages.length + ", serviceMessages.length: " + serviceMessages.length);
        this.srvMessages[serviceIndice] = serviceMessages[((ROSSubscriberMessageManager)getSubscriberMessageManager()).getIndexByServiceName(service)];
        try {
            if (servicePreferredProtocol[serviceIndice].equals("TCPROS")) {
                this.serviceThreads[serviceIndice] = (this.tmpTCPServiceCommunicator = new TCPROSServiceCommunicator(serviceInitializerMessageHandlerClass[serviceIndice], serviceMessageHandlerClass[serviceIndice], serviceTunerMessageHandlerClass[serviceIndice], serviceNames[serviceIndice], false, this));
                // initialize the Thread
                tmpTCPServiceCommunicator.initialize(serviceServer[serviceIndice], servicePort[serviceIndice], serviceTCP_Block_Size[serviceIndice], serviceTCP_NoDelay[serviceIndice], servicePersistance[serviceIndice], "TCPROS_SVC_" + serviceNames[serviceIndice]);

                // Connect and start the thread
                //tmpTCPServiceCommunicator.start();
                tmpTCPServiceCommunicator.disconnect();
                // Send first message with ROS Connection Header to perform connection handshake - returns response with definition in Connection Header. ROS may technically only allow a handshake,  response, then disconnect unless persistent is true.
                //tmpTCPServiceCommunicator.send(srvMessages[serviceIndice].getMessage());
                return true;
            } else if (servicePreferredProtocol[serviceIndice].startsWith("UDPROS")) {
                if (serviceThreads[serviceIndice] == null) {
                    this.serviceThreads[serviceIndice] = (this.tmpUDPCommunicator = new UDPROSSubscriberCommunicator(serviceNames[serviceIndice], false));
                }
                if (!serviceThreads[serviceIndice].isRunning()) {
                    tmpUDPCommunicator.initialize(serviceServer[serviceIndice], servicePort[serviceIndice], serviceUDPPacketSize[serviceIndice], "UDPROS_SVC_" + serviceNames[serviceIndice], servicePersistance[serviceIndice], serviceInitializerMessageHandlerClass[serviceIndice], serviceMessageHandlerClass[serviceIndice], serviceTunerMessageHandlerClass[serviceIndice]);
                }
                if (serviceConnectOnStartup[serviceIndice]) {
                    // Connect and start the thread
                    tmpUDPCommunicator.start();
                }
                // Connect and start the thread
                //tmpUDPCommunicator.start();
                // Send first message with ROS Connection Header to perform connection handshake - returns response with definition in Connection Header.
                //tmpUDPCommunicator.send(subMessages[serviceIndice].getMessage());
                return true;
            }
        } catch (IOException ex) {
            throw new RCSMException("IOException on ROSNode registerService(String) method. ", ex);
        } catch (ClassNotFoundException ex) {
            throw new RCSMException("ClassNotFoundException on ROSNode registerService(String) method. ", ex);
        } catch (InstantiationException ex) {
            throw new RCSMException("InstantiationException on ROSNode registerService(String) method. ", ex);
        } catch (IllegalAccessException ex) {
            throw new RCSMException("IllegalAccessException on ROSNode registerService(String) method. ", ex);
        }
        return false;
    }

    /**
     * Destroy the Service Object. Set the Sender, and Receiver Objects
     * associated with the service index to null (removing up to 3 threads per
     * service).
     */
    public void destroyService(String service) {
        // Get the service indice
        int serviceIndice = getServiceIndex(service);
        try {
            serviceThreads[serviceIndice].shutdown();
            serviceThreads[serviceIndice] = null;
        } catch (ArrayIndexOutOfBoundsException e) {
            // do nothing
        } catch (Exception e) {
            //nothing to do
            if (serviceIndice > 0 && serviceIndice < serviceThreads.length) {
                serviceThreads[serviceIndice] = null;
            }
        }
    }

    /* Returns the CommunicationSenderInterface for the specified service. If the
     * service is null, it will initialize the service Object
     * 
     * @exception RCSMException, if service not found.
     */
    public CommunicationSenderInterface getService(String service) throws RCSMException {
        // Get the service indice
        int serviceIndice = getServiceIndex(service);
        if (serviceIndice < 0 || serviceThreads.length <= serviceIndice) {
            throw new RCSMException("Service not found.", null);
        } else if (serviceThreads[serviceIndice] == null) {
            launchService(service);
        }
        return serviceThreads[serviceIndice];
    }

    /* Returns the CommunicationReceiverInterface for the specified topic subscription. If the
     * topic is null, it will initialize the subscriber Object
     * 
     * @exception RCSMException, if topic not found.
     */
    public CommunicationReceiverInterface getSubscriber(String topic) throws RCSMException {
        // Get the topic indice
        int subscriberIndice = getTopicIndex(topic);
        if (subscriberIndice < 0 || subscriberThreads.length <= subscriberIndice) {
            throw new RCSMException("Topic not found.", null);
        } else if (subscriberThreads[subscriberIndice] == null) {
            launchSubscriber(topic);
        }
        return (CommunicationReceiverInterface) subscriberThreads[subscriberIndice];
    }

    /* Returns the CommunicationSenderInterface for the specified topic publisher. If the
     * topic is null, it will initialize the publisher Object
     * 
     * @exception RCSMException, if topic not found.
     */
    public CommunicationSenderInterface getPublisher(String topic) throws RCSMException {
        // Get the topic indice
        int publisherIndice = getTopicIndex(topic);
        if (publisherIndice < 0 || publisherThreads.length <= publisherIndice) {
            throw new RCSMException("Topic not found.", null);
        } else if (publisherThreads[publisherIndice] == null) {
            launchPublisher(topic);
        }
        return publisherThreads[publisherIndice];
    }
    // TODO: Finish experimental method 
    // TODO: Fix performance issues in calling this method.
    private ROSTopicRegistryMessageDefinition trmd;
    /** Not supported. Is used in populating an empty topic_registry file via a plugin. */
    public void populateTopicRegistry() {
        // Get the registry data.
        Object[][] registryData = getMonitoringInformation();
        // Update the local TopicRegistryDefinition Objects and call add in SubscriberMessageManager,
        for (int i = 0; i < registryData.length; i++) {
            if (registryData[i][11].equals("1")) {
                registryData[i][11] = Boolean.TRUE;
            } else {
                registryData[i][11] = Boolean.FALSE;
            }
            if (registryData[i][12].equals("1")) {
                registryData[i][12] = Boolean.TRUE;
            } else {
                registryData[i][12] = Boolean.FALSE;
            }
            if (registryData[i][13].equals("1")) {
                registryData[i][13] = Boolean.TRUE;
            } else {
                registryData[i][13] = Boolean.FALSE;
            }
            if (registryData[i][14].equals("1")) {
                registryData[i][14] = Boolean.TRUE;
            } else {
                registryData[i][14] = Boolean.FALSE;
            }
            if (registryData[i][15].equals("")) {
                registryData[i][15] = "TCPROS";
            }
            if (registryData[i][16].equals("")) {
                registryData[i][16] = "1024";
            }
            if (registryData[i][17].equals("")) {
                registryData[i][17] = "1024";
            }
            // Topic Message Handlers
            if (registryData[i][18].equals("") && !registryData[i][0].equals("Service")) {
                registryData[i][18] = "org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.DefaultMessageHandler";
            }
            if (registryData[i][19].equals("") && !registryData[i][0].equals("Service")) {
                registryData[i][19] = "org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.DefaultInitializerMessageHandler";
            }
            if (registryData[i][20].equals("") && !registryData[i][0].equals("Service")) {
                registryData[i][20] = "org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.DefaultTunerMessageHandler";
            }
            // Service Message Handlers            
            if (registryData[i][18].equals("") && registryData[i][0].equals("Service")) {
                registryData[i][18] = "org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.DefaultServiceMessageHandler";
            }
            if (registryData[i][19].equals("") && registryData[i][0].equals("Service")) {
                registryData[i][19] = "org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.DefaultServiceInitializerMessageHandler";
            }
            if (registryData[i][20].equals("") && registryData[i][0].equals("Service")) {
                registryData[i][20] = "org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.DefaultServiceTunerMessageHandler";
            }
            if (registryData[i][25].equals("1")) {
                registryData[i][25] = Boolean.TRUE;
            } else {
                registryData[i][25] = Boolean.FALSE;
            }
            if (registryData[i][26].equals("")) {
                registryData[i][26] = "org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.DefaultPublisherMessageHandler";
            }
            if (registryData[i][27].equals("")) {
                registryData[i][27] = "org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.DefaultPublisherInitializerMessageHandler";
            }
            if (registryData[i][28].equals("")) {
                registryData[i][28] = "org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.DefaultPublisherTunerMessageHandler";
            }
            if (registryData[i][29] != null && registryData[i][29].equals("1")) {
                registryData[i][29] = Boolean.TRUE;
            } else {
                registryData[i][29] = Boolean.FALSE;
            }
        }
        // TODO: Loop through each trmd, and call add in SubscriberMessageManager.      
        for (int i = 0; i < registryData.length; i++) {
            this.trmd = new ROSTopicRegistryMessageDefinition();

            // Set topic or service, and the name.
            if (((String) registryData[i][0]).equals("Topic")) {
                trmd.topic = (String) registryData[i][1];
                trmd.service = "";
            } else if (((String) registryData[i][0]).equals("Service")) {
                trmd.topic = "";
                trmd.service = (String) registryData[i][1];
            } else {
                trmd.topic = "";
                trmd.service = "";
            }
            // Set the topic type
            trmd.type = (String) registryData[i][2];
            if (trmd.type == null) {
                trmd.type = EMPTY_STRING;
            }
            // Set the caller id
            trmd.callerid = caller_id;

            // Set the md5sum.
            trmd.md5sum = (String) registryData[i][9];

            ///trmd.definition
            trmd.definition = (String) registryData[i][10];

            // ALWAYS_CONNECTED
            if (((Boolean) registryData[i][13]).booleanValue()) {
                trmd.always_connected = "1";
            } else {
                trmd.always_connected = "0";
            }

            // CONNECT_ON_START
            if (((Boolean) registryData[i][11]).booleanValue()) {
                trmd.connect_on_start = "1";
            } else {
                trmd.connect_on_start = "0";
            }

            // TCP_NODELAY
            if (((Boolean) registryData[i][13]).booleanValue()) {
                trmd.tcp_nodelay = "1";
            } else {
                trmd.tcp_nodelay = "0";
            }
            // Service PERSISTANT
            if (((Boolean) registryData[i][14]).booleanValue()) {
                trmd.persistant = "1";
            } else {
                trmd.persistant = "0";
            }
            // PREFERRED Protocol
            trmd.preferred_protocol = (String) registryData[i][15];
            // TCP Block size
            trmd.tcp_block_size = (String) registryData[i][16];
            // UDP Packet size
            trmd.udp_packet_size = (String) registryData[i][17];
            // Message Handler
            trmd.redirect_class = (String) registryData[i][18];
            // Message Handler Initializer
            trmd.redirect_class_initializer = (String) registryData[i][19];
            // Message Handler Tuner
            trmd.redirect_class_tuner = (String) registryData[i][20];                    // Service Request Type
            trmd.request_type = (String) registryData[i][21];
            // Service Response Type
            trmd.response_type = (String) registryData[i][22];
            // Service Request Definition               
            trmd.request_type_definition = (String) registryData[i][23];
            // Service Response Definition               
            trmd.response_type_definition = (String) registryData[i][24];
            // Publisher Connect on Start
            if (((Boolean) registryData[i][25]).booleanValue()) {
                trmd.publisher_connect_on_start = "1";
            } else {
                trmd.publisher_connect_on_start = "0";
            }
            // Message Handler
            trmd.publisher_redirect_class = (String) registryData[i][26];
            // Message Handler Initializer
            trmd.publisher_redirect_class_initializer = (String) registryData[i][27];
            // Publisher Message Handler Tuner
            trmd.publisher_redirect_class_tuner = (String) registryData[i][28];
            // Publisher Latching
            if (((Boolean) registryData[i][29]).booleanValue()) {
                trmd.latching = "1";
            } else {
                trmd.latching = "0";
            }
            try {

                subscriberMessageManager.add(trmd);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(ROSNode.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        /*try 
         {
         // Update the Service Messages in the SubscriberMessageManager
         this.serviceMessages = subscriberMessageManager.getAvailableServiceMessages();     
         updateServiceAPIsTableData();

         } catch (XmlRpcException ex) {
         Logger.getLogger(ROSNode.class.getName()).log(Level.SEVERE, null, ex);
         } catch (ROSXMLRPCException ex) {
         Logger.getLogger(ROSNode.class.getName()).log(Level.SEVERE, null, ex);
         }*/
    }
// TODO: Remove all development only methods before release.
    Object[] monitorColumnNames = {"Pub/Sub/Svc", "Topic/Service Name", "Type", "Publisher URL", "Subscriber API", "Service Caller API", "Server", "Port", "Protocol", "MD5SUM", "Definition", "Connect on Startup", "Always Connected", "TCP_NODELAY", "SVC Persistant", "Preferred Protocol", "TCP_BLOCK_SIZE", "UDP_PACKET_SIZE", "MessageHandler Class", "Message Handler Initializer Class", "Message Handler Autotuner Class", "Svc Request Type", "Svc Response Type", "SVC Request Definition", "SVC Response Definition", "Pub Connect on Startup", "Pub MessageHandler Class", "Pub Message Handler Initializer Class", "Pub Message Handler Autotuner Class", "Pub Latching"};
    Object[][] monitorData;
    // currentTID used to store the current TID value for verifying int is not -1
    private int currentTID;

    public Object[][] getMonitoringInformation() {
        this.monitorData = new Object[topicNames.length + serviceNames.length][30];
        for (int i = 0; i < topicNames.length; i++) {
            // pub/sub/service type
            this.monitorData[i][0] = "Topic";
            // subscriber topic type
            this.monitorData[i][1] = topicNames[i];
            // subscriberTopicTypes loaded by call to getPublishedTopics
            this.monitorData[i][2] = subscriberTopicTypes[i];
            // topic publisher String URLs
            this.monitorData[i][3] = topicPublisherURLs[i];
            // topic subscriber apis
            this.monitorData[i][4] = topicSubscriberAPIs[i];
            // Service Caller API
            this.monitorData[i][5] = "N/A";
            // Topic Server Name
            this.monitorData[i][6] = topicServerName[i];
            // Topic Port
            this.monitorData[i][7] = topicPort[i];
            // Topic Protocol
            this.monitorData[i][8] = topicProtocol[i];
            // Get the associated topic data from the subscriber message manager.
            if ((this.currentTID = this.subscriberMessageManager.getTIDByTopicName(topicNames[i])) != -1) {
                this.tempTRMD = (ROSTopicRegistryMessageDefinition)subscriberMessageManager.getTopicRegistryMessageDefinitionByTID(currentTID);
            } else {
                this.tempTRMD = new ROSTopicRegistryMessageDefinition();
            }
            // Topic Message MD5SUM
            this.monitorData[i][9] = tempTRMD.md5sum;
            // Topic Message Definition
            this.monitorData[i][10] = tempTRMD.definition;
            // Topic connect on startvvc
            this.monitorData[i][11] = tempTRMD.connect_on_start;
            // Topic Message always connected
            this.monitorData[i][12] = tempTRMD.always_connected;
            // TCPROS TCP_NODELAY
            this.monitorData[i][13] = tempTRMD.tcp_nodelay;
            // Service = Persistant
            this.monitorData[i][14] = tempTRMD.persistant;
            // Topic = Preferred Protocol
            this.monitorData[i][15] = tempTRMD.preferred_protocol;
            // Topic = TCP Array Buffer Block Size
            this.monitorData[i][16] = tempTRMD.tcp_block_size;
            // Topic = UDP Packet Size
            this.monitorData[i][17] = tempTRMD.udp_packet_size;
            // Topic = MessageHandlerInterface class
            this.monitorData[i][18] = tempTRMD.redirect_class;
            // Topic = Redirect class initializer (used to obtain or validate message definition.
            this.monitorData[i][19] = tempTRMD.redirect_class_initializer;                       // Topic = Redirect class tuner (used to automatically tune the TCP Byte array read size/UDP Packet size.
            this.monitorData[i][20] = tempTRMD.redirect_class_tuner;
            // 21, 22, 23, and 24 are Service Specific variables.
            this.monitorData[i][21] = EMPTY_STRING;
            this.monitorData[i][22] = EMPTY_STRING;
            this.monitorData[i][23] = EMPTY_STRING;
            this.monitorData[i][24] = EMPTY_STRING;
            // Topic Publisher Connect on Startup
            this.monitorData[i][25] = tempTRMD.publisher_connect_on_start;
            // Topic Publisher = MessageHandlerInterface class
            this.monitorData[i][26] = tempTRMD.publisher_redirect_class;
            // Topic Publisher = Redirect class initializer (used to obtain or validate message definition.
            this.monitorData[i][27] = tempTRMD.publisher_redirect_class_initializer;                       // Topic Publisher = Redirect class tuner (used to automatically tune the TCP Byte array read size/UDP Packet size.
            this.monitorData[i][28] = tempTRMD.publisher_redirect_class_tuner;
            // Topic Publisher Latching.
            this.monitorData[i][29] = tempTRMD.latching;
        }
        for (int i = topicNames.length; i < monitorData.length; i++) {
            this.serviceLoopMonIndice = i - topicNames.length;
            // pub/sub/service type
            this.monitorData[i][0] = "Service";
            // service name
            this.monitorData[i][1] = serviceNames[serviceLoopMonIndice];
            // service topic - moved to TRMD object post index 8.
//            this.monitorData[i][2] = getTopicType(serviceNames[serviceLoopMonIndice]);
            // topic publisher String URLs
            this.monitorData[i][3] = "N/A";
            // topic subscriber apis
            this.monitorData[i][4] = "N/A";
            // Service Caller API
            this.monitorData[i][5] = serviceCallerAPIs[serviceLoopMonIndice];
            // Topic Server Name
            this.monitorData[i][6] = serviceServer[serviceLoopMonIndice];
            // Topic Port
            this.monitorData[i][7] = servicePort[serviceLoopMonIndice];
            // Topic Protocol
            this.monitorData[i][8] = serviceProtocol[serviceLoopMonIndice];
            // Get the associated service data from the subscriber message manager.
            if ((this.currentTID = this.subscriberMessageManager.getTIDByServiceName(serviceNames[serviceLoopMonIndice])) != -1) {
                this.tempTRMD = (ROSTopicRegistryMessageDefinition)subscriberMessageManager.getTopicRegistryMessageDefinitionByTID(currentTID);
            } else {
                this.tempTRMD = new ROSTopicRegistryMessageDefinition();
            }

            // service topic.
            this.monitorData[i][2] = tempTRMD.type;
            // Topic Message MD5SUM
            this.monitorData[i][9] = tempTRMD.md5sum;
            // Service Message Definition
            this.monitorData[i][10] = tempTRMD.definition;
            // Topic connect on start
            this.monitorData[i][11] = tempTRMD.connect_on_start;
            // Topic Message always connected
            this.monitorData[i][12] = tempTRMD.always_connected;
            // TCPROS TCP_NODELAY
            this.monitorData[i][13] = tempTRMD.tcp_nodelay;
            // Service = Persistant
            this.monitorData[i][14] = tempTRMD.persistant;
            // Service = Preferred Protocol
            this.monitorData[i][15] = tempTRMD.preferred_protocol;
            // Service = TCP Array Buffer Block Size
            this.monitorData[i][16] = tempTRMD.tcp_block_size;
            // Service = UDP Packet Size
            this.monitorData[i][17] = tempTRMD.udp_packet_size;
            // Service = MessageHandlerInterface class
            this.monitorData[i][18] = tempTRMD.redirect_class;
            // Topic = Redirect class initializer (used to obtain or validate message definition.
            this.monitorData[i][19] = tempTRMD.redirect_class_initializer;                       // Topic = Redirect class tuner (used to automatically tune the TCP Byte array read size/UDP Packet size.
            this.monitorData[i][20] = tempTRMD.redirect_class_tuner;
            this.monitorData[i][21] = tempTRMD.request_type;
            this.monitorData[i][22] = tempTRMD.response_type;
            this.monitorData[i][23] = tempTRMD.request_type_definition;
            this.monitorData[i][24] = tempTRMD.response_type_definition;
            // Topic Publisher Connect on Startup
            this.monitorData[i][25] = tempTRMD.publisher_connect_on_start;
            // Topic Publisher = MessageHandlerInterface class
            this.monitorData[i][26] = EMPTY_STRING;
            // Topic Publisher = Redirect class initializer (used to obtain or validate message definition.
            this.monitorData[i][27] = EMPTY_STRING;                       // Topic Publisher = Redirect class tuner (used to automatically tune the TCP Byte array read size/UDP Packet size.
            this.monitorData[i][28] = EMPTY_STRING;
            // Topic Publisher Latching.
            this.monitorData[i][29] = "0";
        }
        this.tempTRMD = null;
        return monitorData;
    }
    // TODO: Remove all development only methods before release.

    public Object[] getMonitoringTableColumnNames() {

        return monitorColumnNames;
    }

    /**
     * Return a reference to the SubscriberMessageManager.
     *
     * @return SubscriberMessageManager
     */
    public SubscriberMessageManager getSubscriberMessageManager() {
        return subscriberMessageManager;
    }

    // TODO: Could cache arrays used in CommunicationSenderInterface lookup. Probably a good idea for performance.
    private CommunicationSenderInterface[] senderServiceArray;
    private CommunicationSenderInterface[] senderPubSubArray;

    /**
     * Return the topic/service name CommunicationSenderInterface
     * Map<String,CommunicationSenderInterface[]>.
     *
     * @return Map<String,CommunicationSenderInterface[]>
     * CommunicationSenderInterface[] length is 1 for a service. Length is 2 for
     * a pub/sub topic, element 0 is Subsciber, and element 1 is Publisher. This
     * method shares looping variables with Sender Lookup Maps with
     * getSenderServiceLookupMap(), getSenderSubscriberLookupMap(),
     * getSenderPublisherLookupMap(), and getSenderLookupMap() (These methods
     * should always be called synchronously,and are not synchronized methods).
     *
     *
     */
    public Map<String, CommunicationSenderInterface[]> getSenderLookupMap() throws RCSMException {
        try {
            // Define a HashMap to the length of topicNames.length + serviceNames.length
            this.senderLookupMap = (Map) new HashMap<String, CommunicationSenderInterface[]>(this.senderLookupElementCount = topicNames.length + serviceNames.length);
            // Set the loop counter to 0.
            this.senderLookupLoopCounter = 0;
            // Populate topics in Map 
            while (senderLookupLoopCounter < topicNames.length) {
                this.senderPubSubArray = new CommunicationSenderInterface[2];
                // Set subscriber
                this.senderPubSubArray[0] = subscriberThreads[senderLookupLoopCounter];
                // Set publisher
                this.senderPubSubArray[1] = publisherThreads[senderLookupLoopCounter];

                senderLookupMap.put(topicNames[senderLookupLoopCounter], senderPubSubArray);
                // iterate the while loop counter.
                this.senderLookupLoopCounter = senderLookupLoopCounter + 1;
            }
            // Set the senderLookupElementCount to to topicNames.length
            this.senderLookupElementCount = topicNames.length;
            // Populate services in Map
            while (senderLookupLoopCounter < senderLookupElementCount) {
                this.currentServiceIndex = senderLookupLoopCounter - topicNames.length;
                this.senderServiceArray = new CommunicationSenderInterface[1];
                // Set service
                this.senderServiceArray[0] = serviceThreads[currentServiceIndex];
                senderLookupMap.put(serviceNames[currentServiceIndex], senderServiceArray);
                // iterate the while loop counter.
                this.senderLookupLoopCounter = senderLookupLoopCounter + 1;
            }
        } catch (Exception e) {
            throw new RCSMException(e);
        }
        // return the Sender Lookup Map
        return senderLookupMap;
    }

    /**
     * Return the topic subscriber name CommunicationSenderInterface
     * Map<String,CommunicationSenderInterface>.This method shares looping
     * variables with Sender Lookup Maps with getSenderServiceLookupMap(),
     * getSenderSubscriberLookupMap(), getSenderPublisherLookupMap(), and
     * getSenderLookupMap() (These methods should always be called
     * synchronously, and are not synchronized methods).
     *
     * @return Map<String,CommunicationSenderInterface> The topic Subscriber.
     *
     */
    public Map<String, CommunicationSenderInterface> getSenderSubscriberLookupMap() throws RCSMException {
        try {
            // Define a HashMap to the length of topicNames.length
            this.senderSubscriberLookupMap = new HashMap<String, CommunicationSenderInterface>(this.senderLookupElementCount = topicNames.length);
            // Set the loop counter to 0.
            this.senderLookupLoopCounter = 0;
            // Populate topics in Map 
            while (senderLookupLoopCounter < topicNames.length) {
                // Set subscriber
                senderSubscriberLookupMap.put(topicNames[senderLookupLoopCounter], subscriberThreads[senderLookupLoopCounter]);
                // iterate the while loop counter.
                this.senderLookupLoopCounter = senderLookupLoopCounter + 1;
            }
        } catch (Exception e) {
            throw new RCSMException(e);
        }
        // return the Sender Subscriber Lookup Map
        return senderSubscriberLookupMap;
    }

    /**
     * Return the topic publisher name CommunicationSenderInterface
     * Map<String,CommunicationSenderInterface>.This method shares looping
     * variables with Sender Lookup Maps with getSenderServiceLookupMap(),
     * getSenderSubscriberLookupMap(), getSenderPublisherLookupMap(), and
     * getSenderLookupMap() (These methods should always be called
     * synchronously, and are not synchronized methods).
     *
     * @return Map<String,CommunicationSenderInterface> The topic Publisher.
     *
     */
    public Map<String, CommunicationSenderInterface> getSenderPublisherLookupMap() throws RCSMException {
        try {
            // Define a HashMap to the length of topicNames.length
            this.senderPublisherLookupMap = new HashMap<String, CommunicationSenderInterface>(this.senderLookupElementCount = topicNames.length);
            // Set the loop counter to 0.
            this.senderLookupLoopCounter = 0;
            // Populate topics in Map 
            while (senderLookupLoopCounter < topicNames.length) {
                // Set publisher
                senderPublisherLookupMap.put(topicNames[senderLookupLoopCounter], publisherThreads[senderLookupLoopCounter]);
                // iterate the while loop counter.
                this.senderLookupLoopCounter = senderLookupLoopCounter + 1;
            }
        } catch (Exception e) {
            throw new RCSMException(e);
        }
        // return the Sender Publisherer Lookup Map
        return senderPublisherLookupMap;
    }

    /**
     * Return the topic service name CommunicationSenderInterface
     * Map<String,CommunicationSenderInterface>. This method shares looping
     * variables with Sender Lookup Maps with getSenderServiceLookupMap(),
     * getSenderSubscriberLookupMap(), getSenderPublisherLookupMap(), and
     * getSenderLookupMap() (These methods should always be called
     * synchronously, and are not synchronized methods).
     *
     * @return Map<String,CommunicationSenderInterface> The topic Services..
     *
     */
    public Map<String, CommunicationSenderInterface> getSenderServiceLookupMap() throws RCSMException {
        try {
            // Define a HashMap to the length of topicNames.length
            this.senderServiceLookupMap = new HashMap<String, CommunicationSenderInterface>(this.senderLookupElementCount = serviceNames.length);
            // Set the loop counter to 0.
            this.senderLookupLoopCounter = 0;
            // Populate topics in Map 
            while (senderLookupLoopCounter < serviceNames.length) {
                // Set publisher
                senderServiceLookupMap.put(serviceNames[senderLookupLoopCounter], serviceThreads[senderLookupLoopCounter]);
                // iterate the while loop counter.
                this.senderLookupLoopCounter = senderLookupLoopCounter + 1;
            }
        } catch (Exception e) {
            throw new RCSMException(e);
        }
        // return the Sender Service Lookup Map
        return senderServiceLookupMap;
    }

    /**
     * Return true if a topic is subscribable (some topics are only subscribable
     * or publishable.
     */
    public boolean getIsSubscribable(String topic) {
        for (int i = 0; i < subscribableTopics.length; i++) {
            if (subscribableTopics[i].indexOf(topic) != -1 && subscribableTopics[i].length() == topic.length()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return true if a topic is publishable (some topics are only subscribable
     * or publishable.
     */
    public boolean getIsPublishable(String topic) {
        for (int j = 0; j < publishableTopics.length; j++) {
            //System.out.println(j + ". publishableTopics[j]" + publishableTopics[j]);
            if (publishableTopics[j].indexOf(topic) != -1 && publishableTopics[j].length() == topic.length()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return the topic name by topic indice.
     *
     * @return String Topic name
     */
    public String getTopicByIndice(int topicIndice) {
        return topicNames[topicIndice];
    }

    /**
     * Return an array of subscriber topic names. Topic sender indexes directly
     * match the index of the getTopicSenders() method for matching a topic name
     * to a returned CommunicationSenderInterface Object. Topic Senders are used
     * to send data to ROS nodes.
     *
     * @return String[] Topic Senders
     */
    public String[] getTopicSenderNames() {
        return topicNames;
    }

    /**
     * Return the service name by service indice.
     *
     * @return String Service name
     */
    public String getServiceByIndice(int topicIndice) {
        return serviceNames[topicIndice];
    }

    /**
     * Return an array of service names. Service sender indexes directly match
     * the index of the getServiceSenders() method for matching a service name
     * to a returned CommunicationSenderInterface Object. Service Senders are
     * used to send data to ROS nodes.
     *
     * @return String[] Service Senders
     */
    public String[] getServiceSenderNames() {
        return serviceNames;
    }

    /**
     * Return an array of publisher topic senders. Publisher topic sender
     * indexes directly match the index of the getTopicSenderNames() method for
     * matching a topic name to this returned CommunicationSenderInterface[].
     * Topic Senders are used to send data to ROS nodes.
     *
     * @return CommunicationSenderInterface[] Topic Senders
     */
    public CommunicationSenderInterface[] getPublisherSenders() {
        return publisherThreads;
    }

    /**
     * Return an array of subscriber topic senders. Topic sender indexes
     * directly match the index of the getTopicSenderNames() method for matching
     * a topic name to this returned CommunicationSenderInterface[]. Topic
     * Senders are used to send data to ROS nodes.
     *
     * @return CommunicationSenderInterface[] Subscriber Topic Senders
     */
    public CommunicationSenderInterface[] getSubscriberSenders() {
        return subscriberThreads;
    }

    /**
     * Return an array of service senders. Service sender indexes directly match
     * the index of the getServiceSenderNames() method for matching a service
     * name to this returned CommunicationSenderInterface[]. Service Senders are
     * used to send data to ROS nodes.
     *
     * @return CommunicationSenderInterface[] Service Senders
     */
    public CommunicationSenderInterface[] getServiceSenders() {
        return serviceThreads;
    }

    /**
     * Send a single service message. This method connects to the ROS Service,
     * sends a message, receives a response, and disconnects. Do not use this
     * method to send persistent service messages, it will disconnect the
     * service immediately after sending the message. This method is may be
     * deprecated, or removed in a future release of the RMDMIA, due to its
     * inconsistency with other topic and service communications. A workaround
     * is to manually call start, send, and disconnect on the
     * CommunicationSenderInterface implementation, every time a service is
     * called.
     *
     * Note: This method does not currently implement a callback, so its likely
     * the disconnect will occur before a message is received. This
     * functionality will be resolved a functional release.
     *
     * @param serviceMessage byte[] the ROS service message.
     * @param service CommunicationSenderInterface the service reference.
     * @throws IOException if I/O error occurs on connection.
     * @throws RCSMException if issue establishing connection (call to service
     * start() method).
     *
     * @exception NullPointerException is thrown if the service was null. Causes
     * include not calling the method launchService(<String>serviceName), if
     * connect on startup is not enabled.
     */
    public synchronized void sendServiceMessage(byte[] serviceMessage, CommunicationSenderInterface service) throws IOException, NullPointerException, RCSMException {
        // TODO: Implement Unpersistant Service Receiver that sends a callback to this message to disconnect, or timeout on timeout, and disconnects on timeout.
        try {
            // TODO: Check if is persistant connection. Otherwise reconnect. Add support for SocketException broken pipe to reattempt connect on exception.
            //service.   
//            if(service.isRunning()&&service.isConnected()&&!service.getSocket().isInputShutdown()&&!service.getSocket().isOutputShutdown()&&service.getSocket().isBound())
//            {
            // service is running and is connected.
            service.send(serviceMessage);
            //          }
            //        else if(!service.isRunning())
            //       {
            // service is not running yet.
            //         service.start();
            //           System.out.println("CALLED SERVICE START");
            //          service.send(serviceMessage);
            //         service.disconnect();
//            }
            //           else
            //         {
            // Service is running, but is disconnected...
            //           System.out.println("SERVICE IS RUNNING BUT DISCONNECTED");                
            //     }
        } catch (NullPointerException e) {
            throw e;
        } catch (java.net.SocketException e) {
            // TODO: implement attempt to reconnect and send.
            // service is not running yet.
            //  System.out.println("Received SocketException, attempting reconnect");
            try {
                //    System.out.println("CALLING SERVICE connect");
                service.connect();
                //  System.out.println("CALLED SERVICE connect");
                service.send(serviceMessage);
                //service.disconnect();
            } catch (Exception ex) {
                // System.out.println("Failed Socket reconnect attempt on SocketException.");
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Return a reference to the caller_id.
     *
     * @return String caller_id
     */
    public String getCallerID() {
        return caller_id;
    }

    @Override
    public double getVersion() {
        return 1.00;
    }

    @Override
    public String getName() {
        return "ros";
    }

    @Override
    public boolean recycle() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /** Return a new ROS InstructionDefinition for RMDMIA instruction handling. */ 
    @Override
    public InstructionDefinition getInstructionDefinition() 
    {
        return new ROSInstructionDefinition(this);
    }
}