package org.happy.artist.rmdmia.utilities;

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
//package com.google.gdata.util.common.base;


/**
 * Some common string manipulation utilities.
 */
public class HexToBytes{

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
            
          throw new IllegalArgumentException("string contains non-hex chars: "+c);
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
     * 
     * @return Return byte[] - the array_to_write_bytes with string data appended
     */
    public static byte[] hexToBytes(String str, byte[] array_to_write_bytes, int array_start_position) 
    {
      if (str.length() == 0) 
      {
        return array_to_write_bytes;
      }
      array_to_write_bytes[array_start_position] = 0;
      int nibbleIdx = (str.length() % 2);
      int i = array_start_position;
      while (i < (array_start_position+str.length())) 
      {
        char c = str.charAt(i);
        if (!isHex(c)) {
            
          throw new IllegalArgumentException("string contains non-hex chars: "+c);
        }
        if ((nibbleIdx % 2) == 0) 
        {
          array_to_write_bytes[nibbleIdx >> 1] = (byte) (hexValue(c) << 4);
        } 
        else 
        {
          array_to_write_bytes[nibbleIdx >> 1] += (byte) hexValue(c);
        }
        nibbleIdx=nibbleIdx+1;
        i=i+1;
      }
      return array_to_write_bytes;
    }      
}