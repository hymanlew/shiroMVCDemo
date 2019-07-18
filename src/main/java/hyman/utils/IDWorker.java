package hyman.utils;

import com.alibaba.fastjson.JSONObject;
import hyman.entity.ResponseData;
import org.apache.commons.lang3.RandomUtils;

import java.util.Date;

public class IDWorker {

    /**
     * 唯一识别码生成工具类对象
     */
    private static final IDWorker IDWORKER;

    static {
        IDWORKER = new IDWorker(Long.parseLong("1000"),
                Long.parseLong("1000"));
    }

    // ==============================Fields===========================================
    /** 开始时间截 (2017-03-15) */
    private final long twepoch = 1489567217840L;

    /** 机器id所占的位数 */
    private final long workerIdBits = 5L;

    /** 数据标识id所占的位数 */
    private final long datacenterIdBits = 5L;

    /** 支持的最大机器id，结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数) */
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);

    /** 支持的最大数据标识id，结果是31 */
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

    /** 序列在id中占的位数 */
    private final long sequenceBits = 12L;

    /** 机器ID向左移12位 */
    private final long workerIdShift = sequenceBits;

    /** 数据标识id向左移17位(12+5) */
    private final long datacenterIdShift = sequenceBits + workerIdBits;

    /** 时间截向左移22位(5+5+12) */
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

    /** 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095) */
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    /** 工作机器ID(0~31) */
    private long workerId;

    /** 数据中心ID(0~31) */
    private long datacenterId;

    /** 毫秒内序列(0~4095) */
    private long sequence = 0L;

    /** 上次生成ID的时间截 */
    private long lastTimestamp = -1L;

    // ==============================Constructors=====================================
    /**
     * 构造函数
     *
     * @param workerId
     *            工作ID (0~31)
     * @param datacenterId
     *            数据中心ID (0~31)
     */
    public IDWorker(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(
                    String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(
                    String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    // ==============================Methods==========================================
    /**
     * 获得下一个ID (该方法是线程安全的)
     *
     * @return SnowflakeId
     */
    public synchronized long nextId() {
        long timestamp = timeGen();

        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format(
                    "Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        // 如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            // 毫秒内序列溢出
            if (sequence == 0) {
                // 阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {// 时间戳改变，毫秒内序列重置
            sequence = 0L;
        }

        // 上次生成ID的时间截
        lastTimestamp = timestamp;

        // 移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - twepoch) << timestampLeftShift) //
                | (datacenterId << datacenterIdShift) //
                | (workerId << workerIdShift) //
                | sequence;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
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
     *
     * @return 当前时间(毫秒)
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }

    /**
     *
     * <p><b>方法描述：</b>唯一识别码生成</p>
     * @return 唯一识别码
     */
    public static String getUUID() {
        return String.valueOf(IDWORKER.nextId());
    }

    public static String getUUID(String tableName) {

        String id = Constant.getProperty("frameId", "acnid");
        String appId = Constant.getProperty("appId", "app001");
        String userIdentityId = UserUtils.getCurrentUserId();

        //TODO 从redis获取
        try {
            String idString = getIDString(id, tableName, appId);
            if (idString != null) {
                return userIdentityId + idString + randomIdentity();
            }else{
                return userIdentityId + id+"-"+appId+"000000000000guru"+randomIdentity();
            }
        } catch (Exception e) {
            return userIdentityId + id+"-"+appId+"000000000000guru"+randomIdentity();
        }

    }

    private static String randomIdentity() {
        return new Date().getTime()+String.valueOf(RandomUtils.nextInt(100000, 999999));
    }

    private static String getIDString(String id,String tableName,String appId){
        String resultString = RedisUtil.get(id+tableName);
        if (StringUtils.isBlank(resultString)||resultString == null) {
            String result = HttpClientUtils.httpGetRequest("http://cms.cmeplaza.com/iaiid-develop/api/identity.json?id="+id+"&tableName="+tableName+"&appId="+appId);
            ResponseData object = JSONObject.parseObject(result, ResponseData.class);
            if (object != null && object.getState().equals("1")) {
                RedisUtil.set(id+tableName,(String)object.getData());
                return (String) object.getData();
            }
        }else{
            return resultString;
        }
        return null;
    }
}
