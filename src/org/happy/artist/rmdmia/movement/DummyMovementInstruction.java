/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.happy.artist.rmdmia.movement;

import org.happy.artist.rmdmia.instruction.Instruction;

/**
 * org.happy.artist.rmdmia.rcsm.DummyMovementInstruction is a dummy class to test 
 * the MovementProcessorManager. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2012-2015 Happy Artist. All rights reserved.
 */
public class DummyMovementInstruction extends Instruction
{
    public int testValue = -1;
    // pass in a unique hardware id for the controller to modify (remember the id must be registered in the initialized MovementProcessorManager).
    public DummyMovementInstruction(int hid)
    {
        this.hid = hid;
    }

    @Override
    public
    int length()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
