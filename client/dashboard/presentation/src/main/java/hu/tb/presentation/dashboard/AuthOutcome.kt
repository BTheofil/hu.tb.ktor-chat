package hu.tb.presentation.dashboard

/**
 * why the dashboard was opened, so it can confirm what just happened on the auth screen.
 * NONE covers the auto login, where the user did not do anything to confirm.
 */
enum class AuthOutcome {
    WAS_AUTO_LOGIN,
    LOGGED_IN,
    ACCOUNT_CREATED
}
