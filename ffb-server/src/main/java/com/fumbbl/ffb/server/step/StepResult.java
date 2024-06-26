package com.fumbbl.ffb.server.step;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Animation;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.ReportList;
import com.fumbbl.ffb.server.IServerJsonOption;

/**
 * 
 * @author Kalimar
 */
public class StepResult implements IJsonSerializable {

	private StepAction fNextAction;
	private String fNextActionParameter;
	private ReportList fReportList;
	private Animation fAnimation;
	private SoundId fSound;
	private boolean fSynchronize;

	public StepResult() {
		setNextAction(StepAction.CONTINUE);
		setSynchronize(true);
		reset();
	}

	public void reset() {
		setReportList(new ReportList());
		setAnimation(null);
		setSound(null);
	}

	public void addReport(IReport pReport) {
		if (pReport != null) {
			fReportList.add(pReport);
		}
	}

	private void setReportList(ReportList pReportList) {
		fReportList = pReportList;
	}

	public ReportList getReportList() {
		return fReportList;
	}

	public Animation getAnimation() {
		return fAnimation;
	}

	public void setAnimation(Animation pAnimation) {
		fAnimation = pAnimation;
	}

	public SoundId getSound() {
		return fSound;
	}

	public void setSound(SoundId pSound) {
		fSound = pSound;
	}

	public StepAction getNextAction() {
		return fNextAction;
	}

	public void setNextAction(StepAction pNextAction) {
		fNextAction = pNextAction;
	}

	public void setNextAction(StepAction pNextAction, String pNextActionParameter) {
		setNextAction(pNextAction);
		setNextActionParameter(pNextActionParameter);
	}

	public String getNextActionParameter() {
		return fNextActionParameter;
	}

	public void setNextActionParameter(String pNextActionParameter) {
		fNextActionParameter = pNextActionParameter;
	}

	public void setSynchronize(boolean pSynchronize) {
		fSynchronize = pSynchronize;
	}

	public boolean isSynchronize() {
		return fSynchronize;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		IServerJsonOption.NEXT_ACTION.addTo(jsonObject, fNextAction);
		IServerJsonOption.NEXT_ACTION_PARAMETER.addTo(jsonObject, fNextActionParameter);
		IServerJsonOption.REPORT_LIST.addTo(jsonObject, fReportList.toJsonValue());
		if (fAnimation != null) {
			IServerJsonOption.ANIMATION.addTo(jsonObject, fAnimation.toJsonValue());
		}
		IServerJsonOption.SOUND.addTo(jsonObject, fSound);
		IServerJsonOption.SYNCHRONIZE.addTo(jsonObject, fSynchronize);
		return jsonObject;
	}

	public StepResult initFrom(IFactorySource source, JsonValue jsonValue) {
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		fNextAction = (StepAction) IServerJsonOption.NEXT_ACTION.getFrom(source, jsonObject);
		fNextActionParameter = IServerJsonOption.NEXT_ACTION_PARAMETER.getFrom(source, jsonObject);
		fReportList.clear();
		JsonObject reportListObject = IServerJsonOption.REPORT_LIST.getFrom(source, jsonObject);
		if (reportListObject != null) {
			fReportList.initFrom(source, reportListObject);
		}
		fAnimation = null;
		JsonObject animationObject = IServerJsonOption.ANIMATION.getFrom(source, jsonObject);
		if (animationObject != null) {
			fAnimation = new Animation().initFrom(source, animationObject);
		}
		fSound = (SoundId) IServerJsonOption.SOUND.getFrom(source, jsonObject);
		fSynchronize = IServerJsonOption.SYNCHRONIZE.getFrom(source, jsonObject);
		return this;
	}

}
