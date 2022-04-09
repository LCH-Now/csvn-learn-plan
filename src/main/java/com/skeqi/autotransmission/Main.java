package com.skeqi.autotransmission;



import com.trilead.ssh2.Connection;
import com.trilead.ssh2.SCPClient;
import com.trilead.ssh2.Session;

import java.io.*;

import java.sql.DriverManager;
import java.sql.SQLException;


/**
 * @author CHUNAHO LIU
 * 测试
 */
public class Main {

    public static void main(String[] args) {


        //Connection con=connectService("192.168.8.92","csvn","liuchunhao");
        //getFile(con,"/usr/local/software/csvn/data/csvn-production-hsqldb.script","D:/Work/SKEQI/Project/(oa-svn)/");
        //putFile(con,"D:/Work/SKEQI/Project/(oa-svn)/csvn-production-hsqldb.script","/usr/local/software/csvn/data/","cd /usr/local/software/csvn/data && rm -rf csvn-production-hsqldb.script");
       // Connection con=getConnection();


    }




    /**
     * 建立与服务器的链接
     *
     * @param serviceIp 服务器IP
     * @param userName  服务器用户名
     * @param password  服务器密码
     * @return
     */
    public static Connection connectService(String serviceIp, String userName, String password) {

        try {
            Connection conn = new Connection(serviceIp);
            conn.connect();
            boolean isAuthed = conn.authenticateWithPassword(userName, password);
            if (isAuthed) {
                //验证成功 返回链接
                return conn;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }


    /**
     * 获取服务器文件到本地
     *
     * @param conn     与服务器的链接
     * @param filePath 文件路径
     * @param savePath 保存本地的路径
     * @return
     */
    public static Boolean getFile(Connection conn, String filePath, String savePath) {

        try {
            //第一步:获取远程服务器文件到本地
            Session session = conn.openSession();
            SCPClient scpClient = conn.createSCPClient();
            scpClient.get(filePath, savePath);
            //关闭连接
            session.close();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public static Boolean putFile(Connection conn, String localPath, String filePath, String cmd) {

        try {
            //第一步：删掉旧版本文件
            Session session = conn.openSession();
            SCPClient scpClient = conn.createSCPClient();
            session.execCommand(cmd);
            session.close();

            //第二步：将修改后的本地文件放入远程服务器原路径中
            scpClient.put(localPath, filePath);
        } catch (IOException e) {
            e.printStackTrace();

            return false;
        }

        return true;
    }

//    public static Connection getConnection() {
//        java.sql.Connection con = null;
//
//        try {
//            Class.forName("org.hsqldb.jdbc.JDBCDriver");
//            System.out.println("HSQLDB JDBCDriver Loaded");
//            con = DriverManager.getConnection(
//                    "jdbc:hsqldb:hsql://192.168.8.92/csvn-production-hsqldb", "SA", "");
//            System.out.println("HSQLDB Connection Created");
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return con;
//    }



}
