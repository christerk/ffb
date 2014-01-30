package com.balancedbytes.games.ffb;

import java.util.Comparator;


/**
 * 
 * @author Kalimar
 */
public enum Card implements IEnumWithId, IEnumWithName {

  // ------------------------
	// 13x Miscellaneous Mayhem
  // ------------------------

  // ---------------------
	// 13x Special Team Play
  // ---------------------
	
  // --------------
	// 13x Magic Item
  // --------------
	
  BEGUILING_BRACERS(27, "Beguiling Bracers", "Beguiling Bracers", CardType.MAGIC_ITEM, CardTarget.OWN_PLAYER, false,
  	new InducementPhase[] { InducementPhase.START_OF_OWN_TURN }, InducementDuration.UNTIL_END_OF_GAME,
  	"Player gets Hypnotic Gaze, Side Step & Bone-Head"),
  //  Description:
  //    The player has come across the bracers of Count Luthor to use for the match.
  //    They are so good that they even distract the player wearing them sometimes.
  //  Timing:
  //    Play at the beginning of your turn before any player takes an Action.
  //  Effect:
  //    Choose one player on your team. That player gains the skills
  //    Hypnotic Gaze, Side Step, and Bone-head for the remainder of this game.

  BELT_OF_INVULNERABILITY(28, "Belt of Invulnerability", "Invulnerability Belt", CardType.MAGIC_ITEM, CardTarget.OWN_PLAYER, false,
  	new InducementPhase[] { InducementPhase.END_OF_OWN_TURN, InducementPhase.AFTER_KICKOFF_TO_OPPONENT_RESOLVED }, InducementDuration.UNTIL_END_OF_GAME,
  	"No modifiers or re-rolls on armour rolls"),
  //  Description:
  //    Your player really has found a way to become a man of steel.
  //  Timing:
  //    Play after your turn has ended or your kick-off to an opponent is
  //    resolved, but before your opponents turn begins.
  //  Effect:
  //    Armour rolls made against a player of your choice may not be
  //    modified or re-rolled by any positive modifiers for the remainder of
  //    this game. This includes (but is not limited to) Claw, Mighty Blow,
  //    Dirty Player, Piling On, fouling assists and Chainsaw attacks.
  
  FAWNDOUGHS_HEADBAND(29, "Fawndough's Headband", "Fawndough's Headband", CardType.MAGIC_ITEM, CardTarget.OWN_PLAYER, false,
  	new InducementPhase[] { InducementPhase.START_OF_OWN_TURN }, InducementDuration.UNTIL_END_OF_TURN,
  	"Player gets Pass & Accurate, opponents get +1 to intercept"),
  //  Description:
  //    One of the great passers of all time has loaned your player his
  //    headband for this game, but you had better make sure you get it
  //    back before he notices it missing!
  //  Timing:
  //    Play at the beginning of your turn before any player takes an Action.
  //  Effect:
  //    A player of your choice gains Pass and Accurate for this turn, but an
  //    additional +1 modifier on any interception rolls against him is applied
  //    as well.
  
  FORCE_SHIELD(30, "Force Shield", "Force Shield", CardType.MAGIC_ITEM, CardTarget.OWN_PLAYER, false,
   	new InducementPhase[] { InducementPhase.END_OF_OWN_TURN, InducementPhase.AFTER_KICKOFF_TO_OPPONENT_RESOLVED }, InducementDuration.WHILE_HOLDING_THE_BALL,
   	"Player gets Sure Hands & Fend"),
  //  Description:
  //    Your player paid top gold for a Ring of Invincibility, but it's not all that
  //    was advertised.
  //  Timing:
  //    Play after your turn has ended or your kick-off to an opponent is
  //    resolved, but before your opponents turn begins.
  //  Effect:
  //    Choose the player on your team holding the ball. That player gains
  //    the Sure Hands and Fend skills until he no longer has the ball.

  GIKTAS_STRENGTH_OF_DA_BEAR(31, "Gikta's Strength of da Bear", "Gikta's Strength", CardType.MAGIC_ITEM, CardTarget.OWN_PLAYER, true,
   	new InducementPhase[] { InducementPhase.START_OF_OWN_TURN }, InducementDuration.UNTIL_END_OF_DRIVE,
   	"Player gets +1 ST for this drive, then -1 ST for the remainder of the game"),
  //  Description:
  //    A scroll found in the house of a retired legendary coach contains a
  //    spell of Bear strength.
  //  Timing:
  //    Play at the beginning of your turn before any player takes an Action.
  //  Effect:
  //    A player of your choice on your team gains +1 Strength until the
  //    drive ends. After this the player has -1 Strength for the remainder of
  //    this game.
  
  GLOVES_OF_HOLDING(32, "Gloves of Holding", "Gloves of Holding", CardType.MAGIC_ITEM, CardTarget.OWN_PLAYER, false,
  	new InducementPhase[] { InducementPhase.BEFORE_KICKOFF_SCATTER }, InducementDuration.UNTIL_END_OF_GAME,
  	"Player gets Catch & Sure Hands, but may not Pass or Hand-off"),
  //  Description:
  //	  A player puts a magic salve, Grisnick's Stickum, onto his gloves
  //    before the drive.
  //  Timing:
  //	  Play at any kick-off after all players have been set up and the ball
  //    placed, but before any scatter has been rolled.
  //  Effect:
  //	  A player of your choice on your team gains the Catch and Sure
  //    Hands skills, but may not take Pass or Hand-off Actions for the
  //    remainder of this game.

  INERTIA_DAMPER(33, "Inertia Damper", "Inertia Damper", CardType.MAGIC_ITEM, CardTarget.OWN_PLAYER, false,
    new InducementPhase[] { InducementPhase.END_OF_OWN_TURN, InducementPhase.AFTER_KICKOFF_TO_OPPONENT_RESOLVED }, InducementDuration.UNTIL_END_OF_DRIVE,
    "Opponents get -1 ST to Blitzing from 1 or more squares away"),
  //  Description:
  //	  The player has come across a magic amulet that slows the speed of
  //    any large objects that happen to intersect with his location.
  //  Timing:
  //	  Play after your turn has ended or your kick-off to an opponent is
  //    resolved, but before your opponents turn begins.
  //  Effect:
  //	  Choose one player on your team. For the remainder of this drive, any
  //    opponent moving one square or more first and then blitzing this
  //    player suffers a -1 modifier to his Strength (minimum Strength of 1)
  //    for the block attempt.
  
  LUCKY_CHARM(34, "Lucky Charm", "Lucky Charm", CardType.MAGIC_ITEM, CardTarget.OWN_PLAYER, false,
  	new InducementPhase[] { InducementPhase.AFTER_INDUCEMENTS_PURCHASED }, InducementDuration.UNTIL_USED,
  	"Ignore first armour break roll"),
  //  Description:
  //  	The player has acquired some lucky charms from a Halfling in a green
  //  	coat before the game.
  //  Timing:
  //		Play during the pre-game after all inducements are purchased.
  //  Effect:
  //  	A player of your choice may ignore the first time his armour is broken,
  //  	and just be Placed Prone. Any roll that ignores armour, such as the
  //  	crowd or throw a rock, is not affected by a lucky charm.
  
  MAGIC_GLOVES_OF_JARK_LONGARM(35, "Magic Gloves of Jark Longarm", "Magic Gloves", CardType.MAGIC_ITEM, CardTarget.OWN_PLAYER, false,
  	new InducementPhase[] { InducementPhase.END_OF_OWN_TURN, InducementPhase.AFTER_KICKOFF_TO_OPPONENT_RESOLVED }, InducementDuration.UNTIL_END_OF_DRIVE,
  	"Player gets Pass Block & +1 to interception"),
  //  Description:
  //		Your team is featured in Spike! magazine and the magazine gives you
  //  	these gloves for your upcoming game.
  //  Timing:
  //  	Play after your turn has ended or your kick-off to an opponent is
  //  	resolved, but before your opponents turn begins.
  //  Effect:
  //  	A player of your choice gains the Pass Block skill, and an additional +1
  //  	modifier to all interception rolls until the drive ends.
  
	GOOD_OLD_MAGIC_CODPIECE(36, "Good Old Magic Codpiece", "Magic Codpiece", CardType.MAGIC_ITEM, CardTarget.OWN_PLAYER, false,
  	new InducementPhase[] { InducementPhase.AFTER_INDUCEMENTS_PURCHASED }, InducementDuration.UNTIL_END_OF_GAME,
  	"Player cannot be fouled and no modifiers to injury rolls"),
  //  Description:
  //  	Mother always said "never play without your codpiece". After years of
  //  	being passed from one generation to the next, the magic is still
  //  	working.
  //  Timing:
  //		Play during the pre-game after all inducements are purchased.
  //  Effect:
  //  	A player of your choice may not be fouled for this game and injury rolls
  //  	against this player cannot be modified or re-rolled by anything
  //  	including (but not limited to) Dirty Player, Mighty Blow, Piling On, and
  //  	Stunty.
  
	RABBITS_FOOT(37, "Rabbit's Foot", "Rabbit's Foot", CardType.MAGIC_ITEM, CardTarget.OWN_PLAYER, false,
  	new InducementPhase[] { InducementPhase.START_OF_OWN_TURN }, InducementDuration.UNTIL_END_OF_GAME,
  	"Player gets Pro (not playable on a Loner)"),
  //  Description:
  //  	One player finds himself a lucky rabbit's foot after the pre-game meal
  //  	of, well, rabbit.
  //  Timing:
  //  	Play at the beginning of your turn before any player takes an Action.
  //  Effect:
  //  	A player of your choice without Loner gains the Pro skill for the
  //  	remainder of this game.
  
//  RING_OF_TELEPORTATION(38, "Ring of Teleportation", CardType.MAGIC_ITEM, CardTarget.OWN_PLAYER, false,
//  	new InducementPhase[] { InducementPhase.END_OF_TURN_NOT_HALF }, InducementDuration.INSTANTANEOUS),
  	
  //  Description:
  //  	Where'd he go? The player uses a teleportation ring to get out of a
  //  	tight spot.
  //  Timing:
  //		Play after your turn has ended (unless your turn ending would end the
  //  	half.)
  //  Effect:
  //  	One player on your team of your choice can be moved D6 squares in a
  //  	single direction of your choice (note: you must move the full D6
  //  	squares and must choose the direction before rolling the D6). Treat
  //  	this movement as if the player had been thrown with the Throw Team-
  //  	Mate skill but without the 3 scatters to determine the landing square.
  //  	The landing roll from the teleportation is automatically successful
  //  	unless he has bounced off another player.
  
  WAND_OF_SMASHING(39, "Wand of Smashing", "Wand of Smashing", CardType.MAGIC_ITEM, CardTarget.OWN_PLAYER, false,
  	new InducementPhase[] { InducementPhase.START_OF_OWN_TURN }, InducementDuration.UNTIL_END_OF_TURN,
  	"Player gets +1 ST & Mighty Blow"),
  //  Description:
  //  	Stick! Smash!
  //  Timing:
  //  	Play at the beginning of your turn before any player takes an Action.
  //  Effect:
  //  	Choose one player on your team. That player gains +1 strength and
  //  	the Mighty Blow skill for this turn.
  
  // ---------------
  // 13x Dirty Trick
  // ---------------
  
  BLATANT_FOUL(40, "Blatant Foul", "Blatant Foul", CardType.DIRTY_TRICK, CardTarget.TURN, false,
   	new InducementPhase[] { InducementPhase.START_OF_OWN_TURN }, InducementDuration.UNTIL_END_OF_TURN,
   	"Next foul breaks armour automatically"),
  //	Description:
  //  	A player on your team is determined to take out the opposition, no matter what.
  //	Timing:
 	//  	Play at the beginning of your turn before any player takes an Action.
  //	Effect:
  //  	The armour roll for your Foul Action this turn automatically succeeds
  //  	and is considered a non-doubles roll, however the injury roll for the
  //  	foul must be rolled as normal with the player sent off on doubles.
 
  CHOP_BLOCK(41, "Chop Block", "Chop Block", CardType.DIRTY_TRICK, CardTarget.OWN_PLAYER, false,
		new InducementPhase[] { InducementPhase.END_OF_OWN_TURN }, InducementDuration.UNTIL_END_OF_TURN,
		"Player drops prone and stuns an adjacent player"),
  //	Description:
  //  	A player throws a dirty block on the opponent.
  //	Timing:
  //  	Play after your turn has ended but before your opponents turn
  // 		begins. You may not play this card after a kick-off is resolved.
  //	Effect:
  // 		This card may only be played on one of your Standing players that
  //  	did not take an Action during your last turn. Your player is Placed
  //  	Prone and an opposing player in a square adjacent to him is now
  //  	considered Stunned.

  CUSTARD_PIE(42, "Custard Pie", "Custard Pie", CardType.DIRTY_TRICK, CardTarget.OPPOSING_PLAYER, false,
		new InducementPhase[] { InducementPhase.START_OF_OWN_TURN }, InducementDuration.UNTIL_END_OF_TURN,
		"Opponent distracted as per Hypnotic Gaze"),
  //	Description:
  //		One of your players thrusts a cleverly concealed custard pie in the
  //		face of an opposing player.
  //	Timing:
  //		Play at the beginning of your turn before any player takes an Action.
  //	Effect:
  //		Choose one player on the opposing team adjacent to one of your
  //		Standing or Prone players (not Stunned). That opposing player is so
  //		flabbergasted by the pie hit that he loses his tackle zones for the
  //		remainder of this turn as per a successful Hypnotic Gaze roll.

//  DISTRACT(43, "Distract", "Distract", CardType.DIRTY_TRICK, CardTarget.OWN_PLAYER, false,
//		new InducementPhase[] { InducementPhase.END_OF_OWN_TURN, InducementPhase.AFTER_KICKOFF_TO_OPPONENT_RESOLVED }, InducementDuration.UNTIL_END_OF_TURN,
//		"Player gets Disturbing Presence & opponents in 3 squares get Bone-head"),
  //	Description:
  //		Your player is very good at distracting all those around him.
  //	Timing:
  //		Play after your turn has ended or your kick-off to an opponent is
  //		resolved, but before your opponent's turn begins.
  //	Effect:
  //		The chosen player gains the skill Disturbing Presence for this turn
  //		and all opposing players starting their Action within 3 squares of the
  //		player count as having Bone-head (lost Tackle Zones from failed
  //		Bone-head rolls return at the end of this turn).
		
  GREASED_SHOES(44, "Greased Shoes", "Greased Shoes", CardType.DIRTY_TRICK, CardTarget.TURN, false,
		new InducementPhase[] { InducementPhase.END_OF_OWN_TURN, InducementPhase.AFTER_KICKOFF_TO_OPPONENT_RESOLVED }, InducementDuration.UNTIL_END_OF_OPPONENTS_TURN,
		"Opposing players need to roll 5+ to Go For It"),
  //	Description:
  //		The magic grease applied to your opponent's shoes has finally taken	effect.
  //	Timing:
  //		Play after your turn has ended or your kick-off to an opponent is
  //		resolved, but before your opponent's turn begins.
  //	Effect:
  //		This turn all opposing players need to roll a 5+ to Go For It instead of
  //		the normal 2+.
		
  GROMSKULLS_EXPLODING_RUNES(45, "Gromskull's Exploding Runes", "Exploding Runes", CardType.DIRTY_TRICK, CardTarget.OWN_PLAYER, false,
		new InducementPhase[] { InducementPhase.BEFORE_SETUP }, InducementDuration.UNTIL_END_OF_GAME,
		"Player gets Bombardier, No Hands, Secret Weapon & -1 to pass");
  //  Description:
  //  	A player purchased some exploding runes from a dwarven runesmith
  //  	before the game. Although they are illegal, they are highly effective.
  //  Timing:
  //  	Play before setting up for a drive.
  //  Effect:
  //  	Choose one player on your team. That player gains the Bombardier,
  //  	No Hands, and Secret Weapon skills for this game. Because the
  //  	Rune can be very volatile, any pass roll made with a Rune bomb is
  //  	performed with a -1 modifier to the pass roll.
  	
  //  ILLEGAL SUBSTITUTION
  //  Description:
  //  	A reserve sneaks onto the pitch while the ref is cleaning his glasses.
  //  Timing:
  //  	Play at the beginning of your turn before any player takes an Action.
  //  Effect:
  //  	You may place any player from the reserves box in an unoccupied
  //  	square in the end zone you are defending. This player may only take
  //  	a Move Action this turn. This may take your team to 12 players for
  //  	the remainder of the drive.
  	
  //  KICKING BOOTS
  //  Description:
  //  	These boots were made for stomping, and that is just what they will do!
  //	Timing:
  //		Play after all players have been set up for a kick-off, but before any
  //		kick-off result is rolled.
  //	Effect:
  //		A player of your choice on your team gains the Kick and Dirty Player
  //		skills and a -1 MA for the remainder of this game.
		
  //	PIT TRAP
  //  Description:
  //  	A devious groundskeeper has set up a pit trap for you.
  //  Timing:
  //    Play after your turn has ended or your kick-off to an opponent is
  //    resolved, but before your opponents turn begins.
  //  Effect:
  //  	Choose a player: that player is Placed Prone, no armour roll is made,
  //  	and if the player had the ball bounce it as normal.

  //  SPIKED BALL
  //  Description:
  //    A Bloodthirster is in the crowd today, so in honour of this event a
  //    spiked ball is swapped with the real ball. More blood for the blood god
  //    and the fans!
  //  Timing:
  //		Play after all players have been set up for a kick-off, but before any
  //  	kick-off result is rolled.
  //  Effect:
  //    Until the drive ends any failed pick up or catch roll (but not interception
  //    roll) is treated as the player being attacked with the Stab skill by an
  //    opponent.
    
  //  STOLEN PLAYBOOK
  //  Description:
  //  	You nabbed a playbook from the opponents coach! He sure will be
  //    surprised when you know exactly how to ruin his play.
  //  Timing:
  //    Play after your turn has ended or your kick-off to an opponent is
  //    resolved, but before your opponents turn begins.
  //  Effect:
  //    A player of your choice gains Pass Block and Shadowing until the drive
  //    ends.
    
  //  TRAMPOLINE TRAP
  //  Description:
  //  	Someone set up a deep pit trap...with a trampoline in it!
  //  Timing:
  //    Play after your turn has ended or your kick-off to an opponent is
  //    resolved, but before your opponents turn begins.
  //  Effect:
  //    Choose any opposing player. Using all the rules for the Throw Team-
  //    Mate skill, the player is automatically thrown (i.e. cannot be fumbled) to
  //    a target square that is D6 squares away in a random direction from his
  //    own square (use the scatter template). The player will need to make a
  //    landing roll as normal if they land on the pitch.
    
  //  WITCH'S BREW
  //  Description:
  //    You've spiked the opponents Kroxorade bottle with a witch's
  //    concoction!
  //  Timing:
  //		Play after all players have been set up for a kick-off, but before any
  //  	kick-off result is rolled.
  //  Effect:
  //    Choose an opponent and roll on this table.
  //    1- Woops! Mad Cap Mushroom potion! The player gains the Jump Up
  //    and No Hands skills until the drive ends.
  //    2- Snake Oil! Bad taste, but no effect.
  //    3-6 Sedative! The player gains the Really Stupid skill until the drive
  //    ends.
  
  // --------------
  // 26x Good Karma
  // --------------
  
  // ----------------
  // 18x Random Event
  // ----------------
  
  // --------------------
  // 8x Desperate Measure
  // --------------------
  
  private int fId;
  private String fName;
  private String fShortName;
  private CardType fType;
  private CardTarget fTarget;
  private boolean fRemainsInPlay;
  private InducementPhase[] fPhases;
  private InducementDuration fDuration;
  private String fDescription;
  
  private Card(int pValue, String pName, String pShortName, CardType pType, CardTarget pTarget, boolean pRemainsInPlay, InducementPhase[] pPhases, InducementDuration pDuration, String pDescription) {
    fId = pValue;
    fName = pName;
    fShortName = pShortName;
    fType = pType;
    fTarget = pTarget;
    fRemainsInPlay = pRemainsInPlay;
    fPhases = pPhases;
    fDuration = pDuration;
    fDescription = pDescription;
  }

  public int getId() {
    return fId;
  }
  
  public String getName() {
    return fName;
  }
  
  public String getShortName() {
	  return fShortName;
  }
  
  public CardType getType() {
	  return fType;
  }
  
  public CardTarget getTarget() {
	  return fTarget;
  }
  
  public boolean isRemainsInPlay() {
	  return fRemainsInPlay;
  }
    
  public InducementPhase[] getPhases() {
	  return fPhases;
  }
  
  public InducementDuration getDuration() {
	  return fDuration;
  }
  
  public String getDescription() {
	  return fDescription;
  }
  
  public String getHtmlDescription() {
  	StringBuilder description = new StringBuilder();
  	description.append(getDescription());
  	description.append("<br>");
  	description.append(getDuration().getDescription());
  	return description.toString();
  }
  
  public String getHtmlDescriptionWithPhases() {
  	StringBuilder description = new StringBuilder();
  	description.append(getHtmlDescription());
  	description.append("<br>");
  	description.append(new InducementPhaseFactory().getDescription(getPhases()));
  	return description.toString();
  }

  public static Comparator<Card> createComparator() {
  	return new Comparator<Card>() {
    	public int compare(Card pCard1, Card pCard2) {
    	  return pCard1.getName().compareTo(pCard2.getName());
    	}
  	};
  }
    
}
