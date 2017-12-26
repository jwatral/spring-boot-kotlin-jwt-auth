package com.example.springbootkotlinjwtauth.security

import com.example.springbootkotlinjwtauth.common.with
import com.example.springbootkotlinjwtauth.security.SecurityConstants.SIGN_UP_URL
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.crypto.password.LdapShaPasswordEncoder




@EnableWebSecurity
//@EnableGlobalMethodSecurity(securedEnabled=true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebSecurity(@Qualifier("userDetailsServiceImpl") val userDetailsService: UserDetailsService,
                  val bCryptPasswordEncoder: BCryptPasswordEncoder)
  : WebSecurityConfigurerAdapter() {

  @Throws(Exception::class)
  override fun configure(http: HttpSecurity) {
//    http
//        .authorizeRequests()
//        .anyRequest().authenticated()
//        .and()
//        .formLogin()
    http.cors().and().csrf().disable().authorizeRequests()
        .antMatchers(HttpMethod.POST, SIGN_UP_URL).permitAll()
        .anyRequest().authenticated()
        .and()
        .addFilter(JWTAuthenticationFilter(authenticationManager()))
        .addFilter(JWTAuthorizationFilter(authenticationManager()))
//         this disables session creation on Spring Security
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
  }

//  @Throws(Exception::class)
//  public override fun configure(auth: AuthenticationManagerBuilder?) {
//    auth!!.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder)
//  }

  @Throws(Exception::class)
  public override fun configure(auth: AuthenticationManagerBuilder?) {
    auth!!
        .ldapAuthentication()
        .userDnPatterns("uid={0},ou=people")
        .groupSearchBase("ou=groups")
        .contextSource()
        .url("ldap://localhost:8389/dc=springframework,dc=org")
        .and()
        .passwordCompare()
        .passwordAttribute("userPassword")
  }

  @Bean
  internal fun corsConfigurationSource(): CorsConfigurationSource =
      UrlBasedCorsConfigurationSource().with {
        registerCorsConfiguration("/**", CorsConfiguration().applyPermitDefaultValues())
      }
}