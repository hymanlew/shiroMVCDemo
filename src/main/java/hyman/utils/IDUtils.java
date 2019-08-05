package hyman.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class IDUtils {

    private static IDUtils idutils = new IDUtils();

    public static IDUtils getIDUtils(){
        return idutils;
    }

    /** 上次生成ID的时间截 */
    private long lastTimestamp = -1L;

    /** 毫秒内序列(0~4095) */
    private long sequence = 0L;

    /** 序列在id中占的位数 */
    private final long sequenceBits = 12L;

    /** 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095) */
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     * @param lastTimestamp
     *            上次生成ID的时间截
     * @return 当前时间戳
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     * @return 当前时间(毫秒)
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }

    /**
     * 截取当前时间的年月日
     * @return 当前时间(日)
     */
    protected String getCurDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date  = sdf.format(new Date());
        String format = date.replaceAll("-", "");
        String var = format.substring(2, 8);
        return var;
    }

    /**
     * <p><b>方法描述：</b>生成随机六位数</p>
     * @return 六位随机数
     */
    protected String RandomNum(){
        Random random = new Random();
        String result="";
        for (int i=0;i<6;i++)
        {
            result+=random.nextInt(10);
        }
        return result;
    }

    public synchronized long nextCreateId(String areaCode){

        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format(
                    "Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        } else if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            // 毫秒内序列溢出
            if (sequence == 0) {
                // 阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {// 时间戳改变，毫秒内序列重置
            sequence = 0L;
        }
        // 如果是同一时间生成的，则进行毫秒内序列
        lastTimestamp = timestamp;
        return Long.valueOf(areaCode+getCurDate()+RandomNum());
    }

    public synchronized long nextProductId(String b,String n){

        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format(
                    "Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        } else if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            // 毫秒内序列溢出
            if (sequence == 0) {
                // 阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {// 时间戳改变，毫秒内序列重置
            sequence = 0L;
        }
        // 如果是同一时间生成的，则进行毫秒内序列
        lastTimestamp = timestamp;
        return Long.valueOf(b+getCurDate()+n+RandomNum());
    }

    public long getProductRandom(String s,String n){
        return nextProductId(s,n);
    }

    /**
     * <p><b>方法描述：</b>生成唯一默认id</p>
     * @return 验证id
     */
    public  long getCreateId(String areaCode){
        return nextCreateId(areaCode);
    }
}
