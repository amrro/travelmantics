package dev.amr.travelmantics.data


interface DataSource {

    suspend fun getDeals(): Result<List<Deal>>

    suspend fun newDeal(deal: Deal) : Result<Boolean>

}