package com.inno72.job.executer.service;

import com.inno72.job.executer.model.Inno72MachineConnectionMsg;

import java.io.IOException;

public interface ConnectionMsgService {
    void execute(Integer type);
    void sendMsg(Inno72MachineConnectionMsg msg) throws IOException;
}
