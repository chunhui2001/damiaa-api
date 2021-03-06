package net.snnmo.controller;

import net.snnmo.assist.ApiResult;
import net.snnmo.assist.Common;
import net.snnmo.assist.DeliveryCompany;
import net.snnmo.assist.OrderStatus;
import net.snnmo.dao.IOrderDAO;
import net.snnmo.dao.IQrcodeDAO;
import net.snnmo.entity.OrderEntity;
import net.snnmo.entity.OrderEventEntity;
import net.snnmo.entity.QrcodeEntity;
import net.snnmo.entity.UserEntity;
import net.snnmo.exception.DbException;

import net.snnmo.assist.Common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by cc on 16/3/14.
 */
@Controller
public class CommonController extends BaseController {

    @Value("${customer.token}")
    private String myToken;

    @Value("http://${WCHAT_SERVER_HOSTNAME}/unifiedorder")
    private String wchatServerHost;


    public IOrderDAO getOrderDao() {
        return orderDao;
    }

    public void setOrderDao(IOrderDAO orderDao) {
        this.orderDao = orderDao;
    }

    private IOrderDAO orderDao;

    public IQrcodeDAO getQrcodeDao() {
        return qrcodeDao;
    }

    public void setQrcodeDao(IQrcodeDAO qrcodeDao) {
        this.qrcodeDao = qrcodeDao;
    }

    private IQrcodeDAO qrcodeDao;

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







    @RequestMapping(value = {"/create-order-auto/", "/create-order-auto"}, method = RequestMethod.POST)
    public ResponseEntity<ApiResult> createOrder(
            @RequestBody Map<String, String> params
    ) throws Exception {


//        curl -v -X POST -H "Accept: application/json"
//                -H "Content-Type: application/json"
//                -H "Authorization: Bearer 812d866f-de27-478b-90c8-b1e2a5ebef16"
//                --data '{"payMethod": "2"}' http://127.0.0.1:8088/order/


        ApiResult result = new ApiResult();

        // key : 产品id
        // val : 购买数量
        // payMethod : 支付方式
        // openid : openid
        // ticket : 二维码票据

        OrderEntity order = orderDao.create(null, params);

        result.setData(order);

        return new ResponseEntity<ApiResult>(result, HttpStatus.OK);
    }



    @RequestMapping(value = {"/flush-order", "/flush-order/"})
    public ResponseEntity<ApiResult> flushOrder(
            @RequestBody Map<String, Object> params
            , HttpServletRequest request) throws DbException, IllegalAccessException {

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

            orderDao.addEvent(OrderStatus.SENDED, order, null, null);
            orderDao.update(order);

            sendResult.setData(Common.processOrder(order, orderDao));
        } else {
            sendResult.setStatus(HttpStatus.BAD_REQUEST);
            sendResult.setMessage("非法请求!");
            sendResult.setData(orderid);
        }



        //sendResult.setData(orderDao.paymentCompleted(orderid, userId, openId, paymentInfo));
        //sendResult.setData("userId:" + userId + ", openId:" + openId + ", " + orderid + ", " + paymentInfo);

        return new ResponseEntity<ApiResult>(sendResult, sendResult.getStatus());
    }

    @RequestMapping(value = {"/orderlist/{status}", "/orderlist/{status}/"}, method = {RequestMethod.POST})
    public ResponseEntity<ApiResult> flushOrder(
            @PathVariable("status") OrderStatus status,
            @RequestBody Map<String, String> params,
            HttpServletRequest request) throws DbException {

        ApiResult sendResult    = new ApiResult();
        String token            = (String)params.get("token");

        if (!myToken.equals(token)) {
            sendResult.setStatus(HttpStatus.BAD_REQUEST);
            sendResult.setMessage("非法请求!");
        } else
            sendResult.setData(orderDao.get(status));

        return new ResponseEntity<ApiResult>(sendResult, sendResult.getStatus());
    }



    @RequestMapping(
            value = {"/push-events/{deliveryStatus}"
            , "/push-events/{deliveryStatus}"}
            , method = {RequestMethod.POST})
    public ResponseEntity<ApiResult> pushEvents(
            @PathVariable("deliveryStatus") int deliveryStatus,
            @RequestBody Map<String, String> deliveryContext,
            HttpServletRequest request) throws DbException, ParseException {

//        {
//            "userid": "ff808081522fe8b901522fe921600000",
//            "openid": "ofnVVw9aVxkxSfvvW373yuMYT7fs",
//            "orderid": "145609791231388228",
//            "token":"BudbXmq1bgnyJWXL",
//            "2016-03-04 06:30:00":"珠海:到达",
//            "2016-03-04 17:00:09":"中国邮政集团公司珠海市金邦达邮局:已经收寄",
//            "2016-03-04 17:17:00":"中国邮政集团公司珠海市金邦达邮局:离开",
//        }

        ApiResult sendResult    = new ApiResult();
        String token            = (String)deliveryContext.get("token");
        String orderid          = (String)deliveryContext.get("orderid");
        String userid           = (String)deliveryContext.get("userid");
        String openid           = (String)deliveryContext.get("openid");

        if (!myToken.equals(token)) {
            throw new OAuth2Exception("非法请求!");
        } else {
            OrderEntity order   = orderDao.get(orderid, userid);

            if (order == null) {
                throw new OAuth2Exception("订单不存在!");
            }

            if (!order.getOpenId().equals(openid)) {
                throw new OAuth2Exception("非法请求2!");
            }

            Collection<OrderEventEntity> eventList  = new ArrayList<>();
            SortedSet<String> deliveryContextKeys   = new TreeSet<>(deliveryContext.keySet());

            for (String key : deliveryContextKeys) {
                if (key.equals("token")
                        || key.equals("orderid")
                        || key.equals("userid")
                        || key.equals("openid")) {
                    continue;
                }

                DateFormat df           = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                DateFormat df2          = new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA);
                String value            = deliveryContext.get(key);
                OrderEventEntity event  = new OrderEventEntity();

                event.setEventTime(df.parse(key));
                event.setOrder(order);
                event.setType(OrderStatus.DELIVERY);
                event.setMessage("[" + df2.format(event.getEventTime()) + "] " + value);

                eventList.add(event);
            }

            if (eventList.size() == 0) {
                sendResult.setMessage("未增加任何数据！");
            } else {

                OrderEntity newOrder    = orderDao.addEvents(order, eventList);

                if (newOrder != null || deliveryStatus == 6) {

                    // 当快递接口返回的状态是签收(6)时，　更新订单的状态为已签收
                    if (deliveryStatus == 6)
                        order.setStatus(OrderStatus.SIGNED);

                    orderDao.update(newOrder);
                    sendResult.setData(newOrder);

                }

                if (newOrder == null) {
                    sendResult.setMessage("未增加任何数据2！");
                }

            }

        }

        return new ResponseEntity<ApiResult>(sendResult, sendResult.getStatus());
    }





    @RequestMapping(value = {"/get-qrcode/{openid}/", "/get-qrcode/{openid}"}, method = RequestMethod.GET)
    public ResponseEntity<ApiResult> get(
            @PathVariable("openid") String openid
    ) {

        ApiResult sendResult = new ApiResult();

        QrcodeEntity qrcode     = qrcodeDao.get(openid);

        if (qrcode != null)
            sendResult.setData(qrcode);
        else {
            sendResult.setStatus(HttpStatus.BAD_REQUEST);
            sendResult.setMessage("invalid openid");
        }


        return new ResponseEntity<ApiResult>(sendResult, sendResult.getStatus());
    }



    @RequestMapping(value = {"/unified-order/", "/unified-order"}, method = RequestMethod.POST)
    public ResponseEntity<ApiResult> unifiedOrder(
            @RequestBody Map<String, String> params
    ) throws Exception {

        String orderid  = params.get("orderid");
        String openid   = params.get("openid");

        OrderEntity order = orderDao.get(orderid, openid);

        ApiResult sendResult = new ApiResult();

        if (order == null) {
            sendResult.setStatus(HttpStatus.BAD_REQUEST);
            sendResult.setMessage("Invalid Params");
            return new ResponseEntity<ApiResult>(sendResult, sendResult.getStatus());
        }


        sendResult.setData(Common.UnifiedOrder(wchatServerHost, order));

        return new ResponseEntity<ApiResult>(sendResult, sendResult.getStatus());

    }
}
