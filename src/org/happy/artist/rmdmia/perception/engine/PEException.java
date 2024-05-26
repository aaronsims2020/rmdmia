package org.happy.artist.rmdmia.perception.engine;

import org.happy.artist.rmdmia.roci.*;

/** An Exception Wrapper class for throwing Exceptions by PEProvider 
 * Interface methods. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2014 Happy Artist. All rights reserved.
 * 
 */
public class PEException extends java.lang.Exception
{
    /**
     *
     * @param e
     */
    public PEException(Exception e)
    {
            super(e);
    }
    /**
     *
     * @param msg
     * @param e
     */
    public PEException(String msg, Exception e)
    {
            super(msg,e);
    }
}