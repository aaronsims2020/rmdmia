package org.happy.artist.rmdmia.mission.manager;

/** An Exception Wrapper class for throwing Exceptions by MissionManagerProvider 
 * Interface methods. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2014 Happy Artist. All rights reserved.
 * 
 */
public class MissionManagerException extends java.lang.Exception
{
    /**
     *
     * @param e
     */
    public MissionManagerException(Exception e)
    {
            super(e);
    }
    /**
     *
     * @param msg
     * @param e
     */
    public MissionManagerException(String msg, Exception e)
    {
            super(msg,e);
    }
}