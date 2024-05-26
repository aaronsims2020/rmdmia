package org.happy.artist.rmdmia.rcsm.providers.ros;

/** ROSXMLRPCException.java - A Java based Exception intended to be thrown if ROS XMLRPC Calls return error on response. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright © 2013 Happy Artist. All rights reserved.
 */
public class ROSXMLRPCException extends Exception 
{
    public ROSXMLRPCException()
    {
        super();
    }

    public ROSXMLRPCException(String message)
    {
        super(message);
    }

    public ROSXMLRPCException(Throwable cause)
    {
        super(cause);
    }

    public ROSXMLRPCException(String message, Throwable cause)
    {
        super(message, cause);
    }
}   
