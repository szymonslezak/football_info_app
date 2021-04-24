package pl.edu.uwr.pum.footballapp.model.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import pl.edu.uwr.pum.footballapp.model.Competition;
import pl.edu.uwr.pum.footballapp.model.models.club.ModelTeam;
import pl.edu.uwr.pum.footballapp.model.models.club.ModelTeamFav;
import pl.edu.uwr.pum.footballapp.model.models.match.ModelMatch;
import pl.edu.uwr.pum.footballapp.model.models.table.CompetitionTable;

@Dao
public interface FootballDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(Competition... competitions);

    @Query("SELECT * FROM Competitions ORDER BY name ASC")
    List<Competition> getAllCompetitions();

    @Query("SELECT * FROM Competitions WHERE id = :id")
    Competition getCompetition(int id);

    @Query("DELETE FROM Competitions")
    void deleteAllCompetitions();





    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(CompetitionTable... competitions);

    @Query("SELECT * FROM competitionTable")
    List<CompetitionTable> getAllCompetitionsTable();

    @Query("SELECT * FROM competitionTable WHERE competition_id = :id ORDER BY position ASC")
    List<CompetitionTable> getCompetitionTable(int id);

    @Query("SELECT * FROM competitionTable WHERE team_id = :id")
    CompetitionTable getCompetitionTableTeam(int id);

    @Query("DELETE FROM competitionTable")
    void deleteAllCompetitionsTable();





    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(ModelTeam... competitions);

    @Query("SELECT * FROM Team")
    List<ModelTeam> getAllTeams();

    @Query("SELECT * FROM Team WHERE id = :id")
    ModelTeam getTeam(int id);

    @Query("DELETE FROM Team")
    void deleteAllTeams();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(ModelTeamFav... competitions);

    @Query("SELECT * FROM TeamFavourite WHERE favourite = 1")
    List<ModelTeamFav> getAllTeamsFav();

    @Query("SELECT * FROM TeamFavourite WHERE id = :id")
    ModelTeamFav getTeamFavourite(int id);

    @Query("DELETE FROM TeamFavourite")
    void deleteAllTeamsFavourite();



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(ModelMatch... matches);

    @Query("SELECT * FROM Matches")
    List<ModelMatch> getAllMatches();

    @Query("SELECT * FROM Matches WHERE homeID = :id OR awayID = :id ORDER BY matchday ASC")
    List<ModelMatch> getTeamMatches(int id);

    @Query("SELECT * FROM Matches WHERE competition_id = :id ORDER BY matchday ASC")
    List<ModelMatch> getCompetitionMatches(int id);

    @Query("SELECT * FROM Matches WHERE (homeID = :id OR awayID = :id) AND status = 'SCHEDULED' ORDER BY matchday ASC LIMIT 1")
    ModelMatch getTeamFavMatch(int id);

    @Query("DELETE FROM Matches")
    void deleteAllMatches();


}
