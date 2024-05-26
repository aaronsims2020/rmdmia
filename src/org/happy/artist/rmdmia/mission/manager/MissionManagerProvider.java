package org.happy.artist.rmdmia.mission.manager;

import java.util.Properties;
import org.happy.artist.rmdmia.Controller;

/** Abstract class for Mission Manager provider implementors. The MissionManagerProvider class 
 * initializes the Mission Manager Provider into the RMDMIA system via an automatic 
 * provider identifier, and registration of Provider Properties into the file system. 
 * A Mission Manager Provider jar can provide a default Properties file into the RMDMIA 
 * Mission Manager Provider Jar distribution, that will automatically copy and load the 
 * Properties file to the RMDMIA implementation file system for non-default 
 * properties modification. RMDMIA systems that do not provide access to the 
 * file system will use the default Mission Manager Provider Properties as defined in the 
 * Mission Manager JAR distribution.
 * 
 * The Mission Manager manages execution of top level mission, multiple sub-missions, 
 * and tasks in the RMDMIA. 
 * The Perception Engine accesses the Mission Manager for Mission Object Analysis.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2014 Happy Artist. All rights reserved.
 * 
 */
public abstract class MissionManagerProvider implements MissionManagerInterface
{
    private int mmID=-1;
    private Controller controller;
    private String filePath="";    
    private java.util.Properties properties;
    
    /** Return the Mission Manager ID. 
     * 
     * @return int Mission Manager ID.
     */
    public int getID() 
    {
        return mmID;
    }

    /** set the Mission Manager ID. 
     * 
     */
    public void setID(int mm_id) 
    {
        this.mmID=mm_id;
    }

    /** The Controller ID is set automatically by the ControllerManager.
     * Controller implementers of the MissionManagerProvider do not need to know anything 
     * about this method. Automatically set by the Mission Manager.
     * 
     * @param controller the Controller of this instance of this RMDMIA Mission Manager.
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
     * to a global properties document with the Mission Manager ID appended to the front of 
     * each associated property key with a "." separator in the name to identify 
     * all properties associated with that Mission Manager provider. Mission Manager Provider 
     * constructors are empty, and therefore the properties is an option to add Properties 
     * at startup.
     * 
     * @param properties java.util.Properties
     * */
    public void setProperties(Properties properties) 
    {
        this.properties=properties;
    }    

    /** Return the Mission Manager Provider Properties Object. Managed by the 
     * Mission Manager.
     * 
     *@return java.util.Properties  
     */
    public Properties getProperties() 
    {
        return properties;
    }       
    
    /** Set the Mission Manager Provider's Properties File Path.
     * 
     * @param filePath String The Properties File Path.
     */ 
    public void setPropertiesFilePath(String filePath)
    {
        this.filePath=filePath;
    }
    
    /** Return the Mission Manager Provider's associated Properties File Path.
     * 
     * @return String The Properties File Path.
     */
    public String getPropertiesFilePath()
    {
        return filePath;
    }    

    /** onInitialized is called by Mission Manager following all Provider 
     * initialization. Implementing as a do nothing method that must be 
     * overridden to use. .
     */    
    public void onInitialized(){}    
}
