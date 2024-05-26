package org.happy.artist.rmdmia.rcsm.providers.ros.client.message;

import org.happy.artist.rmdmia.instruction.utility.RunnableInstructionBuilder;
import org.happy.artist.rmdmia.rcsm.RCSMProvider;
import org.happy.artist.rmdmia.rcsm.provider.message.AbstractInstructionDefinition;
import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.generator.ROSRunnableInstructionBuilder;

/**
 * ROSInstructionDefinition RCSMProvider InstructionDefinition implementation for "ros" RCSMProvider.
 *
 * @author Happy Artist
 *
 * @copyright Copyright ©2014-2015 Happy Artist. All rights reserved.
 *
 */
public class ROSInstructionDefinition extends AbstractInstructionDefinition
{
    private final static String[] msg_extensions = {"msg", "srv", "action"};
    private ROSRunnableInstructionBuilder builder;
    
    public ROSInstructionDefinition(RCSMProvider rcsm_provider)
    {
        super(rcsm_provider);                                                                        
    }    
    
    /**
     * Return all associated Schema File Extensions for the ROS Messages. msg,
     * and srv.
     */
    @Override
    public String[] getSchemaFileExtensions()
    {
        return msg_extensions;
    }

    /**
     * Return the RCSM Provider specific RunnableInstructionBuilder
     * implementation.
     *
     * @return RunnableInstructionBuilder
     */
    @Override
    public RunnableInstructionBuilder getRunnableInstructionBuilder()
    {
        if (builder == null)
        {
            this.builder = new ROSRunnableInstructionBuilder(getRCSMProvider().getController(), provider_name);
        }
        return builder;
    }
}
