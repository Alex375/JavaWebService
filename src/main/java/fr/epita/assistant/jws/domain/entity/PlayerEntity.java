package fr.epita.domain.entity;


import lombok.Value;
import lombok.With;

import java.sql.Timestamp;

@Value @With
public class PlayerEntity
{
    public long id;
    public String name;
    public int lives;
    public int posX;
    public int posY;
}
