package ua.kpi.comsys.collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class WordCounterTest {

    private final WordCounter counter = new WordCounter();

    // countWords: граничні випадки

    @Test
    void countWords_nullList_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> counter.countWords(null));
    }

    @Test
    void countWords_nullElement_throwsIllegalArgumentException() {
        List<String> lines = Arrays.asList("valid line", null);
        assertThrows(IllegalArgumentException.class, () -> counter.countWords(lines));
    }

    @Test
    void countWords_emptyList_returnsEmptyMap() {
        assertEquals(Map.of(), counter.countWords(List.of()));
    }

    @Test
    void countWords_blankAndPunctuationOnlyLines_ignoreEmptyWords() {
        assertEquals(Map.of(), counter.countWords(List.of("", "   ", "!!!", "\t\n")));
    }

    // countWords: правила обробки тексту

    @Test
    void countWords_countsOccurrences() {
        Map<String, Integer> result = counter.countWords(List.of("the cat and the hat"));
        assertEquals(Map.of("the", 2, "cat", 1, "and", 1, "hat", 1), result);
    }

    @Test
    void countWords_lowercasesWords() {
        assertEquals(Map.of("java", 3), counter.countWords(List.of("Java JAVA java")));
    }

    @Test
    void countWords_splitsOnPunctuationAndWhitespace() {
        Map<String, Integer> result = counter.countWords(List.of("Hello, world! hello-world.\tHELLO"));
        assertEquals(Map.of("hello", 3, "world", 2), result);
    }

    @Test
    void countWords_apostropheSplitsWord() {
        assertEquals(Map.of("don", 1, "t", 1), counter.countWords(List.of("don't")));
    }

    @Test
    void countWords_keepsDigitsAsPartOfWord() {
        assertEquals(Map.of("java", 1, "25", 2, "x2", 1), counter.countWords(List.of("Java 25 25 x2")));
    }

    @Test
    void countWords_nonLatinLettersAreDelimiters() {
        assertEquals(Map.of("world", 1, "caf", 1), counter.countWords(List.of("Привіт world café")));
    }

    @Test
    void countWords_countsAcrossLines() {
        Map<String, Integer> result = counter.countWords(List.of("one two", "two three", "three three"));
        assertEquals(Map.of("one", 1, "two", 2, "three", 3), result);
    }

    @Test
    void countWords_resultIsSortedAlphabetically() {
        List<String> keys = new ArrayList<>(counter.countWords(List.of("banana apple cherry")).keySet());
        assertEquals(List.of("apple", "banana", "cherry"), keys);
    }

    // getUniqueWords

    @Test
    void getUniqueWords_nullList_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> counter.getUniqueWords(null));
    }

    @Test
    void getUniqueWords_nullElement_throwsIllegalArgumentException() {
        List<String> lines = Arrays.asList(null, "text");
        assertThrows(IllegalArgumentException.class, () -> counter.getUniqueWords(lines));
    }

    @Test
    void getUniqueWords_emptyList_returnsEmptySet() {
        assertEquals(Set.of(), counter.getUniqueWords(List.of()));
    }

    @Test
    void getUniqueWords_removesDuplicatesAndLowercases() {
        Set<String> result = counter.getUniqueWords(List.of("Java java", "JAVA Kotlin"));
        assertEquals(Set.of("java", "kotlin"), result);
    }

    @Test
    void getUniqueWords_isSortedAlphabetically() {
        List<String> words = new ArrayList<>(counter.getUniqueWords(List.of("pear apple mango")));
        assertEquals(List.of("apple", "mango", "pear"), words);
    }
}
