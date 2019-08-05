package hyman.utilsDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBAnalysis {

    private Connection connection;

    private DBAnalysis(String connStr, String db, String username, String password) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection(connStr + db, username, password);
    }

    private static DBAnalysis instance = null;

    private static DBAnalysis getInstance(String connStr, String db, String username, String password) throws SQLException, ClassNotFoundException {
        if (instance == null) {
            instance = new DBAnalysis(connStr, db, username, password);
        }
        return instance;
    }

    private static Connection getConnection(String connStr, String db, String username, String password) throws SQLException, ClassNotFoundException {
        return getInstance(connStr, db, username, password).connection;
    }

    /**
     * 获取表的主键
     * @param conn 数据库连接
     * @param tableName  表名
     * @return 表中的主键
     * @throws SQLException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static List getPks(Connection conn, String tableName) throws SQLException {
        List pks = new ArrayList();
        ResultSet rsPks = conn.getMetaData().getPrimaryKeys(null, null, tableName);

        while (rsPks.next()) {
            pks.add(rsPks.getString("COLUMN_NAME"));
        }
        rsPks.close(); //关闭
        return pks;
    }

    /**
     *  获取所有的列信息
     * @param conn 数据库连接
     * @param tableName 表名
     * @return 列的详细信息
     * @throws SQLException
     */
    @SuppressWarnings("rawtypes")
    private static List<Column> getColumns(Connection conn,String tableName) throws SQLException {

        /**
         * ResultSetMetaData，是得到结果集的结构，比如字段数、字段名等。有两个方法获取字段名称:
         *
         * 一、getColumnName(int index)，这个方法获取的是该字段在表内的名称。
         * 二、getConlumnLabel(int index)，这个方法获取的是你在语句中要求的该字段的名称
         */
        List<Column> cols = new ArrayList<Column>();
        //获取这个表的主键 ，并存储在list中
        List pks = getPks(conn,tableName);
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from " + tableName);//此处需要优化 limit 1 top 1 rownum <= 1  根据不同数据库
        ResultSetMetaData rsCols = rs.getMetaData();
        int columnCount = rsCols.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            Column col = new Column();
            col.setTableName(rsCols.getTableName(i));
            col.setName(rsCols.getColumnName(i));
            col.setType(rsCols.getColumnTypeName(i));
            col.setPk(pks.contains(rsCols.getColumnName(i)) ? 1 : 0);
            col.setLength(rsCols.getColumnDisplaySize(i));
            col.setNotNull(rsCols.isNullable(i) == 0 ? 1 : 0);
            cols.add(col);
        }
        rs.close();
        stmt.close();
        return cols;
    }

    /**
     * 获取所有表信息
     * @param connStr  数据库连接字符串
     * @param db 连接的库
     * @param username  数据库用户名
     * @param password   数据库密码
     * @return  库中表信息
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static List<Table> collectAllTables(String connStr, String db, String username, String password) throws SQLException, ClassNotFoundException {
        Connection conn = getConnection(connStr, db, username, password);
        return collectAllTables(conn,db);
    }

    /**
     *  获取所有表信息
     * @param conn 数据库连接 s
     * @param db 数据库
     * @return  库中表信息
     * @throws SQLException
     */
    public static List<Table> collectAllTables(Connection conn,String db) throws SQLException {

        /**
         * DatabaseMetaData 类是 java.sql 包中的类，利用它可以获取我们连接到的数据库的结构、存储等很多信息。如：
         * 1、数据库与用户，数据库标识符以及函数与存储过程。
         * 2、数据库限制。
         * 3、数据库支持不支持的功能。
         * 4、架构、编目、表、列和视图等。
         *
         * 通过调用DatabaseMetaData的各种方法，程序可以动态的了解一个数据库。这个类中的方法非常的多。
         */
        DatabaseMetaData dmd = conn.getMetaData();

        //获取库中的所有表
        ResultSet rsTables = dmd.getTables(null, null, null, new String[]{"TABLE"});
        List<Table> tables = new ArrayList<Table>();
        //将表存到list中
        while (rsTables.next()) {
            Table tb = new Table();
            tb.setSpace(db);
            //获取表名称
            String tbName = rsTables.getString("TABLE_NAME");
            tb.setName(tbName);

            //获取表中的字段及其类型
            List<Column> cols = getColumns(conn,tbName);
            tb.setColumns(cols);
            tables.add(tb);
        }
        rsTables.close();
        return tables;//connection未关闭
    }
}
