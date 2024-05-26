package org.happy.artist.rmdmia.decision.manager;

import org.happy.artist.rmdmia.Controller;

/** The Decision Manager Provider Interface for management of Robot/Object 
 * interactions and path to follow upon decision. in the RMDMIA. A single Decision 
 * Manager instance will run in the RMDMIA as a Factory pattern, multiple Decision 
 * Managers may be configured. but only one may be in control of the 
 * RMDMIA (Robotic Mission Decision Manager Intelligent Agent) Decision Management at a time.
 * 
 * @author Happy Artist
 * 
 * @copyright Copyright © 2014 Happy Artist. All rights reserved.
 */
public interface DecisionManagerInterface 
{    
    /** Return this provider's Decision Manager ID. 
     * 
     * @return int The Decision Manager ID.
     */
    public int getID();
    
    /** Set the Decision Manager ID. This most likely will not be public in the release 
     * version of this Interface. Used by DecisionManager. 
     * 
     * @param dm_id int
     */
    public void setID(int dm_id);    
    
    /** The Controller ID is set automatically by the ControllerManager.
     * Controller implementers of the DecisionManagerProvider do not need to know anything 
     * about this method. Automatically set by the DecisionManager.
     * 
     * @param controller the Controller of this instance of the Decision Manager.
     */
    public void setController(Controller controller);
    
    /** Return the Controller. Used by the Provider to obtain a reference 
     * to the Controller.
     * 
     * @return Controller reference.
     */
    public Controller getController();
        
    /** Set the Decision Manager Provider's Properties Object. 
     * 
     * @param properties java.util.Properties
     */ 
    public void setProperties(java.util.Properties properties);     
    
    /** Return the Decision Manager Provider's associated Properties Object.
     * 
     * @return java.util.Properties
     */
    public java.util.Properties getProperties();
    
    /** Set the Decision Manager Provider's Properties File Path.
     * 
     * @param filePath String The Properties File Path.
     */ 
    public void setPropertiesFilePath(String filePath);    
    
    /** Return the Decision Manager Provider's associated Properties File Path.
     * 
     * @return String The Properties File Path.
     */
    public String getPropertiesFilePath();    
        
    
    /** Return the Decision Manager Provider implementation version #. Default is 0.
     * 
     * @return double Decision Manager Provider version.
     */
    public double getVersion();
    
    /** Return the Decision Manager Provider implementation name. 
     * 
     * @return String the Decision Manager Provider name.
     */
    public String getName();    
    
    /** Decision Manager Provider initialization is called automatically by 
     * DecisionManager. Or manually via a RMDMIA Control Panel. 
     */    
    public void initialize();

    /** Return boolean is initialized on Decision Manager Provider. 
     * @return boolean returns false if Decision Manager Provider is not initialized. 
     */    
    public boolean isInitialized();      
    
    /** onInitialized is called by DecisionManager following all Provider 
     * initialization. Is implemented as abstract method and must be overridden
     * to use in Provider implementation.
     */    
    public void onInitialized();
    
    /** Recycle the Decision Manager Plugin back to its initialization state. without 
     * reinitializing the internal Objects (an Object reset switch). Returns 
     * true if provider was recycled. 
     * @return boolean true on success. 
     */
    public boolean recycle();
    
    /** Shutdown the Decision Manager Plugin. */   
    public void shutdown();
}
