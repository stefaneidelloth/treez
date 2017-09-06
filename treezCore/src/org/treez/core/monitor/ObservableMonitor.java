package org.treez.core.monitor;

public interface ObservableMonitor extends Monitor {

	void addPropertyChangedListener(Runnable listener);

	void addChildCreatedListener(ChildCreatedListener listener);

}
