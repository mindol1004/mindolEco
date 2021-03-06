/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.bas.sdm.biz;

import java.util.Map;

import nexcore.framework.core.data.IDataSet;
import nexcore.framework.core.data.IOnlineContext;
import nexcore.framework.core.data.IRecordSet;
import nexcore.framework.core.data.RecordSet;
import nexcore.framework.core.log.LogManager;
import nexcore.framework.core.util.DataSetFactory;

import org.apache.commons.logging.Log;

import com.sktst.common.base.BaseBizUnit;
import com.sktst.common.base.BaseConstants;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/기준정보</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2010-02-10 13:05:28</li>
 * </ul>
 *
 * @author 전현주 (junhyunjoo)
 */
public class BASSDM00100 extends BaseBizUnit {

	/**
	 * 
	 *
	 * @author 전현주 (junhyunjoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getSalesDayList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getSalesDayList method start");
		}

		IRecordSet rs = queryForRecordSet("BASSDM00100.getSalesDayList",
				requestData.getFieldMap());

		if (rs == null) {
			rs = new RecordSet("rs_list");
		}
		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });

		responseData.putRecordSet("rs_list", rs);

		return responseData;
	}

	/**
	 * 
	 *
	 * @author 전현주 (junhyunjoo)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet saveSalesDayList(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("saveSalesDayList method start");
		}

		IRecordSet rs = requestData.getRecordSet("rs_list");
		
		Map mSave = null;
		String sType = "I";
		if (rs != null) {
			for (int i = 0; i < rs.getRecordCount(); i++) {
				mSave = rs.getRecordMap(i);
				
				if (mSave.get("FLAG").toString().equals(sType)) {
					insert("BASSDM00100.addSalesDayList", mSave, onlineCtx);
					
				} else {
					
					update("BASSDM00100.updateSalesDayList", mSave, onlineCtx);
				}
					
			}
		}
		
		return DataSetFactory.createWithOKResultMessage(
				BaseConstants.UPDATEALL_OK_MESSAGE_ID, new String[] { "1" });
	}

}