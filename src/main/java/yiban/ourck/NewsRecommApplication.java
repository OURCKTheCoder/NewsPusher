package yiban.ourck;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NewsRecommApplication {// extends SpringBootServletInitializer{

//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
//        // 设置启动类,用于独立tomcat运行的入口
//        return builder.sources(NewsRecommApplication.class);
//    }
    
	public static void main(String[] args) {
		SpringApplication.run(NewsRecommApplication.class, args);
	}
	
}
