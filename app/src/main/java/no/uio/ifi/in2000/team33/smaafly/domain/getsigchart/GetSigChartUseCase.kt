package no.uio.ifi.in2000.team33.smaafly.domain.getsigchart

import android.graphics.Bitmap
import no.uio.ifi.in2000.team33.smaafly.data.sigchart.SigChartRepository

/**
 * Use case for fetching current SigChart
 *
 * @param sigChartRepository Repository for SigChart
 */
class GetSigChartUseCase(private val sigChartRepository: SigChartRepository) {
    
    /**
     * Get current sigchart
     *
     * @return Bitmap with current sigchart for norway
     */
    suspend fun getSigChart(): Bitmap {
        return sigChartRepository.getSigChart()
    }
}