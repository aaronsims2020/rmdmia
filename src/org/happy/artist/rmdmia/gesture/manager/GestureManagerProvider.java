package org.happy.artist.rmdmia.gesture.manager;

import java.util.Properties;
import org.happy.artist.rmdmia.Controller;
import org.happy.artist.rmdmia.movement.MovementProcessorManager;

/** Abstract class for Gesture Manager provider implementors. The GestureManagerProvider class 
 * initializes the Gesture Manager Provider into the RMDMIA system via an automatic 
 * provider identifier, and registration of Provider Properties into the file system. 
 * A Gesture Manager Provider jar can provide a default Properties file into the RMDMIA 
 * Gesture Manager Provider Jar distribution, that will automatically copy and load the 
 * Properties file to the RMDMIA implementation file system for non-default 
 * properties modification. RMDMIA systems that do not provide access to the 
 * file system will use the default Gesture Manager Provider Properties as defined in the 
 * Gesture Manager JAR distribution.
 * 
 * The Gesture Manager manages execution of Gestures, Dynamic Control Gestures.
 * Dynamic Control Gestures add the Movement Processor via the MovementProcessorManager
 * functionality. Movement Processor implements algorithmic movement control for 
 * movement calibration and movement based event launching. Algorithms built into 
 * movement processors are intended to increase/decrease numeric input parameters 
 * for movement updates in the RMDMIA. 
 * 
 * The Perception Engine accesses the Gesture Manager for Gesture Object Analysis.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2014 Happy Artist. All rights reserved.
 * 
 */
public abstract class GestureManagerProvider implements GestureManagerInterface
{
    private int gmID=-1;
    private Controller controller;
    private String filePath="";    
    private java.util.Properties properties;
    private MovementProcessorManager movementProcessorManager;    
    
    /** Return the Gesture Manager ID. 
     * 
     * @return int Gesture Manager ID.
     */
    public int getID() 
    {
        return gmID;
    }

    /** set the Gesture Manager ID. 
     * 
     */
    public void setID(int gm_id) 
    {
        this.gmID=gm_id;
    }

    /** The Controller ID is set automatically by the ControllerManager.
     * Controller implementers of the GestureManagerProvider do not need to know anything 
     * about this method. Automatically set by the Gesture Manager.
     * 
     * @param controller the Controller of this instance of this RMDMIA Gesture Manager.
     */
    public void setController(Controller controller)
    {
        this.controller=controller;
    }
    
    /** Return the Controller. Used by the Provider to obtain a reference 
     * to the Controller.
     * 
     * @return Controller reference.
     */
    public Controller getController()
    {
        return controller;
    }   
    
    /** The setProperties class is used to load the default Properties at 
     *  provider install time. At load time the Properties will be transferred to 
     * either a properties file in the configuration directory, and/or appended 
     * to a global properties document with the Gesture Manager ID appended to the front of 
     * each associated property key with a "." separator in the name to identify 
     * all properties associated with that Gesture Manager provider. Gesture Manager Provider 
     * constructors are empty, and therefore the properties is an option to add Properties 
     * at startup.
     * 
     * @param properties java.util.Properties
     * */
    public void setProperties(Properties properties) 
    {
        this.properties=properties;
    }    

    /** Return the Gesture Manager Provider Properties Object. Managed by the 
     * Gesture Manager.
     * 
     *@return java.util.Properties  
     */
    public Properties getProperties() 
    {
        return properties;
    }       
    
    /** Set the Gesture Manager Provider's Properties File Path.
     * 
     * @param filePath String The Properties File Path.
     */ 
    public void setPropertiesFilePath(String filePath)
    {
        this.filePath=filePath;
    }
    
    /** Return the Gesture Manager Provider's associated Properties File Path.
     * 
     * @return String The Properties File Path.
     */
    public String getPropertiesFilePath()
    {
        return filePath;
    }    

    /** Set the Movement Processor Manager.  
     * 
     * @param MovementProcessorManager
     */
    public void setMovementProcessorManager(MovementProcessorManager movementProcessorManager)
    {
        this.movementProcessorManager=movementProcessorManager;
    }    
    
    /** Return the Movement Processor Manager the GestureManagerProvider implements. 
     * 
     * @return MovementProcessorManager
     */
    public MovementProcessorManager getMovementProcessorManager()
    {
        return movementProcessorManager;
    }    
    
    /** onInitialized is called by Gesture Manager following all Provider 
     * initialization. Implementing as a do nothing method that must be 
     * overridden to use. .
     */    
    public void onInitialized(){}    
}
