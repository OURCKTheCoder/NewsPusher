package yiban.ourck.spring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import yiban.ourck.service.search.NewsMaintainceService;

@Configuration
@EnableScheduling
public class DBSyncScheduler {

	@Autowired
	NewsMaintainceService solrManService;
	
	@Scheduled(cron = "0 0 0 0/1 * *")
	public void syncTask() {
		try {
			solrManService.coreDBSync("news_on_chd", "74");
			solrManService.coreDBSync("users_on_chd", "74");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
