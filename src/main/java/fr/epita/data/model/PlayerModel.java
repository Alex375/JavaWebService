package fr.epita.data.model;


import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "player")
@AllArgsConstructor @NoArgsConstructor @With @ToString
public class PlayerModel extends PanacheEntityBase
{
    public @Id @GeneratedValue(strategy = GenerationType.IDENTITY) long id;
    public @Column(name = "lastbomb") Timestamp lastBomb;
    public @Column(name = "lastmovement") Timestamp lastMovement;
    public @Column(name = "lives") int lives;
    public @Column(name = "name") String name;
    public @Column(name = "posx") int posx;
    public @Column(name = "posy") int posy;
    public @Column(name = "bombposx") int bombPosX;
    public @Column(name = "bombposy") int bombPosY;
    public @ManyToOne GameModel game;
}
