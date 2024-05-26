package org.happy.artist.rmdmia.perception.engine.plugins;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.happy.artist.rmdmia.perception.engine.PEProvider;
import org.happy.artist.rmdmia.perception.engine.sensor.SensorProcessorManager;
import org.happy.artist.rmdmia.perception.engine.sensor.SensorProcessorManagerImpl;
import org.happy.artist.rmdmia.rcsm.provider.CommunicationSenderInterface;
import org.happy.artist.rmdmia.rcsm.providers.ros.ROSNode;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.ROSSubscriberMessageManager;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.ROSTopicRegistryMessageDefinition;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.sensor.ROSBagSensorProcessorFactory;
import org.happy.artist.rmdmia.utilities.ros.bag.RMDMIABagQueueFileWriter;

/** The Perception Engine interface/API between the Robot Hardware Controller 
 * and the Robotic Mission Decision Manager Intelligent Agent (RMDMIA).
 * 
 * @author Happy Artist
 * 
 * @copyright Copyright © 2014 Happy Artist. All rights reserved.
 */
// TODO: Add record, and stop record methods,that update SensorProcessor Manager Objects. Additionally, add ability to chose which topics will be recorded.
public class ROSPEProvider extends PEProvider
{
    // perception Engine Provider variables
    private final static String name="ros_pe_provider"; 
    private final static double version = 1.0;       
    private Properties properties;
    // initialize method variables
    private boolean isInitialized=false;
    // Thread to run the controller loop.
    private Thread thread;    
    // Reference to rosNode
    private ROSNode rosNode;    
    // Reference to SubscriberMessageManager
    private ROSSubscriberMessageManager smm;
    // reference to the SensorProcessorManagerImpl associated with the Perception Engine Manager.
    private SensorProcessorManagerImpl sensorProcessorManager;
    // ROS Bag Sensor Factory
    private ROSBagSensorProcessorFactory bag_factory = new ROSBagSensorProcessorFactory(getController());
    //  ROSBAG Sensor Processor class name
    private final static String ROSBAG_SENSOR_PROCESSOR_FACTORY_CLASS="org.happy.artist.rmdmia.rcsm.providers.ros.client.sensor.ROSBagSensorProcessorFactory";
    // isConnected method variables
    private ROSTopicRegistryMessageDefinition trmd;
    private String topic;
    private CommunicationSenderInterface csi;    

    /** Return the Perception Engine's SensorProcessorManagerImpl. */ 
    @Override
    public SensorProcessorManager getSensorProcessorManager()
    {
        return sensorProcessorManager;
    }

    // Define and instantiate Logger
    private static final Logger logger = Logger.getLogger(ROSPEProvider.class.getName());  
    // set instance in constructor for message callback to reference.
    private static ROSPEProvider instance=null;
    // rosbag writer Object
    private RMDMIABagQueueFileWriter rosBag;
    
    public ROSPEProvider()
    {
        instance=this;
    }
    
    /** return reference to constructed ROSPEProvider. */
    public static ROSPEProvider getInstance()
    {
        return instance;
    }
    
    
    /** Unsupported - Remove ROSBag SensorProcessors from SensorProcessingManager. 
     * Improve sensor processing performance while not recording by removing 
     * the sensor processor from the sensor processor iterations.
     */
    private int bag_loop_count;
    public synchronized void removeRecorderSensorProcessors()
    {
        // TODO: Remove recording sensor processors from SensorProcessorManager while not in use.
            // Set Bag Loop Count to 0.
            this.bag_loop_count=0;            
            while(bag_loop_count<hid_array.length)
            {
                sensorProcessorManager.remove(bag_loop_count);
            }
            
          //hid_array
    }
    
    public boolean is_recording=false;
    /** Return boolean rosbag is recording. Returns true if bag file recording is in process. */
    public boolean isRecording()
    {
        return is_recording;
    }      

    
    // In process ROSBag connection hids recording to rosbag. 
    private int[] hid_array=new int[0];
    private int[] sid_array=new int[0];;
    private int[] bag_factory_indices=new int[0];
    private int[][] registered_sensor_processor_ids= new int[0][0];
    private int current_hid;
    // For calls to isConnected on startRecording.
    private CommunicationSenderInterface[] subscribers;
    private CommunicationSenderInterface[] services;
    
    /** Start recording the rosbag file. Requires SensorProcessorFactory be registered in the Sensor Processor Manager for each org.happy.artist.rmdmia.rcsm.providers.ros.client.sensor.ROSBagSensorProcessorFactory associated with a topic or service. */
    public void startRecording(String file_path, int[] hid_array)
    {
        this.hid_array=hid_array;
        this.sid_array=new int[hid_array.length];
        this.bag_factory_indices=sensorProcessorManager.getSensorProcessorFactoryClassIndicesByClassname(bag_factory.getClass());
        this.registered_sensor_processor_ids=sensorProcessorManager.getRegisteredSensorProcessorIDs();
        // Obtain reference to CommunicationSenderInterfaces for isConnected check.
        this.subscribers=rosNode.getSubscriberSenders();
        this.services=rosNode.getServiceSenders();
        try 
        {
            this.is_recording=true;          
            rosBag.start(file_path, RMDMIABagQueueFileWriter.CHUNK_COMPRESSION_NONE);
 // TODO: Add Connection Header for each connection that is already connected.
            
// Determine if topic is connected....       
            StringBuilder builder = new StringBuilder();
            // Set all rosbag sensor factories to running.
            for(int i=0;i<hid_array.length;i++)
            {
                this.current_hid=hid_array[i];
                //// TEST CODE
                this.trmd=smm.getTopicRegistryMessageDefinitionByTID(current_hid);
                builder.append("TOPIC:").append(trmd.topic);
 /*               if(trmd.topic!=null&&current_hid!=-1&&rosNode.getTopicIndex(trmd.topic)==-1)
                {
                   System.out.println("TOPIC".concat(trmd.topic).concat(String.valueOf(rosNode.getTopicIndex(trmd.topic))));
                }
                * */
                ///// TEST CODE
                // If current_hid is connected call add connection header.
/*                if(current_hid!=-1&&isConnected(current_hid))
                {
                    // trmd should be safe to reference since isConnected was last to reference in synchronized manner.
                    rosBag.addConnectionHeader(trmd.topic.toCharArray(), current_hid,trmd.definition.toCharArray());
                }
*/
                for(int j=0;j<bag_factory_indices.length;j++)
                {
                    if(registered_sensor_processor_ids[bag_factory_indices[j]][0]==current_hid)
                    {
                        sensorProcessorManager.continuer(current_hid, registered_sensor_processor_ids[bag_factory_indices[j]][1]);
                        // end of loop
                        j=bag_factory_indices.length;
                    }
                }
            }
           // System.out.println("KAABOOM:" + builder.toString());
        } 
        catch (Exception ex) 
        {
            this.is_recording=false;
            Logger.getLogger(ROSPEProvider.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }    

    /** Return true if CommunicationsenderInterface associated with tid is connected. */
    private boolean isConnected(int tid)
    {
        this.trmd=smm.getTopicRegistryMessageDefinitionByTID(tid);
        if(trmd.topic!=null)
        {
            try
            {
                System.out.println("TOPIC:" + trmd.topic );
                this.csi=subscribers[rosNode.getTopicIndex(trmd.topic)];
                if(csi!=null)
                {
                    return csi.isConnected();
                }
            }
            catch(Exception e)
            {
                System.out.println("Kaa Boom Topic: " + trmd.topic + ", tid: " + tid);
                e.printStackTrace();
            }            
        }
        else if(trmd.service!=null)
        {
            this.csi=services[rosNode.getServiceIndex(trmd.service)];
            if(csi!=null)
            {
                return csi.isConnected();           
            }
        }
        return false;
    }
    
    /** Stop recording rosbag file. */ 
    public void stopRecording()
    {
        try 
        {
            // Set all rosbag sensor processors to hold.
            for(int i=0;i<hid_array.length;i++)
            {
                this.current_hid=hid_array[i];
                for(int j=0;j<bag_factory_indices.length;j++)
                {
                    if(registered_sensor_processor_ids[bag_factory_indices[j]][0]==current_hid)
                    {
                        sensorProcessorManager.hold(current_hid, registered_sensor_processor_ids[bag_factory_indices[j]][1]);
                        // end of loop
                        j=bag_factory_indices.length;
                    }
                }
            }            
            rosBag.end();
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(ROSPEProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.is_recording=false;

    }   
    
    /** Initialize the Perception Engine Plugin. */
    @Override
    public void initialize() 
    {
        logger.log(Level.INFO, "Initializing ROS Perception Engine plugin.");       
        // Setup the Perception Engine Plugin properties.
        this.properties=getProperties();
        try 
        {
             // Instantiate the SensorProcessorManager
             this.sensorProcessorManager = new SensorProcessorManagerImpl(getController());
        } 
        catch (UnsupportedEncodingException ex) 
        {
            Logger.getLogger(ROSPEProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Get a reference to rosBag
        this.rosBag=RMDMIABagQueueFileWriter.getInstance();
        
        // Get the ROSNode from RCSMManager.
        this.rosNode = (ROSNode)getController().getRCSM().getProviderByName("ros");
        this.smm=(ROSSubscriberMessageManager)rosNode.getSubscriberMessageManager();
        
        if(rosNode!=null&&rosNode.isInitialized())
        {
/*            try 
            {
                // get the topic/service lookup Map
                //this.topicServiceLookupMap = rosNode.getSenderLookupMap();
               
//                System.out.println("Turtle Sender Topic threadName: " + ((TCPROSPublisherCommunicator)sender).threadName);
                // hook up the PS/3 controller.
                this.thread = new Thread(new Runnable() {
                public void run()
                {
// TODO: ROS Thread....
                    
                }
       });
                thread.start();
                
            } 
            catch (Exception ex) 
            {
                Logger.getLogger(ROSPEProvider.class.getName()).log(Level.SEVERE, null, ex);
            } */
            
            // Set isInitialized to false.
            this.isInitialized=true;                  
        }
        else if(!rosNode.isInitialized())
        {
            // ROS Plugin disabled
             logger.log(Level.WARNING, "The \"ros\" RCSM Provider is not enabled. To start the ROS Perception Engine, the \"ros\" RCSM Provider must be enabled.");       
            // Set isInitialized to false.
            this.isInitialized=false;                            
        }
        else
        {
            // Could not find ROS Log this.
            logger.log(Level.WARNING, "A \"ros\" RCSM Provider was not found. ROS Perception Engine not started.");
            // Set isInitialized to false.
            this.isInitialized=false;                    
        }
      
        logger.log(Level.INFO, "ROS Perception Engine initialized");

     }  
    
    /** Return the plugin version number. A version number of 0 is default of no version number. */
    @Override
    public double getVersion() 
    {
        return version;
    }
 
    /** Return the Perception Engine Plugin name. */ 
    @Override
    public String getName() 
    {
        return name;
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
    }

    /** Return boolean is initialized on Perception Engine Provider. 
     * @return boolean returns false if Perception Engine Provider is not initialized. 
     */   
    @Override
    public boolean isInitialized() 
    {
        return isInitialized;
    }
}
