package com.inno72.job.core.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.inno72.job.core.biz.AdminBiz;
import com.inno72.job.core.biz.ExecutorBiz;
import com.inno72.job.core.biz.impl.ExecutorBizImpl;
import com.inno72.job.core.log.JobFileAppender;
import com.inno72.job.core.rpc.netcom.NetComClientProxy;
import com.inno72.job.core.rpc.netcom.NetComServerFactory;
import com.inno72.job.core.thread.JobLogFileCleanThread;
import com.inno72.job.core.thread.JobThread;
import com.inno72.job.core.util.NetUtil;


public class JobExecutor implements ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(JobExecutor.class);

    // ---------------------- param ----------------------
    static private String adminAddresses;
    private String appName;
    private String ip;
    private int port;
    private String accessToken;
    private String logPath;
    private int logRetentionDays;

    public static String getAdminAddresses() {
    	return adminAddresses;
    }
    
    public void setAdminAddresses(String adminAddresses) {
    	JobExecutor.adminAddresses = adminAddresses;
    }
    public void setAppName(String appName) {
        this.appName = appName;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }
    public void setLogRetentionDays(int logRetentionDays) {
        this.logRetentionDays = logRetentionDays;
    }

    // ---------------------- applicationContext ----------------------
    private static ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        JobExecutor.applicationContext = applicationContext;
    }
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }


    // ---------------------- start + stop ----------------------
    public void start() throws Exception {
        // init admin-client
        initAdminBizList(adminAddresses, accessToken);

        // init logpath
        JobFileAppender.initLogPath(logPath);

        // init executor-server
        initExecutorServer(port, ip, appName, accessToken);

        // init JobLogFileCleanThread
        JobLogFileCleanThread.getInstance().start(logRetentionDays);
    }
    public void destroy(){
        // destory JobThreadRepository
        if (JobThreadRepository.size() > 0) {
            for (Map.Entry<Integer, JobThread> item: JobThreadRepository.entrySet()) {
                removeJobThread(item.getKey(), "Web容器销毁终止");
            }
            JobThreadRepository.clear();
        }

        // destory executor-server
        stopExecutorServer();

        // destory JobLogFileCleanThread
        JobLogFileCleanThread.getInstance().toStop();
    }


    // ---------------------- admin-client ----------------------
    private static List<AdminBiz> adminBizList;
    private static void initAdminBizList(String adminAddresses, String accessToken) throws Exception {
        if (adminAddresses!=null && adminAddresses.trim().length()>0) {
            for (String address: adminAddresses.trim().split(",")) {
                if (address!=null && address.trim().length()>0) {
                    String addressUrl = address.concat(AdminBiz.MAPPING);
                    AdminBiz adminBiz = (AdminBiz) new NetComClientProxy(AdminBiz.class, addressUrl, accessToken).getObject();
                    if (adminBizList == null) {
                        adminBizList = new ArrayList<AdminBiz>();
                    }
                    adminBizList.add(adminBiz);
                }
            }
        }
    }
    public static List<AdminBiz> getAdminBizList(){
        return adminBizList;
    }


    // ---------------------- executor-server(jetty) ----------------------
    private NetComServerFactory serverFactory = new NetComServerFactory();
    private void initExecutorServer(int port, String ip, String appName, String accessToken) throws Exception {
        // valid param
        port = port>0?port: NetUtil.findAvailablePort(9999);

        // start server
        NetComServerFactory.putService(ExecutorBiz.class, new ExecutorBizImpl());   // rpc-service, base on jetty
        NetComServerFactory.setAccessToken(accessToken);
        serverFactory.start(port, ip, appName); // jetty + registry
    }
    private void stopExecutorServer() {
        serverFactory.destroy();    // jetty + registry + callback
    }


    // ---------------------- job thread repository ----------------------
    private static ConcurrentHashMap<Integer, JobThread> JobThreadRepository = new ConcurrentHashMap<Integer, JobThread>();
    public static JobThread registJobThread(int jobId, String removeOldReason){
        JobThread newJobThread = new JobThread(jobId);
        newJobThread.start();
        logger.info(">>>>>>>>>>> job register JobThread success, jobId:{}", new Object[]{jobId});

        JobThread oldJobThread = JobThreadRepository.put(jobId, newJobThread);	// putIfAbsent | oh my god, map's put method return the old value!!!
        if (oldJobThread != null) {
            oldJobThread.toStop(removeOldReason);
            oldJobThread.interrupt();
        }

        return newJobThread;
    }
    public static void removeJobThread(int jobId, String removeOldReason){
        JobThread oldJobThread = JobThreadRepository.remove(jobId);
        if (oldJobThread != null) {
            oldJobThread.toStop(removeOldReason);
            oldJobThread.interrupt();
        }
    }
    public static JobThread loadJobThread(int jobId){
        JobThread jobThread = JobThreadRepository.get(jobId);
        return jobThread;
    }

}
