package org.happy.artist.rmdmia.rcsm.providers.ros.client.message.generator;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import org.happy.artist.rmdmia.utilities.BytesToHex;
import org.happy.artist.rmdmia.utilities.HexStringConverter;
import org.happy.artist.rmdmia.utilities.HexToBytes;

/**
 * org.happy.artist.rmdmia.rcsm.providers.ros.client.message.generator.ROSTypesToJava 
 * is a ROS Message Types utility that converts ROS 
 * Message Types to Java types.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2014-2015 Happy Artist. All rights reserved.
 */
public class ROSTypesToJava 
{
      private static HexStringConverter hexStringConverter = HexStringConverter.getHexStringConverterInstance(); 
    
    /** Return ROS String as byte[]. */
    public static byte[] getString(String value) throws UnsupportedEncodingException
    {
        return HexToBytes.hexToBytes(hexStringConverter.stringToHex(value));
    }
    
    /** Return Java float from ROS float32 byte[]. */
    public static float getFloat32(byte[] bytes)
    {
        return Float.intBitsToFloat(((bytes[0] & 0xFF) << 24) 
            | ((bytes[1] & 0xFF) << 16) 
            | ((bytes[2] & 0xFF) << 8) 
            | ((bytes[3] & 0xFF) << 0));    
    }    
    
    /** Return Java float[] as ROS multiple combines ROS float32s. */ 
    public static float[] getFloat32Array(byte[] bytes)
    {
        final float[] float32Array = new float[bytes.length/4];
        int b=0;        
        for(int i=0;i<bytes.length;i++)
        {
            float32Array[i]=Float.intBitsToFloat(((bytes[b] & 0xFF) << 24) 
            | ((bytes[b=b+1] & 0xFF) << 16) 
            | ((bytes[b=b+1] & 0xFF) << 8) 
            | ((bytes[b=b+1] & 0xFF) << 0)); 
            b=b+1;
        }
        return float32Array;
    }            
    
    /** Return Java double from ROS float64 byte[]. */
    /*public static double getFloat64(byte[] bytes)
    {
        return Double.longBitsToDouble((long)((bytes[0] & 0xFF) << 56) 
            | ((bytes[1] & 0xFF) << 48) 
            | ((bytes[2] & 0xFF) << 40) 
            | ((bytes[3] & 0xFF) << 32)
            | ((bytes[4] & 0xFF) << 24) 
            | ((bytes[5] & 0xFF) << 16) 
            | ((bytes[6] & 0xFF) << 8) 
            | ((bytes[7] & 0xFF) << 0)); 
    }    
    */
    public static double getFloat64(byte[] bytes)
    {
        return Double.longBitsToDouble(((bytes[0] & 0xFFL) << 56) 
            | ((bytes[1] & 0xFFL) << 48) 
            | ((bytes[2] & 0xFFL) << 40) 
            | ((bytes[3] & 0xFFL) << 32)
            | ((bytes[4] & 0xFFL) << 24) 
            | ((bytes[5] & 0xFFL) << 16) 
            | ((bytes[6] & 0xFFL) << 8) 
            | ((bytes[7] & 0xFFL) << 0)); 
    } 
    
    /** Return Java double[] as ROS multiple combines ROS float64s. */ 
    public static double[] getFloat64Array(byte[] bytes)
    {
        final double[] float64Array = new double[bytes.length/8];
        int b=0;
        for(int i=0;i<bytes.length;i++)
        {
            // Assign each byte to the float32Bytes[]            
            float64Array[i]=Double.longBitsToDouble((long)((bytes[b] & 0xFFL) << 56) 
            | ((bytes[b=b+1] & 0xFFL) << 48) 
            | ((bytes[b=b+1] & 0xFFL) << 40) 
            | ((bytes[b=b+1] & 0xFFL) << 32)
            | ((bytes[b=b+1] & 0xFFL) << 24) 
            | ((bytes[b=b+1] & 0xFFL) << 16) 
            | ((bytes[b=b+1] & 0xFFL) << 8) 
            | ((bytes[b=b+1] & 0xFFL) << 0)); 
            b=b+1;
        }
        return float64Array;
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
    
    /** Return ROS int64 as byte[] (Big Endian). */
    public static long getInt64(byte[] bytes)
    {
        return (long)((bytes[0]<<56) | (bytes[1]<<48) | (bytes[2]<<40) | (bytes[3]<<32) | (bytes[4]<<24) | (bytes[5]<<16) | (bytes[6]<<8) | (bytes[7]));
    }     
    
    /** Return Java boolean from ROS bool as byte. */  
    public static boolean getBool(byte value)
    {
        return value!=0;      
    }
    
    /** Return Java boolean[] from ROS bool[] as byte[]. */ 
    public static boolean[] getBoolArray(byte[] values)
    {
        final boolean[] booleans = new boolean[values.length];
        for(int i=0;i<values.length;i++)
        {
            // Assign each byte to the int8Bytes[]
            booleans[i] = values[i]!=0;
        }
        return booleans;
    }     
     
    /** Return Java long[] as ROS multiple combines ROS int64. */ 
    public static long[] getInt64Array(byte[] bytes)
    {
        final long[] int64Array = new long[bytes.length/8];
        int b=0;
        for(int i=0;i<bytes.length;i++)
        {
            int64Array[i]=(long) ((bytes[b]<<56) | (bytes[b=b+1]<<48) | (bytes[b=b+1]<<40) | (bytes[b=b+1]<<32) | (bytes[b=b+1]<<24) | (bytes[b=b+1]<<16) | (bytes[b=b+1]<<8) | (bytes[b=b+1]));
            b=b+1;
        }
        return int64Array;
    }       
    
    /** Return Java long for byte[] of ROS uint64 (Big Endian). */
    public static long getUInt64(byte[] value)
    {
        return (long) ((value[0] & 0xFFL) << 56) | ((value[1] & 0xFFL) << 48) | ((value[2] & 0xFFL) << 40) | ((value[3] & 0xFFL) << 32) | ((value[4] & 0xFFL) << 24) | ((value[5] & 0xFFL) << 16) | (value[6] & 0xFFL) << 8 | ((value[7] & 0xFFL));
    }     
    
     
    /** Return Java long[] as ROS multiple combines ROS uint64. */ 
    public static long[] getUInt64Array(byte[] values)
    {
        final long[] uint64Array = new long[values.length/8];
        int b=0;
        for(int i=0;i<values.length;i++)
        {
            uint64Array[b]=(long) ((values[b=b+1] & 0xFFL) << 56) | ((values[b=b+1] & 0xFFL) << 48) | ((values[b=b+1] & 0xFFL) << 40) | ((values[b=b+1] & 0xFFL) << 32) | ((values[b=b+1] & 0xFFL) << 24) | ((values[b=b+1] & 0xFFL) << 16) | (values[b=b+1] & 0xFFL) << 8 | ((values[b=b+1] & 0xFFL));
            b=b+1;
        }
        return uint64Array;
    }     
    
    /** Return Java long from ROS uint32 byte[]. */
    public static long getUInt32(byte[] value)
    {
        return (long) ((value[0] & 0xFFL) << 24) | ((value[1] & 0xFFL) << 16) | (value[2] & 0xFFL) << 8 | ((value[3] & 0xFFL));
    }     
    
    
    /** Return Java long[] as ROS multiple combines ROS uint32. */ 
    public static long[] getUInt32Array(byte[] values)
    {
        final long[] uint32Array = new long[values.length/4];
        int b=0;
        for(int i=0;i<values.length;i++)
        {
            uint32Array[i]=(long) ((values[b] & 0xFFL) << 24) | ((values[b=b+1] & 0xFFL) << 16) | (values[b=b+1] & 0xFFL) << 8 | ((values[b=b+1] & 0xFFL));
            b=b+1;
        }
        return uint32Array;
    }       
    
    /** Return ROS int32 as byte[] (Big Endian). */
    public static int getInt32(byte[] bytes)
    {
        return ((bytes[0]<<24) | (bytes[1]<<16) | (bytes[2]<<8) | (bytes[3]));
    }         

    /** Return Java int[] from ROS int32[] of bytes. */ 
    public static int[] getInt32Array(byte[] bytes)
    {
        final int[] int32Array = new int[bytes.length/4];
        int b=0;
        for(int i=0;i<bytes.length;i++)
        {
            int32Array[i]=((bytes[b]<<24) | (bytes[b=b+1]<<16) | (bytes[b=b+1]<<8) | (bytes[b=b+1]));
            b=b+1;
        }
        return int32Array;
    }           
    
    /** Return Java int as ROS uint16.  */
    public static int getUInt16(byte[] value)
    {   
        return (int) (value[0] & 0xFF) | ((value[1] & 0xFF) << 8);
    }     
    
    /** Return Java int[] as ROS multiple combines ROS uint16. */ 
    public static int[] getUInt16Array(byte[] values)
    {
        final int[] uint16Array = new int[values.length/2];
        int b=0;
        for(int i=0;i<uint16Array.length;i++)
        {
            uint16Array[i] = (values[b] & 0xFF) | ((values[b=b+1] & 0xFF) << 8);
            b=b+1;
        }
        return uint16Array;
    }      
    
    /** Return ROS int16 as short (Big Endian). */
    public static short getInt16(byte[] value)
    {
        return (short)((value[0]<<8) | (value[1]));
    }    
    
    /** Return Java short[] as ROS multiple combines ROS int16. */ 
    public static short[] getInt16Array(byte[] values)
    {
        final short[] shorts = new short[values.length/2];
        for(int i=0;i<shorts.length;i++)
        {
            // Assign each byte to the int8Bytes[]
            shorts[i]=(short)((values[(2*values.length) + 0] & (short)0xFF<<8) | (values[(2*values.length) + 1] & (short)0xFF));
        }
        return shorts;
    }         
    
    /** Return ROS int8 as byte[]. */
    public static byte getInt8(byte value)
    {
        return value;
    }    
    
    /** Return Java short[] as ROS multiple combines ROS int8. */ 
    public static byte[] getInt8Array(byte[] values)
    {
        return values;
    }     
    
    /** Return Java short from ROS uint8. */
    public static short getUInt8(byte value)
    {
        return (short) (value & (short)0xFF);
    } 

    
    /** Return Java short[] as ROS multiple combines ROS uint8. */ 
    public static short[] getUInt8Array(byte[] values)
    {
        final short[] shortArray = new short[values.length];
        for(int i=0;i<values.length;i++)
        {
            // Assign each byte to the int8Bytes[]
            shortArray[i] = (short)(values[i] & 0xFF);
        }
        return shortArray;
    } 

    public static void main(String[] args)
    {
        // getBool tests
        System.out.println("boolean true: "+getBool(JavaToROSTypes.getBool(true)));      
  
float tempFloat=3.6f;
double tempDouble=11.27d;
  
        // float32 Tests
        System.out.println("JavaToROSTypes Hex float32 3.6f: "+BytesToHex.bytesToHex(JavaToROSTypes.getFloat32(3.6f)));
        System.out.println("ROSTYpesToJava float32 3.6f Hex: 40666666: "+getFloat32(JavaToROSTypes.getFloat32(3.6f)));       
        
        // float64 Tests
        System.out.println("JavaToROSTypes Hex float64 11.27d: "+BytesToHex.bytesToHex(JavaToROSTypes.getFloat64(11.27d)));
        System.out.println("ROSTYpesToJava float64 10.10d Hex: 40268A3D70A3D70A: "+getFloat64(HexToBytes.hexToBytes("40268A3D70A3D70A")));

        long longBits = Long.valueOf("40268A3D70A3D70A",16).longValue(); 
double doubleValue = Double.longBitsToDouble(longBits);
System.out.println( "double float hexString is = " + doubleValue );

        // Int64 Tests
        System.out.println("JavaToROSTypes Hex Int64 7777: "+BytesToHex.bytesToHex(JavaToROSTypes.getInt64(7777)));
        System.out.println("ROSTYpesToJava Int64 7777 Hex: 00001E61: "+getInt64(JavaToROSTypes.getInt64(7777)));       
        
        // UInt64 Tests
        System.out.println("JavaToROSTypes Hex UInt64 7777: "+BytesToHex.bytesToHex(JavaToROSTypes.getUInt64(7777)));
        System.out.println("ROSTYpesToJava UInt64 7777 Hex: 00001E61: "+getUInt64(JavaToROSTypes.getUInt64(7777)));           
        
        
        
        // Int32 Tests
        System.out.println("JavaToROSTypes Hex Int32 7777: "+BytesToHex.bytesToHex(JavaToROSTypes.getInt32(7777)));
        System.out.println("ROSTYpesToJava Int32 7777 Hex: 00001E61: "+getInt32(JavaToROSTypes.getInt32(7777)));       
        
        // UInt32 Tests
        System.out.println("JavaToROSTypes Hex UInt32 7777: "+BytesToHex.bytesToHex(JavaToROSTypes.getUInt32(7777)));
        System.out.println("ROSTYpesToJava UInt32 7777 Hex: 00001E61: "+getUInt32(JavaToROSTypes.getUInt32(7777)));        
        
        // UInt16 & Int 16 tests
        System.out.println("UInt16 7777: "+getUInt16(JavaToROSTypes.getUInt16(7777)));
        System.out.println("JavaToROSTypes Hex Int16 7777: "+BytesToHex.bytesToHex(JavaToROSTypes.getInt16((short)7777)));
        System.out.println("ROSTYpesToJava Int16 7777 Hex: 1E61: "+(short)getInt16(HexToBytes.hexToBytes("1E61")));
        
        // UInt8 Tests
        byte[] bytes=new byte[1];
        bytes[0]=JavaToROSTypes.getUInt8((short)69);
        System.out.println("JavaToROSTypes Hex UInt8 Red: 69:"+ BytesToHex.bytesToHex(bytes)); 
        bytes[0]=JavaToROSTypes.getUInt8((short)86);
        System.out.println("JavaToROSTypes Hex UInt8 Green: 86: "+BytesToHex.bytesToHex(bytes));
        bytes[0]=JavaToROSTypes.getUInt8((short)255);
        System.out.println("JavaToROSTypes Hex UInt8 Blue: 255: "+BytesToHex.bytesToHex(bytes));           
        byte r=HexToBytes.hexToBytes("45")[0];
        byte g=HexToBytes.hexToBytes("56")[0];
        byte b=HexToBytes.hexToBytes("ff")[0];
        System.out.println("ROSTYpesToJava UInt8 Red Hex: 45: "+getUInt8(r)); 
        System.out.println("ROSTYpesToJava UInt8 Green Hex: 56: "+getUInt8(g));
        System.out.println("ROSTYpesToJava UInt8 Blue Hex: ff: "+getUInt8(b));
        
        System.out.println("Color message using ROSTYpes Message Length: " + getByteArrayLengthInBytes(HexToBytes.hexToBytes("03000000")) + ", Color:" +Arrays.toString(getUInt8Array(HexToBytes.hexToBytes("4556ff"))));
        
        //System.out.println(BytesToHex.bytesToHex(getInt32(20)))
    }
    
    /** Get the int length of the ROS Message Prepend message/field length of bytes in ROS message byte[]. */
    public static int getByteArrayLengthInBytes(byte[] length)
    {
        return ((length[3] & 0xFF) << 24) | ((length[2] & 0xFF) << 16)
    | ((length[1] & 0xFF) << 8) | (length[0] & 0xFF);   
    }    
}
