package org.happy.artist.rmdmia.rcsm.provider.message;

import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.*;

/** TopicRegistryMessageDefinition.java - The Topic Registry MessageDefinition, 
 *  defines numeric variables associated with each TCPROS message Connection 
 *  Header variable types. This class allows usage of the pre-existing movement
 *  processor registry format of #.#, that is a very simple, cheap property 
 *  lookup algorithm. This registry message definition is for topic_registry.eax.
 *
 * @author Happy Artist
 *
 * @copyright Copyright © 2013-2015 Happy Artist. All rights reserved.
 */
public abstract class TopicRegistryMessageDefinition 
{
//  public final static int TOPIC_0=0;    

    
  // Empty String used for faster Object creation without the requirement to instantiate or define new empty Strings.
  private final static String EMPTY_STRING="";
  private final static String FALSE="0";
  public int tid=-1;  
  // if topic has a value service cannot.  
  public String topic=EMPTY_STRING;
  public String type=EMPTY_STRING;
  // Topic definition
  public String definition=EMPTY_STRING;
  public String connect_on_start=FALSE;  
  
  /** Return the Topic name used to identify the Topic in an InstructionDefinition. */ 
  public abstract String getTopicName();
  /** Return Topic Type. */
  public abstract String getTopicType();
  /** Return the Topic Schema Message Definition. */
  public abstract String getTopicDefinition();  
}
