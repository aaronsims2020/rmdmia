package org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.udpros.packet;

/**
 * org.happy.artist.rmdmia.rcsm.client.transport.udpros.packet.UDPSinglePacketMessageFactory implements UDPMessageFactory. Used to generate single packet UDP message Objects. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2013 Happy Artist. All rights reserved.
 */
public class UDPSinglePacketMessageFactory implements UDPMessageFactory
{

    @Override
    /** Create a new single packet UDP message. */
    public UDPMessage newUDPMessage(int messageID) 
    {
        return new UDPPacketMessage(messageID);
    }
    
}
