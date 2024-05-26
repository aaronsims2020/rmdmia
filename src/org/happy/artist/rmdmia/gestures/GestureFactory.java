package org.happy.artist.rmdmia.gestures;

/**
 * org.happy.artist.rmdmia.rcsm.GestureFactory interface defines a basic Gesture. Each Gesture implementation requires a GestureFactory to instantiate the GesturePool. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2012 Happy Artist. All rights reserved.
 */
public interface GestureFactory 
{
	public Gesture newGesture();
}