package org.happy.artist.rmdmia.perception.engine.sensor;

/**
 * org.happy.artist.rmdmia.rcsm.SensorProcessorFactory interface defines a basic SensorProcessor. Each SensorProcessor implementation requires a SensorProcessorFactory to instantiate the SensorProcessorPool. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2013 Happy Artist. All rights reserved.
 */
public interface SensorProcessorFactory 
{
	public SensorProcessor newSensorProcessor();
}