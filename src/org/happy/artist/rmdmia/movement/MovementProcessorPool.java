package org.happy.artist.rmdmia.movement;

/** MovementProcessorPool.java - High Performance MovementProcessor Object Pool 
 *  implemented to improve Object access efficiency over Java Collections. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2012 Happy Artist. All rights reserved.
 */
public class MovementProcessorPool 
{
    // The availabilityArray specifies whether or not a movementProcessor Object is available to be checked out.
    private final boolean[] availabilityArray;  
    // The movementProcessorArray is an array of instantiated and recycled MovementProcessor Objects, associated by index to the movementProcessorArray for fast lookup of MovementProcessor Objects that are avaiable to checkout via the availability array.	
    private final MovementProcessor[] movementProcessorArray; 
    private int checkoutCount = 0;    
    // MOVEMENT_PROCESSOR_POOL_SIZE The size of the MovementProcessor Pool. This is a fixed pool size.
    private static int MOVEMENT_PROCESSOR_POOL_SIZE;
    // TODO: Likely need to remove static instances for multiple running MovementProcessorPools to function.
//    private static MovementProcessorPool movementProcessorPool;   
    // Current range iteration array
    private final int[] poolIterationCountArray;
    // Movement Processor Factory Pool Range Length
    private final int RANGE_LENGTH;
    // Maximum Index Range plus one is used in checkout to loop and if less then statements
    private int maximumIndexRangePlusOne;
    // Movement Processor Initialization Factory Pool Range Count
    private int factoryPoolRangeCount;
    // Factory Pool Loop Counter
    private int factoryPoolLoopCount;
    
    // TODO: Figure out if a GestureThreadPool even makes sense to implementor, and performance.
    // TODO: Implement shutdown method.
    // TODO: revert back to private if getInstance() implementation is completed.
    /**
     * org.happy.artist.rmdmia.rcsm.MovementProcessorPool constructor.
     * 
     *  Pre-generate MovementProcessor Objects in an Array by MovementProcessor Factories for each MovementProcessor, with each processor pool being in the array range of int movementProcessorPoolRangeLength, and the mid being the index element number of the passed in movementProcessorFactoryArray.
     *
     *  Total Movement Processor Pool size is movementProcessorFactoryArray.length * movementProcessorPoolRangeLength
     * 
     *  @param movementProcessorFactoryArray
     *         The ThreadFactory from the concurrency API was used for convenience as an Object Factory interface. Will likely be replaced.
     */
    public MovementProcessorPool(MovementProcessorFactory[] movementProcessorFactoryArray, int movementProcessorPoolRangeLength)
    {
        // Set Movement Processor Pool Size
        this.MOVEMENT_PROCESSOR_POOL_SIZE = movementProcessorFactoryArray.length * movementProcessorPoolRangeLength;
        // Set the RANGE_LENGTH at class scope
        this.RANGE_LENGTH = movementProcessorPoolRangeLength;
        // initialize the poolRangeIterationArray
        this.poolIterationCountArray = new int[MOVEMENT_PROCESSOR_POOL_SIZE];
        this.availabilityArray = new boolean[MOVEMENT_PROCESSOR_POOL_SIZE];
        this.movementProcessorArray = new MovementProcessor[MOVEMENT_PROCESSOR_POOL_SIZE];	
        // Set the Factory Pool Range Count to Zero for the loop.
        this.factoryPoolRangeCount = 0;
        while(checkoutCount<movementProcessorArray.length)
        {
            // Set the Factory Pool Loop Count
            this.factoryPoolLoopCount=0;
            while(factoryPoolLoopCount<RANGE_LENGTH)
            {  
                // Core process            
                this.availabilityArray[checkoutCount]=true;
                this.movementProcessorArray[checkoutCount]=(MovementProcessor) movementProcessorFactoryArray[factoryPoolRangeCount].newMovementProcessor();
                this.movementProcessorArray[checkoutCount].cid = checkoutCount;
                // TODO: Strip this variable later on if it is not implemented one way or another.
                this.movementProcessorArray[checkoutCount].mid = factoryPoolRangeCount;
                this.checkoutCount=checkoutCount + 1;
                // End core process
                // Increment Loop
                this.factoryPoolLoopCount = factoryPoolLoopCount + 1;
            }
            // Append to factoryPoolRangeCount
            this.factoryPoolRangeCount = factoryPoolRangeCount + 1;
        }
        this.checkoutCount = 0;
    }    
	
    // TODO: Only implementable if a single MovementProcessorPool is used for all Gestures. Implement later...
	// Return the singleton instance of MovementProcessorPool.
//	public final static MovementProcessorPool getInstance()
//	{
//		if(movementProcessorPool!=null)
//		{
//			return MovementProcessorPool.movementProcessorPool;
//		}
//		return MovementProcessorPool.movementProcessorPool = new MovementProcessorPool(new GestureThreadFactory());
//	}
	
    // Return null if no resources are available. Suggested action retry. Note
    // Checkout a MovementProcessor by mid. mid is the MovementProcessorFactory array index of the MovementProcessor being requested.
    // Note: It is up to the implementor of this class to make sure that the RANGE_LENGTH is sufficient for the number of threads that may execute the RANGE. Additionally, the loop only cycles till the end of the array, and if the oldest checked out Object has not yet been returned to the pool, null is returned, therefore it is advised if null is returned that the implementing code retry checkout again. If the quantity of RANGE Objects is sufficient an Object wil always be available.
    public MovementProcessor checkout(int mid)
    {
        // maximum range index = (mid * RANGE_LENGTH) + RANGE_LENGTH - 1;
        this.maximumIndexRangePlusOne = (mid * RANGE_LENGTH) + RANGE_LENGTH;
        // Set the current checkoutCount to the mid range last checked out.
        // checkoutCount = (mid * RANGE_LENGTH) + poolIterationCountArray[mid];
        // subtraction is less math then multiplication, so subtract RANGE_LENGTH from maximumIndexRangePlusOne
        this.checkoutCount = (maximumIndexRangePlusOne - RANGE_LENGTH) + poolIterationCountArray[mid];
        if(checkoutCount<maximumIndexRangePlusOne)
        {
            // While loop must test range length to ensure an infinite loop does not occur.
            while(checkoutCount<maximumIndexRangePlusOne)
            {
                // If value is false, check out Object, and set value to true.
                if(this.availabilityArray[checkoutCount])
                {
                    this.availabilityArray[checkoutCount]=false;
                    // Set the next checkout range position.
                    this.poolIterationCountArray[mid]=(this.checkoutCount - (maximumIndexRangePlusOne - RANGE_LENGTH)) + 1;
                    return (MovementProcessor)movementProcessorArray[checkoutCount];
                }
                this.checkoutCount = checkoutCount + 1;
            }  
            return null;
        }
        else
        {
            // Reset poolIterationCountArray
            poolIterationCountArray[mid]=0;
            // Reset checkoutCount to 0 because it is out of index bounds.
            this.checkoutCount = maximumIndexRangePlusOne - RANGE_LENGTH;
            // While loop must test range length to ensure an infinite loop does not occur.
            while(checkoutCount<maximumIndexRangePlusOne)
            {
                // If value is false, check out Object, and set value to true.
                if(this.availabilityArray[checkoutCount])
                {
                    this.availabilityArray[checkoutCount]=false;
                    // Set the next checkout range position.
                    this.poolIterationCountArray[mid]=(this.checkoutCount - (maximumIndexRangePlusOne - RANGE_LENGTH)) + 1;
                    return (MovementProcessor)movementProcessorArray[checkoutCount];
                }
                this.checkoutCount = checkoutCount + 1;
            }  
            return null;        
        }
    }	    

    // Checkin the MovementProcessor Object for recycling. No synchronized blocks are included, because the 
    // checkin MovementProcessor by cid (Movement Processor Cache Pool ID).
    public void checkin(final int cid)
    {
        this.movementProcessorArray[cid].recycle();
        availabilityArray[cid]=true;
    }

}
