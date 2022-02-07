package fr.epita.assistant.jws.domain.entity;


import lombok.Value;
import lombok.With;

@Value @With
public class PlayerEntity
{
    public long id;
    public String name;
    public int lives;
    public int posX;
    public int posY;
}
