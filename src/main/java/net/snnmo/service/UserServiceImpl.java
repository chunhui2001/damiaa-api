package net.snnmo.service;

import net.snnmo.dao.IUserDAO;
import net.snnmo.entity.UserEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by cc on 16/2/28.
 */
public class UserServiceImpl implements UserDetailsService {

    private IUserDAO userDao;

    public IUserDAO getUserDao() {
        return userDao;
    }

    public void setUserDao(IUserDAO userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
System.out.println(username + " loadUserByUsername");
        UserEntity currentUser = userDao.findByName(username);

        if (currentUser == null)
            throw new OAuth2Exception("username not valid, " + username);

        String[] roles      = currentUser.getRoles().split(",");
        String password     = currentUser.getPasswd();

        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }

        User user = null;

        try {
            user = new User(username, password, true, true, true, true, authorities);

            final Authentication auth = new UsernamePasswordAuthenticationToken(username, password, authorities);

            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return user;

    }


}
