package com.example.MCRound.Round2;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Driver, Lap Time
 * "Driver1", 100
 * "Driver2", 90
 * "Driver3", 70
 * "Driver1", 110
 * "Driver2", 95
 * "Driver3", 50
 * // Driver 1 Last Lap Gain = 110 - (100+110)/2 = 5
 * // Driver 2 Last Lap Gain = 95 - (90+95)/2 = 2.5
 * // Driver 3 Last Lap Gain = 50 - (70+50)/2 = -10
 * // Driver 3 is last lap hero
 */
public class LapHeroServiceTest {
    private LapHeroService lapHeroService;

    @BeforeEach
    void setup() {
        lapHeroService= new LapHeroService(new GetHeroIncludingPitstops());
    }

    @Test
    public void testLastLapHeroCase1() {
        lapHeroService.addDriverLap("Driver1", 100);
        lapHeroService.addDriverLap("Driver2", 90);
        lapHeroService.addDriverLap("Driver3", 70);
        lapHeroService.addDriverLap("Driver1", 110);
        lapHeroService.addDriverLap("Driver2", 95);
        lapHeroService.addDriverLap("Driver3", 50);

        String actualHapHero = lapHeroService.getDriverHero();
        // expected Driver3
        System.out.println(actualHapHero);
    }

    @Test
    public void testLastLapHeroCase2() {
        lapHeroService.addDriverLap("Driver1", 100);
        lapHeroService.addDriverLap("Driver2", 90);
        lapHeroService.addDriverLap("Driver3", 70);
//        lapHeroService.addDriverLap("Driver1", 110);
//        lapHeroService.addDriverLap("Driver2", 95);
//        lapHeroService.addDriverLap("Driver3", 50);

        String actualHapHero = lapHeroService.getDriverHero();
        // expected Driver3
        System.out.println(actualHapHero);
    }


}
