package com.skeqi.autotransmission.util;

import com.skeqi.autotransmission.constant.CommonLogConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author SVN工具类
 */

@Component
public class SvnUtil {

    private static String symbol = "/";

    private static String svnUserName = SpringContextUtil.getPropertiesValue("svnUserName");

    private static String svnUserPassWord = SpringContextUtil.getPropertiesValue("svnUserPassword");

    private static String svnURL = SpringContextUtil.getPropertiesValue("svnURL");

    private static SVNClientManager clientManager;

    private static ISVNAuthenticationManager authManager;

    private static SVNRepository repository;

    private static String workSpace = SpringContextUtil.getPropertiesValue("workSpace");

    /**
     * 通过不同的协议初始化版本库
     */
    private static void setupLibrary() {
        DAVRepositoryFactory.setup();
        SVNRepositoryFactoryImpl.setup();
        FSRepositoryFactory.setup();
    }

    /**
     * 启动svn连接
     *
     * @throws SVNException 异常信息
     */
    public static void SvnUpload() throws SVNException {
        try {
            createDefaultAuthenticationManager(svnUserName, svnUserPassWord);
            authSvn(svnURL);
        } catch (SVNException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    /**
     * @param username svn用户名称
     * @param password svn用户密码
     * @throws SVNException 异常信息
     */
    private static void createDefaultAuthenticationManager(String username, String password) throws SVNException {
        try {
            // 身份验证
            authManager = SVNWCUtil.createDefaultAuthenticationManager(username, password.toCharArray());
        } catch (Exception e) {
            throw new RuntimeException("SVN身份认证失败：" + e.getMessage());
        }
    }

    /**
     * 验证登录svn
     *
     * @param svnUrl 用户svn的仓库地址
     * @throws SVNException 异常信息
     */
    private static void authSvn(String svnUrl) throws SVNException {
        // 初始化版本库
        setupLibrary();
        try {
            repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(svnUrl));
        } catch (SVNException e) {
            throw new RuntimeException("SVN创建库连接失败：" + e.getMessage());
        }

        // 创建身份验证管理器
        repository.setAuthenticationManager(authManager);
        DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(true);
        try {
            //创建SVN实例管理器
            clientManager = SVNClientManager.newInstance(options, authManager);
        } catch (Exception e) {
            throw new RuntimeException("SVN实例管理器创建失败：" + e.getMessage());
        }
    }

    /**
     * 添加文件和目录到版本控制下
     *
     * @param path 工作区路径
     * @throws SVNException 异常信息
     */
    private static void addEntry(File[] path) throws SVNException {

        clientManager.getWCClient().doAdd(path, true,
                false, false, SVNDepth.INFINITY, false, false, true);

    }


    /**
     * 将工作副本提交到svn
     *
     * @param fileArr       被提交的工作区路径
     * @param keepLocks     是否在SVN仓库中打开或不打开文件
     * @param commitMessage 提交信息
     * @return 返回信息
     * @throws SVNException 异常信息
     */
    private static SVNCommitInfo commit(File[] fileArr, boolean keepLocks, String commitMessage) throws SVNException {

        return clientManager.getCommitClient().doCommit(
                fileArr, keepLocks, commitMessage, null,
                null, true, false, SVNDepth.INFINITY);

    }

    /**
     * 创建svn文件夹
     *
     * @param url           svn地址
     * @param commitMessage 提交信息
     * @return 返回信息
     * @throws SVNException 异常信息
     */
    private static SVNCommitInfo makeDirectory(SVNURL url, String commitMessage) throws SVNException {
        try {
            return clientManager.getCommitClient().doMkDir(new SVNURL[]{url}, commitMessage);
        } catch (SVNException e) {
            throw new RuntimeException("SVN新建文件夹失败：" + e.getMessage());
        }
    }

    /**
     * 删除
     *
     * @param url           svn地址
     * @param commitMessage 提交信息
     * @return
     * @throws SVNException
     */
    private SVNCommitInfo delete(SVNURL url, String commitMessage) throws SVNException {
        try {
            return clientManager.getCommitClient().doDelete(new SVNURL[]{url}, commitMessage);
        } catch (SVNException e) {
            throw new RuntimeException("SVN删除文件失败：" + e.getMessage());
        }
    }

    /**
     * 确定path是否是一个工作空间
     *
     * @param path 文件路径
     * @return 返回信息
     * @throws SVNException 异常信息
     */
    private static boolean isWorkingCopy(File path) throws SVNException {
        if (!path.exists()) {
            return false;
        }
        try {
            if (null == SVNWCUtil.getWorkingCopyRoot(path, false)) {
                return false;
            }
        } catch (SVNException e) {
            throw new RuntimeException("确定path是否是一个工作空间 失败：" + e.getMessage());
        }
        return true;
    }

    /**
     * 确定一个URL在SVN上是否存在
     *
     * @param url svn访问地址
     * @return 返回信息
     * @throws SVNException 异常信息
     */
    public static boolean isURLExist(SVNURL url) throws SVNException {

        SVNRepository svnRepository = SVNRepositoryFactory.create(url);
        svnRepository.setAuthenticationManager(authManager);
        SVNNodeKind nodeKind = svnRepository.checkPath("", -1);

        return nodeKind == SVNNodeKind.NONE ? false : true;

    }


    /**
     * 删除目录（文件夹）以及目录下的文件
     *
     * @param sPath 被删除目录的文件
     * @return 目录删除成功返回true，否则返回false
     */
    private static boolean deleteDirectory(String sPath) {
        //如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        //如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        //删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                //删除子文件
                if (files[i].isFile()) {
                    flag = deleteFile(files[i].getAbsolutePath());
                    if (!flag) {
                        break;
                    }
                } else {
                    //删除子目录
                    flag = deleteDirectory(files[i].getAbsolutePath());
                    if (!flag) {
                        break;
                    }
                }
            }
        }
        if (!flag) {
            return false;
        }
        //删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除单个文件
     *
     * @param sPath 被删除文件的文件
     * @return 单个文件删除成功返回true，否则返回false
     */
    private static boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    /**
     * 根据路径删除指定的目录或文件，无论存在与否
     *
     * @param sPath 要删除的目录或文件
     * @return 删除成功返回 true，否则返回 false。
     */
    private boolean DeleteFolder(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 判断目录或文件是否存在 不存在返回 false
        if (!file.exists()) {
            return flag;
        } else {
            // 判断是否为文件
            if (file.isFile()) {
                // 为文件时调用删除文件方法
                return deleteFile(file.getAbsolutePath());
            } else {
                // 为目录时调用删除目录方法
                return deleteDirectory(file.getAbsolutePath());
            }
        }
    }

    /**
     * 更新SVN工作区
     *
     * @param wcPath           工作区路径
     * @param updateToRevision 更新版本
     * @param depth            update的深度：目录、子目录、文件
     * @return 返回信息
     * @throws SVNException 异常信息
     */
    public static long updateWorkSpace(File wcPath, SVNRevision updateToRevision, SVNDepth depth) throws SVNException {
        SVNUpdateClient updateClient = clientManager.getUpdateClient();
        updateClient.setIgnoreExternals(false);

        return updateClient.doUpdate(wcPath, updateToRevision, depth, false, false);
    }

    /**
     * SVN仓库文件检出
     *
     * @param url      文件url
     * @param revision 检出版本
     * @param destPath 目标路径
     * @param depth    checkout的深度，目录、子目录、文件
     * @return 返回信息
     * @throws SVNException 异常信息
     */
    private static long checkout(SVNURL url, SVNRevision revision, File destPath, SVNDepth depth) throws SVNException {
        SVNUpdateClient updateClient = clientManager.getUpdateClient();
        updateClient.setIgnoreExternals(false);

        return updateClient.doCheckout(url, destPath, revision, revision, depth, false);
    }

    /**
     * 循环删除.svn目录
     *
     * @param spath
     */
    private static void deletePointSVN(String spath) {
        File wc = new File(spath);
        File[] files = wc.listFiles();
        if (files != null) {
            for (File sub : files) {
                if (sub.isDirectory() && sub.getName().equals(".svn")) {
                    deleteDirectory(sub.getAbsolutePath());
                    continue;
                }
                if (sub.isDirectory()) {
                    deletePointSVN(sub.getAbsolutePath());
                }
            }
        }
    }

    /**
     * @param workspace     文件工作空间路径
     * @param filepath      上传的文件地址 以"/"开头的路径，用于在svn上新建对应文件夹
     * @param fileNameArr   上传的文件名称
     * @param commitMessage 提交信息
     * @throws SVNException 异常信息
     */
    public static void upload(String workspace, String filepath, String[] fileNameArr, String commitMessage) throws SVNException {


        File temporaryFilePath = new File(filepath);
        String temporaryStringPath = temporaryFilePath.getPath();
        List<File> fileList = new ArrayList<>();

        //检查文件夹在svn中是否存在,如果不存在则新增到集合之中
        while (!isURLExist(SVNURL.parseURIEncoded(svnURL + temporaryStringPath))) {
            System.out.println("before:" + svnURL + temporaryStringPath);
            //将本地路径的该文件夹存入到集合之中
            fileList.add(new File(workSpace + temporaryStringPath));

            //去上一层再次判断,直到文件夹在SVN存在为止.
            temporaryStringPath = temporaryFilePath.getParent();
            temporaryFilePath = new File(temporaryStringPath);
        }

        for (int i = 0; i < fileNameArr.length; i++) {
            String fileName = fileNameArr[i];

            String workFilePath = workspace + symbol + fileName;
            //将本次的内容提交到集合之中。
            File file = new File(workFilePath);
            fileList.add(file);
        }

        if ((fileList.size() == 0)) {
            return;
        }

        File[] fileArr = new File[fileList.size()];
        fileArr = fileList.toArray(fileArr);

        for (int i = 0; i < fileArr.length; i++) {
            System.out.println("地址:" + fileArr[i].getPath());

        }

        //将文件提交到版本控制之下
        addEntry(fileArr);
        //提交文件
        commit(fileArr, true, commitMessage);


    }

    /**
     * 删除svn上文件,
     *
     * @param filepath 上传的文件地址
     * @param filename 文件名称
     * @throws SVNException
     */
    public void deleteSvnFile(String filepath, String filename) throws SVNException {
        String svnFilePath = svnURL + symbol + filename;
        if (!"".equals(filepath)) {
            svnFilePath = svnURL + filepath + symbol + filename;
        }
        //检查文件在svn中是否存在
        boolean flag = isURLExist(SVNURL.parseURIEncoded(svnFilePath));
        if (flag) {
            delete(SVNURL.parseURIEncoded(svnFilePath), "删除文件：" + svnFilePath);
        }

    }
}
