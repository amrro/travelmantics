package dev.amr.travelmantics.data


interface DataSource {

    suspend fun getTravels(): Result<List<Travel>>

    suspend fun newTravel(travel: Travel) : Result<Boolean>

}