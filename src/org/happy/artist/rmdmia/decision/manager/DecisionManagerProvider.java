package org.happy.artist.rmdmia.decision.manager;

import java.util.Properties;
import org.happy.artist.rmdmia.Controller;

/** Abstract class for Decision Manager provider implementors. The DecisionManagerProvider class 
 * initializes the Decision Manager Provider into the RMDMIA system via an automatic 
 * provider identifier, and registration of Provider Properties into the file system. 
 * A Decision Manager Provider jar can provide a default Properties file into the RMDMIA 
 * Decision Manager Provider Jar distribution, that will automatically copy and load the 
 * Properties file to the RMDMIA implementation file system for non-default 
 * properties modification. RMDMIA systems that do not provide access to the 
 * file system will use the default Decision Manager Provider Properties as defined in the 
 * Decision Manager JAR distribution.
 * 
 * The Decision Manager manages Robot/Object interaction, and path to follow upon
 * decision.
 * 
 * Object Interaction scoring criteria includes:
 * Risk - Identified Object interaction risk.
 * Priority - Priority score to current task, or mission.
 * Rules - System, Mission, or Task Rules to evaluate against.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2014 Happy Artist. All rights reserved.
 * 
 */
public abstract class DecisionManagerProvider implements DecisionManagerInterface
{
    private int dmID=-1;
    private Controller controller;
    private String filePath="";    
    private java.util.Properties properties;
    
    /** Return the Decision Manager ID. 
     * 
     * @return int Decision Manager ID.
     */
    public int getID() 
    {
        return dmID;
    }

    /** set the Decision Manager ID. 
     * 
     */
    public void setID(int dm_id) 
    {
        this.dmID=dm_id;
    }

    /** The Controller ID is set automatically by the ControllerManager.
     * Controller implementers of the DecisionManagerProvider do not need to know anything 
     * about this method. Automatically set by the Decision Manager.
     * 
     * @param controller the Controller of this instance of this RMDMIA Decision Manager.
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
     * to a global properties document with the Decision Manager ID appended to the front of 
     * each associated property key with a "." separator in the name to identify 
     * all properties associated with that Decision Manager provider. Decision Manager Provider 
     * constructors are empty, and therefore the properties is an option to add Properties 
     * at startup.
     * 
     * @param properties java.util.Properties
     * */
    public void setProperties(Properties properties) 
    {
        this.properties=properties;
    }    

    /** Return the Decision Manager Provider Properties Object. Managed by the 
     * Decision Manager.
     * 
     *@return java.util.Properties  
     */
    public Properties getProperties() 
    {
        return properties;
    }       
    
    /** Set the Decision Manager Provider's Properties File Path.
     * 
     * @param filePath String The Properties File Path.
     */ 
    public void setPropertiesFilePath(String filePath)
    {
        this.filePath=filePath;
    }
    
    /** Return the Decision Manager Provider's associated Properties File Path.
     * 
     * @return String The Properties File Path.
     */
    public String getPropertiesFilePath()
    {
        return filePath;
    }
    
    /** onInitialized is called by Decision Manager following all Provider 
     * initialization. Implementing as a do nothing method that must be 
     * overridden to use. .
     */    
    public void onInitialized(){}    
}
