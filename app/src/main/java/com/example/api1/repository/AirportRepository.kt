import android.content.Context
import android.util.Log
import com.example.api1.data.model.Airports
import com.example.api1.data.network.AirportApiClient
import com.example.api1.data.network.ApiService
import com.example.api1.data.preferences.DataStoreManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AirportRepository(private val context: Context) {
    private val apiService: ApiService = AirportApiClient.instance
    private val studentsKey = DataStoreManager.getStringKey("students")
    private val gson = Gson()

    fun getAirports(onResult: (List<Airports>?) -> Unit, onError: (Throwable) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            // First, try to fetch data locally
            val localData = fetchLocally()
            if (localData != null && localData.isNotEmpty()) {
                Log.d("AirportRepository", "Fetched locally: $localData")
                onResult(localData)
            } else {
                // If no local data, fetch from API
                fetchStudentsFromApi(onResult, onError)
            }
        }
    }

    private fun fetchLocally(): List<Airports>? {
        val savedStudentsJson = DataStoreManager.getData(context, studentsKey)
        Log.d("AirportRepository", "Local JSON: $savedStudentsJson")
        return if (!savedStudentsJson.isNullOrEmpty()) {
            deserializeStudents(savedStudentsJson)
        } else {
            null
        }
    }

    private fun fetchStudentsFromApi(onResult: (List<Airports>?) -> Unit, onError: (Throwable) -> Unit) {
        val call = apiService.getAirports()

        call.enqueue(object : Callback<List<Airports>> {
            override fun onResponse(call: Call<List<Airports>>, response: Response<List<Airports>>) {
                if (response.isSuccessful) {
                    val students = response.body()
                    Log.d("AirportRepository", "Fetched from API: $students")
                    // Save the fetched student data in DataStore
                    CoroutineScope(Dispatchers.IO).launch {
                        val studentsJson = serializeStudents(students)
                        Log.d("AirportRepository", "Saving to local storage: $studentsJson")
                        DataStoreManager.saveData(context, studentsKey, studentsJson)
                    }
                    onResult(students)
                } else {
                    onError(Throwable(response.errorBody()?.string()))
                }
            }

            override fun onFailure(call: Call<List<Airports>>, t: Throwable) {
                onError(t)
            }
        })
    }

    private fun serializeStudents(students: List<Airports>?): String {
        // Serialize the list of students to JSON
        return gson.toJson(students)
    }

    fun deserializeStudents(jsonString: String): List<Airports> {
        val airportListType = object : TypeToken<List<Airports>>() {}.type
        return try {
            val airports: List<Airports> = gson.fromJson(jsonString, airportListType)
            Log.d("AirportRepository", "Deserialized JSON: $airports")
            airports
        } catch (e: Exception) {
            Log.e("AirportRepository", "Failed to parse JSON: ${e.message}")
            emptyList()
        }
    }

}
