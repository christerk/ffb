package com.fumbbl.ffb.inducement.bb2016;

import com.fumbbl.ffb.CardEffect;
import com.fumbbl.ffb.CardTarget;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.inducement.CardReport;
import com.fumbbl.ffb.inducement.InducementDuration;
import com.fumbbl.ffb.inducement.InducementPhase;
import com.fumbbl.ffb.mechanics.StatsMechanic;
import com.fumbbl.ffb.model.property.ISkillProperty;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillClassWithValue;
import com.fumbbl.ffb.modifiers.GoForItModifier;
import com.fumbbl.ffb.modifiers.InterceptionContext;
import com.fumbbl.ffb.modifiers.InterceptionModifier;
import com.fumbbl.ffb.modifiers.ModifierType;
import com.fumbbl.ffb.modifiers.PassModifier;
import com.fumbbl.ffb.modifiers.PlayerStatKey;
import com.fumbbl.ffb.modifiers.RollModifier;
import com.fumbbl.ffb.modifiers.TemporaryEnhancements;
import com.fumbbl.ffb.modifiers.TemporaryStatDecrementer;
import com.fumbbl.ffb.modifiers.TemporaryStatIncrementer;
import com.fumbbl.ffb.skill.Catch;
import com.fumbbl.ffb.skill.DisturbingPresence;
import com.fumbbl.ffb.skill.Fend;
import com.fumbbl.ffb.skill.Kick;
import com.fumbbl.ffb.skill.Pass;
import com.fumbbl.ffb.skill.Pro;
import com.fumbbl.ffb.skill.SureHands;
import com.fumbbl.ffb.skill.bb2016.Accurate;
import com.fumbbl.ffb.skill.bb2016.Bombardier;
import com.fumbbl.ffb.skill.bb2016.BoneHead;
import com.fumbbl.ffb.skill.bb2016.DirtyPlayer;
import com.fumbbl.ffb.skill.bb2016.HypnoticGaze;
import com.fumbbl.ffb.skill.bb2016.MightyBlow;
import com.fumbbl.ffb.skill.bb2016.NoHands;
import com.fumbbl.ffb.skill.bb2016.PassBlock;
import com.fumbbl.ffb.skill.bb2016.SecretWeapon;
import com.fumbbl.ffb.skill.bb2016.Shadowing;
import com.fumbbl.ffb.skill.bb2016.SideStep;
import com.fumbbl.ffb.util.UtilCards;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RulesCollection(RulesCollection.Rules.BB2016)
public class Cards implements com.fumbbl.ffb.inducement.Cards {

	private final Set<Card> cards = new HashSet<Card>() {{

		// --------------
		// 13x Magic Item
		// --------------

		// Description:
		// The player has come across the bracers of Count Luthor to use for the match.
		// They are so good that they even distract the player wearing them sometimes.
		// Timing:
		// Play at the beginning of your turn before any player takes an Action.
		// Effect:
		// Choose one player on your team. That player gains the skills
		// Hypnotic Gaze, Side Step, and Bone-head for the remainder of this game.
		add(new Card("Beguiling Bracers", "Beguiling Bracers", CardType.MAGIC_ITEM, CardTarget.OWN_PLAYER, false,
			new InducementPhase[]{InducementPhase.START_OF_OWN_TURN}, InducementDuration.UNTIL_END_OF_GAME,
			"Player gets Hypnotic Gaze, Side Step & Bone-Head") {
			@Override
			public TemporaryEnhancements activationEnhancement(StatsMechanic mechanic) {
				return super.activationEnhancement(mechanic).withSkills(
					new HashSet<SkillClassWithValue>() {{
						add(new SkillClassWithValue(BoneHead.class));
						add(new SkillClassWithValue(HypnoticGaze.class));
						add(new SkillClassWithValue(SideStep.class));
					}}
				);
			}
		});


		// Description:
		// Your player really has found a way to become a man of steel.
		// Timing:
		// Play after your turn has ended or your kick-off to an opponent is
		// resolved, but before your opponent's turn begins.
		// Effect:
		// Armour rolls made against a player of your choice may not be
		// modified or re-rolled by any positive modifiers for the remainder of
		// this game. This includes (but is not limited to) Claw, Mighty Blow,
		// Dirty Player, Piling On, fouling assists and Chainsaw attacks.
		add(new Card("Belt of Invulnerability", "Invulnerability Belt", CardType.MAGIC_ITEM, CardTarget.OWN_PLAYER,
			false, new InducementPhase[]{InducementPhase.END_OF_OWN_TURN, InducementPhase.AFTER_KICKOFF_TO_OPPONENT},
			InducementDuration.UNTIL_END_OF_GAME, "No modifiers or re-rolls on armour rolls") {
			@Override
			public TemporaryEnhancements activationEnhancement(StatsMechanic mechanic) {
				return super.activationEnhancement(mechanic)
					.withProperties(Collections.singleton(NamedProperties.preventArmourModifications));
			}
		});

		// Description:
		// One of the great passers of all time has loaned your player his
		// headband for this game, but you had better make sure you get it
		// back before he notices it missing!
		// Timing:
		// Play at the beginning of your turn before any player takes an Action.
		// Effect:
		// A player of your choice gains Pass and Accurate for this turn, but an
		// additional +1 modifier on any interception rolls against him is applied
		// as well.
		add(new Card("Fawndough's Headband", "Fawndough's Headband", CardType.MAGIC_ITEM, CardTarget.OWN_PLAYER, false,
			new InducementPhase[]{InducementPhase.START_OF_OWN_TURN}, InducementDuration.UNTIL_END_OF_TURN,
			"Player gets Pass & Accurate, opponents get +1 to intercept") {
			private final Card instance = this;

			@Override
			public Set<RollModifier<?>> rollModifiers() {
				return Collections.singleton(new InterceptionModifier("Fawndough's Headband", -1, ModifierType.REGULAR) {
					@Override
					public boolean appliesToContext(Skill skill, InterceptionContext context) {
						return super.appliesToContext(skill, context) && UtilCards.hasCard(context.getGame(), context.getGame().getThrower(), instance);
					}
				});
			}

			@Override
			public TemporaryEnhancements activationEnhancement(StatsMechanic mechanic) {
				return super.activationEnhancement(mechanic).withSkills(
					new HashSet<SkillClassWithValue>() {{
						add(new SkillClassWithValue(Pass.class));
						add(new SkillClassWithValue(Accurate.class));
					}}
				);
			}

		});

		// Description:
		// Your player paid top gold for a Ring of Invincibility, but it's not all that
		// was advertised.
		// Timing:
		// Play after your turn has ended or your kick-off to an opponent is
		// resolved, but before your opponent's turn begins.
		// Effect:
		// Choose the player on your team holding the ball. That player gains
		// the Sure Hands and Fend skills until he no longer has the ball.
		add(new Card("Force Shield", "Force Shield", CardType.MAGIC_ITEM, CardTarget.OWN_PLAYER, false,
			new InducementPhase[]{InducementPhase.END_OF_OWN_TURN, InducementPhase.AFTER_KICKOFF_TO_OPPONENT},
			InducementDuration.WHILE_HOLDING_THE_BALL, "Player gets Sure Hands & Fend", CardHandlerKey.FORCE_SHIELD) {
			@Override
			public TemporaryEnhancements activationEnhancement(StatsMechanic mechanic) {
				return super.activationEnhancement(mechanic).withSkills(
					new HashSet<SkillClassWithValue>() {{
						add(new SkillClassWithValue(Fend.class));
						add(new SkillClassWithValue(SureHands.class));
					}});
			}
		});

		// Description:
		// A scroll found in the house of a retired legendary coach contains a
		// spell of Bear strength.
		// Timing:
		// Play at the beginning of your turn before any player takes an Action.
		// Effect:
		// A player of your choice on your team gains +1 Strength until the
		// drive ends. After this the player has -1 Strength for the remainder of
		// this game
		add(new Card("Gikta's Strength of da Bear", "Gikta's Strength", CardType.MAGIC_ITEM,
			CardTarget.OWN_PLAYER, true, new InducementPhase[]{InducementPhase.START_OF_OWN_TURN},
			InducementDuration.UNTIL_END_OF_DRIVE,
			"Player gets +1 ST for this drive, then -1 ST for the remainder of the game") {
			@Override
			public TemporaryEnhancements activationEnhancement(StatsMechanic mechanic) {
				return super.activationEnhancement(mechanic)
					.withModifiers(Collections.singleton(new TemporaryStatIncrementer(PlayerStatKey.ST, mechanic)));
			}

			@Override
			public TemporaryEnhancements deactivationEnhancement(StatsMechanic mechanic) {
				return super.deactivationEnhancement(mechanic)
					.withModifiers(Collections.singleton(new TemporaryStatDecrementer(PlayerStatKey.ST, mechanic)));
			}
		});

		// Description:
		// A player puts a magic salve, Grisnick's Stickum, onto his gloves
		// before the drive.
		// Timing:
		// Play at any kick-off after all players have been set up and the ball
		// placed, but before any scatter has been rolled.
		// Effect:
		// A player of your choice on your team gains the Catch and Sure
		// Hands skills, but may not take Pass or Hand-off Actions for the
		// remainder of this game.
		add(new Card("Gloves of Holding", "Gloves of Holding", CardType.MAGIC_ITEM, CardTarget.OWN_PLAYER, false,
			new InducementPhase[]{InducementPhase.BEFORE_KICKOFF_SCATTER}, InducementDuration.UNTIL_END_OF_GAME,
			"Player gets Catch & Sure Hands, but may not Pass or Hand-off") {
			@Override
			public TemporaryEnhancements activationEnhancement(StatsMechanic mechanic) {
				return super.activationEnhancement(mechanic)
					.withSkills(
						new HashSet<SkillClassWithValue>() {{
							add(new SkillClassWithValue(Catch.class));
							add(new SkillClassWithValue(SureHands.class));
						}}
					)
					.withProperties(
						new HashSet<ISkillProperty>() {{
							add(NamedProperties.preventRegularHandOverAction);
							add(NamedProperties.preventRegularPassAction);
						}});
			}
		});

		// Description:
		// The player has come across a magic amulet that slows the speed of
		// any large objects that happen to intersect with his location.
		// Timing:
		// Play after your turn has ended or your kick-off to an opponent is
		// resolved, but before your opponent's turn begins.
		// Effect:
		// Choose one player on your team. For the remainder of this drive, any
		// opponent moving one square or more first and then blitzing this
		// player suffers a -1 modifier to his Strength (minimum Strength of 1)
		// for the block attempt.
		add(new Card("Inertia Damper", "Inertia Damper", CardType.MAGIC_ITEM, CardTarget.OWN_PLAYER, false,
			new InducementPhase[]{InducementPhase.END_OF_OWN_TURN, InducementPhase.AFTER_KICKOFF_TO_OPPONENT},
			InducementDuration.UNTIL_END_OF_DRIVE, "Opponents get -1 ST to Blitzing from 1 or more squares away") {
			@Override
			public TemporaryEnhancements activationEnhancement(StatsMechanic mechanic) {
				return super.activationEnhancement(mechanic)
					.withProperties(Collections.singleton(NamedProperties.weakenOpposingBlitzer));
			}
		});


		// Description:
		// The player has acquired some lucky charms from a Halfling in a green
		// coat before the game.
		// Timing:
		// Play during the pre-game after all inducements are purchased.
		// Effect:
		// A player of your choice may ignore the first time his armour is broken,
		// and just be Placed Prone. Any roll that ignores armour, such as the
		// crowd or throw a rock, is not affected by a lucky charm.
		add(new Card("Lucky Charm", "Lucky Charm", CardType.MAGIC_ITEM, CardTarget.OWN_PLAYER, false,
			new InducementPhase[]{InducementPhase.AFTER_INDUCEMENTS_PURCHASED}, InducementDuration.UNTIL_USED,
			"Ignore first armour break roll") {
			@Override
			public TemporaryEnhancements activationEnhancement(StatsMechanic mechanic) {
				return super.activationEnhancement(mechanic)
					.withProperties(Collections.singleton(NamedProperties.ignoreFirstArmourBreak));
			}
		});

		// Description:
		// Your team is featured in Spike! magazine and the magazine gives you
		// these gloves for your upcoming game.
		// Timing:
		// Play after your turn has ended or your kick-off to an opponent is
		// resolved, but before your opponent's turn begins.
		// Effect:
		// A player of your choice gains the Pass Block skill, and an additional +1
		// modifier to all interception rolls until the drive ends.
		add(new Card("Magic Gloves of Jark Longarm", "Magic Gloves", CardType.MAGIC_ITEM,
			CardTarget.OWN_PLAYER, false, new InducementPhase[]{InducementPhase.END_OF_OWN_TURN, InducementPhase.AFTER_KICKOFF_TO_OPPONENT},
			InducementDuration.UNTIL_END_OF_DRIVE, "Player gets Pass Block & +1 to interception") {
			private final Card instance = this;

			@Override
			public Set<RollModifier<?>> rollModifiers() {
				return Collections.singleton(new InterceptionModifier("Magic Gloves of Jark Longarm", -1, ModifierType.REGULAR) {
					@Override
					public boolean appliesToContext(Skill skill, InterceptionContext context) {
						return super.appliesToContext(skill, context) && UtilCards.hasCard(context.getGame(), context.getPlayer(), instance);
					}
				});
			}

			@Override
			public TemporaryEnhancements activationEnhancement(StatsMechanic mechanic) {
				return super.activationEnhancement(mechanic).withSkills(Collections.singleton(new SkillClassWithValue(PassBlock.class)));
			}

		});

		// Description:
		// Mother always said "never play without your codpiece". After years of
		// being passed from one generation to the next, the magic is still
		// working.
		// Timing:
		// Play during the pre-game after all inducements are purchased.
		// Effect:
		// A player of your choice may not be fouled for this game and injury rolls
		// against this player cannot be modified or re-rolled by anything
		// including (but not limited to) Dirty Player, Mighty Blow, Piling On, and
		// Stunty.
		add(new Card("Good Old Magic Codpiece", "Magic Codpiece", CardType.MAGIC_ITEM, CardTarget.OWN_PLAYER,
			false, new InducementPhase[]{InducementPhase.AFTER_INDUCEMENTS_PURCHASED},
			InducementDuration.UNTIL_END_OF_GAME, "Player cannot be fouled and no modifiers to injury rolls") {

			@Override
			public TemporaryEnhancements activationEnhancement(StatsMechanic mechanic) {
				return super.activationEnhancement(mechanic).withProperties(new HashSet<ISkillProperty>() {{
					add(NamedProperties.preventDamagingInjuryModifications);
					add(NamedProperties.preventBeingFouled);
				}});
			}
		});

		// Description:
		// One player finds himself a lucky rabbit's foot after the pre-game meal
		// of, well, rabbit.
		// Timing:
		// Play at the beginning of your turn before any player takes an Action.
		// Effect:
		// A player of your choice without Loner gains the Pro skill for the
		// remainder of this game.
		add(new Card("Rabbit's Foot", "Rabbit's Foot", CardType.MAGIC_ITEM, CardTarget.OWN_PLAYER, false,
			new InducementPhase[]{InducementPhase.START_OF_OWN_TURN}, InducementDuration.UNTIL_END_OF_GAME,
			"Player gets Pro (not playable on a Loner)", CardHandlerKey.RABBITS_FOOT) {
			@Override
			public TemporaryEnhancements activationEnhancement(StatsMechanic mechanic) {
				return super.activationEnhancement(mechanic).withSkills(Collections.singleton(new SkillClassWithValue(Pro.class)));
			}

		});


		// Description:
		// Stick! Smash!
		// Timing:
		// Play at the beginning of your turn before any player takes an Action.
		// Effect:
		// Choose one player on your team. That player gains +1 strength and
		// the Mighty Blow skill for this turn.
		add(new Card("Wand of Smashing", "Wand of Smashing", CardType.MAGIC_ITEM, CardTarget.OWN_PLAYER, false,
			new InducementPhase[]{InducementPhase.START_OF_OWN_TURN}, InducementDuration.UNTIL_END_OF_TURN,
			"Player gets +1 ST & Mighty Blow") {

			@Override
			public TemporaryEnhancements activationEnhancement(StatsMechanic mechanic) {
				return super.activationEnhancement(mechanic)
					.withSkills(Collections.singleton(new SkillClassWithValue(MightyBlow.class)))
					.withModifiers(Collections.singleton(new TemporaryStatIncrementer(PlayerStatKey.ST, mechanic)));
			}
		});


		// ---------------
		// 13x Dirty Trick
		// ---------------
		// Description:
		// A player on your team is determined to take out the opposition, no matter
		// what.
		// Timing:
		// Play at the beginning of your turn before any player takes an Action.
		// Effect:
		// The armour roll for your Foul Action this turn automatically succeeds
		// and is considered a non-doubles roll, however the injury roll for the
		// foul must be rolled as normal with the player sent off on doubles.
		add(new Card("Blatant Foul", "Blatant Foul", CardType.DIRTY_TRICK, CardTarget.TURN, false,
			new InducementPhase[]{InducementPhase.START_OF_OWN_TURN}, InducementDuration.UNTIL_END_OF_TURN,
			"Next foul breaks armour automatically") {
			@Override
			public Set<ISkillProperty> globalProperties() {
				return Collections.singleton(NamedProperties.foulBreaksArmourWithoutRoll);
			}
		});

		// Description:
		// A player throws a dirty block on the opponent.
		// Timing:
		// Play after your turn has ended but before your opponent's turn
		// begins. You may not play this card after a kick-off is resolved.
		// Effect:
		// This card may only be played on one of your Standing players that
		// did not take an Action during your last turn. Your player is Placed
		// Prone and an opposing player in a square adjacent to him is now
		// considered Stunned.
		add(new Card("Chop Block", "Chop Block", CardType.DIRTY_TRICK, CardTarget.OWN_PLAYER, false,
			new InducementPhase[]{InducementPhase.END_OF_OWN_TURN}, InducementDuration.UNTIL_END_OF_TURN,
			"Unmoved player drops prone and stuns an adjacent player", CardHandlerKey.CHOP_BLOCK) {

			@Override
			public boolean requiresBlockablePlayerSelection() {
				return true;
			}
		});

		// Description:
		// One of your players thrusts a cleverly concealed custard pie in the
		// face of an opposing player.
		// Timing:
		// Play at the beginning of your turn before any player takes an Action.
		// Effect:
		// Choose one player on the opposing team adjacent to one of your
		// Standing or Prone players (not Stunned). That opposing player is so
		// flabbergasted by the pie hit that he loses his tackle zones for the
		// remainder of this turn as per a successful Hypnotic Gaze roll.
		add(new Card("Custard Pie", "Custard Pie", CardType.DIRTY_TRICK, CardTarget.OPPOSING_PLAYER, false,
			new InducementPhase[]{InducementPhase.START_OF_OWN_TURN}, InducementDuration.UNTIL_END_OF_TURN,
			"Opponent distracted as per Hypnotic Gaze", CardHandlerKey.CUSTARD_PIE));

		// Description:
		// Your player is very good at distracting all those around him.
		// Timing:
		// Play after your turn has ended or your kick-off to an opponent is
		// resolved, but before your opponent's turn begins.
		// Effect:
		// The chosen player gains the skill Disturbing Presence for this turn
		// and all opposing players starting their Action within 3 squares of the
		// player count as having Bone-head (lost Tackle Zones from failed
		// Bone-head rolls return at the end of this turn).
		add(new Card("Distract", "Distract", CardType.DIRTY_TRICK, CardTarget.OWN_PLAYER, false,
			new InducementPhase[]{InducementPhase.END_OF_OWN_TURN, InducementPhase.AFTER_KICKOFF_TO_OPPONENT},
			InducementDuration.UNTIL_END_OF_OPPONENTS_TURN,
			"Player gets Disturbing Presence & opponents in 3 squares get Bone-head", CardHandlerKey.DISTRACT) {
			@Override
			public TemporaryEnhancements activationEnhancement(StatsMechanic mechanic) {
				return super.activationEnhancement(mechanic).withSkills(Collections.singleton(new SkillClassWithValue(DisturbingPresence.class)));
			}
		});

		// Description:
		// The magic grease applied to your opponent's shoes has finally taken effect.
		// Timing:
		// Play after your turn has ended or your kick-off to an opponent is
		// resolved, but before your opponent's turn begins.
		// Effect:
		// This turn all opposing players need to roll a 5+ to Go For It instead of
		// the normal 2+.
		add(new Card("Greased Shoes", "Greased Shoes", CardType.DIRTY_TRICK, CardTarget.TURN, false,
			new InducementPhase[]{InducementPhase.END_OF_OWN_TURN, InducementPhase.AFTER_KICKOFF_TO_OPPONENT},
			InducementDuration.UNTIL_END_OF_OPPONENTS_TURN, "Opposing players need to roll 5+ to Go For It") {
			@Override
			public Set<ISkillProperty> globalProperties() {
				return Collections.singleton(NamedProperties.setGfiRollToFive);
			}

			@Override
			public Set<RollModifier<?>> rollModifiers() {
				return Collections.singleton(new GoForItModifier("Greased Shoes", 3));
			}
		});

		// Description:
		// A player purchased some exploding runes from a dwarven runesmith
		// before the game. Although they are illegal, they are highly effective.
		// Timing:
		// Play before setting up for a drive.
		// Effect:
		// Choose one player on your team. That player gains the Bombardier,
		// No Hands, and Secret Weapon skills for this game. Because the
		// Rune can be very volatile, any pass roll made with a Rune bomb is
		// performed with a -1 modifier to the pass roll.
		add(new Card("Gromskull's Exploding Runes", "Exploding Runes", CardType.DIRTY_TRICK,
			CardTarget.OWN_PLAYER, false, new InducementPhase[]{InducementPhase.BEFORE_SETUP},
			InducementDuration.UNTIL_END_OF_GAME, "Player gets Bombardier, No Hands, Secret Weapon & -1 to pass") {
			@Override
			public Set<RollModifier<?>> rollModifiers() {
				return Collections.singleton(new PassModifier("Gromskull's Exploding Runes", 1,
					ModifierType.REGULAR));
			}

			@Override
			public TemporaryEnhancements activationEnhancement(StatsMechanic mechanic) {
				return super.activationEnhancement(mechanic).withSkills(
					new HashSet<SkillClassWithValue>() {{
						add(new SkillClassWithValue(NoHands.class));
						add(new SkillClassWithValue(Bombardier.class));
						add(new SkillClassWithValue(SecretWeapon.class));
					}});
			}
		});

		// Description:
		// A reserve sneaks onto the pitch while the ref is cleaning his glasses.
		// Timing:
		// Play at the beginning of your turn before any player takes an Action.
		// Effect:
		// You may place any player from the reserves box in an unoccupied
		// square in the end zone you are defending. This player may only take
		// a Move Action this turn. This may take your team to 12 players for
		// the remainder of the drive.
		add(new Card("Illegal Substitution", "Illegal Substitution", CardType.DIRTY_TRICK, CardTarget.TURN, false,
			new InducementPhase[]{InducementPhase.START_OF_OWN_TURN}, InducementDuration.UNTIL_END_OF_TURN,
			"Place an extra player in your end zone", CardHandlerKey.ILLEGAL_SUBSTITUTION));

		// Description:
		// These boots were made for stomping, and that is just what they will do!
		// Timing:
		// Play after all players have been set up for a kick-off, but before any
		// kick-off result is rolled.
		// Effect:
		// A player of your choice on your team gains the Kick and Dirty Player
		// skills and a -1 MA for the remainder of this game.
		add(new Card("Kicking Boots", "Kicking Boots", CardType.DIRTY_TRICK, CardTarget.OWN_PLAYER, false,
			new InducementPhase[]{InducementPhase.BEFORE_KICKOFF_SCATTER}, InducementDuration.UNTIL_END_OF_GAME,
			"Player gets Kick, Dirty Player & -1 MA") {
			@Override
			public TemporaryEnhancements activationEnhancement(StatsMechanic mechanic) {
				return super.activationEnhancement(mechanic)
					.withSkills(
						new HashSet<SkillClassWithValue>() {{
							add(new SkillClassWithValue(Kick.class));
							add(new SkillClassWithValue(DirtyPlayer.class));
						}})
					.withModifiers(Collections.singleton(new TemporaryStatDecrementer(PlayerStatKey.MA, mechanic)));
			}
		});

		// Description:
		// A devious groundskeeper has set up a pit trap for you.
		// Timing:
		// Play after your turn has ended or your kick-off to an opponent is
		// resolved, but before your opponent's turn begins.
		// Effect:
		// Choose a player: that player is Placed Prone, no armour roll is made,
		// and if the player had the ball bounce it as normal.
		add(new Card("Pit Trap", "Pit Trap", CardType.DIRTY_TRICK, CardTarget.ANY_PLAYER, false,
			new InducementPhase[]{InducementPhase.END_OF_OWN_TURN}, InducementDuration.UNTIL_END_OF_TURN,
			"Player is placed prone, no armour roll", CardHandlerKey.PIT_TRAP));

		// Description:
		// A Bloodthirster is in the crowd today, so in honour of this event a
		// spiked ball is swapped with the real ball. More blood for the blood god
		// and the fans!
		// Timing:
		// Play after all players have been set up for a kick-off, but before any
		// kick-off result is rolled.
		// Effect:
		// Until the drive ends any failed pick up or catch roll (but not interception
		// roll) is treated as the player being attacked with the Stab skill by an
		// opponent.
		add(new Card("Spiked Ball", "Spiked Ball", CardType.DIRTY_TRICK, CardTarget.TURN, false,
			new InducementPhase[]{InducementPhase.BEFORE_KICKOFF_SCATTER}, InducementDuration.UNTIL_END_OF_DRIVE,
			"Any failed pick up or catch roll results in being stabbed") {
			@Override
			public Set<ISkillProperty> globalProperties() {
				return Collections.singleton(NamedProperties.droppedBallCausesArmourRoll);
			}
		});

		// Description:
		// You nabbed a playbook from the opponent's coach! He sure will be
		// surprised when you know exactly how to ruin his play.
		// Timing:
		// Play after your turn has ended or your kick-off to an opponent is
		// resolved, but before your opponent's turn begins.
		// Effect:
		// A player of your choice gains Pass Block and Shadowing until the drive
		// ends.
		add(new Card("Stolen Playbook", "Stolen Playbook", CardType.DIRTY_TRICK, CardTarget.OWN_PLAYER, false,
			new InducementPhase[]{InducementPhase.END_OF_OWN_TURN}, InducementDuration.UNTIL_END_OF_DRIVE,
			"Player gets Pass Block and Shadowing") {
			@Override
			public TemporaryEnhancements activationEnhancement(StatsMechanic mechanic) {
				return super.activationEnhancement(mechanic).withSkills(
					new HashSet<SkillClassWithValue>() {{
						add(new SkillClassWithValue(PassBlock.class));
						add(new SkillClassWithValue(Shadowing.class));
					}});
			}
		});


		// Description:
		// You've spiked the opponent's Kroxorade bottle with a witch's concoction!
		// Timing:
		// Play after all players have been set up for a kick-off, but before any
		// kick-off result is rolled.
		// Effect:
		// Choose an opponent and roll on this table.
		// 1- Woops! Mad Cap Mushroom potion! The player gains the Jump Up
		// and No Hands skills until the drive ends.
		// 2- Snake Oil! Bad taste, but no effect.
		// 3-6 Sedative! The player gains the Really Stupid skill until the drive
		// ends.

		add(new Card("Witch's Brew", "Witch Brew", CardType.DIRTY_TRICK, CardTarget.OPPOSING_PLAYER, false,
			new InducementPhase[]{InducementPhase.BEFORE_KICKOFF_SCATTER}, InducementDuration.UNTIL_END_OF_DRIVE,
			"Poison an opponent (random effect)", CardHandlerKey.WITCH_BREW) {
			@Override
			public Optional<CardReport> cardReport(CardEffect effect, int roll) {

				String rollReport = "Witch Brew Roll [ " + roll + " ]";
				String effectReport = "Snake Oil! Bad taste, but no effect.";

				if (effect != null) {
					switch (effect) {
						case SEDATIVE:
							effectReport = "Sedative! The player gains the Really Stupid skill until the drive ends.";
							break;
						case MAD_CAP_MUSHROOM_POTION:
							effectReport = "Mad Cap Mushroom potion! The player gains the Jump Up and No Hands skills until the drive ends.";
							break;
						default:
							break;
					}
				}
				return Optional.of(new CardReport(rollReport, effectReport));
			}
		});
	}};

	@Override
	public Set<Card> allCards() {
		return cards;
	}
}
