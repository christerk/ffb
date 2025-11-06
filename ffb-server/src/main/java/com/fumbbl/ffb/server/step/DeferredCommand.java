package com.fumbbl.ffb.server.step;

import com.fumbbl.ffb.json.IJsonSerializable;

public interface DeferredCommand extends IJsonSerializable {
	void execute(IStep step);
}
