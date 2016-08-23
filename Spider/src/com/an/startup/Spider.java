package com.an.startup;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.an.model.MainFrame;
import com.an.util.ExcelHelper;

public class Spider {

	public volatile static int CURRENT_EXCELL_ROW_NUM = 1;
	public static final String HOST_URL = "http://m.shuzheng.com/";
	public static String INFO = "";

	public static void extractDataById(int maxId, int batchSize, String outputPath) throws Exception {

		batchSize = batchSize > maxId?maxId:batchSize;

		List<String> urls = new ArrayList<>();
		Workbook wb = new HSSFWorkbook();
		OutputStream stream = null;

		try {
			stream = new FileOutputStream(outputPath);
			Sheet sheet = (Sheet) wb.createSheet("保健食品库");
			ExcelHelper.createHeader(sheet);

			for (int id = 1; id < maxId; id++) {
				urls.add("http://m.shuzheng.com/do.php?op=guochanlanmaozidetail&id=" + id);
				if (id % batchSize == 0 || id == maxId - 1) {
					feedData2Sheet(fetchDataViaUrls(urls), sheet);
					urls = new ArrayList<>();
				}
			}

			wb.write(stream);

		} catch (Exception e) {
			throw e;
		} finally {
			if (stream != null) {
				stream.close();
			}
		}

	}

	public static List<Elements> fetchDataViaUrls(List<String> urls) {
		
		List<Elements> elementsList = new ArrayList<>();
		urls.parallelStream().forEach(url -> {
			try {
				elementsList.add(fetchFieldsValue(url));
			} catch (Exception e) {
				MainFrame.appendInfo("Error "+e.getMessage()+ " occured while fetching from " + url);
			}
		});

		return elementsList;
	}

	public static void feedData2Sheet(List<Elements> elementsList, Sheet sheet) {
		
		for (Elements fields : elementsList) {
			
			String bianhao = fields.get(0).select("td").get(1).text();
			
			if (bianhao != null && !"".equals(bianhao.trim())) {

				Row row = sheet.createRow(CURRENT_EXCELL_ROW_NUM++);
				Cell nameCell = row.createCell(0);
				nameCell.setCellValue(fields.attr("name"));

				for (int i = 0; i < fields.size(); i++) {
					Elements tds = fields.get(i).select("td");
					String fieldName = tds.get(0).text();
					fieldName = fieldName.substring(0, fieldName.length() - 1).trim();
					String value = tds.get(1).text();
					Cell cell = row.createCell(i + 1);
					cell.setCellValue(value);
				}
			}
		}
	}

	public static Elements fetchFieldsValue(String url) throws Exception {
		Document doc = Jsoup.connect(url).timeout(5000).get();
		Element elem = doc.getElementById("zhengwen");
		String name = elem.child(0).text();
		Elements fields = elem.child(1).select("tr");
		fields.attr("name", name);
		MainFrame.appendInfo("Get " + name + " from : " + url);
		return elem.child(1).select("tr");
	}

	public static List<String> fetchRelocatedUrlsByDate(String date) throws Exception {
		
		String host = "http://m.shuzheng.com/do.php?op=guochanlanmaozijiansuo";
		PrintWriter out = null;
		BufferedReader in = null;
		
		try {
			
			HttpPost httppost = new HttpPost(host);
			// 建立HttpPost对象
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("shenqingriqi", date));
			httppost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse response = new DefaultHttpClient().execute(httppost);
			String location = response.getHeaders("location")[0].getValue();
			String relocatedUrl = HOST_URL + location;
			MainFrame.appendInfo("Fetching ids for relocated Url " + relocatedUrl);
			return fetchIdUrlViaRelocatedUrl(relocatedUrl);
		} catch (Exception e) {
			throw e;
		}
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static List<String> fetchIdUrlViaRelocatedUrl(String url) throws Exception {

		Document doc = Jsoup.connect(url).timeout(5000).get();
		Element elem = doc.getElementById("zhengwen");
		Elements linkElements = elem.select("li>a");

		List<String> urlList = new ArrayList<>();

		for (Element linkElem : linkElements) {
			String idPath = linkElem.attr("href");
			if (idPath != null && idPath.contains("guochanlanmaozidetail")) {
				urlList.add(HOST_URL + idPath);
			}
		}

		return urlList;
	}

	public static List<String> fetchIdUrlsByDateRange(List<String> dates) throws Exception{

		MainFrame.appendInfo("\r\nStart fetching relocated urls....");
		// Step 2 - Fetch relocated Urls
		List<String> relocatedUrls = new ArrayList<>();
		dates.parallelStream().forEach(date -> {
			try {
				// Get relocated urls
				relocatedUrls.addAll(fetchRelocatedUrlsByDate(date));
			} catch (Exception e) {
				MainFrame.appendInfo("Error "+e.getMessage()+ " occured while fetching relocated urls for " + date);
			}
		});
		
		MainFrame.appendInfo("\r\nStart fetching ID urls....");
		// Step 3 - Fetch ID Urls
		List<String> idUrls = new ArrayList<>();
		relocatedUrls.parallelStream().forEach(relocatedUrl -> {
			try {
				idUrls.addAll(fetchIdUrlViaRelocatedUrl(relocatedUrl));
			} catch (Exception e) {
				MainFrame.appendInfo("Error "+e.getMessage()+ " occured while fetching from " + relocatedUrl);
			}
		});

		return relocatedUrls;
	}

	/*
	 * @Description: Fetch data via dates
	 * 
	 */
	public static void fetchDataByDate(List<String> dates, String fileName) throws Exception {

		Workbook wb = new HSSFWorkbook();
		OutputStream stream = null;

		try {
			Sheet sheet = (Sheet) wb.createSheet("Drug Date");
			ExcelHelper.createHeader(sheet);
			feedData2Sheet(fetchDataViaUrls(fetchIdUrlsByDateRange(dates)), sheet);
			
			stream = new FileOutputStream(fileName);
			wb.write(stream);
			MainFrame.appendInfo("\r\nDone.");
			CURRENT_EXCELL_ROW_NUM = 1;
		} catch (Exception e) {
			throw e;
		} finally {
			if (stream != null) {
				stream.close();
			}
		}

	}
}
