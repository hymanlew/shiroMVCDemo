package hyman.utils;

public enum Global {

    /**
     * 是
     */
    YES(1, "是"),

    /**
     * 否
     */
    NO(0, "否"),

    /**
     * 显示
     */
    SHOW(1, "显示"),

    /**
     * 隐藏
     */
    HIDE(0, "隐藏"),

    /**
     * 对
     */
    TRUE(1, "对"),

    /**
     * 错
     */
    FALSE(0, "错"),

    /**
     * 成功
     */
    SUCCESS(1, "成功"),

    /**
     * 失败
     */
    FAILED(0, "失败");


    /**
     * 编码
     */
    private int code;

    /**
     * 值
     */
    private String value;

    Global() {

    }

    Global(int code, String value) {
        this.code = code;
        this.value = value;
    }

    /**
     *
     * <p><b>方法描述：</b>获取字符串类型code值</p>
     * @return code值
     */
    public String stringValue() {
        return String.valueOf(this.code);
    }

    /**
     *
     * <p><b>方法描述：</b>获取int类型code值</p>
     * @return code值
     */
    public int intValue() {
        return this.code;
    }

    /**
     *
     * <p><b>方法描述：</b>获取long类型code值</p>
     * @return code值
     */
    public long longValue() {
        return this.code;
    }

    /**
     *
     * <p><b>方法描述：</b>获取boolean类型code值</p>
     * @return code值
     */
    public boolean booleanValue() {
        return this.code == 1 ? true : false;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
