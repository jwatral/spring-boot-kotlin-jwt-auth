package com.example.springbootkotlinjwtauth.user

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(private val applicationUserRepository: ApplicationUserRepository,
                     private val bCryptPasswordEncoder: BCryptPasswordEncoder) {

  @PostMapping("/sign-up")
  fun signUp(@RequestBody user: ApplicationUser): ApplicationUser? {
    user.password = bCryptPasswordEncoder.encode(user.password)
    return applicationUserRepository.save(user)
  }
}