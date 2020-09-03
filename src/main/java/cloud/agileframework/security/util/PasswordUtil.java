package cloud.agileframework.security.util;

import cloud.agileframework.common.constant.Constant;
import cloud.agileframework.common.util.pattern.PatternUtil;
import cloud.agileframework.common.util.string.StringUtil;
import cloud.agileframework.security.properties.SecurityProperties;
import cloud.agileframework.security.properties.StrengthProperties;
import cloud.agileframework.security.properties.WeightMap;
import cloud.agileframework.spring.util.BeanUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.List;

import static java.math.BigDecimal.ROUND_HALF_DOWN;

/**
 * @author 佟盟
 * @version 1.0
 * @since 1.0
 */
public class PasswordUtil {

    public static final double NUM_TYPE_WEIGHT = 3.90625;
    public static final double LETTER_TYPE_WEIGHT = 10.15625;
    public static final double OTHER_TYPE_WEIGHT = 25.78125;

    /**
     * 密码强度
     */
    public enum LEVEL {
        /**
         * 密码强度
         */
        SO_EASY, EASY, ORDINARY, STRONG, SO_STRONG
    }

    private static final BCryptPasswordEncoder B_CRYPT_PASSWORD_ENCODER = new BCryptPasswordEncoder(Constant.NumberAbout.FOUR);

    private static final StrengthProperties STRENGTH_CONF = BeanUtil.getBean(SecurityProperties.class).getPassword().getStrengthConf();
    private static final int MIN_NUM = 48;
    private static final int MAN_NUM = 57;
    private static final int MIN_CAPITAL_LETTER = 65;
    private static final int MAN_CAPITAL_LETTER = 90;
    private static final int MIN_SMALL_LETTER = 97;
    private static final int MAN_SMALL_LETTER = 122;

    /**
     * NUM 数字
     * SMALL_LETTER 小写字母
     * CAPITAL_LETTER 大写字母
     * OTHER_CHAR  特殊字符
     */
    private static final int NUM = Constant.NumberAbout.ONE;
    private static final int SMALL_LETTER = Constant.NumberAbout.TWO;
    private static final int CAPITAL_LETTER = Constant.NumberAbout.THREE;
    private static final int OTHER_CHAR = Constant.NumberAbout.FOUR;

    private static double charLevel;

    /**
     * 检查字符类型，包括num、大写字母、小写字母和其他字符。
     *
     * @param c 字符
     * @return 字符类型
     */
    private static int checkCharacterType(char c) {
        if (c >= MIN_NUM && c <= MAN_NUM) {
            return NUM;
        }
        if (c >= MIN_CAPITAL_LETTER && c <= MAN_CAPITAL_LETTER) {
            return CAPITAL_LETTER;
        }
        if (c >= MIN_SMALL_LETTER && c <= MAN_SMALL_LETTER) {
            return SMALL_LETTER;
        }
        return OTHER_CHAR;
    }

    /**
     * 按不同类型计算密码的数量
     *
     * @param password 密码
     * @param type     字符类型
     * @return 数量
     */
    private static int countLetter(String password, int type) {
        int count = 0;
        if (null != password && password.length() > 0) {
            for (char c : password.toCharArray()) {
                if (checkCharacterType(c) == type) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * 检查密码的强度
     *
     * @param password 密码
     * @return strength level
     */
    public static double checkPasswordStrength(String password) {
        if (StringUtil.isBlank(password)) {
            throw new IllegalArgumentException("password is empty");
        }
        double level;

        level = init(password);

        /**
         * 减分
         */
        level = parsingRegex(password, level);

        /**
         * 减分
         */
        level = parsingKeyWord(password, level);

        return level;
    }

    private static double init(String password) {
        // 长度最大得分
        final int lengthLevel = 50;
        assert STRENGTH_CONF != null;
        final int maxLength = STRENGTH_CONF.getMaxLength();
        charLevel = new BigDecimal(lengthLevel).divide(new BigDecimal(maxLength), Constant.NumberAbout.TEN, ROUND_HALF_DOWN).doubleValue();

        double level = password.length() * charLevel;

        // 种类得分
        if (countLetter(password, NUM) > 0) {
            level += NUM_TYPE_WEIGHT;
        }
        if (countLetter(password, SMALL_LETTER) > 0) {
            level += LETTER_TYPE_WEIGHT;
        }
        if (countLetter(password, CAPITAL_LETTER) > 0) {
            level += LETTER_TYPE_WEIGHT;
        }
        if (countLetter(password, OTHER_CHAR) > 0) {
            level += OTHER_TYPE_WEIGHT;
        }
        return level;

    }

    /**
     * 验证关键字
     *
     * @param password 密码
     * @param level    强度
     * @return 强度
     */
    private static double parsingKeyWord(String password, double level) {
        assert STRENGTH_CONF != null;
        double weightOfKeyWord = STRENGTH_CONF.getWeightOfKeyWord();
        List<String> keyWords = STRENGTH_CONF.getWeightOfKeyWords();

        BigDecimal countKewWords = new BigDecimal(keyWords.size());
        for (String keyWord : keyWords) {
            if (password.contains(keyWord)) {
                // 计算当前正则的权重分数，每个字符的有效分数 * 关键字打分占比 * 当前关键字占比
                double percentage = new BigDecimal(1).divide(countKewWords, Constant.NumberAbout.TEN, ROUND_HALF_DOWN).doubleValue() * (charLevel * weightOfKeyWord);
                level -= keyWord.length() * percentage;
            }
        }
        return level;
    }

    /**
     * 正则匹配
     *
     * @param password 密码
     * @param level    强度
     * @return 强度
     */
    private static double parsingRegex(String password, double level) {
        assert STRENGTH_CONF != null;
        double weightOfRegex = STRENGTH_CONF.getWeightOfRegex();
        List<WeightMap> weightMaps = STRENGTH_CONF.getWeightOfRegexMap();
        double sum = weightMaps.stream().map(WeightMap::getWeight).mapToDouble(a -> a).sum();

        for (WeightMap weightMap : weightMaps) {
            double weight = weightMap.getWeight();
            // 计算当前正则的权重分数，每个字符的有效分数 * 正则打分占比 * 当前正则占比
            double percentage = new BigDecimal(weight).divide(new BigDecimal(sum), Constant.NumberAbout.TEN, ROUND_HALF_DOWN).doubleValue() * (charLevel * weightOfRegex);
            level = regexScoring(weightMap.getRegex(), password, level, percentage);
        }

        return level;
    }

    /**
     * 获得密码强度等级，包括弱、较弱、中、较强、强
     *
     * @param password 密码
     * @return 强度
     */
    public static LEVEL getPasswordLevel(String password) {
        final double weight = 3.6;
        double level = checkPasswordStrength(password);
        if (level <= weight) {
            return LEVEL.SO_EASY;
        } else if (level <= Constant.NumberAbout.NINE * Constant.NumberAbout.THREE) {
            return LEVEL.EASY;
        } else if (level <= Constant.NumberAbout.NINE * Constant.NumberAbout.FOUR) {
            return LEVEL.ORDINARY;
        } else if (level <= Constant.NumberAbout.NINE * Constant.NumberAbout.FIVE) {
            return LEVEL.STRONG;
        } else {
            return LEVEL.SO_STRONG;
        }
    }

    /**
     * 加密
     *
     * @param clear 明文
     * @return 密文
     */
    public static String encryption(String clear) {
        return B_CRYPT_PASSWORD_ENCODER.encode(clear);
    }

    /**
     * 密码匹配
     *
     * @param cipher 预想匹配的密码明文
     * @return 是否匹配
     */
    public static boolean decryption(String clear, String cipher) {
        return B_CRYPT_PASSWORD_ENCODER.matches(clear, cipher);
    }

    private static double regexScoring(String regex, String text, double level, double weight) {
        List<String> matches = PatternUtil.getMatched(regex, text);

        int sum = matches.stream().map(String::length).mapToInt(s -> s).sum();

        level -= sum * (1 - weight);

        return level;
    }
}
