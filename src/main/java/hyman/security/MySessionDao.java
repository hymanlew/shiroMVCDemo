package hyman.security;

import hyman.entity.Sessions;
import hyman.utils.SerializableUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.ValidatingSession;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.mybatis.spring.SqlSessionTemplate;

import javax.annotation.Resource;
import java.io.Serializable;

/**
 * Shiro提供了完整的企业级会话管理功能，不依赖于底层容器（如web容器tomcat），不管JavaSE还是JavaEE环境都可以使用，提供了会话管
 * 理、会话事件监听、会话存储/持久化、容器无关的集群、失效/过期支持、对Web 的透明支持、SSO 单点登录的支持等特性。
 *
 * 会话监听器用于监听会话创建、过期及停止事件（start，stop，expiration）。
 *
 * SessionDao：
 * • AbstractSessionDAO 提供了SessionDAO的基础实现，如生成会话ID等
 * • CachingSessionDAO 提供了对开发者透明的会话缓存的功能，需要设置相应的CacheManager
 * • MemorySessionDAO 直接在内存中进行会话维护（默认的）
 * • EnterpriseCacheSessionDAO 提供了缓存功能的会话维护，默认情况下使用MapCache实现，内部使用ConcurrentHashMap保存缓存的会话。
 *
 * 但是要注意：
 * SecurityUtils.getSubject() 是每个请求创建一个Subject，但其内部经过 AbstractShiroFilter 转换为同一个 Subject，但是不同的
 * 客户端还是会生成不同的 Subject，即每次 getsession 都会不同, Subject 生成后并保存到 ThreadContext的 resources
 * （ThreadLocal<Map<Object, Object>>）变量中，也就是一个 http 请求一个 subject，并绑定到当前线程。
 *
 * 如此每个不同的客户端都会生成不同的 session，也就不能实现单点登录。如果只是将 session 存入到 redis 中，倒不如直接使用
 * SpringSession（SpringHttpSessionConfiguration），因为 Spring Session 就是用 Redis来实现的。
 */
public class MySessionDao extends EnterpriseCacheSessionDAO{
    /**
     * 此 sessiondao 是项目启动后就开始运行了。
     */
    @Resource(name="sqlSessionTemplate")
    private SqlSessionTemplate sqlSessionTemplate;

    @Override
    protected Serializable doCreate(Session session) {
        // 生成session的id，是序列化处理之后生成的，只需要调用一次即可。重复调用会生成不同的 id。
        Serializable sessionId = generateSessionId(session);
        // 给Session设定id
        assignSessionId(session, sessionId);

        Sessions ses = new Sessions();
        ses.setId(sessionId.toString());
        ses.setSession(SerializableUtils.serializ(session));

        // shiro 启动时就会先生成一个 session，而此时 user是空的，所以不能在这里拿到 userId。
        sqlSessionTemplate.insert("sessionsMapper.insertSelective", ses);

        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        Sessions ses = sqlSessionTemplate.selectOne("sessionsMapper.selectByPrimaryKey",sessionId.toString());
        if (null == ses){
            return null;
        }
        return SerializableUtils.deserializ(ses.getSession());
    }

    @Override
    protected void doUpdate(Session session) {
        // 当是ValidatingSession 无效的情况下，直接退出
        if (session instanceof ValidatingSession
                && !((ValidatingSession) session).isValid()) {
            return;
        }
        Sessions ses = sqlSessionTemplate.selectOne("sessionsMapper.selectByPrimaryKey",session.getId().toString());
        String username = String.valueOf(session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY));
        ses.setSession(SerializableUtils.serializ(session));
        ses.setUsername(username);
        Object o = session.getAttribute("userId");
        if(o != null){
            Integer userid = (int)o;
            ses.setUserId(userid);
        }
        sqlSessionTemplate.update("sessionsMapper.updateByPrimaryKeySelective",ses);
    }

    @Override
    protected void doDelete(Session session) {
        sqlSessionTemplate.delete("sessionsMapper.deleteByPrimaryKey",session.getId().toString());
    }
}
