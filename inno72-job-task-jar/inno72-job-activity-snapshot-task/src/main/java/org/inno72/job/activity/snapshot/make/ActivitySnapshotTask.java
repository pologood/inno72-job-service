package org.inno72.job.activity.snapshot.make;

import com.inno72.job.core.biz.model.ReturnT;
import com.inno72.job.core.handle.IJobHandler;
import com.inno72.job.core.handle.annotation.JobHandler;
import com.inno72.job.core.log.JobLogger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.springframework.stereotype.Component;

@JobHandler("inno72.task.ActivitySnapshotTask")
@Component
public class ActivitySnapshotTask
		implements IJobHandler
{
	@Resource
	DataSource ds;

	public ReturnT<String> execute(String param)
			throws Exception
	{
		Date workDate = new Date(System.currentTimeMillis());

		SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");

		JobLogger.log("ActivitySnapshotTask job, handTime:" + df.format(workDate), new Object[0]);

		int count = insertActivitySnapShot(workDate);

		return new ReturnT(0, df
				.format(workDate) + "处理" + count + " 条activity plan数据");
	}

	private int insertActivitySnapShot(Date workDate)
			throws SQLException
	{
		Connection conn = null;
		PreparedStatement stm = null;
		try
		{
			String insertSql = "insert into inno72.inno72_statistic_activity_machine_snapshot(activity_id, machine_id, machine_code, activity_type, handle_time) select   `m`.`activity_id`   AS `activity_id`,         `m`.`machine_id`    AS `machine_id`,         `m`.`machine_code`  AS `machine_code`,         `m`.`activity_type` AS `activity_type`, ?   from (select `i`.`name`         AS `activity_name`,               `m`.`machine_code` AS `machine_code`,               `i`.`id`           AS `activity_id`,               `m`.`id`           AS `machine_id`,               1                  AS `activity_type`        from (((`inno72`.`inno72_interact_machine` `im` left join `inno72`.`inno72_interact_machine_time` `imt` on ((          `im`.`id` = `imt`.`interact_machine_id`))) left join `inno72`.`inno72_machine` `m` on ((`m`.`id` =                                                                                                  `im`.`machine_id`))) left join `inno72`.`inno72_interact` `i` on ((          `i`.`id` = `im`.`interact_id`)))        where ((`m`.`machine_status` = 4) and (`i`.`is_delete` = 0) and (`i`.`status` = 1) and               (now() between `imt`.`start_time` and `imt`.`end_time`))        union select `a`.`name`         AS `activity_name`,                     `m`.`machine_code` AS `machine_code`,                     `ap`.`id`          AS `activity_id`,                     `m`.`id`           AS `machine_id`,                     0                  AS `activity_type`              from (((`inno72`.`inno72_activity_plan` `ap` left join `inno72`.`inno72_activity_plan_machine` `apm` on ((                `apm`.`activity_plan_id` = `ap`.`id`))) left join `inno72`.`inno72_machine` `m` on ((`m`.`id` =                                                                                                     `apm`.`machine_id`))) left join `inno72`.`inno72_activity` `a` on ((                `a`.`id` = `ap`.`activity_id`)))              where ((`m`.`machine_status` = 4) and (`ap`.`is_delete` = 0) and                     (now() between `ap`.`start_time` and `ap`.`end_time`))) `m`";
			conn = this.ds.getConnection();
			stm = conn.prepareStatement(insertSql);
			stm.setTimestamp(1, new Timestamp(workDate.getTime()));
			return stm.executeUpdate();
		}
		finally
		{
			if (stm != null) {
				stm.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
	}

	public void init() {}

	public void destroy() {}
}
