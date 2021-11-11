package br.com.up.mypins.connection

import br.com.up.mypins.model.Places

class PlaceAPI {

    fun searchPlaces(query: String, listener: PlaceAPIListener) {
        val url =
            "https://maps.googleapis.com/maps/api/place/textsearch/json?query=$query&key=AIzaSyDyFUltXibk_rGCb7Nak3wB5HC_KamkKRI&language=pt-BR"

        ConnectionAsyncTask { jsonObject ->
            val places = arrayListOf<Places>()



            if (jsonObject != null) {
                val results = jsonObject.getJSONArray("results")


                for(position in 0 until results.length()){
                    val objectPlaces = results.getJSONObject(position)

                    val objectGeometry = objectPlaces.getJSONObject("geometry")
                    val objectLocation = objectGeometry.getJSONObject("location")
                    val latitude = objectLocation.getDouble("lat")
                    val longitude = objectLocation.getDouble("lng")

                    val name = objectPlaces.getString("name");

                    val address = objectPlaces.getString("formatted_address");

                    val openNow = if (objectPlaces.has("opening_hours")) {
                        val objectOpeningHours = objectPlaces.getJSONObject("opening_hours")
                        objectOpeningHours.getString("open_now")
                    } else {
                        "n/a"
                    }

                    places.add(Places(name, latitude, longitude, openNow, address))

                }
                listener.onPlacesResult(places)
            }

        }.execute(url)
    }

    interface PlaceAPIListener{
        fun onPlacesResult(places: ArrayList<Places>)

    }
}

