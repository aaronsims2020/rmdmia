package org.happy.artist.rmdmia.gesture.manager;

import org.happy.artist.rmdmia.Controller;
import org.happy.artist.rmdmia.movement.MovementProcessorManager;

/** The Gesture Manager Provider Interface for management of individual gestures, 
 * and Gestures in the RMDMIA. A single Gesture Manager instance will 
 * run in the RMDMIA as a Factory pattern, multiple Gesture Managers may be configured,
 * but only one may be in control of the RMDMIA (Robotic Mission Decision Manager 
 * Intelligent Agent) Gesture Management at a time.
 * 
 * @author Happy Artist
 * 
 * @copyright Copyright © 2014 Happy Artist. All rights reserved.
 */
public interface GestureManagerInterface 
{    
    /** Return this provider's Gesture Manager ID. 
     * 
     * @return int The Gesture Manager ID.
     */
    public int getID();
    
    /** Set the Gesture Manager ID. This most likely will not be public in the release 
     * version of this Interface. Used by GestureManager. 
     * 
     * @param gm_id int
     */
    public void setID(int gm_id);    
    
    /** The Controller ID is set automatically by the ControllerManager.
     * Controller implementers of the GestureManagerProvider do not need to know anything 
     * about this method. Automatically set by the GestureManager.
     * 
     * @param controller the Controller of this instance of the Gesture Manager.
     */
    public void setController(Controller controller);
    
    /** Return the Controller. Used by the Provider to obtain a reference 
     * to the Controller.
     * 
     * @return Controller reference.
     */
    public Controller getController();
        
    /** Set the Gesture Manager Provider's Properties Object. 
     * 
     * @param properties java.util.Properties
     */ 
    public void setProperties(java.util.Properties properties);     
    
    /** Return the Gesture Manager Provider's associated Properties Object.
     * 
     * @return java.util.Properties
     */
    public java.util.Properties getProperties();
    
    /** Set the Gesture Manager Provider's Properties File Path.
     * 
     * @param filePath String The Properties File Path.
     */ 
    public void setPropertiesFilePath(String filePath);    
    
    /** Return the Gesture Manager Provider's associated Properties File Path.
     * 
     * @return String The Properties File Path.
     */
    public String getPropertiesFilePath();    
        
    
    /** Return the Gesture Manager Provider implementation version #. Default is 0.
     * 
     * @return double Gesture Manager Provider version.
     */
    public double getVersion();
    
    /** Return the Gesture Manager Provider implementation name. 
     * 
     * @return String the Gesture Manager Provider name.
     */
    public String getName();    
    
    /** Gesture Manager Provider initialization is called automatically by 
     * GestureManager. Or manually via a RMDMIA Control Panel. 
     */    
    public void initialize();

    /** Return boolean is initialized on Gesture Manager Provider. 
     * @return boolean returns false if Gesture Manager Provider is not initialized. 
     */    
    public boolean isInitialized();      
    
    /** onInitialized is called by GestureManager following all Provider 
     * initialization. Is implemented as abstract method and must be overridden
     * to use in Provider implementation.
     */    
    public void onInitialized();    
    
    /** Set the Movement Processor Manager.  
     * 
     * @param MovementProcessorManager
     */
    public void setMovementProcessorManager(MovementProcessorManager movementProcessorManager);
    
    /** Return the Movement Processor Manager the GestureManagerProvider implements. 
     * 
     * @return MovementProcessorManager
     */
    public MovementProcessorManager getMovementProcessorManager();
    
    /** Recycle the Gesture Manager Plugin back to its initialization state. without 
     * reinitializing the internal Objects (an Object reset switch). Returns 
     * true if provider was recycled. 
     * @return boolean true on success. 
     */
    public boolean recycle();
    
    /** Shutdown the Gesture Manager Plugin. */   
    public void shutdown();
}
