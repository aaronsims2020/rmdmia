/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.happy.artist.rmdmia.rcsm.providers.ros;

import org.happy.artist.rmdmia.perception.engine.sensor.SensorMessage;

/** DummyTCPROSSensorMessage.java - The Dummy implementation of the SensorMessage interface.
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2013 Happy Artist. All rights reserved.
 */
public class DummyTCPROSSensorMessage extends ROSMessage 
{
    // callerid: Name of node sending data. 
    public String callerid="/rostopic_4767_1316912741557";
    // topic: name of the topic the subscriber is connecting to.
    public String topic="/chatter";  
    // md5sum: md5sum of the message type
    public String md5sum="992ce8a1687cec8c8bd883ec73ca41d1";
    // type: message type
    public String type="std_msgs/String";
    //message_definition: full text of message definition (output of gendeps --cat)
    public String message_definition="string data";
    //latching: publisher is in latching mode (i.e. sends the last value published to new subscribers)
    public String latching="1";
    // on Text Data Type, use String text_body, otherwise, use binary_body.
    public String text_body="hello";
}
