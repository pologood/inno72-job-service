package com.inno72.job.core.handle;

import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.glue.GlueTypeEnum;
import com.inno72.job.core.log.JobFileAppender;
import com.inno72.job.core.log.JobLogger;
import com.inno72.job.core.util.FileUtil;
import com.inno72.job.core.util.ScriptUtil;
import com.inno72.job.core.util.ShardingUtil;

public class ScriptJobHandler extends IJobHandler {

    private int jobId;
    private long glueUpdatetime;
    private String gluesource;
    private GlueTypeEnum glueType;

    public ScriptJobHandler(int jobId, long glueUpdatetime, String gluesource, GlueTypeEnum glueType){
        this.jobId = jobId;
        this.glueUpdatetime = glueUpdatetime;
        this.gluesource = gluesource;
        this.glueType = glueType;
    }

    public long getGlueUpdatetime() {
        return glueUpdatetime;
    }

    @Override
    public ReturnT<String> execute(String param) throws Exception {

        if (!glueType.isScript()) {
            return new ReturnT<String>(ReturnT.FAIL.getCode(), "glueType["+ glueType +"] invalid.");
        }

        // cmd
        String cmd = glueType.getCmd();

        long currentTime = System.currentTimeMillis();
        
        // make script file
        String scriptFileName = JobFileAppender.getGlueSrcPath()
                .concat("/")
                .concat(String.valueOf(jobId))
                .concat("_")
                .concat(String.valueOf(glueUpdatetime))
                .concat(String.valueOf(currentTime))
                .concat(glueType.getSuffix());
        ScriptUtil.markScriptFile(scriptFileName, gluesource);

        // log file
        String logFileName = JobFileAppender.contextHolder.get();

        // script params：0=param、1=分片序号、2=分片总数
        ShardingUtil.ShardingVO shardingVO = ShardingUtil.getShardingVo();
        String[] scriptParams = new String[3];
        scriptParams[0] = param;
        scriptParams[1] = String.valueOf(shardingVO.getIndex());
        scriptParams[2] = String.valueOf(shardingVO.getTotal());

        // invoke
        JobLogger.log("----------- script file:"+ scriptFileName +" -----------");
        int exitValue = ScriptUtil.execToFile(cmd, scriptFileName, logFileName, scriptParams);
        
        FileUtil.deleteFile("scriptFileName");
         

        ReturnT<String> result = (exitValue==0)?new ReturnT<String>(ReturnT.SUCCESS_CODE, "ok"):new ReturnT<String>(ReturnT.FAIL_CODE, "script exit value("+exitValue+") is failed");
        return result;
    }

}
