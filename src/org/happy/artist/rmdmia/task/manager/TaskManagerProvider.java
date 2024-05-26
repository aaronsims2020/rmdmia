package org.happy.artist.rmdmia.task.manager;

import java.util.Properties;
import org.happy.artist.rmdmia.Controller;

/** Abstract class for Task Manager provider implementors. The TaskManagerProvider class 
 * initializes the Task Manager Provider into the RMDMIA system via an automatic 
 * provider identifier, and registration of Provider Properties into the file system. 
 * A Task Manager Provider jar can provide a default Properties file into the RMDMIA 
 * Task Manager Provider Jar distribution, that will automatically copy and load the 
 * Properties file to the RMDMIA implementation file system for non-default 
 * properties modification. RMDMIA systems that do not provide access to the 
 * file system will use the default Task Manager Provider Properties as defined in the 
 * Task Manager JAR distribution.
 * 
 * The Task Manager manages execution of individual tasks, and Gestures in the RMDMIA. 
 * The Perception Engine accesses the Task Manager for Task Object Analysis.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2014 Happy Artist. All rights reserved.
 * 
 */
public abstract class TaskManagerProvider implements TaskManagerInterface
{
    private int tmID=-1;
    private Controller controller;
    private String filePath="";    
    private java.util.Properties properties;
    
    /** Return the Task Manager ID. 
     * 
     * @return int Task Manager ID.
     */
    public int getID() 
    {
        return tmID;
    }

    /** set the Task Manager ID. 
     * 
     */
    public void setID(int tm_id) 
    {
        this.tmID=tm_id;
    }

    /** The Controller ID is set automatically by the ControllerManager.
     * Controller implementers of the TaskManagerProvider do not need to know anything 
     * about this method. Automatically set by the Task Manager.
     * 
     * @param controller the Controller of this instance of this RMDMIA Task Manager.
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
     * to a global properties document with the Task Manager ID appended to the front of 
     * each associated property key with a "." separator in the name to identify 
     * all properties associated with that Task Manager provider. Task Manager Provider 
     * constructors are empty, and therefore the properties is an option to add Properties 
     * at startup.
     * 
     * @param properties java.util.Properties
     * */
    public void setProperties(Properties properties) 
    {
        this.properties=properties;
    }    

    /** Return the Task Manager Provider Properties Object. Managed by the 
     * Task Manager.
     * 
     *@return java.util.Properties  
     */
    public Properties getProperties() 
    {
        return properties;
    }       
    
    /** Set the Task Manager Provider's Properties File Path.
     * 
     * @param filePath String The Properties File Path.
     */ 
    public void setPropertiesFilePath(String filePath)
    {
        this.filePath=filePath;
    }
    
    /** Return the Task Manager Provider's associated Properties File Path.
     * 
     * @return String The Properties File Path.
     */
    public String getPropertiesFilePath()
    {
        return filePath;
    }    

    /** onInitialized is called by Task Manager following all Provider 
     * initialization. Implementing as a do nothing method that must be 
     * overridden to use. .
     */    
    public void onInitialized(){}    
}
