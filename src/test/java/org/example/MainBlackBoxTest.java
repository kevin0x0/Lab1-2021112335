package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

import static org.example.Main.*;

public class MainBlackBoxTest {

  @Test
  public void generateGraphTest() throws IOException {
    // 读取文件
    String currentDir = System.getProperty("user.dir");
    String fileName = "test.txt";
    String filePath = currentDir + "/" + fileName;
    BufferedReader reader = new BufferedReader(new FileReader(filePath));
    String line;

    //临时存放有效单词集合(字符串映射图中对应下标)
    HashMap<String, Integer> words = new HashMap<>();
    //存放图中下标对应的字符串
    HashMap<Integer, String> posToWord = new HashMap<>();
    //存放单词的位置信息
    ArrayList<String> list = new ArrayList<>();
    int pos = 0;//每个单词的下标
    boolean flag = true;
    // 逐行读取文件内容
    while ((line = reader.readLine()) != null) {
      // 提取出有效单词信息
      StringBuilder temp = new StringBuilder();
      for (char c : line.toCharArray()) {
        if (isSplit(c)) {
          if (temp.length() > 0) {
            if(!flag){
              temp.setLength(0);
              flag = true;
              continue;
            }
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
          flag = false;
        }
      }
      //处理行末尾单词
      if (temp.length() > 0&&flag) {
        list.add(temp.toString());
        if (words.containsKey(temp.toString())) {
          continue;
        }
        words.put(temp.toString(), pos);
        posToWord.put(pos, temp.toString());
        pos++;
      }
    }

    //生成图结构
    int[][] graph = generateGraph(words, list.toArray(new String[0]));

    //展示图结构
    showDirectedGraph(graph, posToWord);
    reader.close();
  }
}

