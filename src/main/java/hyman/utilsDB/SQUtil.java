package hyman.utilsDB;

import hyman.utils.Constant;
import hyman.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;

/**
 * 1.PreparedStatement是预编译的,对于批量处理可以大大提高效率. 也叫JDBC存储过程。
 *
 * 2.使用 Statement 对象。在对数据库只执行一次性存取的时侯，用 Statement 对象进行处理。PreparedStatement 对象的开销比 Statement
 *      大，对于一次性操作并不会带来额外的好处。
 *
 * 3.statement每次执行sql语句，相关数据库都要执行sql语句的编译，preparedstatement是预编译得, preparedstatement支持批处理。
 *
 * 4、getParameterMetaData 获取参数元数据信息（即只有字段信息而没有值信息）。
 *
 * 5，ResultSetMetaData 获取结果集元数据信息（即只有字段信息而没有值信息 resultset.getMetaData()），而取值时使用：
 *      resultset.getObjectr(column)，ResultSetMetaData.getColumnLabel(i + 1)。
 */
public class SQUtil {

    private static final Logger LOG = LoggerFactory.getLogger(SQUtil.class);
    public static final String DB_NAME = Constant.getProperty("SysCode", "");
    private static String driver;
    private static String url;
    private static String name;
    private static String password;

    static {
        /**
         * Properties在java.util包下
         *  是JDK自带的用于配置连接数据库的配置文件类。其存储形式是Map的建值对。

         * Properties是专门为了处理放在resource中的后缀为properties的文件，调用
         *	  时直接使用其相对路径即可，即直接写文件名.后缀名

         * properties里面都是键值对

         * Properties实现了map接口

         * properties键值对都必须是String，文件中不能有中文，#符号表示注释

         * properties中"="左右都没有空格，值也没有引号，如果是空值则什么都不用写
         *
         * 是在Maven项目中的src/resources包内创建的，在包内new一个最普通的file
         * 文件，以点 .properties结尾。
         */

        try {
            //1.获得properties对象
            Properties cfg=new Properties();

            //2.获取db.properties文件的流
            InputStream in=SQUtil.class.getClassLoader().getResourceAsStream(
                    "db.properties");

            //3.调用load方法
            cfg.load(in);

            //4.通过cfg.getProperty(key)获得值
            driver=cfg.getProperty("jdbc.driver");
            url=cfg.getProperty("jdbc.url");
            name=cfg.getProperty("jdbc.name");
            password=cfg.getProperty("jdbc.password");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取数据库连接
     *
     * @param dbname
     *            数据库名称
     * @return conn
     * @throws ClassNotFoundException
     */
    private static Connection getConnection(final String dbname) throws SQLException, ClassNotFoundException {
        if (StringUtils.isBlank(dbname)) {
            return null;
        }
        Connection con=null;
        try {
            // 获取数据库文件路径
            String dbPath = getDbPath(dbname);

            Class.forName(driver);
            con=DriverManager.getConnection(url+dbPath,name,password);
            return con;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static String getCurrentDbPath(String dbName) {
        return getDbPath(dbName);
    }

    /**
     * <p>
     * <b>方法描述：</b>获取db的路径
     * </p>
     *
     * @param dbName
     *            数据库名称
     * @return 数据库文件地址
     */
    private static String getDbPath(String dbName) {
        String dbPath = dbName;
        if (!dbName.endsWith(".db")) {
            dbPath = dbName + ".db";
        }
        return Constant.getProperty("SQLitePath", "") + dbPath;
        // return SQUtil.class.getClassLoader().getResource("").getPath() +
        // dbPath;
    }

    /**
     * 获取一个数据连接声明
     *
     * @param conn
     *            数据库连接
     * @return statement
     * @throws SQLException
     */
    private static Statement getStatement(Connection conn) throws SQLException {
        if (null == conn) {
            return null;
        }
        return conn.createStatement();
    }

    /**
     * 根据数据库名称获取数据库连接声明
     *
     * @param dbname
     *            数据库名称
     * @return statement
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    private static Statement getStatementByDBName(String dbname) throws SQLException, ClassNotFoundException {
        if (StringUtils.isBlank(dbname)) {
            return null;
        }
        return getStatement(getConnection(dbname));
    }

    /**
     * 创建sqlite数据库
     *
     * @param dbname
     *            数据库名称
     * @return 0：失败；1：成功
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static int createDatabase(String dbname) throws ClassNotFoundException, SQLException {
        Statement statement = getStatementByDBName(dbname);
        if (null != statement) {
            return 1;
        }
        return 0;
    }

    /**
     * 关闭声明
     *
     * @param statement
     * @throws SQLException
     */
    private static void closeStatement(Statement statement) throws SQLException {
        if (null != statement && !statement.isClosed()) {
            Connection conn = statement.getConnection();
            statement.close();
            closeConnection(conn);
        }
    }

    /**
     * 关闭声明
     *
     * @param statement
     * @throws SQLException
     */
    private static void closeConnection(Connection conn) throws SQLException {
        if (null != conn && !conn.isClosed()) {
            conn.close();
        }
    }

    /**
     * 创建数据库表
     *
     * @param dbname
     *            数据库名称
     * @param sql
     *            创建语句
     * @return 0：创建失败；1：创建成功
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static int createTables(String dbname, String sql) throws ClassNotFoundException, SQLException {
        if (StringUtils.isBlank(sql)) {
            return 0;
        }
        Statement statement = getStatementByDBName(dbname);
        if (null != statement) {
            try {

                statement.executeUpdate(sql);

            } catch (Exception e) {


            } finally {

                closeStatement(statement);

            }

            return 1;
        }
        return 0;
    }

    /**
     * 插入数据
     *
     * @param dbname
     *            数据库名称
     * @param sql
     *            insert语句
     * @return 0：插入失败；1：插入成功
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static int insert(String dbname, String sql) throws ClassNotFoundException, SQLException {
        if (StringUtils.isBlank(sql)) {
            return 0;
        }
        Statement statement = getStatementByDBName(dbname);
        if (null != statement) {
            statement.executeUpdate(sql);
            closeStatement(statement);
            return 1;
        }
        return 0;
    }

    /**
     * 修改数据
     *
     * @param dbname
     *            数据库名称
     * @param sql
     *            update语句
     * @return 0：插入失败；1：插入成功
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static int update(String dbname, String sql) throws ClassNotFoundException, SQLException {
        if (StringUtils.isBlank(sql)) {
            return 0;
        }
        Statement statement = getStatementByDBName(dbname);
        if (null != statement) {
            statement.executeUpdate(sql);
            closeStatement(statement);
            return 1;
        }
        return 0;
    }

    /**
     * 批量插入数据
     *
     * @param dbname
     *            数据库名称
     * @param sql
     *            insert语句
     * @return 0：插入失败；1：插入成功
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static int insertBatch(String dbname, List<String> sqls) throws ClassNotFoundException, SQLException {
        if (null == sqls || sqls.isEmpty()) {
            return 0;
        }
        Connection conn = getConnection(dbname);

        if (null == conn) {
            return 0;
        }

        conn.setAutoCommit(false);
        Statement statement = getStatement(conn);

        if (null == statement) {
            closeConnection(conn);
            return 0;
        }

        for (String sql : sqls) {
            if (StringUtils.isNotBlank(sql)) {
                statement.executeUpdate(sql);
            }
        }
        closeStatement(statement);
        conn.commit();
        closeConnection(conn);
        return 1;
    }

    /**
     * 更新和删除
     *
     * @param dbname
     *            数据库名称
     * @param sql
     * @param parameters
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static int update(String dbname, String sql, Object[] parameters)
            throws ClassNotFoundException, SQLException {
        return execute(dbname, sql, parameters);
    }

    /**
     * 添加
     *
     * @param dbname
     *            数据库名称
     * @param sql
     * @param parameters
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static int insert(String dbname, String sql, Object[] parameters)
            throws ClassNotFoundException, SQLException {
        return execute(dbname, sql, parameters);
    }

    /**
     * 执行增删改
     *
     * @param dbname
     *            数据库名称
     * @param sql
     * @param parameters
     * @param type
     *            0为删改，1为增加
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static int execute(String dbname, String sql, Object[] parameters)
            throws SQLException, ClassNotFoundException {
        Connection conn = getConnection(dbname);
        PreparedStatement ps = null;
        int count = 0;
        if (conn != null) {
            try {

                ps = conn.prepareStatement(sql);
                if (null == ps) {
                    closeConnection(conn);
                    return count;
                }
                for (int i = 1; i <= parameters.length; i++) {
                    ps.setObject(i, parameters[i - 1]);
                }
                count = ps.executeUpdate();
            } catch (Exception e) {
                throw e;
            } finally {
                closeStatement(ps);
            }
        }
        return count;
    }

    /**
     * 执行查询，并将值反射到bean
     *
     * @param sql
     * @param parameters
     * @param clazz
     * @return
     * @throws Exception
     */
    public static <T> List<T> select(String dbname, String sql, Object[] parameters, Class<T> clazz)
            throws ClassNotFoundException, SQLException {
        if (null == parameters) {
            parameters = new Object[] {};
        }
        List<T> list = new ArrayList<T>();
        Connection conn = getConnection(dbname);
        if (null == conn) {
            return null;
        }
        PreparedStatement ps = null;
        ResultSet rs = null;
        ps = conn.prepareStatement(sql);
        LOG.debug("SQL:{} PARAMETERS:{}", sql, Arrays.toString(parameters));
        if (parameters != null && ps != null) {
            try {
                if (ps.getParameterMetaData().getParameterCount() == parameters.length) {
                    for (int i = 1; i <= parameters.length; i++) {
                        ps.setObject(i, parameters[i - 1]);
                    }
                }
                // 执行查询方法
                rs = ps.executeQuery();
                ResultSetMetaData rsmd = rs.getMetaData();
                List<String> columnList = new ArrayList<String>();
                for (int i = 0; i < rsmd.getColumnCount(); i++) {
                    columnList.add(rsmd.getColumnLabel(i + 1));
                }
                // 循环遍历记录
                while (rs.next()) {
                    // 创建封装记录的对象
                    T obj = clazz.newInstance();
                    // 遍历一个记录中的所有列
                    for (int i = 0; i < columnList.size(); i++) {
                        // 获取列名
                        String column = columnList.get(i);
                        // 根据列名创建set方法
                        String setMethd = "set" + column.substring(0, 1).toUpperCase() + column.substring(1);
                        // 获取clazz中所有方法对应的Method对象
                        Method[] ms = clazz.getMethods();
                        // 循环遍历ms
                        for (int j = 0; j < ms.length; j++) {
                            // 获取每一个method对象
                            Method m = ms[j];
                            // 判断m中对应的方法名和数据库中列名创建的set方法名是否形同
                            if (m.getName().equals(setMethd)) {
                                // 反调set方法封装数据
                                Object object = rs.getObject(column);
                                if (null != object) {
                                    m.invoke(obj, object);// 获取rs中对应的值，封装到obj中
                                }
                                break; // 提高效率
                            }
                        }
                    }
                    list.add(obj);
                }

            } catch (Exception e) {
            } finally {
                closeStatement(ps);
            }
        } else {
            closeConnection(conn);
        }
        return list;
    }

    /**
     * 执行查询，并将值反射到map
     *
     * @param sql
     * @param parameters
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static List<Map<String, Object>> select(String dbname, String sql, Object[] parameters)
            throws ClassNotFoundException, SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        Connection conn = getConnection(dbname);
        if (null == conn) {
            return null;
        }
        PreparedStatement ps = null;
        ResultSet rs = null;
        ps = conn.prepareStatement(sql);
        LOG.debug("SQL:{} PARAMETERS:{}", sql, Arrays.toString(parameters));
        if (ps != null) {

            try {
                if (parameters != null && ps.getParameterMetaData().getParameterCount() == parameters.length) {
                    for (int i = 1; i <= parameters.length; i++) {
                        ps.setObject(i, parameters[i - 1]);
                    }
                }
                // 执行查询方法
                rs = ps.executeQuery();
                ResultSetMetaData rsmd = rs.getMetaData();
                List<String> columnList = new ArrayList<String>();
                for (int i = 0; i < rsmd.getColumnCount(); i++) {
                    columnList.add(rsmd.getColumnLabel(i + 1));
                }
                // 循环遍历记录
                while (rs.next()) {
                    Map<String, Object> obj = new HashMap<>();
                    // 遍历一个记录中的所有列
                    for (int i = 0; i < columnList.size(); i++) {
                        // 获取列名
                        String column = columnList.get(i);
                        obj.put(column, rs.getObject(column));
                    }
                    list.add(obj);
                }

            } catch (Exception e) {
                throw e;
            } finally {
                closeStatement(ps);
            }
        } else {
            closeConnection(conn);
        }
        return list;
    }
}
