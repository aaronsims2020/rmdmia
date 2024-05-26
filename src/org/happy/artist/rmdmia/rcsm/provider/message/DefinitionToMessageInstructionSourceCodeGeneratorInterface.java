package org.happy.artist.rmdmia.rcsm.provider.message;

import org.happy.artist.rmdmia.Controller;

/**
 * @author Happy Artist
 * 
 * @copyright Copyright © 2015 Happy Artist. All rights reserved.
 */
public interface DefinitionToMessageInstructionSourceCodeGeneratorInterface{
     
    /** Process String Array of Definition "type key", and return Java source code.
     * Map parent_map - pass in null - this is used by the process method for child Objects 
     * iteratively calling the process method.
     */
    public String process(int hid, String class_name, String package_name, String message_definition, Controller controller);   
    
    /** Return Instruction Object Pool Source code.
     * 
     * @param object_name Instruction Object name
     * @return String
     */
    public String getObjectPoolSRC(String object_name,String package_name);
    
    
}
