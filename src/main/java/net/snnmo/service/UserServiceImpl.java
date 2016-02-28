package net.snnmo.service;

import net.snnmo.dao.IUserDAO;
import net.snnmo.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    private PasswordEncoder passwordEncoder;
    private IUserDAO userDao;

    public IUserDAO getUserDao() {
        return userDao;
    }

    public void setUserDao(IUserDAO userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println(username);

        UserEntity currentUser = userDao.findByName(username);

        if (currentUser == null)
            throw new OAuth2Exception("username not valid, " + username);

        String[] roles      = currentUser.getRoles().split(",");
        String password     = currentUser.getPassword();

        boolean enabled             = true;
        boolean accountNonExpired   = true;
        boolean credentialsNonExpired   = true;
        boolean accountNonLocked        = true;

        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }

        User user = null;

        try {
            user = new User(username, password, true, true, true, true, authorities);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return user;

    }


}
