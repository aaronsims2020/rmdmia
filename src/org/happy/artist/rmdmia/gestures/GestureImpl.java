package org.happy.artist.rmdmia.gestures;

/** GestureImpl.java - A simple Gesture interface implementation. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2012 Happy Artist. All rights reserved.
 */
public class GestureImpl extends Gesture {
    int resetValForTest = 1;
    @Override
    public void recycle() {
        // set value back to zero on recycle.
        this.resetValForTest = 0;
    }
    
}
