package hu.tb.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date
import kotlin.time.Duration.Companion.days

data class GenerateInfo(
    val audience: String,
    val issuer: String
)

private val expireDuration = 5.days

class TokenGeneratorService {
    operator fun invoke(username: String, generateInfo: GenerateInfo): String =
        JWT.create()
            .withAudience(generateInfo.audience)
            .withIssuer(generateInfo.issuer)
            .withClaim("username", username)
            .withExpiresAt(Date(System.currentTimeMillis() + expireDuration.inWholeMilliseconds))
            .sign(Algorithm.HMAC256(System.getenv("JWT-SECRET")))
}