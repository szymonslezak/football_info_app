package pl.edu.uwr.pum.footballapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.edu.uwr.pum.footballapp.model.Competition;
import pl.edu.uwr.pum.footballapp.model.local.FootballRoom;
import pl.edu.uwr.pum.footballapp.model.models.table.CompetitionTable;
import pl.edu.uwr.pum.footballapp.model.models.topscorer.ModelScorerList;
import pl.edu.uwr.pum.footballapp.model.models.topscorer.ModelScorers;
import pl.edu.uwr.pum.footballapp.model.remote.FootballService;
import pl.edu.uwr.pum.footballapp.util.Util;

public class TopScorerViewModel extends AndroidViewModel {

    public MutableLiveData<List<ModelScorers>> scorers =
            new MutableLiveData<>();
    public MutableLiveData<Boolean> CompetitionLoadError =
            new MutableLiveData<>();
    public MutableLiveData<Boolean> CompetitionLoading =
            new MutableLiveData<>();
    public MutableLiveData<Competition> comp =
            new MutableLiveData<>();

    private final FootballService service = new FootballService();
    public Disposable disposable;

    private final ExecutorService databaseExecutor =
            Executors.newFixedThreadPool(Util.NUM_OF_THREADS);

    public TopScorerViewModel(@NonNull Application application) {
        super(application);
    }


    public void refreshListFromRemote(int id){
        fetchRemote(id);
    }

    private void fetchRemote(int id){
        CompetitionLoading.setValue(true);
        fetchLocalCompetition(id);
        disposable = service.getTopScorer(id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<ModelScorerList>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull ModelScorerList scorerList) {
                        int index = 0;
                        scorers.setValue(scorerList.scorers);
                        for(ModelScorers d : scorerList.scorers)
                        {
                            fetchTeam(d.team.id,index);
                            index++;
                        }
                        CompetitionLoadError.setValue(false);
                        CompetitionLoading.setValue(false);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        CompetitionLoadError.setValue(true);
                        CompetitionLoading.setValue(false);
                        e.printStackTrace();
                    }
                });
    }

    private void fetchLocalCompetition(int id)
    {
        databaseExecutor.execute(() -> {
        Competition competitions = FootballRoom.getInstance(
                getApplication()
        ).footballDAO().getCompetition(id);

        FootballRoom.uiHandler.post(() -> {
            dataRetrieved(competitions);
        });
    });
    }
    private void fetchTeam(int id,int index)
    {
        databaseExecutor.execute(() -> {
            CompetitionTable team = FootballRoom.getInstance(
                    getApplication()
            ).footballDAO().getCompetitionTableTeam(id);

            FootballRoom.uiHandler.post(() -> {
                dataRetrieved(team,index);
            });
        });
    }

    private void dataRetrieved(Competition competitions){
        comp.setValue(competitions);


    }
    private void dataRetrieved(CompetitionTable team, int index){

        Objects.requireNonNull(scorers.getValue()).get(index).team_url = team.team_crestUrl;

    }


    @Override
    protected void onCleared() {
        super.onCleared();
        if(disposable !=null)
        disposable.dispose();
        databaseExecutor.shutdown();
    }
}
