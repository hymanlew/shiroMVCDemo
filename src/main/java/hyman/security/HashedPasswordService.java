package hyman.security;

import hyman.entity.User;
import org.apache.shiro.crypto.hash.Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.StringUtils;

// 用户密码业务层
public class HashedPasswordService {

    /**
     * Hash算法
     */
    private String hashAlgorithm;

    /**
     * Hash迭代s
     */
    private int hashIterations;

    public HashedPasswordService() {
        this.hashAlgorithm = null;
        this.hashIterations = 1;
    }
    public HashedPasswordService(String hashAlgorithmName) {
        this();
        if (!StringUtils.hasText(hashAlgorithmName)) {
            throw new IllegalArgumentException("hashAlgorithmName cannot be null or empty.");
        }
        this.hashAlgorithm = hashAlgorithmName;
    }

    public String encryptPassword(User user) {
        if (user == null || org.apache.commons.lang3.StringUtils.isBlank(user.getId().toString())
                || org.apache.commons.lang3.StringUtils.isBlank(user.getId().toString())
                || org.apache.commons.lang3.StringUtils.isBlank(user.getPassword())) {
            return null;
        }
        Hash hash = hashProvidedCredentials(user.getPassword().trim(), createByteSource(user.getId()), hashIterations);
        return hash.toHex();
    }

    // 返回字节数据资源
    protected ByteSource createByteSource(Object o) {
        return ByteSource.Util.bytes(o);
    }

    // 方法描述：获取 Hash 过的凭证
    protected Hash hashProvidedCredentials(Object credentials, Object salt, int hashIterations) {
        String hashAlgorithmName = assertHashAlgorithmName();
        return new SimpleHash(hashAlgorithmName, credentials, salt, hashIterations);
    }

    // 确认加密算法
    private String assertHashAlgorithmName() throws IllegalStateException {
        String hashAlgorithmName = getHashAlgorithmName();
        if (hashAlgorithmName == null) {
            String msg = "Required 'hashAlgorithmName' property has not been set.  This is required to execute "
                    + "the hashing algorithm.";
            throw new IllegalStateException(msg);
        }
        return hashAlgorithmName;
    }

    public String getHashAlgorithmName() {
        return hashAlgorithm;
    }
    public void setHashAlgorithmName(String hashAlgorithmName) {
        this.hashAlgorithm = hashAlgorithmName;
    }
    public int getHashIterations() {
        return hashIterations;
    }
    public void setHashIterations(int hashIterations) {
        this.hashIterations = hashIterations;
    }
}
