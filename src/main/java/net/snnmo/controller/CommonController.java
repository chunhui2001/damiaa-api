package net.snnmo.controller;

import net.snnmo.assist.ApiResult;
import net.snnmo.dao.IOrderDAO;
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
            , HttpServletRequest request) {

        String userId       = (String)params.get("userid");
        String openId       = (String)params.get("openid");
        String orderid      = (String)params.get("orderid");
        String paymentInfo  = (String)params.get("paymentInfo");

        ApiResult sendResult    = new ApiResult();

        sendResult.setData(orderDao.paymentCompleted(orderid, userId, openId, paymentInfo));
        //sendResult.setData("userId:" + userId + ", openId:" + openId + ", " + orderid + ", " + paymentInfo);

        return new ResponseEntity<ApiResult>(sendResult, HttpStatus.OK);
    }
}
