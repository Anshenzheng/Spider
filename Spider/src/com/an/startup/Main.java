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

public class Main {

	public volatile static int CURRENT_EXCELL_ROW_NUM = 1;

	public static void main(String[] args) {
		try {
			//extractFromSite(30,10,"C:\\annan\\test.xls");
			queryByDate("2016-06-24");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void extractFromSite(int maxId, int batchSize, String outputPath) throws Exception {
		if (batchSize > maxId) {
			throw new Exception("batch size is great than max id, please check your input");
		}
		List<Integer> ids = new ArrayList<>();
		// 创建工作文档对象
		Workbook wb = new HSSFWorkbook();
		// 创建文件流
		OutputStream stream = null;

		try {
			stream = new FileOutputStream(outputPath);

			// 创建sheet对象
			Sheet sheet = (Sheet) wb.createSheet("sheet");
			createHeader(sheet);

			for (int id = 1; id < maxId; id++) {
				ids.add(id);
				if (id % batchSize == 0 || id == maxId - 1) {
					List<Elements> elementsList = new ArrayList<>();
					ids.parallelStream().forEach(currentId -> {
						try {
							elementsList.add(fetchFieldsValue(currentId));
						} catch (Exception e) {
							e.printStackTrace();
						}
					});
					writeElements2Xls(elementsList, sheet);
					// 写入数据
					ids = new ArrayList<>();
				}
			}
			wb.write(stream);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭文件流
			if (stream != null) {
				stream.close();

			}
		}

	}

	public static void writeElements2Xls(List<Elements> elementsList, Sheet sheet) {
		for (Elements fields : elementsList) {
			String bianhao = fields.get(0).select("td").get(1).text();
			if (bianhao != null && !"".equals(bianhao.trim())) {

				Row row = sheet.createRow(CURRENT_EXCELL_ROW_NUM++);
				Cell idCell = row.createCell(0);
				idCell.setCellValue(fields.attr("id"));
				Cell nameCell = row.createCell(1);
				nameCell.setCellValue(fields.attr("name"));
				
				for (int i = 0; i < fields.size(); i++) {
					Elements tds = fields.get(i).select("td");
					String fieldName = tds.get(0).text();
					fieldName = fieldName.substring(0, fieldName.length() - 1).trim();
					String value = tds.get(1).text();
					Cell cell = row.createCell(i+2);
					cell.setCellValue(value);
				}
			}
		}
	}

	public static Elements fetchFieldsValue(int id) throws Exception {
		final String url = "http://m.shuzheng.com/do.php?op=guochanlanmaozidetail&id=" + id;
		Document doc = Jsoup.connect(url).timeout(5000).get();
		Element elem = doc.getElementById("zhengwen");
		System.out.println("processing " + url);
		String name = elem.child(0).text();
		Elements fields = elem.child(1).select("tr");
		fields.attr("id",id+"");
		fields.attr("name",name);
		return elem.child(1).select("tr");
	}

	public static void createHeader(Sheet sheet) {
		Row header = sheet.createRow(0);
		
		Cell cell00 = header.createCell(0);
		cell00.setCellValue("ID");
		
		Cell cell01 = header.createCell(1);
		cell01.setCellValue("名称");
		
		
		Cell cell0 = header.createCell(2);
		cell0.setCellValue("批准文号");

		Cell cell1 = header.createCell(3);
		cell1.setCellValue("批准日期");

		Cell cell2 = header.createCell(4);
		cell2.setCellValue("申请人");

		Cell cell3 = header.createCell(5);
		cell3.setCellValue("申请人地址");

		Cell cell4 = header.createCell(6);
		cell4.setCellValue("保健功能");

		Cell cell5 = header.createCell(7);
		cell5.setCellValue("功效成分");

		Cell cell6 = header.createCell(8);
		cell6.setCellValue("主要原料");

		Cell cell7 = header.createCell(9);
		cell7.setCellValue("适宜人群");

		Cell cell8 = header.createCell(10);
		cell8.setCellValue("不适宜人群");

		Cell cell9 = header.createCell(11);
		cell9.setCellValue("食用方法");

		Cell cell10 = header.createCell(12);
		cell10.setCellValue("产品规格");

		Cell cell11 = header.createCell(13);
		cell11.setCellValue("保质期");

		Cell cell12 = header.createCell(14);
		cell12.setCellValue("贮藏方法");

		Cell cell13 = header.createCell(15);
		cell13.setCellValue("注意事项");

		Cell cell14 = header.createCell(16);
		cell14.setCellValue("批文有效期");

		Cell cell15 = header.createCell(17);
		cell15.setCellValue("变更内容");

		Cell cell16 = header.createCell(18);
		cell16.setCellValue("批准变更日期");

		Cell cell17 = header.createCell(19);
		cell17.setCellValue("转让方");

		Cell cell18 = header.createCell(20);
		cell18.setCellValue("受让方");

		Cell cell19 = header.createCell(21);
		cell19.setCellValue("转让前批号");

		Cell cell20 = header.createCell(22);
		cell20.setCellValue("批准转让日期");

		Cell cell21 = header.createCell(23);
		cell21.setCellValue("注销原因");

		Cell cell22 = header.createCell(24);
		cell22.setCellValue("注销日期");

		Cell cell23 = header.createCell(25);
		cell23.setCellValue("备注");
	}

	public static void queryByDate(String date){
		String host = "http://m.shuzheng.com/do.php?op=guochanlanmaozijiansuo";
		PrintWriter out = null;
        BufferedReader in = null;
        try {
        	HttpPost httppost=new HttpPost(host);  
            //建立HttpPost对象  
            List<NameValuePair> params=new ArrayList<NameValuePair>();  
            params.add(new BasicNameValuePair("shenqingriqi",date));  
            httppost.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));  
            HttpResponse response=new DefaultHttpClient().execute(httppost);              
            String location =  response.getHeaders("location")[0].getValue();
            String relocatedUrl = "http://m.shuzheng.com/"+location;
            queryIdsWithUrl(relocatedUrl);
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
	}
	
	public static List<String> queryIdsWithUrl(String url) throws Exception{
		List<String> urlList = new ArrayList<>();
		Document doc = Jsoup.connect(url).timeout(5000).get();
		Element elem = doc.getElementById("zhengwen");
		Elements linkElements = elem.select("li>a");
		for(Element linkElem:linkElements){
			String linkUrl = linkElem.attr("href");
			if(linkUrl.contains("guochanlanmaozidetail")){
				//System.out.println("link: "+linkUrl);
				urlList.add(linkUrl);
			}
		}
		
		return urlList;
	}
}
