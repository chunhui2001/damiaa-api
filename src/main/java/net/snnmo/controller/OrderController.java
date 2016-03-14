package net.snnmo.controller;

import net.snnmo.assist.*;
import net.snnmo.dao.IAddrDAO;
import net.snnmo.dao.IGoodsDAO;
import net.snnmo.dao.IOrderDAO;
import net.snnmo.dao.IUserDAO;
import net.snnmo.entity.AddressEntity;
import net.snnmo.entity.GoodsEntity;
import net.snnmo.entity.OrderEntity;
import net.snnmo.entity.UserEntity;
import net.snnmo.exception.DbException;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.cglib.core.CollectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by cc on 16/2/14.
 */

@Controller
@RequestMapping(value = {"/order"})
public class OrderController extends BaseController {

    private IUserDAO userDao;
    private IOrderDAO orderDao;
    private IAddrDAO addrDao;
    private IGoodsDAO goodsDao;

    public IOrderDAO getOrderDao() {
        return orderDao;
    }

    public void setOrderDao(IOrderDAO orderDao) {
        this.orderDao = orderDao;
    }

    public IAddrDAO getAddrDao() {
        return addrDao;
    }

    public void setAddrDao(IAddrDAO addrDao) {
        this.addrDao = addrDao;
    }

    public IGoodsDAO getGoodsDao() {
        return goodsDao;
    }

    public void setGoodsDao(IGoodsDAO goodsDao) {
        this.goodsDao = goodsDao;
    }


    public IUserDAO getUserDao() {
        return userDao;
    }

    public void setUserDao(IUserDAO userDao) {
        this.userDao = userDao;
    }

    @ModelAttribute
    @RequestMapping(value={"/", "index"},
                method = {RequestMethod.POST},
                headers="Accept=application/json",
                produces = { "application/json" })
    public ResponseEntity<ApiResult> index(
            @RequestBody Map<String, String> params, HttpServletRequest request) throws DbException {

//        curl -v -X POST -H "Accept: application/json"
//                -H "Content-Type: application/json"
//                -H "Authorization: Bearer 812d866f-de27-478b-90c8-b1e2a5ebef16"
//                --data '{"payMethod": "2"}' http://127.0.0.1:8088/order/


        ApiResult result = new ApiResult();

        // key : 产品id
        // val : 购买数量
        // payMethod : 支付方式
        // addrid : 收货地址

        UserEntity user = userDao.findByName(this.getCurrentUserName());


        PayMethod payMethod         = null;
        AddressEntity addrEntity    = null;

        String addrid       = null;
        String goodsId      = null;

        Map<GoodsEntity, Integer> goodsList = new HashMap<GoodsEntity, Integer>();


        for (Map.Entry<String, String> param : params.entrySet()) {
            //param.getKey() + "/" + param.getValue()
            if (param.getKey().toLowerCase().equals("paymethod")) {
                if (param.getValue().equals("1")) {
                    payMethod = PayMethod.WCHAT;
                } else if (param.getValue().equals("2")) {
                    payMethod = PayMethod.ARRIVED;
                } else if (param.getValue().equals("3")) {
                    payMethod = PayMethod.ZHIFUBAO;
                } else {
                    throw new DbException("支付方式有误!");
                }
            } else if (param.getKey().toLowerCase().equals("addrid")) {
                // 根据收获地址id去的收货地址
                addrEntity   = addrDao.get(Integer.valueOf(param.getValue()), user.getId());

                if (addrEntity == null)
                    throw new DbException("收货地址有误!");
            } else {
                // 根据商品id取得商品
                GoodsEntity goods   = goodsDao.get(param.getKey());

                if (goods == null) {
                    throw new DbException("提供的商品id有误("+param.getKey()+")!");
                }

                goodsList.put(goods, Integer.valueOf(param.getValue()));
            }
        }

        if (payMethod == null)
            throw new DbException("请提供支付方式(paymethod)!");

        if (addrEntity == null)
            throw new DbException("请提供收获地址(addrid)!");

        if (goodsList.size() == 0)
            throw new DbException("请提供商品列表");

        //result.setData(payMethod);

        OrderEntity order = orderDao.create(user, payMethod, addrEntity, goodsList);

        result.setData(order);

        return new ResponseEntity<ApiResult>(result, HttpStatus.OK);
    }


    @RequestMapping(value={"/", "index"},
            method = {RequestMethod.GET},
            headers="Accept=application/json",
            produces = { "application/json" })
    public ResponseEntity<ApiResult> index(HttpServletRequest request) throws IllegalAccessException {

        ApiResult result = new ApiResult();
        Collection<OrderEntity> orderList   = null;
        Collection<Map<String, Object>> orderListMap = new ArrayList<>();
        UserEntity user = userDao.findByName(this.getCurrentUserName());

        orderList   = orderDao.list(user.getId());

        for (OrderEntity o : orderList) {
            Map<String, Object> currentMap  = new HashMap<>();
            Field[] attributes = o.getClass().getDeclaredFields();

            for (Field f : attributes) {

                try {

                    if (f.getName().toLowerCase().equals("createtime")) {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH点mm分");
                        currentMap.put(f.getName(), format.format(PropertyUtils.getProperty(o, f.getName())));
                    } else
                        currentMap.put(f.getName(), PropertyUtils.getProperty(o, f.getName()));

                    if (f.getName().toLowerCase().equals("status")) {
                        switch (PropertyUtils.getProperty(o, f.getName()).toString().toUpperCase()) {
                            case "PENDING":
                                currentMap.put("statusText", "待付款");
                                break;
                            case "CANCEL":
                                currentMap.put("statusText", "已取消");
                                break;
                            case "RECEIVED":
                                currentMap.put("statusText", "已签收");
                                break;
                            case "CASHED":
                                currentMap.put("statusText", "已付款");
                                break;
                            case "SENDED":
                                currentMap.put("statusText", "已发货");
                                break;
                            default:
                                currentMap.put("statusText", PropertyUtils.getProperty(o, f.getName()));
                        }

                    }



                } catch (NoSuchMethodException e) {

                } catch (InvocationTargetException e) {
                   // e.printStackTrace();
                }

            }

            currentMap.put("listOfItems", orderDao.items(o.getId()));

            orderListMap.add(currentMap);
        }

        result.setData(orderListMap);

        return new ResponseEntity<ApiResult>(result, HttpStatus.OK);
    }


    @RequestMapping(value={"/{orderid}"},
            method = {RequestMethod.PUT, RequestMethod.DELETE},
            headers="Accept=application/json",
            produces = { "application/json" })
    public ResponseEntity<ApiResult> index(
            @PathVariable("orderid") String orderId
            , @RequestBody Map<String, Object> params
            , HttpServletRequest request) {
        ApiResult result = new ApiResult();

        UserEntity user     = userDao.findByName(this.getCurrentUserName());
        OrderEntity order   = orderDao.get(orderId, user);

        if (request.getMethod().toLowerCase().equals("put")) {
            try {

                if (((String)params.get("action")).equals("updateStatus")) {
                    order = orderDao.updateStatus(orderId, user
                            , OrderStatus.valueOf ((String)params.get("status")));
//                    order.setStatus(status);//
//                    orderDao.addEvent(status, order, null);
//                    orderDao.update(order);
                } else if (((String)params.get("action")).equals("updatePrePayId")) {
                    String prePayId     = (String)params.get("prepay_id");
                    order.setPrePayId(prePayId);

                    orderDao.update(order);
                } else {
                    throw new OAuth2Exception(
                            "please provide a param in request body name is: 'action'" +
                                    ", and value in ['updateStatus', 'updatePrePayId']" );
                }

            } catch (Exception e) {
                result.setStatus(HttpStatus.BAD_REQUEST);
                result.setMessage(e.getMessage());
            }
        }

        if (request.getMethod().toLowerCase().equals("delete")) {
            order.setStatus(OrderStatus.DELETED);

            orderDao.addEvent(OrderStatus.DELETED, order, null);
            orderDao.update(order);
        }

        result.setData(order);

        return new ResponseEntity<ApiResult>(result, result.getStatus());
    }


    @RequestMapping(value={"/{orderid}"},
            method = {RequestMethod.GET},
            headers="Accept=application/json",
            produces = { "application/json" })
    public ResponseEntity<ApiResult> index(@PathVariable("orderid") String orderId) {
        ApiResult result = new ApiResult();

        UserEntity user = userDao.findByName(this.getCurrentUserName());

        Map<String, Object> orderDetail = new HashMap<>();

        orderDetail.put("order", orderDao.get(orderId, user));
        orderDetail.put("orderItems", orderDao.items(orderId));

        result.setData(orderDetail);

        return new ResponseEntity<ApiResult>(result, HttpStatus.OK);
    }


    // 验证订单是否存在
    @RequestMapping(value={"/{openid}/{prepayid}/"},
            method = {RequestMethod.GET},
            headers="Accept=application/json",
            produces = { "application/json" })
    public ResponseEntity<ApiResult> index(
                      @PathVariable("prepayid") String preparid
                    , @PathVariable("openid") String openid) {
        ApiResult result = new ApiResult();

        UserEntity user = userDao.findByName(this.getCurrentUserName());

        boolean isExists    = orderDao.exists(user.getId(), openid, preparid);

        result.setData(isExists);
        result.serError(!isExists);

        return new ResponseEntity<ApiResult>(result, isExists ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
}
