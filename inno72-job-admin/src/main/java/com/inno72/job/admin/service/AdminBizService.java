package com.inno72.job.admin.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.inno72.job.admin.mapper.JobInfoDao;
import com.inno72.job.admin.mapper.JobLogDao;
import com.inno72.job.admin.mapper.JobRegistryDao;
import com.inno72.job.admin.model.JobInfo;
import com.inno72.job.admin.model.JobLog;
import com.inno72.job.core.biz.AdminBiz;
import com.inno72.job.core.biz.model.HandleCallbackParam;
import com.inno72.job.core.biz.model.RegistryParam;
import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.glue.GlueTypeEnum;
import com.inno72.job.core.util.FileUtil;
import com.inno72.job.core.util.Submittable;



@Service
public class AdminBizService implements AdminBiz {
    private static Logger logger = LoggerFactory.getLogger(AdminBizService.class);

    @Resource
    public JobLogDao jobLogDao;
    @Resource
    private JobInfoDao jobInfoDao;
    @Resource
    private JobRegistryDao jobRegistryDao;
    @Resource
    private JobService jobService;


    public ReturnT<Void> callback(List<HandleCallbackParam> callbackParamList) {
        for (HandleCallbackParam handleCallbackParam: callbackParamList) {
            ReturnT<Void> callbackResult = callback(handleCallbackParam);
            logger.info(">>>>>>>>> JobApiController.callback {}, handleCallbackParam={}, callbackResult={}",
                    (callbackResult.getCode()==0?"success":"fail"), handleCallbackParam, callbackResult);
        }

        return ReturnT.SUCCESS;
    }

    private ReturnT<Void> callback(HandleCallbackParam handleCallbackParam) {
        // valid log item
        JobLog log = jobLogDao.load(handleCallbackParam.getLogId());
        if (log == null) {
            return new ReturnT<Void>(ReturnT.FAIL_CODE, "log item not found.");
        }
        if (log.getHandleCode() != 1000) {
            return new ReturnT<Void>(ReturnT.FAIL_CODE, "log repeate callback.");     // avoid repeat callback, trigger child job etc
        }

        // trigger success, to trigger child job
        String callbackMsg = null;
        if (ReturnT.SUCCESS_CODE == handleCallbackParam.getExecuteResult().getCode()) {
            JobInfo jobInfo = jobInfoDao.loadById(log.getJobId());
            if (jobInfo!=null && StringUtils.isNotBlank(jobInfo.getChildJobId())) {
                callbackMsg = "<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>> Trigger child job <<<<<<<<<<< </span><br>";

                String[] childJobIds = jobInfo.getChildJobId().split(",");
                for (int i = 0; i < childJobIds.length; i++) {
                    int childJobId = (StringUtils.isNotBlank(childJobIds[i]) && StringUtils.isNumeric(childJobIds[i]))?Integer.valueOf(childJobIds[i]):-1;
                    if (childJobId > 0) {
                        ReturnT<Void> triggerChildResult = jobService.triggerJob(childJobId);

                        // add msg
						callbackMsg += MessageFormat.format("{0}/{1} [Job ID={2}], Trigger {3}, Trigger msg: {4} <br>",
                                (i+1),
                                childJobIds.length,
                                childJobIds[i],
                                (triggerChildResult.getCode()==ReturnT.SUCCESS_CODE?"success":"fails"),
                                triggerChildResult.getMsg());
                    } else {
                        callbackMsg += MessageFormat.format("{0}/{1} [Job ID={2}], Trigger Fail, Trigger msg: Job ID is illegal <br>",
                                (i+1),
                                childJobIds.length,
                                childJobIds[i]);
                    }
                }

            }
        } else if (ReturnT.FAIL_RETRY_CODE == handleCallbackParam.getExecuteResult().getCode()){
            ReturnT<Void> retryTriggerResult = jobService.triggerJob(log.getJobId());
            callbackMsg = "<br><br><span style=\"color:#F39C12;\" > >>>>>>>>>>> Handle fail retry <<<<<<<<<<< </span><br>";

            callbackMsg += MessageFormat.format("Trigger {0}, Trigger msg: {1} <br>",
                   (retryTriggerResult.getCode()==ReturnT.SUCCESS_CODE? "success" : "fail"), retryTriggerResult.getMsg());
        }

        // handle msg
        StringBuffer handleMsg = new StringBuffer();
        if (log.getHandleMsg()!=null) {
            handleMsg.append(log.getHandleMsg()).append("<br>");
        }
        if (handleCallbackParam.getExecuteResult().getMsg() != null) {
            handleMsg.append(handleCallbackParam.getExecuteResult().getMsg());
        }
        if (callbackMsg != null) {
            handleMsg.append(callbackMsg);
        }

        // success, save log
        log.setHandleTime(new Date());
        log.setHandleCode(handleCallbackParam.getExecuteResult().getCode());
        log.setHandleMsg(handleMsg.toString());
        jobLogDao.updateHandleInfo(log);

        return ReturnT.SUCCESS;
    }

    public ReturnT<Void> registry(RegistryParam registryParam) {
        int ret = jobRegistryDao.registryUpdate(registryParam.getRegistGroup(), registryParam.getRegistryKey(), registryParam.getRegistryValue());
        if (ret < 1) {
            jobRegistryDao.registrySave(registryParam.getRegistGroup(), registryParam.getRegistryKey(), registryParam.getRegistryValue());
        }
        return ReturnT.SUCCESS;
    }


    public ReturnT<Void> registryRemove(RegistryParam registryParam) {
        jobRegistryDao.registryDelete(registryParam.getRegistGroup(), registryParam.getRegistryKey(), registryParam.getRegistryValue());
        return ReturnT.SUCCESS;
    }


    public ReturnT<Void> triggerJob(int jobId) {
        return jobService.triggerJob(jobId);
    }
    
    
    
    public ReturnT<String> getJarChecksum(int jobId) {
		
    	JobInfo jobInfo = jobInfoDao.loadById(jobId);
    	if(jobInfo == null) {
    		return new ReturnT<String>(ReturnT.FAIL_CODE, "jobId not found.");
    	}
    	
    	if(!GlueTypeEnum.JAVA_JAR_INTERNAL.name().equals(jobInfo.getGlueType()) &&
    			!GlueTypeEnum.JAVA_JAR_EXTERNAL.name().equals(jobInfo.getGlueType())) {
    		return new ReturnT<String>(ReturnT.FAIL_CODE, "GlueType is error.");
    	}
    	
    	String prefix = jobInfo.getGlueSource();
		final String jarFileName = String.format("./jar_repositories/%s.jar", prefix);
		final String jarLockFileName = String.format("./jar_repositories_lock/%s.lock", prefix);
    	
    	try {
    		byte[] jarFile = (byte[]) FileUtil.processByFileLock(new File(jarLockFileName), new Submittable() {
				@Override
				public Object submit() throws IOException {
					
					File file = new File(jarFileName);
					Long filelength = file.length();
					byte[] jarFile = new byte[filelength.intValue()];
					
					FileInputStream in = new FileInputStream(file);  
		            in.read(jarFile);  
		            in.close();  
					
		            return jarFile;	
				}
			});
    		
    		String checksum = FileUtil.GetMD5Code(jarFile);
    		return new ReturnT<String>(checksum);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return new ReturnT<String>(ReturnT.FAIL_CODE, e.getMessage());
		}
    }
    

}
