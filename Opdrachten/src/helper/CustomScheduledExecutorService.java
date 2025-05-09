package helper;

import java.util.concurrent.*;

/**
 * A custom implementation of ScheduledThreadPoolExecutor that provides
 * additional methods for safe execution of tasks. The safe methods ensure
 * that any uncaught exceptions are handled by the thread's UncaughtExceptionHandler.
 */
public class CustomScheduledExecutorService extends ScheduledThreadPoolExecutor {

	/**
	 * Constructs a CustomScheduledExecutorService with the given core pool size.
	 *
	 * @param corePoolSize the number of threads to keep in the pool, even if they are idle
	 */
	public CustomScheduledExecutorService(int corePoolSize) {
		super(corePoolSize);
	}

	/**
	 * Executes the given command at some time in the future. If the command
	 * throws an exception, it will be handled by the thread's UncaughtExceptionHandler.
	 *
	 * @param command the runnable task
	 */
	public void safeExecute(Runnable command) {
		super.execute(() -> {
			try {
				command.run();
			} catch (Throwable t) {
				handleException(t);
			}
		});
	}

	/**
	 * Creates and executes a one-shot action that becomes enabled after the given delay.
	 * If the command throws an exception, it will be handled by the thread's UncaughtExceptionHandler.
	 *
	 * @param command the task to execute
	 * @param delay the time from now to delay execution
	 * @param unit the time unit of the delay parameter
	 */
	public void safeSchedule(Runnable command, long delay, TimeUnit unit) {
		super.schedule(() -> {
			try {
				command.run();
			} catch (Throwable t) {
				handleException(t);
			}
		}, delay, unit);
	}

	/**
	 * Handles exceptions by delegating to the current thread's UncaughtExceptionHandler.
	 *
	 * @param t the exception to handle
	 */
	private void handleException(Throwable t) {
		Thread currentThread = Thread.currentThread();
		Thread.UncaughtExceptionHandler handler = currentThread.getUncaughtExceptionHandler();
		if (handler != null) {
			handler.uncaughtException(currentThread, t);
		}
	}
}