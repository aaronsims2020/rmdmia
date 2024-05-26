package org.happy.artist.rmdmia.rcsm.providers.ros.client.sensor;

import org.happy.artist.rmdmia.Controller;
import org.happy.artist.rmdmia.perception.engine.sensor.SensorProcessor;
import org.happy.artist.rmdmia.perception.engine.sensor.SensorProcessorFactory;

/**
 * ROSBagSensorProcessorFactory generates SensorProcessor Objects that write 
 * incoming messages to a rosbag file. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2014 Happy Artist. All rights reserved.
 */

public class ROSBagSensorProcessorFactory implements SensorProcessorFactory
{
    // Reference to the RMDMIA Controller.
    private Controller controller;
    public ROSBagSensorProcessorFactory(Controller controller)
    {
        // Get the ROSNode from RCSMManager.
        this.controller = controller;
    }
    
    @Override
    public SensorProcessor newSensorProcessor() 
    {
        return new ROSBagSensorProcessor(controller);
    }
    
}
