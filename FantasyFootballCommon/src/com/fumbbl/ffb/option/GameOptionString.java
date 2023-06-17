package com.fumbbl.ffb.option;

import com.fumbbl.ffb.util.StringTool;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kalimar
 */
public class GameOptionString extends GameOptionAbstract {

	public static final String CHAINSAW_TURNOVER_NEVER = "never";
	public static final String CHAINSAW_TURNOVER_KICKBACK = "kickback";
	public static final String CHAINSAW_TURNOVER_KICKBACK_AV_BREAK_ONLY = "kickbackAvBreak";
	public static final String CHAINSAW_TURNOVER_ALL_AV_BREAKS = "allAvBreaks";

	// legacy, keep around for old replays and ongoing matches after update
	public static final String CHAINSAW_TURNOVER_KICKBACK_ONLY = "kickbackOnly";
	public static final String CHAINSAW_TURNOVER_ALWAYS = "always";

	private String fDefault;
	private String fValue;
	private String fMessage;

	private final Map<String, String> messages = new HashMap<>();

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


	public GameOptionString addValueMessage(String value, String pMessage) {
		messages.put(value, pMessage);
		return this;
	}

	@Override
	public String getDisplayMessage() {
		String param = messages.get(getValueAsString());
		if (!StringTool.isProvided(param)) {
			param = getValueAsString();
		}

		return StringTool.bind(fMessage, param);
	}

}
