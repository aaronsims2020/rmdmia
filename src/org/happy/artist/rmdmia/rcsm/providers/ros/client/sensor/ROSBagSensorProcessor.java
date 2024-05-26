package org.happy.artist.rmdmia.rcsm.providers.ros.client.sensor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.happy.artist.rmdmia.Controller;
import org.happy.artist.rmdmia.rcsm.providers.ros.ROSMessage;
import org.happy.artist.rmdmia.perception.engine.sensor.SensorMessage;
import org.happy.artist.rmdmia.perception.engine.sensor.SensorProcessor;
import org.happy.artist.rmdmia.timing.TimerService;
import org.happy.artist.rmdmia.utilities.BytesToHex;
import org.happy.artist.rmdmia.utilities.ros.bag.RMDMIABagQueueFileWriter;

/** ROSBagSensorProcessor.java - The rosbag sensor processor reads incoming
 *  ROS messages, and writes them to the ROSBag file. Register the ROSBagSensorProcessor,
 *  in the SensorProcessorManager to Hardware IDs tp process to ROSBag.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2014 Happy Artist. All rights reserved.
 */
public class ROSBagSensorProcessor extends SensorProcessor
{
    // ROSMessage variable
    private ROSMessage ros_message;
    // 
    private boolean is_connection_header=true; 
    // Reference to the Bag Object
// TODO: add call to ROSNode to get a reference to ROSBagManager for writing.    
    private RMDMIABagQueueFileWriter rosBag;
    // Controller reference for latest time.
    private Controller controller;
    // TImer service reference
    private TimerService time;
    
    /** Construct the ROSBagProcessor. */
    public ROSBagSensorProcessor(Controller controller)
    {
        // Set reference to the controller.
        this.controller=controller;
        // Get reference to TimerService
        if((this.time=controller.getControllerManager().getTimerService())==null)
        {
            // Set default TimerService to microseconds. If user wants more or less precision they must set at startup.
            this.time = new TimerService(1,1,TimeUnit.MICROSECONDS);            
        }
        // Get a reference to rosBag
        this.rosBag=RMDMIABagQueueFileWriter.getInstance();
    }
    
    private boolean connection_header_set=false;
    @Override
    public SensorMessage process(SensorMessage message) 
    {
        // Obtain a reference to the message.
        this.ros_message=(ROSMessage)message;        
        // TODO: Figure out how to process the connection header...
        if(ros_message.connection_header!=null)
        {
            try {
                System.out.println("topic:ros_message.topic"+ros_message.topic +",ros_message.hid"+ros_message.hid+",connection_header:" + ros_message.connection_header);
                rosBag.addConnectionHeader(ros_message.topic, ros_message.hid, BytesToHex.bytesToHexChars(ros_message.connection_header));
            } catch (IOException ex) {
                Logger.getLogger(ROSBagSensorProcessor.class.getName()).log(Level.SEVERE, String.valueOf(TimerService.SYSTEM_TIME) + " rosbag recording failed to add Connection Header for "  + ros_message.topic + " hid: " + String.valueOf(ros_message.hid) , ex);
            }
            return message;
        }
        try {
            // Send the message to the ROS Bag file.
                rosBag.addMessage(TimerService.SYSTEM_TIME, ros_message.hid, BytesToHex.bytesToHexChars(ros_message.binary_body));
        } catch (IOException ex) {
            if(ros_message.topic!=null)
            {
                Logger.getLogger(ROSBagSensorProcessor.class.getName()).log(Level.SEVERE, String.valueOf(TimerService.SYSTEM_TIME) + " rosbag recording failed to add Message for topic: "  + ros_message.topic + " hid: " + String.valueOf(ros_message.hid), ex);
            }
            else if(ros_message.service!=null)
            {
                Logger.getLogger(ROSBagSensorProcessor.class.getName()).log(Level.SEVERE, String.valueOf(TimerService.SYSTEM_TIME) + " rosbag recording failed to add Message for service: "  + ros_message.service + " hid: " + String.valueOf(ros_message.hid), ex);                
            }
            else
            {
                Logger.getLogger(ROSBagSensorProcessor.class.getName()).log(Level.SEVERE, String.valueOf(TimerService.SYSTEM_TIME) + " rosbag recording failed to add Message: " + BytesToHex.bytesToHex(ros_message.binary_body) + ", hid: " + String.valueOf(ros_message.hid), ex);                
            }
        }
            return message;
    }
    
}
