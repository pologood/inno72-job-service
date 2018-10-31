package com.inno72.job.executer.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import javax.annotation.Resource;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.inno72.common.utils.StringUtil;
import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.handle.IJobHandler;
import com.inno72.job.core.handle.annotation.JobHandler;
import com.inno72.job.core.log.JobLogger;
import com.inno72.job.executer.mapper.Inno72MachineDataCountMapper;
import com.inno72.job.executer.model.Inno72MachineDataCount;
import com.inno72.job.executer.vo.MachineDataCount;


@Component
@JobHandler("taskDumpToMysql")
public class Task implements IJobHandler {

	@Resource
	private MongoOperations mongoOperations;

	@Resource
	private Inno72MachineDataCountMapper machineDataCountMapper;

	private ExecutorService exec = Executors.newCachedThreadPool();

	private Semaphore semaphore;

	private int batchSubmitRowNum = 100;

	@Override
	public ReturnT<String> execute(String param) {
		JobLogger.log("TaskDumpToMysql job, start");

		Query query = new Query();
		String lastDate = machineDataCountMapper.findLastDate();
		if (!StringUtil.isEmpty(lastDate)){
			query.addCriteria(Criteria.where("date").gt(lastDate));
		}
		query.with(new Sort(new Sort.Order(Sort.Direction.ASC, "date")));
		List<MachineDataCount> machineDataCountS = mongoOperations.find(query, MachineDataCount.class, "MachineDataCount");

		List<Inno72MachineDataCount> inno72MachineDataCounts = new ArrayList<>();

		if (machineDataCountS.size() > 0){

			Map<String,List<MachineDataCount>> byDateAndMachine = new HashMap<>();

			List<List<Inno72MachineDataCount>> counts = new ArrayList<>();

			for (MachineDataCount machineDataCount : machineDataCountS){
				String date = machineDataCount.getDate();
				String machineCode = machineDataCount.getMachineCode();
				if ( StringUtil.isEmpty(date) || StringUtil.isEmpty(machineCode)){
					JobLogger.log("统计数据mongo数据迁移mysql任务，主要数据缺失: {}", JSON.toJSONString(machineDataCount));
					continue;
				}
				String dateAndMachineCode = date + machineCode;
				List<MachineDataCount> machineDataCounts = byDateAndMachine.get(dateAndMachineCode);
				if (null == machineDataCounts){
					machineDataCounts = new ArrayList<>();
				}
				machineDataCounts.add(machineDataCount);
				byDateAndMachine.put(dateAndMachineCode, machineDataCounts);

			}

			Map<String, Map<String,String>> allMachine = machineDataCountMapper.findAllMachine();

			new Semaphore(byDateAndMachine.size());

			for (Map.Entry<String, List<MachineDataCount>> entry : byDateAndMachine.entrySet()){
				String key = entry.getKey();
				if (key == null){
					continue;
				}
				List<MachineDataCount> value = entry.getValue();
				System.out.println("当前合并的日志 " + JSON.toJSONString(value));
				if ( value.size() > 0 ){

					Inno72MachineDataCount count = Util.count(value);
					System.out.println("合并后的日志 " + JSON.toJSONString(count));

					count.setPoint(Optional.ofNullable(allMachine.get(count.getMachineCode())).map( v -> Optional.ofNullable(v.get("point")).map(Object::toString).orElse("")).orElse(""));
					inno72MachineDataCounts.add(count);

					if (inno72MachineDataCounts.size() >= batchSubmitRowNum){
						counts.add(inno72MachineDataCounts);
						inno72MachineDataCounts = new ArrayList<>();
					}
				}
			}
			if (inno72MachineDataCounts.size() > 0){
				counts.add(inno72MachineDataCounts);
				inno72MachineDataCounts = new ArrayList<>();
			}

			for (List<Inno72MachineDataCount> count: counts){

				exec.execute(() -> {

					try {

						System.out.println("开始进入插入线程");
						// 获取许可
						semaphore.acquire();
						int i = machineDataCountMapper.insertS(count);
						JobLogger.log("TaskDumpToMysql job, insert sex "+ i);

						System.out.println("完成插入线程");

					} catch (InterruptedException e) {
						e.printStackTrace();
					}finally {
						semaphore.release();
					}
				});
			}
		}

		JobLogger.log("TaskDumpToMysql job, param:"+param);
		JobLogger.log("TaskDumpToMysql job, end");
		return new ReturnT<>(ReturnT.SUCCESS_CODE, "ok");
	}


	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	private static Set<String> uuidSet = new HashSet<>();
	static class Util{
		static String uuid(){
			int size = uuidSet.size();
			String uuid = "";
			while (uuidSet.size() == size){
				uuid = StringUtil.uuid();
				uuidSet.add(uuid);
			}
			if (uuidSet.size() > 5){
				uuidSet.clear();
			}
			return uuid;
		}
		static Inno72MachineDataCount count(List<MachineDataCount> values){

			int r_concern = 0;
			int r_fans =  0;
			int r_order =  0;
			int r_pv =  0;
			int r_shipment =  0;
			int r_uv =  0;
			int r_visitor =  0;
			String r_activityId = "";
			String r_date = "";
			String r_machineCode = "";

			for(MachineDataCount v : values){

				if (StringUtil.isEmpty(r_activityId) && v.getActivityId() != null && !v.getActivityId().equals("-1")){
					r_activityId = v.getActivityId();
				}

				if (StringUtil.isEmpty(r_date)){
					r_date = v.getDate();
				}

				if (StringUtil.isEmpty(r_machineCode)){
					r_machineCode = v.getMachineCode();
				}

				r_concern += Optional.ofNullable(v.getConcern()).orElse(0);
				r_fans +=  Optional.ofNullable(v.getFans()).orElse(0);
				r_order +=  Optional.ofNullable(v.getOrder()).orElse(0);
				r_pv +=  Optional.ofNullable(v.getPv()).orElse(0);
				r_shipment += Optional.ofNullable(v.getShipment()).orElse(0);
				r_uv += Optional.ofNullable(v.getUv()).orElse(0);
				r_visitor += Optional.ofNullable(v.getVisitor()).orElse(0);

			}
			return new Inno72MachineDataCount(
					Util.uuid(), r_activityId,  null,
					r_date,  r_machineCode,  null,
					r_pv, r_uv, r_order,
					r_shipment, r_fans, r_visitor, r_concern);

		}
	}

}
