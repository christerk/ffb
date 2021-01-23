package com.balancedbytes.games.ffb.model.change;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Kalimar
 */
public abstract class ModelChangeObservable {

	private transient Set<IModelChangeObserver> fObservers;

	public ModelChangeObservable() {
		fObservers = Collections.synchronizedSet(new HashSet<>());
	}

	public boolean addObserver(IModelChangeObserver pObserver) {
		if (pObserver == null) {
			return false;
		}
		return fObservers.add(pObserver);
	}

	public boolean removeObserver(IModelChangeObserver pObserver) {
		if (pObserver == null) {
			return false;
		}
		return fObservers.remove(pObserver);
	}

	public IModelChangeObserver[] getObservers() {
		return fObservers.toArray(new IModelChangeObserver[fObservers.size()]);
	}

	public int countObservers() {
		return fObservers.size();
	}

	public void clearObservers() {
		fObservers.clear();
	}

	public void notifyObservers(ModelChange pModelChange) {
		if ((pModelChange == null) || (pModelChange.getChangeId() == null)) {
			return;
		}
		for (IModelChangeObserver observer : fObservers) {
			observer.update(pModelChange);
		}
	}

}
