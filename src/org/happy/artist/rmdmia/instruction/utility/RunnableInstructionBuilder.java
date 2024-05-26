package org.happy.artist.rmdmia.instruction.utility;

import java.io.File;
import org.happy.artist.rmdmia.Controller;
import org.happy.artist.rmdmia.rcsm.provider.message.DefinitionToMessageInstructionSourceCodeGeneratorInterface;

/**
 * org.happy.artist.rmdmia.instruction.utility.RunnableInstructionBuilder
 * an abstract implementation for RCSM Provider specific RunnableInstructionBuilder 
 * implementations. Implement the schema file read and compile in the Runnable run method
 * implementation.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2014-2015 Happy Artist. All rights reserved. 
 */
public abstract class RunnableInstructionBuilder implements Runnable
{
    private String file_path;
    private String rcsm_provider_name;
    private Controller controller;
    public int hid=-1;
    
    public RunnableInstructionBuilder(Controller controller, String rcsm_provider_name)
    {
        this.rcsm_provider_name=rcsm_provider_name;
        this.controller=controller;
    }

    /** Internal method. Not or API use. Not Supported. Must be called before 
     * thread is executed.
     * 
     * @return RunnableInstructionBuilder
     */  
    public RunnableInstructionBuilder setFilePath(String file_path)
    {
        this.file_path=file_path;
        return this;
    }    
    
    
    public String getFilePath()
    {
        return file_path;
    }

    /** Set int HID. */
    public void setHID(int hid)
    {
        this.hid=hid;
    }
    
    /** Return int hid. */
    public int getHID()
    {
        return hid;
    }

    /** Return the RCSM Provider name. */
    public String getRCSMProviderName()
    {
        return rcsm_provider_name;
    }
    
    /** Return the RMDMIA Controller Object. */
    public Controller getController()
    {
        return controller;
    }    
    
    private File file;
    /** Create folder if one does not exist. */
    private String getFolder(String folder)
    {
        if(folder!=null)
        {
            this.file = new File(folder);
            if(!file.exists())
            {
                file.mkdirs();
            }
            this.file=null;
        }
        return folder;
    }   
    
    /** Return the DefinitionToMessageInstructionSourceCodeGeneratorInterface for the RCSM Provider for generating source code. */
    public abstract DefinitionToMessageInstructionSourceCodeGeneratorInterface getDefinitionToMessageInstructionSourceCodeGeneratorInterface();
}
