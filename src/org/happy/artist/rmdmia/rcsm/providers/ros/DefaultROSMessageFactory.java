package org.happy.artist.rmdmia.rcsm.providers.ros;

/**
 * org.happy.artist.rmdmia.rcsm.sensor.ros.DefaultROSMessageFactory interface 
 * implementation generates an empty ROSMessage. Each ROSMessage 
 * implementation requires a ROSMessageFactory to instantiate the ROSMessagePool. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2013 Happy Artist. All rights reserved.
 */
public class DefaultROSMessageFactory implements ROSMessageFactory
{

    @Override
    public ROSMessage newMessage() 
    {
        return (ROSMessage)new DefaultROSMessage();
    }
    
}
