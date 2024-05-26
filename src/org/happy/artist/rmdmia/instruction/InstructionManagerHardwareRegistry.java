package org.happy.artist.rmdmia.instruction;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import com.trinity.ea.util.SortedEAProperties;
import java.io.*;
import java.util.Comparator;

/**
 * org.happy.artist.rmdmia.instruction.InstructionManagerHardwareRegistry class is 
 * implemented by the InstructionManager to manage registered instructions.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2003-2015 Happy Artist. All rights reserved.
 * 
 * implementation loosely copies Trinity Software, LLC RuleBuilder.java for 
 * EAProperties file implementation
 * 
 */
public class InstructionManagerHardwareRegistry 
{
    // Using the SortedEAProperties implementation for sorted key arrays (intention to implement encryption 
    // to protect proprietary data).
    private static SortedEAProperties props;
    // 
    private static String strRulesFileName = "conf/im/instruction_manager.eax";     
    // Singleton instance of InstructionManagerHardwareRegistry
    private static InstructionManagerHardwareRegistry instructionRegistry; 
    // 
    
    // Set the InstructionManagerHardwareRegistry to private for singleton control.
    private InstructionManagerHardwareRegistry()
    {
        // Implement SortedMap to get MovementProcessor Arrays in sorted order.
        InstructionManagerHardwareRegistry.props = new SortedEAProperties(new InstructionManagerHardwareRegistry.HardwareIDKeyComparator());
        // Nothing to do at this point
        if(!readPropertiesFile())
        {
            // Properties file could not be read and likely does not exist.
            // attempt to write a new file(This is primarily for debugging purposes.
            // TODO: once implementation is running work out actual file location 
            // and configuration of movementProcessors. Default to DummyGestureFactory...
            writeFile();
        }
    }
    
    // Return the singleton instance of GesturePool.
    public final static InstructionManagerHardwareRegistry getInstance()
    {
        if(instructionRegistry!=null)
        {
            return InstructionManagerHardwareRegistry.instructionRegistry;
        }
        return InstructionManagerHardwareRegistry.instructionRegistry = new InstructionManagerHardwareRegistry();
    }
    
    /** Write the Properties File. */
    public static boolean writeFile()
    {
        try
        {
            File propFile = new File(getFileName());
            if(propFile.exists()==true)
            {
                    try
                    {
                        FileOutputStream fos = new FileOutputStream(propFile,false);
                        props.store(fos);
                        return true;
                    }
                    catch(FileNotFoundException ee)
                    {
                        return false;
                    }
            }
            else
            {
                if(propFile.createNewFile()==true)
                {
                    try
                    {
                        FileOutputStream fos = new FileOutputStream(propFile,false);
                        props.store(fos);
                        return true;
                    }
                    catch(FileNotFoundException ee)
                    {
                        return false;
                    }
                }
                else
                {
                    return false;
                }
                
               
            }
        }
        catch(IOException e)
        {
            return false;
        }
    }

    /** Read the Properties File */
    public static boolean readPropertiesFile()
    {
       try
        {
            File propFile = new File(getFileName());
            if(propFile.exists()==true)
            {
                    try
                    {
                        FileInputStream fis = new FileInputStream(propFile);
                        //propFile);
                        props.load(fis);
                        return true;
                    }
                    catch(IOException ee)
                    {
                        return false;
                    }
            }
            else
            {
                 return false;
            }
        }
        catch(Exception e)
        {
            return false;
        }        
    }    
    
    /** Set the rules filename. */
    public static void setFileName(String strFileName)
    {
        strRulesFileName = strFileName;
    }
    
    /** Return the Rules File filename. Default Rules filename is rules.eax */
    public static String getFileName()
    {
        return strRulesFileName;
    }
    
    public static boolean put(String key, String value)
    {
         try
        {
            props.put(key,value);
            return true;
        }
        catch(Exception e)
        {
            return false;
        }       
    }
    
    public static String get(String key)
    {
        try
        {
            return (String)props.get(key);
        }
        catch(Exception e)
        {
            return null;
        }
    }   
    
    public static boolean remove(String key)
    {
        try
        {
            props.remove(key);
            return true;
        }
        catch(Exception e)
        {
            return false;
        }
    }    
    
    public static SortedEAProperties getProperties()
    {
        return props;
    }
   
    public static void main(String[] args)
    {
        // Test the InstructionManagerHardwareRegistry...
        InstructionManagerHardwareRegistry.getInstance();
    }
    
    // Copied most of the file reader method below from: http://www.java-gaming.org/index.php?topic=24639.0
 //   public final static String openFile(String file) throws IOException 
  //  {
  //      FileChannel channel = new FileInputStream(new File(file)).getChannel();
  //      ByteBuffer buffer = ByteBuffer.allocate((int) channel.size());
  //      channel.read(buffer);
  //      channel.close();
  //      return new String(buffer.array());
  //  }
    
 /*   public final static String openFile(String file)
    {
        FileInputStream fIn = null;
        FileChannel fChan = null;
        long fSize;
        ByteBuffer mBuf;

        final StringBuilder builder = new StringBuilder();
        try 
        {
            System.out.println("Starting to read the file using NIO");
            final long start_time = System.currentTimeMillis();
            fIn = new FileInputStream("c:\\test-nio.txt");
            fChan = fIn.getChannel();
            fSize = fChan.size();
            mBuf = ByteBuffer.allocate((int) fSize);
            fChan.read(mBuf);
            mBuf.rewind();
            for (int i = 0; i < fSize; i++) 
            {
                //System.out.print((char) mBuf.get());
                builder.append((char) mBuf.get());
            }
            fChan.close();
            fIn.close();
            System.out.println("Finished!  Read took " + (System.currentTimeMillis() - start_time) + " ms");
        }
        catch (final IOException exc) 
        {
            System.out.println(exc);
            System.exit(1);
        }
        finally 
        {
            if (fChan != null) 
            {
                fChan.close();
            }
            if (fIn != null) 
            {
                fIn.close();
            }
        }
    }    
   */     
    // Comparator for use in sorting the instruction_manager.eax Properties Hashtable
    class HardwareIDKeyComparator implements Comparator
    {
        private String[] hid1;
        private String[] hid2;
        private int intHid1;
        private int intHid2;
        private int intSid1;
        private int intSid2;  
        @Override
        public int compare(Object hidAndsidKey, Object hidAndsidKey2)
        {
            // compare hardware id (hid), and if the same compare movement processor id (sid)
            // Step one cast hidAndsidKey/hidAndsidKey2 to String
            this.hid1 = ((String)hidAndsidKey).split("\\.");
            this.hid2 = ((String)hidAndsidKey2).split("\\.");
            this.intHid1 = Integer.valueOf(hid1[0]);
            this.intHid2 = Integer.valueOf(hid2[0]);
            try
            {
                this.intSid1 = Integer.valueOf(hid1[1]);
            }
            catch(ArrayIndexOutOfBoundsException e)
            {
         //       System.out.println("Array Index out of bounds Exception, on Array length hid1: " + hid1.length + ", value: " + hid1[0]);
                System.out.println("Unsupported TreeMap/SortedMap method called. This Comparator has limited functionality.");
                e.printStackTrace();
            }
            try
            {
                this.intSid2 = Integer.valueOf(hid2[1]);             
            }
            catch(ArrayIndexOutOfBoundsException e)
            {
         //       System.out.println("Array Index out of bounds Exception, on Array length hid1: " + hid1.length + ", value: " + hid1[0]);
                System.out.println("Unsupported TreeMap/SortedMap method called. This Comparator has limited functionality.");
                e.printStackTrace();
            }
            
            if(intHid1 > intHid2)
            {
                return 1;
            }
            else if(intHid1 < intHid2)
            {
                return -1;
            }
            else
            {
                if(intSid1 > intSid2)
                {
                    return 1;
                }
                else if(intSid1 < intSid2)
                {
                    return -1;
                }
                else
                {
                    return 0;
                }
            }      
        }
    }     
}
