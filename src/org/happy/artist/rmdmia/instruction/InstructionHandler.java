package org.happy.artist.rmdmia.instruction;

/** A InstructionHandler Interface to process the Instruction. Primary use 
 * is forwarding an instruction to next destination.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2015 Happy Artist. All rights reserved.
 */
public interface InstructionHandler
{
    /** Process the Instruction. */
    public void process(Instruction instruction);
}
