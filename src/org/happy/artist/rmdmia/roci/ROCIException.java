package org.happy.artist.rmdmia.roci;

/** An Exception Wrapper class for throwing Exceptions by ROCIProvider 
 * Interface methods. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2013 Happy Artist. All rights reserved.
 * 
 */
public class ROCIException extends java.lang.Exception
{
    /**
     *
     * @param e
     */
    public ROCIException(Exception e)
    {
            super(e);
    }
    /**
     *
     * @param msg
     * @param e
     */
    public ROCIException(String msg, Exception e)
    {
            super(msg,e);
    }
}