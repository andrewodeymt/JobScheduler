/**
 * Authors: Liam Fallon and Andrew O'Donnell
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import static java.lang.System.out;

public class JobScheduler {
    public static void main(String[] args) throws FileNotFoundException {

        start(args[0], args[1]);
    }

    public static void start(String fileName, String coreCountString) throws FileNotFoundException {
        int coreCount = Integer.valueOf(coreCountString);
        Job[] jobData = new Job[10];
        int inputSize = 0;

        out.println("Second by second output for a Job Scheduler. "
                + "If a job is completed, the wait time and total execution " + "time for the job are also printed.\n");

        //Standard BufferedReader Implementation, take file, split by line, then convert line of file to Job Object
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.trim().split("\\s+");

                //Really should be in a for Loop
                Job newJob = new Job(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]),
                        Integer.parseInt(tokens[3]));
                //Same principal followed when re-hashing hashMap, if Array too small double size and add Job
                if (inputSize == jobData.length) {
                    Job[] newArray = new Job[2 * inputSize];
                    jobData = resize(jobData, newArray);
                }
                //Initial insertion of job into Array Object
                jobData[inputSize] = newJob;
                inputSize++;
            }

            //Sorts Jobs by ArrivalTime
            quicksort(jobData, 0, inputSize - 1);

            out.println(String.format("Number of jobs: " + "%d\n", inputSize));

            JobPriorityQueue jobHeap = new JobPriorityQueue(inputSize);

            int i = 0, reorderIndex = 0;
            Job jobs[] = new Job[coreCount];
            for (int currSecond = 0; !(jobHeap.isEmpty() && i == inputSize); currSecond++) {
                out.println("\n");
                while (i != inputSize && jobData[i].getArrivalTime() == currSecond) {
                    jobHeap.insert(jobData[i]);
                    i++;
                }

                if (!jobHeap.isEmpty()) {
                    for (int j = 0; j < jobs.length; j++) {
                        if (jobs[j] == null) {
                            boolean isTaken = false;
                            for (int j2 = 0; j2 < jobs.length; j2++) {
                                if (((Job) jobHeap.peek(j + 1) == jobs[j2]) && (jobs[j2] != null)) {
                                    //checks if a core is already doing a task and only assigns if one isn't
                                    isTaken = true;
                                }
                            }
                            if (isTaken == false) {
                                jobs[j] = (Job) jobHeap.peek(j + 1);
                            }
                        }

                    }
                    for (int j = 0; j < jobs.length; j++) {
                        for (int w = 1; w < jobHeap.getSize() + 1; w++) {
                            Boolean isTaken = false;
                            if ((jobs[j] == null) || (((Job) jobHeap.peek(w)).getPriority()) >= jobs[j].getPriority()) {
                                //changes cores if they have a new higher priority task
                                for (int k = 0; k < jobs.length; k++) {
                                    if ((jobs[k] != null) && (jobs[k].getJobNumber() == ((Job) jobHeap.peek(w)).getJobNumber())) {
                                        isTaken = true;
                                    }
                                }
                                if (isTaken == false) {
                                    jobs[j] = (Job) jobHeap.peek(w);
                                }
                            }
                        }
                    }


                    //Job currJob = (Job) jobHeap.peekMax();
                    //Runs Job for 1 second as per constraints
                    String output = "";
                    for (int j = 0; j < jobs.length; j++) {
                        if (jobs[j] != null) {
                            Job currJob = jobs[j];

                            currJob.runJob(currSecond);
                            output = String.format("Current second: %-4d | " + "Core: %-2d |" + "Current job number: %-2d |", currSecond, j + 1,
                                    currJob.getJobNumber());
                            //When Job Completed logically remove from Heap
                            if (currJob.getDuration() <= currJob.getProgress()) {
                                //output += String.format("\nCompleted job %d", currJob.getJobNumber());
                                out.println("Completed job " + currJob.getJobNumber());
                                currJob.calculateJobTime(currSecond);
                                int index = -1;
                                for (int w = 0; w < jobHeap.getSize(); w++) {
                                    if (currJob.getJobNumber() == ((Job) jobHeap.peek(w + 1)).getJobNumber()) {
                                        //finds heap index of currJob
                                        index = w + 1;
                                    }

                                }

                                jobData[reorderIndex] = (Job) jobHeap.remove(index);
                                reorderIndex++;
                                jobs[j] = null;
                            }
                            out.println(output);

                        } else {
                            out.println(String.format("Current second: %-4d |" + "Core: %-2d |" + "Core Idle", currSecond, j + 1));
                        }
                    }
                } else {
                    out.println(String.format("Current second: %-4d | Queue Idle", currSecond));
                }
            }

            int sumWaitTime = 0, sumExecTime = 0;
            out.println("\nStatistics:");
            for (int j = 0; j < inputSize; j++) {
                sumWaitTime += jobData[j].getWaitTime();
                sumExecTime += jobData[j].getExectuionTime();
                out.println(jobData[j]);
            }

            out.println(String.format("\nAverage wait time: %.1f" + "\nAverage execution time: %.1f",
                    ((float) sumWaitTime) / ((float) (inputSize)), ((float) sumExecTime) / ((float) (inputSize))));
        } catch (FileNotFoundException ex) {
            out.println(ex);
        } catch (IOException ex) {
            out.println(ex);
        }
    }

    //Should be refactored if time allows to be .clone
    public static Job[] resize(Job[] oldArray, Job[] newArray) {
        for (int i = 0; i < oldArray.length; i++) {
            newArray[i] = oldArray[i];
        }
        return newArray;
    }

    /* The main function that implements QuickSort
     * arr[] --> Array to be sorted,
     * low --> Starting index,
     * high --> Ending index
     * http://developer.classpath.org/doc/java/util/Arrays-source.html
     */
    public static void quicksort(Job[] array, int low, int high) {
        if (low < high) {
            Job lastValue = array[high];
            int pivot = lastValue.getArrivalTime();
            int unsortedLowValue = 0, unsortedHighValue = high;

            Job nextCompare = array[0];
            while (unsortedLowValue < unsortedHighValue) {
                if (nextCompare.getArrivalTime() <= pivot) {
                    array[unsortedLowValue] = nextCompare;
                    unsortedLowValue++;
                    nextCompare = array[unsortedLowValue];
                } else {
                    array[unsortedHighValue] = nextCompare;
                    unsortedHighValue--;
                    nextCompare = array[unsortedHighValue];
                }
            }
            array[unsortedLowValue] = lastValue;
            quicksort(array, 0, unsortedLowValue - 1);
            quicksort(array, unsortedLowValue + 1, high);
        }
    }
}
