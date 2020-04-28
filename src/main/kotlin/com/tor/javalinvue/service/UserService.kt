package com.tor.javalinvue.service

import java.util.concurrent.atomic.AtomicInteger

data class User(val id: Int, val name: String, val email: String, val userDetails: UserDetails?)
data class UserDetails(val dateOfBirth: String, val salary: String)

val users = hashMapOf<Int, User>(
    1 to User(id = 1, name = "John", email = "john@fake.co", userDetails = UserDetails("21.02.1964", "2773 JB")),
    2 to User(id = 2, name = "Mary", email = "mary@fake.co", userDetails = UserDetails("12.05.1994", "1222 JB")),
    3 to User(id = 3, name = "Dave", email = "dave@fake.co", userDetails = UserDetails("01.05.1984", "1833 JB")),
    4 to User(id = 4, name = "Jane", email = "jane@fake.co", userDetails = UserDetails("30.12.1989", "1532 JB")),
    5 to User(id = 5, name = "Eric", email = "eric@fake.co", userDetails = UserDetails("14.09.1973", "2131 JB")),
    6 to User(id = 6, name = "Gina", email = "gina@fake.co", userDetails = UserDetails("16.08.1977", "1982 JB")),
    7 to User(id = 7, name = "Ryan", email = "ryan@fake.co", userDetails = UserDetails("07.11.1988", "1638 JB")),
    8 to User(id = 8, name = "Judy", email = "judy@fake.co", userDetails = UserDetails("05.01.1959", "2983 JB"))
)

object UserService {
    private var lastId: AtomicInteger = AtomicInteger(users.size - 1)

    fun getAll() = users.values
    fun save(name: String, email: String, userDetails: UserDetails?) {
        val id = lastId.incrementAndGet()
        users[id] = User(id, name, email, userDetails)
    }

    fun findById(id: Int): User? = users[id]
    fun update(id: Int, name: String, email: String, userDetails: UserDetails?) {
        users[id] = User(id, name, email, userDetails)
    }

    fun delete(id: Int) {
        users.remove(id)
    }
}