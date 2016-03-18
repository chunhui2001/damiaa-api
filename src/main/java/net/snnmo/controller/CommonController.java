package net.snnmo.controller;

import net.snnmo.assist.ApiResult;
import net.snnmo.assist.DeliveryCompany;
import net.snnmo.assist.OrderStatus;
import net.snnmo.dao.IOrderDAO;
import net.snnmo.entity.OrderEntity;
import net.snnmo.exception.DbException;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by cc on 16/3/14.
 */
@Controller
public class CommonController extends BaseController {

    public IOrderDAO getOrderDao() {
        return orderDao;
    }

    public void setOrderDao(IOrderDAO orderDao) {
        this.orderDao = orderDao;
    }

    private IOrderDAO orderDao;

    @RequestMapping(value = {"/payment-completed", "/payment-completed/"})
    public ResponseEntity<ApiResult> index(
              @RequestBody Map<String, Object> params
            , HttpServletRequest request) throws DbException {

        String userId       = (String)params.get("userid");
        String openId       = (String)params.get("openid");
        String orderid      = (String)params.get("orderid");
        String paymentInfo  = (String)params.get("paymentInfo");

        ApiResult sendResult    = new ApiResult();

        sendResult.setData(orderDao.paymentCompleted(orderid, userId, openId, paymentInfo));
        //sendResult.setData("userId:" + userId + ", openId:" + openId + ", " + orderid + ", " + paymentInfo);

        return new ResponseEntity<ApiResult>(sendResult, HttpStatus.OK);
    }

    @RequestMapping(value = {"/flush-order", "/flush-order/"})
    public ResponseEntity<ApiResult> flushOrder(
            @RequestBody Map<String, Object> params
            , HttpServletRequest request) throws DbException {

        String userId       = (String)params.get("userid");
        String openId       = (String)params.get("openid");
        String orderid      = (String)params.get("orderid");

        DeliveryCompany deliveryCompany     = DeliveryCompany.valueOf((String)params.get("delivery_company"));
        String deliveryNo                   = (String)params.get("delivery_no");

        ApiResult sendResult    = new ApiResult();

        OrderEntity order = orderDao.get(orderid, userId);

        if (order != null && order.getOpenId().equals(openId)) {
            order.setDeliveryCompany(deliveryCompany);
            order.setDeliveryNo(deliveryNo);
            order.setStatus(OrderStatus.SENDED);

            orderDao.addEvent(OrderStatus.SENDED, order, null);
            orderDao.update(order);
        } else {
            sendResult.setStatus(HttpStatus.BAD_REQUEST);
            sendResult.setMessage("非法请求!");
            sendResult.setData(orderid);
        }



        //sendResult.setData(orderDao.paymentCompleted(orderid, userId, openId, paymentInfo));
        //sendResult.setData("userId:" + userId + ", openId:" + openId + ", " + orderid + ", " + paymentInfo);

        return new ResponseEntity<ApiResult>(sendResult, sendResult.getStatus());
    }
}
