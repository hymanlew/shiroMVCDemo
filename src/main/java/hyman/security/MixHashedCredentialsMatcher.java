package hyman.security;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.codec.Hex;

// 混合密码验证，该类主要是为了处理 OAUTH 登录时，密码验证问题
public class MixHashedCredentialsMatcher extends HashedCredentialsMatcher {

    /**
     * oauth登录的密码前缀
     */
    public static final String OAUTH_PREFIX = "OAuth2:";

    @Override
    public boolean doCredentialsMatch(AuthenticationToken authcToken, AuthenticationInfo info) {

        UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
        Object accountCredentials = getCredentials(info);
        String pwd = String.valueOf(token.getPassword());

        if (pwd.length() == (OAUTH_PREFIX.length() + 32)) {
            //取出密码，直接 equals 比对字符串
            pwd = pwd.substring(OAUTH_PREFIX.length());
            return equals(Hex.decode(pwd), accountCredentials);
        }

        // 将密码加密与系统加密后的密码校验，内容一致就返回true,不一致就返回false
        return super.doCredentialsMatch(token, info);
    }
}
