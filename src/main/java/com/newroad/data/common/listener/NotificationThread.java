package com.newroad.data.common.listener;

import java.util.ArrayList;
import java.util.Collections;


public abstract class NotificationThread implements Runnable {


  /**
   * 
   * An abstract function that children must implement. This function is where
   * 
   * all work - typically placed in the run of runnable - should be placed.
   */

  public abstract void doWork();



  /**
   * 
   * Our list of listeners to be notified upon thread completion.
   */
  private java.util.List<TaskListener> listeners = Collections.synchronizedList(new ArrayList<TaskListener>());



  /**
   * 
   * Adds a listener to this object.
   * 
   * @param listener Adds a new listener to this object.
   */

  public void addListener(TaskListener listener) {
    listeners.add(listener);
  }

  /**
   * 
   * Removes a particular listener from this object, or does nothing if the listener
   * 
   * is not registered.
   * 
   * @param listener The listener to remove.
   */

  public void removeListener(TaskListener listener) {
    listeners.remove(listener);
  }

  /**
   * 
   * Notifies all listeners that the thread has completed.
   */

  private final void notifyListeners() {
    synchronized (listeners) {
      for (TaskListener listener : listeners) {
        listener.threadComplete(this);
      }
    }
  }

  /**
   * 
   * Implementation of the Runnable interface. This function first calls doRun(), then
   * 
   * notifies all listeners of completion.
   */

  public void run() {
    doWork();
    notifyListeners();
  }

}
