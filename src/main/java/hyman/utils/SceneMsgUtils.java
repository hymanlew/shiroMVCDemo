package hyman.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import hyman.entity.ResponseData;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 场景消息工具类 由应用发消息和SendSceneMsgUtil消息工具类实现功能一样  （2.3版本的使用）
 * @author
 */
public class SceneMsgUtils {


    /**
     * 场景节点完成，推送下一个节点完成发送消息工具类
     * @param request
     * @param detailurl 上一个场景节点消息连接详情
     * @param remark 场景节点结束消息备注
     * @param editUrl 业务编辑页面链接
     * @return
     */
    public static Integer sendSceneMsg(HttpServletRequest request, String detailurl, String remark, String editUrl){
        int state=0;
        if (request.getSession().getAttribute("msgGroupId")!=null && request.getSession().getAttribute("workId")!=null  && request.getSession().getAttribute("nodeId")!=null) {
            //商圈群组id
            String msgGroupId=request.getSession().getAttribute("msgGroupId").toString();
            //场景工作id
            String workId = request.getSession().getAttribute("workId").toString();
            //场景节点id
            String nodeId = request.getSession().getAttribute("nodeId").toString();
            try {
                //获取场景节点信息
                String result = HttpUtil.getData(Constant.getProperty("scene_work", "")+"?workId="+workId+"&nodeId="+nodeId+"&userId="+UserUtils.getCurrentUserId()+"&msgGroupId="+msgGroupId+"&editUrl="+ URLEncoder.encode(editUrl,"UTF-8")+"&detailUrl="+URLEncoder.encode(detailurl,"UTF-8"));
                JSONObject jsonObject = JSONObject.parseObject(result);
                if ("1".equals(jsonObject.get("State").toString())) {
                    JSONObject jsonData=JSONObject.parseObject(jsonObject.get("Data").toString());
                    JSONObject jsonContent=JSONObject.parseObject(jsonData.get("content").toString());
                    jsonContent.put("link", detailurl);
                    jsonContent.put("urlType", "1");
                    jsonContent.put("remark", remark);
                    jsonData.put("content", jsonContent);
                    jsonData.put("cmsClient", "yz");
                    //修改场景节点办理状态
                    state = Integer.parseInt(
                            JSON.parseObject(HttpUtil.getPostData(Constant.getProperty("send_msg", ""),jsonData.toJSONString()), new TypeReference<ResponseData>() {}).getState()
                            );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return state;
    }

    /**
     * 场景平台下项目本身业务逻辑发送消息
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
    public static Integer sendBusinessMsg(HttpServletRequest request, String nodeUrl, String detailurl, String link, String remark, List<String> nodeUsers, List<String> flowUsers, String msgState){
        int businessState=0;
        int state=0;
        if (request.getSession().getAttribute("circleId")!=null && request.getSession().getAttribute("msgGroupId")!=null && request.getSession().getAttribute("sceneId")!=null &&  request.getSession().getAttribute("workId")!=null  && request.getSession().getAttribute("nodeId")!=null) {
            //商圈id
            String circleId=request.getSession().getAttribute("circleId").toString();
            //商圈群组id
            String msgGroupId=request.getSession().getAttribute("msgGroupId").toString();
            //场景id
            String sceneId=request.getSession().getAttribute("sceneId").toString();
            //场景工作id
            String workId = request.getSession().getAttribute("workId").toString();
            //场景节点id
            String nodeId = request.getSession().getAttribute("nodeId").toString();
            //当前登录者id
            String userId=UserUtils.getCurrentUserId();
            //发送项目业务本身消息
            JSONObject jsonMsg=new JSONObject();
            //消息类型
            jsonMsg.put("flag", "5");
            //消息场景标题
            jsonMsg.put("title", "场景工作");
            //消息推送主题内容
            jsonMsg.put("msgInfo", remark);
            //消息节点id
            jsonMsg.put("nodeId", nodeId);
            //消息工作id
            jsonMsg.put("workId", workId);
            //场景id
            jsonMsg.put("sceneId", sceneId);
            //节点人，消息推送办理人 node_users可以多人 拥有办理权限
            jsonMsg.put("node_users", nodeUsers);
            //流程下的所有人包括节点人，只有查看消息权限
            jsonMsg.put("flow_users", flowUsers);
            //消息节点办理连接
            jsonMsg.put("nodeurl", nodeUrl);
            //在消息体内节点参与人员查看的业务连接
            jsonMsg.put("detailurl", detailurl);
            //上一个节点的详情根据业务判断有可能和detailurl相同
            jsonMsg.put("link", link);
            //发送消息时间
            jsonMsg.put("time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            //节点类型 业务类型消息5
            jsonMsg.put("node_type", "5");
            //消息办理状态0办理；1：详情    每次发消息同时修改上一消息的办理状态
            jsonMsg.put("state", msgState);
            //商圈id
            jsonMsg.put("circleId", circleId);
            JSONObject jsonData=new JSONObject();
            jsonData.put("content", jsonMsg);
            jsonData.put("cmsClient", "yz");
            //发送消息人id
            jsonData.put("send_id", userId);
            //消息内容类型
            jsonData.put("content_type", "21");
            //商圈id
            jsonData.put("group_id", circleId);
            //消息id
            jsonData.put("msg_id", IDWorker.getUUID());
            try {
                //调取消息接口发送消息
                ResponseData data = JSON.parseObject(HttpUtil.getPostData(Constant.getProperty("send_msg", ""),jsonData.toJSONString()), new TypeReference<ResponseData>() {});
                businessState = Integer.parseInt(data.getState());
                //调取客户端接口修改场景消息办理状态
                if (businessState>0) {
                    ResponseData responseData = JSON.parseObject(HttpUtil.getData(Constant.getProperty("msg_state", "")+"?msgGroupId="+msgGroupId), new TypeReference<ResponseData>() {});
                    state = Integer.parseInt(responseData.getState());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return state;
    }
}
