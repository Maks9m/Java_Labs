package ua.kpi.comsys.collections;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordCounter {

    private static final Pattern WORD_PATTERN = Pattern.compile("[a-zA-Z0-9]+");

    public Map<String, Integer> countWords(List<String> textLines) {
        Map<String, Integer> counts = new TreeMap<>();
        for (String word : extractWords(textLines)) {
            counts.merge(word, 1, Integer::sum);
        }
        return counts;
    }

    public Set<String> getUniqueWords(List<String> textLines) {
        return new TreeSet<>(extractWords(textLines));
    }

    private static List<String> extractWords(List<String> textLines) {
        if (textLines == null) {
            throw new IllegalArgumentException("textLines must not be null");
        }
        List<String> words = new ArrayList<>();
        for (String line : textLines) {
            if (line == null) {
                throw new IllegalArgumentException("textLines must not contain null elements");
            }
            Matcher matcher = WORD_PATTERN.matcher(line.toLowerCase(Locale.ROOT));
            while (matcher.find()) {
                words.add(matcher.group());
            }
        }
        return words;
    }
}
