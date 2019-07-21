package hyman.utils;

import hyman.config.CustomException;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * 使用 hibernate-validator 校验工具类，为什么要使用这个工具类呢？
 * 1、controller 方法中不用加入 BindingResult 参数。
 * 2、controller 方法中需要校验的参数也不需要加入 @Valid 或者 @Validated 注解。
 *
 * 具体使用，在controller方法或者全局拦截校验器中调用 ValidatorUtils.validateResultProcess(需要校验的Bean) 直接获取校验的结果。
 * 请参考（https://github.com/hjzgg/usually_util/blob/master/spring-validate-demo/validator/ValidatorUtils.java）。
 */
@Component
public class ValidatorUtils implements ApplicationContextAware {

    /**
     * 使用 hibernate-validator 校验工具类
     * 参考文档：http://docs.jboss.org/hibernate/validator/5.4/reference/en-US/html_single/
     */
    private static Validator validator;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ValidatorUtils.validator = (Validator) applicationContext.getBean("validator");
        //validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    /**
     * 校验对象
     * @param object        待校验对象
     * @param groups        待校验的组
     * @throws CustomException  校验不通过，则报RRException异常
     */
    public static Optional<String> validateEntity(Object object, Class<?>... groups) throws CustomException {

        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object, groups);
        if (!constraintViolations.isEmpty()) {
            ConstraintViolation<Object> constraint = (ConstraintViolation<Object>)constraintViolations.iterator().next();
            return Optional.empty();
        }

        StringBuilder sb = new StringBuilder();
        for (Iterator<ConstraintViolation<Object>> iterator = constraintViolations.iterator(); iterator.hasNext(); ) {
            sb.append(iterator.next().getMessage());
            if (iterator.hasNext()) {
                sb.append(" ,");
            }
        }
        return Optional.of(sb.toString());
    }

    public static Optional<String> validateEntity(Object obj)  {
        Set<ConstraintViolation<Object>> results = validator.validate(obj);
        if (CollectionUtils.isEmpty(results)) {
            return Optional.empty();
        }

        StringBuilder sb = new StringBuilder();
        for (Iterator<ConstraintViolation<Object>> iterator = results.iterator(); iterator.hasNext(); ) {
            sb.append(iterator.next().getMessage());
            if (iterator.hasNext()) {
                sb.append(" ,");
            }
        }
        return Optional.of(sb.toString());
    }

    /**
     * 验证表单
     * @param list
     *            例子：[["用户名","wrj","isNull|isContainIllegalChar"], ["邮箱",";"1234567@qq.com","isMail|..."], ...]
     *            说明：<br>
     *            验证列表为空，则返回 ""<br>
     *            isNull是否为空("",null) 返回 "001"<br>
     *            isContainIllegalChar是否包含非法字符("~","'","$","|") 返回 "002"<br>
     *            isContainsChinese是否包含中文 返回 "003"<br>
     *            isDate日期格式是否正确(yyyy-MM-dd) 返回 "004"<br>
     *            isDateTime日期格式是否正确(yyyy-MM-dd HH:mm:ss) 返回 "005"<br>
     *            isMail邮箱格式是否正确 返回 "006"<br>
     *            isIDCard身份证号是否正确  返回 "007"<br>
     *            isIpIP格式是否正确 返回 "008"<br>
     *            isInteger是否为整数 返回 "009"<br>
     *            isPosNumber是否正整数 返回 "010"<br>
     *            isMobile是否为手机号 返回 "011"<br>
     *            isPhone是否为座机号 返回 "012"<br>
     *            isUrl是否为网址 返回 "013"<br>
     *
     *@param msa 国际化资源存取器
     */
    public static void validate(List<String[]> list, MessageSourceAccessor msa) {

        if (list != null && !list.isEmpty()) {
            for (String[] item : list) {
                for (int i = 0; i < item.length; i++) {

                    String paramName = item[0]; // 需验证的参数中文名
                    String paramValue = item[1]; // 需验证的参数值
                    String checks = item[2]; // 需验证的格式

                    String[] checksArr = checks.split("\\|");

                    for (int j = 0; j < checksArr.length; j++) {
                        // 是否为空
                        if ("isNull".equals(checksArr[j])) {
                            if (StringUtils.isEmpty(paramValue)) {
                                throw new CustomException(paramName + msa.getMessage("val.isblank"));
                            }
                        }
                        // 是否包含非法字符("~","'","$","|")
                        if ("isContainIllegalChar".equals(checksArr[j])) {
                            if (isLawlessPwd(paramValue)) {
                                throw new CustomException(paramName + msa.getMessage("val.isContainIllegalChar"));
                            }
                        }
                        // 是否包含中文
                        if ("isContainsChinese".equals(checksArr[j])) {
                            if (isContainsChinese(paramValue)) {
                                throw new CustomException(paramName + msa.getMessage("val.isContainsChinese"));
                            }
                        }
                        // 日期格式是否正确
                        if ("isDate".equals(checksArr[j])) {
                            if (!isValidDate(paramValue)) {
                                throw new CustomException(paramName + msa.getMessage("val.isDate"));
                            }
                        }
                        // 日期格式是否正确
                        if ("isDateTime".equals(checksArr[j])) {
                            if (!isValidDateTime(paramValue)) {
                                throw new CustomException(paramName + msa.getMessage("val.isDate"));
                            }
                        }
                        // 邮箱格式是否正确
                        if ("isMail".equals(checksArr[j])) {
                            if (!isEmail(paramValue)) {
                                throw new CustomException(paramName + msa.getMessage("val.IncorrectFormat"));
                            }
                        }
                        // 身份证号是否正确
                        if ("isIDCard".equals(checksArr[j])) {
                            if (!isIDCard(paramValue)) {
                                throw new CustomException(paramName + msa.getMessage("val.error"));
                            }
                        }
                        // IP格式是否正确
                        if ("isIp".equals(checksArr[j])) {
                            if (!isIp(paramValue)) {
                                throw new CustomException(paramName + msa.getMessage("val.IncorrectFormat"));
                            }
                        }
                        // 是否为整数
                        if ("isInteger".equals(checksArr[j])) {
                            if (!isInteger(paramValue)) {
                                throw new CustomException(paramName + msa.getMessage("val.isInteger"));
                            }
                        }
                        // 是否正整数
                        if ("isPosNumber".equals(checksArr[j])) {
                            if (!isPosNumber(paramValue)) {
                                throw new CustomException(paramName + msa.getMessage("val.isPosNumber"));
                            }
                        }
                        // 是否为手机号
                        if ("isMobile".equals(checksArr[j])) {
                            if (!isMobile(paramValue)) {
                                throw new CustomException(paramName + msa.getMessage("val.IncorrectFormat"));
                            }
                        }
                        // 是否为座机号
                        if ("isPhone".equals(checksArr[j])) {
                            if (!isPhone(paramValue)) {
                                throw new CustomException(paramName + msa.getMessage("val.IncorrectFormat"));
                            }
                        }
                        // 是否为网址
                        if ("isUrl".equals(checksArr[j])) {
                            if (!isUrl(paramValue)) {
                                throw new CustomException(paramName + msa.getMessage("val.IncorrectFormat"));
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 验证表单
     *
     * @param list
     *            例子：[["用户名","wrj","isNull|isContainIllegalChar"], ["邮箱",";"1234567@qq.com","isMail|..."], ...]
     *            说明：<br>
     *            验证列表为空	返回""<br>
     *            isNull是否为空("",null)	返回 "001"<br>
     *            isContainIllegalChar是否包含非法字符("~","'","$","|")	返回 "002"<br>
     *            isContainsChinese是否包含中文	返回 "003"<br>
     *            isDate日期格式是否正确(yyyy-MM-dd)	返回 "004"<br>
     *            isDateTime日期格式是否正确(yyyy-MM-dd HH:mm:ss)	返回 "005"<br>
     *            isMail邮箱格式是否正确	返回 "006"<br>
     *            isIDCard身份证号是否正确 	返回 "007"<br>
     *            isIpIP格式是否正确	返回 "008"<br>
     *            isInteger是否为整数	返回 "009"<br>
     *            isPosNumber是否正整数	返回 "010"<br>
     *            isMobile是否为手机号	返回 "011"<br>
     *            isPhone是否为座机号	返回 "012"<br>
     *            isUrl是否为网址	返回 "013"<br>
     *
     *@return 表单验证通过返回""，未通过返回相应的提示信息
     */
    public static String validate(List<String[]> list) {

        if (list != null && !list.isEmpty()) {
            for (String[] item : list) {
                for (int i = 0; i < item.length; i++) {

                    String paramName = item[0]; // 需验证的参数中文名
                    String paramValue = item[1]; // 需验证的参数值
                    String checks = item[2]; // 需验证的格式
                    String[] checksArr = checks.split("\\|");

                    for (int j = 0; j < checksArr.length; j++) {
                        // 是否为空
                        if ("isNull".equals(checksArr[j])) {
                            if (StringUtils.isEmpty(paramValue)) {
                                return paramName + "001";
                            }
                        }
                        // 是否包含非法字符("~","'","$","|")
                        if ("isContainIllegalChar".equals(checksArr[j])) {
                            if (isLawlessPwd(paramValue)) {
                                return paramName + "002";
                            }
                        }
                        // 是否包含中文
                        if ("isContainsChinese".equals(checksArr[j])) {
                            if (isContainsChinese(paramValue)) {
                                return paramName + "003";
                            }
                        }
                        // 日期格式是否正确
                        if ("isDate".equals(checksArr[j])) {
                            if (!isValidDate(paramValue)) {
                                return paramName + "004";
                            }
                        }
                        // 日期格式是否正确
                        if ("isDateTime".equals(checksArr[j])) {
                            if (!isValidDateTime(paramValue)) {
                                return paramName + "005";
                            }
                        }
                        // 邮箱格式是否正确
                        if ("isMail".equals(checksArr[j])) {
                            if (!isEmail(paramValue)) {
                                return paramName + "006";
                            }
                        }
                        // 身份证号是否正确
                        if ("isIDCard".equals(checksArr[j])) {
                            if (!isIDCard(paramValue)) {
                                return paramName + "007";
                            }
                        }
                        // IP格式是否正确
                        if ("isIp".equals(checksArr[j])) {
                            if (!isIp(paramValue)) {
                                return paramName + "008";
                            }
                        }
                        // 是否为整数
                        if ("isInteger".equals(checksArr[j])) {
                            if (!isInteger(paramValue)) {
                                return paramName + "009";
                            }
                        }
                        // 是否正整数
                        if ("isPosNumber".equals(checksArr[j])) {
                            if (!isPosNumber(paramValue)) {
                                return paramName + "010";
                            }
                        }
                        // 是否为手机号
                        if ("isMobile".equals(checksArr[j])) {
                            if (!isMobile(paramValue)) {
                                return paramName + "011";
                            }
                        }
                        // 是否为座机号
                        if ("isPhone".equals(checksArr[j])) {
                            if (!isPhone(paramValue)) {
                                return paramName + "012";
                            }
                        }
                        // 是否为网址
                        if ("isUrl".equals(checksArr[j])) {
                            if (!isUrl(paramValue)) {
                                return paramName + "013";
                            }
                        }
                    }
                }
            }
        }
        return "";
    }

    /**
     *
     * <p><b>方法描述：</b>密码是否包含非法字符（"~","'","$","|"," "）</p>
     * @param str 要验证的字符串
     * @return true or false
     */
    public static boolean isLawlessPwd(String str) {
        boolean f = false;
        if (str == null) {
            f = false;
        } else {
            // 非法字符数组
            String[] lawless = { "~", "'", "$", "|", " " };
            for (int i = 0; i < lawless.length; i++) {
                if (str.contains(lawless[i])) {
                    f = true;
                    break;
                } else {
                    f = false;
                }
            }
        }
        return f;
    }

    /**
     *
     * <p><b>方法描述：</b>是否包含中文</p>
     * @param str 要验证的字符串
     * @return true or false
     */
    public static boolean isContainsChinese(String str) {
        String regEx = "[\u4e00-\u9fa5]";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        boolean f = false;
        if (matcher.find()) {
            f = true;
        }
        return f;
    }

    /**
     *
     * <p><b>方法描述：</b>是否日期格式</p>
     * @param str 要验证的日期
     * @return true or false
     */
    public static boolean isValidDate(String str) {
        boolean f = true;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            sdf.setLenient(false);
            sdf.parse(str);
        } catch (ParseException e) {
            f = false;
        }
        return f;
    }

    /**
     *
     * <p><b>方法描述：</b>是否时间格式</p>
     * @param str 要验证的时间
     * @return true or false
     */
    public static boolean isValidTime(String str) {
        boolean f = true;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        try {
            sdf.setLenient(false);
            sdf.parse(str);
        } catch (ParseException e) {
            f = false;
        }
        return f;
    }

    /**
     *
     * <p><b>方法描述：</b>是否日期时间格式</p>
     * @param str 要验证的日期时间
     * @return true or false
     */
    public static boolean isValidDateTime(String str) {
        boolean f = true;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            sdf.setLenient(false);
            sdf.parse(str);
        } catch (ParseException e) {
            f = false;
        }
        return f;
    }

    /**
     *
     * <p><b>方法描述：</b>邮箱验证</p>
     * @param str 要验证的邮箱
     * @return true or false
     */
    public static boolean isEmail(String str) {
        String regxStr = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern pattern = Pattern.compile(regxStr);
        return pattern.matcher(str).matches();
    }

    /**
     *
     * <p><b>方法描述：</b>是否是身份证号</p>
     * @param str 要验证的身份证号
     * @return true or false
     */
    public static boolean isIDCard(String str) {
        String regxStr = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{4}$";
        Pattern pattern = Pattern.compile(regxStr);
        return pattern.matcher(str).matches();
    }

    /**
     *
     * <p><b>方法描述：</b>验证IP</p>
     * @param str 要验证的IP
     * @return true or false
     */
    public static boolean isIp(String str) {
        String regxStr = "\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b";
        Pattern pattern = Pattern.compile(regxStr);
        return pattern.matcher(str).matches();
    }

    /**
     *
     * <p><b>方法描述：</b>验证是否为整数</p>
     * @param str 要验证的整数
     * @return true or false
     */
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    /**
     *
     * <p><b>方法描述：</b>验证是否为小数</p>
     * @param str 要验证的小数
     * @return true or false
     */
    public static boolean isDecimal(String str) {
        Pattern pattern = Pattern.compile("\\d+\\.\\d+$|-\\d+\\.\\d+$");
        return pattern.matcher(str).matches();
    }

    /**
     *
     * <p><b>方法描述：</b>验证是否为正整数</p>
     * @param str 要验证的正整数
     * @return true or false
     */
    public static boolean isPosNumber(String str) {
        boolean f = false;
        try {
            // 把字符串强制转换为数字
            int num = Integer.parseInt(str);
            // 如果是数字，返回True
            if (num > 0) {
                f = true;
            } else {
                f = false;
            }
        } catch (NumberFormatException e) {
            // 如果抛出异常，返回False

        }
        return f;
    }

    /**
     *
     * <p><b>方法描述：</b>验证是否为正整数</p>
     * @param str 要验证的正整数
     * @return true or false
     */
    public static boolean isNumber(String str) {
        boolean f = false;
        try {
            // 把字符串强制转换为数字
            double num = Double.parseDouble(str);
            // 如果是数字，返回True
            if (num > 0) {
                f = true;
            } else {
                f = false;
            }
        } catch (NumberFormatException e) {
            // 如果抛出异常，返回False

        }
        return f;
    }

    /**
     *
     * <p><b>方法描述：</b>格式化数字（四舍五入保留两位小数）</p>
     * @param num 需格式化的数字
     * @return String
     */
    public static String formatNumber(double num) {
        String value = String.format("%.2f", num);
        return value;
    }

    /**
     *
     * <p><b>方法描述：</b>获取某个范围内的一个随机数</p>
     * @param min 范围最小值
     * @param max 范围最大值
     * @return 随机数
     */
    public static String getRandom(int min, int max) {
        if (max <= 0) {
            return Integer.toString(max);
        }
        SecureRandom random = new SecureRandom();
        int s = random.nextInt(max) % (max - min + 1) + min;
        return String.valueOf(s);
    }

    /**
     *
     * <p><b>方法描述：</b>是否是手机号</p>
     * @param str 要验证的手机号
     * @return true or false
     */
    public static boolean isMobile(String str) {
        Pattern pattern = Pattern.compile("^0{0,1}(1[0-9][0-9]|15[7-9]|153|156|18[7-9])[0-9]{8}$");
        return pattern.matcher(str).matches();
    }

    /**
     *
     * <p><b>方法描述：</b>是否是电话号码</p>
     * @param str 要验证的电话
     * @return true or false
     */
    public static boolean isPhone(String str) {
        Pattern pattern = Pattern.compile("0\\d{2,3}-\\d{7,8}");
        return pattern.matcher(str).matches();
    }

    /**
     *
     * <p><b>方法描述：</b>是否是网址</p>
     * @param urlstr 要验证的网址
     * @return true or false
     */
    public static boolean isUrl(String urlstr) {
        boolean f;
        URL url;
        try {
            url = new URL(urlstr);
            url.openStream();
            f = true;
        } catch (MalformedURLException e) {
            f = false;
        } catch (IOException e) {
            f = false;
        }
        return f;
    }

    public static boolean isEmailLegal(String str) throws PatternSyntaxException {
        String regExp = "\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    public static boolean isChinaPhoneLegal(String str) throws PatternSyntaxException {
//		String regExp = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";
        String regExp = "0?(13|14|15|18|17)[0-9]{9}";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }
}
