package org.happy.artist.rmdmia.rcsm.providers.ros.client.message.generator;
import java.io.UnsupportedEncodingException;
import org.happy.artist.rmdmia.utilities.BytesToHex;
import org.happy.artist.rmdmia.utilities.HexStringConverter;
import org.happy.artist.rmdmia.utilities.HexToBytes;
/**
 * org.happy.artist.rmdmia.rcsm.providers.ros.client.message.generator.JavaToROSTypes 
 * is a ROS Message Types utility that converts Java types to ROS 
 * Message Types.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2014-2015 Happy Artist. All rights reserved.
 */
public class JavaToROSTypesSource 
{
    private static HexStringConverter hexStringConverter = HexStringConverter.getHexStringConverterInstance();
    
    /** Return ROS Hex String from Java String. */
    public static String getHexString(String value) throws UnsupportedEncodingException
    {
        return hexStringConverter.stringToHex(value);
    }
    
       
    
    /** Return ROS String as byte[]. */
    public static byte[] getString(String value) throws UnsupportedEncodingException
    {
        return HexToBytes.hexToBytes(hexStringConverter.stringToHex(value));
    }       
    
    /** Return ROS Hex Float32. */
    public static String getHexFloat32(float value)
    {
        return Float.toHexString(value);
    }
    
    /** Return ROS float32 as byte[]. */
    public static byte[] getFloat32(float value)
    {
        final byte[] float32Bytes = new byte[4];
        int float32Int=Float.floatToRawIntBits(value);
        float32Bytes[0] = (byte)((float32Int >> 24) & 0xff);
        float32Bytes[1] = (byte)((float32Int >> 16) & 0xff);
        float32Bytes[2] = (byte)((float32Int >> 8) & 0xff);
        float32Bytes[3] = (byte)((float32Int >> 0) & 0xff);
        return float32Bytes;
    }       
    
    /** Return Java float[] as ROS multiple combines ROS float32s. */ 
    public static byte[] getFloat32Array(float[] values)
    {
        final byte[] float32Bytes = new byte[4*values.length];
        int float32Int;
        for(int i=0;i<values.length;i++)
        {
            // get the float as an int
            float32Int=Float.floatToRawIntBits(values[i]);
            // Assign each byte to the float32Bytes[]
            float32Bytes[(4*values.length)+0] = (byte)((float32Int >> 24) & 0xff);
            float32Bytes[(4*values.length)+1] = (byte)((float32Int >> 16) & 0xff);
            float32Bytes[(4*values.length)+2] = (byte)((float32Int >> 8) & 0xff);
            float32Bytes[(4*values.length)+3] = (byte)((float32Int >> 0) & 0xff);        
        }
        return float32Bytes;
    }    
    
     /** Return byte[] of Fixed float32 from Java float input parameter. */
    public static String getFixedFloat32(int current_position, String instruction_variable)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("this.float32Int=Float.floatToRawIntBits(");
        sb.append(instruction_variable);
        sb.append(");\n");
        sb.append("ros_msg[");
        sb.append(current_position);
        sb.append("] = (byte)((float32Int >> 24) & 0xff);\n");
        sb.append("ros_msg[");
        sb.append(current_position+1);
        sb.append("] = (byte)((float32Int >> 16) & 0xff);\n");
        sb.append("ros_msg[");
        sb.append(current_position+2);
        sb.append("] = (byte)((float32Int >> 8) & 0xff);\n");
        sb.append("ros_msg[");
        sb.append(current_position+3);
        sb.append("] = (byte)((float32Int >> 0) & 0xff);\n");        
        return sb.toString();  
    }      
    
    /** Return Java Source Code converting a Fixed Java float[] to ROS float32[] in byte[]. */ 
    public static String getFixedFloat32Array(int start_position, int end_position, String instruction_variable)
    {
        StringBuilder sb = new StringBuilder();        
        int j=0;                  
        for(int i=start_position;i<end_position;i++)
        {              
            // get the float as an int
            sb.append("this.float32Int=Float.floatToRawIntBits(");
            sb.append(instruction_variable);
            sb.append("[");
            sb.append(j);
            sb.append("]);\n");
            // Assign each byte to the ros_msg byte[]
            sb.append("ros_msg[");
            sb.append(i);
            sb.append("] = (byte)((float32Int >> 24) & 0xff);\n");
            sb.append("ros_msg[");
            sb.append(i=i+1);
            sb.append("] = (byte)((float32Int >> 16) & 0xff);\n");
            sb.append("ros_msg[");
            sb.append(i=i+1);
            sb.append("] = (byte)((float32Int >> 8) & 0xff);\n");
            sb.append("ros_msg[");
            sb.append(i=i+1);
            sb.append("] = (byte)((float32Int >> 0) & 0xff);\n");    
            j=j+1;
        }
        return sb.toString();        
    }
    
    /** Return Java Source Code converting a Java float[] to ROS variable float32[] in byte[]. */public static String getVariableFloat32(String current_position_variable, String instruction_variable)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("this.float32Int=Float.floatToRawIntBits(");
        sb.append(instruction_variable);
        sb.append(");\n");
        sb.append("ros_msg[");
        sb.append(current_position_variable);
        sb.append("] = (byte)((float32Int >> 24) & 0xff);\n");
        sb.append("ros_msg[");
        sb.append(current_position_variable.concat("+1"));
        sb.append("] = (byte)((float32Int >> 16) & 0xff);\n");
        sb.append("ros_msg[");
        sb.append(current_position_variable.concat("+2"));
        sb.append("] = (byte)((float32Int >> 8) & 0xff);\n");
        sb.append("ros_msg[");
        sb.append(current_position_variable.concat("+3"));
        sb.append("] = (byte)((float32Int >> 0) & 0xff);\n");   
        // Set the current_position to the first int following this variable.
        sb.append(current_position_variable);
        sb.append(" = ");
        sb.append(current_position_variable);
        sb.append(" + 4;");
        //          
        return sb.toString();  
    }          

            
    /** Return Java Source Code converting a Java float[] to a variable ROS float32[] in byte[]. */ 
    public static String getVariableFloat32Array(String instruction_variable, String current_position_variable)
    {
        StringBuilder sb = new StringBuilder();        
        // set the array length in bytes for setting the prefix bytes
        sb.append("this.array_length=");
        sb.append("4*"+instruction_variable+".length;\n");
        // Set the array length prefix bytes
        sb.append("ros_msg["+current_position_variable+"="+current_position_variable+"+3] = (byte)((array_length >>> 24) & 0xff);\n");
        sb.append("ros_msg["+current_position_variable+"="+current_position_variable+"+2] = (byte)((array_length >>> 16) & 0xff);\n");  
        sb.append("ros_msg["+current_position_variable+"="+current_position_variable+"+1] = (byte)((array_length >>> 8) & 0xff);\n");  
        sb.append("ros_msg["+current_position_variable+"] = (byte)((array_length >>> 0) & 0xff);\n");
        // Iterate to the next current_position
        sb.append(current_position_variable+"="+current_position_variable+"+1;\n");
        // Loop through float[]
        sb.append("j=0;\n");        
        sb.append("for(int i="+current_position_variable+";i<"+instruction_variable+".length;i++)\n");
        sb.append("{\n");              
        // get the float as an int
        sb.append("this.float32Int=Float.floatToRawIntBits(");
        sb.append(instruction_variable);
        sb.append("[j]);\n");
        // Assign each byte to the ros_msg byte[]
        sb.append("ros_msg[i] = (byte)((float32Int >> 24) & 0xff);\n");
        sb.append("ros_msg[i=i+1] = (byte)((float32Int >> 16) & 0xff);\n");
        sb.append("ros_msg[i=i+1] = (byte)((float32Int >> 8) & 0xff);\n");
        sb.append("ros_msg[i=i+1] = (byte)((float32Int >> 0) & 0xff);\n");
        sb.append("j=j+1;\n");
        sb.append("}\n");
        
        // Set the current position before proceeding...
        sb.append(current_position_variable+"="+current_position_variable+"+(4*"+instruction_variable+".length);\n");
        return sb.toString();        
    }    
    
    /** Return ROS Hex Float64. */
    public static String getHexFloat64(double value)
    {
        return Double.toHexString(value);
    }

    /** Return Java Source Code for byte[] of Fixed float64 from Java float input parameter. */public static String getFixedFloat64(int current_position, String instruction_variable)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("this.float64Long=Double.doubleToLongBits(");
        sb.append(instruction_variable);
        sb.append(");\n");
        sb.append("ros_msg[");
        sb.append(current_position);
        sb.append("] = (byte)((float64Long >> 56) & 0xffL);\n");
        sb.append("ros_msg[");
        sb.append(current_position+1);
        sb.append("] = (byte)((float64Long >> 48) & 0xffL);\n");
        sb.append("ros_msg[");
        sb.append(current_position+2);
        sb.append("] = (byte)((float64Long >> 40) & 0xffL);\n");
        sb.append("ros_msg[");
        sb.append(current_position+3);
        sb.append("] = (byte)((float64Long >> 32) & 0xffL);\n");        
        sb.append("ros_msg[");
        sb.append(current_position+4);
        sb.append("] = (byte)((float64Long >> 24) & 0xffL);\n");
        sb.append("ros_msg[");
        sb.append(current_position+5);
        sb.append("] = (byte)((float64Long >> 16) & 0xffL);\n");
        sb.append("ros_msg[");
        sb.append(current_position+6);
        sb.append("] = (byte)((float64Long >> 8) & 0xffL);\n");
        sb.append("ros_msg[");
        sb.append(current_position+7);
        sb.append("] = (byte)((float64Long >> 0) & 0xffL);\n");        
        return sb.toString();  
    }      
    
    /** Return Java Source Code converting a Fixed Java float[] to ROS float64[] in byte[]. */ 
    public static String getFixedFloat64Array(int start_position, int end_position, String instruction_variable)
    {
        StringBuilder sb = new StringBuilder();        
        int j=0;        
        for(int i=start_position;i<end_position;i++)
        {              
            // get the float as an int
            sb.append("this.float64Long=Double.doubleToLongBits(");
            sb.append(instruction_variable);
            sb.append("[");
            sb.append(j);
            sb.append("]);\n");
            // Assign each byte to the ros_msg byte[]
            sb.append("ros_msg[");
            sb.append(i);
            sb.append("] = (byte)((float64Long >> 56) & 0xffL);\n");
            sb.append("ros_msg[");
            sb.append(i=i+1);
            sb.append("] = (byte)((float64Long >> 48) & 0xffL);\n");
            sb.append("ros_msg[");
            sb.append(i=i+1);
            sb.append("] = (byte)((float64Long >> 40) & 0xffL);\n");
            sb.append("ros_msg[");
            sb.append(i=i+1);
            sb.append("] = (byte)((float64Long >> 32) & 0xffL);\n");                  
            sb.append("ros_msg[");
            sb.append(i=i+1);
            sb.append("] = (byte)((float64Long >> 24) & 0xffL);\n");
            sb.append("ros_msg[");
            sb.append(i=i+1);
            sb.append("] = (byte)((float64Long >> 16) & 0xffL);\n");
            sb.append("ros_msg[");
            sb.append(i=i+1);
            sb.append("] = (byte)((float64Long >> 8) & 0xffL);\n");
            sb.append("ros_msg[");
            sb.append(i=i+1);
            sb.append("] = (byte)((float64Long >> 0) & 0xffL);\n");        
            j=j+1;
        }
        return sb.toString();        
    }
    
    /** Return Java Source Code converting a Java float[] to ROS variable float64[] in byte[]. */public static String getVariableFloat64(String current_position_variable, String instruction_variable)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("this.float64Long=Double.doubleToLongBits(");
        sb.append(instruction_variable);
        sb.append(");\n");
        sb.append("ros_msg[");
        sb.append(current_position_variable);
        sb.append("] = (byte)((float64Long >> 56) & 0xffL);\n");
        sb.append("ros_msg[");
        sb.append(current_position_variable.concat("+1"));
        sb.append("] = (byte)((float64Long >> 48) & 0xffL);\n");
        sb.append("ros_msg[");
        sb.append(current_position_variable.concat("+2"));
        sb.append("] = (byte)((float64Long >> 40) & 0xffL);\n");
        sb.append("ros_msg[");
        sb.append(current_position_variable.concat("+3"));
        sb.append("] = (byte)((float64Long >> 32) & 0xffL);\n");             
        sb.append("ros_msg[");
        sb.append(current_position_variable.concat("+4"));
        sb.append("] = (byte)((float64Long >> 24) & 0xffL);\n");
        sb.append("ros_msg[");
        sb.append(current_position_variable.concat("+5"));
        sb.append("] = (byte)((float64Long >> 16) & 0xffL);\n");
        sb.append("ros_msg[");
        sb.append(current_position_variable.concat("+6"));
        sb.append("] = (byte)((float64Long >> 8) & 0xffL);\n");
        sb.append("ros_msg[");
        sb.append(current_position_variable.concat("+7"));
        sb.append("] = (byte)((float64Long >> 0) & 0xffL);\n");   
        // Set the current_position to the first int following this variable.
        sb.append(current_position_variable);
        sb.append(" = ");
        sb.append(current_position_variable);
        sb.append(" + 8;");
        //          
        return sb.toString();  
    }              

    /** Return Java Source Code converting a Java float[] to a variable ROS float64[] in byte[]. */ 
    public static String getVariableFloat64Array(String instruction_variable, String current_position_variable)
    {
        StringBuilder sb = new StringBuilder();        
        // set the array length in bytes for setting the prefix bytes
        sb.append("this.array_length=");
        sb.append("8*"+instruction_variable+".length;\n");
        // Set the array length prefix bytes
        sb.append("ros_msg["+current_position_variable+"="+current_position_variable+"+3] = (byte)((array_length >>> 24) & 0xff);\n");
        sb.append("ros_msg["+current_position_variable+"="+current_position_variable+"+2] = (byte)((array_length >>> 16) & 0xff);\n");  
        sb.append("ros_msg["+current_position_variable+"="+current_position_variable+"+1] = (byte)((array_length >>> 8) & 0xff);\n");  
        sb.append("ros_msg["+current_position_variable+"] = (byte)((array_length >>> 0) & 0xff);\n");
        // Iterate to the next current_position
        sb.append(current_position_variable+"="+current_position_variable+"+1;\n");
        // Loop through float[]
        sb.append("j=0;\n");
        sb.append("for(int i="+current_position_variable+";i<"+instruction_variable+".length;i++)\n");
        sb.append("{\n");              
        // get the float as an long
        sb.append("this.float64Long=Double.doubleToLongBits(");
        sb.append(instruction_variable);
        sb.append("[j]);\n");
        // Assign each byte to the ros_msg byte[]
        sb.append("ros_msg[i] = (byte)((float64Long >> 56) & 0xffL);\n");
        sb.append("ros_msg[i=i+1] = (byte)((float64Long >> 48) & 0xffL);\n");
        sb.append("ros_msg[i=i+1] = (byte)((float64Long >> 40) & 0xffL);\n");
        sb.append("ros_msg[i=i+1] = (byte)((float64Long >> 32) & 0xffL);\n");        
        sb.append("ros_msg[i=i+1] = (byte)((float64Long >> 24) & 0xffL);\n");
        sb.append("ros_msg[i=i+1] = (byte)((float64Long >> 16) & 0xffL);\n");
        sb.append("ros_msg[i=i+1] = (byte)((float64Long >> 8) & 0xffL);\n");
        sb.append("ros_msg[i=i+1] = (byte)((float64Long >> 0) & 0xffL);\n");     
        sb.append("j=j+1;\n");
        sb.append("}\n");
        
        // Set the current position before proceeding...
        sb.append(current_position_variable+"="+current_position_variable+"+(8*"+instruction_variable+".length);\n");
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

    /** Return ROS Hex int64. */
    public static String getHexInt64(long value)
    {       
        return Long.toHexString((value >> 0) & 0xffffffff);
    }    

    
    /** Return ROS int64 as byte[] (Big Endian). */
    public static byte[] getInt64(long value)
    {
        final byte[] int64Bytes = new byte[8];
        int64Bytes[0] = (byte)((value >> 56) & 0xff);
        int64Bytes[1] = (byte)((value >> 48) & 0xff);  
        int64Bytes[2] = (byte)((value >> 40) & 0xff);  
        int64Bytes[3] = (byte)((value >> 32) & 0xff);
        int64Bytes[4] = (byte)((value >> 24) & 0xff);
        int64Bytes[5] = (byte)((value >> 16) & 0xff);  
        int64Bytes[6] = (byte)((value >> 8) & 0xff);  
        int64Bytes[7] = (byte)((value >> 0) & 0xff);         
        return int64Bytes;
    }     
    
    /** Return Java Source Code for byte[] of Fixed int64 from Java long input parameter. */
    public static String getFixedInt64(int current_position, String instruction_variable)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("ros_msg[");
        sb.append(current_position);
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >> 56) & 0xffL);\n");
        sb.append("ros_msg[");
        sb.append(current_position+1);
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >> 48) & 0xffL);\n");
        sb.append("ros_msg[");
        sb.append(current_position+2);
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >> 40) & 0xffL);\n");
        sb.append("ros_msg[");
        sb.append(current_position+3);
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >> 32) & 0xffL);\n");        
        sb.append("ros_msg[");
        sb.append(current_position+4);
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >> 24) & 0xffL);\n");
        sb.append("ros_msg[");
        sb.append(current_position+5);
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >> 16) & 0xffL);\n");
        sb.append("ros_msg[");
        sb.append(current_position+6);
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >> 8) & 0xffL);\n");
        sb.append("ros_msg[");
        sb.append(current_position+7);
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >> 0) & 0xffL);\n");        
        return sb.toString();  
    }      

    /** Return Java Source Code for byte[] of Variable int64 from Java long input parameter. */
    public static String getVariableInt64(String current_position_variable, String instruction_variable)
    {
        StringBuilder sb = new StringBuilder();

        sb.append("ros_msg[");
        sb.append(current_position_variable);
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >> 56) & 0xffL);\n");
        sb.append("ros_msg[");
        sb.append(current_position_variable.concat("+1"));
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >> 48) & 0xffL);\n");
        sb.append("ros_msg[");
        sb.append(current_position_variable.concat("+2"));
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >> 40) & 0xffL);\n");
        sb.append("ros_msg[");
        sb.append(current_position_variable.concat("+3"));
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >> 32) & 0xffL);\n");             
        sb.append("ros_msg[");
        sb.append(current_position_variable.concat("+4"));
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >> 24) & 0xffL);\n");
        sb.append("ros_msg[");
        sb.append(current_position_variable.concat("+5"));
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >> 16) & 0xffL);\n");
        sb.append("ros_msg[");
        sb.append(current_position_variable.concat("+6"));
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >> 8) & 0xffL);\n");
        sb.append("ros_msg[");
        sb.append(current_position_variable.concat("+7"));
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >> 0) & 0xffL);\n");   
        // Set the current_position to the first int following this variable.
        sb.append(current_position_variable);
        sb.append(" = ");
        sb.append(current_position_variable);
        sb.append(" + 8;");
        //          
        return sb.toString();  
    }     
    
    /** Return Java Source Code converting a Fixed Java float[] to ROS int64[] in byte[]. */ 
    public static String getFixedInt64Array(int start_position, int end_position, String instruction_variable)
    {
        StringBuilder sb = new StringBuilder();        
        int j=0;                  
        for(int i=start_position;i<end_position;i++)
        {              
            // get the long array element
            sb.append("this.int64Long=");
            sb.append(instruction_variable);
            sb.append("[");
            sb.append(j);
            sb.append("];\n");            
            // Assign each byte to the ros_msg byte[]
            sb.append("ros_msg[");
            sb.append(i);
            sb.append("] = (byte)((");
            sb.append(instruction_variable);
            sb.append("[");
            sb.append(i);
            sb.append("]) >> 56) & 0xffL);\n");
            sb.append("ros_msg[");
            sb.append(i+1);
            sb.append("] = (byte)((int64Long >> 48) & 0xffL);\n");
            sb.append("ros_msg[");
            sb.append(i+2);
            sb.append("] = (byte)((int64Long >> 40) & 0xffL);\n");
            sb.append("ros_msg[");
            sb.append(i+3);
            sb.append("] = (byte)((int64Long >> 32) & 0xffL);\n");                  
            sb.append("ros_msg[");
            sb.append(i+4);
            sb.append("] = (byte)((int64Long >> 24) & 0xffL);\n");
            sb.append("ros_msg[");
            sb.append(i+5);
            sb.append("] = (byte)((int64Long >> 16) & 0xffL);\n");
            sb.append("ros_msg[");
            sb.append(i+6);
            sb.append("] = (byte)((int64Long >> 8) & 0xffL);\n");
            sb.append("ros_msg[");
            sb.append(i+7);
            sb.append("] = (byte)((int64Long >> 0) & 0xffL);\n");   
            j=j+1;
        }
        return sb.toString();        
    }    

    /** Return Java Source Code converting a Java long[] to a variable ROS int64[] in byte[]. */ 
    public static String getVariableInt64Array(String instruction_variable, String current_position_variable)
    {
        StringBuilder sb = new StringBuilder();        
        // set the array length in bytes for setting the prefix bytes
        sb.append("this.array_length=");
        sb.append("8*"+instruction_variable+".length;\n");
        // Set the array length prefix bytes
        sb.append("ros_msg["+current_position_variable+"="+current_position_variable+"+3] = (byte)((array_length >>> 24) & 0xff);\n");
        sb.append("ros_msg["+current_position_variable+"="+current_position_variable+"+2] = (byte)((array_length >>> 16) & 0xff);\n");  
        sb.append("ros_msg["+current_position_variable+"="+current_position_variable+"+1] = (byte)((array_length >>> 8) & 0xff);\n");  
        sb.append("ros_msg["+current_position_variable+"] = (byte)((array_length >>> 0) & 0xff);\n");
        // Iterate to the next current_position
        sb.append(current_position_variable+"="+current_position_variable+"+1;\n");
        // Loop through float[]
        sb.append("j=0;\n");
        sb.append("for(int i="+current_position_variable+";i<"+instruction_variable+".length;i++)\n");
        sb.append("{\n");              
        // get the long array element
        sb.append("this.int64Long=");
        sb.append(instruction_variable);
        sb.append("[j];\n");      
        
        // Assign each byte to the ros_msg byte[]
        sb.append("ros_msg[i] = (byte)((int64Long >> 56) & 0xffL);\n");
        sb.append("ros_msg[i=i+1] = (byte)((int64Long >> 48) & 0xffL);\n");
        sb.append("ros_msg[i=i+1] = (byte)((int64Long >> 40) & 0xffL);\n");
        sb.append("ros_msg[i=i+1] = (byte)((int64Long >> 32) & 0xffL);\n");        
        sb.append("ros_msg[i=i+1] = (byte)((int64Long >> 24) & 0xffL);\n");
        sb.append("ros_msg[i=i+1] = (byte)((int64Long >> 16) & 0xffL);\n");
        sb.append("ros_msg[i=i+1] = (byte)((int64Long >> 8) & 0xffL);\n");
        sb.append("ros_msg[i=i+1] = (byte)((int64Long >> 0) & 0xffL);\n");
        sb.append("j=j+1;\n");
        sb.append("}\n");
        
        // Set the current position before proceeding...
        sb.append(current_position_variable+"="+current_position_variable+"+(8*"+instruction_variable+".length);\n");
        return sb.toString();        
    }        

    /** Return ROS uint64 as byte[] (Big Endian). */
    public static byte[] getUInt64(long value)
    {
        final byte[] int64Bytes = new byte[8];
        int64Bytes[0] = (byte)((value >>> 56) & 0xff);
        int64Bytes[1] = (byte)((value >>> 48) & 0xff);  
        int64Bytes[2] = (byte)((value >>> 40) & 0xff);  
        int64Bytes[3] = (byte)((value >>> 32) & 0xff);
        int64Bytes[4] = (byte)((value >>> 24) & 0xff);
        int64Bytes[5] = (byte)((value >>> 16) & 0xff);  
        int64Bytes[6] = (byte)((value >>> 8) & 0xff);  
        int64Bytes[7] = (byte)((value >>> 0) & 0xff);         
        return int64Bytes;
    }     

    /** Return Java Source Code for byte[] of Fixed uint64 from Java long input parameter. */
    public static String getFixedUInt64(int current_position, String instruction_variable)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("ros_msg[");
        sb.append(current_position);
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >>> 56) & 0xffL);\n");
        sb.append("ros_msg[");
        sb.append(current_position+1);
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >>> 48) & 0xffL);\n");
        sb.append("ros_msg[");
        sb.append(current_position+2);
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >>> 40) & 0xffL);\n");
        sb.append("ros_msg[");
        sb.append(current_position+3);
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >>> 32) & 0xffL);\n");        
        sb.append("ros_msg[");
        sb.append(current_position+4);
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >>> 24) & 0xffL);\n");
        sb.append("ros_msg[");
        sb.append(current_position+5);
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >>> 16) & 0xffL);\n");
        sb.append("ros_msg[");
        sb.append(current_position+6);
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >>> 8) & 0xffL);\n");
        sb.append("ros_msg[");
        sb.append(current_position+7);
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >>> 0) & 0xffL);\n");        
        return sb.toString();  
    }      

    /** Return Java Source Code for byte[] of Variable uint64 from Java long input parameter. */
    public static String getVariableUInt64(String current_position_variable, String instruction_variable)
    {
        StringBuilder sb = new StringBuilder();

        sb.append("ros_msg[");
        sb.append(current_position_variable);
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >>> 56) & 0xffL);\n");
        sb.append("ros_msg[");
        sb.append(current_position_variable.concat("+1"));
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >>> 48) & 0xffL);\n");
        sb.append("ros_msg[");
        sb.append(current_position_variable.concat("+2"));
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >>> 40) & 0xffL);\n");
        sb.append("ros_msg[");
        sb.append(current_position_variable.concat("+3"));
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >>> 32) & 0xffL);\n");             
        sb.append("ros_msg[");
        sb.append(current_position_variable.concat("+4"));
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >>> 24) & 0xffL);\n");
        sb.append("ros_msg[");
        sb.append(current_position_variable.concat("+5"));
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >>> 16) & 0xffL);\n");
        sb.append("ros_msg[");
        sb.append(current_position_variable.concat("+6"));
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >>> 8) & 0xffL);\n");
        sb.append("ros_msg[");
        sb.append(current_position_variable.concat("+7"));
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >>> 0) & 0xffL);\n");   
        // Set the current_position to the first int following this variable.
        sb.append(current_position_variable);
        sb.append(" = ");
        sb.append(current_position_variable);
        sb.append(" + 8;");
        //          
        return sb.toString();  
    }     
    
    /** Return Java Source Code converting a Fixed Java long[] to ROS uint64[] in byte[]. */ 
    public static String getFixedUInt64Array(int start_position, int end_position, String instruction_variable)
    {
        StringBuilder sb = new StringBuilder();        
        int j=0;                  
        for(int i=start_position;i<end_position;i++)
        {              
            // get the long array element
            sb.append("this.int64Long=");
            sb.append(instruction_variable);
            sb.append("[");
            sb.append(j);
            sb.append("];\n");            
            // Assign each byte to the ros_msg byte[]
            sb.append("ros_msg[");
            sb.append(i);
            sb.append("] = (byte)((");
            sb.append(instruction_variable);
            sb.append("[");
            sb.append(i);
            sb.append("]) >>> 56) & 0xffL);\n");
            sb.append("ros_msg[");
            sb.append(i+1);
            sb.append("] = (byte)((int64Long >>> 48) & 0xffL);\n");
            sb.append("ros_msg[");
            sb.append(i+2);
            sb.append("] = (byte)((int64Long >>> 40) & 0xffL);\n");
            sb.append("ros_msg[");
            sb.append(i+3);
            sb.append("] = (byte)((int64Long >>> 32) & 0xffL);\n");                  
            sb.append("ros_msg[");
            sb.append(i+4);
            sb.append("] = (byte)((int64Long >>> 24) & 0xffL);\n");
            sb.append("ros_msg[");
            sb.append(i+5);
            sb.append("] = (byte)((int64Long >>> 16) & 0xffL);\n");
            sb.append("ros_msg[");
            sb.append(i+6);
            sb.append("] = (byte)((int64Long >>> 8) & 0xffL);\n");
            sb.append("ros_msg[");
            sb.append(i+7);
            sb.append("] = (byte)((int64Long  0) & 0xffL);\n");   
            j=j+1;
        }
        return sb.toString();        
    }    

    /** Return Java Source Code converting a Java float[] to a variable ROS uint64[] in byte[]. */ 
    public static String getVariableUInt64Array(String instruction_variable, String current_position_variable)
    {
        StringBuilder sb = new StringBuilder();        
        // set the array length in bytes for setting the prefix bytes
        sb.append("this.array_length=");
        sb.append("8*"+instruction_variable+".length;\n");
        // Set the array length prefix bytes
        sb.append("ros_msg["+current_position_variable+"="+current_position_variable+"+3] = (byte)((array_length >>> 24) & 0xff);\n");
        sb.append("ros_msg["+current_position_variable+"="+current_position_variable+"+2] = (byte)((array_length >>> 16) & 0xff);\n");  
        sb.append("ros_msg["+current_position_variable+"="+current_position_variable+"+1] = (byte)((array_length >>> 8) & 0xff);\n");  
        sb.append("ros_msg["+current_position_variable+"] = (byte)((array_length >>> 0) & 0xff);\n");
        // Iterate to the next current_position
        sb.append(current_position_variable+"="+current_position_variable+"+1;\n");
        // Loop through float[]
        sb.append("j=0;\n");
        sb.append("for(int i="+current_position_variable+";i<"+instruction_variable+".length;i++)\n");
        sb.append("{\n");              
        // get the long array element
        sb.append("this.int64Long=");
        sb.append(instruction_variable);
        sb.append("[j];\n");      
        
        // Assign each byte to the ros_msg byte[]
        sb.append("ros_msg[i] = (byte)((int64Long >>> 56) & 0xffL);\n");
        sb.append("ros_msg[i=i+1] = (byte)((int64Long >>> 48) & 0xffL);\n");
        sb.append("ros_msg[i=i+1] = (byte)((int64Long >>> 40) & 0xffL);\n");
        sb.append("ros_msg[i=i+1] = (byte)((int64Long >>> 32) & 0xffL);\n");        
        sb.append("ros_msg[i=i+1] = (byte)((int64Long >>> 24) & 0xffL);\n");
        sb.append("ros_msg[i=i+1] = (byte)((int64Long >>> 16) & 0xffL);\n");
        sb.append("ros_msg[i=i+1] = (byte)((int64Long >>> 8) & 0xffL);\n");
        sb.append("ros_msg[i=i+1] = (byte)((int64Long >>> 0) & 0xffL);\n");
        sb.append("j=j+1;\n");
        sb.append("}\n");
        
        // Set the current position before proceeding...
        sb.append(current_position_variable+"="+current_position_variable+"+(8*"+instruction_variable+".length);\n");
        return sb.toString();        
    }      
    
    /** Return ROS bool as byte[]. */  
    public static byte getBool(boolean value)
    {
        return (byte)(((short)(value?1:0) >>> 0) & 0xff);       
    }            
            
    /** Return String Fixed Java source for boolean from ROS bool as byte. */  
    public static String getFixedBool(int current_position, String instruction_variable)
    {
        StringBuilder sb=new StringBuilder();  
        sb.append("ros_msg[");
        sb.append(current_position);
        sb.append("]=(byte)(((short)(");
        sb.append(instruction_variable);
        sb.append("?1:0) >>> 0) & 0xff);");  
        return sb.toString();      
    }

    
    /** Return Java Source Code converting a Java boolean to a variable ROS bool in byte. */ 
    public static String getVariableBool(String instruction_variable, String current_position_variable)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("ros_msg[");
        sb.append(current_position_variable);
        sb.append("]=(byte)(((short)(");
        sb.append(instruction_variable);
        sb.append("?1:0) >>> 0) & 0xff);"); 
        // Set the current_position to the first int following this variable.
        sb.append(current_position_variable);
        sb.append(" = ");
        sb.append(current_position_variable);
        sb.append(" + 1;");
        //          
        return sb.toString();  
    }      
    
    /** Return Fixed String Java source for boolean[] from ROS bool[] as byte[]. */ 
    public static String getFixedBoolArray(int start_position, int end_position, String instruction_variable)
    {
        StringBuilder sb = new StringBuilder();        
        int j=0;                  
        for(int i=start_position;i<end_position;i++)
        {              
            // get the short array element
            sb.append("this.booleanByte=");
            sb.append(instruction_variable);
            sb.append("[");
            sb.append(j);
            sb.append("];\n");            
            // Assign each byte to the ros_msg byte[]
            sb.append("ros_msg[");
            sb.append(i);
            sb.append("]=(byte)(((short)(booleanByte?1:0) >>> 0) & 0xff);\n");  
            j=j+1;
        }
        return sb.toString();     
    }     
    
    /** Return Java Source Code converting a Java boolean[] to a variable ROS bool[] in byte[]. */ 
    public static String getVariableBoolArray(String instruction_variable, String current_position_variable)
    {
        StringBuilder sb = new StringBuilder();        
        // set the array length in bytes for setting the prefix bytes
        sb.append("this.array_length=");
        sb.append(instruction_variable+".length;\n");
        // Set the array length prefix bytes
        sb.append("ros_msg["+current_position_variable+"="+current_position_variable+"+3] = (byte)((array_length >>> 24) & 0xff);\n");
        sb.append("ros_msg["+current_position_variable+"="+current_position_variable+"+2] = (byte)((array_length >>> 16) & 0xff);\n");  
        sb.append("ros_msg["+current_position_variable+"="+current_position_variable+"+1] = (byte)((array_length >>> 8) & 0xff);\n");  
        sb.append("ros_msg["+current_position_variable+"] = (byte)((array_length >>> 0) & 0xff);\n");
        // Iterate to the next current_position
        sb.append(current_position_variable+"="+current_position_variable+"+1;\n");
        // Loop through float[]
        sb.append("j=0;\n");
        sb.append("for(int i="+current_position_variable+";i<"+instruction_variable+".length;i++)\n");
        sb.append("{\n");              
        // get the long array element

        sb.append("this.booleanByte=");
        sb.append(instruction_variable);
        sb.append("[j];\n");       
        sb.append("ros_msg[i]=(byte)(((short)(booleanByte?1:0) >>> 0) & 0xff);\n");         
        sb.append("j=j+1;\n");
        sb.append("}\n");
        // Set the current position before proceeding...
        sb.append(current_position_variable+"="+current_position_variable+"+("+instruction_variable+".length);\n");
        return sb.toString();  
    }        
    
    
   /** Return ROS Hex bool. */
    public static String getHexBool(boolean value)
    {
        return Integer.toHexString(((short)(value?1:0) >>> 0) & 0xff);
    }
    
    /** Return Java boolean[] as ROS multiple combines ROS bool. */ 
    public static byte[] getBoolArray(boolean[] values)
    {
        final byte[] int8Bytes = new byte[values.length];
        for(int i=0;i<values.length;i++)
        {
            // Assign each byte to the int8Bytes[]
            int8Bytes[i] = (byte)(((short)(values[i]?1:0) >>> 0) & 0xff);
        }
        return int8Bytes;
    }     
     
    /** Return Java long[] as ROS multiple combines ROS int64. */ 
    public static byte[] getInt64Array(long[] values)
    {
        final byte[] int64Bytes = new byte[8*values.length];
        for(int i=0;i<values.length;i++)
        {
            // Assign each byte to the int64Bytes[]
            int64Bytes[(8*values.length) +0] = (byte)((values[i] >> 56) & 0xff);
            int64Bytes[(8*values.length) +1] = (byte)((values[i] >> 48) & 0xff);  
            int64Bytes[(8*values.length) +2] = (byte)((values[i] >> 40) & 0xff);  
            int64Bytes[(8*values.length) +3] = (byte)((values[i] >> 32) & 0xff);
            int64Bytes[(8*values.length) +4] = (byte)((values[i] >> 24) & 0xff);
            int64Bytes[(8*values.length) +5] = (byte)((values[i] >> 16) & 0xff);  
            int64Bytes[(8*values.length) +6] = (byte)((values[i] >> 8) & 0xff);  
            int64Bytes[(8*values.length) +7] = (byte)((values[i] >> 0) & 0xff); 
        }
        return int64Bytes;
    }       

    /** Return ROS Hex uint64. */
    public static String getHexUInt64(long value)
    {       
        return Long.toHexString((value >>> 0) & 0xffffffff);
    }    
    
    
    
// TODO if these methods dont work anymore the solution will be to reversing the counting not the array count...     
    /** Return Java long[] as ROS multiple combines ROS uint64. */ 
    public static byte[] getUInt64Array(long[] values)
    {
        final byte[] int64Bytes = new byte[8*values.length];
        for(int i=0;i<values.length;i++)
        {
            // Assign each byte to the int64Bytes[]
            int64Bytes[(8*values.length) +0] = (byte)((values[i] >>> 56) & 0xff);
            int64Bytes[(8*values.length) +1] = (byte)((values[i] >>> 48) & 0xff);  
            int64Bytes[(8*values.length) +2] = (byte)((values[i] >>> 40) & 0xff);  
            int64Bytes[(8*values.length) +3] = (byte)((values[i] >>> 32) & 0xff);
            int64Bytes[(8*values.length) +4] = (byte)((values[i] >>> 24) & 0xff);
            int64Bytes[(8*values.length) +5] = (byte)((values[i] >>> 16) & 0xff);  
            int64Bytes[(8*values.length) +6] = (byte)((values[i] >>> 8) & 0xff);  
            int64Bytes[(8*values.length) +7] = (byte)((values[i] >>> 0) & 0xff); 
        }
        return int64Bytes;
    }     
    
    /** Return ROS Hex uint32. */
    public static String getHexUInt32(long value)
    {       
        return Long.toHexString((value >>> 0) & 0xffffffff);
    }    
    
    /** Return ROS uint32 as byte[] (Big Endian). */
    public static byte[] getUInt32(long value)
    {
        final byte[] int32Bytes = new byte[4];
        int32Bytes[0] = (byte)((value >>> 24) & 0xff);
        int32Bytes[1] = (byte)((value >>> 16) & 0xff);  
        int32Bytes[2] = (byte)((value >>> 8) & 0xff);  
        int32Bytes[3] = (byte)((value >>> 0) & 0xff);         
        return int32Bytes;
    }     
 
    /** Return Java Source Code for byte[] of Fixed uint32 from Java long input parameter. */
    public static String getFixedUInt32(int current_position, String instruction_variable)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("ros_msg[");
        sb.append(current_position);
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >>> 24) & 0xff);\n");
        sb.append("ros_msg[");
        sb.append(current_position+1);
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >>> 16) & 0xff);\n");
        sb.append("ros_msg[");
        sb.append(current_position+2);
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >>> 8) & 0xff);\n");
        sb.append("ros_msg[");
        sb.append(current_position+3);
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >>> 0) & 0xff);\n");        
        return sb.toString();  
    }      

    /** Return Java Source Code for byte[] of Variable uint32 from Java long input parameter. */
    public static String getVariableUInt32(String current_position_variable, String instruction_variable)
    {
        StringBuilder sb = new StringBuilder();

        sb.append("ros_msg[");
        sb.append(current_position_variable);
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >>> 24) & 0xff);\n");
        sb.append("ros_msg[");
        sb.append(current_position_variable.concat("+1"));
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >>> 16) & 0xff);\n");
        sb.append("ros_msg[");
        sb.append(current_position_variable.concat("+2"));
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >>> 8) & 0xff);\n");
        sb.append("ros_msg[");
        sb.append(current_position_variable.concat("+3"));
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >>> 0) & 0xff);\n");   
        // Set the current_position to the first int following this variable.
        sb.append(current_position_variable);
        sb.append(" = ");
        sb.append(current_position_variable);
        sb.append(" + 4;");
        //          
        return sb.toString();  
    }     
    
    /** Return Java Source Code converting a Fixed Java long[] to ROS uint32[] in byte[]. */ 
    public static String getFixedUInt32Array(int start_position, int end_position, String instruction_variable)
    {
        StringBuilder sb = new StringBuilder();        
        int j=0;                  
        for(int i=start_position;i<end_position;i++)
        {              
            // get the long array element
            sb.append("this.uint32Long=");
            sb.append(instruction_variable);
            sb.append("[");
            sb.append(j);
            sb.append("];\n");            
            // Assign each byte to the ros_msg byte[]
            sb.append("ros_msg[");
            sb.append(i);
            sb.append("] = (byte)((uint32Long >>> 24) & 0xff);\n");
            sb.append("ros_msg[");
            sb.append(i+1);
            sb.append("] = (byte)((uint32Long >>> 16) & 0xff);\n");
            sb.append("ros_msg[");
            sb.append(i+2);
            sb.append("] = (byte)((uint32Long >>> 8) & 0xff);\n");
            sb.append("ros_msg[");
            sb.append(i+3);
            sb.append("] = (byte)((uint32Long  0) & 0xff);\n");   
            j=j+1;
        }
        return sb.toString();        
    }    

    /** Return Java Source Code converting a Java long[] to a variable ROS uint32[] in byte[]. */ 
    public static String getVariableUInt32Array(String instruction_variable, String current_position_variable)
    {
        StringBuilder sb = new StringBuilder();        
        // set the array length in bytes for setting the prefix bytes
        sb.append("this.array_length=");
        sb.append("4*"+instruction_variable+".length;\n");
        // Set the array length prefix bytes
        sb.append("ros_msg["+current_position_variable+"="+current_position_variable+"+3] = (byte)((array_length >>> 24) & 0xff);\n");
        sb.append("ros_msg["+current_position_variable+"="+current_position_variable+"+2] = (byte)((array_length >>> 16) & 0xff);\n");  
        sb.append("ros_msg["+current_position_variable+"="+current_position_variable+"+1] = (byte)((array_length >>> 8) & 0xff);\n");  
        sb.append("ros_msg["+current_position_variable+"] = (byte)((array_length >>> 0) & 0xff);\n");
        // Iterate to the next current_position
        sb.append(current_position_variable+"="+current_position_variable+"+1;\n");
        // Loop through float[]
        sb.append("j=0;\n");
        sb.append("for(int i="+current_position_variable+";i<"+instruction_variable+".length;i++)\n");
        sb.append("{\n");              
        // get the long array element
        sb.append("this.uint32Long=");
        sb.append(instruction_variable);
        sb.append("[j];\n");      
        
        // Assign each byte to the ros_msg byte[]     
        sb.append("ros_msg[i] = (byte)((uint32Long >>> 24) & 0xff);\n");
        sb.append("ros_msg[i=i+1] = (byte)((uint32Long >>> 16) & 0xff);\n");
        sb.append("ros_msg[i=i+1] = (byte)((uint32Long >>> 8) & 0xff);\n");
        sb.append("ros_msg[i=i+1] = (byte)((uint32Long >>> 0) & 0xff);\n");
        sb.append("j=j+1;\n");
        sb.append("}\n");
        
        // Set the current position before proceeding...
        sb.append(current_position_variable+"="+current_position_variable+"+(4*"+instruction_variable+".length);\n");
        return sb.toString();        
    }          
    
    
    /** Return Java long[] as ROS multiple combines ROS uint32. */ 
    public static byte[] getUInt32Array(long[] values)
    {
        final byte[] int32Bytes = new byte[4*values.length];
        for(int i=0;i<values.length;i++)
        {
            // Assign each byte to the int8Bytes[]
            int32Bytes[(4*values.length) + 0] = (byte)((values[i] >>> 24) & 0xff);
            int32Bytes[(4*values.length) + 1] = (byte)((values[i] >>> 16) & 0xff);  
            int32Bytes[(4*values.length) + 2] = (byte)((values[i] >>> 8) & 0xff);  
            int32Bytes[(4*values.length) + 3] = (byte)((values[i] >>> 0) & 0xff);    
        }
        return int32Bytes;
    }       
    
    /** Return ROS Hex int32. */
    public static String getHexInt32(int value)
    {       
        return Integer.toHexString(value);
    }
    
    /** Return ROS int32 as byte[] (Big Endian). */
    public static byte[] getInt32(int value)
    {
        final byte[] int32Bytes = new byte[4];
        int32Bytes[0] = (byte)((value >> 24) & 0xff);
        int32Bytes[1] = (byte)((value >> 16) & 0xff);  
        int32Bytes[2] = (byte)((value >> 8) & 0xff);  
        int32Bytes[3] = (byte)((value >> 0) & 0xff);         
        return int32Bytes;
    }    
    
        /** Return Java Source Code for byte[] of Fixed int32 from Java int input parameter. */
    public static String getFixedInt32(int current_position, String instruction_variable)
    {
        StringBuilder sb = new StringBuilder();
 
        sb.append("ros_msg[");
        sb.append(current_position);
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >> 24) & 0xff);\n");
        sb.append("ros_msg[");
        sb.append(current_position+1);
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >> 16) & 0xff);\n");
        sb.append("ros_msg[");
        sb.append(current_position+2);
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >> 8) & 0xff);\n");
        sb.append("ros_msg[");
        sb.append(current_position+3);
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >> 0) & 0xff);\n");        
        return sb.toString();  
    }      

    /** Return Java Source Code for byte[] of Variable int32 from Java int input parameter. */
    public static String getVariableInt32(String current_position_variable, String instruction_variable)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("ros_msg[");
        sb.append(current_position_variable);
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >> 24) & 0xff);\n");
        sb.append("ros_msg[");
        sb.append(current_position_variable.concat("+1"));
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >> 16) & 0xff);\n");
        sb.append("ros_msg[");
        sb.append(current_position_variable.concat("+2"));
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >> 8) & 0xff);\n");
        sb.append("ros_msg[");
        sb.append(current_position_variable.concat("+3"));
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >> 0) & 0xff);\n");   
        // Set the current_position to the first int following this variable.
        sb.append(current_position_variable);
        sb.append(" = ");
        sb.append(current_position_variable);
        sb.append(" + 4;");
        //          
        return sb.toString();  
    }     
    
    /** Return Java Source Code converting a Fixed Java int[] to ROS int32[] in byte[]. */ 
    public static String getFixedInt32Array(int start_position, int end_position, String instruction_variable)
    {
        StringBuilder sb = new StringBuilder();        
        int j=0;                  
        for(int i=start_position;i<end_position;i++)
        {              
            // get the long array element
            sb.append("this.int32Int=");
            sb.append(instruction_variable);
            sb.append("[");
            sb.append(j);
            sb.append("];\n");            
            // Assign each byte to the ros_msg byte[]
            sb.append("ros_msg[");
            sb.append(i);
            sb.append("] = (byte)((int32Int >> 24) & 0xff);\n");
            sb.append("ros_msg[");
            sb.append(i+1);
            sb.append("] = (byte)((int32Int >> 16) & 0xff);\n");
            sb.append("ros_msg[");
            sb.append(i+2);
            sb.append("] = (byte)((int32Int >> 8) & 0xff);\n");
            sb.append("ros_msg[");
            sb.append(i+3);
            sb.append("] = (byte)((int32Int >> 0) & 0xff);\n");   
            j=j+1;
        }
        return sb.toString();        
    }    

    /** Return Java Source Code converting a Java int[] to a variable ROS int32[] in byte[]. */ 
    public static String getVariableInt32Array(String instruction_variable, String current_position_variable)
    {
        StringBuilder sb = new StringBuilder();        
        // set the array length in bytes for setting the prefix bytes
        sb.append("this.array_length=");
        sb.append("4*"+instruction_variable+".length;\n");
        // Set the array length prefix bytes
        sb.append("ros_msg["+current_position_variable+"="+current_position_variable+"+3] = (byte)((array_length >>> 24) & 0xff);\n");
        sb.append("ros_msg["+current_position_variable+"="+current_position_variable+"+2] = (byte)((array_length >>> 16) & 0xff);\n");  
        sb.append("ros_msg["+current_position_variable+"="+current_position_variable+"+1] = (byte)((array_length >>> 8) & 0xff);\n");  
        sb.append("ros_msg["+current_position_variable+"] = (byte)((array_length >>> 0) & 0xff);\n");
        // Iterate to the next current_position
        sb.append(current_position_variable+"="+current_position_variable+"+1;\n");
        // Loop through float[]
        sb.append("j=0;\n");
        sb.append("for(int i="+current_position_variable+";i<"+instruction_variable+".length;i++)\n");
        sb.append("{\n");              
        // get the long array element
        sb.append("this.int32Int=");
        sb.append(instruction_variable);
        sb.append("[j]);\n");      
        
        // Assign each byte to the ros_msg byte[]    
        sb.append("ros_msg[i] = (byte)((int32Int >> 24) & 0xff);\n");
        sb.append("ros_msg[i=i+1] = (byte)((int32Int >> 16) & 0xff);\n");
        sb.append("ros_msg[i=i+1] = (byte)((int32Int >> 8) & 0xff);\n");
        sb.append("ros_msg[i=i+1] = (byte)((int32Int >> 0) & 0xff);\n");
        sb.append("j=j+1;\n");
        sb.append("}\n");
        
        // Set the current position before proceeding...
        sb.append(current_position_variable+"="+current_position_variable+"+(4*"+instruction_variable+".length);\n");
        return sb.toString();        
    }        

    /** Return Java int[] as ROS multiple combines ROS int32. */ 
    public static byte[] getInt32Array(int[] values)
    {
        final byte[] int32Bytes = new byte[4*values.length];
        for(int i=0;i<values.length;i++)
        {
            // Assign each byte to the int8Bytes[]
            int32Bytes[(4*values.length) + 0] = (byte)((values[i] >> 24) & 0xff);
            int32Bytes[(4*values.length) + 1] = (byte)((values[i] >> 16) & 0xff);  
            int32Bytes[(4*values.length) + 2] = (byte)((values[i] >> 8) & 0xff);  
            int32Bytes[(4*values.length) + 3] = (byte)((values[i] >> 0) & 0xff);    
        }
        return int32Bytes;
    }           

    /** Return ROS Hex uint16. */
    public static String getHexUInt16(short value)
    {       
        return Integer.toHexString((value >>> 0) & 0xffff);
    }
    
    /** Return ROS uint16 as byte[] (Big Endian). */
    public static byte[] getUInt16(int value)
    {
        final byte[] int16Bytes = new byte[2];
        int16Bytes[0] = (byte)((value >>> 0) & 0xff);
        int16Bytes[1] = (byte)((value >>> 8) & 0xff);      
        return int16Bytes;
    }   
    
    /** Return Java Source Code for byte[] of Fixed uint16 from Java int input parameter. */
    public static String getFixedUInt16(int current_position, String instruction_variable)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("ros_msg[");
        sb.append(current_position);
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >>> 8) & 0xff);\n");
        sb.append("ros_msg[");
        sb.append(current_position+1);
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >>> 0) & 0xff);\n");        
        return sb.toString();  
    }      

    /** Return Java Source Code for byte[] of Variable uint16 from Java int input parameter. */
    public static String getVariableUInt16(String current_position_variable, String instruction_variable)
    {
        StringBuilder sb = new StringBuilder();

        sb.append("ros_msg[");
        sb.append(current_position_variable);
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >>> 8) & 0xff);\n");
        sb.append("ros_msg[");
        sb.append(current_position_variable.concat("+1"));
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >>> 0) & 0xff);\n");   
        // Set the current_position to the first int following this variable.
        sb.append(current_position_variable);
        sb.append(" = ");
        sb.append(current_position_variable);
        sb.append(" + 2;");
        //          
        return sb.toString();  
    }     
    
    /** Return Java Source Code converting a Fixed Java int[] to ROS uint16[] in byte[]. */ 
    public static String getFixedUInt16Array(int start_position, int end_position, String instruction_variable)
    {
        StringBuilder sb = new StringBuilder();        
        int j=0;                  
        for(int i=start_position;i<end_position;i++)
        {              
            // get the long array element
            sb.append("this.uint16Int=");
            sb.append(instruction_variable);
            sb.append("[");
            sb.append(j);
            sb.append("];\n");            
            // Assign each byte to the ros_msg byte[]
            sb.append("ros_msg[");
            sb.append(i);
            sb.append("] = (byte)((uint16Int >>> 8) & 0xff);\n");
            sb.append("ros_msg[");
            sb.append(i+1);
            sb.append("] = (byte)((uint16Int  0) & 0xff);\n");   
            j=j+1;
        }
        return sb.toString();        
    }    

    /** Return Java Source Code converting a Java int[] to a variable ROS uint16[] in byte[]. */ 
    public static String getVariableUInt16Array(String instruction_variable, String current_position_variable)
    {
        StringBuilder sb = new StringBuilder();        
        // set the array length in bytes for setting the prefix bytes
        sb.append("this.array_length=");
        sb.append("2*"+instruction_variable+".length;\n");
        // Set the array length prefix bytes
        sb.append("ros_msg["+current_position_variable+"="+current_position_variable+"+3] = (byte)((array_length >>> 24) & 0xff);\n");
        sb.append("ros_msg["+current_position_variable+"="+current_position_variable+"+2] = (byte)((array_length >>> 16) & 0xff);\n");  
        sb.append("ros_msg["+current_position_variable+"="+current_position_variable+"+1] = (byte)((array_length >>> 8) & 0xff);\n");  
        sb.append("ros_msg["+current_position_variable+"] = (byte)((array_length >>> 0) & 0xff);\n");
        // Iterate to the next current_position
        sb.append(current_position_variable+"="+current_position_variable+"+1;\n");
        // Loop through float[]
        sb.append("j=0;\n");
        sb.append("for(int i="+current_position_variable+";i<"+instruction_variable+".length;i++)\n");
        sb.append("{\n");              
        // get the int array element
        sb.append("this.uint16Int=");
        sb.append(instruction_variable);
        sb.append("[j];\n");      
        
        // Assign each byte to the ros_msg byte[]     
        sb.append("ros_msg[i] = (byte)((int32Long >>> 8) & 0xff);\n");
        sb.append("ros_msg[i=i+1] = (byte)((int32Long >>> 0) & 0xff);\n");
        sb.append("j=j+1;\n");
        sb.append("}\n");
        
        // Set the current position before proceeding...
        sb.append(current_position_variable+"="+current_position_variable+"+(2*"+instruction_variable+".length);\n");
        return sb.toString();        
    }          
    
    
    /** Return Java short[] as ROS multiple combines ROS uint16. */ 
    public static byte[] getUInt16Array(int[] values)
    {
        final byte[] int16Bytes = new byte[2*values.length];
        for(int i=0;i<values.length;i++)
        {
            // Assign each byte to the int8Bytes[]
            int16Bytes[(2*values.length) + 0] = (byte)((values[i] >>> 0) & 0xff);
            int16Bytes[(2*values.length) + 1] = (byte)((values[i] >>> 8) & 0xff);   
        }
        return int16Bytes;
    }      
    
    /** Return ROS Hex int16. */
    public static String getHexInt16(short value)
    {       
        return Integer.toHexString((value >> 0) & 0xffff);
    }
    
    /** Return ROS int16 as byte[] (Big Endian). */
    public static byte[] getInt16(short value)
    {
        final byte[] int16Bytes = new byte[2];
        int16Bytes[0] = (byte)((value >> 8));
        int16Bytes[1] = (byte)((value >> 0));      
        return int16Bytes;
    }    
    
        /** Return Java Source Code for byte[] of Fixed int16 from Java short input parameter. */
    public static String getFixedInt16(int current_position, String instruction_variable)
    {
        StringBuilder sb = new StringBuilder();
 
        sb.append("ros_msg[");
        sb.append(current_position);
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >> 8));\n");
        sb.append("ros_msg[");
        sb.append(current_position+1);
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >> 0));\n");        
        return sb.toString();  
    }      

    /** Return Java Source Code for byte[] of Variable int16 from Java short input parameter. */
    public static String getVariableInt16(String current_position_variable, String instruction_variable)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("ros_msg[");
        sb.append(current_position_variable);
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >> 8));\n");
        sb.append("ros_msg[");
        sb.append(current_position_variable.concat("+1"));
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >> 0));\n");   
        // Set the current_position to the first int following this variable.
        sb.append(current_position_variable);
        sb.append(" = ");
        sb.append(current_position_variable);
        sb.append(" + 2;");
        //          
        return sb.toString();  
    }     
    
    /** Return Java Source Code converting a Fixed Java short[] to ROS int16[] in byte[]. */ 
    public static String getFixedInt16Array(int start_position, int end_position, String instruction_variable)
    {
        StringBuilder sb = new StringBuilder();        
        int j=0;                  
        for(int i=start_position;i<end_position;i++)
        {              
            // get the short array element
            sb.append("this.int16Short=");
            sb.append(instruction_variable);
            sb.append("[");
            sb.append(j);
            sb.append("];\n");            
            // Assign each byte to the ros_msg byte[]
            sb.append("ros_msg[");
            sb.append(i);
            sb.append("] = (byte)((int16Short >> 8));\n");
            sb.append("ros_msg[");
            sb.append(i+1);
            sb.append("] = (byte)((int16Short >> 0));\n");   
            j=j+1;
        }
        return sb.toString();        
    }    

    /** Return Java Source Code converting a Java short[] to a variable ROS int16[] in byte[]. */ 
    public static String getVariableInt16Array(String instruction_variable, String current_position_variable)
    {
        StringBuilder sb = new StringBuilder();        
        // set the array length in bytes for setting the prefix bytes
        sb.append("this.array_length=");
        sb.append("2*"+instruction_variable+".length;\n");
        // Set the array length prefix bytes
        sb.append("ros_msg["+current_position_variable+"="+current_position_variable+"+3] = (byte)((array_length >>> 24) & 0xff);\n");
        sb.append("ros_msg["+current_position_variable+"="+current_position_variable+"+2] = (byte)((array_length >>> 16) & 0xff);\n");  
        sb.append("ros_msg["+current_position_variable+"="+current_position_variable+"+1] = (byte)((array_length >>> 8) & 0xff);\n");  
        sb.append("ros_msg["+current_position_variable+"] = (byte)((array_length >>> 0) & 0xff);\n");
        // Iterate to the next current_position
        sb.append(current_position_variable+"="+current_position_variable+"+1;\n");
        // Loop through float[]
        sb.append("j=0;\n");
        sb.append("for(int i="+current_position_variable+";i<"+instruction_variable+".length;i++)\n");
        sb.append("{\n");              
        // get the long array element
        sb.append("this.int16Short=");
        sb.append(instruction_variable);
        sb.append("[j];\n");      
        
        // Assign each byte to the ros_msg byte[]    
        sb.append("ros_msg[i] = (byte)((int16Short >> 8));\n");
        sb.append("ros_msg[i=i+1] = (byte)((int16Short >> 0));\n");
        sb.append("j=j+1;\n");
        sb.append("}\n");
        
        // Set the current position before proceeding...
        sb.append(current_position_variable+"="+current_position_variable+"+(2*"+instruction_variable+".length);\n");
        return sb.toString();        
    }        
    
    
    /** Return Java short[] as ROS multiple combines ROS int16. */ 
    public static byte[] getInt16Array(short[] values)
    {
        final byte[] int16Bytes = new byte[2*values.length];
        for(int i=0;i<values.length;i++)
        {
            // Assign each byte to the int8Bytes[]
            int16Bytes[(2*values.length) + 0] = (byte)((values[i] >> 8)); 
            int16Bytes[(2*values.length) + 1] = (byte)((values[i] >> 0));  
        }
        return int16Bytes;
    }        
    
    
    /** Return ROS Hex int8. */
    public static String getHexInt8(short value)
    {
        return Integer.toHexString((byte)value);
    }
    
    /** Return ROS int8 as byte. */
    public static byte getInt8(byte value)
    {
        return (byte)value;
    }    
    
    /** Return Java Source Code for byte of Fixed int8 from Java byte input parameter. */
    public static String getFixedInt8(int current_position, String instruction_variable)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("ros_msg[");
        sb.append(current_position);
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >> 0));\n");        
        return sb.toString();  
    }     
    
    /** Return Java Source Code for byte[] of Variable int8 from Java byte input parameter. */
    public static String getVariableInt8(String current_position_variable, String instruction_variable)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("ros_msg[");
        sb.append(current_position_variable);
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >> 0));\n");   
        // Set the current_position to the first int following this variable.
        sb.append(current_position_variable);
        sb.append(" = ");
        sb.append(current_position_variable);
        sb.append(" + 1;");
        //          
        return sb.toString();  
    }     
    
    /** Return Java Source Code converting a Fixed Java byte[] to ROS int8[] in byte[]. */public static String getFixedInt8Array(int start_position, int end_position, String instruction_variable)
    {
        StringBuilder sb = new StringBuilder();        
        int j=0;                  
        for(int i=start_position;i<end_position;i++)
        {              
            // get the short array element
            sb.append("this.int8Byte=");
            sb.append(instruction_variable);
            sb.append("[");
            sb.append(j);
            sb.append("];\n");            
            // Assign each byte to the ros_msg byte[]
            sb.append("ros_msg[");
            sb.append(i);
            sb.append("] = (byte)((int8Byte >> 0));\n");   
            j=j+1;
        }
        return sb.toString();        
    }    

    /** Return Java Source Code converting a Java short[] to a variable ROS int8[] in byte[]. */ 
    public static String getVariableInt8Array(String instruction_variable, String current_position_variable)
    {
        StringBuilder sb = new StringBuilder();        
        // set the array length in bytes for setting the prefix bytes
        sb.append("this.array_length=");
        sb.append(instruction_variable+".length;\n");
        // Set the array length prefix bytes
        sb.append("ros_msg["+current_position_variable+"="+current_position_variable+"+3] = (byte)((array_length >>> 24) & 0xff);\n");
        sb.append("ros_msg["+current_position_variable+"="+current_position_variable+"+2] = (byte)((array_length >>> 16) & 0xff);\n");  
        sb.append("ros_msg["+current_position_variable+"="+current_position_variable+"+1] = (byte)((array_length >>> 8) & 0xff);\n");  
        sb.append("ros_msg["+current_position_variable+"] = (byte)((array_length >>> 0) & 0xff);\n");
        // Iterate to the next current_position
        sb.append(current_position_variable+"="+current_position_variable+"+1;\n");
        // Loop through float[]
        sb.append("j=0;\n");
        sb.append("for(int i="+current_position_variable+";i<"+instruction_variable+".length;i++)\n");
        sb.append("{\n");              
        // get the long array element
        sb.append("this.int8Byte=");
        sb.append(instruction_variable);
        sb.append("[j];\n");      
        
        // Assign each byte to the ros_msg byte[]    
        sb.append("ros_msg[i] = (byte)((int8Byte >> 0));\n");
        sb.append("j=j+1;\n");
        sb.append("}\n");
        
        // Set the current position before proceeding...
        sb.append(current_position_variable+"="+current_position_variable+"+("+instruction_variable+".length);\n");
        return sb.toString();        
    }        
      

   /** Return ROS Hex uint8. */
    public static String getHexUInt8(short value)
    {
        return Integer.toHexString((value >>> 0) & 0xff);
    }
    
    /** Return ROS uint8 as byte[]. */
    public static byte getUInt8(short value)
    {
        return (byte)((value >>> 0) & 0xff);
    }    
    
    /** Return Java Source Code for byte[] of Fixed uint8 from Java short input parameter. */
    public static String getFixedUInt8(int current_position, String instruction_variable)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("ros_msg[");
        sb.append(current_position);
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >>> 0) & 0xff);\n");        
        return sb.toString();  
    }      

    /** Return Java Source Code for byte[] of Variable uint8 from Java short input parameter. */
    public static String getVariableUInt8(String current_position_variable, String instruction_variable)
    {
        StringBuilder sb = new StringBuilder();

        sb.append("ros_msg[");
        sb.append(current_position_variable);
        sb.append("] = (byte)((");
        sb.append(instruction_variable);
        sb.append(" >>> 0) & 0xff);\n");   
        // Set the current_position to the first int following this variable.
        sb.append(current_position_variable);
        sb.append(" = ");
        sb.append(current_position_variable);
        sb.append(" + 1;");
        //          
        return sb.toString();  
    }     
    
    /** Return Java Source Code converting a Fixed Java short[] to ROS uint8[] in byte[]. */ 
    public static String getFixedUInt8Array(int start_position, int end_position, String instruction_variable)
    {
        StringBuilder sb = new StringBuilder();        
        int j=0;                  
        for(int i=start_position;i<end_position;i++)
        {              
            // get the long array element
            sb.append("this.uint8Short=");
            sb.append(instruction_variable);
            sb.append("[");
            sb.append(j);
            sb.append("];\n");            
            // Assign each byte to the ros_msg byte[]
            sb.append("ros_msg[");
            sb.append(i);
            sb.append("] = (byte)((uint8Short  0) & 0xff);\n");   
            j=j+1;
        }
        return sb.toString();        
    }    

    /** Return Java Source Code converting a Java short[] to a variable ROS uint8[] in byte[]. */ 
    public static String getVariableUInt8Array(String instruction_variable, String current_position_variable)
    {
        StringBuilder sb = new StringBuilder();        
        // set the array length in bytes for setting the prefix bytes
        sb.append("this.array_length=");
        sb.append(instruction_variable+".length;\n");
        // Set the array length prefix bytes
        sb.append("ros_msg["+current_position_variable+"="+current_position_variable+"+3] = (byte)((array_length >>> 24) & 0xff);\n");
        sb.append("ros_msg["+current_position_variable+"="+current_position_variable+"+2] = (byte)((array_length >>> 16) & 0xff);\n");  
        sb.append("ros_msg["+current_position_variable+"="+current_position_variable+"+1] = (byte)((array_length >>> 8) & 0xff);\n");  
        sb.append("ros_msg["+current_position_variable+"] = (byte)((array_length >>> 0) & 0xff);\n");
        // Iterate to the next current_position
        sb.append(current_position_variable+"="+current_position_variable+"+1;\n");
        // Loop through short[]
        sb.append("j=0;\n");
        sb.append("for(int i="+current_position_variable+";i<"+instruction_variable+".length;i++)\n");
        sb.append("{\n");              
        // get the int array element
        sb.append("this.uint8Short=");
        sb.append(instruction_variable);
        sb.append("[j];\n");      
        
        // Assign each byte to the ros_msg byte[]     
        sb.append("ros_msg[i] = (byte)((int8Short >>> 0) & 0xff);\n");
        sb.append("j=j+1;\n");
        sb.append("}\n");
        
        // Set the current position before proceeding...
        sb.append(current_position_variable+"="+current_position_variable+"+("+instruction_variable+".length);\n");
        return sb.toString();        
    }          
    
    
    /** Return Java short[] as ROS multiple combines ROS uint8. */ 
    public static byte[] getUInt8Array(short[] values)
    {
        final byte[] int8Bytes = new byte[values.length];
        for(int i=0;i<values.length;i++)
        {
            // Assign each byte to the int8Bytes[]
            int8Bytes[i] = (byte)((values[i] >>> 0) & 0xff);
        }
        return int8Bytes;
    } 
       
            
 //   /** Return Assigned Header Java source for Header from ROS Header. */
 /*   public static String getAssignedVariableHeader(String assignment_variable, String current_position_variable)
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
   */
            
    /** Return Java Source Code converting a Java String to a variable ROS string in byte[]. */ 
    public static String getVariableString(String instruction_variable, String current_position_variable)
    {
        // TODO: Implement below return in code to ros_msg....
        //        return HexToBytes.hexToBytes(hexStringConverter.stringToHex(value));
        StringBuilder sb = new StringBuilder();        
        // set the string length in bytes for setting the prefix bytes
        sb.append("this.string_length=("+instruction_variable+".length()+1)/2;\n");
        // Set the array length prefix bytes
        sb.append("ros_msg["+current_position_variable+"="+current_position_variable+"+3] = (byte)((string_length >>> 24) & 0xff);\n");
        sb.append("ros_msg["+current_position_variable+"="+current_position_variable+"+2] = (byte)((string_length >>> 16) & 0xff);\n");  
        sb.append("ros_msg["+current_position_variable+"="+current_position_variable+"+1] = (byte)((string_length >>> 8) & 0xff);\n");  
        sb.append("ros_msg["+current_position_variable+"] = (byte)((string_length >>> 0) & 0xff);\n");
        // Iterate to the next current_position
        sb.append(current_position_variable+"="+current_position_variable+"+1;\n");

//        sb.append("ros_msg=HexToBytes.hexToBytes(hexStringConverter.stringToHex(");
//        sb.append(instruction_variable);
//        sb.append("),ros_msg,");
//        sb.append(current_position_variable);
//        sb.append(");");
///////////////////////////////////// Manual Hex to Bytes

sb.append("      try\n{\nthis.str=hexStringConverter.stringToHex("+instruction_variable+");\n}\ncatch(java.io.UnsupportedEncodingException exc)\n{\njava.util.logging.Logger.getLogger(new Throwable() .getStackTrace()[0].getClassName()).log(Level.SEVERE, null, exc);\n}\n" +
"      if (str.length() != 0) \n" +
"      {\n" +
"      ros_msg["+current_position_variable+"] = 0;\n" +
"      this.soup = (str.length() % 2);\n" +
"      this.y = "+current_position_variable+";\n" +
"      while (y < ("+current_position_variable+"+str.length())) \n" +
"      {\n" +
"        char c = str.charAt(y);\n" +
"        if (!isHex(c)) {\n" +
"            \n" +
"          throw new IllegalArgumentException(\"string contains non-hex chars: \"+c);\n" +
"        }\n" +
"        if ((soup % 2) == 0) \n" +
"        {\n" +
"          ros_msg[soup >> 1] = (byte) (hexValue(c) << 4);\n" +
"        } \n" +
"        else \n" +
"        {\n" +
"          ros_msg[soup >> 1] += (byte) hexValue(c);\n" +
"        }\n" +
"        soup=soup+1;\n" +
"        y=y+1;\n" +        
"      }\n");
sb.append("      }\n");
        
/////////////////////////////////////     
        
        
        // Set the current position before proceeding...
        sb.append(current_position_variable+"="+current_position_variable+"+string_length;\n");
        return sb.toString(); 
    }  
 
    /** Return Java Source Code converting a Java Header to a variable ROS Header in byte[]. */     
    public static String getVariableHeader(String instruction_variable, String current_position_variable)
    {
        StringBuilder sb = new StringBuilder();
       
        sb.append(instruction_variable.concat(".seq").concat("=(this.seq=seq + 1);\n"));
        sb.append("ros_msg[");
        sb.append(current_position_variable);
        sb.append("] = (byte)((");
        sb.append(instruction_variable.concat(".seq"));
        sb.append(" >>> 24) & 0xff);\n");
        sb.append("ros_msg[");
        sb.append(current_position_variable.concat("+1"));
        sb.append("] = (byte)((");
        sb.append(instruction_variable.concat(".seq"));
        sb.append(" >>> 16) & 0xff);\n");
        sb.append("ros_msg[");
        sb.append(current_position_variable.concat("+2"));
        sb.append("] = (byte)((");
        sb.append(instruction_variable.concat(".seq"));
        sb.append(" >>> 8) & 0xff);\n");
        sb.append("ros_msg[");
        sb.append(current_position_variable.concat("+3"));
        sb.append("] = (byte)((");
        sb.append(instruction_variable.concat(".seq"));
        sb.append(" >>> 0) & 0xff);\n");   
        // Set the current_position to the first int following this variable.
        sb.append(current_position_variable);
        sb.append(" = ");
        sb.append(current_position_variable);
        sb.append(" + 4;\n");
        // time stamp secs      
        sb.append("ros_msg[");
        sb.append(current_position_variable);
        sb.append("] = (byte)((");
        sb.append(instruction_variable.concat(".stamp.secs"));
        sb.append(" >>> 24) & 0xff);\n");
        sb.append("ros_msg[");
        sb.append(current_position_variable.concat("+1"));
        sb.append("] = (byte)((");
        sb.append(instruction_variable.concat(".stamp.secs"));
        sb.append(" >>> 16) & 0xff);\n");
        sb.append("ros_msg[");
        sb.append(current_position_variable.concat("+2"));
        sb.append("] = (byte)((");
        sb.append(instruction_variable.concat(".stamp.secs"));
        sb.append(" >>> 8) & 0xff);\n");
        sb.append("ros_msg[");
        sb.append(current_position_variable.concat("+3"));
        sb.append("] = (byte)((");
        sb.append(instruction_variable.concat(".stamp.secs"));
        sb.append(" >>> 0) & 0xff);\n");   
        // Set the current_position to the first int following this variable.
        sb.append(current_position_variable);
        sb.append(" = ");
        sb.append(current_position_variable);
        sb.append(" + 4;\n");        
        // time stamp nsecs
        sb.append("ros_msg[");
        sb.append(current_position_variable);
        sb.append("] = (byte)((");
        sb.append(instruction_variable.concat(".stamp.nsecs"));
        sb.append(" >>> 24) & 0xff);\n");
        sb.append("ros_msg[");
        sb.append(current_position_variable.concat("+1"));
        sb.append("] = (byte)((");
        sb.append(instruction_variable.concat(".stamp.nsecs"));
        sb.append(" >>> 16) & 0xff);\n");
        sb.append("ros_msg[");
        sb.append(current_position_variable.concat("+2"));
        sb.append("] = (byte)((");
        sb.append(instruction_variable.concat(".stamp.nsecs"));
        sb.append(" >>> 8) & 0xff);\n");
        sb.append("ros_msg[");
        sb.append(current_position_variable.concat("+3"));
        sb.append("] = (byte)((");
        sb.append(instruction_variable.concat(".stamp.nsecs"));
        sb.append(" >>> 0) & 0xff);\n");   
        // Set the current_position to the first int following this variable.
        sb.append(current_position_variable);
        sb.append(" = ");
        sb.append(current_position_variable);
        sb.append(" + 4;\n");        
        // string frame_id
         // set the string length in bytes for setting the prefix bytes
        sb.append("this.string_length=("+instruction_variable+".frame_id.length()+1)/2;\n");
        // Set the array length prefix bytes
        sb.append("ros_msg["+current_position_variable+"="+current_position_variable+"+3] = (byte)((string_length >>> 24) & 0xff);\n");
        sb.append("ros_msg["+current_position_variable+"="+current_position_variable+"+2] = (byte)((string_length >>> 16) & 0xff);\n");  
        sb.append("ros_msg["+current_position_variable+"="+current_position_variable+"+1] = (byte)((string_length >>> 8) & 0xff);\n");  
        sb.append("ros_msg["+current_position_variable+"] = (byte)((string_length >>> 0) & 0xff);\n");
        // Iterate to the next current_position
        sb.append(current_position_variable+"="+current_position_variable+"+1;\n");

//        sb.append("ros_msg=HexToBytes.hexToBytes(hexStringConverter.stringToHex(");
//        sb.append(instruction_variable.concat(".frame_id"));
//        sb.append("),ros_msg,");
//        sb.append(current_position_variable);
//        sb.append(");");
  
///////////////////////////////////// Manual Hex to Bytes
sb.append("      try\n{\nthis.str=hexStringConverter.stringToHex("+instruction_variable.concat(".frame_id")+");\n}\ncatch(java.io.UnsupportedEncodingException exc)\n{\njava.util.logging.Logger.getLogger(new Throwable() .getStackTrace()[0].getClassName()).log(Level.SEVERE, null, exc);\n}\n" +
"      if (str.length() != 0) \n" +
"      {\n" +
"      ros_msg["+current_position_variable+"] = 0;\n" +
"      this.soup = (str.length() % 2);\n" +
"      this.y = "+current_position_variable+";\n" +
"      while (y < ("+current_position_variable+"+str.length())) \n" +
"      {\n" +
"        char c = str.charAt(y);\n" +
"        if (!isHex(c)) {\n" +
"            \n" +
"          throw new IllegalArgumentException(\"string contains non-hex chars: \"+c);\n" +
"        }\n" +
"        if ((soup % 2) == 0) \n" +
"        {\n" +
"          ros_msg[soup >> 1] = (byte) (hexValue(c) << 4);\n" +
"        } \n" +
"        else \n" +
"        {\n" +
"          ros_msg[soup >> 1] += (byte) hexValue(c);\n" +
"        }\n" +
"        soup=soup+1;\n" +
"        y=y+1;\n" +        
"      }\n");
sb.append("      }\n");
        
/////////////////////////////////////        
        // Set the current position before proceeding...
        sb.append(current_position_variable+"="+current_position_variable+"+string_length;\n");       
        //
        
        return sb.toString();                      
    }
    
    /** Return Java Source Code converting a Java String[] to a fixed ROS string[] in byte[]. */ 
    public static String getFixedStringArray(String instruction_variable, String current_position_variable, int size)
    {
        StringBuilder sb = new StringBuilder();       
// TODO: add arrays_length, array_length, string_length, str_i, and     private static HexStringConverter hexStringConverter=HexStringConverter.getHexStringConverterInstance(); to global variable
        sb.append("this.arrays_length=0;\n");
        sb.append("this.str_i=0;\n");
        sb.append("while(str_i<");sb.append(instruction_variable);sb.append(".length)\n");
        sb.append("{\n");
        sb.append("this.arrays_length=arrays_length + ((");
        sb.append(instruction_variable);
        sb.append("[str_i].length()+1)/2);\nthis.str_i=str_i+1;\n}\n");
        sb.append("// arrays.length + prefix length is (String[].length*4)=ROS Fixed String[]length\n");
        sb.append("this.array_length=arrays_length + ("+instruction_variable+".length*4);\n");
        
        // Convert Fixed String[] to ROS Fixed string[] bytes  
        sb.append("this.str_i=0;\n");
        sb.append("try\n{\n");        
        sb.append("while(str_i<"+instruction_variable+".length)\n");
        sb.append("{\n");
        sb.append("    this.string_length=("+instruction_variable+"[str_i].length()+1)/2;\n");
        sb.append("    ros_msg["+current_position_variable+"+3] = (byte)((string_length >>> 24) & 0xff);\n");
        sb.append("    ros_msg["+current_position_variable+"+2] = (byte)((string_length >>> 16) & 0xff);\n");
        sb.append("    ros_msg["+current_position_variable+"+1] = (byte)((string_length >>> 8) & 0xff);\n");
        sb.append("    ros_msg["+current_position_variable+"] = (byte)((string_length >>> 0) & 0xff);\n");
        sb.append("    // increment the current_position to the first array byte index past the \n");
        sb.append("    "+current_position_variable+"="+current_position_variable+" + 4;\n");
        sb.append("\n");
//        sb.append("    ros_msg=HexToBytes.hexToBytes(hexStringConverter.stringToHex("+instruction_variable+"[str_i]),ros_msg,"+current_position_variable+");\n");
///////////////////////////////////// Manual Hex to Bytes
sb.append("      try\n{\nthis.str=hexStringConverter.stringToHex("+instruction_variable.concat("[str_i]")+");\n}\ncatch(java.io.UnsupportedEncodingException exc)\n{\njava.util.logging.Logger.getLogger(new Throwable() .getStackTrace()[0].getClassName()).log(Level.SEVERE, null, exc);\n}\n" +        
"      if (str.length() != 0) \n" +
"      {\n" +
"      ros_msg["+current_position_variable+"] = 0;\n" +
"      this.soup = (str.length() % 2);\n" +
"      this.y = "+current_position_variable+";\n" +
"      while (y < ("+current_position_variable+"+str.length())) \n" +
"      {\n" +
"        char c = str.charAt(y);\n" +
"        if (!isHex(c)) {\n" +
"            \n" +
"          throw new IllegalArgumentException(\"string contains non-hex chars: \"+c);\n" +
"        }\n" +
"        if ((soup % 2) == 0) \n" +
"        {\n" +
"          ros_msg[soup >> 1] = (byte) (hexValue(c) << 4);\n" +
"        } \n" +
"        else \n" +
"        {\n" +
"          ros_msg[soup >> 1] += (byte) hexValue(c);\n" +
"        }\n" +
"        soup=soup+1;\n" +
"        y=y+1;\n" +        
"      }\n");
sb.append("      }\n");
        
/////////////////////////////////////     
        
        sb.append("\n");
        sb.append("    // Set the current position\n");
        sb.append("    "+current_position_variable+"="+current_position_variable+"+string_length;\n");
        sb.append("    this.str_i=str_i+1;\n");
        sb.append("}\n");
        sb.append("}\ncatch(Exception e)\n{\ne.printStackTrace();\n}\n");
        
        // Set the current position before proceeding...
        sb.append(current_position_variable+"="+current_position_variable+"+array_length;\n");        
        return sb.toString();
    }
    
    /** Return Java Source Code converting a Java String[] to a variable ROS string[] in byte[]. */ 
    public static String getVariableStringArray(String instruction_variable, String current_position_variable)
    {
        StringBuilder sb = new StringBuilder();             
// TODO: add arrays_length, array_length, string_length, str_i, and     private static HexStringConverter hexStringConverter=HexStringConverter.getHexStringConverterInstance(); to global variable
        // set the string array length in bytes for setting the prefix bytes
        sb.append("arrays_length=0;\n");
        sb.append("this.str_i=0;\n");
        sb.append("while(str_i<");sb.append(instruction_variable);sb.append(".length)\n");
        sb.append("{\n");
        sb.append("arrays_length=arrays_length + ((");
        sb.append(instruction_variable);
        sb.append("[str_i].length()+1)/2);\nthis.str_i=str_i+1;\n}\n");
        sb.append("//  Set the array_length for the variable array prefix - arrays.length + prefix length is (String[].length*4)=ROS Fixed String[]length\n");
        // Set the array_length for the variable array prefix  
        sb.append("this.array_length=arrays_length + ("+instruction_variable+".length*4);\n");
  
        // Set the array length prefix bytes
        sb.append("ros_msg["+current_position_variable+"="+current_position_variable+"+3] = (byte)((array_length >>> 24) & 0xff);\n");
        sb.append("ros_msg["+current_position_variable+"="+current_position_variable+"+2] = (byte)((array_length >>> 16) & 0xff);\n");  
        sb.append("ros_msg["+current_position_variable+"="+current_position_variable+"+1] = (byte)((array_length >>> 8) & 0xff);\n");  
        sb.append("ros_msg["+current_position_variable+"] = (byte)((array_length >>> 0) & 0xff);\n");        
        // Convert Fixed String[] to ROS Fixed string[] bytes  
        sb.append("this.str_i=0;\n");
        sb.append("try\n{\n");        
        sb.append("while(str_i<"+instruction_variable+".length)\n");
        sb.append("{\n");
        sb.append("    this.string_length=("+instruction_variable+"[str_i].length()+1)/2;\n");
        sb.append("    ros_msg["+current_position_variable+"+3] = (byte)((string_length >>> 24) & 0xff);\n");
        sb.append("    ros_msg["+current_position_variable+"+2] = (byte)((string_length >>> 16) & 0xff);\n");
        sb.append("    ros_msg["+current_position_variable+"+1] = (byte)((string_length >>> 8) & 0xff);\n");
        sb.append("    ros_msg["+current_position_variable+"] = (byte)((string_length >>> 0) & 0xff);\n");
        sb.append("    // increment the current_position to the first array byte index past the \n");
        sb.append("    "+current_position_variable+"="+current_position_variable+" + 4;\n");
        sb.append("\n");
//        sb.append("    ros_msg=HexToBytes.hexToBytes(hexStringConverter.stringToHex("+instruction_variable+"[str_i]),ros_msg,"+current_position_variable+");\n");
///////////////////////////////////// Manual Hex to Bytes
sb.append("      try\n{\nthis.str=hexStringConverter.stringToHex("+instruction_variable.concat("[str_i]")+");\n}\ncatch(java.io.UnsupportedEncodingException exc)\n{\njava.util.logging.Logger.getLogger(new Throwable() .getStackTrace()[0].getClassName()).log(Level.SEVERE, null, exc);\n}\n" +       
"      if (str.length() != 0) \n" +
"      {\n" +
"      ros_msg["+current_position_variable+"] = 0;\n" +
"      this.soup = (str.length() % 2);\n" +
"      this.y = "+current_position_variable+";\n" +
"      while (y < ("+current_position_variable+"+str.length())) \n" +
"      {\n" +
"        char c = str.charAt(y);\n" +
"        if (!isHex(c)) {\n" +
"            \n" +
"          throw new IllegalArgumentException(\"string contains non-hex chars: \"+c);\n" +
"        }\n" +
"        if ((soup % 2) == 0) \n" +
"        {\n" +
"          ros_msg[soup >> 1] = (byte) (hexValue(c) << 4);\n" +
"        } \n" +
"        else \n" +
"        {\n" +
"          ros_msg[soup >> 1] += (byte) hexValue(c);\n" +
"        }\n" +
"        soup=soup+1;\n" +
"        y=y+1;\n" +        
"      }\n");
sb.append("      }\n");
        
/////////////////////////////////////   
        
        sb.append("\n");
        sb.append("    // Set the current position\n");
        sb.append("    "+current_position_variable+"="+current_position_variable+"+string_length;\n");
        sb.append("    this.str_i=str_i+1;\n");
        sb.append("}\n");
        sb.append("}\ncatch(Exception e)\n{\ne.printStackTrace();\n}\n");
        
        // Set the current position before proceeding...
        sb.append(current_position_variable+"="+current_position_variable+"+array_length;\n");
        return sb.toString(); 
    }        
    
    public static void main(String[] args)
    {
        System.out.println(BytesToHex.bytesToHex(getInt32(20)));
    }
    
    /** Get the ROS Message Prepend message/field length of bytes in a byte[]. */
    public byte[] getByteArrayLengthInBytes(int length)
    {
        final byte[] int32Bytes = new byte[4];
        int32Bytes[0] = (byte)((length >> 0) & 0xff);
        int32Bytes[1] = (byte)((length >> 8) & 0xff);  
        int32Bytes[2] = (byte)((length >> 16) & 0xff);  
        int32Bytes[3] = (byte)((length >> 24) & 0xff);         
        return int32Bytes;
    }    
     
    
}
