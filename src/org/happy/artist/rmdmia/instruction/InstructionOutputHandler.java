package org.happy.artist.rmdmia.instruction;

import org.happy.artist.rmdmia.instruction.providers.InstructionObjectPool;

/** A InstructionOutputHandler Interface is to convert an Instruction 
 * to the associated output format via a byte[].
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2014-2015 Happy Artist. All rights reserved.
 */
public interface InstructionOutputHandler 
{
    /** Return the byte[] to send through the RCSM. */
    public byte[] transform(Instruction instruction);
    
     /** Send the message directly to the RCSM, and checkin the Instruction 
     *  to the Instruction Object Pool if the pool is not null, if pool 
     *  is null set the Object to null. 
     * 
     * @param send_instruction
     */
    public void send(Instruction send_instruction);

     /** process the instruction via the InstructionHandler. If the Handler 
      * is not registered, the default instruction handler will checkin the Instruction 
      * to the Instruction Object Pool. 
     * 
     * @param process_instruction
     */
    public void process_handler(Instruction process_instruction);    
    
    /** Set boolean isRecording. */
    public void setIsRecording(boolean isRecording);

    /** Return is recording. */
    public boolean getIsRecording();
    
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
    
    /** Return the InstructionObjectPool associated with the InstructionOutputHandler implementation. Return null, if it is not implemented in the InstructionOutputHandler. */
    public InstructionObjectPool getInstructionObjectPool();    
}
