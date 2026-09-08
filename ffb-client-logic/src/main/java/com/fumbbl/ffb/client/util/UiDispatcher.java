package com.fumbbl.ffb.client.util;

import com.fumbbl.ffb.FantasyFootballException;

import javax.swing.SwingUtilities;
import java.lang.reflect.InvocationTargetException;

/**
 * Dispatches work to the AWT event dispatch thread.
 * <p>
 * Swing components must only be created and modified on the event dispatch thread. Background threads (the client
 * communication thread, icon download workers, timers) therefore have to hand their user interface work over to the
 * event dispatch thread. Never call {@link #runOnUiThread(Runnable)} while holding a lock that the event dispatch
 * thread may need, otherwise the application deadlocks.
 */
public class UiDispatcher {

	/**
	 * @return true if the calling thread is the event dispatch thread
	 */
	public boolean isUiThread() {
		return SwingUtilities.isEventDispatchThread();
	}

	/**
	 * Runs the given task on the event dispatch thread and waits for its completion. Runs the task directly when
	 * already on the event dispatch thread.
	 *
	 * @throws FantasyFootballException if the task throws an exception
	 */
	public void runOnUiThread(Runnable task) {
		if (isUiThread()) {
			task.run();
			return;
		}
		try {
			SwingUtilities.invokeAndWait(task);
		} catch (InterruptedException interrupted) {
			Thread.currentThread().interrupt();
		} catch (InvocationTargetException targetException) {
			throw new FantasyFootballException(targetException);
		}
	}

	/**
	 * Schedules the given task on the event dispatch thread without waiting for its completion. Runs the task
	 * directly when already on the event dispatch thread.
	 */
	public void runLaterOnUiThread(Runnable task) {
		if (isUiThread()) {
			task.run();
		} else {
			SwingUtilities.invokeLater(task);
		}
	}

	/**
	 * Waits until all user interface tasks scheduled before this call have been processed.
	 */
	public void awaitPendingUiTasks() {
		runOnUiThread(() -> {
		});
	}
}
