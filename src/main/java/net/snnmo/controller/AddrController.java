package net.snnmo.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.Deserializers;
import net.snnmo.assist.ApiResult;
import net.snnmo.dao.IAddrDAO;
import net.snnmo.dao.IUserDAO;
import net.snnmo.entity.AddressEntity;
import net.snnmo.exception.DbException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by TTong on 16-1-12.
 */
@Controller
@RequestMapping(value = {"/addr"})
public class AddrController extends BaseController {

    private IUserDAO userDao;
    private IAddrDAO addrDao;

    @RequestMapping(value={"", "/"}, method = RequestMethod.POST)
    public ResponseEntity<ApiResult> index(
            @RequestBody AddressEntity addressEntity) {

        ApiResult result = new ApiResult();

//        String province  = (String)commandBean.get("province");
//        String city      = (String)commandBean.get("city");
//        String area      = (String)commandBean.get("area");
//        String street    = (String)commandBean.get("street");
//        String detail    = (String)commandBean.get("detail");
//        String pincode   = (String)commandBean.get("pincode");

//        curl -v -X POST -H "Accept: application/json" -H "Content-Type: application/json" -H "Authorization: Bearer 32c0a3b2-2bf8-4ba3-b72c-30a1651a5051"
//        --data '{"area":"朝阳区", "city":"北京市", "detail":"123", "province":"pp", "street":"ss"}' http://192.168.88.142:8080/damiaa-api/addr



        addrDao.add(userDao.findByName(this.getCurrentUserName()), addressEntity);

        return new ResponseEntity<ApiResult>(result, HttpStatus.OK);
    }

    @RequestMapping(value={"/list"},
            method = RequestMethod.GET,
            headers="Accept=application/json",
            produces = {"application/json"})
    public ResponseEntity<ApiResult> list() {

        ApiResult result = new ApiResult();

        result.setData(addrDao.userAddrList(userDao.findByName(this.getCurrentUserName()).getId()));

        return new ResponseEntity<ApiResult>(result, HttpStatus.OK);
    }

    @RequestMapping(value={"/set"},
            method = RequestMethod.POST,
            headers="Accept=application/json",
            produces = {"application/json"})
    public ResponseEntity<ApiResult> set(
            @RequestBody JsonNode params) throws Exception {

        int addrid = params.get("addrid").asInt();

        ApiResult result = new ApiResult();

//        AddressEntity addrentity = addrDao.get(addrid);
//        String userid   = userDao.findByName(this.getCurrentUserName()).getId();
//        if (addrentity.getUser().getId().equals(userid)) {
//            addrentity.setDetault(true);
//            addrDao.update(userid, addrentity);
//        } else {
//            result.serError(true);
//            result.setMessage("修改默认地址失败!");
//        }

        addrDao.setDefault(userDao.findByName(this.getCurrentUserName()).getId(), addrid);


        return new ResponseEntity<ApiResult>(result, HttpStatus.OK);
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
}
