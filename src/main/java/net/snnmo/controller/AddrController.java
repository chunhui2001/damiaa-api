package net.snnmo.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.Deserializers;
import net.snnmo.assist.ApiResult;
import net.snnmo.dao.IAddrDAO;
import net.snnmo.dao.IOrderDAO;
import net.snnmo.dao.IUserDAO;
import net.snnmo.entity.AddressEntity;
import net.snnmo.entity.OrderEntity;
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

    public IOrderDAO getOrderDao() {
        return orderDao;
    }

    public void setOrderDao(IOrderDAO orderDao) {
        this.orderDao = orderDao;
    }

    private IOrderDAO orderDao;

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

//        curl -v -X POST -H "Accept: application/json" -H "Content-Type: application/json" -H "Authorization: Bearer 95296aa6-f759-4369-8b33-d2a53b7caae3"
//        --data '{"area":"南岗区", "city":"南岗区", "detail":"闽江小区13栋4单元302", "province":"黑龙江省", "street":"闽江路", "linkMan": "张春晖", "linkPone": "18500183080"}' http://127.0.0.1:8088/addr


        AddressEntity newAddr = addrDao.add(userDao.findByNameOrOpenId(this.getCurrentUserName()), addressEntity);

        result.setData(newAddr);

        return new ResponseEntity<ApiResult>(result, HttpStatus.OK);
    }


    @RequestMapping(value={"/{addrid}"}, method = RequestMethod.DELETE)
    public ResponseEntity<ApiResult> del(@PathVariable("addrid") long addrid) {

        ApiResult result = new ApiResult();

        result.setData(addrDao.delete(userDao.findByNameOrOpenId(this.getCurrentUserName()).getId(), addrid));

        return new ResponseEntity<ApiResult>(result, HttpStatus.OK);
    }

    @RequestMapping(value={"/list"},
            method = RequestMethod.GET,
            headers="Accept=application/json",
            produces = {"application/json"})
    public ResponseEntity<ApiResult> list() {

        ApiResult result = new ApiResult();

        result.setData(addrDao.userAddrList(userDao.findByNameOrOpenId(this.getCurrentUserName()).getId()));

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

        addrDao.setDefault(userDao.findByNameOrOpenId(this.getCurrentUserName()).getId(), addrid);


        return new ResponseEntity<ApiResult>(result, HttpStatus.OK);
    }



    @RequestMapping(value={"/update_addr"},
            method = RequestMethod.POST,
            headers="Accept=application/json",
            produces = {"application/json"})
    public ResponseEntity<ApiResult> updateAddr(
            @RequestBody JsonNode params) throws Exception {

        int addrid = params.get("addrid").asInt();
        String orderId = params.get("orderId").asText();

        ApiResult result = new ApiResult();

        OrderEntity order = orderDao.get(orderId, userDao.findByNameOrOpenId(this.getCurrentUserName()));
        AddressEntity addr  = addrDao.get(addrid);

        order.setReceiveMan(addr.getLinkMan());
        order.setPhone(addr.getLinkPone());
        order.setAddress(addr.getAddressString());

        orderDao.update(order);

        result.setData(order);

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
