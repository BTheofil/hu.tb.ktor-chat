package hu.tb.install

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt

fun Application.installAuth() {
    val configRealm = environment.config.property("jwt.realm").getString()
    val configAudience = environment.config.property("jwt.audience").getString()
    val configIssuer = environment.config.property("jwt.issuer").getString()

    install(Authentication) {
        jwt("auth-jwt") {
            realm = configRealm
            verifier(
                JWT.require(Algorithm.HMAC256(System.getenv("JWT-SECRET")))
                    .withAudience(configAudience)
                    .withIssuer(configIssuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("username").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}