package dam.pmdm.juegotablero.network

import dam.pmdm.juegotablero.models.gamestats.GameStatsResponse
import dam.pmdm.juegotablero.models.login.LoginRequest
import dam.pmdm.juegotablero.models.login.LoginResponse
import dam.pmdm.juegotablero.models.Riddle
import dam.pmdm.juegotablero.models.gamestats.GameStatsCreate
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Header

interface ApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("riddles")
    suspend fun getRiddles(@Query("category") category: String): Response<List<Riddle>>

    @GET("game_stats")
    suspend fun getGameStats(@Header("Authorization") authHeader: String): Response<List<GameStatsResponse>>

    @POST("game_stats")
    suspend fun postGameStats(
        @Header("Authorization") authHeader: String,
        @Body gameStat: GameStatsCreate
    ): Response<GameStatsResponse>

}
