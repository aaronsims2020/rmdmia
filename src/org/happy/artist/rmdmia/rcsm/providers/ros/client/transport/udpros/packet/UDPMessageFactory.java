package org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.udpros.packet;

/**
 * org.happy.artist.rmdmia.rcsm.client.transport.udpros.packet.UDPMessageFactory interface defines a basic UDPMessage. Each UDPMessage implementation requires a UDPMessageFactory to instantiate the UDPMessagePool. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2013 Happy Artist. All rights reserved.
 */
public interface UDPMessageFactory 
{
	public UDPMessage newUDPMessage(int messageID);
}