package de.mpg.mpdl.auth.configuration;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import de.mpg.mpdl.auth.web.security.ExceptionHandlerFilter;
import de.mpg.mpdl.auth.web.security.TokenAuthenticationEntryPoint;
import de.mpg.mpdl.auth.web.security.TokenAuthenticationFilter;
import de.mpg.mpdl.auth.web.security.TokenAuthenticationProvider;
import de.mpg.mpdl.auth.web.security.TokenAuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class TokenSecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	@Autowired
	TokenAuthenticationEntryPoint entryPoint;
	
	@Autowired
	TokenAuthenticationProvider provider;
	
	@Autowired
	public void confgureGlobal(AuthenticationManagerBuilder auth) {
		auth.authenticationProvider(provider);
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}
	
	@Bean
	@Override
	protected AuthenticationManager authenticationManager() throws Exception {
		return new ProviderManager(Arrays.asList(provider));
	}
	
	@Bean
	public TokenAuthenticationFilter filterBean() throws Exception {
		TokenAuthenticationFilter filter = new TokenAuthenticationFilter();
		filter.setAuthenticationManager(authenticationManager());
		filter.setAuthenticationSuccessHandler(new TokenAuthenticationSuccessHandler());
		filter.setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler());
		return filter;
	}
	
	@Bean
	public ExceptionHandlerFilter errorBean() throws Exception {
		ExceptionHandlerFilter filter = new ExceptionHandlerFilter();
		return filter;
	}
	
	public void configure(WebSecurity webSecurity) throws Exception {
        webSecurity
            .ignoring()
                .antMatchers(HttpMethod.POST, "/token")
                .antMatchers(HttpMethod.OPTIONS, "/**");
    }
	
	@Override
	  protected void configure(HttpSecurity httpSecurity) throws Exception {
	    httpSecurity
	     .csrf()
	      .disable()
	      .exceptionHandling()
	        .authenticationEntryPoint(entryPoint)
	        .and()
	      .sessionManagement()
	        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	        .and()
	      .authorizeRequests()
	      	.anyRequest().authenticated()
	    .and()
	    .addFilterBefore(errorBean(), SecurityContextPersistenceFilter.class)
	    //.addFilterBefore(new CORSFilter(), ChannelProcessingFilter.class)
	     .addFilterBefore(filterBean(), UsernamePasswordAuthenticationFilter.class);
	  }

}
