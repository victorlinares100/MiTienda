package Data

import Api.RetrofitClient
import Model.CarritoRequest
import android.util.Log

class CarritoRepository {

    private val api = RetrofitClient.apiService

    suspend fun procesarCompra(request: CarritoRequest): Boolean {
        // LOG 1: Ver qué estamos enviando
        Log.d("RepoCarrito", "Enviando compra: UsuarioID=${request.usuarioId}, Items=${request.items.size}")

        return try {
            val response = api.procesarCompra(request)

            if (response.isSuccessful) {
                // ÉXITO
                Log.d("RepoCarrito", " Compra exitosa! ID Comprobante: ${response.body()?.id}")
                true
            } else {
                // ERROR DEL SERVIDOR (400, 500)
                val errorString = response.errorBody()?.string()
                Log.e("RepoCarrito", "Error del servidor (${response.code()}): $errorString")
                false
            }
        } catch (e: Exception) {
            // ERROR DE CONEXIÓN
            Log.e("RepoCarrito", " Error de conexión/red: ${e.message}")
            e.printStackTrace() // Esto imprime toda la traza del error
            false
        }
    }
}