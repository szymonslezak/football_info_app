package pl.edu.uwr.pum.footballapp.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import pl.edu.uwr.pum.footballapp.model.models.competition.CompetitionArea;

@Entity (tableName = "Competitions")
public class Competition {

    @PrimaryKey(autoGenerate = false)
    public int id;

    public String name;

    public String code;

    public String plan;

    @Ignore
    public CompetitionArea area;

    public String emblemUrl;

    public Competition(int id,String name, String code, String plan, String emblemUrl) {
        this.id = id;
        this.name = name;
        this.name = name;
        this.code = code;
        this.plan = plan;
        this.emblemUrl = emblemUrl;
    }

}
