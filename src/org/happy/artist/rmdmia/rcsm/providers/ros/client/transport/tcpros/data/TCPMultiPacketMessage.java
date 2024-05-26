package org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.tcpros.data;

import java.util.Arrays;
import org.happy.artist.rmdmia.utilities.HexStringConverter;

/** TCPMultiPacketMessage.java is a cachable multi-packet message Object implementation for TCPROS Transport protocol. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2013 Happy Artist. All rights reserved.
 */
public class TCPMultiPacketMessage implements TCPMessage
{ 
    // TODO: add a variable here in place of 2 and store the Packet[] values of each topic in config file for updates via the Autotuner, to avoid mass Object initializations at initialization.
    // TODO: Make this number an updatable configuration property
    private int STARTUP_PACKET_ARRAY_LENGTH=2;
    // The array of Packets for this message    
    private Packet[] packets = new Packet[STARTUP_PACKET_ARRAY_LENGTH];
    // The complete message byte[], only returned on complete message.
    private byte[] messageBytes;
    // The message ID.
    public final int messageID;
    // the isComplete boolean specifies when a message is complete.
    public boolean isComplete=false;
    
    // The last received Packet block data length
    private int blockDataLength; 
    
    // iteration is used by the recycle, and shutdown methods.
    private int iteration=0;
    // The length of the message in packets.
    public int totalPackets;
    // Packet reference used in first and append methods.
    private Packet currentPacket;     
    // synchronously used by first, and append methods.
    private int blockNumber;
    // the block size variable
    private int blockSize=0;

//    private final static int ZERO=0;    
//    private final static int ONE=1;
    
    // Instatiate Array Packets
    public TCPMultiPacketMessage(int messageID)
    {
        // Set the default message id at initialization.
        this.messageID=messageID;
        //instantiate the array packets.
        for(int i=0;i<packets.length;i++)
        {
            packets[i] = new Packet();            
        }
    }
    
    /** Always call this method on the first packet of a multi-packet message. */
    public void first(byte[] packet, int blockSize, int blockDataLength)
    {
        this.blockDataLength=blockDataLength;
        this.totalPackets=1;
        // Set the blocksRemaining variable to non 0 value.
        this.isComplete=false;         
        this.blockNumber=0;
        // Set the block size
        this.blockSize=blockSize;
        
        // Assign Data to Packet
        this.currentPacket=packets[blockNumber];
        currentPacket.bytes=packet;
        currentPacket.blockID=blockNumber;
    }    
    
 // TODO: On true set the byte[] message length

    /** Always call this method after the first method. If dataLength of -1 it means end of message at end of packet byte[]. */
    public void append(byte[] packet, int blockDataLength)
    {       
        // store the current dataLength variable to Object scope.
        this.blockDataLength=blockDataLength;
        // increment the blockNumber
        this.blockNumber=(blockNumber + 1);
   
        // Determine if packet is null or 0 length.
        if(packet!=null && packet.length>0)
        {
            // increment the totalPackets.
            this.totalPackets=totalPackets + 1;     
            // If the Packet[] is to small for all the data make it larger, but not larger than the current packet count. Increment size by 1. Instantiate new Object for new array element.
            if(totalPackets>packets.length)
            {
                this.packets=Arrays.copyOf(packets, packets.length + 1);
                packets[blockNumber]=new Packet();
                this.currentPacket=packets[blockNumber];                
            }
            else
            {
                this.currentPacket=packets[blockNumber];   
            }
            // Append the append packet byte[] and new blockID to the current packet.    
            currentPacket.bytes=packet;
            currentPacket.blockID=blockNumber;
        }
        else
        {
            // Set message to complete if message data is null or 0 and do not append new packets. A message can be tossed or passed forward at this point.
            this.isComplete=true;
            this.blockDataLength=0;
        }
    // Check if this is last packet. If dataLength == -1 The packet length is block length. and isComplete==true. If dataLength is less than block length message is complete once data is appended.If last Packet messageBytes must be set.        
        if(blockDataLength==-1)
        {
            //
            this.isComplete=true;
            // Initialize the messageBytes array before done. 
            if(messageBytes==null)
            {
                // initialize messageBytes[]
                this.messageBytes=new byte[(totalPackets*blockSize)];
            }
            else if(((totalPackets*blockSize) + blockDataLength)!=messageBytes.length)
            {
                // resize messageBytes[]
                this.messageBytes=Arrays.copyOf(messageBytes,(totalPackets*blockSize));
            }  
        }
// TODO: Looks incorrectly calculated, may need to recalculate at later date, starting with reversing if to packet.length<blockDataLength
        else if(blockDataLength<packet.length)            
        {
            //
            this.isComplete=true;
            // Initialize the messageBytes array before done. 
            if(messageBytes==null)
            {
                // initialize messageBytes[]
                this.messageBytes=new byte[(totalPackets*blockSize) + blockDataLength];
            }
            else if(((totalPackets*blockSize) + blockDataLength)!=messageBytes.length)
            {
                // resize messageBytes[]
                this.messageBytes=Arrays.copyOf(messageBytes,(totalPackets*blockSize) + blockDataLength);
            }              
        }
// If the above is broken revert back to below code - still may need some modifications to make work.
//        else if(blockDataLength<packet.length)            
//        {
            //
//            this.isComplete=true;
            // Initialize the messageBytes array before done. 
//            if(messageBytes==null)
//            {
                // initialize messageBytes[]
//                this.messageBytes=new byte[(totalPackets*blockSize) + blockDataLength];
//            }
//            else if(((totalPackets*blockSize) + blockDataLength)!=messageBytes.length)
//            {
//                // resize messageBytes[]
//                this.messageBytes=Arrays.copyOf(messageBytes,(totalPackets*blockSize) + blockDataLength);
//            }              
//        }
    }
    
    /** returns the block ordered TCPROS Packet array. */ 
    public TCPMessage.Packet[] getPackets()
    {
        return packets;
    }
    
   private int packetCount;
   private int bytesOffset;
   private byte[] tempByteArray;    
    /** Return the message once all packets arrive (when append returns true, or blocksRemaining=0. This message should only be called once because it reprocesses multiple byte arrays every call. */
    public byte[] getMessage()
    {       
// TODO: getMessage 11/23/2013        
        this.packetCount=0;
        this.bytesOffset=0;
        while(packetCount<totalPackets)
        {
            this.tempByteArray=packets[packetCount].bytes;
//        System.out.println("getMessage Block " + packetCount + " Data: " + String.valueOf(HexStringConverter.bytesToHex(tempByteArray)));
            try
            {
//TODO:11/22/2013 Revert System ArrYCOPY LENGTH CODE                
            //    System.arraycopy(tempByteArray, 0, messageBytes, bytesOffset, tempByteArray.length);
 System.arraycopy(tempByteArray, 0, messageBytes, bytesOffset, tempByteArray.length);                
            }
            catch(IndexOutOfBoundsException e)
            {
//                System.out.println("tempByteArray: " + tempByteArray + "messageBytes: " + messageBytes);
//               System.out.println("tempByteArray.length: " + tempByteArray.length + "messageBytes.length: " + messageBytes.length + ", packet count iteration: " + packetCount + ", dataLength: " + dataLength + ", bytesOffset: " + bytesOffset);
                e.printStackTrace();
                System.out.println("IndexOutOfBoundsException tempByteArray: " + tempByteArray + "messageBytes: " + messageBytes);
                System.out.println("IndexOutOfBoundsException tempByteArray.length: " + tempByteArray.length + "messageBytes.length: " + messageBytes.length + ", packet count iteration: " + packetCount + ", dataLength: " + blockDataLength + ", bytesOffset: " + bytesOffset);                
            }
            this.bytesOffset = bytesOffset + tempByteArray.length;    
            this.packetCount=packetCount + 1;
        }
        return messageBytes;
    }
 
    public void recycle()
    {
//        System.out.println("Calling recycle on message id: " + messageID);
        // Set total packets to 0 to prepare for next usage.
        this.totalPackets=0;
//        this.messageBytes=null;
        this.isComplete=false;
        //this.blockDataLength=0;
        //this.blockSize=0;
        this.packetCount=0;
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
        if(messageBytes!=null)
        {
            Arrays.fill(messageBytes, (byte)0x00);
        }
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
