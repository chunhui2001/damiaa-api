package net.snnmo.assist;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Created by cc on 16/2/28.
 */
public class LegacyEncoder implements PasswordEncoder {

    private static final String BCRYP_TYPE = "$";
    private static final PasswordEncoder BCRYPT = new BCryptPasswordEncoder();

    @Override
    public String encode(CharSequence rawPassword) {

        return BCRYPT.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {

        if (encodedPassword.startsWith(BCRYP_TYPE)) {
            return BCRYPT.matches(rawPassword, encodedPassword);
        }

        return false;
    }

}