package com.an.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DateUtil {
	private static String datepatter = "yyyy-MM-dd";
	private static SimpleDateFormat dateFormatter = new SimpleDateFormat(datepatter);
	
	public static String dateToString(Date date){
		return dateFormatter.format(date);
	}

	public static List<String> generateDataList(String fromDate, String toDate) throws Exception{

		LocalDate localFromDate = LocalDate.parse(fromDate, DateTimeFormatter.ISO_DATE);
		LocalDate localToDate = LocalDate.parse(toDate, DateTimeFormatter.ISO_DATE);

		if(localToDate.compareTo(localFromDate) < 0){
			throw new Exception("To date must great than from date");
		}
		List<String> dateList = new ArrayList<>();
		LocalDate tempDate = localFromDate;

		int i=0;
		if(fromDate.equals(toDate)){
			dateList.add(fromDate);
		}else{
			while(true){
				if(tempDate.compareTo(localToDate) != 0){
					tempDate = localFromDate.plus(i++, ChronoUnit.DAYS);
					dateList.add(tempDate.toString());
				}else{
					break;
				}
				
			}
		}
		
		return dateList;
	}
	
}
