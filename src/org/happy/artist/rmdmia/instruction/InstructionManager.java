package org.happy.artist.rmdmia.instruction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
//import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.happy.artist.rmdmia.Controller;
import org.happy.artist.rmdmia.instruction.utility.InstructionImporter;
import org.happy.artist.rmdmia.instruction.utility.SchemaImporter;

/**  InstructionManager manages Instruction Manager Providers, and all the Instruction Manager 
 * Interfaces/Properties files. Instantiate this Object to start the InstructionManager. 
 * 
 * @author Happy Artist
 * 
 * @copyright Copyright © 2014 Happy Artist. All rights reserved.
 */
public class InstructionManager 
{
    private static InstructionManagerProperties properties;
    private Map map;
    private String filePath="instruction_manager.properties";
    private final static String PROPERTIES_EXTENSION=".properties";
    private final static String FORWARD_SLASH="/";   
    private final static String UNDERSCORE_v="_v";  
    private final static String PROPERTY_NAME="name";
    private final static String PROPERTY_VERSION="version";
    private final static String PROPERTY_CLASS="class";
    private final static String PROPERTY_ENABLED="enabled";  

// TODO: Implement Instruction Class Generation on startup by rcsm provider    
    // Schema Generation Variables
    private final static String CLASS_FOLDER_KEY="class_folder";
    private final static String SCHEMA_FOLDER_KEY="schema_folder";  
    private String schemaFolderPath;
    private String classFolderPath;   
    private File schemaDirectory;
    private File classDirectory;    
    // The plugin properties files folder path to find plugin properties files.
    private String pluginPropertiesFolderPath;
    private String[] classes;
    private String[] names;
    private double[] versions;
    private InstructionManagerProvider[] providers;
    private boolean[] enabled;
    private final static String PLUGIN_PROPS_FOLDER_KEY="plugin_props_folder_key";
    private final static String PLUGIN_FOLDER_KEY="im_plugins_folder";    
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
    private Logger logger = Logger.getLogger(InstructionManager.class.getName());
    // The RMDMIA Controller of perception Engine.
    private Controller controller;
    // getProviderByName variables.
    private int providerCount;    
    // RCSM Properties File related variables.
    private FileOutputStream fos;
    private InputStream in;
    private byte[] buffer;
    private int len;
    private File confDir;
    private InstructionManagerProvider provider_to_enable;
    // setProviderEnabledByName method variables.
    private int current_im_id;
    // onInitialized variables
    private int postInitLoopCount;
    
    /** The Instruction Manager Provider properties folder is defined in the instruction_manager.properties file under the "im_plugins_folder" key.
    * 
    * @param filePath - The instruction_manager.properties file path.
    * @param controller  
    */
    public InstructionManager(String filePath, Controller controller) 
    {
        this.filePath = filePath;
        this.controller=controller;
        logger.log(Level.INFO, "Starting Instruction Manager..."); 
        if(controller.getControllerManager().IS_ANDROID)
        {
            // Is Android
            this.confDir = new File(((android.content.Context) controller.getControllerManager().context).getFilesDir().getAbsolutePath() + "/conf/");
            if (!confDir.exists()) {
                // If conf dir doesn't exist create conf dir, and rcsm and pe subfolders.
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
                // Make the tm dir
                this.confDir = new File(((android.content.Context) controller.getControllerManager().context).getFilesDir().getAbsolutePath() + "/conf/tm/");
                logger.log(Level.INFO, "Created Dir conf/tm: " + String.valueOf(confDir.mkdir()));    
                // Make the mm dir
                this.confDir = new File(((android.content.Context) controller.getControllerManager().context).getFilesDir().getAbsolutePath() + "/conf/mm/");
                logger.log(Level.INFO, "Created Dir conf/mm: " + String.valueOf(confDir.mkdir()));
                // Make the im dir
                this.confDir = new File(((android.content.Context) controller.getControllerManager().context).getFilesDir().getAbsolutePath() + "/conf/im/");
                logger.log(Level.INFO, "Created Dir conf/im: " + String.valueOf(confDir.mkdir()));    	                     
                // Make the gm dir
                this.confDir = new File(((android.content.Context) controller.getControllerManager().context).getFilesDir().getAbsolutePath() + "/conf/gm/");
                logger.log(Level.INFO, "Created Dir conf/gm: " + String.valueOf(confDir.mkdir()));    	                                
                
            } 
            else if (!(this.confDir = new File(((android.content.Context) controller.getControllerManager().context).getFilesDir().getAbsolutePath() + "/conf/im/")).exists()) 
            {
                // Make the im dir
                this.confDir = new File(((android.content.Context) controller.getControllerManager().context).getFilesDir().getAbsolutePath() + "/conf/im/");
                logger.log(Level.INFO, "Created Dir conf/im: " + String.valueOf(confDir.mkdir()));
            }
            if (!(this.confDir = new File(((android.content.Context) controller.getControllerManager().context).getFilesDir().getAbsolutePath() + "/conf/instruction_manager.properties")).exists()) {
                this.in = this.getClass().getResourceAsStream("/conf/instruction_manager.properties");
                // Instruction Manager instruction_manager.properties file copy to file system from classpath.
                try {
                    this.fos = new FileOutputStream(((android.content.Context) controller.getControllerManager().context).getFilesDir().getAbsolutePath() + "/conf/instruction_manager.properties");
                    this.buffer = new byte[1024];
                    while ((this.len = in.read(buffer)) != -1) {
                        try {
                            fos.write(buffer, 0, len);
                        } catch (IOException e) {
                            logger.log(Level.SEVERE, "Instruction Manager: Input stream read error on: " + "conf/instruction_manager.properties", e);
                        }
                    }
                } catch (FileNotFoundException e) {
                    logger.log(Level.SEVERE, "Instruction Manager Manager: Properties file not found: " + "conf/instruction_manager.properties", e);
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Instruction Manager: File output stream initialization error on: " + "conf/instruction_manager.properties", e);
                } finally {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        logger.log(Level.WARNING, "Instruction Manager: Exception Closing File output stream: " + "conf/instruction_manager.properties", e);
                        this.fos = null;
                    }
                    try {
                        in.close();
                    } catch (IOException e) {
                        logger.log(Level.WARNING, "Instruction Manager: Exception Closing resource input stream: " + "conf/instruction_manager.properties", e);
                        this.in = null;
                    }
                }
            }
        } else {
            // Is Java
            this.confDir = new File(filePath).getParentFile();
            if (!confDir.exists()) {
                // If conf dir doesn't exist create conf dir, and rcsm and pe subfolders.
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
                // Make the tm dir
                this.confDir = new File(confDir.getAbsolutePath() + "/tm/");
                logger.log(Level.INFO, "Created Dir conf/tm: " + String.valueOf(confDir.mkdir()));
                // Make the mm dir
                this.confDir = new File(confDir.getAbsolutePath() + "/mm/");
                logger.log(Level.INFO, "Created Dir conf/mm: " + String.valueOf(confDir.mkdir()));                  
                // Make the gm dir
                this.confDir = new File(confDir.getAbsolutePath() + "/gm/");
                logger.log(Level.INFO, "Created Dir conf/gm: " + String.valueOf(confDir.mkdir()));                
                // Make the im dir
                this.confDir = new File(confDir.getAbsolutePath() + "/im/");
                logger.log(Level.INFO, "Created Dir conf/im: " + String.valueOf(confDir.mkdir()));                
                
            } else if (!(this.confDir = new File(confDir.getAbsolutePath() + "/im/")).exists()) {
                // Make the im dir
                logger.log(Level.INFO, "Created Dir conf/im: " + String.valueOf(confDir.mkdir()));
            }
            if (!(this.confDir = new File(filePath)).exists()) {
                this.in = this.getClass().getResourceAsStream("/conf/instruction_manager.properties");
                // Instruction Manager instruction_manager.properties file copy to file system from classpath.
                try {
                    this.fos = new FileOutputStream(filePath);
                    this.buffer = new byte[1024];
                    while ((this.len = in.read(buffer)) != -1) {
                        try {
                            fos.write(buffer, 0, len);
                        } catch (IOException e) {
                            logger.log(Level.SEVERE, "Instruction Manager: Input stream read error on: " + "conf/instruction_manager.properties", e);
                        }
                    }
                } catch (FileNotFoundException e) {
                    logger.log(Level.SEVERE, "Instruction Manager: Properties file not found: " + "conf/instruction_manager.properties", e);
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Instruction Manager: File output stream initialization error on: " + "conf/instruction_manager.properties", e);
                } finally {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        logger.log(Level.WARNING, "Instruction Manager: Exception Closing File output stream: " + "conf/instruction_manager.properties", e);
                        this.fos = null;
                    }
                    try {
                        in.close();
                    } catch (IOException e) {
                        logger.log(Level.WARNING, "Instruction Manager: Exception Closing resource input stream: " + "conf/instruction_manager.properties", e);
                        this.in = null;
                    }
                }
            }
        }    
        InstructionManagerProperties.setFileName(filePath);
        InstructionManager.properties = InstructionManagerProperties.getInstance();
        // Set the plugin properties folder path (plugin provider properties files directory path
        this.pluginPropertiesFolderPath = InstructionManager.properties.get(PLUGIN_PROPS_FOLDER_KEY);      
        this.pluginsFolderPath = InstructionManager.properties.get(PLUGIN_FOLDER_KEY);      
        // Set the Schema & Class Folder paths
        this.schemaFolderPath = InstructionManager.properties.get(SCHEMA_FOLDER_KEY);                   this.classFolderPath = InstructionManager.properties.get(CLASS_FOLDER_KEY);      
        
//        System.out.println("pluginPropertiesFolderPath: " + pluginPropertiesFolderPath);
        // Get the Instruction Manager Properties.
        this.map = (Map) properties.getProperties();
    }

    // Default to true, and if not found set to false.
    private boolean providers_defined=true;    
    /** Initialize the Provider Manager. */ 
    public void initialize()
    {
        // TODO: Provider Loading
        // 1. Get a directory Listing of the Providers Folder
        this.files = controller.getControllerManager().getProviderFiles(this.pluginDirectory=new File(pluginsFolderPath), true);
        // 2. Read the /conf/im Folder and look for associated Properties to each, and If an associated Properties file does not exist for a Jar, copy the associated properties file to the correct conf directory.
        this.rootConfigurationDirectory = new File(filePath).getAbsoluteFile();
        if(rootConfigurationDirectory.getParentFile()!=null&&rootConfigurationDirectory.getParentFile().getParentFile()!=null)
        {
            this.rootConfigurationDirectory=rootConfigurationDirectory.getParentFile().getParentFile();
        }
        else if(rootConfigurationDirectory.getParentFile()!=null)
        {
            this.rootConfigurationDirectory=rootConfigurationDirectory.getParentFile();
        }
// If Schema directories do not exist create them
        try
        {
            this.schemaDirectory=new File(schemaFolderPath);
            this.classDirectory=new File(classFolderPath);
            if(!schemaDirectory.exists())
            {
                schemaDirectory.mkdirs();
            }
            if(!classDirectory.exists())
            {
                classDirectory.mkdirs();
            }  
        }
        catch(Exception e)
        {
             logger.log(Level.WARNING,"Exception thrown attempting to generate schema_folder, or class folder for Instruction Manager. Class directory file path: ".concat(classDirectory.getAbsolutePath().concat(", Schema directory file path: ").concat(schemaDirectory.getAbsolutePath())),e);
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
                    logger.log(Level.WARNING,"Exception thrown attempting to generate configuration file for Instruction Manager Provider. File path: ".concat(files[i].getAbsolutePath()),e);
                }
            }
        }
        // 3. Load all Providers in the plugins directory.
// TODO: (DONE) Add support for #.enabled property, and default set all providers to false. For now all plugins will be automatically be loaded to classpath, but not available unless configured in the Instruction Manager Properties file..        
        // 4. Cycle through the Instruction Manager loading process. 

        this.outNames = map.keySet().toArray();
//        System.out.println("Properties size: " + outNames.length);
        this.currentID = 0;
        this.arrayLength = 0;
        this.pos = 0;
        // Obtain the number of registered Instruction Manager Providers.
        while (pos < outNames.length) 
        {
            this.name = (String) outNames[pos];
            this.keyArray = name.split("\\.");
            //System.out.println("keyArray length: " + keyArray.length);
            //System.out.println("key: " + name);
            if (keyArray.length == 2) 
            {
                // Is a Instruction Manager Provider Provider Property.
                try 
                {
                    // PE_ID
                    this.currentID = Integer.parseInt(keyArray[0]);
                    if (currentID > arrayLength) 
                    {
                        this.arrayLength = currentID;
                    }
                } 
                catch (NumberFormatException e) 
                {
                    // Ignore this property key. It does not match requirements
                    logger.log(Level.WARNING, "instruction_manager.properties key for plugin property does not contain a valid im id in the format of #.key=. Actual key value is: " + keyArray[0]);
                }
            }
            this.pos=pos + 1;
        }
        //System.out.println("Array length: " + (arrayLength + 1));
        // Initialize the Instruction Manager Properties Arrays
        this.classes = new String[arrayLength + 1];
        this.names = new String[arrayLength + 1];
        this.versions = new double[arrayLength + 1];
        this.providers = new InstructionManagerProvider[arrayLength + 1];
        this.enabled = new boolean[arrayLength + 1];
        
        this.pos=0;
        this.currentID = 0;
        while (pos < outNames.length) 
        {
            this.name = (String) outNames[pos];
            this.keyArray = name.split("\\.");

            if (keyArray.length == 2) 
            {
                // Is a Instruction Manager Provider Provider Property.
                try 
                {
                    // PE_ID
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
                    logger.log(Level.WARNING, "instruction_manager.properties key for plugin property does not contain a valid im id in the format of #.key=. Actual key value is: " + keyArray[0]);
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
                    this.providers[pos] = (InstructionManagerProvider) Class.forName(classes[pos]).getConstructor(Controller.class).newInstance(controller);
                    // Set the Controller ID
                    this.providers[pos].setController(controller);
                    // Set the Instruction Manager ID.
                    this.providers[pos].setID(pos);
                    // Set the Instruction Manager Provider Properties
                    try 
                    {
                        this.prop = new Properties();
                            logger.log(Level.INFO, "Starting Instruction Manager Provider: " + provider_properties_file_name.substring(0,provider_properties_file_name.indexOf(".properties")) + "\n" + provider_properties_file_name.substring(0,provider_properties_file_name.indexOf(".properties")) + " Properties: " + pluginPropertiesFolderPath.concat(FORWARD_SLASH).concat(provider_properties_file_name));
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
                            logger.log(Level.WARNING, "Instruction Manager Provider Properties File not found: " + pluginPropertiesFolderPath + "/" + provider_properties_file_name);
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
                    Logger.getLogger(InstructionManager.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(InstructionManager.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(InstructionManager.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchMethodException ex) {
                    Logger.getLogger(InstructionManager.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SecurityException ex) {
                    Logger.getLogger(InstructionManager.class.getName()).log(Level.SEVERE, null, ex);
                }

                // Set the Instruction Manager ID;
                this.providers[pos].setID(pos);

/*                // Initialize the Instruction Manager Provider
                if(enabled[pos])
                {
                    // Instruction Manager plugin enabled
                    this.providers[pos].initialize();
                }
                else
                {
                    // Provider Provider Disabled
                    logger.log(Level.INFO,("Instruction Manager Provider: ".concat(names[pos].concat("_v").concat(String.valueOf(versions[pos]))).concat(" is disabled.")));
                }                        */
            }
            else
            {
                this.providers_defined=false;
                logger.log(Level.INFO, "No Instruction Manager Providers are defined.");                       }
            this.pos = pos + 1;
        }
///////////////////////////////////////////////////////////////// MOVING BELOW BELOW updateSchemaMap code...        
/*        if(providers_defined)
        {        
            // Initialize the Instruction Manager Providers
                    this.pos=0;
            this.currentID = 0;
            while (pos < providers.length) 
            {
                // Initialize the Instruction Manager Provider
                if(enabled[pos])
                {
                    // Instruction Manager plugin enabled
                    this.providers[pos].initialize();
                }
                else
                {
                    // Provider Provider Disabled
                    logger.log(Level.INFO,("Instruction Manager Provider: ".concat(names[pos].concat("_v").concat(String.valueOf(versions[pos]))).concat(" is disabled.")));
                }  
                this.pos = pos + 1;    
            } 
        }*/
 ////////////////////////////////////////////////////////////////////
//        System.out.println("~~~~~~~~~~~~~~~~~~~");
        // initialize the Schema message maps & Load the custom classes into the classpath.
        for(int i = 0;i<controller.getRCSM().getProviderNames().length;i++)
        {
            updateSchemaMap(controller.getRCSM().getProviderNames()[i]);    
 //           System.out.println("CALLED updateSchemaMap");
            // Update Class Map for Deciphering Messages Dynamically...
            updateClassLookupMap(controller.getRCSM().getProviderNames()[i]);
            try
            {
                if(classFolderPath.endsWith("/"))
                {
                    //System.out.println("~~~~~~~~~~~~~~~~~~~CLASSPATH: "+classFolderPath.concat(controller.getRCSM().getProviderNames()[i]).concat("/"));
                    controller.getControllerManager().addProvider(new File(classFolderPath.concat(controller.getRCSM().getProviderNames()[i]).concat("/")));
                }
                else
                {
                   //System.out.println("~~~~~~~~~~~~~~~~~~~CLASSPATH: "+classFolderPath.concat("/").concat(controller.getRCSM().getProviderNames()[i]).concat("/"));                    
                    controller.getControllerManager().addProvider(new File(classFolderPath.concat("/").concat(controller.getRCSM().getProviderNames()[i]).concat("/")));
                }
            }
            catch(MalformedURLException e)
            {
                Logger.getLogger(InstructionManager.class.getName()).log(Level.SEVERE, null, e);
            } 
            catch (IOException ex) 
            {
                Logger.getLogger(InstructionManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
///////////////////////////////TEST updateSchemaMap Location
        if(providers_defined)
        {        
            // Initialize the Instruction Manager Providers
                    this.pos=0;
            this.currentID = 0;
            while (pos < providers.length) 
            {
                // Initialize the Instruction Manager Provider
                if(enabled[pos])
                {
                    // Instruction Manager plugin enabled
                    this.providers[pos].initialize();
                }
                else
                {
                    // Provider Provider Disabled
                    logger.log(Level.INFO,("Instruction Manager Provider: ".concat(names[pos].concat("_v").concat(String.valueOf(versions[pos]))).concat(" is disabled.")));
                }  
                this.pos = pos + 1;    
            } 
        }        
        
///////////////////////////////////////////////////////////        
        
        this.isInitialized=true;
        
        // TODO: REMOVE THIS TEST CODE, for testing generation of Instruction Class        
/*        InstructionManagerPlugin plugin_test=((InstructionManagerPlugin)getProviderByName("instruction_manager"));
        int[] test_hids=plugin_test.getHids();
        for(int i=0;i<test_hids.length;i++)
        {
                try
                {
                    InstructionDefinition def=plugin_test.getInstructionDefinition(test_hids[i]);
                    def.getInstructionObject();
// TODO: The following line needs to be removed and is test code.
//System.out.println("HAPPYHAPPYHAPPY controller:" + controller);                    
                    // def.getInstructionInputHandler();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
        }
*/         
// TODO: REMOVE TEST CODE/  
 //       System.out.println("SCHEMA MAP KEYS InitializationManager: " + Arrays.deepToString(controller.getInstructionManager().getSchemaLookupMap().keySet().toArray()));
//        System.out.println("CLASS MAP VALUES InitializationManager: " + Arrays.deepToString(controller.getInstructionManager().getClassLookupMap().values().toArray()));        
// TODO: initialize the class message maps.        
    }   

    /** Return folder to import message schemas. */    
    public String getSchemaFolderPath()
    {
        return schemaFolderPath;
    }
    
    /** Return folder to auto-generated Instruction classes. */
    public String getClassFolderPath()
    {
        return classFolderPath;
    }
    
    /** Return an array of Instruction Manager Provider name values from the instruction_manager.properties 
     * where Instruction Manager ID = array element index.
     * 
     * @return String[] The complete list of instruction_manager.properties name elements by Instruction Manager ID.
     */
    public String[] getProviderNames()
    {
        return names;
    }
    
    /** Return an array of Instruction Manager Provider version values from the instruction_manager.properties 
     * where Instruction Manager ID = array element index.
     * 
     * @return double[] The complete list of instruction_manager.properties version elements by Instruction Manager ID.
     */
    public double[] getProviderVersions()
    {
        return versions;
    }
    
    /** Return an array of Instruction Manager Provider class values from the instruction_manager.properties 
     * where Instruction Manager ID = array element index.
     * 
     * @return String[] The complete list of instruction_manager.properties class elements by Instruction Manager ID.
     */
    public String[] getProviderClasses()
    {
        return classes;
    }
    
    /** Return an array of Instruction Manager Provider enabled values from the instruction_manager.properties 
     * where Instruction Manager ID = array element index.
     * 
     * @return boolean[] The complete list of instruction_manager.properties enabled elements by Instruction Manager ID.
     */
    public boolean[] getProviderEnabled()
    {
        return enabled;
    }    
    
    /** Return a list of Instruction Manager Provider Providers.
     * 
     * @return InstructionManagerProvider[] The complete list of Registered Providers.
     */
    public InstructionManagerProvider[] getProviders()
    {
        return providers;
    }

    /** Return an instruction manager provider by im_id.
     * @param im_id
     * @return InstructionManagerProvider 
     */
    public InstructionManagerProvider getProvider(int im_id)
    {
        return providers[im_id];
    }
    
    /** Return an instruction manager plugin provider by provider name.
     * @param provider_name
     * @return InstructionManagerProvider 
     */
    public InstructionManagerProvider getProviderByName(String provider_name)
    {
        this.providerCount=0;
        if(providers==null||names==null)
        {
            return null;
        }      
        while(providerCount<providers.length)
        {
            if(names[providerCount]!=null&&names[providerCount].equals(provider_name))
            {
                return providers[providerCount];
            }
            this.providerCount=providerCount + 1;
        }
        return null;
    }        

    /** Return an instruction manager plugin provider im_id, by the provider name.
     * @param provider_name
     * @return im_id int returns -1 of provider not found by name.
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
        // return false if a im_if does not exist for provider_name
        if(provider_to_enable==null||(this.current_im_id=getProviderIDByName(provider_name))==-1)
        {
            return false;
        }
        // if save startup properties is true change default enabled value in file.
        if(saveStartupProperties)
        {
           properties.put(String.valueOf(current_im_id).concat(".").concat(PROPERTY_ENABLED),String.valueOf(saveStartupProperties));
           if(!properties.writeFile())
           {
               logger.log(Level.WARNING,"Failed to change instruction_manager.properties key value: " + String.valueOf(current_im_id).concat(".").concat(PROPERTY_ENABLED) + ", to value: " + String.valueOf(saveStartupProperties));
           }
        }
        try
        {
            // If provider is set to disabled, shut it down.
            if(isEnabled==false&&enabled[current_im_id])
            {
                // Shutdown the provider if it is not already shutdown.
                provider_to_enable.shutdown();
            }
            else if(isEnabled==true&&!enabled[current_im_id])
            {
                // Start the provider if it is not already running.
                provider_to_enable.initialize();
            }
        }
        catch(Exception e)
        {
            logger.log(Level.WARNING,"Exception thrown while changing Instruction Manager Provider enabled status of: " + names[current_im_id]);
        }
        // Cleanup the method references.
        this.provider_to_enable = null;
        return true;
    }    

    private boolean isInitialized=false;
    /**
     * Return boolean is initialized on the InstructionManager.
     *
     * @return boolean returns false if InstructionManager is not initialized.
     */
    public boolean isInitialized() {
        return isInitialized;
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
                    logger.log(Level.WARNING,"Exception thrown while attempting to call onInitialized() method of provider name: " + names[current_im_id] + ", provider not instantiated.", e);       
                }               
                catch(Exception e)
                {
                    logger.log(Level.WARNING,"Exception thrown while attempting to call onInitialized() method of provider name: " + names[current_im_id],e);                   
                }
                // increment loop count.
                this.postInitLoopCount=postInitLoopCount + 1;
            }
        }
    }  
    
    private Map messageMap = new HashMap();
    private Map classMap = new HashMap();
    
    /** Return the Schema Message Lookup Map in the format key is Object name in Schema file. Returns null if message not found. */
    public Map<String,String> getSchemaLookupMap()
    {
        return messageMap;
    }

    /** Return the Instruction class location, for the associated Schema Object Type. Returns null if class not found. key names are RCSM Provider name/Object Name without package path. */    
    public Map<String,String> getClassLookupMap()
    {
        return classMap;
    }
    
    // TODO: search the file system for classes, and message files.
    // Completed - untested
    /** Unsupported internal method. Call at startup. */public void updateSchemaMap(String rcsm_provider_name)
    {
        // Get schema file list for rcsm provider.
        List<String> list=null;
        int current_index;
        String current_schema;
        String schema_temp;
        String[] schema_extension;
        int schema_path_position;
        try 
        {
            schema_extension=controller.getRCSM().getProviderByName(rcsm_provider_name).getInstructionDefinition().getSchemaFileExtensions();
            // need to loop through each schema extension type.
            for(int j=0;j<schema_extension.length;j++)
            {
                list = SchemaImporter.getSchemaFileList(rcsm_provider_name, schema_extension[j], getSchemaFolderPath());
                // Update the map based on schema file list.
                for(int i=0;i<list.size();i++)
                {
                 try
                 {
                    if((current_index=(current_schema=list.get(i)).lastIndexOf("/"))!=-1)
                    {
                        // TODO: Adding generic version without package (can be buggy if multiple Objects with same name exist...) - Added if else to find rcsm_provider name pattern, and add Object path with package name. else has potential to be buggy, but should not ever go down that path
 //                       System.out.println("Current Schema: "+current_schema);
                        if((schema_path_position=current_schema.indexOf(rcsm_provider_name.concat("/")))!=-1)
                        {
                        //  System.out.println("**************");  
                            schema_temp=current_schema.substring(schema_path_position);
                            schema_temp=schema_temp.replace("/msg/", "/");
                            schema_temp=schema_temp.replace("/srv/", "/");
                            schema_temp=schema_temp.replace("/action/", "/");                     
                            schema_temp=schema_temp.substring(0,schema_temp.lastIndexOf(".")).toLowerCase();
                            messageMap.put(schema_temp,current_schema); 
                            
                            // Add the pathless version as well
                            schema_temp=rcsm_provider_name.concat("/").concat(current_schema.substring(current_index + 1));
                            schema_temp=schema_temp.substring(0,schema_temp.lastIndexOf(".")).toLowerCase();
                            messageMap.put(schema_temp,current_schema);                           
// Add generic version...                            
                        }
                        else
                        {
                            schema_temp=rcsm_provider_name.concat("/").concat(current_schema.substring(current_index + 1));
                            schema_temp=schema_temp.substring(0,schema_temp.lastIndexOf(".")).toLowerCase();
                            messageMap.put(schema_temp,current_schema);                            
                        }

                    }
                    else
                    {
                        messageMap.put(rcsm_provider_name.concat("/").concat(current_schema.substring(0,current_schema.lastIndexOf("."))).toLowerCase(),current_schema);
                    }
                 }
                 catch(Exception e)
                 {
                     Logger.getLogger(InstructionManager.class.getName()).log(Level.SEVERE, null, e);
                 }
                }            
            }
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(InstructionManager.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (InterruptedException ex) 
        {
            Logger.getLogger(InstructionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
//System.out.println("Schema Map: " + messageMap.toString());
    }
    
    /** Unsupported internal method. Call at startup. */
    public void updateClassLookupMap(String rcsm_provider_name)
    {
        // Get schema file list for rcsm provider.
        List<String> list=null;
        int current_index;
        String current_instruction;
        String instruction_temp;        
        try 
        {
            list = InstructionImporter.getClassFileList(rcsm_provider_name, "class", this.getClassFolderPath());
            // Update the map based on schema file list.
            for(int i=0;i<list.size();i++)
            {
             try
             {
                if((current_index=(current_instruction=list.get(i)).lastIndexOf("/"))!=-1)
                {
                    instruction_temp=rcsm_provider_name.concat("/").concat(current_instruction.substring(current_index + 1));
                    instruction_temp=instruction_temp.substring(0,instruction_temp.lastIndexOf(".")).toLowerCase();
                    classMap.put(instruction_temp,current_instruction);
                }
                else
                {
                    classMap.put(rcsm_provider_name.concat("/").concat(current_instruction.substring(0,current_instruction.lastIndexOf("."))).toLowerCase(),current_instruction);
                }
             }
             catch(Exception e)
             {
                 Logger.getLogger(InstructionManager.class.getName()).log(Level.SEVERE, null, e);
             }
            }            
            
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(InstructionManager.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (InterruptedException ex) 
        {
            Logger.getLogger(InstructionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
//    public static void main(String[] args)
//    {
        // Initialize InstructionManager to the following Instruction Manager Properties file, and pe plugin provider properties directory path.
//        InstructionManager im = new InstructionManager("conf/instruction_manager.properties");
        
//    }
}
