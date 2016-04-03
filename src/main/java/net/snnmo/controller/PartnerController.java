package net.snnmo.controller;

import com.fasterxml.jackson.databind.JsonNode;
import net.snnmo.assist.ApiResult;
import net.snnmo.assist.PartnerType;
import net.snnmo.assist.UserRole;
import net.snnmo.dao.IPartnerDAO;
import net.snnmo.dao.IUserDAO;
import net.snnmo.entity.PartnerEntity;
import net.snnmo.entity.UserEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.stereotype.Controller;
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


    @RequestMapping(value = {"", "/"}, method = RequestMethod.POST, headers="Accept=application/json")
    public ResponseEntity<ApiResult> save(
            @RequestBody JsonNode params) {

        ApiResult sendResult = new ApiResult();

        String openid               = params.get("openid").asText();
        String unionid              = params.get("unionid").asText();
        String nickname             = params.get("nickname").asText().replaceAll("[^0-9a-zA-Z\u4e00-\u9fa5]+", "");
        PartnerType partnerType     = PartnerType.valueOf(params.get("partnerType").asText());

System.out.println(nickname);
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
