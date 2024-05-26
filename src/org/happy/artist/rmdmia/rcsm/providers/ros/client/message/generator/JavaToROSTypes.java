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
public class JavaToROSTypes 
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
    

    /** Return ROS Hex Float64. */
    public static String getHexFloat64(double value)
    {
        return Double.toHexString(value);
    }
    
    /** Return ROS float64 as byte[]. */
    public static byte[] getFloat64(double value)
    {
        final byte[] float64Bytes = new byte[8];
        long double64Long=Double.doubleToLongBits(value);
        float64Bytes[0] = (byte)((double64Long >> 56) & 0xff);
        float64Bytes[1] = (byte)((double64Long >> 48) & 0xff);        
        float64Bytes[2] = (byte)((double64Long >> 40) & 0xff);
        float64Bytes[3] = (byte)((double64Long >> 32) & 0xff);
        float64Bytes[4] = (byte)((double64Long >> 24) & 0xff);
        float64Bytes[5] = (byte)((double64Long >> 16) & 0xff);
        float64Bytes[6] = (byte)((double64Long >> 8) & 0xff);
        float64Bytes[7] = (byte)((double64Long >> 0) & 0xff);        
        return float64Bytes;
    }    
    
    /** Return Java float[] as ROS multiple combines ROS float64s. */ 
    public static byte[] getFloat64Array(double[] values)
    {
        final byte[] float64Bytes = new byte[8*values.length];
        long double64Long;
        for(int i=0;i<values.length;i++)
        {
            // get the float64 as a long
            double64Long=Double.doubleToRawLongBits(values[i]);
            // Assign each byte to the float32Bytes[]            
            float64Bytes[(4*values.length)+0] = (byte)((double64Long >> 56) & 0xff);
            float64Bytes[(4*values.length)+1] = (byte)((double64Long >> 48) & 0xff);
            float64Bytes[(4*values.length)+2] = (byte)((double64Long >> 40) & 0xff);
            float64Bytes[(4*values.length)+3] = (byte)((double64Long >> 32) & 0xff);
            float64Bytes[(4*values.length)+4] = (byte)((double64Long >> 24) & 0xff);
            float64Bytes[(4*values.length)+5] = (byte)((double64Long >> 16) & 0xff);
            float64Bytes[(4*values.length)+6] = (byte)((double64Long >> 8) & 0xff);
            float64Bytes[(4*values.length)+7] = (byte)((double64Long >> 0) & 0xff);             
        }
        return float64Bytes;
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
    
    /** Return ROS bool as byte[]. */  
    public static byte getBool(boolean value)
    {
        return (byte)(((short)(value?1:0) >>> 0) & 0xff);       
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
    
    /** Return ROS int8 as byte[]. */
    public static byte getInt8(short value)
    {
        return (byte)value;
    }    
    
    /** Return Java short[] as ROS multiple combines ROS int8. */ 
    public static byte[] getInt8Array(short[] values)
    {
        final byte[] int8Bytes = new byte[values.length];
        for(int i=0;i<values.length;i++)
        {
            // Assign each byte to the int8Bytes[]
            int8Bytes[i] = (byte)(values[i]);
        }
        return int8Bytes;
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
