package no.uio.ifi.in2000.team33.smaafly.data.sigchart

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import no.uio.ifi.in2000.team33.smaafly.model.sigchart.AvailableSigCharts
import javax.inject.Named

/**
 * Data source for SigChart
 *
 * @param client Client for fetching data from remote API
 */
class SigChartDataSource(@Named("metno") private val client: HttpClient) {
    /**
     * Get current SigChart
     *
     * @return Bitmap with current SigChart for norway
     */
    suspend fun getAvailableSigcharts(): Bitmap {
        Log.d("SigChartDataSours", "Starting to fetch SigCharts")
        Log.d("api", "SigChart norway API call")
        try {
            /*
            We could not use the IFI proxy because the binary files were corrupted
             */
            val i: ByteArray =
                client.get("https://api.met.no/weatherapi/sigcharts/2.0/norway").body<ByteArray>()
            Log.d("SigChartDataSours", "Fetched SigCharts successfully. ByteArray size: ${i.size}")
            return convertImageByteArrayToBitmap(i)
        } catch (e: Exception) {
            Log.e("SigChartDataSours", "Error fetching SigCharts: ${e.message}", e)
            throw e
        }
    }

    /**
     * Convert byte array to bitmap
     *
     * @param imageData ByteArray containing the image
     * @return Bitmap for image
     */
    private fun convertImageByteArrayToBitmap(imageData: ByteArray): Bitmap =
        BitmapFactory.decodeByteArray(imageData, 0, imageData.size)

}


