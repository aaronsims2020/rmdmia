package org.happy.artist.rmdmia.rcsm;

/** An Exception Wrapper class for throwing Exceptions by RCSMProvider 
 * Interface methods. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2013 Happy Artist. All rights reserved.
 * 
 */
public class RCSMException extends java.lang.Exception
{
    /**
     *
     * @param e
     */
    public RCSMException(Exception e)
    {
            super(e);
    }
    /**
     *
     * @param msg
     * @param e
     */
    public RCSMException(String msg, Exception e)
    {
            super(msg,e);
    }
}