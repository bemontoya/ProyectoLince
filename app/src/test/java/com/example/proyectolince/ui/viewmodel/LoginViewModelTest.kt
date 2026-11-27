package com.example.proyectolince.ui.viewmodel

import android.app.Application
import com.example.proyectolince.data.SessionManager
import com.example.proyectolince.data.network.BackendLoginResponse
import com.example.proyectolince.data.remote.BackendClient
import com.example.proyectolince.data.remote.LinceBackendApi
import com.example.proyectolince.ui.screens.Rol
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var application: Application
    private lateinit var backendApi: LinceBackendApi

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        application = mockk(relaxed = true)
        backendApi = mockk(relaxed = true)

        mockkObject(BackendClient)
        every { BackendClient.create(any()) } returns backendApi

        mockkConstructor(SessionManager::class)
        coEvery {
            anyConstructed<SessionManager>().saveSession(any(), any(), any(), any(), any(), any())
        } returns Unit
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `validacion campos vacios y controles de UI funcionan correctamente`() {
        val vm = LoginViewModel(application)

        vm.onRolSelected(Rol.GUIA)
        assertEquals(Rol.GUIA, vm.uiState.value.rolSeleccionado)

        vm.togglePasswordVisibility()
        assertTrue(vm.uiState.value.isPasswordVisible)

        vm.onCorreoChanged("")
        vm.onClaveChanged("")
        vm.iniciarSesion()

        assertEquals("Ingresa tu correo y contraseña", vm.uiState.value.mensajeError)
        assertFalse(vm.uiState.value.isLoading)
    }

    @Test
    fun `login exitoso guarda sesion y marca isSuccess`() = runTest {
        val vm = LoginViewModel(application)
        vm.onRolSelected(Rol.DRIVER)
        vm.onCorreoChanged("driver@southbound.cl")
        vm.onClaveChanged("123456")

        coEvery { backendApi.login(any()) } returns Response.success(
            mockk(relaxed = true) {
                every { token } returns "jwt-123"
                every { email } returns "driver@southbound.cl"
                every { nombre } returns "Carlos Driver"
                every { rol } returns "DRIVER"
                every { linceRecursoId } returns 10
                every { oficinaCodigo } returns "SCL"
            }
        )

        vm.iniciarSesion()
        advanceUntilIdle()

        assertTrue(vm.uiState.value.isSuccess)
        assertNull(vm.uiState.value.mensajeError)

        vm.resetSuccessState()
        assertFalse(vm.uiState.value.isSuccess)
    }

    @Test
    fun `login rechaza si el rol del backend no coincide con el seleccionado`() = runTest {
        val vm = LoginViewModel(application)
        vm.onRolSelected(Rol.GUIA)
        vm.onCorreoChanged("driver@southbound.cl")
        vm.onClaveChanged("123456")

        coEvery { backendApi.login(any()) } returns Response.success(
            mockk(relaxed = true) {
                every { token } returns "jwt-123"
                every { email } returns "driver@southbound.cl"
                every { nombre } returns "Carlos Driver"
                every { rol } returns "DRIVER"
                every { linceRecursoId } returns 10
                every { oficinaCodigo } returns "SCL"
            }
        )

        vm.iniciarSesion()
        advanceUntilIdle()

        assertFalse(vm.uiState.value.isSuccess)
        assertNotNull(vm.uiState.value.mensajeError)
    }

    @Test
    fun `login maneja error 401 y excepcion de red`() = runTest {
        val vm = LoginViewModel(application)
        vm.onCorreoChanged("user@southbound.cl")
        vm.onClaveChanged("malaclave")

        coEvery { backendApi.login(any()) } returns Response.error(
            401,
            "Unauthorized".toResponseBody("text/plain".toMediaType())
        )

        vm.iniciarSesion()
        advanceUntilIdle()
        assertEquals("Correo o contraseña incorrectos", vm.uiState.value.mensajeError)

        coEvery { backendApi.login(any()) } throws RuntimeException("Sin conexión")
        vm.iniciarSesion()
        advanceUntilIdle()
        assertTrue(vm.uiState.value.mensajeError!!.contains("Error de conexión"))
    }
}