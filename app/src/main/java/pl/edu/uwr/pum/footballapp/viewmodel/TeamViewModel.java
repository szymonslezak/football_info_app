package pl.edu.uwr.pum.footballapp.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.edu.uwr.pum.footballapp.model.local.FootballDAO;
import pl.edu.uwr.pum.footballapp.model.local.FootballRoom;
import pl.edu.uwr.pum.footballapp.model.models.club.ModelTeam;
import pl.edu.uwr.pum.footballapp.model.models.club.ModelTeamFav;
import pl.edu.uwr.pum.footballapp.model.remote.FootballService;
import pl.edu.uwr.pum.footballapp.util.Util;

public class TeamViewModel extends AndroidViewModel {

    public MutableLiveData<ModelTeam> _team =
            new MutableLiveData<ModelTeam>();
    public MutableLiveData<ModelTeamFav> _team_fav =
            new MutableLiveData<ModelTeamFav>();
    public MutableLiveData<Boolean> CompetitionLoadError =
            new MutableLiveData<>();
    public MutableLiveData<Boolean> CompetitionLoading =
            new MutableLiveData<>();

    private final FootballService service = new FootballService();
    public Disposable disposable;

    private final ExecutorService databaseExecutor =
            Executors.newFixedThreadPool(Util.NUM_OF_THREADS);

    public TeamViewModel(@NonNull Application application) {
        super(application);
    }

    public void refreshListFromRemote(int id){
        fetchRemote(id);
    }


    private void fetchRemote(int id){
        CompetitionLoading.setValue(true);

        disposable = service.getTeam(id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<ModelTeam>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull ModelTeam team) {
                        insertToLocal(team);
                        fetchLocalFavourite(id);
                        Toast.makeText(getApplication(), "REMOTE", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        CompetitionLoadError.setValue(true);
                        CompetitionLoading.setValue(false);
                        fetchLocal(id);
                        fetchLocalFavourite(id);
                        e.printStackTrace();
                    }
                });
    }

    private void fetchLocal(int id){
        CompetitionLoading.setValue(true);
        retrieveFromLocal(id);
        Toast.makeText(getApplication(), "LOCAL", Toast.LENGTH_SHORT).show();
    }
    private void fetchLocalFavourite(int id)
    {
        databaseExecutor.execute(() -> {
            ModelTeamFav team = FootballRoom.getInstance(
                    getApplication()
            ).footballDAO().getTeamFavourite(id);

            if(team != null)
            FootballRoom.uiHandler.post(() -> {
                dataRetrieved(team);
            });
        });
    }

    private void dataRetrieved(ModelTeam team){
        _team.setValue(team);
        CompetitionLoadError.setValue(team == null);
        CompetitionLoading.setValue(false);
    }
    private void dataRetrieved(ModelTeamFav team)
    {
        _team_fav.setValue(team);
    }


    private void insertToLocal(ModelTeam team){
        databaseExecutor.execute(() -> {
            FootballDAO dao = FootballRoom.getInstance(getApplication()).footballDAO();
            dao.insertAll(team);
            FootballRoom.uiHandler.post(() -> {
                dataRetrieved(team);
            });
        });
    }

    public void updateTeamFav(ModelTeamFav team)
    {
        databaseExecutor.execute(() -> {
            FootballDAO dao = FootballRoom.getInstance(getApplication()).footballDAO();
            dao.insertAll(team);
        });

    }

    private void retrieveFromLocal(int id){
        databaseExecutor.execute(() -> {
            ModelTeam team = FootballRoom.getInstance(
                    getApplication()
            ).footballDAO().getTeam(id);

            FootballRoom.uiHandler.post(() -> {
                dataRetrieved(team);
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
