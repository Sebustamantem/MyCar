package com.example.mycar

import com.example.mycar.screen.validarCampos
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class RegisterValidationTest {

    @Test
    fun `campos validos deben devolver true y mensaje vacio`() {
        val result = validarCampos(
            nombre = "Juan",
            apellido = "Pérez",
            correo = "juan.perez@example.com",
            telefono = "+56912345678",
            contrasena = "password123",
            confirmar = "password123"
        )

        assertTrue(result.first)
        assertEquals("", result.second)
    }

    @Test
    fun `nombre vacio devuelve error`() {
        val result = validarCampos(
            nombre = "",
            apellido = "Pérez",
            correo = "juan.perez@example.com",
            telefono = "+56912345678",
            contrasena = "password123",
            confirmar = "password123"
        )

        assertFalse(result.first)
        assertEquals("Ingresa tu nombre.", result.second)
    }

    @Test
    fun `apellido vacio devuelve error`() {
        val result = validarCampos(
            nombre = "Juan",
            apellido = "",
            correo = "juan.perez@example.com",
            telefono = "+56912345678",
            contrasena = "password123",
            confirmar = "password123"
        )

        assertFalse(result.first)
        assertEquals("Ingresa tu apellido.", result.second)
    }

    @Test
    fun `correo invalido devuelve error`() {
        val result = validarCampos(
            nombre = "Juan",
            apellido = "Pérez",
            correo = "correo-invalido",
            telefono = "+56912345678",
            contrasena = "password123",
            confirmar = "password123"
        )

        assertFalse(result.first)
        assertEquals("Correo inválido.", result.second)
    }

    @Test
    fun `telefono invalido devuelve error`() {
        val result = validarCampos(
            nombre = "Juan",
            apellido = "Pérez",
            correo = "juan.perez@example.com",
            telefono = "912345678",
            contrasena = "password123",
            confirmar = "password123"
        )

        assertFalse(result.first)
        assertEquals("Teléfono debe ser +569XXXXXXXX.", result.second)
    }

    @Test
    fun `contrasena corta devuelve error`() {
        val result = validarCampos(
            nombre = "Juan",
            apellido = "Pérez",
            correo = "juan.perez@example.com",
            telefono = "+56912345678",
            contrasena = "12345",
            confirmar = "12345"
        )

        assertFalse(result.first)
        assertEquals("La contraseña debe tener al menos 6 caracteres.", result.second)
    }

    @Test
    fun `contrasenas distintas devuelven error`() {
        val result = validarCampos(
            nombre = "Juan",
            apellido = "Pérez",
            correo = "juan.perez@example.com",
            telefono = "+56912345678",
            contrasena = "password123",
            confirmar = "otraClave"
        )

        assertFalse(result.first)
        assertEquals("Las contraseñas no coinciden.", result.second)
    }
}
