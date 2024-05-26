package org.happy.artist.rmdmia;

import com.trinity.ea.util.PropertiesAccessor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.happy.artist.rmdmia.timing.TimerService;

/** The ControllerManager Object manages multiple Controllers. 
 * Only a single Controller Object is supported in the 1.0 release
 * and the support for multiple Controllers will be added in a follow up release.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2012-2013 Happy Artist. All rights reserved.
 */
public class ControllerManager 
{
    // Controller variables.
    //private static Controller[] controllers;
    private Controller controller;
    private int maxControllers;
    // Timer Service variables.
    private TimerService timerService;
    private boolean timerServiceEnabled=false; 
    // The Java Runtime
    private final static String JAVA_RUNTIME=System.getProperty("java.runtime.name").toLowerCase();
    public static boolean IS_ANDROID;
    // addProvider method variables
    //Parameters of the method to add an URL to the System classes. 
    private static final Class<?>[] parameters = new Class[]{URL.class};      
    // The Logger
    private final static Logger logger = Logger.getLogger(ControllerManager.class.getName());     // getProviderFiles method
    private int spc_count=-1;
    private String spcs;
    private List<File> listOfFiles = new ArrayList<File>();
    private File[] files;    
    // extractProviderConfiguration method variables.
    private InputStream is;
    private ZipInputStream zis;
    private ZipEntry zipentry;
    private byte[] buf;
    private String entryName;
    private int n;
    private File newFile;
    private FileOutputStream fos; 
    private final static String PLUGIN_PROPERTIES="plugin.properties";
    private Properties plugin_properties;
    private int pos;
    private int currentID=0;  
    private String[] keyArray;  
    private Object[] outNames;
    private int arrayLength=0;
    private String name;
    private String currentKey;
    private final static String PROPERTY_NAME="name";
    private final static String PROPERTY_VERSION="version";
    private final static String PROPERTY_CLASS="class"; 
    private final static String PROPERTY_ENABLED="enabled";  
    // plugin.properties file variables for plugin on install
    private String[] classes;
    private String[] names;
    private double[] versions;
    private boolean[] enabled;    
    // plugin properties read from Properties Object.
    private String[] properties_classes;
    private String[] properties_names;
    private double[] properties_versions;
    private boolean[] properties_enabled; 
    private boolean[] isSameArray;
    private boolean isSame=false;
    
    // Application Context for Android
    public android.content.Context context;

    /** Java ControllerManager Constructor. TimerService disabled at startup. */
    public ControllerManager()
    {    
        if(JAVA_RUNTIME.indexOf("ndroid")==-1)
        {
            IS_ANDROID=false;
        }
        else
        {
            IS_ANDROID=true;            
        }
        this.controller = new Controller(this);
    }        
    
    /** Java ControllerManager Constructor.
     * 
     * @param timerServiceEnabled boolean Call TimerService.SYSTEM_TIME to get 
     * the latest system time if timerServiceEnabled.
     */
    public ControllerManager(boolean timerServiceEnabled)
    {    
        if(JAVA_RUNTIME.indexOf("ndroid")==-1)
        {
            // Is Java
            IS_ANDROID=false;
        }
        else
        {
            // Is Android
            IS_ANDROID=true;            
        }
        // Setup Timer Service
        if(timerServiceEnabled==true)
        {
            // Timer Service Enabled
            // Update System Time every millasecond.
            this.timerService = new TimerService(1,1,TimeUnit.MILLISECONDS);
        }
        else
        {
            // TimerService disabled.
            this.timerService=null;
        }
        this.controller = new Controller(this);
    }    

/** Constructor for Android implementations. Requires Application Context.
 *     
 */
    /**  Android ControllerManager Constructor.
     * 
     * @param timerServiceEnabled boolean Call TimerService.SYSTEM_TIME to get 
     * the latest system time if timerServiceEnabled.
     */
    public ControllerManager(boolean timerServiceEnabled, android.content.Context context)
    {    
    	this.context=context;
        if(JAVA_RUNTIME.indexOf("ndroid")==-1)
        {
            // Is Java
            IS_ANDROID=false;
        }
        else
        {
            // Is Android
            IS_ANDROID=true;            
        }
        // Setup Timer Service
        if(timerServiceEnabled==true)
        {
            // Timer Service Enabled
            // Update System Time every millasecond.
            this.timerService = new TimerService(1,1,TimeUnit.MILLISECONDS);
        }
        else
        {
            // TimerService disabled.
            this.timerService=null;
        }
        this.controller = new Controller(this);
    }      
    
    /** Return the specified instance of Controller Object. 
     * @return Controller
     */
    //If no 
    // * Controller exists at specified id one will be created, provided the 
    // * length is within the parameters of the maximum Controller length. 
    // * By default if only 1 controller is in use, use 0 as the int id. 
    // * Additional IDs will not be supported in the first release 
    public Controller getController()
    {
        return controller;
    }
    
    /** Return a reference to the TimerService.
     * 
     * @return TimerService  
     */     
    public TimerService getTimerService()
    {
        return timerService;
    }
    
    /** setTimerInterval will enable a disabled TimerService, or
     * update the TimerService instance to the specified Timer 
     * Interval.
     * 
     * @param startDelay long
     * @param delay long
     * @param unit TimeUnit
     */
    public void setTimerInterval(long startDelay,long delay,TimeUnit unit)
    {
            // Set Timer Service to Enabled
            this.timerServiceEnabled=true;    
            // Update System Time every millasecond.
            if(timerService!=null)
            {
                timerService.shutdown();
            }
            this.timerService = new TimerService(startDelay,delay,unit);
        }
    
    /** Not implemented. */
    public static void shutdown()
    {
        System.exit(0);
        //throw new UnsupportedOperationException();
    }
    
    // TODO: Add Android Support
    /** Internal use only. Not supported. Adds a Jar File to the Classpath. 
     * 
     * @throws MalformedURLException, IOException
     */
    public void addProvider(File jarFilePath) throws MalformedURLException, IOException
    {
        // If is android load the plugin to android. else its Java
        if(IS_ANDROID)
        {
            dalvik.system.DexClassLoader classLoader = new dalvik.system.DexClassLoader(jarFilePath.getAbsolutePath(), "/data/data/" + context.getPackageName() + "/", null, getClass().getClassLoader());
            // Class<?> myClass = classLoader.loadClass("MyClass");            
        }
        else
        {
            addProvider(jarFilePath.toURI().toURL());
        }
        
    }
  
    /** Internal use only. Not supported. Adds a URL to the Classpath.
     * 
     * @throws IOException
     */
    public void addProvider(URL url) throws IOException
    {
        // If is android load the plugin to android. else its Java
        if(IS_ANDROID)
        {
            dalvik.system.DexClassLoader classLoader = new dalvik.system.DexClassLoader(url.getPath(), "/data/data/" + context.getPackageName() + "/", null, getClass().getClassLoader());
            //Class<?> myClass = classLoader.loadClass("MyClass");
        }
        else
        {
            URLClassLoader sysloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
            Class<?> sysclass = URLClassLoader.class;
            try 
            {
                Method method = sysclass.getDeclaredMethod("addURL",parameters);
                method.setAccessible(true);
                method.invoke(sysloader,new Object[]{ url }); 
            } 
            catch (Throwable e) 
            {
                e.printStackTrace();
                throw new IOException("Could not add plugin provider " + url + " to system classloader.");
            }
        }
    }    

    /** Internal use only. Not supported */
    public synchronized File[] getProviderFiles(File providerDirectory, boolean isFirstCall)
    {
        if(isFirstCall)
        {
            this.spc_count=-1;
            this.listOfFiles.clear();
        }

        this.spc_count=spc_count+1;
        this.spcs = "";
        for (int i = 0; i < spc_count; i++)
        {
            this.spcs += " ";
        }
        if(providerDirectory.isFile())
        {
            // Add file to List.
            listOfFiles.add(providerDirectory.getAbsoluteFile());
        }
        else if (providerDirectory.isDirectory()) 
        {
            // Is a directory, call getProviderFiles again
            this.files=providerDirectory.listFiles();
            if(files!=null) 
            {
                for (int i = 0; i < files.length; i++)
                {
                    getProviderFiles(files[i], false);
                }
            } 
            else 
            {
                logger.log(Level.WARNING, "Skipping Plugin Provider Directory due to inadequete permissions: {0}", providerDirectory.getAbsolutePath());
            }
       }
       spc_count--;
       return listOfFiles.toArray(new File[0]);
    }

    /** Internal use only. Not supported. Only extracts configuration file if one does not yet exist. */
    public synchronized void extractProviderConfiguration(File rootConfigurationDirectory, File providerFile, String pluginPropertiesFolderPath, PropertiesAccessor propertiesToUpdate) throws Exception 
    {
        pluginPropertiesFolderPath.indexOf(pluginPropertiesFolderPath);
        this.is= new FileInputStream(providerFile.getAbsolutePath());
        this.zis = new ZipInputStream(is);
        try 
        {
            this.buf = new byte[8192];
            //this.zipentry = zis.getNextEntry();
            while ((this.zipentry= zis.getNextEntry()) != null) 
            {
                // If the path is /conf, and plugin.properties is not the zip entry...
                if(zipentry.getName().indexOf(pluginPropertiesFolderPath)==0)
                {
 //                   System.out.println(zipentry.getName() + ":" + zipentry.getName().indexOf(PLUGIN_PROPERTIES) + " = -1");
                    if(zipentry.getName().indexOf(PLUGIN_PROPERTIES)==-1)
                    {
                        //System.out.println(zipentry.getName() + ":" + zipentry.getName().indexOf(PLUGIN_PROPERTIES) + " = -1");
                        // Is a conf entry so write to file if it does not already exist.
                        this.entryName = rootConfigurationDirectory.getAbsolutePath().concat("/").concat(zipentry.getName());
                        this.entryName = entryName.replace('/', File.separatorChar);
                        this.entryName = entryName.replace('\\', File.separatorChar);
                        this.newFile = new File(entryName);  
                        // If file does not exist copy it, otherwise leave it as is.
                        if(!newFile.exists())
                        {
                            if (zipentry.isDirectory()) 
                            {
                                if (!newFile.exists() && !newFile.mkdirs()) 
                                {
                                    throw new Exception("Could not create new configuration directory during provider configuration extraction: " + newFile);
                                }
                                this.zipentry = zis.getNextEntry();
                            }
                            else 
                            {
                                this.fos = new FileOutputStream(entryName);
                                try 
                                {
                                    while ((n = zis.read(buf)) > 0) 
                                    {
                                        fos.write(buf, 0, n);
                                    }
                                } 
                                finally 
                                {
                                    fos.close();
                                }
                                zis.closeEntry();
                                this.zipentry = zis.getNextEntry();
                            }
                        }
                    }
                    else if(zipentry.getName().indexOf(PLUGIN_PROPERTIES)!=-1)
                    {                      
                        // If this is the plugin.properties zip entry extract the Properties and write to roci.properties, or rcsm.properties depending on which it is.
                        this.plugin_properties = new Properties();
                        // Load the Properties file from zipEntry.
                        plugin_properties.load(zis);
                        // Get the plugin_properties keySet
                        this.outNames = plugin_properties.keySet().toArray(); 
                        // Get the Array length
                        this.currentID = 0;
                        this.arrayLength = 0;
                        this.pos = 0;
                        // Obtain the number of registered RCSM Plugins.
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
                                    if (currentID > arrayLength) 
                                    {
                                        this.arrayLength = currentID;
                                    }
                                } 
                                catch (NumberFormatException e) 
                                {
                                    // Ignore this property key. It does not match requirements
                                    logger.log(Level.WARNING, "rcsm.properties key for plugin property does not contain a valid rcsm id in the format of #.key=. Actual key value is: {0}", keyArray[0]);
                                }
                            }
                            this.pos=pos + 1;
                        }                        
                        
                        // Initialize arrays and set the Array Lengths
                        this.classes = new String[arrayLength + 1];
                        this.names = new String[arrayLength + 1];
                        this.versions = new double[arrayLength + 1];
                        this.enabled = new boolean[arrayLength + 1];     
                        // Update the Properties Object:
                        // 1. read the keys and values.                                       
                        this.pos=0;                       
                        this.currentID = 0;
        // Initialize the RCSM or ROCI Properties Arrays                       
                        while (pos < outNames.length) 
                        {
                            this.name = (String) outNames[pos];
                            this.keyArray = name.split("\\.");

                            if (keyArray.length == 2) 
                            {
                                // Is a Plugin Provider Property.
                                try 
                                {
                                    // Provider ID
                                    this.currentID = Integer.parseInt(keyArray[0]);
                                    this.currentKey = keyArray[1];
                                    if (currentKey.equals(PROPERTY_NAME)) 
                                    {
                                        this.names[currentID] = (String) plugin_properties.get(name);
                                    } 
                                    else if (currentKey.equals(PROPERTY_VERSION)) 
                                    {
                                        this.versions[currentID] = Double.parseDouble((String) plugin_properties.get(name));
                                    } 
                                    else if (currentKey.equals(PROPERTY_CLASS)) 
                                    {
                                        this.classes[currentID] = (String) plugin_properties.get(name);
                                    }
                                    else if (currentKey.equals(PROPERTY_ENABLED)) 
                                    {
                                        if(((String)plugin_properties.get(name))!=null&&((String)plugin_properties.get(name)).toLowerCase().equals("true"))
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
                                    logger.log(Level.SEVERE,"plugin.properties key for plugin property does not contain a valid property id in the format of #.key=. Actual key value is: " + keyArray[0], e);
                                }
                            }
                            this.pos = pos + 1;
                        }  
                        // Read the actual Plugin Manager Properties file keys and values for compare.        
                        // Get the propertiesToUpdate keySet
                        this.outNames = ((Map) propertiesToUpdate.getProperties()).keySet().toArray(); 
                        // Update the Properties Object:
                        // Get the Array length
                        this.currentID = 0;
                        this.arrayLength = 0;
                        this.pos = 0;
                        // Obtain the number of registered RCSM Plugins.
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
                                    if (currentID > arrayLength) 
                                    {
                                        this.arrayLength = currentID;
                                    }
                                } 
                                catch (NumberFormatException e) 
                                {
                                    // Ignore this property key. It does not match requirements
                                    logger.log(Level.WARNING, "rcsm.properties or roci.properties key for plugin property does not contain a valid rcsm id in the format of #.key=. Actual key value is: {0}", keyArray[0]);
                                }
                            }
                            this.pos=pos + 1;
                        }                        
                        
                        // Initialize arrays and set the Array Lengths
                        this.properties_classes = new String[arrayLength + 1];
                        this.properties_names = new String[arrayLength + 1];
                        this.properties_versions = new double[arrayLength + 1];
                        this.properties_enabled = new boolean[arrayLength + 1];                    
                        
                        // 1. read the keys and values.                                       
                        this.pos=0;                       
                        this.currentID = 0;
                        while (pos < outNames.length) 
                        {
                            this.name = (String) outNames[pos];
                            this.keyArray = name.split("\\.");

                            if (keyArray.length == 2) 
                            {
                                // Is a Plugin Provider Property.
                                try 
                                {
                                    // Provider ID
                                    this.currentID = Integer.parseInt(keyArray[0]);
                                    this.currentKey = keyArray[1];
                                    if (currentKey.equals(PROPERTY_NAME)) 
                                    {
                                        this.properties_names[currentID] = (String) propertiesToUpdate.get(name);
                                    } 
                                    else if (currentKey.equals(PROPERTY_VERSION)) 
                                    {
                                        this.properties_versions[currentID] = Double.parseDouble((String) propertiesToUpdate.get(name));
                                    } 
                                    else if (currentKey.equals(PROPERTY_CLASS)) 
                                    {
                                        this.properties_classes[currentID] = (String) propertiesToUpdate.get(name);
                                    }
                                    else if (currentKey.equals(PROPERTY_ENABLED)) 
                                    {
                                        if(((String)propertiesToUpdate.get(name))!=null&&((String)propertiesToUpdate.get(name)).toLowerCase().equals("true"))
                                        {
                                            this.properties_enabled[currentID] = true;
                                        }
                                        else
                                        {
                                            this.properties_enabled[currentID] = false;
                                        }
                                    }                    
                                } 
                                catch (NumberFormatException e) 
                                {
                                    // Ignore this property key. It does not match requirements
                                    logger.log(Level.SEVERE,"plugin.properties key for plugin property does not contain a valid property id in the format of #.key=. Actual key value is: " + keyArray[0], e);
                                }
                            }
                            this.pos = pos + 1;
                        }                         
                                
                        // 2. Check if existing plugin name and version is already defined, and if so skip, and skip if one does.
                        // instantiate the isSameArray with isSame boolean variables.
                        Arrays.fill(this.isSameArray=new boolean[classes.length], false);
                        for(int i = 0;i<classes.length;i++)
                        {
                            for(int j=0;j<properties_classes.length;j++)
                            {
                                if(names[i].equals(properties_names[j])&&versions[i]==properties_versions[j])
                                {
                                   this.isSameArray[i]=(this.isSame=true); 
                                }
                            }
                        }
                        // 3. If one does not iterate last index id, and add to the Properties
                        for(int i=0;i<isSameArray.length;i++)
                        {
                            if(!isSameArray[i])
                            { 
                                // name and version not registered in properties file
                                propertiesToUpdate.put(String.valueOf(properties_classes.length-1).concat(".").concat("name"), names[i]);
                                propertiesToUpdate.put(String.valueOf(properties_classes.length-1).concat(".").concat("version"), String.valueOf(versions[i]));                      
                                propertiesToUpdate.put(String.valueOf(properties_classes.length-1).concat(".").concat("class"), classes[i]);                      
                                propertiesToUpdate.put(String.valueOf(properties_classes.length-1).concat(".").concat("enabled"), String.valueOf(enabled[i]));
                            }
                        }
                        // 4. Store the Properties (write to file).        
                        if(!propertiesToUpdate.writeFile())
                        {
                            logger.log(Level.WARNING, "Not able to update the RMDMIA Plugin Properties file. Manual definition of plugin required.");
                        }   
                        // close the plugin_properties specific variables.
                        this.plugin_properties=null;  
                        this.names=null; 
                        this.classes=null; 
                        this.versions=null; 
                        this.enabled=null;
                        this.properties_names=null; 
                        this.properties_classes=null; 
                        this.properties_versions=null; 
                        this.properties_enabled=null;                        
                        this.currentKey=null;
                        this.keyArray=null;
                        this.outNames=null;
                        this.zipentry = zis.getNextEntry();
                    }  
                }
            }
        } 
        finally 
        {
            zis.close();
        }
    }    

    /**
     *
     */
    public final static String timer="-timer_on";
    /**
     *
     * @param args
     */
    public static void main(String[] args)
    {
        ControllerManager rmdmia;
        if(0<args.length)
        {
            // arguments passed in parse them.
            if(args[1].equals("-timer_on"))
            {
                rmdmia = new ControllerManager(true);
            }
            else
            {
                // Timer Service disabled.
                rmdmia = new ControllerManager(false);                
            }
        }
        else
        {
            // Timer Service disabled.
            rmdmia = new ControllerManager(false);             
        }
        Controller controller=rmdmia.getController();
    }
}
