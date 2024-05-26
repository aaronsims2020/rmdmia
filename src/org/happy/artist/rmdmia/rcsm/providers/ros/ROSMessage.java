package org.happy.artist.rmdmia.rcsm.providers.ros;

import org.happy.artist.rmdmia.perception.engine.sensor.SensorMessage;

/** ROSMessage.java - An abstract class TCPROS Message implementation of 
 * the SensorMessage interface.
 *  Implement this class for ROSTCP String type messages
 * 
 * @author Happy Artist
 * 
 * @copyright Copyright © 2013-2014 Happy Artist. All rights reserved.
 */
public abstract class ROSMessage extends SensorMessage 
{
    // ROS Message ID (used by ROSMessagePool for Object identification and indexing).
    public int rosMessageID = -1; 
    // isTopic is a helper variable for processing the ROSMessage Object data. Processing a 1-byte (single bit) boolean value is more efficient then checking for null value on String topic and service to determine if it is a topic or service.
    public boolean isTopic;
    // callerid: Name of node sending data. 
    public char[] callerid;
    // topic: name of the topic the subscriber is connecting to.
    public char[] topic;
    // service: name of service the client is calling
    public char[] service;    
    // md5sum: md5sum of the message type
    public char[] md5sum;
    // type: message type
    public char[] type;
    //message_definition: full text of message definition (output of gendeps --cat)
    public char[] message_definition;
    //error: human-readable error message if the connection is not successful
    public String error;
    //persistent: sent from a service client to a service. If '1', keep connection open for multiple requests.
    public char[] persistent;
    //tcp_nodelay: sent from subscriber to publisher. If '1', publisher will set TCP_NODELAY on socket if possible
    public char[] tcp_nodelay;
    //latching: publisher is in latching mode (i.e. sends the last value published to new subscribers)
    public char[] latching;
    // Service connection header attribute request_type.
    public char[] request_type;
    // Service connection header attribute response_type.
    public char[] response_type;  
    // binary connection header
    public byte[] connection_header;
    // binary body, used on non String data types.
    public byte[] binary_body;   
    
    /** Recycles the Object back to its starting values for reuse in a ROSMessagePool. Called automatically by ROSMessagePool during checkin. */
    public void recycle()
    {
        // Header information is reused by MessagePool, due to only initialization Object containing header information.
        this.binary_body=null;
        this.error=null;
        this.topic=null;
        this.service=null;
        this.md5sum=null;
        this.type=null;
        this.message_definition=null;
        this.connection_header=null;
    }
}
