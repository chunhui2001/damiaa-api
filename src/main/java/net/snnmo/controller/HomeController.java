package net.snnmo.controller;

import net.snnmo.dao.IUserDAO;
import net.snnmo.dao.UserDaoImpl;
import net.snnmo.entity.UserEntity;
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
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Created by TTong on 16-1-8.
 */
@Controller
public class HomeController extends BaseController {

    private IUserDAO userDao;


    //http://localhost:8080/damiaa-api/oauth/token?grant_type=password&client_id=ios-clients&client_secret=ios&username=keesh.zhang&password=111111
    //http://localhost:8080/damiaa-api/me/?access_token=35d54933-2eff-46c8-a100-e5cb083580f9

    @RequestMapping(value={"/", "/index", "/welcome"}, method = RequestMethod.GET)
    public ModelAndView index(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView("home/index");
        mv.addObject("message", "This is Spring MVC index page~");

        String userid = request.getParameter("userid");
        System.out.println(userid);
        if (userid != null && !userid.isEmpty()) {
//            userDao.addRoles(userid,
//                    new String[]{"ROLE_ADMIN","ROLE_TESTER"});
        }

        return mv;
    }


    @RequestMapping(value="/me", method = RequestMethod.GET)
    public ResponseEntity<String> me() {
        UserEntity user = userDao.findByName(this.getCurrentUserName());

        return new ResponseEntity<String>(user.getName(), HttpStatus.OK);
    }


    @ModelAttribute
    @RequestMapping(value = "/register")
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

    public IUserDAO getUserDao() {
        return userDao;
    }

    public void setUserDao(IUserDAO userDao) {
        this.userDao = userDao;
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
