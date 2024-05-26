package org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.udpros.packet;

import java.util.Arrays;

/** UDPMultiPacketMessage.java is a cachable multi-packet message Object implementation for UDPROS Transport protocol. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2013 Happy Artist. All rights reserved.
 */
public class UDPMultiPacketMessage implements UDPMessage
{
    // The array of Packets for this message
    private Packet[] packets;
    // The complete message byte[], only returned on complete message.
    private byte[] messageBytes;
    // The message ID.
    public final int messageID;
    // the number of blocks remaining until a message is completed.
    public int blocksRemaining;
    // The wait time on an incomplete message to timeout based on last received message packet. 
    private int messageTimeout;
    // iteration is used by the recycle, and shutdown methods.
    private int iteration=0;
    // The length of the message in packets.
    private int totalPackets;
    // Packet reference used in first and append methods.
    private Packet currentPacket;     

    public UDPMultiPacketMessage(int messageID)
    {
        this.messageID=messageID;
    }
    /** Always call this method on the first packet of a multi-packet message. */
    public void first(byte[] packet, int blockNumber, int expectedPackets)
    {
        this.totalPackets=expectedPackets;
        this.blocksRemaining=(expectedPackets-1);
        if(packets!=null)
        {
            if(packets.length==expectedPackets)
            {
//                System.out.println("Reusing Packet[], reusing Packet message id: " + messageID + ", for block#: " + blockNumber);
                // reuse existing Packets
                this.currentPacket=packets[blockNumber];
                currentPacket.bytes=packet;
                currentPacket.blockID=blockNumber;
            }
            else
            {
//                System.out.println("Resizing Packet[] from " + packets.length + " to " + expectedPackets + ".");
                // recreate the packet array to the new length
                this.packets=Arrays.copyOf(packets, expectedPackets);
                if((this.currentPacket=packets[blockNumber])!=null)
                {
//                    System.out.println("Reusing Packet[], reusing Packet message id: " + messageID + ", for block#: " + blockNumber);                    
                    currentPacket.bytes=packet;
                    currentPacket.blockID=blockNumber;
                }
                else
                {
//                System.out.println("Reusing Packet[], creating new Packet Object for message id: " + messageID + ", for block#: " + blockNumber);                         
                    packets[blockNumber]=new Packet();
                    currentPacket.bytes=packet;
                    currentPacket.blockID=blockNumber;                   
                }
            }
        }
        else
        {
//            System.out.println("Creating new Packet[], creating Packet message id: " + messageID + ", for block#: " + blockNumber);
            // create a new Packet[]
            this.packets = new Packet[expectedPackets];
            packets[blockNumber]=new Packet();
            this.currentPacket=packets[blockNumber];
            this.currentPacket.bytes=packet;
            this.currentPacket.blockID=blockNumber;            
        }
        // Initialize the messageBytes array before done. 
        if(messageBytes==null)
        {
            // initialize messageBytes[]
            this.messageBytes=new byte[expectedPackets*packet.length];
        }
        else if((expectedPackets*packet.length)!=messageBytes.length)
        {
            // resize messageBytes[]
            this.messageBytes=Arrays.copyOf(messageBytes,expectedPackets*packet.length);
        }
//        System.out.println("Done with call to first().");
    }    
    
    /** Always call this method after the first method. */
    public boolean append(byte[] packet, int blockNumber)
    {
        this.blocksRemaining=(blocksRemaining-1);
        //try
        //{
        if((this.currentPacket=packets[blockNumber])!=null)
        {
//            System.out.println("Calling append() on block number:" + blockNumber + ", reusing Packet for message id: " + messageID + ", blocksRemaining: " + blocksRemaining);                    
            currentPacket.bytes=packet;
            currentPacket.blockID=blockNumber;
        }
        else
        {
//            System.out.println("Calling append() on block number:" + blockNumber + ", Creating new Packet Object for message id: " + messageID + ", blocksRemaining: " + blocksRemaining);                       
            packets[blockNumber]=new Packet();
            packets[blockNumber].bytes=packet;
            packets[blockNumber].blockID=blockNumber;  
            //this.currentPacket=packets[blockNumber];
            //currentPacket.bytes=packet;
            //currentPacket.blockID=blockNumber;                   
        }
        //}
      //  catch(Exception e)
       // {
       //     e.printStackTrace();
       // }
        if(blocksRemaining==0)
        {
//            System.out.println("Done with call to append(). Return true.");
            return true;
        }
        else
        {
//            System.out.println("Done with call to append(). Return false.");            
            return false;
        }

    }
    
    /** returns the block ordered UDPROS Packet array. */ 
    public UDPMessage.Packet[] getPackets()
    {
        return packets;
    }

   private int packetCount;
   private int bytesOffset;
   private byte[] tempByteArray;    
    /** Return the message once all packets arrive (when append returns true, or blocksRemaining=0. This message should only be called once because it reprocesses multiple byte arrays every call. */
    public byte[] getMessage()
    {
        this.packetCount=0;
        this.bytesOffset=0;
        while(packetCount<packets.length)
        {
            this.tempByteArray=packets[packetCount].bytes;
            System.arraycopy(tempByteArray, 0, messageBytes, bytesOffset, tempByteArray.length);
            this.bytesOffset = bytesOffset + tempByteArray.length;    
            this.packetCount=packetCount + 1;
        }
        return messageBytes;
    }
 
    public void recycle()
    {
//        System.out.println("Calling recycle on message id: " + messageID);
        if(packets==null)
        {
            return;
        }
        while(iteration<packets.length)
        {
            packets[iteration].recycle();
            // loop count iteration
            this.iteration = iteration + 1;
        }
        this.iteration=0;
        // fill the message byte[] with default values.
        Arrays.fill(messageBytes, (byte)0x00);
    }
    
    /** Not Thread safe, so make sure that all calls to recycle finish before a call to shutdown. */
    public void shutdown()
    {
        if(packets==null)
        {
            return;
        }
        while(iteration<packets.length)
        {
            packets[iteration]=null;
            // loop count iteration
            this.iteration = iteration + 1;
        }
        this.packets=null;
        this.iteration=0;
    }
    
}
