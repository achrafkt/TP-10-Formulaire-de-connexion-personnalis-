package ma.fstg.security.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// 视图控制器：每个方法返回 templates 目录中的 HTML 页面名称
// 访问权限由 SecurityConfig 控制，这里不需要做任何检查
@Controller
public class AuthController {

    // 自定义登录页面（POST /authenticate 由 Spring Security 处理，无需在此编写）
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // 登录成功后的首页
    @GetMapping("/home")
    public String home() {
        return "home";
    }

    // 管理员空间：仅 ADMIN 角色可访问
    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "admin-dashboard";
    }

    // 用户空间：USER 和 ADMIN 角色可访问
    @GetMapping("/user/dashboard")
    public String userDashboard() {
        return "user-dashboard";
    }
}
