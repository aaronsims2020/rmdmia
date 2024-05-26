package org.happy.artist.rmdmia.perception.engine;

import org.happy.artist.rmdmia.Controller;
import org.happy.artist.rmdmia.perception.engine.sensor.SensorProcessorManager;
import org.happy.artist.rmdmia.perception.engine.sensor.SensorProcessorManagerImpl;

/** The Robot Operator Control Interface implemented by operator 
 * controller communication components to translate data going to/from 
 * the RMDMIA via the PEManager as a Factory pattern, each Perception Engine 
 * implementation is a communication translation service between the 
 * operator controller and the RMDMIA (Robotic Mission Decision Manager 
 * Intelligent Agent).
 * 
 * @author Happy Artist
 * 
 * @copyright Copyright © 2014 Happy Artist. All rights reserved.
 */
public interface PEInterface 
{    
    /** Return this plugin's Perception Engine ID. 
     * 
     * @return int The PE ID.
     */
    public int getID();
    
    /** Set the Perception Engine ID. This most likely will not be public in the release 
     * version of this Interface. Used by PEManager. 
     * 
     * @param pe_id int
     */
    public void setID(int pe_id);    
    
    /** The Controller ID is set automatically by the ControllerManager.
     * Controller implementers of the PEProvider do not need to know anything 
     * about this method. Automatically set by the PEManager.
     * 
     * @param controller the Controller of this instance of the Perception Engine.
     */
    public void setController(Controller controller);
    
    /** Return the Controller. Used by the Provider to obtain a reference 
     * to the Controller.
     * 
     * @return Controller reference.
     */
    public Controller getController();
        
    /** Set the Perception Engine Provider's Properties Object. 
     * 
     * @param properties java.util.Properties
     */ 
    public void setProperties(java.util.Properties properties);     
    
    /** Return the Perception Engine Plugin's associated Properties Object.
     * 
     * @return java.util.Properties
     */
    public java.util.Properties getProperties();
    
    /** Set the Perception Engine Provider's Properties File Path.
     * 
     * @param filePath String The Properties File Path.
     */ 
    public void setPropertiesFilePath(String filePath);    
    
    /** Return the Perception Engine Plugin's associated Properties File Path.
     * 
     * @return String The Properties File Path.
     */
    public String getPropertiesFilePath();    
        
    
    /** Return the Perception Engine Provider implementation version #. Default is 0.
     * 
     * @return double PE Provider version.
     */
    public double getVersion();
    
    /** Return the Perception Engine Provider implementation name. 
     * 
     * @return String the PE Provider name.
     */
    public String getName();    
    
    /** Perception Engine Provider initialization is called automatically by 
     * PEManager. Or manually via a RMDMIA Control Panel. 
     */    
    public void initialize();

    /** Return boolean is initialized on Perception Engine Provider. 
     * @return boolean returns false if Perception Engine Provider is not initialized. 
     */    
    public boolean isInitialized();      
    
    /** onInitialized is called by PEManager following all Provider 
     * initialization. Is implemented as abstract method and must be overridden
     * to use in Provider implementation.
     */    
    public void onInitialized();    
    
    /** Recycle the Perception Engine Plugin back to its initialization state. without 
     * reinitializing the internal Objects (an Object reset switch). Returns 
     * true if provider was recycled. 
     * @return boolean true on success.
     */
    public boolean recycle();
    
    /** Shutdown the Perception Engine Plugin. */   
    public void shutdown();
    
    /** Set the Sensor Processor Manager.  
     * 
     * @param SensorProcessorManager
     */
    public void setSensorProcessorManager(SensorProcessorManager sensorProcessorManager);    
    
    /** Return the Sensor Processor Manager the PEProvider implements. 
     * 
     * @return SensorProcessorManager
     */
    public SensorProcessorManager getSensorProcessorManager();
}
