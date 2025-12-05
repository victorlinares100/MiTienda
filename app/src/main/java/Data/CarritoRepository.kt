package Data

import Api.RetrofitClient
import Model.CarritoRequest
import android.util.Log

class CarritoRepository {

    private val api = RetrofitClient.apiService

    suspend fun procesarCompra(request: CarritoRequest): Boolean {

        Log.d("RepoCarrito", "Enviando compra: UsuarioID=${request.usuarioId}, Items=${request.items.size}")

        return try {
            val response = api.procesarCompra(request)

            if (response.isSuccessful) {

                Log.d("RepoCarrito", " Compra exitosa! ID Comprobante: ${response.body()?.id}")
                true
            } else {

                val errorString = response.errorBody()?.string()
                Log.e("RepoCarrito", "Error del servidor (${response.code()}): $errorString")
                false
            }
        } catch (e: Exception) {

            Log.e("RepoCarrito", " Error de conexión/red: ${e.message}")
            e.printStackTrace()
            false
        }
    }
}