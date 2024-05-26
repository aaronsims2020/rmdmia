package org.happy.artist.rmdmia.utilities;

import java.io.UnsupportedEncodingException;

/**
 * class copied from: http://mdsaputra.wordpress.com/2010/09/03/convert-string-to-hex-to-string-java/
 * @author EtaYuy88
 */
public class HexStringConverter
{
    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();
    private static HexStringConverter hexStringConverter = null;
 
    private HexStringConverter()
    {}
 
    public static HexStringConverter getHexStringConverterInstance()
    {
        if (hexStringConverter==null) hexStringConverter = new HexStringConverter();
        return hexStringConverter;
    }
 
    public String stringToHex(String input) throws UnsupportedEncodingException
    {
        if (input == null) throw new NullPointerException();
        return asHex(input.getBytes());
    }
 
    public String hexToString(String txtInHex)
    {
        byte [] txtInByte = new byte [txtInHex.length() / 2];
        int j = 0;
        for (int i = 0; i < txtInHex.length(); i += 2)
        {
                txtInByte[j++] = Byte.parseByte(txtInHex.substring(i, i + 2), 16);
        }
        return new String(txtInByte);
    }
    
    private StringBuilder builder;
    private int hexLoopCount;
    private int firstDigit;
    private int lastDigit;
    private int hexDec;            
    // Idea for this one came from http://stackoverflow.com/questions/12039341/hex-to-string-in-java-performance-is-too-slow
public String convertHexToString(char[] hex) 
{
    this.builder = new StringBuilder();
    this.hexLoopCount=0;
    while (hexLoopCount < hex.length - 1) 
    {
        this.firstDigit = Character.digit(hex[hexLoopCount], 16);
        this.lastDigit = Character.digit(hex[hexLoopCount + 1], 16);
        this.hexDec = firstDigit * 16 + lastDigit;
        builder.append((char)hexDec);
        // increment the loop count by 2.
        this.hexLoopCount=hexLoopCount + 2;
    }
    return builder.toString();
}        
 
    private String asHex(byte[] buf)
    {
        char[] chars = new char[2 * buf.length];
        for (int i = 0; i < buf.length; ++i)
        {
            chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
            chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
        }
        return new String(chars);
    }
 
    // Need to give credit on this method being from the following URL: http://stackoverflow.com/questions/9655181/convert-from-byte-array-to-hex-string-in-java
    public static char[] bytesToHex(byte[] bytes) 
    {
        final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for ( int j = 0; j < bytes.length; j++ ) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return hexChars;
    }    

/////////////////////////////////////////////////////////
// hexToBytes method falls under     
// Code found at: http://www.java9.net/code/9881.html
/* @copyright Copyright (c) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */    
    
    /**
     * Convert a string of hex digits to a byte array, with the first
     * byte in the array being the MSB. The string passed in should be
     * just the raw digits (upper or lower case), with no leading
     * or trailing characters (like '0x' or 'h').
     * An odd number of characters is supported.
     * If the string is empty, an empty array will be returned.
     *
     * This is significantly faster than using
     *   new BigInteger(str, 16).toByteArray();
     * especially with larger strings. Here are the results of some
     * microbenchmarks done on a P4 2.8GHz 2GB RAM running
     * linux 2.4.22-gg11 and JDK 1.5 with an optimized build:
     *
     * String length        hexToBytes (usec)   BigInteger
     * -----------------------------------------------------
     * 16                       0.570                 1.43
     * 256                      8.21                 44.4
     * 1024                    32.8                 526
     * 16384                  546                121000
     */
    public static byte[] hexToBytes(String str) {
      byte[] bytes = new byte[(str.length() + 1) / 2];
      if (str.length() == 0) {
        return bytes;
      }
      bytes[0] = 0;
      int nibbleIdx = (str.length() % 2);
      for (int i = 0; i < str.length(); i++) {
        char c = str.charAt(i);
        if (!isHex(c)) {
          throw new IllegalArgumentException("string contains non-hex chars");
        }
        if ((nibbleIdx % 2) == 0) {
          bytes[nibbleIdx >> 1] = (byte) (hexValue(c) << 4);
        } else {
          bytes[nibbleIdx >> 1] += (byte) hexValue(c);
        }
        nibbleIdx++;
      }
      return bytes;
    }
    private static boolean isHex(char c) {
        return ((c >= '0') && (c <= '9')) ||
               ((c >= 'a') && (c <= 'f')) ||
               ((c >= 'A') && (c <= 'F'));
      }

      private static int hexValue(char c) {
        if ((c >= '0') && (c <= '9')) {
          return (c - '0');
        } else if ((c >= 'a') && (c <= 'f')) {
          return (c - 'a') + 10;
        } else {
          return (c - 'A') + 10;
        }
      }    
    
}