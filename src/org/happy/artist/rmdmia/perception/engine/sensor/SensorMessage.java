package org.happy.artist.rmdmia.perception.engine.sensor;

/** SensorMessage.java - The SensorMessage abstract class. The SensorMessage is a bridge between Proprietary Message formats, and the RMDMIA Sensor Processing system via the SensorProcessor that must be implemented to process Sensor Messages.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2013 Happy Artist. All rights reserved.
 */
public abstract class SensorMessage 
{
    // The sensor message must identify the sensor hardware ID the message applies to hid(hardware identifier) resolves the 
    public transient int hid=-1;
}
