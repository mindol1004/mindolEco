/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.cst.adv.biz;

import java.util.Map;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

import nexcore.framework.core.data.DataSet;
import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.IResultMessage;
import nexcore.framework.core.data.ResultMessage;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTST/상담</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2013-01-23 11:06:18</li>
 * </ul>
 *
 * @author 민동운 (mindongwoon)
 */
public class CSTADV01500 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 민동운 (mindongwoon)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getUkeyIfResultList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);

		//질의를 수행한다.
		IRecordSet rs = queryForRecordSet("CSTADV01500.getUkeyIfResultList",
				requestData.getFieldMap());

		//응답데이터를 생성한다.

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("MovOutList", rs);
		return responseData;
	}

}