package pl.edu.uwr.pum.footballapp.model.models.club;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;
@Entity(tableName = "Team")
public class ModelTeam {

    @PrimaryKey(autoGenerate = false)
    public int id;

    public String name;
    public String shortName;
    public String tla;
    public String crestUrl;
    public String address;
    public String phone;
    public String website;
    public String email;
    public int founded;
    public String clubColors;
    public String venue;
    @Ignore
    public List<ModelTeamSquad> squad;
}
