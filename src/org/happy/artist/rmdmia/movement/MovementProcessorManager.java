package org.happy.artist.rmdmia.movement;

// TODO: Ultimate TODO determine how ROS and Gazebo send multiple movements in messages. emulate that here.

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.happy.artist.rmdmia.instruction.Instruction;


// Performance is easy to integrate once the method of message passing is known, and its sort of a waste
// of time to try and create a base optimization without knowing the method being used.
// Are we modifying the movements, or modifying an array of data each being a different movement formula?

/** MovementProcessorManager.java - Movement Processor Manager Object Prototype.
 * Movement Processor ents algorithmic movement control for movement 
 * calibration and movement based event launching. Algorithms built into movement 
 * processors are intended to increase/decrease numeric input parameters for 
 * movement updates. Modifying the movements in a movement processor manner 
 * is not efficient and in a later release the architecture of the RMDMIA will 
 * need to be re-architected. 
 * 
 * The Movement Processing Manager Object will support the following functions 
 * to support managing movement processors (planned to be used by Gestures):
 *  - add
 *  - remove
 *  - hold
 *  - continue 
 *  - start 
 *  - stop
 *  - recycle
 * 
 * A movement processor interface must be defined for movement processor 
 * implementers to define processing rules for a test method to determine if the 
 * current registered processor will modify data, call a gesture, or perform 
 * an action. Movement Processors are intended to be used for calibrating movements.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2012-2015 Happy Artist. All rights reserved.
 */
public class MovementProcessorManager 
{
    // TODO: convert ints to byte, and/or byte[] where performancee can be improved.
    
    // TODO: Post MovementProcessorManager implementation should all be implemented in a single MovementProcessorPool Array instead of multiple with grouping ranges. It is expected to perform at much higher efficiency then multiple MovementProcessorPool Object instatiations. 
    // MovementProcessorRegistry class is implemented by the MovementProcessorManager 
    // to manage registered Movement Processors by designated package name, movement processor  quantity, 
    // and associated robot function grouping for MovementProcessorPool initialization in the MovementProcessorManager.
    private final static MovementProcessorRegistry registry = MovementProcessorRegistry.getInstance();
    // Define the MovementProcessorPool Object
    public static MovementProcessorPool pool;
    // Object Pool Range for each Factory (i.e. number of Factory Objects for each mid).
    // TODO: Add this variable to the Application Properties.
    private static int MOVEMENT_PROCESSOR_OBJECTS_PER_RANGE = 10;
    // MovementProcessorFactory[] for initializing the MovementProcessorPool.
    private MovementProcessorFactory[] factoryArray;
    
    // The movement processor id arrays. 
    // TODO: Resolve technical debate of having two arrays one for ids, and one for ids and status. The primary difference is an additional element for the status. Will do some performance tests later to determine whether one or multiples performs better, but in terms of the methods to return the current array multiple is better, to avoid processing a new array to give a fast readable list to process, and since multidimensional arrays are so much more processor intensive it makes sense to keep them separate... Ultimately it will depend on whether or not the array return methods are used frequently by other methods, and since this is not a multithreaded Object, the fast return will ensure the timing remains accurate vs, waiting for a looping operation to finish creating an array. To many unknowns about the Robot OS to know yet...
    // TODO: Technically, it makes more sense to run the processing system from two seperate arrays that are synced up instead of one 2 dimensional array because we know the array length will always be two, and the efficiency of two arrays is highly greater. The most complicated problem to solve is usability for code users, whom may find 3 arrays confusing as opposed to simply obtaining a Collection, which usability wise is much simpler. Since this is not subject to usability and rather the competition is about performance and energy efficiency the more efficient non user friendly implementation makes the most sense.
    private int[][] processingMovementProcessorIDs;
    private int[][] registeredMovementProcessorIDs;
    private int[][] registeredMovementProcessorIDsStatus;  
    
    // TODO: Figure this out...
    // 3 arrays required for processing runtime status.
    private int[] hardwareIDs;
    private int[] movementProcessorIDs;
    private boolean[] holdStatus;
    private Class[] movementProcessorFactoryClasses;
    // TODO: Implement LAST - MovementProcessor array will be used when process is called. That said, a more complicated dynamic system will likely be used so this will be the last step. This is due to using Object Cache Pools, and ultimately this will make a huge efficiency difference in a threaded model where a thread executes each movement processor step separately, not likely unless its a synchonrous executor, but that is most likely unless movement processing tasks are so processor intensive they need to be skipped for a next step that completes quicker.......
    // Movement Processing Array contains only the processing Movement Processors and not the on hold processors.
    private MovementProcessor movementProcessingArray[];
    
    // keys array used by constructor to assign the movement processor keys for class initialization of pre-configured movement processor class files.
    // Note: Did not assign as final due to the expectation that this will eventually be a live system, and if the properties file is read while the system is live it will never be allowed to reload or update the array with updated movement Processors via the Properties file.
    private Object[] keys;
    // splitting properties keys at startup use this String[]
    private String[] keySplitArray;
    // splitting properties values at startup use this String[]
    private String[] valueSplitArray;    
    // while loop is always fewer CPU operations then a for loop. Also, pre-initializing the counter for loop variables is an efficiency increase.
    private int keyCount;
    
    // variables used by the getNextMovementIDByHardwareID method a private method called from inside the add method.
    private int hardwareIDCount;
    // hardwareID while loop counter.
    private int hardwareIDLoopCount;
    // variables used add method
    private int newMovementProcessorID;
    
    // variables used by getAllIndexesLinkedToHardwareID & remove method (will allow reuse of retIndexArray since remove is a synchronized method - another of those power saving tweaks... theoretically the method could be a void call that internal methods access this array after calling(actually may do that for efficiency purposes... Not very user friendly code...) ).
    private int[] retIndexArray;
    // Remove loop counter for while loop in the remove method.
    private int removeLoopCount;
    // variables used by removeProcessingArrayElementsByIndexes method
    private int indexToWriteCount;
    private int indexMarker;  
    // A reusable array in the remove emthod for a single movement processor so I can be lazy and reuse the multiple element array remove method code.
    private int[] removeValueArray = new int[1];
    
    // Variables used by get method
    private int midIndex;
    
    // Used by process method
    private Instruction movementInstruction;
    private MovementProcessor movementProcessor;
    private int processLoopCount = 0;
//    private boolean isHardwareIDLoopComplete=false;
    
    /**
     * 
     */
    public MovementProcessorManager()
    {
        // TODO: This can only cover reprocessed movements, and not movements that will not b changed.
        // TODO: Add movement Processor ID Descriptors (consider date later on).
        // TODO: Movement identifier Array
     // 1. Identifier:  hardwareID1, hardwareID2, hardwareID3, hardwareID4, 
     // 2. Quantity:  processorQuantityHWID1, processorQuantityHWID2, processorQuantityHWID3, processorQuantityHWID4
 
// TODO: Initialization
        // 1 read Registry, and load classes/Objects/associated ids.
        // Read the Movement Processor Registry keys for a loop that will initialize all the movement processor classes based on key values.  
        // The properties reader will read the key in the format of hardwareID.movementprocessorID where " is the last period in the property key. Its very likely if a single property file is used, or multiple grouped that a pre identifier word will be used (i.e. - hardware group id, precededed even by a component id). This iniital implementation will simply use the last ".". The value will be the class name package path, and a comman will terminate the class name and be a following value. In this case initially it will be a is holding value for initialization.
        // SortedEAProperties instance sorts the keys Array for the process lookup algorithm that maintains the hardwareID movement processor count, and groups them together by quantity at which time the while loop stops when the movement processor total is acheived. Improves performance on all but the last element in the array. More frequently accessed hardwareIDs should be lowest numbers, and least frequently accessed index numbers should be the highest elements.
        this.keys = MovementProcessorManager.registry.getProperties().keySet().toArray();
//        System.out.println("Sorted movement processor ids: " + Arrays.toString(keys));
        // Initialize the movementProcessorFactoryClasses Array.
        this.movementProcessorFactoryClasses = new Class[MovementProcessorManager.registry.getProperties().size()];
        // initialize hardwareIDs.
        this.hardwareIDs = new int[keys.length];
        // initialize movementProcessorIDs.
        this.movementProcessorIDs = new int[keys.length];
        this.keyCount=0;
        
        while(keyCount<keys.length)
        {
            // TODO: note the processorID may be better off first if movement processors must be run on all movements, never likely so probably just a note.
            // Load the Movement Processors Arrays
            this.keySplitArray=((String)keys[keyCount]).split("\\.");
            if(keySplitArray.length==2)
            {
                hardwareIDs[keyCount]=new Integer(keySplitArray[0]).intValue();
                movementProcessorIDs[keyCount]=new Integer(keySplitArray[1]).intValue();
            }
            // get the key value
            this.valueSplitArray=MovementProcessorManager.registry.get(((String)keys[keyCount])).split(",");
            if(valueSplitArray.length==1)
            {
                try 
                {
//     System.out.println("Count: " + keyCount + ",value: " + valueSplitArray[0] + " Class" + Class.forName(valueSplitArray[0]));
                    // add the class name of the movement processor
                    movementProcessorFactoryClasses[keyCount]=Class.forName(valueSplitArray[0]);
                } 
                catch (ClassNotFoundException ex) 
                {
                    System.out.println("MovementProcessorFactory class specified in properties file not found: " + valueSplitArray[0]);
                    Logger.getLogger(MovementProcessorManager.class.getName()).log(Level.SEVERE, null, ex);
                }                  
            }
            else if(valueSplitArray.length==2)
            {
                try 
                {
                    // add the class name of the movement processor
                    movementProcessorFactoryClasses[keyCount]=Class.forName(valueSplitArray[0]);
                    holdStatus[keyCount]=Boolean.parseBoolean(valueSplitArray[1]);
                } 
                catch (ClassNotFoundException ex) 
                {
                    System.out.println("MovementProcessor class specified in properties file not found: " + valueSplitArray[0]);
                    Logger.getLogger(MovementProcessorManager.class.getName()).log(Level.SEVERE, null, ex);
                }                 
            }
            else
            {
                // no idea what this is need to add support I guess.
                System.out.println("This string not supported in movement processor properties for key: " + MovementProcessorManager.registry.get(((String)keys[keyCount])));                
            }
            // Remember to increment the count. Always use  i = i + 1 for primitive thread safety. ++ is not thread safe.
            this.keyCount = keyCount + 1;
        }
        // reset keyCount back to 0
        this.keyCount=0;

        // Create the MovementProcessorFactory Array for MovementProcessorPool initialization, by initializing the movementProcessorFactoryClasses into an array.
        this.factoryArray = new MovementProcessorFactory[movementProcessorFactoryClasses.length];

        while(keyCount<movementProcessorFactoryClasses.length)
        {
            try 
            {
                factoryArray[keyCount]=(MovementProcessorFactory) movementProcessorFactoryClasses[keyCount].newInstance();
            } 
            catch (InstantiationException ex) 
            {
                ex.printStackTrace();
                Logger.getLogger(MovementProcessorManager.class.getName()).log(Level.SEVERE, null, ex);
            } 
            catch (IllegalAccessException ex) 
            {
                ex.printStackTrace();
                Logger.getLogger(MovementProcessorManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        // Remember to increment the count. Always use  i = i + 1 for primitive thread safety. ++ is not thread safe.
        this.keyCount = keyCount + 1;            
        }
        // reset keyCount back to 0
        this.keyCount=0;
        try
        {
        // Initialize the MovementProcessorPool
        MovementProcessorManager.pool = new MovementProcessorPool(factoryArray,MovementProcessorManager.MOVEMENT_PROCESSOR_OBJECTS_PER_RANGE);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }  
    }
    
    // TODO: Determine best algorithm for Pre-generate Movement Processor Objects in an Array (Theoretical maximum number of Gestures should be: the number of maximum Gestures per second * timeout seconds, reality, is based on System memory usage and power consumption (Need to revisit this later on in performance testing)).
 
    // TODO: Add Hardware Groupings that can contain a group of hardware IDs for the same movement group, or many, whatever works, but it is logical to group hardware together, especially if they work together. Even if just for logical grouping of a human interface to be able to specify movement areas with language...

    // TODO:  (Note not in this class - just a note for RMDMIA) Add alias indexes to associate grouping identifiers with alias words, i.e. key=legs, value = hardwareid1, hardwareid2, and so on. 
   
    // TODO: on final non-prototype implementation use byte instead of int where a total collection is less then 128, and consider byte[] for collections smaller than an int byte length.
    
    // Call the process method on the movement processor to update the output data on that movement processor.
    /** Process the Instruction[] and return the calibrated movement 
     *  instructions. Note: calibrated instructions are intended to be sent 
     *  immediately for the RCSM to be sent in an output message to the hardware 
     *  controller. 
     * 
     * @param movementInstructions - The movement instruction array to be 
     * processed.
     */
    public Instruction[] process(Instruction[] movementInstructions)
    {
        // Set the current value to false for isHardwareIDLoopComplete used to stop while loop on sorted hardwareIDs on complete.
     //   this.isHardwareIDLoopComplete=false;
        // TODO: Implement a more efficient mechanism to obtain movement processor ids for each hardware id.
        //TODO: Iterate through Movement Processors, then output message data to message output.
        this.processLoopCount=0;
        while(processLoopCount<movementInstructions.length)
        {
            this.movementInstruction = movementInstructions[processLoopCount];
                    //pool.checkout(i);
            // Count the number of registered movement processors with the specified hardwareID
            this.hardwareIDLoopCount=0;
//try
//{
            while(hardwareIDLoopCount<hardwareIDs.length)
            {
                if(hardwareIDs[hardwareIDLoopCount]==movementInstruction.hid)
                {
                   // this.isHardwareIDLoopComplete=true;
                    // TODO: process instruction here
                    this.movementProcessor=pool.checkout(hardwareIDLoopCount);
                    movementInstructions[processLoopCount]=movementProcessor.process(movementInstruction);
                    pool.checkin(movementProcessor.cid);
                }
 //               else if(isHardwareIDLoopComplete)
 //               {
                    // end while loop if isHardwareIDLoopComplete==true, and hardwareIDs[hardwareIDLoopCount]!=movementInstruction.hid
  //                  if((hardwareIDLoopCount + 1)<hardwareIDs.length&&hardwareIDs[hardwareIDLoopCount]!=movementInstruction.hid)
 //                   {
   //                     this.hardwareIDLoopCount =  hardwareIDs.length;
                        // Reset the loop complete for next iteration if 
   //                     this.isHardwareIDLoopComplete=false;
   //                 }
   //             }
                // increment the loop count
                this.hardwareIDLoopCount = hardwareIDLoopCount + 1;
            }
            // Perform processing on current hardwareID movement processor[]
//}
//catch(Exception e)
//{
//    System.out.println("hardwareIDs array: " + hardwareIDs);
//    System.out.println(e.getMessage());
//    e.printStackTrace();
//}
        // Iterate the movementInstructions loop count.
        this.processLoopCount = processLoopCount + 1;
        }
        return movementInstructions;
    }
    
    /** Process the Instruction and return the calibrated movement 
     *  instructions. Note: calibrated instructions are intended to be sent 
     *  immediately for the RCSM to be sent in an output message to the hardware 
     *  controller. 
     * 
     * @param movementInstruction - The movement instruction to be 
     * processed.
     */
    public Instruction process(Instruction movementInstruction)
    {
        // Set the current value to false for isHardwareIDLoopComplete used to stop while loop on sorted hardwareIDs on complete.
     //   this.isHardwareIDLoopComplete=false;
        // TODO: Implement a more efficient mechanism to obtain movement processor ids for each hardware id.

        // Count the number of registered movement processors with the specified hardwareID
        this.hardwareIDLoopCount=0;
//try
//{
        while(hardwareIDLoopCount<hardwareIDs.length)
        {
            if(hardwareIDs[hardwareIDLoopCount]==movementInstruction.hid)
            {
               // this.isHardwareIDLoopComplete=true;
                // TODO: process instruction here
                this.movementProcessor=pool.checkout(hardwareIDLoopCount);
                movementInstruction=movementProcessor.process(movementInstruction);
                pool.checkin(movementProcessor.cid);
            }
            // increment the loop count
            this.hardwareIDLoopCount = hardwareIDLoopCount + 1;
        }
            // Perform processing on current hardwareID movement processor[]
//}
//catch(Exception e)
//{
//    System.out.println("hardwareIDs array: " + hardwareIDs);
//    System.out.println(e.getMessage());
//    e.printStackTrace();
//}
        // Iterate the movementInstructions loop count.
        return movementInstruction;
    }    

    // Fully implemented.
    /** Add movement processor by hardware id. Note: Hardware IDs must be manually registered/defined, and can be virtual, logical, or physical hardware groupings (implementor specific). If a hardware ID was not pre-created the specified input will be created as a new hardware ID on the fly. A return value of -1 means an error occurred.
     * @param hardwareID 
     * @param movementProcessor
     * @return int movement processor ID. -1 an error occurred.
     */
    public synchronized int add(int hardwareID, MovementProcessorFactory movementProcessor)
    {
        // Design decision note: Its decided to make the movement processor id for properties file iterative, based on preceding number of items already defined for that particular hardware id starting with 0. This properties file movement processor id is in no way related to the API id variable on movement processors that is used by the movement processor cache pool.
        // Return new movement processor ID.
        // Add the new movement processor to the registry...
        MovementProcessorManager.registry.put(String.valueOf(hardwareID) + "." + String.valueOf(this.newMovementProcessorID=getNextMovementIDByHardwareID(hardwareID)), movementProcessor.getClass().getCanonicalName());
        this.hardwareIDs = Arrays.copyOf(hardwareIDs, hardwareIDs.length + 1);
        this.movementProcessorIDs = Arrays.copyOf(movementProcessorIDs, movementProcessorIDs.length + 1);
        this.holdStatus = Arrays.copyOf(holdStatus, holdStatus.length + 1); 
        this.movementProcessorFactoryClasses = Arrays.copyOf(movementProcessorFactoryClasses, movementProcessorFactoryClasses.length + 1);         
        hardwareIDs[hardwareIDs.length - 1]=hardwareID;
        movementProcessorIDs[movementProcessorIDs.length - 1]=newMovementProcessorID;
        holdStatus[holdStatus.length - 1]=false;
        movementProcessorFactoryClasses[movementProcessorFactoryClasses.length - 1]=movementProcessor.getClass();        
       return newMovementProcessorID; 
    } 

    // Returns a new movement processor id for the properties file naming, and identification programmatically inside the RMDMIA system using an int identifier (could be byte identifier). Specifically this method is called by the add method.
    private int getNextMovementIDByHardwareID(int hardwareID)
    {
        // Count the number of registered movement processors with the specified hardwareID
        // reset the hardwareIDCount to 0
        this.hardwareIDCount=0;
        this.hardwareIDLoopCount=0;
        while(hardwareIDLoopCount<hardwareIDs.length)
        {
            if(hardwareIDs[hardwareIDLoopCount]==hardwareID)
            {
                this.hardwareIDCount = hardwareIDCount + 1;
            }
            // increment the loop count
            this.hardwareIDLoopCount = hardwareIDLoopCount + 1;
        }
        // This is not an effective way to do this if more than a few movement processor IDs exist that were manually choosen and did not begin with 0, but it works, and it is what it is:)
        // loop until a valid unused movement processor id is found.
        while(!validateUnusedMovementProcesserID(hardwareID, hardwareIDCount))
        {        
            this.hardwareIDCount = hardwareIDCount + 1;
        }
        return hardwareIDCount;
    }
    
    // A function used by getNextMovementIDByHardwareID(int hardwareID) to obtain a valid new movement processor id
    private boolean validateUnusedMovementProcesserID(int hardwareID, int newMovementProcessorID)
    {
        // Reset the hardwareIDLoopCount for another looping.
        this.hardwareIDLoopCount=0;
        // verify the new hardwareID is not being used, if it is iterate to the next number and try that one.
        while(hardwareIDLoopCount<hardwareIDs.length)
        {
            if(hardwareIDs[hardwareIDLoopCount]==hardwareID)
            {
                if(movementProcessorIDs[hardwareIDLoopCount]==newMovementProcessorID)
                {
                    return false;
                }
            }
            // increment the loop count
            this.hardwareIDLoopCount = hardwareIDLoopCount + 1;            
        }        
        return true;
    }
    
    // Update the private array variable retIndexArray all element indexes linked by hardwareID. This method was code reviewed to ensure it will work as intended.
    private void updateArrayElementIndexesLinkedToHardwareID(int hardwareID)
    {
        // Count the number of registered movement processors with the specified hardwareID
        // create a copy of the hardwareID array, and assign it to the retIndexArray, the hardwareIDs array was only chosen because its guaranteed to have enough elements to not have an arrayindexoutofboundsexception, I assumed it would be faster to make a copy then initialize a new array but might be wrong and it might make sense later to test the timings to see which is faster (I assumed the copyOf method might be faster then initializing an int array from scratch might be a poor assumption). Did this rather then loop through the variables twice, and could do it in a single loop on the array then use Arrays copyTo to copy just the section of the array with the data that was added.
        this.retIndexArray = Arrays.copyOf(hardwareIDs,hardwareIDs.length);
        // reset the hardwareIDCount to 0
        this.hardwareIDCount=0;
        this.hardwareIDLoopCount=0;
        while(hardwareIDLoopCount<hardwareIDs.length)
        {
            if(hardwareIDs[hardwareIDLoopCount]==hardwareID)
            {
                // add the element index value (this is a common index shared amongst at least 3 processing arrays) to the retIndexArray first so hardwareIDCount can be used as the index without neededin to do a -1 on every index operation, index value assigned is the hardwareIDLoopCount.
                this.retIndexArray[hardwareIDCount]=hardwareIDLoopCount;
                // increment the hardwareIDCount after retIndexArray index update for storage of the hardwareID elements indexing
                this.hardwareIDCount = hardwareIDCount + 1;
            }
            // increment the loop count
            this.hardwareIDLoopCount = hardwareIDLoopCount + 1;
        }
        this.retIndexArray = Arrays.copyOf(retIndexArray,hardwareIDCount);        
    }
    
    // A method used by the get/remove method to obtain the common array index of the specified hardwareID, and movementProcessorID. Return -1 if the index cannot be found - likely due to not being a valid hardwareID/movementProcessorID.
    private int getMovementProcessorIndex(int hardwareID, int movementProcessorID)
    {
        this.hardwareIDLoopCount=0;
        while(hardwareIDLoopCount<hardwareIDs.length)
        {
            if(hardwareIDs[hardwareIDLoopCount]==hardwareID&&movementProcessorIDs[hardwareIDLoopCount]==movementProcessorID)
            {
                return hardwareIDLoopCount;
            }
            // increment the loop count
            this.hardwareIDLoopCount = hardwareIDLoopCount + 1;
        }    
        return -1;
    }  
    
    // This helper method was added to assist in the multi element removal from multiple arrays. There is not clean way to do this using primitive arrays, and this is a necessary evil to obtain the mind blowing performance gain the processor will have over using something like Java Collections Arrays. The gets alone will be blown away in the number of gets that will be called for each single process call. Good programming decision for efficiency and energy consumption reduction, bad decision if a run of the mill programmer is evaluating the code. This is very specialized code, for winning the DRC, not making coders happy.
    // Note about Java 6+ int, boolean, byte arrays are so well optimized it does not make sense to use system level array shifting, due to the actual latency on the call to the system itself taking longer then manually performing several hundred iterations on int arrays.
    private void removeProcessingArrayElementsByIndexes(int[] indexesToRemove)
    {        
        // Sort the indexesToRemove Array to avoid overwriting values that may be added later.
        Arrays.sort(indexesToRemove);
        // Use the indexToWrite int variable to mark the index to write the current loop iteration element.
        this.indexToWriteCount=0;
        // Use the indexMarker int to mark the current indexesToRemove element to check against.
        this.indexMarker=0;
        // Reset the removeLoopCount to 0.
        this.removeLoopCount=0;       
        while(removeLoopCount<hardwareIDs.length)
        {
            // Test the indexesToRemove to determine if the current Iteration must be copied.
            if(removeLoopCount!=indexesToRemove[indexMarker])
            {
                hardwareIDs[indexToWriteCount]=hardwareIDs[removeLoopCount];
                movementProcessorIDs[indexToWriteCount]=movementProcessorIDs[removeLoopCount];
                holdStatus[indexToWriteCount]=holdStatus[removeLoopCount];        
                movementProcessorFactoryClasses[indexToWriteCount]=movementProcessorFactoryClasses[removeLoopCount];
                this.indexToWriteCount=indexToWriteCount+1;
            }
            else
            {
                // increment to the next index to delete.
                this.indexMarker=indexMarker + 1; 
            }
            // increment the while loop count
            this.removeLoopCount=removeLoopCount + 1;            
        }
        this.hardwareIDs = Arrays.copyOf(hardwareIDs, hardwareIDs.length - indexesToRemove.length);
        this.movementProcessorIDs = Arrays.copyOf(movementProcessorIDs, hardwareIDs.length - indexesToRemove.length);
        this.holdStatus = Arrays.copyOf(holdStatus, hardwareIDs.length - indexesToRemove.length); 
        this.movementProcessorFactoryClasses = Arrays.copyOf(movementProcessorFactoryClasses, hardwareIDs.length - indexesToRemove.length);
    }

    // Fully implemented.    
    /** Remove all movement processors associated with the specified hardwareID. Removes the operational movement processor and the registry listing.
     *  Note: All add and remove operations are synchronized to avoid conflict of using the same counter variables while processing (this is correctable with more variables, however, it is not anticipated during the prototype implementation that adding and removing of registered movement processors will be performed while the robot is processing movements, if necessary this can be modified later to ensure new processors can be added/removed while processing occurs, but for the time being this is the implementation in the prototype Obviously we do not want movement processing instructions being performed and having the entire robots movement pause temporarily while the robot is in movement (That said the pause would be at the microsecond level and likely would not be noticable unless the robot were under a high CPU utilization at the time of the add/remove movement processor to notice. Additional potential conflicts could come from using the following methods that would need to be synchronized if add/remove methods are not synchronized: getNextMovementIDByHardwareID, and validateUnusedMovementProcesserID. This information is provided to assist the person that undoubtedly will be updating the methods to run without interference in movement processor process method execution/interference (This undoubtedly will be an issue on somebodies robot platform in the unforseen future.) An additional note is fewer variables means better power efficiency in most cases, therefore, it is important to remember most implementations had efficiency and power consumption in mind when implementing the API the way it implemented for the DRC competition. Another note: the processing arrays need to be reconstructed in the current implementation which is the primary reason this method is synchronized (just remembered when implementing remove.), however, it was an intentional trade because the array data accesses are 3 to 8 times faster then collection object gets when repeatedly accessed (as in this implementation).
     *
     * @param hardwareID 
     */
    public synchronized void remove(int hardwareID)
    {
        // TODO: If/when the registry file type changes to DB or key format changes this method will need to be rewritten it is a prototype and the add/remove methods are fairly inefficient due to the multiple looping mechanisms used to obtain hardware id groupings of movement processors.
        // loop through int[] obtained by getArrayElementIndexesLinkedToHardwareID(int hardwareID) to remove/generate all keys for removing the registry methods. The processing array removal update will need to be performed in the code following registry removal.
        // Remove the movement processor from the registry...
        // update the private variable retIndexArray with the array indexes of all movement processors associated with hardwareID.
        updateArrayElementIndexesLinkedToHardwareID(hardwareID);
        // call remove on each movement processor in the registry.
        // reset the removeLoopCount to 0
        this.removeLoopCount=0;
        while(removeLoopCount<retIndexArray.length)
        {
            // if item is not found in the registry or an exception occurs remove returns false, popup a System message for debugging purposes...
            if(!MovementProcessorManager.registry.remove(String.valueOf(hardwareID) + "." + String.valueOf(movementProcessorIDs[retIndexArray[removeLoopCount]])))
            {
                System.out.println("Registry key: " + String.valueOf(hardwareID) + "." + String.valueOf(movementProcessorIDs[retIndexArray[removeLoopCount]]) + " could not be removed, more likely then not because it does not exist in the registry (probably a naming problem therefore bug in coding).");
            }
            // increment the while loop count
            this.removeLoopCount=removeLoopCount + 1;
        }
        // Remove hardwareID related elements from processing arrays, and reconstruct to new array sizes.
        removeProcessingArrayElementsByIndexes(retIndexArray);      
    }    
    
    // Fully implemented.
    /** Remove the specified movement processor by hardwareID/movementProcessorID.
     * @param hardwareID
     * @param movementProcessorID  
     */    
    public synchronized void remove(int hardwareID, int movementProcessorID)
    {
        // if item is not found in the registry or an exception occurs remove returns false, popup a System message for debugging purposes...
        if(!MovementProcessorManager.registry.remove(String.valueOf(hardwareID) + "." + String.valueOf(movementProcessorID)))
        {
            System.out.println("Registry key: " + String.valueOf(hardwareID) + "." + String.valueOf(movementProcessorID) + " could not be removed, more likely then not because it does not exist in the registry (probably a naming problem therefore bug in coding).");
        }
        try
        {
            removeValueArray[0] = getMovementProcessorIndex(hardwareID, movementProcessorID);
            // Remove hardwareID related elements from processing arrays, and reconstruct to new array sizes.
            removeProcessingArrayElementsByIndexes(removeValueArray);
        }
        catch(ArrayIndexOutOfBoundsException e ) 
        { 
            System.out.println("Method: remove(int hardwareID, int movementProcessorID) could not locate the passed in hardwareID/movementProcessorID in the common arrays.");
        }       
    }   
    
    // Completed.
    /** Checks out the specified movement processor by hardwareID, movementProcessorID. All checked out movement processors must be checked back in because they decrement the total pool count each checkout. Returns null if an associated movement processor is not registered.
     * @param hardwareID 
     * @param movementProcessorID 
     * @return MovementProcessor
     */
    public MovementProcessor checkout(int hardwareID, int movementProcessorID)
    {
        // Some of these global variables are shared by getMovementProcessorIndex, which is implemented in remove.
        this.hardwareIDLoopCount=0;
        while(hardwareIDLoopCount<hardwareIDs.length)
        {
            if(hardwareIDs[hardwareIDLoopCount]==hardwareID&&movementProcessorIDs[hardwareIDLoopCount]==movementProcessorID)
            {
                this.midIndex=hardwareIDLoopCount;
                this.hardwareIDLoopCount=hardwareIDs.length;
            }
            // increment the loop count
            this.hardwareIDLoopCount = hardwareIDLoopCount + 1;
        }    
        return MovementProcessorManager.pool.checkout(midIndex);
    }

    // Completed.
    /** Checks out the specified movement processor by hardwareID, movementProcessorID. All checked out movement processors must be checked back in because they decrement the total pool count each checkout. Returns null if an associated movement processor is not registered.
     * @param mid - movement processor id, is the index order the hardwareID, and movement processor id are registered. Much more efficient then calling any other method, aside from using the pool variable itself to checkout.  
     * @return MovementProcessor
     */
    public MovementProcessor checkout(int mid)
    {
        return MovementProcessorManager.pool.checkout(mid);
    }
    
    // Completed.
    /** Checks in the specified checked out movement processor by cid, that can be obtained by calling MovementProcessor.cid. 
     * @param cid - Cache Pool ID movement processor id, is the index order the hardwareID, and movement processor id are registered. Much more efficient then calling any other method, aside from using the pool variable itself to checkin.  
     */
    public void checkin(int cid)
    {
        MovementProcessorManager.pool.checkin(cid);
    }       
    
    /** Hold execution of all movement processors on specified hardwareID. All registered movement processors will be excluded from modifying movement instructions on the specified hardwareID until continuer is called.
     * @param hardwareID 
     */
    public void hold(int hardwareID)
    {
          
    }
 
    /** Hold execution of the specified movement processor for the specified hardwareID. The movement processor will be excluded from modifying movement instructions until continuer is called.
     * @param hardwareID
     * @param movementProcessorID  
     */
    public void hold(int hardwareID, int movementProcessorID)
    {
        
    }

    /** Continue execution of all movement processors on the specified hardwareID. If continuer is called on an already functional not on hold movement processor, the movement processor will continue to operate normally.
     * @param hardwareID 
     */
    public void continuer(int hardwareID)
    {
        
    }    
    
    /** Continue execution of the specified movement processor on the specified hardwareID. If continuer is called on an already functional not on hold movement processor, the movement processor will continue to operate normally.
     * @param hardwareID
     * @param movementProcessorID  
     */
    public void continuer(int hardwareID, int movementProcessorID)
    {
        
    }

    /** Start the movement processor manager run loop. */
    public void start()
    {

    }

    /** Stop this object - stop the movement processor manager run loop. */
    public void stop()
    {


    }

    /** Recycle this object to start state for reuse. */
    public void recycle()
    {

    }

    /** Return boolean for is on hold on specified Movement Processor 
     * by hardwareID/movementProcessorID
     * @param hardwareID 
     * @param movementProcessorID
     * @return boolean is on hold. 
     */
    public boolean isOnHold(int hardwareID, int movementProcessorID)
    {
        // TODO: implement this method.
        return true;
    }
    
    /** Return a list (int[][]) of processing movement processors, the 
     * returned list excludes all onHold or registered but non-functional
     * movement processors. The returned list is an int[][] the format of each
     * element as int[] where int[0]=hardwareID, int[1]=movementProcessorID.
     * @return int[][] the format of each element as int[] where 
     * int[0]=hardwareID, int[1]=movementProcessorID.
     */
    public int[][] getProcessingMovementProcessorIDs()
    {
        // TODO: Implement method to return movement processor IDs.
        return null;
    }    
    
    /** Return a list (int[][]) of registered movement processors, in the format of each element as int[] where int[0]=hardwareID, int[1]=movementProcessorID.
     * @return int[][] The format of each element as int[] where int[0]=hardwareID, int[1]=movementProcessorID.
     */
    public int[][] getRegisteredMovementProcessorIDs()
    {
        // TODO: Implement method to return movement processor IDs.
        return null;
    }
 
    /** Return a list (int[][]) of registered movement processors and isOnHoldStatus(), in the format of each element as int[] where int[0]=hardwareID, int[1]=movementProcessorID, int[2]=boolean is on hold.
     * @return int[][] in the format of each element as int[] where 
     * int[0]=hardwareID, int[1]=movementProcessorID, int[2]=boolean is on hold.
     */
    public int[][] getRegisteredMovementProcessorsIDsStatus()
    {
        // TODO: Implement method to return movement processor IDs.
        return null;
    }    
    
    /** write registry add/removes to properties file. Utility function. */
    public void writeRegistry()
    {
        MovementProcessorManager.registry.writeFile();
    }   
    
}