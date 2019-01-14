package com.inno72.job.executer.common.util;

import java.util.UUID;

/**
 * Title:AjaxUtils Description:生成uuid工具类
 *
 * @Create_by:gaohuan
 * @Create_date:2018-6-29
 * @Last_Edit_By:
 * @Edit_Description
 * @version:ShareWithUs 1.0
 *
 */
public class UuidUtil {

	public static String getUUID32() {
		String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
		return uuid;
	}
}
