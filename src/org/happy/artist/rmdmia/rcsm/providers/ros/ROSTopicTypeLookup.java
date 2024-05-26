package org.happy.artist.rmdmia.rcsm.providers.ros;

/** ROSTopicTypeLookup.java - Maintains a key/value pair lookup for topics and topic types. 
 *  Updates are called by the XMLRPC call to getTopicTypes().
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2013 Happy Artist. All rights reserved.
 */
public class ROSTopicTypeLookup 
{
    private Object[] topics;
    private Object[] topicTypes;
    private Object[] keyValuePair;
    private int currentIndice;
    /** Pass in the initial key/value pair Object[]. */
    public ROSTopicTypeLookup(Object[] initialKeyValuePairArray)
    {
        if(initialKeyValuePairArray!=null)
        {
            this.topics=new Object[initialKeyValuePairArray.length];
            this.topicTypes=new Object[initialKeyValuePairArray.length];
            for(int i =0;i<initialKeyValuePairArray.length;i++)
            {
                this.keyValuePair=(Object[])initialKeyValuePairArray[i];
                this.topics[i]=keyValuePair[0];
                this.topicTypes[i]=keyValuePair[1];       
            }
        }
    }
    
    /** Returns the topic type by topic name. Returns null if topic not found. */
    public String getTopicType(String topic)
    {
        // reset the while loop counter
        this.currentIndice=0;
        while(currentIndice<topics.length)
        {
            if(topics[currentIndice].equals(topic))
            {
                return (String)topicTypes[currentIndice];
            }
            this.currentIndice=currentIndice + 1;
        }
        return null;        
    }
    
 //   public void add(String topic, String topicType)
 //   {
        
 //   }
    
 //   public void remove(String topic)
 //   {
        
 //   }    
    
    /** Returns an Object[] of Strings. */
    public Object[] getTopics()
    {
        return topics;
    }

    /** Returns an Object[] of Strings. */    
    public Object[] getTopicTypes()
    {
        return topicTypes;
    }    
}
