package net.snnmo.controller;

import com.sun.management.DiagnosticCommandMBean;
import net.snnmo.assist.ApiResult;
import net.snnmo.dao.IUserDAO;
import net.snnmo.entity.UserEntity;
import org.codehaus.jackson.map.deser.ValueInstantiators;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by TTong on 16-1-11.
 */
//@RequestMapping(value = "/roles"
@Controller
@RequestMapping("/roles")
public class RoleController extends BaseController {

//
//        curl -v -X PUT
//                -H "Accept: application/json"
//                -H "Content-Type: application/json"
//                -H "Authorization: Bearer 5c7e46c8-9c56-45b6-b72d-b26f2226d53d"
//        http://192.168.88.142:8080/damiaa-api/roles/sfsdfs
//

    private IUserDAO userDao;

    @PostConstruct
    public void init() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        UserEntity user = userDao.findByName(userName);

        if (user.getRoles().indexOf("ROLE_ADMIN") == -1) {
            throw new OAuth2Exception("User not have permission to manager roles");
        }
    }

    @RequestMapping(value = {"/", ""}, method = RequestMethod.GET)
    public ResponseEntity<String> index() {
        return new ResponseEntity<String>("HELLLO", HttpStatus.OK);
    }

    @RequestMapping(value = "/{userid}/remove", method = RequestMethod.PUT)
    public ResponseEntity<ApiResult> remove(@PathVariable("userid") String userid,
                                         @RequestBody Map<String, Object> commandBean) {

        ApiResult result = new ApiResult();

        ArrayList<String> listOfRoles = (ArrayList<String>)commandBean.get("roles");

        if (listOfRoles == null || listOfRoles.size() == 0) {
            result.setMessage("roles empty");
            result.setStatus(HttpStatus.BAD_REQUEST);
        } else {
            try {
                userDao.removeRoles(userid, listOfRoles.toArray(new String[listOfRoles.size()]));
            } catch (Exception e) {
                result.setMessage(e.getMessage());
                result.setStatus(HttpStatus.BAD_REQUEST);
            }
        }

        return new ResponseEntity<ApiResult>(result, result.getStatus());
    }

    @RequestMapping(value = "/{userid}/add", method = RequestMethod.PUT)
    public ResponseEntity<ApiResult> add(@PathVariable("userid") String userid,
                                            @RequestBody Map<String, Object> commandBean) {

        ApiResult result = new ApiResult();

        ArrayList<String> listOfRoles = (ArrayList<String>)commandBean.get("roles");

        if (listOfRoles == null || listOfRoles.size() == 0) {
            result.setMessage("roles empty");
            result.setStatus(HttpStatus.BAD_REQUEST);
        } else {
            try {
                userDao.addRoles(userid, listOfRoles.toArray(new String[listOfRoles.size()]));
            } catch (Exception e) {
                result.setMessage(e.getMessage());
                result.setStatus(HttpStatus.BAD_REQUEST);
            }
        }

        return new ResponseEntity<ApiResult>(result, result.getStatus());
    }

    public IUserDAO getUserDao() {
        return userDao;
    }

    public void setUserDao(IUserDAO userDao) {
        this.userDao = userDao;
    }
}
