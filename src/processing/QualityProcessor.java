package processing;

/**
 * Created by Lucas on 4/7/2016.
 *
 * @param Garage
 * Garage is nog geen class, kan vervangen worden door aparte parameters
 */

public class QualityProcessor implements IProcessor {
    public float getProcessedValue(float crimIndex, Garage garage) {
        int vacancies = garage.getAvailabeSpace();
        int capacity = garage.getCapacityMax();
        float score = 0;

        for (int i = 0; i < capacity; i++){
            score += 0.03;
        }

        for (int j = 0; j < vacancies; j++) {
            score -= 0.02;
        }

        for (float k = 0; k < crimIndex; k++) {
            score -= 0.005;
        }
        return score;
    }

    public Quality getQuality(float score) {
        if (score > 1.5) {
            if (score > 3) {
                if (score > 4.5) {
                    if (score > 6) {
                        return Quality.SAFE;
                    }
                    return Quality.GOOD;
                }
                return Quality.NORMAL;
            }
            return Quality.RISKY;
        }
        return Quality.UNSAFE;
    }
}
