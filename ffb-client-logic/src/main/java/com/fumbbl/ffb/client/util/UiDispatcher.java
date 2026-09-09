package com.fumbbl.ffb.client.util;

import com.fumbbl.ffb.FantasyFootballException;

import javax.swing.SwingUtilities;
import java.lang.reflect.InvocationTargetException;

/**
 * Dispatches work to the AWT event dispatch thread.
 * <p>
 * Swing components must only be created and modified on the event dispatch thread. Background threads (the client
 * communication thread, icon download workers, timers) therefore have to hand their user interface work over to the
 * event dispatch thread.
 * <p>
 * The methods differ in two independent aspects, pick the one matching both requirements:
 * <ul>
 * <li>whether the caller is blocked until the task has finished: {@link #runOnUiThreadAndWait(Runnable)} blocks,
 * the other two return immediately</li>
 * <li>whether the task may run inline when the caller already is the event dispatch thread:
 * {@link #runOnUiThreadAndWait(Runnable)} and {@link #runOnUiThreadWithoutWaiting(Runnable)} run it inline,
 * {@link #runOnUiThreadAfterCurrentEvent(Runnable)} always queues it as a separate event</li>
 * </ul>
 * <p>
 * Never call {@link #runOnUiThreadAndWait(Runnable)} while holding a lock that the event dispatch thread may need,
 * otherwise the application deadlocks.
 */
public class UiDispatcher {

	/**
	 * @return true if the calling thread is the event dispatch thread
	 */
	public boolean isUiThread() {
		return SwingUtilities.isEventDispatchThread();
	}

	/**
	 * Runs the given task on the event dispatch thread and blocks the calling thread until the task has finished.
	 * Runs the task inline when the caller already is the event dispatch thread, so unlike
	 * {@link SwingUtilities#invokeAndWait(Runnable)} this is safe to call from anywhere.
	 * <p>
	 * Use this when the caller depends on the result or on the user interface state the task produces. Do not call it
	 * while holding a lock that the event dispatch thread may need, that deadlocks the application.
	 *
	 * @throws FantasyFootballException if the task throws an exception
	 */
	public void runOnUiThreadAndWait(Runnable task) {
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
	 * Runs the given task on the event dispatch thread without blocking the calling thread. Runs the task inline when
	 * the caller already is the event dispatch thread, which means the task may have finished by the time this method
	 * returns.
	 * <p>
	 * Use this for fire and forget user interface updates from background threads. When the task must not run before
	 * the currently processed event has been completed, for example because it reads document, focus or layout state
	 * that is still being modified, use {@link #runOnUiThreadAfterCurrentEvent(Runnable)} instead. The same applies
	 * when callers rely on tasks being coalesced, inline execution defeats any such throttling.
	 */
	public void runOnUiThreadWithoutWaiting(Runnable task) {
		if (isUiThread()) {
			task.run();
		} else {
			SwingUtilities.invokeLater(task);
		}
	}

	/**
	 * Queues the given task as a separate event on the event dispatch thread and returns immediately. The task never
	 * runs inline, even when the caller already is the event dispatch thread, so it always runs after the currently
	 * processed event has been completed.
	 * <p>
	 * Use this when the task depends on state that the current event is still changing, for example caret positions
	 * during a document update or focus after a popup has been shown. When inline execution is acceptable, prefer
	 * {@link #runOnUiThreadWithoutWaiting(Runnable)}, it avoids the extra event round trip.
	 */
	public void runOnUiThreadAfterCurrentEvent(Runnable task) {
		SwingUtilities.invokeLater(task);
	}

	/**
	 * Blocks the calling thread until all user interface tasks queued before this call have been processed. Returns
	 * immediately when the caller already is the event dispatch thread, since the current event is by definition the
	 * one being processed.
	 */
	public void awaitPendingUiTasks() {
		runOnUiThreadAndWait(() -> {
		});
	}
}
