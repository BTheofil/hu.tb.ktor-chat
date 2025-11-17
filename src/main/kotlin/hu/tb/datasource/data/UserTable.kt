package hu.tb.datasource.data

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass

object Users : IntIdTable("users") {
    val name = varchar("name", 128)
    val password = varchar("password", 128)
}

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    var name by Users.name
    var password by Users.password

    override fun toString(): String {
        return "User(id=$id, name=$name, password=$password)"
    }
}