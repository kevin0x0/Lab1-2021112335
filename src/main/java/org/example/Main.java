package org.example;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;


// this is a comment

/**
 * Main class, read content from file, then ask what to do.
 * user can
 * 1. query bridge words.
 * 2. random walk.
 * 3. generate new text given input text.
 * 4. calculate the shortest path between two words.
 */
public class Main {

  public static boolean isLetter(char x) {
    return (x >= 'a' && x <= 'z') || (x >= 'A' && x <= 'Z');
  }

  public static boolean isSplit(char x) {
    return x == ' ' || x == ',' || x == '，' || x == ';' || x == ':';
  }

  /**
   * query bridge words.
   *
   * @param word1 first word to query.
   * @param word2 second word to query.
   * @param graph graph.
   * @param words hashmap from words to indices.
   * @param posToWord hashmap from indices to words.
   * @return a list of bridge words, if first or second words are not exist, return null.
   */
  public static ArrayList<String> queryBridgeWords(String word1, String word2, int[][] graph,
                                                   HashMap<String, Integer> words,
                                                   HashMap<Integer, String> posToWord) {
    if (!words.containsKey(word1) || !words.containsKey(word2)) {
      return null;
    }
    int p1 = words.get(word1);
    int p2 = words.get(word2);
    //存放桥接单词
    ArrayList<String> list = new ArrayList<>();
    //找到p1的邻接点
    for (int i = 0; i < graph.length; i++) {
      if (graph[p1][i] == 1 && graph[i][p2] == 1) {
        list.add(posToWord.get(i));
      }
    }
    return list;
  }

  /**
   * check whether the 'filePath' is safe.
   *
   * @param filePath to be checked file path.
   * @return Path the safe path.
   * @throws Exception if 'filePath' is not safe, throw an exception.
   */
  @SuppressFBWarnings("PATH_TRAVERSAL_IN")
  public static Path validatePath(String filePath) throws Exception {
    Path path = Paths.get(filePath);
    if (!Files.isRegularFile(path) || !Files.isReadable(path)) {
      throw new Exception(filePath + " is not a valid path");
    }
    return path;
  }

  /**
   * generate graph given mapping from word to its index in 's' and words array 's'.
   *
   * @param words hashmap from words to indices.
   * @param s words array.
   * @return graph.
   */
  public static int[][] generateGraph(HashMap<String, Integer> words, String[] s) {
    int n = words.size();
    int[][] graph = new int[n][n];
    for (int i = 0; i < s.length - 1; i++) {
      graph[words.get(s[i])][words.get(s[i + 1])] = 1;
    }
    return graph;
  }

  /**
   * print graph.
   *
   * @param graph graph.
   * @param map hashmap from indices to words.
   */
  public static void showDirectedGraph(int[][] graph, HashMap<Integer, String> map) {
    for (int i = 0; i < graph.length; i++) {
      System.out.print(map.get(i));
      System.out.print(" -> ");
      for (int j = 0; j < graph[i].length; j++) {
        if (graph[i][j] == 1) {
          System.out.print(map.get(j));
          System.out.print(", ");
        }
      }
      System.out.println();
    }
  }

  /**
   * query bridge words and convert result to a string.
   *
   * @param word1 first word to query.
   * @param word2 second word to query.
   * @param graph graph.
   * @param words hashmap from words to indices.
   * @param posToWord hashmap from indices to words.
   * @return  a text representation of bridge words.
   */
  public static String queryBridgeWordsToString(String word1, String word2,
                                                int[][] graph, HashMap<String, Integer> words,
                                                HashMap<Integer, String> posToWord) {
    List<String> bridgeWords = queryBridgeWords(word1, word2, graph, words, posToWord);
    if (bridgeWords == null) {
      return "No " + word1 + " or " + word2 + " in the graph!";
    } else if (!bridgeWords.isEmpty()) {
      return "The bridge words from " + word1 + " to " + word2 + " are: " + bridgeWords;
    } else {
      return "No bridge words from " + word1 + " to " +  word2 + "!";
    }
  }

  /**
   * calculate the shortest path from word1 to word2.
   *
   * @param word1 the 'from' word.
   * @param word2 the 'to' word.
   * @param graph graph.
   * @param words hashmap from words to indices.
   * @param posToWord hashmap from indices to words.
   * @return the string representation of the shortest path.
   */
  public static String calcShortestPath(String word1, String word2, int[][] graph,
                                        HashMap<String, Integer> words,
                                        HashMap<Integer, String> posToWord) {
    //使用bfs寻找最短路径
    ArrayDeque<Integer> queue = new ArrayDeque<>();
    int start = words.get(word1);
    int end = words.get(word2);
    int pre = start;
    queue.addLast(start);
    HashSet<Integer> visited = new HashSet<>(); //存放遍历过的结点
    visited.add(pre);
    HashMap<Integer, Integer> way = new HashMap<>();  //存放bfs过程中的路径
    boolean ok = false;
    while (!queue.isEmpty()) {
      pre = queue.pollFirst();
      for (int i = 0; i < graph.length; i++) {
        if (!visited.contains(i) && graph[pre][i] == 1) {
          visited.add(i);
          way.put(i, pre);
          queue.add(i);
          if (i == end) {
            ok = true;
            break;
          }
        }
      }
      if (ok) {
        break;
      }
    }
    if (ok) {
      //从end开始倒序找路径
      ArrayList<Integer> l = new ArrayList<>();
      l.add(end);
      int pos = end;
      while (pos != start) {
        pos = way.get(pos);
        l.add(pos);
      }
      StringBuilder res = new StringBuilder();
      for (int i = l.size() - 1; i >= 0; i--) {
        res.append(posToWord.get(l.get(i)));
        res.append("->");
      }
      return res.substring(0, res.length() - 2);


    } else {
      return "不可达";
    }
  }

  /**
   * do the random walk.
   *
   * @param graph graph.
   * @param posToWord hashmap from indices to words.
   * @return the string representation of the path walked through.
   */
  public static String randomWalk(int[][] graph, HashMap<Integer, String> posToWord) {
    // 创建Random对象
    Random random = new SecureRandom();
    StringBuilder res = new StringBuilder();
    //随机选择起点
    int pos = random.nextInt(graph.length);
    res.append(posToWord.get(pos));
    res.append(" ");
    while (true) {
      int cnt = 0;
      ArrayList<Integer> possible = new ArrayList<>();  //存放可能的下一个结点
      for (int i = 0; i < graph.length; i++) {
        if (graph[pos][i] == 1) {
          possible.add(i);
          cnt++;
        }
      }
      if (cnt == 0) {
        break;
      }

      //随机选一个结点
      int nextPos = random.nextInt(cnt);
      int next = possible.get(nextPos);
      graph[pos][next] = 2;
      pos = next;
      res.append(posToWord.get(pos));
      res.append(" ");
    }
    for (int i = 0; i < graph.length; i++) {
      for (int j = 0; j < graph[i].length; j++) {
        graph[i][j] = graph[i][j] == 2 ? 1 : graph[i][j];
      }
    }
    return res.toString();
  }

  /**
   * generate new text by bridge words.
   *
   * @param inputText an input text.
   * @param graph graph.
   * @param words hashmap from words to indices.
   * @param posToWord hashmap from indices to words.
   * @return generated text.
   */
  public static String generateNewText(String inputText, int[][] graph,
                                       HashMap<String, Integer> words,
                                       HashMap<Integer, String> posToWord) {
    String[] ss = inputText.split(" ");
    Random random = new SecureRandom();
    ArrayList<String> list = new ArrayList<>();
    for (int i = 0; i < ss.length - 1; i++) {
      list.add(ss[i]);
      ArrayList<String> bridges = queryBridgeWords(ss[i], ss[i + 1], graph, words, posToWord);
      if (bridges != null && !bridges.isEmpty()) {
        //随机选一个加入
        int pos = random.nextInt(bridges.size());
        list.add(bridges.get(pos));
      }
    }
    list.add(ss[ss.length - 1]);
    StringBuilder res = new StringBuilder();
    for (String s : list) {
      res.append(s);
      res.append(" ");
    }
    return res.toString();

  }

  /**
   * read from 'filePath' and store words to 'list', create mapping from indices and words.
   *
   * @param filePath the file to be read.
   * @param words store the mapping from words to indices.
   * @param posToWord store the mapping from indices to words.
   * @param list store all words by their order in file.
   * @throws IOException if file not exist or something wrong in file reading.
   */
  public static void getFileContent(String filePath,
                                    HashMap<String, Integer> words,
                                    HashMap<Integer, String> posToWord,
                                    ArrayList<String> list) throws Exception {
    BufferedReader reader = new BufferedReader(
            new InputStreamReader(Files.newInputStream(validatePath(filePath)),
                                  StandardCharsets.UTF_8));

    int pos = 0;  //每个单词的下标
    String line;
    while ((line = reader.readLine()) != null) {
      // 提取出有效单词信息
      StringBuilder temp = new StringBuilder();
      for (char c : line.toCharArray()) {
        if (isSplit(c)) {
          if (temp.length() > 0) {
            list.add(temp.toString());
            if (words.containsKey(temp.toString())) {
              temp.setLength(0);
              continue;
            }
            words.put(temp.toString(), pos);
            posToWord.put(pos, temp.toString());
            pos++;
          }
          temp.setLength(0);
        } else if (isLetter(c)) {
          temp.append(c);
        } else {
          temp.setLength(0);
        }
      }
      //处理行末尾单词
      if (temp.length() > 0) {
        list.add(temp.toString());
        if (words.containsKey(temp.toString())) {
          continue;
        }
        words.put(temp.toString(), pos);
        posToWord.put(pos, temp.toString());
        pos++;
      }
    }
    // 关闭文件
    reader.close();
  }

  /**
   * show help.
   */
  public static void showInteractiveHelp() {
    System.out.println("输入下列命令:");
    System.out.println("bridge: 查询桥接词");
    System.out.println("path: 查询最短路径");
    System.out.println("random: 随机游走");
    System.out.println("generate: 生成新文本");
    System.out.println("quit: 退出");
  }

  /**
   * do interactive loop.
   *
   * @param graph graph.
   * @param words hashmap from words to indices.
   * @param posToWord hashmap from indices to words.
   */
  public static void interactiveLoop(int[][] graph, HashMap<String, Integer> words,
                                     HashMap<Integer, String> posToWord) {
    Scanner scanner = new Scanner(System.in, "UTF-8");
    try {
      showInteractiveHelp();
      while (true) {
        String command = scanner.next();
        switch (command) {
          case "bridge": {
            //查询桥接词
            System.out.print("第一个词：");
            String word1 = scanner.next();
            System.out.print("第二个词：");
            String word2 = scanner.next();
            System.out.print("桥接词为：");
            System.out.println(queryBridgeWordsToString(word1, word2, graph, words, posToWord));
            break;
          }
          case "path": {
            //输出最短路径
            System.out.print("第一个词：");
            String word1 = scanner.next();
            System.out.print("第二个词：");
            String word2 = scanner.next();
            System.out.println(calcShortestPath(word1, word2, graph, words, posToWord));
            break;
          }
          case "random":
            //随机游走
            System.out.println(randomWalk(graph, posToWord));
            break;
          case "generate":
            //根据bridge生成新文本
            if (scanner.hasNextLine()) {
              scanner.nextLine();
            }
            System.out.print("输入新文本：");
            String inputText = scanner.nextLine();
            System.out.println(generateNewText(inputText, graph, words, posToWord));
            break;
          case "quit":
            scanner.close();
            return;
          default:
            System.out.println("未知命令: " + command);
            showInteractiveHelp();
            break;
        }
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    scanner.close();
  }

  /**
   * main.
   *
   * @param args arguments from command line. none or a file path.
   */
  public static void main(String[] args) {
    if (args.length == 0) {
      System.out.println("Usage: java org.example.Main <file>");
      return;
    }
    try {
      String filePath = args[0];
      //临时存放有效单词集合(字符串映射图中对应下标)
      HashMap<String, Integer> words = new HashMap<>();
      //存放图中下标对应的字符串
      HashMap<Integer, String> posToWord = new HashMap<>();
      //存放单词的位置信息
      ArrayList<String> list = new ArrayList<>();
      getFileContent(filePath, words, posToWord, list);

      //生成图结构
      int[][] graph = generateGraph(words, list.toArray(new String[0]));

      //展示图结构
      showDirectedGraph(graph, posToWord);
      interactiveLoop(graph, words, posToWord);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }
}
