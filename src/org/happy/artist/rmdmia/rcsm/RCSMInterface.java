package org.happy.artist.rmdmia.rcsm;

import org.happy.artist.rmdmia.Controller;
import org.happy.artist.rmdmia.instruction.InstructionDefinition;

/** The Robot Control System Messenger communication messaging 
 * interface/API between the Robot Hardware Controller and the Robotic 
 * Mission Decision Manager Intelligent Agent (RMDMIA).
 * 
 * @author Happy Artist
 * 
 * @copyright Copyright © 2012-2015 Happy Artist. All rights reserved.
 */
public interface RCSMInterface 
{
    /** Return this plugin's RCSM ID.
     * @return int The RCSM ID.
     */
    public int getID();
    /** Set the RCSM ID. This most likely will not be a published method in 
     * the release version of this Interface. Called automatically by RCSMManager
     * @param rcsm_id 
     */
    public void setID(int rcsm_id); 
    
    /** The Controller ID is set automatically by the ControllerManager.
     * Controller implementers of the RCSMProvider do not need to know anything 
     * about this method. Automatically set by the RCSMManager.
     * 
     * @param controller the Controller of this instance of the RCSM.
     */
    public void setController(Controller controller);
    
    /** Return the Controller. Used by the Provider to obtain a reference 
     * to the Controller.
     * 
     * @return Controller reference.
     */
    public Controller getController();
    
    /** Set the RCSM Provider's Properties Object.
     * @param properties java.util.Properties
     */ 
    public void setProperties(java.util.Properties properties); 
    
    /** Return the RCSM Plugin's associated Properties Object.
     * @return java.util.Properties
     */
    public java.util.Properties getProperties();
    
    /** Set the RCSM Provider's Properties File Path.
     * @param filePath String The Properties File Path.
     */ 
    public void setPropertiesFilePath(String filePath);    
    
    /** Return the RCSM Plugin's associated Properties File Path.
     * @return String The Properties File Path.
     */
    public String getPropertiesFilePath();    
    
    /** Return the RCSM Provider implementation version #. Default is 0.
     * @return double RCSM Provider version #. 
     */
    public double getVersion();
    /** Return the RCSM Provider implementation name.
     * @return String RCSM Provider name.
     */
    public String getName(); 
            
    /** RCSM Provider initialization is called automatically by RCSMManager. 
     * Or manually via a RMDMIA Control Panel. Throws RCSMException as an 
     * Exception wrapper class for throwing Exceptions. 
     * @throws RCSMException an Exception wrapper class for throwing Exceptions. 
     */    
    public void initialize() throws RCSMException;
    
    /** Return boolean is initialized on RCSM Provider. 
     * @return boolean returns false if RCSM Provider is not initialized. 
     */    
    public boolean isInitialized();  
    
    /** onInitialized is called by RCSMManager following all Provider 
     * initialization. Is implemented as abstract method and must be overridden
     * to use in Provider implementation.
     */    
    public void onInitialized();
    
    /** Recycle the RCSM Plugin back to its initialization state. without reinitializing the internal Objects (an Object reset switch). Returns true if provider was recycled.
     * @return boolean true on success.
     */
    public boolean recycle();
    /** Shutdown the RCSM Plugin. */   
    public void shutdown();
    
    /** Return a new RCSM InstructionDefinition. Implementations of Instruction, 
     * and I/O handlers are RCSM implementation specific. Called by InstructionObjectRegistry 
     * populate InstructionDefinition Object associated with the provider.   
     * 
     * @return InstructionDefinition
     */
    public abstract InstructionDefinition getInstructionDefinition();    
}
