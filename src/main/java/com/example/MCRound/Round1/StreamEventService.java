package com.example.MCRound.Round1;

public interface StreamEventService {
    void increase(int id);
    void decrease(int id);
    int fetchMostPopular();
}
