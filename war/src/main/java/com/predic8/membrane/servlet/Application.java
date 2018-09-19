package com.predic8.membrane.servlet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

import com.predic8.membrane.servlet.embedded.MembraneServlet;

@SpringBootApplication
public class Application {
	
	
	@Bean
	public ServletRegistrationBean<MembraneServlet> registrationBean() {
	  ServletRegistrationBean<MembraneServlet> bean = new ServletRegistrationBean(new MembraneServlet(), "/*");
	  bean.addInitParameter("proxiesXml", "/WEB-INF/proxies.xml");
	  return bean;
	}
		
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
