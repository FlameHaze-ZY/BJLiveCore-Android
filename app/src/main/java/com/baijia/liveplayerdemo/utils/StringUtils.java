package com.baijia.liveplayerdemo.utils;

import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by bjhl on 16/6/22.
 */
public class StringUtils {
    private static final String[] hexDigits = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    public StringUtils() {
    }

    public static String byteArrayToHexString(byte[] b) {
        StringBuffer resultSb = new StringBuffer();

        for(int i = 0; i < b.length; ++i) {
            resultSb.append(byteToHexString(b[i]));
        }

        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if(b < 0) {
            n = 256 + b;
        }

        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    public static String toMD5(String origin) {
        try {
            MessageDigest ex = MessageDigest.getInstance("MD5");
            return byteArrayToHexString(ex.digest(origin.getBytes()));
        } catch (Exception var2) {
            return null;
        }
    }

    public static String encodeURL(String url, String encode) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        StringBuilder noAsciiPart = new StringBuilder();

        for(int i = 0; i < url.length(); ++i) {
            char c = url.charAt(i);
            if(c > 255) {
                noAsciiPart.append(c);
            } else {
                if(noAsciiPart.length() != 0) {
                    sb.append(URLEncoder.encode(noAsciiPart.toString(), encode));
                    noAsciiPart.delete(0, noAsciiPart.length());
                }

                sb.append(c);
            }
        }

        return sb.toString();
    }

    public static int string2Int(String str) {
        if(TextUtils.isEmpty(str)) {
            return 0;
        } else {
            int ret = 0;

            try {
                ret = Integer.parseInt(str);
            } catch (Exception var3) {
                ;
            }

            return ret;
        }
    }

    public static long parseLong(String str, long defaultValue) {
        if(isEmpty(str)) {
            return defaultValue;
        } else {
            try {
                return Long.parseLong(str);
            } catch (NumberFormatException var4) {
                return defaultValue;
            }
        }
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isBlank(String str) {
        return str != null && isEmpty(str.trim());
    }

    public static boolean isNull(String str) {
        return str == null || "null".equalsIgnoreCase(str.trim());
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str) && !isNull(str);
    }

    public static String toString(int value) {
        return String.valueOf(value);
    }

    public static String toString(boolean value) {
        return String.valueOf(value);
    }

    public static String toString(double value) {
        return String.valueOf(value);
    }

    public static String toString(long value) {
        return String.valueOf(value);
    }

    public static SpannableString scoreSpannable(String content, int large, int small) {
        SpannableString spannableString = new SpannableString(content);
        if(large > 0 && small > 0) {
            int index = content.indexOf(".");
            int end = index > -1?index:1;
            spannableString.setSpan(new AbsoluteSizeSpan(large), 0, end, 33);
            spannableString.setSpan(new AbsoluteSizeSpan(small), end, spannableString.length(), 33);
            return spannableString;
        } else {
            return spannableString;
        }
    }


    public static String nullStrToEmpty(Object str) {
        return str == null?"":(str instanceof String?(String)str:str.toString());
    }

    public static String capitalizeFirstLetter(String str) {
        if(isEmpty(str)) {
            return str;
        } else {
            char c = str.charAt(0);
            return Character.isLetter(c) && !Character.isUpperCase(c)?(new StringBuilder(str.length())).append(Character.toUpperCase(c)).append(str.substring(1)).toString():str;
        }
    }

    public static String utf8Encode(String str) {
        if(!isEmpty(str) && str.getBytes().length != str.length()) {
            try {
                return URLEncoder.encode(str, "UTF-8");
            } catch (UnsupportedEncodingException var2) {
                throw new RuntimeException("UnsupportedEncodingException occurred. ", var2);
            }
        } else {
            return str;
        }
    }

    public static String utf8Encode(String str, String defultReturn) {
        if(!isEmpty(str) && str.getBytes().length != str.length()) {
            try {
                return URLEncoder.encode(str, "UTF-8");
            } catch (UnsupportedEncodingException var3) {
                return defultReturn;
            }
        } else {
            return str;
        }
    }

    public static String htmlEscapeCharsToString(String source) {
        return isEmpty(source)?source:source.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&").replaceAll("&quot;", "\"");
    }

    public static String fullWidthToHalfWidth(String s) {
        if(isEmpty(s)) {
            return s;
        } else {
            char[] source = s.toCharArray();

            for(int i = 0; i < source.length; ++i) {
                if(source[i] == 12288) {
                    source[i] = 32;
                } else if(source[i] >= '！' && source[i] <= '～') {
                    source[i] -= 'ﻠ';
                } else {
                    source[i] = source[i];
                }
            }

            return new String(source);
        }
    }

    public static String halfWidthToFullWidth(String s) {
        if(isEmpty(s)) {
            return s;
        } else {
            char[] source = s.toCharArray();

            for(int i = 0; i < source.length; ++i) {
                if(source[i] == 32) {
                    source[i] = 12288;
                } else if(source[i] >= 33 && source[i] <= 126) {
                    source[i] += 'ﻠ';
                } else {
                    source[i] = source[i];
                }
            }

            return new String(source);
        }
    }

    public static int getStringLen(String origin) {
        int len = 0;

        for(int i = 0; i < origin.length(); ++i) {
            Character c = Character.valueOf(origin.charAt(i));
            if(c.toString().getBytes().length != 1) {
                len += 2;
            } else {
                ++len;
            }
        }

        return len;
    }

    public static String getMixSubString(String origin, int max, String sub) {
        String result = "";
        if(!TextUtils.isEmpty(origin) && max > 0) {
            int len = 0;

            int i;
            for(i = 0; i < origin.length(); ++i) {
                Character c = Character.valueOf(origin.charAt(i));
                if(c.toString().getBytes().length != 1) {
                    len += 2;
                } else {
                    ++len;
                }

                result = result + c;
                if(len >= max) {
                    break;
                }
            }

            if(i < origin.length()) {
                result = result + sub;
            }

            return result;
        } else {
            return result;
        }
    }

    public static String getMixSubString(String origin, int max) {
        return getMixSubString(origin, max, "");
    }

    public static String getLastSplit(String org, String split) {
        return TextUtils.isEmpty(org)?"":org.substring(org.lastIndexOf(split) + 1, org.length());
    }

    public static boolean isMobileNumber(String mobiles) {
        Pattern p = Pattern.compile("((\\d{11})|^((\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1})|(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1}))$)");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }
}