package com.cyz.basic.util;

import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

/**
 * 
 * @author cyz
 *
 */
public abstract class StrUtil extends StringUtils {
	
	 private static final Pattern special = Pattern.compile("\\s*|\t|\r|\n");
    
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
	
}
