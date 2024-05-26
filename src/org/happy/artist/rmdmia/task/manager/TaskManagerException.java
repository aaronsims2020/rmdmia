package org.happy.artist.rmdmia.task.manager;

/** An Exception Wrapper class for throwing Exceptions by TaskManagerProvider 
 * Interface methods. 
 *
 * @author Happy Artist
 * 
 * @copyright Copyright ©2014 Happy Artist. All rights reserved.
 * 
 */
public class TaskManagerException extends java.lang.Exception
{
    /**
     *
     * @param e
     */
    public TaskManagerException(Exception e)
    {
            super(e);
    }
    /**
     *
     * @param msg
     * @param e
     */
    public TaskManagerException(String msg, Exception e)
    {
            super(msg,e);
    }
}