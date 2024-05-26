package org.happy.artist.rmdmia.perception.engine.sensor;

/** SensorProcessorPool.java - High Performance SensorProcessor Object Pool 
 *  implemented to improve Object access efficiency over Java Collections. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2013-2015 Happy Artist. All rights reserved.
 */
public class SensorProcessorPool 
{
    // The availabilityArray specifies whether or not a sensorProcessor Object is available to be checked out.
    private final boolean[] availabilityArray;  
    // The sensorProcessorArray is an array of instantiated and recycled SensorProcessor Objects, associated by index to the sensorProcessorArray for fast lookup of SensorProcessor Objects that are avaiable to checkout via the availability array.	
    private final SensorProcessor[] sensorProcessorArray; 
    private int checkoutCount = 0;    
    // SENSOR_PROCESSOR_POOL_SIZE The size of the SensorProcessor Pool. This is a fixed pool size.
    private static int SENSOR_PROCESSOR_POOL_SIZE;
    // TODO: Likely need to remove static instances for multiple running SensorProcessorPools to function.
//    private static SensorProcessorPool sensorProcessorPool;   
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
     * org.happy.artist.rmdmia.rcsm.SensorProcessorPool constructor.
     * 
     *  Pre-generate SensorProcessor Objects in an Array by SensorProcessor Factories for each SensorProcessor, with each processor pool being in the array range of int sensorProcessorPoolRangeLength, and the sid being the index element number of the passed in sensorProcessorFactoryArray.
     *
     *  Total Sensor Processor Pool size is sensorProcessorFactoryArray.length * sensorProcessorPoolRangeLength
     * 
     *  @param sensorProcessorFactoryArray
     *         The ThreadFactory from the concurrency API was used for convenience as an Object Factory interface. Will likely be replaced.
     */
    public SensorProcessorPool(SensorProcessorFactory[] sensorProcessorFactoryArray, int sensorProcessorPoolRangeLength)
    {
        // Set Movement Processor Pool Size
        this.SENSOR_PROCESSOR_POOL_SIZE = sensorProcessorFactoryArray.length * sensorProcessorPoolRangeLength;
        // Set the RANGE_LENGTH at class scope
        this.RANGE_LENGTH = sensorProcessorPoolRangeLength;
        // initialize the poolRangeIterationArray
        this.poolIterationCountArray = new int[SENSOR_PROCESSOR_POOL_SIZE];
        this.availabilityArray = new boolean[SENSOR_PROCESSOR_POOL_SIZE];
        this.sensorProcessorArray = new SensorProcessor[SENSOR_PROCESSOR_POOL_SIZE];	
        // Set the Factory Pool Range Count to Zero for the loop.
        this.factoryPoolRangeCount = 0;
        while(checkoutCount<sensorProcessorArray.length)
        {
            // Set the Factory Pool Loop Count
            this.factoryPoolLoopCount=0;
            while(factoryPoolLoopCount<RANGE_LENGTH)
            {  
                // Core process            
                this.availabilityArray[checkoutCount]=true;
                // adding if statement for troubleshooting NullPointerException
             //   if(sensorProcessorFactoryArray[factoryPoolRangeCount]!=null)
             //   {
                    this.sensorProcessorArray[checkoutCount]=(SensorProcessor) sensorProcessorFactoryArray[factoryPoolRangeCount].newSensorProcessor();
                    this.sensorProcessorArray[checkoutCount].cid = checkoutCount;
                    // TODO: Strip this variable later on if it is not implemented one way or another.
                    this.sensorProcessorArray[checkoutCount].sid = factoryPoolRangeCount;
                    this.checkoutCount=checkoutCount + 1;
             //   }
                // End core process
                // Increment Loop
                this.factoryPoolLoopCount = factoryPoolLoopCount + 1;
            }
            // Append to factoryPoolRangeCount
            this.factoryPoolRangeCount = factoryPoolRangeCount + 1;
        }
        this.checkoutCount = 0;
    }    
	
    // TODO: Only implementable if a single SensorProcessorPool is used for all Gestures. Implement later...
	// Return the singleton instance of SensorProcessorPool.
//	public final static SensorProcessorPool getInstance()
//	{
//		if(sensorProcessorPool!=null)
//		{
//			return SensorProcessorPool.sensorProcessorPool;
//		}
//		return SensorProcessorPool.sensorProcessorPool = new SensorProcessorPool(new GestureThreadFactory());
//	}
	
    // Return null if no resources are available. Suggested action retry. Note
    // Checkout a SensorProcessor by sid. sid is the SensorProcessorFactory array index of the SensorProcessor being requested.
    // Note: It is up to the implementor of this class to make sure that the RANGE_LENGTH is sufficient for the number of threads that may execute the RANGE. Additionally, the loop only cycles till the end of the array, and if the oldest checked out Object has not yet been returned to the pool, null is returned, therefore it is advised if null is returned that the implementing code retry checkout again. If the quantity of RANGE Objects is sufficient an Object wil always be available.
    public SensorProcessor checkout(int sid)
    {
        // maximum range index = (sid * RANGE_LENGTH) + RANGE_LENGTH - 1;
        this.maximumIndexRangePlusOne = (sid * RANGE_LENGTH) + RANGE_LENGTH;
        // Set the current checkoutCount to the sid range last checked out.
        // checkoutCount = (sid * RANGE_LENGTH) + poolIterationCountArray[sid];
        // subtraction is less math then multiplication, so subtract RANGE_LENGTH from maximumIndexRangePlusOne
        this.checkoutCount = (maximumIndexRangePlusOne - RANGE_LENGTH) + poolIterationCountArray[sid];
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
                    this.poolIterationCountArray[sid]=(this.checkoutCount - (maximumIndexRangePlusOne - RANGE_LENGTH)) + 1;
                    return (SensorProcessor)sensorProcessorArray[checkoutCount];
                }
                this.checkoutCount = checkoutCount + 1;
            }  
            return null;
        }
        else
        {
            // Reset poolIterationCountArray
            poolIterationCountArray[sid]=0;
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
                    this.poolIterationCountArray[sid]=(this.checkoutCount - (maximumIndexRangePlusOne - RANGE_LENGTH)) + 1;
                    return (SensorProcessor)sensorProcessorArray[checkoutCount];
                }
                this.checkoutCount = checkoutCount + 1;
            }  
            return null;        
        }
    }	    

    // Checkin the SensorProcessor Object for recycling. No synchronized blocks are included, because the 
    // checkin SensorProcessor by cid (Movement Processor Cache Pool ID).
    public void checkin(final int cid)
    {
        this.sensorProcessorArray[cid].recycle();
        availabilityArray[cid]=true;
    }
}
