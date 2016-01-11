package net.snnmo.controller;

import javafx.print.Collation;
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
public class HomeController {

    private IUserDAO userDao;

    @RequestMapping(value={"/", "/index", "/welcome"}, method = RequestMethod.GET)
    public ModelAndView index(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView("home/index");
        mv.addObject("message", "This is Spring MVC index page~");
        return mv;
    }


    @RequestMapping(value="/api/test/users", method = RequestMethod.GET)
    public ResponseEntity<Collection<String>> testUsers() {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        System.out.println(user.getUsername());

        for(GrantedAuthority authority : user.getAuthorities()) {
           System.out.println(authority.getAuthority());
        }

        Collection<String> coll = new ArrayList<String>();
        coll.add("keesh");
        coll.add("jenny");
        coll.add("tongtong");
        return new ResponseEntity<Collection<String>>(coll, HttpStatus.OK);
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
//
//            Configuration configuration = new Configuration().configure();
//            ServiceRegistry serviceRegistry
//                    = new StandardServiceRegistryBuilder()
//                    .applySettings(configuration.getProperties()).build();
//
//            // builds a session factory from the service registry
//            SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
//
//            userDao = new UserDaoImpl(sessionFactory);

            // save user to db
            userDao.saveOrUpdate(user);
            return new ModelAndView("redirect:/?uderid="+user.getId());
        }

        return mv;
    }


    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(format, true));
    }


    public IUserDAO getUserDao() {
        return userDao;
    }

    public void setUserDao(IUserDAO userDao) {
        this.userDao = userDao;
    }
}
