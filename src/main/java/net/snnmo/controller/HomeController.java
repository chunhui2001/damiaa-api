package net.snnmo.controller;

import net.snnmo.assist.ApiResult;
import net.snnmo.dao.IAddrDAO;
import net.snnmo.dao.IUserDAO;
import net.snnmo.dao.UserDaoImpl;
import net.snnmo.entity.UserEntity;
import org.codehaus.jackson.node.ObjectNode;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
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

    private IUserDAO userDao;
    private IAddrDAO addrDao;

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
        ModelAndView mv = new ModelAndView("home/index");
        mv.addObject("message", "This is Spring MVC index page~");
        return mv;
    }


    @RequestMapping(value="/me", method = RequestMethod.GET)
    public ResponseEntity<ApiResult> me() {

        ApiResult result = new ApiResult();

        UserEntity user = userDao.findByName(this.getCurrentUserName());

        //user.setPasswd(null);

        Map<String, String> userInfo = new HashMap<>();

        int orderCount  = 100;


        userInfo.put("name", user.getName());
        userInfo.put("addressCount", addrDao.userAddrList(user.getId()).size() + "");
        userInfo.put("orderCount", orderCount > 99 ? "99+" : orderCount+"");


        result.setData(userInfo);

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
                result.setMessage("用户名已经存在!");
                result.setStatus(HttpStatus.BAD_REQUEST);

                return new ResponseEntity<ApiResult>(result, HttpStatus.OK);
            }

            // save user to db
            try {
                userDao.saveOrUpdate(user);
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
