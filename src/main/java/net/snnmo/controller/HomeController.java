package net.snnmo.controller;

import javafx.print.Collation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by TTong on 16-1-8.
 */
@Controller
public class HomeController {
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
            @Validated @ModelAttribute("user1") String user, BindingResult bindResult, HttpServletRequest request) {
        ModelAndView mv = new ModelAndView("home/register");

        if (request.getMethod().toUpperCase().equals("POST")) {
            mv.addObject("isPost", true);

            if (bindResult.hasErrors()) {
                mv.addObject("user", user);
                return mv;
            }

            // save user to db
           // userDao.saveOrUpdate(user);
           // return new ModelAndView("redirect:/?uderid="+user.getId());
        }

        return mv;
    }
}
