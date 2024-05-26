package org.happy.artist.rmdmia.instruction;

import java.util.Properties;
import org.happy.artist.rmdmia.Controller;

/** Abstract class for Instruction Manager provider implementors. The InstructionManagerProvider class 
 * initializes the Instruction Manager Provider into the RMDMIA system via an automatic 
 * provider identifier, and registration of Provider Properties into the file system. 
 * A Instruction Manager Provider jar can provide a default Properties file into the RMDMIA 
 * Instruction Manager Provider Jar distribution, that will automatically copy and load the 
 * Properties file to the RMDMIA implementation file system for non-default 
 * properties modification. RMDMIA systems that do not provide access to the 
 * file system will use the default Instruction Manager Provider Properties as defined in the 
 * Instruction Manager JAR distribution.
 * 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2014 Happy Artist. All rights reserved.
 * 
 */
public abstract class InstructionManagerProvider implements InstructionManagerInterface
{
    private int imID=-1;
    private Controller controller;
    private String filePath="";    
    private java.util.Properties properties;
    
    /** Return the Instruction Manager ID. 
     * 
     * @return int Instruction Manager ID.
     */
    public int getID() 
    {
        return imID;
    }

    /** set the Instruction Manager ID. 
     * 
     */
    public void setID(int im_id) 
    {
        this.imID=im_id;
    }

    /** The Controller ID is set automatically by the ControllerManager.
     * Controller implementers of the InstructionManagerProvider do not need to know anything 
     * about this method. Automatically set by the Instruction Manager.
     * 
     * @param controller the Controller of this instance of this RMDMIA Instruction Manager.
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
     * to a global properties document with the Instruction Manager ID appended to the front of 
     * each associated property key with a "." separator in the name to identify 
     * all properties associated with that Instruction Manager provider. Instruction Manager Provider 
     * constructors are empty, and therefore the properties is an option to add Properties 
     * at startup.
     * 
     * @param properties java.util.Properties
     * */
    public void setProperties(Properties properties) 
    {
        this.properties=properties;
    }    

    /** Return the Instruction Manager Provider Properties Object. Managed by the 
     * Instruction Manager.
     * 
     *@return java.util.Properties  
     */
    public Properties getProperties() 
    {
        return properties;
    }       
    
    /** Set the Instruction Manager Provider's Properties File Path.
     * 
     * @param filePath String The Properties File Path.
     */ 
    public void setPropertiesFilePath(String filePath)
    {
        this.filePath=filePath;
    }
    
    /** Return the Instruction Manager Provider's associated Properties File Path.
     * 
     * @return String The Properties File Path.
     */
    public String getPropertiesFilePath()
    {
        return filePath;
    }
    
    /** onInitialized is called by Instruction Manager following all Provider 
     * initialization. Implementing as a do nothing method that must be 
     * overridden to use. .
     */    
    public void onInitialized(){}  
    
   /** Return the InstructionDefinition by hid. */
   public abstract InstructionDefinition getInstructionDefinition(int hid);
   
   /** Return the InstructionDefinition by name. */
   public abstract InstructionDefinition getInstructionDefinition(String name);   
   
   /** Return hid by instruction name.
    * @param name String the name of the instruction.
    * @return int hid. -1 id instruction definition for name not defined.
    */
   public abstract int getHidByName(String name);
   
   /** Return hid by instruction name.
    * @param hid int of the instruction.
    * @return String instruction name. null returned if hid not defined.
    */
   public abstract String getNameByHid(int hid); 
}
