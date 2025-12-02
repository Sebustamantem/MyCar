package com.example.mycar

import android.app.Application
import com.example.mycar.models.AlertRecord
import com.example.mycar.models.MaintenanceRecord
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class UserViewModelTest {

    // Creamos un Application simple solo para el constructor
    private fun createViewModel(): UserViewModel {
        val app = Application()   // no usa nada de Android real, solo la clase
        return UserViewModel(app)
    }

    @Test
    fun `logout limpia los datos de usuario y listas`() {
        val vm = createViewModel()

        // Simulamos que el usuario tiene datos y listas con elementos
        vm.userId.value = 1L
        vm.userName.value = "Juan"
        vm.userLastName.value = "Pérez"
        vm.userEmail.value = "juan@example.com"
        vm.userPhone.value = "+56912345678"
        vm.token.value = "abc123"
        vm.isLoggedIn.value = true

        vm.vehicles.add(
            // no necesitamos campos reales, solo un objeto cualquiera
            com.example.mycar.network.dto.VehicleResponse(
                id = 1L,
                brand = "Toyota",
                model = "Yaris",
                year = 2020,
                plate = "AA-BB-11",
                km = 10000,
                soapDate = "01/01/2025",
                permisoCirculacionDate = "01/01/2025",
                revisionTecnicaDate = "01/01/2025",
                userId = 1L
            )
        )

        vm.maintenanceList.add(
            MaintenanceRecord(
                vehiclePlate = "AA-BB-11",
                type = "Cambio de aceite",
                date = "01/01/2025",
                km = "10000",
                notes = "Usar aceite 5W30"
            )
        )

        vm.alerts.add(
            AlertRecord(
                title = "Recordatorio",
                message = "Revisión técnica",
                date = "10/01/2025"
            )
        )

        // Ejecutamos logout
        vm.logout()

        // Verificamos que todo quedó limpio
        assertNull(vm.userId.value)
        assertEquals("", vm.userName.value)
        assertEquals("", vm.userLastName.value)
        assertEquals("", vm.userEmail.value)
        assertEquals("", vm.userPhone.value)
        assertEquals("", vm.token.value)
        assertFalse(vm.isLoggedIn.value)

        assertTrue(vm.vehicles.isEmpty())
        assertTrue(vm.maintenanceList.isEmpty())
        assertTrue(vm.alerts.isEmpty())
        assertNull(vm.profilePhoto.value)
    }

    @Test
    fun `addAlert agrega una alerta a la lista`() {
        val vm = createViewModel()

        assertTrue(vm.alerts.isEmpty())

        vm.addAlert(
            title = "Cambio de aceite",
            message = "Recordatorio de mantenimiento"
        )

        assertEquals(1, vm.alerts.size)
        val alert = vm.alerts[0]
        assertEquals("Cambio de aceite", alert.title)
        assertEquals("Recordatorio de mantenimiento", alert.message)
        // la fecha se genera internamente, así que solo comprobamos que no esté vacía
        assertTrue(alert.date.isNotBlank())
    }

    @Test
    fun `addMaintenance y removeMaintenance funcionan correctamente`() {
        val vm = createViewModel()

        val record = MaintenanceRecord(
            vehiclePlate = "AA-BB-11",
            type = "Cambio de neumáticos",
            date = "02/02/2025",
            km = "15000",
            notes = "Revisar presión"
        )

        // Agregamos
        vm.addMaintenance(record)
        assertEquals(1, vm.maintenanceList.size)
        assertSame(record, vm.maintenanceList[0])

        // Eliminamos
        vm.removeMaintenance(record)
        assertTrue(vm.maintenanceList.isEmpty())
    }
}
