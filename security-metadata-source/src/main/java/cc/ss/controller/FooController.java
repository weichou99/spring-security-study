package cc.ss.controller;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.ExpressionBasedFilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import cc.ss.config.WebSecurityConfig;

@RestController
public class FooController {

  @Autowired
  private ApplicationContext ctx;

  // 這個目前發現在啟動後就會有這個bean。
  @Autowired
  private SecurityExpressionHandler<FilterInvocation> expressionHandler;

  @GetMapping(WebSecurityConfig.TEST_PATH)
  public ResponseEntity<Object> printBeanDefinitiionNames() {
    Object body = "test ok";
    String[] beanDefinitionNames = ctx.getBeanDefinitionNames();
    Arrays.sort(beanDefinitionNames, (String x, String y) -> (x).compareTo(y));
    Arrays.stream(beanDefinitionNames).forEach(x -> System.out.println(x));
    return ResponseEntity.ok(body);
  }

  @GetMapping("updateSecurityMetadataSource")
  public ResponseEntity<Object> updateSecurityMetadataSource() {
    Object body = "updateSecurityMetadataSource ok";

    // 取得FilterSecurityInterceptor
    FilterChainProxy filterChainProxy = ctx.getBean("springSecurityFilterChain", FilterChainProxy.class);
    SecurityFilterChain securityFilterChain = filterChainProxy.getFilterChains().stream().findFirst().get();
    Filter filter = securityFilterChain.getFilters().stream().filter(o -> o instanceof FilterSecurityInterceptor)
        .findFirst().get();
    FilterSecurityInterceptor filterSecurityInterceptor = (FilterSecurityInterceptor) filter;

    // 建立新的securityMetadataSource
    // 使用ExpressionBasedFilterInvocationSecurityMetadataSource，是因為在設定http.authorizeRequests()後，spring
    // security建立的就是ExpressionBasedFilterInvocationSecurityMetadataSource。
    LinkedHashMap<RequestMatcher, Collection<ConfigAttribute>> requestMap = new LinkedHashMap<RequestMatcher, Collection<ConfigAttribute>>();
    AntPathRequestMatcher antPathRequestMatcher = new AntPathRequestMatcher(WebSecurityConfig.TEST_PATH);
    List<ConfigAttribute> createList = SecurityConfig
        .createList("hasAuthority('" + WebSecurityConfig.ROLE_ADMIN + "')");
    requestMap.put(antPathRequestMatcher, createList);
    ExpressionBasedFilterInvocationSecurityMetadataSource securityMetadataSource = new ExpressionBasedFilterInvocationSecurityMetadataSource(
        requestMap, expressionHandler);

    filterSecurityInterceptor.setSecurityMetadataSource(securityMetadataSource);

    return ResponseEntity.ok(body);
  }

}
