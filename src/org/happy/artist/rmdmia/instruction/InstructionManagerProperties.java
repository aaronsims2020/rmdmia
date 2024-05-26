package org.happy.artist.rmdmia.instruction;

import com.trinity.ea.util.EAProperties;
import com.trinity.ea.util.PropertiesAccessor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.*;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Implemented by InstructionManager Object to manage registered Instruction Manager 
 * Provider configuration/initialization properties.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2003-2014 Happy Artist. All rights reserved.
 * 
 * implementation loosely copies Trinity Software, LLC RuleBuilder.java for 
 * EAProperties file implementation
 * 
 */
public class InstructionManagerProperties implements PropertiesAccessor 
{
    // Using the EAProperties implementation for sorted key arrays (intention to implement encryption 
    // to protect proprietary data).
    private static EAProperties props;
    // 
    private static String strPropertiesFileName = "instruction_manager.properties";     
    // Singleton instance of InstructionManagerProperties
    private static InstructionManagerProperties instructionManagerRegistry; 
    // 
    
    // Set the InstructionManagerProperties to private for singleton control.
    private InstructionManagerProperties()
    {
        // Implement SortedMap to get MovementProcessor Arrays in sorted order.
        InstructionManagerProperties.props = new EAProperties();
        // Nothing to do at this point
        if(!readPropertiesFile())
        {
            // Properties file could not be read and likely does not exist.
            // attempt to write a new file(This is primarily for debugging purposes.
            // TODO: once implementation is running work out actual file location 
            // and configuration of movementProcessors. Default to DummyInstructionFactory...
            //writeFile();
            Logger.getLogger(InstructionManagerProperties.class.getName()).log(Level.WARNING, "instruction_manager.properties not found.");
        }
    }
    
    // Return the singleton instance of InstructionManagerProperties.
    /**
     *
     * @return InstructionManagerProperties
     */
    public final static InstructionManagerProperties getInstance()
    {
        if(instructionManagerRegistry!=null)
        {
            return InstructionManagerProperties.instructionManagerRegistry;
        }
        return InstructionManagerProperties.instructionManagerRegistry = new InstructionManagerProperties();
    }
    
    /** Write the Properties File.
     * @return boolean true if property file exists.
     */
    public boolean writeFile()
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

    /** Read the Properties File
     * @return boolean true if property file exists
     */
    public boolean readPropertiesFile()
    {
       try
        {
            File propFile = new File(getFileName());
            Logger.getLogger(InstructionManagerProperties.class.getName()).log(Level.INFO, "Instruction Manager Properties: " + propFile.getAbsolutePath());
            if(propFile.exists()==true)
            {
                    try
                    {
                        FileInputStream fis = new FileInputStream(propFile);
                        props.load(fis);
                        return true;
                    }
                    catch(IOException ee)
                    {
                        System.out.println(ee.getMessage());
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
            e.printStackTrace();
                        System.out.println("Property file does not exist...");
            return false;
        }        
    }    
    
    /** Set the properties filename.
     * @param strFileName 
     */
    public static void setFileName(String strFileName)
    {
        strPropertiesFileName = strFileName;
    }
    
    /** Return the Properties File filename. Default Properties filename is
     * instruction_manager.properties.
     * 
     * @return String The file name.
     */
    public static String getFileName()
    {
        return strPropertiesFileName;
    }
    
    /**
     *
     * @param key
     * @param value
     * @return boolean true on success.
     */
    public boolean put(String key, String value)
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
    
    /**
     *
     * @param key
     * @return String value. Returns null if key not found.
     */
    public String get(String key)
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
    
    /**
     *
     * @param key
     * @return boolean true on success.
     */
    public boolean remove(String key)
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
    
    /**
     *
     * @return EAProperties
     */
    public EAProperties getProperties()
    {
        return props;
    }
   
//    public static void main(String[] args)
//    {
        // Test the InstructionManagerProperties...
//        InstructionManagerProperties.getInstance();
//    }
    
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
    // Comparator for use in sorting the Instruction Manager Properties Hashtable
    class HardwareIDKeyComparator implements Comparator
    {
        private String[] hid1;
        private String[] hid2;
        private int intHid1;
        private int intHid2;
        private int intMid1;
        private int intMid2;  
        @Override
        public int compare(Object hidAndmidKey, Object hidAndmidKey2)
        {
            // compare hardware id (hid), and if the same compare movement processor id (mid)
            // Step one cast hidAndmidKey/hidAndmidKey2 to String
            this.hid1 = ((String)hidAndmidKey).split("\\.");
            this.hid2 = ((String)hidAndmidKey2).split("\\.");
            this.intHid1 = Integer.valueOf(hid1[0]);
            this.intHid2 = Integer.valueOf(hid2[0]);
            try
            {
                this.intMid1 = Integer.valueOf(hid1[1]);
                this.intMid2 = Integer.valueOf(hid2[1]);             
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
                if(intMid1 > intMid2)
                {
                    return 1;
                }
                else if(intMid1 < intMid2)
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
