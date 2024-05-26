package org.happy.artist.rmdmia.rcsm.providers.ros;

import org.happy.artist.rmdmia.perception.engine.sensor.SensorMessage;
import org.happy.artist.rmdmia.perception.engine.sensor.SensorProcessor;
import org.happy.artist.rmdmia.rcsm.providers.ros.DummyTCPROSSensorMessage;

/** TCPROSSensorProcessorImpl.java - A simple SensorProcessor interface implementation. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2013 Happy Artist. All rights reserved.
 */
public class TCPROSSensorProcessorImpl extends SensorProcessor {
    private SensorMessage message;
    
    @Override
    public void recycle() {
        // set message value to null on recycle.
        this.message = null;
    }

    @Override
    public SensorMessage process(SensorMessage sensorMessage) 
    {
        if(sensorMessage.getClass().getName().equals(DummyTCPROSSensorMessage.class.getName()))
        {
            this.message = ((DummyTCPROSSensorMessage)sensorMessage);
            System.out.println("Processing Dummy TCPROS message...");
        }
        return sensorMessage; 
    }
}
