package com.example

import com.example.kgcfootball.data.model.MatchFixtureEntity
import com.example.kgcfootball.data.model.PlayerEntity
import com.example.kgcfootball.data.model.TeamEntity
import com.example.kgcfootball.engine.MatchEngine
import com.example.kgcfootball.engine.OddsEngine
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    private val homeTeam = TeamEntity(
        id = "madrid",
        leagueId = "kgc_champions",
        name = "KGC Madrid",
        shortName = "MAD",
        stadiumName = "Santiago Bernabéu",
        primaryColorHex = 0xFFFFFFFF,
        secondaryColorHex = 0xFF0038A8,
        attackRating = 92,
        defenseRating = 89,
        midfieldRating = 91,
        overallRating = 91,
        tacticStyle = "CONTRE_ATTAQUE",
        formation = "4-3-3"
    )

    private val awayTeam = TeamEntity(
        id = "manchester",
        leagueId = "kgc_champions",
        name = "KGC City",
        shortName = "MCI",
        stadiumName = "Etihad Stadium",
        primaryColorHex = 0xFF6CABDD,
        secondaryColorHex = 0xFF1C2C5B,
        attackRating = 93,
        defenseRating = 90,
        midfieldRating = 93,
        overallRating = 92,
        tacticStyle = "TIKI_TAKA",
        formation = "4-3-3"
    )

    private val samplePlayers = listOf(
        PlayerEntity("p1", "madrid", "K. Mbappé", 9, "ATT", 94),
        PlayerEntity("p2", "madrid", "F. Valverde", 8, "MID", 89),
        PlayerEntity("p3", "madrid", "T. Courtois", 1, "GK", 90)
    )

    @Test
    fun testOddsEngineCalculatesValidPreMatchOdds() {
        val odds = OddsEngine.calculatePreMatchOdds(homeTeam, awayTeam)

        assertTrue("Cote domicile positive et réaliste", odds.homeWin in 1.10..25.0)
        assertTrue("Cote match nul réaliste", odds.draw in 1.80..25.0)
        assertTrue("Cote extérieur réaliste", odds.awayWin in 1.10..25.0)
        assertTrue("Cote Over 2.5 valide", odds.over25 in 1.10..15.0)
        assertTrue("Cote Under 2.5 valide", odds.under25 in 1.10..15.0)
        assertTrue("Cote Double Chance 1X inférieure à cote sèche 1", odds.doubleChance1X < odds.homeWin)
    }

    @Test
    fun testOddsEngineDynamicLiveAdjustment() {
        // En fin de match avec 2-0 pour le domicile à la 85e minute
        val liveOdds = OddsEngine.calculateLiveOdds(
            homeTeam = homeTeam,
            awayTeam = awayTeam,
            currentMinute = 85,
            currentHomeScore = 2,
            currentAwayScore = 0
        )

        assertTrue("La cote domicile s'effondre en fin de match avec avance", liveOdds.homeWin < 1.15)
        assertTrue("La cote extérieur explose", liveOdds.awayWin > 10.0)
    }

    @Test
    fun testMatchEngineFullSimulation() {
        val fixture = MatchFixtureEntity(
            id = "test_match",
            leagueId = "kgc_champions",
            round = 1,
            homeTeamId = homeTeam.id,
            awayTeamId = awayTeam.id
        )

        val result = MatchEngine.simulateFullMatch(
            fixture = fixture,
            homeTeam = homeTeam,
            awayTeam = awayTeam,
            homePlayers = samplePlayers,
            awayPlayers = samplePlayers
        )

        assertEquals("Match terminé", "FINISHED", result.updatedFixture.status)
        assertEquals("90 minutes complétées", 90, result.updatedFixture.minute)
        assertTrue("Score domicile positif ou nul", result.updatedFixture.homeScore >= 0)
        assertTrue("Score extérieur positif ou nul", result.updatedFixture.awayScore >= 0)
        assertTrue("Possession totale égale à 100", result.updatedFixture.homePossession + result.updatedFixture.awayPossession == 100)
    }

    @Test
    fun testComboBoostCalculations() {
        assertEquals("0 sélection = 0% boost", 0, OddsEngine.getComboBoostPercent(1))
        assertEquals("2 sélections = 5% boost", 5, OddsEngine.getComboBoostPercent(2))
        assertEquals("3 sélections = 10% boost", 10, OddsEngine.getComboBoostPercent(3))
        assertEquals("4 sélections = 20% boost", 20, OddsEngine.getComboBoostPercent(4))
        assertEquals("5 sélections = 35% boost", 35, OddsEngine.getComboBoostPercent(5))
        assertEquals("6+ sélections = 50% boost", 50, OddsEngine.getComboBoostPercent(6))

        assertEquals("Multiplicateur 3 sélections", 1.10, OddsEngine.getComboBoostMultiplier(3), 0.001)
        assertEquals("Multiplicateur 6 sélections", 1.50, OddsEngine.getComboBoostMultiplier(6), 0.001)
    }

    @Test
    fun testPostMatchNewsGeneration() {
        val finishedFixture = MatchFixtureEntity(
            id = "test_fixture_1",
            leagueId = "kgc_champions",
            round = 1,
            homeTeamId = homeTeam.id,
            awayTeamId = awayTeam.id,
            homeScore = 3,
            awayScore = 0,
            status = "FINISHED",
            minute = 90
        )

        val article = com.example.kgcfootball.data.model.NewsProvider.generatePostMatchArticle(
            fixture = finishedFixture,
            homeTeam = homeTeam,
            awayTeam = awayTeam,
            topScorerName = "K. Mbappé"
        )

        assertNotNull(article)
        assertTrue("Titre mentionne le vainqueur", article.title.contains("KGC Madrid"))
        assertTrue("Score mentionné dans le titre", article.title.contains("3 - 0"))
        assertTrue("Contenu riche et détaillé", article.fullContent.length > 50)
    }
}

