package com.fumbbl.ffb.client;

/**
 * 
 * @author Kalimar
 */
public interface IProgressListener {

	public void initProgress(int pMinimum, int pMaximum);

	public void updateProgress(int pProgress);

}
