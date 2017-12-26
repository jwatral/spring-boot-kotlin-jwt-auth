package com.example.springbootkotlinjwtauth.common

infix inline fun <T> T.with(initWith: T.() -> Unit): T {
  this.initWith()
  return this
}