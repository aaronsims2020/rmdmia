package org.happy.artist.rmdmia.rcsm.providers.ros.client.message.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.happy.artist.rmdmia.Controller;
import org.happy.artist.rmdmia.instruction.utility.SchemaImporter;
import org.happy.artist.rmdmia.rcsm.provider.message.DefinitionToMessageInstructionSourceCodeGeneratorInterface;

 
/**
 * org.happy.artist.rmdmia.rcsm.providers.ros.client.message.generator.DefinitionToMessageGenerator 
 * is a ROS Topic/Service Message Definition to Java Class Generator.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2014-2015 Happy Artist. All rights reserved.
 */
public class DefinitionToMessageGenerator implements DefinitionToMessageInstructionSourceCodeGeneratorInterface
{
    private final static String lineSeparator=System.getProperty("line.separator");
    public static void main(String[] args)
    {
        // Test message definition here:
        DefinitionToMessageGenerator gen = new DefinitionToMessageGenerator();
//        String def="float32 x\n" +
//"float32 y\n" +
//"float32 theta\n" +
//"\n" +
//"float32 linear_velocity\n" +
//"float32 angular_velocity";
        String def="# Get the map as a nav_msgs/OccupancyGrid\n" +
"---\n" +
"nav_msgs/OccupancyGrid map\n" +
"---\n"+
"# no feedback";   
        // The hid implementation may not be testable without a valid hid from InstructionManager.
        System.out.println("Action source code:\n".concat(gen.process(0,"TestClass","org.package.test", def, null)));
        //String[] definitions = gen.process(def);
//        System.out.println("Java Source Code:\n" + process("TestClass","org.package.test", def));
    }
    
    /** Process String Array of Definition "type key", and return Java source code.
     * Map parent_map - pass in null - this is used by the process method for child Objects 
     * iteratively calling the process method.
     */
    // TODO: Determine Object interface implementation type, probably MovementInstruction.
    public String process(int hid, String class_name, String package_name, String message_definition, Controller controller)
    {
        if(class_name.isEmpty())
        {
            return null;
        }
        // Check if the class is a Service Definition. If so create an Internal Request, and Response Class. Then assign those classes as internal public final variables REQUEST, and RESPONSE, that are instantiated at initialization.
        boolean is_service;
        boolean is_action=false;
        // is_result is used to determine if the step is result, or feedback, after a --- is discovered.
        boolean is_result=false;        
        int definition_index;
        // if service has response variables used for class structure curly bracket variables.
        boolean has_response_variables=false;
        if(message_definition!=null&&(definition_index=message_definition.indexOf("---"))!=-1)
        {
            // Is it a service or action definition? 
            if((definition_index=message_definition.indexOf("\n",definition_index))!=-1)
            {
                if((definition_index=message_definition.indexOf("---",definition_index))!=-1)
                {
                    is_service=false;
                    is_action=true;
                }
                else
                {
                    is_service=true;
                }
            }
            else
            {
                is_service=true;
            }
        }
        else
        {
            is_service=false;
        }
        
//        System.out.println("Generating Instruction Object for " + class_name + ", package " + package_name + ", message_definition:\n" + message_definition);
        String[] definition=process(message_definition);
        StringBuilder sb = new StringBuilder();
        // Fixed array StringBuilder fixed_array_sb
        StringBuilder fixed_array_sb = new StringBuilder();      
        // The header HashMap contains a list of imports to append later.
        HashMap<String,String> header = new HashMap<String,String>();
        // variable type
        String type;
        
        String[] var_type;
        // Append package to header map
        if(package_name!=null)
        {
            header.put("package","package ".concat(package_name).concat(";"));       
        }
        // Add the Instruction import
        header.put("instruction", "import org.happy.artist.rmdmia.instruction.Instruction;");           // add the class declaration
        sb.append("public class ");
        sb.append(class_name);
        sb.append(" extends Instruction");        
        // Class opening bracket
        sb.append("\n{\n");
        
// TODO: Add length code to each instruction for obtaining byte[] length of output messages.        
        // append a temporary length method implementation, until a complete implementation is ready.
        1
        sb.append("public int length()\n{\nreturn -1;\n}\n");
if(is_service)
{
// Add the Request and Response Objects.    
    sb.append("public final Request REQUEST=new Request();\n");
    sb.append("public final Response RESPONSE=new Response();\n");
    // Start Request Class for Service 
    sb.append("class Request extends Instruction\n{\n");
    // append a temporary length method implementation, until a complete implementation is ready.
    sb.append("public int length()\n{\nreturn -1;\n}\n");    
}
else if(is_action)
{
// Add the ACTION Goal, Result, Components.    
    sb.append("public final Goal GOAL=new Goal();\n");
    sb.append("public final Result RESULT=new Result();\n");
    sb.append("public final Feedback FEEDBACK=new Feedback();\n");
    // Start Goal Class for Action 
    sb.append("class Goal extends Instruction\n{\n");  
    // append a temporary length method implementation, until a complete implementation is ready.
    sb.append("public int length()\n{\nreturn -1;\n}\n");      
}

//        System.out.println("Definition length: " + definition.length);
        for(int i=0;i<definition.length;i++)
        {
            // Get variable type and strip off comment code.
            var_type=getVariableType(definition[i].trim().split("#")[0]);
            // Length 2 is a type and variable name.
            if(var_type.length>=2)
            {
//          System.out.println("variable type length: " + var_type.length);
    //            for(int j=0;j<var_type.length;j++)
    //            {
//           System.out.println("variable type " + j + ": " + var_type[j]);                   
    //            }
                
                if((type=getJavaType(hid,var_type[0],controller))!=null&&!type.equals("MSG:"))
                {
                    if(type.contains("/"))
                    {
                        // if ros/ is not prepended to an Object with / prepend it, and replace / with . 
                        if(type.startsWith("ros/")||type.startsWith("ros."))
                        { 
                            type=type.replaceAll("/", ".");
                        }
                        else
                        {
                            type="ros/".concat(type).replaceAll("/", ".");
                        }
                    }
                     sb.append("public ");
                    // If array of specified length, instantiate array to length specified
                    int open;
                    int close=-1;
                    int array_length;
                    // if is a constant variable set to final static
                    if(var_type[1].indexOf("=")!=-1)
                    {
                       sb.append("final static "); 
                    }
                    if((open=var_type[0].indexOf("["))!=-1)
                    {
                        close=var_type[0].indexOf("]");
                        if(open+2<=close)
                        {
                            // fixed array
                            //TODO: if is Object then package name, otherwise no package name
                            if(isCustomType(type))
                            {
                                //sb.append(package_name + "." +type.substring(0,open+1).concat("]"));
                                sb.append(package_name + "." +type.substring(0,open+1));
                            }
                            else
                            {
                               // sb.append(type.substring(0,open+1).concat("]"));
                                sb.append(type.substring(0,open+1));
                            }
                            
                            for(int s=1;s<var_type.length;s++)
                            {
                                sb.append(" ");
                                sb.append(var_type[s]);
                            }
                            sb.append("=new ");
                            if(isCustomType(type))
                            {
                                //sb.append(package_name + "." +type);
                                sb.append(package_name + "." +type);                                
                            }
                            else
                            {
                                //sb.append(type.substring(0,open)); 
                                sb.append(type.substring(0,type.indexOf("[")).concat(var_type[0].substring(var_type[0].indexOf("["),var_type[0].indexOf("]")+1))); 
                            }
                            if(isCustomType(type))
                            {
                            // generate array Object generating code loop.
                            fixed_array_sb.append("for(int i=0;i<");
                            fixed_array_sb.append(var_type[1]);
                            fixed_array_sb.append(".length;i++)\n{\n");
                            // Instantiate array Objects
                            fixed_array_sb.append(var_type[1]);
                            fixed_array_sb.append("[");
                            fixed_array_sb.append("i");
                            fixed_array_sb.append("]=new ");
                            // Object Type
                            //fixed_array_sb.append(package_name + "." +type.substring(0,type.indexOf("[")));
                            fixed_array_sb.append(type.substring(0,type.indexOf("[")));
                            fixed_array_sb.append("();\n}\n");
                            }
                        }
                        else
                        {
                            // variable length array
                            //sb.append(type.substring(0,open+1).concat("]"));
                            sb.append(type);
                            for(int s=1;s<var_type.length;s++)
                            {
                                sb.append(" ");
                                sb.append(var_type[s]);
                            }
                        }
                    }                    
                    else
                    {                       
                        sb.append(type);
                        for(int s=1;s<var_type.length;s++)
                        {
                            sb.append(" ");
                            sb.append(var_type[s]);
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
                        // If type is --- class the REQUEST CLass, and start the RESPONSE Class.
                        sb.append("\n}\n");
                        sb.append("class Response extends Instruction\n{\n");     
                        // append a temporary length method implementation, until a complete implementation is ready.
                        sb.append("public int length()\n{\nreturn -1;\n}\n");                          
                    }   
                    else if (is_action&&!is_result)
                    {
                        // Is Result ACTION Class
                        is_result=true;
                        sb.append("\n}\n");
                        sb.append("class Result extends Instruction\n{\n");            
                        // append a temporary length method implementation, until a complete implementation is ready.
                        sb.append("public int length()\n{\nreturn -1;\n}\n");  
                    }
                    else if (is_action&&is_result)
                    {
                        // Is Feedback ACTION Class
                        sb.append("\n}\n");
                        sb.append("class Feedback extends Instruction\n{\n"); 
                        // append a temporary length method implementation, until a complete implementation is ready.
                        sb.append("public int length()\n{\nreturn -1;\n}\n");                     
                    }
                    
                }
                else
                {
                    // Type not recognized, is a custom type.
                    // Throw an Exception of some sort to determine alternative options.
      
                    System.out.println("Attempt to generate unsupported type in source code: " + var_type[0] + " " + var_type[1] + ", Source code for class " + package_name + "." + class_name + " invalid...");
                 

                    
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
                if((type=getJavaType(hid,var_type[0],controller))!=null&&type.equals("---"))
                {
                    if(is_service)
                    {
                        // If type is --- class the REQUEST CLass, and start the RESPONSE Class.
                        sb.append("}\n");
                        sb.append("class Response extends Instruction\n{\n");      
                        // append a temporary length method implementation, until a complete implementation is ready.
                        sb.append("public int length()\n{\nreturn -1;\n}\n");                          
                    }   
                    else if (is_action&&!is_result)
                    {
                        // Is Result ACTION Class
                        is_result=true;
                        sb.append("}\n");
                        sb.append("class Result extends Instruction\n{\n");      
                        // append a temporary length method implementation, until a complete implementation is ready.
                        sb.append("public int length()\n{\nreturn -1;\n}\n");                          
                    }
                    else if (is_action&&is_result)
                    {
                        // Is Feedback ACTION Class
                        sb.append("\n}\n");
                        sb.append("class Feedback extends Instruction\n{\n");   
                        // append a temporary length method implementation, until a complete implementation is ready.
                        sb.append("public int length()\n{\nreturn -1;\n}\n");                 
                    }
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
                        if(!definition[i].trim().isEmpty())
                        {
                            System.out.println("Unsupported single length variable type that is not a service request/response separator. Type: ".concat(var_type[0]) + "Message Definition:\n"+ message_definition+ "\nMessage Definition line: " + i + ", Line: " + definition[i]);
                        }
                    }
                    else
                    {
                        if(!definition[i].trim().isEmpty())
                        {                        
                            System.out.println("Unsupported single length variable type that is not a service request/response separator. - No variable at all. Message Definition:\n"+ message_definition+ "\nMessage Definition line: " + i + ", Line: " + definition[i]);
                        }
                    }
                }
               // System.out.println("variable type length: " + var_type.length + "array: " + Arrays.deepToString(var_type));
            }
        }
        // Class Closing Bracket
        if((is_service)&&has_response_variables)
        {
            sb.append("\n}\n");
            sb.append("public ");
            sb.append(class_name);
            sb.append("()\n{\n");
            // Fixed array Object instantiation code here
            sb.append(fixed_array_sb);
            sb.append("}\n}");
        }
        else
        {
            sb.append("\npublic ");
            sb.append(class_name);
            sb.append("()\n{\n");
            // Fixed array Object instantiation code here
            sb.append(fixed_array_sb);
            sb.append("}\n}");
        }
        // Insert Header
        sb.insert(0, getHeaderSource(header));
//System.out.println("INSTRUCTION SOURCE :".concat(sb.toString()));         
        return sb.toString();
    }
    
    /** Returns the Java header source (package, and imports) String. */
    private static String getHeaderSource(HashMap<String, String> map)
    {
        StringBuilder source = new StringBuilder();
        Object[] keys;
        if(map.get("package")!=null)
        {
            source.append(map.get("package").concat("\n\n"));
        }
        // Loop through other keys
        keys=map.keySet().toArray();
        for(int i=0;i<keys.length;i++)
        {
            if(((String)keys[i]).equals("package")==false)
            {
                source.append(map.get((String)keys[i]));
                source.append("\n");
            }
        }
        source.append("\n");
        
        source.append(" /**" +
        " * @author Happy Artist\n" +
" * \n" +
" * @copyright Copyright © 2014 Happy Artist. All rights reserved.\n" +
" */\n");
        return source.toString();
    }
    
    /** Return true if Java src type is a Custom type. */
    private static boolean isCustomType(String type)
    {   
        if(type.equals("boolean")||type.indexOf("boolean[")!=-1)
        {
            return false;
        }        
        else if(type.equals("byte")||type.indexOf("byte[")!=-1)
        {
            return false;
        }              
        else if(type.equals("char")||type.indexOf("char[")!=-1)
        {
            return false;
        }        
        else if(type.equals("short")||type.indexOf("short[")!=-1)
        {
            return false;
        }    
        else if(type.equals("int")||type.indexOf("int[")!=-1)
        {
            return false;
        }       
        else if(type.equals("long")||type.indexOf("long[")!=-1)
        {
            return false;              
        }              
        else if(type.equals("float")||type.indexOf("float[")!=-1)
        {
            return false;
        }        
        else if(type.equals("double")||type.indexOf("double[")!=-1)
        {
            return false;
        }           
        else if(type.equals("String")||type.indexOf("String[")!=-1)
        {
            return false;
        }  
        else
        {
            return true;
        }
    }
    
    
    /** Return the Java type for the ROS Message Definition variable. Returns null if field type unrecognized. */
    public static String getJavaType(int hid, String ROS_Type, Controller controller)
    {     
        if(ROS_Type.equals("Header"))
        {
            // bool - unsigned 8-bit int
            return "Header";
        }        
        else if(ROS_Type.equals("bool"))
        {
            // bool - unsigned 8-bit int
            return "boolean";
        }
        else if(ROS_Type.indexOf("bool[")!=-1)
        {
            // bool - unsigned 8-bit int
            return "boolean[]";
        }        
        else if(ROS_Type.equals("int8"))
        {
            return "byte";
        }
        else if(ROS_Type.indexOf("int8[")!=-1)
        {
            return "byte[]";
        }        
        else if(ROS_Type.equals("byte"))
        {
            return "byte";
        }
       else if(ROS_Type.indexOf("byte[")!=-1)
        {
            return "byte[]";
        }        
        else if(ROS_Type.equals("char"))
        {
            return "char";
        }        
        else if(ROS_Type.indexOf("char[")!=-1)
        {
            return "char[]";
        }        
        else if(ROS_Type.equals("uint8"))
        {
            return "short";
        }
        else if(ROS_Type.indexOf("uint8[")!=-1)
        {
            return "short[]";
        }
        else if(ROS_Type.equals("int16"))
        {
            return "int";
        }    
        else if(ROS_Type.indexOf("int16[")!=-1)
        {
            return "int[]";
        }           
        else if(ROS_Type.equals("uint16"))
        {
            return "int";           
        }  
        else if(ROS_Type.indexOf("uint16[")!=-1)
        {
            return "int[]";           
        }  
        else if(ROS_Type.equals("int32"))
        {
            return "int";
        }        
        else if(ROS_Type.indexOf("int32[")!=-1)
        {
            return "int[]";
        }        
        else if(ROS_Type.equals("uint32"))
        {
            return "long";
        }        
        else if(ROS_Type.indexOf("uint32[")!=-1)
        {
            return "long[]";
        }        
        else if(ROS_Type.equals("int64"))
        {
            return "long";            
        }        
        else if(ROS_Type.indexOf("int64[")!=-1)
        {
            return "long[]";            
        }        
        else if(ROS_Type.equals("uint64"))
        {
            return "long";              
        }    
        else if(ROS_Type.indexOf("uint64[")!=-1)
        {
            return "long[]";              
        }         
        else if(ROS_Type.equals("float32"))
        {
            return "float";
        }        
        else if(ROS_Type.indexOf("float32[")!=-1)
        {
            return "float[]";
        }        
        else if(ROS_Type.equals("float64"))
        {
            return "double";
        }            
        else if(ROS_Type.indexOf("float64[")!=-1)
        {
            return "double[]";
        }            
        else if(ROS_Type.equals("string"))
        {
            return "java.lang.String";
        }     
        else if(ROS_Type.indexOf("string[")!=-1)
        {
            return "java.lang.String[]";
        }     

        else if(ROS_Type.equals("time"))
        {
            // return secs/nsecs int32/int32 =64 long
            return "Time";
        }        
        else if(ROS_Type.indexOf("time[")!=-1)
        {
            // return secs/nsecs int32/int32 =64 long
            return "Time[]";
        }  
        else if(ROS_Type.equals("duration"))
        {
            // return secs/nsecs int32/int32 =64 long
            return "Duration";            
        } 
        else if(ROS_Type.indexOf("duration[")!=-1)
        {
            // return secs/nsecs int32/int32 =64 long
            return "Duration[]";            
        } 
        else if(ROS_Type.equals("Empty"))
        {
            // return Empty
            return "Empty";            
        }
        else if(ROS_Type.equals("std_msgs/Empty"))
        {
            // return Empty
            return "Empty";         
        }        
        else if(ROS_Type.equals("std_srvs/Empty"))
        {
            // return Empty
            return "Empty";            
        }         
        else if(ROS_Type.indexOf("duration[")!=-1)
        {
            // return secs/nsecs int32/int32 =64 long
            return "Duration[]";            
        } 
        else if(ROS_Type.startsWith("MSG:")||ROS_Type.startsWith("SRV:"))
        {
            // Incoming ROS Connection Headers sometimes include a line starting with MSG: that contains another message definition of the Message Definition of a non-primitive defined Object.            
            return "MSG:";
        }
        else if(ROS_Type.startsWith("---"))
        {
            // return secs/nsecs int32/int32 =64 longc
            return "---";            
        }         
        else
        {
//            System.out.println("Attempted to process unsupported type: ".concat(ROS_Type).concat(". Searching schema folder for the message definition..."));
            // Attempt to lookup in Schema Map.
//System.out.println("ROS_Type1: " + ROS_Type);            
    if(ROS_Type!=null&&!ROS_Type.startsWith("="))
            {
                Map<String,String> classLookupMap;
                if(controller!=null)
                {
                    // Check if Class exists, return class name
                    classLookupMap=controller.getInstructionManager().getClassLookupMap();
                }
                else
                {
                    System.out.println("Unit Testing the DefinitionToMessageGenerator...");
                    classLookupMap=new HashMap();
                }
                boolean is_array;
                String array_text;
                if(ROS_Type.indexOf("[")!=-1)
                {
                    is_array=true;
                    array_text=ROS_Type.substring(ROS_Type.indexOf("["));
                }
                else
                {
                    is_array=false;
                }
                String value;
                if(is_array)
                {
                    value=classLookupMap.get("ros/".concat(ROS_Type.substring(0,ROS_Type.indexOf("[")).toLowerCase()));
                }
                else
                {
                    value=classLookupMap.get("ros/".concat(ROS_Type.toLowerCase()));               
                }
//                System.out.println("ROS_Type2: value: " + value);
                if(value==null&&ROS_Type.indexOf("/")!=-1&&!is_array)
                {
                    value=classLookupMap.get("ros/".concat(ROS_Type.substring(ROS_Type.lastIndexOf("/")+1).toLowerCase()));
                }
                else if(value==null&&ROS_Type.indexOf("/")!=-1&&is_array)
                {
                    // is array
                    String temp_substring=ROS_Type.substring(0,ROS_Type.indexOf("["));
                    value=classLookupMap.get("ros/".concat(temp_substring.substring(temp_substring.lastIndexOf("/")+1).toLowerCase()));                    
                }
//                System.out.println("ROS_Type3: value: " + value);
                if(value!=null)
                {
                    String path=controller.getInstructionManager().getClassFolderPath().concat("/ros/");
                    if(value.indexOf(path)!=-1)
                    {
                        value=value.replaceAll("\\.class","");
                        value=value.substring(value.indexOf(path)+path.length()).replaceAll("/","\\.");
                        if(is_array)
                        {
                            value=value.concat("[]");
                        }
  //                      System.out.println("CLASS EXISTS 2: ".concat(value));
                        return value;
                    }
                }
                else
                {
// If class does not exist check if schema exists
                    // reuse existing classMap for schemaMap
                    if(controller!=null)
                    {
                        // Check if Class exists, return class name
                        classLookupMap=controller.getInstructionManager().getSchemaLookupMap();
                    }                 
                    if(is_array)
                    {
                        value=classLookupMap.get("ros/".concat(ROS_Type.substring(0,ROS_Type.indexOf("[")).toLowerCase()));                 
                    }
                    else
                    {
                        value=classLookupMap.get("ros/".concat(ROS_Type.toLowerCase()));          
                    }
                    
                    if(value==null&&ROS_Type.indexOf("/")!=-1&&!is_array)
                    {
                        value=classLookupMap.get("ros/".concat(ROS_Type.substring(ROS_Type.lastIndexOf("/")+1).toLowerCase()));
                    }
                    else if(value==null&&ROS_Type.indexOf("/")!=-1&&is_array)
                    {
                        String temp_substring=ROS_Type.substring(0,ROS_Type.indexOf("["));
                        value=classLookupMap.get("ros/".concat(temp_substring.substring(temp_substring.lastIndexOf("/")+1).toLowerCase()));
                    }
                          
//                    System.out.println("VALUE NOT FOUND IN MAP: " + value);
                    if(value!=null)
                    { 
//                        System.out.println("SCHEMA MAP key/value found: " + value);
                        ArrayList<String> list= new ArrayList<String>(1);
                        list.add(value);
                        SchemaImporter.generateSchemaInstructionsSynchronously(hid,list, controller, "ros");

//                        System.out.println("Generated Class File Code for:".concat(value));
                        // Update classmap and return the new type...
//                        controller.getInstructionManager().updateClassLookupMap("ros");
                        // Get the class lookup map
                        classLookupMap=controller.getInstructionManager().getClassLookupMap();
value=classLookupMap.get("ros/".concat(ROS_Type.toLowerCase()));         
                        if(value==null&&ROS_Type.indexOf("/")!=-1)
                        {
                            value=classLookupMap.get("ros/".concat(ROS_Type.substring(ROS_Type.lastIndexOf("/")+1).toLowerCase()));
                        }
                        if(value!=null)
                        {
                            String path=controller.getInstructionManager().getClassFolderPath().concat("/ros/");
                            if(value.indexOf(path)!=-1)
                            {
                                value=value.replaceAll("\\.class","");
                                value=value.substring(value.indexOf(path)+path.length()).replaceAll("/","\\.");
                                if(is_array)
                                {
                                    value=value.concat("[]");
                                }
  //                              System.out.println("CLASS EXISTS 2: ".concat(value));
                                return value;
                            }
                        }                            
                    }        
                }
            }             
            return ROS_Type;
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
  
    /** An internal method used to process Objects types inside the message definition. */
/*    private static boolean processDefinitionType(String[] message)
    {
        StringBuilder sb = new StringBuilder();
        boolean is_new_definition=false;
        String class_name_temp;
        String package_name_temp;
        String msg_type;
        // Parse out any additional Msgs, for separate processing
        for (int i=0;i<message.length;i++)
        {
            if(message[i].equalsIgnoreCase("msg:")||message[i].equalsIgnoreCase("srv:"))
            {
                if()
                // Get thge msg type
                
                // Get the Package name 
                
                // Get the class name
                
                // Set is_new_definition to true
                is_new_definition=true;
            }
            if(is_new_definition)
            {
                // Call process on the new type if class does not exist.
                // If not tbe initial MSG element.
                if(i!=0)
                {
                    String Java_source_code=DefinitionToMessageGenerator.process(String class_name, String package_name, sb.toString(), controller);
                }
                // Set is_new_definition to false.
                is_new_definition=false;
            }
        }
            //(message[i].equalsIgnoreCase("msg:")||message[i].equalsIgnoreCase("srv:"))
            // Check to see if the Object type has a pre-existing class available.
        
        // If a class is not available attempt to generate one using the attached MSG: definition.
        // If Object cannot be created or does not exist return false, else return true.

    }
  */    
    /** Returns the ROS message schema as a String[]. */
    public static String[] process(String messageDefinition)
    {
        int objDefinesCount;
        String[] objDefines;  
        int objCount=0;
        String[] lines = messageDefinition.split(lineSeparator);
        // count the Definition Object lines.
        for(int i=0;i<lines.length;i++)
        {
            if(lines[i].length()!=0&&lines[i].trim().indexOf("#")!=0)
            {
                objCount=objCount + 1;
            }
        }
        objDefinesCount=0;
        objDefines = new String[objCount];
        // Generate the Object Definition List.
        for(int i=0;i<lines.length;i++)
        {
            if(lines[i].length()!=0&&lines[i].trim().indexOf("#")!=0)
            {
                objDefines[objDefinesCount]=lines[i].trim();
                objDefinesCount=objDefinesCount + 1;
            }            
        }        
        
        //System.out.println(Arrays.toString(objDefines));
        return objDefines;
    }
    
    /** Return Instruction Object Pool Source code.
     * 
     * @param object_name Instruction Object name
     * @return String
     */
    public String getObjectPoolSRC(String object_name,String package_name)
    {
        StringBuilder sb=new StringBuilder();
        sb.append("package " + package_name + ";\n");
        sb.append("\n");
        sb.append("import java.util.Arrays;\n");
        sb.append("import java.util.logging.Logger;\n");
        sb.append("import org.happy.artist.rmdmia.instruction.Instruction;\n");
        sb.append("import "+package_name+"."+object_name+";\n");        
        sb.append("import org.happy.artist.rmdmia.instruction.providers.InstructionObjectPool;\n");
        sb.append("\n");
        sb.append("/** " + object_name + "InstructionPool.java is the Instruction Object Pool.  \n");
        sb.append(" *\n");
        sb.append(" * @author Happy Artist\n");
        sb.append(" * \n");
        sb.append(" * @copyright Copyright © 2015 Happy Artist. All rights reserved.\n");
        sb.append(" */\n");
        sb.append("public class " + object_name + "InstructionPool implements InstructionObjectPool\n");
        sb.append("{\n");
        sb.append("    private int STARTUP_INSTRUCTION_ARRAY_LENGTH=2;  \n");
        sb.append("    private " + object_name + "[] instructions = new " + object_name + "[STARTUP_INSTRUCTION_ARRAY_LENGTH];\n");
        sb.append("    private boolean[] availabilityArray = new boolean[STARTUP_INSTRUCTION_ARRAY_LENGTH];      public int hid;\n");
        sb.append("    private int checkoutIndex=0;\n");
        sb.append("    private int recycleCount=0;\n");
        sb.append("    private int checkoutCount=0;\n");
        sb.append("    private final static Logger logger = Logger.getLogger(" + object_name + "InstructionPool.class.getName());    \n");
        sb.append("\n");
        sb.append("    public " + object_name + "InstructionPool(int hid)\n");
        sb.append("    {\n");
        sb.append("        this.hid=hid;\n");
        sb.append("        for(int i=0;i<instructions.length;i++)\n");
        sb.append("        {\n");
        sb.append("            instructions[i] = new " + object_name + "(); \ninstructions[i].hid=hid;\n");
        sb.append("            availabilityArray[i]=true;\n");
        sb.append("        }\n");
        sb.append("    }    \n");
        sb.append("    \n");
        sb.append("    public void checkin(int instruction_id)\n");
        sb.append("    {\n");
        sb.append("        if(this.instructions[instruction_id].in_use<1)\n");
        sb.append("        {\n");
        sb.append("            //this.instructions[instruction_id].recycle();\n");
        sb.append("            availabilityArray[instruction_id]=true;\n");
        sb.append("            this.checkoutCount=checkoutCount - 1;\n");
        sb.append("        }\n");
        sb.append("    }        \n");
        sb.append("\n");
        sb.append("    public "+object_name+" checkout()\n");
        sb.append("    {\n");
        sb.append("        if(checkoutCount==STARTUP_INSTRUCTION_ARRAY_LENGTH)\n");
        sb.append("        {\n");
        sb.append("            this.instructions=Arrays.copyOf(instructions, instructions.length + 1);\n");
        sb.append("            this.availabilityArray=Arrays.copyOf(availabilityArray, instructions.length + 1);\n");
        sb.append("            this.checkoutIndex = checkoutCount; \n");
        sb.append("            availabilityArray[checkoutIndex]=true;           \n");
        sb.append("            instructions[checkoutIndex]=new " + object_name + "();\ninstructions[checkoutIndex].hid=checkoutIndex;\n");
        sb.append("            this.STARTUP_INSTRUCTION_ARRAY_LENGTH=instructions.length;\n");
        sb.append("        }\n");
        sb.append("        else\n");
        sb.append("        {\n");
        sb.append("            this.checkoutIndex = checkoutIndex + 1;            \n");
        sb.append("        }\n");
        sb.append("        if(checkoutIndex==STARTUP_INSTRUCTION_ARRAY_LENGTH)\n");
        sb.append("        {        \n");
        sb.append("            this.checkoutIndex=0;\n");
        sb.append("        }\n");
        sb.append("        if(this.availabilityArray[checkoutIndex])\n");
        sb.append("        {            \n");
        sb.append("            this.availabilityArray[checkoutIndex]=false;\n");
        sb.append("            this.checkoutCount = checkoutCount + 1;\n");
        sb.append("        }\n");
        sb.append("//        System.out.println(\"checkoutIndex:\"+checkoutIndex+\", checkoutCount:\"+checkoutCount+\", availabilityArray:\"+availabilityArray[checkoutIndex]);\n");
        sb.append("        return (" + object_name + ")instructions[checkoutIndex];  \n");
        sb.append("    }\n");
        sb.append("    public synchronized void recycle()\n");
        sb.append("    {\n");
        sb.append("        while(recycleCount<instructions.length)\n");
        sb.append("        {\n");
        sb.append("            //instructions[recycleCount].recycle();\n");
        sb.append("            availabilityArray[recycleCount]=true;\n");
        sb.append("            this.recycleCount=recycleCount + 1;\n");
        sb.append("        }\n");
        sb.append("        this.checkoutCount=0;\n");
        sb.append("        this.checkoutIndex=0;\n");
        sb.append("        this.recycleCount=0;\n");
        sb.append("    }    \n");
        sb.append("    public void shutdown()\n");
        sb.append("    {\n");
        sb.append("        while(checkoutIndex<instructions.length)\n");
        sb.append("        {\n");
        sb.append("            availabilityArray[recycleCount]=true;\n");
        sb.append("            this.checkoutIndex=recycleCount + 1;\n");
        sb.append("        }\n");
        sb.append("        this.checkoutCount=0;        \n");
        sb.append("        this.checkoutIndex=0;     \n");
        sb.append("    }\n");
        sb.append("}");
        return sb.toString();
    }        
}
