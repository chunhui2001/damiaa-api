package net.snnmo.controller;

import com.fasterxml.jackson.databind.JsonNode;
//import com.sun.org.apache.xpath.internal.operations.String;
import net.snnmo.assist.ApiResult;
import net.snnmo.assist.PartnerType;
import net.snnmo.assist.UserRole;
import net.snnmo.dao.IPartnerDAO;
import net.snnmo.dao.IQrcodeDAO;
import net.snnmo.dao.IUserDAO;
import net.snnmo.entity.PartnerEntity;
import net.snnmo.entity.QrcodeEntity;
import net.snnmo.entity.UserEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.PostConstruct;

/**
 * Created by cc on 16/4/3.
 */
@Controller
@RequestMapping("/partners")
public class PartnerController extends BaseController {

    private IUserDAO userDao;
    private IPartnerDAO partnerDao;
    private IQrcodeDAO qrcodeDao;

    public IQrcodeDAO getQrcodeDao() {
        return qrcodeDao;
    }

    public void setQrcodeDao(IQrcodeDAO qrcodeDao) {
        this.qrcodeDao = qrcodeDao;
    }

    @PostConstruct
    public void init() {
        String userName = this.getCurrentUserName();
        UserEntity user = userDao.findByName(userName);

        if (!userDao.hasAnyRole(user
                , new UserRole[]{ UserRole.ROLE_ADMIN, UserRole.ROLE_SUPERADMIN })) {
            throw new OAuth2Exception("permission deny for manager partners");
        }
    }


    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET, headers="Accept=application/json")
    public ResponseEntity<ApiResult> list() {
        ApiResult sendResult = new ApiResult();

        sendResult.setData(partnerDao.list());

        return new ResponseEntity<ApiResult>(sendResult, HttpStatus.OK);
    }


    @RequestMapping(value = {"/{partnerId}", "/{partnerId}/"}, method = RequestMethod.GET, headers="Accept=application/json")
    public ResponseEntity<ApiResult> get(
            @PathVariable("partnerId") String partnerId
    ) {
        ApiResult sendResult = new ApiResult();

        sendResult.setData(partnerDao.get(partnerId));

        return new ResponseEntity<ApiResult>(sendResult, HttpStatus.OK);
    }


    @RequestMapping(value = {"", "/"}, method = RequestMethod.POST, headers="Accept=application/json")
    public ResponseEntity<ApiResult> save(
            @RequestBody JsonNode params) {

        ApiResult sendResult = new ApiResult();

        String openid               = params.get("openid").asText();
        String unionid              = params.get("unionid").asText();
        String nickname             = params.get("nickname").asText().replaceAll("[^0-9a-zA-Z\u4e00-\u9fa5]+", "");
        PartnerType partnerType     = PartnerType.valueOf(params.get("partnerType").asText());

        PartnerEntity partner = partnerDao.get(unionid);

        if (partner == null) {
            partner = new PartnerEntity();

            partner.setOpenid(openid);
            partner.setUnionid(unionid);
            partner.setNickname(nickname);
            partner.setType(partnerType);

        } else {
            if (partner.getType().compareTo(partnerType) != 0) {
                partner.setType(partnerType);
            } else {
                sendResult.setMessage("partnerType not change");
            }
        }

        partnerDao.saveOrUpdate(partner);

        sendResult.setData(partner);

        return new ResponseEntity<ApiResult>(sendResult, HttpStatus.OK);
    }


    @RequestMapping(value = {"/{partnerid}", "/{partnerid}/"}, method = RequestMethod.PUT, headers="Accept=application/json")
    public ResponseEntity<ApiResult> update(
            @PathVariable("partnerid") String partnerid,
            @RequestBody JsonNode params) {

        ApiResult sendResult = new ApiResult();

        String id                 = params.get("id").asText();
        String partnerName        = params.get("partnerName").asText();
        String phone              = params.get("phone").asText();
        String address            = params.get("address").asText();

        PartnerEntity partner = partnerDao.get(id);

        if (partner == null) {
            sendResult.setMessage("partner not exists!");
            sendResult.setStatus(HttpStatus.BAD_REQUEST);
        } else {
            partner.setPartnerName(partnerName);
            partner.setPhone(phone);
            partner.setAddress(address);

            partnerDao.saveOrUpdate(partner);

            sendResult.setData(partner);
        }


        return new ResponseEntity<ApiResult>(sendResult, sendResult.getStatus());
    }


    @RequestMapping(value = {"/{partnerid}/qrcode", "/{partnerid}/qrcode/"}, method = RequestMethod.PUT, headers="Accept=application/json")
    public ResponseEntity<ApiResult> qrcode(
            @PathVariable("partnerid") String partnerid,
            @RequestBody JsonNode params) {

        ApiResult sendResult = new ApiResult();

        Integer qrcodeid     = params.get("qrcodeid").asInt();
        String action        = params.get("action").asText();

        if (!(action.toLowerCase().equals("set") || action.toLowerCase().equals("remove"))) {
            sendResult.setStatus(HttpStatus.BAD_REQUEST);
            sendResult.setMessage("invalid action");
            return new ResponseEntity<ApiResult>(sendResult, sendResult.getStatus());
        }

        PartnerEntity partner   = partnerDao.get(partnerid);
        QrcodeEntity qrcode     = qrcodeDao.get(qrcodeid);

        if (partner == null) {
            sendResult.setStatus(HttpStatus.BAD_REQUEST);
            sendResult.setMessage("invalid partner id");
            return new ResponseEntity<ApiResult>(sendResult, sendResult.getStatus());
        }

        if (qrcode == null) {
            sendResult.setStatus(HttpStatus.BAD_REQUEST);
            sendResult.setMessage("qrcode not exists");
            return new ResponseEntity<ApiResult>(sendResult, sendResult.getStatus());
        }

        if (action.toLowerCase().equals("set")) {

            if (partner.getQrcode() > 0) {
                sendResult.setStatus(HttpStatus.BAD_REQUEST);
                sendResult.setMessage("partner already have qrcode");
                return new ResponseEntity<ApiResult>(sendResult, sendResult.getStatus());
            }

            if (qrcode.getBelongTo() != null) {
                sendResult.setStatus(HttpStatus.BAD_REQUEST);
                sendResult.setMessage("qrcode inuse");
                return new ResponseEntity<ApiResult>(sendResult, sendResult.getStatus());
            }

            partner.setQrcode(qrcode.getId());
            partnerDao.saveOrUpdate(partner);
            qrcodeDao.set(qrcodeid, "unionid:" + partner.getUnionid());
        }

        if (action.toLowerCase().equals("remove")) {
            partner.setQrcode(0);
            qrcode.setBelongTo(null);

            partnerDao.saveOrUpdate(partner);
            qrcodeDao.set(qrcodeid, null);
        }


        return new ResponseEntity<ApiResult>(sendResult, sendResult.getStatus());
    }





    public IUserDAO getUserDao() {
        return userDao;
    }

    public void setUserDao(IUserDAO userDao) {
        this.userDao = userDao;
    }

    public IPartnerDAO getPartnerDao() {
        return partnerDao;
    }

    public void setPartnerDao(IPartnerDAO partnerDao) {
        this.partnerDao = partnerDao;
    }
}
