package com.example.springbootkotlinjwtauth.security

import com.example.springbootkotlinjwtauth.security.SecurityConstants.HEADER_STRING
import com.example.springbootkotlinjwtauth.security.SecurityConstants.ROLES_CLAIM
import com.example.springbootkotlinjwtauth.security.SecurityConstants.SECRET
import com.example.springbootkotlinjwtauth.security.SecurityConstants.TOKEN_PREFIX
import io.jsonwebtoken.Jwts
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.io.IOException
import java.util.ArrayList

class JWTAuthorizationFilter(authManager: AuthenticationManager) : BasicAuthenticationFilter(authManager) {

  @Throws(IOException::class, ServletException::class)
  override fun doFilterInternal(req: HttpServletRequest,
                                res: HttpServletResponse,
                                chain: FilterChain) {
    val header: String? = req.getHeader(HEADER_STRING)

    if (header?.startsWith(TOKEN_PREFIX) == true) {
      val authentication = getAuthentication(req)
      SecurityContextHolder.getContext().authentication = authentication
    }

    chain.doFilter(req, res)
  }

  private fun getAuthentication(request: HttpServletRequest): UsernamePasswordAuthenticationToken? {
    val token = request.getHeader(HEADER_STRING)
    return getUserFromToken(token).let {
      UsernamePasswordAuthenticationToken(it, "", AuthorityUtils.commaSeparatedStringToAuthorityList(getAuthoritiesFromToken(token)))
    }
  }

  private fun getUserFromToken(token: String?): String?
      = token?.let {
    Jwts.parser()
        .setSigningKey(SECRET.toByteArray())
        .parseClaimsJws(it.replace(TOKEN_PREFIX, ""))
        .body
        .subject
  }

  private fun getAuthoritiesFromToken(token: String?): String?
      = token?.let {
    Jwts.parser()
        .setSigningKey(SECRET.toByteArray())
        .parseClaimsJws(it.replace(TOKEN_PREFIX, ""))
        .body
        .get(ROLES_CLAIM, String::class.java)
  }

}