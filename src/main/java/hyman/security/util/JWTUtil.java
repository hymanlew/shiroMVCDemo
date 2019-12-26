package hyman.security.util;

import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.TextCodec;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * jwt 工具类
 * JJWT 是一个提供端到端的 JWT 创建和验证的 Java库，是在JVM上创建和验证JSON Web Token(JWTs)的库。
 * JJWT 是在 JVM 上创建和跨域身份验证的库，是基于JWT、JWS、JWE、JWK和JWA RFC规范的Java实现。
 *
 * @author hyman
 * @date 2019/6/6 9:13 下午
 */
@Component
public class JWTUtil {

    private static Logger log = LoggerFactory.getLogger(JWTUtil.class);

    /**
     * 加密SECRET，签名密钥
     */
    @Value("${spring.jwt.secret}")
    private static String secret;

    /**
     * 过期时间，超时秒数
     */
    @Value("${spring.jwt.expire-second}")
    private static Long expireSecond;

    /**
     * JWT加密的密匙
     */
    @Value("${spring.jwt.datakey}")
    private static String datakey;

    private static Map IDmap = new HashMap();

    /**
     * 创建token
     * @param map
     * @return
     */
    public static String createJWT(Map<String, Object> map) {

        // 签名算法，选择 SHA-256
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        // 将签名密钥常量字符串使用 base64 解码成字节数组
        byte[] secretBytes = DatatypeConverter.parseBase64Binary(secret);

        // 使用 HmacSHA256 签名算法生成一个 HS256的签名秘钥 Key。或使用 TextCodec 加密一个字符串
        String encode = TextCodec.BASE64.encode(secret);
        Key signingKey = new SecretKeySpec(secretBytes, signatureAlgorithm.getJcaName());

        // 添加构成 JWT 的参数
        Map<String, Object> header = new HashMap<>(2);
        header.put("alg", SignatureAlgorithm.HS256.getValue());
        header.put("typ", "JWT");
        Instant now = Instant.now();

        // 对主识别码进行单独加密
        String id = map.get("id").toString();
        IDmap.put(id, "");
        id = AESSecretUtil.encryptToStr(id, datakey);
        map.put("id", id);

        String jwt = Jwts.builder()
                .setClaims(map)
                .setHeader(header)
                .setExpiration(Date.from(now.plus(expireSecond, ChronoUnit.SECONDS)))
                .setIssuedAt(Date.from(now))
                .signWith(signatureAlgorithm, signingKey)
                .compact();

        return Base64.getEncoder().encodeToString(jwt.getBytes());
    }


    /**
     * 校验token
     * @param jwtToken token
     * @return boolean
     */
    public static String verify(String jwtToken) {

        Map map = null;
        try {
            map = getJWTData(jwtToken);
            //解密编号
            String decryptId = AESSecretUtil.decryptToStr((String)map.get("id"), datakey);

            if(IDmap.containsKey(decryptId)){
                map.put("id", decryptId);
            }
        } catch (ExpiredJwtException var3) {
            log.error(var3.getMessage(), var3);
        } catch (MalformedJwtException var4) {
            log.error(var4.getMessage(), var4);
        } catch (SignatureException var5) {
            log.error(var5.getMessage(), var5);
        } catch (Exception var6) {
            log.error(var6.getMessage(), var6);
        }
        return map!=null ? JSONObject.toJSONString(map):null;
    }

    /**
     * Token解密
     * @param jwtToken token
     * @return map
     */
    public static Map getJWTData(String jwtToken) {

        try {
            if (StringUtils.isNotBlank(jwtToken)) {
                byte[] b = Base64.getDecoder().decode(jwtToken);
                String base64jwt = new String(b);
                Map map = (Map) Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(secret)).parse(base64jwt).getBody();
                return map;
            }else {
                log.warn("json web token 为空");
            }
        } catch (ExpiredJwtException var4) {
            log.error(var4.getMessage(), var4);
        } catch (MalformedJwtException var5) {
            log.error(var5.getMessage(), var5);
        } catch (SignatureException var6) {
            log.error(var6.getMessage(), var6);
        } catch (Exception e) {
            log.error("JWT解析异常：可能因为token已经超时或非法token");
        }
        return null;
    }

    /**
     * 刷新token
     * @param token
     * @return
     */
    public static String refreshToken(String token) {
        return StringUtils.isNotEmpty(verify(token)) ? createJWT(getJWTData(token)) : null;
    }
}
