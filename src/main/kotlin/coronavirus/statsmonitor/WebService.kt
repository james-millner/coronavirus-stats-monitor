package coronavirus.statsmonitor

import org.jsoup.Jsoup
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "statistics")
data class Stat(
        @Id @GeneratedValue
        val id: Long? = null,
        @Column(nullable = false)
        val country: String,
        @Column(nullable = true)
        val totalCases: Long?,
        @Column(nullable = true)
        val newCases: Long?,
        @Column(nullable = true)
        val totalDeaths: Long?,
        @Column(nullable = true)
        val newDeaths: Long?,
        @Column(nullable = true)
        val totalRecovered: Long?,
        @Column(nullable = true)
        val activeCases: Long?,
        @Column(nullable = true)
        val seriousCriticalCases: Long?,
        @Column(nullable = true)
        val totalCasesPer1MPopulation: Double?,
        @Column(nullable = false)
        val timestamp: Instant = Instant.now()
)

interface StatRepository : JpaRepository<Stat, Long>

@Service
class WebService {

    fun getData(): List<Stat> =
            Jsoup.connect("https://www.worldometers.info/coronavirus/")
                    .header("User-Agent",
                            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:74.0) Gecko/20100101 Firefox/74.0")
                    .header("Host", "www.worldometers.info")
                    .header("Accept-Language", "en-GB,en;q=0.5")
                    .get().run {
                        val tableRows = getElementById("main_table_countries_today")
                                .select("tbody > tr")

                        tableRows.map {
                            val td = it.select("td")

                            val stat = Stat(
                                    country = td[0].text(),
                                    totalCases = td[1].text().trim().replace(",", "").toLongOrNull(),
                                    newCases = td[2].text().trim().replace(",", "").toLongOrNull(),
                                    totalDeaths = td[3].text().trim().replace(",", "").toLongOrNull(),
                                    newDeaths = td[4].text().trim().replace(",", "").toLongOrNull(),
                                    totalRecovered = td[5].text().trim().replace(",", "").toLongOrNull(),
                                    activeCases = td[6].text().trim().replace(",", "").toLongOrNull(),
                                    seriousCriticalCases = td[7].text().trim().replace(",", "").toLongOrNull(),
                                    totalCasesPer1MPopulation = td[8].text().trim().toDoubleOrNull()
                            )
                            stat
                        }
                    }
}
