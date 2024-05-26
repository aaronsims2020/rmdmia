package org.happy.artist.rmdmia.perception.engine.sensor;

/** SensorProcessor.java - The SensorProcessor abstract class.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2013 Happy Artist. All rights reserved.
 */
public abstract class SensorProcessor 
{
    // cache pool id
    public int cid = -1;
    // sensor processor id
    public int sid = -1;
    // hardware sensor id
    public int hid = -1;
    
    // TODO: Make sense of all this code and create Objects. The intended design is
    // to create a SensorProcessor property at initialization that generates a SensorProcessorPool 
    // for each SensorProcessor, that populate an array of sensor processors for each sensor of
    // all sensors.    
    /** each sensor processor must implement process to process a sensor message. In many cases 
     * Sensor Processors may simply write a sensor message to a sensor message queue. 
     * In cases such as Clock updates the data will be written to a transient long variable that is accessed from the entire system.
     */
    public abstract SensorMessage process(SensorMessage message);
    
    // recycle() is called by SensorProcessorPool checkin to reset the Object to its initialization state for reuse.
    public void recycle()
    {
        
    }

}
