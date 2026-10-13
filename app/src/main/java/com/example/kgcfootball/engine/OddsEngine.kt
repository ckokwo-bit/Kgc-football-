package com.example.kgcfootball.engine

import com.example.kgcfootball.data.model.TeamEntity
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round

data class MarketOdds(
    val homeWin: Double,
    val draw: Double,
    val awayWin: Double,
    val over15: Double = 1.25,
    val under15: Double = 3.60,
    val over25: Double,
    val under25: Double,
    val over35: Double = 2.80,
    val under35: Double = 1.40,
    val bttsYes: Double,
    val bttsNo: Double,
    val doubleChance1X: Double,
    val doubleChance12: Double,
    val doubleChanceX2: Double,
    val handicapHomeMinus1: Double = 2.45,
    val handicapAwayPlus1: Double = 1.55,
    val exactScores: Map<String, Double> = emptyMap(),
    // Live in-play specific
    val nextGoalHome: Double = 2.10,
    val nextGoalNone: Double = 3.20,
    val nextGoalAway: Double = 2.40
)

object OddsEngine {

    private const val BOOKMAKER_MARGIN = 1.06 // 6% margin
    private const val BASE_GOALS = 1.35

    /**
     * Calcule les cotes pré-match basées sur la force relative des deux équipes,
     * l'avantage du terrain et leurs styles tactiques via la loi de Poisson.
     */
    fun calculatePreMatchOdds(homeTeam: TeamEntity, awayTeam: TeamEntity): MarketOdds {
        val lambdaHome = computeLambdaHome(homeTeam, awayTeam, minute = 0, currentRedCardsHome = 0, currentRedCardsAway = 0)
        val lambdaAway = computeLambdaAway(homeTeam, awayTeam, minute = 0, currentRedCardsHome = 0, currentRedCardsAway = 0)

        return computeOddsFromLambdas(
            lambdaHome = lambdaHome,
            lambdaAway = lambdaAway,
            currentHomeGoals = 0,
            currentAwayGoals = 0,
            minute = 0
        )
    }

    /**
     * Ajustement dynamique des cotes en direct (Live) pendant le match
     * en fonction du score actuel, des cartons rouges et du temps restant.
     */
    fun calculateLiveOdds(
        homeTeam: TeamEntity,
        awayTeam: TeamEntity,
        currentMinute: Int,
        currentHomeScore: Int,
        currentAwayScore: Int,
        homeRedCards: Int = 0,
        awayRedCards: Int = 0
    ): MarketOdds {
        val clampedMinute = currentMinute.coerceIn(0, 90)
        val remainingFraction = (90 - clampedMinute).toDouble() / 90.0

        val baseLambdaHome = computeLambdaHome(homeTeam, awayTeam, clampedMinute, homeRedCards, awayRedCards)
        val baseLambdaAway = computeLambdaAway(homeTeam, awayTeam, clampedMinute, homeRedCards, awayRedCards)

        val remLambdaHome = max(0.05, baseLambdaHome * remainingFraction)
        val remLambdaAway = max(0.05, baseLambdaAway * remainingFraction)

        val odds = computeOddsFromLambdas(
            lambdaHome = remLambdaHome,
            lambdaAway = remLambdaAway,
            currentHomeGoals = currentHomeScore,
            currentAwayGoals = currentAwayScore,
            minute = clampedMinute
        )

        // Calcul du marché 'Prochain But' en direct
        val probNextHome = remLambdaHome / (remLambdaHome + remLambdaAway + 0.3)
        val probNextAway = remLambdaAway / (remLambdaHome + remLambdaAway + 0.3)
        val probNextNone = 1.0 - (probNextHome + probNextAway).coerceAtMost(0.95)

        return odds.copy(
            nextGoalHome = toOdds(probNextHome),
            nextGoalNone = toOdds(probNextNone),
            nextGoalAway = toOdds(probNextAway)
        )
    }

    private fun computeLambdaHome(
        home: TeamEntity,
        away: TeamEntity,
        minute: Int,
        currentRedCardsHome: Int,
        currentRedCardsAway: Int
    ): Double {
        val attRatio = home.attackRating.toDouble() / away.defenseRating.toDouble()
        val homeAdvantage = 0.25 // Avantage du terrain (environ +0.25 xG)
        var lambda = BASE_GOALS * attRatio + homeAdvantage

        // Modificateurs tactiques
        when (home.tacticStyle) {
            "GEGENPRESS" -> lambda += 0.20
            "CATENACCIO" -> lambda -= 0.30
            "CONTRE_ATTAQUE" -> if (away.attackRating > 88) lambda += 0.15
        }
        // Impact des cartons rouges
        lambda -= currentRedCardsHome * 0.35
        lambda += currentRedCardsAway * 0.40

        return max(0.3, lambda)
    }

    private fun computeLambdaAway(
        home: TeamEntity,
        away: TeamEntity,
        minute: Int,
        currentRedCardsHome: Int,
        currentRedCardsAway: Int
    ): Double {
        val attRatio = away.attackRating.toDouble() / home.defenseRating.toDouble()
        var lambda = BASE_GOALS * attRatio - 0.10 // Léger malus extérieur

        when (away.tacticStyle) {
            "GEGENPRESS" -> lambda += 0.18
            "CATENACCIO" -> lambda -= 0.35
            "CONTRE_ATTAQUE" -> if (home.attackRating > 88) lambda += 0.18
        }
        lambda -= currentRedCardsAway * 0.35
        lambda += currentRedCardsHome * 0.40

        return max(0.3, lambda)
    }

    private fun computeOddsFromLambdas(
        lambdaHome: Double,
        lambdaAway: Double,
        currentHomeGoals: Int,
        currentAwayGoals: Int,
        minute: Int
    ): MarketOdds {
        // Matrice de Poisson 6x6 pour les buts additionnels
        val maxSim = 6
        val matrix = Array(maxSim) { DoubleArray(maxSim) }

        var totalProb = 0.0
        for (i in 0 until maxSim) {
            val pI = poisson(i, lambdaHome)
            for (j in 0 until maxSim) {
                val pJ = poisson(j, lambdaAway)
                val prob = pI * pJ
                matrix[i][j] = prob
                totalProb += prob
            }
        }

        // Normalisation
        if (totalProb > 0) {
            for (i in 0 until maxSim) {
                for (j in 0 until maxSim) {
                    matrix[i][j] /= totalProb
                }
            }
        }

        var pHomeWin = 0.0
        var pDraw = 0.0
        var pAwayWin = 0.0
        var pOver15 = 0.0
        var pOver25 = 0.0
        var pOver35 = 0.0
        var pBttsYes = 0.0
        var pHandicapHomeMinus1 = 0.0
        var pHandicapAwayPlus1 = 0.0

        val exactScoreProbMap = mutableMapOf<String, Double>()

        for (i in 0 until maxSim) {
            for (j in 0 until maxSim) {
                val finalHome = currentHomeGoals + i
                val finalAway = currentAwayGoals + j
                val prob = matrix[i][j]

                if (finalHome > finalAway) pHomeWin += prob
                else if (finalHome == finalAway) pDraw += prob
                else pAwayWin += prob

                val totalGoals = finalHome + finalAway
                if (totalGoals > 1) pOver15 += prob
                if (totalGoals > 2) pOver25 += prob
                if (totalGoals > 3) pOver35 += prob

                if (finalHome > 0 && finalAway > 0) pBttsYes += prob

                if ((finalHome - finalAway) >= 2) pHandicapHomeMinus1 += prob
                if ((finalAway - finalHome) >= 0) pHandicapAwayPlus1 += prob

                val scoreKey = "$finalHome-$finalAway"
                exactScoreProbMap[scoreKey] = (exactScoreProbMap[scoreKey] ?: 0.0) + prob
            }
        }

        val pUnder15 = max(0.01, 1.0 - pOver15)
        val pUnder25 = max(0.01, 1.0 - pOver25)
        val pUnder35 = max(0.01, 1.0 - pOver35)
        val pBttsNo = max(0.01, 1.0 - pBttsYes)

        val p1X = min(0.99, pHomeWin + pDraw)
        val p12 = min(0.99, pHomeWin + pAwayWin)
        val pX2 = min(0.99, pDraw + pAwayWin)

        // Conversion des scores exacts les plus populaires
        val topScores = listOf("1-0", "2-0", "2-1", "3-0", "3-1", "0-0", "1-1", "2-2", "0-1", "0-2", "1-2", "0-3")
        val exactScoresOdds = topScores.associateWith { score ->
            toOdds(exactScoreProbMap[score] ?: 0.01)
        }

        return MarketOdds(
            homeWin = toOdds(pHomeWin),
            draw = toOdds(pDraw),
            awayWin = toOdds(pAwayWin),
            over15 = toOdds(pOver15),
            under15 = toOdds(pUnder15),
            over25 = toOdds(pOver25),
            under25 = toOdds(pUnder25),
            over35 = toOdds(pOver35),
            under35 = toOdds(pUnder35),
            bttsYes = toOdds(pBttsYes),
            bttsNo = toOdds(pBttsNo),
            doubleChance1X = toOdds(p1X),
            doubleChance12 = toOdds(p12),
            doubleChanceX2 = toOdds(pX2),
            handicapHomeMinus1 = toOdds(pHandicapHomeMinus1.coerceAtLeast(0.04)),
            handicapAwayPlus1 = toOdds(pHandicapAwayPlus1.coerceAtLeast(0.04)),
            exactScores = exactScoresOdds
        )
    }

    private fun poisson(k: Int, lambda: Double): Double {
        var fact = 1.0
        for (i in 2..k) fact *= i
        return (Math.pow(lambda, k.toDouble()) * exp(-lambda)) / fact
    }

    /**
     * Calcul réaliste du Cashout dynamique en direct (style Betclic/1xBet).
     * Prend en compte la probabilité actuelle de gain de chaque sélection.
     */
    fun calculateLiveCashout(
        stake: Double,
        potentialWin: Double,
        selections: List<Pair<com.example.kgcfootball.data.model.BetItemEntity, Double?>>
    ): Double {
        if (selections.isEmpty()) return 0.0

        var combinedMultiplier = 1.0
        for ((item, liveOdds) in selections) {
            when {
                item.status == "LOST" -> return 0.0
                item.status == "WON" -> { /* déjà validé */ }
                liveOdds != null && liveOdds >= 1.01 -> {
                    // Ratio entre cote initiale et cote en direct
                    val ratio = (item.odds / liveOdds).coerceIn(0.02, 3.5)
                    combinedMultiplier *= ratio
                }
                else -> {
                    combinedMultiplier *= 0.95
                }
            }
        }

        val estimated = stake * combinedMultiplier * 0.92
        val maxCashout = potentialWin * 0.98
        val minCashout = (stake * 0.05).coerceAtLeast(10.0)

        val finalValue = estimated.coerceIn(minCashout, maxCashout)
        return (round(finalValue * 100) / 100.0)
    }

    /**
     * Taux de super boost combiné selon le nombre de sélections dans le coupon.
     * Style bookmaker professionnel (1xBet / Betclic / Mozzart) :
     * 2 sélections -> +5%
     * 3 sélections -> +10%
     * 4 sélections -> +20%
     * 5 sélections -> +35%
     * 6+ sélections -> +50%
     */
    fun getComboBoostPercent(selectionCount: Int): Int = when {
        selectionCount >= 6 -> 50
        selectionCount == 5 -> 35
        selectionCount == 4 -> 20
        selectionCount == 3 -> 10
        selectionCount == 2 -> 5
        else -> 0
    }

    fun getComboBoostMultiplier(selectionCount: Int): Double =
        1.0 + (getComboBoostPercent(selectionCount) / 100.0)

    private fun toOdds(probability: Double): Double {
        val clampedProb = probability.coerceIn(0.01, 0.98)
        val rawOdds = 1.0 / (clampedProb * BOOKMAKER_MARGIN)
        val boundedOdds = rawOdds.coerceIn(1.04, 45.00)
        return (round(boundedOdds * 100) / 100.0)
    }
}

