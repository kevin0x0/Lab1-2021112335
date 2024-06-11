import java.io.IOException;
import java.util.*;

import org.example.Main;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class MainWhiteBoxTest {
    static HashMap<String, Integer> words = new HashMap<>();
    static HashMap<Integer, String> posToWord = new HashMap<>();
    static int [][] graph;

    @BeforeClass
    public static void initResource() throws IOException {
        ArrayList<String> list = new ArrayList<>();
        Main.getFileContent("test.txt", words, posToWord, list);
        graph = Main.generateGraph(words, list.toArray(new String[0]));
    }

    @Test
    public void queryBridgeWordsTest1() {
        List<String> result = Main.queryBridgeWords("monkey", "pig", graph, words, posToWord);
        assertNull(result);
    }

    @Test
    public void queryBridgeWordsTest2() {
        List<String> result = Main.queryBridgeWords("Like", "pig", graph, words, posToWord);
        assertNull(result);
    }

    @Test
    public void queryBridgeWordsTest3() {
        List<String> result = Main.queryBridgeWords("help", "us", graph, words, posToWord);
        assertEquals(new ArrayList<String>(), result);
    }

    @Test
    public void queryBridgeWordsTest4() {
        List<String> result = Main.queryBridgeWords("together", "than", graph, words, posToWord);
        assertEquals(new ArrayList<String>(), result);
    }

    @Test
    public void queryBridgeWordsTest5() {
        List<String> result = Main.queryBridgeWords("than", "together", graph, words, posToWord);
        assertEquals(new ArrayList<String>(), result);
    }

    @Test
    public void queryBridgeWordsTest6() {
        List<String> result = Main.queryBridgeWords("Better", "than", graph, words, posToWord);
        assertEquals(Collections.singletonList("late"), result);
    }
}
