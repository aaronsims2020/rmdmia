package org.happy.artist.rmdmia.instruction;

import org.happy.artist.rmdmia.instruction.providers.InstructionObjectPool;

/** A InstructionInputHandler Interface is to convert an incoming SensorMessage
 *  to an hid associated Instruction.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2014-2015 Happy Artist. All rights reserved.
 */
public interface InstructionInputHandler 
{
    /** Return the Instruction from an incoming byte[]. */
    public Instruction transform(byte[] message);   

    /** Register an InstructionHandler, that will send the Instruction to its next destination, 
     * after the byte[] conversion  to Instruction is completed. If not registered process will call
     * checkin on the Instruction Object.
     */
    public void registerInstructionHandler(InstructionHandler handler);
    
    /** Return boolean is InstructionHandler Registered.
     * 
     * @return boolean 
     */
    public boolean isRegistered();
    
    /** Return the Registered InstructionHandler. Returns null if an InstructionHandler is not Registered. */
    public InstructionHandler getRegisteredInstructionHandler();
    
    /** Return the InstructionObjectPool associated with the InstructionInputHandler implementation. Return null, if it is not implemented in the InstructionInputHandler. */
    public InstructionObjectPool getInstructionObjectPool();
        
}
