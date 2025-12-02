package Model

import com.google.gson.annotations.SerializedName

data class CarritoRequest(
    @SerializedName("usuarioId") val usuarioId: Long,
    @SerializedName("metodoPagoId") val metodoPagoId: Long,
    @SerializedName("estadoId") val estadoId: Long,
    @SerializedName("items") val items: List<CarritoItemRequest>
)

data class CarritoItemRequest(
    @SerializedName("productoId") val productoId: Long,
    @SerializedName("cantidad") val cantidad: Int,
    @SerializedName("precioUnitario") val precioUnitario: Double
)

// Respuesta del backend (Comprobante)
data class ComprobanteResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("totalCompra") val total: Double,
    @SerializedName("fechaOrden") val fecha: String
)