package com.inno72.job.task.task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.alibaba.fastjson.JSON;
import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.handle.IJobHandler;
import com.inno72.job.core.handle.annotation.JobHandler;
import com.inno72.job.core.handle.annotation.JobMapperScanner;
import com.inno72.job.core.log.JobLogger;
import com.inno72.job.task.mapper.Inno72ActivityMapper;
import com.inno72.job.task.mapper.Inno72MerchantTotalCountByDayMapper;
import com.inno72.job.task.mapper.Inno72MerchantTotalCountMapper;
import com.inno72.job.task.mapper.Inno72MerchantUserMapper;
import com.inno72.job.task.model.Inno72MerchantTotalCount;
import com.inno72.job.task.model.Inno72MerchantTotalCountByDay;
import com.inno72.job.task.model.Inno72MerchantUser;
import com.inno72.job.task.util.Testpoi;


@JobMapperScanner(value = "classpath*:/com/inno72/job/task/mapper/*.xml", basePackage="com.inno72.job.task.mapper")
@JobHandler("inno72.task.ReadExcel")
public class ReadExcel implements IJobHandler {

	@Resource
	private Inno72MerchantTotalCountByDayMapper inno72MerchantTotalCountByDayMapper;

	@Override
	public ReturnT<String> execute(String param) throws Exception {

		Workbook wb =null;
		Sheet sheet = null;
		Row row = null;
		List<Map<String,String>> list = null;
		String cellData = null;
		String filePath = "/Users/zb.zhou/Desktop/xxxx.xls";
		String columns[] = {"date","city","keliu","pv","uv","order","ship"};
		wb = Testpoi.readExcel(filePath);
		if(wb != null){
			//用来存放表中数据
			list = new ArrayList<Map<String,String>>();
			//获取第一个sheet
			sheet = wb.getSheetAt(0);
			//获取最大行数
			int rownum = sheet.getPhysicalNumberOfRows();
			//获取第一行
			row = sheet.getRow(0);
			//获取最大列数
			int colnum = row.getPhysicalNumberOfCells();
			for (int i = 1; i<rownum; i++) {
				Map<String,String> map = new LinkedHashMap<String,String>();
				row = sheet.getRow(i);
				if(row !=null){
					for (int j=0;j<colnum;j++){
						cellData = (String) Testpoi.getCellFormatValue(row.getCell(j));
						map.put(columns[j], Optional.ofNullable(cellData).map(Object::toString).orElse(""));
					}
				}else{
					break;
				}
				list.add(map);
			}
		}
		//遍历解析出来的list
		for (Map<String,String> map : list) {
			String date = map.get("date");
			String city = map.get("city");
			String keliu = map.get("keliu");
			String pv = map.get("pv");
			String uv = map.get("uv");
			String order = map.get("order");
			String ship = map.get("ship");

			Inno72MerchantTotalCountByDay day = inno72MerchantTotalCountByDayMapper.selectC(map);

			if (day == null){
				day = new Inno72MerchantTotalCountByDay();
				day.setGoodsId("7b352481760b40b8bba9dde37a72b51a");
				day.setGoodsName("周黑鸭卤蛋");
				day.setId(UUID.randomUUID().toString().replaceAll("-",""));
				day.setActivityId("8ad75f62bca249fdac5e7b14680dde7d");
				day.setActivityName("渠道态双十一四种活动");
				day.setMerchantId("201812030046");
				day.setConcernNum(0);
				day.setCouponNum(0);
				day.setLastUpdateTime(LocalDateTime.now());
				day.setSellerId("94298837");
				day.setDate(date);
				day.setCity(city);

				day.setPv((int)Double.parseDouble(pv));
				day.setStayNum((int)Double.parseDouble(keliu));
				day.setUv((int)Double.parseDouble(uv));
				day.setOrderQtySucc((int)Double.parseDouble(order));
				day.setOrderQtySucc((int)Double.parseDouble(ship));
				List<Inno72MerchantTotalCountByDay> insertS = new ArrayList<>();
				insertS.add(day);
				inno72MerchantTotalCountByDayMapper.insertS(insertS);
			}else {
				day.setPv((int)Double.parseDouble(pv));
				day.setStayNum((int)Double.parseDouble(keliu));
				day.setUv((int)Double.parseDouble(uv));
				day.setOrderQtyTotal((int)Double.parseDouble(order));
				day.setOrderQtySucc((int)Double.parseDouble(ship));
				day.setVisitorNum((int)Double.parseDouble(keliu));
				inno72MerchantTotalCountByDayMapper.updateC(day);
			}


		}
		// 修补活动ID为空情况
		return  new ReturnT<>(ReturnT.SUCCESS_CODE, "ok");

	}

	@Override
	public void init() {

	}

	@Override
	public void destroy() {

	}
}
