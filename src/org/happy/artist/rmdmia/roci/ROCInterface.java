package org.happy.artist.rmdmia.roci;

import java.io.InputStream;
import java.io.OutputStream;
import org.happy.artist.rmdmia.Controller;

/** The Robot Operator Control Interface implemented by operator 
 * controller communication components to translate data going to/from 
 * the RMDMIA via the ROCIManager as a Factory pattern, each ROCI 
 * implementation is a communication translation service between the 
 * operator controller and the RMDMIA (Robotic Mission Decision Manager 
 * Intelligent Agent).
 *
 * Effective non-expert supervised autonomy requires a messaging bridge between 
 * the Robot and the non-expert supervised user. A connection to the robot must 
 * be supported via an external robot control device (examples - phone, 
 * computer, human interaction). The Robot Controller device interfaces directly
 * with the Robot Operator Control Interface Manager (ROCI Manager).
 * 
 * @author Happy Artist
 * 
 * @copyright Copyright © 2012-2013 Happy Artist. All rights reserved.
 */
public interface ROCInterface 
{
    /** Return the data transfer method the ROCIProvider implements. 
     * 0=streaming (setInputStream/setOutputStream methods), 
     * 1=bytes (processIncoming/processOutgoing methods). Default data transfer 
     * method is streaming.
     * 2=none
     * 
     * @return int 0=streaming, 1=bytes, 2=none.
     */
    public int getDataTransferMethod();
    
    /** Set the data transfer method the ROCIProvider implements. 
     * 0=streaming (setInputStream/setOutputStream methods), 
     * 1=bytes (processIncoming/processOutgoing methods), 
     * 2=no data transfer method. 
     * Default data transfer method is no data transfer method.. 
     * 
     * @param type int 0=streaming, 1=bytes, 2=none 
     */
    public void setDataTransferMethod(int type);
    
    /** Set the ROCI Plugin OutputStream. 
     * 
     * @param os The OutputStream Object
     */      
    public void setOutputStream(OutputStream os);
    
    /** Set the ROCI Plugin InputStream. 
     * 
     * @param is The InputStream Object
     */  
    public void setInputStream(InputStream is);
    
    /** Return the set ROCI Plugin InputStream. 
     * 
     * @return InputStream
     */ 
    public InputStream getInputStream();
    
    /** Return the set ROCI Plugin OutputStream. 
     * 
     * @return OutputStream
     */
    public OutputStream getOutputStream();
    
    /** Return this plugin's ROCI ID. 
     * 
     * @return int The ROCI ID.
     */
    public int getID();
    
    /** Set the ROCI ID. This most likely will not be public in the release 
     * version of this Interface. Used by ROCIManager. 
     * 
     * @param roci_id int
     */
    public void setID(int roci_id);    
    
    /** The Controller ID is set automatically by the ControllerManager.
     * Controller implementers of the ROCIProvider do not need to know anything 
     * about this method. Automatically set by the ROCIManager.
     * 
     * @param controller the Controller of this instance of the ROCI.
     */
    public void setController(Controller controller);
    
    /** Return the Controller. Used by the Provider to obtain a reference 
     * to the Controller.
     * 
     * @return Controller reference.
     */
    public Controller getController();
    
    /** Called by ROCI Plugin to push data to RMDMIA. 
     * 
     * @param data The byte[] data.
     */  
    
    public void processIncoming(byte[] data);
    
    /** Called by RMDMIA to push outgoing data. 
     * 
     * @param data The byte[] data.
     */  
    public void processOutgoing(byte[] data);
    
    /** Set the ROCI Provider's Properties Object. 
     * 
     * @param properties java.util.Properties
     */ 
    public void setProperties(java.util.Properties properties);     
    
    /** Return the ROCI Plugin's associated Properties Object.
     * 
     * @return java.util.Properties
     */
    public java.util.Properties getProperties();
    
    /** Set the ROCI Provider's Properties File Path.
     * 
     * @param filePath String The Properties File Path.
     */ 
    public void setPropertiesFilePath(String filePath);    
    
    /** Return the ROCI Plugin's associated Properties File Path.
     * 
     * @return String The Properties File Path.
     */
    public String getPropertiesFilePath();    
        
    
    /** Return the ROCI Provider implementation version #. Default is 0.
     * 
     * @return double ROCI Provider version.
     */
    public double getVersion();
    
    /** Return the ROCI Provider implementation name. 
     * 
     * @return String the ROCI Provider name.
     */
    public String getName();    
    
    /** ROCI Provider initialization is called automatically by 
     * ROCIManager. Or manually via a RMDMIA Control Panel. 
     */    
    public void initialize();

    /** Return boolean is initialized on ROCI Provider. 
     * @return boolean returns false if ROCI Provider is not initialized. 
     */    
    public boolean isInitialized();      
    
    /** onInitialized is called by ROCIManager following all Provider 
     * initialization. Is implemented as abstract method and must be overridden
     * to use in Provider implementation.
     */    
    public void onInitialized();    
    
    /** Recycle the ROCI Plugin back to its initialization state. without 
     * reinitializing the internal Objects (an Object reset switch). Returns 
     * true if provider was recycled. 
     * @return boolean true on success.
     */
    public boolean recycle();
    
    /** Shutdown the ROCI Plugin. */   
    public void shutdown();
}
