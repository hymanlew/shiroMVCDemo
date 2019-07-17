package hyman.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import hyman.entity.ResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * 场景消息工具类     场景消息发送由客户端发送消息工具类 和SceneMsgUtils消息工具类实现功能一样（3.0版本以后的使用）
 * @author
 */
public class SendSceneMsgUtil {

    /**
     * Logger对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SendSceneMsgUtil.class);

    /**
     * 场景节点完成，推送下一个节点完成发送消息工具类 --大场景消息
     * @param request
     * @param detailurl 上一个场景节点消息连接详情
     * @param remark 场景节点结束消息备注
     * @param editUrl 业务编辑页面链接
     * @param urlType 页面链接类型 4:功能点连接
     * @return
     */
    public static Integer sendSceneMsg(HttpServletRequest request, String detailurl, String remark, String editUrl, String urlType){
        int state=0;
        if (request.getSession().getAttribute("msgGroupId")!=null && request.getSession().getAttribute("workId")!=null  && request.getSession().getAttribute("nodeId")!=null) {
            //商圈群组id
            String msgGroupId=request.getSession().getAttribute("msgGroupId").toString();
            LOGGER.info("大场景消息--商圈群组id:"+msgGroupId);
            //场景工作id
            String workId = request.getSession().getAttribute("workId").toString();
            LOGGER.info("大场景消息--场景工作id:"+workId);
            try {
                String url=Constant.getProperty("send_scene_big_msg", "")+"?workId="+workId+"&userId="+UserUtils.getCurrentUserId()+"&urlType="+urlType+
                        "&msgGroupId="+msgGroupId+"&editUrl="+ URLEncoder.encode(editUrl,"UTF-8")+"&pfId="+RedisTool.getPfId(request)+
                        "&detailUrl="+URLEncoder.encode(detailurl,"UTF-8")+"&link="+URLEncoder.encode(detailurl,"UTF-8")+"&remark="+URLEncoder.encode(remark,"UTF-8");
                LOGGER.info("大场景消息--调取客户端接口url:"+url);
                String result = HttpUtil.getData(url);
                LOGGER.info("大场景消息--调取客户端接口返回数据结果:"+result);
                state = Integer.parseInt(
                        JSON.parseObject(result, new TypeReference<ResponseData>() {}).getState()
                        );
                LOGGER.info("大场景消息--解析调取客户端接口返回数据结果状态码:"+state);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return state;
    }

    /**
     * 场景平台下项目本身业务逻辑发送消息--小场景消息
     * @param request
     * @param nodeUrl  节点办理的连接
     * @param detailurl 场景消息办理详情连接
     * @param link 场景消息办理详情连接
     * @param remark 场景消息内容
     * @param nodeUsers 消息节点办理人员集合
     * @param flowUsers 消息节点查看详情人员集合
     * @param msgState  业务场景消息状态0：未办理  ；1：已办理
     * @return
     */
    public static Integer sendBusinessMsg(HttpServletRequest request, String nodeUrl, String detailurl, String link,
                                          String remark, List<String> nodeUsers, List<String> flowUsers, String msgState)
                                    throws Exception{
        int state=0;
        int businessState=0;
        if ( request.getSession().getAttribute("msgGroupId")!=null &&  request.getSession().getAttribute("workId")!=null) {
            //商圈群组id
            String msgGroupId=request.getSession().getAttribute("msgGroupId").toString();
            LOGGER.info("小场景消息--商圈群组id:"+msgGroupId);
            //场景工作id
            String workId = request.getSession().getAttribute("workId").toString();
            LOGGER.info("小场景消息--场景工作id:"+workId);
            try {
                //发送小场景节点信息
                String url=Constant.getProperty("send_scene_small_msg", "")+"?workId="+workId+"&userId="+UserUtils.getCurrentUserId()+"&pfId="+RedisTool.getPfId(request)+"&msgInfo="+URLEncoder.encode(remark,"UTF-8")+"&nodeUsers="+URLEncoder.encode((nodeUsers.size()>0? JSONObject.toJSONString(nodeUsers):""),"UTF-8")+
                        "&flowUsers="+URLEncoder.encode((flowUsers.size()>0?JSONObject.toJSONString(flowUsers):""),"UTF-8")+"&nodeUrl="+URLEncoder.encode(nodeUrl,"UTF-8")+"&detailUrl="+URLEncoder.encode(detailurl,"UTF-8")+"&link="+URLEncoder.encode(link,"UTF-8")+"&workState="+msgState;
                LOGGER.info("小场景消息--调取客户端接口连接:"+url);
                String result = HttpUtil.getData(url);
                LOGGER.info("小场景消息--调取客户端接口返回数据结果:"+result);
                ResponseData data = JSON.parseObject(result, new TypeReference<ResponseData>() {});
                businessState = Integer.parseInt(data.getState());
                //调取客户端接口修改场景消息办理状态
                if (businessState>0) {
                    ResponseData responseData = JSON.parseObject(HttpUtil.getData(Constant.getProperty("msg_state", "")+"?msgGroupId="+msgGroupId), new TypeReference<ResponseData>() {});
                    state = Integer.parseInt(responseData.getState());
                }
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
        }
        return state;
    }
}
