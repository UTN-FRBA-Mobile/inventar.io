package ar.edu.utn.frba.inventario.utils

import android.util.Log

object ShipmentProductToScanList {
    private var active : Boolean = false
    private val ProductToScanList: MutableMap<String, Int> = mutableMapOf()


    fun addProduct(productId: String, loadedQuantity: Int) {
        ProductToScanList[productId] = loadedQuantity
        Log.d("ShipmentProductToScanList", "Se agrega el productId $productId con loadedQuantity: $loadedQuantity")
    }
    fun updateLoadedQuantity(productId: String, loadedQuantity: Int) {
        if(existProductId(productId)){
            ProductToScanList[productId] = loadedQuantity
            Log.d("ShipmentProductToScanList", "Se actualiza el productId $productId con loadedQuantity: $loadedQuantity")
        }else{
            Log.d("ShipmentProductToScanList", "No existe el productId: $productId")
        }
    }
    fun activeShipmentProductToScanList(){
        active = true
    }
    fun statusShipmentProductToScanList(): Boolean{
        return active
    }
    fun getLoadedProducts(): Map<String,Int>{
        return ProductToScanList
    }
    fun existProductId(productId: String):Boolean{
        return ProductToScanList.containsKey(productId)
    }
    fun clear(){
        active = false
        ProductToScanList.clear()
        Log.d("ShipmentProductToScanList", "Se limpia los valores, ya que se salio del circuito de ShipmentDetail")
    }
}