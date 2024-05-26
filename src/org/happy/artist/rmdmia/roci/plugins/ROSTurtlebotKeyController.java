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
import javax.swing.JLabel;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;
import org.happy.artist.rmdmia.instruction.InstructionOutputHandler;
import org.happy.artist.rmdmia.instruction.providers.InstructionObjectPool;
import org.happy.artist.rmdmia.rcsm.RCSMException;
import org.happy.artist.rmdmia.rcsm.provider.CommunicationSenderInterface;
import org.happy.artist.rmdmia.rcsm.providers.ros.ROSNode;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.ServiceProbeMessage;
import org.happy.artist.rmdmia.roci.ROCIProvider;
import org.happy.artist.rmdmia.utilities.BytesToHex;
import org.happy.artist.rmdmia.utilities.HexToBytes;
//import ros.kobuki_msgs.MotorPower;


/**
 * The ROS Turtlebot Keyboard Sample program implemented 
 * as a ROCI Provzider Plugin.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2014 Happy Artist. All rights reserved.
 * 
 */
public class ROSTurtlebotKeyController extends ROCIProvider
{
    // ROCI Provider variables
    private final static String name="ros_key_control_sample"; 
    private final static double version = 1.0;       
    private Properties properties;
    // Reference to rosNode
    private ROSNode rosNode;
    // initialize method variables
    private boolean isInitialized=false;
    // message= 4 bytes message_length + 20 bytes 5 float array.
    private byte[] message = new byte[52];
    // Thread to run the controller loop.
    private Thread thread;
    private CommunicationSenderInterface sender;
    private CommunicationSenderInterface motor_state_sender;    
    //private CommunicationSenderInterface velocity_sender;        
    // JInput variables
    private Component component;
    private EventQueue queue;
    private Event event;
    private Controller[] controllers;
    private Component[] components;
    private String cname;
    // turtlesim movements.
    private static byte[] GAMEPAD_UP=Movements2.getUpMovement();
    private static byte[] GAMEPAD_DOWN=Movements2.getDownMovement();
    private static byte[] GAMEPAD_LEFT=Movements2.getLeftMovement();
    private static byte[] GAMEPAD_RIGHT=Movements2.getRightMovement();
    
    private InstructionOutputHandler motor_power_handler;
    private InstructionObjectPool motor_power_object_pool;
    
    // Instruction Manager

    
    // Define and instantiate Logger
    private Logger logger = Logger.getLogger(ROSTurtlebotKeyController.class.getName());  
    // set instance in constructor for message callback to reference.
    private static ROSTurtlebotKeyController instance=null;
    public ROSTurtlebotKeyController()
    {
        super();
        this.instance=this;
    }
    
    /** return reference to constructed ROSTurtlebotKeyController. */
    public static ROSTurtlebotKeyController getInstance()
    {
        return instance;
    }
    
    /** Initialize the ROCI TelnetServer Plugin. */
    public void initialize() 
    {
        logger.log(Level.INFO, "Initializing ROS Turtlebot Controller plugin.");
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
            logger.log(Level.INFO, "Testing ROS Turtlebot..");
            try 
            {
                // get the topic/service lookup Map
                //this.topicServiceLookupMap = rosNode.getSenderLookupMap();
                this.sender=rosNode.getPublisherSenders()[rosNode.getTopicIndex("/mobile_base/commands/velocity")];
                this.motor_state_sender=rosNode.getPublisherSenders()[rosNode.getTopicIndex("/mobile_base/commands/motor_power")];                
                ///cmd_vel_mux/input/navi
                ///mobile_base/commands/velocity
                ///cmd_vel_mux/input/safety_controller
                //
//                System.out.println("Turtle Sender Topic threadName: " + ((TCPROSPublisherCommunicator)sender).threadName);
 //       this.motor_power_handler = rosNode.getController().getInstructionManager().getProviderByName("instruction_manager").getInstructionDefinition("/mobile_base/commands/motor_power").getInstructionOutputHandler(); 

 //       this.motor_power_object_pool = rosNode.getController().getInstructionManager().getProviderByName("instruction_manager").getInstructionDefinition("/mobile_base/commands/motor_power").getInstructionObjectPool();                 
                // hook up the PS/3 controller.
                this.thread = new Thread(new Runnable() {
                public void run()
                {
                    try 
                    {
                        if(ROSTurtlebotKeyController.this.sender!=null)
                        {
      //                      System.out.println("calling startController on /turtle1/cmd_vel");
                            startController(ROSTurtlebotKeyController.this.sender);
                        }
                        else
                        {
                        Logger.getLogger(ROSTurtlebotKeyController.class.getName()).log(Level.INFO, "Turtlebot Controller could not send message due to the Publisher not being initialized.");
                        // TODO: Remove
                        startController(ROSTurtlebotKeyController.this.sender);
                        }
                    } catch (IOException ex) 
                    {
                        Logger.getLogger(ROSTurtlebotKeyController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
       });
                thread.start();
                
            } 
            catch (Exception ex) 
            {
                Logger.getLogger(ROSTurtlebotKeyController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
            // Set isInitialized to false.
            this.isInitialized=true;                    
 //           this.monitor = new ConnectionMonitor(tableData, tableColumnNames, rosNode.getCallerID(), rosNode.getSubscriberMessageManager(), rosNode, rosNode.getProperties(), rosNode.getPropertiesFilePath());                   
        }
        else if(!rosNode.isInitialized())
        {
            // ROS Plugin disabled
             logger.log(Level.WARNING, "The \"ros\" RCSM Provider is not enabled. To start the ROS Turtlebot controller, the \"ros\" RCSM Provider must be enabled.");       
            // Set isInitialized to false.
            this.isInitialized=false;                            
        }
        else
        {
            // Could not find ROS Log this.
            logger.log(Level.WARNING, "A \"ros\" RCSM Provider was not found. Turtlebot controller not started.");
            // Set isInitialized to false.
            this.isInitialized=false;                    
        }
      
        logger.log(Level.INFO, "ROCI ROS Turtlebot controller initialized");
    }
    
    private void motorOn() throws IOException
    {
  //       MotorPower instruction = (MotorPower)motor_power_object_pool.checkout();
  //       instruction.state=instruction.ON;
         //byte[] bytes = motor_power_handler.transform(instruction);
         //System.out.println("Motor On Instruction: "+BytesToHex.bytesToHex(bytes));
         motor_state_sender.send(HexToBytes.hexToBytes("0100000001"));
         //motor_power_handler.send(instruction);     
         System.out.println("Turtlebot Motor Power ON.");
    }
    
    private void motorOff() throws IOException
    {
//         MotorPower instruction = (MotorPower)motor_power_object_pool.checkout();
//         instruction.state=instruction.OFF;
         //byte[] bytes = motor_power_handler.transform(instruction);
         //System.out.println("Motor Off Instruction: "+BytesToHex.bytesToHex(bytes));         
         motor_state_sender.send(HexToBytes.hexToBytes("0100000000"));        
        // motor_power_handler.send(instruction);
         System.out.println("Turtlebot Motor Power OFF.");       
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
    
    // Clear is an empty message, therefore, this is a 4 byte length of 0 byte[].
    private final static byte[] CLEAR = {(byte)(0x00),(byte)(0x00),(byte)(0x00),(byte)(0x00)};
    public void startController(CommunicationSenderInterface sender) throws IOException
    {
        // Add JFrame for AWTKeyboard to work...
        JFrame frame = new JFrame("JFrame Source Demo");
        // Add a window listner for close button
        frame.addWindowListener(new WindowAdapter() {

                public void windowClosing(WindowEvent e) {
                        System.exit(0);
                }
        });
        // This is an empty content area in the frame
        JLabel jlbempty = new JLabel("");
        jlbempty.setPreferredSize(new Dimension(175, 100));
        frame.getContentPane().add(jlbempty, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
System.out.println("Turtlebot Key Controller Mappings:\nD=Motor Off/E=Motor On\nup arrow=move forward velocity/down arrow move=reverse velocity/right arrow=spin right/left arrow=spin left");
        while(true) 
        {
            this.controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
            if(controllers.length==0) 
            {
               System.out.println("Found no controllers.");
               return;
            }

            for(int i=0;i<controllers.length;i++) 
            {
               controllers[i].poll();
               this.queue = controllers[i].getEventQueue();
               this.event = new Event();
               while(queue.getNextEvent(event)) 
               {
                    this.component=event.getComponent();
                    if((this.cname = component.getName()).equals("Down"))
                    {  
                        sender.send(GAMEPAD_DOWN);
                        System.out.println("Down");
                    }                    
                    else if(cname.equals("Left"))
                    {
                        sender.send(GAMEPAD_LEFT);  
                    }     
                    else if(cname.equals("Up"))
                    {
                         sender.send(GAMEPAD_UP);                                                                 }     
                    else if(cname.equals("Right"))
                    {
                         sender.send(GAMEPAD_RIGHT);   
                    } // PS/3 button on down value is 1.0F, on release value is 0.0. Two events are triggered on PS3 button press.
                    else if(cname.equals("D"))                    
                    {
                        motorOff();
                    }
                    else if(cname.equals("E"))                    
                    {
                        motorOn();
                    }
                    else if(cname.equals("Return"))                    
                    {
                      /* try 
                       {
                            rosNode.sendServiceMessage(CLEAR, rosNode.getService("/clear"));  
                       } 
                       catch (RCSMException ex) 
                       {
                           Logger.getLogger(ROSTurtlebotKeyController.class.getName()).log(Level.SEVERE, null, ex);
                       }
                       */ 
                    }                      
                }
            }

            try 
            {
                // Pause 20ms before polling the Joystick for new events.
                Thread.sleep(20);
            } 
            catch (InterruptedException e) 
            {
               e.printStackTrace();
            }
         }

    }    
}
    class Movements2
    {
        public static byte[] getUpMovement()
        {
            byte[] gamepad_up;
            gamepad_up = new byte[52];
            gamepad_up[0] = (byte)(0x30);
            gamepad_up[1] = (byte)(0x00);
            gamepad_up[2] = (byte)(0x00);
            gamepad_up[3] = (byte)(0x00);                       
            // Append linear x movement event to byte[].
            gamepad_up[4] = (byte)(0x00);
            gamepad_up[5] = (byte)(0x00);
            gamepad_up[6] = (byte)(0x00);
            gamepad_up[7] = (byte)(0x00);
            gamepad_up[8] = (byte)(0x00);
            gamepad_up[9] = (byte)(0x00);
            gamepad_up[10] = (byte)(0xf0);
            gamepad_up[11] = (byte)(0x3f);        
            // Append linear y movement event to byte[].
            gamepad_up[12] = (byte)(0x00);
            gamepad_up[13] = (byte)(0x00);
            gamepad_up[14] = (byte)(0x00);
            gamepad_up[15] = (byte)(0x00);
            gamepad_up[16] = (byte)(0x00);
            gamepad_up[17] = (byte)(0x00);
            gamepad_up[18] = (byte)(0x00);
            gamepad_up[19] = (byte)(0x00);     
            // Append linear theta z to byte[]
            gamepad_up[20] = (byte)(0x00);
            gamepad_up[21] = (byte)(0x00);
            gamepad_up[22] = (byte)(0x00);
            gamepad_up[23] = (byte)(0x00);
            gamepad_up[24] = (byte)(0x00);
            gamepad_up[25] = (byte)(0x00);
            gamepad_up[26] = (byte)(0x00);
            gamepad_up[27] = (byte)(0x00);      

           // Append angular x movement event to byte[].
            gamepad_up[28] = (byte)(0x00);
            gamepad_up[29] = (byte)(0x00);
            gamepad_up[30] = (byte)(0x00);
            gamepad_up[31] = (byte)(0x00);
            gamepad_up[32] = (byte)(0x00);
            gamepad_up[33] = (byte)(0x00);
            gamepad_up[34] = (byte)(0x00);
            gamepad_up[35] = (byte)(0x00);                        
            // Append angular y movement event to byte[].
            gamepad_up[36] = (byte)(0x00);
            gamepad_up[37] = (byte)(0x00);
            gamepad_up[38] = (byte)(0x00);
            gamepad_up[39] = (byte)(0x00);
            gamepad_up[40] = (byte)(0x00);
            gamepad_up[41] = (byte)(0x00);
            gamepad_up[42] = (byte)(0x00);
            gamepad_up[43] = (byte)(0x00);     
            // Append angular theta z to byte[]
            gamepad_up[44] = (byte)(0x00);
            gamepad_up[45] = (byte)(0x00);
            gamepad_up[46] = (byte)(0x00);
            gamepad_up[47] = (byte)(0x00);
            gamepad_up[48] = (byte)(0x00);
            gamepad_up[49] = (byte)(0x00);
            gamepad_up[50] = (byte)(0xf0);
            gamepad_up[51] = (byte)(0x3f); 
            return gamepad_up;
        }
        
        public static byte[] getDownMovement()
        {     
            byte[] gamepad_down=new byte[52];
            gamepad_down[0] = (byte)(0x30);
            gamepad_down[1] = (byte)(0x00);
            gamepad_down[2] = (byte)(0x00);
            gamepad_down[3] = (byte)(0x00);                       
            // Append linear x movement event to byte[].
            gamepad_down[4] = (byte)(0x00);
            gamepad_down[5] = (byte)(0x00);
            gamepad_down[6] = (byte)(0x00);
            gamepad_down[7] = (byte)(0x00);
            gamepad_down[8] = (byte)(0x00);
            gamepad_down[9] = (byte)(0x00);
            gamepad_down[10] = (byte)(0xf0);
            gamepad_down[11] = (byte)(0x3f);        
            // Append linear y movement event to byte[].
            gamepad_down[12] = (byte)(0x00);
            gamepad_down[13] = (byte)(0x00);
            gamepad_down[14] = (byte)(0x00);
            gamepad_down[15] = (byte)(0x00);
            gamepad_down[16] = (byte)(0x00);
            gamepad_down[17] = (byte)(0x00);
            gamepad_down[18] = (byte)(0x00);
            gamepad_down[19] = (byte)(0x00);     
            // Append linear theta z to byte[]
            gamepad_down[20] = (byte)(0x00);
            gamepad_down[21] = (byte)(0x00);
            gamepad_down[22] = (byte)(0x00);
            gamepad_down[23] = (byte)(0x00);
            gamepad_down[24] = (byte)(0x00);
            gamepad_down[25] = (byte)(0x00);
            gamepad_down[26] = (byte)(0x00);
            gamepad_down[27] = (byte)(0x00);      

           // Append angular x movement event to byte[].
            gamepad_down[28] = (byte)(0x00);
            gamepad_down[29] = (byte)(0x00);
            gamepad_down[30] = (byte)(0x00);
            gamepad_down[31] = (byte)(0x00);
            gamepad_down[32] = (byte)(0x00);
            gamepad_down[33] = (byte)(0x00);
            gamepad_down[34] = (byte)(0x00);
            gamepad_down[35] = (byte)(0x00);                        
            // Append angular y movement event to byte[].
            gamepad_down[36] = (byte)(0x00);
            gamepad_down[37] = (byte)(0x00);
            gamepad_down[38] = (byte)(0x00);
            gamepad_down[39] = (byte)(0x00);
            gamepad_down[40] = (byte)(0x00);
            gamepad_down[41] = (byte)(0x00);
            gamepad_down[42] = (byte)(0x00);
            gamepad_down[43] = (byte)(0x00);     
            // Append angular theta z to byte[]
            gamepad_down[44] = (byte)(0x00);
            gamepad_down[45] = (byte)(0x00);
            gamepad_down[46] = (byte)(0x00);
            gamepad_down[47] = (byte)(0x00);
            gamepad_down[48] = (byte)(0x00);
            gamepad_down[49] = (byte)(0x00);
            gamepad_down[50] = (byte)(0xf0);
            gamepad_down[51] = (byte)(0x3f); 
            return gamepad_down;
        }        
        
        public static byte[] getLeftMovement()
        {     
            byte[] gamepad_left=new byte[52];
            gamepad_left[0] = (byte)(0x30);
            gamepad_left[1] = (byte)(0x00);
            gamepad_left[2] = (byte)(0x00);
            gamepad_left[3] = (byte)(0x00);                       
            // Append linear x movement event to byte[].
            gamepad_left[4] = (byte)(0x00);
            gamepad_left[5] = (byte)(0x00);
            gamepad_left[6] = (byte)(0x00);
            gamepad_left[7] = (byte)(0x00);
            gamepad_left[8] = (byte)(0x00);
            gamepad_left[9] = (byte)(0x00);
            gamepad_left[10] = (byte)(0x00);
            gamepad_left[11] = (byte)(0x00);        
            // Append linear y movement event to byte[].
            gamepad_left[12] = (byte)(0x00);
            gamepad_left[13] = (byte)(0x00);
            gamepad_left[14] = (byte)(0x00);
            gamepad_left[15] = (byte)(0x00);
            gamepad_left[16] = (byte)(0x00);
            gamepad_left[17] = (byte)(0x00);
            gamepad_left[18] = (byte)(0x00);
            gamepad_left[19] = (byte)(0x00);     
            // Append linear theta z to byte[]
            gamepad_left[20] = (byte)(0x00);
            gamepad_left[21] = (byte)(0x00);
            gamepad_left[22] = (byte)(0x00);
            gamepad_left[23] = (byte)(0x00);
            gamepad_left[24] = (byte)(0x00);
            gamepad_left[25] = (byte)(0x00);
            gamepad_left[26] = (byte)(0x00);
            gamepad_left[27] = (byte)(0x00);      

           // Append angular x movement event to byte[].
            gamepad_left[28] = (byte)(0x00);
            gamepad_left[29] = (byte)(0x00);
            gamepad_left[30] = (byte)(0x00);
            gamepad_left[31] = (byte)(0x00);
            gamepad_left[32] = (byte)(0x00);
            gamepad_left[33] = (byte)(0x00);
            gamepad_left[34] = (byte)(0x00);
            gamepad_left[35] = (byte)(0x00);                        
            // Append angular y movement event to byte[].
            gamepad_left[36] = (byte)(0x00);
            gamepad_left[37] = (byte)(0x00);
            gamepad_left[38] = (byte)(0x00);
            gamepad_left[39] = (byte)(0x00);
            gamepad_left[40] = (byte)(0x00);
            gamepad_left[41] = (byte)(0x00);
            gamepad_left[42] = (byte)(0x00);
            gamepad_left[43] = (byte)(0x00);     
            // Append angular theta z to byte[]
            gamepad_left[44] = (byte)(0x00);
            gamepad_left[45] = (byte)(0x00);
            gamepad_left[46] = (byte)(0x00);
            gamepad_left[47] = (byte)(0x00);
            gamepad_left[48] = (byte)(0x00);
            gamepad_left[49] = (byte)(0x00);
            gamepad_left[50] = (byte)(0x00);
            gamepad_left[51] = (byte)(0x40); 
            return gamepad_left;
        }        
        public static byte[] getRightMovement()
        {     
            byte[] gamepad_right=new byte[52];
            gamepad_right[0] = (byte)(0x30);
            gamepad_right[1] = (byte)(0x00);
            gamepad_right[2] = (byte)(0x00);
            gamepad_right[3] = (byte)(0x00);                       
            // Append linear x movement event to byte[].
            gamepad_right[4] = (byte)(0x00);
            gamepad_right[5] = (byte)(0x00);
            gamepad_right[6] = (byte)(0x00);
            gamepad_right[7] = (byte)(0x00);
            gamepad_right[8] = (byte)(0x00);
            gamepad_right[9] = (byte)(0x00);
            gamepad_right[10] = (byte)(0x00);
            gamepad_right[11] = (byte)(0x00);        
            // Append linear y movement event to byte[].
            gamepad_right[12] = (byte)(0x00);
            gamepad_right[13] = (byte)(0x00);
            gamepad_right[14] = (byte)(0x00);
            gamepad_right[15] = (byte)(0x00);
            gamepad_right[16] = (byte)(0x00);
            gamepad_right[17] = (byte)(0x00);
            gamepad_right[18] = (byte)(0x00);
            gamepad_right[19] = (byte)(0x00);     
            // Append linear theta z to byte[]
            gamepad_right[20] = (byte)(0x00);
            gamepad_right[21] = (byte)(0x00);
            gamepad_right[22] = (byte)(0x00);
            gamepad_right[23] = (byte)(0x00);
            gamepad_right[24] = (byte)(0x00);
            gamepad_right[25] = (byte)(0x00);
            gamepad_right[26] = (byte)(0x00);
            gamepad_right[27] = (byte)(0x00);      

           // Append angular x movement event to byte[].
            gamepad_right[28] = (byte)(0x00);
            gamepad_right[29] = (byte)(0x00);
            gamepad_right[30] = (byte)(0x00);
            gamepad_right[31] = (byte)(0x00);
            gamepad_right[32] = (byte)(0x00);
            gamepad_right[33] = (byte)(0x00);
            gamepad_right[34] = (byte)(0x00);
            gamepad_right[35] = (byte)(0x00);                        
            // Append angular y movement event to byte[].
            gamepad_right[36] = (byte)(0x00);
            gamepad_right[37] = (byte)(0x00);
            gamepad_right[38] = (byte)(0x00);
            gamepad_right[39] = (byte)(0x00);
            gamepad_right[40] = (byte)(0x00);
            gamepad_right[41] = (byte)(0x00);
            gamepad_right[42] = (byte)(0x00);
            gamepad_right[43] = (byte)(0x00);     
            // Append angular theta z to byte[]
            gamepad_right[44] = (byte)(0x00);
            gamepad_right[45] = (byte)(0x00);
            gamepad_right[46] = (byte)(0x00);
            gamepad_right[47] = (byte)(0x00);
            gamepad_right[48] = (byte)(0x00);
            gamepad_right[49] = (byte)(0x00);
            gamepad_right[50] = (byte)(0x00);
            gamepad_right[51] = (byte)(0xc0); 
            return gamepad_right;
        }             
    }