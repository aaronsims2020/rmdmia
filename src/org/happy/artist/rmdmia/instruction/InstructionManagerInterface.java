package org.happy.artist.rmdmia.instruction;

import org.happy.artist.rmdmia.Controller;

/** The Instruction Manager Provider Interface for management of Robot/Object 
 * interactions and path to follow upon instruction. in the RMDMIA. A single Instruction 
 * Manager instance will run in the RMDMIA as a Factory pattern, multiple Instruction 
 * Managers may be configured. but only one may be in control of the 
 * RMDMIA (Robotic Mission Instruction Manager Intelligent Agent) Instruction Management at a time.
 * 
 * @author Happy Artist
 * 
 * @copyright Copyright © 2014 Happy Artist. All rights reserved.
 */
public interface InstructionManagerInterface 
{    
    /** Return this provider's Instruction Manager ID. 
     * 
     * @return int The Instruction Manager ID.
     */
    public int getID();
    
    /** Set the Instruction Manager ID. This most likely will not be public in the release 
     * version of this Interface. Used by InstructionManager. 
     * 
     * @param im_id int
     */
    public void setID(int im_id);    
    
    /** The Controller ID is set automatically by the ControllerManager.
     * Controller implementers of the InstructionManagerProvider do not need to know anything 
     * about this method. Automatically set by the InstructionManager.
     * 
     * @param controller the Controller of this instance of the Instruction Manager.
     */
    public void setController(Controller controller);
    
    /** Return the Controller. Used by the Provider to obtain a reference 
     * to the Controller.
     * 
     * @return Controller reference.
     */
    public Controller getController();
        
    /** Set the Instruction Manager Provider's Properties Object. 
     * 
     * @param properties java.util.Properties
     */ 
    public void setProperties(java.util.Properties properties);     
    
    /** Return the Instruction Manager Provider's associated Properties Object.
     * 
     * @return java.util.Properties
     */
    public java.util.Properties getProperties();
    
    /** Set the Instruction Manager Provider's Properties File Path.
     * 
     * @param filePath String The Properties File Path.
     */ 
    public void setPropertiesFilePath(String filePath);    
    
    /** Return the Instruction Manager Provider's associated Properties File Path.
     * 
     * @return String The Properties File Path.
     */
    public String getPropertiesFilePath();    
        
    
    /** Return the Instruction Manager Provider implementation version #. Default is 0.
     * 
     * @return double Instruction Manager Provider version.
     */
    public double getVersion();
    
    /** Return the Instruction Manager Provider implementation name. 
     * 
     * @return String the Instruction Manager Provider name.
     */
    public String getName();    
    
    /** Instruction Manager Provider initialization is called automatically by 
     * InstructionManager. Or manually via a RMDMIA Control Panel. 
     */    
    public void initialize();

    /** Return boolean is initialized on Instruction Manager Provider. 
     * @return boolean returns false if Instruction Manager Provider is not initialized. 
     */    
    public boolean isInitialized();      
    
    /** onInitialized is called by InstructionManager following all Provider 
     * initialization. Is implemented as abstract method and must be overridden
     * to use in Provider implementation.
     */    
    public void onInitialized();
    
    /** Recycle the Instruction Manager Plugin back to its initialization state. without 
     * reinitializing the internal Objects (an Object reset switch). Returns 
     * true if provider was recycled. 
     * @return boolean true on success. 
     */
    public boolean recycle();
    
    /** Shutdown the Instruction Manager Plugin. */   
    public void shutdown();
}
