/* Binary Heap implementation, based on https://gist.github.com/dburger/1008320
 * Simple priority queue data structure.
 */

export class BinaryHeap<T> {
    private comparator: (a: T, b: T) => boolean;
    private arr: T[] = [];

    constructor(comparator: (a: T, b: T) => boolean) {
        this.comparator = comparator;
    }

    public pop(): T {
        if (this.arr.length === 0) {
            throw new Error("pop() called on emtpy binary heap");
        }
        let value = this.arr[0];
        let last = this.arr.length - 1;
        this.arr[0] = this.arr[last];
        this.arr.length = last;
        if (last > 0) {
            this.bubbleDown(0);
        }
        return value;
    }

    public push(value: T) {
        this.arr.push(value);
        this.bubbleUp(this.arr.length - 1);
    }

    public size() {
        return this.arr.length;
    }

    public find(x: any, comparator: (a: T, b: any) => boolean): T | undefined {
        return this.arr.find((element) => comparator(element, x));
    }

    public rebalance(x: T, comparator: (a: T, b: any) => boolean) {
        let index = this.findIndex(x, comparator);
        if (index !== undefined) {
            this.bubbleUp(index);
        }
    }

    private findIndex(x: T, comparator: (a: T, b: any) => boolean): number {
        return this.arr.findIndex((element) => comparator(element, x));
    }

    private swap(a: number, b: number) {
        let temp = this.arr[a];
        this.arr[a] = this.arr[b];
        this.arr[b] = temp;
    }

    private bubbleDown(pos: number): number {
        let left = 2 * pos + 1;
        let right = left + 1;
        let largest = pos;
        if (left < this.arr.length && this.comparator(this.arr[left], this.arr[largest])) {
            largest = left;
        }
        if (right < this.arr.length && this.comparator(this.arr[right], this.arr[largest])) {
            largest = right;
        }
        if (largest !== pos) {
            this.swap(largest, pos);
            return this.bubbleDown(largest);
        }
        return pos;
    }

    private bubbleUp(pos: number): number {
        if (pos <= 0) {
            return 0;
        }
        let parent = Math.floor((pos - 1) / 2);
        if (this.comparator(this.arr[pos], this.arr[parent])) {
            this.swap(pos, parent);
            return this.bubbleUp(parent);
        }
        return pos;
    }
}
