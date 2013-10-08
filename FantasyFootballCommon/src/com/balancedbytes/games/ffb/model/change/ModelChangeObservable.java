package com.balancedbytes.games.ffb.model.change;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author Kalimar
 */
public abstract class ModelChangeObservable {

  private transient Set<IModelChangeObserver> fObservers;
  
  public ModelChangeObservable() {
  	fObservers = new HashSet<IModelChangeObserver>();
  }
  
  public void addObserver(IModelChangeObserver pObserver) {
  	if (pObserver == null) {
  		return;
  	}
  	synchronized (fObservers) {
    	fObservers.add(pObserver);
    }
  }
  
  public boolean removeObserver(IModelChangeObserver pObserver) {
  	if (pObserver == null) {
  		return false;
  	}
  	synchronized (fObservers) {
  		return fObservers.remove(pObserver);
  	}
  }
  
  public IModelChangeObserver[] getObservers() {
  	synchronized (fObservers) {
  		return fObservers.toArray(new IModelChangeObserver[fObservers.size()]);
  	}
  }
  
  public int countObservers() {
  	synchronized (fObservers) {
  		return fObservers.size();
  	}
  }

  public void clearObservers() {
  	synchronized (fObservers) {
  		fObservers.clear();
  	}
  }
  
  public void notifyObservers(ModelChange pModelChange) {
  	if ((pModelChange == null) || (pModelChange.getChangeId() == null)) {
  		return;
  	}
  	synchronized (fObservers) {
    	for (IModelChangeObserver observer : fObservers) {
    		observer.update(pModelChange);
    	}
  	}
  }
  
}
