package com.open.lcp.biz.lbs.tool.coordinate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import com.open.lcp.biz.lbs.model.LngLat;
import com.open.lcp.biz.lbs.util.LBSLocationUtil;
import com.open.lcp.common.util.CpdetectorEncodingUtil;

public class BaiduLocalConvertor {

	private List<TempLocationInfo> loadLngLatInfo(String lngLatFilePath) throws Exception {
		// String originDirAbsolutePath = "F:\\coordinate\\origin";
		// File originDir = new File(originDirAbsolutePath);
		// String[] filePathList = originDir.list();
		// String cityFileDirAbsolutePath = null;
		// for (String filePath : filePathList) {
		// cityFileDirAbsolutePath = originDirAbsolutePath + "\\" + filePath;
		// System.out.println(cityFileDirAbsolutePath);
		// File cityFileDir = new File(cityFileDirAbsolutePath);

		// String infoAbsolutePath = cityFileDirAbsolutePath +
		// "\\C_CommunityRegion.mif";
		File coorFile = new File(lngLatFilePath);

		List<TempLocationInfo> rtList = new ArrayList<TempLocationInfo>();

		// TempLocationInfo info = null;
		StringBuilder sb = null;
		BufferedReader br = new BufferedReader(new FileReader(coorFile));
		String line = null;
		int startIndex = 0;
		while ((line = br.readLine()) != null) {
			if (line.indexOf("Region") >= 0) {
				startIndex = 1;

				if (sb != null && !sb.toString().trim().equals("")) {
					String[] lngLatArray = sb.toString().split(";");
					List<LngLat> rtLngLatList = new ArrayList<LngLat>();
					for (String e : lngLatArray) {
						String[] temp = e.split(",");
						LngLat ll = new LngLat();
						ll.setLng(Double.parseDouble(temp[0]));
						ll.setLat(Double.parseDouble(temp[1]));
						rtLngLatList.add(ll);
					}

					LngLat center = LBSLocationUtil.getCenterPoint(rtLngLatList);
					TempLocationInfo locationInfo = new TempLocationInfo();
					locationInfo.setPolygon(sb.toString());
					locationInfo.setCenter(center);

					// System.out.println(locationInfo.getPolygon() + "," +
					// locationInfo.getCenter().getLongitude() + ","
					// + locationInfo.getCenter().getLantitude());

					rtList.add(locationInfo);

				}
				sb = new StringBuilder();

				continue;
			}
			if (startIndex == 1) {
				startIndex = 2;
				continue;
			}

			if (line.indexOf("Pen") >= 0) {
				startIndex = 0;
				continue;
			}

			if (startIndex == 0) {
				continue;
			}

			if (startIndex == 2) {
				String[] array = line.split(" ");
				LngLat ll = BaiduCoordinateConvertor
						.bd_encrypt(new LngLat(Double.parseDouble(array[0]), Double.parseDouble(array[1])));

				sb.append(ll.getLng()).append(",").append(ll.getLat()).append(";");
			}
		}
		br.close();
		return rtList;
	}

	private void loadUnivercityInfo() throws Exception {

		File outFile = new File("E:\\baidu-coor.result");
		if (outFile.exists()) {
			outFile.delete();
		}
		outFile.createNewFile();

		// true = append file
		FileWriter fileWritter = new FileWriter(outFile, true);

		String originDirAbsolutePath = "F:\\coordinate\\origin";
		// String originDirAbsolutePath =
		// "E:\\workspace\\Test\\src\\com\\�ɶ�-����-����";
		File originDir = new File(originDirAbsolutePath);
		String[] filePathList = originDir.list();
		String cityFileDirAbsolutePath = null;
		for (String filePath : filePathList) {
			cityFileDirAbsolutePath = originDirAbsolutePath + "\\" + filePath;

			if (!new File(cityFileDirAbsolutePath).isDirectory()) {
				continue;
			}

			// System.out.println(cityFileDirAbsolutePath);

			String lngLatAbsolutePath = cityFileDirAbsolutePath + "\\C_CommunityRegion.mif";
			if (!new File(lngLatAbsolutePath).exists()) {
				continue;
			}

			String infoAbsolutePath = cityFileDirAbsolutePath + "\\C_CommunityRegion.mid";
			if (!new File(infoAbsolutePath).exists()) {
				continue;
			}

			List<TempLocationInfo> locationList = loadLngLatInfo(lngLatAbsolutePath);

			File infoFile = new File(infoAbsolutePath);

			BufferedReader br = new BufferedReader(new FileReader(infoFile));
			String line = null;

			int index = 0;
			while ((line = br.readLine()) != null) {

				// System.out.println(new
				// CpdetectorEncodingUtil().getFastEncoding(line.getBytes()));

				if (index >= locationList.size()) {
					break;
				}

				if (line.trim().equals("")) {
					continue;
				}
				String[] array = line.replaceAll("\t", ",").replaceAll(" ", ",").split(",");
				// System.out.println(line);
				// String(line.getBytes("US-ASCII"),"utf8"));
				// System.out.println(line);

				locationList.get(index).setBcode(array[0].trim().replaceAll("\"", ""));
				locationList.get(index).setName(array[1].trim().replaceAll("\"", ""));

				TempLocationInfo info = locationList.get(index);

				// if (info.getName().equals("�Ϻ�����ѧԺƽ��У��") ||
				// info.getName().equals("���ݴ�ѧ(������У��)")) {
				// String str = info.getBcode() + "," + info.getName() + "," +
				// info.getPolygon() + ","
				// + info.getCenter().getLongitude() + "," +
				// info.getCenter().getLantitude() + "\n";
				// System.out.println(str);
				// }

				if (UnivercityMap.getUnivercityNameMap().containsKey(info.getName().trim())) {
					// String str = info.getBcode() + "," + info.getName() + ","
					// + info.getPolygon() + ","
					// + info.getCenter().getLongitude() + "," +
					// info.getCenter().getLantitude() + "\n";

					String str = "INSERT INTO location_coordinate_v2(location_name, bcode, lat, lng, polygon, merge_location_code, merge_location_name, city_code, city_name, description, ctime, utime) VALUES(\""
							+ info.getName() + "\",\"" + info.getBcode() + "\"," + +info.getCenter().getLat() + ","
							+ info.getCenter().getLng() + ",\"" + info.getPolygon() + "\",0,\"\",0,\"\",\"\","
							+ System.currentTimeMillis() + "," + System.currentTimeMillis() + ");\n";
					System.out.println(str);
					// System.exit(0);
					UnivercityMap.getUnivercityNameMap().put(info.getName(), str);
				}

				index++;
			}
			br.close();
		}

		BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		for (String name : UnivercityMap.getUnivercityNameMap().keySet()) {
			if (UnivercityMap.getUnivercityNameMap().get(name) == null) {
				// �Ϻ�����ѧԺƽ��У��,���ݴ�ѧ(������У��) ������
				System.out.println(name);
			} else {
				bufferWritter.write(UnivercityMap.getUnivercityNameMap().get(name));
				bufferWritter.flush();
			}
		}
		bufferWritter.close();
	}

	public static void main(String[] args) throws Exception {
		new BaiduLocalConvertor().loadUnivercityInfo();
	}

	public static class TempLocationInfo {

		private String bcode;

		private String name;

		private LngLat center;

		private String polygon;

		public LngLat getCenter() {
			return center;
		}

		public void setCenter(LngLat center) {
			this.center = center;
		}

		public String getPolygon() {
			return polygon;
		}

		public void setPolygon(String polygon) {
			this.polygon = polygon;
		}

		public String getBcode() {
			return bcode;
		}

		public void setBcode(String bcode) {
			this.bcode = bcode;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

}
