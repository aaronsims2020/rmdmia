package org.happy.artist.rmdmia.rcsm.providers.ros.client.message;

import org.happy.artist.rmdmia.rcsm.provider.message.TopicRegistryMessageDefinition;

/** ROSTopicRegistryMessageDefinition.java - The Topic Registry MessageDefinition, 
 *  defines numeric variables associated with each TCPROS message Connection 
 *  Header variable types. This class allows usage of the pre-existing movement
 *  processor registry format of #.#, that is a very simple, cheap property 
 *  lookup algorithm. This registry message definition is for topic_registry.eax.
 *
 * @author Happy Artist
 *
 * @copyright Copyright © 2013-2015 Happy Artist. All rights reserved.
 */
public class ROSTopicRegistryMessageDefinition extends TopicRegistryMessageDefinition 
{
//  public final static int TOPIC_0=0;    
//  public final static int MD5SUM_1=1;
//  public final static int TYPE_2=2;  
//  public final static int TCP_NODELAY_3=3; sent from subscriber to publisher on connection.
//  public final static int DEFINITION_4=4;
//  public final static int SERVICE_5=5; 
//  public final static int CONNECT_ON_START_6=6; 
//  public final static int ALWAYS_CONNECTED_7=7; 
//  public final static int PERSISTANT_8=8; used by service client only. If 1 keep connection option.
//  public final static int LATCHING_9=9; - used in Publisher connection headers only
//  public final static int CALLERID_10=10; 
//  public final static int REDIRECT_CLASS_11=11;     
//  public final static int PREFERRED_PROTOCOL_12=12; - The preferred protocol to establish a connection when option is available.
//  public final static int TCP_BLOCK_SIZE_13=13; - The TCP read buffer byte array size.
//  public final static int UDP_PACKET_SIZE_14=14; - The UDP packet size.    
//  public final static int REDIRECT_CLASS_INITIALIZER_15=15; - (Optional) The redirect class initializer. Used in RMDMIA to obtain the ROS Message Definition at startup, and then append to topic registry if not defined, or if it does not match the defined topic definition to return a message to the log that it does not match. Possibly automatically update the definition.
//  public final static int REDIRECT_CLASS_TUNER_16=16; - (Optional) This class auto tunes the ROS Client. Specifically the networking block sizes, and array read byte size.
//  public final static int REQUEST_TYPE_17=17; - (Service) This option describes the Service Request Type.
//  public final static int RESPONSE_TYPE_18=18; - (Service) This option describes the Service Response Type.
//  public final static int REQUEST_TYPE_DEFINITION_19=19; - (Service) This option contains the Service Request Type Definition.
//  public final static int RESPONSE_TYPE_DEFINITION_20=20; - (Service) This option contains the Service Response Type Definition
//  public final static int _PUBLISHER_REDIRECT_CLASS_INITIALIZER_21=21; - (Optional) The publisher redirect class initializer. Used in RMDMIA to obtain the ROS Message Definition at startup, and then append to topic registry if not defined, or if it does not match the defined topic definition to return a message to the log that it does not match. Possibly automatically update the definition.
//  public final static int PUBLISHER_REDIRECT_CLASS_INITIALIZER_22=22; - (Optional) The publisher redirect class initializer. Used in RMDMIA to obtain the ROS Message Definition at startup, and then append to topic registry if not defined, or if it does not match the defined topic definition to return a message to the log that it does not match. Possibly automatically update the definition.
//  public final static int PUBLISHER_REDIRECT_CLASS_TUNER_23=23; - (Optional) This publisher class auto tunes the ROS Client. Specifically the networking block sizes, and array read byte size.   
//  public final static int PUBLISHER_CONNECT_ON_START_24=24;    
    
  // Empty String used for faster Object creation without the requirement to instantiate or define new empty Strings.
  private final static String EMPTY_STRING="";
  private final static String FALSE="0";
  private final static String DEFAULT_BYTE_ARRAY_SIZE="1024";
  public int tid=-1;  
  // if topic has a value service cannot.  
  public String topic=EMPTY_STRING;
  public String md5sum=EMPTY_STRING;
  public String type=EMPTY_STRING;
  // Service specific
  public String request_type=EMPTY_STRING;
  // Service specific
  public String response_type=EMPTY_STRING;  
  // Service specific
  public String request_type_definition=EMPTY_STRING;
  // Service specific
  public String response_type_definition=EMPTY_STRING;  
  public String tcp_nodelay=FALSE;
  // Topic definition
  public String definition=EMPTY_STRING;
  // if service has a value if topic cannot.
  public String service=EMPTY_STRING;
  // as a rule, must connect on startup. Default is false (0).
  public String connect_on_start=FALSE;  
  // as a rule, must connect publisher on startup. Default is false (0).
  public String publisher_connect_on_start=FALSE;    
  // as a rule, must always be connected for robot to function properly. Default is false.
  public String always_connected=FALSE; 
  // as a rule, must connect on startup. Default is false (0).
  public String persistant=FALSE;  
  // as a rule, latching: if '1', indicates that the publisher is sending latched messages. The protocol for exchanging latched. Default is false (0).
  public String latching=FALSE;      
  // The callerid may or may not be stored, adding it now so it won't need to be added later if storage of the callerid is useful in the configuration.
  public String callerid=EMPTY_STRING;  
  // The redirect class is an optional class, that redirects the output to the class specified. This class will need to implement a message redirect interface.
  public String redirect_class=EMPTY_STRING;    
  //The redirect class initializer. Used in RMDMIA to obtain the ROS Message Definition at startup, and then append to topic registry if not defined, or if it does not match the defined topic definition to return a message to the log that it does not match. Possibly automatically update the definition.
  public String redirect_class_initializer=EMPTY_STRING; 
 //This redirect class auto tunes the ROS Client. Specifically the networking block sizes, and array read byte sizes.
  public String redirect_class_tuner=EMPTY_STRING;   
    // The publisher redirect class is an optional class, that redirects the output to the class specified. This class will need to implement a message redirect interface.
  public String publisher_redirect_class=EMPTY_STRING;    
  //The publisher redirect class initializer. Used in RMDMIA to obtain the ROS Message Definition at startup, and then append to topic registry if not defined, or if it does not match the defined topic definition to return a message to the log that it does not match. Possibly automatically update the definition.
  public String publisher_redirect_class_initializer=EMPTY_STRING; 
 //This publisher redirect class auto tunes the ROS Client. Specifically the networking block sizes, and array read byte sizes.
  public String publisher_redirect_class_tuner=EMPTY_STRING;   
  // The preferred protocol to establish a connection when option is available.
  public String preferred_protocol=EMPTY_STRING;   
  // The TCP read buffer byte array size. (tcp block size = data length)
  public String tcp_block_size=DEFAULT_BYTE_ARRAY_SIZE; 
  // The UDP packet size. (udp block size = 16 bytes + data length)
  public String udp_packet_size=DEFAULT_BYTE_ARRAY_SIZE;   
  
  /** Return the Topic name used to identify the Topic in an InstructionDefinition. */ 
  public String getTopicName()
  {
    if(topic!=null&&!topic.isEmpty())
    {
        return topic;
    }
    else if(service!=null&&!service.isEmpty())
    {
        // Add service
        return service;
    }
    else
    {
        return "";
    }
  }

    @Override
    public
    String getTopicType()
    {
        return type;
    }

    @Override
    public
    String getTopicDefinition()
    {
        return definition;
    }
}
