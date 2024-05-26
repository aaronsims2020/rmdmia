package org.happy.artist.rmdmia.gestures;

import org.happy.artist.rmdmia.configuration.InitializationProperties;

/** GesturePool.java - High Performance Object Pool used by the GestureManager 
 * for super fast access to reusable Gesture Objects. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2012 Happy Artist. All rights reserved.
 */
public class GesturePool 
{
    // The availabilityArray specifies whether or not a gesture Object is available to be checked out.
    private final boolean[] availabilityArray;  
    // The gestureArray is an array of instantiated and recycled Gesture Objects, associated by index to the gestureArray for fast lookup of Gesture Objects that are avaiable to checkout via the availability array.	
    private final Gesture[] gestureArray; 
    private int checkoutCount = 0;    
    // GESTURE_POOL_SIZE The size of the Gesture Pool. This is a fixed pool size.
    private final int GESTURE_POOL_SIZE=InitializationProperties.GESTURE_POOL_SIZE;
    // TODO: Likely need to remove static instances for multiple running GesturePools to function.
    private static GesturePool gesturePool;   
    // TODO: Figure out if a GestureThreadPool even makes sense to implementor, and performance.
    // TODO: Implement shutdown method.
    /**
     * org.happy.artist.rmdmia.rcsm.GesturePool constructor.
     * 
     *  Pre-generate Gesture Objects in an Array (Theoretical maximum number of Gestures should be: the number of maximum Gestures per second * timeout seconds, reality, is based on System memory usage and power consumption (Need to revisit this later on in performance testing)). 
     *  Create a second Array the same length that each Object is checked in on finish. The second counter counts checkins 
     *  (which will be the amount of objects in the first Array). Continue switching Checkout and checkin Arrays.  
     *  Use two counters - one for incrementing element index in array on checkout and one for counting element check-in index 
     *  for the second array.
     *
     *  @param gestureFactory
     *         The ThreadFactory from the concurrency API was used for convenience as an Object Factory interface. Will likely be replaced.
     */

    // TODO: revert back to private if getInstance() implementation is completed.
    // TODO: Probably will replace the Pool size as a constructor input parameter for the multiple variations in each Gesture.
    public GesturePool(GestureFactory gestureFactory)
    {
        this.availabilityArray = new boolean[GESTURE_POOL_SIZE];
        this.gestureArray = new Gesture[GESTURE_POOL_SIZE];		
        while(checkoutCount<gestureArray.length)
        {
            this.availabilityArray[checkoutCount]=true;
            this.gestureArray[checkoutCount]=(Gesture) gestureFactory.newGesture();
            this.gestureArray[checkoutCount].gestureID = checkoutCount;
            this.checkoutCount=checkoutCount + 1;
        }
        this.checkoutCount = 0;
    }
	
    // TODO: Only implementable if a single GesturePool is used for all Gestures. Implement later...
	// Return the singleton instance of GesturePool.
//	public final static GesturePool getInstance()
//	{
//		if(gesturePool!=null)
//		{
//			return GesturePool.gesturePool;
//		}
//		return GesturePool.gesturePool = new GesturePool(new GestureThreadFactory());
//	}
	
    // Return null if no resources are available. Suggested action retry.
    public Gesture checkout()
    {
        switch(checkoutCount)
        {
            case GESTURE_POOL_SIZE:
                this.checkoutCount=0;
            default: 
                while(checkoutCount<gestureArray.length)
                {

                    if(this.availabilityArray[checkoutCount])
                    {
                        // current Count is costing 10 to 13 ms
//						this.checkedOutCount = this.checkedOutCount + 1;
                        this.availabilityArray[checkoutCount]=false;
                        return (Gesture)gestureArray[(this.checkoutCount = checkoutCount + 1)-1];
                    }
                    this.checkoutCount = checkoutCount + 1;
                }
            return null;
        }

    }	

    // Checkin the Gesture Object for recycling. No synchronized blocks are included, because the 
    // checkout mechanism will skip to the next array element if the current element is not true.
    public void checkin(final int gestureID)
    {
        GesturePool.this.gestureArray[gestureID].recycle();
        availabilityArray[gestureID]=true;
    }

}
