package net.snnmo.controller;

import com.fasterxml.jackson.databind.deser.Deserializers;
import net.snnmo.assist.ApiResult;
import net.snnmo.dao.IGoodsDAO;
import net.snnmo.dao.IUserDAO;
import net.snnmo.entity.GoodsEntity;
import net.snnmo.entity.UserEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cc on 16/2/16.
 */

@Controller
@RequestMapping(value = {"/goods"})
public class GoodsController extends BaseController {

    private IUserDAO userDao;
    private IGoodsDAO goodsDao;

    public IUserDAO getUserDao() {
        return userDao;
    }

    public void setUserDao(IUserDAO userDao) {
        this.userDao = userDao;
    }

    public IGoodsDAO getGoodsDao() {
        return goodsDao;
    }

    public void setGoodsDao(IGoodsDAO goodsDao) {
        this.goodsDao = goodsDao;
    }


    @RequestMapping(value={"/", "index"},
            method = {RequestMethod.GET})
    public ResponseEntity<ApiResult> index(HttpServletRequest request) {
        ApiResult result = new ApiResult();
        result.setData(goodsDao.list());
        return new ResponseEntity<ApiResult>(result, HttpStatus.OK);
    }

    @RequestMapping(value={"/{goodsid}", "index"},
            method = {RequestMethod.GET})
    public ResponseEntity<ApiResult> index(@PathVariable("goodsid") String goodsid) {
        ApiResult result    = new ApiResult();
        UserEntity user     = userDao.findByName(this.getCurrentUserName());
        GoodsEntity currentGoods    = goodsDao.get(goodsid);

        Map<String, Object> goodsDetail     = new HashMap<>();

        goodsDetail.put("goods", currentGoods);

        if (user.getRoles().indexOf("ROLE_VIP") != -1) {
            goodsDetail.put("specialPrice", currentGoods.getVipPrice());
        }

        if (user.getRoles().indexOf("ROLE_SUPER_VIP") != -1) {
            goodsDetail.put("specialPrice", currentGoods.getSuperVIPPrice());
        }


        result.setData(goodsDetail);

        return new ResponseEntity<ApiResult>(result, HttpStatus.OK);
    }


    @ModelAttribute
    @RequestMapping(value={"/add", "/add/"},
            method = {RequestMethod.POST},
            headers="Accept=application/json",
            produces = { "application/json" })
    public ResponseEntity<ApiResult> add(
            @Valid @RequestBody GoodsEntity goods, BindingResult bindResult) {
        /*
            curl -v -X POST -H "Accept: application/json"
            -H "Content-Type: application/json"
            -H "Authorization: Bearer 812d866f-de27-478b-90c8-b1e2a5ebef16"
            --data '{"name":"AA精米 2015新米上市 精米现磨", "htmlName": "AA精米 2015新米上市<br />精米现磨", "unit": "袋", "marketPrice": 136.00, "tradePrice": 136.00, "vipPrice": 136.00, "superVIPPrice": 92.00, "place": "黑龙江省绥化市双河镇", "weight":"20kg" }'
            http://127.0.0.1:8088/goods/add
        */

        ApiResult result = new ApiResult();

        UserEntity user = userDao.findByName(this.getCurrentUserName());

        if (user == null || user.getRoles().indexOf("ROLE_ADMIN") == -1) {
            throw new OAuth2Exception("permission deny for manager goods information");
        }

        if (bindResult.hasErrors()) {
            result.setMessage(bindResult.getAllErrors().toString());
            result.setStatus(HttpStatus.BAD_REQUEST);
        } else {
            result.setData(goodsDao.saveOrUpdate(goods));
        }

        //result.setData(goods.getId());

        return new ResponseEntity<ApiResult>(result, HttpStatus.OK);

    }
}
