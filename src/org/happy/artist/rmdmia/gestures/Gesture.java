package org.happy.artist.rmdmia.gestures;

/** Gesture.java - The Gesture abstract class.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2012 Happy Artist. All rights reserved.
 */
public abstract class Gesture 
{
    public int gestureID = -1;
    // TODO: Make sense of all this code and create Objects. The intended design is
    // to create a Gesture property at initialization that generates a GesturePool 
    // for each Gesture, that populate an array of gestures for each movement of
    // each moveable limb that has a dynamic movement Gesture assigned.
    // Will probably be an abstract class.
    
    // recycle() is called by GesturePool checkin to reset the Object to its initialization state for reuse.
    public void recycle()
    {
        
    }
    
}
