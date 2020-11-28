package com.balancedbytes.games.ffb;

import java.lang.reflect.Field;
import java.util.Map;

import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Skill;
import com.balancedbytes.games.ffb.model.SkillConstants;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
import com.balancedbytes.games.ffb.util.UtilCards;
import com.balancedbytes.games.ffb.util.UtilPlayer;

public class DodgeModifiers {
  public static final DodgeModifier TWO_HEADS = new DodgeModifier("Two Heads", -1, false, false);
  public static final DodgeModifier DIVING_TACKLE = new DodgeModifier("Diving Tackle", 2, false, false);
  public static final DodgeModifier TITCHY = new DodgeModifier("Titchy", -1, false, false);
  public static final DodgeModifier TACKLEZONES_1 = new DodgeModifier("1 Tacklezone", 1, true, false);
  public static final DodgeModifier TACKLEZONES_2 = new DodgeModifier("2 Tacklezones", 2, true, false);
  public static final DodgeModifier TACKLEZONES_3 = new DodgeModifier("3 Tacklezones", 3, true, false);
  public static final DodgeModifier TACKLEZONES_4 = new DodgeModifier("4 Tacklezones", 4, true, false);
  public static final DodgeModifier TACKLEZONES_5 = new DodgeModifier("5 Tacklezones", 5, true, false);
  public static final DodgeModifier TACKLEZONES_6 = new DodgeModifier("6 Tacklezones", 6, true, false);
  public static final DodgeModifier TACKLEZONES_7 = new DodgeModifier("7 Tacklezones", 7, true, false);
  public static final DodgeModifier TACKLEZONES_8 = new DodgeModifier("8 Tacklezones", 8, true, false);
  public static final DodgeModifier PREHENSILE_TAIL_1 = new PrehensileDodgeModifier("1 Prehensile Tail", 1, false, true);
  public static final DodgeModifier PREHENSILE_TAIL_2 = new PrehensileDodgeModifier("2 Prehensile Tails", 2, false, true);
  public static final DodgeModifier PREHENSILE_TAIL_3 = new PrehensileDodgeModifier("3 Prehensile Tails", 3, false, true);
  public static final DodgeModifier PREHENSILE_TAIL_4 = new PrehensileDodgeModifier("4 Prehensile Tails", 4, false, true);
  public static final DodgeModifier PREHENSILE_TAIL_5 = new PrehensileDodgeModifier("5 Prehensile Tails", 5, false, true);
  public static final DodgeModifier PREHENSILE_TAIL_6 = new PrehensileDodgeModifier("6 Prehensile Tails", 6, false, true);
  public static final DodgeModifier PREHENSILE_TAIL_7 = new PrehensileDodgeModifier("7 Prehensile Tails", 7, false, true);
  public static final DodgeModifier PREHENSILE_TAIL_8 = new PrehensileDodgeModifier("8 Prehensile Tails", 8, false, true);

  public static final DodgeModifier STUNTY = new DodgeModifier("Stunty", 0, false, false) {
    @Override
    public boolean appliesToContext(Skill skill, DodgeContext context) {
      boolean applies = !UtilCards.hasSkillWithProperty(context.actingPlayer.getPlayer(), NamedProperties.preventStuntyDodgeModifier);
      if (applies) {
        context.addTackleZoneModifier = false;
      }
      return applies;
    }
  };
  
  public static final DodgeModifier BREAK_TACKLE = new DodgeModifier("Break Tackle", 0, false, false) {
    @Override
    public boolean appliesToContext(Skill skill, DodgeContext context) {
      return UtilCards.hasUnusedSkill(context.actingPlayer.getGame(), context.actingPlayer, skill);
    }
  };

  
  public static final class PrehensileDodgeModifier extends DodgeModifier {

    public PrehensileDodgeModifier(String pName, int pModifier, boolean pTacklezoneModifier,
        boolean pPrehensileTailModifier) {
      super(pName, pModifier, pTacklezoneModifier, pPrehensileTailModifier);
      
    }

    @Override
    public boolean appliesToContext(Skill skill, DodgeContext context) {
      int number = findNumberOfPrehensileTails(context.actingPlayer.getGame(), context.sourceCoordinate);
      return number == this.getModifier();
    }
    
    private int findNumberOfPrehensileTails(Game pGame, FieldCoordinate pCoordinateFrom) {
      ActingPlayer actingPlayer = pGame.getActingPlayer();
      Team otherTeam = UtilPlayer.findOtherTeam(pGame, actingPlayer.getPlayer());
      int nrOfPrehensileTails = 0;
      Player<?>[] opponents = UtilPlayer.findAdjacentPlayersWithTacklezones(pGame, otherTeam, pCoordinateFrom, true);
      for (Player<?> opponent : opponents) {
        if (UtilCards.hasSkill(pGame, opponent, SkillConstants.PREHENSILE_TAIL)) {
          nrOfPrehensileTails++;
        }
      }
      return nrOfPrehensileTails;
    }    
  }
  
  private static Map<String, DodgeModifier> values;
  public static Map<String, DodgeModifier> values() { return values;}

  public DodgeModifiers() {
	  try {
		  Class<?> c = this.getClass();
		  Class<?> cModifierType = DodgeModifier.class.getClass();
		  for(Field f :c.getDeclaredFields())
		  {
			  if(f.getType() == cModifierType)
			  {
				  DodgeModifier modifier = (DodgeModifier)f.get(this);
				  values.put(modifier.getName().toLowerCase(), modifier);
			  }
		  }

	  } catch (IllegalArgumentException | IllegalAccessException e) {
		  // TODO Auto-generated catch block
		  e.printStackTrace();
	  }
  }

  public static class DodgeContext {
	  public ActingPlayer actingPlayer;
	  public FieldCoordinate sourceCoordinate;
	  public boolean addTackleZoneModifier;

	  public DodgeContext(ActingPlayer actingPlayer, FieldCoordinate sourceCoordinate) {
		  this.sourceCoordinate = sourceCoordinate;
		  this.actingPlayer = actingPlayer;
		  this.addTackleZoneModifier = true;
	  }
  }

}
