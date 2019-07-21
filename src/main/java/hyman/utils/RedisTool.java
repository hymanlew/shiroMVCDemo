package hyman.utils;

import moreway.redis.RedisUtil;

import javax.servlet.http.HttpServletRequest;

public class RedisTool {

    public static String getPfId(HttpServletRequest request){
        //获取sessionId
        String sessionId=request.getSession().getId();
        //获取当前登陆者id
        String userId=UserUtils.getCurrentUserId();
        //获取平台id
        String pfId= RedisUtil.get("pfId"+sessionId+userId);
        return pfId;
    }
}
