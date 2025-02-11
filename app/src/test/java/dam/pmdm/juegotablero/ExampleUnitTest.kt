// ApiIntegrationTest.kt
package dam.pmdm.juegotablero

import dam.pmdm.juegotablero.models.login.LoginRequest
import dam.pmdm.juegotablero.models.login.LoginResponse
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import dam.pmdm.juegotablero.network.ApiService
import junit.framework.TestCase.assertEquals

class ApiIntegrationTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))  // Usa la URL del servidor simulado
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testLoginIntegration() = runBlocking {
        // Define una respuesta simulada para el login
        val fakeResponse = """
            {"access_token": "fake-token-123", "token_type": "bearer"}
        """.trimIndent()
        mockWebServer.enqueue(MockResponse().setBody(fakeResponse).setResponseCode(200))

        // Realiza la llamada al endpoint login
        val response = apiService.login(LoginRequest("testuser", "secret123"))
        assertEquals(200, response.code())
        val loginResponse: LoginResponse? = response.body()
        assertEquals("fake-token-123", loginResponse?.access_token)
    }
}
