package coronavirus.statsmonitor

import mu.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger{ }

@Service
class ScheduledRunner(val service: WebService, val statRepository: StatRepository) {

    @Scheduled(cron = "0 0/10 0/1 ? * *", zone = "Europe/London")
    fun run() = getLatestStatistics()

    fun getLatestStatistics(): List<Stat> {
        val stats = service.getData().toMutableList()

        stats.forEach {
            statRepository.save(it)
        }

        logger.info { "Published ${stats.size} statistics" }

        logger.info { "Total stat records in db ${statRepository.count()}" }

        return stats
    }
}
