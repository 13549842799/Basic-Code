package com.cyz.basic.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;


/**
 * 
 * @author cyz
 *
 */
public abstract class StrUtil extends StringUtils {
	
	private static Pattern numericPattern = Pattern.compile("^[-\\+]?[\\d.]*$"); 
	private static final Pattern special = Pattern.compile("\\s*|\t|\r|\n");
	
	private static final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy年MM月");
	
	private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
	
	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");
    
	 /**
     * 移除文本中的隐藏字符，包括换行符等
     * @param content
     * @return
     */
    public static String returenOnlyWords(String content) {
        if (content == null) {
        	return "";
        }
		return special.matcher(content.trim()).replaceAll("");
    }
    
    public static boolean isNotEmpty(Object str) {
    	return !isEmpty(str);
    }
    
    /**
     * 判断是否数字表示
     *
     * @param src
     *            源字符串
     * @return 是否数字的标志
     */
    public static boolean isNumeric(String src) {
        boolean return_value = false;
        if (src != null && src.length() > 0) {
            return numericPattern.matcher(src).matches();
        }
        return return_value;
    }
    
    public static String formatDate(LocalDate date) {
    	return date.format(dateFormatter);
    }
    
    public static String formatMonth(LocalDate date) {
    	return date.format(monthFormatter);
    }
    
    public static String formatDate(LocalDateTime time) {
    	return time.format(dateTimeFormatter);
    }
    
	
}
