package de.perdian.apps.flighttracker.web.security.impl;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@ConditionalOnExpression("#{environment['flighttracker.authentication.type'] eq null}")
class DummySecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // Make sure we can access every resource without login
        http.authorizeRequests().anyRequest().permitAll();

    }

}
