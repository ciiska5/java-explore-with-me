package ru.practicum.event.enums.states;

/**
 * Список состояний жизненного цикла события после обновления
 */
public enum StateAction {
    PUBLISH_EVENT,
    REJECT_EVENT,
    SEND_TO_REVIEW,
    CANCEL_REVIEW
}
