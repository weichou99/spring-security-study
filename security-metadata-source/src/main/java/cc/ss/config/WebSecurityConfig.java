package cc.ss.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

@Configuration
//@EnableWebSecurity(debug = true) // 開啟spring security的debug mode。
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  public static final String TEST_PATH = "/printBeanDefinitiionNames";
  public static final String ROLE_ADMIN = "ROLE_ADMIN";
  public static final String ROLE_TEST = "ROLE_TEST";

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder> configurer = auth.inMemoryAuthentication();
    UserBuilder user1 = User.withUsername("admin").password("1234").authorities(ROLE_ADMIN);
    UserBuilder user2 = User.withUsername("test01").password("1234").authorities(ROLE_TEST);
    configurer.withUser(user1);
    configurer.withUser(user2);
    configurer.passwordEncoder(NoOpPasswordEncoder.getInstance());
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
//     super.configure(http);
    http.formLogin();
    http.authorizeRequests().antMatchers(WebSecurityConfig.TEST_PATH).hasAuthority(ROLE_TEST);
    http.authorizeRequests().anyRequest().authenticated();

  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    super.configure(web);
  }

}
