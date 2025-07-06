package ar.edu.utn.frba.inventario.utils

import ar.edu.utn.frba.inventario.api.model.order.Order
import ar.edu.utn.frba.inventario.api.model.order.OrderResponse
import ar.edu.utn.frba.inventario.api.model.product.ProductOperation

object OrderMapper {

    fun toOrder(orderResponse: OrderResponse): Order = Order(
        id = orderResponse.id.toString(),
        number = "P-${orderResponse.id}",
        sender = orderResponse.sender,
        status = orderResponse.status,
        products = emptyList(),
        productsInOrder = toProductsInOrder(orderResponse),
        productAmount = orderResponse.productAmount,
        productNames = orderResponse.productNames,
        creationDate = orderResponse.creationDate.toLocalDateTime(),
        scheduledDate = orderResponse.scheduledDate.toLocalDateTime(),
        lastModifiedDate = orderResponse.lastModifiedDate.toLocalDateTime(),
    )

    fun toProductsInOrder(orderResponse: OrderResponse): List<ProductOperation> = orderResponse.productAmount.mapNotNull { (id, quantity) ->
        orderResponse.productNames[id]?.let { name ->
            ProductOperation(
                id = id.toString(),
                name = name,
                quantity = quantity,
            )
        }
    }
}
