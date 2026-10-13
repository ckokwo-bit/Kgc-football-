package com.example.kgcfootball.data.model

import java.util.UUID
import kotlin.random.Random

data class NewsArticle(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String, // MERCATO, RÉACTION, VAR, INFIRMERIE, TACTIQUE, EXCLUSIF, CLASSEMENT
    val timeAgo: String,
    val source: String = "KGC Foot Média",
    val readTimeMinutes: Int = 2,
    val fullContent: String
)

object NewsProvider {

    fun getInitialNews(): List<NewsArticle> = listOf(
        NewsArticle(
            id = "news_1",
            title = "🔥 KGC Madrid : Mbappé et Vinícius Jr font trembler les défenses européennes",
            subtitle = "Le duo d'attaque madrilène affiche une efficacité record en ce début de championnat.",
            category = "RÉACTION",
            timeAgo = "Il y a 8 min",
            source = "KGC Sport Live",
            readTimeMinutes = 3,
            fullContent = "Après une démonstration offensive sensationnelle, Kylian Mbappé s'est confié au micro de KGC Foot : 'L'alchimie sur le terrain est parfaite. Notre objectif est clair : remporter le titre de la Ligue d'Élite KGC et faire vibrer tous nos supporters. Nous sommes prêts pour tous les chocs à venir ! Le coach nous donne une liberté totale dans le dernier tiers.'"
        ),
        NewsArticle(
            id = "news_2",
            title = "🚨 Mercato KGC : Une offre monumentale de 220M KGC préparée pour Erling Haaland",
            subtitle = "Les géants européens s'activent en coulisses pour s'attacher les services du cyborg norvégien.",
            category = "MERCATO",
            timeAgo = "Il y a 22 min",
            source = "KGC Foot Transferts",
            readTimeMinutes = 2,
            fullContent = "Selon nos informations exclusives, plusieurs états-majors préparent des offres stratosphériques pour tenter d'arracher Haaland à KGC City. L'agent du joueur s'est réuni hier soir avec les émissaires madrilènes et parisiens dans un grand palace. Le joueur a déclaré rester concentré sur les prochaines échéances de championnat."
        ),
        NewsArticle(
            id = "news_3",
            title = "🖥️ Polémique Arbitrage : Le coach de KGC Paris s'emporte contre la VAR",
            subtitle = "'Une décision incompréhensible qui change la physionomie de la rencontre.'",
            category = "VAR",
            timeAgo = "Il y a 45 min",
            source = "Conférence d'Après-Match",
            readTimeMinutes = 2,
            fullContent = "En conférence de presse d'après-match, l'entraîneur parisien n'a pas mâché ses mots : 'Le but annulé à la vidéo pour un hors-jeu de deux millimètres est frustrant. Les arbitres doivent laisser vivre le spectacle. Nous avions totalement la maîtrise tactique. Mais nous allons rebondir dès la prochaine journée !'"
        ),
        NewsArticle(
            id = "news_4",
            title = "🏆 Course au Ballon d'Or KGC : La lutte acharnée entre les stars mondiales",
            subtitle = "Qui succédera au palmarès ? Les statistiques individuelles affolent les compteurs.",
            category = "EXCLUSIF",
            timeAgo = "Il y a 1h",
            source = "Chronique Tactique",
            readTimeMinutes = 4,
            fullContent = "Avec l'enchaînement des journées virtuelles, le classement du Ballon d'Or KGC se resserre. Entre les buteurs d'élite, les maîtres à jouer du milieu et les gardiens héroïques, la fin de saison s'annonce grandiose. Les bookmakers placent désormais Jude Bellingham et Kylian Mbappé au coude-à-coude avec Erling Haaland."
        ),
        NewsArticle(
            id = "news_5",
            title = "📊 Tactique & Data : Pourquoi le Gegenpress de KGC Munich étouffe ses adversaires",
            subtitle = "Analyse approfondie du bloc haut et de la récupération éclair en moins de 5 secondes.",
            category = "TACTIQUE",
            timeAgo = "Il y a 2h",
            source = "KGC Lab Data",
            readTimeMinutes = 3,
            fullContent = "Avec une intensité physique décuplée et un pressing asphyxiant dès la perte du ballon, la méthode bavaroise force les erreurs défensives adverses et génère un nombre record d'occasions de but par match. Les statistiques de xG (Expected Goals) indiquent une domination de 2.85 buts attendus par rencontre."
        ),
        NewsArticle(
            id = "news_6",
            title = "🚑 Point Infirmerie : Les nouvelles rassurantes pour les cadres de KGC City",
            subtitle = "Le staff médical confirme un retour rapide pour les prochaines journées décisives.",
            category = "INFIRMERIE",
            timeAgo = "Il y a 3h",
            source = "Communiqué Médical Officiel",
            readTimeMinutes = 2,
            fullContent = "Sorti par précaution lors du dernier choc, le maître à jouer du milieu de terrain a repris la course aujourd'hui sur les terrains d'entraînement. Les examens cliniques n'ont révélé aucune lésion ligamentaire. Il sera pleinement opérationnel dès le coup d'envoi du prochain grand rendez-vous de championnat."
        ),
        NewsArticle(
            id = "news_7",
            title = "⚔️ KGC Arsenal vs KGC Liverpool : Les coulisses tactiques du choc au sommet",
            subtitle = "Mikel Arteta et Arne Slot ont peaufiné leur plan anti-contre-attaque.",
            category = "TACTIQUE",
            timeAgo = "Il y a 3h 30",
            source = "Inside Stadium KGC",
            readTimeMinutes = 3,
            fullContent = "À quelques heures du duel très attendu entre Arsenal et Liverpool, les analystes de KGC Foot décortiquent le duel du milieu : Declan Rice face à Mac Allister. La bataille des transitions offensives sera déterminante pour faire sauter le verrou adverse."
        ),
        NewsArticle(
            id = "news_8",
            title = "💰 Paris Sportifs : Un parieur empoche 850 000 KGC sur un combiné de 6 matchs !",
            subtitle = "Cote totale astronomique validée grâce à un but salvateur à la 92ème minute.",
            category = "EXCLUSIF",
            timeAgo = "Il y a 4h",
            source = "KGC Bookmaker Gazette",
            readTimeMinutes = 2,
            fullContent = "L'exploit de la semaine ! Avec une mise de départ de 2 500 KGC, un utilisateur inspiré a combiné 6 victoires avec le Booster Combiné +50%. Grâce à l'égalisation inespérée d'Osimhen dans le temps additionnel, le ticket s'est transformé en un jackpot mémorable de 850 000 KGC."
        ),
        NewsArticle(
            id = "news_9",
            title = "🧤 Masterclass dans les cages : Les 9 arrêts décisifs de Thibaut Courtois",
            subtitle = "Le gardien géant a écoeuré les attaquants adverses d'une détente réflexe féline.",
            category = "RÉACTION",
            timeAgo = "Il y a 5h",
            source = "KGC Gardiens d'Élite",
            readTimeMinutes = 2,
            fullContent = "'C'est sans doute l'un des meilleurs matchs de ma carrière en KGC League', a réagi Courtois en zone mixte. Élu Homme du Match avec une note stratosphérique de 9.4/10, il consolide sa première place pour le Gant d'Or de la saison."
        ),
        NewsArticle(
            id = "news_10",
            title = "🌪️ KGC Marseille en feu : Le Vélodrome en ébullition avant le Classique",
            subtitle = "Les supporters préparent un tifo monumental et une ambiance de corrida.",
            category = "RÉACTION",
            timeAgo = "Il y a 6h",
            source = "La Tribune KGC",
            readTimeMinutes = 2,
            fullContent = "La ferveur monte d'un cran dans la cité phocéenne. Avec une série de trois victoires consécutives, l'OM aborde le choc face au rival parisien en pleine confiance. Le vestiaire réclame une intensité maximale dès la première seconde de jeu."
        ),
        NewsArticle(
            id = "news_11",
            title = "📌 Débrief Arbitrage : Le recours au hors-jeu semi-automatique validé à 100%",
            subtitle = "La direction technique de l'arbitrage salue la précision chirurgicale de la technologie.",
            category = "VAR",
            timeAgo = "Il y a 7h",
            source = "Commission Arbitrale KGC",
            readTimeMinutes = 3,
            fullContent = "Les caméras ultra-haute vitesse et les capteurs installés dans le ballon connecté ont permis de statuer sur 14 situations litigieuses lors de la dernière journée en un temps record moyen de 18 secondes. Une avancée majeure pour la fluidité du jeu."
        ),
        NewsArticle(
            id = "news_12",
            title = "💎 La pépite Lamine Yamal affole la planète football : 'Je ne ressens aucune pression'",
            subtitle = "À tout juste 18 ans, l'ailier virevoltant régale par ses dribbles déroutants.",
            category = "EXCLUSIF",
            timeAgo = "Il y a 8h",
            source = "Interview Exclusive KGC",
            readTimeMinutes = 3,
            fullContent = "Dans un entretien exclusif accordé à notre rédaction, le jeune prodige revient sur son ascension fulgurante : 'Je joue comme au quartier, avec le sourire et l'envie de donner du plaisir. Quand le ballon arrive sur mon pied gauche, je sais exactement où va frapper le tir.'"
        ),
        NewsArticle(
            id = "news_13",
            title = "🔄 Révolution tactique : Le retour en force du Catenaccio moderne en Italie",
            subtitle = "L'Inter et la Juventus blindent leur axe défensif avec des blocs bas impénétrables.",
            category = "TACTIQUE",
            timeAgo = "Il y a 9h",
            source = "Cahiers du Football KGC",
            readTimeMinutes = 3,
            fullContent = "Moins de 0.6 but concédé par match en moyenne : les formations transalpines réinventent la rigueur défensive en combinant couverture mutuelle ultra-serrée et contre-attaques foudroyantes guidées par des ailiers supersoniques."
        ),
        NewsArticle(
            id = "news_14",
            title = "🎯 Soulier d'Or KGC : Qui pour arrêter l'armada offensive de Manchester ?",
            subtitle = "Déjà 14 buts inscrits par l'attaque mancunienne en seulement quelques journées.",
            category = "CLASSEMENT",
            timeAgo = "Il y a 10h",
            source = "Stats Zone KGC",
            readTimeMinutes = 2,
            fullContent = "Avec une moyenne vertigineuse de 2.6 buts par rencontre, Manchester domine le tableau des attaques les plus prolifiques. Le pressing haut initié par Bernardo Silva et De Bruyne permet de récupérer le cuir dans les 30 derniers mètres adverses."
        ),
        NewsArticle(
            id = "news_15",
            title = "⚡ Coup de théâtre : Un carton rouge direct dès la 14ème minute change tout !",
            subtitle = "Réduite à dix dès l'entame, l'équipe visiteuse a tenu un siège héroïque avant de craquer.",
            category = "RÉACTION",
            timeAgo = "Il y a 11h",
            source = "Chronique de match",
            readTimeMinutes = 2,
            fullContent = "Un tacle mal maîtrisé les deux pieds décollés a valu une expulsion immédiate. Malgré une résistance acharnée du bloc à 10, la fatigue physique en seconde période a fini par ouvrir des brèches fatales dans la muraille défensive."
        ),
        NewsArticle(
            id = "news_16",
            title = "🔥 Mercato KGC : Liverpool cible un milieu défensif brésilien de classe mondiale",
            subtitle = "Une clause libératoire de 95M KGC prête à être activée dès l'ouverture de la fenêtre.",
            category = "MERCATO",
            timeAgo = "Il y a 12h",
            source = "Transfert Radar KGC",
            readTimeMinutes = 2,
            fullContent = "Les recruteurs des Reds ont été aperçus hier en tribunes pour superviser le jeune international brésilien. Doté d'un abattage monstrueux et d'une relance au millimètre, il constituerait la clé de voûte du milieu à Anfield."
        ),
        NewsArticle(
            id = "news_17",
            title = "🎙️ Pep Guardiola : 'Chaque détail compte, la concentration doit être de 100%'",
            subtitle = "Le technicien catalan exige une intensité constante même lors des temps faibles.",
            category = "RÉACTION",
            timeAgo = "Il y a 14h",
            source = "Conférence d'Avant-Match",
            readTimeMinutes = 3,
            fullContent = "'Si nous perdons un mètre de marquage sur les coups de pied arrêtés, nous sommes immédiatement punis. En KGC Champions League, les huit équipes possèdent des attaquants capables de faire basculer une saison sur une seule touche de balle.'"
        ),
        NewsArticle(
            id = "news_18",
            title = "📈 Analyse Cotes : Pourquoi la 'Double Chance' offre le meilleur ratio de rentabilité",
            subtitle = "Les experts en probabilités décortiquent les pièges des cotes à 1.25 et les valeurs sûres.",
            category = "TACTIQUE",
            timeAgo = "Il y a 16h",
            source = "KGC Pronos & Stats",
            readTimeMinutes = 3,
            fullContent = "Dans un championnat aussi homogène où les outsiders bousculent régulièrement les favoris, sécuriser ses paris combinés avec le marché Double Chance (1X ou X2) permet d'optimiser l'espérance mathématique de gain sur le long terme."
        )
    )

    /**
     * Génère dynamiquement un article d'actualité d'après-match réaliste et sur mesure
     * basé sur le score réel et les événements du match qui vient de se terminer.
     */
    fun generatePostMatchArticle(
        fixture: MatchFixtureEntity,
        homeTeam: TeamEntity,
        awayTeam: TeamEntity,
        topScorerName: String?
    ): NewsArticle {
        val homeScore = fixture.homeScore
        val awayScore = fixture.awayScore
        val totalGoals = homeScore + awayScore

        val isDraw = homeScore == awayScore
        val winnerTeam = if (homeScore > awayScore) homeTeam else awayTeam
        val loserTeam = if (homeScore > awayScore) awayTeam else homeTeam
        val winScore = maxOf(homeScore, awayScore)
        val loseScore = minOf(homeScore, awayScore)

        val articleId = "post_match_${fixture.id}_${System.currentTimeMillis()}"

        return when {
            isDraw && totalGoals >= 4 -> {
                NewsArticle(
                    id = articleId,
                    title = "🍿 Match d'anthologie : Festival offensif ($homeScore - $awayScore) entre ${homeTeam.name} et ${awayTeam.name} !",
                    subtitle = "Un scénario fou à rebondissements qui a tenu les spectateurs en haleine jusqu'au bout.",
                    category = "RÉACTION",
                    timeAgo = "À l'instant",
                    source = "KGC Direct Live",
                    readTimeMinutes = 3,
                    fullContent = "Quel spectacle grandiose sur la pelouse ! Les deux formations se sont rendues coup pour coup dans une rencontre à couper le souffle terminée sur le score de $homeScore à $awayScore. Les entraîneurs ont salué la bravoure de leurs joueurs : 'C'est pour ce genre d'émotion qu'on aime le football virtuel KGC !'"
                )
            }
            isDraw && totalGoals == 0 -> {
                NewsArticle(
                    id = articleId,
                    title = "🧱 Mur défensif impénétrable : 0-0 tactique entre ${homeTeam.name} et ${awayTeam.name}",
                    subtitle = "Les gardiens ont sorti le grand jeu pour préserver leur cage inviolée.",
                    category = "TACTIQUE",
                    timeAgo = "À l'instant",
                    source = "KGC Analyse Terrain",
                    readTimeMinutes = 2,
                    fullContent = "Une véritable partie d'échecs tactique sur le terrain. Malgré plusieurs assauts en fin de rencontre et ${fixture.homeShots + fixture.awayShots} tirs au total, ni ${homeTeam.name} ni ${awayTeam.name} n'ont trouvé la faille. Un point précieux partagé entre les deux prétendants."
                )
            }
            winScore - loseScore >= 3 -> {
                NewsArticle(
                    id = articleId,
                    title = "💥 Démonstration de force : ${winnerTeam.name} écrase ${loserTeam.name} ($winScore - $loseScore) !",
                    subtitle = "Une supériorité éclatante du coup d'envoi au coup de sifflet final.",
                    category = "RÉACTION",
                    timeAgo = "À l'instant",
                    source = "KGC Édition Spéciale",
                    readTimeMinutes = 3,
                    fullContent = "Les protégés de ${winnerTeam.name} ont livré une prestation collective magistrale. Avec une possession écrasante et un pressing étouffant, ils ont littéralement surclassé ${loserTeam.name}. ${topScorerName?.let { "Menés par un $it étincelant, " } ?: ""}ils envoient un message très clair à tous leurs rivaux pour le titre !"
                )
            }
            fixture.minute >= 88 && (winScore - loseScore == 1) -> {
                NewsArticle(
                    id = articleId,
                    title = "🔥 Victoire à l'arraché : ${winnerTeam.name} crucifie ${loserTeam.name} ($winScore - $loseScore) sur le fil !",
                    subtitle = "Le suspense a duré jusqu'à la dernière seconde du temps additionnel.",
                    category = "RÉACTION",
                    timeAgo = "À l'instant",
                    source = "KGC Flash Info",
                    readTimeMinutes = 2,
                    fullContent = "Une fin de match irrespirable ! Alors que le match semblait se diriger vers un nul logique, ${winnerTeam.name} a trouvé les ressources mentales pour inscrire le but de la délivrance. Les tribunes ont exulté dans une ambiance survoltée."
                )
            }
            else -> {
                NewsArticle(
                    id = articleId,
                    title = "✅ Succès mérité : ${winnerTeam.name} prend les 3 points face à ${loserTeam.name} ($winScore - $loseScore)",
                    subtitle = "Une maîtrise tactique récompensée au classement général.",
                    category = "RÉACTION",
                    timeAgo = "À l'instant",
                    source = "KGC Gazette",
                    readTimeMinutes = 2,
                    fullContent = "Grâce à une discipline de fer et un réalisme clinique dans la surface adverse, ${winnerTeam.name} signe une victoire précieuse contre ${loserTeam.name}. Cette performance leur permet de conforter leur position dans la course au titre de champion KGC."
                )
            }
        }
    }
}
