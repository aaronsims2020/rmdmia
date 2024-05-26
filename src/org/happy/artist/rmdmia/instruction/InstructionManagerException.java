package org.happy.artist.rmdmia.instruction;

/** An Exception Wrapper class for throwing Exceptions by InstructionManagerProvider 
 * Interface methods. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2014 Happy Artist. All rights reserved.
 * 
 */
public class InstructionManagerException extends java.lang.Exception
{
    /**
     *
     * @param e
     */
    public InstructionManagerException(Exception e)
    {
            super(e);
    }
    /**
     *
     * @param msg
     * @param e
     */
    public InstructionManagerException(String msg, Exception e)
    {
            super(msg,e);
    }
}