package org.happy.artist.rmdmia.decision.manager;

/** An Exception Wrapper class for throwing Exceptions by DecisionManagerProvider 
 * Interface methods. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2014 Happy Artist. All rights reserved.
 * 
 */
public class DecisionManagerException extends java.lang.Exception
{
    /**
     *
     * @param e
     */
    public DecisionManagerException(Exception e)
    {
            super(e);
    }
    /**
     *
     * @param msg
     * @param e
     */
    public DecisionManagerException(String msg, Exception e)
    {
            super(msg,e);
    }
}