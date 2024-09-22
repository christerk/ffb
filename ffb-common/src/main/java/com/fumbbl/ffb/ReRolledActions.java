package com.fumbbl.ffb;

import com.fumbbl.ffb.skill.Dauntless;
import com.fumbbl.ffb.skill.FoulAppearance;
import com.fumbbl.ffb.skill.JumpUp;
import com.fumbbl.ffb.skill.Pass;
import com.fumbbl.ffb.skill.bb2016.WildAnimal;
import com.fumbbl.ffb.skill.bb2020.AnimalSavagery;
import com.fumbbl.ffb.skill.bb2020.BreatheFire;
import com.fumbbl.ffb.skill.bb2020.ProjectileVomit;
import com.fumbbl.ffb.skill.bb2020.UnchannelledFury;
import com.fumbbl.ffb.skill.bb2020.special.CatchOfTheDay;
import com.fumbbl.ffb.skill.bb2020.special.ThenIStartedBlastin;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class ReRolledActions {

	public static final ReRolledAction GO_FOR_IT = new ReRolledAction("Go For It");
	public static final ReRolledAction RUSH = new ReRolledAction("Rush");
	public static final ReRolledAction DODGE = new ReRolledAction("Dodge");
	public static final ReRolledAction CATCH = new ReRolledAction("Catch");
	public static final ReRolledAction PICK_UP = new ReRolledAction("Pick Up");
	public static final ReRolledAction PASS = new ReRolledAction(Pass.class);
	public static final ReRolledAction DAUNTLESS = new ReRolledAction(Dauntless.class);
	public static final ReRolledAction JUMP = new ReRolledAction("Jump");
	public static final ReRolledAction FOUL_APPEARANCE = new ReRolledAction(FoulAppearance.class);
	public static final ReRolledAction BLOCK = new ReRolledAction("Block");
	public static final ReRolledAction REALLY_STUPID = new ReRolledAction("Really Stupid");
	public static final ReRolledAction BONE_HEAD = new ReRolledAction("Bone Head");
	public static final ReRolledAction BONEHEAD = new ReRolledAction("Bone-Head");
	public static final ReRolledAction WILD_ANIMAL = new ReRolledAction(WildAnimal.class);
	public static final ReRolledAction ANIMAL_SAVAGERY = new ReRolledAction(AnimalSavagery.class);
	public static final ReRolledAction TAKE_ROOT = new ReRolledAction("Take Root");
	public static final ReRolledAction WINNINGS = new ReRolledAction("Winnings");
	public static final ReRolledAction ALWAYS_HUNGRY = new ReRolledAction("Always Hungry");
	public static final ReRolledAction THROW_TEAM_MATE = new ReRolledAction("Throw Team-Mate");
	public static final ReRolledAction KICK_TEAM_MATE = new ReRolledAction("Kick Team-Mate");
	public static final ReRolledAction RIGHT_STUFF = new ReRolledAction("Right Stuff");
	public static final ReRolledAction SHADOWING = new ReRolledAction("Shadowing");
	public static final ReRolledAction SHADOWING_ESCAPE = new ReRolledAction("Shadowing Escape");
	public static final ReRolledAction TENTACLES = new ReRolledAction("Tentacles");
	public static final ReRolledAction TENTACLES_ESCAPE = new ReRolledAction("Tentacles Escape");
	public static final ReRolledAction ESCAPE = new ReRolledAction("Escape");
	public static final ReRolledAction SAFE_THROW = new ReRolledAction("Safe Throw");
	public static final ReRolledAction INTERCEPTION = new ReRolledAction("Interception");
	public static final ReRolledAction JUMP_UP = new ReRolledAction(JumpUp.class);
	public static final ReRolledAction STAND_UP = new ReRolledAction("standUp");
	public static final ReRolledAction CHAINSAW = new ReRolledAction("Chainsaw");
	public static final ReRolledAction BLOOD_LUST = new ReRolledAction("Bloodlust");
	public static final ReRolledAction HYPNOTIC_GAZE = new ReRolledAction("Hypnotic Gaze");
	public static final ReRolledAction ANIMOSITY = new ReRolledAction("Animosity");
	public static final ReRolledAction UNCHANNELED_FURY = new ReRolledAction(UnchannelledFury.class);
	public static final ReRolledAction PROJECTILE_VOMIT = new ReRolledAction(ProjectileVomit.class);
	public static final ReRolledAction BREATHE_FIRE = new ReRolledAction(BreatheFire.class);
	public static final ReRolledAction TRAP_DOOR = new ReRolledAction("Trapdoor");
	public static final ReRolledAction ARGUE_THE_CALL = new ReRolledAction("Argue the Call");
	public static final ReRolledAction OLD_PRO = new ReRolledAction("Old Pro");
	public static final ReRolledAction THROW_KEG = new ReRolledAction("Throw Keg");
	public static final ReRolledAction DIRECTION = new ReRolledAction("Direction");
	public static final ReRolledAction LOOK_INTO_MY_EYES = new ReRolledAction("Look Into My Eyes");
	public static final ReRolledAction BALEFUL_HEX = new ReRolledAction("Baleful Hex");
	public static final ReRolledAction SINGLE_DIE = new ReRolledAction("Single Die");
	public static final ReRolledAction ALL_YOU_CAN_EAT = new ReRolledAction("All You Can Eat");
	public static final ReRolledAction CATCH_OF_THE_DAY = new ReRolledAction(CatchOfTheDay.class);
	public static final ReRolledAction SINGLE_BLOCK_DIE = new ReRolledAction("Single Block Die");
	public static final ReRolledAction THEN_I_STARTED_BLASTIN = new ReRolledAction(ThenIStartedBlastin.class);

	private final Map<String, ReRolledAction> values;

	public Map<String, ReRolledAction> values() {
		return values;
	}

	public ReRolledActions() {
		values = new HashMap<>();
		try {
			Class<?> c = this.getClass();
			Class<?> cModifierType = ReRolledAction.class;
			for (Field f : c.getDeclaredFields()) {
				if (f.getType() == cModifierType) {
					ReRolledAction action = (ReRolledAction) f.get(this);
					values.put(action.getName().toLowerCase(), action);
				}
			}

		} catch (IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
