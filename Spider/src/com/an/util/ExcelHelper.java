package com.an.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class ExcelHelper {

	public static void createHeader(Sheet sheet) {
		Row header = sheet.createRow(0);
		
		Cell cell01 = header.createCell(0);
		cell01.setCellValue("名称");
		
		
		Cell cell0 = header.createCell(1);
		cell0.setCellValue("批准文号");

		Cell cell1 = header.createCell(2);
		cell1.setCellValue("批准日期");

		Cell cell2 = header.createCell(3);
		cell2.setCellValue("申请人");

		Cell cell3 = header.createCell(4);
		cell3.setCellValue("申请人地址");

		Cell cell4 = header.createCell(5);
		cell4.setCellValue("保健功能");

		Cell cell5 = header.createCell(6);
		cell5.setCellValue("功效成分");

		Cell cell6 = header.createCell(7);
		cell6.setCellValue("主要原料");

		Cell cell7 = header.createCell(8);
		cell7.setCellValue("适宜人群");

		Cell cell8 = header.createCell(9);
		cell8.setCellValue("不适宜人群");

		Cell cell9 = header.createCell(10);
		cell9.setCellValue("食用方法");

		Cell cell10 = header.createCell(11);
		cell10.setCellValue("产品规格");

		Cell cell11 = header.createCell(12);
		cell11.setCellValue("保质期");

		Cell cell12 = header.createCell(13);
		cell12.setCellValue("贮藏方法");

		Cell cell13 = header.createCell(14);
		cell13.setCellValue("注意事项");

		Cell cell14 = header.createCell(15);
		cell14.setCellValue("批文有效期");

		Cell cell15 = header.createCell(16);
		cell15.setCellValue("变更内容");

		Cell cell16 = header.createCell(17);
		cell16.setCellValue("批准变更日期");

		Cell cell17 = header.createCell(18);
		cell17.setCellValue("转让方");

		Cell cell18 = header.createCell(19);
		cell18.setCellValue("受让方");

		Cell cell19 = header.createCell(20);
		cell19.setCellValue("转让前批号");

		Cell cell20 = header.createCell(21);
		cell20.setCellValue("批准转让日期");

		Cell cell21 = header.createCell(22);
		cell21.setCellValue("注销原因");

		Cell cell22 = header.createCell(23);
		cell22.setCellValue("注销日期");

		Cell cell23 = header.createCell(24);
		cell23.setCellValue("备注");
	}
}
