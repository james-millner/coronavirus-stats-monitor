package coronavirus.statsmonitor

import org.jsoup.Jsoup
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping

data class Stat(
        val country: String,
        val totalCases: Long?,
        val newCases: Long?,
        val totalDeaths: Long?,
        val newDeaths: Long?,
        val totalRecovered: Long?,
        val activeCases: Long?,
        val seriousCriticalCases: Long?,
        val totalCasesPer1MPopulation: Double?
)

@Service
class WebService() {

    fun getData(): List<Stat> =
            Jsoup.connect("https://www.worldometers.info/coronavirus/").get().run {
                val tableRows = getElementById("main_table_countries")
                        .select("tbody > tr")

                tableRows.map {
                    println("TableRow")
                    println(it.toString())

                    println("*** Each Row ***")
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


@Controller
class test(val service: WebService) {

    @GetMapping("/test")
    fun get() = ResponseEntity.ok(service.getData())
}
