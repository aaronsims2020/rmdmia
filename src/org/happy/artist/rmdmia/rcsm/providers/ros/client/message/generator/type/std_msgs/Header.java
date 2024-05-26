package org.happy.artist.rmdmia.rcsm.providers.ros.client.message.generator.type.std_msgs;

import org.happy.artist.rmdmia.rcsm.providers.ros.client.message.generator.type.Time;

/**
 * ROS provides the special Header type to provide a general mechanism 
 * for setting frame IDs for libraries like tf. While Header is not a built-in 
 * type (it's defined in std_msgs/msg/Header.msg), it is commonly used and has 
 * special semantics. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2014 Happy Artist. All rights reserved.
 */
public class Header 
{
    /** Standard metadata for higher-level flow data types
    #sequence ID: consecutively increasing ID */
    public long seq;
    /** Two-integer timestamp that is expressed as:
     * stamp.secs: seconds (stamp_secs) since epoch
     * stamp.nsecs: nanoseconds since stamp_secs
     * time-handling sugar is provided by the client library
     */ 
    public Time stamp = new Time();
    /** Frame this data is associated with */
    public String frame_id;    
}
