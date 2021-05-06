package entity;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 单词实体类
 * </p>
 *
 * @author AhogeK ahogek@gmail.com
 * @date 2021-04-29 17:32
 * @since 1.00
 */
@Data
public class WordInfo implements Serializable {

    private String world;

    private String translator;

    private String merriamWebsterUrl;

    private Integer num = 0;

    @Override
    public String toString() {
        return "单词 = '" + world + '\'' + "\n" +
                "翻译 = '" + translator + '\'' + "\n" +
                "词典网地址 = '" + merriamWebsterUrl + '\'' + "\n" +
                "记忆次数 = " + num;
    }
}
