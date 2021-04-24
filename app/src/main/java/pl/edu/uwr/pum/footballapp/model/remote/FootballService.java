package pl.edu.uwr.pum.footballapp.model.remote;

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import io.reactivex.rxjava3.core.Single;
import pl.edu.uwr.pum.footballapp.model.models.club.ModelTeam;
import pl.edu.uwr.pum.footballapp.model.models.competition.CompetitionList;
import pl.edu.uwr.pum.footballapp.model.models.match.ModelMatchList;
import pl.edu.uwr.pum.footballapp.model.models.table.CompetitionTableStandings;
import pl.edu.uwr.pum.footballapp.model.models.topscorer.ModelScorerList;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FootballService {

    private static final String BASE_URL = "https://api.football-data.org/v2/";

    private final FootballAPI api;

    public FootballService(){
        api = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
                .create(FootballAPI.class);
    }

    public Single<CompetitionList> getCompetitions(){
        return api.getCompetitions();
    }

    public Single<CompetitionTableStandings> getCompetitionsTable(int id) { return api.getTables(id);}
    public Single<ModelTeam> getTeam(int id){return api.getTeam(id);}
    public Single<ModelMatchList> getMatches(int id){return api.getMatches(id);}
    public Single<ModelMatchList> getCompetitionMatches(int id){return api.getCompetitonMatches(id);}
    public Single<ModelScorerList> getTopScorer(int id){return api.getTopScorer(id);}


}
