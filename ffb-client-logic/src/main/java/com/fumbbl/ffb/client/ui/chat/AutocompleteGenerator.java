package com.fumbbl.ffb.client.ui.chat;

import com.fumbbl.ffb.TalkConstants;
import com.fumbbl.ffb.client.FantasyFootballClient;
import com.fumbbl.ffb.client.util.UtilClientChat;

import javax.swing.text.JTextComponent;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Provides the parsing and suggestion logic for chat autocomplete.
 *
 * Responsible for:
 * - Extracting the current token at the caret
 * - Computing possible completions (emoji, mentions, commands)
 * - Applying a selected completion back into the input field
 *
 * @author Garcangel
 */
public class AutocompleteGenerator {

  private final FantasyFootballClient client;

  private static final int MAX_SUGGESTIONS = 12;

  public AutocompleteGenerator(FantasyFootballClient client) {
    this.client = client;
  }

  // Note: call toSearchTerm() inside each case.
  // Currently all triggers use the same rule, but kept local
  // in case different token -> search term rules are needed later.
  public List<String> getSuggestions(String input, int caretPosition) {
    String token = extractToken(input, caretPosition);
    if (token == null) {
      return Collections.emptyList();
    }

    char trigger = token.charAt(0);
    switch (trigger) {
      case ':':
        if (EmojiLookup.isAlias(token)) {
          return Collections.emptyList();
        }
        return filterCandidates(EmojiLookup.getShortcodes(), token, toSearchTerm(token));

      case '@':
        Collection<String> spectators = client.getClientData().getSpectators().stream()
          .map(name -> "@" + name)
          .collect(Collectors.toList());
        return filterCandidates(spectators, token, toSearchTerm(token));

      case '/':
        return filterCandidates(TalkConstants.EMOTES, token, toSearchTerm(token));

      default:
        return Collections.emptyList();
    }
  }

  public void applySelection(JTextComponent input, String selected) {
    String text = input.getText();
    int caret = input.getCaretPosition();
    String token = extractToken(text, caret);
    if (token == null) {
      return;
    }
    int start = caret - token.length();
    UtilClientChat.applyInsertion(input, start, caret, selected);
  }

  private List<String> filterCandidates(Collection<String> candidates, String token, String searchTerm) {
    List<String> results = new ArrayList<>();
    for (String candidate : candidates) {
      if (candidate.equals(token)) {
        return Collections.emptyList();
      }
      if (candidate.toLowerCase(Locale.ROOT).contains(searchTerm)) {
        results.add(candidate);
        if (results.size() >= MAX_SUGGESTIONS) {
          break;
        }
      }
    }
    return results;
  }

  private String toSearchTerm(String token) {
    if (token.length() > 2) {
      return token.substring(1).toLowerCase(Locale.ROOT);
    }
    return token.toLowerCase(Locale.ROOT);
  }

  private String extractToken(String input, int caretPosition) {
    if (caretPosition == 0) {
      return null;
    }

    for (int index = caretPosition - 1; index >= 0; index--) {
      char character = input.charAt(index);

      if (Character.isWhitespace(character)) {
        break;
      }

      switch (character) {
        case ':':
          String token = input.substring(index, caretPosition);
          return token.length() > 1 ? token : null;
        case '@':
          return input.substring(index, caretPosition);
        case '/':
          if (index == 0) {
            return input.substring(index, caretPosition);
          }
          break;
        default:
      }
    }
    return null;
  }
}
