package fr.epita.domain.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Pos
{
    public int posX;
    public int posY;

    @Override
    public String toString()
    {
        return "Pos{" +
                "posX=" + posX +
                ", posY=" + posY +
                '}';
    }
}
