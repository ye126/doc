//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jfeat.am.uaas.config;

import com.jfeat.am.config.web.EnabledShiroExtension;
import com.jfeat.am.config.web.FilterChainMap;
import com.jfeat.am.config.web.Realms;
import com.jfeat.am.config.web.ShiroConfig;
import com.jfeat.am.core.jwt.JWTFilter;
import com.jfeat.am.core.jwt.JWTPubFilter;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.Filter;

import com.jfeat.am.uaas.system.config.web.AccountShiroConfig;
import com.jfeat.am.uaas.system.config.web.EnabledShiroExtensionConfig;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.*;

@Configuration
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {ShiroFilterFactoryBean.class, AccountShiroConfig.class, EnabledShiroExtensionConfig.class}))
public class ShiroConfig2 extends ShiroConfig {
    @Bean
    @Override
    public ShiroFilterFactoryBean shiroFilter(DefaultSecurityManager securityManager, FilterChainMap filterChainMap) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);
        Map<String, Filter> filterMap = shiroFilter.getFilters();
        filterMap.put("jwt", new JWTFilter());
//        filterMap.put("jwtU", new JWTUFilter());
        filterMap.put("jwtPub", new JWTPubFilter());
        shiroFilter.setLoginUrl("/login");
        shiroFilter.setSuccessUrl("/");
        shiroFilter.setUnauthorizedUrl("/global/error");
        Map<String, String> hashMap = new LinkedHashMap();
        hashMap.put("/static/**", "anon");
        if (filterChainMap != null && filterChainMap.getFilters().size() > 0) {
            hashMap.putAll(filterChainMap.getFilters());
        }

//        hashMap.put("/api/oauth/reset_password", "jwt");
//        hashMap.put("/api/sys/oauth/**", "anon");
//        hashMap.put("/api/app/oauth/**", "anon");
//        hashMap.put("/api/pub/**", "jwtPub");
//        hashMap.put("/api/u/**", "jwtU");
//        hashMap.put("/api/**", "jwt");
        hashMap.put("/**", "anon");
        shiroFilter.setFilterChainDefinitionMap(hashMap);
        return shiroFilter;
    }
}