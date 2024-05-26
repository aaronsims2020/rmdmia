package org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.tcpros.data;

/**
 * org.happy.artist.rmdmia.rcsm.client.transport.tcpros.data.TCPMessageFactory interface defines a basic TCPMessage. Each TCPMessage implementation requires a TCPMessageFactory to instantiate the TCPMessagePool. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2013 Happy Artist. All rights reserved.
 */
public interface TCPMessageFactory 
{
	public TCPMessage newTCPMessage(int messageID);
}