package com.balancedbytes.games.ffb.option;

import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class GameOptionString extends GameOptionAbstract {

	private String fDefault;
	private String fValue;
	private String fMessage;

	public GameOptionString(GameOptionId pId) {
		super(pId);
	}

	protected String getDefault() {
		return fDefault;
	}

	@Override
	protected String getDefaultAsString() {
		return getDefault();
	}

	public GameOptionString setDefault(String pDefault) {
		fDefault = pDefault;
		return setValue(getDefault());
	}

	@Override
	public String getValueAsString() {
		return getValue();
	}

	public String getValue() {
		return fValue;
	}

	@Override
	public GameOptionString setValue(String pValue) {
		fValue = pValue;
		return this;
	}

	public GameOptionString setMessage(String pMessage) {
		fMessage = pMessage;
		return this;
	}

	@Override
	public String getDisplayMessage() {
		return StringTool.bind(fMessage, getValueAsString());
	}

}
