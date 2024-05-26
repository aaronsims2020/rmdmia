package org.happy.artist.rmdmia.perception.engine;

import java.util.Properties;
import org.happy.artist.rmdmia.Controller;
import org.happy.artist.rmdmia.perception.engine.sensor.SensorProcessorManager;

/** Abstract class for Perception Engine plugin implementors. The PEProvider class 
 * initializes the Perception Engine Provider Plugin into the RMDMIA system via an automatic 
 * plugin identifier, and registration of Plugin Properties into the file system. 
 * A Perception Engine Provider jar can provide a default Properties file into the RMDMIA 
 * Perception Engine Provider Jar distribution, that will automatically copy and load the 
 * Properties file to the RMDMIA implementation file system for non-default 
 * properties modification. RMDMIA systems that do not provide access to the 
 * file system will use the default Perception Engine Provider Properties as defined in the 
 * Perception Engine JAR distribution.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2014 Happy Artist. All rights reserved.
 * 
 */
public abstract class PEProvider implements PEInterface
{
    private int peID=-1;
    private Controller controller;
    private String filePath="";    
    private java.util.Properties properties;
    private SensorProcessorManager sensorProcessorManager;
    
    /** Return the Perception Engine ID. 
     * 
     * @return int Perception Engine ID.
     */
    public int getID() 
    {
        return peID;
    }

    /** set the Perception Engine ID. 
     * 
     */
    public void setID(int pe_id) 
    {
        this.peID=pe_id;
    }

    /** The Controller ID is set automatically by the ControllerManager.
     * Controller implementers of the PEProvider do not need to know anything 
     * about this method. Automatically set by the PEManager.
     * 
     * @param controller the Controller of this instance of this RCSM.
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
     * to a global properties document with the PE_ID appended to the front of 
     * each associated property key with a "." separator in the name to identify 
     * all properties associated with that Perception Engine plugin. Perception Engine Plugin constructors 
     * are empty, and therefore the properties is an option to add Properties at 
     * startup.
     * 
     * @param properties java.util.Properties
     * */
    public void setProperties(Properties properties) 
    {
        this.properties=properties;
    }    

    /** Return the Perception Engine Provider Properties Object. Managed by the 
     * PEManager.
     * 
     *@return java.util.Properties  
     */
    public Properties getProperties() 
    {
        return properties;
    }       
    
    /** Set the Perception Engine Provider's Properties File Path.
     * 
     * @param filePath String The Properties File Path.
     */ 
    public void setPropertiesFilePath(String filePath)
    {
        this.filePath=filePath;
    }
    
    /** Return the Perception Engine Plugin's associated Properties File Path.
     * 
     * @return String The Properties File Path.
     */
    public String getPropertiesFilePath()
    {
        return filePath;
    }    
    
    /** Set the Sensor Processor Manager.  
     * 
     * @param SensorProcessorManager
     */
    public void setSensorProcessorManager(SensorProcessorManager sensorProcessorManager)
    {
        this.sensorProcessorManager=sensorProcessorManager;
    }    
    
    /** Return the Sensor Processor Manager the PEProvider implements. 
     * 
     * @return SensorProcessorManager
     */
    public SensorProcessorManager getSensorProcessorManager()
    {
        return sensorProcessorManager;
    }

    /** onInitialized is called by PEManager following all Provider 
     * initialization. Implementing as a do nothing method that must be 
     * overridden to use. .
     */    
    public void onInitialized(){}    
}
