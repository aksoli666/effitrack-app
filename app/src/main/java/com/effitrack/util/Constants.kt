package com.effitrack.util

object Constants {
    const val EMPTY_STRING = ""
    const val SPACE = " "
    const val DASH = "-"
    const val DOT = "."
    const val SLASH = " / "
    const val OLD_VALUE = "T"

    const val STUB_INITIALS = "?"

    const val TIME_H_SHORT = "г"
    const val TIME_M_SHORT = "хв"

    const val URL_AUTH_LOGIN = "auth/login"
    const val URL_USERS_PROFILE = "users/{id}"
    const val URL_USERS_EQUIPMENT = "users/{id}/equipment"
    const val URL_EQUIPMENT_SEARCH = "equipment/search"
    const val URL_EQUIPMENT_BY_ID = "equipment/{id}"
    const val URL_EQUIPMENT_STATUS = "equipment/{id}/status"
    const val URL_TASKS_BY_USER = "tasks/user/{userId}"
    const val URL_TASKS_START = "tasks/{taskId}/start"
    const val URL_TASKS_COMPLETE = "tasks/{taskId}/complete"
    const val URL_REPORTS_SEND = "reports/send/{userId}"
    const val URL_TASKS_UPDATE = "tasks/{id}"
    const val URL_REPORTS_EQUIPMENT = "reports/equipment/{userId}"
    const val URL_EQUIPMENT_UPDATE = "equipment/{id}"
    const val URL_EQUIPMENT_AI_ANALYSIS = "equipment/{id}/ai-analysis"

    const val HEADER_AUTHORIZATION = "Authorization"
    const val TOKEN_PREFIX_BEARER = "Bearer "
    const val TOAST_LOGIN_AGAIN = "Сесія закінчилася, увійдіть знову"
    const val NETWORK_ERROR = "З'єднання відсутнє, підключіться до мережі"
    const val SERVER_ERROR = "Сервер тимчасово не працює. Завітайте пізніше"
    const val SERVER_RETRY_BTN = "Оновити"
    const val OPEN_STATUS_SCREEN = "OPEN_STATUS_SCREEN"
    const val ERROR_MESSAGE = "ERROR_MESSAGE"
    const val ENCODE_TYPE = "UTF-8"


    const val PARAM_ID = "id"
    const val PARAM_USER_ID = "userId"
    const val PARAM_TASK_ID = "taskId"
    const val PARAM_INV = "inv"

    const val FIELD_ID = "id"

    const val ROUTE_LOGIN = "login"
    const val ROUTE_DASHBOARD = "dashboard"
    const val ROUTE_PROFILE = "profile"
    const val ROUTE_MAIN = "main"
    const val ROUTE_EQUIPMENT_DETAILS = "equipment_details/{id}"
    const val ROUTE_SCANNER = "scanner"
    const val ROUTE_UNIVERSAL_STATUS = "universal_status"

    const val ERR_EMPTY_FIELDS = "Введіть табельний номер та PIN"
    const val ERR_LOGIN_FAILED = "Помилка входу: перевірте дані"
    const val ERR_ID_NOT_FOUND = "ID не знайдено"
    const val ERR_PREFIX = "Помилка "
    const val ERR_SEND_REPORT = "Помилка відправки звіту"
    const val ERR_NETWORK = "Помилка мережі "

    const val MSG_REPORT_SENT = "Звіт успішно надіслано! ✅"

    const val REASON_START_BY_OPERATOR = "Запуск оператором"

    const val LABEL_STATUS_RUNNING = "В роботі"
    const val LABEL_STATUS_DOWNTIME = "Зупинено"
    const val LABEL_STATUS_SETUP = "Налагодження"

    const val DRIVE_HOST = "drive.google.com"
    const val PATH_ID_PREFIX = "/d/"
    const val PATH_DELIMITER = "/"
    const val DIRECT_LINK_BASE = "https://drive.google.com/uc?export=view&id="

    const val PATTERN_TIME = "HH:mm"
    const val DATE_FORMAT_ISO = "%04d-%02d-%02dT00:00:00"
}