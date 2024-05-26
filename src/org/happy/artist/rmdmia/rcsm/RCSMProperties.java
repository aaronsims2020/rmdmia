package org.happy.artist.rmdmia.rcsm;

import com.trinity.ea.util.EAProperties;
import com.trinity.ea.util.PropertiesAccessor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.*;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implemented by the RCSMProvider abstract class, and initialized by 
 * the RCSM Manager to manage registered RCSM Plugin 
 * configuration/initialization.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2003-2013 Happy Artist. All rights reserved.
 * 
 * implementation loosely copies Trinity Software, LLC RuleBuilder.java for 
 * EAProperties file implementation
 * 
 */
public class RCSMProperties implements PropertiesAccessor
{
    // Using the SortedEAProperties implementation for sorted key arrays (intention to implement encryption 
    // to protect proprietary data).
    private static EAProperties props;
    // 
    private static String strRulesFileName = "rcsm.properties";     
    // Singleton instance of RCSMProperties
    private static RCSMProperties rcsmRegistry;     
    
    // Set the RCSMProperties to private for singleton control.
    private RCSMProperties()
    {
        // Implement SortedMap to get MovementProcessor Arrays in sorted order.
        RCSMProperties.props = new EAProperties();
        // Nothing to do at this point
        if(!readPropertiesFile())
        {
            // Properties file could not be read and likely does not exist.
            // attempt to write a new file(This is primarily for debugging purposes.
            // TODO: once implementation is running work out actual file location 
            // and configuration of movementProcessors. Default to DummyGestureFactory...
            //writeFile();
            System.out.println("rcsm.properties not found.");
        }
    }
    
    // Return the singleton instance of RCSMProperties.
    /**
     *
     * @return Singleton instance of the RCSMProperties Object.
     */
    public final static RCSMProperties getInstance()
    {
        if(rcsmRegistry!=null)
        {
            return RCSMProperties.rcsmRegistry;
        }
        return RCSMProperties.rcsmRegistry = new RCSMProperties();
    }
    
    /** Write the Properties File.
     * @return boolean true if file exists.
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
     * @return boolean true if properties file exists.
     */
    public boolean readPropertiesFile()
    {
       try
        {
            File propFile = new File(getFileName());
            Logger.getLogger(RCSMProperties.class.getName()).log(Level.INFO, "RCSM Properties: " + propFile.getAbsolutePath());
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
    
    /** Set the rules filename.
     * @param strFileName file name
     */
    public static void setFileName(String strFileName)
    {
        strRulesFileName = strFileName;
    }
    
    /** Return the Rules File filename. Default Rules filename is rules.eax
     * @return String file name.
     */
    public static String getFileName()
    {
        return strRulesFileName;
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
     * @return String, null on if key not found.
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
     * @return boolean true on success
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
        // Test the RCSMProperties...
//        RCSMProperties.getInstance();
//    }
       
    // Comparator for use in sorting the MovementProcessors.eax Properties Hashtable
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
