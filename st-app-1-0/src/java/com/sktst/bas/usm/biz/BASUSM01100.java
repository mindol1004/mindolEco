/*
 * Copyright (c) 2006 SK C&C. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance wih the terms of the license agreement you entered into with SK
 * C&C.
 */

package com.sktst.bas.usm.biz;

import java.io.IOException;
import java.net.InetAddress;
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
import com.sktst.common.telnet.TelnetWrapper;

/**
 * <ul>
 * <li>업무 그룹명 : 프로젝트/SKTPS/기준정보</li>
 * <li>설 명 : </li>
 * <li>작성일 : 2010-08-29 17:22:06</li>
 * </ul>
 *
 * @author 이영진 (leeyoungjin)
 */
public class BASUSM01100 extends BaseBizUnit {

	private static final String MappinFlag = "1";
	private static final String tpsDev = "10.40.10.29"; 	// 팀 개발 서버
	private static final String tpsUser = "150.2.236.145"; 	// 사용자 서버
	private static final String tpsWas1 = "10.40.10.25"; 	// 운영 WAS 2 서버
	private static final String tpsWas2 = "10.40.10.27"; 	// 운영 WAS 1 서버
	private static final String tpsProd = "10.40.10.21"; 	// 운영 DB서버
	
	/**
	 * 
	 *
	 * @author 이영진 (leeyoungjin)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet getMMUserList(IDataSet requestData, IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("getMMUserList method start");
		}

		Map fields = requestData.getFieldMap();
		IRecordSet rs = queryForRecordSet("BASUSM01100.getMMUserList", fields,
				onlineCtx);
		if (rs == null) {
			rs = new RecordSet("userList");
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[] { String
						.valueOf(rs.getRecordCount()) });
		responseData.putRecordSet("userList", rs);
		return responseData;
	}

	/**
	 * 
	 *
	 * @author 이영진 (leeyoungjin)
	 * 
	 * @param requestData
	 *            요청정보를 Wrapping하고 있는 DataSet 객체
	 * @param onlineCtx
	 *            사용자,거래정보등 Infra성 정보를 포함하고 있는 객체
	 * @return 요청처리 완료후 작성된 응답정보를 Wrapping하고 있는 DataSet 객체
	 */
	public IDataSet updateMappingUser(IDataSet requestData,
			IOnlineContext onlineCtx) {

		Log log = LogManager.getLog(onlineCtx);
		if (log.isDebugEnabled()) {
			log.debug("updateMappingUser method start");
		}

		int updateCount = 0;

		IRecordSet rsMappingUser = requestData.getRecordSet("List");
		
		String strJobFlag    = null;
		String mqCallCommand = null;

		Map    oMapRowData   = null;

		// MQ 송신을 위한 변수설정
		String mqSendMessage = "";
		String mqLoginID     = "";
		String mqSysJobID    = "";
		String mqOrgID       = "";
		String mqRelOrgID    = "";
		String mqUserTypeCd  = "";
		String mqHndStsCd    = "";
		String mqEffStaDt    = "";
		String mqEffEndDt    = "";
		String mqAuditUserID = "";
		String mqAuditDtm    = "";
		String mqTransDtm    = "";
		String mqSysCl       = "";

		for (int i = 0; i < rsMappingUser.getRecordCount(); i++) {

			oMapRowData     = rsMappingUser.getRecordMap(i);
			strJobFlag      = oMapRowData.get("CHK")   == null ? "0" : oMapRowData.get("CHK").toString();

			if (MappinFlag.equals(strJobFlag)) {
				
				// PORTAL_USER_ID / PWD Update...
				update("BASUSM01100.saveMappingUser", oMapRowData, onlineCtx);
				
				// MQ를 통해 판매점 포탈로 I/F 전송

				mqLoginID     = oMapRowData.get("LOGIN_ID") == null ?"" : oMapRowData.get("LOGIN_ID").toString();
				mqSysJobID    = oMapRowData.get("USER_ID").toString();
				mqOrgID       = oMapRowData.get("POST_ORG_ID") == null ?"" : oMapRowData.get("POST_ORG_ID").toString();
		        mqRelOrgID    = oMapRowData.get("REL_ORG_ID")    == null ?"" : oMapRowData.get("REL_ORG_ID").toString();
		        mqUserTypeCd  = oMapRowData.get("USER_GRP") == null ?"" : oMapRowData.get("USER_GRP").toString();
		        mqHndStsCd    = "20";
		        mqEffStaDt    = oMapRowData.get("EFF_STA_DT")    == null ?"" : oMapRowData.get("EFF_STA_DT").toString();
		        mqEffEndDt    = oMapRowData.get("EFF_END_DT")    == null ?"" : oMapRowData.get("EFF_END_DT").toString();
		        mqAuditUserID = oMapRowData.get("AUDIT_USER_ID") == null ?"" : oMapRowData.get("AUDIT_USER_ID").toString();
		        mqAuditDtm    = oMapRowData.get("AUDIT_DTM")     == null ?"" : oMapRowData.get("AUDIT_DTM").toString();
		        mqTransDtm    = oMapRowData.get("TRANS_DTM")     == null ?"" : oMapRowData.get("TRANS_DTM").toString();
		        mqSysCl       = "TP";

		        mqSendMessage  = rPad(mqLoginID, 15, "_");
		        mqSendMessage += rPad(mqSysJobID, 15,"_");
		        mqSendMessage += rPad(mqOrgID, 10, "_");
		        mqSendMessage += rPad(mqRelOrgID, 10, "_");
		        mqSendMessage += rPad(mqUserTypeCd, 3, "_");
		        mqSendMessage += rPad(mqHndStsCd, 3, "_");
		        mqSendMessage += rPad(mqEffStaDt, 8, "_");
		        mqSendMessage += rPad(mqEffEndDt, 8, "_");
		        mqSendMessage += rPad(mqAuditUserID, 10, "_");
		        mqSendMessage += rPad(mqAuditDtm, 20, "_");
		        mqSendMessage += rPad(mqTransDtm, 14, "_");
		        mqSendMessage += rPad(mqSysCl, 2, "_");

				log.debug(" MQ Put Batch Call ...." + mqSendMessage);
				mqCallCommand = "BASUKI08300_001.sh 1 2 3 4 " + mqSendMessage + "\n";

			    if(goTelnetClient(log, mqCallCommand)== 0) {
			    	log.debug(" MQ Put Batch Call Fail ....");
				    rsMappingUser.getRecordMap(i).put("MQ_PUT_YN", "N");
			    }

			    log.debug(" MQ Put Batch Call End ....");
			    rsMappingUser.getRecordMap(i).put("MQ_PUT_YN", "Y");
			}
		}

		IDataSet responseData = DataSetFactory.createWithOKResultMessage(
				BaseConstants.QUERY_OK_MESSAGE_ID, new String[]{"" + updateCount});

		responseData.putRecordSet("List", rsMappingUser);

		return responseData;
	}

	private static String rPad(String sStr, int size, String fStr) {
		byte[] b = sStr.getBytes();
		int len = b.length;
		int tmp = size - len;

		for (int i = 0; i < tmp; i++) {
			sStr += fStr;
		}

		return sStr;
	}
	
	public int goTelnetClient(Log log, String sComd) {
		int iTs = 0;
		String sIP = "";
		String host = "";
		int port = 23;
		String sUser = "ps_mng";
		String sPwd = "skttps12";

		TelnetWrapper telnetWrapper = new TelnetWrapper();
		try {
			InetAddress Address = InetAddress.getLocalHost();
			sIP = Address.getHostAddress();
		} catch (IOException e) {
			log.debug("TelnetClient: Got exception during Address: " + e);
			iTs = 0;
			return iTs;
		}

		//System.out.println("============>>>>IP: "+sIP);
		if (tpsWas1.equals(sIP) || tpsWas2.equals(sIP)) { //운영WAS1서버, 운영 WAS2서버
			host = tpsProd;
			sUser = "ps_mng";
			sPwd = "ps_mng";
		} else if (tpsUser.equals(sIP)) { //사용자서버
			host = sIP;
			sUser = "ps_mng";
			sPwd = "ps_mng";
		} else if (tpsDev.equals(sIP)) { //개발서버
			host = sIP;
			sUser = "ps_mng";
			sPwd = "skttps12";
		} else {
			host = tpsDev;
			sUser = "ps_mng";
			sPwd = "skttps12";
		}

		try {
			telnetWrapper.connect(host, port);
			telnetWrapper.login(sUser, sPwd);
			telnetWrapper.send("cd /app/batch");
			telnetWrapper.send(sComd);
			telnetWrapper.send("exit");

		} catch (IOException e) {
			log.debug("TelnetClient: Got exception during connect: " + e);
			iTs = 0;
			return iTs;
		}

		try {
			byte[] buf = new byte[256];
			while (telnetWrapper.read(buf) > -1) { // -1은 eof에 해당하는 코드 
				log.debug("============>>>>명령실행");
				//System.out.println("============>>>>명령실행");
			}

		} catch (IOException e) {
			log.debug("TelnetClient: Got exception in read/write loop: " + e);
			iTs = 0;
			return iTs;
		} finally {
			try {
				telnetWrapper.disconnect();
			} catch (IOException e) {
				log.debug("TelnetClient: got exception in disconnect: " + e);
				iTs = 0;
				return iTs;
			}
		}
		iTs = 1;
		return iTs;
	}	
	
}