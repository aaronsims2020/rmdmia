package org.happy.artist.rmdmia.gestures;

/**
 * org.happy.artist.rmdmia.rcsm.DummyGestureFactory is a dummy class to test the GestureManager. Each Gesture implementation requires a GestureFactory to instantiate the GesturePool. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2012 Happy Artist. All rights reserved.
 */
public class DummyGestureFactory implements GestureFactory
{
  	//TODO: consider adding final to this method. 
	public Gesture newGesture() 
	{
		return new GestureImpl();
	}  
}
