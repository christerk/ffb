package com.fumbbl.ffb.model.change;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.json.IJsonOption;
import com.fumbbl.ffb.json.IJsonSerializable;
import com.fumbbl.ffb.json.UtilJson;
import com.fumbbl.ffb.model.Game;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Kalimar
 */
public class ModelChangeList implements IJsonSerializable {

	private final List<ModelChange> fChanges;

	public ModelChangeList() {
		this(16);
	}

	public ModelChangeList(int pInitialCapacity) {
		fChanges = new ArrayList<>(pInitialCapacity);
	}

	public void add(ModelChange newChange) {
		if (newChange != null) {
			fChanges.add(newChange);
		}
	}

	public void replace(ModelChange newChange) {
		if ((newChange == null) || (newChange.getChangeId() == null)) {
			return;
		}
		for (int i = 0; i < fChanges.size(); i++) {
			if (newChange.getChangeId() == fChanges.get(i).getChangeId()) {
				fChanges.set(i, newChange);
				return;
			}
		}
		add(newChange);
	}

	public void add(ModelChangeList pChangeList) {
		if (pChangeList != null) {
			for (ModelChange change : pChangeList.getChanges()) {
				add(change);
			}
		}
	}

	public ModelChange[] getChanges() {
		return fChanges.toArray(new ModelChange[0]);
	}

	public void clear() {
		fChanges.clear();
	}

	public int size() {
		return fChanges.size();
	}

	public void applyTo(Game pGame) {
		ModelChangeProcessor processor = new ModelChangeProcessor();
		for (ModelChange change : getChanges()) {
			processor.apply(pGame, change);
		}
	}

	// transformation

	public ModelChangeList transform() {
		ModelChangeList transformedList = new ModelChangeList(size());
		ModelChangeProcessor processor = new ModelChangeProcessor();
		for (ModelChange change : getChanges()) {
			transformedList.add(processor.transform(change));
		}
		return transformedList;
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		JsonObject jsonObject = new JsonObject();
		JsonArray modelChanges = new JsonArray();
		for (ModelChange change : fChanges) {
			modelChanges.add(change.toJsonValue());
		}
		IJsonOption.MODEL_CHANGE_ARRAY.addTo(jsonObject, modelChanges);
		return jsonObject;
	}

	public ModelChangeList initFrom(IFactorySource source, JsonValue jsonValue) {
		clear();
		JsonObject jsonObject = UtilJson.toJsonObject(jsonValue);
		JsonArray modelChanges = IJsonOption.MODEL_CHANGE_ARRAY.getFrom(source, jsonObject);
		for (int i = 0; i < modelChanges.size(); i++) {
			add(new ModelChange().initFrom(source, modelChanges.get(i)));
		}
		return this;
	}

}
