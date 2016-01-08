package net.snnmo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

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
}
