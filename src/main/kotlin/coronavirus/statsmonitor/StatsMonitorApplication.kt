package coronavirus.statsmonitor

import io.micrometer.core.instrument.Tag
import io.micrometer.prometheus.PrometheusMeterRegistry
import mu.KotlinLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.TimeZone

@SpringBootApplication
@EnableScheduling
class StatsMonitorApplication

fun main(args: Array<String>) {
    runApplication<StatsMonitorApplication>(*args)
}

private val logger = KotlinLogging.logger{ }

@Service
class Runner(val service: WebService, val meterRegistry: PrometheusMeterRegistry, val statRepository: StatRepository) {

    @Scheduled(cron = "0 30 0 * * ?", zone = "Europe/London")
    fun run() {
        val stats = service.getData()

        stats.forEach {
            val tags = listOf(Tag.of("country", it.country))
            meterRegistry.gauge("total.cases", tags , it.activeCases ?: 0 )
            meterRegistry.gauge("new.cases", tags ,  it.newCases ?: 0)
            meterRegistry.gauge("total.deaths", tags , it.totalDeaths ?: 0)
            meterRegistry.gauge("new.deaths", tags , it.newDeaths ?: 0)
            meterRegistry.gauge("total.recovered.cases", tags , it.totalRecovered ?: 0)
            meterRegistry.gauge("serious.critical.cases", tags , it.seriousCriticalCases ?: 0)
            meterRegistry.gauge("total.cases.per.one.million.population", tags , it.totalCasesPer1MPopulation ?: 0.0)

            statRepository.save(it)
        }

        logger.info { "Published ${stats.size} statistics" }

        logger.info { "Total stat records in mongodb ${statRepository.count()}" }
    }
}
