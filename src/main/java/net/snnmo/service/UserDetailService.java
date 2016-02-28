package net.snnmo.service;

import net.snnmo.dao.IUserDAO;
import net.snnmo.entity.UserEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

import java.util.*;

/**
 * Created by TTong on 16-1-11.
 */
public class UserDetailService implements AuthenticationProvider {

    private IUserDAO userDao;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username     = authentication.getPrincipal() != null ?
                                authentication.getPrincipal().toString() : null;
        String password     = authentication.getCredentials() != null ?
                                authentication.getCredentials().toString() : null;


        try {
            UserEntity userEntity = userDao.findByName(username);

            Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
            boolean isValid = true;

            userEntity = userDao.findByName(username);

//            isValid = userEntity != null
//                        && userEntity.getPasswd().equals(passwordEncoder.encode(password));
            isValid = userEntity != null
                    && userEntity.getPasswd().equals(password);

            if (!isValid) {
                username = null;
                password = null;

                throw new OAuth2Exception("username not valid, " + username);
//                        + "user Passwd:" + userEntity.getPasswd()
//                        + "encode Passwd:" + passwordEncoder.encode(password)
//                        + "Credentials Passwd:" + password);
            } else {
                String[] rolesArrsy = userEntity.getRoles() != null ? userEntity.getRoles().split(",") : null;

                for(String role : rolesArrsy) {
                    authorities.add(new SimpleGrantedAuthority(role));
                }
            }

            final UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(username, password, authorities);

            User userDetails =  new User(username, password, true, true, true, true, authorities);

            token.setDetails(userDetails);
            return token;
        } catch(Exception e) {
            throw new OAuth2Exception(e.getMessage(), e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public IUserDAO getUserDao() {
        return userDao;
    }

    public void setUserDao(IUserDAO userDao) {
        this.userDao = userDao;
    }
}
