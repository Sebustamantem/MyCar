package com.example.mycar.utils

/**
 * Validador para el formulario de Login.
 * Contiene funciones para validar email, contraseña
 * y obtener mensajes de error asociados.
 */
object LoginValidator {

    // Regex sencilla pero completa que permite:
    // - letras, números, . _ % + -
    // - dominios con subdominios
    // - TLD de 2 o más letras
    private val EMAIL_REGEX =
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

    /**
     * Valida el email.
     *
     * Reglas:
     * - No debe estar vacío
     * - Debe cumplir el patrón EMAIL_REGEX
     */
    fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return false
        return EMAIL_REGEX.matches(email.trim())
    }

    /**
     * Valida la contraseña.
     *
     * Reglas:
     * - Mínimo 6 caracteres (ignorando espacios al inicio y final)
     */
    fun isValidPassword(password: String): Boolean {
        return password.trim().length >= 6
    }

    /**
     * Valida el formulario completo de login.
     *
     * True solo si email y password son válidos.
     */
    fun isValidLoginForm(email: String, password: String): Boolean {
        return isValidEmail(email) && isValidPassword(password)
    }

    /**
     * Devuelve el mensaje de error asociado al email,
     * o null si es válido.
     *
     * Posibles mensajes:
     * - "El correo electrónico es requerido"
     * - "El correo electrónico no es válido"
     */
    fun getEmailError(email: String): String? {
        return when {
            email.isBlank() ->
                "El correo electrónico es requerido"
            !isValidEmail(email) ->
                "El correo electrónico no es válido"
            else -> null
        }
    }

    /**
     * Devuelve el mensaje de error asociado al password,
     * o null si es válido.
     *
     * Posibles mensajes:
     * - "La contraseña es requerida"
     * - "La contraseña debe tener al menos 6 caracteres"
     */
    fun getPasswordError(password: String): String? {
        val trimmed = password.trim()
        return when {
            trimmed.isEmpty() ->
                "La contraseña es requerida"
            trimmed.length < 6 ->
                "La contraseña debe tener al menos 6 caracteres"
            else -> null
        }
    }
}
