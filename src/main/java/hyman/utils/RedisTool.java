package hyman.utils;

import javax.servlet.http.HttpServletRequest;

public class RedisTool {

    /**
     * @Description:(获取平台id)
     * @param request
     * @return
     * @author:yankai@cmnec.com
     * @date:2018年11月19日
     */
    public static String getPfId(HttpServletRequest request){
        //获取sessionId
        String sessionId=request.getSession().getId();
        //获取当前登陆者id
        String userId=UserUtils.getCurrentUserId();
        //获取平台id
        String pfId=RedisUtil.get("pfId"+sessionId+userId);
        return pfId;
    }
}
