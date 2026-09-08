package com.fumbbl.ffb.server.util;

import com.fumbbl.ffb.CommonProperty;
import com.fumbbl.ffb.ReRolledAction;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.GameState;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Immutable description of a re-roll request, replacing the numerous
 * {@code askForReRollIfAvailable} overloads with a single fluent api.
 * <p>
 * Values that used to be calculated by dedicated overloads are still calculated, but they are resolved
 * lazily by {@link ReRollService} so that this class stays a pure data holder.
 */
public class ReRollRequest {

	private final GameState gameState;
	private final Player<?> player;
	private final ActingPlayer actingPlayer;
	private final ReRolledAction reRolledAction;
	private final int minimumRoll;
	private final boolean fumble;
	private final Skill modifyingSkill;
	private final Skill reRollSkill;
	private final Set<Skill> ignoreSkills;
	private final CommonProperty menuProperty;
	private final String defaultValueKey;
	private final List<String> messages;
	private final ReRollDialogParameterFactory dialogParameterFactory;

	private ReRollRequest(Builder builder) {
		gameState = builder.gameState;
		player = builder.player;
		actingPlayer = builder.actingPlayer;
		reRolledAction = builder.reRolledAction;
		minimumRoll = builder.minimumRoll;
		fumble = builder.fumble;
		modifyingSkill = builder.modifyingSkill;
		reRollSkill = builder.reRollSkill;
		ignoreSkills = builder.ignoreSkills;
		menuProperty = builder.menuProperty;
		defaultValueKey = builder.defaultValueKey;
		messages = builder.messages;
		dialogParameterFactory = builder.dialogParameterFactory;
	}

	private ReRollRequest(ReRollRequest request, Player<?> player, Skill reRollSkill) {
		gameState = request.gameState;
		this.player = player;
		actingPlayer = request.actingPlayer;
		reRolledAction = request.reRolledAction;
		minimumRoll = request.minimumRoll;
		fumble = request.fumble;
		modifyingSkill = request.modifyingSkill;
		this.reRollSkill = reRollSkill;
		ignoreSkills = request.ignoreSkills;
		menuProperty = request.menuProperty;
		defaultValueKey = request.defaultValueKey;
		messages = request.messages;
		dialogParameterFactory = request.dialogParameterFactory;
	}

	/**
	 * Creates a copy with the player and the re-roll skill the {@link ReRollService} derived from this request, so
	 * that mechanics can rely on both of them being present.
	 */
	public ReRollRequest resolved(Player<?> player, Skill reRollSkill) {
		return new ReRollRequest(this, player, reRollSkill);
	}

	/**
	 * Creates a request for the given acting player. The player the request is made for as well as the re-roll skill
	 * are derived from the acting player.
	 */
	public static Builder forActingPlayer(GameState gameState, ActingPlayer actingPlayer,
	                                      ReRolledAction reRolledAction, int minimumRoll) {
		Builder builder = new Builder(gameState, reRolledAction, minimumRoll);
		builder.actingPlayer = actingPlayer;
		return builder;
	}

	/**
	 * Creates a request for the given player. The re-roll skill is resolved from the game's acting player unless
	 * {@link Builder#reRollSkill(Skill)} is used.
	 */
	public static Builder forPlayer(GameState gameState, Player<?> player, ReRolledAction reRolledAction,
	                                int minimumRoll) {
		Builder builder = new Builder(gameState, reRolledAction, minimumRoll);
		builder.player = player;
		return builder;
	}

	public GameState getGameState() {
		return gameState;
	}

	public Player<?> getPlayer() {
		return player;
	}

	public ActingPlayer getActingPlayer() {
		return actingPlayer;
	}

	public ReRolledAction getReRolledAction() {
		return reRolledAction;
	}

	public int getMinimumRoll() {
		return minimumRoll;
	}

	public boolean isFumble() {
		return fumble;
	}

	public Skill getModifyingSkill() {
		return modifyingSkill;
	}

	public Skill getReRollSkill() {
		return reRollSkill;
	}

	public Set<Skill> getIgnoreSkills() {
		return ignoreSkills;
	}

	public CommonProperty getMenuProperty() {
		return menuProperty;
	}

	public String getDefaultValueKey() {
		return defaultValueKey;
	}

	public List<String> getMessages() {
		return messages;
	}

	/**
	 * @return the factory building the dialog parameter that is shown to the coach or {@code null} to use the default
	 * of the ruleset, only honoured by rulesets showing re-roll properties
	 */
	public ReRollDialogParameterFactory getDialogParameterFactory() {
		return dialogParameterFactory;
	}

	public static class Builder {
		private final GameState gameState;
		private final ReRolledAction reRolledAction;
		private final int minimumRoll;
		private Player<?> player;
		private ActingPlayer actingPlayer;
		private boolean fumble;
		private Skill modifyingSkill;
		private Skill reRollSkill;
		private Set<Skill> ignoreSkills = Collections.emptySet();
		private CommonProperty menuProperty;
		private String defaultValueKey;
		private List<String> messages;
		private ReRollDialogParameterFactory dialogParameterFactory;

		private Builder(GameState gameState, ReRolledAction reRolledAction, int minimumRoll) {
			this.gameState = gameState;
			this.reRolledAction = reRolledAction;
			this.minimumRoll = minimumRoll;
		}

		public Builder fumble() {
			return fumble(true);
		}

		public Builder fumble(boolean fumble) {
			this.fumble = fumble;
			return this;
		}

		public Builder modifyingSkill(Skill modifyingSkill) {
			this.modifyingSkill = modifyingSkill;
			return this;
		}

		/**
		 * Uses the given re-roll skill instead of deriving one from the acting player.
		 */
		public Builder reRollSkill(Skill reRollSkill) {
			this.reRollSkill = reRollSkill;
			return this;
		}

		public Builder ignoreSkills(Set<Skill> ignoreSkills) {
			this.ignoreSkills = ignoreSkills == null ? Collections.emptySet() : ignoreSkills;
			return this;
		}

		public Builder menu(CommonProperty menuProperty, String defaultValueKey) {
			this.menuProperty = menuProperty;
			this.defaultValueKey = defaultValueKey;
			return this;
		}

		public Builder messages(List<String> messages) {
			this.messages = messages;
			return this;
		}

		/**
		 * Uses the given factory to build the dialog parameter instead of the default one of the ruleset.
		 */
		public Builder dialogParameter(ReRollDialogParameterFactory dialogParameterFactory) {
			this.dialogParameterFactory = dialogParameterFactory;
			return this;
		}

		public ReRollRequest build() {
			return new ReRollRequest(this);
		}
	}
}
