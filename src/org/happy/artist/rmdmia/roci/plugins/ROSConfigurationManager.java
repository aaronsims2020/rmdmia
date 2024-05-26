package org.happy.artist.rmdmia.roci.plugins;
import java.io.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.happy.artist.rmdmia.gui.ConnectionMonitor;
import org.happy.artist.rmdmia.rcsm.providers.ros.ROSNode;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.ROSSubscriberMessageManager;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.ROSTopicRegistryMessageDefinition;
import org.happy.artist.rmdmia.roci.ROCIProvider;

/**
 * The ControllerManager ROS Configuration Manager GUI Tool implemented 
 * as a ROCI Plugin.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2013 Happy Artist. All rights reserved.
 * 
 */
public class ROSConfigurationManager extends ROCIProvider
{
    private final static String name="ros_configuration_manager"; 
    private final static double version = 1.0;       
    private Properties properties;
    
    private ROSNode rosNode;
    private ConnectionMonitor monitor;
    private Object[][] tableData;
    private Object[] tableColumnNames;
    // initialize method variables
    private boolean isInitialized=false;
    
    // Define and instantiate Logger
    private Logger logger = Logger.getLogger(ROSConfigurationManager.class.getName());  
    
    /** Initialize the ROCI TelnetServer Plugin. */
    public void initialize() 
    {
        logger.log(Level.INFO, "Initializing ROS Configuration Manager GUI Tool.");
        // Set the ROCI Provider Data Transfer method to byte data transfer 
        // method, even though no actual transfer method is required due to 
        // obtaining a direct reference to ROSNode.
        setDataTransferMethod(this.NO_TRANSFER_METHOD);        
        // Setup the ROCI Plugin properties.
        this.properties=getProperties();

        // Get the ROSNode from RCSMManager.
        this.rosNode = (ROSNode)getController().getRCSM().getProviderByName("ros");
        
        // Update 
//        Controller controller = getController();
//        RCSMManager rcsmManager = controller.getRCSM();
//        RCSMProvider[] providers = rcsmManager.getProviders();
//        for(int i = 0;i<providers.length;i++)
//        {
//            System.out.println("Provider:" + providers[i].getName());
//        }
                //.getProviderByName("ros");
        if(rosNode!=null&&rosNode.isInitialized())
        {
            logger.log(Level.INFO, "Opening ROS ROCI Configuration Manager GUI Tool.");
            // instantiate the GUI
            this.tableData = rosNode.getMonitoringInformation();
            this.tableColumnNames = rosNode.getMonitoringTableColumnNames();
            this.monitor = new ConnectionMonitor(tableData, tableColumnNames, rosNode.getCallerID(), (ROSSubscriberMessageManager)rosNode.getSubscriberMessageManager(), rosNode, rosNode.getProperties(), rosNode.getPropertiesFilePath());                   
        }
        else if(!rosNode.isInitialized())
        {
            // ROS Plugin disabled
             logger.log(Level.WARNING, "The \"ros\" RCSM Provider is not enabled. To start the ROS Configuration Manager, the \"ros\" RCSM Provider must be enabled.");           
        }
        else
        {
            // Could not find ROS Log this.
            logger.log(Level.WARNING, "A \"ros\" RCSM Provider was not found.");
        }
        // Set isInitialized to true.
        this.isInitialized=true;                     
        logger.log(Level.INFO, "ROCI ROS Configuration Manager GUI Tool initialized");
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
        throw new UnsupportedOperationException("Not supported yet.");
        }

    @Override
    public void setInputStream(InputStream is) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public InputStream getInputStream() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public OutputStream getOutputStream() 
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void processIncoming(byte[] data) 
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void processOutgoing(byte[] data) 
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean recycle() 
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void shutdown() 
    {
        // Set isInitialized to false.
        this.isInitialized=false;   
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /** Return boolean is initialized on ROCI Provider. 
     * @return boolean returns false if ROCI Provider is not initialized. 
     */   
    public boolean isInitialized() 
    {
        return isInitialized;
    }
}




