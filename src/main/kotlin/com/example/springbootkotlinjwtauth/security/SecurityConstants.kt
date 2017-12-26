package com.example.springbootkotlinjwtauth.security

object SecurityConstants {
  val SECRET = "SecretKeyToGenJWTs"
  val EXPIRATION_TIME: Long = 864000000 // 10 days
  val TOKEN_PREFIX = "Bearer "
  val HEADER_STRING = "Authorization"
  val SIGN_UP_URL = "/users/sign-up"
  val ROLES_CLAIM = "roles"
}