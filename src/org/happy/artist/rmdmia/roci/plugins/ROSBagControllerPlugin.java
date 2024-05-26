package org.happy.artist.rmdmia.roci.plugins;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.happy.artist.rmdmia.Controller;
import org.happy.artist.rmdmia.perception.engine.plugins.ROSPEProvider;
import org.happy.artist.rmdmia.rcsm.RCSMException;
import org.happy.artist.rmdmia.rcsm.providers.ros.ROSNode;
import org.happy.artist.rmdmia.roci.ROCIProvider;


/**
 * The rosbag Controller Plugin implemented 
 * as a ROCI Provider Plugin. The purpose of this plugin is to create a 
 * GUI for testing Java based rosbag recording on RMDMIA.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2014 Happy Artist. All rights reserved.
 * 
 */
public class ROSBagControllerPlugin extends ROCIProvider
{
    // ROCI Provider variables
    private final static String name="rosbag_test_gui"; 
    private final static double version = 1.0;       
    private Properties properties;
    // Reference to ROS PE Provider
    private ROSPEProvider rosPE;
    // initialize method variables
    private boolean isInitialized=false;
    // Thread to run the controller loop.
    private Thread thread;
    
    // Define and instantiate Logger
    private Logger logger = Logger.getLogger(ROSBagControllerPlugin.class.getName());  
    // set instance in constructor for message callback to reference.
    private static ROSBagControllerPlugin instance=null;
    public ROSBagControllerPlugin()
    {
        this.instance=this;
    }
    
    /** return reference to constructed ROSTurtlesimKeyController. */
    public static ROSBagControllerPlugin getInstance()
    {
        return instance;
    }
    
    /** Initialize the ROCI rosbag test GUI Plugin. */
    public void initialize() 
    {
        logger.log(Level.INFO, "Initializing rosbag test GUI plugin.");
        // Set the ROCI Provider Data Transfer method to byte data transfer 
        // method, even though no actual transfer method is required due to 
        // obtaining a direct reference to ROSNode.
        setDataTransferMethod(this.NO_TRANSFER_METHOD);        
        // Setup the ROCI Plugin properties.
        this.properties=getProperties();

        // Get the ROSPEProvider from PEManager.
        this.rosPE = (ROSPEProvider)getController().getPE().getProviderByName("ros_pe_provider");
        
        if(rosPE!=null&&rosPE.isInitialized())
        {
            logger.log(Level.INFO, "ROSPEProvider is initialized proceeding to launch rosbag test GUI..");
            try 
            {
                // get the topic/service lookup Map
                //this.topicServiceLookupMap = rosNode.getSenderLookupMap();
              //  this.sender=rosNode.getPublisherSenders()[rosNode.getTopicIndex("/turtle1/cmd_vel")];
//                System.out.println("Turtle Sender Topic threadName: " + ((TCPROSPublisherCommunicator)sender).threadName);
                // hook up the PS/3 controller.
                this.thread = new Thread(new Runnable()
                {
                public void run()
                {
                    startController();
                   // try 
                    //{
        //                if(ROSBagControllerPlugin.this.sender!=null)
          //              {

                      //  }
                 //       else
                     //   {
                   //     Logger.getLogger(ROSBagControllerPlugin.class.getName()).log(Level.INFO, "rosbag tester GUI could not send message due to the Publisher not being initialized.");
                   //     }
                    //} catch (IOException ex) 
                   // {
                     //   Logger.getLogger(ROSBagControllerPlugin.class.getName()).log(Level.SEVERE, null, ex);
                    //}
                }
       });
                thread.start();
                
            } 
            catch (Exception ex) 
            {
                Logger.getLogger(ROSBagControllerPlugin.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
            // Set isInitialized to false.
            this.isInitialized=true;                    
 //           this.monitor = new ConnectionMonitor(tableData, tableColumnNames, rosNode.getCallerID(), rosNode.getSubscriberMessageManager(), rosNode, rosNode.getProperties(), rosNode.getPropertiesFilePath());                   
        }
        else if(!rosPE.isInitialized())
        {
            // ROS Plugin disabled
             logger.log(Level.WARNING, "The \"ros_pe_provider\" PE Provider is not enabled. To start the ROS Perception Engine, the \"ros\" RCSM Provider must be enabled.");       
            // Set isInitialized to false.
            this.isInitialized=false;                            
        }
        else
        {
            // Could not find ROS Log this.
            logger.log(Level.WARNING, "A \"ros_pe_provider\" PE Provider was not found. rosbag test GUI not started.");
            // Set isInitialized to false.
            this.isInitialized=false;                    
        }
      
        logger.log(Level.INFO, "ROCI rosbag test GUI plugin initialized");
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
        this.thread=null;
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
    
    // GUI Launcher.
    public void startController()
    {
        // Add JFrame for AWTKeyboard to work...
        JFrame frame = new JFrame("ROSBag Recorder");
        // Add a window listner for close button
        frame.addWindowListener(new WindowAdapter() {

                public void windowClosing(WindowEvent e) {
                        System.exit(0);
                }
        });
        // This is an empty content area in the frame
        ROSRecorderPanel panel = new ROSRecorderPanel(rosPE, getController());
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }    
}
