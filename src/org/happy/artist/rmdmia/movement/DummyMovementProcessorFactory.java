package org.happy.artist.rmdmia.movement;

/**
 * org.happy.artist.rmdmia.rcsm.DummyMovementProcessorFactory is a dummy class to test the GestureManager. Each MovementProcessor implementation requires a MovementProcessorFactory to instantiate the GesturePool. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2012 Happy Artist. All rights reserved.
 */
public class DummyMovementProcessorFactory implements MovementProcessorFactory
{
  	//TODO: consider adding final to this method. 
	public MovementProcessor newMovementProcessor() 
	{
		return new MovementProcessorImpl();
	}  
}
