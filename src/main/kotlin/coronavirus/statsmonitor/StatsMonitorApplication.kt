package coronavirus.statsmonitor

import io.micrometer.core.instrument.Tag
import io.micrometer.prometheus.PrometheusMeterRegistry
import mu.KotlinLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference


@SpringBootApplication
@EnableScheduling
class StatsMonitorApplication

fun main(args: Array<String>) {
    runApplication<StatsMonitorApplication>(*args)
}

private val logger = KotlinLogging.logger{ }

@Service
class ScheduledRunner(val service: WebService, val meterRegistry: PrometheusMeterRegistry, val statRepository: StatRepository) {

    @Scheduled(cron = "0 30 0 * * ?", zone = "Europe/London")
    fun run() = getLatestStatistics()

    fun getLatestStatistics(): List<Stat> {
        val stats = service.getData().toMutableList()

        stats.forEach {
            val tags = listOf(Tag.of("country", it.country))
            meterRegistry.gauge("total.cases", tags, AtomicLong(it.totalCases ?: 0))
            meterRegistry.gauge("active.cases", tags,  AtomicLong(it.activeCases ?: 0))
            meterRegistry.gauge("new.cases", tags,  AtomicLong(it.newCases ?: 0))
            meterRegistry.gauge("total.deaths", tags,  AtomicLong(it.totalDeaths ?: 0))
            meterRegistry.gauge("new.deaths", tags,  AtomicLong(it.newDeaths ?: 0))
            meterRegistry.gauge("total.recovered.cases", tags,  AtomicLong(it.totalRecovered ?: 0))
            meterRegistry.gauge("serious.critical.cases", tags,  AtomicLong(it.seriousCriticalCases ?: 0))

            statRepository.save(it)
        }

        logger.info { "Published ${stats.size} statistics" }

        logger.info { "Total stat records in db ${statRepository.count()}" }

        return stats
    }
}

@Controller
class LiveController(val scheduledRunner: ScheduledRunner) {

    @GetMapping("/live")
    fun getLiveStatistics() = ResponseEntity.ok(scheduledRunner.getLatestStatistics())
}
