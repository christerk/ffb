package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonSerializable;

/**
 * 
 * @author Kalimar
 */
public interface IReport extends IJsonSerializable {

	public static final String XML_TAG = "report";

	public static final String XML_ATTRIBUTE_ID = "id";

	public ReportId getId();

	public IReport transform(IFactorySource source);

}
