package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

// this is a comment

public class Main {
  public static boolean isLetter(char x) {
    return (x >= 'a' && x <= 'z') || (x >= 'A' && x <= 'Z');
  }

  public static boolean isSplit(char x) {
    return x == ' ' || x == ',' || x == '，' || x == ';' || x == ':';
  }

  public static int[][] generateGraph(HashMap<String, Integer> words, String[] s) {
    int n = words.size();
    int[][] graph = new int[n][n];
    // for(int[] g : graph) Arrays.fill(g,-1);
    for (int i = 0; i < s.length - 1; i++) {
      graph[words.get(s[i])][words.get(s[i + 1])] = 1;
    }
    return graph;
  }

  public static void showDirectedGraph(int[][] graph, HashMap<Integer, String> map) {
    System.out.print("   ");
    for (int i = 0; i < graph.length; i++) {
      System.out.print(map.get(i));
      System.out.print("   ");
    }
    System.out.println();
    for (int i = 0; i < graph.length; i++) {
      System.out.print(map.get(i));
      System.out.print("   ");
      for (int j = 0; j < graph.length; j++) {
        System.out.print(graph[i][j]);
        System.out.print("   ");
      }
      System.out.println();
    }
  }

  public static String queryBridgeWords(String word1, String word2, int[][] graph, HashMap<String, Integer> words,
      HashMap<Integer, String> posToWord) {
    if (!words.containsKey(word1) || !words.containsKey(word2)) {
      return "No word1 or word2 in the graph!";
    }
    int p1 = words.get(word1);
    int p2 = words.get(word2);
    // 存放桥接单词
    ArrayList<String> list = new ArrayList<>();
    // 找到p1的邻接点
    for (int i = 0; i < graph.length; i++) {
      if (graph[p1][i] == 1 && graph[i][p2] == 1) {
        list.add(posToWord.get(i));
      }
    }
    if (list.size() > 0) {
      String res = "The bridge words from word1 to word2 are: ";
      res += list.toString();
      return res;
    } else {
      return "“No bridge words from word1 to word2!";
    }
  }

  public static ArrayList<String> queryBridgeWords2(String word1, String word2, int[][] graph,
      HashMap<String, Integer> words, HashMap<Integer, String> posToWord) {
    if (!words.containsKey(word1) || !words.containsKey(word2)) {
      return null;
    }
    int p1 = words.get(word1);
    int p2 = words.get(word2);
    // 存放桥接单词
    ArrayList<String> list = new ArrayList<>();
    // 找到p1的邻接点
    for (int i = 0; i < graph.length; i++) {
      if (graph[p1][i] == 1 && graph[i][p2] == 1) {
        list.add(posToWord.get(i));
      }
    }
    if (list.size() > 0) {
      return list;
    } else {
      return null;
    }
  }

  public static String calcShortestPath(String word1, String word2, int[][] graph, HashMap<String, Integer> words,
      HashMap<Integer, String> posToWord) {
    // 使用bfs寻找最短路径
    ArrayDeque<Integer> queue = new ArrayDeque<>();
    int start = words.get(word1);
    int end = words.get(word2);
    int pre = start;
    queue.addLast(start);
    HashSet<Integer> visited = new HashSet<>();// 存放遍历过的结点
    visited.add(pre);
    HashMap<Integer, Integer> way = new HashMap<>();// 存放bfs过程中的路径
    boolean ok = false;
    while (queue.size() > 0) {
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
      if (ok)
        break;
    }
    if (ok) {
      // 从end开始倒序找路径
      ArrayList<Integer> l = new ArrayList<>();
      l.add(end);
      int pos = end;
      while (pos != start) {
        int next = way.get(pos);
        pos = next;
        l.add(pos);
      }
      StringBuilder res = new StringBuilder();
      for (int i = l.size() - 1; i >= 0; i--) {
        res.append(posToWord.get(l.get(i)));
        res.append("->");
      }
      return res.toString().substring(0, res.length() - 2);

    } else {
      return "不可达";
    }
  }

  public static String randomWalk(int[][] graph, HashMap<String, Integer> words, HashMap<Integer, String> posToWord) {
    // 创建Random对象
    Random random = new Random();
    StringBuilder res = new StringBuilder();
    // 随机选择起点
    int pos = random.nextInt(graph.length);
    res.append(posToWord.get(pos));
    res.append(" ");
    while (true) {
      int cnt = 0;
      ArrayList<Integer> possible = new ArrayList<>();// 存放可能的下一个结点
      for (int i = 0; i < graph.length; i++) {
        if (graph[pos][i] == 1) {
          possible.add(i);
          cnt++;
        }
      }
      if (cnt == 0)
        break;
      // 随机选一个结点
      int next_pos = random.nextInt(cnt);
      int next = possible.get(next_pos);
      graph[pos][next] = 2;
      pos = next;
      res.append(posToWord.get(pos));
      res.append(" ");
    }
    return res.toString();
  }

  // 根据bridge word生成新文本
  public static String generateNewText(String inputText, int[][] graph, HashMap<String, Integer> words,
      HashMap<Integer, String> posToWord) {
    String[] ss = inputText.split(" ");
    Random random = new Random();
    ArrayList<String> list = new ArrayList<>();
    for (int i = 0; i < ss.length - 1; i++) {
      list.add(ss[i]);
      ArrayList<String> bridges = queryBridgeWords2(ss[i], ss[i + 1], graph, words, posToWord);
      if (bridges != null) {
        // 随机选一个加入
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

  public static void main(String[] args) {
    try {
      // 读取文件
      String currentDir = System.getProperty("user.dir");
      String fileName = "test.txt";
      String filePath = currentDir + "/" + fileName;
      BufferedReader reader = new BufferedReader(new FileReader(filePath));
      String line;

      // 临时存放有效单词集合(字符串映射图中对应下标)
      HashMap<String, Integer> words = new HashMap<>();
      // 存放图中下标对应的字符串
      HashMap<Integer, String> posToWord = new HashMap<>();
      // 存放单词的位置信息
      ArrayList<String> list = new ArrayList<>();
      int pos = 0;// 每个单词的下标
      // 逐行读取文件内容
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
        // 处理行末尾单词
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

      // 生成图结构
      int[][] graph = generateGraph(words, list.toArray(new String[0]));

      // 展示图结构
      showDirectedGraph(graph, posToWord);

      // 查询桥接词
      System.out.print("桥接词为：");
      System.out.println(queryBridgeWords("All", "lead", graph, words, posToWord));

      // 输出最短路径
      System.out.print("最短路径：");
      System.out.println(calcShortestPath("can", "and", graph, words, posToWord));

      // 随机游走
      System.out.print("随机游走：");
      System.out.println(randomWalk(graph, words, posToWord));

      // 根据bridge生成新文本
      System.out.print("根据bridge生成新文本：");
      System.out.println(generateNewText("Better than anything", graph, words, posToWord));
      // 关闭文件
      reader.close();
      // 修改测试222 323232 change sth.
      // 666
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
