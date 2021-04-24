package pl.edu.uwr.pum.footballapp.model.models.club;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "TeamFavourite")
public class ModelTeamFav {
    @PrimaryKey(autoGenerate = false)
    public int id;
    public boolean favourite;

    public ModelTeamFav(int id, boolean favourite)
    {
        this.id = id;
        this.favourite = favourite;
    }

}
