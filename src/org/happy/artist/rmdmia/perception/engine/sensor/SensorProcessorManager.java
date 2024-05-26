package org.happy.artist.rmdmia.perception.engine.sensor;

/** SensorProcessorManager.java - Sensor Processor Manager Interface.
 *  Sensor Processors process incoming message data form the RCSM. 
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
public interface SensorProcessorManager
{   
    /** Process the SensorMessage[] and return the sensor 
     *  message. 
     * 
     * @param sensorMessages - The sensor message array to be 
     * processed.
     */
    public SensorMessage[] process(SensorMessage[] sensorMessages);
    
    /** Process the SensorMessage and return the sensor 
     *  message. 
     * 
     * @param sensorMessage - The sensor message to be 
     * processed.
     */
    public SensorMessage process(SensorMessage sensorMsg);
    
    /** Add sensor processor by hardware id. Note: Hardware IDs must be manually registered/defined, and can be virtual, logical, or physical hardware groupings (implementor specific). If a hardware ID was not pre-created the specified input will be created as a new hardware ID on the fly. A return value of -1 means an error occurred.
     * @param hardwareID 
     * @param sensorProcessor
     * @param on_hold boolean
     * @return int sensor id
     */
    public int add(int hardwareID, SensorProcessorFactory sensorProcessor, boolean on_hold);
    
    /** Remove all sensor processors associated with the specified hardwareID. 
     * Removes the operational sensor processor and the registry listing.
     *
     * @param hardwareID 
     */
    public void remove(int hardwareID);
    
    /** Remove the specified sensor processor by hardwareID/sensorProcessorID.
     * @param hardwareID
     * @param sensorProcessorID  
     */    
    public void remove(int hardwareID, int sensorProcessorID);
    
    /** Checks out the specified sensor processor by hardwareID, sensorProcessorID. 
     * All checked out sensor processors must be checked back in because they decrement the 
     * total pool count each checkout. Returns null if an associated sensor processor 
     * is not registered.
     * 
     * @param hardwareID 
     * @param sensorProcessorID 
     * @return SensorProcessor
     */
    public SensorProcessor checkout(int hardwareID, int sensorProcessorID);

    /** Checks out the specified sensor processor by hardwareID, sensorProcessorID. 
     * All checked out sensor processors must be checked back in because they decrement 
     * the total pool count each checkout. Returns null if an associated sensor 
     * processor is not registered.
     * 
     * @param sid - sensor processor id, is the index order the hardwareID, and 
     * sensor processor id are registered. Much more efficient then calling any 
     * other method, aside from using the pool variable itself to checkout.  
     *
     * @return SensorProcessor
     */
    public SensorProcessor checkout(int sid);
    
    /** Checks in the specified checked out sensor processor by cid, that 
     * can be obtained by calling SensorProcessor 
     * <instantiated SensorProcessor Object>.cid.
     * 
     * @param cid - Cache Pool ID sensor processor id, is the index order the 
     * hardwareID, and sensor processor id are registered. Much more efficient 
     * then calling any other method, aside from using the pool variable itself to checkin.  
     */
    public void checkin(int cid);       
    
    /** Hold execution of all sensor processors on specified hardwareID. 
     * All registered sensor processors will be excluded from modifying sensor 
     * instructions on the specified hardwareID until continuer is called.
     * 
     * @param hardwareID 
     */
    public void hold(int hardwareID);
    
    /** Hold execution of the specified sensor processor for the specified 
     * hardwareID. The sensor processor will be excluded from modifying sensor 
     * instructions until continuer is called.
     * 
     * @param hardwareID
     * @param sensorProcessorID  
     */
    public void hold(int hardwareID, int sensorProcessorID);

    /** Continue execution of all sensor processors on the specified hardwareID. 
     * If continuer is called on an already functional not on hold sensor processor, 
     * the sensor processor will continue to operate normally.
     * 
     * @param hardwareID 
     */
    public void continuer(int hardwareID);
    
    /** Continue execution of the specified sensor processor on the specified 
     * hardwareID. If continuer is called on an already functional not on hold 
     * sensor processor, the sensor processor will continue to operate normally.
     * 
     * @param hardwareID
     * @param sensorProcessorID  
     */
    public void continuer(int hardwareID, int sensorProcessorID);

    /** Start the sensor processor manager run loop. */
    public void start();

    /** Stop this object - stop the sensor processor manager run loop. */
    public void stop();

    /** Recycle this object to start state for reuse. */
    public void recycle();

    /** Return boolean for is on hold on specified Sensor Processor by 
     * hardwareID/sensorProcessorID
     * @param hardwareID 
     * @param sensorProcessorID
     * @return boolean 
     */
    public boolean isOnHold(int hardwareID, int sensorProcessorID);
    
    /** Return a list (int[][]) of processing sensor processors, the returned 
     * list excludes all onHold or registered but non-functional sensor 
     * processors. The returned list is an int[][] the format of each element 
     * as int[] where int[0]=hardwareID, int[1]=sensorProcessorID.
     * 
     * @return int[][] The structure of each element as int[] where 
     * int[0]=hardwareID, int[1]=sensorProcessorID.
     */
    public int[][] getProcessingSensorProcessorIDs();
    
    /** Return a list (int[][]) of registered sensor processors, in the format of each element as int[] where int[0]=hardwareID, int[1]=sensorProcessorID.
     * @return int[][] The format of each element as int[] where int[0]=hardwareID, int[1]=sensorProcessorID.
     */
    public int[][] getRegisteredSensorProcessorIDs();
    
    /** Return a list (int[][]) of registered sensor processors and isOnHoldStatus(), in the format of each element as int[] where int[0]=hardwareID, int[1]=sensorProcessorID, int[2]=boolean is on hold.
     * @return int[][] The format of each element as int[] where int[0]=hardwareID, int[1]=sensorProcessorID, int[2]=boolean is on hold.
     */
    public int[][] getRegisteredSensorProcessorsIDsStatus();
    
    /** Return the SensorProcessorFactoryArray for obtaining Class type associated with hid/sid. Index will match element indice of hid/sid. */
    public Class[] getSensorProcessorFactoryClassArray();
    
    /** Return int[] of input Class element indices. */
    public int[] getSensorProcessorFactoryClassIndicesByClassname(java.lang.Class class_type);
            
}