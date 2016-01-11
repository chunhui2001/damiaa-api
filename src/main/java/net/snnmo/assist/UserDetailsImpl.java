package net.snnmo.assist;

import org.springframework.security.core.GrantedAuthority;

import javax.xml.bind.annotation.XmlRootElement;
import java.security.Principal;
import java.util.Collection;
import java.util.HashSet;

/**
 * Created by TTong on 16-1-11.
 */
@XmlRootElement
public class UserDetailsImpl implements org.springframework.security.core.userdetails.UserDetails {
    private static final long serialVersionUID = -6509897037222767090L;

    private Collection<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
    private String password;
    private String username;

    public UserDetailsImpl(String username, String password
            , Collection<GrantedAuthority> authorities){
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    public Collection getAuthorities() {
        return this.authorities;
    }

    public String getPassword() {
        return this.password;
    }

    public String getUsername() {
        return this.username;
    }

    public boolean isAccountNonExpired() {
        return true;
    }

    public boolean isAccountNonLocked() {
        return true;
    }

    public boolean isCredentialsNonExpired() {
        return true;
    }

    public boolean isEnabled() {
        return true;
    }

}
