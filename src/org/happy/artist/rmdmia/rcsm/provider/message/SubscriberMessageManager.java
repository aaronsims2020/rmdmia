package org.happy.artist.rmdmia.rcsm.provider.message;

import java.io.UnsupportedEncodingException;

/**
 * org.happy.artist.rmdmia.rcsm.provider.message.SubscriberMessageManager class 
 * manages initialization of messages at startup, storing message definitions 
 * during runtime, and storing updated message data not available at startup 
 * like message connection header message definition. 
 * 
 * topic_registry.eax key values are as follows:
 *  #.0=<topic>
 *  #.1=<var>
 *  #.2=<var2>
 *  ...
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2013-2015 Happy Artist. All rights reserved.
 * 
 */

public interface SubscriberMessageManager
{
       /** Add/update a topic registry message definition (thread-safe). Returns boolean true on success. A return of false means the topic_registry.eax file could not be written,  and changes will only apply in the running instance. */
    public boolean add(TopicRegistryMessageDefinition definition) throws UnsupportedEncodingException;
    
    // Return topic registry message definitions.
    public TopicRegistryMessageDefinition[] getTopicDefinitions();
    
    /** Returns the TopicRegistryMessageDefinition Object associated with the tid. */    
    public TopicRegistryMessageDefinition getTopicRegistryMessageDefinitionByTID(int tid);
    
    /** Returns the index associated with the topic name in the SubscriberMessages array. Returns -1 if no topic name is associated with the topic name input parameter. */
    public int getIndexByTopicName(String topicName);    
    
    /** Returns the tid associated with the Topic name, or Service name. Returns -1 if no tid is associated with the specified topic or service name. */
    public int getTIDByTopicName(String topicName);    
 
    /** Remove a topic registry message definition (thread-safe). Returns boolean true on success. A return of false means the topic_registry.eax file could not be written,  and changes will only apply in the running instance. */
    public boolean remove(TopicRegistryMessageDefinition definition) throws UnsupportedEncodingException;    
    
    
}
