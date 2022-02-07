package fr.epita.data.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity @Table(name = "game")
@AllArgsConstructor @NoArgsConstructor @With @ToString
public class GameModel extends PanacheEntityBase
{
    public @Id @GeneratedValue(strategy = GenerationType.IDENTITY) long id;
    public @Column(name = "starttime") Timestamp startTime;
    public @Column(name = "state") String state;
    public @OneToMany List<PlayerModel> players;
    public @OneToOne @MapsId GameMapModel map;
}
