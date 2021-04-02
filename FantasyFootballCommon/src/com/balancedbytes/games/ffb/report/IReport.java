package com.balancedbytes.games.ffb.report;

import com.balancedbytes.games.ffb.INamedObject;
import com.balancedbytes.games.ffb.factory.IFactorySource;
import com.balancedbytes.games.ffb.json.IJsonSerializable;

/**
 * 
 * @author Kalimar
 */
public interface IReport extends IJsonSerializable, INamedObject {

	String XML_TAG = "report";

	ReportId getId();

	IReport transform(IFactorySource source);

	@Override
	default String getName() {
		return getId().getName();
	}
}
