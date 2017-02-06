package de.mpg.mpdl.auth.service;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.springframework.data.auditing.DateTimeProvider;

public class CurrentDateTimeProvider implements DateTimeProvider {

	private final DateTimeService dateTimeService;
	
	public CurrentDateTimeProvider(DateTimeService dateTimeService) {
		this.dateTimeService = dateTimeService;
	}
	
	@Override
	public Calendar getNow() {
		return GregorianCalendar.from(dateTimeService.getCurrentDateAndTime());
	}

}
