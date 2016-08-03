package net.snnmo.controller;

import net.snnmo.assist.ApiResult;
import net.snnmo.assist.Common;
import net.snnmo.assist.OrderStatus;
import net.snnmo.assist.UserRole;
import net.snnmo.dao.IAddrDAO;
import net.snnmo.dao.IOrderDAO;
import net.snnmo.dao.IUserDAO;
import net.snnmo.dao.UserDaoImpl;
import net.snnmo.entity.UserEntity;
import org.codehaus.jackson.node.ObjectNode;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by TTong on 16-1-8.
 */
@Controller
public class HomeController extends BaseController {

    private IUserDAO    userDao;
    private IAddrDAO    addrDao;
    private IOrderDAO   orderDao;

    @Value("http://${site.hostname}: ${example.message} (${domain.name})")
    private String message;

    public IOrderDAO getOrderDao() {
        return orderDao;
    }

    public void setOrderDao(IOrderDAO orderDao) {
        this.orderDao = orderDao;
    }

    public IUserDAO getUserDao() {
        return userDao;
    }

    public void setUserDao(IUserDAO userDao) {
        this.userDao = userDao;
    }


    public IAddrDAO getAddrDao() {
        return addrDao;
    }

    public void setAddrDao(IAddrDAO addrDao) {
        this.addrDao = addrDao;
    }
    //http://localhost:8080/damiaa-api/oauth/token?grant_type=password&client_id=ios-clients&client_secret=ios&username=keesh.zhang&password=111111
    //http://localhost:8080/damiaa-api/me/?access_token=35d54933-2eff-46c8-a100-e5cb083580f9

    //
    // curl -v -X PUT
    // -H "Accept: application/json"
    // -H "Content-Type: application/json"
    // -H "Authorization: Bearer 5c7e46c8-9c56-45b6-b72d-b26f2226d53d"
    // http://192.168.88.142:8080/damiaa-api/roles/sfsdfs
    //


    @RequestMapping(value={"/", "/index", "/welcome"}, method = RequestMethod.GET)
    public ModelAndView index(HttpServletRequest request) {

        // sudo timedatectl set-timezone Asia/Shanghai

        ModelAndView mv = new ModelAndView("home/index");


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");

        mv.addObject("message", "Welcome to Spring MVC index page 12: ~ "
                + this.message);

//        Common.SendSimpleMessage(
//                "snnmo.com", "key-a5179b50c49cbbea7aedcf1b12165d70"
//                , "Chunhui Zhang", "76920104@qq.com"
//                , new String[]{"chunhui2001@gmail.com", "18500183080@163.com"}
//                , "text subject2", "<h1>text content2</h1>", "html");

        return mv;
    }


    @RequestMapping(value="/me", method = RequestMethod.GET)
    public ResponseEntity<ApiResult> me() {

        ApiResult result = new ApiResult();

        UserEntity user = userDao.findByName(this.getCurrentUserName());

        Map<String, Object> userInfo = new HashMap<>();

        long orderCount     = orderDao.count(user.getId());
        boolean isAdmin     = userDao.hasAnyRole(user, new UserRole[]{ UserRole.ROLE_ADMIN, UserRole.ROLE_SUPERADMIN });

        userInfo.put("name", user.getName());
        userInfo.put("photo", user.getPhoto());
        userInfo.put("isAdmin", isAdmin);
        //userInfo.put("addressCount", addrDao.userAddrList(user.getId()).size() + "");
        //userInfo.put("orderCount", orderCount > 99 ? "99+" : orderCount+"");

        result.setData(userInfo);

        return new ResponseEntity<ApiResult>(result, HttpStatus.OK);
    }


    @RequestMapping(value="/openid/", method = RequestMethod.GET)
    public ResponseEntity<ApiResult> openid() {
        ApiResult result = new ApiResult();

        UserEntity user = userDao.findByName(this.getCurrentUserName());

        result.setData(user.getOpenId());

        return new ResponseEntity<ApiResult>(result, HttpStatus.OK);
    }

    @RequestMapping(value="/statistic", method = RequestMethod.GET)
    public ResponseEntity<ApiResult> statistic() {
        ApiResult result = new ApiResult();

        UserEntity user     = userDao.findByName(this.getCurrentUserName());
        boolean isAdmin     = userDao.hasAnyRole(user, new UserRole[]{ UserRole.ROLE_ADMIN, UserRole.ROLE_SUPERADMIN });


        Map<String, Object> statistic = new HashMap<>();

        long orderCount  = orderDao.count(user.getId());

        statistic.put("addressCount", addrDao.userAddrList(user.getId()).size() + "");
        statistic.put("orderCount", orderCount > 99 ? "99+" : orderCount+"");

        if (isAdmin) {
            statistic.put("orderCount_WaitingSend", orderDao.list(new OrderStatus[]{ OrderStatus.CASHED }).size());
        }

        result.setData(statistic);

        return new ResponseEntity<ApiResult>(result, HttpStatus.OK);
    }


    @ModelAttribute
    @RequestMapping(value="/resetpwd",
            method = RequestMethod.POST,
            headers="Accept=application/json",
            produces = {"application/json"})
    public ResponseEntity<ApiResult> resetpwd(
            @RequestBody Map<String, String> params) throws Exception {

        ApiResult result = new ApiResult();

        String oldPwd = params.get("oldPwd");
        String newPwd = params.get("newPwd");

        String errorMessage     = null;

       // UserEntity user = userDao.findByName(this.getCurrentUserName());

        errorMessage = userDao.resetPassword(this.getCurrentUserName(), oldPwd, newPwd);

        if (errorMessage != null) {
            result.setMessage(errorMessage);
            result.setStatus(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<ApiResult>(result, HttpStatus.OK);
    }



    @ModelAttribute
    @RequestMapping(value = "/register", method = RequestMethod.POST, headers="Accept=application/json", produces = {"application/json"})
    public ResponseEntity<ApiResult> register(
            @Valid @RequestBody UserEntity user, BindingResult bindResult) {

        ApiResult result = new ApiResult();

        if (bindResult.hasErrors()) {
            result.setMessage(bindResult.getAllErrors().toString());
            result.setStatus(HttpStatus.BAD_REQUEST);
        } else {
            Boolean isExists = userDao.findByName(user.getName()) != null;

            if (isExists) {
                result.setMessage("exists:用户名已经存在!");
                result.setStatus(HttpStatus.BAD_REQUEST);

                return new ResponseEntity<ApiResult>(result, HttpStatus.OK);
            } else {
                if (user.getOpenId() != null) {
                    isExists = userDao.findByOpenId(user.getOpenId()) != null;

                    if (isExists) {
                        result.setMessage("exists:用户名已经存在!");
                        result.setStatus(HttpStatus.BAD_REQUEST);

                        return new ResponseEntity<ApiResult>(result, HttpStatus.OK);
                    }
                }
            }

            // save user to db
            try {
                userDao.create(user);
            } catch (Exception e) {
                result.setMessage(e.getMessage());
                result.setStatus(HttpStatus.BAD_REQUEST);
            }

            if (result.getStatus() != HttpStatus.BAD_REQUEST) {
                //result.setMessage(user.getId());
                result.setData(user.getId());
            }
        }

        return new ResponseEntity<ApiResult>(result, HttpStatus.OK);
    }

    @ModelAttribute
    @RequestMapping(value = "/register", headers="Accept=text/html")
    public ModelAndView register(
            @Valid @ModelAttribute("user1") UserEntity user, BindingResult bindResult, HttpServletRequest request) {
        ModelAndView mv = new ModelAndView("home/register");

        if (request.getMethod().toUpperCase().equals("POST")) {
            mv.addObject("isPost", true);

            if (bindResult.hasErrors()) {
                mv.addObject("user", user);
                return mv;
            }

            // save user to db
            userDao.saveOrUpdate(user);
            return new ModelAndView("redirect:/?userid="+user.getId());
        }

        return mv;
    }






    @RequestMapping(value="/api/test/users", method = RequestMethod.GET)
    public ResponseEntity<Collection<String>> testUsers() {

        Collection<String> coll = new ArrayList<String>();
        coll.add("keesh");
        coll.add("jenny");
        coll.add("tongtong");
        return new ResponseEntity<Collection<String>>(coll, HttpStatus.OK);
    }
}
