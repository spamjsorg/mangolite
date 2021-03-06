package org.spamjs.mangolite.app;

import javax.websocket.Endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.servlet.DispatcherServlet;

// TODO: Auto-generated Javadoc
//import com.utils.Log;

/**
 * {@link EnableAutoConfiguration Auto-configuration} to enable Spring MVC to
 * handle {@link Endpoint} requests. It is assumed that endpoint requests will
 * be mapped and handled via an already registered {@link DispatcherServlet}.
 * 
 * @author <a href="mailto:lalit.tanwar07@gmail.com"> Lalit Tanwar</a>
 * @version 1.0
 */
public abstract class WebApp extends SpringBootServletInitializer {

	/**
	 * Gets the web app properties.
	 *
	 * @return the web app properties
	 */
	@Bean
	public WebAppProperties getWebAppProperties() {
		return new WebAppProperties();
	}

	/** The simp messaging template. */
	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	/**
	 * Gets the stomp tunnel client.
	 *
	 * @return the stomp tunnel client
	 */
	@Bean
	public StompTunnelClient getStompTunnelClient() {
		return new StompTunnelClient(simpMessagingTemplate);
	}

	/**
	 * Register with FilterRegistrationBean, files which are to be minified or
	 * if any other filter is to be added
	 * 
	 * Refer : http://www.leveluplunch.com/blog/2014/04/01/spring-boot-
	 * configure-servlet-mapping-filters/
	 *
	 * @return the filter registration bean
	 */
	@Bean
	public FilterRegistrationBean filterRegistrationBean() {

		WebAppResourceMinifyFilter compressingFilter = new WebAppResourceMinifyFilter();
		FilterRegistrationBean registrationBean = new FilterRegistrationBean();
		registrationBean.setUrlPatterns(compressingFilter
				.getResourcerlPatterns());
		registrationBean.setFilter(compressingFilter);
		return registrationBean;

	}

	/*
	 * @Bean public ServletRegistrationBean
	 * dispatcherRegistration(DispatcherServlet dispatcherServlet) {
	 * ServletRegistrationBean registration = new ServletRegistrationBean(
	 * dispatcherServlet); registration.addUrlMappings("/page/*",
	 * "/whatever2/*"); return registration; }
	 */
}
