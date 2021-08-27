package com.fumbbl.ffb.client;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class GameTitle {

	private static final long _SECONDS = 1000; // milliseconds
	private static final long _MINUTES = 60 * _SECONDS;
	private static final long _HOURS = 60 * _MINUTES;
	private static final long _DAYS = 24 * _HOURS;

	private ClientMode fClientMode;
	private Boolean fTesting;
	private String fHomeCoach;
	private String fAwayCoach;
	private long fPingTime;
	private long fTurnTime;
	private long fGameTime;

	public GameTitle() {
		setPingTime(-1);
		setTurnTime(-1);
		setGameTime(-1);
	}

	public void update(GameTitle gameTitle) {
		if (gameTitle != null) {
			if (gameTitle.getClientMode() != null) {
				setClientMode(gameTitle.getClientMode());
			}
			if (gameTitle.getTesting() != null) {
				setTesting(gameTitle.isTesting());
			}
			if (StringTool.isProvided(gameTitle.getHomeCoach())) {
				setHomeCoach(gameTitle.getHomeCoach());
			}
			if (StringTool.isProvided(gameTitle.getAwayCoach())) {
				setAwayCoach(gameTitle.getAwayCoach());
			}
			if (gameTitle.getPingTime() >= 0) {
				setPingTime(gameTitle.getPingTime());
			}
			if (gameTitle.getGameTime() >= 0) {
				setGameTime(gameTitle.getGameTime());
			}
			if (gameTitle.getTurnTime() >= 0) {
				setTurnTime(gameTitle.getTurnTime());
			}
		}
	}

	public void setTesting(boolean pTesting) {
		fTesting = pTesting;
	}

	public boolean isTesting() {
		return (fTesting != null) ? fTesting : false;
	}

	private Boolean getTesting() {
		return fTesting;
	}

	public ClientMode getClientMode() {
		return fClientMode;
	}

	public void setClientMode(ClientMode pClientMode) {
		fClientMode = pClientMode;
	}

	public String getHomeCoach() {
		return fHomeCoach;
	}

	public void setHomeCoach(String pHomeCoach) {
		fHomeCoach = pHomeCoach;
	}

	public String getAwayCoach() {
		return fAwayCoach;
	}

	public void setAwayCoach(String pAwayCoach) {
		fAwayCoach = pAwayCoach;
	}

	public long getPingTime() {
		return fPingTime;
	}

	public void setPingTime(long pPingTime) {
		fPingTime = pPingTime;
	}

	public long getTurnTime() {
		return fTurnTime;
	}

	public void setTurnTime(long pTurnTime) {
		fTurnTime = pTurnTime;
	}

	public long getGameTime() {
		return fGameTime;
	}

	public void setGameTime(long pGameTime) {
		fGameTime = pGameTime;
	}

	public String toString() {
		StringBuilder title = new StringBuilder();
		title.append("FantasyFootball");
		if (StringTool.isProvided(getHomeCoach()) && StringTool.isProvided(getAwayCoach())) {
			if (isTesting()) {
				title.append(" test ");
			} else {
				if (ClientMode.PLAYER == getClientMode()) {
					title.append(" - ");
				}
				if (ClientMode.SPECTATOR == getClientMode()) {
					title.append(" spectate ");
				}
				if (ClientMode.REPLAY == getClientMode()) {
					title.append(" replay ");
				}
			}
			title.append(getHomeCoach()).append(" vs ").append(getAwayCoach());
		}
		if ((ClientMode.REPLAY != getClientMode()) && (getTurnTime() >= 0)) {
			title.append(" - Turn ");
			appendTime(title, getTurnTime(), false);
		}
		if (getGameTime() >= 0) {
			title.append(" - Game ");
			appendTime(title, getGameTime(), true);
		}
		if (getPingTime() >= 0) {
			title.append(" - Ping ");
			appendPing(title);
		}
		return title.toString();
	}

	private void appendTime(StringBuilder builder, long milliseconds, boolean showHours) {

		long myMilliseconds = (milliseconds > 0) ? milliseconds : 0;

		int days = 0;
		if (myMilliseconds >= _DAYS) {
			days = (int) (myMilliseconds / _DAYS);
			myMilliseconds -= days * _DAYS;
			builder.append(days).append("d");
		}

		int hours = 0;
		if (showHours || (days > 0) || (myMilliseconds >= _HOURS)) {
			hours = (int) (myMilliseconds / _HOURS);
			myMilliseconds -= hours * _HOURS;
			appendMin2Digits(builder, hours).append("h");
		}

		int minutes = (int) (myMilliseconds / _MINUTES);
		myMilliseconds -= minutes * _MINUTES;
		appendMin2Digits(builder, minutes).append("m");

		int seconds = (int) (myMilliseconds / _SECONDS);
		appendMin2Digits(builder, seconds).append("s");

	}

	private void appendPing(StringBuilder builder) {
		builder.append(StringTool.formatThousands(getPingTime())).append("ms");
	}

	private StringBuilder appendMin2Digits(StringBuilder pBuffer, int pValue) {
		if (pValue < 10) {
			pBuffer.append("0");
		}
		pBuffer.append(pValue);
		return pBuffer;
	}

}
