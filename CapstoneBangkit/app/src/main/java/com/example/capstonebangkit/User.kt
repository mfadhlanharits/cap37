package com.example.capstonebangkit

class User {
    var fullName: String? = null
    var email: String? = null

    constructor() {}
    constructor(fullName: String?, email: String?) {
        this.fullName = fullName
        this.email = email
    }
}