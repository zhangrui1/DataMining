package jp.co.freedom.master.scf;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.freedom.common.utilities.StringUtil;
import jp.co.freedom.master.dto.scf.ScfExhibitorDataDto;
import jp.co.freedom.master.dto.scf.ScfExhibitorMasterDto;
import jp.co.freedom.master.utilities.scf.ScfConfig;
import jp.co.freedom.master.utilities.scf.ScfUtil;

/**
 * 【SCF】出展者マスターデータの重複検証サーブレット
 * 
 * @author フリーダム・グループ
 * 
 */
public class ExhibiDataDupulicateCheckForScf extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * Getメソッドの処理
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws ServletException
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		// 出展者情報のユニークリスト
		List<ScfExhibitorMasterDto> uniqueMaster = new ArrayList<ScfExhibitorMasterDto>();
		// 出展者の全データリスト
		List<ScfExhibitorDataDto> allData = new ArrayList<ScfExhibitorDataDto>();
		try {
			// JDBCドライバーのロード
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// DBアクセス
			Connection conn = DriverManager.getConnection(ScfConfig.PSQL_URL,
					ScfConfig.PSQL_USER, ScfConfig.PSQL_PASSWORD);
			allData = ScfUtil.getAllExhibitorData(conn);
			for (ScfExhibitorDataDto data : allData) {
				if (StringUtil.isEmpty(data.V_com)
						|| (StringUtil.isEmpty(data.charge_name) && StringUtil
								.isEmpty(data.Resp_name))) {
					continue;
				}
				// [備忘]重複チェック時に、重複IDを記憶しておく
				if (!ScfUtil.dupulicateCheck(uniqueMaster, data)) { // 重複していない場合
					ScfExhibitorMasterDto master = new ScfExhibitorMasterDto();
					master.id = data.V_no;
					master.year = data.V_year;
					master.company = data.V_com;
					if (StringUtil.isNotEmpty(data.charge_name)) {
						master.name = data.charge_name;
					} else if (StringUtil.isNotEmpty(data.Resp_name)) {
						master.name = data.Resp_name;
					}
					uniqueMaster.add(master);// ユニークリストに追加
				} else {
					ScfUtil.setDupulicateFlg(conn, data.V_no);
				}
			}
			for (ScfExhibitorMasterDto master : uniqueMaster) {
				// 重複IDの出展年度をマスターIDの出展年度フラグに記憶
				for (String dupulicateId : master.dupulicate) {
					String year = ScfUtil.getYear(allData, dupulicateId);
					ScfUtil.setYearFlg(conn, master.id, year);
				}
				// マスターID自身の年度を出展年度フラグに記憶
				ScfUtil.setYearFlg(conn, master.id, master.year);
			}
			conn.close();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// JSPにフォワード
		RequestDispatcher dispatcher = request
				.getRequestDispatcher("/src/jsp/result.jsp");
		dispatcher.forward(request, response);
	}

	/**
	 * POSTメソッドの処理
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws ServletException
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		doGet(request, response); // Getメソッドに受け流す
	}
}