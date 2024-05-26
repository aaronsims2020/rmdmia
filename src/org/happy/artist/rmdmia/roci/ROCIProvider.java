package org.happy.artist.rmdmia.roci;

import java.util.Properties;
import org.happy.artist.rmdmia.Controller;

/** Abstract class for ROCI plugin implementors. The ROCIProvider class 
 * initializes the ROCI Provider Plugin into the RMDMIA system via an automatic plugin 
 * identifier, and registration of Plugin Properties into the file system. 
 * A ROCI Provider jar can provide a default Properties file into the RMDMIA 
 * ROCI Provider Jar distribution, that will automatically copy and load the 
 * Properties file to the RMDMIA implementation file system for non-default 
 * properties modification. RMDMIA systems that do not provide access to the 
 * file system will use the default ROCI Provider Properties as defined in the 
 * ROCI JAR distribution.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2013 Happy Artist. All rights reserved.
 * 
 */
public abstract class ROCIProvider implements ROCInterface
{
    private int rocID=-1;
    private Controller controller;
    private String filePath="";    
    private java.util.Properties properties;
    /**
     *
     */
    public final static int STREAMING_DATA_TRANSFER_METHOD=0;
    /**
     *
     */
    public final static int BYTES_DATA_TRANSFER_METHOD=1;
    /**
     *
     */
    public final static int NO_TRANSFER_METHOD=2;    
    private int dataTransferMethod=NO_TRANSFER_METHOD;
    
    /** Return the ROCI ID. 
     * 
     * @return int ROCI ID.
     */
    public int getID() 
    {
        return rocID;
    }

    /** set the ROCI ID. 
     * 
     */
    public void setID(int roci_id) 
    {
        this.rocID=roci_id;
    }

    /** The Controller ID is set automatically by the ControllerManager.
     * Controller implementers of the ROCIProvider do not need to know anything 
     * about this method. Automatically set by the ROCIManager.
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
     * to a global properties document with the ROCI_ID appended to the front of 
     * each associated property key with a "." separator in the name to identify 
     * all properties associated with that ROCI plugin. ROCI Plugin constructors 
     * are empty, and therefore the properties is an option to add Properties at 
     * startup.
     * 
     * @param properties java.util.Properties
     * */
    public void setProperties(Properties properties) 
    {
        this.properties=properties;
    }    

    /** Return the ROCI Provider Properties Object. Managed by the 
     * ROCIManager.
     * 
     *@return java.util.Properties  
     */
    public Properties getProperties() 
    {
        return properties;
    }       
    
    /** Set the ROCI Provider's Properties File Path.
     * 
     * @param filePath String The Properties File Path.
     */ 
    public void setPropertiesFilePath(String filePath)
    {
        this.filePath=filePath;
    }
    
    /** Return the ROCI Plugin's associated Properties File Path.
     * 
     * @return String The Properties File Path.
     */
    public String getPropertiesFilePath()
    {
        return filePath;
    }    
    
    /** Return the data transfer method the ROCIProvider implements. 
     * 0=streaming (setInputStream/setOutputStream methods), 
     * 1=bytes (processIncoming/processOutgoing methods). Default data transfer 
     * method is streaming.
     * 
     * @return int 0-streaming, 1=bytes
     */
    public int getDataTransferMethod()
    {
        return dataTransferMethod;
    }

    // TODO: Implement Exception Handling for unsupported ints.
    /** Set the data transfer method the ROCIProvider implements. 
     * 0=streaming (setInputStream/setOutputStream methods), 
     * 1=bytes (processIncoming/processOutgoing methods). Default data transfer 
     * method is streaming.
     * 
     * @param type int 0=streaming, 1=bytes
     */
    public void setDataTransferMethod(int type)
    {
        this.dataTransferMethod=type;
    }
    
    /** onInitialized is called by ROCIManager following all Provider 
     * initialization. Implementing as a do nothing method that must be 
     * overridden to use. .
     */    
    public void onInitialized(){}    
}
