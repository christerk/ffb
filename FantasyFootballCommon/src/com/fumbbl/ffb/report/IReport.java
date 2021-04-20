package com.fumbbl.ffb.report;

import com.fumbbl.ffb.INamedObject;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonSerializable;

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
