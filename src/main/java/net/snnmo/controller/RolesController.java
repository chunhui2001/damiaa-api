package net.snnmo.controller;

import org.codehaus.jackson.map.deser.ValueInstantiators;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by TTong on 16-1-11.
 */
//@RequestMapping(value = "/roles"
@Controller
@RequestMapping("/roles")
public class RolesController extends BaseController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<String> index() {
        return new ResponseEntity<String>("HELLLO", HttpStatus.OK);
    }

    @RequestMapping(value = "/{userid}", method = RequestMethod.PUT)
    public ResponseEntity<String> remove(@PathVariable("userid") String userid,
                                         @RequestParam("roles") String roles) {

//
//        curl -v -X PUT
//                -H "Accept: application/json"
//                -H "Content-Type: application/json"
//                -H "Authorization: Bearer 5c7e46c8-9c56-45b6-b72d-b26f2226d53d"
//        http://192.168.88.142:8080/damiaa-api/roles/sfsdfs
//

        System.out.println(userid);
        System.out.println(roles);

        return new ResponseEntity<String>("", HttpStatus.OK);
    }
}
