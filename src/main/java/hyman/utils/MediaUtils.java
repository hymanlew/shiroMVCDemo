package hyman.utils;

/**
 * <p><b>类描述：</b>验证文件格式</p>
 */
public class MediaUtils {

    /**
     * 文本格式
     */
    private static String[] baseType = {".rar", ".doc", ".docx", ".zip", ".pdf", ".txt", ".swf", ".wmv" };
    /**
     * 图片允许格式
     */
    private static String[] photoType = {".gif", ".png", ".jpg", ".jpeg", ".bmp" };
    /**
     * 音频允许格式
     */
    private static String[] audioType = {".wav", ".aif", ".mp3", ".wma", ".au", ".ram", ".mmf", ".amr"};
    /**
     * 压缩允许格式
     */
    private static String[] compressedType = { ".rar", ".zip", ".gz", ".z", ".arj"};
    /**
     * 动画允许格式
     */
    private static String[] cartoonType = { ".avi", ".mpg", ".mov", ".swf", ".flv"};


    /**
     *
     * <p><b>方法描述：</b>检查文件是否对应验证的文件格式</p>
     * @param fileName    文件名
     * @param typeArray   文件格式数组
     * @return  true 通过   false 不通过
     */
    public static boolean checkFileType(String fileName, String[] typeArray) {
        for (String type : typeArray) {
            if (fileName.toLowerCase().endsWith(type)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * <p><b>方法描述：</b>验证是否是文档文件</p>
     * @param fileName  文件名
     * @return true 是   false 不是
     */
    public static boolean isBaseFile(String fileName) {
        return checkFileType(fileName, baseType);
    }

    /**
     *
     * <p><b>方法描述：</b>验证是否是图形文件</p>
     * @param fileName  文件名
     * @return true 是   false 不是
     */
    public static boolean isImageType(String fileName) {
        return checkFileType(fileName, photoType);
    }

    /**
     *
     * <p><b>方法描述：</b>验证是否是声音文件</p>
     * @param fileName  文件名
     * @return true 是   false 不是
     */
    public static boolean isAudioType(String fileName) {
        return checkFileType(fileName, audioType);
    }

    /**
     *
     * <p><b>方法描述：</b>验证是否是压缩文件</p>
     * @param fileName  文件名
     * @return true 是   false 不是
     */
    public static boolean isCompressedType(String fileName) {
        return checkFileType(fileName, compressedType);
    }

    /**
     *
     * <p><b>方法描述：</b>验证是否是动画文件</p>
     * @param fileName  文件名
     * @return true 是   false 不是
     */
    public static boolean isCartoonType(String fileName) {
        return checkFileType(fileName, cartoonType);
    }
}
