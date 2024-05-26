package org.happy.artist.rmdmia.rcsm.providers.ros.client.transport.tcpros.data;

import java.util.Arrays;
import org.happy.artist.rmdmia.utilities.HexStringConverter;

/** TCPMultiPacketMessage_original.java is a cachable multi-packet message Object implementation for TCPROS Transport protocol. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2013 Happy Artist. All rights reserved.
 */
public class TCPMultiPacketMessage_original implements TCPMessage
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
    // the number of blocks remaining until a message is completed.
    public int blocksRemaining;
    // The wait time on an incomplete message to timeout based on last received message packet. 
    
    private int messageTimeout;
    // iteration is used by the recycle, and shutdown methods.
    private int iteration=0;
    // The length of the message in packets.
    public int totalPackets;
    // Packet reference used in first and append methods.
    private Packet currentPacket;     
    // synchronously used by first, and append methods.
    private int blockNumber;

    private final static int ZERO=0;    
    private final static int ONE=1;
    
    public TCPMultiPacketMessage_original(int messageID)
    {
        this.messageID=messageID;
        //instantiate the array packets.
        for(int i=0;i<packets.length;i++)
        {
            packets[i] = new Packet();            
        }
    }
    
    // TODO: Implement dataLength algorithm. Modify totalLength to represent the current totalLength, as opposed to a precalculated length.
    /** Always call this method on the first packet of a multi-packet message. */
    public void first(byte[] packet, double blockSize, double messageSize, int dataLength)
    {
        this.dataLength=dataLength;
// TODO AARON 11/20/2013: Get the message size from more reliable source, it is possible the message size could be corrupted, therefore use incoming byte array measurement instead of  data for length.        
// TODO: Get the message size from the first four bytes in the data, then divide by block size to determine the total packets.

 //       this.totalPackets=(int)Math.ceil(messageSize/blockSize);
 this.totalPackets=ONE;
        
        //       System.out.println("first - block size:" + blockSize + ", messageSize:" + messageSize + ", totalPackets:" + totalPackets);
        // Set the blocksRemaining variable to non 0 value.
        this.blocksRemaining=STARTUP_PACKET_ARRAY_LENGTH;         
        // TODO: 
        //this.blockNumber=(totalPackets-blocksRemaining)-1;
        this.blockNumber=ZERO;
/*        if(packets!=null)
        {*/
            /*if(packets.length==totalPackets)
            {*/
//                System.out.println("TCPROS Reusing Packet[], reusing Packet message id: " + messageID + ", for block#: " + blockNumber);
                // reuse existing Packets
                this.currentPacket=packets[blockNumber];
                currentPacket.bytes=packet;
                currentPacket.blockID=blockNumber;
            /*}
            else
            {
//                System.out.println("TCPROS Resizing Packet[] from " + packets.length + " to " + totalPackets + ".");
                // recreate the packet array to the new length
               // this.packets=Arrays.copyOf(packets, totalPackets);
                if((this.currentPacket=packets[blockNumber])!=null)
                {
//                    System.out.println("TCPROS Reusing Packet[], reusing Packet message id: " + messageID + ", for block#: " + blockNumber);                    
                    currentPacket.bytes=packet;
                    currentPacket.blockID=blockNumber;
                }
                else
                {
//                System.out.println("TCPRPS Reusing Packet[], creating new Packet Object for message id: " + messageID + ", for block#: " + blockNumber);                                    this.currentPacket=new Packet();
                    packets[blockNumber]=currentPacket;
                    currentPacket.bytes=packet;
                    currentPacket.blockID=blockNumber;                   
                }
            }
            */
        /*}
        else
        {
//            System.out.println("TCPROS Creating new Packet[], creating Packet message id: " + messageID + ", for block#: " + blockNumber);
            // create a new Packet[]
            this.packets = new Packet[totalPackets];
            this.currentPacket=new Packet();
            packets[blockNumber]=currentPacket;
            currentPacket.bytes=packet;
            currentPacket.blockID=blockNumber;            
        }
        * */
                /*
        // Initialize the messageBytes array before done. 
        if(messageBytes==null)
        {
            System.out.println("EXCEPTION: Packet Length * totalPackets=" + (totalPackets*packet.length));    
            this.messageBytes=new byte[totalPackets*packet.length];
        }
        else if((totalPackets*packet.length)!=messageBytes.length)
        {
            // resize messageBytes[]
            this.messageBytes=Arrays.copyOf(messageBytes,totalPackets*packet.length);
        }*/
    }    
    
 // TODO: On true set the byte[] message length
 private int messageByteLength=0;  
 private int dataLength;
 // TODO: Implement dataLength algorithm.   
    /** Always call this method after the first method. If dataLength of -1 it means end of message at end of packet byte[]. */
    public boolean append(byte[] packet, int dataLength)
    {       
        this.dataLength=dataLength;
        //this.blocksRemaining=(blocksRemaining-1);
        this.blockNumber=(blockNumber + ONE);
   
        if(dataLength==-1||dataLength<packet.length)
        {
            this.blocksRemaining=ZERO;
            // Initialize the messageBytes array before done. 
            if(messageBytes==null)
            {
                // initialize messageBytes[]
                this.messageBytes=new byte[(totalPackets*packet.length) + dataLength];
            }
            else if(((totalPackets*packet.length) + dataLength)!=messageBytes.length)
            {
                // resize messageBytes[]
                this.messageBytes=Arrays.copyOf(messageBytes,(totalPackets*packet.length) + dataLength);
            }                 
        }
        // increment the totalPackets.
        this.totalPackets=totalPackets + ONE;     
        // Increment the Packets array length if needed.
        if(totalPackets>packets.length)
        {
            this.packets=Arrays.copyOf(packets, packets.length + ONE);
        }

        if((this.currentPacket=packets[blockNumber])!=null)
        {
            currentPacket.bytes=packet;
            currentPacket.blockID=blockNumber;
        }
        else
        {  
            this.currentPacket=new Packet();
            packets[blockNumber]=currentPacket;
            currentPacket.bytes=packet;
            currentPacket.blockID=blockNumber;   
//            System.out.println("Block " + blockNumber + " Data: " + String.valueOf(HexStringConverter.bytesToHex(packet)));
        }
        
 //TODO: AARON 11/21/2013 - blocksRemaining needs to be implemented using the message data now that dataLength check is added. Message Data of same length as block needs to be verified by parsing message to determine if message is encapsulated in 1024 byte package or it is multiple bytes.        
        //if(blocksRemaining==0)
//        this.blocksRemaining=0;////////////////
        if(dataLength<packet.length||blocksRemaining==0)
        {
//            System.out.println("Done with call to append() in TCPDataProcessor. Return true.");
 //TODO: AARON 11/21/2013 - Implement messageByteLength in getMessage() if needed.            
            this.messageByteLength=(packet.length*blockNumber) + dataLength;

            return true;
        }
        else
        {
//            System.out.println("Done with call to append() in TCPDataProcessor. Return false.");            
            return false;
        }
        
       
//,TODO: Initialize messageBytes Array before done based on total length of message.         
        // Initialize the messageBytes array before done. 
        /*if(messageBytes==null)
        {
            System.out.println("EXCEPTION: Packet Length * totalPackets=" + (totalPackets*packet.length));    
            this.messageBytes=new byte[totalPackets*packet.length];
        }
        else if((totalPackets*packet.length)!=messageBytes.length)
        {
            // resize messageBytes[]
            this.messageBytes=Arrays.copyOf(messageBytes,totalPackets*packet.length);
        }
        * */
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
                System.out.println("tempByteArray: " + tempByteArray + "messageBytes: " + messageBytes);
                System.out.println("tempByteArray.length: " + tempByteArray.length + "messageBytes.length: " + messageBytes.length + ", packet count iteration: " + packetCount + ", dataLength: " + dataLength + ", bytesOffset: " + bytesOffset);                
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
