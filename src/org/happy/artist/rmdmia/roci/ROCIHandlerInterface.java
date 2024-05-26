package org.happy.artist.rmdmia.roci;

import java.io.InputStream;
import java.io.OutputStream;

 /** This interface directs received messages/data to/from ROCI/Provider 
 * to the proper message destination, and performs transformation 
 * services if needed.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2013 Happy Artist. All rights reserved.
 * 
 */
public interface ROCIHandlerInterface 
{
    // TODO: Finish this implementation after analysis of the design/API. Review the Project Plan. 
    
    /** Return the data transfer method the ROCIProvider implements. 
     * 0=streaming (setInputStream/setOutputStream methods), 
     * 1=bytes (processIncoming/processOutgoing methods). Default data transfer 
     * method is streaming.
     * 
     * @return int 0=streaming, 1=bytes
     */
    public int getDataTransferMethod();
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
}
