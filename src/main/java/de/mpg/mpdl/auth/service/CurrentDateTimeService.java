package de.mpg.mpdl.auth.service;

import java.time.ZonedDateTime;

public class CurrentDateTimeService implements DateTimeService {

	@Override
	public ZonedDateTime getCurrentDateAndTime() {
		return ZonedDateTime.now();
	}

}
