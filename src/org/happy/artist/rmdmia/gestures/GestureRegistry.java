package org.happy.artist.rmdmia.gestures;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import com.trinity.ea.util.EAProperties;
import java.io.*;

/**
 * org.happy.artist.rmdmia.rcsm.GestureRegistry class is implemented by the MovementProcessorManager 
 * to manage registered Gestures by designated package name, gesture quantity, 
 * and associated robot function grouping for MovementProcessorPool initialization in the 
 * MovementProcesssorManager. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2003-2012 Happy Artist. All rights reserved.
 * 
 * implementation losely copies Trinity Software, LLC RuleBuilder.java for 
 * EAProperties file implementation
 * 
 */
public class GestureRegistry 
{
    // Using the EAProperties implementation (intention to implement encryption 
    // to protect proprietary data).
    private static EAProperties props = new EAProperties();
    // 
    private static String strRulesFileName = "conf/gestures.eax";     
    // Singleton instance of GestureRegistry
    private static GestureRegistry gestureRegistry; 
    // 
    
    // Set the GestureRegistry to private for singleton control.
    private GestureRegistry()
    {
        // Nothing to do at this point
        if(!readPropertiesFile())
        {
            // Properties file could not be read and likely does not exist.
            // attempt to write a new file(This is primarily for debugging purposes.
            // TODO: once implementation is running work out actual file location 
            // and configuration of gestures. Default to DummyGestureFactory...
            writeFile();
        }
    }
    
    // Return the singleton instance of GesturePool.
    public final static GestureRegistry getInstance()
    {
        if(gestureRegistry!=null)
        {
            return GestureRegistry.gestureRegistry;
        }
        return GestureRegistry.gestureRegistry = new GestureRegistry();
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
    
    public static EAProperties getProperties()
    {
        return props;
    }
   
    public static void main(String[] args)
    {
        // Test the GestureRegistry...
        GestureRegistry.getInstance();
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
}
