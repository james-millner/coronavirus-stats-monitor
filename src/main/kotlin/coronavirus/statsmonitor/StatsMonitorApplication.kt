package coronavirus.statsmonitor

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling


@SpringBootApplication
@EnableScheduling
class StatsMonitorApplication

fun main(args: Array<String>) {
    runApplication<StatsMonitorApplication>(*args)
}





