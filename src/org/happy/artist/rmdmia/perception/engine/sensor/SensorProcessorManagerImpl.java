package org.happy.artist.rmdmia.perception.engine.sensor;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.happy.artist.rmdmia.Controller;
//import org.happy.artist.rmdmia.rcsm.providers.ros.ROSNode;

/** SensorProcessorManagerImpl.java - Sensor Processor Manager Object Prototype.
 *  Sensor Processors process incoming message data from the RCSM. 
 *  The following are examples of tasks that sensor processors might perform:
 *  - Convert a proprietary message to an RMDMIA compatible data type or message.
 *  - Add message to a message queue for RMDMIA processing
 *  - Incoming Data Proxy for logging messages.
 *  - Incoming Data Proxy for forwarding messaging to another location.
 * 
 * @author Happy Artist
 * 
 * @copyright Copyright © 2013-2014 Happy Artist. All rights reserved.
 */
public class SensorProcessorManagerImpl implements SensorProcessorManager
{
    // TODO: convert ints to byte, and/or byte[] where performancee can be improved.
    
    // SensorProcessorRegistry class is implemented by the SensorProcessorManagerImpl 
    // to manage registered Sensor Processors by designated package name, sensor processor quantity, 
    // and associated robot function grouping for SensorProcessorPool initialization in the SensorProcessorManagerImpl.
    private final static SensorProcessorRegistry registry = SensorProcessorRegistry.getInstance();
    // Define the SensorProcessorPool Object
    protected static SensorProcessorPool pool;
    // Object Pool Range for each Factory (i.e. number of Factory Objects for each sid).
    // TODO: Add this variable to the Application Properties.
    private static int SENSOR_PROCESSOR_OBJECTS_PER_RANGE = 1;
    // SensorProcessorFactory[] for initializing the SensorProcessorPool.
    private SensorProcessorFactory[] factoryArray;
//    private SensorProcessor[] sensorArray;    
    // The sensor processor id arrays. 
    // TODO: Resolve technical debate of having two arrays one for ids, and one for ids and status. The primary difference is an additional element for the status. Will do some performance tests later to determine whether one or multiples performs better, but in terms of the methods to return the current array multiple is better, to avoid processing a new array to give a fast readable list to process, and since multidimensional arrays are so much more processor intensive it makes sense to keep them separate... Ultimately it will depend on whether or not the array return methods are used frequently by other methods, and since this is not a multithreaded Object, the fast return will ensure the timing remains accurate vs, waiting for a looping operation to finish creating an array. To many unknowns about the Robot OS to know yet...
    // TODO: Technically, it makes more sense to run the processing system from two seperate arrays that are synced up instead of one 2 dimensional array because we know the array length will always be two, and the efficiency of two arrays is highly greater. The most complicated problem to solve is usability for code users, whom may find 3 arrays confusing as opposed to simply obtaining a Collection, which usability wise is much simpler. Since this is not subject to usability and rather the competition is about performance and energy efficiency the more efficient non user friendly implementation makes the most sense.
    private int[][] processingSensorProcessorIDs;
    private int[][] registeredSensorProcessorIDs;
    private int[][] registeredSensorProcessorIDsStatus;  
    
    // 3 arrays required for processing runtime status.
    private int[] hardwareIDs;
    private int[] sensorProcessorIDs;
    private boolean[] holdStatus;
    private Class[] sensorProcessorFactoryClasses;
    private SensorProcessor[] sensorProcessors;
    
    // TODO: Implement LAST - SensorProcessor array will be used when process is called. That said, a more complicated dynamic system will likely be used so this will be the last step. This is due to using Object Cache Pools, and ultimately this will make a huge efficiency difference in a threaded model where a thread executes each sensor processor step separately, not likely unless its a synchonrous executor, but that is most likely unless sensor processing tasks are so processor intensive they need to be skipped for a next step that completes quicker.......
    // Sensor Processing Array contains only the processing Sensor Processors and not the on hold processors.
    private SensorProcessor sensorProcessingArray[];
    
    // keys array used by constructor to assign the sensor processor keys for class initialization of pre-configured sensor processor class files.
    // Note: Did not assign as final due to the expectation that this will eventually be a live system, and if the properties file is read while the system is live it will never be allowed to reload or update the array with updated sensor Processors via the Properties file.
    private Object[] keys;
    // splitting properties keys at startup use this String[]
    private String[] keySplitArray;
    // splitting properties values at startup use this String[]
    private String[] valueSplitArray;    
    // while loop is always fewer CPU operations then a for loop. Also, pre-initializing the counter for loop variables is an efficiency increase.
    private int keyCount;
    
    // variables used by the getNextSensorIDByHardwareID method a private method called from inside the add method.
    private int hardwareIDCount;
    // hardwareID while loop counter.
    private int hardwareIDLoopCount;
    // variables used add method
    private int newSensorProcessorID;
    
    // variables used by getAllIndexesLinkedToHardwareID & remove method (will allow reuse of retIndexArray since remove is a synchronized method - another of those power saving tweaks... theoretically the method could be a void call that internal methods access this array after calling(actually may do that for efficiency purposes... Not very user friendly code...) ).
    private int[] retIndexArray;
    // Remove loop counter for while loop in the remove method.
    private int removeLoopCount;
    // variables used by removeProcessingArrayElementsByIndexes method
    private int indexToWriteCount;
    private int indexMarker;  
    // A reusable array in the remove emthod for a single sensor processor so I can be lazy and reuse the multiple element array remove method code.
    private int[] removeValueArray = new int[1];
    
    // Variables used by get method
    private int sidIndex;
    
    // Used by process method
    private SensorMessage sensorMessage;
    private SensorProcessor sensorProcessor;
    private int processLoopCount = 0;
    private Controller controller;
 
    /** Pass in the RMDMIA Controller for Sensor Processors to obtain access 
     * to Controller Objects, and variables. 
     * 
     */
    public SensorProcessorManagerImpl(Controller controller) throws UnsupportedEncodingException
    {
        this.controller=controller;
 
        // Initialization
        // 1. Read Registry, and load classes/Objects/associated ids.
        // 2. Read the Sensor Processor Registry keys for a loop that will initialize all the sensor processor classes based on key values.  
        // 3. The properties reader will read the key in the format of hardwareID.sensorprocessorID where " is the last period in the property key. Its very likely if a single property file is used, or multiple grouped that a pre identifier word will be used (i.e. - hardware group id, precededed even by a component id). This iniital implementation will simply use the last ".". The value will be the class name package path, and a comman will terminate the class name and be a following value. In this case initially it will be a is holding value for initialization.
        // 4. SortedEAProperties instance sorts the keys Array for the process lookup algorithm that maintains the hardwareID sensor processor count, and groups them together by quantity at which time the while loop stops when the sensor processor total is acheived. Improves performance on all but the last element in the array. More frequently accessed hardwareIDs should be lowest numbers, and least frequently accessed index numbers should be the highest elements.
        this.keys = SensorProcessorManagerImpl.registry.getProperties().keySet().toArray();
        if(keys.length<1)
        {
            System.out.println("SensorProcessorRegistry keys length is less than 1. Generating default values.");
            // if true reset keys
//            System.out.println("registry:" + registry);
//            System.out.println("controller:" + controller);            
            
            if(org.happy.artist.rmdmia.perception.engine.sensor.utility.Utility.generateDefaultSensorProcessorTopicsWithROSBag(controller, registry))
            {
                this.keys = SensorProcessorManagerImpl.registry.getProperties().keySet().toArray();
            }
        }
//        System.out.println("Sorted sensor processor ids: " + Arrays.toString(keys));
        // Initialize the sensorProcessorFactoryClasses Array.
        this.sensorProcessorFactoryClasses = new Class[SensorProcessorManagerImpl.registry.getProperties().size()];
        // initialize hardwareIDs.
        this.hardwareIDs = new int[keys.length];
        // Populate hardwareIDs
        for(int i=0;i<hardwareIDs.length;i++)
        {
            hardwareIDs[i]=i;
        }
        // initialize sensorProcessorIDs.
        this.sensorProcessorIDs = new int[keys.length];
        // initialize hold status
        this.holdStatus= new boolean[keys.length];
        // initialize sensor processors
        this.sensorProcessors = new SensorProcessor[keys.length];
        
        this.keyCount=0;

        while(keyCount<keys.length)
        {
            // TODO: note the processorID may be better off first if sensor processors must be run on all sensors, never likely so probably just a note.
            // Load the Sensor Processors Arrays
            this.keySplitArray=((String)keys[keyCount]).split("\\.");
            if(keySplitArray.length==2)
            {
                hardwareIDs[keyCount]=new Integer(keySplitArray[0]).intValue();
                sensorProcessorIDs[keyCount]=new Integer(keySplitArray[1]).intValue();
            }
            // get the key value
            this.valueSplitArray=SensorProcessorManagerImpl.registry.get(((String)keys[keyCount])).split(",");
            if(valueSplitArray.length==1&&valueSplitArray[0]!=null&&valueSplitArray[0].isEmpty()==false)
            {
                try 
                {
//     System.out.println("Count: " + keyCount + ",value: " + valueSplitArray[0] + " Class" + Class.forName(valueSplitArray[0]));
                    // add the class name of the sensor processor
//                    System.out.println("SensorProcessorFactory Classname: " + valueSplitArray[0]);
                    sensorProcessorFactoryClasses[keyCount]=Class.forName(valueSplitArray[0]);
                    // By default boolean value hold status will be false. No need to write variable.
                } 
                catch (ClassNotFoundException ex) 
                {
                    System.out.println("SensorProcessorFactory class specified in properties file not found: " + valueSplitArray[0]);
                    if(valueSplitArray[0].endsWith("InputHandler"))
                    {
                        controller.getInstructionManager().getProviderByName("instruction_manager").getInstructionDefinition(hardwareIDs[keyCount]).getInstructionObject();
                        factoryArray[keyCount]=(SensorProcessorFactory)controller.getInstructionManager().getProviderByName("instruction_manager").getInstructionDefinition(hardwareIDs[keyCount]).getInstructionInputHandler();
                        sensorProcessorFactoryClasses[keyCount]=factoryArray[keyCount].getClass();
                    }
                    else
                    {
                        Logger.getLogger(SensorProcessorManagerImpl.class.getName()).log(Level.SEVERE, "SensorProcessorFactory class specified in properties file not found: " + valueSplitArray[0], ex);
                    }
                }                  
            }
            else if(valueSplitArray.length==2&&valueSplitArray[0]!=null&&valueSplitArray[0].isEmpty()==false)
            {
                try 
                {               
                    // add the class name of the sensor processor
                   // System.out.println("SensorProcessorFactory Classname: " + valueSplitArray[0]);
                    sensorProcessorFactoryClasses[keyCount]=Class.forName(valueSplitArray[0]);
                    holdStatus[keyCount]=Boolean.parseBoolean(valueSplitArray[1]);                              
                } 
                catch (ClassNotFoundException ex) 
                {
                    System.out.println("SensorProcessor class specified in properties file not found: " + valueSplitArray[0]);
                    if(valueSplitArray[0].endsWith("InputHandler"))
                    {
                        controller.getInstructionManager().getProviderByName("instruction_manager").getInstructionDefinition(hardwareIDs[keyCount]).getInstructionObject();
                        factoryArray[keyCount]=(SensorProcessorFactory)controller.getInstructionManager().getProviderByName("instruction_manager").getInstructionDefinition(hardwareIDs[keyCount]).getInstructionInputHandler();
                        sensorProcessorFactoryClasses[keyCount]=factoryArray[keyCount].getClass();
                        holdStatus[keyCount]=Boolean.parseBoolean(valueSplitArray[1]);  
                    }
                    else
                    {
                        Logger.getLogger(SensorProcessorManagerImpl.class.getName()).log(Level.SEVERE, "SensorProcessorFactory class specified in properties file not found: " + valueSplitArray[0], ex);
                    }
                }                 
            }
            else
            {
                // no idea what this is need to add support I guess.
                Logger.getLogger(SensorProcessorManagerImpl.class.getName()).log(Level.INFO,"This string not supported in sensor processor properties for key: " + SensorProcessorManagerImpl.registry.get(((String)keys[keyCount])));                
            }
            // Remember to increment the count. Always use  i = i + 1 for primitive thread safety. ++ is not thread safe.
            this.keyCount = keyCount + 1;
        }
        // reset keyCount back to 0
        this.keyCount=0;

        // Create the SensorProcessorFactory Array for SensorProcessorPool initialization, by initializing the sensorProcessorFactoryClasses into an array.
        this.factoryArray = new SensorProcessorFactory[sensorProcessorFactoryClasses.length];
//        this.sensorArray = new SensorProcessor[sensorProcessorFactoryClasses.length];
        while(keyCount<sensorProcessorFactoryClasses.length)
        {
            try 
            {
                if(sensorProcessorFactoryClasses[keyCount]!=null&&sensorProcessorFactoryClasses[keyCount].getName().endsWith("InputHandler")==false)
                {
                    factoryArray[keyCount]=(SensorProcessorFactory) sensorProcessorFactoryClasses[keyCount].getConstructor(Controller.class).newInstance(controller);
                }
                else
                {
                    // if InputHandler
                    factoryArray[keyCount]=(SensorProcessorFactory) sensorProcessorFactoryClasses[keyCount].getConstructor(Controller.class,int.class).newInstance(controller,hardwareIDs[keyCount]);                    
                }
//                sensorArray[keyCount]=factoryArray[keyCount].newSensorProcessor();
            } 
            catch (InstantiationException ex) 
            {
                ex.printStackTrace();
                Logger.getLogger(SensorProcessorManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
            } 
            catch (IllegalAccessException ex) 
            {
                ex.printStackTrace();
                Logger.getLogger(SensorProcessorManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (IllegalArgumentException ex) 
            {
                Logger.getLogger(SensorProcessorManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
            } 
            catch (InvocationTargetException ex) 
            {
                Logger.getLogger(SensorProcessorManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
            } 
            catch (NoSuchMethodException ex) 
            {
                Logger.getLogger(SensorProcessorManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
            } 
            catch (SecurityException ex) 
            {
                Logger.getLogger(SensorProcessorManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
            // Remember to increment the count. Always use  i = i + 1 for primitive thread safety. ++ is not thread safe.
            this.keyCount = keyCount + 1;            
        }
        // reset keyCount back to 0
        this.keyCount=0;
        try
        {
            // Initialize the SensorProcessorPool
            SensorProcessorManagerImpl.pool = new SensorProcessorPool(factoryArray,SensorProcessorManagerImpl.SENSOR_PROCESSOR_OBJECTS_PER_RANGE);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
            Logger.getLogger(SensorProcessorManagerImpl.class.getName()).log(Level.SEVERE, null, e);
        }  
    }
    
    // TODO: Determine best algorithm for Pre-generate Sensor Processor Objects in an Array (Theoretical maximum number of SensorProcessor Objects should be: the number of maximum SensorProcessor Objects per second * timeout seconds, reality, is based on System memory usage and power consumption (Need to revisit this later on in performance testing)).
 
    // TODO: Add Hardware Groupings that can contain a group of hardware IDs for the same sensor group, or many, whatever works, but it is logical to group hardware together, especially if they work together. Even if just for logical grouping of a human interface to be able to specify sensor areas with language...

    // TODO:  (Note not in this class - just a note for RMDMIA) Add alias indexes to associate grouping identifiers with alias words, i.e. key=legs, value = hardwareid1, hardwareid2, and so on. 
   
    // TODO: on final non-prototype implementation use byte instead of int where a total collection is less then 128, and consider byte[] for collections smaller than an int byte length.
    
    // Call the process method on the sensor processor to update the output data on that sensor processor.
    /** Process the SensorMessage[] and return the SensorMessage[]. 
     * 
     * @param sensorMessages - The sensor message array to be processed.
     */
// TODO: if synchronized is removed Bag File Writer will need to be set as synchronized on addMessage and addConnectionHeader      
    @Override
    public synchronized SensorMessage[] process(SensorMessage[] sensorMessages)
    {
        // TODO: Implement a more efficient mechanism to obtain sensor processor ids for each hardware id.
        // Iterate through Sensor Processors for each message.
        // Count the number of registered sensor processors with the specified hardwareID
        this.processLoopCount=0;
        while(processLoopCount<sensorMessages.length)
        {
            this.sensorMessage = sensorMessages[processLoopCount];
                    //pool.checkout(i);
            // Count the number of registered sensor processors with the specified hardwareID
            this.hardwareIDLoopCount=0;
//try
//{
            while(hardwareIDLoopCount<hardwareIDs.length)
            {
                if(hardwareIDs[hardwareIDLoopCount]==sensorMessage.hid&&!holdStatus[hardwareIDLoopCount])
                {
                    // process SensorMessage here
                    this.sensorProcessor=pool.checkout(hardwareIDLoopCount);
                    sensorMessages[processLoopCount]=sensorProcessor.process(sensorMessage);
                    pool.checkin(sensorProcessor.cid);
                }
                // increment the loop count
                this.hardwareIDLoopCount = hardwareIDLoopCount + 1;
            }
            // Perform processing on current hardwareID sensor processor[]
//}
//catch(Exception e)
//{
//    System.out.println("hardwareIDs array: " + hardwareIDs);
//    System.out.println(e.getMessage());
//    e.printStackTrace();
//}
            // Iterate the sensorMessages loop count.
            this.processLoopCount = processLoopCount + 1;
        }
        return sensorMessages;
    }
    
    /** Process the SensorMessage and return the sensor 
     *  message. 
     * 
     * @param sensorMessage - The sensor message to be processed.
     */
    
// TODO: Strip out loo[ and just match hid to array element, then on remove set element to null, it will perform better...    
// TODO: if synchronized is removed Bag File Writer will need to be set as synchronized on addM<essage and addConnectionHeader            
    @Override
    public synchronized SensorMessage process(SensorMessage sensorMsg)
    {
        if(sensorMsg==null)
        {
            return null;
        }
        // TODO: Implement a more efficient mechanism to obtain sensor processor ids for each hardware id.
            //Iterate through Sensor Processors.
            // Count the number of registered sensor processors with the specified hardwareID
            this.hardwareIDLoopCount=0;
//try
//{
//            System.out.println("process(sensorMsg) sensorMsg.hid:" + sensorMsg.hid + " topic " + BytesToHex.bytesToHex(((ROSMessage)sensorMsg).binary_body));
            while(hardwareIDLoopCount<hardwareIDs.length)
            {
//                System.out.println("hid:"+hardwareIDs[hardwareIDLoopCount] + "sensorMsg.hid: " + sensorMsg.hid);
                if(hardwareIDs[hardwareIDLoopCount]==sensorMsg.hid&&!holdStatus[hardwareIDLoopCount])
                {                
 //                   System.out.println("Calling Sensor Processor");
                    // Process sensor message here
                    this.sensorProcessor=pool.checkout(hardwareIDLoopCount);
                    sensorMsg=sensorProcessor.process(sensorMsg);
                    pool.checkin(sensorProcessor.cid);
                }
        // Iterate the sensorMessages loop count.
        this.hardwareIDLoopCount = hardwareIDLoopCount + 1;
        }
        return sensorMsg;
    }    

    
    /** Unsupported - Add sensor processor by hardware id and save it to the registry for startup settings. Note: Hardware IDs must be manually registered/defined, and can be virtual, logical, or physical hardware groupings (implementor specific). If a hardware ID was not pre-created the specified input will be created as a new hardware ID on the fly. A return value of -1 means an error occurred. Warning, this method will reinstantiate the SensorProcessorPool, and all SensorProcessors, which could cause issues on in process data.
     * @param hardwareID 
     * @param sensorProcessor
     * @return int sensor id
     */
    public synchronized int add_save(int hardwareID, SensorProcessorFactory sensorProcessor, boolean on_hold)
    {
        // Design decision note: Its decided to make the sensor processor id for properties file iterative, based on preceding number of items already defined for that particular hardware id starting with 0. This properties file sensor processor id is in no way related to the API id variable on sensor processors that is used by the sensor processor cache pool.
        // Return new sensor processor ID.
        // Add the new sensor processor to the registry...
        SensorProcessorManagerImpl.registry.put(String.valueOf(hardwareID) + "." + String.valueOf(this.newSensorProcessorID=getNextSensorIDByHardwareID(hardwareID)), sensorProcessor.getClass().getCanonicalName().concat(",").concat(String.valueOf(on_hold)));
        this.hardwareIDs = Arrays.copyOf(hardwareIDs, hardwareIDs.length + 1);
        this.sensorProcessorIDs = Arrays.copyOf(sensorProcessorIDs, sensorProcessorIDs.length + 1);
        this.holdStatus = Arrays.copyOf(holdStatus, holdStatus.length + 1); 
        this.sensorProcessorFactoryClasses = Arrays.copyOf(sensorProcessorFactoryClasses, sensorProcessorFactoryClasses.length + 1);   
        this.factoryArray = Arrays.copyOf(factoryArray, factoryArray.length + 1);
//        this.sensorArray = Arrays.copyOf(sensorArray, sensorArray.length + 1);           
        hardwareIDs[hardwareIDs.length - 1]=hardwareID;
        sensorProcessorIDs[sensorProcessorIDs.length - 1]=newSensorProcessorID;
        holdStatus[holdStatus.length - 1]=on_hold;
        sensorProcessorFactoryClasses[sensorProcessorFactoryClasses.length - 1]=sensorProcessor.getClass();
        factoryArray[factoryArray.length - 1]=sensorProcessor;
        // Initialize the SensorProcessorPool
        SensorProcessorManagerImpl.pool = new SensorProcessorPool(factoryArray,SensorProcessorManagerImpl.SENSOR_PROCESSOR_OBJECTS_PER_RANGE);          
//        sensorArray[sensorArray.length - 1]=sensorProcessor.newSensorProcessor();         
       return newSensorProcessorID; 
    } 
    
    /** Unsupported - Add sensor processor by hardware id. If a hardware ID was not pre-created 
     * the specified input will be created as a new hardware ID on the fly. A return 
     * value of -1 means an error occurred. Warning, this method will reinstantiate the SensorProcessorPool, and all SensorProcessors, which could cause issues on in process data.
     * 
     * @param hardwareID 
     * @param sensorProcessor
     * @param on_hold boolean Set to false if you want the Sensor to begin processing 
     * incoming messages immediately on specified hardware id.
     * 
     * @return int sensor id
     */
    @Override
    public synchronized int add(int hardwareID, SensorProcessorFactory sensorProcessor, boolean on_hold)
    {
        // Design decision note: Its decided to make the sensor processor id for properties file iterative, based on preceding number of items already defined for that particular hardware id starting with 0. This properties file sensor processor id is in no way related to the API id variable on sensor processors that is used by the sensor processor cache pool.
        // Return new sensor processor ID.
        this.newSensorProcessorID=getNextSensorIDByHardwareID(hardwareID);
        this.hardwareIDs = Arrays.copyOf(hardwareIDs, hardwareIDs.length + 1);
        this.sensorProcessorIDs = Arrays.copyOf(sensorProcessorIDs, sensorProcessorIDs.length + 1);
        this.holdStatus = Arrays.copyOf(holdStatus, holdStatus.length + 1); 
        this.sensorProcessorFactoryClasses = Arrays.copyOf(sensorProcessorFactoryClasses, sensorProcessorFactoryClasses.length + 1);         
        this.factoryArray = Arrays.copyOf(factoryArray, factoryArray.length + 1);
//        this.sensorArray = Arrays.copyOf(sensorArray, sensorArray.length + 1);        
        hardwareIDs[hardwareIDs.length - 1]=hardwareID;
        sensorProcessorIDs[sensorProcessorIDs.length - 1]=newSensorProcessorID;
        holdStatus[holdStatus.length - 1]=on_hold;
        sensorProcessorFactoryClasses[sensorProcessorFactoryClasses.length - 1]=sensorProcessor.getClass();
        factoryArray[factoryArray.length - 1]=sensorProcessor;
        // Initialize the SensorProcessorPool
        SensorProcessorManagerImpl.pool = new SensorProcessorPool(factoryArray,SensorProcessorManagerImpl.SENSOR_PROCESSOR_OBJECTS_PER_RANGE);        
//        sensorArray[sensorArray.length - 1]=sensorProcessor.newSensorProcessor();        
       return newSensorProcessorID; 
    }     

    // Returns a new sensor processor id for the properties file naming, and identification programmatically inside the RMDMIA system using an int identifier (could be byte identifier). Specifically this method is called by the add method.
    private int getNextSensorIDByHardwareID(int hardwareID)
    {
        // Count the number of registered sensor processors with the specified hardwareID
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
        // This is not an effective way to do this if more than a few sensor processor IDs exist that were manually choosen and did not begin with 0, but it works, and it is what it is:)
        // loop until a valid unused sensor processor id is found.
        while(!validateUnusedSensorProcesserID(hardwareID, hardwareIDCount))
        {        
            this.hardwareIDCount = hardwareIDCount + 1;
        }
        return hardwareIDCount;
    }
    
    // A function used by getNextSensorIDByHardwareID(int hardwareID) to obtain a valid new sensor processor id
    private boolean validateUnusedSensorProcesserID(int hardwareID, int newSensorProcessorID)
    {
        // Reset the hardwareIDLoopCount for another looping.
        this.hardwareIDLoopCount=0;
        // verify the new hardwareID is not being used, if it is iterate to the next number and try that one.
        while(hardwareIDLoopCount<hardwareIDs.length)
        {
            if(hardwareIDs[hardwareIDLoopCount]==hardwareID)
            {
                if(sensorProcessorIDs[hardwareIDLoopCount]==newSensorProcessorID)
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
        // Count the number of registered sensor processors with the specified hardwareID
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
    
    // A method used by the get/remove method to obtain the common array index of the specified hardwareID, and sensorProcessorID. Return -1 if the index cannot be found - likely due to not being a valid hardwareID/sensorProcessorID.
    private int getSensorProcessorIndex(int hardwareID, int sensorProcessorID)
    {
        this.hardwareIDLoopCount=0;
        while(hardwareIDLoopCount<hardwareIDs.length)
        {
            if(hardwareIDs[hardwareIDLoopCount]==hardwareID&&sensorProcessorIDs[hardwareIDLoopCount]==sensorProcessorID)
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
                sensorProcessorIDs[indexToWriteCount]=sensorProcessorIDs[removeLoopCount];
                holdStatus[indexToWriteCount]=holdStatus[removeLoopCount];        
                sensorProcessorFactoryClasses[indexToWriteCount]=sensorProcessorFactoryClasses[removeLoopCount];
                factoryArray[indexToWriteCount] = factoryArray[removeLoopCount];
//                sensorArray[indexToWriteCount] = sensorArray[removeLoopCount];      
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
        this.sensorProcessorIDs = Arrays.copyOf(sensorProcessorIDs, hardwareIDs.length - indexesToRemove.length);
        this.holdStatus = Arrays.copyOf(holdStatus, hardwareIDs.length - indexesToRemove.length); 
        this.sensorProcessorFactoryClasses = Arrays.copyOf(sensorProcessorFactoryClasses, hardwareIDs.length - indexesToRemove.length);
        this.factoryArray = Arrays.copyOf(factoryArray, hardwareIDs.length - indexesToRemove.length);
//        this.sensorArray = Arrays.copyOf(sensorArray, hardwareIDs.length - indexesToRemove.length);         
    }

    /** Unsupported - Remove all sensor processors associated with the specified hardwareID. Removes the operational sensor processor and the registry listing.
     *  Note: All add and remove operations are synchronized to avoid conflict of using the same counter variables while processing (this is correctable with more variables, however, it is not anticipated during the prototype implementation that adding and removing of registered sensor processors will be performed while the robot is processing sensors, if necessary this can be modified later to ensure new processors can be added/removed while processing occurs, but for the time being this is the implementation in the prototype Obviously we do not want sensor processing instructions being performed and having the entire robots sensor pause temporarily while the robot is in sensor (That said the pause would be at the microsecond level and likely would not be noticable unless the robot were under a high CPU utilization at the time of the add/remove sensor processor to notice. Additional potential conflicts could come from using the following methods that would need to be synchronized if add/remove methods are not synchronized: getNextSensorIDByHardwareID, and validateUnusedSensorProcesserID. This information is provided to assist the person that undoubtedly will be updating the methods to run without interference in sensor processor process method execution/interference (This undoubtedly will be an issue on somebodies robot platform in the unforseen future.) An additional note is fewer variables means better power efficiency in most cases, therefore, it is important to remember most implementations had efficiency and power consumption in mind when implementing the API the way it implemented for the DRC competition. Another note: the processing arrays need to be reconstructed in the current implementation which is the primary reason this method is synchronized (just remembered when implementing remove.), however, it was an intentional trade because the array data accesses are 3 to 8 times faster then collection object gets when repeatedly accessed (as in this implementation).
     *
     * @param hardwareID 
     */
    public synchronized void remove_save(int hardwareID)
    {
        // TODO: If/when the registry file type changes to DB or key format changes this method will need to be rewritten it is a prototype and the add/remove methods are fairly inefficient due to the multiple looping mechanisms used to obtain hardware id groupings of sensor processors.
        // loop through int[] obtained by updateArrayElementIndexesLinkedToHardwareID(int hardwareID) to remove/generate all keys for removing the registry methods. The processing array removal update will need to be performed in the code following registry removal.
        // Remove the sensor processor from the registry...
        // update the private variable retIndexArray with the array indexes of all sensor processors associated with hardwareID.
        updateArrayElementIndexesLinkedToHardwareID(hardwareID);
        // call remove on each sensor processor in the registry.
        // reset the removeLoopCount to 0
        this.removeLoopCount=0;
        while(removeLoopCount<retIndexArray.length)
        {
            // if item is not found in the registry or an exception occurs remove returns false, popup a System message for debugging purposes...
            if(!SensorProcessorManagerImpl.registry.remove(String.valueOf(hardwareID) + "." + String.valueOf(sensorProcessorIDs[retIndexArray[removeLoopCount]])))
            {
                System.out.println("Registry key: " + String.valueOf(hardwareID) + "." + String.valueOf(sensorProcessorIDs[retIndexArray[removeLoopCount]]) + " could not be removed, more likely then not because it does not exist in the registry (probably a naming problem therefore bug in coding).");
            }
            // increment the while loop count
            this.removeLoopCount=removeLoopCount + 1;
        }
        // Remove hardwareID related elements from processing arrays, and reconstruct to new array sizes.
        removeProcessingArrayElementsByIndexes(retIndexArray);      
    }    
     
    /** Unsupported - Remove all sensor processors associated with the specified hardwareID. Removes the operational sensor processor in memory, and does not modify the startup parameter.
     *  Note: All add and remove operations are synchronized to avoid conflict of using the same counter variables while processing (this is correctable with more variables, however, it is not anticipated during the prototype implementation that adding and removing of registered sensor processors will be performed while the robot is processing sensors, if necessary this can be modified later to ensure new processors can be added/removed while processing occurs, but for the time being this is the implementation in the prototype Obviously we do not want sensor processing instructions being performed and having the entire robots sensor pause temporarily while the robot is in sensor (That said the pause would be at the microsecond level and likely would not be noticable unless the robot were under a high CPU utilization at the time of the add/remove sensor processor to notice. Additional potential conflicts could come from using the following methods that would need to be synchronized if add/remove methods are not synchronized: getNextSensorIDByHardwareID, and validateUnusedSensorProcesserID. This information is provided to assist the person that undoubtedly will be updating the methods to run without interference in sensor processor process method execution/interference (This undoubtedly will be an issue on somebodies robot platform in the unforseen future.) An additional note is fewer variables means better power efficiency in most cases, therefore, it is important to remember most implementations had efficiency and power consumption in mind when implementing the API the way it implemented for the DRC competition. Another note: the processing arrays need to be reconstructed in the current implementation which is the primary reason this method is synchronized (just remembered when implementing remove.), however, it was an intentional trade because the array data accesses are 3 to 8 times faster then collection object gets when repeatedly accessed (as in this implementation).
     *
     * @param hardwareID 
     */
    @Override
    public synchronized void remove(int hardwareID)
    {
        // TODO: If/when the registry file type changes to DB or key format changes this method will need to be rewritten it is a prototype and the add/remove methods are fairly inefficient due to the multiple looping mechanisms used to obtain hardware id groupings of sensor processors.
        // loop through int[] obtained by updateArrayElementIndexesLinkedToHardwareID(int hardwareID) to update retIndexArray. The processing array removal update will need to be performed in the code following registry removal.
        // Remove the sensor processor from the registry...
        // update the private variable retIndexArray with the array indexes of all sensor processors associated with hardwareID.
        updateArrayElementIndexesLinkedToHardwareID(hardwareID);
        // Remove hardwareID related elements from processing arrays, and reconstruct to new array sizes.
        removeProcessingArrayElementsByIndexes(retIndexArray);      
    }    
    
    /**  Unsupported - Remove the specified sensor processor by hardwareID/sensorProcessorID 
     * in memory, and registry. Warning, this method will reinstantiate the SensorProcessorPool, and all SensorProcessors, which could cause issues on in process data.
     * 
     * @param hardwareID
     * @param sensorProcessorID  
     */    
    public synchronized void remove_save(int hardwareID, int sensorProcessorID)
    {
        // if item is not found in the registry or an exception occurs remove returns false, popup a System message for debugging purposes...
        if(!SensorProcessorManagerImpl.registry.remove(String.valueOf(hardwareID) + "." + String.valueOf(sensorProcessorID)))
        {
            System.out.println("Registry key: " + String.valueOf(hardwareID) + "." + String.valueOf(sensorProcessorID) + " could not be removed, more likely then not because it does not exist in the registry (probably a naming problem therefore bug in coding).");
        }
        try
        {
            removeValueArray[0] = getSensorProcessorIndex(hardwareID, sensorProcessorID);
            // Remove hardwareID related elements from processing arrays, and reconstruct to new array sizes.
            removeProcessingArrayElementsByIndexes(removeValueArray);
            // Initialize the SensorProcessorPool
            SensorProcessorManagerImpl.pool = new SensorProcessorPool(factoryArray,SensorProcessorManagerImpl.SENSOR_PROCESSOR_OBJECTS_PER_RANGE);              
        }
        catch(ArrayIndexOutOfBoundsException e ) 
        { 
            System.out.println("Method: remove(int hardwareID, int sensorProcessorID) could not locate the passed in hardwareID/sensorProcessorID in the common arrays.");
        }       
    }       
    
    /** Unsupported - Remove the specified sensor processor by hardwareID/sensorProcessorID 
     * in memory, and will not remove from registry. Warning, this method will reinstantiate the SensorProcessorPool, and all SensorProcessors, which could cause issues on in process data.
     * 
     * @param hardwareID
     * @param sensorProcessorID  
     */    
    @Override
    public synchronized void remove(int hardwareID, int sensorProcessorID)
    {
        // Remove sensor processor from memory.
        try
        {
            removeValueArray[0] = getSensorProcessorIndex(hardwareID, sensorProcessorID);
            // Remove hardwareID related elements from processing arrays, and reconstruct to new array sizes.
            removeProcessingArrayElementsByIndexes(removeValueArray);
            // Initialize the SensorProcessorPool
            SensorProcessorManagerImpl.pool = new SensorProcessorPool(factoryArray,SensorProcessorManagerImpl.SENSOR_PROCESSOR_OBJECTS_PER_RANGE);      
        }
        catch(ArrayIndexOutOfBoundsException e ) 
        { 
            System.out.println("Method: remove(int hardwareID, int sensorProcessorID) could not locate the passed in hardwareID/sensorProcessorID in the common arrays.");
        }       
    }   
    
    /** Checks out the specified sensor processor by hardwareID, sensorProcessorID. All checked out sensor processors must be checked back in because they decrement the total pool count each checkout. Returns null if an associated sensor processor is not registered.
     * @param hardwareID 
     * @param sensorProcessorID 
     * @return SensorProcessor
     */
    @Override
    public SensorProcessor checkout(int hardwareID, int sensorProcessorID)
    {
        // Some of these global variables are shared by getSensorProcessorIndex, which is implemented in remove.
        this.hardwareIDLoopCount=0;
        while(hardwareIDLoopCount<hardwareIDs.length)
        {
            if(hardwareIDs[hardwareIDLoopCount]==hardwareID&&sensorProcessorIDs[hardwareIDLoopCount]==sensorProcessorID)
            {
                this.sidIndex=hardwareIDLoopCount;
                this.hardwareIDLoopCount=hardwareIDs.length;
            }
            // increment the loop count
            this.hardwareIDLoopCount = hardwareIDLoopCount + 1;
        }    
        return SensorProcessorManagerImpl.pool.checkout(sidIndex);
    }

    /** Checks out the specified sensor processor by hardwareID, sensorProcessorID. All checked out sensor processors must be checked back in because they decrement the total pool count each checkout. Returns null if an associated sensor processor is not registered.
     * @param sid - sensor processor id, is the index order the hardwareID, and sensor processor id are registered. Much more efficient then calling any other method, aside from using the pool variable itself to checkout.  
     * @return SensorProcessor
     */
    @Override
    public SensorProcessor checkout(int sid)
    {
        return SensorProcessorManagerImpl.pool.checkout(sid);
    }
    
    /** Checks in the specified checked out sensor processor by cid, that can be obtained by calling SensorProcessor.cid. 
     * @param cid - Cache Pool ID sensor processor id, is the index order the hardwareID, and sensor processor id are registered. Much more efficient then calling any other method, aside from using the pool variable itself to checkin.  
     */
    @Override
    public void checkin(int cid)
    {
        SensorProcessorManagerImpl.pool.checkin(cid);
    }       
    
    /** Hold execution of all sensor processors on specified hardwareID. All registered sensor processors will be excluded from modifying sensor instructions on the specified hardwareID until continuer is called.
     * @param hardwareID 
     */
    @Override
    public void hold(int hardwareID)
    {
        int hold_loop=0;
         while(hold_loop<hardwareIDs.length)
         {
             if(hardwareID==hardwareIDs[hold_loop])
             {
                 holdStatus[hold_loop]=true;
             }
             hold_loop=hold_loop + 1;
         }
    }
 
    /** Hold execution of the specified sensor processor for the specified hardwareID. The sensor processor will be excluded from modifying sensor instructions until continuer is called.
     * @param hardwareID
     * @param sensorProcessorID  
     */
    @Override
    public void hold(int hardwareID, int sensorProcessorID)
    {
         int hold_loop=0;
         while(hold_loop<hardwareIDs.length)
         {
             if(hardwareID==hardwareIDs[hold_loop]&&sensorProcessorIDs[hold_loop]==sensorProcessorID)
             {
                 holdStatus[hold_loop]=true;
                 hold_loop=hardwareIDs.length;
                 break;
             }
             hold_loop=hold_loop + 1;
         } 
    }

    /** Continue execution of all sensor processors on the specified hardwareID. If continuer is called on an already functional not on hold sensor processor, the sensor processor will continue to operate normally.
     * @param hardwareID 
     */
    @Override
    public void continuer(int hardwareID)
    {
        int hold_loop=0;
         while(hold_loop<hardwareIDs.length)
         {
             if(hardwareID==hardwareIDs[hold_loop])
             {
                 holdStatus[hold_loop]=false;
             }
             hold_loop=hold_loop + 1;
         }        
    }    
    
    /** Continue execution of the specified sensor processor on the specified hardwareID. If continuer is called on an already functional not on hold sensor processor, the sensor processor will continue to operate normally.
     * @param hardwareID
     * @param sensorProcessorID  
     */
    @Override
    public void continuer(int hardwareID, int sensorProcessorID)
    {
         int hold_loop=0;
         while(hold_loop<hardwareIDs.length)
         {
             if(hardwareID==hardwareIDs[hold_loop]&&sensorProcessorIDs[hold_loop]==sensorProcessorID)
             {
                 holdStatus[hold_loop]=false;
                 hold_loop=hardwareIDs.length;
                 break;
             }
             hold_loop=hold_loop + 1;
         }         
    }

    /** Start the sensor processor manager run loop. */
    @Override
    public void start()
    {

    }

    /** Stop this object - stop the sensor processor manager run loop. */
    @Override
    public void stop()
    {


    }

    /** Recycle this object to start state for reuse. */
    @Override
    public void recycle()
    {

    }

    /** Return boolean for is on hold on specified Sensor Processor by hardwareID/sensorProcessorID. Returns false if hardwareID, and sensorProcessorID are not found.
     * @param hardwareID 
     * @param sensorProcessorID
     * @return boolean 
     */
    // TODO: Implement throw an exception on hardwareID, and sensorProcessorID not found.
    @Override
    public boolean isOnHold(int hardwareID, int sensorProcessorID)
    {
         int hold_loop=0;
         while(hold_loop<hardwareIDs.length)
         {
             if(hardwareID==hardwareIDs[hold_loop]&&sensorProcessorIDs[hold_loop]==sensorProcessorID)
             {
                 return this.holdStatus[hold_loop];
             }
             hold_loop=hold_loop + 1;
         }   
         return false;
    }
    
    /** Return a list (int[][]) of processing sensor processors, the returned list excludes all onHold or registered but non-functional sensor processors. The returned list is an int[][] the format of each element as int[] where int[0]=hardwareID, int[1]=sensorProcessorID.
     * @return int[][] The structure of each element as int[] where 
     * int[0]=hardwareID, int[1]=sensorProcessorID.
     */
    @Override
    public int[][] getProcessingSensorProcessorIDs()
    {
        // Implement method to return sensor processor ID status.
        int count=0;
        int run_count=0;
        // Return sensor processor IDs.
        if(processingSensorProcessorIDs==null||processingSensorProcessorIDs.length!=hardwareIDs.length)
        {
            this.processingSensorProcessorIDs=new int[hardwareIDs.length][2];
        }
        // Loop through registeredSensorProcessorIDsStatus
        while(count<processingSensorProcessorIDs.length)
        {
            if(holdStatus[count]==false)
            {
                processingSensorProcessorIDs[run_count][0]=hardwareIDs[count];
                processingSensorProcessorIDs[run_count][1]=sensorProcessorIDs[count];
                run_count=run_count + 1;
            }   
            count=count + 1;
        }
        final int[][] result = new int[run_count][2];
        for (int i = 0; i < run_count; i++) 
        {
            result[i][0]=processingSensorProcessorIDs[i][0];
            result[i][1]=processingSensorProcessorIDs[i][1];        
        }
        return result;    
    }    
    

    
    /** Return a list (int[][]) of registered sensor processors, in the format of each element as int[] where int[0]=hardwareID, int[1]=sensorProcessorID.
     * @return int[][] The format of each element as int[] where int[0]=hardwareID, int[1]=sensorProcessorID.
     */
    @Override
    public int[][] getRegisteredSensorProcessorIDs()
    {
        int count=0;
        // Return sensor processor IDs.
        if(registeredSensorProcessorIDs==null||registeredSensorProcessorIDs.length!=hardwareIDs.length)
        {
            this.registeredSensorProcessorIDs=new int[hardwareIDs.length][2];
        }
        // Loop through sensorProcessorIDs
        while(count<registeredSensorProcessorIDs.length)
        {
            registeredSensorProcessorIDs[count][0]=hardwareIDs[count];
            registeredSensorProcessorIDs[count][1]=sensorProcessorIDs[count];
            count=count + 1;
        }
        
        return registeredSensorProcessorIDs;
    }
 
    /** Return a list (int[][]) of registered sensor processors and isOnHoldStatus(), in the format of each element as int[] where int[0]=hardwareID, int[1]=sensorProcessorID, int[2]=boolean is on hold.
     * @return int[][] The format of each element as int[] where int[0]=hardwareID, int[1]=sensorProcessorID, int[2]=boolean is on hold, where 0=false, and 1=true.
     */
    @Override
    public int[][] getRegisteredSensorProcessorsIDsStatus()
    {
        // Implement method to return sensor processor ID status.
        int count=0;
        // Return sensor processor IDs.
        if(registeredSensorProcessorIDsStatus==null||registeredSensorProcessorIDsStatus.length!=hardwareIDs.length)
        {
            this.registeredSensorProcessorIDsStatus=new int[hardwareIDs.length][3];
        }
        // Loop through registeredSensorProcessorIDsStatus
        while(count<registeredSensorProcessorIDsStatus.length)
        {
            registeredSensorProcessorIDsStatus[count][0]=hardwareIDs[count];
            registeredSensorProcessorIDsStatus[count][1]=sensorProcessorIDs[count];
            // convert int to boolean
            registeredSensorProcessorIDsStatus[count][2]=holdStatus[count] ? 1 : 0;  
            count=count + 1;            
        }
        return registeredSensorProcessorIDsStatus;
    }    
    
    /** Return the SensorProcessorFactoryArray for obtaining Class type associated with hid/sid. Index will match element indice of hid/sid. */
    @Override
    public Class[] getSensorProcessorFactoryClassArray()
    {
        return sensorProcessorFactoryClasses;
    }
    
    /** Return int[] of input Class element indices. */
    @Override
    public int[] getSensorProcessorFactoryClassIndicesByClassname(Class class_type)
    {
        Class[] classes1=getSensorProcessorFactoryClassArray();
        int[] elementIDs=new int[classes1.length];
        int count=0;
        for(int i=0;i<classes1.length;i++)
        {
            if(classes1[i].getName().equals(class_type.getName()))
            {
                elementIDs[count]=i;
                count = count + 1;
            }
        }
        return Arrays.copyOf(elementIDs, count);
    }
    
    /** write registry add/removes to properties file. Utility function. */
    public void writeRegistry()
    {
        SensorProcessorManagerImpl.registry.writeFile();
    }       
}