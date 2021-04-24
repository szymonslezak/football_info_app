package pl.edu.uwr.pum.footballapp.model.local;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import pl.edu.uwr.pum.footballapp.model.Competition;
import pl.edu.uwr.pum.footballapp.model.models.club.ModelTeam;
import pl.edu.uwr.pum.footballapp.model.models.club.ModelTeamFav;
import pl.edu.uwr.pum.footballapp.model.models.match.ModelMatch;
import pl.edu.uwr.pum.footballapp.model.models.table.CompetitionTable;

@Database(entities = {Competition.class, CompetitionTable.class, ModelTeam.class, ModelTeamFav.class, ModelMatch.class}, version = 1, exportSchema = false)
public abstract class FootballRoom extends RoomDatabase {

    private static volatile FootballRoom instance;

    public static Handler uiHandler = new Handler(Looper.getMainLooper());

    private static final Object LOCK = new Object();

    private static synchronized void createInstance(Context context) {
        if (instance == null)
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    FootballRoom.class,
                    "football_database_java"
            ).build();
    }

    public static FootballRoom getInstance(Context context) {
        if (instance == null)
            synchronized (LOCK) {
                createInstance(context);
            }

        return instance;
    }

    public abstract FootballDAO footballDAO();
}
