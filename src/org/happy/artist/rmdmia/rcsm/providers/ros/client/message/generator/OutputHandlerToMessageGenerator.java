package org.happy.artist.rmdmia.rcsm.providers.ros.client.message.generator;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.happy.artist.rmdmia.Controller;
import org.happy.artist.rmdmia.instruction.InstructionManagerHardwareRegistry;
import org.happy.artist.rmdmia.utilities.HexStringConverter;

/**
 * org.happy.artist.rmdmia.instruction.utility.OutputHandlerToMessageGenerator
 * class generates Java source code for the Instruction Output Handler to ROS Schema.
 *
 * @author Happy Artist
 *
 * @copyright Copyright ©2015 Happy Artist. All rights reserved.
 */
// TODO: add local processing variables for ros types, and run more tests on byte[] length generation, and fix nested Object type inside Object type classpath issue.
public class OutputHandlerToMessageGenerator
{
// TODO: Update Header Object if in schema to increment sequence by 1, and possible time stamp as well...
    private final static
            String lineSeparator = System.getProperty("line.separator");

    public static
            void main(String[] args)
    {
        String def = "# This expresses velocity in free space broken into its linear and angular parts.\n" +
"Vector3  linear\n" +
"Vector3  angular";
            System.out.println("SRC:\n" + OutputHandlerToMessageGenerator.process(-1,"Color", "ros.turtlesim", def, null));
            
     //   }


    }

    /**
     * Process String Array of Definition "type key", and return Java source
     * code.
     */
    // TODO: Determine Object interface implementation type (probably SensorProcess))
    public static String process(int hid, String instruction_class_name, String package_name, String message_definition, Controller controller)
    {
        if (instruction_class_name==null||instruction_class_name.isEmpty()||message_definition==null)    
        {
            return null;
        }
        // Check if the class is a Service Definition.
        boolean is_service = false;
        boolean is_action = false;
        boolean is_result=false;
        boolean has_response_variables=false;
       
        // Child definition handling 
        boolean contains_child_definition=false;
        // Map Schema file lookup Map
        Map<String,String> schemaLookupMap=null;
        // definition_map will be used if child definitions exist.
        Map<String,String> definition_map=new HashMap<String,String>();
        // String value for schema file Path
        String value;
        // Undefined Types for List of Objects Types
        List undefined_types;
        // Test for child definitions in code
        int child_index;
        int next_index;
        String child_type="";
        int definition_index;
        
        // Set is_service and is_action variables
        if (message_definition != null && (definition_index = message_definition.indexOf("---")) != -1)
        {
            // Is it a service or action definition? 
            if ((definition_index = message_definition.indexOf("\n", definition_index)) != -1)
            {
                if ((definition_index = message_definition.indexOf("---", definition_index)) != -1)
                {
                    is_service = false;
                    is_action = true;
                }
                else
                {
                    is_service = true;
                }
            }
            else
            {
                is_service = true;
            }
        }
        else
        {
            is_service = false;
        }


        if (is_service)
        {
            message_definition = processSRVDefinition(message_definition);
        }
        else if (is_action)
        {
            message_definition = processACTIONDefinition(message_definition);
        }
        else
        {
            // Is msg
            message_definition = processMSGDefinition(message_definition);
        }        
        
        if((child_index=message_definition.indexOf("MSG:"))!=-1)
        {
            contains_child_definition=true;
            definition_map=new HashMap<String,String>();
            child_type="MSG";
        }
        else if((child_index=message_definition.indexOf("SRV:"))!=-1)
        {
            contains_child_definition=true;
            definition_map=new HashMap<String,String>();
            child_type="SRV";
        }
        else if((child_index=message_definition.indexOf("ACTION:"))!=-1)
        {
            contains_child_definition=true;
            definition_map=new HashMap<String,String>();
            child_type="ACTION";
        }
  
// TODO: Process nested message definitions (currently contains some processing bugs)
// START: processing of nested message definition (inline)        
        // Update the Map if contains_child_definition=true
        if(contains_child_definition)
        {
            // Put the main message_definition in the map.    
            definition_map.put("message_definition", message_definition.substring(child_index));            
            // Put the child elements in the map.
            while(child_index!=-1)
            {
               String def_name=null;
               boolean is_next_index=false;
               next_index=message_definition.indexOf("\n", child_index);
               if(next_index!=-1)
               {
                   String msg_line=message_definition.substring(child_index,next_index);
                   
                   if(child_type.equals("MSG"))
                   {
                       def_name=msg_line.substring(4,next_index-child_index).trim();
                   }
                   else if(child_type.equals("SRV"))
                   {
                       def_name=msg_line.substring(4,next_index-child_index).trim();                                         }
                   else if(child_type.equals("ACTION"))
                   {
                       def_name=msg_line.substring(7,next_index-child_index).trim();                                        }
                   
                   // Check for a sub definition
                   if((child_index=message_definition.indexOf("MSG:",next_index))!=-1)
                   {
                        definition_map.put(def_name, message_definition.substring(next_index+1,child_index));
                   }
                   else if((child_index=message_definition.indexOf("SRV:",next_index))!=-1)
                   {
                        definition_map.put(def_name, message_definition.substring(next_index+1,child_index));
                   }
                   else if((child_index=message_definition.indexOf("ACTION:",next_index))!=-1)
                   {
                        definition_map.put(def_name, message_definition.substring(next_index+1,child_index));
                   }
                   else
                   {
                        definition_map.put(def_name, message_definition.substring(next_index+1));                     
                        child_index=-1;
                   }
                        
               }
               else
               {
                   // empty definition
                   String msg_line=message_definition.substring(child_index);                                    if(child_type.equals("MSG"))
                   {
                       def_name=msg_line.substring(4).trim();
                   }
                   else if(child_type.equals("SRV"))
                   {
                       def_name=msg_line.substring(4).trim();                                                               }
                   else if(child_type.equals("ACTION"))
                   {
                       def_name=msg_line.substring(7).trim();                                                               } 
                   if(def_name!=null)
                   {
                       definition_map.put(def_name, "");
                   }
                   child_index=-1;
               }
            }
        }
// End of inline Message Definition Processing (nested message definition)        
        else
        {
            definition_map.put("message_definition", message_definition);
        }
 // TODO: Cycle through the map elements to generate the source code 
        // Get List of undefined Object types.
 // TODO: Run the following code on schema elements obtained from file to ensure all nested child elements contain a schema as well.        
        
        undefined_types=getUndefinedObjectTypes(definition_map);
///////////////////////Get Schema path to pass into Schema file path -------------
// If class does not exist check if schema exists
        // reuse existing classMap for schemaMap
        if(controller!=null)
        {
            // Check if Class exists, return class name
            schemaLookupMap=controller.getInstructionManager().getSchemaLookupMap();
        }                  

 ////////// Begin Schema File code insert
String ROS_Type;
            for(int i=0;i<undefined_types.size();i++)
            {
//////////////////////////////
                ROS_Type=((String)undefined_types.get(i));
//                System.out.println("schemaLookupMap: " + schemaLookupMap);
                value=schemaLookupMap.get("ros/".concat(ROS_Type.toLowerCase()));                            
                if(value==null&&ROS_Type.indexOf("/")!=-1)
                {
                    value=schemaLookupMap.get("ros/".concat(ROS_Type.substring(ROS_Type.lastIndexOf("/")+1).toLowerCase()));
                }
                if(value!=null)
                { 
                    Logger.getLogger(OutputHandlerToMessageGenerator.class.getName()).log(Level.FINEST, "SCHEMA MAP key/value found: ".concat(value));
                    try
                    {
                        String schema=getSchemaFile(value,controller); 
//                    Logger.getLogger(DefinitionToInputHandlerGenerator.class.getName()).log(Level.INFO, "SCHEMA File Path: ".concat((String)undefined_types.get(i)).concat(", SCHEMA: ").concat(schema));

                        
   //                     System.out.println("SCHEMA File Path: " + (String)undefined_types.get(i) +", SCHEMA: " + schema);


                        if(schema!=null)
                        {
                            // Add type to Map.
                            definition_map.put(value, schema);                
                        }
                        // If schema is null then try and strip off the forward slashes
                        else if(value.indexOf("/")!=-1)
                        {
                            schema=getSchemaFile(((String)undefined_types.get(i)).substring(((String)undefined_types.get(i)).indexOf("/")+1),controller);
                            if(schema!=null)
                            {
                                 // Add type to Map.
                                 definition_map.put((String)undefined_types.get(i), schema);                                         }
                        }
                        else
                        {
                            System.out.println("Schema type file not found for: " + ((String)undefined_types.get(i)));
                    Logger.getLogger(OutputHandlerToMessageGenerator.class.getName()).log(Level.WARNING, "Schema type file not found for: ".concat(((String)undefined_types.get(i))));                            
                        }
                    }
                    catch(NullPointerException e)
                    {
                        Logger.getLogger(OutputHandlerToMessageGenerator.class.getName()).log(Level.WARNING, null,e); 
                    }
                }
///////////////////////////////                
                
                
            }                    
                    
/////////// End Schema File Code insert                    
     
                
        
///////////////////////End Schema Path ////////////////////////////      
        
        
        
        // Add message_definition to schema field in InstructionManagerHardwareRegistry 
        if(hid!=-1)
        {   
            // Instruction Class name
            InstructionManagerHardwareRegistry.put(String.valueOf(hid).concat(".2"), package_name.concat(".").concat(instruction_class_name));
        
            // InstructionInputHandler Class name
            InstructionManagerHardwareRegistry.put(String.valueOf(hid).concat(".3"), package_name.concat(".").concat(instruction_class_name).concat("InputHandler"));
            // InstructionOutputHandler Class name
            InstructionManagerHardwareRegistry.put(String.valueOf(hid).concat(".4"), package_name.concat(".").concat(instruction_class_name).concat("OutputHandler"));            
            // InstructionObjectPool Class name
            InstructionManagerHardwareRegistry.put(String.valueOf(hid).concat(".8"), package_name.concat(".").concat(instruction_class_name).concat("InstructionPool"));               
            try
            {
                // Message Definition
                InstructionManagerHardwareRegistry.put(String.valueOf(hid).concat(".7"), HexStringConverter.getHexStringConverterInstance().stringToHex(message_definition));
            }
            catch (UnsupportedEncodingException ex)
            {
                Logger.getLogger(OutputHandlerToMessageGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
// TODO: UNCOMMENT
//        InstructionManagerHardwareRegistry.writeFile();        
        return processSchemaMap(hid,instruction_class_name, package_name, definition_map, controller, is_service, is_action,is_result, has_response_variables, schemaLookupMap);
    }

    /** Return source code from the schema Map. */
    private static String processSchemaMap(int hid, String instruction_class_name, String package_name, Map<String,String> definition_map, Controller controller, boolean is_service, boolean is_action, boolean is_result, boolean has_response_variables, Map<String,String> schema_lookup_map)
    {
        String input_definition;
        int definition_index;
        String message_definition=definition_map.get("message_definition");
        
        // If message_definition not found open the message_definition_file
// TODO: Determine if Map returns an empty String if Object should be null... valid empty Strings are being ignored which is breaking system so removing isEmpty()
        //if(message_definition==null||message_definition.isEmpty())
        if(message_definition==null)
        {
            return null;
        }
        
        //Check for any missing Object types that are not defined in the Map...
        Logger.getLogger(OutputHandlerToMessageGenerator.class.getName()).log(Level.FINE, "Undefined Object Types:\n"+Arrays.toString(getUndefinedObjectTypes(definition_map).toArray()) + ", instruction_class_name: " + instruction_class_name);        
/*        
        // Set is_service and is_action variables
        if (message_definition != null && (definition_index = message_definition.indexOf("---")) != -1)
        {
            // Is it a service or action definition? 
            if ((definition_index = message_definition.indexOf("\n", definition_index)) != -1)
            {
                if ((definition_index = message_definition.indexOf("---", definition_index)) != -1)
                {
                    is_service = false;
                    is_action = true;
                }
                else
                {
                    is_service = true;
                }
            }
            else
            {
                is_service = true;
            }
        }
        else
        {
            is_service = false;
        }


        if (is_service)
        {
            input_definition = processSRVDefinition(message_definition);
        }
        else if (is_action)
        {
            input_definition = processACTIONDefinition(message_definition);
        }
        else
        {
            // Is msg
            input_definition = processMSGDefinition(message_definition);
        }
        */ 
// TODO: Process the Instruction to a OutputHandler class. 
        Logger.getLogger(OutputHandlerToMessageGenerator.class.getName()).log(Level.FINE, "Generating InstructionOutputHandler Object for " + instruction_class_name + ", package " + package_name + ", message_definition:\n" + message_definition);         

        String[] definition = process(message_definition);
       // System.out.println("MESSAGE DEFINITION: "+Arrays.toString(definition));
        StringBuilder sb = new StringBuilder();
        HashMap<String, String> header = new HashMap<String, String>();
        // Define and instantiate globals for global variable defines, and default assignments.
        Map<String, String> globals = new HashMap<String, String>();        
        // variable type
        String type;
        
        String[] var_type;
        // Append package to header map
        if (package_name != null)
        {
            header.put("package", "package ".concat(package_name).concat(";"));
        }
        // Added the IOException import        
        header.put("ioexception_import", "import java.io.IOException;");
        // Added the Logger Level import
        header.put("logger_level_import", "import java.util.logging.Level;");
        // Added the Logger import
        header.put("logger_import", "import java.util.logging.Logger;");  
        // Added SocketException import
        header.put("socketexception_import", "import java.net.SocketException;"); 
        // Added NullPointerException import
        header.put("nullpointerexception_import", "import java.lang.NullPointerException;");         
        // Added the RCSMException import
        header.put("rcsmexception_import", "import org.happy.artist.rmdmia.rcsm.RCSMException;");        
        // Add the Instruction import
        header.put("instruction", "import org.happy.artist.rmdmia.instruction.InstructionOutputHandler;");
        // Add the Controller class import (Used in ,multiple locations in generated class.
        header.put("controller_import", "import org.happy.artist.rmdmia.Controller;");
        // Add the Controller class import (Used in ,multiple locations in generated class.
        header.put("instruction_class_import", "import org.happy.artist.rmdmia.instruction.Instruction;");
        // Add the MovementProcessor class import (Used in process method in generated class.
        header.put("movement_processor_import", "import org.happy.artist.rmdmia.movement.MovementProcessor;");     
        // Add the MovementProcessorFactory import statement.
        header.put("movement_processor_factory_import", "import org.happy.artist.rmdmia.movement.MovementProcessorFactory;");         
        // Add the CommunicationSenderInterface class import (Used in process method in generated class.
        header.put("communication_sender_interface_import", "import org.happy.artist.rmdmia.rcsm.provider.CommunicationSenderInterface;");               
        // Add the ROSNode class import (Used in sending message to ros RCSM method in generated class.
        header.put("ros_node_import", "import org.happy.artist.rmdmia.rcsm.providers.ros.ROSNode;");
        // Add the InstructionHandler import statement.
        header.put("instruction_handler_import", "import org.happy.artist.rmdmia.instruction.InstructionHandler;");       
        // Add the Instruction implementation class import
        header.put("instruction_class_name", "import ".concat(package_name).concat(".").concat(instruction_class_name).concat(";"));
        // Add the InstructionObjectPool implementation class import
        header.put("instruction_object_pool_import", "import org.happy.artist.rmdmia.instruction.providers.InstructionObjectPool;");        
        // Bag file writer imports
        header.put("time_unit_import", "import java.util.concurrent.TimeUnit;\n ");       
        header.put("timer_service_import", "import org.happy.artist.rmdmia.timing.TimerService;\n");
        header.put("bytes_to_hex_import", "import org.happy.artist.rmdmia.utilities.BytesToHex;\n");
        header.put("bag_writer_import", "import org.happy.artist.rmdmia.utilities.ros.bag.RMDMIABagQueueFileWriter;\n");

        // add the class declaration
        sb.append("public class ");
        sb.append(instruction_class_name);
        sb.append("OutputHandler");
        sb.append(" extends MovementProcessor implements InstructionOutputHandler, MovementProcessorFactory");
        // Class opening bracket
        sb.append("\n{\n");
// TODO: Meat of the code goes here
        // variables
        // Assign the pool variable.
        sb.append("private ");
        sb.append(instruction_class_name);
        sb.append("InstructionPool pool;\n");
        sb.append("private ROSNode rosNode;\n");
        sb.append("private CommunicationSenderInterface sender;\n");        
        sb.append("private InstructionHandler handler;\n");
        sb.append("private java.lang.String topicName;\n");    
        sb.append("private "+instruction_class_name+" this_instruction;\n");  
        sb.append("private RMDMIABagQueueFileWriter rosBag;\n");  
        sb.append("private TimerService time;\n");  
        sb.append("private boolean isRecording=false;\n");          
        
        // constructor
        sb.append("public ");
        sb.append(instruction_class_name);
        sb.append("OutputHandler");        
        sb.append("(Controller controller, int hid)\n{\n");
        // meat of constructor
        sb.append("    super();\n");
        sb.append("    this.pool=(");
        sb.append(instruction_class_name);
        sb.append("InstructionPool");
        sb.append(")controller.getInstructionManager().getProviderByName(\"instruction_manager\").getInstructionDefinition(hid).getInstructionObjectPool();\n");
        // Assign Default InputHandler Object.
        sb.append("    this.rosNode=(ROSNode)controller.getRCSM().getProviderByName(\"ros\");\n");
        sb.append("    if(controller.getInstructionManager().getProviderByName(\"instruction_manager\").getInstructionDefinition(hid).name!=null&&controller.getInstructionManager().getProviderByName(\"instruction_manager\").getInstructionDefinition(hid).name.isEmpty()==false)\n");
        sb.append("    {\n");
        sb.append("    this.topicName=controller.getInstructionManager().getProviderByName(\"instruction_manager\").getInstructionDefinition(hid).name;\n");

        if(is_action)
        {
            // Action Sender
            System.out.println("Action not supported in self generated InstructionOutputHandlers in this release.");
        }
        else if(is_service)
        {
            // Service Sender
            sb.append("    this.sender=rosNode.getServiceSenders()[rosNode.getServiceIndex(topicName)];\n");
            
        }
        else
        {
            // Topic sender
            sb.append("    this.sender=rosNode.getPublisherSenders()[rosNode.getTopicIndex(topicName)];\n");
        }
        sb.append("    }\n");
        sb.append("    else\n");
        sb.append("    {\n");
        sb.append("    this.sender=null;\n");
        sb.append("    }this.handler=new DefaultInstructionHandler();\n");
        // ROS Bag file writer.
        sb.append("        // Get reference to TimerService\n" +
"        if((this.time=controller.getControllerManager().getTimerService())==null)\n" +
"        {\n" +
"            // Set default TimerService to microseconds. If user wants more or less precision they must set at startup.\n" +
"            this.time = new TimerService(1,1,TimeUnit.MICROSECONDS);            \n" +
"        }\n" +
"        // Get a reference to rosBag\n" +
"        this.rosBag=RMDMIABagQueueFileWriter.getInstance();    \n");
        // End constructor
        sb.append("}\n");
//********************* constructor complete 
       // ROSBag File Writing methods.
        sb.append("    /** Set boolean isRecording. */\n" +
"    public void setIsRecording(boolean isRecording)\n" +
"    {\n" +
"        this.isRecording=isRecording;\n" +
"    }\n" +
"\n" +
"    /** Return is recording. */\n" +
"    public boolean getIsRecording()\n" +
"    {\n" +
"        return isRecording;\n" +
"    }\n"); 
        sb.append("    /** This call to process will send the message to the RCSM via the Movement \n");
        sb.append("     *  Processor Manager (after pre-ordered movement calibrations processed). \n");
        sb.append("     * \n");
        sb.append("     * @param instruction\n");
        sb.append("     * @return \n");
        sb.append("     */\n");
        sb.append("    @Override\n");
        sb.append("    public Instruction process(Instruction instruction)\n");
        sb.append("    {        \n");

// Reference instruction from here on in to assign byte[] data to the Instruction Object
//********************** TODO: fill in above array length, and continue coding method below...

        
// TODO: Code goes here....  
        Object[] source_resp_array=getProcessSource(hid,instruction_class_name, package_name, definition_map, controller, is_service, is_action, is_result, has_response_variables, header, globals, schema_lookup_map);
        final String process_source=(String)source_resp_array[0];
        globals=(Map<String,String>)source_resp_array[1];
        sb.append(process_source);      
        // End
        if(is_action)
        {
            System.out.println("Actions not supported in InstructionOutputHandlers this release.");
        }
        else if(is_service)
        {
            // Service
            sb.append("    try \n" +
            "    {\n" +
            "         // service is running and is connected.\n" +
            "         sender.send(ros_msg);\n" +
                    "            if(isRecording)\n" +
"            {\n" +
"                // Record\n" +
"                try \n" +
"                {\n" +
"                    // Send the message to the ROS Bag file.\n" +
"                        rosBag.addMessage(TimerService.SYSTEM_TIME, instruction.hid, BytesToHex.bytesToHexChars(ros_msg));\n" +
"                } \n" +
"                catch (IOException exc) \n" +
"                {\n" +
"                        Logger.getLogger(" + instruction_class_name + "OutputHandler.class.getName()).log(Level.SEVERE, java.lang.String.valueOf(TimerService.SYSTEM_TIME) + \" rosbag recording failed to add Message for topic/service: \"  + " + instruction_class_name + "OutputHandler.this.topicName + \" hid: \" + java.lang.String.valueOf(instruction.hid), exc);\n" +
"                }                \n" +
"            }\n" +
            "    } \n" +
            "    catch (NullPointerException e) \n" +
            "    {\n" +
            "         throw e;\n" +
            "    } \n" +
            "    catch (java.net.SocketException e) \n" +
            "    {\n" +
            "          // Service is not running yet. Received SocketException, attempting reconnect.\n" +
            "          try \n" +
            "          {\n" +
            "              sender.connect();\n" +
            "              sender.send(ros_msg);\n" +
                    "            if(isRecording)\n" +
"            {\n" +
"                // Record\n" +
"                try \n" +
"                {\n" +
"                    // Send the message to the ROS Bag file.\n" +
"                        rosBag.addMessage(TimerService.SYSTEM_TIME, instruction.hid, BytesToHex.bytesToHexChars(ros_msg));\n" +
"                } \n" +
"                catch (IOException exc) \n" +
"                {\n" +
"                        Logger.getLogger(" + instruction_class_name + "OutputHandler.class.getName()).log(Level.SEVERE, java.lang.String.valueOf(TimerService.SYSTEM_TIME) + \" rosbag recording failed to add Message for topic/service: \"  + " + instruction_class_name + "OutputHandler.this.topicName + \" hid: \" + java.lang.String.valueOf(instruction.hid), exc);\n" +
"                }                \n" +
"            }\n" +
            "          } \n" +
            "          catch (Exception ex) \n" +
            "          {\n" +
            "              // System.out.println(\"Failed Socket reconnect attempt on SocketException.\");\n" +
            "              Logger.getLogger("+instruction_class_name +"OutputHandler.class.getName()).log(Level.SEVERE, \"Failed Socket reconnect attempt on SocketException.\", ex);\n" +
            "          }\n" +
            "      } \n" +
            "      catch (Exception e) \n" +
            "      {\n" +
            "           Logger.getLogger("+instruction_class_name +"OutputHandler.class.getName()).log(Level.SEVERE, null, e);            \n" +
            "      }\n");
        }  
        else 
        {
            // Is Topic
            sb.append("    if(sender!=null)\n");
            sb.append("    {\n");
            sb.append("        try\n");
            sb.append("        {\n");
    // TODO: Select appropriate sender for service, persistent service, and topics        
            sb.append("            sender.send(ros_msg);\n");
            sb.append("            if(isRecording)\n" +
"            {\n" +
"                // Record\n" +
"                try \n" +
"                {\n" +
"                    // Send the message to the ROS Bag file.\n" +
"                        rosBag.addMessage(TimerService.SYSTEM_TIME, instruction.hid, BytesToHex.bytesToHexChars(ros_msg));\n" +
"                } \n" +
"                catch (IOException exc) \n" +
"                {\n" +
"                        Logger.getLogger(" + instruction_class_name + "OutputHandler.class.getName()).log(Level.SEVERE, java.lang.String.valueOf(TimerService.SYSTEM_TIME) + \" rosbag recording failed to add Message for topic/service: \"  + " + instruction_class_name + "OutputHandler.this.topicName + \" hid: \" + java.lang.String.valueOf(instruction.hid), exc);\n" +
"                }                \n" +
"            }\n");
            sb.append("        }\n");
            sb.append("        catch (IOException ex)\n");
            sb.append("        {\n");
            sb.append("            Logger.getLogger("+instruction_class_name +"OutputHandler.class.getName()).log(Level.SEVERE, null, ex);\n");
            sb.append("        }\n");
            sb.append("    }\n");
        }
        sb.append("    return instruction;      \n    }\n");

//************************************** next method        
sb.append("\n    /** Send the message directly to the RCSM, and checkin the Instruction \n");
sb.append("     *  to the Instruction Object Pool if the pool is not null, if pool \n");
sb.append("     *  is null set the Object to null. \n");
sb.append("     * \n");
sb.append("     * @param instruction\n");
sb.append("     */\n");
sb.append("    @Override\n");
sb.append("    public void send(Instruction instruction)\n");
sb.append("    {\n");
sb.append(process_source);  
sb.append("\n");
        if(is_action)
        {
            System.out.println("Actions not supported in InstructionOutputHandlers this release.");
        }
        else if(is_service)
        {
            // Service
            sb.append("    try \n" +
            "    {\n" +
            "         // service is running and is connected.\n" +
            "         sender.send(ros_msg);\n" +
            "            if(isRecording)\n" +
"            {\n" +
"                // Record\n" +
"                try \n" +
"                {\n" +
"                    // Send the message to the ROS Bag file.\n" +
"                        rosBag.addMessage(TimerService.SYSTEM_TIME, instruction.hid, BytesToHex.bytesToHexChars(ros_msg));\n" +
"                } \n" +
"                catch (IOException exc) \n" +
"                {\n" +
"                        Logger.getLogger(" + instruction_class_name + "OutputHandler.class.getName()).log(Level.SEVERE, java.lang.String.valueOf(TimerService.SYSTEM_TIME) + \" rosbag recording failed to add Message for topic/service: \"  + " + instruction_class_name + "OutputHandler.this.topicName + \" hid: \" + java.lang.String.valueOf(instruction.hid), exc);\n" +
"                }                \n" +
"            }\n"+
            "    } \n" +
            "    catch (NullPointerException e) \n" +
            "    {\n" +
            "         throw e;\n" +
            "    } \n" +
            "    catch (java.net.SocketException e) \n" +
            "    {\n" +
            "          // Service is not running yet. Received SocketException, attempting reconnect.\n" +
            "          try \n" +
            "          {\n" +
            "              sender.connect();\n" +
            "              sender.send(ros_msg);\n" +
"            if(isRecording)\n" +
"            {\n" +
"                // Record\n" +
"                try \n" +
"                {\n" +
"                    // Send the message to the ROS Bag file.\n" +
"                        rosBag.addMessage(TimerService.SYSTEM_TIME, instruction.hid, BytesToHex.bytesToHexChars(ros_msg));\n" +
"                } \n" +
"                catch (IOException exc) \n" +
"                {\n" +
"                        Logger.getLogger(" + instruction_class_name + "OutputHandler.class.getName()).log(Level.SEVERE, java.lang.String.valueOf(TimerService.SYSTEM_TIME) + \" rosbag recording failed to add Message for topic/service: \"  + " + instruction_class_name + "OutputHandler.this.topicName + \" hid: \" + java.lang.String.valueOf(instruction.hid), exc);\n" +
"                }                \n" +
"            }\n"+            
            "          } \n" +
            "          catch (Exception ex) \n" +
            "          {\n" +
            "              // System.out.println(\"Failed Socket reconnect attempt on SocketException.\");\n" +
            "              Logger.getLogger("+instruction_class_name +"OutputHandler.class.getName()).log(Level.SEVERE, \"Failed Socket reconnect attempt on SocketException.\", ex);\n" +
            "          }\n" +
            "      } \n" +
            "      catch (Exception e) \n" +
            "      {\n" +
            "           Logger.getLogger("+instruction_class_name +"OutputHandler.class.getName()).log(Level.SEVERE, null, e);            \n" +
            "      }\n");
        }  
        else 
        {
            // Is Topic
            //////////////
          //  sb.append("if(sender==null){System.out.println(\"Sender is null.\");}else{System.out.println(\"Sender not null. Sender threadName: \"+((org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.tcpros.TCPROSPublisherCommunicator)sender).threadName);}");
            //////////////        
            sb.append("    if(sender!=null)\n");
            sb.append("    {\n");
            sb.append("        try\n");
            sb.append("        {\n");
    // TODO: Select appropriate sender for service, persistent service, and topics  
            
            sb.append("            sender.send(ros_msg);\n");
            sb.append("            if(isRecording)\n" +
"            {\n" +
"                // Record\n" +
"                try \n" +
"                {\n" +
"                    // Send the message to the ROS Bag file.\n" +
"                        rosBag.addMessage(TimerService.SYSTEM_TIME, instruction.hid, BytesToHex.bytesToHexChars(ros_msg));\n" +
"                } \n" +
"                catch (IOException exc) \n" +
"                {\n" +
"                        Logger.getLogger(" + instruction_class_name + "OutputHandler.class.getName()).log(Level.SEVERE, java.lang.String.valueOf(TimerService.SYSTEM_TIME) + \" rosbag recording failed to add Message for topic/service: \"  + " + instruction_class_name + "OutputHandler.this.topicName + \" hid: \" + java.lang.String.valueOf(instruction.hid), exc);\n" +
"                }                \n" +
"            }\n");
            sb.append("        }\n");
            sb.append("        catch (IOException ex)\n");
            sb.append("        {\n");
            sb.append("            Logger.getLogger("+instruction_class_name +"OutputHandler.class.getName()).log(Level.SEVERE, null, ex);\n");
            sb.append("        }\n");
            sb.append("    }\n");
        }
sb.append("        if(pool!=null)\n");
sb.append("        {\n");
sb.append("           pool.checkin(this_instruction.eid);        \n");
sb.append("        }     \n");
sb.append("        else\n");
sb.append("        {\n");
sb.append("            instruction=null;\n");
sb.append("        }\n");
sb.append("    }\n\n");

// Transform method

sb.append("    /** Transform Instruction Object into associated RCSM plugin outgoing \n");
sb.append("     * connection byte[] format.\n");
sb.append("     * \n");
sb.append("     * @return byte[] outgoing message format byte[]\n");
sb.append("     */ \n");
sb.append("   @Override\n");
sb.append("    public byte[] transform(Instruction instruction)\n");
sb.append("    {\n");
sb.append(process_source);  
sb.append("        return ros_msg;\n    }\n\n");


// Add InstructionHandler code.
sb.append("     /** Process the instruction via the registered InstructionHandler. If the Handler \n" +
"     * is not registered, the default instruction handler will checkin the Instruction \n" +
"     * to the Instruction Object Pool. This particular method is intended for autonomous \n" +
"     * data processing (Artificial Intelligence). In reality most implementations will \n" +
"     * call getRegisteredInstructionHandler, and call process directly for performance and custom\n" +
"     * AI interface implementations for improved performance.\n" +
"     * \n" +
"     * @param process_instruction\n" +
"     */\n" +
"    public void process_handler(Instruction process_instruction)\n" +
"    {\n" +
"        handler.process(process_instruction);        \n" +
"    }\n" +
"\n" +
"    @Override\n" +
"    public MovementProcessor newMovementProcessor()\n" +
"    {\n" +
"    return (MovementProcessor)this;\n" +
"    }\n" +
"\n" +
"    /** Register an InstructionHandler, that will send the Instruction to its next destination, \n" +
"     * after the byte[] conversion  to Instruction is completed. If not registered process will call\n" +
"     * checkin on the Instruction Object.\n" +
"     */\n" +
"   public void registerInstructionHandler(InstructionHandler handler)\n" +
"   {\n" +
"	this.handler=handler;\n" +
"   }\n" +
"    \n" +
"    /** Return boolean is InstructionHandler Registered.\n" +
"     * \n" +
"     * @return boolean \n" +
"     */\n" +
"    public boolean isRegistered()\n" +
"    {\n" +
"	if(handler==null)\n" +
"	{\n" +
"           return false;\n" +
"	}\n" +
"	return true;\n" +
"    }\n" +
"    \n" +
"    /** Return the Registered InstructionHandler. Returns null if an InstructionHandler is not Registered. */\n" +
"   public InstructionHandler getRegisteredInstructionHandler()\n" +
"   {\n" +
"	return handler;\n" +
"   }\n" +
"    \n" +
"    class DefaultInstructionHandler implements InstructionHandler\n" +
"    {\n" +
"        /** A default InstructionHandler that automatically checks in the Instruction Object\n" +
"         * if an InstructionHandler is not set.\n" +
"         * \n" +
"         * @param instruction \n" +
"         */\n" +
"        @Override\n" +
"        public void process(Instruction instruction)\n" +
"        {\n" +
"            "+instruction_class_name +"OutputHandler.this.pool.checkin(instruction.eid);\n" +
"        }\n" +
"        \n" +
"    }\n" +
"\n" +
"    /** Return the InstructionObjectPool associated with the InstructionOutputHandler implementation. Return null, if it is not implemented in the InstructionOutputHandler. */\n" +
"    public InstructionObjectPool getInstructionObjectPool()\n" +
"    {\n" +
"        return pool;\n" +
"    }    \n");
    // Insert the Class variables.
//System.out.println("GLOBALS: " + globals.toString());
    sb.append(getClassVariablesSource(globals));
    sb.append("}\n");
        // Insert Header
        sb.insert(0, getHeaderSource(header));
        return sb.toString();        
    }
 
    z
         
    /** Return String byte[] length source code for byte[] ros message definition lengths on variable type definitions (will not work on fixed length definitions). */
    private static String getDefinitionLengthSource(String[] definition_types)
    {
        int fixed_length=0;
        StringBuilder variable_add_sums = new StringBuilder();
        for(int i=0;i<definition_types.length;i++)
        {
            // if fixed type, append length to fixed_length
            if(getIsFixedLength(definition_types[i]))
            {
                if(getIsTypeArray(definition_types[i]))
                {
                    fixed_length = fixed_length + getROSTypeFixedArrayLength(definition_types[i]);
                }
                else
                {
                    fixed_length = fixed_length + getTypeLength(definition_types[i]); 
                }
            }
            else
            {
                // Is variable type
// TODO: Need to process each java variable name for length information.                 

// TODO JavaToROSTypesSource functional implementation ready for implementing here.                             
/*                 if(type==string)
                 {
                     // string.length()+1/2=bytes
                 }
                 else if(primitive_type_array)
                 {
                     // is array
                     if(variable_type_array)
                     {
                        // variable string type array
                        // (array.length*type_length)+(4*array.length)+4
                     }
                     else
                     {
                         // variable fixed type array
                         // (array.length*type_length)+4
                     }
                 }
                 else
                 {
                     // Is Object[]
                     
                 }
                 * */
            }
        }
        // Append fixed length to variable_add_sums if fixed_length>0
        if(fixed_length>0)
        {
            variable_add_sums.append(" + ");
            variable_add_sums.append(fixed_length);
        }
        
        return variable_add_sums.toString();
    }

    /** Return the source code for the definition variable lengths in the byte array length. */
    private static String getDefinitionVariableLengthSource(Map<String,String> definition_map, String object_package_name, Controller controller, boolean is_service, boolean is_action, boolean is_result, boolean has_response_variables, Map<String,String> schema_lookup_map, Map<String,String> globals)
    {
        StringBuilder sb = new StringBuilder();// variable type
        String type;
        String[] var_type;
        String[] definition=process(definition_map.get("message_definition"));
        System.out.println("DEF_ARRAY:"+Arrays.toString(definition));
        // The current byte position of the Java source byte[]. 
        int current_position=0;
        //' is_variable will be set to true on the first non-fixed variable in a variable length message definition.
        boolean is_variable=false;
        // Is a ROS Message Definition constant variable.
        boolean is_constant=false;
        //boolean is_fixed_length=false;
//TODO:  Step one check if variable or fixed length, and set is_fixed_length
  
        boolean is_fixed_length=getIsDefinitionFixedLength(definition_map); 
        int fixed_length=-1;
        if(is_fixed_length)
        {
            fixed_length=getDefinitionFixedLength(definition_map);
        }
        
        
/////////////////////////////////////////////////////////////////        
// TODO: this_instruction is the instruction definition name.
/////////////////////////////////////////////////////////////////
                
// TODO: Begin source processing here        
        boolean isStringOrHeaderType=false;
          //        System.out.println("Definition length: " + definition.length);
        for(int i=0;i<definition.length;i++)
        {
            is_constant=false;
            // Get variable type and strip off comment code.
            var_type=getVariableType(definition[i].split("#")[0]);
            // if is_variable==false then check if current variable is fixed length
            if(is_variable==false)
            {
                // check if current variable is fixed. If variable length set is_variable=true.
                if(!getIsFixedLength(var_type[0])&&var_type.length>=2)
                {
                    is_variable=true;
                }
            }
            if(var_type.length>0&&getIsStringOrHeaderType(var_type[0])&&isStringOrHeaderType==false)
            {
                isStringOrHeaderType=true;              
            }

 //           System.out.println("OutputHandler VARTYPE: " + Arrays.toString(var_type));
            // Length 2 is a type and variable name.
            if(var_type.length>=2)
            {
                if(var_type[1].indexOf("=")!=-1)
                {
  //                  System.out.println("InputHandler variable is a constant!!!!!!!:");
                    is_constant=true;
                }
//          System.out.println("variable type length: " + var_type.length);
           //     for(int j=0;j<var_type.length;j++)
           //     {
//           System.out.println("variable type " + j + ": " + var_type[j]);                   
             //   }
               
                if((type=getJavaType(var_type[0],controller))!=null&&!type.equals("MSG:")&&!is_constant)
                {
// We know it is a variable, don't know the length of that variable to determine number of bytes to read..
// TODO: determine if fixed or variable message definition (fixed are easier), variable contain a variable string otherwise is fixed.                    
//            System.out.println("VAR_TYPE: ".concat(Arrays.toString(var_type)).concat(", TYPE: ").concat(type).concat(", is_fixed_langth").concat(String.valueOf(is_fixed_length)).concat(", is_variable: ").concat(String.valueOf(is_variable)).concat(", getTypeLength(var_type[0]): ").concat(String.valueOf(getTypeLength(var_type[0]))));
                    if((is_fixed_length||is_variable==false)&&getTypeLength(var_type[0])>-1)
                    {
                        // Fixed 
                        // update current_position
                        int length = getROSTypeFixedArrayLength(var_type[0]);
                        if(length!=-1)
                        {
                            // Multiply the array length * the primitive size.
                            length=getTypeLength(var_type[0])*length;
                            current_position=current_position+length;
                        }
                    }
                    else if((is_fixed_length||is_variable==false)&&getTypeLength(var_type[0])==-2&&!is_constant)
                    {
                        // Is Fixed Length Custom Type
                        // Set the instruction primitive variable from bytes
                        String instruction_variable="this_instruction.";
                        if(is_service)
                        {
                            instruction_variable=instruction_variable.concat("REQUEST.");
                        }
                        // sb append variable name
                        instruction_variable=instruction_variable.concat(var_type[1]);
                        // is a  size ROS Custom Object
//TODO: getObjectSource variables is a fixed custom type... 
                        
                        sb.append(getDefinitionVariableLengthObjectSource(instruction_variable, object_package_name, definition[i], definition_map, current_position, true, schema_lookup_map, globals, controller));
                //        if(nested_source!=null&&nested_source[0]!=null)
                  //      {
                            // Update current_byte_array_index
                    //        current_position=((Integer)nested_source[1]).intValue();
                           // System.out.println("CURRENT_POSITION: " + current_position);
                      //  }
                    }
                    else
                    {
                        if(!is_constant)
                        {

                            // Variable length - next determine the start position of the dynamic variable. Preceding variables...  
    // TODO: Write Dynamic code...

                            if(getTypeLength(var_type[0])>-2)
                            {
                                // Variable 
                                // Set the instruction primitive variable from bytes
                                if(!is_service)
                                {
                                    if(getIsTypeArray(var_type[0]))
                                    {
//            System.out.println("IS ARRAY: VAR_TYPE: ".concat(Arrays.toString(var_type)).concat(", TYPE: ").concat(type).concat(", is_fixed_langth").concat(String.valueOf(is_fixed_length)).concat(", is_variable: ").concat(String.valueOf(is_variable)).concat(", getTypeLength(var_type[0]): ").concat(String.valueOf(getTypeLength(var_type[0]))));                                        
                                        // Is variable array
                                        if(getIsFixedLength(var_type[0]))
                                        {
                                            // TODO: This is a fixed length array using a variable length code.... Can be optimized later.
                                            sb.append("((this_instruction.".concat(var_type[1]).concat(".length*"));
                                            // Is Fixed Length
                                            sb.append(getTypeLength(var_type[0]));
                                            sb.append(")+4) +");
                                        }
                                        else
                                        {
                                            // Is variable length
                                            if(getIsStringOrHeaderType(var_type[0]))
                                            {
                                                sb.append("((ROSMessageDefinitionTypeConverter.getStringArrayToBytesLength(this_instruction.".concat(var_type[1].concat(")+4)) +")));
                                            }
                                            else
                                            {
                                                // Is a variable length array
                                                sb.append("((this_instruction.".concat(var_type[1]).concat(".length*"));
                                                // Is Fixed Length
                                                sb.append(getTypeLength(var_type[0]));
                                                sb.append(")+4) +");
                                                }
                                        }
                                    }
                                    else
                                    {
 System.out.println("IS NOT ARRAY: VAR_TYPE: ".concat(Arrays.toString(var_type)).concat(", TYPE: ").concat(type).concat(", is_fixed_langth").concat(String.valueOf(is_fixed_length)).concat(", is_variable: ").concat(String.valueOf(is_variable)).concat(", getTypeLength(var_type[0]): ").concat(String.valueOf(getTypeLength(var_type[0]))));                                         
                                        // is not an array
                                        if(getIsFixedLength(var_type[0]))
                                        {
                                            // fixed
                                            sb.append("("+getTypeLength(var_type[0]));
                                            sb.append(") +");
                                        }
                                        else
                                        {
                                            // variable
                                            if(var_type[0].startsWith("Header"))
                                            {
                                                System.out.println("HEADER CODE");
                                                // is header
                                                 sb.append("12+(((this_instruction.".concat(var_type[1].concat(".frame_id.length()+1)/2)+4) +")));
                                            }
                                            else
                                            {
                                                 System.out.println("VARIABLE: " +var_type[0]);                                               
                                                // is string
                                                sb.append("(((this_instruction.".concat(var_type[1].concat(".length()+1)/2)+4) +")));
                                            }
                                        }
                                    }
                                }
                                if(is_service)
                                {
                                    if(getIsTypeArray(var_type[0]))
                                    {
                                        // Is variable array
                                        if(getIsFixedLength(var_type[0]))
                                        {
                                            sb.append("((this_instruction.REQUEST.".concat(var_type[1]).concat(".length*"));
                                            // Is Fixed Length
                                            sb.append(getTypeLength(var_type[0]));
                                            sb.append(")+4) +");
                                        }
                                        else
                                        {
                                            // Is variable length
                                            if(getIsStringOrHeaderType(var_type[0]))
                                            {
                                                sb.append("((ROSMessageDefinitionTypeConverter.getStringArrayToBytesLength(this_instruction.REQUEST.".concat(var_type[1].concat(")+4)) +")));
                                            }
                                            else
                                            {
                                                // variable length primitive array
                                                sb.append("((this_instruction.REQUEST.".concat(var_type[1]).concat(".length*"));
                                                // Is Fixed Length
                                                sb.append(getTypeLength(var_type[0]));
                                                sb.append(")+4) +");
                                            }
                                        }
                                    }
                                    else
                                    {
                                        // is not an array
                                        if(getIsFixedLength(var_type[0]))
                                        {
                                            // fixed
                                            sb.append("("+getTypeLength(var_type[0]));
                                            sb.append(") +");
                                        }
                                        else
                                        {
                                            // variable
                                            if(var_type[0].endsWith("Header"))
                                            {
                                                // is header
                                                 sb.append("12+(((this_instruction.REQUEST.".concat(var_type[1].concat(".frame_id.length()+1)/2)+4) +")));
                                            }
                                            else
                                            {
                                                System.out.println("VARIABLE: " +Arrays.toString(var_type));
                                                // is string
                                                sb.append("(((this_instruction.REQUEST.".concat(var_type[1].concat(".length()+1)/2)+4) +")));
                                            }
                                        }
                                    }
                                }

// TODO: If not an array use getTypeLength
                                //k
                                //sb.append(";\n");
                                // update current_position
                                int length = getROSTypeFixedArrayLength(var_type[0]);
                                if(length!=-1)
                                {
                                    // Multiply the array length * the primitive size.
                                    length=getTypeLength(var_type[0])*length;
                                    current_position=current_position+length;
                                }
                            }
                            else if(getTypeLength(var_type[0])==-2)
                            {
                                // Is Fixed Length Custom Type
                                // Set the instruction primitive variable from bytes
                                String instruction_variable="this_instruction.";
                                if(is_service)
                                {
                                    instruction_variable=instruction_variable.concat("REQUEST.");
                                }
                                // sb append variable name
                                instruction_variable=instruction_variable.concat(var_type[1]);
                                
                                // is a  size ROS Custom Object
                                sb.append(getDefinitionVariableLengthObjectSource(instruction_variable, object_package_name, definition[i], definition_map, current_position, true, schema_lookup_map, globals, controller));
                                //if(nested_source!=null&&nested_source[0]!=null)
                                //{
                                    // Updated the source code...
                                //    sb.append((String)nested_source[0]);
                                    // Update current_byte_array_index
                                //    current_position=((Integer)nested_source[1]).intValue();
                                   // System.out.println("CURRENT_POSITION: " + current_position);
                                //}
                            }
            //ENd of variable length code...
                        }
                    }
                }
            }
        }
        // Trim, and remove prepended +, and post appended +
        String str=sb.toString().trim();
        if(str.length()==0)
        {
            return str;
        }
        else
        {
            // Insert Header
            return str.substring(0,str.length()-1);
        }
    }
    

    /** getDefinitionVariableLengthObjectSource returns source code for the 
     * getDefinitionVariableLengthSource method.
     * 
     * @param ROS_Type The current message definition variable line text.
     * @param message_definition_map the map containing all the Object definitions
     * @param message the incoming message byte[]
     * @param current_byte_array_index the current index position of the byte[].
     * @param is_fixed_length boolean is the message_definition fixed length.
     * @param globals Map values are Java source code being appended to the class variables section.
     * @return Object[] where 0=String Object source, and 1=post processing 
     * byte[] index position.
     */
    private static String getDefinitionVariableLengthObjectSource(String java_variable_string, String object_package_name, String ROS_Type, Map<String,String> message_definition_map, int current_byte_array_index, boolean is_fixed_length, Map<String,String> schema_lookup_map, Map<String,String> globals, Controller controller)
    {
        // TODO: Update getDefinitionVariableLengthObjectSource to return String with additional numbers to add in byte[]...

   //     final Object[] return_object_array=new Object[2];
        StringBuilder sb = new StringBuilder();// variable type
        String type=null;
        String[] var_type=getVariableType(ROS_Type);
        boolean is_array;
        String array_text;
        String value;
        boolean fixed_length_array=false;
        int array_length=-1;
        // if its an array the index count will be stored in array_count.
        int array_count=0;
        // TODO: 1. Get message_definition for Object type
        if (ROS_Type.indexOf("[") != -1)
        {
            is_array = true;
            type = ROS_Type.substring(0,ROS_Type.indexOf("["));
            fixed_length_array=getIsFixedLength(var_type[0]);
            if(fixed_length_array)
            {
                array_length=getArrayLength(var_type[0]);
            }
        }
        else
        {
            is_array = false;
        }        
        //if(is_fixed_length)
       // {
        if(!is_array)
        {
            // Not an array...
            type=var_type[0];
            // Get the Object Type Definition
//                System.out.println("Object Type definition should only be called on Objects and not primitives...");       

            String definition = getObjectTypeDefinitionInMap(type, message_definition_map);
//            System.out.println("TYPE getDefinitionVariableLengthObjectSource: ".concat(type).concat("\ndefinition: ").concat(definition));            
            if(definition==null)
            {
//                System.out.println("!array Sub-child custom schema Objects not supported for this version of autonomous code generation");
                // Check for definition in Schema Map
                String type_location=schema_lookup_map.get((object_package_name + type).toLowerCase());
                if(type_location==null)
                {
//                    System.out.println("!array Schema Lookup Map:\n"+schema_lookup_map.toString()+"\nCan't find type: "+ (object_package_name + type).toLowerCase());
                }
                else
                {
//                    System.out.println("!array Found sub-child custom schema Object location for: " +(object_package_name + type).toLowerCase()+", Schema File: "+ getSchemaFile((object_package_name + type).toLowerCase(),controller));
                    // set the definition:
                    definition=getSchemaFile((object_package_name + type).toLowerCase(),controller);
//                    System.out.println("Found sub-child custom schema Object location for: " +(object_package_name + type).toLowerCase()+", Schema File: "+ definition);
                    // String sub_schema_file=getSchemaFile(type_location,controller);

                }
                // If definition exists, get the schema, and add to Definition Map
                // Check for Objects in Schema Definition, and if they exist, check the Definition Map to see if they were added yet. 
                // If Definition already added skip, and proceed to next Object
                // If no more Objects we are complete retreiving Schema Definitions, and ready to proceed.

            }                

            // process type definition to type array.
            String[] type_definition=process(definition);
  //          System.out.println("TYPE_DEFINITION: ".concat(Arrays.toString(type_definition)));
            // process to java source
            int type_length=0;           
            for(int i=0;i<type_definition.length;i++)
            {
                // process the type definition into source code.
                if(type_definition[i]!=null)
                {
                      String[] nested_var_type=getVariableType(type_definition[i]);
                    int current_type_length=getTypeLength(nested_var_type[0]);
// TODO: Only covering fixed length, and can screw up dynamic length, so needs a fix
                   /* boolean is_type_array=getIsTypeArray(type_definition[i]);
                    boolean is_fixed_array_type=false;
                    if(is_type_array)
                    {
                        is_fixed_array_type=getIsFixedLength(type_definition[i]);
                    }
                    */
                       
        /*                    if(getIsTypeArray(type_definition[i]))
                    {
                        // Update current_type_length to current_type_length of array
//a
// TODO: Still need to fix the fixed array followed by primitive index being wrong.
                        current_type_length=current_type_length*getFixedTypeArrayLength(type_definition[i]);
                    }                        
                    */ 
                    if(current_type_length<0)
                    {
                        // if type length is less then zero process the value.
                        if(current_type_length==-2)
                        {
                            // is a fixed size ROS Custom Object
                            //Object[] nested_source;
                            String nested_length;
                            if(var_type[0].contains("/"))
                            {
                                // set object_package_name
                               // nested_source=getObjectSource(java_variable_string.concat(".").concat(nested_var_type[1]), "ros/"+var_type[0].substring(0,var_type[0].lastIndexOf("/")+1), type_definition[i], message_definition_map, current_byte_array_index, true, schema_lookup_map, globals, controller);
                                nested_length=getDefinitionVariableLengthObjectSource(java_variable_string.concat(".").concat(nested_var_type[1]), "ros/"+var_type[0].substring(0,var_type[0].lastIndexOf("/")+1), type_definition[i], message_definition_map, current_byte_array_index, true, schema_lookup_map, globals, controller);
                            }
                            else
                            {
                                // set object_package_name to null
                                //nested_source=getObjectSource(java_variable_string.concat(".").concat(nested_var_type[1]), object_package_name, type_definition[i], message_definition_map, current_byte_array_index, true, schema_lookup_map, globals, controller);
                                nested_length=getDefinitionVariableLengthObjectSource(java_variable_string.concat(".").concat(nested_var_type[1]), object_package_name, type_definition[i], message_definition_map, current_byte_array_index, true, schema_lookup_map, globals, controller);                                
                            }                                 

                            /*if(nested_source!=null&&nested_source[0]!=null)
                            {
                                // Updated the source code...
                              //  sb.append((String)nested_source[0]);
                              //  sb.append("\n");
                                sb.append(getDefinitionFixedLength(message_definition_map)+"+");
                                // Update current_byte_array_index
                                current_byte_array_index=((Integer)nested_source[1]).intValue();
                            }*/
                            if(nested_length!=null)
                            {
//                                sb.append(nested_length+"+");
                                  sb.append(nested_length);

                                // Update current_byte_array_index
                 //               current_byte_array_index=((Integer)nested_source[1]).intValue();                                
                            }
                            
                        }
                        else if(current_type_length==-3)
                        {
                            // Stop and return Source String
                            //return_object_array[0]=sb.toString();
                            //return_object_array[1]=current_byte_array_index;
                            System.out.println("BUG:? else if(current_type_length==-3)");
                            return sb.toString();
                        }                        
                        else if(current_type_length==-1)
                        {
                            // is a variable length string, or header
                            if(var_type[0].endsWith("Header"))
                            {
                                // is header
                                 sb.append("12+(((".concat(java_variable_string).concat(".").concat(nested_var_type[1].concat(".frame_id.length()+1)/2)+4) +")));
                            }
                            else
                            {
    //                            System.out.println("VARIABLE: " +Arrays.toString(var_type));
                                // is string
                                sb.append("(((".concat(java_variable_string).concat(".").concat(nested_var_type[1].concat(".length()+1)/2)+4) +")));
                            }
                        }
                    }
                    else
                    {
                        // Is a primitive length variable... append source code to sb, and array index to byte[] current position
//System.out.println("NESTED_VAR_TYPE[0]: " + nested_var_type[0] + ", NESTED_VAR_TYPE[1]: " + nested_var_type[1] + ", current_byte_array_index: " + current_byte_array_index + ", CURRENT_TYPE_LENGTH: " + current_type_length);                            
                        //sb.append(java_variable_string.concat(".").concat(nested_var_type[1]));
                        //sb.append("=");
                        // Not an array so pass in null for assignment_variable.
                        //sb.append(nullStringToEmptyString(getFixedROSTypeJavaSource(java_variable_string.concat(".").concat(nested_var_type[1]),nested_var_type[0],current_byte_array_index)));
                        //sb.append("\n");
                        sb.append(current_type_length);
                        sb.append("+");
                        // update the current index position
                        current_byte_array_index=current_type_length+current_byte_array_index;
                    }
                }
            }
        }
        else
        {
            // TODO: is an array 
            int current_type_length;
            // create an array loop
            if(fixed_length_array)
            {
                for(int j=0;j<array_length;j++)
                {
                    String definition = getObjectTypeDefinitionInMap(type, message_definition_map);

                    if(definition==null)
                    {
 //                       System.out.println("array Sub-child custom schema Objects not supported for this version of autonomous code generation");
                        // Check for definition in Schema Map
                        String type_location=schema_lookup_map.get((object_package_name + type).toLowerCase());
                        if(type_location==null)
                        {
//                            System.out.println("array Schema Lookup Map:\n"+schema_lookup_map.toString()+"\nCan't find type: "+ (object_package_name + type).toLowerCase());
                        }
                        else
                        {
   //                         System.out.println("array Found sub-child custom schema Object location for: " +(object_package_name + type).toLowerCase()+", Schema File: "+ getSchemaFile((object_package_name + type).toLowerCase(),controller));
                            // set the definition:
                            definition=getSchemaFile((object_package_name + type).toLowerCase(),controller);
                            System.out.println("array Found sub-child custom schema Object location for: " +(object_package_name + type).toLowerCase()+", Schema File: "+ definition);
                            // String sub_schema_file=getSchemaFile(type_location,controller);

                        }
                        // If definition exists, get the schema, and add to Definition Map
                        // Check for Objects in Schema Definition, and if they exist, check the Definition Map to see if they were added yet. 
                        // If Definition already added skip, and proceed to next Object
                        // If no more Objects we are complete retreiving Schema Definitions, and ready to proceed.

                    }                

                    // process type definition to type array.
                    String[] type_definition=process(definition);
                    // process to java source
                    int type_length=0;           
                    for(int i=0;i<type_definition.length;i++)
                    {
                        // process the type definition into source code.
                        if(type_definition[i]!=null)
                        {
                            String[] nested_var_type=getVariableType(type_definition[i]);
                            current_type_length=getTypeLength(nested_var_type[0]);
                            if(getIsTypeArray(type_definition[i]))
                            {
                                // Update current_type_length to current_type_length of array
    //a
        // TODO: Still need to fix the fixed array followed by primitive index being wrong.
                                current_type_length=current_type_length*getFixedTypeArrayLength(type_definition[i]);
                            }
                            if(current_type_length<0)
                            {
                                // if type length is less then zero process the value.
                                if(current_type_length==-2)
                                {
                                    // is a fixed size ROS Custom Object
                                    Object[] nested_source;
                                    if(var_type[0].contains("/"))
                                    {
                                        // set object_package_name
                                        nested_source=getObjectSource(java_variable_string.concat("[").concat(String.valueOf(j)).concat("]").concat(".").concat(nested_var_type[1]), "ros/"+var_type[0].substring(0,var_type[0].lastIndexOf("/")+1), type_definition[i], message_definition_map, current_byte_array_index, true, schema_lookup_map, globals, controller);

                                    }
                                    else
                                    {
                                        // set object_package_name to null
                                        nested_source=getObjectSource(java_variable_string.concat("[").concat(String.valueOf(j)).concat("]").concat(".").concat(nested_var_type[1]), object_package_name, type_definition[i], message_definition_map, current_byte_array_index, true, schema_lookup_map, globals, controller);                                    
                                    }                                     
    //                                    System.out.println("ARRAY ALERT2: "+ java_variable_string.concat("[").concat(String.valueOf(j)).concat("]").concat(".").concat(nested_var_type[1]));
                                    if(nested_source!=null&&nested_source[0]!=null)
                                    {
                                        // Updated the source code...
                                      //  sb.append((String)nested_source[0]);
                                      //  sb.append("\n");
                                      sb.append(getDefinitionFixedLength(message_definition_map)+"+");
                                        // Update current_byte_array_index
                                        current_byte_array_index=((Integer)nested_source[1]).intValue();
                                    }
                                }
                                else if(current_type_length==-3)
                                {
                                    // Stop and return Source String
                                    //return_object_array[0]=sb.toString();
                                    //return_object_array[1]=current_byte_array_index;
                                    //return return_object_array;
                                    System.out.println("BUG:? else if(current_type_length==-3)");
                                    return sb.toString();                                    
                                }                        
                                else if(current_type_length==-1)
                                {
                                    // is a variable length string
                                    sb.append("((("+java_variable_string+"."+nested_var_type[1]+".length()+1)/2)+4) +");
                                }
                            }
                            else
                            {
                                // Is a primitive length variable... append source code to sb, and array index to byte[] current position
                               // sb.append(java_variable_string.concat("[").concat(String.valueOf(j)).concat("]").concat(".").concat(nested_var_type[1]));
                                //sb.append("=");
                                //sb.append(nullStringToEmptyString(getFixedROSTypeJavaSource(java_variable_string.concat("[").concat(String.valueOf(j)).concat("]").concat(".").concat(nested_var_type[1]), nested_var_type[0],current_byte_array_index)));
    //                                sb.append("\n");
                                sb.append(current_type_length);
                                sb.append("+");        
                                // update the current index position
                                current_byte_array_index=current_type_length+current_byte_array_index;
                            }
                        }
                    }
                } 
            }
            else
            {
                // TODO: Variable length array.
                // is variable length array needs to be counted by bytes...
   //    c
            }
        }
        
        // Return the Object[].
//        return_object_array[0]=sb.toString();
//        return_object_array[1]=current_byte_array_index;
//        return return_object_array;
        return sb.toString();
    }
    
    
    
    /** Return the source code for the process method source code body that processes message_definition variables. 
     * @return Object[] where [0]=String source, [1]=Map<String,String> globals
     */
    private static Object[] getProcessSource(int hid, String instruction_class_name, String package_name, Map<String,String> definition_map, Controller controller, boolean is_service, boolean is_action, boolean is_result, boolean has_response_variables, HashMap header, Map globals, Map<String,String> schema_lookup_map)
    {
        String object_package_name=null;
        if(package_name!=null)
        {
           object_package_name=package_name.replaceAll("\\.","/").concat("/");
        }    
        
        StringBuilder sb = new StringBuilder();// variable type
        String type;
        String[] var_type;
        String[] definition=process(definition_map.get("message_definition"));
        // The current byte position of the Java source byte[]. 
        int current_position=0;
        //' is_variable will be set to true on the first non-fixed variable in a variable length message definition.
        boolean is_variable=false;
        // Is a ROS Message Definition constant variable.
        boolean is_constant=false;
        //boolean is_fixed_length=false;
//TODO:  Step one check if variable or fixed length, and set is_fixed_length
  
        boolean is_fixed_length=getIsDefinitionFixedLength(definition_map); 
        int fixed_length=-1;
        if(is_fixed_length)
        {
            fixed_length=getDefinitionFixedLength(definition_map);
        }
//System.out.println("IS FIXED LENGTH MAP:"+is_fixed_length+", "+instruction_class_name + ", hid: " + hid);          
/*        if(hid!=-1)
        {
            // Add isFixedLength variable to InstructionManagerHardwareRegistry.  
            InstructionManagerHardwareRegistry.put(String.valueOf(hid).concat(".6"), String.valueOf(is_fixed_length));
        }   
        // Set the default length of fixed and non-fixed definitions.
        if(is_fixed_length)
        {
            // Add definition length variable to InstructionManagerHardwareRegistry.  
            InstructionManagerHardwareRegistry.put(String.valueOf(hid).concat(".9"), String.valueOf(getDefinitionFixedLength(definition_map)));
        }
        else
        {
            // Add default definition length 1024 variable to InstructionManagerHardwareRegistry.  
            InstructionManagerHardwareRegistry.put(String.valueOf(hid).concat(".9"), String.valueOf(1024));
        }
*/
        
        // TODO: Checkout byte array from an Object pool if helps performance... (May need to create Byte array pool, but probably already exists in another class like the one that is used for receiving incoming data...)  
        
// Define the ROS message byte array length. 
s        
// TODO: ensure if is a service that is the request portion of the service definition in the map        
        if(is_fixed_length&&fixed_length>-1)
        {
            // Fixed length variable for ros_msg.
            sb.append("final byte[] ros_msg = new byte[");
            // Fill in Array length with byte[] length of message...   
            sb.append(4+fixed_length);
            sb.append("];\n");
        }
        else
        {
            // Variable byte array length for ros_msg.
            sb.append("final byte[] ros_msg = new byte[");
            // Fill in Array length with dynamic variable length code... No idea why I called this previosuly, and don't want to break anything so am leaving it. I commented out code below, so don't know its purpose. Probably useless. 
            String str=getDefinitionLengthSource(definition);
            
//            if(str.trim().isEmpty())
//            {
                sb.append(getDefinitionVariableLengthSource(definition_map, object_package_name, controller, is_service, is_action, is_result, has_response_variables, schema_lookup_map, globals));               
//            }
//            else
 //           {
 //               System.out.println("Is str prepended data? str: ".concat(str));
 //               sb.append(str+"+"+getDefinitionVariableLengthSource(definition_map, object_package_name, controller, is_service, is_action, is_result, has_response_variables, schema_lookup_map, globals));
 //           }
            sb.append("];\n");
        }
        
        
//////////////////////////////
// TODO: FInish method....
/////////////////////////////////////////////        
        
         
// Add the Instruction assignment variable.
   sb.append("this.this_instruction=("+instruction_class_name+")instruction;\n");                

 //       sb.append("final ");
 //       sb.append(instruction_class_name);
 //       sb.append(" this_instruction=(");
 //       sb.append(instruction_class_name);
 //       sb.append(")instruction;\n");
        
// TODO: Process SRC below ****************************************************
        
// Code the processing of the incoming byte[]to an Instruction
 //       sb.append("    final ");
 //       sb.append(instruction_class_name);
 //       sb.append(" instruction=pool.checkout();\n");
  
         
// TODO: Begin source processing here        
        boolean isStringOrHeaderType=false;
          //        System.out.println("Definition length: " + definition.length);
        for(int i=0;i<definition.length;i++)
        {
            is_constant=false;
            // Get variable type and strip off comment code.
            var_type=getVariableType(definition[i].split("#")[0]);
            globals=updateGlobalMap(var_type[0], globals);  
    //        System.out.println("updateGlobalMap:"+globals.toString());
            // if is_variable==false then check if current variable is fixed length
            if(is_variable==false)
            {
                // check if current variable is fixed. If variable length set is_variable=true.
                if(!getIsFixedLength(var_type[0])&&var_type.length>=2)
                {
                    is_variable=true;
                    sb.append("int current_position=");
                    sb.append(String.valueOf(current_position));
                    sb.append(";\n");
                }
            }
            if(var_type.length>0&&getIsStringOrHeaderType(var_type[0])&&isStringOrHeaderType==false)
            {
                isStringOrHeaderType=true;
                sb.append("int length;// TODO: This may only apply to InputHandler, and not OutputHandler verify...\n");                
            }

//            System.out.println("InputHandler VARTYPE: " + Arrays.toString(var_type));
            // Length 2 is a type and variable name.
            if(var_type.length>=2)
            {
                if(var_type[1].indexOf("=")!=-1)
                {
  //                  System.out.println("InputHandler variable is a constant!!!!!!!:");
                    is_constant=true;
                }
//          System.out.println("variable type length: " + var_type.length);
           //     for(int j=0;j<var_type.length;j++)
           //     {
//           System.out.println("variable type " + j + ": " + var_type[j]);                   
             //   }
               
                if((type=getJavaType(var_type[0],controller))!=null&&!type.equals("MSG:")&&!is_constant)
                {
// We know it is a variable, don't know the length of that variable to determine number of bytes to read..
// TODO: determine if fixed or variable message definition (fixed are easier), variable contain a variable string otherwise is fixed.                    

                    if((is_fixed_length||is_variable==false)&&getTypeLength(var_type[0])>-1)
                    {
                        // Fixed 
                        // Set the instruction primitive variable from bytes
                        if(!is_service)
                        {
                            sb.append(nullStringToEmptyString(getFixedROSTypeJavaSource("this_instruction.".concat(var_type[1]),var_type[0], current_position)));
                        }
                        if(is_service)
                        {
                            sb.append(nullStringToEmptyString(getFixedROSTypeJavaSource("this_instruction.REQUEST.".concat(var_type[1]),var_type[0], current_position)));
                        }
                        // sb append variable name
                        //sb.append(var_type[1]);
                        //sb.append("=");
                        sb.append("\n");
                        //sb.append(";\n");
                        // update current_position
                        int length = getROSTypeFixedArrayLength(var_type[0]);
                        if(length!=-1)
                        {
                            // Multiply the array length * the primitive size.
                            length=getTypeLength(var_type[0])*length;
                            current_position=current_position+length;
                        }
                    }
                    else if((is_fixed_length||is_variable==false)&&getTypeLength(var_type[0])==-2&&!is_constant)
                    {
                        // Is Fixed Length Custom Type
                        // Set the instruction primitive variable from bytes
                        String instruction_variable="this_instruction.";
                        if(is_service)
                        {
                            instruction_variable=instruction_variable.concat("REQUEST.");
                        }
                        // sb append variable name
                        instruction_variable=instruction_variable.concat(var_type[1]);
                        // is a  size ROS Custom Object
                        Object[] nested_source;
                        if(var_type[0].contains("/"))
                        {
                            // set object_package_name
                            nested_source=getObjectSource(instruction_variable, "ros/"+var_type[0].substring(0,var_type[0].lastIndexOf("/")+1), definition[i], definition_map, current_position, true, schema_lookup_map,globals, controller);

                        }
                        else
                        {
                            // set object_package_name to null
                            nested_source=getObjectSource(instruction_variable, object_package_name, definition[i], definition_map, current_position, true, schema_lookup_map,globals, controller);                                    
                        } 
                        
                        globals=(Map<String,String>)nested_source[2];
                        if(nested_source!=null&&nested_source[0]!=null)
                        {
                            // Updated the source code...
                            sb.append((String)nested_source[0]);
                            sb.append("\n");
                            // Update current_byte_array_index
                            current_position=((Integer)nested_source[1]).intValue();
                           // System.out.println("CURRENT_POSITION: " + current_position);
                        }
                    }
                    else
                    {
                        if(!is_constant)
                        {
                            // Variable length - next determine the start position of the dynamic variable. Preceding variables...  
    // TODO: Write Dynamic code...
                            // Add import for the ROSMessageDefinitionTypeConverter
                            header.put("type_converter", "import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.generator.ROSMessageDefinitionTypeConverter;");

                            //if(getTypeLength(var_type[0])>-1)
                            if(getTypeLength(var_type[0])>-2)
                            {
                                // Variable 
                                // Set the instruction primitive variable from bytes
                                if(!is_service)
                                {
                                    sb.append(getVariableROSTypeJavaSource("this_instruction.".concat(var_type[1]),var_type[0], "current_position"));
                                }
                                if(is_service)
                                {
                                    sb.append(getVariableROSTypeJavaSource("this_instruction.REQUEST.".concat(var_type[1]),var_type[0], "current_position"));
                                }
                                // sb append variable name
                                //sb.append(var_type[1]);
                                //sb.append("=");

// TODO: If not an array use getTypeLength

    // TODO: If is an array, get the Java type, and... I think we need to increment the count in each Source method instead of here.                             

                                //k
                                //sb.append(";\n");
                                // update current_position
                                int length = getROSTypeFixedArrayLength(var_type[0]);
                                if(length!=-1)
                                {
                                    // Multiply the array length * the primitive size.
                                    length=getTypeLength(var_type[0])*length;
                                    current_position=current_position+length;
                                }
                            }
                            else if(getTypeLength(var_type[0])==-2)
                            {
                                // Is Fixed Length Custom Type
                                // Set the instruction primitive variable from bytes
                                String instruction_variable="this_instruction.";
                                if(is_service)
                                {
                                    instruction_variable=instruction_variable.concat("REQUEST.");
                                }
                                // sb append variable name
                                instruction_variable=instruction_variable.concat(var_type[1]);
                                // is a  size ROS Custom Object
                               // Object[] nested_source=getObjectSource(instruction_variable, object_package_name,definition[i], definition_map, current_position, true, schema_lookup_map, globals, controller);
                                
                                Object[] nested_source;
                                if(var_type[0].contains("/"))
                                {
                                    // set object_package_name
                                    nested_source=getObjectSource(instruction_variable, "ros/"+var_type[0].substring(0,var_type[0].lastIndexOf("/")+1), definition[i], definition_map, current_position, true, schema_lookup_map,globals, controller);

                                }
                                else
                                {
                                    // set object_package_name to null
                                    nested_source=getObjectSource(instruction_variable, object_package_name, definition[i], definition_map, current_position, true, schema_lookup_map,globals, controller);                                    
                                }                                                
                                
                                globals=(Map<String,String>)nested_source[2];
                                if(nested_source!=null&&nested_source[0]!=null)
                                {
                                    // Updated the source code...
                                    sb.append((String)nested_source[0]);
                                    sb.append("\n");
                                    // Update current_byte_array_index
                                    current_position=((Integer)nested_source[1]).intValue();
                                   // System.out.println("CURRENT_POSITION: " + current_position);
                                }
                            }
            //ENd of variable length code...
    //        a
            // TODO: Add Support for variable array custom Objects and test existing methods.... SetPen InputHandler is missing request types, Log has no InputHandler
                        }
                    }
                }
            }
        }


        
        /*          
sb.append("public ");
                    // if is a constant variable set to final static
                    if(var_type[1].indexOf("=")!=-1)
                    {
                       sb.append("final static "); 
                    }
                    sb.append(type);
                    for(int s=1;s<var_type.length;s++)
                    {
                        sb.append(" ");
                        sb.append(var_type[s]);
                    }
                    // If array of specified length, instantiate array to length specified
                    int open;
                    int close=-1;
                    int array_length;
                    if((open=var_type[0].indexOf("["))!=-1)
                    {
                        close=var_type[0].indexOf("]");
                        if(open+2<=close)
                        {
                            // fixed array
                            sb.append("=new ");
                            sb.append(var_type[0]);
                        }
                    }
 
                    // If primitive Object type instantiate.
                    if(type.equals("Header"))
                    {
                        sb.append("=new Header()"); 
                        // Add Header class import to header.
                        header.put("header", "import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.generator.type.std_msgs.Header;");
                    }                    
                    else if(type.equals("Time"))
                    {
                        sb.append("=new Time()"); 
                        // Add Time class import to header.
                        header.put("time", "import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.generator.type.Time;");
                    }
                    else if(type.indexOf("Time[")!=-1&&open+2<=close)
                    {
                        sb.append(";\n");
                        sb.append(var_type[1]);
                        sb.append("={");

                        // get the array length
                        array_length=Integer.parseInt(var_type[0].substring(open+1,close));
                        sb.append("new Time()");
                        for(int a=1;a<array_length;a++)
                        {
                            sb.append(",new Time()");
                        }
                    
                        sb.append("}");
                        // Add Time class import to header.
                        header.put("time", "import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.generator.type.Time;");
                    }                    
                    else if(type.equals("Duration"))
                    {
                        sb.append("=new Duration()");   
                        // Add Duration class import to header.
                        header.put("duration", "import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.generator.type.Duration;");                        
                    }
                    else if(type.indexOf("Duration[")!=-1&&open+2<=close)
                    {
                        sb.append(";\n");
                        sb.append(var_type[1]);
                        sb.append("={");

                        // get the array length
                        array_length=Integer.parseInt(var_type[0].substring(open+1,close));
                        sb.append("new Duration()");
                        for(int a=1;a<array_length;a++)
                        {
                            sb.append(",new Duration()");
                        }
                    
                        sb.append("}");
                        // Add Time class import to header.
                        header.put("time", "import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.generator.type.Duration;");
                    }    
                    else if(type.equals("Empty")||type.indexOf("/Empty")!=-1)
                    {
                        sb.append("=new Empty()");   
                        // Add Duration class import to header.
                        header.put("empty", "import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.generator.type.std_msgs.Empty;");                        
                    }
                    else if(type!=null&&type.indexOf("[")==-1&&isCustomType(type))
                    {
                        // Not an Array
                        sb.append("=new ");
                        sb.append(type);
                        sb.append("()");
                    }
                    else if(type!=null&&type.indexOf("[")==-1 &&open+2<=close&&isCustomType(type))                  
                    {
                        // Is an array
                        // If unknown type we assume it is autogenerated class, therefore we must instatiate it in source.
                        sb.append(";\n");
                        sb.append(var_type[1]);
                        sb.append("={");

                        // get the array length
                        array_length=Integer.parseInt(var_type[0].substring(open+1,close));
                        sb.append("new ");
                        sb.append(type.substring(0,type.indexOf("[")));
                        sb.append("()");
                        for(int a=1;a<array_length;a++)
                        {
                            sb.append(",new ");
                            sb.append(type.substring(0,type.indexOf("[")));
                            sb.append("()");
                        }

                        sb.append("\n}");                           
                    }
                }
                else if(type!=null&&type.equals("MSG:"))
                {
                    if(is_service||is_action)
                    {
                        sb.append("}\n");
                    }
                    // If type is MSG: its time to end the current loop, and call process on the new message if the class does not exist.
                    i=definition.length;
                }
                else if(type!=null&&type.equals("---"))
                {
                    if(is_service)
                    {
                        // If type is --- class the REQUEST CLass, and start the REQUEST Class.
                        sb.append("\n}\n");
                        sb.append("class Response extends Instruction\n{\n");                   
                    }   
                    else if (is_action&&!is_result)
                    {
                        // Is Result ACTION Class
                        is_result=true;
                        sb.append("\n}\n");
                        sb.append("class Result extends Instruction\n{\n");            
                    }
                    else if (is_action&&is_result)
                    {
                        // Is Feedback ACTION Class
                        sb.append("\n}\n");
                        sb.append("class Feedback extends Instruction\n{\n");                                       }
                    
                }
                else
                {
                    // Type not recognized, is a custom type.
                    // Throw an Exception of some sort to determine alternative options.
      
                    System.out.println("Attempt to generate unsupported type in source code: " + var_type[0] + " " + var_type[1] + ", Source code for class " + package_name + "." + instruction_class_name + "InputHandler invalid...");
                 

                    
                }
                // If not MSG: type append the semi-colon
                if(type!=null&&!type.equals("MSG:"))
                {
          //          if(is_service)
          //          {
          //              sb.append("}\n");
          //          }
                    sb.append(";");
                }
            }
            else
            {
                // var length other than 2
                if((type=getJavaType(var_type[0],controller))!=null&&type.equals("---"))
                {
                    if(is_service)
                    {
                        // If type is --- class the REQUEST CLass, and start the REQUEST Class.
                        sb.append("}\n");
                        sb.append("class Response extends Instruction\n{\n");                   
                    }   
                    else if (is_action&&!is_result)
                    {
                        // Is Result ACTION Class
                        is_result=true;
                        sb.append("}\n");
                        sb.append("class Result extends Instruction\n{\n");            
                    }
                    else if (is_action&&is_result)
                    {
                        // Is Feedback ACTION Class
                        sb.append("\n}\n");
                        sb.append("class Feedback extends Instruction\n{\n");                                       }
                    if(i+1==definition.length)
                    {
                        sb.append("\n}");
                    }
                    else
                    {
                        has_response_variables=true;
                    }
                }
                else
                {
                    if(var_type.length==1)
                    {
                        System.out.println("Unsupported single length variable type that is not a service request/response separator. Type: ".concat(var_type[0]));
                    }
                    else
                    {
                        System.out.println("Unsupported single length variable type that is not a service request/response separator. - No variable at all");                        
                    }
                }
                System.out.println("ariable type length: " + var_type.length + "array: " + Arrays.deepToString(var_type));
            }
        }
        // Class Closing Bracket
        if((is_service)&&has_response_variables)
        {
            sb.append("\n}\n}");
        }
        else
        {
            sb.append("\n}");
        }
        */

        // Insert Header
        Object[] resp_array=new Object[2];
        resp_array[0]=sb.toString();
        resp_array[1]=globals;
        return resp_array;
    }

    
    /** getObjectSource returns source code for the current message 
     * definition Object.
     * 
     * @param java_variable_string The current message definition variable line text.
     * @param object_package_name The current object package name.     
     * @param message_definition_map the map containing all the Object definitions
     * @param message the incoming message byte[]
     * @param current_byte_array_index the current index position of the byte[].
     * @param is_fixed_length boolean is the message_definition fixed length.
     * @return Object[] where 0=String Object source, 1=post processing 
     * byte[] index position, and 2=globals.
     */
    private static Object[] getObjectSource(String java_variable_string, String object_package_name, String ROS_Type, Map<String,String> message_definition_map, int current_byte_array_index, boolean is_fixed_length, Map<String,String> schema_lookup_map, Map<String,String> globals, Controller controller)
    {
//        globals=updateGlobalMap(ROS_Type, globals); 
//        System.out.println("updateGlobalMap:"+globals.toString());        
        final Object[] return_object_array=new Object[3];
        StringBuilder sb = new StringBuilder();// variable type
        String type=null;
        String[] var_type=getVariableType(ROS_Type);
        boolean is_array;
        String array_text;
        String value;
        boolean fixed_length_array=false;
        int array_length=-1;
        // if its an array the index count will be stored in array_count.
        int array_count=0;
        // TODO: 1. Get message_definition for Object type
        if (ROS_Type.indexOf("[") != -1)
        {
            is_array = true;
            type = ROS_Type.substring(0,ROS_Type.indexOf("["));
            fixed_length_array=getIsFixedLength(var_type[0]);
            if(fixed_length_array)
            {
                array_length=getArrayLength(var_type[0]);
            }
        }
        else
        {
            is_array = false;
        }        
        //if(is_fixed_length)
       // {
            if(!is_array)
            {
                // Not an array...
                type=var_type[0];
                // Get the Object Type Definition
//                System.out.println("Object Type definition should only be called on Objects and not primitives...");            
                String definition = getObjectTypeDefinitionInMap(type, message_definition_map);
                if(definition==null)
                {
//                    System.out.println("Sub-child custom schema Objects not supported for this version of autonomous code generation");
                    // Check for definition in Schema Map
                    String type_location=schema_lookup_map.get((object_package_name + type).toLowerCase());
                    if(type_location==null)
                    {
//                        System.out.println("Schema Lookup Map:\n"+schema_lookup_map.toString()+"\nCan't find type: "+ (object_package_name + type).toLowerCase());
                    }
                    else
                    {
//                        System.out.println("Found sub-child custom schema Object location for: " +(object_package_name + type).toLowerCase()+", Schema File: "+ getSchemaFile((object_package_name + type).toLowerCase(),controller));
                        // set the definition:
                        definition=getSchemaFile((object_package_name + type).toLowerCase(),controller);
  //                      System.out.println("Found sub-child custom schema Object location for: " +(object_package_name + type).toLowerCase()+", Schema File: "+ definition);
                        // String sub_schema_file=getSchemaFile(type_location,controller);
                        
                    }
                    // If definition exists, get the schema, and add to Definition Map
                    // Check for Objects in Schema Definition, and if they exist, check the Definition Map to see if they were added yet. 
                    // If Definition already added skip, and proceed to next Object
                    // If no more Objects we are complete retreiving Schema Definitions, and ready to proceed.
                    
                }                
                // process type definition to type array.
                String[] type_definition=process(definition);
                // process to java source
                int type_length=0;           
                for(int i=0;i<type_definition.length;i++)
                {
                    // process the type definition into source code.
                    if(type_definition[i]!=null)
                    {
                        String[] nested_var_type=getVariableType(type_definition[i]);
                        int current_type_length=getTypeLength(nested_var_type[0]);
                        // Update Globals Map
                    globals=updateGlobalMap(nested_var_type[0], globals); 
//                    System.out.println("updateGlobalMap:"+globals.toString());                      
                        if(getIsTypeArray(type_definition[i]))
                        {
                            // Update current_type_length to current_type_length of array
//a
    // TODO: Still need to fix the fixed array followed by primitive index being wrong.
                            current_type_length=current_type_length*getFixedTypeArrayLength(type_definition[i]);
                        }                        
                        if(current_type_length<0)
                        {
                            // if type length is less then zero process the value.
                            if(current_type_length==-2)
                            {
                                // is a fixed size ROS Custom Object

                                // if var_type[0] contains a package name, then set an object_package_name in getObjectSource. Otherwise, set it to null.
                                Object[] nested_source;
                                if(var_type[0].contains("/"))
                                {
                                    // set object_package_name
                                    nested_source=getObjectSource(java_variable_string.concat(".").concat(nested_var_type[1]), "ros/"+var_type[0].substring(0,var_type[0].lastIndexOf("/")+1), type_definition[i], message_definition_map, current_byte_array_index, true, schema_lookup_map,globals, controller);

                                }
                                else
                                {
                                    // set object_package_name to null
                                    nested_source=getObjectSource(java_variable_string.concat(".").concat(nested_var_type[1]), object_package_name, type_definition[i], message_definition_map, current_byte_array_index, true, schema_lookup_map,globals, controller);                                    
                                }                                
                                globals=(Map<String,String>)nested_source[2];                                                          if(nested_source!=null&&nested_source[0]!=null)
                                {
                                    // Updated the source code...
                                    sb.append((String)nested_source[0]);
                                    sb.append("\n");
                                    // Update current_byte_array_index
                                    current_byte_array_index=((Integer)nested_source[1]).intValue();
                                }
                            }
                            else if(current_type_length==-3)
                            {
                                // Stop and return Source String
                                return_object_array[0]=sb.toString();
                                return_object_array[1]=current_byte_array_index;
                                return_object_array[2]=globals;
                                return return_object_array;
                            }                        
                            else if(current_type_length==-1)
                            {
                                // is a variable length string

                            }
                        }
                        else
                        {
                            // Is a primitive length variable... append source code to sb, and array index to byte[] current position
//System.out.println("NESTED_VAR_TYPE[0]: " + nested_var_type[0] + ", NESTED_VAR_TYPE[1]: " + nested_var_type[1] + ", current_byte_array_index: " + current_byte_array_index + ", CURRENT_TYPE_LENGTH: " + current_type_length);                            
                            //sb.append(java_variable_string.concat(".").concat(nested_var_type[1]));
                            //sb.append("=");
                            // Not an array so pass in null for assignment_variable.
                            sb.append(nullStringToEmptyString(getFixedROSTypeJavaSource(java_variable_string.concat(".").concat(nested_var_type[1]),nested_var_type[0],current_byte_array_index)));
                            sb.append("\n");
                            // update the current index position
                            current_byte_array_index=current_type_length+current_byte_array_index;
                        }

                    }
                }
            }
            else
            {
                // TODO: is fixed 
                int current_type_length;
                // create an array loop
                for(int j=0;j<array_length;j++)
                {
                    String definition = getObjectTypeDefinitionInMap(type, message_definition_map);
                    // process type definition to type array.
                    String[] type_definition=process(definition);
                    // process to java source
                    int type_length=0;           
                    for(int i=0;i<type_definition.length;i++)
                    {
                        // process the type definition into source code.
                        if(type_definition[i]!=null)
                        {
                            String[] nested_var_type=getVariableType(type_definition[i]);
                            current_type_length=getTypeLength(nested_var_type[0]);
                            if(getIsTypeArray(type_definition[i]))
                            {
                                // Update current_type_length to current_type_length of array
//a
        // TODO: Still need to fix the fixed array followed by primitive index being wrong.
                                current_type_length=current_type_length*getFixedTypeArrayLength(type_definition[i]);
                            }
                            if(current_type_length<0)
                            {
                                // if type length is less then zero process the value.
                                if(current_type_length==-2)
                                {
                                    // is a fixed size ROS Custom Object
                                    Object[] nested_source;
                                    if(var_type[0].contains("/"))
                                    {
                                        // set object_package_name
                                        nested_source=getObjectSource(java_variable_string.concat(".").concat(nested_var_type[1]), "ros/"+var_type[0].substring(0,var_type[0].lastIndexOf("/")+1), type_definition[i], message_definition_map, current_byte_array_index, true, schema_lookup_map,globals, controller);

                                    }
                                    else
                                    {
                                        // set object_package_name to null
                                        nested_source=getObjectSource(java_variable_string.concat(".").concat(nested_var_type[1]), object_package_name, type_definition[i], message_definition_map, current_byte_array_index, true, schema_lookup_map,globals, controller);                                    
                                    }                                      
                                    
//                                    System.out.println("ARRAY ALERT2: "+ java_variable_string.concat("[").concat(String.valueOf(j)).concat("]").concat(".").concat(nested_var_type[1]));
                                    globals=(Map<String,String>)nested_source[2];                                                          if(nested_source!=null&&nested_source[0]!=null)
                                    {
                                        // Updated the source code...
                                        sb.append((String)nested_source[0]);
                                        sb.append("\n");
                                        // Update current_byte_array_index
                                        current_byte_array_index=((Integer)nested_source[1]).intValue();
                                    }
                                }
                                else if(current_type_length==-3)
                                {
                                    // Stop and return Source String
                                    return_object_array[0]=sb.toString();
                                    return_object_array[1]=current_byte_array_index;
                                    return_object_array[2]=globals;
                                    return return_object_array;
                                }                        
                                else if(current_type_length==-1)
                                {
                                    // is a variable length string

                                }
                            }
                            else
                            {
                                // Is a primitive length variable... append source code to sb, and array index to byte[] current position
                               // sb.append(java_variable_string.concat("[").concat(String.valueOf(j)).concat("]").concat(".").concat(nested_var_type[1]));
                                //sb.append("=");
                                sb.append(nullStringToEmptyString(getFixedROSTypeJavaSource(java_variable_string.concat("[").concat(String.valueOf(j)).concat("]").concat(".").concat(nested_var_type[1]), nested_var_type[0],current_byte_array_index)));
//                                sb.append("\n");
                                // update the current index position
                                current_byte_array_index=current_type_length+current_byte_array_index;
                            }
                        }
                    }
                }                
            }
 //       }
 //       else
 //       {
            // TODO: Variable length array.
            // is variable length array needs to be counted by bytes...
            
  //      }
        
        // Return the Object[].
        return_object_array[0]=sb.toString();
        return_object_array[1]=current_byte_array_index;
        return_object_array[2]=globals;
        return return_object_array;
    }
    
    /** If String is null return empty String. Used by StringBuilder to avoid entering null into StringBuilder source code. */ 
    private static String nullStringToEmptyString(String src) 
    {
        return src == null ? "" : src;
    }    
    
    /** Return a ROS Message Definition Fixed Array Size Length. */
    private static int getArrayLength(String ROS_Type)
    {
        int open;
        int close=-1;
        if((open=ROS_Type.indexOf("["))!=-1)
        {
            close=ROS_Type.indexOf("]");
        }        
        if(close==-1)
        {
            return -1;
        }
        else
        {
            return Integer.parseInt(ROS_Type.substring(open+1,close));
        }
    }
    
    // getVariableType method variables
    private final static String[] zero_length_array=new String[0];
    /** Return a String array of length 2 where 0=type, and 1=variable name. Returns 0 length array on null. */
    private static String[] getVariableType(String variable_definition)
    {
        if(variable_definition!=null)
        {
            // Remove trailing whitespace, and split String int array by space (removing multiple spaces if they exist.
            return variable_definition.trim().split("\\s+");
        }
        else
        {
            return zero_length_array;
        }
    }    
    
    /** Return a ROS .msg, .srv, or.action message based on Object type. Return null, if not found. */
    private static String getSchemaFile(String ROS_Type, Controller controller)
    {
        if (ROS_Type != null && !ROS_Type.startsWith("="))
        {
            Map<String, String> schemaLookupMap;
            boolean is_array;
            String array_text;
            String value;
            if (ROS_Type.indexOf("[") != -1)
            {
                is_array = true;
                array_text = ROS_Type.substring(ROS_Type.indexOf("["));
            }
            else
            {
                is_array = false;
            }

// If class does not exist check if schema exists
            // reuse existing classMap for schemaMap
            if (controller != null)
            {
                // Check if Class exists, return class name
                schemaLookupMap = controller.getInstructionManager().getSchemaLookupMap();
            }
            else
            {
                System.out.println("Unit Testing the DefinitionToMessageGenerator...");
                schemaLookupMap=new HashMap();
            }                
            value = schemaLookupMap.get("ros/".concat(ROS_Type.toLowerCase()));
            if (value == null && ROS_Type.indexOf("/") != -1)
            {
                value = schemaLookupMap.get("ros/".concat(ROS_Type.substring(ROS_Type.lastIndexOf("/") + 1).toLowerCase()));
            }
            if (value != null)
            {
//                System.out.println("SCHEMA MAP key/value found: " + value);

// TODO: Replace the SchemaImporter line with a line that applies to this InputHandler class.                   // TODO: process schema into 
                return getReadSchemaFile(value, controller, "ros");    
            }
        }
        return null;
    }
    
    /** Return a list of any Objects that are defined in a definition in the Map, but a message_definition in the Map does not exist for that Object. */
    private static List getUndefinedObjectTypes(Map<String,String> message_definition_map)
    {
        List<String> list=new ArrayList();
        // Process List
        String message_definition=message_definition_map.get("message_definition");
        String[] schema_defines;
        if(message_definition!=null&&message_definition.isEmpty()==false)
        {
            schema_defines=process(message_definition);
//            System.out.println("schema_defines:" + Arrays.toString(schema_defines));
            for(int i=0;i<schema_defines.length;i++)
            {
                String[] variable = schema_defines[i].split(" ");
                if(variable.length>1)
                {
                    if(getTypeLength(variable[0])==-2)
                    {
                        // Check the map for variable...
                        if(isObjectTypeInMap(variable[0], message_definition_map)==false)
                        {
                            //TODO: need to strip any array symbols.
                            
                            list.add(variable[0]);
                        }    
                    }
                }
            }
        }
  //      else
  //      {
            // Get Schema Definition from File...
            
  //      }
        
        return list;
    }
    
    /** Return boolean is Object Type in Map. */
    private static boolean isObjectTypeInMap(String type_name, Map<String,String> map)
    {
        String key_value;
        String value;
        // Check for match
        if((value=map.get(type_name))!=null)
        {
            return true;
        }
        // If match not found check the keys for /, strip the prepended data and slash,then check for match, (try on both type_name, and key
        Object[] keys=map.keySet().toArray();
        if(type_name.indexOf("/")!=-1)
        {
            type_name=type_name.substring(type_name.indexOf("/")+1);
            for(int i=0;i<keys.length;i++)
            {
                if((key_value=((String)keys[i])).indexOf("/")!=-1)
                {
                    key_value=key_value.substring(key_value.indexOf("/")+1);
                    if(key_value.equals(type_name))
                    {
                        return true;
                    }
                }
                else if(key_value.equals(type_name))
                {
                    return true;
                }
            }
        }
        // If match still not found return false.
        return false;        
    }
    
    /** Return String message_definition from Object Type in Map. Returns null if
     * Object type not found.     
     */
    private static String getObjectTypeDefinitionInMap(String type_name, Map<String,String> map)
    {
        String key_value;
        String value;
        // Check for match
        if((value=map.get(type_name))!=null)
        {
            return value;
        }
        // If match not found check the keys for /, strip the prepended data and slash,then check for match, (try on both type_name, and key
        Object[] keys=map.keySet().toArray();
        if(type_name.indexOf("/")!=-1)
        {
            type_name=type_name.substring(type_name.indexOf("/")+1);
            for(int i=0;i<keys.length;i++)
            {
                if((key_value=((String)keys[i])).indexOf("/")!=-1)
                {
                    key_value=key_value.substring(key_value.indexOf("/")+1);
                    if(key_value.equals(type_name))
                    {
                        return map.get(type_name);
                    }
                }
                else if(key_value.equals(type_name))
                {
                    return map.get(type_name);
                }
            }
        }
        // If match still not found return false.
        return null;        
    }
    

    /**
     * Returns the Java Class variables source String.
     */
    private static String getClassVariablesSource(Map<String, String> map)
    {
        StringBuilder source = new StringBuilder();
        Object[] keys;
        // Loop through keys
        keys = map.keySet().toArray();
        for (int i = 0; i < keys.length; i++)
        {
            source.append(map.get((String) keys[i]));
        }
        return source.toString();
    }    
    
    /**
     * Returns the Java header source (package, and imports) String.
     */
    private static String getHeaderSource(HashMap<String, String> map)
    {
        StringBuilder source = new StringBuilder();
        Object[] keys;
        if (map.get("package") != null)
        {
            source.append(map.get("package").concat("\n\n"));
        }
        // Loop through other keys
        keys = map.keySet().toArray();
        for (int i = 0; i < keys.length; i++)
        {
            if (((String) keys[i]).equals("package") == false)
            {
                source.append(map.get((String) keys[i]));
                source.append("\n");
            }
        }
        source.append("\n");

        source.append(" /**"
                + " * @author Happy Artist\n"
                + " * \n"
                + " * @copyright Copyright © 2015 Happy Artist. All rights reserved.\n"
                + " */\n");
        return source.toString();
    }

    /**
     * Return the ROS .msg definition for the InputHandler.
     */
    private static
            String processMSGDefinition(String message_definition)
    {
        // This method does nothing, except return the input variable, but it does simplify the processing code for implementation. It would be good idea to remove this in processing if everything works...
        return message_definition;
    }

    /**
     * Return the ROS .srv definition for the InputHandler.
     */
    private static
            String processSRVDefinition(String message_definition)
    {
        int request_index;
        //int eol;
        // Cut the request portion out of the message definition, and return that.
        if((request_index=message_definition.indexOf("---"))!=-1)
        {
            message_definition=message_definition.substring(0,request_index);
        }
  //      else if(response_index!=-1)
  //      {
  //          message_definition=message_definition.substring(response_index+3);
  //      }
//        System.out.println("Updated SERVICE MESSAGE DEFINITION: " + message_definition);
        return message_definition;
    }

    /**
     * Return the ROS .action definition for the InputHandler.
     */
    private static
            String processACTIONDefinition(String message_definition)
    {
        System.out.println("ROS Action Definition Self Code Generation of Input/Output Handlers not supported. Please contact Happy Artist if you desire this functionality.");
        return message_definition;
    }

    /**
     * Return boolean is ROS Message array variable type.
     */    
    public static boolean getIsTypeArray(String ROS_Type)
    {
        if(ROS_Type.indexOf("[") != -1)
        {
            // bool - unsigned 8-bit int
            return true;
        }        
        return false;
    }
    
    /**
     * Return int the fixed ROS Message array length.
     */    
    public static int getFixedTypeArrayLength(String ROS_Type)
    {
        if(ROS_Type.indexOf("]")!=-1)
        {
            int open;
            int close=-1;
            if((open=ROS_Type.indexOf("["))!=-1)
            {
                close=ROS_Type.indexOf("]");
                if(open+2<=close)
                {
                    return Integer.parseInt(ROS_Type.substring(open+1,close));
                }
                else
                {
                    return -1;
                }
            }
            return -1;
        }
        else
        {
            return -1;
        }
    }    
    
    /**
     * Return ROS Message variable type length in bytes. -1 is variable length,
     * -2 means it is an Object, so dig deeper..., -3 means it should be ignored.
     */
    public static int getTypeLength(String ROS_Type)
    {
        if (ROS_Type.equals("bool"))
        {
            // bool - unsigned 8-bit int
            return 1;
        }
        else if (ROS_Type.indexOf("bool[") != -1)
        {
            // bool - unsigned 8-bit int
            return 1;
        }
        else if (ROS_Type.equals("int8"))
        {
            return 1;
        }
        else if (ROS_Type.indexOf("int8[") != -1)
        {
            return 1;
        }
        else if (ROS_Type.equals("byte"))
        {
            return 1;
        }
        else if (ROS_Type.indexOf("byte[") != -1)
        {
            return 1;
        }
        else if (ROS_Type.equals("char"))
        {
            return 1;
        }
        else if (ROS_Type.indexOf("char[") != -1)
        {
            return 1;
        }
        else if (ROS_Type.equals("uint8"))
        {
            return 1;
        }
        else if (ROS_Type.indexOf("uint8[") != -1)
        {
            return 1;
        }
        else if (ROS_Type.equals("int16"))
        {
            return 2;
        }
        else if (ROS_Type.indexOf("int16[") != -1)
        {
            return 2;
        }
        else if (ROS_Type.equals("uint16"))
        {
            return 2;
        }
        else if (ROS_Type.indexOf("uint16[") != -1)
        {
            return 2;
        }
        else if (ROS_Type.equals("int32"))
        {
            return 4;
        }
        else if (ROS_Type.indexOf("int32[") != -1)
        {
            return 4;
        }
        else if (ROS_Type.equals("uint32"))
        {
            return 4;
        }
        else if (ROS_Type.indexOf("uint32[") != -1)
        {
            return 4;
        }
        else if (ROS_Type.equals("int64"))
        {
            return 8;
        }
        else if (ROS_Type.indexOf("int64[") != -1)
        {
            return 8;
        }
        else if (ROS_Type.equals("uint64"))
        {
            return 8;
        }
        else if (ROS_Type.indexOf("uint64[") != -1)
        {
            return 8;
        }
        else if (ROS_Type.equals("float32"))
        {
            return 4;
        }
        else if (ROS_Type.indexOf("float32[") != -1)
        {
            return 4;
        }
        else if (ROS_Type.equals("float64"))
        {
            return 8;
        }
        else if (ROS_Type.indexOf("float64[") != -1)
        {
            return 8;
        }
        else if (ROS_Type.equals("time"))
        {
            //int32/int32
            return 8;
        }
        else if (ROS_Type.indexOf("time[") != -1)
        {
            //int32/int32
            return 8;
        }        
        else if (ROS_Type.equals("duration"))
        {
            //int32/int32
            return 8;
        }
        else if (ROS_Type.indexOf("duration[") != -1)
        {
            //int32/int32
            return 8;
        }        

        else if (ROS_Type.equals("string"))
        {
            return -1;
        }
        else if (ROS_Type.indexOf("string[") != -1)
        {
            return -1;
        }
        else if (ROS_Type.equals("Empty"))
        {
            // return Empty
            return 0;
        }
        else if (ROS_Type.equals("Header"))
        {
            // return is variable length
            return -1;
        }        
        else if (ROS_Type.equals("std_msgs/Empty"))
        {
            // return Empty
            return 0;
        }
        else if (ROS_Type.equals("std_srvs/Empty"))
        {
            // return Empty
            return 0;
        }
        else if(ROS_Type.equals("MSG:")||ROS_Type.equals("SRV:")||ROS_Type.equals("ACTION:"))
        {
            return -3;
        }
        else
        {
            // -2 is an Object, because a primitive value does not exist.
            return -2;
        }
    }
    
    /** Check if Definition is fixed length by definition_map. Returns false if variable length. */
    private static boolean getIsDefinitionFixedLength(Map<String,String> definition_map)
    {
        if(definition_map==null)
        {
            return true;
        }
        String definition;
        Object[] keys=definition_map.keySet().toArray();
//        System.out.println("getIsDefinitionFixedLength: " + definition_map.get("message_definition"));
        for(int i=0;i<keys.length;i++)
        {
            if(keys[i]!=null&&(definition=definition_map.get((String)keys[i]))!=null)
            {
                String[]types=process(definition);
                for(int j=0;j<types.length;j++)
                {
                    if(!getIsFixedLength(types[j]))
                    {
//                        System.out.println("###############NOT FIXED LENGTH: " + types[j]);
                        return false;
                    }
                }
            }
        }
        return true;    
    }

    /** Return Definition fixed length by definition_map. Will not return valid length if definition is not fixed. */private static int getDefinitionFixedLength(Map<String,String> definition_map)
    {
        if(definition_map==null)
        {
            return -1;
        }
        String definition;
        //Object[] keys=definition_map.keySet().toArray();
//        System.out.println("getIsDefinitionFixedLength: " + definition_map.get("message_definition"));
        int definition_length=0;
//        for(int i=0;i<keys.length;i++)
//        {
            if((definition=definition_map.get("message_definition"))!=null)
            {
                String[]types=process(definition);
         //       System.out.println("TYPES: "+Arrays.toString(types));
                for(int j=0;j<types.length;j++)
                {
                    String[] variable = types[j].split(" ");
                    if(getTypeLength(variable[0])<0)
                    {
                        // Is a negative variable need to handle here
                        if(getTypeLength(variable[0])==-2)
                        {       
                            // Is an Object. Need to process Object Primitives for sizes...
                            HashMap<String,String> nestedMap=(HashMap<String,String>) ((HashMap)definition_map).clone(); 
                            nestedMap.put("message_definition", nestedMap.get(variable[0]));
                            int sub_length=getDefinitionFixedLength(nestedMap);
                            definition_length=definition_length + sub_length;
           //                 System.out.println("OBJECT TYPE " + types[j] +" TODO: process for primitive lengths... LENGTH:"+sub_length);

                        }
                    }
                    else if(variable[0].indexOf("[")!=-1)
                    {
             //           System.out.println("ARRAY TYPE LENGTH:"+(getTypeLength(variable[0]) * getFixedTypeArrayLength(variable[0]) + 4));
                        //is array add 4 additional bytes to length
                        definition_length=definition_length + (getTypeLength(variable[0]) * getFixedTypeArrayLength(variable[0]) + 4);
                    }
                    else
                    {
               //         System.out.println("TYPE " + variable[0] +" LENGTH:"+getTypeLength(variable[0]));
                        definition_length=definition_length + getTypeLength(variable[0]);
                    }
                }
            }
  //      }
        return definition_length;    
    }   

    /** Return true if type contains a string or Header type.*/
    private static boolean getIsStringOrHeaderType(String type)
    {
        if(type.startsWith("string"))
        {
            return true;
        }
        if(type.contains("Header"))
        {
            return true;
        }        
        else if(type.indexOf("string[")!=-1)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
        
    
    /** Return true if type does not contain a variable length array, or string.*/
    private static boolean getIsFixedLength(String type)
    {
        if(type.equals("string"))
        {
            return false;
        }
        if(type.equals("Header"))
        {
            return false;
        }        
        else if(type.indexOf("]")!=-1)
        {
            int open;
            int close=-1;
            if((open=type.indexOf("["))!=-1)
            {
                close=type.indexOf("]");
                if(open+2<=close)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            return true;
        }
        else
        {
            return true;
        }
    }
    

    
    /**
     * Return the Map<String,String> of global variables (Java source code that will 
     * be added at the class variable scope). 
     * 
     * @param ROS_Type String of ROS message definition type
     * @param globals Map<String,String> of variable define to disallow duplicates, 
     * and the actual code to append to the Java class variable scope. 
     */
    private static Map<String,String> updateGlobalMap(String ROS_Type, Map<String,String> globals)
    {
//        System.out.println("Update Global Map with type: ".concat(ROS_Type));
        if (ROS_Type.equals("bool"))
        {
            // bool - unsigned 8-bit int
            return globals;
        }
        else if (ROS_Type.indexOf("bool[") != -1)
        {
            if(getIsFixedLength(ROS_Type))
            {
                // fixed array
                globals.put("booleanByte","private boolean booleanByte;\n");
            }
            else
            {
                //variable array
                globals.put("jInt","private int j;\n");                   
                globals.put("booleanByte","private boolean booleanByte;\n");             
                globals.put("array_length","private int array_length;\n");
            }
            return globals;
        }
        else if (ROS_Type.equals("int8"))
        {
            return globals;
        }
        else if (ROS_Type.indexOf("int8[") != -1)
        {
            if(getIsFixedLength(ROS_Type))
            {
                // fixed array
                globals.put("int8Byte","private byte int8Byte;\n");
            }
            else
            {
                //variable array
                globals.put("jInt","private int j;\n");   
                globals.put("int8Byte","private byte int8Byte;\n");             
                globals.put("array_length","private int array_length;\n");
            }
            return globals;
        }
        else if (ROS_Type.equals("byte"))
        {
            return globals;
        }
        else if (ROS_Type.indexOf("byte[") != -1)
        {
            if(getIsFixedLength(ROS_Type))
            {
                // fixed array
                globals.put("int8Byte","private byte int8Byte;\n");
            }
            else
            {
                //variable array
                globals.put("jInt","private int j;\n");   
                globals.put("int8Byte","private byte int8Byte;\n");             
                globals.put("array_length","private int array_length;\n");
            }
            return globals;
        }
        else if (ROS_Type.equals("char"))
        {
            return globals;
        }
        else if (ROS_Type.indexOf("char[") != -1)
        {
            if(getIsFixedLength(ROS_Type))
            {
                // fixed array
                globals.put("uint8Short","private short uint8Short;\n");
            }
            else
            {
                //variable array
                globals.put("jInt","private int j;\n");                   
                globals.put("uint8Short","private short uint8Short;\n");             
                globals.put("array_length","private int array_length;\n");
            }
            return globals;
        }
        else if (ROS_Type.equals("uint8"))
        {
            return globals;
        }
        else if (ROS_Type.indexOf("uint8[") != -1)
        {
            if(getIsFixedLength(ROS_Type))
            {
                // fixed array
                globals.put("uint8Short","private short uint8Short;\n");
            }
            else
            {
                //variable array
                globals.put("jInt","private int j;\n");                   
                globals.put("uint8Short","private short uint8Short;\n");             
                globals.put("array_length","private int array_length;\n");
            }
            return globals;
        }
        else if (ROS_Type.equals("int16"))
        {
            return globals;
        }
        else if (ROS_Type.indexOf("int16[") != -1)
        {
            if(getIsFixedLength(ROS_Type))
            {
                // fixed array
                globals.put("int16Short","private short int16Short;\n");
            }
            else
            {
                //variable array
                globals.put("jInt","private int j;\n");                   
                globals.put("int16Short","private short int16Short;\n");             
                globals.put("array_length","private int array_length;\n");
            }
            return globals;
        }
        else if (ROS_Type.equals("uint16"))
        {
            return globals;
        }
        else if (ROS_Type.indexOf("uint16[") != -1)
        {
            if(getIsFixedLength(ROS_Type))
            {
                // fixed array
                globals.put("uint16Int","private int uint16Int;\n");
            }
            else
            {
                //variable array
                globals.put("jInt","private int j;\n");                   
                globals.put("uint16Int","private int uint16Int;\n");             
                globals.put("array_length","private int array_length;\n");
            }
            return globals;
        }
        else if (ROS_Type.equals("int32"))
        {
            return globals;
        }
        else if (ROS_Type.indexOf("int32[") != -1)
        {
            if(getIsFixedLength(ROS_Type))
            {
                // fixed array
                globals.put("int32Int","private int int32Int;\n");
            }
            else
            {
                //variable array
                globals.put("jInt","private int j;\n");                   
                globals.put("int32Int","private int int32Int;\n");             
                globals.put("array_length","private int array_length;\n");
            }
            return globals;
        }
        else if (ROS_Type.equals("uint32"))
        {
            return globals;
        }
        else if (ROS_Type.indexOf("uint32[") != -1)
        {
            if(getIsFixedLength(ROS_Type))
            {
                // fixed array
                globals.put("uint32Long","private long uint32Long;\n");
            }
            else
            {
                //variable array
                globals.put("jInt","private int j;\n");                   
                globals.put("uint32Long","private long uint32Long;\n");             
                globals.put("array_length","private int array_length;\n");
            }
            return globals;
        }
        else if (ROS_Type.equals("int64"))
        {
            return globals;
        }
        else if (ROS_Type.indexOf("int64[") != -1)
        {
            if(getIsFixedLength(ROS_Type))
            {
                // fixed array
                globals.put("int64Long","private long int64Long;\n");
            }
            else
            {
                //variable array
                globals.put("jInt","private int j;\n");                   
                globals.put("int64Long","private long int64Long;\n");             
                globals.put("array_length","private int array_length;\n");
            }
            return globals;
        }
        else if (ROS_Type.equals("uint64"))
        {
            return globals;
        }
        else if (ROS_Type.indexOf("uint64[") != -1)
        {
            if(getIsFixedLength(ROS_Type))
            {
                // fixed array
                globals.put("int64Long","private long int64Long;\n");
            }
            else
            {
                // variable array (int64Long is used by both uint64, and int64.)
                globals.put("jInt","private int j;\n");                   
                globals.put("int64Long","private long int64Long;\n");             
                globals.put("array_length","private int array_length;\n");
            }
            return globals;
        }
        else if (ROS_Type.equals("float32"))
        {
            globals.put("float32Int","private int float32Int;\n");      
            return globals;
        }
        else if (ROS_Type.indexOf("float32[") != -1)
        {
            // variable array (int64Long is used by both uint64, and int64.)
            globals.put("float32Int","private int float32Int;\n");       
            globals.put("jInt","private int j;\n");                     
            globals.put("array_length","private int array_length;\n");
            return globals;
        }
        else if (ROS_Type.equals("float64"))
        {
            globals.put("float64Long","private long float64Long;\n");      
            return globals;
        }
        else if (ROS_Type.indexOf("float64[") != -1)
        {
            // variable array (int64Long is used by both uint64, and int64.)
            globals.put("float64Long","private long float64Long;\n");      
            globals.put("jInt","private int j;\n");                     
            globals.put("array_length","private int array_length;\n");
            return globals;
        }
        else if (ROS_Type.equals("string"))
        {
            globals.put("hexStringConverter", "    private org.happy.artist.rmdmia.utilities.HexStringConverter hexStringConverter = org.happy.artist.rmdmia.utilities.HexStringConverter.getHexStringConverterInstance();\n");
            globals.put("soup","private int soup;\n"); 
            globals.put("y","private int y;\n");
            globals.put("stringy","private java.lang.String str;\n");  
            globals.put("string_length","private int string_length;\n");     
            globals.put("string_to_bytes", "    private static boolean isHex(char c) {\n" +
"        return ((c >= '0') && (c <= '9')) ||\n" +
"               ((c >= 'a') && (c <= 'f')) ||\n" +
"               ((c >= 'A') && (c <= 'F'));\n" +
"      }\n" +
"\n" +
"      private static int hexValue(char c) {\n" +
"        if ((c >= '0') && (c <= '9')) {\n" +
"          return (c - '0');\n" +
"        } else if ((c >= 'a') && (c <= 'f')) {\n" +
"          return (c - 'a') + 10;\n" +
"        } else {\n" +
"          return (c - 'A') + 10;\n" +
"        }\n" +
"      }");
            return globals;
        }
        else if (ROS_Type.indexOf("string[") != -1)
        {
            globals.put("hexStringConverter", "    private org.happy.artist.rmdmia.utilities.HexStringConverter hexStringConverter = org.happy.artist.rmdmia.utilities.HexStringConverter.getHexStringConverterInstance();\n");
            globals.put("soup","private int soup;\n"); 
            globals.put("y","private int y;\n");
            globals.put("stringy","private java.lang.String str;\n");              
            globals.put("str_i","private int str_i;\n");
            globals.put("arrays_length","private int arrays_length;\n");
            globals.put("array_length","private int array_length;\n");
            globals.put("string_length","private int string_length;\n");  
            globals.put("string_to_bytes", "    private static boolean isHex(char c) {\n" +
"        return ((c >= '0') && (c <= '9')) ||\n" +
"               ((c >= 'a') && (c <= 'f')) ||\n" +
"               ((c >= 'A') && (c <= 'F'));\n" +
"      }\n" +
"\n" +
"      private static int hexValue(char c) {\n" +
"        if ((c >= '0') && (c <= '9')) {\n" +
"          return (c - '0');\n" +
"        } else if ((c >= 'a') && (c <= 'f')) {\n" +
"          return (c - 'a') + 10;\n" +
"        } else {\n" +
"          return (c - 'A') + 10;\n" +
"        }\n" +
"      }");            
            return globals;
        }
        else if (ROS_Type.equals("time"))
        {
            // return secs/nsecs int32/int32 =64 long
            return globals;
        }
        else if (ROS_Type.indexOf("time[") != -1)
        {
            // return secs/nsecs int32/int32 =64 long
            return globals;
        }
        else if (ROS_Type.equals("duration"))
        {
            // return secs/nsecs int32/int32 =64 long
            return globals;
        }
        else if (ROS_Type.indexOf("duration[") != -1)
        {
            // return secs/nsecs int32/int32 =64 long
            return globals;
        }
        else if (ROS_Type.endsWith("Header"))
        {
            globals.put("hexStringConverter", "    private org.happy.artist.rmdmia.utilities.HexStringConverter hexStringConverter = org.happy.artist.rmdmia.utilities.HexStringConverter.getHexStringConverterInstance();\n");            
            globals.put("soup","    private int soup;\n"); 
            globals.put("y","    private int y;\n");
            globals.put("stringy","    private java.lang.String str;\n");            
            globals.put("header","    private long seq=0;\n");             
            globals.put("string_length","    private int string_length;\n");  
            globals.put("string_to_bytes", "    private static boolean isHex(char c) {\n" +
"        return ((c >= '0') && (c <= '9')) ||\n" +
"               ((c >= 'a') && (c <= 'f')) ||\n" +
"               ((c >= 'A') && (c <= 'F'));\n" +
"      }\n" +
"\n" +
"      private static int hexValue(char c) {\n" +
"        if ((c >= '0') && (c <= '9')) {\n" +
"          return (c - '0');\n" +
"        } else if ((c >= 'a') && (c <= 'f')) {\n" +
"          return (c - 'a') + 10;\n" +
"        } else {\n" +
"          return (c - 'A') + 10;\n" +
"        }\n" +
"      }");
            return globals;
        }        
        else if (ROS_Type.equals("Empty"))
        {
            // return Empty
            return globals;
        }
        else if (ROS_Type.equals("std_msgs/Empty"))
        {
            // return Empty
            return globals;
        }
        else if (ROS_Type.equals("std_srvs/Empty"))
        {
            // return Empty
            return globals;
        }
        else if (ROS_Type.startsWith("MSG:") || ROS_Type.startsWith("SRV:"))
        {
            // Incoming ROS Connection Headers sometimes include a line starting with MSG: that contains another message definition of the Message Definition of a non-primitive defined Object.            
            return globals;
        }
        else if (ROS_Type.startsWith("---"))
        {
            // return secs/nsecs int32/int32 =64 longc
            return globals;
        }
        else
        {
            return globals;
        }
    }
        
    
    /**
     * Return the Java type for the ROS Message Definition variable. Returns
     * null if field type unrecognized.
     */
    public static String getJavaType(String ROS_Type, Controller controller)
    {
        if (ROS_Type.equals("bool"))
        {
            // bool - unsigned 8-bit int
            return "boolean";
        }
        else if (ROS_Type.indexOf("bool[") != -1)
        {
            // bool - unsigned 8-bit int
            return "boolean[]";
        }
        else if (ROS_Type.equals("int8"))
        {
            return "byte";
        }
        else if (ROS_Type.indexOf("int8[") != -1)
        {
            return "byte[]";
        }
        else if (ROS_Type.equals("byte"))
        {
            return "byte";
        }
        else if (ROS_Type.indexOf("byte[") != -1)
        {
            return "byte[]";
        }
        else if (ROS_Type.equals("char"))
        {
            return "char";
        }
        else if (ROS_Type.indexOf("char[") != -1)
        {
            return "char[]";
        }
        else if (ROS_Type.equals("uint8"))
        {
            return "short";
        }
        else if (ROS_Type.indexOf("uint8[") != -1)
        {
            return "short[]";
        }
        else if (ROS_Type.equals("int16"))
        {
            return "int";
        }
        else if (ROS_Type.indexOf("int16[") != -1)
        {
            return "int[]";
        }
        else if (ROS_Type.equals("uint16"))
        {
            return "int";
        }
        else if (ROS_Type.indexOf("uint16[") != -1)
        {
            return "int[]";
        }
        else if (ROS_Type.equals("int32"))
        {
            return "int";
        }
        else if (ROS_Type.indexOf("int32[") != -1)
        {
            return "int[]";
        }
        else if (ROS_Type.equals("uint32"))
        {
            return "long";
        }
        else if (ROS_Type.indexOf("uint32[") != -1)
        {
            return "long[]";
        }
        else if (ROS_Type.equals("int64"))
        {
            return "long";
        }
        else if (ROS_Type.indexOf("int64[") != -1)
        {
            return "long[]";
        }
        else if (ROS_Type.equals("uint64"))
        {
            return "long";
        }
        else if (ROS_Type.indexOf("uint64[") != -1)
        {
            return "long[]";
        }
        else if (ROS_Type.equals("float32"))
        {
            return "float";
        }
        else if (ROS_Type.indexOf("float32[") != -1)
        {
            return "float[]";
        }
        else if (ROS_Type.equals("float64"))
        {
            return "double";
        }
        else if (ROS_Type.indexOf("float64[") != -1)
        {
            return "double[]";
        }
        else if (ROS_Type.equals("string"))
        {
            return "java.lang.String";
        }
        else if (ROS_Type.indexOf("string[") != -1)
        {
            return "java.lang.String[]";
        }
        else if (ROS_Type.equals("time"))
        {
            // return secs/nsecs int32/int32 =64 long
            return "Time";
        }
        else if (ROS_Type.indexOf("time[") != -1)
        {
            // return secs/nsecs int32/int32 =64 long
            return "Time[]";
        }
        else if (ROS_Type.equals("duration"))
        {
            // return secs/nsecs int32/int32 =64 long
            return "Duration";
        }
        else if (ROS_Type.indexOf("duration[") != -1)
        {
            // return secs/nsecs int32/int32 =64 long
            return "Duration[]";
        }
        else if (ROS_Type.endsWith("Header"))
        {
            // return secs/nsecs int32/int32 =64 long
            return "Header";
        }        
        else if (ROS_Type.equals("Empty"))
        {
            // return Empty
            return "Empty";
        }
        else if (ROS_Type.equals("std_msgs/Empty"))
        {
            // return Empty
            return "Empty";
        }
        else if (ROS_Type.equals("std_srvs/Empty"))
        {
            // return Empty
            return "Empty";
        }
        else if (ROS_Type.startsWith("MSG:") || ROS_Type.startsWith("SRV:"))
        {
            // Incoming ROS Connection Headers sometimes include a line starting with MSG: that contains another message definition of the Message Definition of a non-primitive defined Object.            
            return "MSG:";
        }
        else if (ROS_Type.startsWith("---"))
        {
            // return secs/nsecs int32/int32 =64 longc
            return "---";
        }
        else
        {
//            System.out.println("Attempted to process unsupported type: ".concat(ROS_Type).concat(". Searching schema folder for the message definition..."));
            // Attempt to lookup in Schema Map.
            if (ROS_Type != null && !ROS_Type.startsWith("="))
            {
                Map<String, String> schemaLookupMap;
                boolean is_array;
                String array_text;
                String value;
                if (ROS_Type.indexOf("[") != -1)
                {
                    is_array = true;
                    array_text = ROS_Type.substring(ROS_Type.indexOf("["));
                }
                else
                {
                    is_array = false;
                }

// If class does not exist check if schema exists
                // reuse existing classMap for schemaMap
                if (controller != null)
                {
                    // Check if Class exists, return class name
                    schemaLookupMap = controller.getInstructionManager().getSchemaLookupMap();
                }
                else
                {
                    System.out.println("Unit Testing the DefinitionToMessageGenerator...");
                    schemaLookupMap=new HashMap();
                }                
                value = schemaLookupMap.get("ros/".concat(ROS_Type.toLowerCase()));
                if (value == null && ROS_Type.indexOf("/") != -1)
                {
                    value = schemaLookupMap.get("ros/".concat(ROS_Type.substring(ROS_Type.lastIndexOf("/") + 1).toLowerCase()));
                }
                if (value != null)
                {
//                    System.out.println("SCHEMA MAP key/value found: " + value);

// TODO: Replace the SchemaImporter line with a line that applies to this InputHandler class.                   // TODO: process schema into 
 //                   String schema=getSchemaFile(value, controller, "ros");     
//                    SchemaImporter.generateSchemaInstructionsSynchronously(list, controller, "ros");
//                    System.out.println("Generated Class File Code for:".concat(value));
                    // Update classmap and return the new type...                    controller.getInstructionManager().updateClassLookupMap("ros");
                    // Get the class lookup map
                    schemaLookupMap = controller.getInstructionManager().getClassLookupMap();
                    value = schemaLookupMap.get("ros/".concat(ROS_Type.toLowerCase()));
                    if (value == null && ROS_Type.indexOf("/") != -1)
                    {
                        value = schemaLookupMap.get("ros/".concat(ROS_Type.substring(ROS_Type.lastIndexOf("/") + 1).toLowerCase()));
                    }
                    if (value != null)
                    {
                        String path = controller.getInstructionManager().getClassFolderPath().concat("/ros/");
                        if (value.indexOf(path) != -1)
                        {
                            value = value.replaceAll("\\.class", "");
                            value = value.substring(value.indexOf(path) + path.length()).replaceAll("/", "\\.");
                            if (is_array)
                            {
                                value = value.concat("[]");
                            }
//                            System.out.println("CLASS EXISTS 2: ".concat(value));
                            return value;
                        }
                    }
                }
            }
            return ROS_Type;
        }
    }
    
     /**
     * Return the Java source code for a ROS Type fixed length ROS Message 
     * Definition variable. Returns null if field type unrecognized.
     * 
     * @param String instruction_variable
     * @param String ROS_Type the ROS Message Definition variable type.
     * @param int current_position in the ROS fixed length byte[].
     */
    private static
            String getFixedROSTypeJavaSource(String instruction_variable, String ROS_Type, int current_position)
    {
        if (ROS_Type.equals("bool"))
        {
            // bool - unsigned 8-bit int
            return JavaToROSTypesSource.getFixedBool(current_position, instruction_variable);
        }
        else if (ROS_Type.indexOf("bool[") != -1)
        {
            int length = getROSTypeFixedArrayLength(ROS_Type);
            if(length!=-1)
            {
                // Multiply the array length * the primitive size.
                length=getTypeLength(ROS_Type)*length;
                return JavaToROSTypesSource.getFixedBoolArray(current_position, current_position+length-1, instruction_variable);
            }
            else 
            {
                return null;
            }
        }
        else if (ROS_Type.equals("int8"))
        {
            // is a 8 bit byte
            return JavaToROSTypesSource.getFixedInt8(current_position, instruction_variable);
        }
        else if (ROS_Type.indexOf("int8[") != -1)
        {
            int length = getROSTypeFixedArrayLength(ROS_Type);
            if(length!=-1)
            {
                // Multiply the array length * the primitive size.
                length=getTypeLength(ROS_Type)*length;
                return JavaToROSTypesSource.getFixedInt8Array(current_position, current_position+length-1, instruction_variable);
            }
            else 
            {
                return null;
            }
        }
        else if (ROS_Type.equals("byte"))
        {
             return JavaToROSTypesSource.getFixedInt8(current_position, instruction_variable);
        }
        else if (ROS_Type.indexOf("byte[") != -1)
        {
            int length = getROSTypeFixedArrayLength(ROS_Type);
            if(length!=-1)
            {
                // Multiply the array length * the primitive size.
                length=getTypeLength(ROS_Type)*length;
                return JavaToROSTypesSource.getFixedInt8Array(current_position, current_position+length-1, instruction_variable);
            }
            else 
            {
                return null;
            }
        }
        else if (ROS_Type.equals("char"))
        {
            return JavaToROSTypesSource.getFixedUInt8(current_position, instruction_variable);
        }
        else if (ROS_Type.indexOf("char[") != -1)
        {
            int length = getROSTypeFixedArrayLength(ROS_Type);
            if(length!=-1)
            {
                // Multiply the array length * the primitive size.
                length=getTypeLength(ROS_Type)*length;
                return JavaToROSTypesSource.getFixedUInt8Array(current_position, current_position+length-1, instruction_variable);
            }
            else 
            {
                return null;
            }        
        }
        else if (ROS_Type.equals("uint8"))
        {
            return JavaToROSTypesSource.getFixedUInt8(current_position, instruction_variable);
        }
        else if (ROS_Type.indexOf("uint8[") != -1)
        {
            int length = getROSTypeFixedArrayLength(ROS_Type);
            if(length!=-1)
            {
                // Multiply the array length * the primitive size.
                length=getTypeLength(ROS_Type)*length;
                return JavaToROSTypesSource.getFixedUInt8Array(current_position, current_position+length-1, instruction_variable);
            }
            else 
            {
                return null;
            }    
        }
        else if (ROS_Type.equals("int16"))
        {
                return JavaToROSTypesSource.getFixedInt16(current_position, instruction_variable);
        }
        else if (ROS_Type.indexOf("int16[") != -1)
        {
            int length = getROSTypeFixedArrayLength(ROS_Type);
            if(length!=-1)
            {
                // Multiply the array length * the primitive size.
                length=getTypeLength(ROS_Type)*length;
                return JavaToROSTypesSource.getFixedInt16Array(current_position, current_position+length-1, instruction_variable);
            }
            else 
            {
                return null;
            }  
        }
        else if (ROS_Type.equals("uint16"))
        {
                return JavaToROSTypesSource.getFixedUInt16(current_position, instruction_variable);
        }
        else if (ROS_Type.indexOf("uint16[") != -1)
        {
            int length = getROSTypeFixedArrayLength(ROS_Type);
            if(length!=-1)
            {
                // Multiply the array length * the primitive size.
                length=getTypeLength(ROS_Type)*length;
                return JavaToROSTypesSource.getFixedUInt16Array(current_position, current_position+length-1, instruction_variable);
            }
            else 
            {
                return null;
            }    
        }
        else if (ROS_Type.equals("int32"))
        {
                return JavaToROSTypesSource.getFixedInt32(current_position, instruction_variable);
        }
        else if (ROS_Type.indexOf("int32[") != -1)
        {
            int length = getROSTypeFixedArrayLength(ROS_Type);
            if(length!=-1)
            {
                // Multiply the array length * the primitive size.
                length=getTypeLength(ROS_Type)*length;
                return JavaToROSTypesSource.getFixedInt32Array(current_position, current_position+length-1, instruction_variable);
            }
            else 
            {
                return null;
            }    
        }
        else if (ROS_Type.equals("uint32"))
        {
            return JavaToROSTypesSource.getFixedUInt32(current_position, instruction_variable);
        }
        else if (ROS_Type.indexOf("uint32[") != -1)
        {
            int length = getROSTypeFixedArrayLength(ROS_Type);
            if(length!=-1)
            {
                // Multiply the array length * the primitive size.
                length=getTypeLength(ROS_Type)*length;
                return JavaToROSTypesSource.getFixedUInt32Array(current_position, current_position+length-1, instruction_variable);
            }
            else 
            {
                return null;
            }           
        }
        else if (ROS_Type.equals("int64"))
        {
                return JavaToROSTypesSource.getFixedInt64(current_position, instruction_variable);
        }
        else if (ROS_Type.indexOf("int64[") != -1)
        {
            int length = getROSTypeFixedArrayLength(ROS_Type);
            if(length!=-1)
            {
                // Multiply the array length * the primitive size.
                length=getTypeLength(ROS_Type)*length;
                return JavaToROSTypesSource.getFixedInt64Array(current_position, current_position+length-1, instruction_variable);
            }
            else 
            {
                return null;
            }    
        }
        else if (ROS_Type.equals("uint64"))
        {
                return JavaToROSTypesSource.getFixedUInt64(current_position, instruction_variable);
        }
        else if (ROS_Type.indexOf("uint64[") != -1)
        {
            int length = getROSTypeFixedArrayLength(ROS_Type);
            if(length!=-1)
            {
                // Multiply the array length * the primitive size.
                length=getTypeLength(ROS_Type)*length;
                return JavaToROSTypesSource.getFixedUInt64Array(current_position, current_position+length-1, instruction_variable);
            }
            else 
            {
                return null;
            }    
        }
        else if (ROS_Type.equals("float32"))
        {
                return JavaToROSTypesSource.getFixedFloat32(current_position, instruction_variable);
        }
        else if (ROS_Type.indexOf("float32[") != -1)
        {
            int length = getROSTypeFixedArrayLength(ROS_Type);
            if(length!=-1)
            {
                // Multiply the array length * the primitive size.
                length=getTypeLength(ROS_Type)*length;
                return JavaToROSTypesSource.getFixedFloat32Array(current_position, current_position+length-1, instruction_variable);
            }
            else 
            {
                return null;
            }    
        }
        else if (ROS_Type.equals("float64"))
        {
                return JavaToROSTypesSource.getFixedFloat64(current_position, instruction_variable);
        }
        else if (ROS_Type.indexOf("float64[") != -1)
        {
            int length = getROSTypeFixedArrayLength(ROS_Type);
            if(length!=-1)
            {
                // Multiply the array length * the primitive size.
                length=getTypeLength(ROS_Type)*length;
                return JavaToROSTypesSource.getFixedFloat64Array(current_position, current_position+length-1, instruction_variable);
            }
            else 
            {
                return null;
            }    
        }
        else if (ROS_Type.equals("string"))
        {
            return null;
        }
        else if (ROS_Type.indexOf("string[") != -1)
        {
            // TODO:
            return null;
        }
        else if (ROS_Type.equals("time"))
        {
            return JavaToROSTypesSource.getFixedUInt32(current_position, instruction_variable.concat(".secs")).concat(JavaToROSTypesSource.getFixedUInt32(current_position+4, instruction_variable.concat(".nsecs")));
        }
        else if (ROS_Type.indexOf("time[") != -1)
        {
            // TODO:            
            return null;
        }     
        else if (ROS_Type.equals("duration"))
        {
            return JavaToROSTypesSource.getFixedInt32(current_position, instruction_variable.concat(".secs")).concat(JavaToROSTypesSource.getFixedInt32(current_position+4, instruction_variable.concat(".nsecs")));        
        }
        else if (ROS_Type.indexOf("duration[") != -1)
        {
            // TODO:            
            return null;
        }      
        else if (ROS_Type.endsWith("Header"))
        {
            // Header is not a fixed length therefore it should never go down this path. If it does things will break, which is what we need to find the source of the problem.
            return null;
        }        
        else if (ROS_Type.equals("Empty"))
        {
            // TODO:            
            // return Empty
            return null;
        }
        else if (ROS_Type.equals("std_msgs/Empty"))
        {
            // TODO:            
            // return Empty
            return null;
        }
        else if (ROS_Type.equals("std_srvs/Empty"))
        {
            // TODO:            
            // return Empty
            return null;
        }
        else if (ROS_Type.startsWith("MSG:") || ROS_Type.startsWith("SRV:"))
        {
            // TODO:            
            // Incoming ROS Connection Headers sometimes include a line starting with MSG: that contains another message definition of the Message Definition of a non-primitive defined Object.            
            return null;
        }
        else if (ROS_Type.startsWith("---"))
        {
            // TODO:            
            // return secs/nsecs int32/int32 =64 longc
            return null;
        }
        else
        {        
            return null;
/*            if (ROS_Type.equals("Header"))
            {
                // TODO: Header
                return null;
            }
            else if (ROS_Type.equals("time"))
            {
                // TODO:            
                // return secs/nsecs int32/int32 =64 long
                return "Time";
            }
            else if (ROS_Type.indexOf("time[") != -1)
            {
                // TODO:            
                // return secs/nsecs int32/int32 =64 long
                return "Time[]";
            }
            else if (ROS_Type.equals("duration"))
            {
                // TODO:            
                // return secs/nsecs int32/int32 =64 long
                return "Duration";
            }
            else if (ROS_Type.indexOf("duration[") != -1)
            {
                // TODO:            
                // return secs/nsecs int32/int32 =64 long
                return "Duration[]";
            }            
            else if (ROS_Type.indexOf("duration[") != -1)
            {
                // TODO:            
                // return secs/nsecs int32/int32 =64 long
                return "Duration[]";
            }

            
            // TODO:            
//            System.out.println("Attempted to process unsupported type: ".concat(ROS_Type).concat(". Searching schema folder for the message definition..."));
            // Attempt to lookup in Schema Map.
            if (ROS_Type != null && !ROS_Type.startsWith("="))
            {
                Map<String, String> schemaLookupMap;
                boolean is_array;
                String array_text;
                String value;
                if (ROS_Type.indexOf("[") != -1)
                {
                    is_array = true;
                    array_text = ROS_Type.substring(ROS_Type.indexOf("["));
                }
                else
                {
                    is_array = false;
                }

// If class does not exist check if schema exists
                // reuse existing classMap for schemaMap
                if (controller != null)
                {
                    // Check if Class exists, return class name
                    schemaLookupMap = controller.getInstructionManager().getSchemaLookupMap();
                }
                else
                {
                    System.out.println("Unit Testing the DefinitionToMessageGenerator...");
                    schemaLookupMap=new HashMap();
                }                
                value = schemaLookupMap.get("ros/".concat(ROS_Type.toLowerCase()));
                if (value == null && ROS_Type.indexOf("/") != -1)
                {
                    value = schemaLookupMap.get("ros/".concat(ROS_Type.substring(ROS_Type.lastIndexOf("/") + 1).toLowerCase()));
                }
                if (value != null)
                {
                    System.out.println("SCHEMA MAP key/value found: " + value);

// TODO: Replace the SchemaImporter line with a line that applies to this InputHandler class.                   // TODO: process schema into 
 //                   String schema=getSchemaFile(value, controller, "ros");     
//                    SchemaImporter.generateSchemaInstructionsSynchronously(list, controller, "ros");
                    System.out.println("Generated Class File Code for:".concat(value));
                    // Update classmap and return the new type...                    controller.getInstructionManager().updateClassLookupMap("ros");
                    // Get the class lookup map
                    schemaLookupMap = controller.getInstructionManager().getClassLookupMap();
                    value = schemaLookupMap.get("ros/".concat(ROS_Type.toLowerCase()));
                    if (value == null && ROS_Type.indexOf("/") != -1)
                    {
                        value = schemaLookupMap.get("ros/".concat(ROS_Type.substring(ROS_Type.lastIndexOf("/") + 1).toLowerCase()));
                    }
                    if (value != null)
                    {
                        String path = controller.getInstructionManager().getClassFolderPath().concat("/ros/");
                        if (value.indexOf(path) != -1)
                        {
                            value = value.replaceAll("\\.class", "");
                            value = value.substring(value.indexOf(path) + path.length()).replaceAll("/", "\\.");
                            if (is_array)
                            {
                                value = value.concat("[]");
                            }
                            System.out.println("CLASS EXISTS 2: ".concat(value));
                            return value;
                        }
                    }
                }
            }
            return ROS_Type;
        */}
    }

     /**
     * Return the Java source code for converting Java Type to a variable 
     * length ROS Type in a ROS message. Returns null if field type unrecognized.
     * 
     * @param String instruction_variable
     * @param String ROS_Type the ROS Message Definition variable type.
     * @param int current_position in the ROS fixed length byte[].
     */
    private static
            String getVariableROSTypeJavaSource(String instruction_variable, String ROS_Type, String current_position_variable)
    {
        if (ROS_Type.equals("bool"))
        {
            // bool - unsigned 8-bit int
            return JavaToROSTypesSource.getVariableBool(instruction_variable, current_position_variable);
        }
        else if (ROS_Type.indexOf("bool[") != -1)
        {
            return JavaToROSTypesSource.getVariableBoolArray(instruction_variable, current_position_variable);
        }
        else if (ROS_Type.equals("int8"))
        {
            // is a 8 bit byte
            return JavaToROSTypesSource.getVariableInt8(current_position_variable, instruction_variable);
        }
        else if (ROS_Type.indexOf("int8[") != -1)
        {
            return JavaToROSTypesSource.getVariableInt8Array(instruction_variable, current_position_variable);
        }
        else if (ROS_Type.equals("byte"))
        {
            return JavaToROSTypesSource.getVariableInt8(current_position_variable, instruction_variable);
        }
        else if (ROS_Type.indexOf("byte[") != -1)
        {
            return JavaToROSTypesSource.getVariableInt8Array(instruction_variable, current_position_variable);
        }
        else if (ROS_Type.equals("char"))
        {
            return JavaToROSTypesSource.getVariableUInt8(current_position_variable, instruction_variable);
        }
        else if (ROS_Type.indexOf("char[") != -1)
        {
            return JavaToROSTypesSource.getVariableUInt8Array(instruction_variable, current_position_variable);
        }
        else if (ROS_Type.equals("uint8"))
        {
            return JavaToROSTypesSource.getVariableUInt8(current_position_variable, instruction_variable);
        }
        else if (ROS_Type.indexOf("uint8[") != -1)
        {
            return JavaToROSTypesSource.getVariableUInt8Array(instruction_variable, current_position_variable);
        }
        else if (ROS_Type.equals("int16"))
        {
            return JavaToROSTypesSource.getVariableInt16(current_position_variable, instruction_variable);
        }
        else if (ROS_Type.indexOf("int16[") != -1)
        {
            return JavaToROSTypesSource.getVariableUInt16Array(instruction_variable, current_position_variable);
        }
        else if (ROS_Type.equals("uint16"))
        {
            return JavaToROSTypesSource.getVariableUInt16(current_position_variable, instruction_variable);
        }
        else if (ROS_Type.indexOf("uint16[") != -1)
        {
            return JavaToROSTypesSource.getVariableUInt16Array(instruction_variable, current_position_variable);
        }
        else if (ROS_Type.equals("int32"))
        {
            return JavaToROSTypesSource.getVariableInt32(current_position_variable, instruction_variable);
        }
        else if (ROS_Type.indexOf("int32[") != -1)
        {
            return JavaToROSTypesSource.getVariableInt32Array(instruction_variable, current_position_variable);
        }
        else if (ROS_Type.equals("uint32"))
        {
            return JavaToROSTypesSource.getVariableUInt32(current_position_variable, instruction_variable);
        }
        else if (ROS_Type.indexOf("uint32[") != -1)
        {
            return JavaToROSTypesSource.getVariableUInt32Array(instruction_variable, current_position_variable);
        }
        else if (ROS_Type.equals("int64"))
        {
            return JavaToROSTypesSource.getVariableInt64(current_position_variable, instruction_variable);
        }
        else if (ROS_Type.indexOf("int64[") != -1)
        {
            return JavaToROSTypesSource.getVariableInt64Array(instruction_variable, current_position_variable);
        }
        else if (ROS_Type.equals("uint64"))
        {
            return JavaToROSTypesSource.getVariableUInt64(current_position_variable, instruction_variable);
        }
        else if (ROS_Type.indexOf("uint64[") != -1)
        {
            return JavaToROSTypesSource.getVariableUInt64Array(instruction_variable, current_position_variable);
        }
        else if (ROS_Type.equals("float32"))
        {
            return JavaToROSTypesSource.getVariableFloat32(current_position_variable, instruction_variable);
        }
        else if (ROS_Type.indexOf("float32[") != -1)
        {
            return JavaToROSTypesSource.getVariableFloat32Array(instruction_variable, current_position_variable);
        }
        else if (ROS_Type.equals("float64"))
        {
            return JavaToROSTypesSource.getVariableFloat64(current_position_variable, instruction_variable);
        }
        else if (ROS_Type.indexOf("float64[") != -1)
        {
            return JavaToROSTypesSource.getVariableFloat64Array(instruction_variable, current_position_variable);
        }
        else if (ROS_Type.equals("time"))
        {
            return JavaToROSTypesSource.getVariableUInt32(current_position_variable, instruction_variable.concat(".secs")).concat(JavaToROSTypesSource.getVariableUInt32(current_position_variable.concat("+4"), instruction_variable.concat(".nsecs")));
        }
        else if (ROS_Type.indexOf("time[") != -1)
        {
            // TODO:            
            return null;
        }     
        else if (ROS_Type.equals("duration"))
        {            
             return JavaToROSTypesSource.getVariableInt32(current_position_variable, instruction_variable.concat(".secs")).concat(JavaToROSTypesSource.getVariableInt32(current_position_variable.concat("+4"), instruction_variable.concat(".nsecs")));
        }
        else if (ROS_Type.indexOf("duration[") != -1)
        {
            // TODO:            
            return null;
        }   
        else if (ROS_Type.endsWith("Header"))
        {
            return JavaToROSTypesSource.getVariableHeader(instruction_variable, current_position_variable);
        }           
        else if (ROS_Type.equals("string"))
        {
             return JavaToROSTypesSource.getVariableString(instruction_variable, current_position_variable);
        }
        else if (ROS_Type.indexOf("string[") != -1)
        {
             return JavaToROSTypesSource.getVariableStringArray(instruction_variable, current_position_variable);
        }        
        else if (ROS_Type.startsWith("MSG:") || ROS_Type.startsWith("SRV:"))
        {
            // TODO:            
            // Incoming ROS Connection Headers sometimes include a line starting with MSG: that contains another message definition of the Message Definition of a non-primitive defined Object.            
            return null;
        }
        else if (ROS_Type.startsWith("---"))
        {
            // TODO:            
            // return secs/nsecs int32/int32 =64 longc
            return null;
        }
        else
        {        
            return null;
        }
}
    
    
    /** Returns the int array length in elements, or -1 if not found. */
    private static int getROSTypeFixedArrayLength(String ros_type)
    {
        if(ros_type.indexOf("]")!=-1)
        {
            int open;
            int close=-1;
            if((open=ros_type.indexOf("["))!=-1)
            {
                close=ros_type.indexOf("]");
                if(open+2<=close)
                {
                    try
                    {
                        return Integer.parseInt(ros_type.substring(ros_type.indexOf("[")+1,ros_type.indexOf("]")));
                    }
                    catch(Exception e)
                    {
                        return -1;
                    }
                }
            }
        }
        return -1;
    }
    
   /** Return Schema synchronously. Used to obtain nested Message Definitions 
    * for an undefined parent Object for generation of InstructionInputHandler, 
    * and InstructionOutputHandler
    */
    public static String getReadSchemaFile(String file_path, Controller controller, String rcsm_provider_name)
    {
        String class_name;
        String source;    
        try
        {
            // Read Schema File
            return source = new String(Files.readAllBytes(Paths.get(new File(file_path).toURI())));
        }
        catch (IOException ex)
        {
            Logger.getLogger(OutputHandlerToMessageGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }    
    
    /**
     * Returns the ROS message schema as a String[].
     */
    public static String[] process(String messageDefinition)
    {
        if(messageDefinition==null)
        {
            return null;
        }
        int objDefinesCount;
        String[] objDefines;
        int objCount = 0;
        String[] lines = messageDefinition.split(lineSeparator);
        // count the Definition Object lines.
        for (int i = 0; i < lines.length; i++)
        {
            if (lines[i].length() != 0 && lines[i].trim().indexOf("#") != 0)
            {
                objCount = objCount + 1;
            }
        }
        objDefinesCount = 0;
        objDefines = new String[objCount];
        // Generate the Object Definition List.
        for (int i = 0; i < lines.length; i++)
        {
            if (lines[i].length() != 0 && lines[i].trim().indexOf("#") != 0)
            {
                objDefines[objDefinesCount] = lines[i].trim();
                objDefinesCount = objDefinesCount + 1;
            }
        }

        //System.out.println(Arrays.toString(objDefines));
        return objDefines;
    }
}
