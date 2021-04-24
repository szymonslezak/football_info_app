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
import pl.edu.uwr.pum.footballapp.model.models.club.ModelTeamFav;
import pl.edu.uwr.pum.footballapp.model.models.competition.CompetitionList;
import pl.edu.uwr.pum.footballapp.model.local.FootballRoom;
import pl.edu.uwr.pum.footballapp.model.local.FootballDAO;
import pl.edu.uwr.pum.footballapp.model.models.match.ModelMatch;
import pl.edu.uwr.pum.footballapp.model.remote.FootballService;
import pl.edu.uwr.pum.footballapp.util.NotificationHelper;
import pl.edu.uwr.pum.footballapp.util.Util;

public class ListViewModel extends AndroidViewModel {

    public MutableLiveData<List<Competition>> comps =
            new MutableLiveData<List<Competition>>();
    public MutableLiveData<Boolean> CompetitionLoadError =
            new MutableLiveData<>();
    public MutableLiveData<Boolean> CompetitionLoading =
            new MutableLiveData<>();

    public List<ModelMatch> favMatches = new ArrayList<>();

    private final FootballService service = new FootballService();
    public Disposable disposable;

    private final ExecutorService databaseExecutor =
            Executors.newFixedThreadPool(Util.NUM_OF_THREADS);

    public ListViewModel(@NonNull Application application) {
        super(application);
    }

    public void refreshList() {
        fetchRemote();
    }

    public void refreshListFromRemote() {
        fetchRemote();
    }

    private void fetchRemote() {
        CompetitionLoading.setValue(true);

        disposable = service.getCompetitions()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<CompetitionList>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull CompetitionList compList) {
                        List<Competition> comps = new ArrayList<>();
                        for (Competition c : compList.competitions) {
                            if (c.emblemUrl == null) {
                                c.emblemUrl = c.area.ensignUrl;
                            }
                            comps.add(c);
                        }
                        insertToLocal(comps);
                        Toast.makeText(getApplication(), "REMOTE", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        CompetitionLoadError.setValue(true);
                        CompetitionLoading.setValue(false);
                        fetchLocal();
                        e.printStackTrace();
                    }
                });
    }

    private void fetchLocal() {
        CompetitionLoading.setValue(true);
        retrieveFromLocal();
        Toast.makeText(getApplication(), "LOCAL", Toast.LENGTH_SHORT).show();
    }

    private void dataRetrieved(List<Competition> competitions) {
        comps.setValue(competitions);
        CompetitionLoadError.setValue(competitions.size() == 0);
        CompetitionLoading.setValue(false);
        fetchTeamFavs();
    }

    private void insertToLocal(List<Competition> competitions) {
        databaseExecutor.execute(() -> {
            FootballDAO dao = pl.edu.uwr.pum.footballapp.model.local.FootballRoom.getInstance(getApplication()).footballDAO();
            dao.insertAll(competitions.toArray(new Competition[0]));
            FootballRoom.uiHandler.post(() -> {
                dataRetrieved(competitions);
            });
        });
    }

    private void retrieveFromLocal() {
        databaseExecutor.execute(() -> {
            List<Competition> competitions = pl.edu.uwr.pum.footballapp.model.local.FootballRoom.getInstance(
                    getApplication()
            ).footballDAO().getAllCompetitions();

            pl.edu.uwr.pum.footballapp.model.local.FootballRoom.uiHandler.post(() -> {
                dataRetrieved(competitions);
            });
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.dispose();
        databaseExecutor.shutdown();
    }

    private void fetchUpcomingTeamFavMatches(int id,boolean create_not)
    {
        favMatches = new ArrayList<>();
        databaseExecutor.execute(() -> {
            ModelMatch match = pl.edu.uwr.pum.footballapp.model.local.FootballRoom.getInstance(
                    getApplication()
            ).footballDAO().getTeamFavMatch(id);

            pl.edu.uwr.pum.footballapp.model.local.FootballRoom.uiHandler.post(() -> {
                dataFavRetrieved(match,create_not);
            });
        });


    }
    private void dataFavRetrieved(ModelMatch match,boolean create_not) {
        if (match != null)
        {
            favMatches.add(match);

        }
        if(create_not) {
            NotificationHelper notificationHelper = new NotificationHelper(getApplication());
            notificationHelper.createNotification(favMatches);
        }

    }


    private void fetchTeamFavs()
    {
        databaseExecutor.execute(() -> {
            List<ModelTeamFav> favs = pl.edu.uwr.pum.footballapp.model.local.FootballRoom.getInstance(
                    getApplication()
            ).footballDAO().getAllTeamsFav();

            pl.edu.uwr.pum.footballapp.model.local.FootballRoom.uiHandler.post(() -> {
                dataFavRetrieved(favs);
            });
        });

    }
    private void dataFavRetrieved(List<ModelTeamFav> favs) {
        int index = 0;
        boolean finished = false;
        for(ModelTeamFav team : favs)
        {
            index++;
            if(index == favs.size())
                finished = true;
            fetchUpcomingTeamFavMatches(team.id,finished);
        }

    }

}
