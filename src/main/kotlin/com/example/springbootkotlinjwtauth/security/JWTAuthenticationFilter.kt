package com.example.springbootkotlinjwtauth.security

import com.example.springbootkotlinjwtauth.security.SecurityConstants.EXPIRATION_TIME
import com.example.springbootkotlinjwtauth.security.SecurityConstants.HEADER_STRING
import com.example.springbootkotlinjwtauth.security.SecurityConstants.ROLES_CLAIM
import com.example.springbootkotlinjwtauth.security.SecurityConstants.SECRET
import com.example.springbootkotlinjwtauth.security.SecurityConstants.TOKEN_PREFIX
import com.example.springbootkotlinjwtauth.user.ApplicationUser
import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.User
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.io.IOException
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class JWTAuthenticationFilter(val manager: AuthenticationManager) : UsernamePasswordAuthenticationFilter() {

  override fun getAuthenticationManager() = manager

  @Throws(AuthenticationException::class)
  override fun attemptAuthentication(req: HttpServletRequest,
                                     res: HttpServletResponse?): Authentication {
    val (_, username, password) = ObjectMapper()
        .readValue(req.inputStream, ApplicationUser::class.java)

    return manager.authenticate(
        UsernamePasswordAuthenticationToken(
            username,
            password)
    )

  }


  @Throws(IOException::class, ServletException::class)
  override fun successfulAuthentication(req: HttpServletRequest,
                                        res: HttpServletResponse,
                                        chain: FilterChain?,
                                        auth: Authentication) {

    val ldapAuth = auth.principal as LdapUserDetailsImpl
    val token = Jwts.builder()
        .setSubject(ldapAuth.username)
        .claim(ROLES_CLAIM, AuthorityUtils.authorityListToSet(ldapAuth.authorities).joinToString())
        .setExpiration(Date(System.currentTimeMillis() + EXPIRATION_TIME))
        .signWith(SignatureAlgorithm.HS512, SECRET.toByteArray())
        .compact()
    res.addHeader(HEADER_STRING, TOKEN_PREFIX + token)
  }
}