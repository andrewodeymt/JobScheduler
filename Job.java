/**
 * Authors: Liam Fallon and Andrew O'Donnell
 */
public class Job implements Comparable<Job> {
    private int jobNum, priority, arrivalTime, duration, progress, startTime, waitTime, execTime = 0;

    Job(int procJobNumber, int procPriority, int procArrivalTime, int procDuration) {

        jobNum = procJobNumber;
        priority = procPriority;
        arrivalTime = procArrivalTime;
        duration = procDuration;
    }

    public void calculateJobTime(int finishTime) {
        waitTime = startTime - arrivalTime;
        execTime = finishTime - startTime + 1;
    }

    @Override
    public int compareTo(final Job object) {
        if (this.priority > object.getPriority()) {
            return 1;
        } else if (this.priority == object.getPriority()) {
            return 0;
        } else {
            return -1;
        }
    }

    //Outputs up to date Job Information
    @Override
    public String toString() {
        String output = String.format("Job number: %-2d | " + "Wait time: %-4d | Execution time: %-4d |",
                jobNum, waitTime, execTime);
        return output;
    }

    public void runJob(int currSecond) {
        if (startTime == 0) {
            startTime = currSecond;
        }
        progress++;
    }

    public void setStartTime(int procStartTime) {
        startTime = procStartTime;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getJobNumber() {
        return jobNum;
    }

    public int getPriority() {
        return priority;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getDuration() {
        return duration;
    }

    public int getProgress() {
        return progress;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public int getExectuionTime() {
        return execTime;
    }
}
