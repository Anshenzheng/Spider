package com.an.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class DateUtil {

	public static List<String> generateDataList(String fromDate, String toDate){

		LocalDate localFromDate = LocalDate.parse(fromDate, DateTimeFormatter.ISO_DATE);
		LocalDate localToDate = LocalDate.parse(toDate, DateTimeFormatter.ISO_DATE);

		List<String> dateList = new ArrayList<>();
		dateList.add(fromDate);
		int i=0;
		while(true){
			LocalDate tempDate = localFromDate.plus(++i, ChronoUnit.DAYS);
			if(tempDate.compareTo(localToDate) != 0){
				dateList.add(tempDate.toString());
			}else{
				dateList.add(toDate);
				break;
			}
			
		}
		return dateList;
	}
	
	public static void main(String args[]){
		List<String> dates = generateDataList("2016-07-08","2016-07-10");
		dates.stream().forEach(date->{
			System.out.println(date);
		});
	}
}
