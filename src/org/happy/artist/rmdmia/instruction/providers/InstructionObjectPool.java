package org.happy.artist.rmdmia.instruction.providers;

import org.happy.artist.rmdmia.instruction.Instruction;

/** A InstructionObjectPool interface contains Instruction Objects that are pre-cached 
 * to save time on instantiation, and maintain a predictable memory usage.
 * A single Instruction Object Pool will be used across the
 * entire RMDMIA for each instruction type. Every Instruction Object will have a built 
 * in counter for checkins. A single Instruction may be passed and reprocessed multiple 
 * times before being checked back into the pool, and these counters will ensure if 
 * multiple Objects are using the Instruction simultaneously, that it will not get 
 * checked back in until each user calls checkin
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2015 Happy Artist. All rights reserved.
 */
public interface InstructionObjectPool
{
   public void checkin(int instruction_id);
   
   public Instruction checkout();
   
   public void shutdown();
}
