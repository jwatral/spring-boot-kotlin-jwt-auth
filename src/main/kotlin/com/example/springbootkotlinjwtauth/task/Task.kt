package com.example.springbootkotlinjwtauth.task

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Task (
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  var id: Long = 0,
  var description: String = ""
)