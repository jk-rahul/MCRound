package com.example.MCRound.Round2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class LapHeroService {
    @Getter
    private Map<String, DriverMetrics> driverMetricsMap;
    private GetHeroStrategy getHeroStrategy;

    public LapHeroService(GetHeroStrategy getHeroStrategy) {
        driverMetricsMap = new HashMap<>();
        this.getHeroStrategy = getHeroStrategy;
    }



    public void addDriverLap(String driverId, int lapTime) {
        if (driverMetricsMap.containsKey(driverId)) {
            DriverMetrics driverMetrics = driverMetricsMap.get(driverId);
            driverMetrics.setTotalLaps(1 + driverMetrics.getTotalLaps());
            driverMetrics.setLapTimes(lapTime + driverMetrics.getLapTimes());
            driverMetrics.setLastLapTime(lapTime);
            double lastAvgLapTime = (1.0*driverMetrics.getLapTimes())/ driverMetrics.getTotalLaps();
            driverMetrics.setLastLapAvgGain(lapTime - lastAvgLapTime);
//            System.out.println(driverMetrics.getLastLapAvgGain());
        } else {
            driverMetricsMap.put(driverId, new DriverMetrics(driverId, 1, lapTime, lapTime, lapTime, lapTime, lapTime));
        }
    }

    public String getDriverHero() {
        String hero = "";
        double lapImprovement = Double.MAX_VALUE;
        int lapTime = Integer.MAX_VALUE;
        if (driverMetricsMap.isEmpty()) return hero;
        for (DriverMetrics driverMetrics : driverMetricsMap.values()) {
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


@Data
@AllArgsConstructor
class DriverMetrics {
    String driverId;
    int totalLaps;
    int totalPitstopLaps;
    int lapTimes;
    int totalPitstopTimes;
    int lastLapTime;
    double lastLapAvgGain;

}