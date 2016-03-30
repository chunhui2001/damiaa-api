package net.snnmo.controller;

import net.snnmo.assist.ApiResult;
import net.snnmo.assist.Common;
import net.snnmo.assist.OrderStatus;
import net.snnmo.assist.UserRole;
import net.snnmo.dao.IOrderDAO;
import net.snnmo.dao.IUserDAO;
import net.snnmo.entity.OrderEntity;
import net.snnmo.entity.UserEntity;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by TTong on 16-3-30.
 */
@Controller
@RequestMapping(value = {"/console"})
public class ConsoleController extends BaseController {

    private IUserDAO userDao;
    private IOrderDAO orderDao;

    @RequestMapping(value={"/user-orders/{orderStatus}", "/user-orders/{orderStatus}/"},
            method = {RequestMethod.GET},
            headers="Accept=application/json",
            produces = { "application/json" })
    public ResponseEntity<ApiResult> userOrders(
            @PathVariable("orderStatus") String orderStatus)  throws IllegalAccessException{

        ApiResult sendResult = new ApiResult();

        String[] statusStrArr   = orderStatus.split(",");
        OrderStatus[] statusArr = new OrderStatus[statusStrArr.length];

        for (int i = 0; i < statusArr.length; i++) {
            statusArr[i]    = OrderStatus.valueOf(statusStrArr[i]);
        }

        UserEntity user = userDao.findByName(this.getCurrentUserName());

        if (!userDao.hasAnyRole(user, new UserRole[]{ UserRole.ROLE_ADMIN, UserRole.ROLE_SUPERADMIN })) {
            throw new OAuth2Exception("permission deny for list user orders!");
        }


        Collection<OrderEntity> orderList   = orderDao.list(statusArr);
        Collection<Map<String, Object>> orderListMap = Common.processOrderList(orderList, orderDao);


        sendResult.setData(orderListMap);

        return new ResponseEntity<ApiResult>(sendResult, HttpStatus.OK);
    }



    public IUserDAO getUserDao() {
        return userDao;
    }

    public void setUserDao(IUserDAO userDao) {
        this.userDao = userDao;
    }

    public IOrderDAO getOrderDao() {
        return orderDao;
    }

    public void setOrderDao(IOrderDAO orderDao) {
        this.orderDao = orderDao;
    }
}
