package net.snnmo.controller;

import net.snnmo.assist.ApiResult;
import net.snnmo.dao.IUploadDAO;
import net.snnmo.dao.IUserDAO;
import net.snnmo.entity.UserUploadEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Created by TTong on 16-3-11.
 */

@Controller
@RequestMapping(value = {"/upload"})
public class UploadController extends BaseController {

    @Value("${customer.token}")
    private String _CUSTOMER_TOKEN;

    private IUploadDAO uploadDao;
    private IUserDAO userDao;

    public IUserDAO getUserDao() {
        return userDao;
    }

    public void setUserDao(IUserDAO userDao) {
        this.userDao = userDao;
    }

    @PostConstruct
    public void init() {

    }

    @RequestMapping(value={"/", "", }, method = RequestMethod.GET)
    public void index(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.getWriter().write("Upload controller");
    }


    @RequestMapping(value={"/", "", }, method = RequestMethod.POST)
    public ResponseEntity<ApiResult> index(
            @RequestBody Map<String, Object> params, HttpServletRequest request) throws IOException {

        String uri          = (String)params.get("uri");
        //byte[] content      = (byte[])params.get("content");
        String uploadType   = (String)params.get("uploadType");
        String unionid      = (String)params.get("unionid");
        String openid       = (String)params.get("openid");
        String customerToken       = (String)params.get("customerToken");

        if (!customerToken.equals(_CUSTOMER_TOKEN)) {
            throw new OAuth2Exception("permission deny for upload operation!");
        }


        ApiResult result = new ApiResult();

        UserUploadEntity upload = new UserUploadEntity();
        upload.setOpenid(openid);
        upload.setUnionid(unionid);
        upload.setUploadType(uploadType);
        upload.setUri(uri);

        uploadDao.add(upload);

        if(!userDao.updatePhoto(openid, unionid, uri)) {
            result.setStatus(HttpStatus.BAD_REQUEST);
            result.setData("更新头像失败!");
        } else {
            result.setData("更新头像成功.");
        }

        return new ResponseEntity<ApiResult>(result, result.getStatus());
    }

    public IUploadDAO getUploadDao() {
        return uploadDao;
    }

    public void setUploadDao(IUploadDAO uploadDao) {
        this.uploadDao = uploadDao;
    }
}