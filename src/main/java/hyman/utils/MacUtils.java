package hyman.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MAC地址工具
 *
 * @author ThinkGem
 * @version 2013-12-21
 */
public class MacUtils {


    private static final Logger logger = LoggerFactory.getLogger(MacUtils.class);

    /**
     * 获取当前操作系统名称. return 操作系统名称 例如:windows,Linux,Unix等.
     */
    public static String getOSName() {
        return System.getProperty("os.name").toLowerCase();
    }

    /**
     * 获取Unix网卡的mac地址.
     *
     * @return mac地址
     */
    public static String getUnixMACAddress() {
        logger.debug("getUnixMACAddress");
        String mac = null;
        BufferedReader bufferedReader = null;
        Process process = null;
        try {
            /**
             * Unix下的命令，一般取eth0作为本地主网卡 显示信息中包含有mac地址信息
             */
            process = Runtime.getRuntime().exec("ifconfig eth0");
            logger.debug("getUnixMACAddress:process" + process);
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            int index = -1;
            while ((line = bufferedReader.readLine()) != null) {
                /**
                 * 寻找标示字符串[hwaddr]
                 */
                index = line.toLowerCase().indexOf("hwaddr");
                logger.debug("getUnixMACAddress:hwaddr" + index);
                /**
                 * 找到了
                 */
                if (index != -1) {
                    /**
                     * 取出mac地址并去除2边空格
                     */
                    mac = line.substring(index + "hwaddr".length() + 1).trim();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.debug("getUnixMACAddress:异常" + e.getMessage());
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            bufferedReader = null;
            process = null;
        }

        return mac;
    }

    /**
     * 获取Linux网卡的mac地址.
     *
     * @return mac地址
     */
    public static String getLinuxMACAddress() {
        String mac = null;
        BufferedReader bufferedReader = null;
        Process process = null;
        try {
            /**
             * linux下的命令，一般取eth0作为本地主网卡 显示信息中包含有mac地址信息
             */
            process = Runtime.getRuntime().exec("ifconfig eth0");
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            int index = -1;
            while ((line = bufferedReader.readLine()) != null) {
                logger.debug("getLinuxMACAddress:while==" + index);
                index = line.toLowerCase().indexOf("硬件地址");
                /**
                 * 找到了
                 */
                if (index != -1) {
                    /**
                     * 取出mac地址并去除2边空格
                     */
                    mac = line.substring(index + 4).trim();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.debug("getLinuxMACAddress异常:" + e.getMessage());
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            bufferedReader = null;
            process = null;
        }

        // 取不到，试下Unix取发
        if (mac == null) {
            logger.debug("getLinuxMACAddress:mac==null");
            mac = getUnixMACAddress();
        }
        if (StringUtils.isBlank(mac)) {
            mac = getMacAddr();
        }
        logger.debug("mac:" + mac);
        return mac;
    }

    /**
     * 获取widnows网卡的mac地址.
     *
     * @return mac地址
     */
    public static String getWindowsMACAddress() {
        String mac = null;
        BufferedReader bufferedReader = null;
        Process process = null;
        try {
            /**
             * windows下的命令，显示信息中包含有mac地址信息
             */
            process = Runtime.getRuntime().exec("ipconfig /all");
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            int index = -1;
            while ((line = bufferedReader.readLine()) != null) {
                /**
                 * 寻找标示字符串[physical address]
                 */
                // index = line.toLowerCase().indexOf("physical address");
                // if (index != -1) {
                if (line.split("-").length == 6) {
                    index = line.indexOf(":");
                    if (index != -1) {
                        /**
                         * 取出mac地址并去除2边空格
                         */
                        mac = line.substring(index + 1).trim();
                    }
                    break;
                }
                index = line.toLowerCase().indexOf("物理地址");
                if (index != -1) {
                    index = line.indexOf(":");
                    if (index != -1) {
                        /**
                         * 取出mac地址并去除2边空格
                         */
                        mac = line.substring(index + 1).trim();
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            bufferedReader = null;
            process = null;
        }

        return mac;
    }

    /**
     * 获取网卡的mac地址自动区分window/linux/unix.
     *
     * @return mac地址
     */
    public static String getMac() {
        String os = getOSName();
        logger.debug("os:" + os);
        String mac = null;
        try {
            if (os.startsWith("windows")) {
                logger.debug("进入windows");
                mac = getWindowsMACAddress();
            } else if (os.startsWith("linux")) {
                logger.debug("进入linux");
                mac = getLinuxMACAddress();
            } else {
                logger.debug("进入其他");
                mac = getUnixMACAddress();
            }
        } catch (Exception e) {
            logger.debug("getMac:" + e.getMessage());
        }
        return mac == null ? "" : mac;
    }

    /**
     * 获取CPU序列号
     *
     * @return cpu序列号
     */
    @SuppressWarnings({ "resource", "unused" })
    public static String getCPU() {

        Process process;
        String serial = "";
        String os = getOSName();
        logger.debug("os:" + os);
        try {
            if (os.startsWith("windows")) {
                logger.debug("进入windows");
                try {
                    process = Runtime.getRuntime().exec(new String[] { "wmic", "cpu", "get", "ProcessorId" });
                    process.getOutputStream().close();
                    Scanner sc = new Scanner(process.getInputStream());
                    String property = sc.next();
                    serial = sc.next();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (os.startsWith("linux")) {
                logger.debug("进入linux");
            } else {
                logger.debug("进入其他");
            }
        } catch (Exception e) {
            logger.debug("getMac:" + e.getMessage());
        }

        return serial;
    }

    /**
     * 获取硬盘串口号
     *
     * @param drive 盘符号 "C"
     * @return 硬盘串口号
     */

    public static String getSerialNumber(String drive) {
        String result = "";
        try {
            File file = File.createTempFile("realhowto", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new java.io.FileWriter(file);
            String vbs = "Set objFSO = CreateObject(\"Scripting.FileSystemObject\")\n"
                    + "Set colDrives = objFSO.Drives\n" + "Set objDrive = colDrives.item(\"" + drive + "\")\n"
                    + "Wscript.Echo objDrive.SerialNumber"; // see note
            fw.write(vbs);
            fw.close();
            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                result += line;
            }
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.trim();
    }

    public static String getMacAddr() {
        String MACAddr = "";
        try {
            Process process = Runtime.getRuntime().exec("ifconfig -a");
            InputStreamReader ir = new InputStreamReader(process.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String line;
            Pattern macPattern = Pattern.compile("([0-9A-Fa-f]{2})(:[0-9A-Fa-f]{2}){5}");
            Matcher macMatcher;
            while ((line = input.readLine()) != null){
                logger.debug("MAC address = [" + line + "]");
                if (StringUtils.isBlank(line)) {
                    continue;
                }
                macMatcher = macPattern.matcher(line);
                boolean result = macMatcher.find();
                if (result)
                {
                    logger.debug("MACAddr = " + macMatcher.group(0) );
                    MACAddr = macMatcher.group(0).toUpperCase();
                    logger.debug("MACAddr = " +MACAddr );
                    break;
                }
            }
        } catch (java.io.IOException e) {
            logger.debug("IOException " + e.getMessage());
        }
        return MACAddr ;
    }

    /**
     * 测试用的main方法.
     *
     * @param argc
     *            运行参数.
     */
    public static void main(String[] argc) {
        String os = getOSName();
        System.out.println("os: " + os);
        if (os.startsWith("windows")) {
            String mac = getWindowsMACAddress();
            System.out.println("mac: " + mac);
        } else if (os.startsWith("linux")) {
            // String mac = getLinuxMACAddress();
            // System.out.println("mac: " + mac);
        } else {
            String mac = getUnixMACAddress();
            System.out.println("mac: " + mac);
        }
    }
}
