package org.happy.artist.rmdmia.instruction;

import java.util.List;
import org.happy.artist.rmdmia.instruction.providers.InstructionObjectPool;
import org.happy.artist.rmdmia.instruction.utility.RunnableInstructionBuilder;
import org.happy.artist.rmdmia.rcsm.provider.message.MessageCompiler.DynamicSourceCodeObject;

/** A definition Object for the InstructionObjectRegistry with access to 
 * Hardware ID related Instruction Objects. Every RCSM Provider must implement 
 * this class for reading/writing messages to RCSM implementation format messages. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2014-2015 Happy Artist. All rights reserved.
 */
// TODO: Generate source, and class on the fly if null. On calls to getInstructionObject, and the Handlers.
public abstract class InstructionDefinition 
{
    public int hid;
    public String provider_name;
    public String provider_version;
    public String name;    
    public String description;
//    public InstructionInputHandler inputHandler;
//    public InstructionOutputHandler outputHandler;
//    public Instruction instruction;
    public String instruction_class_name;
    public String instruction_object_pool_class_name;
    public String instruction_input_handler_class_name;
    public String instruction_output_handler_class_name;
    public String schema;
    public boolean schema_is_fixed_length=false;
    // Determined by Schema definition type incoming/outgoing message length (Used to determine size of incoming outgoing network communication buffers/packets)
    public int maximum_schema_bytes_size=1024;
    
    /** Returns hid. hid set by InstructionObjectRegistry. */
    public int getHID()
    {
        return hid;
    }

    /** Get the Instruction name (sensor connection identifier String). */
    public String getName()
    {
        return name;
    }
    
    /** Set the Instruction name (sensor connection identifier String).*/
    public void setName(String name)
    {
        this.name=name;
    }    

    
    
    /** Get the Instruction description (information about the associated sensor connection). */
    public String getDescription()
    {
        return description;
    }
    
    /** Set the Instruction description (information about the associated sensor connection).*/
    public void setDescription(String description)
    {
        this.description=description;
    }           
    
    /** Set the instruction object class name (package name). */
    public void setInstructionObjectClassName(String instruction_class_name)
    {
        this.instruction_class_name=instruction_class_name;
    }
    
    /** Set the instruction object pool class name (package name). */
    public void setInstructionObjectPoolClassName(String instruction_object_pool_class_name)
    {
        this.instruction_object_pool_class_name=instruction_object_pool_class_name;
    }    

    /** Set the instruction input handler class name (package name). */
    public void setInstructionInputHandlerClassName(String instruction_input_handler_class_name)
    {
        this.instruction_input_handler_class_name=instruction_input_handler_class_name;
    }    

    /** Set the instruction output handler class name (package name). */
    public void setInstructionOutputHandlerClassName(String instruction_output_handler_class_name)
    {
        this.instruction_output_handler_class_name=instruction_output_handler_class_name;
    }   
    
    /** Get the instruction object class name (package name). Returns null if not set. */
    public String getInstructionObjectClassName()
    {
        return instruction_class_name;
    }
    
    /** Get the instruction object pool class name (package name). Returns null if not set. */
    public String getInstructionObjectPoolClassName()
    {
        return instruction_object_pool_class_name;
    }    

    /** Get the instruction input handler class name (package name). Returns null if not set. */
    public String getInstructionInputHandlerClassName()
    {
        return instruction_input_handler_class_name;
    }    

    /** Get the instruction output handler class name (package name). Returns null if not set. */
    public String getInstructionOutputHandlerClassName()
    {
        return instruction_output_handler_class_name;
    }  

    /** Set boolean schema is fixed length. */
    public void setSchemaIsFixedLength(boolean schema_is_fixed_length)
    {
        this.schema_is_fixed_length=schema_is_fixed_length;
    }    
    
    /** Return boolean schema is fixed length. */
    public boolean getSchemaIsFixedLength()
    {
        return schema_is_fixed_length;
    }
    
    /** Set int schema definition type size in bytes. If the type is variable default is 1024, and it is advised this to be set to a number above the average message size for better performance. Fixed message type size should never change.  */
    public void setSchemaTypeMessageLength(int message_length)
    {
        this.maximum_schema_bytes_size=message_length;
    }    
    
    /** Return int Schema definition type message length of message bytes. */
    public int getSchemaTypeMessageLength()
    {
        return maximum_schema_bytes_size;
    }    

    /** Set String schema. */
    public void setSchema(String schema)
    {
        this.schema=schema;
    }    
    
    /** Return String schema. */
    public String getSchema()
    {
        return schema;
    }    
      
    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();
    /** Return a Hex String. */
    private static String stringToHex(String text)
    {
        byte[] buf=text.getBytes();
        char[] chars = new char[2 * buf.length];
        for (int i = 0; i < buf.length; ++i)
        {
            chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
            chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
        }
        return String.valueOf(chars);
    }    
    
    /** Return a List of DynamicSourceCodeObject for the Message Compiler to build.
     *
     * @return List<DynamicSourceCodeObject> DynamicSourceCodeObject List of Source Code, and Class Names for the Java Compiler.
     */
    public abstract List<DynamicSourceCodeObject> getSource();     
    /** Internal method: Add a DynamicSourceCodeObject to the Source Code List. Used by source code generator to add generated Objects to the InstructionDefinition Source Code getInstructionSource method call. */     
    public abstract void addDynamicSourceCodeObject(DynamicSourceCodeObject source); 
    public abstract Instruction getInstructionObject();
    public abstract InstructionObjectPool getInstructionObjectPool();
    public abstract InstructionInputHandler getInstructionInputHandler();
    public abstract InstructionOutputHandler getInstructionOutputHandler();
    /** Return the schema file extensions associated with the RCSM Provider schema message (Do not include a period). */
    public abstract String[] getSchemaFileExtensions();
    /** Return the RunnableInstructionBuilder abstract class implementation 
     * associated with the RCSM Provider. The RunnableInstructionBuilder is called 
     * by the SchemaImporter to import RCSM Provider specific schema messages and 
     * compiling them into Objects used in Instruction Objects.
     * 
     * @return RunnableInstructionBuilder
     */    
    public abstract RunnableInstructionBuilder getRunnableInstructionBuilder();
}
