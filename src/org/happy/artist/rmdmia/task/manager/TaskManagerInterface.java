package org.happy.artist.rmdmia.task.manager;

import org.happy.artist.rmdmia.Controller;

/** The Task Manager Provider Interface for management of individual tasks, 
 * and Gestures in the RMDMIA. A single Task Manager instance will 
 * run in the RMDMIA as a Factory pattern, multiple Task Managers may be configured,
 * but only one may be in control of the RMDMIA (Robotic Mission Decision Manager 
 * Intelligent Agent) Task Management at a time.
 * 
 * @author Happy Artist
 * 
 * @copyright Copyright © 2014 Happy Artist. All rights reserved.
 */
public interface TaskManagerInterface 
{    
    /** Return this provider's Task Manager ID. 
     * 
     * @return int The Task Manager ID.
     */
    public int getID();
    
    /** Set the Task Manager ID. This most likely will not be public in the release 
     * version of this Interface. Used by TaskManager. 
     * 
     * @param tm_id int
     */
    public void setID(int tm_id);    
    
    /** The Controller ID is set automatically by the ControllerManager.
     * Controller implementers of the TaskManagerProvider do not need to know anything 
     * about this method. Automatically set by the TaskManager.
     * 
     * @param controller the Controller of this instance of the Task Manager.
     */
    public void setController(Controller controller);
    
    /** Return the Controller. Used by the Provider to obtain a reference 
     * to the Controller.
     * 
     * @return Controller reference.
     */
    public Controller getController();
        
    /** Set the Task Manager Provider's Properties Object. 
     * 
     * @param properties java.util.Properties
     */ 
    public void setProperties(java.util.Properties properties);     
    
    /** Return the Task Manager Provider's associated Properties Object.
     * 
     * @return java.util.Properties
     */
    public java.util.Properties getProperties();
    
    /** Set the Task Manager Provider's Properties File Path.
     * 
     * @param filePath String The Properties File Path.
     */ 
    public void setPropertiesFilePath(String filePath);    
    
    /** Return the Task Manager Provider's associated Properties File Path.
     * 
     * @return String The Properties File Path.
     */
    public String getPropertiesFilePath();    
        
    
    /** Return the Task Manager Provider implementation version #. Default is 0.
     * 
     * @return double Task Manager Provider version.
     */
    public double getVersion();
    
    /** Return the Task Manager Provider implementation name. 
     * 
     * @return String the Task Manager Provider name.
     */
    public String getName();    
    
    /** Task Manager Provider initialization is called automatically by 
     * TaskManager. Or manually via a RMDMIA Control Panel. 
     */    
    public void initialize();

    /** Return boolean is initialized on Task Manager Provider. 
     * @return boolean returns false if Task Manager Provider is not initialized. 
     */    
    public boolean isInitialized();      
    
    /** onInitialized is called by TaskManager following all Provider 
     * initialization. Is implemented as abstract method and must be overridden
     * to use in Provider implementation.
     */    
    public void onInitialized();    
    
    /** Recycle the Task Manager Plugin back to its initialization state. without 
     * reinitializing the internal Objects (an Object reset switch). Returns 
     * true if provider was recycled. 
     * @return boolean true on success.
     */
    public boolean recycle();
    
    /** Shutdown the Task Manager Plugin. */   
    public void shutdown();
}
