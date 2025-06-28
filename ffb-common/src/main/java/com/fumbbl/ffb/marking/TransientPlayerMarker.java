package com.fumbbl.ffb.marking;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.factory.IFactorySource;

/**
 * Subclass of {@link PlayerMarker} to be used only on client side. Resides in common module, so we can use it explicitly
 * in {@link com.fumbbl.ffb.model.FieldModel}
 */

public class TransientPlayerMarker extends PlayerMarker {

	private Mode mode;

	public TransientPlayerMarker(String pPlayerId, Mode mode) {
		super(pPlayerId);
		this.mode = mode;
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	@Override
	public void setAwayText(String pAwayText) {
		throw new UnsupportedOperationException("Transient marker can not be used for the away team");
	}

	@Override
	public String getAwayText() {
		throw new UnsupportedOperationException("Transient marker can not be used for the away team");
	}
// Transformation

	public TransientPlayerMarker transform() {
		throw new UnsupportedOperationException("Transient marker must not be sent to the server");
	}

	// JSON serialization

	public JsonObject toJsonValue() {
		throw new UnsupportedOperationException("Transient marker must not be sent to the server");
	}

	public TransientPlayerMarker initFrom(IFactorySource source, JsonValue jsonValue) {
		throw new UnsupportedOperationException("Transient marker must not be sent to the server");

	}

	public enum Mode {
		APPEND("Append"), PREPEND("Prepend"), REPLACE("Replace"), SEPARATE("Separate");

		private String displayText;

		Mode(String displayText) {
			this.displayText = displayText;
		}

		public String getDisplayText() {
			return displayText;
		}
	}
}
