package pl.edu.uwr.pum.footballapp.viewmodel;

import android.app.Application;
import android.view.View;
import android.widget.Button;
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
import pl.edu.uwr.pum.footballapp.R;
import pl.edu.uwr.pum.footballapp.model.models.table.CompetitionTable;
import pl.edu.uwr.pum.footballapp.model.models.table.CompetitionTableStand;
import pl.edu.uwr.pum.footballapp.model.models.table.CompetitionTableStandings;
import pl.edu.uwr.pum.footballapp.model.local.FootballDAO;
import pl.edu.uwr.pum.footballapp.model.local.FootballRoom;
import pl.edu.uwr.pum.footballapp.model.remote.FootballService;
import pl.edu.uwr.pum.footballapp.util.Util;
import pl.edu.uwr.pum.footballapp.view.detail.table.TableViewModelClickListener;

public class TableViewModel extends AndroidViewModel
implements TableViewModelClickListener {

    public MutableLiveData<List<CompetitionTable>> comps =
            new MutableLiveData<List<CompetitionTable>>();
    public List<CompetitionTable> comps_total =
            new ArrayList<>();
    public List<CompetitionTable> comps_away =
            new ArrayList<>();
    public List<CompetitionTable> comps_home =
            new ArrayList<>();
    public MutableLiveData<Boolean> CompetitionLoadError =
            new MutableLiveData<>();
    public MutableLiveData<Boolean> CompetitionLoading =
            new MutableLiveData<>();
    private int status = 0; //0-main 1-home 2-away

    private final FootballService service = new FootballService();
    public Disposable disposable;

    private final ExecutorService databaseExecutor =
            Executors.newFixedThreadPool(Util.NUM_OF_THREADS);

    public TableViewModel(@NonNull Application application) {
        super(application);
    }

    public void refreshListFromRemote(int id) {
        fetchRemote(id);
    }


    private void fetchRemote(int id) {
        CompetitionLoading.setValue(true);
        status = 0;

        disposable = service.getCompetitionsTable(id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<CompetitionTableStandings>() {
                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull CompetitionTableStandings compList) {
                        comps_total = new ArrayList<>();
                        comps_away = new ArrayList<>();
                        comps_home = new ArrayList<>();
                        for (CompetitionTableStand d : compList.standings) {
                                for (CompetitionTable c : d.table) {
                                    if (c.competition_id == 0) {
                                        c.competition_id = compList.competition.id;
                                    }
                                    if (c.team_id == 0) {
                                        c.team_id = c.team.id;
                                    }
                                    if (c.team_name == null) {
                                        c.team_name = c.team.name;
                                    }
                                    if (c.team_crestUrl == null) {
                                        c.team_crestUrl = c.team.crestUrl;
                                    }
                                    if (c.type == null) {
                                        if (d.type != null)
                                            c.type = d.type;
                                    }
                                    if (d.type.equals("TOTAL"))
                                        comps_total.add(c);
                                    else if (d.type.equals("AWAY"))
                                        comps_away.add(c);
                                    else if (d.type.equals("HOME"))
                                        comps_home.add(c);
                                }
                        }
                        insertToLocal(comps_total);
                        Toast.makeText(getApplication(), "REMOTE", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        CompetitionLoadError.setValue(true);
                        CompetitionLoading.setValue(false);
                        fetchLocal(id);
                        e.printStackTrace();
                    }
                });
    }

    private void fetchLocal(int id) {
        CompetitionLoading.setValue(true);
        retrieveFromLocal(id);
        Toast.makeText(getApplication(), "LOCAL", Toast.LENGTH_SHORT).show();
    }

    private void dataRetrieved(List<CompetitionTable> competitions) {
        comps.setValue(competitions);
        CompetitionLoadError.setValue(competitions.size() == 0);
        CompetitionLoading.setValue(false);
    }

    private void insertToLocal(List<CompetitionTable> competitions) {
        databaseExecutor.execute(() -> {
            FootballDAO dao = FootballRoom.getInstance(getApplication()).footballDAO();

            dao.insertAll(competitions.toArray(new CompetitionTable[0]));

            FootballRoom.uiHandler.post(() -> {
                dataRetrieved(competitions);
            });
        });
    }

    private void retrieveFromLocal(int id) {
        databaseExecutor.execute(() -> {
            List<CompetitionTable> competitions = FootballRoom.getInstance(
                    getApplication()
            ).footballDAO().getCompetitionTable(id);

            FootballRoom.uiHandler.post(() -> {
                dataRetrieved(competitions);
            });
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (disposable != null)
            disposable.dispose();
        databaseExecutor.shutdown();
    }

    @Override
    public void onButtonClicked(View view) {
        if (comps_home.size() != 0 && comps_away.size() != 0) {
            Button button = (Button)view.findViewById(R.id.AwayButton);
            if (status == 0) {
                status = 1;
                comps.setValue(comps_home);

                button.setText("AWAY");
            }
            else if (status == 1)
            {
                comps.setValue(comps_away);
                status = 2;
                button.setText("TOTAL");
            }
            else
            {
                status = 0;
                comps.setValue(comps_total);
                button.setText("HOME");
            }
        }
    }

}
