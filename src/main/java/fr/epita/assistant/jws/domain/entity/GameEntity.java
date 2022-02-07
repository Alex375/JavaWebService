package fr.epita.domain.entity;

import lombok.Value;
import lombok.With;

import java.sql.Timestamp;
import java.util.List;

@Value @With
public class GameEntity
{
    public Timestamp startTime;
    public String state;
    public List<PlayerEntity> players;
    public List<String> map;
    public long id;
}
