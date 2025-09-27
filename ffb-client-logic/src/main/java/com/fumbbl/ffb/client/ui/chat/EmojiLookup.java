package com.fumbbl.ffb.client.ui.chat;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Central lookup for supported emojis in chat.
 *
 * Maps emoji shortcodes (and aliases) to resource paths. Used by the
 * parser, autocomplete, and picker to resolve icons for display.
 *
 * @author Garcangel
 */
public final class EmojiLookup {

  private static final Map<String, String> SHORTCODE_TO_PATH;
  private static final Map<String, String> ALIASES;
  private static final String BASE = "/icons/emoji/";

  static {
    Map<String, String> map = new LinkedHashMap<>();

    // FUMBBL 
    map.put(":pow:", BASE + "pow.png");
    map.put(":powpush:", BASE + "powpush.png");
    map.put(":push:", BASE + "push.png");
    map.put(":bothdown:", BASE + "bothdown.png");
    map.put(":skull:", BASE + "skull.png");

    // Core Smiles
    map.put(":grinning:", BASE + "emoji_u1f600.png");
    map.put(":smile:", BASE + "emoji_u1f604.png");
    map.put(":grin:", BASE + "emoji_u1f601.png");
    map.put(":joy:", BASE + "emoji_u1f602.png");
    map.put(":rofl:", BASE + "emoji_u1f923.png");
    map.put(":wink:", BASE + "emoji_u1f609.png");
    map.put(":innocent:", BASE + "emoji_u1f607.png");
    map.put(":slight_smile:", BASE + "emoji_u1f642.png");
    map.put(":slight_frown:", BASE + "emoji_u1f641.png");
    map.put(":stuck_out_tongue:", BASE + "emoji_u1f61b.png");
    map.put(":kissing_heart:", BASE + "emoji_u1f618.png");
    map.put(":flushed:", BASE + "emoji_u1f633.png");
    map.put(":head_bandage:", BASE + "emoji_u1f915.png");
    map.put(":lying_face:", BASE + "emoji_u1f925.png");
    
    
    // Reactions / Mood
    map.put(":relaxed:", BASE + "emoji_u263a.png");
    map.put(":rolling_eyes:", BASE + "emoji_u1f644.png");
    map.put(":sob:", BASE + "emoji_u1f62d.png");
    map.put(":cry:", BASE + "emoji_u1f622.png");
    map.put(":scream:", BASE + "emoji_u1f631.png");
    map.put(":rage:", BASE + "emoji_u1f621.png");
    map.put(":face_with_symbols_over_mouth:", BASE + "emoji_u1f92c.png");
    map.put(":zipper_mouth:", BASE + "emoji_u1f910.png");
    map.put(":thinking:", BASE + "emoji_u1f914.png");
    map.put(":face_with_peeking_eye:", BASE + "emoji_u1fae3.png");
    map.put(":shhh:", BASE + "emoji_u1f92b.png");
    map.put(":yawning:", BASE + "emoji_u1f971.png");
    map.put(":saluting:", BASE + "emoji_u1fae1.png");
    map.put(":hand_over_mouth:", BASE + "emoji_u1fae2.png");
    map.put(":face_vomiting:", BASE + "emoji_u1f92e.png");
    map.put(":sick:", BASE + "emoji_u1f922.png");
    map.put(":pleading_face:", BASE + "emoji_u1f97a.png");
    map.put(":raised_eyebrow:", BASE + "emoji_u1f928.png");
    map.put(":open_mouth:", BASE + "emoji_u1f62f.png");
    map.put(":heart_eyes:", BASE + "emoji_u1f60d.png");
    map.put(":star_struck:", BASE + "emoji_u1f929.png");

    // Fun Faces
    map.put(":sunglasses:", BASE + "emoji_u1f60e.png");
    map.put(":face_with_monocle:", BASE + "emoji_u1f9d0.png");
    map.put(":exploding_head:", BASE + "emoji_u1f92f.png");
    map.put(":partying_face:", BASE + "emoji_u1f973.png");
    map.put(":clown:", BASE + "emoji_u1f921.png");
    map.put(":devil:", BASE + "emoji_u1f608.png");

    // Symbols
    map.put(":heart:", BASE + "emoji_u2764.png");
    map.put(":broken_heart:", BASE + "emoji_u1f494.png");
    map.put(":eyes:", BASE + "emoji_u1f440.png");
    map.put(":fire:", BASE + "emoji_u1f525.png");
    map.put(":snowflake:", BASE + "emoji_u2744.png");
    map.put(":tada:", BASE + "emoji_u1f389.png");
    map.put(":poop:", BASE + "emoji_u1f4a9.png");
    map.put(":100:", BASE + "emoji_u1f4af.png");
    map.put(":question:", BASE + "emoji_u2753.png");
    map.put(":exclamation:", BASE + "emoji_u2757.png");
    map.put(":skull_crossbones:", BASE + "emoji_u2620.png");
    map.put(":ghost:", BASE + "emoji_u1f47b.png");
    map.put(":clover:", BASE + "emoji_u1f340.png");

    // Objects
    map.put(":medal:", BASE + "emoji_u1f3c5.png");
    map.put(":trophy:", BASE + "emoji_u1f3c6.png");
    map.put(":ball:", BASE + "emoji_u1f3c8.png");
    map.put(":bomb:", BASE + "emoji_u1f4a3.png");
    map.put(":hourglass:", BASE + "emoji_u231b.png");
    map.put(":die:", BASE + "emoji_u1f3b2.png");
    map.put(":popcorn:", BASE + "emoji_u1f37f.png");
    map.put(":beers:", BASE + "emoji_u1f37b.png");
    map.put(":whisky:", BASE + "emoji_u1f943.png");
    map.put(":wine:", BASE + "emoji_u1f377.png");

    // Gestures
    map.put(":wave:", BASE + "emoji_u1f44b.png");
    map.put(":clap:", BASE + "emoji_u1f44f.png");
    map.put(":you:", BASE + "emoji_u1faf5.png");
    map.put(":muscle:", BASE + "emoji_u1f4aa.png");
    map.put(":thumbsup:", BASE + "emoji_u1f44d.png");
    map.put(":thumbsdown:", BASE + "emoji_u1f44e.png");
    map.put(":fingers_crossed:", BASE + "emoji_u1f91e.png");
    map.put(":ok_hand:", BASE + "emoji_u1f44c.png");
    map.put(":vulcan:", BASE + "emoji_u1f596.png");
    map.put(":point_up:", BASE + "emoji_u261d.png");
    map.put(":victory:", BASE + "emoji_u270c.png");

    // Animals
    map.put(":speak_no_evil:", BASE + "emoji_u1f64a.png");
    map.put(":see_no_evil:", BASE + "emoji_u1f648.png");
    map.put(":hear_no_evil:", BASE + "emoji_u1f649.png");
    map.put(":sheep:", BASE + "emoji_u1f40f.png");
    map.put(":chicken:", BASE + "emoji_u1f414.png");
    map.put(":frog:", BASE + "emoji_u1f438.png");
    map.put(":bug:", BASE + "emoji_u1f41e.png");


    SHORTCODE_TO_PATH = Collections.unmodifiableMap(map);

    Map<String, String> aliases = new LinkedHashMap<>();
    aliases.put(":)", ":slight_smile:");
    aliases.put(":-)", ":slight_smile:");

    aliases.put(":(", ":slight_frown:");
    aliases.put(":-(", ":slight_frown:");

    aliases.put(":D", ":smile:");
    aliases.put(":-D", ":smile:");

    aliases.put(":'(", ":cry:");

    aliases.put(":-P", ":stuck_out_tongue:");
    aliases.put(":P", ":stuck_out_tongue:");
    
    aliases.put(">:(", ":rage:");     
    
    aliases.put(";)", ":wink:"); 
    aliases.put(";-)", ":wink:"); 

    ALIASES = Collections.unmodifiableMap(aliases);
  }

  public static final String PICKER_GREY  = BASE + "pickerButton/pickerButton_grey.png";
  public static final String PICKER_COLOR = BASE + "pickerButton/pickerButton_color.png";

  private EmojiLookup() {}

  public static String getPath(String token) {
    String canonical = token;
    if (ALIASES.containsKey(token)) {
      canonical = ALIASES.get(token);
    }
    return SHORTCODE_TO_PATH.get(canonical);
  }

  public static boolean isEmoji(String token) {
    return getPath(token) != null;
  }

  public static java.util.Set<String> getShortcodes() {
    return SHORTCODE_TO_PATH.keySet();
  }

  public static boolean isAlias(String token) {
    return ALIASES.containsKey(token);
  }

  public static Map<String, String> getAliases() {
    return ALIASES;
  }
}
