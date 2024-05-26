package org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.udpros.packet;

/** UDPPacketMessage.java is a single packet message Object implementation for UDPROS Transport protocol. 
 *  This entire class is overkill for a single packet message, however, implementing for sake of usability. Personal implementation will be byte[] passing on single packet message.
 * 
 * @author Happy Artist
 * 
 * @copyright Copyright © 2013 Happy Artist. All rights reserved.
 */
public class UDPPacketMessage implements UDPMessage
{
    // The complete message byte[], only returned on complete message.
    public final int messageID;
    private byte[] messageBytes;
    private final Packet[] packets = new Packet[1];
    private Packet currentPacket;

    public UDPPacketMessage(int messageID)
    {
        this.messageID=messageID;
        this.currentPacket=new Packet();
        this.packets[0]=currentPacket;
    }
    
    /** set the packet byte[] into the message. */
    public void set(byte[] packet)
    {
        this.messageBytes=packet;
        this.currentPacket.bytes=packet;
        System.out.println("Done with call to set().");
    }      
    
    @Override
    public Packet[] getPackets() 
    {
        return packets;
    }

    @Override
    public byte[] getMessage() 
    {
        return messageBytes;
    }

    @Override
    public void recycle() 
    {
       
    }

    @Override
    public void shutdown() 
    {
        this.messageBytes=null;
        currentPacket=null;
    }
    
}
