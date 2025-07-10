package com.example.domain.error

class UserAlreadyExistsException(email: String) : Exception("User with email '$email' already exists")