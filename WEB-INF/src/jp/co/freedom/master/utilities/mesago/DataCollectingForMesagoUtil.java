package jp.co.freedom.master.utilities.mesago;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.co.freedom.master.dto.mesago.MesagoCardDto;
import jp.co.freedom.master.dto.mesago.MesagoUserDataDto;

/**
 * 【MESAGO】データクレンジング用ユーティリティ
 * 
 * @author フリーダム・グループ
 * 
 */
public class DataCollectingForMesagoUtil {

	/**
	 * JDBCプラグインJARのロード
	 */
	public static void loadJdbcPluginJar() {
		// JDBCドライバーのロード
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ユーザーデータのインスタンスを生成しリストに格納
	 * 
	 * @param csvData
	 *            CSVデータ
	 * @return <b>List&lt;MesagoUserDataDto&gt;</b>
	 */
	public static List<MesagoUserDataDto> createInstance(List<String[]> csvData) {
		List<MesagoUserDataDto> userDataList = new ArrayList<MesagoUserDataDto>();// ユーザーデータを保持するリスト
		// CSVデータを1行ずつ処理
		for (int nIndex = 0; nIndex < csvData.size(); nIndex++) {
			MesagoUserDataDto userdata = new MesagoUserDataDto();
			MesagoCardDto cardInfo = new MesagoCardDto();
			String[] row = csvData.get(nIndex);
			userdata.id = row[0]; // バーコード番号
			cardInfo.V_VID = row[0];
			cardInfo.V_CORP = row[1]; // 会社名
			cardInfo.V_DEPT1 = row[2]; // 所属
			cardInfo.V_ZIP = row[3]; // 郵便番号
			cardInfo.V_ADDR1 = row[4]; // 都道府県
			cardInfo.V_ADDR2 = row[5]; // 市区郡
			cardInfo.V_ADDR3 = row[6]; // 町域
			cardInfo.V_ADDR4 = row[7]; // ビル名
			userdata.cardInfo = cardInfo;
			userDataList.add(userdata);
		}
		return userDataList;
	}

	/**
	 * データ修復
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param userDataList
	 *            ユーザーデータ
	 * @return 処理成否のブール値
	 * @throws SQLException
	 */
	public static boolean collect(Connection conn,
			List<MesagoUserDataDto> userDataList) throws SQLException {
		for (MesagoUserDataDto userdata : userDataList) {
			MesagoCardDto cardInfo = (MesagoCardDto) userdata.cardInfo;
			String sql = "UPDATE first SET V_CORP=?,V_DEPT=?,V_ZIP=?,V_ADDR1=?,V_ADDR2=?,V_ADDR3=?,V_ADDR4=? WHERE V_ID =?;";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, cardInfo.V_CORP);
			ps.setString(2, cardInfo.V_DEPT1);
			ps.setString(3, cardInfo.V_ZIP);
			ps.setString(4, cardInfo.V_ADDR1);
			ps.setString(5, cardInfo.V_ADDR2);
			ps.setString(6, cardInfo.V_ADDR3);
			ps.setString(7, cardInfo.V_ADDR4);
			ps.setString(8, userdata.id);
			int result = ps.executeUpdate();
			if (ps != null) {
				ps.close();
			}
			if (result == 0) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * ユーザーデータのインスタンスを生成しリストに格納
	 * お客様に削除したデータをsecondテーブルに更新
	 * @param csvData
	 *            CSVデータ
	 * @return <b>List&lt;MesagoUserDataDto&gt;</b>
	 */
	public static List<MesagoUserDataDto> createInstanceForSecond(List<String[]> csvData) {
		List<MesagoUserDataDto> userDataList = new ArrayList<MesagoUserDataDto>();// ユーザーデータを保持するリスト
		// CSVデータを1行ずつ処理
		for (int nIndex = 0; nIndex < csvData.size(); nIndex++) {
			MesagoUserDataDto userdata = new MesagoUserDataDto();
			MesagoCardDto cardInfo = new MesagoCardDto();
			String[] row = csvData.get(nIndex);
			userdata.id = row[0]; // バーコード番号
			cardInfo.V_VID = row[0];
			userdata.cardInfo = cardInfo;
			userDataList.add(userdata);
		}
		return userDataList;
	}
	
	/**
	 * データ修復
	 * 
	 * @param conn
	 *            DBサーバーへの接続情報
	 * @param userDataList
	 *            ユーザーデータ
	 * @return 処理成否のブール値
	 * @throws SQLException
	 */
	public static boolean collectForSecond(Connection conn,
			List<MesagoUserDataDto> userDataList) throws SQLException {
		for (MesagoUserDataDto userdata : userDataList) {
			String sql = "UPDATE second SET remove1='1' WHERE V_ID =?;";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, userdata.id);
			int result = ps.executeUpdate();
			if (ps != null) {
				ps.close();
			}
			if (result == 0) {
				return false;
			}
		}
		return true;
	}
	
}