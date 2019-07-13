package com.ns.timezone.javatimezone;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = JavaTimezoneServiceApplicationTests.AppConfig.class)
public class JavaTimezoneServiceApplicationTests
{
	@Configuration
	static class AppConfig
	{
		@Bean
		TimezoneService timezoneService() {
			return new TimezoneService();
		}
	}

	@Autowired
	private TimezoneService timezoneService;

	@Test
	public void verifyGetAllTimezoneDisplay() {
		Flux<TimezoneService.TimezoneDisplayInfo> timezones = timezoneService.getAllTimezoneDisplay();
		int count = timezones.collectList().block().size();
		Assert.assertEquals(627, count);
	}

}
