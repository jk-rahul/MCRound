package com.example.MCRound.Round2;

public class GetHeroIncludingPitstops implements GetHeroStrategy {
    @Override
    public String getDriverHero(LapHeroService lapHeroService) {
        String hero = "";
        double lapImprovement = Double.MAX_VALUE;
        int lapTime = Integer.MAX_VALUE;
        if (lapHeroService.getDriverMetricsMap().isEmpty()) return hero;
        for (DriverMetrics driverMetrics : lapHeroService.getDriverMetricsMap().values()) {
            if ((driverMetrics.getLastLapAvgGain() < lapImprovement) ||
                    (driverMetrics.getLastLapAvgGain() == lapImprovement &&
                            driverMetrics.getLastLapTime() < lapTime)) {
                hero = driverMetrics.driverId;
                lapImprovement = driverMetrics.getLastLapAvgGain();
                lapTime = driverMetrics.getLastLapTime();
            }
        }
        return hero;
    }
}
