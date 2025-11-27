
package org.eclipse.tm4e.languageconfiguration.internal.model;

import static org.eclipse.tm4e.core.internal.utils.NullSafetyHelper.castNullable;
import static org.eclipse.tm4e.core.internal.utils.NullSafetyHelper.lazyNonNull;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tm4e.languageconfiguration.internal.model.EnterAction.IndentAction;
import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import io.github.rosemoe.sora.util.Logger;

public class LanguageConfiguration {
    private static final Logger log = Logger.instance(LanguageConfiguration.class.getName());
    private static String removeTrailingCommas(String jsonString) {
        return jsonString.replaceAll("(,)(\\s*\\n(\\s*\\/\\/.*\\n)*\\s*[\\]}])", "$2");
    }
    @NonNullByDefault({})
    public static @Nullable LanguageConfiguration load(@NonNull final Reader reader) {
        final var jsonString = removeTrailingCommas(new BufferedReader(reader).lines().collect(Collectors.joining("\n")));
        final LanguageConfiguration langCfg = new GsonBuilder()
                .registerTypeAdapter(String.class, (JsonDeserializer<String>) (json, typeOfT, context) -> {
                    if (json.isJsonObject()) {
                        final var jsonObj = json.getAsJsonObject();
                        return jsonObj.has("pattern") && jsonObj.get("pattern").isJsonPrimitive() 
                                ? jsonObj.get("pattern").getAsString()
                                : null;
                    }
                    return json.getAsString();
                })
                .registerTypeAdapter(OnEnterRule.class, (JsonDeserializer<OnEnterRule>) (json, typeOfT, context) -> {
                    if (!json.isJsonObject()) {
                        return null;
                    }
                    final var jsonObj = json.getAsJsonObject();
                    final var beforeText = getAsPattern(jsonObj.get("beforeText")); 
                    if (beforeText == null) {
                        return null;
                    }
                    final var actionElem = jsonObj.get("action"); 
                    if (actionElem != null && actionElem.isJsonObject()) {
                        final var actionJsonObj = actionElem.getAsJsonObject();
                        final var indentActionString = getAsString(actionJsonObj.get("indent")); 
                        if (indentActionString != null) {
                            final var afterText = getAsPattern(jsonObj.get("afterText")); 
                            final var previousLineText = getAsPattern(jsonObj.get("previousLineText")); 
                            final var indentAction = IndentAction.get(indentActionString);
                            final var appendText = getAsString(actionJsonObj.get("appendText")); 
                            final var removeText = getAsInteger(actionJsonObj.get("removeText")); 
                            final var action = new EnterAction(indentAction, appendText, removeText);
                            return new OnEnterRule(beforeText, afterText, previousLineText, action);
                        }
                    }
                    return null;
                })
                .registerTypeAdapter(CommentRule.class, (JsonDeserializer<CommentRule>) (json, typeOfT, context) -> {
                    if (!json.isJsonObject()) {
                        return null;
                    }
                    final var jsonObj = json.getAsJsonObject();
                    final var lineComment = getAsString(jsonObj.get("lineComment")); 
                    final var blockCommentElem = jsonObj.get("blockComment"); 
                    CharacterPair blockComment = null;
                    if (blockCommentElem != null && blockCommentElem.isJsonArray()) {
                        final var blockCommentArray = blockCommentElem.getAsJsonArray();
                        if (blockCommentArray.size() == 2) {
                            final var blockCommentStart = getAsString(blockCommentArray.get(0));
                            final var blockCommentEnd = getAsString(blockCommentArray.get(1));
                            if (blockCommentStart != null && blockCommentEnd != null) {
                                blockComment = new CharacterPair(blockCommentStart, blockCommentEnd);
                            }
                        }
                    }
                    return lineComment == null && blockComment == null
                            ? null
                            : new CommentRule(lineComment, blockComment);
                })
                .registerTypeAdapter(CharacterPair.class, (JsonDeserializer<CharacterPair>) (json, typeOfT,
                                                                                             context) -> {
                    if (!json.isJsonArray()) {
                        return null;
                    }
                    final var charsPair = json.getAsJsonArray();
                    if (charsPair.size() != 2) {
                        return null;
                    }
                    final var open = getAsString(charsPair.get(0));
                    final var close = getAsString(charsPair.get(1));
                    return open == null || close == null
                            ? null
                            : new CharacterPair(open, close);
                })
                .registerTypeAdapter(AutoClosingPair.class, (JsonDeserializer<AutoClosingPair>) (json, typeOfT,
                                                                                                 context) -> {
                    String open = null;
                    String close = null;
                    if (json.isJsonArray()) {
                        final var charsPair = json.getAsJsonArray();
                        if (charsPair.size() != 2) {
                            return null;
                        }
                        open = getAsString(charsPair.get(0));
                        close = getAsString(charsPair.get(1));
                    } else if (json.isJsonObject()) {
                        final var autoClosePair = json.getAsJsonObject();
                        open = getAsString(autoClosePair.get("open")); 
                        close = getAsString(autoClosePair.get("close")); 
                    }
                    return open == null || close == null
                            ? null
                            : new AutoClosingPair(open, close);
                })
                .registerTypeAdapter(AutoClosingPairConditional.class, (JsonDeserializer<AutoClosingPairConditional>) (
                        json, typeOfT, context) -> {
                    final var notInList = new ArrayList<String>(2);
                    String open = null;
                    String close = null;
                    if (json.isJsonArray()) {
                        final var charsPair = json.getAsJsonArray();
                        if (charsPair.size() != 2) {
                            return null;
                        }
                        open = getAsString(charsPair.get(0));
                        close = getAsString(charsPair.get(1));
                    } else if (json.isJsonObject()) {
                        final var autoClosePair = json.getAsJsonObject();
                        open = getAsString(autoClosePair.get("open")); 
                        close = getAsString(autoClosePair.get("close")); 
                        final var notInElem = autoClosePair.get("notIn"); 
                        if (notInElem != null && notInElem.isJsonArray()) {
                            for (final JsonElement elem : notInElem.getAsJsonArray()) {
                                final var string = getAsString(elem);
                                if (string != null) {
                                    notInList.add(string);
                                }
                            }
                        }
                    }
                    return open == null || close == null
                            ? null
                            : new AutoClosingPairConditional(open, close, notInList);
                })
                .registerTypeAdapter(FoldingRules.class, (JsonDeserializer<FoldingRules>) (json, typeOfT, context) -> {
                    if (!json.isJsonObject()) {
                        return null;
                    }
                    final var jsonObj = json.getAsJsonObject();
                    final var markersElem = jsonObj.get("markers"); 
                    if (markersElem != null && markersElem.isJsonObject()) {
                        final var markersObj = markersElem.getAsJsonObject();
                        final var startMarker = getAsPattern(markersObj.get("start")); 
                        final var endMarker = getAsPattern(markersObj.get("end")); 
                        if (startMarker != null && endMarker != null) {
                            final var offSide = getAsBoolean(jsonObj.get("offSide"), false); 
                            return new FoldingRules(offSide, startMarker, endMarker);
                        }
                    }
                    return null;
                })
                .registerTypeAdapter(IndentationRules.class, (JsonDeserializer<IndentationRules>) (json, typeT, context) -> {
                    if (!json.isJsonObject()) {
                        return null;
                    }
                    final var jsonObj = json.getAsJsonObject();
                    final var decreaseIndentPattern = getAsPattern(jsonObj.get("decreaseIndentPattern"));
                    if (decreaseIndentPattern == null)
                        return null;
                    final var increaseIndentPattern = getAsPattern(jsonObj.get("increaseIndentPattern"));
                    if (increaseIndentPattern == null)
                        return null;
                    return new IndentationRules(
                            decreaseIndentPattern,
                            increaseIndentPattern,
                            getAsPattern(jsonObj.get("indentNextLinePattern")),
                            getAsPattern(jsonObj.get("unIndentedLinePattern")));
                })
                .create()
                .fromJson(jsonString, LanguageConfiguration.class);
        if (castNullable(langCfg.autoClosingPairs) == null) {
            langCfg.autoClosingPairs = Collections.emptyList();
        } else {
            langCfg.autoClosingPairs.removeIf(t -> t == null);
        }
        if (castNullable(langCfg.brackets) == null) {
            langCfg.brackets = Collections.emptyList();
        } else {
            langCfg.brackets.removeIf(t -> t == null);
        }
        if (castNullable(langCfg.onEnterRules) == null) {
            langCfg.onEnterRules = Collections.emptyList();
        } else {
            langCfg.onEnterRules.removeIf(t -> t == null);
        }
        if (castNullable(langCfg.surroundingPairs) == null) {
            langCfg.surroundingPairs = Collections.emptyList();
        } else {
            langCfg.surroundingPairs.removeIf(t -> t == null);
        }
        if (castNullable(langCfg.colorizedBracketPairs) == null) {
            langCfg.colorizedBracketPairs = Collections.emptyList();
        } else {
            langCfg.colorizedBracketPairs.removeIf(t -> t == null);
        }
        return langCfg;
    }
    private static @Nullable RegExPattern getAsPattern(@Nullable final JsonElement element) {
        if (element == null) {
            return null;
        }
        if (element.isJsonObject()) {
            final var pattern = getAsString(((JsonObject) element).get("pattern"));
            if (pattern == null) {
                return null;
            }
            final var flags = getAsString(((JsonObject) element).get("flags"));
            return RegExPattern.of(pattern, flags);
        }
        return RegExPattern.ofNullable(getAsString(element), null);
    }
    private static @Nullable String getAsString(@Nullable final JsonElement element) {
        if (element != null)
            try {
                return element.getAsString();
            } catch (final Exception ex) {
                log.e("Failed to convert JSON element [" + element + "] to String.", ex);
            }
        return null;
    }
    private static boolean getAsBoolean(@Nullable final JsonElement element, final boolean defaultValue) {
        if (element != null)
            try {
                return element.getAsBoolean();
            } catch (final Exception ex) {
                log.e("Failed to convert JSON element [" + element + "] to boolean.", ex);
            }
        return defaultValue;
    }
    private static @Nullable Integer getAsInteger(@Nullable final JsonElement element) {
        if (element != null)
            try {
                return element.getAsInt();
            } catch (final Exception ex) {
                log.e("Failed to convert JSON element [" + element + "] to Integer.", ex);
            }
        return null;
    }
    private @Nullable CommentRule comments;
    public @Nullable CommentRule getComments() {
        return comments;
    }
    private List<CharacterPair> brackets = lazyNonNull();
    public List<CharacterPair> getBrackets() {
        return brackets;
    }
    private @Nullable String wordPattern;
    public @Nullable String getWordPattern() {
        return wordPattern;
    }
    private @Nullable IndentationRules indentationRules;
    public @Nullable IndentationRules getIndentationRules() {
        return indentationRules;
    }
    private List<OnEnterRule> onEnterRules = lazyNonNull();
    public List<OnEnterRule> getOnEnterRules() {
        return onEnterRules;
    }
    private List<AutoClosingPairConditional> autoClosingPairs = lazyNonNull();
    public List<AutoClosingPairConditional> getAutoClosingPairs() {
        return autoClosingPairs;
    }
    private List<AutoClosingPair> surroundingPairs = lazyNonNull();
    public List<AutoClosingPair> getSurroundingPairs() {
        return surroundingPairs;
    }
    private List<CharacterPair> colorizedBracketPairs = lazyNonNull();
    public List<CharacterPair> getColorizedBracketPairs() {
        return colorizedBracketPairs;
    }
    private @Nullable String autoCloseBefore;
    public @Nullable String getAutoCloseBefore() {
        return autoCloseBefore;
    }
    private @Nullable FoldingRules folding;
    public @Nullable FoldingRules getFolding() {
        return folding;
    }
}
