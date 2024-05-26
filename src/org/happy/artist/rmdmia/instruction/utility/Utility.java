package org.happy.artist.rmdmia.instruction.utility;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.happy.artist.rmdmia.Controller;
import org.happy.artist.rmdmia.instruction.InstructionManagerHardwareRegistry;
import org.happy.artist.rmdmia.rcsm.RCSMProvider;
import org.happy.artist.rmdmia.rcsm.provider.message.SubscriberMessageManager;
import org.happy.artist.rmdmia.rcsm.provider.message.TopicRegistryMessageDefinition;

/**
 * org.happy.artist.rmdmia.instruction.utility.Utility class is a helper 
 * class for InstructionManagerPlugin to populate the default configuration using the 
 * ros RCSMProvider. Note: In a future iteration hardware ids will need to be assigned 
 * differently. The current implementation has some ROS Plugin specific code for 
 * proof of concept. The solution will be to cycle each new hid by calling add at startup
 * on each new rcsm connection, or topic type, and to register hid on return int.
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
    public static boolean generateDefaultInstructionManagerTopicsWithRegisteredRCSMProviders(Controller controller, InstructionManagerHardwareRegistry registry) throws UnsupportedEncodingException
    {
        // TODO: wait for ros rcsm to be initialized, then query registry ids.
        int hids;
        RCSMProvider[] providers = controller.getRCSM().getProviders();
        SubscriberMessageManager smm;
        int provider_loop_count=0;
        while(provider_loop_count<providers.length)
        {
            if(providers[provider_loop_count]!=null&&providers[provider_loop_count].isInitialized())
            {
                logger.log(Level.INFO, "Populating the default Instruction Manager registry instruction_manager.properties.");
                smm=providers[provider_loop_count].getSubscriberMessageManager();
                TopicRegistryMessageDefinition[] trmds = smm.getTopicDefinitions();

                hids = trmds.length;
    // TODO: generate base instruction_manager.properties values for each hid.
                for(int i=0;i<hids;i++)
                {
                    //0=name
                    if(trmds[i].getTopicName()!=null&&!trmds[i].getTopicName().isEmpty())
                    {
                        // Add topic name
                        registry.put(String.valueOf(i).concat(".0"),trmds[i].getTopicName());                                             }
                    else
                    {
                        // populate an empty string for the topic name,
                        registry.put(String.valueOf(i).concat(".0"),"");                                                              }
                    // TODO: add description support.
                    //1=description
                    // Add description
                    registry.put(String.valueOf(i).concat(".1"),"");  
                    //2=instruction class
                    registry.put(String.valueOf(i).concat(".2"),"");                  
                    //3=input handler class
                    registry.put(String.valueOf(i).concat(".3"),"");  
                    //4=output handler class
                    registry.put(String.valueOf(i).concat(".4"),"");  
                    //5=rcsm provider name
                    registry.put(String.valueOf(i).concat(".5"),providers[provider_loop_count].getName());
                    //6=schema is fixed size (boolean). Default to false. This probably should be removed later on but is useful in ROS implementation. This will likely be a proprietary field, but was best place in ROS implementation to use in prototype, due to time constraints. Implementation of non-ros rcsm client will be more telling if this is a useful field
                    registry.put(String.valueOf(i).concat(".6"),"false");
                    //7=message_definition hex code
                    registry.put(String.valueOf(i).concat(".7"),"");
                    //8=instruction object pool class
                    registry.put(String.valueOf(i).concat(".8"),"");         
                    //9=Schema message bytes length (the message length in bytes, if variable then pick average message length, defaults to 1024)
                    registry.put(String.valueOf(i).concat(".9"),"");                   
                    //10=rcsm provider version
                    registry.put(String.valueOf(i).concat(".10"),String.valueOf(providers[provider_loop_count].getVersion()));
                }      
                return registry.writeFile();

            }
            else if(providers[provider_loop_count]!=null&&!providers[provider_loop_count].isInitialized())
            {
                // RCSM Provider disabled
                logger.log(Level.WARNING, "The \""+providers[provider_loop_count].getName()+"\" RCSM Provider is not enabled. To start the InstructionManager supplemental Support, the \""+providers[provider_loop_count].getName()+"\" RCSM Provider must be enabled.");           
            }
            else
            {
                // Could not find RCSM Provider Log this.
                logger.log(Level.WARNING, "A \""+providers[provider_loop_count].getName()+"\" RCSM Provider was not found.");
            }
            provider_loop_count=provider_loop_count + 1;
        }
        return false;
    }
}
