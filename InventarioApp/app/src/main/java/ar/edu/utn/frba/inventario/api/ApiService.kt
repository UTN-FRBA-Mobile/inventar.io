package ar.edu.utn.frba.inventario.api

import ar.edu.utn.frba.inventario.api.model.auth.LoginRequest
import ar.edu.utn.frba.inventario.api.model.auth.LoginResponse
import ar.edu.utn.frba.inventario.api.model.order.OrderResponse
import ar.edu.utn.frba.inventario.api.model.product.Product
import ar.edu.utn.frba.inventario.api.model.product.ProductResponse
import ar.edu.utn.frba.inventario.api.model.product.ProductStockLocationResponse
import ar.edu.utn.frba.inventario.api.model.self.LocationResponse
import ar.edu.utn.frba.inventario.api.model.self.UserResponse
import ar.edu.utn.frba.inventario.api.model.shipment.ShipmentResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    /* Authentication */
    @POST("/api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("/api/v1/auth/refresh")
    fun refreshToken(@Body refreshToken: String): Call<LoginResponse>

    /* Self location & user */
    @GET("/api/v1/user/self")
    suspend fun getMyUser(): Response<UserResponse>

    @GET("/api/v1/location/self")
    suspend fun getMyLocation(): Response<LocationResponse>

    /*Order*/
    @GET("/api/v1/orders/{id}")
    suspend fun getOrder(@Path("id") id:String): Response<OrderResponse>

    @GET("/api/v1/orders")
    suspend fun getOrdersList(): Response<List<OrderResponse>>

    /*Shipment*/
    @GET("/api/v1/shipments/{id}")
    suspend fun getShipment(@Path("id") id:Long): Response<ShipmentResponse>

    @GET("/api/v1/shipments")
    suspend fun getShipmentList(): Response<List<ShipmentResponse>>

    /*Products*/
    @GET("/api/v1/products")
    suspend fun getProductList(@Query("ean13s") ean13s: List<String>): Response<Map<Long, Product>>

    @GET("/api/v1/products")
    suspend fun getProductListById(@Query("id") id: String): Response<Map<String, ProductResponse>>

    @GET("/api/v1/products/stock")
    suspend fun getStockByProductId(@Query("ids") ids: String): Response<ProductStockLocationResponse>
}