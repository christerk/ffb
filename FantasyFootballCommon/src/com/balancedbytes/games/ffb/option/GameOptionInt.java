package com.balancedbytes.games.ffb.option;

import com.balancedbytes.games.ffb.util.StringTool;

/**
 * 
 * @author Kalimar
 */
public class GameOptionInt extends GameOptionAbstract {

	private int fDefault;
	private int fValue;
	private String fMessage;

	public GameOptionInt(GameOptionId pId) {
		super(pId);
	}

	protected int getDefault() {
		return fDefault;
	}

	@Override
	protected String getDefaultAsString() {
		return Integer.toString(getDefault());
	}

	public GameOptionInt setDefault(int pDefault) {
		fDefault = pDefault;
		return setValue(getDefault());
	}

	@Override
	public String getValueAsString() {
		return Integer.toString(getValue());
	}

	public int getValue() {
		return fValue;
	}

	@Override
	public GameOptionInt setValue(String pValue) {
		if (StringTool.isProvided(pValue)) {
			return setValue(Integer.parseInt(pValue));
		} else {
			return setValue(0);
		}
	}

	public GameOptionInt setValue(int pValue) {
		fValue = pValue;
		return this;
	}

	public GameOptionInt setMessage(String pMessage) {
		fMessage = pMessage;
		return this;
	}

	@Override
	public String getDisplayMessage() {
		return StringTool.bind(fMessage, StringTool.formatThousands(getValue()));
	}

}
