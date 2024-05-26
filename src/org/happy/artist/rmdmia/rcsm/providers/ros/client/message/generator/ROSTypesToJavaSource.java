package org.happy.artist.rmdmia.rcsm.providers.ros.client.message.generator;

import java.util.Arrays;

/**
 * org.happy.artist.rmdmia.rcsm.providers.ros.client.message.generator.ROSTypesToJavaSource
 * is a ROS Message Types utility that converts ROS 
 * Message Types to Java source code.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2014-2015 Happy Artist. All rights reserved.
 */
public class ROSTypesToJavaSource
{
    
    /** Return Fixed Java float source code String from ROS float32 byte[]. */
    public static String getFixedFloat32(int current_position)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Float.intBitsToFloat(((message[");
        sb.append(current_position);
        sb.append("] & 0xFF) << 24)\n");
        sb.append("| ((message[");
        sb.append(current_position + 1);
        sb.append("] & 0xFF) << 16)\n");
        sb.append("| ((message[");
        sb.append(current_position + 2);
        sb.append("] & 0xFF) << 8)\n");
        sb.append("| ((message[");
        sb.append(current_position + 3);
        sb.append("] & 0xFF) << 0));\n");        
        
        return sb.toString();  
    }      

    /** Return Assigned Variable Java Source Code String float[] as ROS multiple combines ROS float32s. */ 
    public static String getAssignedVariableFloat32Array(String assignment_variable, String current_position_variable)
    {
        return assignment_variable.concat("=").concat("ROSMessageDefinitionTypeConverter.getFloat32Array(message, "+current_position_variable+");\n").concat(current_position_variable).concat((" = ").concat(current_position_variable).concat(" + 4;\n"));
    }    
    
    /** Return Assigned Variable Java float source code String from ROS float32 byte[]. */public static String getAssignedVariableFloat32(String assignment_variable, String current_position_variable)
    {
        StringBuilder sb = new StringBuilder();
        // Assignment code
        sb.append(assignment_variable);
        sb.append("=");
        //        
        sb.append("Float.intBitsToFloat(((message[");
        sb.append(current_position_variable);
        sb.append("] & 0xFF) << 24)\n");
        sb.append("| ((message[");
        sb.append(current_position_variable.concat(" + 1"));
        sb.append("] & 0xFF) << 16)\n");
        sb.append("| ((message[");
        sb.append(current_position_variable.concat(" + 2"));
        sb.append("] & 0xFF) << 8)\n");
        sb.append("| ((message[");
        sb.append(current_position_variable.concat(" + 3"));
        sb.append("] & 0xFF) << 0));\n");              
        // Set the current_position to the first int following this variable.
        sb.append(current_position_variable);
        sb.append(" = ");
        sb.append(current_position_variable);
        sb.append(" + 4;");
        //        
        return sb.toString();  
    }      

    /** Return Assigned Fixed Java float source code String from ROS float32 byte[]. */
    public static String getAssignedFixedFloat32(String assignment_variable, int current_position)
    {
        StringBuilder sb = new StringBuilder();
        // Assignment code
        sb.append(assignment_variable);
        sb.append("=");
        //        
        sb.append("Float.intBitsToFloat(((message[");
        sb.append(current_position);
        sb.append("] & 0xFF) << 24)\n");
        sb.append("| ((message[");
        sb.append(current_position + 1);
        sb.append("] & 0xFF) << 16)\n");
        sb.append("| ((message[");
        sb.append(current_position + 2);
        sb.append("] & 0xFF) << 8)\n");
        sb.append("| ((message[");
        sb.append(current_position + 3);
        sb.append("] & 0xFF) << 0));\n");        
        
        return sb.toString();  
    }      
    
    /** Return Fixed Java Source Code String float[] as ROS multiple combines ROS float32s. */ 
    public static String getFixedFloat32Array(int start_position, int end_position)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        for(int i=start_position;i<end_position;i++)
        {            
            sb.append("Float.intBitsToFloat(((message[");
            sb.append(i);
            sb.append("] & 0xFF) << 24)\n");
            sb.append("| ((message[");
            sb.append(i=i+1);
            sb.append("] & 0xFF) << 16)\n");
            sb.append("| ((message[");
            sb.append(i=i+1);
            sb.append("] & 0xFF) << 8)\n");
            sb.append("| ((message[");
            sb.append(i=i+1);
            sb.append("] & 0xFF) << 0))\n");              
            // If end of array set i to message.length to end loop.
            if(i>=end_position)
            {
                //i=message.length;
            }
            else
            {
                // append , to StringBuilder
                sb.append(", ");
            }
        }
        sb.append("};\n");
        return sb.toString();         
    }   
    
    /** Return Assigned Fixed Java Source Code String float[] as ROS multiple combines ROS float32s. */ 
    public static String getAssignedFixedFloat32Array(String assignment_variable,int start_position, int end_position)
    {
        StringBuilder sb = new StringBuilder();
        int zero_iterator=0;
        for(int i=start_position;i<end_position;i++)
        {     
            // Assignment code
            sb.append(assignment_variable);
            sb.append("[");
            sb.append(String.valueOf(zero_iterator));
            sb.append("]=");
            //
            sb.append("Float.intBitsToFloat(((message[");
            sb.append(i);
            sb.append("] & 0xFF) << 24)\n");
            sb.append("| ((message[");
            sb.append(i=i+1);
            sb.append("] & 0xFF) << 16)\n");
            sb.append("| ((message[");
            sb.append(i=i+1);
            sb.append("] & 0xFF) << 8)\n");
            sb.append("| ((message[");
            sb.append(i=i+1);
            sb.append("] & 0xFF) << 0));\n");              
            // increment zero_iterator 
            zero_iterator=zero_iterator + 1;
        }
        return sb.toString();         
    }        
    
    /** Return Java double from ROS float64 byte[]. */
    /*public static double getFloat64(byte[] message)
    {
        return Double.longBitsToDouble((long)((message[0] & 0xFF) << 56) 
            | ((message[1] & 0xFF) << 48) 
            | ((message[2] & 0xFF) << 40) 
            | ((message[3] & 0xFF) << 32)
            | ((message[4] & 0xFF) << 24) 
            | ((message[5] & 0xFF) << 16) 
            | ((message[6] & 0xFF) << 8) 
            | ((message[7] & 0xFF) << 0)); 
    }    
    */
    /** Return Fixed Java float source code String from ROS float64 byte[]. */
    public static String getFixedFloat64(int current_position)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Double.longBitsToDouble(((message[");
        sb.append(current_position);
        sb.append("] & 0xFFL) << 56)\n");
        sb.append("| ((message[");
        sb.append(current_position + 1);
        sb.append("] & 0xFFL) << 48)\n");
        sb.append("| ((message[");
        sb.append(current_position + 2);
        sb.append("] & 0xFFL) << 40)\n");
        sb.append("| ((message[");
        sb.append(current_position + 3);
        sb.append("] & 0xFFL) << 32)\n");         
        sb.append("| ((message[");
        sb.append(current_position + 4);
        sb.append("] & 0xFFL) << 24)\n");
        sb.append("| ((message[");
        sb.append(current_position + 5);
        sb.append("] & 0xFFL) << 16)\n");
        sb.append("| ((message[");
        sb.append(current_position + 6);
        sb.append("] & 0xFFL) << 8)\n");         
        sb.append("| ((message[");
        sb.append(current_position + 7);
        sb.append("] & 0xFFL) << 0));");
        return sb.toString();         
    } 

    /** Return Assigned Variable Java float source code String from ROS float64 byte[]. */
    public static String getAssignedVariableFloat64(String assignment_variable, String current_position_variable)
    {
        StringBuilder sb = new StringBuilder();
        // Assignment code
        sb.append(assignment_variable);
        sb.append("=");
        //                
        sb.append("Double.longBitsToDouble(((message[");
        sb.append(current_position_variable);
        sb.append("] & 0xFFL) << 56)\n");
        sb.append("| ((message[");
        sb.append(current_position_variable.concat(" + 1"));
        sb.append("] & 0xFFL) << 48)\n");
        sb.append("| ((message[");
        sb.append(current_position_variable.concat(" + 2"));
        sb.append("] & 0xFFL) << 40)\n");
        sb.append("| ((message[");
        sb.append(current_position_variable.concat(" + 3"));
        sb.append("] & 0xFFL) << 32)\n");         
        sb.append("| ((message[");
        sb.append(current_position_variable.concat(" + 4"));
        sb.append("] & 0xFFL) << 24)\n");
        sb.append("| ((message[");
        sb.append(current_position_variable.concat(" + 5"));
        sb.append("] & 0xFFL) << 16)\n");
        sb.append("| ((message[");
        sb.append(current_position_variable.concat(" + 6"));
        sb.append("] & 0xFFL) << 8)\n");         
        sb.append("| ((message[");
        sb.append(current_position_variable.concat(" + 7"));
        sb.append("] & 0xFFL) << 0));\n");
        // Set the current_position to the first int following this variable.
        sb.append(current_position_variable);
        sb.append(" = ");
        sb.append(current_position_variable);
        sb.append(" + 8;\n");
        //         
        return sb.toString();         
    }    
    
    /** Return Assigned Variable Java Source Code String double[] as ROS multiple combines ROS float64s. */ 
    public static String getAssignedVariableFloat64Array(String assignment_variable, String current_position_variable)
    {
        return assignment_variable.concat("=").concat("ROSMessageDefinitionTypeConverter.getFloat64Array(message, "+current_position_variable+");\n").concat(current_position_variable).concat((" = ").concat(current_position_variable).concat(" + 8;\n"));
    }    
    
    /** Return Assigned Fixed Java float source code String from ROS float64 byte[]. */
    public static String getAssignedFixedFloat64(String assignment_variable, int current_position)
    {
        StringBuilder sb = new StringBuilder();
        // Assignment code
        sb.append(assignment_variable);
        sb.append("=");
        //                
        sb.append("Double.longBitsToDouble(((message[");
        sb.append(current_position);
        sb.append("] & 0xFFL) << 56)\n");
        sb.append("| ((message[");
        sb.append(current_position + 1);
        sb.append("] & 0xFFL) << 48)\n");
        sb.append("| ((message[");
        sb.append(current_position + 2);
        sb.append("] & 0xFFL) << 40)\n");
        sb.append("| ((message[");
        sb.append(current_position + 3);
        sb.append("] & 0xFFL) << 32)\n");         
        sb.append("| ((message[");
        sb.append(current_position + 4);
        sb.append("] & 0xFFL) << 24)\n");
        sb.append("| ((message[");
        sb.append(current_position + 5);
        sb.append("] & 0xFFL) << 16)\n");
        sb.append("| ((message[");
        sb.append(current_position + 6);
        sb.append("] & 0xFFL) << 8)\n");         
        sb.append("| ((message[");
        sb.append(current_position + 7);
        sb.append("] & 0xFFL) << 0));");
        return sb.toString();         
    }     
    
    /** Return Fixed Java source String for double[] as ROS multiple combines ROS float64s. */ 
    public static String getFixedFloat64Array(int start_position, int end_position)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        for(int i=start_position;i<end_position;i++)
        {            
            sb.append("Double.longBitsToDouble(((message[");
            sb.append(i);
            sb.append("] & 0xFFL) << 56)\n");
            sb.append("| ((message[");
            sb.append(i=i+1);
            sb.append("] & 0xFFL) << 48)\n");
            sb.append("| ((message[");
            sb.append(i=i+1);
            sb.append("] & 0xFFL) << 40)\n");
            sb.append("| ((message[");
            sb.append(i=i+1);
            sb.append("] & 0xFFL) << 32)\n");         
            sb.append("| ((message[");
            sb.append(i=i+1);
            sb.append("] & 0xFFL) << 24)\n");
            sb.append("| ((message[");
            sb.append(i=i+1);
            sb.append("] & 0xFFL) << 16)\n");
            sb.append("| ((message[");
            sb.append(i=i+1);
            sb.append("] & 0xFFL) << 8)\n");         
            sb.append("| ((message[");
            sb.append(i=i+1);
            sb.append("] & 0xFFL) << 0))");            
            // If end of array set i to message.length to end loop.
            if(i>=end_position)
            {
                //i=message.length;
            }
            else
            {
                // append , to StringBuilder
                sb.append(", ");
            }
        }
        sb.append("};\n");
        return sb.toString();           
    } 

    /** Return Assigned Fixed Java source String for double[] as ROS multiple combines ROS float64s. */ 
    public static String getAssignedFixedFloat64Array(String assignment_variable, int start_position, int end_position)
    {
        StringBuilder sb = new StringBuilder();
        int zero_iterator=0;
        for(int i=start_position;i<end_position;i++)
        {          
            // Assignment code
            sb.append(assignment_variable);
            sb.append("[");
            sb.append(String.valueOf(zero_iterator));
            sb.append("]=");
            //            
            sb.append("Double.longBitsToDouble(((message[");
            sb.append(i);
            sb.append("] & 0xFFL) << 56)\n");
            sb.append("| ((message[");
            sb.append(i=i+1);
            sb.append("] & 0xFFL) << 48)\n");
            sb.append("| ((message[");
            sb.append(i=i+1);
            sb.append("] & 0xFFL) << 40)\n");
            sb.append("| ((message[");
            sb.append(i=i+1);
            sb.append("] & 0xFFL) << 32)\n");         
            sb.append("| ((message[");
            sb.append(i=i+1);
            sb.append("] & 0xFFL) << 24)\n");
            sb.append("| ((message[");
            sb.append(i=i+1);
            sb.append("] & 0xFFL) << 16)\n");
            sb.append("| ((message[");
            sb.append(i=i+1);
            sb.append("] & 0xFFL) << 8)\n");         
            sb.append("| ((message[");
            sb.append(i=i+1);
            sb.append("] & 0xFFL) << 0));\n");            
            // increment zero_iterator 
            zero_iterator=zero_iterator + 1;
        }
        return sb.toString();           
    } 
    
    
    //time (secs/nsecs signed 32-bit ints)
    //duration (secs/nsecs signed 32-bit ints)       
    //bool to boolean
    //
    
    // arrays: 
    // fixed-length - []
    // variable-length - List
    // uint8[] - String or byte[] - int[] or byte[]
    // bool[] - boolean[]
    
    // Header uint32 seq,time stamp,string frame_id
    
    /** Return Fixed String Java Source code for ROS int64 as byte[]. */
    public static String getFixedInt64(int current_position)
    {        
        StringBuilder sb = new StringBuilder();
        sb.append("(long)((message[");
        sb.append(current_position);
        sb.append("]<<56) | (message[");
        sb.append(current_position + 1);
        sb.append("]<<48) | (message[");
        sb.append(current_position + 2);
        sb.append("]<<40) | (message[");
        sb.append(current_position + 3);
        sb.append("]<<32) | (message[");         
        sb.append(current_position + 4);
        sb.append("]<<24) | (message[");
        sb.append(current_position + 5);
        sb.append("]<<16) | (message[");
        sb.append(current_position + 6);
        sb.append("]<<8) | (message[");         
        sb.append(current_position + 7);
        sb.append("]));");
        return sb.toString();          
    }   
    
    /** Return Assigned Variable String Java Source code for ROS int64 as byte[]. */public static String getAssignedVariableInt64(String assignment_variable, String current_position_variable)
    {        
        StringBuilder sb = new StringBuilder();
        // Assignment code
        sb.append(assignment_variable);
        sb.append("=");
        //         
        sb.append("(long)((message[");
        sb.append(current_position_variable);
        sb.append("]<<56) | (message[");
        sb.append(current_position_variable.concat(" + 1"));
        sb.append("]<<48) | (message[");
        sb.append(current_position_variable.concat(" + 2"));
        sb.append("]<<40) | (message[");
        sb.append(current_position_variable.concat(" + 3"));
        sb.append("]<<32) | (message[");         
        sb.append(current_position_variable.concat(" + 4"));
        sb.append("]<<24) | (message[");
        sb.append(current_position_variable.concat(" + 5"));
        sb.append("]<<16) | (message[");
        sb.append(current_position_variable.concat(" + 6"));
        sb.append("]<<8) | (message[");         
        sb.append(current_position_variable.concat(" + 7"));
        sb.append("]));\n");
        // Set the current_position to the first int following this variable.
        sb.append(current_position_variable);
        sb.append(" = ");
        sb.append(current_position_variable);
        sb.append(" + 8;\n");
        //         
        return sb.toString();          
    }      
    
    /** Return Assigned Fixed String Java Source code for ROS int64 as byte[]. */
    public static String getAssignedFixedInt64(String assignment_variable, int current_position)
    {        
        StringBuilder sb = new StringBuilder();
        // Assignment code
        sb.append(assignment_variable);
        sb.append("=");
        //         
        sb.append("(long)((message[");
        sb.append(current_position);
        sb.append("]<<56) | (message[");
        sb.append(current_position + 1);
        sb.append("]<<48) | (message[");
        sb.append(current_position + 2);
        sb.append("]<<40) | (message[");
        sb.append(current_position + 3);
        sb.append("]<<32) | (message[");         
        sb.append(current_position + 4);
        sb.append("]<<24) | (message[");
        sb.append(current_position + 5);
        sb.append("]<<16) | (message[");
        sb.append(current_position + 6);
        sb.append("]<<8) | (message[");         
        sb.append(current_position + 7);
        sb.append("]));");
        return sb.toString();          
    }    
    
    /** Return String Fixed Java source for boolean from ROS bool as byte. */  
    public static String getFixedBool(int current_position)
    {
        return "message[".concat(String.valueOf(current_position)).concat("]!=0;");      
    }

    /** Return Assigned String Fixed Java source for boolean from ROS bool as byte. */  
    public static String getAssignedFixedBool(String assignment_variable, int current_position)
    {
        // Assignment code
        return assignment_variable.concat("=").concat("message[").concat(String.valueOf(current_position)).concat("]!=0;");      
    }    
    
    /** Return Assigned String Fixed Java source for boolean from ROS bool as byte. */  
    public static String getAssignedVariableBool(String assignment_variable, String current_position_variable)
    {
        // Assignment code
        return assignment_variable.concat("=").concat("message[").concat(String.valueOf(current_position_variable)).concat("]!=0;\n").concat(current_position_variable).concat((" = ").concat(current_position_variable).concat(" + 1;\n"));
    }      
    
    /** Return Fixed String Java source for boolean[] from ROS bool[] as byte[]. */ 
    public static String getFixedBoolArray(int start_position, int end_position)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for(int i=start_position;i<end_position;i++)
        {            
            sb.append("message[");
            sb.append(i);
            sb.append("]!=0");           
            // If end of array set i to message.length to end loop.
            if(i>=end_position)
            {
                //i=message.length;
            }
            else
            {
                // append , to StringBuilder
                sb.append(", ");
            }
        }
        sb.append("};\n");
        return sb.toString();                 
    }     

    /** Return Assigned Fixed String Java source for boolean[] from ROS bool[] as byte[]. */ 
    public static String getAssignedFixedBoolArray(String assignment_variable, int start_position, int end_position)
    {
        StringBuilder sb = new StringBuilder();
        int zero_iterator=0;
        for(int i=start_position;i<end_position;i++)
        {            
            // Assignment code
            sb.append(assignment_variable);
            sb.append("[");
            sb.append(String.valueOf(zero_iterator));
            sb.append("]=");
            //            
            sb.append("message[");
            sb.append(i);
            sb.append("]!=0;\n");           
            // increment zero_iterator 
            zero_iterator=zero_iterator + 1;
        }
        return sb.toString();                 
    }     
    
    /** Return Assigned Variable Java Source Code String boolean[] as ROS multiple combines ROS bool[]. */ 
    public static String getAssignedVariableBoolArray(String assignment_variable, String current_position_variable)
    {
        return assignment_variable.concat("=").concat("ROSMessageDefinitionTypeConverter.getBoolArray(message, "+current_position_variable+");\n").concat(current_position_variable).concat((" = ").concat(current_position_variable).concat(" + "+assignment_variable+".length*1;\n"));
    }        
    
    /** Return String Java source for fixed long[] as ROS multiple combines ROS int64. */ 
    public static String getFixedInt64Array(int start_position, int end_position)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for(int i=start_position;i<end_position;i++)
        {            
            sb.append("(long)((message[");
            sb.append(i);
            sb.append("]<<56) | (message[");
            sb.append(i=i+1);
            sb.append("]<<48) | (message[");
            sb.append(i=i+1);
            sb.append("]<<40) | (message[");
            sb.append(i=i+1);
            sb.append("]<<32) | (message[");         
            sb.append(i=i+1);
            sb.append("]<<24) | (message[");
            sb.append(i=i+1);
            sb.append("]<<16) | (message[");
            sb.append(i=i+1);
            sb.append("]<<8) | (message[");         
            sb.append(i=i+1);
            sb.append("]))");            
            // If end of array set i to message.length to end loop.
            if(i>=end_position)
            {
//                i=message.length;
            }
            else
            {
                // append , to StringBuilder
                sb.append(", ");
            }
        }
        sb.append("};\n");
        return sb.toString();            
    }       

    /** Return Assigned String Java source for fixed long[] as ROS multiple combines ROS int64. */ 
    public static String getAssignedFixedInt64Array(String assignment_variable, int start_position, int end_position)
    {
        StringBuilder sb = new StringBuilder();
        int zero_iterator=0;
        for(int i=start_position;i<end_position;i++)
        {           
            // Assignment code
            sb.append(assignment_variable);
            sb.append("[");
            sb.append(String.valueOf(zero_iterator));
            sb.append("]=");
            //            
            sb.append("(long)((message[");
            sb.append(i);
            sb.append("]<<56) | (message[");
            sb.append(i=i+1);
            sb.append("]<<48) | (message[");
            sb.append(i=i+1);
            sb.append("]<<40) | (message[");
            sb.append(i=i+1);
            sb.append("]<<32) | (message[");         
            sb.append(i=i+1);
            sb.append("]<<24) | (message[");
            sb.append(i=i+1);
            sb.append("]<<16) | (message[");
            sb.append(i=i+1);
            sb.append("]<<8) | (message[");         
            sb.append(i=i+1);
            sb.append("]));\n");            
            // increment zero_iterator 
            zero_iterator=zero_iterator + 1;
        }
        return sb.toString();            
    }     
    
    /** Return Assigned Variable Java Source Code String long[] as ROS multiple combines ROS int64[]. */ 
    public static String getAssignedVariableInt64Array(String assignment_variable, String current_position_variable)
    {
        return assignment_variable.concat("=").concat("ROSMessageDefinitionTypeConverter.getInt64Array(message, "+current_position_variable+");\n").concat(current_position_variable).concat((" = ").concat(current_position_variable).concat(" + 8;\n"));
    }      
    
    
    /** Return String for Java source of fixed long for byte[] of ROS uint64. */
    public static String getFixedUInt64(int current_position)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("(long)((message[");
        sb.append(current_position);
        sb.append("] & 0xFFL) << 56) | ((message[");
        sb.append(current_position + 1);
        sb.append("] & 0xFFL) << 48) | ((message[");
        sb.append(current_position + 2);
        sb.append("] & 0xFFL) << 40) | ((message[");
        sb.append(current_position + 3);
        sb.append("] & 0xFFL) << 32) | ((message[");         
        sb.append(current_position + 4);
        sb.append("] & 0xFFL) << 24) | ((message[");
        sb.append(current_position + 5);
        sb.append("] & 0xFFL) << 16) | (message[");
        sb.append(current_position + 6);
        sb.append("] & 0xFFL) << 8 | ((message[");         
        sb.append(current_position + 7);
        sb.append("] & 0xFFL));");
        return sb.toString();          
    }     

    /** Return Assigned Variable String for Java source of fixed long for byte[] of ROS uint64. */
    public static String getAssignedVariableUInt64(String assignment_variable, String current_position_variable)
    {
        StringBuilder sb = new StringBuilder();
        // Assignment code
        sb.append(assignment_variable);
        sb.append("=");
        //         
        sb.append("(long)((message[");
        sb.append(current_position_variable);
        sb.append("] & 0xFFL) << 56) | ((message[");
        sb.append(current_position_variable.concat(" + 1"));
        sb.append("] & 0xFFL) << 48) | ((message[");
        sb.append(current_position_variable.concat(" + 2"));
        sb.append("] & 0xFFL) << 40) | ((message[");
        sb.append(current_position_variable.concat(" + 3"));
        sb.append("] & 0xFFL) << 32) | ((message[");         
        sb.append(current_position_variable.concat(" + 4"));
        sb.append("] & 0xFFL) << 24) | ((message[");
        sb.append(current_position_variable.concat(" + 5"));
        sb.append("] & 0xFFL) << 16) | (message[");
        sb.append(current_position_variable.concat(" + 6"));
        sb.append("] & 0xFFL) << 8 | ((message[");         
        sb.append(current_position_variable.concat(" + 7"));
        sb.append("] & 0xFFL));\n");
        // Set the current_position to the first int following this variable.
        sb.append(current_position_variable);
        sb.append(" = ");
        sb.append(current_position_variable);
        sb.append(" + 8;\n");        
        return sb.toString();          
    }     
    
    /** Return Assigned String for Java source of fixed long for byte[] of ROS uint64. */
    public static String getAssignedFixedUInt64(String assignment_variable, int current_position)
    {
        StringBuilder sb = new StringBuilder();
        // Assignment code
        sb.append(assignment_variable);
        sb.append("=");
        //         
        sb.append("(long)((message[");
        sb.append(current_position);
        sb.append("] & 0xFFL) << 56) | ((message[");
        sb.append(current_position + 1);
        sb.append("] & 0xFFL) << 48) | ((message[");
        sb.append(current_position + 2);
        sb.append("] & 0xFFL) << 40) | ((message[");
        sb.append(current_position + 3);
        sb.append("] & 0xFFL) << 32) | ((message[");         
        sb.append(current_position + 4);
        sb.append("] & 0xFFL) << 24) | ((message[");
        sb.append(current_position + 5);
        sb.append("] & 0xFFL) << 16) | (message[");
        sb.append(current_position + 6);
        sb.append("] & 0xFFL) << 8 | ((message[");         
        sb.append(current_position + 7);
        sb.append("] & 0xFFL));");
        return sb.toString();          
    }  
    
    /** Return String Java source for fixed long[] as ROS multiple combines ROS uint64. */ 
    public static String getFixedUInt64Array(int start_position, int end_position)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for(int i=start_position;i<end_position;i++)
            {            
            sb.append("(long)((message[");
            sb.append(i);
            sb.append("] & 0xFFL) << 56) | ((message[");
            sb.append(i=i+1);
            sb.append("] & 0xFFL) << 48) | ((message[");
            sb.append(i=i+1);
            sb.append("] & 0xFFL) << 40) | ((message[");
            sb.append(i=i+1);
            sb.append("] & 0xFFL) << 32) | ((message[");         
            sb.append(i=i+1);
            sb.append("] & 0xFFL) << 24) | ((message[");
            sb.append(i=i+1);
            sb.append("] & 0xFFL) << 16) | (message[");
            sb.append(i=i+1);
            sb.append("] & 0xFFL) << 8 | ((message[");         
            sb.append(i=i+1);
            sb.append("] & 0xFFL))");           
            // If end of array set i to message.length to end loop.
            if(i>=end_position)
            {
                //i=message.length;
            }
            else
            {
                // append , to StringBuilder
                sb.append(", ");
            }
        }
        sb.append("};\n");
        return sb.toString(); 
    }     
    
    /** Return Assigned Variable Java Source Code String long[] as ROS multiple combines ROS uint64[]. */ 
    public static String getAssignedVariableUInt64Array(String assignment_variable, String current_position_variable)
    {
        return assignment_variable.concat("=").concat("ROSMessageDefinitionTypeConverter.getUInt64Array(message, "+current_position_variable+");\n").concat(current_position_variable).concat((" = ").concat(current_position_variable).concat(" + 8;\n"));
    }        

    /** Return Assigned String Java source for fixed long[] as ROS multiple combines ROS uint64. */ 
    public static String getAssignedFixedUInt64Array(String assignment_variable, int start_position, int end_position)
    {
        StringBuilder sb = new StringBuilder();
        int zero_iterator=0;
        for(int i=start_position;i<end_position;i++)
            {    
            // Assignment code
            sb.append(assignment_variable);
            sb.append("[");
            sb.append(String.valueOf(zero_iterator));
            sb.append("]=");
            //                
            sb.append("(long)((message[");
            sb.append(i);
            sb.append("] & 0xFFL) << 56) | ((message[");
            sb.append(i=i+1);
            sb.append("] & 0xFFL) << 48) | ((message[");
            sb.append(i=i+1);
            sb.append("] & 0xFFL) << 40) | ((message[");
            sb.append(i=i+1);
            sb.append("] & 0xFFL) << 32) | ((message[");         
            sb.append(i=i+1);
            sb.append("] & 0xFFL) << 24) | ((message[");
            sb.append(i=i+1);
            sb.append("] & 0xFFL) << 16) | (message[");
            sb.append(i=i+1);
            sb.append("] & 0xFFL) << 8 | ((message[");         
            sb.append(i=i+1);
            sb.append("] & 0xFFL));\n");           
            // increment zero_iterator 
            zero_iterator=zero_iterator + 1;
        }
        return sb.toString(); 
    }     
    
    
    /** Return String Java source for fixed long from ROS uint32 byte[]. */public static String getFixedUInt32(int current_position)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("(long)((message[");
        sb.append(current_position);
        sb.append("]&0xFFL) << 24) | ((message[");
        sb.append(current_position + 1);
        sb.append("] & 0xFFL) << 16) | (message[");
        sb.append(current_position + 2);
        sb.append("] & 0xFFL) << 8 | ((message[");
        sb.append(current_position + 3);
        sb.append("] & 0xFFL));\n");
        return sb.toString();              
    }     

    /** Return Assigned Variable String Java source for fixed long from ROS uint32 byte[]. */
    public static String getAssignedVariableUInt32(String assignment_variable, String current_position_variable)
    {
        StringBuilder sb = new StringBuilder();
        // Assignment code
        sb.append(assignment_variable);
        sb.append("=");
        //            
        sb.append("(long)((message[");
        sb.append(current_position_variable);
        sb.append("]&0xFFL) << 24) | ((message[");
        sb.append(current_position_variable.concat(" + 1"));
        sb.append("] & 0xFFL) << 16) | (message[");
        sb.append(current_position_variable.concat(" + 2"));
        sb.append("] & 0xFFL) << 8 | ((message[");
        sb.append(current_position_variable.concat(" + 3"));
        sb.append("] & 0xFFL));\n");
        // Set the current_position to the first int following this variable.
        sb.append(current_position_variable);
        sb.append(" = ");
        sb.append(current_position_variable);
        sb.append(" + 4;\n");         
        return sb.toString();              
    }     
    
    /** Return Assigned String Java source for fixed long from ROS uint32 byte[]. */
    public static String getAssignedFixedUInt32(String assignment_variable, int current_position)
    {
        StringBuilder sb = new StringBuilder();
        // Assignment code
        sb.append(assignment_variable);
        sb.append("=");
        //            
        sb.append("(long)((message[");
        sb.append(current_position);
        sb.append("]&0xFFL) << 24) | ((message[");
        sb.append(current_position + 1);
        sb.append("] & 0xFFL) << 16) | (message[");
        sb.append(current_position + 2);
        sb.append("] & 0xFFL) << 8 | ((message[");
        sb.append(current_position + 3);
        sb.append("] & 0xFFL));\n");
        return sb.toString();              
    }      
    
    /** Return Fixed String Java source long[] as ROS multiple combines ROS uint32. */ 
    public static String getFixedUInt32Array(int start_position, int end_position)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for(int i=start_position;i<end_position;i++)
        {            
            sb.append("(long)((message[");
            sb.append(i);
            sb.append("]&0xFFL) << 24) | ((message[");
            sb.append(i=i+1);
            sb.append("] & 0xFFL) << 16) | (message[");
            sb.append(i=i+1);
            sb.append("] & 0xFFL) << 8 | ((message[");
            sb.append(i=i+1);
            sb.append("] & 0xFFL))");
            // If end of array set i to message.length to end loop.
            if(i>=end_position)
            {
//                i=message.length;
            }
            else
            {
                // append , to StringBuilder
                sb.append(", ");
            }
        }
        sb.append("};\n");
        return sb.toString();         
    }       

    /** Return Assigned Fixed String Java source long[] as ROS multiple combines ROS uint32. */ 
    public static String getAssignedFixedUInt32Array(String assignment_variable, int start_position, int end_position)
    {
        StringBuilder sb = new StringBuilder();
        int zero_iterator=0;
        for(int i=start_position;i<end_position;i++)
        {    
            // Assignment code
            sb.append(assignment_variable);
            sb.append("[");
            sb.append(String.valueOf(zero_iterator));
            sb.append("]=");
            //            
            sb.append("(long)((message[");
            sb.append(i);
            sb.append("]&0xFFL) << 24) | ((message[");
            sb.append(i=i+1);
            sb.append("] & 0xFFL) << 16) | (message[");
            sb.append(i=i+1);
            sb.append("] & 0xFFL) << 8 | ((message[");
            sb.append(i=i+1);
            sb.append("] & 0xFFL));\n");
            // increment zero_iterator 
            zero_iterator=zero_iterator + 1;
        }
        return sb.toString();         
    }       
    
    /** Return Assigned Variable Java Source Code String long[] as ROS multiple combines ROS uint32[]. */ 
    public static String getAssignedVariableUInt32Array(String assignment_variable, String current_position_variable)
    {
        return assignment_variable.concat("=").concat("ROSMessageDefinitionTypeConverter.getUInt32Array(message, "+current_position_variable+");\n").concat(current_position_variable).concat((" = ").concat(current_position_variable).concat(" + 4;\n"));
    }     
    
    /** Return String Java source for int from ROS int32 as byte[]. */
    public static String getFixedInt32(int current_position)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("((message[");
        sb.append(current_position);
        sb.append("]<<24) | (message[");
        sb.append(current_position + 1);
        sb.append("]<<16) | (message[");
        sb.append(current_position + 2);
        sb.append("]<<8) | (message[");
        sb.append(current_position + 3);
        sb.append("]));\n");
        return sb.toString();         
    }         

    /** Return Assigned Variable String Java source for int from ROS int32 as byte[]. */
    public static String getAssignedVariableInt32(String assignment_variable, String current_position_variable)
    {
        StringBuilder sb = new StringBuilder();
        // Assignment code
        sb.append(assignment_variable);
        sb.append("=");
        //           
        sb.append("((message[");
        sb.append(current_position_variable);
        sb.append("]<<24) | (message[");
        sb.append(current_position_variable.concat(" + 1"));
        sb.append("]<<16) | (message[");
        sb.append(current_position_variable.concat(" + 2"));
        sb.append("]<<8) | (message[");
        sb.append(current_position_variable.concat(" + 3"));
        sb.append("]));\n");
        // Set the current_position to the first int following this variable.
        sb.append(current_position_variable);
        sb.append(" = ");
        sb.append(current_position_variable);
        sb.append(" + 4;\n");         
        return sb.toString();         
    }     
    
    /** Return Assigned String Java source for int from ROS int32 as byte[]. */
    public static String getAssignedFixedInt32(String assignment_variable, int current_position)
    {
        StringBuilder sb = new StringBuilder();
        // Assignment code
        sb.append(assignment_variable);
        sb.append("=");
        //           
        sb.append("((message[");
        sb.append(current_position);
        sb.append("]<<24) | (message[");
        sb.append(current_position + 1);
        sb.append("]<<16) | (message[");
        sb.append(current_position + 2);
        sb.append("]<<8) | (message[");
        sb.append(current_position + 3);
        sb.append("]));\n");
        return sb.toString();         
    }         
    
    
    /** Return String Java source for fixed Java int[] from ROS int32[] of message. */ 
    public static String getFixedInt32Array(int start_position, int end_position)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for(int i=start_position;i<end_position;i++)
        {            
        sb.append("((message[");
        sb.append(i);
        sb.append("]<<24) | (message[");
        sb.append(i=i+1);
        sb.append("]<<16) | (message[");
        sb.append(i=i+1);
        sb.append("]<<8) | (message[");
        sb.append(i=i+1);
        sb.append("]))");
        // If end of array set i to message.length to end loop.
        if(i>=end_position)
        {
//            i=message.length;
        }
        else
        {
            // append , to StringBuilder
            sb.append(", ");
        }
        }
        sb.append("};\n");
        return sb.toString();          
    }           

    /** Return Assigned Variable Java Source Code String int[] as ROS multiple combines ROS int32[]. */ 
    public static String getAssignedVariableInt32Array(String assignment_variable, String current_position_variable)
    {
        return assignment_variable.concat("=").concat("ROSMessageDefinitionTypeConverter.getInt32Array(message, "+current_position_variable+");\n").concat(current_position_variable).concat((" = ").concat(current_position_variable).concat(" + 4;\n"));
    }         
    
    /** Return Assigned String Java source for fixed Java int[] from ROS int32[] of message. */ 
    public static String getAssignedFixedInt32Array(String assignment_variable, int start_position, int end_position)
    {
        StringBuilder sb = new StringBuilder();
        int zero_iterator=0;
        for(int i=start_position;i<end_position;i++)
        {          
            // Assignment code
            sb.append(assignment_variable);
            sb.append("[");
            sb.append(String.valueOf(zero_iterator));
            sb.append("]=");
            //            
        sb.append("((message[");
        sb.append(i);
        sb.append("]<<24) | (message[");
        sb.append(i=i+1);
        sb.append("]<<16) | (message[");
        sb.append(i=i+1);
        sb.append("]<<8) | (message[");
        sb.append(i=i+1);
        sb.append("]));\n");
            // increment zero_iterator 
            zero_iterator=zero_iterator + 1;
        }
        return sb.toString();          
    }           
    
    
    /** Return Fixed String Java source for int from ROS uint16.  */
    public static String getFixedUInt16(int current_position)
    {   
        StringBuilder sb = new StringBuilder();
        sb.append("(int)(message[");
        sb.append(current_position);
        sb.append("] & 0xFF) | ((message[");
        sb.append(current_position + 1);
        sb.append("] & 0xFF) << 8);");
        return sb.toString();         
    }     

    /** Return Assigned Variable String Java source for int from ROS uint16.  */
    public static String getAssignedVariableUInt16(String assignment_variable, String current_position_variable)
    {   
        StringBuilder sb = new StringBuilder();
        // Assignment code
        sb.append(assignment_variable);
        sb.append("=");
        //                 
        sb.append("(int)(message[");
        sb.append(current_position_variable);
        sb.append("] & 0xFF) | ((message[");
        sb.append(current_position_variable.concat(" + 1"));
        sb.append("] & 0xFF) << 8);\n");
        // Set the current_position to the first int following this variable.
        sb.append(current_position_variable);
        sb.append(" = ");
        sb.append(current_position_variable);
        sb.append(" + 2;\n");            
        return sb.toString();         
    }     
    
    /** Return Assigned Fixed String Java source for int from ROS uint16.  */
    public static String getAssignedFixedUInt16(String assignment_variable, int current_position)
    {   
        StringBuilder sb = new StringBuilder();
        // Assignment code
        sb.append(assignment_variable);
        sb.append("=");
        //                 
        sb.append("(int)(message[");
        sb.append(current_position);
        sb.append("] & 0xFF) | ((message[");
        sb.append(current_position + 1);
        sb.append("] & 0xFF) << 8);");
        return sb.toString();         
    }     
    
    /** Return Fixed String Java source for int[] as ROS multiple combines ROS uint16. */ 
    public static String getFixedUInt16Array(int start_position, int end_position)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for(int i=start_position;i<end_position;i++)
        {            
            sb.append("(int)(message[");
            sb.append(i);
            sb.append("] & 0xFF) | ((message[");
            sb.append(i=i+1);
            sb.append("] & 0xFF) << 8)");
            // If end of array set i to message.length to end loop.
            if(i>=end_position)
            {
                //i=message.length;
            }
            else
            {
                // append , to StringBuilder
                sb.append(", ");
            }
        }
        sb.append("};\n");
        return sb.toString();          
    }      

    /** Return Assigned Fixed String Java source for int[] as ROS multiple combines ROS uint16. */ 
    public static String getAssignedFixedUInt16Array(String assignment_variable, int start_position, int end_position)
    {
        StringBuilder sb = new StringBuilder();
        int zero_iterator=0;
        for(int i=start_position;i<end_position;i++)
        {      
            // Assignment code
            sb.append(assignment_variable);
            sb.append("[");
            sb.append(String.valueOf(zero_iterator));
            sb.append("]=");
            //            
            sb.append("(int)(message[");
            sb.append(i);
            sb.append("] & 0xFF) | ((message[");
            sb.append(i=i+1);
            sb.append("] & 0xFF) << 8);\n");
            // increment zero_iterator 
            zero_iterator=zero_iterator + 1;
        }
        return sb.toString();          
    }      
        
    /** Return Assigned Variable Java Source Code String int[] as ROS multiple combines ROS uint16[]. */ 
    public static String getAssignedVariableUInt16Array(String assignment_variable, String current_position_variable)
    {
        return assignment_variable.concat("=").concat("ROSMessageDefinitionTypeConverter.getUInt16Array(message, "+current_position_variable+");\n").concat(current_position_variable).concat((" = ").concat(current_position_variable).concat(" + 2;\n"));
    }      
    
    /** Return Java source for short from ROS int16 as short. */
    public static String getFixedInt16(int current_position)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("(short)((message[");
        sb.append(current_position);
        sb.append("]<<8) | (message[");
        sb.append(current_position + 1);
        sb.append("]));");
        return sb.toString();            
    }    

    /** Return Assigned Variable Java source for short from ROS int16 as short. */
    public static String getAssignedVariableInt16(String assignment_variable, String current_position_variable)
    {
        StringBuilder sb = new StringBuilder();
        // Assignment code
        sb.append(assignment_variable);
        sb.append("=");
        //               
        sb.append("(short)((message[");
        sb.append(current_position_variable);
        sb.append("]<<8) | (message[");
        sb.append(current_position_variable.concat(" + 1"));
        sb.append("]));\n");
        // Set the current_position to the first int following this variable.
        sb.append(current_position_variable);
        sb.append(" = ");
        sb.append(current_position_variable);
        sb.append(" + 2;\n");          
        return sb.toString();            
    }      
    
    /** Return Assigned Java source for short from ROS int16 as short. */
    public static String getAssignedFixedInt16(String assignment_variable, int current_position)
    {
        StringBuilder sb = new StringBuilder();
        // Assignment code
        sb.append(assignment_variable);
        sb.append("=");
        //               
        sb.append("(short)((message[");
        sb.append(current_position);
        sb.append("]<<8) | (message[");
        sb.append(current_position + 1);
        sb.append("]));");
        return sb.toString();            
    }    
    
    
    /** Return Fixed String Java source short[] as ROS multiple combines ROS int16. */ 
    public static String getFixedInt16Array(int start_position, int end_position)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for(int i=start_position;i<end_position;i++)
        {            
            sb.append("(short)((message[");
            sb.append(i);
            sb.append("]<<8) | (message[");
            sb.append(i=i+1);
            sb.append("]))");        // If end of array set i to message.length to end loop.
            if(i>=end_position)
            {
//                i=message.length;
            }
            else
            {
                // append , to StringBuilder
                sb.append(", ");
            }
        }
        sb.append("};\n");
        return sb.toString();
    }         

    /** Return Assigned Fixed String Java source short[] as ROS multiple combines ROS int16. */ 
    public static String getAssignedFixedInt16Array(String assignment_variable, int start_position, int end_position)
    {
        StringBuilder sb = new StringBuilder();
        int zero_iterator=0;
        for(int i=start_position;i<end_position;i++)
        {       
            // Assignment code
            sb.append(assignment_variable);
            sb.append("[");
            sb.append(String.valueOf(zero_iterator));
            sb.append("]=");
            //            
            sb.append("(short)((message[");
            sb.append(i);
            sb.append("]<<8) | (message[");
            sb.append(i=i+1);
            sb.append("]));\n");        // If end of array set i to message.length to end loop.
            // increment zero_iterator 
            zero_iterator=zero_iterator + 1;
        }
        return sb.toString();
    }         
    
    /** Return Assigned Variable Java Source Code String short[] as ROS multiple combines ROS int16[]. */ 
    public static String getAssignedVariableInt16Array(String assignment_variable, String current_position_variable)
    {
        return assignment_variable.concat("=").concat("ROSMessageDefinitionTypeConverter.getInt16Array(message, "+current_position_variable+");\n").concat(current_position_variable).concat((" = ").concat(current_position_variable).concat(" + 2;\n"));
    }    
    
    /** Return String Java byte source from ROS int8. */
    public static String getFixedInt8(int current_position)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("message[");
        sb.append(current_position);
        sb.append("];");
        return sb.toString();            
    }    

    /** Return Variable String Java byte source from ROS int8. */
    public static String getAssignedVariableInt8(String assignment_variable, String current_position_variable)
    {
        StringBuilder sb = new StringBuilder();
        // Assignment code
        sb.append(assignment_variable);
        sb.append("=");
        //               
        sb.append("message[");
        sb.append(current_position_variable);
        sb.append("];\n");
        // Set the current_position to the first int following this variable.
        sb.append(current_position_variable);
        sb.append(" = ");
        sb.append(current_position_variable);
        sb.append(" + 1;\n");          
        return sb.toString();            
    }     
    
    /** Return Assigned String Java byte source from ROS int8. */
    public static String getAssignedFixedInt8(String assignment_variable, int current_position)
    {
        StringBuilder sb = new StringBuilder();
        // Assignment code
        sb.append(assignment_variable);
        sb.append("=");
        //               
        sb.append("message[");
        sb.append(current_position);
        sb.append("];");
        return sb.toString();            
    }     
    
    /** Return Fixed String Java source for byte[] as ROS multiple combines ROS int8. */ 
    public static String getFixedInt8Array(int start_position, int end_position)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for(int i=start_position;i<end_position;i++)
        {            
            sb.append("message[");
            sb.append(i);
            sb.append("]");
            // If end of array set i to message.length to end loop.
            if(i>=end_position)
            {
    //            i=message.length;
            }
            else
            {
                // append , to StringBuilder
                sb.append(", ");
            }
        }
        sb.append("};\n");
        return sb.toString();        
    }     
    
    /** Return Assigned Variable Java Source Code String byte[] as ROS multiple combines ROS int8[]. */ 
    public static String getAssignedVariableInt8Array(String assignment_variable, String current_position_variable)
    {
        return assignment_variable.concat("=").concat("ROSMessageDefinitionTypeConverter.getInt8Array(message, "+current_position_variable+");\n").concat(current_position_variable).concat((" = ").concat(current_position_variable).concat(" + 1;\n"));
    }     

    /** Return Assigned Fixed String Java source for byte[] as ROS multiple combines ROS int8. */ 
    public static String getAssignedFixedInt8Array(String assignment_variable, int start_position, int end_position)
    {
        StringBuilder sb = new StringBuilder();
        int zero_iterator=0;
        for(int i=start_position;i<end_position;i++)
        {      
            // Assignment code
            sb.append(assignment_variable);
            sb.append("[");
            sb.append(String.valueOf(zero_iterator));
            sb.append("]=");
            //            
            sb.append("message[");
            sb.append(i);
            sb.append("];\n");
            // increment zero_iterator 
            zero_iterator=zero_iterator + 1;
        }
        return sb.toString();        
    }     
    
    
    /** Return String Java source for short from ROS uint8. */
    public static String getFixedUInt8(int current_position)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("(short)(message[");
        sb.append(current_position);
        sb.append("] & (short)0xFF);");
        return sb.toString();         
    } 
    
    /** Return Assigned Variable String Java source for short from ROS uint8. */
    public static String getAssignedVariableUInt8(String assignment_variable, String current_position_variable)
    {
        StringBuilder sb = new StringBuilder();
        // Assignment code
        sb.append(assignment_variable);
        sb.append("=");
        //        
        sb.append("(short)(message[");
        sb.append(current_position_variable);
        sb.append("] & (short)0xFF);\n");
        // Set the current_position to the first int following this variable.
        sb.append(current_position_variable);
        sb.append(" = ");
        sb.append(current_position_variable);
        sb.append(" + 1;\n");        
        return sb.toString();         
    }      
    
    /** Return Assigned String Java source for short from ROS uint8. */
    public static String getAssignedFixedUInt8(String assignment_variable, int current_position)
    {
        StringBuilder sb = new StringBuilder();
        // Assignment code
        sb.append(assignment_variable);
        sb.append("=");
        //        
        sb.append("(short)(message[");
        sb.append(current_position);
        sb.append("] & (short)0xFF);");
        return sb.toString();         
    }     

    /** Return Fixed String Java source short[] as ROS multiple combines ROS uint8. */ 
    public static String getFixedUInt8Array(int start_position, int end_position)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for(int i=start_position;i<end_position;i++)
        {            
            sb.append("(short)(message[");
            sb.append(i);
            sb.append("] & (short)0xFF)");
            // If end of array set i to message.length to end loop.
            if(i>=end_position)
            {
  //              i=message.length;
            }
            else
            {
                // append , to StringBuilder
                sb.append(", ");
            }
        }
        sb.append("};\n");
        return sb.toString();            
    } 

    /** Return Assigned Variable Java Source Code String short[] as ROS multiple combines ROS uint8[]. */ 
    public static String getAssignedVariableUInt8Array(String assignment_variable, String current_position_variable)
    {
        return assignment_variable.concat("=").concat("ROSMessageDefinitionTypeConverter.getUInt8Array(message, "+current_position_variable+");\n").concat(current_position_variable).concat((" = ").concat(current_position_variable).concat(" + 1;\n"));
    }      
    
    /** Return Assigned Fixed String Java source short[] as ROS multiple combines ROS uint8. */ 
    public static String getAssignedFixedUInt8Array(String assignment_variable, int start_position, int end_position)
    {
        StringBuilder sb = new StringBuilder();
        int zero_iterator=0;
        for(int i=start_position;i<end_position;i++)
        {   
            // Assignment code
            sb.append(assignment_variable);
            sb.append("[");
            sb.append(String.valueOf(zero_iterator));
            sb.append("]=");
            //            
            sb.append("(short)(message[");
            sb.append(i);
            sb.append("] & (short)0xFF);\n");
            // increment zero_iterator 
            zero_iterator=zero_iterator + 1;
        }
        return sb.toString();            
    }     
    
    /** Return Assigned Header Java source for Header from ROS Header. */
    public static String getAssignedVariableHeader(String assignment_variable, String current_position_variable)
    {        
        // uint32 seq
        return ROSTypesToJavaSource.getAssignedVariableUInt32(assignment_variable.concat(".seq"), current_position_variable).concat(
        
        // time
        ROSTypesToJavaSource.getAssignedVariableUInt32(assignment_variable.concat(".stamp.secs"), current_position_variable).concat(ROSTypesToJavaSource.getAssignedVariableUInt32(assignment_variable.concat(".stamp.nsecs"), current_position_variable))).concat(
        
        // string frame_id
        
         assignment_variable.concat(".frame_id").concat("=").concat("ROSMessageDefinitionTypeConverter.getString(message, "+current_position_variable+");\n").concat("if (("+assignment_variable.concat(".frame_id")+".length() & 1) != 0){length=("+assignment_variable.concat(".frame_id")+".length()+1)/2;}else{length="+assignment_variable.concat(".frame_id")+".length()/2;}\n").concat(

                // check if is odd, and if so add 1 byte, and then .concat(current_position_variable).concat("=").concat(current_position_variable).concat("+"+ String.valueOf(assignment_variable.length/2) +";\n");            
                current_position_variable).concat("=").concat(current_position_variable).concat("+length;\n"));
    }      
    
    /** Return Assigned String Java source for String from ROS string. */
    public static String getAssignedVariableString(String assignment_variable, String current_position_variable)
    {
        String temp = assignment_variable.concat("=").concat("ROSMessageDefinitionTypeConverter.getString(message, "+current_position_variable+");\n").concat("if (("+assignment_variable+".length() & 1) != 0){length=("+assignment_variable+".length()+1)/2;}else{length="+assignment_variable+".length()/2;}\n");
        // check if is odd, and if so add 1 byte, and then .concat(current_position_variable).concat("=").concat(current_position_variable).concat("+"+ String.valueOf(assignment_variable.length/2) +";\n");
        return temp.concat(current_position_variable).concat("=").concat(current_position_variable).concat("+length;\n");
    }  

    /** Return Assigned String Java source for String from ROS string. */
    public static String getAssignedFixedStringArray(String assignment_variable, String current_position_variable, int size)
    {
        return assignment_variable.concat("=").concat("ROSMessageDefinitionTypeConverter.getFixedStringArray(message, "+current_position_variable+", " + String.valueOf(size) + ");\n").concat("int array_length=0;for(int i=0;i<").concat(assignment_variable).concat(".length;i++){array_length=array_length + ROSMessageDefinitionTypeConverter.getStringLength(").concat(assignment_variable).concat("[i]);}\n").concat(current_position_variable).concat("=").concat(current_position_variable).concat("+array_length;\n");
    }      

    /** Return the ROS string type length in bytes by Java String. */
    public int getStringLength(String assignment_variable)
    {
        int length;
        if ((assignment_variable.length() & 1) != 0)
        {
            length=(assignment_variable.length()+1)/2;
        }
        else
        {
            length=assignment_variable.length()/2;
        }
        return length;
    }
    
    /** Return Assigned Variable String Java source for String from ROS string. */
    public static String getAssignedVariableStringArray(String assignment_variable, String current_position_variable)
    {
        return assignment_variable.concat("=").concat("ROSMessageDefinitionTypeConverter.getVariableStringArray(message, "+current_position_variable+");\n").concat("int array_length=0;for(int i=0;i<").concat(assignment_variable).concat(".length;i++){array_length=array_length + ROSMessageDefinitionTypeConverter.getStringLength(").concat(assignment_variable).concat("[i]);}\n").concat(current_position_variable).concat("=").concat(current_position_variable).concat("+array_length;\n");
    }     
    
    @SuppressWarnings("empty-statement")
    public static void main(String[] args)
    {
        // byte 0=bool,1=bool, 2,3,4,5=float32, 6,7,8,9=float32, 10,11,12,13=float32, or 6-13=float64,14-21=float64
        byte[] message={0x00,0x01,0x32,0x33,0x34,0x35,0x22,0x00,0x00,0x6A,0x10,0x33,0x20,0x00,0x22,0x00,0x00,0x6A,0x10,0x33,0x20,0x00};
        // getFixedBool tests
        System.out.println("getFixedBool: "+getFixedBool(0));     
        boolean testbool=message[0]!=0;
        System.out.println("getFixedBool value test: " + testbool);
        // getFixedBool Array tests
        System.out.println("getFixedBoolArray: "+getFixedBoolArray(0,1));     
        boolean[] testboolarray={message[0]!=0, message[1]!=0};
        System.out.println("getFixedBoolArray value test: " + Arrays.toString(testboolarray));
        
        // getFixedFloat32 tests
        System.out.println("getFixedFloat32: "+getFixedFloat32(2));     
        float testfloat=Float.intBitsToFloat(((message[2] & 0xFF) << 24)
| ((message[3] & 0xFF) << 16)
| ((message[4] & 0xFF) << 8)
| ((message[5] & 0xFF) << 0));
        System.out.println("getFixedFloat32 value test: " + testfloat);
        // getFixedFloat32 Array tests
        float[] testfloatarray={
Float.intBitsToFloat(((message[6] & 0xFF) << 24)
| ((message[7] & 0xFF) << 16)
| ((message[8] & 0xFF) << 8)
| ((message[9] & 0xFF) << 0))
, Float.intBitsToFloat(((message[10] & 0xFF) << 24)
| ((message[11] & 0xFF) << 16)
| ((message[12] & 0xFF) << 8)
| ((message[13] & 0xFF) << 0))
};
        System.out.println("getFixedFloat32Array value test: "+Arrays.toString(testfloatarray));     
        
        // getFixedFloat64 tests
        System.out.println("getFixedFloat64: "+getFixedFloat64(6)); 
        double testdouble=Double.longBitsToDouble(((message[6] & 0xFFL) << 56)
| ((message[7] & 0xFFL) << 48)
| ((message[8] & 0xFFL) << 40)
| ((message[9] & 0xFFL) << 32)
| ((message[10] & 0xFFL) << 24)
| ((message[11] & 0xFFL) << 16)
| ((message[12] & 0xFFL) << 8)
| ((message[13] & 0xFFL) << 0));
        
        System.out.println("getFixedFloat64 value test: " + testdouble);
        System.out.println("getFixedFloat64Array: "+getFixedFloat64Array(6,21)); 
        double[] doublearray={
Double.longBitsToDouble(((message[6] & 0xFFL) << 56)
| ((message[7] & 0xFFL) << 48)
| ((message[8] & 0xFFL) << 40)
| ((message[9] & 0xFFL) << 32)
| ((message[10] & 0xFFL) << 24)
| ((message[11] & 0xFFL) << 16)
| ((message[12] & 0xFFL) << 8)
| ((message[13] & 0xFFL) << 0)), Double.longBitsToDouble(((message[14] & 0xFFL) << 56)
| ((message[15] & 0xFFL) << 48)
| ((message[16] & 0xFFL) << 40)
| ((message[17] & 0xFFL) << 32)
| ((message[18] & 0xFFL) << 24)
| ((message[19] & 0xFFL) << 16)
| ((message[20] & 0xFFL) << 8)
| ((message[21] & 0xFFL) << 0))};
        System.out.println("getFixedFloat64Array value test: " + Arrays.toString(doublearray));

        // getFixedInt16 tests
        System.out.println("getFixedInt16: "+getFixedInt16(6));
        short shortval=(short)((message[6]<<8) | (message[7]));
        System.out.println("getFixedInt16 value test: " + shortval);
        System.out.println("getFixedInt16Array: "+getFixedInt16Array(6,9));        
        short[] shortvalarray={(short)((message[6]<<8) | (message[7])), (short)((message[8]<<8) | (message[9]))};
        System.out.println("getFixedInt16Array value test: " + Arrays.toString(shortvalarray));

        // getFixedInt32 tests
        System.out.println("getFixedInt32: "+getFixedInt32(6));
        int intval= ((message[6]<<24) | (message[7]<<16) | (message[8]<<8) | (message[9]));
        System.out.println("getFixedInt32 value test: " + intval);
        System.out.println("getFixedInt32Array: "+getFixedInt32Array(6,13));        
        int[] intvalarray={((message[6]<<24) | (message[7]<<16) | (message[8]<<8) | (message[9])), ((message[10]<<24) | (message[11]<<16) | (message[12]<<8) | (message[13]))};
        System.out.println("getFixedInt32Array value test: " + Arrays.toString(intvalarray)); 
        
        // getFixedInt64 tests
        System.out.println("getFixedInt64: "+getFixedInt64(6));
        long longval=(long)((message[6]<<56) | (message[7]<<48) | (message[8]<<40) | (message[9]<<32) | (message[10]<<24) | (message[11]<<16) | (message[12]<<8) | (message[13]));
        System.out.println("getFixedInt64 value test: " + longval);
        System.out.println("getFixedInt64Array: "+getFixedInt64Array(6,21));        
        long[] longvalarray={(long)((message[6]<<56) | (message[7]<<48) | (message[8]<<40) | (message[9]<<32) | (message[10]<<24) | (message[11]<<16) | (message[12]<<8) | (message[13])), (long)((message[14]<<56) | (message[15]<<48) | (message[16]<<40) | (message[17]<<32) | (message[18]<<24) | (message[19]<<16) | (message[20]<<8) | (message[21]))};
        System.out.println("getFixedInt64Array value test: " + Arrays.toString(longvalarray));
        
        // getFixedInt8 tests
        System.out.println("getFixedInt8: "+getFixedInt8(6));
        byte byteval=message[6];
        System.out.println("getFixedInt8 value test: " + byteval);
        System.out.println("getFixedInt8Array: "+getFixedInt8Array(6,9));        
        byte[] bytevalarray={message[6], message[7], message[8], message[9]};
        System.out.println("getFixedInt8Array value test: " + Arrays.toString(bytevalarray));        
        
        // getFixedUInt16 tests
        System.out.println("getFixedUInt16: "+getFixedUInt16(6));
        intval= (int)(message[6] & 0xFF) | ((message[7] & 0xFF) << 8);
        System.out.println("getFixedUInt16 value test: " + intval);
        System.out.println("getFixedUInt16Array: "+getFixedUInt16Array(6,13));        
        int[] testintvalarray = 
                {
                    (int)(message[6] & 0xFF) | ((message[7] & 0xFF) << 8), 
                    (int)(message[8] & 0xFF) | ((message[9] & 0xFF) << 8), 
                    (int)(message[10] & 0xFF) | ((message[11] & 0xFF) << 8), 
                    (int)(message[12] & 0xFF) | ((message[13] & 0xFF) << 8)
                };
        System.out.println("getFixedUInt16Array value test: " + Arrays.toString(testintvalarray));
        

        // getFixedUInt32 tests
        System.out.println("getFixedUInt32: "+getFixedUInt32(6));
        long uintval= (long)((message[6]&0xFFL) << 24) | ((message[7] & 0xFFL) << 16) | (message[8] & 0xFFL) << 8 | ((message[9] & 0xFFL));
        System.out.println("getFixedUInt32 value test: " + intval);
        System.out.println("getFixedUInt32Array: "+getFixedUInt32Array(6,13));        
        long[] uintvalarray={(long)((message[6]&0xFFL) << 24) | ((message[7] & 0xFFL) << 16) | (message[8] & 0xFFL) << 8 | ((message[9] & 0xFFL)), (long)((message[10]&0xFFL) << 24) | ((message[11] & 0xFFL) << 16) | (message[12] & 0xFFL) << 8 | ((message[13] & 0xFFL))};
        System.out.println("getFixedUInt32Array value test: " + Arrays.toString(uintvalarray)); 
 
        // getFixedUInt64 tests
        System.out.println("getFixedUInt64: "+getFixedUInt64(6));
        long ulongval= (long)((message[6] & 0xFFL) << 56) | ((message[7] & 0xFFL) << 48) | ((message[8] & 0xFFL) << 40) | ((message[9] & 0xFFL) << 32) | ((message[10] & 0xFFL) << 24) | ((message[11] & 0xFFL) << 16) | (message[12] & 0xFFL) << 8 | ((message[13] & 0xFFL));
        System.out.println("getFixedUInt64 value test: " + ulongval);
        System.out.println("getFixedUInt64Array: "+getFixedUInt64Array(6,21));        
        long[] ulongvalarray={(long)((message[6] & 0xFFL) << 56) | ((message[7] & 0xFFL) << 48) | ((message[8] & 0xFFL) << 40) | ((message[9] & 0xFFL) << 32) | ((message[10] & 0xFFL) << 24) | ((message[11] & 0xFFL) << 16) | (message[12] & 0xFFL) << 8 | ((message[13] & 0xFFL)), (long)((message[14] & 0xFFL) << 56) | ((message[15] & 0xFFL) << 48) | ((message[16] & 0xFFL) << 40) | ((message[17] & 0xFFL) << 32) | ((message[18] & 0xFFL) << 24) | ((message[19] & 0xFFL) << 16) | (message[20] & 0xFFL) << 8 | ((message[21] & 0xFFL))};
        System.out.println("getFixedUInt64Array value test: " + Arrays.toString(ulongvalarray));
                
        // getFixedUInt8 tests
        System.out.println("getFixedUInt8: "+getFixedUInt8(6));
        short testushortval=(short)(message[6] & (short)0xFF);
        System.out.println("getFixedUInt8 value test: " + testushortval);
        System.out.println("getFixedUInt8Array: "+getFixedUInt8Array(6,9));        
        short[] testushortvalarray={(short)(message[6] & (short)0xFF), (short)(message[7] & (short)0xFF), (short)(message[8] & (short)0xFF), (short)(message[9] & (short)0xFF)};
        System.out.println("getFixedUInt8Array value test: " + Arrays.toString(testushortvalarray));                
        
                    

    }
    
    /** Get the int length of the ROS Message Prepend message/field length of message in ROS message byte[]. */
    public static int getByteArrayLengthInBytes(byte[] bytes,int current_position)
    {
        return ((bytes[current_position + 3] & 0xFF) << 24) | ((bytes[current_position + 2] & 0xFF) << 16)
    | ((bytes[current_position + 1] & 0xFF) << 8) | (bytes[current_position] & 0xFF);   
    }    
}

