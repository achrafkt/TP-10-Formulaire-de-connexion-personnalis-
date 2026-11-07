package ma.fstg.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

// 安全配置类：定义用户、访问规则、自定义登录页和注销行为
@Configuration
public class SecurityConfig {

    // 用户详情服务：在内存中创建两个用户（admin 和 user）
    @Bean
    public UserDetailsService userDetailsService() {
        // 管理员用户，密码 1234，角色 ADMIN（{noop} 表示密码不加密）
        UserDetails admin = User.withUsername("admin")
                .password("{noop}1234")
                .roles("ADMIN")
                .build();

        // 普通用户，密码 1111，角色 USER
        UserDetails user = User.withUsername("user")
                .password("{noop}1111")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, user);
    }

    // 安全过滤器链：拦截每个请求并应用以下规则
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 访问规则
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/css/**").permitAll()          // 登录页和样式所有人可访问
                .requestMatchers("/admin/**").hasRole("ADMIN")             // 仅 ADMIN 角色
                .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")   // USER 和 ADMIN 角色
                .anyRequest().authenticated()                              // 其他请求需要登录
            )
            // 自定义登录表单
            .formLogin(form -> form
                .loginPage("/login")                     // 使用我们自己的登录页面
                .loginProcessingUrl("/authenticate")     // 表单提交地址，由 Spring 自动处理
                .defaultSuccessUrl("/home", true)        // 登录成功后跳转到首页
                .failureUrl("/login?error=true")         // 登录失败后返回登录页并显示错误
                .permitAll()
            )
            // 注销配置
            .logout(logout -> logout
                .logoutUrl("/logout")                    // 注销地址（必须使用 POST，因为启用了 CSRF 保护）
                .logoutSuccessUrl("/login?logout=true")  // 注销成功后显示提示信息
                .permitAll()
            );

        return http.build();
    }
}
