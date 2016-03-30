package com.sktst.batch.sal.sta.biz;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.sktst.batch.base.AbsBatchJobBiz;

/**
 * <li>업무 그룹명 : 프로젝트/SKTST/영업/U.Key통계 I/F - 더블할인 상세기변</li>
 * <li>설 명      : U.Key에서 I/F된 통계자료를 T.Key+ Table에 INSERT 한다.</li>
 * <li>작성일     : 2010-07-19 09:00:00</li>
 * </ul>
 *
 *
 * @author 이영진 (leeyoungjin)
 */
public class SALSTA08100 extends AbsBatchJobBiz {

	private static final String PROG_ID = "SALSTA08100";
	private static final String USER_ID = "SALSTA0810";
	private static String fileName = "";
	private static String ukeyIfType = "A0";

	/**
	 * 배치 프로그램을 수행한다.
	 *
	 * @author 이영진 (leeyoungjin)
	 * 
	 * @param request
	 *            Map 객체
	 * @return 수행결과
	 *            0이면 정상, 아니면 비정상
	 * @throws Exception
	 */
	public int execute(Map request) throws Exception {

		super.getProperties();
		
		if (log.isDebugEnabled()) {
			log.debug(PROG_ID + ".execute");
		}

		SqlMapClient sqlMap = getSqlMapClient();
		
		fileName = (String)request.get("args1");

		try {
			
			logMng.openLogFile(PROG_ID);
			logMng.writeLogFile("args1 : " + fileName);

			// 전문양식이 맞는지 Check
            if (ukeyIfType.equals(fileName.substring(6, 8) )) {

    			// 업무 시작.
    			sqlMap.startTransaction();
    			
    			// FILE을 읽어서 DB insert.
    			openDataFileAddDB(sqlMap);
    			logMng.writeLogFile("------------------------------------------------------------");

    			// 처리 결과 commit
    			if (log.isDebugEnabled()) {
    				log.debug(PROG_ID + ".execute.commitTransaction");
    			}
    			sqlMap.commitTransaction();

            } else {
            	logMng.writeLogFile("Correct IF Type is 'A0'.. But '" + fileName.substring(6, 8) + "' type accepted...");
            }

			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.startTransaction");
			}

		} catch(Exception e) {
			logMng.writeLogFile("execute Exception : " + e.getMessage());
			if (log.isDebugEnabled()) {
				log.debug("execute Exception : " + e.getMessage());
			}

		} finally {
			// 처리 완료. (commit이 안된 경우 rollback)
			if (log.isDebugEnabled()) {
				log.debug(PROG_ID + ".execute.endTransaction");
			}
			sqlMap.endTransaction();

			logMng.closeLogFile();
		}
		return 0;
	}

	/**
	 * 파일을 읽어서 DB에 기록한다.
	 *
	 * @author 이영진 (leeyoungjin)
	 * 
	 * @param sqlMap
	 *            SqlMapClient 객체
	 * @return void
	 * 
	 * @throws Exception
	 */
	private void openDataFileAddDB(SqlMapClient sqlMap) throws Exception {

		logMng.writeLogFile("openDataFileAddDB method start......");

		FileReader fr = null;
		BufferedReader br = null;

		int readCnt     = 0;	// I/F된 sam file read count
		int insertCnt   = 0; 	// 부가서비스 insert count
		int errorCnt    = 0;    // 오류건수

		String dataPath = new StringBuffer()
			.append(inFilePath + "/")
			.append(fileName)
			.toString();

		// Input File 자료처리를 위한 변수 선언
		String fAgencyCd       		= "";
		String fSubCd          		= "";
		String fStrdDt         		= "";
		String fDblDcClStcCd 		= "";
		String fTkGnrlCellChgCnt	= "";
		String fTkVipCellChgCnt     = "";
		String fTkGoldCellChgCnt    = "";
		String fTkSilvCellChgCnt    = "";
		String fTkSumCnt            = "";
		String fThGnrlCellChgCnt    = "";
		String fThVipCellChgCnt     = "";
		String fThGoldCellChgCnt    = "";
		String fThSilvCellChgCnt    = "";
		String fThSumCnt            = "";
		String fTtGnrlCellChgCnt    = "";
		String fTtVipCellChgCnt     = "";
		String fTtGoldCellChgCnt    = "";
		String fTtSilvCellChgCnt    = "";
		String fTtSumCnt            = "";
		String fM12AgrmtCnt         = "";
		String fM18AgrmtCnt         = "";
		String fM24AgrmtCnt         = "";
		String fM36AgrmtCnt         = "";
		String fM42AgrmtCnt         = "";
		String fM48AgrmtCnt         = "";
		String fM54AgrmtCnt         = "";
		
		// Table 저장을 위한 변수 선언
		String pAgencyPlc     		= "";
		String pProcPlc       		= "";


        // QueryForObject를 위한 Map 선언
        Map    resultDeal;

		Map<String, Object> requestMap    = new HashMap<String, Object>();
		Map<String, Object> requestMapUpd = new HashMap<String, Object>();

		String sCurdate = dataPath.substring(12, 20);
		logMng.writeLogFile(" U.Key I/F SAM 일자 [" + sCurdate + "]");

		// 이미 처리된 자료인 경우 재처리를 위해 이전 작업 자료 삭제...
	 	requestMap.put("STRD_DT", sCurdate);
		sqlMap.update("SALSTA08100.deleteUkeySuplProd", requestMap);

		try {
			fr = new FileReader(dataPath);
			logMng.writeLogFile("Input File : " + dataPath);
		 
			br = new BufferedReader(fr);

			for (String readLine; (readLine = br.readLine()) != null;) {

				try {

					readCnt++;

					// 전문양식에 맞게 읽은 자료를 자른다.
					fAgencyCd       = readLine.substring(   2,   8).trim();
					fSubCd          = readLine.substring(   8,  12).trim();
					fStrdDt         = readLine.substring(  12,  20).trim();
					
					
					
					fDblDcClStcCd 		= readLine.substring(20, 21).trim();
					fTkGnrlCellChgCnt	= readLine.substring(21, 32).trim();
					fTkVipCellChgCnt    = readLine.substring(32, 43).trim();
					fTkGoldCellChgCnt   = readLine.substring(43, 54).trim();
					fTkSilvCellChgCnt   = readLine.substring(54, 65).trim();
					fTkSumCnt           = readLine.substring(65, 76).trim();
					fThGnrlCellChgCnt   = readLine.substring(76, 87).trim();
					fThVipCellChgCnt    = readLine.substring(87, 98).trim();
					fThGoldCellChgCnt   = readLine.substring(98, 109).trim();
					fThSilvCellChgCnt   = readLine.substring(109, 120).trim();
					fThSumCnt           = readLine.substring(120, 131).trim();
					fTtGnrlCellChgCnt   = readLine.substring(131, 142).trim();
					fTtVipCellChgCnt    = readLine.substring(142, 153).trim();
					fTtGoldCellChgCnt   = readLine.substring(153, 164).trim();
					fTtSilvCellChgCnt   = readLine.substring(164, 175).trim();
					fTtSumCnt           = readLine.substring(175, 186).trim();
					fM12AgrmtCnt        = readLine.substring(186, 197).trim();
					fM18AgrmtCnt        = readLine.substring(197, 208).trim();
					fM24AgrmtCnt        = readLine.substring(208, 219).trim();
					fM36AgrmtCnt        = readLine.substring(219, 230).trim();
					fM42AgrmtCnt        = readLine.substring(230, 241).trim();
					fM48AgrmtCnt        = readLine.substring(241, 252).trim();
					fM54AgrmtCnt        = readLine.substring(252, 263).trim();
					
					// 대리점 정보 조회
					requestMap.put("UKEY_AGENCY_CD", fAgencyCd);
					requestMap.put("EFF_DT",         fStrdDt);
					resultDeal = (Map)sqlMap.queryForObject("SALSTA08100.getDealAgencyInfo", requestMap);

					if (resultDeal == null)  {
						pAgencyPlc = "";
					} else {
						pAgencyPlc = (String)  resultDeal.get("DEAL_CO_CD");
					}

					// 처리점(판매점) 정보 조회
					requestMap.put("UKEY_AGENCY_CD", fAgencyCd);
					requestMap.put("UKEY_SUB_CD",    fSubCd);
					requestMap.put("EFF_DT",         fStrdDt);
					resultDeal = (Map)sqlMap.queryForObject("SALSTA08100.getDealPlcInfo", requestMap);

					if (resultDeal == null)  {
						pProcPlc = "";
					} else {
						pProcPlc = (String)  resultDeal.get("DEAL_CO_CD");
					}

            		requestMapUpd.put("STRD_DT",              fStrdDt);
	            	requestMapUpd.put("UKEY_AGENCY_CD",       fAgencyCd);
	            	requestMapUpd.put("UKEY_SUB_CD",          fSubCd);
	            	requestMapUpd.put("AGENCY_CD",            pAgencyPlc);
	            	requestMapUpd.put("PROC_PLC",             pProcPlc);
	            	
	            	requestMapUpd.put("DBL_DC_CL_STC_CD",		fDblDcClStcCd 		);                    
	            	requestMapUpd.put("TK_GNRL_CELL_CHG_CNT",   fTkGnrlCellChgCnt   );
	            	requestMapUpd.put("TK_VIP_CELL_CHG_CNT",    fTkVipCellChgCnt    );
	            	requestMapUpd.put("TK_GOLD_CELL_CHG_CNT",   fTkGoldCellChgCnt   );
	            	requestMapUpd.put("TK_SILV_CELL_CHG_CNT",   fTkSilvCellChgCnt   );
	            	requestMapUpd.put("TK_SUM_CNT",             fTkSumCnt           );
	            	requestMapUpd.put("TH_GNRL_CELL_CHG_CNT",   fThGnrlCellChgCnt   );
	            	requestMapUpd.put("TH_VIP_CELL_CHG_CNT",    fThVipCellChgCnt    );
	            	requestMapUpd.put("TH_GOLD_CELL_CHG_CNT",   fThGoldCellChgCnt   );
	            	requestMapUpd.put("TH_SILV_CELL_CHG_CNT",   fThSilvCellChgCnt   );
	            	requestMapUpd.put("TH_SUM_CNT",             fThSumCnt           );
	            	requestMapUpd.put("TT_GNRL_CELL_CHG_CNT",   fTtGnrlCellChgCnt   );
	            	requestMapUpd.put("TT_VIP_CELL_CHG_CNT",    fTtVipCellChgCnt    );
	            	requestMapUpd.put("TT_GOLD_CELL_CHG_CNT",   fTtGoldCellChgCnt   );
	            	requestMapUpd.put("TT_SILV_CELL_CHG_CNT",   fTtSilvCellChgCnt   );
	            	requestMapUpd.put("TT_SUM_CNT",             fTtSumCnt           );
	            	requestMapUpd.put("M12_AGRMT_CNT",          fM12AgrmtCnt        );
	            	requestMapUpd.put("M18_AGRMT_CNT",          fM18AgrmtCnt        );
	            	requestMapUpd.put("M24_AGRMT_CNT",          fM24AgrmtCnt        );
	            	requestMapUpd.put("M36_AGRMT_CNT",          fM36AgrmtCnt        );
	            	requestMapUpd.put("M42_AGRMT_CNT",          fM42AgrmtCnt        );
	            	requestMapUpd.put("M48_AGRMT_CNT",          fM48AgrmtCnt        );
	            	requestMapUpd.put("M54_AGRMT_CNT",          fM54AgrmtCnt        );
	            	
	            	requestMapUpd.put("USER_ID",              USER_ID);

	            	sqlMap.update("SALSTA08100.addUkeyDblDtlRtn", requestMapUpd);
	            	
	            	insertCnt++;

				} catch (Exception ex) {
					logMng.writeLogFile("ERRCODE:E ");
					logMng.writeLogFile(ex.getMessage());
					logMng.writeLogFile("Error Rec==>[" + readLine + "]");
					errorCnt++;
					if (log.isDebugEnabled()) {
						log.debug(ex.getMessage());
					}
				}
			
			}
		} finally {
			try {
				br.close();
			} catch (Exception e) {
				logMng.writeLogFile(e.getMessage());
				if (log.isDebugEnabled()) {
					log.debug(e.getMessage());
				}
			}
		}

		logMng.writeLogFile("openDataFileAddDB method end......");
		logMng.writeLogFile(" ");

		logMng.writeLogFile("------------------------------------------------------------");
		logMng.writeLogFile("    File Read Count : " + readCnt);
		logMng.writeLogFile("       Insert Count : " + insertCnt);
		logMng.writeLogFile("------------------------------------------------------------");
	}
}