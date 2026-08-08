package hu.tb.message.domain

/**
 * One page of chat history, oldest message first.
 *
 * [hasMore] is false once the server hands back a short page, which means the start of the
 * conversation has been reached and there is nothing older left to ask for.
 */
data class MessagePage(
    val messages: List<Message>,
    val hasMore: Boolean
)
