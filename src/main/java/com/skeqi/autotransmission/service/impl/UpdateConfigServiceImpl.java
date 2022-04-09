package com.skeqi.autotransmission.service.impl;

import com.skeqi.autotransmission.constant.CommonLogConstant;
import com.skeqi.autotransmission.model.Header;
import com.skeqi.autotransmission.model.vo.ResultVo;
import com.skeqi.autotransmission.service.UpdateConfigService;
import com.trilead.ssh2.Connection;
import com.trilead.ssh2.SCPClient;
import com.trilead.ssh2.Session;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

/**
 * @author CHUNHAO LIU
 */
@Service
public class UpdateConfigServiceImpl implements UpdateConfigService {

    /**
     * 从配置文件之中读取SVN服务器IP
     */
    @Value("${svnServerIP}")
    private String svnServiceIp;

    /**
     * 从配置文件之中读取SVN服务器用户名
     */
    @Value("${svnServerUserName}")
    private String svnServerUserName;

    /**
     * 从配置文件之中读取SVN服务器密码
     */
    @Value("${svnServerPassWord}")
    private String svnServerPassWord;

    /**
     * 从配置文件之中读取SVN权限文件名称
     */
    @Value("${svnAccessFileName}")
    private String svnAccessFileName;

    /**
     * 从配置文件之中读取SVN权限文件名称
     */
    @Value("${svnAuthFileName}")
    private String svnAuthFileName;

    /**
     * 从配置文件之中读取SVN服务器中权限文件路径
     */
    @Value("${svnAccessFilePath}")
    private String svnAccessFilePath;

    /**
     * 从配置文件之中读取本地权限文件路径
     */
    @Value("${localAccessFilePath}")
    private String localAccessFilePath;

    /**
     * 从配置文件之中读取更改文件文件命令
     */
    @Value("${svnRemoveAccessFileCommand}")
    private String svnRemoveAccessFileCommand;


    @Override
    public ResultVo<String> updateAccessFile(Map<String, Map<String, List<String>>> updateMap) {
        Connection isConnect = connectService(svnServiceIp, svnServerUserName, svnServerPassWord);
        try {
            if (null != isConnect) {
                //获取文件
                Boolean isGetFile = getFile(isConnect, svnAccessFilePath + svnAccessFileName, localAccessFilePath);
                if (isGetFile) {
                    //将本次修改的与服务器上的进行整合
                    fileDeal(updateMap);
                    //将修改的文件上传到服务器
                    Boolean isPutFile = putFile(isConnect, localAccessFilePath + svnAccessFileName, svnAccessFilePath, svnRemoveAccessFileCommand);
                    if (isPutFile) {
                        //成功将文件上传至服务器,整体流程结束。
                        return new ResultVo<>(new Header().success(), CommonLogConstant.UPDATE_SUCCESS);
                    } else {
                        //记录日志：文件上传至服务器失败。
                    }
                } else {
                    //记录日志：为获取到文件
                }
            } else {
                //记录日志:SVN服务器链接异常
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭与服务器的链接
            isConnect.close();
        }


        return new ResultVo<>(new Header().fail(), CommonLogConstant.UPDATE_FAIL);

    }

    @Override
    public ResultVo<Map<String, Map<String, List<String>>>> getAccessFile() {

        return new ResultVo<Map<String, Map<String, List<String>>>>().sucess(readAccessFile(localAccessFilePath + svnAccessFileName));
    }

    @Override
    public ResultVo<List<String>> getAuthFile() {
        Connection isConnect = connectService(svnServiceIp, svnServerUserName, svnServerPassWord);
        try {
            if (null != isConnect) {
                //获取文件
                Boolean isGetFile = getFile(isConnect, svnAccessFilePath + svnAuthFileName, localAccessFilePath);
                if (isGetFile) {
                    //获取成功,对文件进行处理。
                    return new ResultVo<List<String>>().sucess(readAuthFile(localAccessFilePath + svnAuthFileName));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            isConnect.close();
        }
        return new ResultVo<List<String>>().fail();
    }


    /**
     * 建立与服务器的链接
     *
     * @param serviceIp 服务器IP
     * @param userName  服务器用户名
     * @param password  服务器密码
     * @return
     */
    public Connection connectService(String serviceIp, String userName, String password) {

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
    public Boolean getFile(Connection conn, String filePath, String savePath) {

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

    public Boolean putFile(Connection conn, String localPath, String filePath, String cmd) {

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

    /**
     * 处理服务器上的文件,并与此次修改的文件进行对比整合。
     *
     * @param updateMap 本次修改的文件
     */
    public void fileDeal(Map<String, Map<String, List<String>>> updateMap) {

        String fileName = localAccessFilePath + svnAccessFileName;
        //读取配置文件并转化为数据集合.
        Map<String, Map<String, List<String>>> map = readAccessFile(fileName);

        //将修改的文件进行便利转化。
        for (Map.Entry<String, Map<String, List<String>>> first : updateMap.entrySet()) {
            String tagName = first.getKey();

            Map<String, List<String>> tagMap = first.getValue();
            //判断配置文件之中是否有此标签,如果没有，说明连标签都是新增的就不需要进行下一步操作
            if (!map.containsKey(tagName)) {
                map.put(tagName, tagMap);
                continue;
            }
            Map<String, List<String>> lineDataMap = map.get(tagName);
            for (Map.Entry<String, List<String>> second : tagMap.entrySet()) {
                String attributeName = second.getKey();
                //获取到此次变更的数据
                List<String> attributeList = second.getValue();
                //判断配置文件之中是否有此属性，如果没有,则说明属性都是新增的,直接加入,就没有必要进行下一步操作了.
                if (lineDataMap.containsKey(attributeName)) {
                    //后续用来记录更改内容
                }
                //说明是有此数据
                lineDataMap.put(attributeName, attributeList);
            }
            map.replace(tagName, map.get(tagName), lineDataMap);
        }
        //生成文件
        creteFile(map, fileName);
    }


    /**
     * 读取Access文件将文件转化为Map集合的形式格式
     *
     * @param fileName 文件路径以及名称
     * @return
     */
    public Map<String, Map<String, List<String>>> readAccessFile(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        Map<String, Map<String, List<String>>> fileMap = new HashMap<>();
        try {
            reader = new BufferedReader(new FileReader(file));
            String lineData;
            String labelName = null;

            Map<String, List<String>> contextMap = new HashMap<>();
            while ((lineData = reader.readLine()) != null) {
                //若同时包含这2个符号则说明是新的标签,组呀,目录呀之类的
                if (lineData.contains(CommonLogConstant.LEFT_BRACKETS) && lineData.contains(CommonLogConstant.RIGHT_BRACKETS)) {
                    //去除第一次的时候。不添加进集合之中
                    if (null != labelName) {
                        fileMap.put(labelName, contextMap);
                    }
                    //将新的标签赋值给临时标签变量名
                    labelName = lineData;
                    //重新初始化Map
                    contextMap = new HashMap<>();
                } else {
                    if ("".equals(lineData)) {
                        //说明是空白行,不用管，直接跳出就好
                        continue;
                    }
                    //进入else说明此行不是标签，是数据.需要整理成MAP存放,
                    String[] tempStrArr = lineData.split(CommonLogConstant.EQUIP);
                    List<String> data = Arrays.asList(tempStrArr[1].split(CommonLogConstant.COMMA));
                    contextMap.put(tempStrArr[0].trim(), data);
                }

            }
            //最后一次的数据
            fileMap.put(labelName, contextMap);
            reader.close();
            return fileMap;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return fileMap;
    }


    /**
     * 读取Auth文件将文件转化为List集合的形式格式
     *
     * @param fileName 文件路径以及名称
     * @return
     */
    public List<String> readAuthFile(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        List<String> authList = new ArrayList<>();
        try {
            reader = new BufferedReader(new FileReader(file));
            String lineData;

            while ((lineData = reader.readLine()) != null) {
                String[] tempStrArr = lineData.split(CommonLogConstant.COLON);
                authList.add(tempStrArr[0]);

            }
            reader.close();
            return authList;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

        return authList;
    }

    /**
     * 将Map集合的形式处理成SVN访问规则的形式
     */
    public Boolean creteFile(Map<String, Map<String, List<String>>> map, String localFilePath) {

        //临时变量,用于文件生成
        StringBuffer strBuffer = new StringBuffer();
        try {
            for (Map.Entry<String, Map<String, List<String>>> tag : map.entrySet()) {
                String tagName = tag.getKey();
                strBuffer.append(CommonLogConstant.NEW_LINE + tagName + CommonLogConstant.NEW_LINE);
                //获取到此次变更的数据
                Map<String, List<String>> tagList = tag.getValue();
                for (Map.Entry<String, List<String>> attribute : tagList.entrySet()) {
                    String attributeName = attribute.getKey();
                    //获取到此次变更的数据
                    List<String> attributeList = attribute.getValue();
                    String context = StringUtils.join(attributeList, CommonLogConstant.COMMA);
                    strBuffer.append(attributeName + CommonLogConstant.EQUIP + context.trim() + CommonLogConstant.NEW_LINE);
                }
            }
            FileOutputStream fos = new FileOutputStream(localFilePath);
            System.out.println();
            //把字符数据转换成字节数据
            byte[] bytes = strBuffer.toString().getBytes();
            //通过管道把数据写入文件
            fos.write(bytes);
            //回收  关闭管道
            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


}
