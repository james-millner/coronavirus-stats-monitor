package coronavirus.statsmonitor

import mu.KotlinLogging
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

private val logger = KotlinLogging.logger { }

@RestController
class LiveController(val scheduledRunner: ScheduledRunner, val statRepository: StatRepository) {

    @GetMapping("/live")
    fun getLiveStatistics() = ResponseEntity.ok(scheduledRunner.getLatestStatistics())

    @GetMapping(value = ["/metrics"], produces = [MediaType.TEXT_PLAIN_VALUE])
    fun metrics(): ResponseEntity<String> {

        val stats = statRepository.findByTimestamp(statRepository.findMaxTimestamp())

        val prometheusMetrics = stats.map { stat ->
            listOf(
                    createPrometheusMetric("total_cases", (stat.totalCases ?: 0).toDouble(),
                                           mapOf("country" to stat.country)),
                    createPrometheusMetric("new_cases", (stat.newCases ?: 0).toDouble(),
                                           mapOf("country" to stat.country)),
                    createPrometheusMetric("total_deaths", (stat.totalDeaths ?: 0).toDouble(),
                                           mapOf("country" to stat.country)),
                    createPrometheusMetric("new_deaths", (stat.newDeaths ?: 0).toDouble(),
                                           mapOf("country" to stat.country)),
                    createPrometheusMetric("total_recovered", (stat.totalRecovered ?: 0).toDouble(),
                                           mapOf("country" to stat.country)),
                    createPrometheusMetric("active_cases", (stat.activeCases ?: 0).toDouble(),
                                           mapOf("country" to stat.country)),
                    createPrometheusMetric("serious_critical", (stat.seriousCriticalCases ?: 0).toDouble(),
                                           mapOf("country" to stat.country)),
                    createPrometheusMetric("total_cases_per_one_million_population",
                                           stat.totalCasesPer1MPopulation ?: 0.0, mapOf("country" to stat.country))
                    )
        }.flatten()
            .sorted()
            .joinToString("\n")

        return ResponseEntity.ok(prometheusMetrics)
    }

    fun createPrometheusMetric(metricName: String, value: Double?, tags: Map<String, String>): String =
            tags.entries
                    .joinToString(", ") { "${it.key}=\"${it.value}\", " }
                    .let { "$metricName{$it} $value" }


}
