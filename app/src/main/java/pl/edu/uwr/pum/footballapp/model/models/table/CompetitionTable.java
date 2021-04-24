package pl.edu.uwr.pum.footballapp.model.models.table;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "competitionTable")
public class CompetitionTable {

    @PrimaryKey(autoGenerate = false)
    public int team_id;
    public String type;
    public int competition_id;

    public String team_name;
    public String team_crestUrl;

    public int position;

    public int playedGames;

    public int won;

    public int draw;

    public int lost;

    public int points;

    public int goalsFor;

    public int goalsAgainst;

    public int goalDifference;

    @Ignore
    public CompetitionTableTeam team;

}
