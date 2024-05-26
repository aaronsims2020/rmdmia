package org.happy.artist.rmdmia.rcsm.provider.message;

/** The RCSM Provider Message Handler Controller Interface, is intended 
 * as a tool to change Message Handler Types on the fly from within a 
 * MessageHandler.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright© 2013-2014 Happy Artist. All rights reserved.
 */
public interface MessageHandlerControllerInterface 
{
    /** Switches to using the tuner message handler. */    
    public void setUseTunerMessageHandler();
    
    /** Switches to using the initializer message handler. */    
    public void setUseInitializerMessageHandler();
    
    /** Switches to using the base message handler. */
    public void setUseMessageHandler(); 

    /** Return the tuner message handler. */    
    public MessageHandlerInterface getUseTunerMessageHandler();
    
    /** Return the initializer message handler. */    
    public MessageHandlerInterface getUseInitializerMessageHandler();
    
    /** Return the base message handler. */
    public MessageHandlerInterface getUseMessageHandler();       
}
