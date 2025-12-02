package com.example.mycar

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import com.example.mycar.utils.LoginValidator

class LoginValidationTest {

    // ======================================================
    // Email
    // ======================================================

    @Test
    fun `email vacio debe ser invalido`() {
        assertFalse(LoginValidator.isValidEmail(""))
    }

    @Test
    fun `email sin arroba debe ser invalido`() {
        assertFalse(LoginValidator.isValidEmail("testtest.com"))
    }

    @Test
    fun `email con formato valido pasa`() {
        val emails = listOf(
            "user@example.com",
            "test.user@domain.com",
            "user+test@test.co"
        )

        emails.forEach { email ->
            assertTrue(LoginValidator.isValidEmail(email), "Falló con: $email")
        }
    }

    // ======================================================
    // Password
    // ======================================================

    @Test
    fun `password vacio debe ser invalido`() {
        assertFalse(LoginValidator.isValidPassword(""))
    }

    @Test
    fun `password con menos de 6 caracteres debe ser invalido`() {
        assertFalse(LoginValidator.isValidPassword("12345"))
    }

    @Test
    fun `password de 6 o mas caracteres debe ser valido`() {
        assertTrue(LoginValidator.isValidPassword("123456"))
        assertTrue(LoginValidator.isValidPassword("password123"))
    }

    // ======================================================
    // Formulario completo
    // ======================================================

    @Test
    fun `formulario valido`() {
        assertTrue(LoginValidator.isValidLoginForm("test@test.com", "password123"))
    }

    @Test
    fun `formulario invalido por email`() {
        assertFalse(LoginValidator.isValidLoginForm("invalidemail", "password123"))
    }

    @Test
    fun `formulario invalido por password`() {
        assertFalse(LoginValidator.isValidLoginForm("test@test.com", "123"))
    }

    // ======================================================
    // Mensajes de error
    // ======================================================

    @Test
    fun `mensaje de error de email vacio`() {
        val msg = LoginValidator.getEmailError("")
        assertEquals("El correo electrónico es requerido", msg)
    }

    @Test
    fun `mensaje de error de email invalido`() {
        val msg = LoginValidator.getEmailError("invalid")
        assertEquals("El correo electrónico no es válido", msg)
    }

    @Test
    fun `mensaje de error de password vacio`() {
        val msg = LoginValidator.getPasswordError("")
        assertEquals("La contraseña es requerida", msg)
    }

    @Test
    fun `mensaje de error de password corto`() {
        val msg = LoginValidator.getPasswordError("123")
        assertEquals("La contraseña debe tener al menos 6 caracteres", msg)
    }
}
