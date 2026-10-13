package com.example.kgcfootball.data.db

import com.example.kgcfootball.data.model.*

object DatabaseSeeder {

    fun getInitialLeagues(): List<LeagueEntity> = listOf(
        LeagueEntity(
            id = "kgc_champions",
            name = "Ligue d'Élite KGC",
            country = "Europe",
            flagEmoji = "🏆",
            currentRound = 1,
            totalRounds = 7,
            season = "2026/2027"
        ),
        LeagueEntity(
            id = "kgc_ligue1",
            name = "Ligue 1 Virtuelle",
            country = "France",
            flagEmoji = "🇫🇷",
            currentRound = 1,
            totalRounds = 7,
            season = "2026/2027"
        ),
        LeagueEntity(
            id = "kgc_premier",
            name = "Premier League KGC",
            country = "Angleterre",
            flagEmoji = "🏴󠁧󠁢󠁥󠁮󠁧󠁿",
            currentRound = 1,
            totalRounds = 7,
            season = "2026/2027"
        )
    )

    fun getInitialTeams(): List<TeamEntity> = listOf(
        // ==========================================
        // 1. Ligue d'Élite KGC (8 Équipes)
        // ==========================================
        TeamEntity(
            id = "madrid",
            leagueId = "kgc_champions",
            name = "KGC Madrid",
            shortName = "MAD",
            stadiumName = "Santiago Bernabéu",
            primaryColorHex = 0xFFFFFFFF,
            secondaryColorHex = 0xFF0038A8,
            attackRating = 93,
            defenseRating = 90,
            midfieldRating = 92,
            overallRating = 92,
            tacticStyle = "CONTRE_ATTAQUE",
            formation = "4-3-3",
            formString = "V,V,N,V,V",
            coachName = "Carlo Ancel",
            coachPersonality = "Sérénité & Tactique Légendaire",
            coachTacticalQuote = "Le football se gagne par le sang-froid et la confiance en ses cracks.",
            starPlayerName = "Kylian Mb.",
            recentH2hWins = 4,
            recentH2hDraws = 1,
            recentH2hLosses = 0,
            avgGoalsScored = 2.6f,
            avgGoalsConceded = 0.8f
        ),
        TeamEntity(
            id = "manchester",
            leagueId = "kgc_champions",
            name = "KGC City",
            shortName = "MCI",
            stadiumName = "Etihad Stadium",
            primaryColorHex = 0xFF6CABDD,
            secondaryColorHex = 0xFF1C2C5B,
            attackRating = 94,
            defenseRating = 91,
            midfieldRating = 93,
            overallRating = 93,
            tacticStyle = "TIKI_TAKA",
            formation = "4-3-3",
            formString = "V,V,V,N,V",
            coachName = "Pep Guardi",
            coachPersonality = "Génie Perfectionniste du Tiki-Taka",
            coachTacticalQuote = "Si nous conservons le ballon 75% du temps, le rival ne peut rien créer.",
            starPlayerName = "Erling Haa.",
            recentH2hWins = 3,
            recentH2hDraws = 2,
            recentH2hLosses = 0,
            avgGoalsScored = 2.8f,
            avgGoalsConceded = 0.7f
        ),
        TeamEntity(
            id = "paris",
            leagueId = "kgc_champions",
            name = "KGC Paris SG",
            shortName = "PAR",
            stadiumName = "Parc des Princes",
            primaryColorHex = 0xFF004170,
            secondaryColorHex = 0xFFDA291C,
            attackRating = 91,
            defenseRating = 87,
            midfieldRating = 89,
            overallRating = 89,
            tacticStyle = "GEGENPRESS",
            formation = "4-3-3",
            formString = "V,N,V,V,D",
            coachName = "Luis Enriq",
            coachPersonality = "Ultra-Pressing Possessif & Sans Concession",
            coachTacticalQuote = "Je veux 11 bêtes de course qui étouffent l'adversaire dès la première seconde.",
            starPlayerName = "Ousmane Demb.",
            recentH2hWins = 3,
            recentH2hDraws = 1,
            recentH2hLosses = 1,
            avgGoalsScored = 2.3f,
            avgGoalsConceded = 1.1f
        ),
        TeamEntity(
            id = "munich",
            leagueId = "kgc_champions",
            name = "KGC Munich",
            shortName = "BAY",
            stadiumName = "Allianz Arena",
            primaryColorHex = 0xFFDC052D,
            secondaryColorHex = 0xFF0066B2,
            attackRating = 92,
            defenseRating = 88,
            midfieldRating = 90,
            overallRating = 90,
            tacticStyle = "GEGENPRESS",
            formation = "4-2-3-1",
            formString = "V,V,D,V,V",
            coachName = "Vincent Komp",
            coachPersonality = "Intensité Rigoureuse & Domination",
            coachTacticalQuote = "Aucune baisse de régime tolérée. Nous attaquons en bloc uni.",
            starPlayerName = "Harry Ka.",
            recentH2hWins = 3,
            recentH2hDraws = 1,
            recentH2hLosses = 1,
            avgGoalsScored = 2.5f,
            avgGoalsConceded = 0.9f
        ),
        TeamEntity(
            id = "milan",
            leagueId = "kgc_champions",
            name = "KGC Internazionale",
            shortName = "INT",
            stadiumName = "San Siro",
            primaryColorHex = 0xFF001489,
            secondaryColorHex = 0xFF000000,
            attackRating = 88,
            defenseRating = 90,
            midfieldRating = 87,
            overallRating = 88,
            tacticStyle = "CATENACCIO",
            formation = "3-5-2",
            formString = "V,N,N,V,V",
            coachName = "Simone Inzag",
            coachPersonality = "Maître du 3-5-2 & Pistons Explosifs",
            coachTacticalQuote = "Verrouillage défensif impénétrable et transitions tranchantes à deux attaquants.",
            starPlayerName = "Lautaro Mart.",
            recentH2hWins = 2,
            recentH2hDraws = 2,
            recentH2hLosses = 1,
            avgGoalsScored = 1.9f,
            avgGoalsConceded = 0.7f
        ),
        TeamEntity(
            id = "london_blues",
            leagueId = "kgc_champions",
            name = "KGC London Blues",
            shortName = "CHE",
            stadiumName = "Stamford Bridge",
            primaryColorHex = 0xFF034694,
            secondaryColorHex = 0xFFFFFFFF,
            attackRating = 87,
            defenseRating = 86,
            midfieldRating = 87,
            overallRating = 87,
            tacticStyle = "EQUILIBRE",
            formation = "4-2-3-1",
            formString = "N,V,D,V,N",
            coachName = "Enzo Mares",
            coachPersonality = "Pédagogue Tactique & Jeu de Position",
            coachTacticalQuote = "Créer les supériorités numériques entre les lignes avec patience.",
            starPlayerName = "Cole Palm.",
            recentH2hWins = 2,
            recentH2hDraws = 1,
            recentH2hLosses = 2,
            avgGoalsScored = 1.8f,
            avgGoalsConceded = 1.2f
        ),
        TeamEntity(
            id = "casablanca",
            leagueId = "kgc_champions",
            name = "KGC Casablanca",
            shortName = "WAC",
            stadiumName = "Stade Mohammed V",
            primaryColorHex = 0xFFDC143C,
            secondaryColorHex = 0xFFFFFFFF,
            attackRating = 85,
            defenseRating = 84,
            midfieldRating = 83,
            overallRating = 84,
            tacticStyle = "CONTRE_ATTAQUE",
            formation = "4-4-2",
            formString = "V,V,V,N,D",
            coachName = "Faouzi Benz",
            coachPersonality = "Ferveur Passionnée & Discipline de Fer",
            coachTacticalQuote = "La grinta et la ferveur populaire nous portent vers les sommets.",
            starPlayerName = "Ayoub El K.",
            recentH2hWins = 3,
            recentH2hDraws = 1,
            recentH2hLosses = 1,
            avgGoalsScored = 1.7f,
            avgGoalsConceded = 0.9f
        ),
        TeamEntity(
            id = "lubumbashi",
            leagueId = "kgc_champions",
            name = "KGC Lubumbashi",
            shortName = "TPM",
            stadiumName = "Stade Mazembe",
            primaryColorHex = 0xFF000000,
            secondaryColorHex = 0xFFFFFFFF,
            attackRating = 84,
            defenseRating = 84,
            midfieldRating = 83,
            overallRating = 84,
            tacticStyle = "GEGENPRESS",
            formation = "4-3-3",
            formString = "V,N,V,D,V",
            coachName = "Lamine N'Diaye",
            coachPersonality = "Stratège Africain Expérimenté",
            coachTacticalQuote = "Le respect de l'adversaire commence par une intensité maximale de la 1ère à la 90ème.",
            starPlayerName = "Glody Lik.",
            recentH2hWins = 2,
            recentH2hDraws = 2,
            recentH2hLosses = 1,
            avgGoalsScored = 1.6f,
            avgGoalsConceded = 1.0f
        ),

        // ==========================================
        // 2. Ligue 1 Virtuelle (8 Équipes)
        // ==========================================
        TeamEntity(
            id = "marseille",
            leagueId = "kgc_ligue1",
            name = "KGC Marseille",
            shortName = "OM",
            stadiumName = "Orange Vélodrome",
            primaryColorHex = 0xFF2FAEEA,
            secondaryColorHex = 0xFFFFFFFF,
            attackRating = 87,
            defenseRating = 85,
            midfieldRating = 86,
            overallRating = 86,
            tacticStyle = "GEGENPRESS",
            formation = "4-3-3",
            formString = "V,D,V,V,N"
        ),
        TeamEntity(
            id = "lyon",
            leagueId = "kgc_ligue1",
            name = "KGC Lyon",
            shortName = "OL",
            stadiumName = "Groupama Stadium",
            primaryColorHex = 0xFF1B2E58,
            secondaryColorHex = 0xFFDA291C,
            attackRating = 85,
            defenseRating = 83,
            midfieldRating = 84,
            overallRating = 84,
            tacticStyle = "EQUILIBRE",
            formation = "4-2-3-1",
            formString = "N,V,D,V,D"
        ),
        TeamEntity(
            id = "monaco",
            leagueId = "kgc_ligue1",
            name = "KGC Monaco",
            shortName = "ASM",
            stadiumName = "Stade Louis-II",
            primaryColorHex = 0xFFD81E05,
            secondaryColorHex = 0xFFFFFFFF,
            attackRating = 86,
            defenseRating = 84,
            midfieldRating = 85,
            overallRating = 85,
            tacticStyle = "CONTRE_ATTAQUE",
            formation = "4-4-2",
            formString = "V,V,N,D,V"
        ),
        TeamEntity(
            id = "lille",
            leagueId = "kgc_ligue1",
            name = "KGC Lille",
            shortName = "LOSC",
            stadiumName = "Decathlon Arena",
            primaryColorHex = 0xFFE01E37,
            secondaryColorHex = 0xFF112244,
            attackRating = 84,
            defenseRating = 85,
            midfieldRating = 83,
            overallRating = 84,
            tacticStyle = "CATENACCIO",
            formation = "4-2-3-1",
            formString = "N,V,V,N,N"
        ),
        TeamEntity(
            id = "lens",
            leagueId = "kgc_ligue1",
            name = "KGC Lens",
            shortName = "RCL",
            stadiumName = "Stade Bollaert-Delelis",
            primaryColorHex = 0xFFFFD700,
            secondaryColorHex = 0xFFCC0000,
            attackRating = 84,
            defenseRating = 84,
            midfieldRating = 83,
            overallRating = 84,
            tacticStyle = "GEGENPRESS",
            formation = "3-4-3",
            formString = "V,N,V,D,V"
        ),
        TeamEntity(
            id = "rennes",
            leagueId = "kgc_ligue1",
            name = "KGC Rennes",
            shortName = "SRFC",
            stadiumName = "Roazhon Park",
            primaryColorHex = 0xFFE30613,
            secondaryColorHex = 0xFF000000,
            attackRating = 83,
            defenseRating = 82,
            midfieldRating = 83,
            overallRating = 83,
            tacticStyle = "EQUILIBRE",
            formation = "4-3-3",
            formString = "D,V,N,V,D"
        ),
        TeamEntity(
            id = "nice",
            leagueId = "kgc_ligue1",
            name = "KGC Nice",
            shortName = "OGC",
            stadiumName = "Allianz Riviera",
            primaryColorHex = 0xFFCC0000,
            secondaryColorHex = 0xFF000000,
            attackRating = 83,
            defenseRating = 84,
            midfieldRating = 82,
            overallRating = 83,
            tacticStyle = "CATENACCIO",
            formation = "4-3-3",
            formString = "N,N,V,V,D"
        ),
        TeamEntity(
            id = "paris_fc",
            leagueId = "kgc_ligue1",
            name = "KGC Paris FC",
            shortName = "PFC",
            stadiumName = "Stade Charléty",
            primaryColorHex = 0xFF002366,
            secondaryColorHex = 0xFFFFFFFF,
            attackRating = 82,
            defenseRating = 81,
            midfieldRating = 82,
            overallRating = 82,
            tacticStyle = "CONTRE_ATTAQUE",
            formation = "4-2-3-1",
            formString = "V,D,N,V,N"
        ),

        // ==========================================
        // 3. Premier League KGC (8 Équipes)
        // ==========================================
        TeamEntity(
            id = "arsenal",
            leagueId = "kgc_premier",
            name = "KGC Arsenal",
            shortName = "ARS",
            stadiumName = "Emirates Stadium",
            primaryColorHex = 0xFFEF0107,
            secondaryColorHex = 0xFF063672,
            attackRating = 91,
            defenseRating = 92,
            midfieldRating = 90,
            overallRating = 91,
            tacticStyle = "TIKI_TAKA",
            formation = "4-3-3",
            formString = "V,V,V,D,V"
        ),
        TeamEntity(
            id = "liverpool",
            leagueId = "kgc_premier",
            name = "KGC Liverpool",
            shortName = "LIV",
            stadiumName = "Anfield",
            primaryColorHex = 0xFFC8102E,
            secondaryColorHex = 0xFF00B2A9,
            attackRating = 92,
            defenseRating = 89,
            midfieldRating = 90,
            overallRating = 90,
            tacticStyle = "GEGENPRESS",
            formation = "4-3-3",
            formString = "V,V,N,V,V"
        ),
        TeamEntity(
            id = "man_red",
            leagueId = "kgc_premier",
            name = "KGC Manchester Red",
            shortName = "MUN",
            stadiumName = "Old Trafford",
            primaryColorHex = 0xFFDA291C,
            secondaryColorHex = 0xFF000000,
            attackRating = 88,
            defenseRating = 86,
            midfieldRating = 88,
            overallRating = 87,
            tacticStyle = "CONTRE_ATTAQUE",
            formation = "4-2-3-1",
            formString = "V,D,V,N,V"
        ),
        TeamEntity(
            id = "tottenham",
            leagueId = "kgc_premier",
            name = "KGC Tottenham",
            shortName = "TOT",
            stadiumName = "Tottenham Hotspur Stadium",
            primaryColorHex = 0xFF132257,
            secondaryColorHex = 0xFFFFFFFF,
            attackRating = 88,
            defenseRating = 85,
            midfieldRating = 87,
            overallRating = 87,
            tacticStyle = "GEGENPRESS",
            formation = "4-3-3",
            formString = "V,V,D,V,D"
        ),
        TeamEntity(
            id = "aston_villa",
            leagueId = "kgc_premier",
            name = "KGC Aston Villa",
            shortName = "AVL",
            stadiumName = "Villa Park",
            primaryColorHex = 0xFF670E36,
            secondaryColorHex = 0xFF95BFE5,
            attackRating = 87,
            defenseRating = 86,
            midfieldRating = 86,
            overallRating = 86,
            tacticStyle = "CONTRE_ATTAQUE",
            formation = "4-4-2",
            formString = "N,V,V,N,V"
        ),
        TeamEntity(
            id = "newcastle",
            leagueId = "kgc_premier",
            name = "KGC Newcastle",
            shortName = "NEW",
            stadiumName = "St James' Park",
            primaryColorHex = 0xFF241F20,
            secondaryColorHex = 0xFFFFFFFF,
            attackRating = 87,
            defenseRating = 86,
            midfieldRating = 85,
            overallRating = 86,
            tacticStyle = "EQUILIBRE",
            formation = "4-3-3",
            formString = "V,D,N,V,V"
        ),
        TeamEntity(
            id = "chelsea_eng",
            leagueId = "kgc_premier",
            name = "KGC Chelsea FC",
            shortName = "CFC",
            stadiumName = "Stamford Bridge London",
            primaryColorHex = 0xFF034694,
            secondaryColorHex = 0xFFEE242C,
            attackRating = 87,
            defenseRating = 85,
            midfieldRating = 86,
            overallRating = 86,
            tacticStyle = "EQUILIBRE",
            formation = "4-2-3-1",
            formString = "N,V,V,D,N"
        ),
        TeamEntity(
            id = "west_ham",
            leagueId = "kgc_premier",
            name = "KGC West Ham",
            shortName = "WHU",
            stadiumName = "London Stadium",
            primaryColorHex = 0xFF7A263A,
            secondaryColorHex = 0xFF1BB1E7,
            attackRating = 84,
            defenseRating = 83,
            midfieldRating = 84,
            overallRating = 84,
            tacticStyle = "CATENACCIO",
            formation = "4-2-3-1",
            formString = "D,V,N,D,V"
        )
    )

    fun getInitialPlayers(): List<PlayerEntity> = listOf(
        // Madrid
        PlayerEntity("p1", "madrid", "K. Mbappé (KGC)", 9, "ATT", 94, 8, 3),
        PlayerEntity("p2", "madrid", "Vinícius Jr.", 7, "ATT", 92, 6, 5),
        PlayerEntity("p3", "madrid", "J. Bellingham", 5, "MID", 91, 5, 4),
        PlayerEntity("p4", "madrid", "F. Valverde", 8, "MID", 89, 2, 3),
        PlayerEntity("p5", "madrid", "T. Courtois", 1, "GK", 90, 0, 0),

        // Manchester
        PlayerEntity("p6", "manchester", "E. Haaland (KGC)", 9, "ATT", 94, 9, 1),
        PlayerEntity("p7", "manchester", "K. De Bruyne", 17, "MID", 92, 3, 8),
        PlayerEntity("p8", "manchester", "P. Foden", 47, "MID", 89, 5, 4),
        PlayerEntity("p9", "manchester", "Rodri", 16, "MID", 92, 2, 3),
        PlayerEntity("p10", "manchester", "Ederson", 31, "GK", 88, 0, 0),

        // Paris
        PlayerEntity("p11", "paris", "O. Dembélé", 10, "ATT", 88, 4, 6),
        PlayerEntity("p12", "paris", "B. Barcola", 29, "ATT", 86, 5, 3),
        PlayerEntity("p13", "paris", "Vitinha", 17, "MID", 88, 3, 5),
        PlayerEntity("p14", "paris", "Marquinhos", 5, "DEF", 87, 1, 0),
        PlayerEntity("p15", "paris", "G. Donnarumma", 99, "GK", 89, 0, 0),

        // Munich
        PlayerEntity("p16", "munich", "H. Kane (KGC)", 9, "ATT", 92, 8, 4),
        PlayerEntity("p17", "munich", "J. Musiala", 42, "MID", 90, 6, 5),
        PlayerEntity("p18", "munich", "M. Olise", 17, "ATT", 87, 4, 4),
        PlayerEntity("p19", "munich", "M. Neuer", 1, "GK", 88, 0, 0),

        // Casablanca
        PlayerEntity("p20", "casablanca", "A. El Kaabi", 9, "ATT", 85, 6, 2),
        PlayerEntity("p21", "casablanca", "Y. Jabrane", 5, "MID", 83, 1, 3),
        PlayerEntity("p22", "casablanca", "A. Tagnaouti", 26, "GK", 84, 0, 0),

        // Lubumbashi
        PlayerEntity("p23", "lubumbashi", "J. Muleka", 9, "ATT", 84, 7, 1),
        PlayerEntity("p24", "lubumbashi", "T. Mwape", 14, "DEF", 82, 0, 1),
        PlayerEntity("p25", "lubumbashi", "R. Kidiaba (Légende)", 1, "GK", 85, 0, 0),

        // Marseille
        PlayerEntity("p26", "marseille", "M. Greenwood", 10, "ATT", 87, 7, 2),
        PlayerEntity("p27", "marseille", "P. Højbjerg", 23, "MID", 86, 2, 4),
        PlayerEntity("p28", "marseille", "G. Rulli", 1, "GK", 85, 0, 0),

        // Lyon
        PlayerEntity("p29", "lyon", "A. Lacazette", 10, "ATT", 85, 6, 3),
        PlayerEntity("p30", "lyon", "R. Cherki", 18, "MID", 85, 3, 5),

        // Arsenal
        PlayerEntity("p31", "arsenal", "B. Saka", 7, "ATT", 91, 6, 6),
        PlayerEntity("p32", "arsenal", "M. Ødegaard", 8, "MID", 90, 4, 7),
        PlayerEntity("p33", "arsenal", "D. Raya", 22, "GK", 89, 0, 0),

        // Liverpool
        PlayerEntity("p34", "liverpool", "M. Salah", 11, "ATT", 92, 9, 5),
        PlayerEntity("p35", "liverpool", "V. van Dijk", 4, "DEF", 91, 2, 0),
        PlayerEntity("p36", "liverpool", "Alisson", 1, "GK", 90, 0, 0)
    )

    /**
     * Génère un calendrier complet round-robin de 7 journées x 4 matchs = 28 matchs par championnat.
     * Avec programmes horaires cohérents et décalés (Minimum 2 min, max 8 min) pour une disponibilité continue du direct !
     */
    fun getInitialFixtures(): List<MatchFixtureEntity> {
        val fixtures = mutableListOf<MatchFixtureEntity>()

        // 1. Champions League (8 équipes)
        val championsTeams = listOf("madrid", "manchester", "paris", "munich", "milan", "london_blues", "casablanca", "lubumbashi")
        fixtures.addAll(generateLeagueSchedule(
            leagueId = "kgc_champions",
            teams = championsTeams,
            initialLiveMatchIndex = 0 // Match 1 déjà en direct (minute 38') !
        ))

        // 2. Ligue 1 (8 équipes)
        val ligue1Teams = listOf("marseille", "lyon", "monaco", "lille", "lens", "rennes", "nice", "paris_fc")
        fixtures.addAll(generateLeagueSchedule(
            leagueId = "kgc_ligue1",
            teams = ligue1Teams,
            initialLiveMatchIndex = null,
            kickoffDelayBase = 110 // Premier match dans ~1m50s !
        ))

        // 3. Premier League (8 équipes)
        val premierTeams = listOf("arsenal", "liverpool", "man_red", "tottenham", "aston_villa", "newcastle", "chelsea_eng", "west_ham")
        fixtures.addAll(generateLeagueSchedule(
            leagueId = "kgc_premier",
            teams = premierTeams,
            initialLiveMatchIndex = 0, // Match 1 en direct à la 62' !
            liveMinute = 62,
            kickoffDelayBase = 160
        ))

        return fixtures
    }

    /**
     * Algorithme Round-Robin standard pour 8 équipes sur 7 journées.
     * Garantit que chaque équipe joue exactement 1 match par journée.
     */
    fun generateLeagueSchedule(
        leagueId: String,
        teams: List<String>,
        initialLiveMatchIndex: Int? = null,
        liveMinute: Int = 38,
        kickoffDelayBase: Int = 120
    ): List<MatchFixtureEntity> {
        val schedule = mutableListOf<MatchFixtureEntity>()
        val totalTeams = teams.size
        require(totalTeams == 8) { "Schedule requires 8 teams" }

        // Paires standard du Round-Robin pour 8 équipes
        val roundPairings = listOf(
            // Journée 1
            listOf(Pair(0, 7), Pair(1, 6), Pair(2, 5), Pair(3, 4)),
            // Journée 2
            listOf(Pair(0, 6), Pair(7, 5), Pair(1, 4), Pair(2, 3)),
            // Journée 3
            listOf(Pair(0, 5), Pair(6, 4), Pair(7, 3), Pair(1, 2)),
            // Journée 4
            listOf(Pair(0, 4), Pair(5, 3), Pair(6, 2), Pair(7, 1)),
            // Journée 5
            listOf(Pair(0, 3), Pair(4, 2), Pair(5, 1), Pair(6, 7)),
            // Journée 6
            listOf(Pair(0, 2), Pair(3, 1), Pair(4, 7), Pair(5, 6)),
            // Journée 7
            listOf(Pair(0, 1), Pair(2, 7), Pair(3, 6), Pair(4, 5))
        )

        val kickoffSlots = listOf("15:00", "15:03", "15:06", "15:09")

        for (roundIndex in 0 until 7) {
            val roundNumber = roundIndex + 1
            val pairings = roundPairings[roundIndex]

            for (matchIndex in pairings.indices) {
                val pair = pairings[matchIndex]
                val homeId = teams[pair.first]
                val awayId = teams[pair.second]
                val fixId = "fix_${leagueId}_r${roundNumber}_m${matchIndex + 1}"

                // Calcul du programme horaire réaliste
                // En Journée 1 : décalage échelonné (ex: 2 min, 4 min, 6 min, 8 min)
                // Pour les journées ultérieures : espacées pour analyse
                val matchKickoffOffsetSeconds = (matchIndex * 120) + kickoffDelayBase + (roundIndex * 600)
                val isInitialLive = (roundNumber == 1 && initialLiveMatchIndex == matchIndex)

                val status = if (isInitialLive) "LIVE" else "SCHEDULED"
                val minute = if (isInitialLive) liveMinute else 0
                val homeScore = if (isInitialLive) (if (matchIndex == 0 && leagueId == "kgc_premier") 2 else 1) else 0
                val awayScore = if (isInitialLive) (if (matchIndex == 0 && leagueId == "kgc_premier") 1 else 0) else 0
                val eventText = if (isInitialLive) {
                    "Match en cours ($minute') • Cotes Live actives"
                } else {
                    "Coup d'envoi prévu à ${kickoffSlots[matchIndex]} • Paris ouverts"
                }

                val countdown = if (isInitialLive) 0 else ((matchIndex * 120) + kickoffDelayBase).coerceIn(90, 520)

                schedule.add(
                    MatchFixtureEntity(
                        id = fixId,
                        leagueId = leagueId,
                        round = roundNumber,
                        homeTeamId = homeId,
                        awayTeamId = awayId,
                        status = status,
                        minute = minute,
                        homeScore = homeScore,
                        awayScore = awayScore,
                        lastEventText = eventText,
                        scheduledKickoffTime = kickoffSlots[matchIndex],
                        countdownSecondsRemaining = countdown,
                        ballX = if (isInitialLive) 0.65f else 0.5f,
                        ballY = if (isInitialLive) 0.42f else 0.5f,
                        playZoneDescription = if (isInitialLive) "Phase d'attaque active" else "Échauffement d'avant-match",
                        kickoffTimestamp = if (isInitialLive) System.currentTimeMillis() - (liveMinute * 60_000L) else System.currentTimeMillis() + (countdown * 1000L),
                        lastSimulatedTimestamp = System.currentTimeMillis(),
                        simulationMode = "VIRTUEL_EXPRESS"
                    )
                )
            }
        }

        return schedule
    }
}
