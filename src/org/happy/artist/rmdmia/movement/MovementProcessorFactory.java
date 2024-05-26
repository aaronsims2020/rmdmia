package org.happy.artist.rmdmia.movement;

/**
 * org.happy.artist.rmdmia.rcsm.MovementProcessorFactory interface defines a basic MovementProcessor. Each MovementProcessor implementation requires a MovementProcessorFactory to instantiate the GesturePool. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2012 Happy Artist. All rights reserved.
 */
public interface MovementProcessorFactory 
{
	public MovementProcessor newMovementProcessor();
}