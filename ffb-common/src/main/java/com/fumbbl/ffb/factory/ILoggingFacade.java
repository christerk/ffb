package com.fumbbl.ffb.factory;

public interface ILoggingFacade {
	void logError(long gameId, String message);

	void logDebug(long gameId, String message);

	void logWithOutGameId(Throwable throwable);
}
