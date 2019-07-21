package hyman.utils;

import org.springframework.util.StringUtils;

import java.beans.PropertyEditorSupport;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期编辑器
 * <p><b>类描述：</b>根据日期字符串长度判断是长日期还是短日期。只支持yyyy-MM-dd，yyyy-MM-dd HH:mm:ss两种格式。
 */
public class DateTypeEditor extends PropertyEditorSupport {

    /**
     * 格式化日期yyyy-MM-dd HH:mm:ss
     */
    public static final DateFormat DF_LONG = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * 格式化日期yyyy-MM-dd
     */
    public static final DateFormat DF_SHORT = new SimpleDateFormat("yyyy-MM-dd");
    /**
     * 格式化日期yyyy
     */
    public static final DateFormat DF_YEAR = new SimpleDateFormat("yyyy");
    /**
     * 格式化日期yyyy-MM
     */
    public static final DateFormat DF_MONTH = new SimpleDateFormat("yyyy-MM");
    /**
     * 短类型日期长度
     */
    public static final int SHORT_DATE = 10;
    /**
     * 年份长度
     */
    public static final int YEAR_DATE = 4;
    /**
     * 月份长度
     */
    public static final int MONTH_DATE = 7;

    /**
     * 设置日期值
     * @param text 日期字符串
     * @throws IllegalArgumentException 参数异常
     */
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        String tmpText = text.trim();
        if (!StringUtils.hasText(tmpText)) {
            setValue(null);
            return;
        }
        try {
            if (tmpText.length() <= YEAR_DATE) {
                setValue(new java.sql.Date(DF_YEAR.parse(tmpText).getTime()));
            } else if (tmpText.length() <= MONTH_DATE) {
                setValue(new java.sql.Date(DF_MONTH.parse(tmpText).getTime()));
            } else if (tmpText.length() <= SHORT_DATE) {
                setValue(new java.sql.Date(DF_SHORT.parse(tmpText).getTime()));
            } else {
                setValue(new java.sql.Timestamp(DF_LONG.parse(tmpText).getTime()));
            }
        } catch (ParseException ex) {
            IllegalArgumentException iae = new IllegalArgumentException("Could not parse date: " + ex.getMessage());
            iae.initCause(ex);
            throw iae;
        }
    }

    /**
     * Format the Date as String, using the specified DateFormat.
     * @return 日期字符串值
     */
    public String getAsText() {
        Date value = (Date) getValue();
        return (value != null ? DF_LONG.format(value) : "");
    }
}
