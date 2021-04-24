package pl.edu.uwr.pum.footballapp.model.models.match;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import pl.edu.uwr.pum.footballapp.model.Competition;
@Entity(tableName = "Matches")
public class ModelMatch {

    @PrimaryKey(autoGenerate = false)
    public int id;
    public int competition_id;
    @Ignore
    public ModelMatchCompetition competition;

    public String utcDate;
    public String status;
    public int matchday;
    public String fullTime_home;
    public String fullTime_away;
    public String halfTime_home;
    public String halfTime_away;
    @Ignore
    public ModelMatchTeam homeTeam;
    @Ignore
    public ModelMatchTeam awayTeam;
    public int homeID;
    public int awayID;

    @Ignore
    public ModelScore score;


    public String competition_url;
    public String competitionName;
    public String homeUrl;
    public String awayUrl;
    public String homeName;
    public String awayName;

}
