package org.happy.artist.rmdmia.roci;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.happy.artist.rmdmia.Controller;

/**  ROCIManager manages ROCI, and all the ROCI Interfaces/Properties 
 * files. Instantiate this Object to start the ROCIManager. 
 * 
 * @author Happy Artist
 * 
 * @copyright Copyright © 2012-2013 Happy Artist. All rights reserved.
 */
public class ROCIManager 
{
    private static ROCIProperties properties;
    private Map map;
    private String filePath="roci.properties";
    private final static String PROPERTIES_EXTENSION=".properties";
    private final static String FORWARD_SLASH="/";   
    private final static String UNDERSCORE_v="_v";  
    private final static String PROPERTY_NAME="name";
    private final static String PROPERTY_VERSION="version";
    private final static String PROPERTY_CLASS="class";
    private final static String PROPERTY_ENABLED="enabled";  
    
    // The plugin properties files folder path to find plugin properties files.
    private String pluginPropertiesFolderPath;
    private String[] classes;
    private String[] names;
    private double[] versions;
    private ROCIProvider[] providers;
    private boolean[] enabled;
    private final static String PLUGIN_PROPS_FOLDER_KEY="plugin_props_folder_key";
    private final static String PLUGIN_FOLDER_KEY="roci_plugins_folder";    
    private String provider_properties_file_name;
    private Properties prop;
    private File pluginDirectory;
    private String pluginsFolderPath;
    // The plugin files array
    private File[] files;    

    private int pos;
    private int currentID=0;
    private int arrayLength=0;
    private String currentKey;
    private String name;
    private Object[] outNames;
    private String[] keyArray;
    private FileInputStream fis;
    private File rootConfigurationDirectory;
 
    // Define and instantiate Logger
    private Logger logger = Logger.getLogger(ROCIManager.class.getName());
    // The RMDMIA Controller of ROCI.
    private Controller controller;
    // getProviderByName variables.
    private int providerCount;    
    // RCSM Properties File related variables.
    private FileOutputStream fos;
    private InputStream in;
    private byte[] buffer;
    private int len;
    private File confDir;
    private ROCIProvider provider_to_enable;
    // setProviderEnabledByName method variables.
    private int current_roci_id;
    // onInitialized variables
    private int postInitLoopCount;
    
    /** The ROCI Plugin properties folder is defined in the roci.properties file under the "roci_plugins_folder" key.
    * 
    * @param filePath - The roci.properties file path.
    * @param controller  
    */
    public ROCIManager(String filePath, Controller controller) 
    {
        this.filePath = filePath;
        this.controller=controller;
        logger.log(Level.INFO, "Starting ROCI..."); 
        if(controller.getControllerManager().IS_ANDROID)
        {
            // Is Android
            this.confDir = new File(((android.content.Context) controller.getControllerManager().context).getFilesDir().getAbsolutePath() + "/conf/");
            if (!confDir.exists()) {
                // If conf dir doesn't exist create conf dir, and rcsm and roci subfolders.
                logger.log(Level.INFO, "Created Dir conf/: " + String.valueOf(confDir.mkdir()));
                // Make the rcsm dir
                this.confDir = new File(((android.content.Context) controller.getControllerManager().context).getFilesDir().getAbsolutePath() + "/conf/rcsm/");
                logger.log(Level.INFO, "Created Dir conf/rcsm: " + String.valueOf(confDir.mkdir()));
                // Make the roci dir
                this.confDir = new File(((android.content.Context) controller.getControllerManager().context).getFilesDir().getAbsolutePath() + "/conf/roci/");
                logger.log(Level.INFO, "Created Dir conf/roci: " + String.valueOf(confDir.mkdir())); 
                // Make the pe dir
                this.confDir = new File(((android.content.Context) controller.getControllerManager().context).getFilesDir().getAbsolutePath() + "/conf/pe/");
                logger.log(Level.INFO, "Created Dir conf/pe: " + String.valueOf(confDir.mkdir())); 
                // TODO: copy the pe & roci & rcsm properties files via getResourceAsStream, or getResource.  
            } else if (!(this.confDir = new File(((android.content.Context) controller.getControllerManager().context).getFilesDir().getAbsolutePath() + "/conf/roci/")).exists()) {
                // Make the roci dir
                this.confDir = new File(((android.content.Context) controller.getControllerManager().context).getFilesDir().getAbsolutePath() + "/conf/roci/");
                logger.log(Level.INFO, "Created Dir conf/roci: " + String.valueOf(confDir.mkdir()));
            }
            if (!(this.confDir = new File(((android.content.Context) controller.getControllerManager().context).getFilesDir().getAbsolutePath() + "/conf/roci.properties")).exists()) {
                this.in = this.getClass().getResourceAsStream("/conf/roci.properties");
                // ROCI roci.properties file copy to file system from classpath.
                try {
                    this.fos = new FileOutputStream(((android.content.Context) controller.getControllerManager().context).getFilesDir().getAbsolutePath() + "/conf/roci.properties");
                    this.buffer = new byte[1024];
                    while ((this.len = in.read(buffer)) != -1) {
                        try {
                            fos.write(buffer, 0, len);
                        } catch (IOException e) {
                            logger.log(Level.SEVERE, "ROCI Manager: Input stream read error on: " + "conf/roci.properties", e);
                        }
                    }
                } catch (FileNotFoundException e) {
                    logger.log(Level.SEVERE, "ROCI Manager: Properties file not found: " + "conf/roci.properties", e);
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "ROCI Manager: File output stream initialization error on: " + "conf/roci.properties", e);
                } finally {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        logger.log(Level.WARNING, "ROCI Manager: Exception Closing File output stream: " + "conf/roci.properties", e);
                        this.fos = null;
                    }
                    try {
                        in.close();
                    } catch (IOException e) {
                        logger.log(Level.WARNING, "ROCI Manager: Exception Closing resource input stream: " + "conf/roci.properties", e);
                        this.in = null;
                    }
                }
            }
        } else {
            // Is Java
            this.confDir = new File(filePath).getParentFile();
            if (!confDir.exists()) {
                // If conf dir doesn't exist create conf dir, and rcsm and roci subfolders.
                logger.log(Level.INFO, "Created Dir conf/: " + String.valueOf(confDir.mkdir()));
                // Make the rcsm dir
                this.confDir = new File(confDir.getAbsolutePath() + "/rcsm/");
                logger.log(Level.INFO, "Created Dir conf/rcsm: " + String.valueOf(confDir.mkdir()));
                // Make the roci dir
                this.confDir = new File(confDir.getAbsolutePath() + "/roci/");
                logger.log(Level.INFO, "Created Dir conf/roci: " + String.valueOf(confDir.mkdir()));
                // Make the pe dir
                this.confDir = new File(confDir.getAbsolutePath() + "/pe/");
                logger.log(Level.INFO, "Created Dir conf/pe: " + String.valueOf(confDir.mkdir()));                
            } else if (!(this.confDir = new File(confDir.getAbsolutePath() + "/roci/")).exists()) {
                // Make the roci dir
                logger.log(Level.INFO, "Created Dir conf/roci: " + String.valueOf(confDir.mkdir()));
            }
            if (!(this.confDir = new File(filePath)).exists()) {
                this.in = this.getClass().getResourceAsStream("/conf/roci.properties");
                // ROCI roci.properties file copy to file system from classpath.
                try {
                    this.fos = new FileOutputStream(filePath);
                    this.buffer = new byte[1024];
                    while ((this.len = in.read(buffer)) != -1) {
                        try {
                            fos.write(buffer, 0, len);
                        } catch (IOException e) {
                            logger.log(Level.SEVERE, "ROCI Manager: Input stream read error on: " + "conf/roci.properties", e);
                        }
                    }
                } catch (FileNotFoundException e) {
                    logger.log(Level.SEVERE, "ROCI Manager: Properties file not found: " + "conf/roci.properties", e);
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "ROCI Manager: File output stream initialization error on: " + "conf/roci.properties", e);
                } finally {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        logger.log(Level.WARNING, "ROCI Manager: Exception Closing File output stream: " + "conf/roci.properties", e);
                        this.fos = null;
                    }
                    try {
                        in.close();
                    } catch (IOException e) {
                        logger.log(Level.WARNING, "ROCI Manager: Exception Closing resource input stream: " + "conf/roci.properties", e);
                        this.in = null;
                    }
                }
            }
        }    
        ROCIProperties.setFileName(filePath);
        ROCIManager.properties = ROCIProperties.getInstance();
        // Set the plugin properties folder path (plugin provider properties files directory path
        this.pluginPropertiesFolderPath = ROCIManager.properties.get(PLUGIN_PROPS_FOLDER_KEY);      
        this.pluginsFolderPath = ROCIManager.properties.get(PLUGIN_FOLDER_KEY);         
//        System.out.println("pluginPropertiesFolderPath: " + pluginPropertiesFolderPath);
        // Get the RCSM Properties.
        this.map = (Map) properties.getProperties();
    }

    // Default to true, and if not found set to false.
    private boolean providers_defined=true;        
    /** Initialize the Provider Manager. */ 
    public void initialize()
    {
        // TODO: Plugin Loading
        // 1. Get a directory Listing of the Plugins Folder
        this.files = controller.getControllerManager().getProviderFiles(this.pluginDirectory=new File(pluginsFolderPath), true);
        // 2. Read the /conf/rcsm Folder and look for associated Properties to each, and If an associated Properties file does not exist for a Jar, copy the associated properties file to the correct conf directory.
        this.rootConfigurationDirectory = new File(filePath).getAbsoluteFile();
        if(rootConfigurationDirectory.getParentFile()!=null&&rootConfigurationDirectory.getParentFile().getParentFile()!=null)
        {
            this.rootConfigurationDirectory=rootConfigurationDirectory.getParentFile().getParentFile();
        }
        else if(rootConfigurationDirectory.getParentFile()!=null)
        {
            this.rootConfigurationDirectory=rootConfigurationDirectory.getParentFile();
        }
        for(int i=0;i<files.length;i++)
        {
            if(files[i]!=null)
            {
                try
                {                    
                    controller.getControllerManager().extractProviderConfiguration(rootConfigurationDirectory, files[i], pluginPropertiesFolderPath, properties);
                    controller.getControllerManager().addProvider(files[i]);
                }
                catch(Exception e)
                {
                    logger.log(Level.WARNING,"Exception thrown attempting to generate configuration file for RCSM Provider. File path: ".concat(files[i].getAbsolutePath()),e);
                }
            }
        }
        // 3. Load all Plugins in the plugins directory.
// TODO: (DONE) Add support for #.enabled property, and default set all providers to false. For now all plugins will be automatically be loaded to classpath, but not available unless configured in the RCSM Properties file..        
        // 4. Cycle through the RCSM loading process. 

        this.outNames = map.keySet().toArray();
//        System.out.println("Properties size: " + outNames.length);
        this.currentID = 0;
        this.arrayLength = 0;
        this.pos = 0;
        // Obtain the number of registered ROCI Plugins.
        while (pos < outNames.length) 
        {
            this.name = (String) outNames[pos];
            this.keyArray = name.split("\\.");
            //System.out.println("keyArray length: " + keyArray.length);
            //System.out.println("key: " + name);
            if (keyArray.length == 2) 
            {
                // Is a ROCI Plugin Provider Property.
                try 
                {
                    // ROCI_ID
                    this.currentID = Integer.parseInt(keyArray[0]);
                    if (currentID > arrayLength) 
                    {
                        this.arrayLength = currentID;
                    }
                } 
                catch (NumberFormatException e) 
                {
                    // Ignore this property key. It does not match requirements
                    logger.log(Level.WARNING, "roci.properties key for plugin property does not contain a valid roci id in the format of #.key=. Actual key value is: " + keyArray[0]);
                }
            }
            this.pos=pos + 1;
        }
        //System.out.println("Array length: " + (arrayLength + 1));
        // Initialize the ROCI Properties Arrays
        this.classes = new String[arrayLength + 1];
        this.names = new String[arrayLength + 1];
        this.versions = new double[arrayLength + 1];
        this.providers = new ROCIProvider[arrayLength + 1];
        this.enabled = new boolean[arrayLength + 1];
        
        this.pos=0;
        this.currentID = 0;
        while (pos < outNames.length) 
        {
            this.name = (String) outNames[pos];
            this.keyArray = name.split("\\.");

            if (keyArray.length == 2) 
            {
                // Is a ROCI Plugin Provider Property.
                try 
                {
                    // ROCI_ID
                    this.currentID = Integer.parseInt(keyArray[0]);
                    this.currentKey = keyArray[1];
                    if (currentKey.equals(PROPERTY_NAME)) 
                    {
                        this.names[currentID] = (String) map.get(name);
                    } 
                    else if (currentKey.equals(PROPERTY_VERSION)) 
                    {
                        this.versions[currentID] = Double.parseDouble((String) map.get(name));
                    } 
                    else if (currentKey.equals(PROPERTY_CLASS)) 
                    {
                        this.classes[currentID] = (String) map.get(name);
                    }
                   else if (currentKey.equals(PROPERTY_ENABLED)) 
                    {
                        if(((String)map.get(name))!=null&&((String)map.get(name)).toLowerCase().equals("true"))
                        {
                            this.enabled[currentID] = true;
                        }
                        else
                        {
                            this.enabled[currentID] = false;
                        }
                    }                       
                } 
                catch (NumberFormatException e) 
                {
                    // Ignore this property key. It does not match requirements
                    logger.log(Level.WARNING, "roci.properties key for plugin property does not contain a valid roci id in the format of #.key=. Actual key value is: " + keyArray[0]);
                }
            }
            this.pos = pos + 1;
        }

        
        
        // Initialize the Providers.
        currentID = 0;
        this.pos = 0;
        while (pos < providers.length) 
        {
            if (versions[pos] != 0) 
            {
                // Use file format is with version number.
                this.provider_properties_file_name = this.names[pos] + UNDERSCORE_v + String.valueOf(this.versions[pos]) + PROPERTIES_EXTENSION;
            } 
            else 
            {
                // Use file format without version number.
                this.provider_properties_file_name = this.names[pos] + PROPERTIES_EXTENSION;
            }

            if(3<outNames.length)
            {
                try 
                {
                    this.providers[pos] = (ROCIProvider) Class.forName(classes[pos]).newInstance();
                    // Set the Controller ID
                    this.providers[pos].setController(controller);
                    // Set the ROCI ID.
                    this.providers[pos].setID(pos);
                    // Set the ROCI Provider Properties
                    try 
                    {
                        this.prop = new Properties();
                            logger.log(Level.INFO, "Starting ROCI Plugin: " + provider_properties_file_name.substring(0,provider_properties_file_name.indexOf(".properties")) + "\n" + provider_properties_file_name.substring(0,provider_properties_file_name.indexOf(".properties")) + " Properties: " + pluginPropertiesFolderPath.concat(FORWARD_SLASH).concat(provider_properties_file_name));
                            //load a properties file
                            prop.load(this.fis=new FileInputStream(pluginPropertiesFolderPath.concat(FORWARD_SLASH).concat(provider_properties_file_name)));
                            this.providers[pos].setProperties(prop);
                            // Set the Properties File Path.
                            this.providers[pos].setPropertiesFilePath(pluginPropertiesFolderPath.concat(FORWARD_SLASH).concat(provider_properties_file_name));
                        } 
                        catch (FileNotFoundException ex) 
                        {
// TODO: Remove File debug code                            
File temp = new File(pluginPropertiesFolderPath.concat(FORWARD_SLASH).concat(provider_properties_file_name));
System.out.println("Absolute Path: " + temp.getAbsolutePath());
                            ex.printStackTrace();
// TODO: End                            
                            logger.log(Level.WARNING, "ROCI Provider Properties File not found: " + pluginPropertiesFolderPath + "/" + provider_properties_file_name);
                        } 
                        catch (IOException ex) 
                        {
                            logger.log(Level.SEVERE, null, ex);
                        }
                } 
                catch (ClassNotFoundException ex) 
                {
                    logger.log(Level.SEVERE, null, ex);
                } 
                catch (InstantiationException ex) 
                {
                    logger.log(Level.SEVERE, null, ex);
                } 
                catch (IllegalAccessException ex) 
                {
                    Logger.getLogger(ROCIManager.class.getName()).log(Level.SEVERE, null, ex);
                }

                // Set the ROCI ID;
                this.providers[pos].setID(pos);

/*                // Initialize the ROCI Provider
                if(enabled[pos])
                {
                    // ROCI plugin enabled
                    this.providers[pos].initialize();
                }
                else
                {
                    // Plugin Provider Disabled
                    logger.log(Level.INFO,("ROCI Plugin: ".concat(names[pos].concat("_v").concat(String.valueOf(versions[pos]))).concat(" is disabled.")));
                }                        */
            }
            else
            {
                this.providers_defined=false;
                logger.log(Level.INFO, "No ROCI Plugins are defined.");                    
            }
            this.pos = pos + 1;
        }
        if(providers_defined)
        {         
            // Initialize the ROCI Plugins
            this.pos=0;
            this.currentID = 0;
            while (pos < providers.length) 
            {
                // Initialize the ROCI Provider
                if(enabled[pos])
                {
                    // ROCI plugin enabled
                    this.providers[pos].initialize();
                }
                else
                {
                    // Plugin Provider Disabled
                    logger.log(Level.INFO,("ROCI Plugin: ".concat(names[pos].concat("_v").concat(String.valueOf(versions[pos]))).concat(" is disabled.")));
                }  
                this.pos = pos + 1;    
            }        
        }
    }   
    
    /** Return an array of ROCI Provider name values from the roci.properties 
     * where ROCI ID = array element index.
     * 
     * @return String[] The complete list of roci.properties name elements by ROCI ID.
     */
    public String[] getProviderNames()
    {
        return names;
    }
    
    /** Return an array of ROCI Provider version values from the roci.properties 
     * where ROCI ID = array element index.
     * 
     * @return double[] The complete list of roci.properties version elements by ROCI ID.
     */
    public double[] getProviderVersions()
    {
        return versions;
    }
    
    /** Return an array of ROCI Provider class values from the roci.properties 
     * where ROCI ID = array element index.
     * 
     * @return String[] The complete list of roci.properties class elements by ROCI ID.
     */
    public String[] getProviderClasses()
    {
        return classes;
    }
    
    /** Return an array of ROCI Provider enabled values from the roci.properties 
     * where ROCI ID = array element index.
     * 
     * @return boolean[] The complete list of roci.properties enabled elements by ROCI ID.
     */
    public boolean[] getProviderEnabled()
    {
        return enabled;
    }    
    
    /** Return a list of ROCI Plugin Providers.
     * 
     * @return ROCIProvider[] The complete list of Registered Providers.
     */
    public ROCIProvider[] getProviders()
    {
        return providers;
    }

    /** Return an roci provider by roci_id.
     * @param roci_id
     * @return ROCIProvider 
     */
    public ROCIProvider getProvider(int roci_id)
    {
        return providers[roci_id];
    }
    
    /** Return an roci plugin provider by roci_id.
     * @param provider_name
     * @return ROCIProvider 
     */
    public ROCIProvider getProviderByName(String provider_name)
    {
        this.providerCount=0;
        if(providers==null)
        {
            return null;
        }          
        while(providerCount<providers.length)
        {
            if(names[providerCount].equals(provider_name))
            {
                return providers[providerCount];
            }
            this.providerCount=providerCount + 1;
        }
        return null;
    }        

    /** Return an roci plugin provider roci_id, by the provider name.
     * @param provider_name
     * @return roci_id int returns -1 of provider not found by name.
     */
    private int getProviderIDByName(String provider_name)
    {
        this.providerCount=0;
        while(providerCount<providers.length)
        {
            if(names[providerCount].equals(provider_name))
            {
                return providerCount;
            }
            this.providerCount=providerCount + 1;
        }
        return -1;
    }      
    
    /** Set the provider to enabled/disabled by Name. Setting a provider to
     * enabled will shutdown that provider, and set the provider Object to null.
     * @param provider_name
     * @param isEnabled boolean
     * @param saveStartupProperties boolean - set to true if this provider 
     * should be disabled at startup from this point forward, and it is  
     * enabled on startup.
     * 
     * @return true if success, return false on failure to write to properties 
     * file, if input parameter saveStartupProperties is true, or if provider_name 
     * does not exist.  
     */
    public boolean setProviderEnabledByName(String provider_name, boolean isEnabled, boolean saveStartupProperties)
    {
        // Obtain a reference to the provider by name.
        this.provider_to_enable = getProviderByName(provider_name);
        // return false if a roci_if does not exist for provider_name
        if(provider_to_enable==null||(this.current_roci_id=getProviderIDByName(provider_name))==-1)
        {
            return false;
        }
        // if save startup properties is true change default enabled value in file.
        if(saveStartupProperties)
        {
           properties.put(String.valueOf(current_roci_id).concat(".").concat(PROPERTY_ENABLED),String.valueOf(saveStartupProperties));
           if(!properties.writeFile())
           {
               logger.log(Level.WARNING,"Failed to change roci.properties key value: " + String.valueOf(current_roci_id).concat(".").concat(PROPERTY_ENABLED) + ", to value: " + String.valueOf(saveStartupProperties));
           }
        }
        try
        {
            // If provider is set to disabled, shut it down.
            if(isEnabled==false&&enabled[current_roci_id])
            {
                // Shutdown the provider if it is not already shutdown.
                provider_to_enable.shutdown();
            }
            else if(isEnabled==true&&!enabled[current_roci_id])
            {
                // Start the provider if it is not already running.
                provider_to_enable.initialize();
            }
        }
        catch(Exception e)
        {
            logger.log(Level.WARNING,"Exception thrown while changing ROCI Provider enabled status of: " + names[current_roci_id]);
        }
        // Cleanup the method references.
        this.provider_to_enable = null;
        return true;
    }    

    /** Internal method. onInitialized is called by the Controller following all Provider 
     * initialization.
     */     
    public void onInitialized()
    {
        this.postInitLoopCount=0;
        if(providers!=null)
        {
            while(postInitLoopCount<providers.length)
            {
                try
                {
                    if(providers[postInitLoopCount]!=null&&enabled[postInitLoopCount])
                    {
                       providers[postInitLoopCount].onInitialized();
                    }
                }
                catch(NullPointerException e)
                {
                    logger.log(Level.WARNING,"Exception thrown while attempting to call onInitialized() method of provider name: " + names[current_roci_id] + ", provider not instantiated.", e);       
                }               
                catch(Exception e)
                {
                    logger.log(Level.WARNING,"Exception thrown while attempting to call onInitialized() method of provider name: " + names[current_roci_id],e);                   
                }
                // increment loop count.
                this.postInitLoopCount=postInitLoopCount + 1;
            }
        }
    }   
//    public static void main(String[] args)
//    {
        // Initialize ROCIManager to the following ROCI Properties file, and roci plugin provider properties directory path.
//        ROCIManager roci = new ROCIManager("conf/roci.properties");
        
//    }
}
