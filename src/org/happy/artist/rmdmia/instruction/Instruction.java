package org.happy.artist.rmdmia.instruction;

/** Instruction abstract class must be extended for all RMDMIA incoming/outgoing
 * instructions. All Instructions are identified by the associated hid.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2014-2015 Happy Artist. All rights reserved.
 */
public abstract class Instruction 
{
    // The instruction must identify the hardwareID the instruction applies to hid(hardware identifier)
    public transient int hid = -1;
    // InstructionPool element id
    public int eid;
    // int in_use - The number of processors using this Instruction. Must be equal to 0 for Object to be recycled and checked back into pool. Default is 0, so can be ignored unless multiple processors will call checkin on the same Object.
    public int in_use=0;
    
    /** The byte[] length of the Instruction Object. Return -1 if not implemented. This method is the most efficient location to determine the output length of the Object for determining the byte[] length of dynamic nested Objects. */ 
    public abstract int length();

    /** . */ //   public abstract void recycle();
//    {
  //      this.eid=-1;
 //       this.bytes=null;
 //   }
}
