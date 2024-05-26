package org.happy.artist.rmdmia.movement;

import org.happy.artist.rmdmia.instruction.Instruction;

/** MovementProcessorImpl.java - A simple MovementProcessor interface implementation. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2012-2015 Happy Artist. All rights reserved.
 */
public class MovementProcessorImpl extends MovementProcessor {
    private DummyMovementInstruction instruction;
    
    @Override
    public void recycle() {
        // set instruction value to null on recycle.
        this.instruction = null;
    }

    @Override
    public Instruction process(Instruction movementInstruction) 
    {
        if(movementInstruction.getClass().getName().equals(DummyMovementInstruction.class.getName()))
        {
            this.instruction = ((DummyMovementInstruction)movementInstruction);
            this.instruction.testValue=777;
        }
        return instruction; 
    }
}
