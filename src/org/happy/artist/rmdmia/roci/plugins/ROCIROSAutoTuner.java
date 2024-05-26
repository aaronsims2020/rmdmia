package org.happy.artist.rmdmia.roci.plugins;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.happy.artist.rmdmia.rcsm.RCSMException;
import org.happy.artist.rmdmia.rcsm.provider.CommunicationSenderInterface;
import org.happy.artist.rmdmia.rcsm.providers.ros.ROSNode;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.ServiceProbeMessage;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.ROSSubscriberMessageManager;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.ROSTopicRegistryMessageDefinition;
import org.happy.artist.rmdmia.roci.ROCIProvider;

/**
 * The ROS Auto Tuner implemented as a ROCI Plugin. Checks the ROS Configuration
 * at startup, and if it is not configured will automatically discover Service
 * MD5Sum, and Types. This functionality was intended to go in the ROS Plugin,
 * and needed to be moved outside of the ROSNode Thread to set a timer while 
 * waiting for ROSNode response data, and reinitializing ROSNode between 
 * discovery messages.
 * 
 * Check for Empty topicRegistry file. Check for missing md5sums. Check for missing 
 * service types.
 *  Step 1 - probe services for md5sums.
 *  Step 2 - probe services for types.
 *  Step 3 - restart ROSNode with new configuration.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2014 Happy Artist. All rights reserved.
 * 
 */
public class ROCIROSAutoTuner  extends ROCIProvider
{
    // ROCI Provider variables
    private final static String name="ros_auto_tuner"; 
    private final static double version = 1.0;       
    private Properties properties;
    // Reference to rosNode
    private ROSNode rosNode;
    // initialize method variables
    private boolean isInitialized=false;  
    // Thread to run the controller loop.
    private Thread thread;
    private CommunicationSenderInterface sender;    
    // Define and instantiate Logger
    private Logger logger = Logger.getLogger(ROCIROSAutoTuner.class.getName());  
    // set instance in constructor for message callback to reference.
    private static ROCIROSAutoTuner instance=null;
    // processAutoConfiguration method variables.
    private int serviceTID;
    private ROSTopicRegistryMessageDefinition autoConfigTRMD;
    private String serviceName;
    // currentTID used to store the current TID value for verifying int is not -1
    private int currentTID;   
    private String[] serviceNames;
    private ROSSubscriberMessageManager subscriberMessageManager;
    private String caller_id;
    // Timed loop counting...
    private boolean outOfTime;
    private boolean allMd5sumServiceDefsFilled = false;
    private ROSTopicRegistryMessageDefinition[] defs;
    private int serviceMd5sumsFilled = 0;
    //private int servicesCount = 0;
    private int previousServicesCount = 0;
    private long startTime;
    private int serviceTypesFilled = 0;
    private boolean allTypeServiceDefsFilled = false;        
    
    public ROCIROSAutoTuner()
    {
        this.instance=this;
    }
    
    /** return reference to constructed ROCIROSAutoTuner. */
    public static ROCIROSAutoTuner getInstance()
    {
        return instance;
    }    

    /** Initialize the ROCI ROS Auto Tuner Plugin. */
    public void initialize() 
    {
        logger.log(Level.INFO, "Initializing ROS Auto Tuner plugin.");
        // Set the ROCI Provider Data Transfer method to byte data transfer 
        // method, even though no actual transfer method is required due to 
        // obtaining a direct reference to ROSNode.
        setDataTransferMethod(this.NO_TRANSFER_METHOD);        
        // Setup the ROCI Plugin properties.
        this.properties=getProperties();

        // Get the ROSNode from RCSMManager.
        this.rosNode = (ROSNode)getController().getRCSM().getProviderByName("ros");
        
        if(rosNode!=null&&rosNode.isInitialized())
        {
            //logger.log(Level.INFO, "Testing ROS TurtleSim..");
            try 
            {
                // get the topic/service lookup Map
                //this.topicServiceLookupMap = rosNode.getSenderLookupMap();
              //  this.sender=rosNode.getPublisherSenders()[rosNode.getTopicIndex("/turtle1/cmd_vel")];
//                System.out.println("Turtle Sender Topic threadName: " + ((TCPROSPublisherCommunicator)sender).threadName);
                // hook up the PS/3 controller.
                this.thread = new Thread(new Runnable() {
                public void run()
                {
                    // if ROSNode auto configuration on startup...
                    if(rosNode.startup_auto_config)
                    {
                        processAutoConfiguration();
                    }
                    // Shutdown the plugin.
                    shutdown();
                }
       });
                thread.start();
                
            } 
            catch (Exception ex) 
            {
                Logger.getLogger(ROSTurtlesimController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
            // Set isInitialized to false.
            this.isInitialized=true;                    
 //           this.monitor = new ConnectionMonitor(tableData, tableColumnNames, rosNode.getCallerID(), rosNode.getSubscriberMessageManager(), rosNode, rosNode.getProperties(), rosNode.getPropertiesFilePath());                   
        }
        else if(!rosNode.isInitialized())
        {
            // ROS Plugin disabled
             logger.log(Level.WARNING, "The \"ros\" RCSM Provider is not enabled. To start the ROS TurtleSim controller, the \"ros\" RCSM Provider must be enabled.");       
            // Set isInitialized to false.
            this.isInitialized=false;                            
        }
        else
        {
            // Could not find ROS Log this.
            logger.log(Level.WARNING, "A \"ros\" RCSM Provider was not found. TurtleSim controller not started.");
            // Set isInitialized to false.
            this.isInitialized=false;                    
        }
      
        logger.log(Level.INFO, "ROCI ROS TurtleSim controller initialized");
    }

    /**
     * Process the startup automatic configuration. Called if the property
     * startup_auto_config is set to true.
     */
    private synchronized void processAutoConfiguration() 
    {
        // TODO: Add checking that will remove the need to reinitialize at all.
        // Loop through reinitializeCount while less than 2.
        while(rosNode.reinitializeCount < 3)
        {
        System.out.println("Inside processAutoConfiguration Loop: " + rosNode.reinitializeCount);
        this.serviceNames=rosNode.getServiceSenderNames();
        this.subscriberMessageManager=(ROSSubscriberMessageManager)rosNode.getSubscriberMessageManager();
        this.caller_id=rosNode.getCallerID();
        // Call populateTopicRegistry to verify all the configuration items exist.
        // If reinitializeCount is 0, call populateTopicRegistry(), otherwise
        // skip it and call the following items, that call the probe to retrieve 
        // service Topic Types.
        if (rosNode.reinitializeCount == 0) {
            rosNode.populateTopicRegistry();
        }

        if (rosNode.reinitializeCount == 1) {
            // Set the service TID to 0
            this.serviceTID = 0;
            // Get the associated service data from the subscriber message manager, and loop through the md5sums for null, or invalid hashmap lengths.
            System.out.println("Servicenames.length:" + serviceNames.length);
            while (serviceTID < serviceNames.length) {
                // Set the service name for faster variable access than doing an array lookup every time...
                
                this.serviceName = serviceNames[serviceTID];
                if ((this.currentTID = subscriberMessageManager.getTIDByServiceName(serviceNames[serviceTID])) != -1) {
                    this.autoConfigTRMD = this.subscriberMessageManager.getTopicRegistryMessageDefinitionByTID(serviceTID);
                } else {
                    this.autoConfigTRMD = new ROSTopicRegistryMessageDefinition();
                }

                // If md5sum for Service is null, or the md5sum property is less than 32 characters in length, process a service probe on the md5sum.
                if (autoConfigTRMD.md5sum == null || autoConfigTRMD.md5sum.length() < 32) {
                    try {
                        rosNode.sendServiceMessage(new ServiceProbeMessage(caller_id, serviceName).getMessage(), rosNode.getService(serviceName));
                    } catch (IOException ex) {
                        Logger.getLogger(ROSNode.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (NullPointerException ex) {
                        Logger.getLogger(ROSNode.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (RCSMException ex) {
                        Logger.getLogger(ROSNode.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                // Increment the serviceTID by one for the while loop.
                this.serviceTID = serviceTID + 1;
            }
        }

// TODO: Implement a timer delay if necessary based on variable updates to the properties file. 

// need to run in a separate thread since calling sleep on the main thread will not get us anywhere...
// Loop until all MD5Sums are found.     
        if (rosNode.reinitializeCount == 1) {        
        this.outOfTime=false;
        this.allMd5sumServiceDefsFilled = false;
        this.defs=null;
        this.serviceMd5sumsFilled = 0;
        //this.servicesCount = 0;
        this.previousServicesCount = 0;        
        this.startTime = System.currentTimeMillis();
        
       
        while (outOfTime != true) {
// TODO: take current time, and if 2 seconds elapses without a change set outOfTIme to true.
            System.out.println("Inside outOfTime Loop...");
        this.serviceMd5sumsFilled = 0; 
            this.defs = subscriberMessageManager.getTopicDefinitions();
            // Get current time in Millis
            this.startTime = System.currentTimeMillis();
            System.out.println("defs.length:" + defs.length);
            for (int i = 0; i < defs.length; i++) {
                if ((defs[i].service != null && defs[i].service.length() > 0) && (defs[i].md5sum != null && defs[i].md5sum.length() > 0)) {
                    this.serviceMd5sumsFilled = serviceMd5sumsFilled + 1;
                }

            }
            System.out.println("serviceMd5sumsFilled: " + serviceMd5sumsFilled + ", servicesCount: " + serviceNames.length);
            
            if (serviceMd5sumsFilled == serviceNames.length ) {
                this.allMd5sumServiceDefsFilled = true;
                // TODO: end while loop and proceed
                this.outOfTime = true;
                System.out.println("All Md5sums populated by responses.");                
            } 
            else if(System.currentTimeMillis() - startTime > 2000)
            {
                this.allMd5sumServiceDefsFilled = true;
                // TODO: end while loop and proceed
                this.outOfTime = true;                
                System.out.println("Timeout on Md5sum responses");
            }
            else if(previousServicesCount!=serviceMd5sumsFilled&&serviceMd5sumsFilled<serviceNames.length)
            {
                this.previousServicesCount=serviceMd5sumsFilled;
                // Get current time in Millis
                this.startTime = System.currentTimeMillis();
            }
            try {
                // sleep for 10ms.
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(ROCIROSAutoTuner.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        }
        // TODO: Update the topic registry properties from file...
// Process the service types.
            System.out.println("Passing Service Types Loop: " + rosNode.reinitializeCount);
        if (rosNode.reinitializeCount == 2) {
            System.out.println("Inside Service Types Loop");
            // Now perform type lookup loop
            // Set the service TID to 0
            this.serviceTID = 0;
            // Get the associated service data from the subscriber message manager, and loop through the type for null, or invalid hashmap lengths, and verify if it is null, or invalid length, that it does not attempt to call the sendServiceMessage method.
            while (serviceTID < serviceNames.length) {
                // Set the service name for faster variable access than doing an array lookup every time...
                this.serviceName = serviceNames[serviceTID];
                if ((this.currentTID = this.subscriberMessageManager.getTIDByServiceName(serviceNames[serviceTID])) != -1) {
                    this.autoConfigTRMD = this.subscriberMessageManager.getTopicRegistryMessageDefinitionByTID(serviceTID);
                } else {
                    this.autoConfigTRMD = new ROSTopicRegistryMessageDefinition();
                }

                // If md5sum for Service is null, or the md5sum property is less than 32 characters in length, process a service probe on the md5sum.
                if ((autoConfigTRMD.md5sum != null && autoConfigTRMD.md5sum.length() == 32) && autoConfigTRMD.type == null || autoConfigTRMD.type.equals("")) {
                    try {
                        rosNode.sendServiceMessage(new ServiceProbeMessage(caller_id, serviceName).getMessage(), rosNode.getService(serviceName));
                    } catch (IOException ex) {
                        Logger.getLogger(ROSNode.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (NullPointerException ex) {
                        Logger.getLogger(ROSNode.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (RCSMException ex) {
                        Logger.getLogger(ROSNode.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                // Increment the serviceTID by one for the while loop.
                this.serviceTID = serviceTID + 1;
            }
// TODO: Test timing
        this.outOfTime = false;
        this.defs=null;
        this.serviceTypesFilled = 0;
        this.previousServicesCount = 0;
        this.startTime = System.currentTimeMillis();
        while (outOfTime != true) 
        {
// TODO: take current time, and if 2 seconds elapses without a change set outOfTIme to true.
            this.serviceTypesFilled = 0;
            this.defs = subscriberMessageManager.getTopicDefinitions();
            // Get current time in Millis
            this.startTime = System.currentTimeMillis();
            for (int i = 0; i < defs.length; i++) {
                if ((defs[i].service != null && defs[i].service.length() > 0) && (defs[i].type != null && defs[i].type.length() > 0)) {
                    this.serviceTypesFilled = serviceTypesFilled + 1;
                }

            }
            if (serviceTypesFilled == serviceNames.length) {
                this.allTypeServiceDefsFilled = true;
                // TODO: end while loop and proceed
                this.outOfTime = true;
                System.out.println("All Service Types populated by responses.");                 
            } 
            else if(System.currentTimeMillis() - startTime > 2000)
            {
                this.allTypeServiceDefsFilled = true;
                // TODO: end while loop and proceed
                this.outOfTime = true;
                System.out.println("Timeout on Service Types responses");                
            }
            else {
                // Get current time in Millis
                this.startTime = System.currentTimeMillis();
            }
            try {
                // sleep for 10 ms till next iteration...
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(ROCIROSAutoTuner.class.getName()).log(Level.SEVERE, null, ex);
            }
        }            
// End timing test            
        }
        System.out.println("Reinitialize count: " + rosNode.reinitializeCount);
        if (rosNode.reinitializeCount < 3) {
            try {
                rosNode.reinitialize();
        System.out.println("Reinitialize count: " + rosNode.reinitializeCount + ", post reinitialize call.");                
            } catch (RCSMException ex) {
                Logger.getLogger(ROSNode.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        }
// TODO: Implement a timer delay if necessary based on variable updates to the properties file. 

// TODO: Update the topic registry properties from file...        
    }
    
    /** Return the plugin version number. A version number of 0 is default of no version number. */
    public double getVersion() 
    {
        return version;
    }
 
    /** Return the ROCI Plugin name. */ 
    public String getName() 
    {
        return name;
    }    
    
    @Override
    public void setOutputStream(OutputStream os) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setInputStream(InputStream is) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public InputStream getInputStream() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public OutputStream getOutputStream() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void processIncoming(byte[] data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void processOutgoing(byte[] data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean recycle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void shutdown() 
    {
        this.thread=null;
        // Set isInitialized to false.
        this.isInitialized=false;   
    }

    /** Return boolean is initialized on ROCI Provider. 
     * @return boolean returns false if ROCI Provider is not initialized. 
     */   
    public boolean isInitialized() 
    {
        return isInitialized;
    }
    
}
