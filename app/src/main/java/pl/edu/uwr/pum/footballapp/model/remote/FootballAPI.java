package pl.edu.uwr.pum.footballapp.model.remote;

import io.reactivex.rxjava3.core.Single;
import pl.edu.uwr.pum.footballapp.model.models.club.ModelTeam;
import pl.edu.uwr.pum.footballapp.model.models.competition.CompetitionList;
import pl.edu.uwr.pum.footballapp.model.models.match.ModelMatchList;
import pl.edu.uwr.pum.footballapp.model.models.table.CompetitionTableStandings;
import pl.edu.uwr.pum.footballapp.model.models.topscorer.ModelScorerList;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface FootballAPI {

    @Headers("X-Auth-Token: 08c83e7e204b49d996f63912fa68423c")
    @GET("competitions?plan=TIER_ONE")
    Single<CompetitionList> getCompetitions();

    @Headers("X-Auth-Token: 08c83e7e204b49d996f63912fa68423c")
    @GET("competitions/{id}/standings")
    Single<CompetitionTableStandings> getTables(@Path(value = "id", encoded = false) int id);

    @Headers("X-Auth-Token: 08c83e7e204b49d996f63912fa68423c")
    @GET("teams/{id}")
    Single<ModelTeam> getTeam(@Path(value = "id", encoded = false) int id);


    @Headers("X-Auth-Token: 08c83e7e204b49d996f63912fa68423c")
    @GET("teams/{id}/matches")
    Single<ModelMatchList> getMatches(@Path(value = "id", encoded = false) int id);

    @Headers("X-Auth-Token: 08c83e7e204b49d996f63912fa68423c")
    @GET("competitions/{id}/matches")
    Single<ModelMatchList> getCompetitonMatches(@Path(value = "id", encoded = false) int id);

    @Headers("X-Auth-Token: 08c83e7e204b49d996f63912fa68423c")
    @GET("competitions/{id}/scorers")
    Single<ModelScorerList> getTopScorer(@Path(value = "id", encoded = false) int id);
}
