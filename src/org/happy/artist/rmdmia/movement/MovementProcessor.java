package org.happy.artist.rmdmia.movement;

import org.happy.artist.rmdmia.instruction.Instruction;

/** MovementProcessor.java - The MovementProcessor abstract class.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2012-2015 Happy Artist. All rights reserved.
 */
public abstract class MovementProcessor 
{
    // cache pool id
    public int cid = -1;
    // movement processor id
    public int mid = -1;
    // hardware id
    public int hid = -1;
    
    // TODO: Make sense of all this code and create Objects. The intended design is
    // to create a MovementProcessor property at initialization that generates a GesturePool 
    // for each MovementProcessor, that populate an array of gestures for each movement of
    // each moveable limb that has a dynamic movement MovementProcessor assigned.
    // Will probably be an abstract class.
    
    /** each movement instruction processor must implement process to modify a Movement Instruction. */
    public abstract Instruction process(Instruction movementInstruction);
    
    // recycle() is called by GesturePool checkin to reset the Object to its initialization state for reuse.
    public void recycle()
    {
        
    }
    
}
