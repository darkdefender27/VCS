
package com.diff.core;

public interface Cancellable {

  public void cancel();
  public boolean isCancelled();
}
