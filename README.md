# Travelmantics
This is a simple app to show off MVVM architecture used for firebase applications. MVVM combined with Android Architecture Component, coroutines, and  data binding library can help to write a very, efficient, and less-error prone applications. 


# Architecture
Google introduced `LiveData` and `ViewModel` that works greatly with MVVM pattern. Combined with Data Binding library, You can actually get a very satisfying result, clean code free from UI logic. In addition, Kotlin corountines helps in making async callbacks really simple and readble like sequentials code. Kotlin coroutnies is well supporeted in Android actually ([Improve app performance with Kotlin coroutines](https://developer.android.com/kotlin/coroutines))


**This is a general view of application architecture**: 
![eagle-view.png](https://i.imgur.com/aQHGmKM.png)


# Workers
Workers API is really powerful and easy to configure. It allow users to add content (deals) to application even if s/he is offline. Once the `NetworkType.CONNECTED` constraint is satisfied, It will do the request.
![worker.png](https://i.imgur.com/iQcfGfv.png)

