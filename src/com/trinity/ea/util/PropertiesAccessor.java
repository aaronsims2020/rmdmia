package com.trinity.ea.util;

/**
 * Interface implemented to perform EAProperties reads/writes/updates.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2013 Happy Artist. All rights reserved.
 */
public interface PropertiesAccessor 
{
    
    /** Write the Properties File.
     * @return boolean true if file exists.
     */
    public boolean writeFile();
    
    /** Read the Properties File
     * @return boolean true if properties file exists.
     */
    public boolean readPropertiesFile();
    
    /**
     *
     * @param key
     * @param value
     * @return boolean true on success.
     */
    public boolean put(String key, String value);
    
    /**
     *
     * @param key
     * @return String, null on if key not found.
     */
    public String get(String key);
    
    /**
     *
     * @param key
     * @return boolean true on success
     */
    public boolean remove(String key);    
    
    /**
     *
     * @return EAProperties
     */
    public EAProperties getProperties();   
}
