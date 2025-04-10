package com.hyper.assignment.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateTimeUtil {

	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

	public static String getFormattedLocalDateTimeNow(String format) {
		LocalDateTime localDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		return localDateTime.format(formatter);
	}
}
