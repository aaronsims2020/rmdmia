package org.happy.artist.rmdmia.rcsm;

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

/** Robot Control System Messenger (RCSMManager) communication messaging
 * interface/API between the Robot Hardware Controller and the Robotic 
 * Mission Decision Manager Intelligent Agent (RMDMIA).
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2012-2013 Happy Artist. All rights reserved.
 */
public class RCSMManager
{
    private RCSMProperties properties;
    private Map map;
    private String filePath="rcsm.properties";
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
    private RCSMProvider[] providers;
    private boolean[] enabled;
    private final static String PLUGIN_PROPS_FOLDER_KEY="plugin_props_folder_key";
    private final static String PLUGIN_FOLDER_KEY="rcsm_plugins_folder";
    private String pluginsFolderPath;
    private String provider_properties_file_name;
    private Properties prop;
    private File pluginDirectory;
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
    // The RCSM Controller reference. 
    private Controller controller;
// TODO: Discover Plugins...    
    // getProviderByName variables.
    private int providerCount;
    // RCSMProvider constructor
    private File rootConfigurationDirectory;
    private File confDir;
    private final static Logger logger = Logger.getLogger(RCSMManager.class.getName());  
    // RCSM Properties File related variables.
    private FileOutputStream fos;
    private InputStream in;
    private byte[] buffer;
    private int len;
    // setProviderEnabledByName method variables.
    private RCSMProvider provider_to_enable;
    private int current_rcsm_id; 
    // onInitialized method variables. 
    private int postInitLoopCount;
     
    /** The RCSM Plugin properties folder is defined in the rcsm.properties file under the "rcsm_plugins_folder" key.
    * 
    * @param filePath - The rcsm.properties file path.
    * @param controller - The rcsm Controller reference.
    */
    public RCSMManager(String filePath, Controller controller)
    {
        this.filePath = filePath;
        this.controller=controller;
        logger.log(Level.INFO, "Starting RCSM..."); 
        if(controller.getControllerManager().IS_ANDROID)
        {
            // Is Android
            this.confDir = new File(((android.content.Context) controller.getControllerManager().context).getFilesDir().getAbsolutePath() + "/conf/");
            if (!confDir.exists()) {
                // If conf dir doesn't exist create conf dir, and rcsm and rcsm subfolders.
                logger.log(Level.INFO, "Created Dir conf/: " + String.valueOf(confDir.mkdir()));
                // Make the roci dir
                this.confDir = new File(((android.content.Context) controller.getControllerManager().context).getFilesDir().getAbsolutePath() + "/conf/roci/");
                logger.log(Level.INFO, "Created Dir conf/roci: " + String.valueOf(confDir.mkdir()));
                // Make the rcsm dir
                this.confDir = new File(((android.content.Context) controller.getControllerManager().context).getFilesDir().getAbsolutePath() + "/conf/rcsm/");
                logger.log(Level.INFO, "Created Dir conf/rcsm: " + String.valueOf(confDir.mkdir()));    		
                // Make the pe dir
                this.confDir = new File(((android.content.Context) controller.getControllerManager().context).getFilesDir().getAbsolutePath() + "/conf/pe/");
                logger.log(Level.INFO, "Created Dir conf/pe: " + String.valueOf(confDir.mkdir())); 
                // TODO: copy the rcsm & rcsm properties files via getResourceAsStream, or getResource.  

            } else if (!(this.confDir = new File(((android.content.Context) controller.getControllerManager().context).getFilesDir().getAbsolutePath() + "/conf/rcsm/")).exists()) {
                // Make the rcsm dir
                logger.log(Level.INFO, "Created Dir conf/rcsm: " + String.valueOf(confDir.mkdir()));
            }
            if (!(this.confDir = new File(((android.content.Context) controller.getControllerManager().context).getFilesDir().getAbsolutePath() + "/conf/rcsm.properties")).exists()) {
                this.in = this.getClass().getResourceAsStream("/conf/rcsm.properties");
                // RCSM rcsm.properties file copy to file system from classpath.
                try {
                    this.fos = new FileOutputStream(((android.content.Context) controller.getControllerManager().context).getFilesDir().getAbsolutePath() + "/conf/rcsm.properties");
                    this.buffer = new byte[1024];
                    while ((this.len = in.read(buffer)) != -1) {
                        try {
                            fos.write(buffer, 0, len);
                        } catch (IOException e) {
                            logger.log(Level.SEVERE, "RCSM Manager: Input stream read error on: " + "conf/rcsm.properties", e);
                        }
                    }
                } catch (FileNotFoundException e) {
                    logger.log(Level.SEVERE, "RCSM Manager: Properties file not found: " + "conf/rcsm.properties", e);
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "RCSM Manager: File output stream initialization error on: " + "conf/rcsm.properties", e);
                } finally {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        logger.log(Level.WARNING, "RCSM Manager: Exception Closing File output stream: " + "conf/rcsm.properties", e);
                        this.fos = null;
                    }
                    try {
                        in.close();
                    } catch (IOException e) {
                        logger.log(Level.WARNING, "RCSM Manager: Exception Closing resource input stream: " + "conf/rcsm.properties", e);
                        this.in = null;
                    }
                }
            }

        }
        else
        {
        	// Is Java
        	this.confDir = new File(filePath).getParentFile();        	
        	if(!confDir.exists())
        	{
        		// If conf dir doesn't exist create conf dir, and rcsm and rcsm subfolders.
    			logger.log(Level.INFO, "Created Dir conf/: " + String.valueOf(confDir.mkdir()));
    			// Make the roci dir
    			this.confDir = new File(confDir.getAbsolutePath() + "/roci/");
    			logger.log(Level.INFO, "Created Dir conf/roci: " + String.valueOf(confDir.mkdir()));	
    			// Make the rcsm dir
    			this.confDir = new File(confDir.getAbsolutePath() + "/rcsm/");
    			logger.log(Level.INFO, "Created Dir conf/rcsm: " + String.valueOf(confDir.mkdir())); 
                        // Make the pe dir
                        this.confDir = new File(confDir.getAbsolutePath() + "/pe/");
                        logger.log(Level.INFO, "Created Dir conf/pe: " + String.valueOf(confDir.mkdir()));
        	}
        	else if(!(this.confDir = new File(confDir.getAbsolutePath() + "/rcsm/")).exists())
        	{
    			// Make the rcsm dir
    			logger.log(Level.INFO, "Created Dir conf/rcsm: " + String.valueOf(confDir.mkdir()));         		
        	}
        	if(!(this.confDir = new File(filePath)).exists())
			{
        		this.in = this.getClass().getResourceAsStream("/conf/rcsm.properties");
				// RCSM rcsm.properties file copy to file system from classpath.
				try 
				{
					this.fos = new FileOutputStream(filePath);
					this.buffer = new byte[1024];
					while ((this.len = in.read(buffer)) != -1) 
					{
					    try 
					    {
							fos.write(buffer, 0, len);
						} 
					    catch (IOException e) 
					    {
							logger.log(Level.SEVERE, "RCSM Manager: Input stream read error on: " + "conf/rcsm.properties",e);
						}
					}
				}  
				catch (FileNotFoundException e) 
				{
					logger.log(Level.SEVERE, "RCSM Manager: Properties file not found: " + "conf/rcsm.properties",e);
				}
				catch (IOException e) 
				{
					logger.log(Level.SEVERE, "RCSM Manager: File output stream initialization error on: " + "conf/rcsm.properties",e);
				}
				finally
				{
					try 
					{
						fos.close();
					} 
					catch (IOException e) 
					{
						logger.log(Level.WARNING, "RCSM Manager: Exception Closing File output stream: " + "conf/rcsm.properties",e);
						this.fos=null;
					}	
					try 
					{
						in.close();
					} 
					catch (IOException e) 
					{
						logger.log(Level.WARNING, "RCSM Manager: Exception Closing resource input stream: " + "conf/rcsm.properties",e);
						this.in=null;
					}						
	        	}
        	}
        }            
        RCSMProperties.setFileName(filePath);
        this.properties = RCSMProperties.getInstance();
        // Set the plugin properties folder path (plugin provider properties files directory path
        this.pluginPropertiesFolderPath = properties.get(PLUGIN_PROPS_FOLDER_KEY);      
        this.pluginsFolderPath = properties.get(PLUGIN_FOLDER_KEY);         
//        System.out.println("pluginPropertiesFolderPath: " + pluginPropertiesFolderPath);
        // Get the RCSM Properties.
        this.map = (Map) properties.getProperties();
    }
      
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
        // TODO: Add support for #.enabled property, and default set all providers to false. For now all plugins will be automatically be loaded to classpath, but not available unless configured in the RCSM Properties file..        
        // 4. Cycle through the RCSM loading process. 
        
        
        this.outNames = map.keySet().toArray();
//        System.out.println("Properties size: " + outNames.length);
        this.currentID = 0;
        this.arrayLength = 0;
        this.pos = 0;
        // Obtain the number of registered RCSM Plugins.
        while (pos < outNames.length) 
        {
            this.name = (String) outNames[pos];
            this.keyArray = name.split("\\.");
//            System.out.println("keyArray length: " + keyArray.length);
//            System.out.println("key: " + name);
//            System.out.println("value: " + (String)map.get(name));            
            if (keyArray.length == 2) 
            {
                // Is a RCSM Plugin Provider Property.
                try 
                {
                    // RCSM_ID
                    this.currentID = Integer.parseInt(keyArray[0]);
                    if (currentID > arrayLength) 
                    {
                        this.arrayLength = currentID;
                    }
                } 
                catch (NumberFormatException e) 
                {
                    // Ignore this property key. It does not match requirements
                    logger.log(Level.WARNING, "rcsm.properties key for plugin property does not contain a valid rcsm id in the format of #.key=. Actual key value is: " + keyArray[0]);
                }
            }
            this.pos=pos + 1;
        }

        // Initialize the RCSM Properties Arrays
        this.classes = new String[arrayLength + 1];
        this.names = new String[arrayLength + 1];
        this.versions = new double[arrayLength + 1];
        this.providers = new RCSMProvider[arrayLength + 1];
        this.enabled = new boolean[arrayLength + 1];
        
        this.pos=0;
        this.currentID = 0;
        while (pos < outNames.length) 
        {
            this.name = (String) outNames[pos];
            this.keyArray = name.split("\\.");

            if (keyArray.length == 2) 
            {
                // Is a RCSM Plugin Provider Property.
                try 
                {
                    // RCSM_ID
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
                    logger.log(Level.SEVERE,"rcsm.properties key for plugin property does not contain a valid rcsm id in the format of #.key=. Actual key value is: " + keyArray[0], e);
                }
            }
            this.pos = pos + 1;
        }

        // Initialize the Providers.
        currentID = 0;
        this.pos = 0;
        if(3<outNames.length)
        {        
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

                try 
                {
                    this.providers[pos] = (RCSMProvider) Class.forName(classes[pos]).newInstance();
                    // Set the Controller ID
                    this.providers[pos].setController(controller);
                    // Set the RCSM ID.
                    this.providers[pos].setID(pos);
                    // Set the RCSM Provider Properties
                    try 
                    {
                        this.prop = new Properties();
                        logger.log(Level.INFO,("Starting RCSM Plugin: " + provider_properties_file_name.substring(0,provider_properties_file_name.indexOf(".properties")) + "\n" + provider_properties_file_name.substring(0,provider_properties_file_name.indexOf(".properties")) + " Properties: " + pluginPropertiesFolderPath.concat(FORWARD_SLASH).concat(provider_properties_file_name)));
                        //load a properties file
                        prop.load(this.fis=new FileInputStream(pluginPropertiesFolderPath.concat(FORWARD_SLASH).concat(provider_properties_file_name)));
    //logger.log(Level.INFO,"RCSM Provider Properties File: " + pluginPropertiesFolderPath.concat(FORWARD_SLASH).concat(provider_properties_file_name));
                        this.providers[pos].setProperties(prop);
                        // Set the Properties File Path.
                        this.providers[pos].setPropertiesFilePath(pluginPropertiesFolderPath.concat(FORWARD_SLASH).concat(provider_properties_file_name));
                    } 
                    catch (FileNotFoundException ex) 
                    {
                        logger.log(Level.SEVERE,"RCSM Provider Properties File not found: " + pluginPropertiesFolderPath + provider_properties_file_name,ex);
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
                    logger.log(Level.SEVERE, null, ex);
                }

                // Set the RCSM ID;
                this.providers[pos].setID(pos);
                try 
                {
                    // Initialize the RCSM Provider
                    if(enabled[pos])
                    {
                        // RCSM plugin enabled
                        this.providers[pos].initialize();
                    }
                    else
                    {
                        // Plugin Provider Disabled
                        logger.log(Level.INFO,("RCSM Plugin: ".concat(names[pos].concat("_v").concat(String.valueOf(versions[pos]))).concat(" is disabled.")));
                    }
                } 
                catch (RCSMException ex) 
                {
                    if(this.providers[pos]!=null)
                    {
                        logger.log(Level.SEVERE, "ERROR: Failed to initialize RCSM Provider: " + this.providers[pos].getName(), ex);
                    }
                    else
                    {
                        logger.log(Level.SEVERE, "ERROR: Failed to initialize null RCSM Provider... ", ex);                    
                    }
                }
                this.pos = pos + 1;
            }
        }
        else
        {          
            // No RCSM Providers Defined.
            logger.log(Level.INFO, "No RCSM Provider Plugins are defined.");
        }            
    }
    
    /** Return an array of RCSM Provider name values from the rcsm.properties 
     * where RCSM ID = array element index.
     * 
     * @return String[] The complete list of rcsm.properties name elements by RCSM ID.
     */
    public String[] getProviderNames()
    {
        return names;
    }
    
    /** Return an array of RCSM Provider version values from the rcsm.properties 
     * where RCSM ID = array element index.
     * 
     * @return double[] The complete list of rcsm.properties version elements by RCSM ID.
     */
    public double[] getProviderVersions()
    {
        return versions;
    }
    
    /** Return an array of RCSM Provider class values from the rcsm.properties 
     * where RCSM ID = array element index.
     * 
     * @return String[] The complete list of rcsm.properties class elements by RCSM ID.
     */
    public String[] getProviderClasses()
    {
        return classes;
    }
    
    /** Return an array of RCSM Provider enabled values from the rcsm.properties 
     * where RCSM ID = array element index.
     * 
     * @return boolean[] The complete list of rcsm.properties enabled elements by RCSM ID.
     */
    public boolean[] getProviderEnabled()
    {
        return enabled;
    }
    
    /** Return a list of RCSM Providers.
     * 
     * @return RCSMProvider[] The complete list of Registered Providers.
     */
    public RCSMProvider[] getProviders()
    {
        return providers;
    }    

    /** Return an rcsm provider by rcsm_id.
     * @param rcsm_id
     * @return RCSMProvider 
     */
    public RCSMProvider getProvider(int rcsm_id)
    {
        return providers[rcsm_id];
    }
    
    /** Return an rcsm provider by rcsm_id.
     * @param provider_name
     * @return RCSMProvider 
     */
    public RCSMProvider getProviderByName(String provider_name)
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
    
    /** Return an rcsm plugin provider rcsm_id, by the provider name.
     * @param provider_name
     * @return rcsm_id int returns -1 of provider not found by name.
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
        // return false if a rcsm_if does not exist for provider_name
        if(provider_to_enable==null||(this.current_rcsm_id=getProviderIDByName(provider_name))==-1)
        {
            return false;
        }
        // if save startup properties is true change default enabled value in file.
        if(saveStartupProperties)
        {
           properties.put(String.valueOf(current_rcsm_id).concat(".").concat(PROPERTY_ENABLED),String.valueOf(saveStartupProperties));
           if(!properties.writeFile())
           {
               logger.log(Level.WARNING,"Failed to change rcsm.properties key value: " + String.valueOf(current_rcsm_id).concat(".").concat(PROPERTY_ENABLED) + ", to value: " + String.valueOf(saveStartupProperties));
           }
        }
        try
        {
            // If provider is set to disabled, shut it down.
            if(isEnabled==false&&enabled[current_rcsm_id])
            {
                // Shutdown the provider if it is not already shutdown.
                provider_to_enable.shutdown();
            }
            else if(isEnabled==true&&!enabled[current_rcsm_id])
            {
                // Start the provider if it is not already running.
                provider_to_enable.initialize();
            }
        }
        catch(Exception e)
        {
            logger.log(Level.WARNING,"Exception thrown while changing RCSM Provider enabled status of: " + names[current_rcsm_id]);
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
                    logger.log(Level.WARNING,"Exception thrown while attempting to call onInitialized() method of provider name: " + names[current_rcsm_id] + ", provider not instatiated.");       
                }               
                catch(Exception e)
                {
                    logger.log(Level.WARNING,"Exception thrown while attempting to call onInitialized() method of provider name: " + names[current_rcsm_id]);                   
                }
                // increment loop count.
                this.postInitLoopCount=postInitLoopCount + 1;
            }
        }
    }
    
    //    public static void main(String[] args)
//    {
//        try 
//        {
            // Initialize RCSMManager to the following RCSM Properties file, and roci plugin provider properties directory path.
//            RCSMManager rcsm = new RCSMManager("conf/rcsm.properties");
//        } catch (IOException ex) {
//            Logger.getLogger(RCSMManager.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (XmlRpcException ex) {
//            Logger.getLogger(RCSMManager.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (ROSXMLRPCException ex) {
//            Logger.getLogger(RCSMManager.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
}
