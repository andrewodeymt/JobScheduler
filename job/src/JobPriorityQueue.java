/**
 * http://developer.classpath.org/doc/java/util/PriorityQueue-source.html
 */
public class JobPriorityQueue<T extends Comparable<T>> {
    private T[] heap;
    private int heapSize = 0;

    @SuppressWarnings("unchecked")
	JobPriorityQueue(int size) {

        heap = (T[]) new Comparable<?>[size + 1];
    }

    public void insert(T newJob) {
        heapSize++;
        heap[heapSize] = newJob;

        int newJobIndex = heapSize;
        boolean sorted = false;
        while (!sorted) {
            if (newJobIndex == 1) {
                sorted = true;
            } else {
                if (compareNodes(newJobIndex, newJobIndex / 2) >= 0) {
                    newJobIndex = bubbleUp(newJobIndex);
                } else {
                    sorted = true;
                }
            }
        }
    }

    public T remove(int i) {
    	
        T returnJob = heap[i];  //removes job at index i

        heap[i] = heap[heapSize];
        boolean changedSize = false;
        if (heapSize % 2 == 0) {
            heap[heapSize] = null;
            heapSize--;
            changedSize = true;
        }

        int currIndex = 1;
        int childIndex;
        boolean sorted = false;
        while (!sorted) {
            if (2 * currIndex > heapSize) {
                sorted = true;
            } else {
                if (compareNodes(2 * currIndex, 2 * currIndex + 1) >= 0) {
                    childIndex = 2 * currIndex;
                } else {
                    childIndex = 2 * currIndex + 1;
                }

                if (!(compareNodes(currIndex, childIndex) >= 0)) {
                    currIndex = bubbleDown(currIndex);
                } else {
                    sorted = true;
                }
            }
        }

        //Removes last value off heap if that hasn't already taken place
        if (!changedSize) {
            heap[heapSize] = null;
            heapSize--;
        }
        return returnJob;
    }

    public T peekMax() {
        return heap[1];
    }
    
    public T peek(int indexToSend) {
    	return heap[indexToSend];
    }

    public boolean isEmpty() {
        return heap[1] == null;
    }
    public int getSize() {
    	return heapSize;
    }

    private int bubbleUp(int childIndex) {
        T temp = heap[childIndex];
        heap[childIndex] = heap[childIndex / 2];
        heap[childIndex / 2] = temp;
        return childIndex / 2;
    }

    private int bubbleDown(int parentIndex) {
        T temp = heap[parentIndex];

        int childIndex = 0;
        if (compareNodes(2 * parentIndex, 2 * parentIndex + 1) >= 0) {
            childIndex = 2 * parentIndex;
        } else {
            childIndex = 2 * parentIndex + 1;
        }

        heap[parentIndex] = heap[childIndex];
        heap[childIndex] = temp;
        return childIndex;
    }

    /**
     * Method to compare the priority of two nodes
     * Returns 1 if value at firstIndex is larger than value at secondIndex
     * Returns 0 if values are equivalent
     * Returns -1 if value at firstIndex is smaller than value at secondIndex
     *
     * @return
     */
    private int compareNodes(final int firstIndex, final int secondIndex) {
        return heap[firstIndex].compareTo(heap[secondIndex]);
    }
}
