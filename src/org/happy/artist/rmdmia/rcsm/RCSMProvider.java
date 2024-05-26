package org.happy.artist.rmdmia.rcsm;

import java.util.Properties;
import org.happy.artist.rmdmia.Controller;
import org.happy.artist.rmdmia.instruction.InstructionDefinition;
import org.happy.artist.rmdmia.rcsm.provider.message.SubscriberMessageManager;

/**
 * An abstract class for RCSM Plugin provider implementors. The RCSMProvider class 
 * initializes the RCSM Plugin Provider into the RMDMIA system via an automatic 
 * plugin identifier, and registration of Plugin Properties into the file system. 
 * A RCSM Provider jar can provide a default Properties file into the RMDMIA 
 * RCSM Provider Jar distribution, that will automatically copy and load the 
 * Properties file to the RMDMIA implementation file system for non-default 
 * properties modification. RMDMIA systems that do not provide access to the 
 * file system will use the default RCSM Provider Properties as defined in the 
 * RCSM JAR distribution.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2013 Happy Artist. All rights reserved.
 * 
 */
public abstract class RCSMProvider implements RCSMInterface
{
    private int rcsmID=-1;
    private String filePath="";
    private java.util.Properties properties;
    private Controller controller;
    
    /** Return the RCSM ID.
     * @return int RCSM ID.
     */
    public int getID() 
    {
        return rcsmID;
    }

    /** set the RCSM ID.
     * @param rcsm_id int
     */
    public void setID(int rcsm_id) 
    {
        this.rcsmID=rcsm_id;
    }

    /** The Controller ID is set automatically by the ControllerManager.
     * Controller implementers of the RCSMProvider do not need to know anything 
     * about this method, because it is automatically set by the RCSMManager.
     * 
     * @param controller the Controller of this instance of the RCSM.
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
     *  plugin install time. At load time the Properties will be transferred to 
     * either a properties file in the configuration directory, and/or appended 
     * to a global properties document with the RCSM_ID appended to the front of 
     * each associated property key with a "." separator in the name to identify 
     * all properties associated with that RCSM plugin. RCSM Plugin constructors 
     * are empty, and therefore the properties is an option to add Properties at 
     * startup.
     *
     * @param properties java.util.Properties
     */
    public void setProperties(Properties properties) 
    {
        this.properties=properties;
    }    

    /** Return the RCSM Properties Object.
     * 
     * @return java.util.Properties 
     */
    public Properties getProperties() 
    {
        return properties;
    }       
    
    /** Set the RCSM Provider's Properties File Path.
     * 
     * @param filePath String The Properties File Path.
     */ 
    public void setPropertiesFilePath(String filePath)
    {
        this.filePath=filePath;
    }
    
    /** Return the RCSM Plugin's associated Properties File Path.
     * 
     * @return String The Properties File Path.
     */
    public String getPropertiesFilePath()
    {
        return filePath;
    }
    
    /** onInitialized is called by RCSMManager following all Provider 
     * initialization. Implementing as a do nothing method that must be 
     * overridden to use. .
     */    
    public void onInitialized(){}   
    
    /**
     * Return a reference to the SubscriberMessageManager.
     *
     * @return SubscriberMessageManager
     */
    public abstract SubscriberMessageManager getSubscriberMessageManager();    
    
    /** Return a new RCSM InstructionDefinition. Implementations of Instruction, 
     * and I/O handlers are RCSM implementation specific. Called by InstructionObjectRegistry 
     * populate InstructionDefinition Object associated with the provider.   
     * 
     * @return InstructionDefinition
     */
    public abstract InstructionDefinition getInstructionDefinition();
}
