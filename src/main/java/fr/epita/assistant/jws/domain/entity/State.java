package fr.epita.domain.entity;

public enum State
{
    FINISHED("FINISHED"),
    RUNNING("RUNNING"),
    STARTING("STARTING");

    public String state;
    State(String state)
    {
        this.state = state;
    }
}
