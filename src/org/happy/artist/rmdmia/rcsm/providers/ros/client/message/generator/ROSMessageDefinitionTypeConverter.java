package org.happy.artist.rmdmia.rcsm.providers.ros.client.message.generator;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import org.happy.artist.rmdmia.utilities.HexStringConverter;
import org.happy.artist.rmdmia.utilities.HexToBytes;

/** ROS Message Definition Variable Type Converter. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2015 Happy Artist. All rights reserved.
 *
 */
public class ROSMessageDefinitionTypeConverter
{
    // TODO: variable custom type Object arrays
    private final static char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
   
    /** Return the ROS string type length in bytes by Java String. */
    public static int getStringLength(String assignment_variable)
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
    
    /** Return the ROS string type length in bytes by Java String. */
    public static int getStringToBytesLength(String assignment_variable)
    {
        final int length=4+((assignment_variable.length()+1)/2);
        return length;
    }    
    
    public static int getStringArrayToBytesLength(String[] string_array)
    {
        final int prefix_length=string_array.length*4;
        int arrays_length=0;
        for(int i=0;i<string_array.length;i++)
        {
            arrays_length=arrays_length + ((string_array[i].length()+1)/2);
        }
        return arrays_length + prefix_length; 
    }
    
    /** Return Java String as ROS String. */ 
    public static String getString(byte[] bytes, int current_position)
    {
        int length=((bytes[current_position + 3] & 0xFF) << 24) | 
                   ((bytes[current_position + 2] & 0xFF) << 16) | 
                   ((bytes[current_position + 1] & 0xFF) << 8) | 
                   (bytes[current_position] & 0xFF); 
        // increment the current_position to the first array byte index past the 
        current_position=current_position + 4;
        // Get hex chars
        char[] hexChars = new char[length * 2];
        int v;
        for(int j=current_position;j<(current_position + length);j++)
        {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }           
        return convertHexToString(hexChars);        
    }    
    
    private static HexStringConverter hexStringConverter=HexStringConverter.getHexStringConverterInstance();
    /** Return Java byte[] - append String[] to Fixed String[] in byte[]. */ 
    public static byte[] getFixedStringArrayToBytes(byte[] bytes, int current_position, String[] stringArray) throws UnsupportedEncodingException
    {
        int j;
        int string_length;
        for(int i=0;i<stringArray.length;i++)
        {
            string_length=(stringArray[i].length()+1)/2;
            bytes[current_position+3] = (byte)((string_length >>> 24) & 0xff);
            bytes[current_position+2] = (byte)((string_length >>> 16) & 0xff);
            bytes[current_position+1] = (byte)((string_length >>> 8) & 0xff);
            bytes[current_position] = (byte)((string_length >>> 0) & 0xff);
            // increment the current_position to the first array byte index past the 
            current_position=current_position + 4;

            bytes=HexToBytes.hexToBytes(hexStringConverter.stringToHex(stringArray[i]),bytes,current_position);

            // Set the current position before proceeding...
            current_position=current_position+string_length;
        }
        return bytes;
    }         
    
    /** Return Java String[] as ROS multiple combines ROS Strings. */ 
    public static String[] getFixedStringArray(byte[] bytes, int current_position, int size)
    {
        
        final String[] stringArray = new String[size];
        int length;
        char[] hexChars;
        int v;
        for(int i=0;i<stringArray.length;i++)
        {
            length=((bytes[current_position + 3] & 0xFF) << 24) | 
                   ((bytes[current_position + 2] & 0xFF) << 16) | 
                   ((bytes[current_position + 1] & 0xFF) << 8) | 
                   (bytes[current_position] & 0xFF); 
            // increment the current_position to the first array byte index past the 
            current_position=current_position + 4;

            // Get hex chars
            hexChars = new char[length * 2];
            for(int j=current_position;j<(current_position + length);j++)
            {
                v = bytes[j] & 0xFF;
                hexChars[j * 2] = hexArray[v >>> 4];
                hexChars[j * 2 + 1] = hexArray[v & 0x0F];
            }           
            stringArray[i]=convertHexToString(hexChars);
            // increment current_position
            current_position=current_position + stringArray[i].length();
        }
        return stringArray;
    }     


    
    // Idea for this one came from http://stackoverflow.com/questions/12039341/hex-to-string-in-java-performance-is-too-slow
    private static String convertHexToString(char[] hex) 
    {
        StringBuilder builder = new StringBuilder();
        int hexLoopCount=0;
        int firstDigit;
        int lastDigit;
        int hexDec;
        while (hexLoopCount < hex.length - 1) 
        {
            firstDigit = Character.digit(hex[hexLoopCount], 16);
            lastDigit = Character.digit(hex[hexLoopCount + 1], 16);
            hexDec = firstDigit * 16 + lastDigit;
            builder.append((char)hexDec);
            // increment the loop count by 2.
            hexLoopCount=hexLoopCount + 2;
        }
        return builder.toString();
    }     
    
    /** Return Java String[] as ROS multiple combines ROS Strings. */ 
    public static String[] getVariableStringArray(byte[] bytes, int current_position)
    {
        // 
        int msg_length=((bytes[current_position + 3] & 0xFF) << 24) | 
                   ((bytes[current_position + 2] & 0xFF) << 16) | 
                   ((bytes[current_position + 1] & 0xFF) << 8) | 
                   (bytes[current_position] & 0xFF); 
        // increment the current_position to the first array byte index past the 
        current_position=current_position + 4;
        ArrayList<String> list = new ArrayList<String>();
        int length;
        char[] hexChars;
        int v;
        for(int i=current_position;i<(current_position + msg_length);i++)
        {
            length=((bytes[current_position + 3] & 0xFF) << 24) | 
                   ((bytes[current_position + 2] & 0xFF) << 16) | 
                   ((bytes[current_position + 1] & 0xFF) << 8) | 
                   (bytes[current_position] & 0xFF); 
            // increment the current_position to the first array byte index past the 
            current_position=current_position + 4;

            // Get hex chars
            hexChars = new char[length * 2];
            for(int j=current_position;j<(current_position + length);j++)
            {
                v = bytes[j] & 0xFF;
                hexChars[j * 2] = hexArray[v >>> 4];
                hexChars[j * 2 + 1] = hexArray[v & 0x0F];
            }           
            list.add(convertHexToString(hexChars));
            // increment current_position
            current_position=current_position + (hexChars.length/2);
        }
        String[] string_array = new String[list.size()];
        return list.toArray(string_array);
    }     
    
    
    /** Return Java float[] as ROS multiple combines ROS float32s. */ 
    public static float[] getFloat32Array(byte[] bytes, int current_position)
    {
        // 
        int length=((bytes[current_position + 3] & 0xFF) << 24) | 
                   ((bytes[current_position + 2] & 0xFF) << 16) | 
                   ((bytes[current_position + 1] & 0xFF) << 8) | 
                   (bytes[current_position] & 0xFF); 
        // increment the current_position to the first array byte index past the 
        current_position=current_position + 4;
        
        final float[] float32Array = new float[length/4];
        for(int i=current_position;i<(current_position + length);i++)
        {
            float32Array[i]=Float.intBitsToFloat(((bytes[current_position] & 0xFF) << 24) 
            | ((bytes[current_position=current_position+1] & 0xFF) << 16) 
            | ((bytes[current_position=current_position+1] & 0xFF) << 8) 
            | ((bytes[current_position=current_position+1] & 0xFF) << 0)); 
            // iterate current_position once
            current_position=current_position+1;
            i=i+3;
        }
        return float32Array;
    }   
    
    /** Return Java double[] as ROS multiple combines ROS float64s. */ 
    public static double[] getFloat64Array(byte[] bytes, int current_position)
    {
        // 
        int length=((bytes[current_position + 3] & 0xFF) << 24) | 
                   ((bytes[current_position + 2] & 0xFF) << 16) | 
                   ((bytes[current_position + 1] & 0xFF) << 8) | 
                   (bytes[current_position] & 0xFF); 
        // increment the current_position to the first array byte index past the 
        current_position=current_position + 4;
        
        final double[] float64Array = new double[length/8];
        for(int i=current_position;i<(current_position + length);i++)
        {
            // Assign each byte to the float32Bytes[]            
            float64Array[i]=Double.longBitsToDouble((long)((bytes[current_position] & 0xFFL) << 56) 
            | ((bytes[current_position=current_position+1] & 0xFFL) << 48) 
            | ((bytes[current_position=current_position+1] & 0xFFL) << 40) 
            | ((bytes[current_position=current_position+1] & 0xFFL) << 32)
            | ((bytes[current_position=current_position+1] & 0xFFL) << 24) 
            | ((bytes[current_position=current_position+1] & 0xFFL) << 16) 
            | ((bytes[current_position=current_position+1] & 0xFFL) << 8) 
            | ((bytes[current_position=current_position+1] & 0xFFL) << 0)); 
            // iterate current_position once
            current_position=current_position+1;
            i=i+7;
        }
        return float64Array;
    }     
    
    /** Return Java boolean[] from ROS bool[] as byte[]. */ 
    public static boolean[] getBoolArray(byte[] bytes, int current_position)
    {
        // 
        int length=((bytes[current_position + 3] & 0xFF) << 24) | 
                   ((bytes[current_position + 2] & 0xFF) << 16) | 
                   ((bytes[current_position + 1] & 0xFF) << 8) | 
                   (bytes[current_position] & 0xFF); 
        // increment the current_position to the first array byte index past the 
        current_position=current_position + 4;        
        
        final boolean[] booleans = new boolean[bytes.length];
        for(int i=current_position;i<(current_position + length);i++)
        {
            // Assign each byte to the int8Bytes[]
            booleans[i] = bytes[current_position]!=0;
            // iterate current_position once
            current_position=current_position+1;   
        }
        return booleans;
    }     
     
    /** Return Java long[] as ROS multiple combines ROS int64. */ 
    public static long[] getInt64Array(byte[] bytes, int current_position)
    {
        // 
        int length=((bytes[current_position + 3] & 0xFF) << 24) | 
                   ((bytes[current_position + 2] & 0xFF) << 16) | 
                   ((bytes[current_position + 1] & 0xFF) << 8) | 
                   (bytes[current_position] & 0xFF); 
        // increment the current_position to the first array byte index past the 
        current_position=current_position + 4;
        
        final long[] int64Array = new long[length/8];
        for(int i=current_position;i<(current_position + length);i++)
        {
            int64Array[i]=(long) ((bytes[current_position]<<56) | (bytes[current_position=current_position+1]<<48) | (bytes[current_position=current_position+1]<<40) | (bytes[current_position=current_position+1]<<32) | (bytes[current_position=current_position+1]<<24) | (bytes[current_position=current_position+1]<<16) | (bytes[current_position=current_position+1]<<8) | (bytes[current_position=current_position+1]));
            // iterate current_position once
            current_position=current_position+1;  
            i=i+7;
        }
        return int64Array;
    }       
    
    /** Return Java long[] as ROS multiple combines ROS uint64. */ 
    public static long[] getUInt64Array(byte[] bytes, int current_position)
    {
        // 
        int length=((bytes[current_position + 3] & 0xFF) << 24) | 
                   ((bytes[current_position + 2] & 0xFF) << 16) | 
                   ((bytes[current_position + 1] & 0xFF) << 8) | 
                   (bytes[current_position] & 0xFF); 
        // increment the current_position to the first array byte index past the 
        current_position=current_position + 4;        
        final long[] uint64Array = new long[length/8];
        for(int i=current_position;i<(current_position + length);i++)
        {
            uint64Array[current_position]=(long) ((bytes[current_position=current_position+1] & 0xFFL) << 56) | ((bytes[current_position=current_position+1] & 0xFFL) << 48) | ((bytes[current_position=current_position+1] & 0xFFL) << 40) | ((bytes[current_position=current_position+1] & 0xFFL) << 32) | ((bytes[current_position=current_position+1] & 0xFFL) << 24) | ((bytes[current_position=current_position+1] & 0xFFL) << 16) | (bytes[current_position=current_position+1] & 0xFFL) << 8 | ((bytes[current_position=current_position+1] & 0xFFL));
            // iterate current_position once
            current_position=current_position+1;
            i=i+7;
        }
        return uint64Array;
    }     
    
    /** Return Java long[] as ROS multiple combines ROS uint32. */ 
    public static long[] getUInt32Array(byte[] bytes, int current_position)
    {
        // 
        int length=((bytes[current_position + 3] & 0xFF) << 24) | 
                   ((bytes[current_position + 2] & 0xFF) << 16) | 
                   ((bytes[current_position + 1] & 0xFF) << 8) | 
                   (bytes[current_position] & 0xFF); 
        // increment the current_position to the first array byte index past the 
        current_position=current_position + 4;          
        final long[] uint32Array = new long[length/4];
        for(int i=current_position;i<(current_position + length);i++)
        {
            uint32Array[i]=(long) ((bytes[current_position] & 0xFFL) << 24) | ((bytes[current_position=current_position+1] & 0xFFL) << 16) | (bytes[current_position=current_position+1] & 0xFFL) << 8 | ((bytes[current_position=current_position+1] & 0xFFL));
            // iterate current_position once
            current_position=current_position+1;
            i=i+3;
        }
        return uint32Array;
    }  
    
    /** Return Java int[] from ROS int32[] of bytes. */ 
    public static int[] getInt32Array(byte[] bytes, int current_position)
    {
        // 
        int length=((bytes[current_position + 3] & 0xFF) << 24) | 
                   ((bytes[current_position + 2] & 0xFF) << 16) | 
                   ((bytes[current_position + 1] & 0xFF) << 8) | 
                   (bytes[current_position] & 0xFF); 
        // increment the current_position to the first array byte index past the 
        current_position=current_position + 4;  
        
        final int[] int32Array = new int[length/4];
        for(int i=current_position;i<(current_position + length);i++)
        {
            int32Array[i]=((bytes[current_position]<<24) | (bytes[current_position=current_position+1]<<16) | (bytes[current_position=current_position+1]<<8) | (bytes[current_position=current_position+1]));
            // iterate current_position once
            current_position=current_position+1;  
            i=i+3;
        }
        return int32Array;
    }     
    
    /** Return Java int[] as ROS multiple combines ROS uint16. */ 
    public static int[] getUInt16Array(byte[] bytes, int current_position)
    {
        // 
        int length=((bytes[current_position + 3] & 0xFF) << 24) | 
                   ((bytes[current_position + 2] & 0xFF) << 16) | 
                   ((bytes[current_position + 1] & 0xFF) << 8) | 
                   (bytes[current_position] & 0xFF); 
        // increment the current_position to the first array byte index past the 
        current_position=current_position + 4;  
        final int[] uint16Array = new int[length/2];
        for(int i=current_position;i<(current_position + length);i++)
        {
            uint16Array[i] = (bytes[current_position] & 0xFF) | ((bytes[current_position=current_position+1] & 0xFF) << 8);
            // iterate current_position once
            current_position=current_position+1; 
            i=i+1;
        }
        return uint16Array;
    }  
    
    /** Return Java short[] as ROS multiple combines ROS int16. */ 
    public static short[] getInt16Array(byte[] bytes, int current_position)
    {
        // 
        int length=((bytes[current_position + 3] & 0xFF) << 24) | 
                   ((bytes[current_position + 2] & 0xFF) << 16) | 
                   ((bytes[current_position + 1] & 0xFF) << 8) | 
                   (bytes[current_position] & 0xFF); 
        // increment the current_position to the first array byte index past the 
        current_position=current_position + 4;  
        final short[] shorts = new short[length/2];
        for(int i=current_position;i<(current_position + length);i++)
        {
            // Assign each byte to the int8Bytes[]
            shorts[i]=(short)((bytes[current_position] & (short)0xFF<<8) | 
                      (bytes[current_position=current_position+1] & (short)0xFF));
            // iterate current_position once
            current_position=current_position+1;         
            // update i
            i=i+1;
        }
        return shorts;
    }       
    
    /** Return Java short[] as ROS multiple combines ROS int8. */ 
    public static byte[] getInt8Array(byte[] bytes, int current_position)
    {
        // 
        int length=((bytes[current_position + 3] & 0xFF) << 24) | 
                   ((bytes[current_position + 2] & 0xFF) << 16) | 
                   ((bytes[current_position + 1] & 0xFF) << 8) | 
                   (bytes[current_position] & 0xFF); 
        // increment the current_position to the first array byte index past the 
        current_position=current_position + 4;  
        final byte[] byteArray = new byte[length];
        for(int i=current_position;i<(current_position + length);i++)
        {
            // Assign each byte to the int8Bytes[]
            byteArray[i] = bytes[current_position];
            // iterate current_position once
            current_position=current_position+1; 
        }        
        return byteArray;
    } 
    
    /** Return Java short[] as ROS multiple combines ROS uint8. */ 
    public static short[] getUInt8Array(byte[] bytes, int current_position)
    {
        // 
        int length=((bytes[current_position + 3] & 0xFF) << 24) | 
                   ((bytes[current_position + 2] & 0xFF) << 16) | 
                   ((bytes[current_position + 1] & 0xFF) << 8) | 
                   (bytes[current_position] & 0xFF); 
        // increment the current_position to the first array byte index past the 
        current_position=current_position + 4;          
        final short[] shortArray = new short[length];
        for(int i=current_position;i<(current_position + length);i++)
        {
            // Assign each byte to the int8Bytes[]
            shortArray[i] = (short)(bytes[current_position] & 0xFF);
            // iterate current_position once
            current_position=current_position+1; 
        }
        return shortArray;
    }      
}
