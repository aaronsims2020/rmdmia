package org.happy.artist.rmdmia.perception.engine.sensor.utility;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.happy.artist.rmdmia.Controller;
import org.happy.artist.rmdmia.instruction.providers.InstructionManagerPlugin;
import org.happy.artist.rmdmia.perception.engine.sensor.SensorProcessorRegistry;
import org.happy.artist.rmdmia.rcsm.RCSMProvider;

/**
 * org.happy.artist.rmdmia.pe.sensor.utility.Utility class is a helper 
 * class for SensorProcessorManager related class. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2014-2015 Happy Artist. All rights reserved. 
 */
public class Utility 
{
    // Class Logger define & instantiation
    private final static Logger logger = Logger.getLogger(Utility.class.getName());    
    
    // Append initial values of all Service, and Topic Sensor Processors for ROSBag recording, and default Event Listener Sensor Processors.
    public static boolean generateDefaultSensorProcessorTopicsWithROSBag(Controller controller, SensorProcessorRegistry registry) throws UnsupportedEncodingException
    {
        // TODO: wait for each rcsm to be initialized, then query registry ids.
        int hids=0;
        RCSMProvider[] providers = controller.getRCSM().getProviders();
        InstructionManagerPlugin instruction_manager_plugin=((InstructionManagerPlugin)controller.getInstructionManager().getProviderByName("instruction_manager"));
        int provider_loop_count = 0;    
        int hid_loop_count=0;
        boolean is_write_registry=false;
        while(provider_loop_count<providers.length)
        {
            if(providers[provider_loop_count]!=null&&providers[provider_loop_count].isInitialized())
            {
                logger.log(Level.INFO, "Populating the default Perception Engine Sensor Processor Manager registry sensor_message_queue.eax.");
                hids = hids + providers[provider_loop_count].getSubscriberMessageManager().getTopicDefinitions().length;
    // TODO: generate base sensor_message_queue.eax values for each hid.
                while(hid_loop_count<hids)
                {
                    if(instruction_manager_plugin.getInstructionDefinition(hid_loop_count).getInstructionInputHandlerClassName()!=null&&instruction_manager_plugin.getInstructionDefinition(hid_loop_count).getInstructionInputHandlerClassName().isEmpty()==false)
                    {
                        registry.put(String.valueOf(hid_loop_count).concat(".0"),instruction_manager_plugin.getInstructionDefinition(hid_loop_count).getInstructionInputHandlerClassName().concat(",true"));                
                        registry.put(String.valueOf(hid_loop_count).concat(".1"),"org.happy.artist.rmdmia.rcsm.providers.ros.client.sensor.ROSBagSensorProcessorFactory,false");
                    }
                    else
                    {
                        registry.put(String.valueOf(hid_loop_count).concat(".0"),"org.happy.artist.rmdmia.rcsm.providers.ros.client.sensor.ROSBagSensorProcessorFactory,false");                    
                    }
                    hid_loop_count = hid_loop_count + 1;
                }      
                is_write_registry=true;
            }
            else if(providers[provider_loop_count]!=null&&!providers[provider_loop_count].isInitialized())
            {
                // RCSM Plugin disabled
                logger.log(Level.WARNING, "The \"" +providers[provider_loop_count].getName() +"\" RCSM Provider is not enabled. To start Perception engine Sensor Processor Support, the \"" +providers[provider_loop_count].getName() +"\" RCSM Provider must be enabled.");           
            }
            else
            {
                // Could not find RCSM Provider Log this.
                logger.log(Level.WARNING, "A \"" +providers[provider_loop_count].getName() +"\" RCSM Provider was not found.");
            }  
            // increment provider loop count by 1.
            provider_loop_count = provider_loop_count + 1;
        }
        if(is_write_registry)
        {
                return registry.writeFile();
        }
        else
        {
            return false;
        }
    }
}
