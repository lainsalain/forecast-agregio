# forecast-agregio
KATA Agregio : Forecast

## Objectif
- Construction d'une API REST avec pagination permettant de récupérer les données de prévision dans un intervalle **[start_date_time, end_date_time[**.
- Ajout d'une route supplémentaire permettant de récupérer la moyenne des valeurs de prévision pour un périmètre donné et une date-time donnée.


## Contraintes 
- Le tri des lignes de la table ne peut être effectué que sur la dimension "time".
- Il n'erst pas possible d'utiliser les méta-donnéees, méta-colonnes, fonctions et macros spécifiques des bases de PostgreSQL.
- Limite de lignes retournées par chaque appel tel que **LIMIT = 200**.

