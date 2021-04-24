package pl.edu.uwr.pum.footballapp.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.edu.uwr.pum.footballapp.model.Competition;
import pl.edu.uwr.pum.footballapp.model.local.FootballDAO;
import pl.edu.uwr.pum.footballapp.model.local.FootballRoom;
import pl.edu.uwr.pum.footballapp.model.models.match.ModelMatch;
import pl.edu.uwr.pum.footballapp.model.models.match.ModelMatchList;
import pl.edu.uwr.pum.footballapp.model.models.table.CompetitionTable;
import pl.edu.uwr.pum.footballapp.model.remote.FootballService;
import pl.edu.uwr.pum.footballapp.util.Util;

public class MatchViewModel extends AndroidViewModel {

    public MutableLiveData<List<ModelMatch>> matches =
            new MutableLiveData<List<ModelMatch>>();
    public MutableLiveData<Boolean> CompetitionLoadError =
            new MutableLiveData<>();
    public MutableLiveData<Boolean> CompetitionLoading =
            new MutableLiveData<>();
    List<ModelMatch> comps = new ArrayList<>();
    private static final Object LOCK = new Object();
    private boolean finished;

    private final FootballService service = new FootballService();
    public Disposable disposable;

    private final ExecutorService databaseExecutor =
            Executors.newFixedThreadPool(Util.NUM_OF_THREADS);

    public MatchViewModel(@NonNull Application application) {
        super(application);
    }


    public void refreshListFromRemote(int id,boolean calledFromTeam){
        fetchRemote(id,calledFromTeam);
    }

    private void fetchRemote(int id,boolean calledFromTeam){
        CompetitionLoading.setValue(true);

        if(calledFromTeam) {
            disposable = service.getMatches(id)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<ModelMatchList>() {
                        @Override
                        public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull ModelMatchList compList) {
                            procces_data(compList,true);
                        }

                        @Override
                        public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                            CompetitionLoadError.setValue(true);
                            CompetitionLoading.setValue(false);
                            fetchLocal(id, calledFromTeam);
                            e.printStackTrace();
                        }
                    });
        }
        else
        {
            disposable = service.getCompetitionMatches(id)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<ModelMatchList>() {
                        @Override
                        public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull ModelMatchList compList) {
                            procces_data(compList,false);
                        }

                        @Override
                        public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                            CompetitionLoadError.setValue(true);
                            CompetitionLoading.setValue(false);
                            fetchLocal(id, calledFromTeam);
                            e.printStackTrace();
                        }
                    });
        }
    }


    private void procces_data(ModelMatchList compList, boolean calledFromTeam)
    {
        int index = 0;
        finished = false;
        for(ModelMatch d : compList.matches)
        {
            d.awayID = d.awayTeam.id;
            d.homeID = d.homeTeam.id;
            if(calledFromTeam)
                d.competition_id = d.competition.id;
            else
                d.competition_id = compList.competition.id;
            d.homeName = d.homeTeam.name;
            d.awayName = d.awayTeam.name;
            if(calledFromTeam)
                d.competitionName = d.competition.name;
            else
                d.competitionName = compList.competition.name;
            String a =  d.score.fullTime.homeTeam;
            if(a == null)
                d.fullTime_home = "-";
            else
                d.fullTime_home = a;
            a =  d.score.fullTime.awayTeam;
            if(a == null)
                d.fullTime_away = "-";
            else
                d.fullTime_away = a;
            a =  d.score.halfTime.homeTeam;
            if(a == null)
                d.halfTime_home = "-";
            else
                d.halfTime_home = a;
            a =  d.score.halfTime.awayTeam;
            if(a == null)
                d.halfTime_away = "-";
            else
                d.halfTime_away = a;
            synchronized (LOCK) {
                comps.add(d);
            }
            fetchLocalCompetition(d.competition_id,index);
            fetchLocalTeams(d.homeID,true,index);
            fetchLocalTeams(d.awayID,false,index);
            index++;
        }
        Toast.makeText(getApplication(), "REMOTE", Toast.LENGTH_SHORT).show();
    }

    private void fetchLocalCompetition(int id,int index)
    {
        databaseExecutor.execute(() -> {
        Competition competitions = FootballRoom.getInstance(
                getApplication()
        ).footballDAO().getCompetition(id);

        FootballRoom.uiHandler.post(() -> {
            dataRetrieved(competitions,index);
        });
    });
    }
    private void fetchLocalTeams(int id,boolean home,int index)
    {
        databaseExecutor.execute(() -> {
            CompetitionTable team = FootballRoom.getInstance(
                    getApplication()
            ).footballDAO().getCompetitionTableTeam(id);

            FootballRoom.uiHandler.post(() -> {
                dataRetrieved(team,home,index);
            });
        });
    }


    private void fetchLocal(int id,boolean fromTeam){
        CompetitionLoading.setValue(true);
        retrieveFromLocal(id,fromTeam);
        Toast.makeText(getApplication(), "LOCAL", Toast.LENGTH_SHORT).show();
    }

    private void dataRetrieved(List<ModelMatch> match){
        matches.setValue(match);
        CompetitionLoadError.setValue(match.size() == 0);
        CompetitionLoading.setValue(false);
    }
    private void dataRetrieved(Competition competitions,int index){
        synchronized (LOCK)
        {
            if(competitions != null)
                comps.get(index).competition_url = competitions.emblemUrl;

            if(index == comps.size() - 1)
            {
                if(finished)
                {
                    insertToLocal(comps);
                }
                else
                    finished = true;
            }
        }

    }
    private void dataRetrieved(CompetitionTable team, boolean home, int index){
        synchronized (LOCK)
        {
            if(team != null) {
                if (home)
                    comps.get(index).homeUrl = team.team_crestUrl;
                else
                    comps.get(index).awayUrl = team.team_crestUrl;
            }
            if(index == comps.size() - 1)
            {
                if(finished)
                {
                    insertToLocal(comps);
                }
                else
                    finished = true;
            }
        }

    }

    private void insertToLocal(List<ModelMatch> competitions){
        databaseExecutor.execute(() -> {
            FootballDAO dao = FootballRoom.getInstance(getApplication()).footballDAO();
            dao.insertAll(competitions.toArray(new ModelMatch[0]));
            FootballRoom.uiHandler.post(() -> {
                dataRetrieved(competitions);
            });
        });
    }

    private void retrieveFromLocal(int id,boolean fromTeam){

        databaseExecutor.execute(() -> {
            List<ModelMatch> competitions;
            if(fromTeam) {
                 competitions = FootballRoom.getInstance(
                        getApplication()
                ).footballDAO().getTeamMatches(id);
            }
            else
                competitions = FootballRoom.getInstance(
                        getApplication()
                ).footballDAO().getCompetitionMatches(id);

            FootballRoom.uiHandler.post(() -> {
                dataRetrieved(competitions);
            });
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if(disposable !=null)
        disposable.dispose();
        databaseExecutor.shutdown();
    }
}
