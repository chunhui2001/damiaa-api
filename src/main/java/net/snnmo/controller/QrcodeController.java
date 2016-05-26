package net.snnmo.controller;


import net.snnmo.assist.ApiResult;
import net.snnmo.assist.UserRole;
import net.snnmo.dao.IQrcodeDAO;
import net.snnmo.dao.IUserDAO;
import net.snnmo.entity.QrcodeEntity;
import net.snnmo.entity.UserEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.PostConstruct;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by cc on 16/4/6.
 */
@Controller
@RequestMapping("/qrcode")
public class QrcodeController extends BaseController {

    private IUserDAO userDao;
    private IQrcodeDAO qrcodeDao;

    public IQrcodeDAO getQrcodeDao() {
        return qrcodeDao;
    }

    public void setQrcodeDao(IQrcodeDAO qrcodeDao) {
        this.qrcodeDao = qrcodeDao;
    }

    public IUserDAO getUserDao() {
        return userDao;
    }

    public void setUserDao(IUserDAO userDao) {
        this.userDao = userDao;
    }

    @PostConstruct
    public void init() {
        String userName = this.getCurrentUserName();
        UserEntity user = userDao.findByName(userName);

        if (!userDao.hasAnyRole(user
                , new UserRole[]{ UserRole.ROLE_ADMIN, UserRole.ROLE_SUPERADMIN })) {
            throw new OAuth2Exception("permission deny for manager qrcodes");
        }
    }


    @RequestMapping(value = {"/", ""}, method = RequestMethod.GET)
    public ResponseEntity<ApiResult> list() {

        ApiResult sendResult = new ApiResult();

        sendResult.setData(qrcodeDao.random(1));

        return new ResponseEntity<ApiResult>(sendResult, HttpStatus.OK);
    }


    @ModelAttribute
    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST,
            headers="Accept=application/json",
            produces = {"application/json"})
    public ResponseEntity<ApiResult> save(@RequestBody Collection<QrcodeEntity> qrcodeList) {

//        [
//            {
//                "no": 1,
//                    "ticket": "gQHk7zoAAAAAAAAAASxodHRwOi8vd2VpeGluLnFxLmNvbS9xL2xqZ2pCV0hseGRLOHRvb0dmUlM2AAIE7UfyVgMEAAAAAA==",
//                    "qrcodeURI": "http://weixin.qq.com/q/ljgjBWHlxdK8tooGfRS6"
//            }
//        ]

        ApiResult sendResult = new ApiResult();

        sendResult.setData(qrcodeDao.save(qrcodeList));

        return new ResponseEntity<ApiResult>(sendResult, HttpStatus.OK);
    }
}
