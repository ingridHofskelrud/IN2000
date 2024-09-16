package no.uio.ifi.in2000.team33.smaafly.data.sigchart

import android.graphics.Bitmap

/**
 * Repository for SigCharts
 *
 * @param sigChartDataSource Data source for SigChart
 */
class SigChartRepository(private val sigChartDataSource: SigChartDataSource) {
    
    /**
     * Get current sigchart
     *
     * @return Bitmap with current sigchart for norway
     */
    suspend fun getSigChart(): Bitmap {
        return sigChartDataSource.getAvailableSigcharts()
    }
}