package hyman.aop;

import com.alibaba.fastjson.JSON;
import hyman.utils.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class CmeLogAop {


    Logger logger = LoggerFactory.getLogger(CmeLogAop.class);

    //@Autowired
    //private ICmsUserService cmsUserInter;

//    @Autowired
//	private AmqpTemplate amqpTemplate;

//    @Autowired
//    private JmsTemplate jmsTemplate;

    /**
     * @Value("${some.key:my default value}")
     * private String stringWithDefaultValue;
     *
     * some.key 没有设置值，stringWithDefaultValue 变量值将会被设置成 my default value 。
     * 如果默认值设为空，也将会被设置成默认值。
     *
     * @Value("${some.key:true}")
     * private boolean booleanWithDefaultValue;
     */

    @Value("${appId:未配置}")
    private String appId;

    @Value("${appName:未配置}")
    private String appName;

    @Value("${frame.type:未配置}")
    private  String frameType;

    @Value("${data.monitor.url:false}")
    private String dataMonitorUrl;

    @Value("${data.monitor.switch:true}")
    private  String isOpenDataMonitor;

    /**
     * 计算方法执行时间
     */
    ThreadLocal<Long> executeTime = new ThreadLocal<Long>();


    /**
     * 项目执行状态1成功0失败
     */
    ThreadLocal<Integer> executeState = new ThreadLocal<Integer>();


    /**
     * 用于生成操作日志的唯一标识，用于业务流程审计日志调用
     */
    public static ThreadLocal<String> serviceLog = new ThreadLocal<String>();


    /**
     * 声明AOP切入点，凡是使用了XXXOperateLog的方法均被拦截
     */
    //@Pointcut("@annotation(com.common.aspect.log.CmeSystemLog)")
    @Pointcut
    public void log() {
        System.out.println("我是一个切入点");
    }

    /**
     * 在所有标注@Log的地方切入
     * @param joinPoint
     */
    @Before("log()")
    public void beforeExec(JoinPoint joinPoint) {

        executeTime.set(System.currentTimeMillis());
    }

    /**
     * AfterReturning 增强处理将在目标方法正常完成后被织入
     * @param joinPoint
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    //@After("@annotation(com.cms.common.aspect.log.CmeSystemLog)")
    public void afterExec(JoinPoint joinPoint) throws NoSuchMethodException, SecurityException {

        executeTime.set(System.currentTimeMillis());

//    	System.out.println("--------------" + JSON.toJSONString(rvt));
    }

    /**
     * 环绕通知方法
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("log()")
    @SuppressWarnings("rawtypes")
    public Object doWriteLog(ProceedingJoinPoint pjp) throws Throwable {

        if (isOpenDataMonitor.equals("true")) {

            //获取request
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

            //获取操作平台id
            String optPfId= request.getSession().getAttribute("opt_pf_id") == null ? "未知" : request.getSession().getAttribute("opt_pf_id").toString();

            Object obj = null;

            // 拦截的实体类
            Object target = pjp.getTarget();

            MethodSignature methodSignature = (MethodSignature) pjp.getSignature();

            // 拦截的方法名称
            String methodName = methodSignature.getName();

            String[] parameNames = methodSignature.getParameterNames();

            // 拦截的方法参数值
            Object[] args = pjp.getArgs();

            // 拦截的参数类型
            Class[] parameterTypes = ((MethodSignature) pjp.getSignature()).getMethod().getParameterTypes();

            //需要转换成Json的HashMap
            Map<String, Object> paramMaps = new HashMap<String, Object>();

            // 获得被拦截的方法
            Method method = target.getClass().getMethod(methodName, parameterTypes);

            if (null != method) {

                // 判断是否包含自定义的注解
                if (method.isAnnotationPresent(CmeSystemLog.class)) {

                    //获取自定义注解实体
                    CmeSystemLog systemLog = method.getAnnotation(CmeSystemLog.class);

                    //日志类实体类

                    //循环获得所有参数对象
                    for(int i=0; i<args.length; i++){
                        if (null != args[i]) {
                            paramMaps.put(parameNames[i].toString(), args[i]);
                        }else {
                            paramMaps.put(parameNames[i].toString(), "空参数");
                        }
                    }

                    //设置方法参数及参数值
                    try {
                        // 执行该方法
                        obj = pjp.proceed();
                        //设置方法执行状态


                    } catch (Exception e) {

                        //设置方法执行状态

                    }finally {

                        //设置方法结束时间

                    }

                    //设置方法执行花费的时间

                    //发送到消息队列
//    				amqpTemplate.convertAndSend(RabbitMqConstant.LOG_EXCHANGE,RabbitMqConstant.ADD_LOG_ROUTING_KEY,cmsLog);
//    				jmsTemplate.convertAndSend(new ActiveMQTopic(ActiveMqConstant.TOPIC_ADD_LOG), JSON.toJSONString(cmsLog));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //此处调用.net接口，存储数据
                            if (!dataMonitorUrl.equals("false")) {
                                Map<String, Object> param = new HashMap<String, Object>();
                                param.put("data", JSON.toJSONString(new Object()));
                                HttpUtil.post(dataMonitorUrl, param, 5000);
                                logger.debug("数据监控生成数据===》" + JSON.toJSONString(new Object()));
                            }
                        }
                    }).run();
                }
            }
            return obj;
        }
        return pjp.proceed();
    }
}
