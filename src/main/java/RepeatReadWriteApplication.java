import cn.hutool.json.JSONUtil;
import entity.WordInfo;

import java.io.*;
import java.util.*;

/**
 * <p>
 * 英语读写程序启动类
 * </p>
 *
 * @author AhogeK ahogek@gmail.com
 * @date 2021-04-29 17:06
 * @since 1.00
 */
public class RepeatReadWriteApplication {

    private static final Map<Integer, String> MODAL_MAP = new HashMap<>(8);

    private static final Map<String, WordInfo> MY_ALL_RECORDS_WORDS = new HashMap<>(1024);

    private static final String RESOURCES_PATH = System.getProperty("user.dir") + "/MY_ALL_RECORD_WORDS";

    static {
        MODAL_MAP.put(1, "记录模式");
        MODAL_MAP.put(2, "记忆模式");
        MODAL_MAP.put(-1, "保存并退出");
        try {
            FileInputStream fi = new FileInputStream(RESOURCES_PATH);
            ObjectInputStream oi = new ObjectInputStream(fi);
            // Read objects
            Map<?, ?> map = JSONUtil.toBean(JSONUtil.parseObj(oi.readObject()), Map.class);
            map.forEach((k, v) -> MY_ALL_RECORDS_WORDS.put((String) k, JSONUtil.toBean(JSONUtil.parseObj(v),
                    WordInfo.class)));
            oi.close();
            fi.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("No data!");
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found!");
        }
    }

    public static void main(String[] args) {
        System.out.println("欢迎使用 单词记录与记忆 的简单终端程序！_(:з」∠)_\n");
        while (true) {
            System.out.println("请选择你要进行模式：\n1 - 记录模式\n2 - 记忆模式\n-1 - 保存并退出");
            try {
                Scanner sc = new Scanner(System.in);
                int userSelectModal = sc.nextInt();
                if (MODAL_MAP.containsKey(userSelectModal)) {
                    System.out.printf("您选择了\"%s\"!\n\n", MODAL_MAP.get(userSelectModal));
                    if (userSelectModal == 1) {
                        boolean isTrue = true;
                        while (isTrue) {
                            WordInfo word = new WordInfo();
                            System.out.println("请输入需要记忆的单词:");
                            sc = new Scanner(System.in);
                            String wordStr = sc.nextLine();
                            System.out.println("请输入该单词的翻译:");
                            String translator = sc.nextLine();
                            System.out.println("如果有，请输入该单词的网页词典地址:");
                            String url = sc.nextLine();
                            System.out.println("是否确认？(退回选择请输入 'exit') y | n");
                            String checkStr = sc.nextLine();
                            if ("y".equalsIgnoreCase(checkStr) || "".equals(checkStr)) {
                                addNewWord(word, wordStr, translator, url);
                            } else if ("exit".equalsIgnoreCase(checkStr)) {
                                addNewWord(word, wordStr, translator, url);
                                isTrue = false;
                            }
                        }
                        throw new RuntimeException();
                    } else if (userSelectModal == 2) {
                        boolean isTrue = true;
                        while (isTrue) {
                            if (MY_ALL_RECORDS_WORDS.size() == 0) {
                                System.err.println("您还未记录过任何单词，请重新选择");
                                throw new RuntimeException();
                            }
                            TreeMap<Double, WordInfo> weightMap = new TreeMap<>();
                            MY_ALL_RECORDS_WORDS.forEach((k, v) -> {
                                double lastWeight = weightMap.size() == 0 ? 0 : weightMap.lastKey();
                                weightMap.put((v.getNum() - v.getNum() * 2 + 10000) + lastWeight, v);
                            });
                            double randomWeight = weightMap.lastKey() * Math.random();
                            SortedMap<Double, WordInfo> tailMap = weightMap.tailMap(randomWeight, false);
                            if (weightMap.containsKey(tailMap.firstKey())) {
                                WordInfo wordInfo = weightMap.get(tailMap.firstKey());
                                System.out.println(wordInfo.toString());
                                MY_ALL_RECORDS_WORDS.get(wordInfo.getWorld()).setNum(wordInfo.getNum() + 1);
                                System.out.println("回车继续，或输入 'exit' 退回模式选择");
                                sc = new Scanner(System.in);
                                String inputStr = sc.nextLine();
                                if ("exit".equalsIgnoreCase(inputStr)) {
                                    isTrue = false;
                                }
                            }
                        }
                        throw new RuntimeException();
                    } else if (userSelectModal == -1) {
                        System.out.println("正在保存退出中... ...");
                        FileOutputStream f = new FileOutputStream(RESOURCES_PATH);
                        ObjectOutputStream o = new ObjectOutputStream(f);
                        // Write objects to file
                        o.writeObject(MY_ALL_RECORDS_WORDS);
                        o.flush();
                        o.close();
                        f.close();
                        System.exit(0);
                    }
                } else {
                    System.err.println("您的输入有误，请重新输入！");
                }
            } catch (InputMismatchException | IOException e) {
                System.err.println("您的输入有误，请重新输入！");
            } catch (RuntimeException e) {
                System.out.println("等待进入重新选择... ...");
            }
        }
    }

    /**
     * 添加新单词到单词MAP
     *
     * @param word       新单词
     * @param wordStr    单词拼写
     * @param translator 单词翻译
     * @param url        单词
     * @author AhgoeK ahogek@gmail.com
     * @date 2021-04-30 10:42
     */
    private static void addNewWord(WordInfo word, String wordStr, String translator, String url) {
        word.setWorld(wordStr);
        word.setTranslator(translator);
        word.setMerriamWebsterUrl(url);
        if (MY_ALL_RECORDS_WORDS.containsKey(wordStr)) {
            System.out.println("该单词已存在，记忆次数 + 1");
            int num = MY_ALL_RECORDS_WORDS.get(wordStr).getNum() + 1;
            MY_ALL_RECORDS_WORDS.remove(wordStr);
            word.setNum(num);
        }
        MY_ALL_RECORDS_WORDS.put(wordStr, word);
    }
}
