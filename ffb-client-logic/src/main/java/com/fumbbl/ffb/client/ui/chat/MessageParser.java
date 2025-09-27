package com.fumbbl.ffb.client.ui.chat;

import com.fumbbl.ffb.client.Component;
import com.fumbbl.ffb.client.DimensionProvider;
import com.fumbbl.ffb.client.IconCache;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.util.StringTool;

import javax.swing.ImageIcon;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

/**
 * Parses a chat message into structured segments.
 *
 * Converts raw text into typed segments (text, emoji, mention),
 * resolving emoji paths and handling fallbacks when icons aren't found.
 *
 * @author Garcangel
 */
public final class MessageParser {

	private static final Pattern EMOJI_PATTERN = createEmojiPattern();

	private static Pattern createEmojiPattern() {
		String aliasRegex = EmojiLookup.getAliases().keySet().stream().map(Pattern::quote).collect(Collectors.joining("|"));
		String shortcodeRegex = ":[a-z0-9_]+:";
		return Pattern.compile(aliasRegex + "|" + shortcodeRegex);
	}

	public static List<ChatSegment> parse(String message, String coach, TextStyle style,IconCache iconCache,DimensionProvider dimensionProvider) {
		
		if (!StringTool.isProvided(message)) {
			return Collections.emptyList();
		}
		List<Match> matches = findMatches(message, coach);
		
		return buildSegments(message, matches, coach, style, iconCache, dimensionProvider);
	}

	private static List<Match> findMatches(String message, String coach) {
		List<Match> matches = new ArrayList<>();

		Matcher emojiMatcher = EMOJI_PATTERN.matcher(message);
		while (emojiMatcher.find()) {
			matches.add(new Match(emojiMatcher.group(), emojiMatcher.start(), emojiMatcher.end(), true));
		}

		if (StringTool.isProvided(coach)) {
			Pattern mentionPattern = Pattern.compile("@" + Pattern.quote(coach) + "(?![\\p{Alnum}_])");
			Matcher mentionMatcher = mentionPattern.matcher(message);
			while (mentionMatcher.find()) {
				matches.add(new Match(mentionMatcher.group(), mentionMatcher.start(), mentionMatcher.end(), false));
			}
		}

		matches.sort(Comparator.comparingInt(m -> m.start));
		return matches;
	}

	private static List<ChatSegment> buildSegments(String message, List<Match> matches, String coach,	TextStyle style, IconCache iconCache, DimensionProvider dimensionProvider) {
		
		boolean isEmojiOnly = isEmojiOnlyMessage(message);
		
		Component iconComponent = isEmojiOnly ? Component.CHAT_ICON_LARGE : Component.CHAT_ICON;
		
		int offset = dimensionProvider.dimension(iconComponent).height / 5;
		
		List<ChatSegment> segments = new ArrayList<>();
		int position = 0;
		
		for (Match match : matches) {

			if (match.start > position) {
				String text = message.substring(position, match.start);
				segments.add(new ChatSegment(style, text));
			}
			
			if (match.isEmoji) {
				if (EmojiLookup.isEmoji(match.text)) {
					String path = EmojiLookup.getPath(match.text);
					ImageIcon icon = iconCache.getEmojiIcon(path, iconComponent, dimensionProvider);
					segments.add(new ChatSegment(icon, offset));
				} else {
					segments.add(new ChatSegment(style, match.text));
				}
			} else {
				segments.add(new ChatSegment(TextStyle.MENTION, match.text));
			}
			
			position = match.end;
		}
		
		if (position < message.length()) {
			String text = message.substring(position);
			segments.add(new ChatSegment(style, text));
		}
		
		return segments;
	}

	private static boolean isEmojiOnlyMessage(String message) {
		String withoutEmojis = EMOJI_PATTERN.matcher(message).replaceAll("");
		return withoutEmojis.trim().isEmpty();
	}

	private static class Match {
		final String text;
		final int start;
		final int end;
		final boolean isEmoji;
		
		Match(String text, int start, int end, boolean isEmoji) {
			this.text = text;
			this.start = start;
			this.end = end;
			this.isEmoji = isEmoji;
		}
	}
}