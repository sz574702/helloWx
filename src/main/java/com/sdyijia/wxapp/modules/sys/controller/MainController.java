package com.sdyijia.wxapp.modules.sys.controller;

import com.sdyijia.wxapp.modules.sys.bean.SysRole;
import com.sdyijia.wxapp.modules.sys.bean.SysUser;
import com.sdyijia.wxapp.modules.sys.repository.PermissionRepository;
import com.sdyijia.wxapp.modules.sys.repository.RoleRepository;
import com.sdyijia.wxapp.modules.sys.repository.UserRepository;
import com.sdyijia.wxapp.util.EncryptionUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
public class MainController {
    public final static Logger logger = LoggerFactory.getLogger(MainController.class);

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PermissionRepository permissionRepository;

    @GetMapping("/login")
    public String login(Model m, RedirectAttributes redirectAttributes) {
        Subject currentUser = SecurityUtils.getSubject();
        if (currentUser.isAuthenticated()) {
            return "index";
        }
        Map<String, ?> map = redirectAttributes.getFlashAttributes();
        if (map != null && map.size() > 0) {
            m.addAllAttributes(map);
        }
        return "login";
    }

    @RequestMapping("/")
    public String index() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@Valid SysUser user, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "login";
        }
        String loginName = user.getUsername();
        logger.info("准备登陆用户 =>  " + loginName);
        //根据用户名从数据库中获取用户信息
        SysUser dbuser = userRepository.findByUsername(user.getUsername());
        String salt = "";
        String pwd = "";
        //判断该用户是否使用默认掩码
        if (Objects.nonNull(dbuser) && Objects.nonNull(dbuser.getSalt()) && !"".equals(dbuser.getSalt().trim()))
            salt = dbuser.getSalt();
        //如果掩码为空使用的是EncryptionUtils类的常量salt
        if (salt != null && !salt.trim().equals("")) {
            pwd = EncryptionUtils.getSha512Hash(user.getPassword(), salt);
        } else {
            pwd = EncryptionUtils.getSha512Hash(user.getPassword());
        }
        UsernamePasswordToken token = new UsernamePasswordToken(loginName, pwd);
        //获取当前的Subject
        Subject currentUser = SecurityUtils.getSubject();
        try {
            //在调用了login方法后,SecurityManager会收到AuthenticationToken,并将其发送给已配置的Realm执行必须的认证检查
            //每个Realm都能在必要时对提交的AuthenticationTokens作出反应
            //所以这一步在调用login(token)方法时,它会走到MyRealm.doGetAuthenticationInfo()方法中,具体验证方式详见此方法
            logger.info("对用户[" + loginName + "]进行登录验证..验证开始");
            currentUser.login(token);
            logger.info("对用户[" + loginName + "]进行登录验证..验证通过");
        } catch (UnknownAccountException un) {
            redirectAttributes.addFlashAttribute("message", "未知账户");
        } catch (IncorrectCredentialsException ice) {
            logger.info("对用户[" + loginName + "]进行登录验证..验证未通过,错误的凭证");
            redirectAttributes.addFlashAttribute("message", "密码不正确");
        } catch (LockedAccountException lae) {
            logger.info("对用户[" + loginName + "]进行登录验证..验证未通过,账户已锁定");
            redirectAttributes.addFlashAttribute("message", "账户已锁定");
        } catch (ExcessiveAttemptsException eae) {
            logger.info("对用户[" + loginName + "]进行登录验证..验证未通过,错误次数过多");
            redirectAttributes.addFlashAttribute("message", "用户名或密码错误次数过多");
        } catch (AuthenticationException ae) {
            if (ae instanceof UnknownAccountException) {
                logger.info("对用户[" + loginName + "]进行登录验证..验证未通过,未知账户");
                redirectAttributes.addFlashAttribute("message", "未知账户");
            } else {
                redirectAttributes.addFlashAttribute("message", "用户名或密码不正确");
            }
            //通过处理Shiro的运行时AuthenticationException就可以控制用户登录失败或密码错误时的情景
//            logger.info("对用户[" + loginName + "]进行登录验证..验证未通过,堆栈轨迹如下");
//            ae.printStackTrace();
        }
        //验证是否登录成功
        if (currentUser.isAuthenticated()) {
            logger.info("用户[" + loginName + "]登录认证通过(这里可以进行一些认证通过后的一些系统参数初始化操作)");
            //根据用户获取其权限列表
            List<SysRole> list = dbuser.getRoleList();
//            list.forEach();
            return "index";
        } else {
            token.clear();
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String logout(RedirectAttributes redirectAttributes) {
        //使用权限管理工具进行用户的退出，跳出登录，给出提示信息
        SecurityUtils.getSubject().logout();
        redirectAttributes.addFlashAttribute("message", "您已安全退出");
        return "redirect:/login";
    }


    @RequestMapping("/403")
    public String unauthorizedRole() {
        System.out.println("------没有权限-------");
        return "403";
    }


}
