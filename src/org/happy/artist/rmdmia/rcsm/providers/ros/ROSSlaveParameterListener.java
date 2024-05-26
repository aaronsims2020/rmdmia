package org.happy.artist.rmdmia.rcsm.providers.ros;

/** ROSSlaveParameterListener.java - An implementation of the ROS Slave Parameter
 *  Listener for Slave API parameter updates. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2013 Happy Artist. All rights reserved.
 */
public class ROSSlaveParameterListener 
{
    // TODO: Implement This Object.
    // Not implemented.
    // Register a Listener for the specified parameter
    public void registerParameter(String caller_id, String topic, String name)
    {
        
    }
    
    // TODO: Implement a usable update method...
    public void update(String caller_id, String topic, String name, Object value)
    {
        System.out.println("Update Parameter (not supported): caller_id=" + caller_id + ", topic=" + topic + ", key=" + name + ", value=" + value);
    }
    
}
