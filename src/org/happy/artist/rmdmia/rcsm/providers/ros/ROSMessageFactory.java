package org.happy.artist.rmdmia.rcsm.providers.ros;

/**
 * org.happy.artist.rmdmia.rcsm.sensor.ros.ROSMessageFactory interface 
 * defines a ROSMessage. Each ROSMessage implementation requires a 
 * ROSMessageFactory to instantiate the ROSMessagePool. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2012-2013 Happy Artist. All rights reserved.
 */
public interface ROSMessageFactory 
{
    public ROSMessage newMessage();
}