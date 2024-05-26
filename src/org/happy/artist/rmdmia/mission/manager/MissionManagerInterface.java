package org.happy.artist.rmdmia.mission.manager;

import org.happy.artist.rmdmia.Controller;

/** The Mission Manager Provider Interface for management of top level mission, 
 * multiple sub-missions, and tasks in the RMDMIA. A single Mission Manager instance will 
 * run in the RMDMIA as a Factory pattern, multiple Mission Managers may be configured,
 * but only one may be in control of the RMDMIA (Robotic Mission Decision Manager 
 * Intelligent Agent) Mission Management at a time.
 * 
 * @author Happy Artist
 * 
 * @copyright Copyright © 2014 Happy Artist. All rights reserved.
 */
public interface MissionManagerInterface 
{    
    /** Return this provider's Mission Manager ID. 
     * 
     * @return int The Mission Manager ID.
     */
    public int getID();
    
    /** Set the Mission Manager ID. This most likely will not be public in the release 
     * version of this Interface. Used by MissionManager. 
     * 
     * @param mm_id int
     */
    public void setID(int mm_id);    
    
    /** The Controller ID is set automatically by the ControllerManager.
     * Controller implementers of the MissionManagerProvider do not need to know anything 
     * about this method. Automatically set by the MissionManager.
     * 
     * @param controller the Controller of this instance of the Mission Manager.
     */
    public void setController(Controller controller);
    
    /** Return the Controller. Used by the Provider to obtain a reference 
     * to the Controller.
     * 
     * @return Controller reference.
     */
    public Controller getController();
        
    /** Set the Mission Manager Provider's Properties Object. 
     * 
     * @param properties java.util.Properties
     */ 
    public void setProperties(java.util.Properties properties);     
    
    /** Return the Mission Manager Provider's associated Properties Object.
     * 
     * @return java.util.Properties
     */
    public java.util.Properties getProperties();
    
    /** Set the Mission Manager Provider's Properties File Path.
     * 
     * @param filePath String The Properties File Path.
     */ 
    public void setPropertiesFilePath(String filePath);    
    
    /** Return the Mission Manager Provider's associated Properties File Path.
     * 
     * @return String The Properties File Path.
     */
    public String getPropertiesFilePath();    
        
    
    /** Return the Mission Manager Provider implementation version #. Default is 0.
     * 
     * @return double Mission Manager Provider version.
     */
    public double getVersion();
    
    /** Return the Mission Manager Provider implementation name. 
     * 
     * @return String the Mission Manager Provider name.
     */
    public String getName();    
    
    /** Mission Manager Provider initialization is called automatically by 
     * MissionManager. Or manually via a RMDMIA Control Panel. 
     */    
    public void initialize();

    /** Return boolean is initialized on Mission Manager Provider. 
     * @return boolean returns false if Mission Manager Provider is not initialized. 
     */    
    public boolean isInitialized();      
    
    /** onInitialized is called by MissionManager following all Provider 
     * initialization. Is implemented as abstract method and must be overridden
     * to use in Provider implementation.
     */    
    public void onInitialized();    
    
    /** Recycle the Mission Manager Plugin back to its initialization state. without 
     * reinitializing the internal Objects (an Object reset switch). Returns 
     * true if provider was recycled. 
     * @return boolean true on success.
     */
    public boolean recycle();
    
    /** Shutdown the Mission Manager Plugin. */   
    public void shutdown();
}
