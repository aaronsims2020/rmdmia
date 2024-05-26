package org.happy.artist.rmdmia.gesture.manager;

/** An Exception Wrapper class for throwing Exceptions by GestureManagerProvider 
 * Interface methods. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2014 Happy Artist. All rights reserved.
 * 
 */
public class GestureManagerException extends java.lang.Exception
{
    /**
     *
     * @param e
     */
    public GestureManagerException(Exception e)
    {
            super(e);
    }
    /**
     *
     * @param msg
     * @param e
     */
    public GestureManagerException(String msg, Exception e)
    {
            super(msg,e);
    }
}