
package com.diff.core;

public class CancellableImpl implements Cancellable{

  private boolean cancelled = false;

  public void cancel() {
    cancelled = true;
  }

  public boolean isCancelled(){
    return cancelled;
  }
}
