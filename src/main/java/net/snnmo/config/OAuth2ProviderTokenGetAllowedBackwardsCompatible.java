package net.snnmo.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;

import java.util.HashSet;

/**
 * Created by TTong on 16-2-26.
 */
@Configuration
public class OAuth2ProviderTokenGetAllowedBackwardsCompatible implements InitializingBean
{
    @Autowired
    private TokenEndpoint tokenEndpoint;

    @Override
    public void afterPropertiesSet() {
        tokenEndpoint.setAllowedRequestMethods(new HashSet<HttpMethod>() {{
            add(HttpMethod.GET);
            add(HttpMethod.POST);
        }});
    }
}